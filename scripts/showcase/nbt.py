"""Typed, big-endian Java NBT, including DataInput's modified UTF-8 (stdlib)."""

from dataclasses import dataclass
import io
import struct


@dataclass
class Tag:
    type: int
    value: object
    element_type: int = 0  # Retains the type of empty TAG_List values.


def compound(**values):
    return Tag(10, values)


def list_tag(values=(), element_type=10):
    return Tag(9, list(values), element_type)


def encode_string(text):
    units = text.encode("utf-16-be", "surrogatepass")
    encoded = bytearray()
    for (unit,) in struct.iter_unpack(">H", units):
        if 1 <= unit <= 0x7f:
            encoded.append(unit)
        elif unit <= 0x7ff:
            encoded.extend((0xc0 | unit >> 6, 0x80 | unit & 63))
        else:
            encoded.extend((0xe0 | unit >> 12, 0x80 | unit >> 6 & 63, 0x80 | unit & 63))
    if len(encoded) > 65535:
        raise ValueError("NBT modified UTF-8 string exceeds 65535 bytes")
    return struct.pack(">H", len(encoded)) + encoded


def decode_string(data):
    units = bytearray()
    index = 0
    while index < len(data):
        first = data[index]
        index += 1
        if first <= 0x7f:
            unit = first
        else:
            count = 1 if first & 0xe0 == 0xc0 else 2 if first & 0xf0 == 0xe0 else -1
            if count < 0 or index + count > len(data):
                raise ValueError("Invalid modified UTF-8 sequence")
            unit = first & (31 if count == 1 else 15)
            for byte in data[index:index + count]:
                if byte & 0xc0 != 0x80:
                    raise ValueError("Invalid modified UTF-8 continuation")
                unit = unit << 6 | byte & 63
            index += count
            if (count == 1 and unit < 128 and unit != 0) or (count == 2 and unit < 2048):
                raise ValueError("Overlong modified UTF-8 sequence")
        units.extend(struct.pack(">H", unit))
    return units.decode("utf-16-be", "surrogatepass")


FORMATS = {1: "b", 2: "h", 3: "i", 4: "q", 5: "f", 6: "d"}


class Reader:
    def __init__(self, data):
        self.stream = io.BytesIO(data)
        self.size = len(data)

    def take(self, count):
        if count < 0 or count > self.size - self.stream.tell():
            raise ValueError("Truncated NBT")
        return self.stream.read(count)

    def number(self, fmt):
        return struct.unpack(">" + fmt, self.take(struct.calcsize(fmt)))[0]

    def string(self):
        return decode_string(self.take(self.number("H")))

    def length(self):
        count = self.number("i")
        if count < 0:
            raise ValueError("Negative NBT array/list length")
        return count

    def payload(self, kind, depth=0):
        if depth > 512:
            raise ValueError("NBT nesting exceeds 512")
        if kind == 0:
            return Tag(0, None)
        if kind in FORMATS:
            return Tag(kind, self.number(FORMATS[kind]))
        if kind == 7:
            return Tag(7, self.take(self.length()))
        if kind == 8:
            return Tag(8, self.string())
        if kind == 9:
            element = self.number("B")
            count = self.length()
            if not 0 <= element <= 12 or (element == 0 and count):
                raise ValueError("Invalid NBT list element type")
            if count > self.size - self.stream.tell():
                raise ValueError("Truncated NBT list")
            return Tag(9, [self.payload(element, depth + 1) for _ in range(count)], element)
        if kind == 10:
            values = {}
            while True:
                element = self.number("B")
                if element == 0:
                    return Tag(10, values)
                name = self.string()
                values[name] = self.payload(element, depth + 1)
        if kind in (11, 12):
            fmt = "i" if kind == 11 else "q"
            data = self.take(self.length() * struct.calcsize(fmt))
            return Tag(kind, [value for (value,) in struct.iter_unpack(">" + fmt, data)])
        raise ValueError(f"Unknown NBT tag type {kind}")


def loads(data):
    """Return (root name, typed root); refuse truncated or trailing data."""
    reader = Reader(data)
    kind = reader.number("B")
    name = reader.string() if kind else ""
    root = reader.payload(kind)
    if reader.stream.tell() != reader.size:
        raise ValueError("Trailing NBT bytes")
    return name, root


def _payload(tag, depth=0):
    if depth > 512:
        raise ValueError("NBT nesting exceeds 512")
    kind, value = tag.type, tag.value
    if kind == 0:
        return b""
    if kind in FORMATS:
        return struct.pack(">" + FORMATS[kind], value)
    if kind == 7:
        return struct.pack(">i", len(value)) + bytes(value)
    if kind == 8:
        return encode_string(value)
    if kind == 9:
        element = tag.element_type
        if not 0 <= element <= 12 or (element == 0 and value):
            raise ValueError("Invalid NBT list element type")
        if any(child.type != element for child in value):
            raise ValueError("NBT lists must be homogeneous")
        return struct.pack(">Bi", element, len(value)) + b"".join(
            _payload(child, depth + 1) for child in value)
    if kind == 10:
        if any(child.type == 0 for child in value.values()):
            raise ValueError("Named TAG_End is not a compound member")
        return b"".join(bytes((child.type,)) + encode_string(name) + _payload(child, depth + 1)
                        for name, child in value.items()) + b"\0"
    if kind in (11, 12):
        fmt = "i" if kind == 11 else "q"
        return struct.pack(">i", len(value)) + b"".join(struct.pack(">" + fmt, item) for item in value)
    raise ValueError(f"Unknown NBT tag type {kind}")


def dumps(root, name=""):
    return bytes((root.type,)) + (encode_string(name) if root.type else b"") + _payload(root)
