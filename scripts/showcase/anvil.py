"""Anvil region sectors and 26.x block-state palettes, without dependencies."""

import gzip
import os
from pathlib import Path
import struct
import tempfile
import time
import zlib

import nbt

SECTOR = 4096
MASK64 = (1 << 64) - 1


class Region:
    """Retain untouched compressed records and timestamps when rebuilding a region.

    External .mcc records are deliberately refused, including on dry runs. No
    region or external payload is modified if one is encountered.
    """

    def __init__(self, data):
        if len(data) < 2 * SECTOR or len(data) % SECTOR:
            raise ValueError("Region must have two header sectors and 4 KiB alignment")
        self.records = {}
        self.timestamps = list(struct.unpack(">1024I", data[SECTOR:2 * SECTOR]))
        occupied = {0, 1}
        for index in range(1024):
            location = struct.unpack_from(">I", data, index * 4)[0]
            offset, sectors = location >> 8, location & 255
            if not location:
                continue
            if offset < 2 or not sectors or (offset + sectors) * SECTOR > len(data):
                raise ValueError(f"Invalid region sector location for slot {index}")
            span = set(range(offset, offset + sectors))
            if occupied & span:
                raise ValueError(f"Overlapping region sectors for slot {index}")
            occupied.update(span)
            start = offset * SECTOR
            length = struct.unpack_from(">I", data, start)[0]
            if length < 1 or length + 4 > sectors * SECTOR:
                raise ValueError(f"Invalid chunk record length for slot {index}")
            record = data[start + 4:start + 4 + length]
            if record[0] & 128:
                raise ValueError(f"External .mcc chunk at slot {index}; editing this region is refused")
            if record[0] not in (1, 2, 3):
                raise ValueError(f"Unsupported Anvil compression type {record[0]} at slot {index}")
            self.records[index] = record

    @classmethod
    def read(cls, path):
        return cls(Path(path).read_bytes())

    @staticmethod
    def slot(x, z):
        return (x & 31) + (z & 31) * 32

    def get_chunk(self, x, z):
        record = self.records.get(self.slot(x, z))
        if record is None:
            return None
        compression, payload = record[0], record[1:]
        data = gzip.decompress(payload) if compression == 1 else zlib.decompress(payload) if compression == 2 else payload
        return nbt.loads(data)

    def set_chunk(self, x, z, root, name="", timestamp=None):
        record = b"\x02" + zlib.compress(nbt.dumps(root, name))
        if (len(record) + 4 + SECTOR - 1) // SECTOR > 255:
            raise ValueError("Chunk exceeds 255 sectors; external .mcc output is refused")
        index = self.slot(x, z)
        self.records[index] = record
        self.timestamps[index] = int(time.time()) if timestamp is None else timestamp

    def to_bytes(self):
        header = bytearray(2 * SECTOR)
        struct.pack_into(">1024I", header, SECTOR, *self.timestamps)
        bodies = []
        offset = 2
        for index, record in sorted(self.records.items()):
            size = (len(record) + 4 + SECTOR - 1) // SECTOR
            if size > 255 or offset >= 1 << 24:
                raise ValueError("Anvil sector address/count overflow")
            struct.pack_into(">I", header, index * 4, offset << 8 | size)
            bodies.append(struct.pack(">I", len(record)) + record + bytes(size * SECTOR - 4 - len(record)))
            offset += size
        return bytes(header) + b"".join(bodies)

    def write(self, path):
        """Replace one region atomically after fully encoding it."""
        data = self.to_bytes()
        path = Path(path)
        fd, temporary = tempfile.mkstemp(prefix=path.name + ".", suffix=".tmp", dir=path.parent)
        try:
            with os.fdopen(fd, "wb") as output:
                output.write(data)
                output.flush()
                os.fsync(output.fileno())
            os.replace(temporary, path)
        finally:
            if os.path.exists(temporary):
                os.unlink(temporary)


def palette_bits(size):
    if size < 1:
        raise ValueError("Empty block-state palette")
    return max(4, (size - 1).bit_length())


def unpack_indices(data, palette_size, count=4096):
    if palette_size == 1:
        return [0] * count
    bits = palette_bits(palette_size)
    per_long = 64 // bits
    if len(data) != (count + per_long - 1) // per_long:
        raise ValueError("Block-state packed data has the wrong length")
    mask = (1 << bits) - 1
    values = [(data[index // per_long] & MASK64) >> (index % per_long * bits) & mask
              for index in range(count)]
    if any(value >= palette_size for value in values):
        raise ValueError("Block-state palette index out of bounds")
    return values


def pack_indices(values, palette_size):
    if any(value < 0 or value >= palette_size for value in values):
        raise ValueError("Block-state palette index out of bounds")
    if palette_size == 1:
        return []
    bits = palette_bits(palette_size)
    per_long = 64 // bits
    data = [0] * ((len(values) + per_long - 1) // per_long)
    for index, value in enumerate(values):
        data[index // per_long] |= value << (index % per_long * bits)
    return [value if value < 1 << 63 else value - (1 << 64) for value in data]


def state_key(state):
    name = state.value["Name"].value
    properties = state.value.get("Properties", nbt.compound()).value
    return name, tuple(sorted((key, value.value) for key, value in properties.items()))


def decode_states(container):
    palette = container.value["palette"].value
    data = container.value.get("data", nbt.Tag(12, [])).value
    return [palette[index] for index in unpack_indices(data, len(palette))]


def encode_states(states):
    if len(states) != 4096:
        raise ValueError("A section must contain 4096 block states")
    palette, mapping, indices = [], {}, []
    for state in states:
        key = state_key(state)
        if key not in mapping:
            mapping[key] = len(palette)
            palette.append(state)
        indices.append(mapping[key])
    result = nbt.compound(palette=nbt.list_tag(palette))
    if len(palette) > 1:
        result.value["data"] = nbt.Tag(12, pack_indices(indices, len(palette)))
    return result


def block_index(x, y, z):
    return (y & 15) * 256 + (z & 15) * 16 + (x & 15)
