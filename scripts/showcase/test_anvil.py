"""Generated NBT/Anvil fixtures, plus a dry run on a temporary pristine save copy."""

import copy
import gzip
import hashlib
import json
import os
from pathlib import Path
import shutil
import struct
import subprocess
import sys
import tempfile
import unittest
import zlib

sys.path.insert(0, str(Path(__file__).resolve().parent))
import anvil
import apply_to_world as apply
from block_entities import BUILD_SCAPE, entity_id
import build_showcase
import nbt


def section(y=0, state="minecraft:air"):
    return nbt.compound(Y=nbt.Tag(1, y),
                        biomes=nbt.compound(palette=nbt.list_tag([nbt.Tag(8, "minecraft:plains")], 8)),
                        block_states=anvil.encode_states([apply.parse_state(state)] * 4096),
                        BlockLight=nbt.Tag(7, bytes(2048)), SkyLight=nbt.Tag(7, bytes([255]) * 2048))


def chunk(cx=0, cz=0):
    return nbt.compound(DataVersion=nbt.Tag(3, apply.DATA_VERSION), xPos=nbt.Tag(3, cx),
                        zPos=nbt.Tag(3, cz), Status=nbt.Tag(8, "minecraft:full"),
                        sections=nbt.list_tag([section(-4), section(0), section(3)]),
                        isLightOn=nbt.Tag(1, 1), Heightmaps=nbt.compound(WORLD_SURFACE=nbt.Tag(12, [0] * 37)),
                        block_entities=nbt.list_tag(), block_ticks=nbt.list_tag(),
                        fluid_ticks=nbt.list_tag(), entities=nbt.list_tag([nbt.compound(id=nbt.Tag(8, "sentinel"))]))


def region_data(records):
    """Independent sector fixture with deliberate gaps and supplied raw records."""
    header = bytearray(8192)
    body = bytearray(4096)  # Empty sector 2 tests non-contiguous locations.
    offset = 3
    for slot, record in records.items():
        sectors = (len(record) + 4 + 4095) // 4096
        struct.pack_into(">I", header, slot * 4, offset << 8 | sectors)
        struct.pack_into(">I", header, 4096 + slot * 4, 100 + slot)
        body.extend(struct.pack(">I", len(record)) + record + bytes(sectors * 4096 - len(record) - 4))
        offset += sectors
    return bytes(header + body)


def showcase_fixture(origin=(30, 30)):
    blocks = [
        {"id": "buildscape:oak_door", "className": "DoorBlock", "blockType": "Door",
         "states": [{"half": half, "facing": "north"} for half in ("lower", "upper")]},
        {"id": "buildscape:straw_bed", "className": "StrawBedBlock", "blockType": "Bed",
         "states": [{"part": part, "facing": "south"} for part in ("foot", "head")]},
        {"id": "buildscape:experience_liquid", "className": "FluidBlock", "blockType": "Fluid",
         "states": [{"level": "0"}]},
        {"id": "buildscape:copper_chest", "className": "CopperChest", "blockType": "CopperChest",
         "states": [{"facing": "north", "type": "single", "waterlogged": "false"}]},
        {"id": "buildscape:display_widget", "className": "GlassJarBlock", "blockType": "GlassJar",
         "states": [{}]},
    ]
    return build_showcase.build_layout({"minecraftVersion": "26.2", "blocks": blocks},
                                      origin_x=origin[0], origin_z=origin[1], ground_top_y=-61, layer_size=4)


def expected_counts(layout):
    # Independently compute counts from geometry (not the writer's iterator).
    layers = layout["layers"]
    clearing = (max(layer["width"] for layer in layers) * max(layer["depth"] for layer in layers)
                * (layers[-1]["top_y"] - layout["ground_top_y"]))
    with_floors = layout.get("floors", True)
    floors = sum(layer["width"] * layer["depth"] for layer in layers[1:]) if with_floors else 0
    signs = sum(group["sign"] is not None for layer in layers for group in layer["groups"])
    cells = [cell for layer in layers for group in layer["groups"] for cell in group["cells"]]
    displays = sum(len(cell["parts"]) + (5 if cell["kind"] == "fluid" else 0) for cell in cells)
    if not with_floors:  # one glass bottom per fluid cell above layer 1
        displays += sum(cell["kind"] == "fluid" for layer in layers[1:]
                        for group in layer["groups"] for cell in group["cells"])
    return clearing + floors + signs + displays, signs + 4  # two bed parts, chest, jar


class NBTTests(unittest.TestCase):
    def test_all_tag_types_and_named_root_round_trip(self):
        tags = {"byte": nbt.Tag(1, -128), "short": nbt.Tag(2, -32768),
                "int": nbt.Tag(3, -(1 << 31)), "long": nbt.Tag(4, -(1 << 63)),
                "float": nbt.Tag(5, 1.25), "double": nbt.Tag(6, -1.5),
                "bytes": nbt.Tag(7, b"\x00\x80\xff"),
                "string": nbt.Tag(8, "null\0é水😀\ud800"),
                "list": nbt.list_tag([nbt.Tag(3, 2), nbt.Tag(3, -3)], 3),
                "compound": nbt.compound(inner=nbt.Tag(8, "yes")),
                "ints": nbt.Tag(11, [-2147483648, 0, 2147483647]),
                "longs": nbt.Tag(12, [-(1 << 63), -1, 0, (1 << 63) - 1]),
                "empty_list": nbt.list_tag([], 8), "end_list": nbt.list_tag([], 0)}
        root = nbt.Tag(10, tags)
        self.assertEqual(nbt.loads(nbt.dumps(root, "root\0😀")), ("root\0😀", root))
        for tag in tags.values():
            self.assertEqual(nbt.loads(nbt.dumps(tag)), ("", tag))
        self.assertEqual(nbt.loads(b"\0"), ("", nbt.Tag(0, None)))

    def test_modified_utf8_wire_bytes_and_big_endian(self):
        self.assertEqual(nbt.encode_string("\0😀"), b"\x00\x08\xc0\x80\xed\xa0\xbd\xed\xb8\x80")
        self.assertEqual(nbt.dumps(nbt.Tag(3, 0x12345678)), b"\x03\x00\x00\x12\x34\x56\x78")
        self.assertEqual(nbt.dumps(nbt.Tag(12, [0x123456789abcdef])),
                         b"\x0c\x00\x00\x00\x00\x00\x01\x01\x23\x45\x67\x89\xab\xcd\xef")
        with self.assertRaises(ValueError):
            nbt.encode_string("x" * 65536)
        with self.assertRaises(ValueError):
            nbt.decode_string(b"\xf0\x9f\x98\x80")  # Standard UTF-8 is not modified UTF-8.

    def test_malformed_nbt_refused(self):
        for data in (b"", b"\x03\0\0\0", b"\x0c\0\0\xff\xff\xff\xff", b"\0x",
                     b"\x09\0\0\0\0\0\0\x01", b"\x0d\0\0"):
            with self.subTest(data=data), self.assertRaises(ValueError):
                nbt.loads(data)
        with self.assertRaises(ValueError):
            nbt.dumps(nbt.list_tag([nbt.Tag(3, 1)], 8))


class RegionTests(unittest.TestCase):
    def test_region_round_trip_all_compressions_timestamps_and_preservation(self):
        roots = [chunk(i, 0) for i in range(3)]
        records = {0: b"\x01" + gzip.compress(nbt.dumps(roots[0])),
                   1: b"\x02" + zlib.compress(nbt.dumps(roots[1])),
                   2: b"\x03" + nbt.dumps(roots[2])}
        region = anvil.Region(region_data(records))
        for i, root in enumerate(roots):
            self.assertEqual(region.get_chunk(i, 0), ("", root))
        self.assertIsNone(region.get_chunk(-1, -1))
        roots[1].value["LastUpdate"] = nbt.Tag(4, 1234)
        region.set_chunk(1, 0, roots[1], timestamp=999)
        output = region.to_bytes()
        self.assertEqual(len(output) % 4096, 0)
        reread = anvil.Region(output)
        self.assertEqual(reread.get_chunk(1, 0), ("", roots[1]))
        self.assertEqual(reread.records[0], records[0])
        self.assertEqual(reread.records[2], records[2])
        self.assertEqual(reread.timestamps[:3], [100, 999, 102])
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "r.0.0.mca"
            region.write(path)
            self.assertEqual(anvil.Region.read(path).to_bytes(), output)
            self.assertEqual([p.name for p in Path(directory).iterdir()], [path.name])

    def test_invalid_sectors_and_external_records_refused(self):
        with self.assertRaisesRegex(ValueError, "External .mcc"):
            anvil.Region(region_data({0: b"\x82"}))
        with self.assertRaises(ValueError):
            anvil.Region(bytes(8193))
        data = bytearray(region_data({0: b"\x03" + nbt.dumps(chunk())}))
        struct.pack_into(">I", data, 4, struct.unpack_from(">I", data, 0)[0])
        with self.assertRaisesRegex(ValueError, "Overlapping"):
            anvil.Region(data)
        data = bytearray(region_data({0: b"\x03\0"}))
        struct.pack_into(">I", data, 3 * 4096, 4096)
        with self.assertRaisesRegex(ValueError, "length"):
            anvil.Region(data)

    def test_oversized_output_refused_before_mutation(self):
        region = anvil.Region(bytes(8192))
        root = nbt.compound(random=nbt.Tag(7, os.urandom(255 * 4096)))
        with self.assertRaisesRegex(ValueError, "external .mcc"):
            region.set_chunk(0, 0, root)
        self.assertEqual(region.records, {})


class PaletteAndChunkTests(unittest.TestCase):
    def test_all_requested_palette_sizes_and_minimal_palettes(self):
        for size in (1, 2, 5, 16, 17, 300):
            with self.subTest(size=size):
                values = [index % size for index in range(4096)]
                data = anvil.pack_indices(values, size)
                self.assertEqual(anvil.unpack_indices(data, size), values)
                if size == 1:
                    self.assertEqual(data, [])
                else:
                    per_long = 64 // max(4, (size - 1).bit_length())
                    self.assertEqual(len(data), (4096 + per_long - 1) // per_long)
                    # Independent bit packing oracle: values never straddle a long.
                    bits = max(4, (size - 1).bit_length())
                    for index in (0, per_long - 1, per_long, 4095):
                        unsigned = data[index // per_long] & ((1 << 64) - 1)
                        self.assertEqual(unsigned >> (index % per_long * bits) & ((1 << bits) - 1), values[index])
                states = [apply.parse_state(f"buildscape:test[variant={index}]") for index in range(size)]
                original = [states[index] for index in values]
                encoded = anvil.encode_states(original)
                self.assertEqual(anvil.decode_states(encoded), original)
                self.assertEqual(len(encoded.value["palette"].value), size)
                self.assertEqual("data" in encoded.value, size != 1)
                shrunk = anvil.encode_states([original[0]] * 4096)
                self.assertEqual(len(shrunk.value["palette"].value), 1)
                self.assertNotIn("data", shrunk.value)

    def test_independent_five_bit_boundary_fixture(self):
        values = [0] * 4096
        values[11], values[12], values[13] = 16, 3, 7
        data = [0] * 342
        data[0], data[1] = 16 << 55, 3 | 7 << 5
        self.assertEqual(anvil.unpack_indices(data, 17), values)
        self.assertEqual(anvil.pack_indices(values, 17), data)
        with self.assertRaises(ValueError):
            anvil.unpack_indices([0], 17)

    def test_section_boundaries_biomes_lighting_and_heightmaps(self):
        root = chunk(-1, -1)
        untouched = copy.deepcopy(root.value["sections"].value[2])
        before_entities = copy.deepcopy(root.value["entities"])
        placements = []
        for y in (-1, 0, 15, 16, 31, 32):
            pos = (-1, y, -1)
            placements.append(apply.Placement(pos, pos, apply.parse_state(f"buildscape:test[height={y}]")))
        apply.ChunkEditor(root).apply(placements)
        sections = {s.value["Y"].value: s for s in root.value["sections"].value}
        for placement in placements:
            x, y, z = placement.low
            edited = sections[y // 16]
            states = anvil.decode_states(edited.value["block_states"])
            self.assertEqual(states[anvil.block_index(x, y, z)], placement.state)
            self.assertNotIn("SkyLight", edited.value)
            self.assertNotIn("BlockLight", edited.value)
            self.assertEqual(edited.value["biomes"], untouched.value["biomes"])
            if y // 16 != 0:
                self.assertIsNot(edited.value["biomes"], untouched.value["biomes"])
        self.assertEqual(sections[3], untouched)
        self.assertEqual(root.value["entities"], before_entities)
        self.assertEqual(root.value["isLightOn"].value, 0)
        self.assertNotIn("Heightmaps", root.value)

    def test_block_entities_replacement_signs_beds_containers_and_ticks(self):
        root = chunk()
        old = apply.block_entity("minecraft:chest", (1, 0, 1))
        keep = apply.block_entity("minecraft:chest", (9, 0, 9))
        root.value["block_entities"] = nbt.list_tag([old, keep])
        ticks = [nbt.compound(x=nbt.Tag(3, x), y=nbt.Tag(3, 0), z=nbt.Tag(3, x), i=nbt.Tag(8, "water"))
                 for x in (1, 9)]
        root.value["block_ticks"] = nbt.list_tag(copy.deepcopy(ticks))
        root.value["fluid_ticks"] = nbt.list_tag(copy.deepcopy(ticks))
        pos = (1, 0, 1)
        sign = apply.block_entity("minecraft:sign", pos, ["A\0😀", "second line"])
        bed = apply.block_entity("minecraft:bed", (2, 0, 1))
        jar = apply.block_entity("buildscape:glass_jar_block_entity", (3, 0, 1))
        placements = [apply.Placement(pos, pos, apply.parse_state("minecraft:oak_sign[rotation=0]"), sign),
                      apply.Placement((2, 0, 1), (2, 0, 1), apply.parse_state("buildscape:straw_bed[part=foot]"), bed),
                      apply.Placement((3, 0, 1), (3, 0, 1), apply.parse_state("buildscape:jar"), jar)]
        self.assertEqual(apply.ChunkEditor(root).apply(placements), 3)
        entities = root.value["block_entities"].value
        self.assertEqual(entities, [keep, sign, bed, jar])
        self.assertEqual(sign.value["front_text"].value["messages"].value[0], nbt.Tag(8, "A\0😀"))
        self.assertEqual(sign.value["back_text"].value["messages"].element_type, 8)
        self.assertEqual(sign.value["is_waxed"], nbt.Tag(1, 1))
        self.assertTrue(all(entity.value["keepPacked"] == nbt.Tag(1, 0) for entity in entities))
        self.assertEqual(root.value["block_ticks"].value, ticks[1:])
        self.assertEqual(root.value["fluid_ticks"].value, ticks[1:])
        apply.ChunkEditor(root).apply([apply.Placement(pos, (3, 0, 1), apply.parse_state("minecraft:air"))])
        self.assertEqual(root.value["block_entities"].value, [keep])
        self.assertEqual(nbt.loads(nbt.dumps(root))[1], root)

    def test_entity_class_mapping_metadata_and_no_entity_subclass(self):
        for name, identifier in BUILD_SCAPE.items():
            self.assertEqual(entity_id("buildscape:arbitrary", name + "Block"), "buildscape:" + identifier)
        self.assertEqual(entity_id("buildscape:lights", "MulticolorGlowLightsBlock", "GlowLights"), None)
        self.assertEqual(entity_id("buildscape:bamboo_wall_sign"), "buildscape:bamboo_sign_block_entity")
        self.assertEqual(entity_id("buildscape:straw_bed", "StrawBedBlock"), "minecraft:bed")
        self.assertIsNone(entity_id("buildscape:icicle", "Block"))
        layout = showcase_fixture()
        cells = [cell for layer in layout["layers"] for group in layer["groups"] for cell in group["cells"]]
        self.assertTrue(all("className" in cell and "blockType" in cell for cell in cells))
        placements = list(apply.layout_placements(layout))
        self.assertEqual(sum(p.count for p in placements), expected_counts(layout)[0])
        self.assertEqual(sum(p.entity is not None for p in placements), expected_counts(layout)[1])
        layout = showcase_fixture()
        layout["floors"] = False
        placements = list(apply.layout_placements(layout))
        self.assertFalse(any(p.state.value["Name"].value == "minecraft:grass_block" for p in placements))
        self.assertEqual(sum(p.count for p in placements), expected_counts(layout)[0])
        self.assertEqual(sum(p.entity is not None for p in placements), expected_counts(layout)[1])


class WorldTests(unittest.TestCase):
    def make_world(self, root, legacy=False):
        world = root / "world"
        region_dir = world / ("region" if legacy else "dimensions/minecraft/overworld/region")
        region_dir.mkdir(parents=True)
        (world / "session.lock").write_bytes(b"\xe2\x98\x83")
        level = nbt.compound(Data=nbt.compound(DataVersion=nbt.Tag(3, apply.DATA_VERSION)))
        (world / "level.dat").write_bytes(gzip.compress(nbt.dumps(level)))
        region = anvil.Region(bytes(8192))
        region.set_chunk(0, 0, chunk())
        region.set_chunk(1, 0, chunk(1, 0))  # Outside the layout, must remain byte-identical.
        region.write(region_dir / "r.0.0.mca")
        (world / "poi").mkdir()
        (world / "poi/sentinel").write_bytes(b"leave alone")
        return world, region_dir

    def test_dry_run_write_backup_and_legacy_world(self):
        for legacy in (False, True):
            with self.subTest(legacy=legacy), tempfile.TemporaryDirectory() as directory:
                world, region_dir = self.make_world(Path(directory), legacy)
                layout = showcase_fixture((0, 0))
                before = (region_dir / "r.0.0.mca").read_bytes()
                dry = apply.apply_world(world, layout, dry_run=True)
                self.assertEqual((region_dir / "r.0.0.mca").read_bytes(), before)
                self.assertFalse((world / "showcase-region-backup").exists())
                self.assertEqual(dry["skipped_placements"], 0)
                self.assertEqual(dry["applied_placements"], expected_counts(layout)[0])
                self.assertEqual(dry["block_entities_added"], expected_counts(layout)[1])
                report = apply.apply_world(world, layout)
                backup = world / "showcase-region-backup"
                self.assertEqual((backup / "r.0.0.mca").read_bytes(), before)
                self.assertEqual(report["edited_chunks"], 1)
                after_region = anvil.Region.read(region_dir / "r.0.0.mca")
                self.assertEqual(after_region.records[1], anvil.Region(before).records[1])
                self.assertEqual(after_region.timestamps[1], anvil.Region(before).timestamps[1])
                _, result = after_region.get_chunk(0, 0)
                sections = {s.value["Y"].value: anvil.decode_states(s.value["block_states"])
                            for s in result.value["sections"].value}
                for p in apply.layout_placements(layout):
                    if p.low == p.high:
                        x, y, z = p.low
                        self.assertEqual(sections[y // 16][anvil.block_index(x, y, z)], p.state)
                self.assertEqual((world / "poi/sentinel").read_bytes(), b"leave alone")
                self.assertEqual((world / "session.lock").read_bytes(), b"\xe2\x98\x83")
                self.assertEqual(len(result.value["block_entities"].value), expected_counts(layout)[1])
                with self.assertRaisesRegex(ValueError, "Default backup already exists"):
                    apply.apply_world(world, layout)
                apply.apply_world(world, layout, backup_dir=backup)
                self.assertEqual((backup / "r.0.0.mca").read_bytes(), before)

    def test_missing_chunks_are_reported_not_created(self):
        with tempfile.TemporaryDirectory() as directory:
            world, region_dir = self.make_world(Path(directory))
            layout = showcase_fixture((10000, -10000))
            before = (region_dir / "r.0.0.mca").read_bytes()
            report = apply.apply_world(world, layout)
            self.assertEqual(report["skipped_placements"], expected_counts(layout)[0])
            self.assertEqual(report["applied_placements"], 0)
            self.assertEqual(report["edited_chunks"], 0)
            self.assertTrue(report["missing_chunks"])
            self.assertEqual((region_dir / "r.0.0.mca").read_bytes(), before)
            self.assertEqual(len(list(region_dir.glob("*.mca"))), 1)

    def test_versions_and_external_records_preflight_without_backup_or_writes(self):
        with tempfile.TemporaryDirectory() as directory:
            world, region_dir = self.make_world(Path(directory))
            path = region_dir / "r.0.0.mca"
            region = anvil.Region.read(path)
            _, bad = region.get_chunk(0, 0)
            bad.value["DataVersion"] = nbt.Tag(3, 1)
            region.set_chunk(0, 0, bad)
            region.write(path)
            before = path.read_bytes()
            with self.assertRaisesRegex(ValueError, "DataVersion 1"):
                apply.apply_world(world, showcase_fixture((0, 0)))
            self.assertEqual(path.read_bytes(), before)
            self.assertFalse((world / "showcase-region-backup").exists())
            path.write_bytes(region_data({0: b"\x82"}))
            with self.assertRaisesRegex(ValueError, "External .mcc"):
                apply.apply_world(world, showcase_fixture((0, 0)), dry_run=True)

    def test_held_session_lock_refuses_cli(self):
        with tempfile.TemporaryDirectory() as directory:
            world, _ = self.make_world(Path(directory))
            layout_file = Path(directory) / "layout.json"
            layout_file.write_text(json.dumps(showcase_fixture((0, 0))), encoding="utf-8")
            with apply.closed_world(world):
                result = subprocess.run([sys.executable, "-B", str(Path(apply.__file__)),
                                         str(world), str(layout_file), "--dry-run"], capture_output=True, text=True)
            self.assertEqual(result.returncode, 2)
            self.assertIn("close the game" if os.name == "nt" and "Cannot open" in result.stderr
                          else "session.lock is held", result.stderr)

    def test_backup_cannot_overlap_regions(self):
        with tempfile.TemporaryDirectory() as directory:
            world, region_dir = self.make_world(Path(directory))
            for destination in (region_dir, region_dir / "backup", region_dir.parent):
                with self.assertRaisesRegex(ValueError, "separate"):
                    apply.ensure_backup(region_dir, destination, True)

    def test_pristine_real_world_copy_cli_dry_run(self):
        workspace = os.environ.get("WORKSPACES")
        if not workspace:
            repos = os.environ.get("REPOS")
            workspace = str(Path(repos) / "Codex Workspaces") if repos else None
        if not workspace:
            self.skipTest("REPOS/WORKSPACES are not set; pristine world unavailable")
        pristine = Path(workspace) / "Buildscape-26/artifacts/showcase-world-pristine/Buildscape Showcase"
        if not pristine.is_dir():
            self.skipTest("Optional pristine Minecraft 26.2 test world is unavailable")
        with tempfile.TemporaryDirectory() as directory:
            copied = Path(directory) / "world"
            shutil.copytree(pristine, copied)
            layout = showcase_fixture()
            path = Path(directory) / "layout.json"
            path.write_text(json.dumps(layout), encoding="utf-8")
            regions = apply.overworld_region(copied)
            def fingerprints():
                return {file.name: hashlib.sha256(file.read_bytes()).hexdigest() for file in regions.iterdir()}
            before = fingerprints()
            result = subprocess.run([sys.executable, "-B", str(Path(apply.__file__)), str(copied),
                                     str(path), "--dry-run"], capture_output=True, text=True, timeout=120)
            self.assertEqual(result.returncode, 0, result.stderr)
            report = json.loads(result.stdout)
            self.assertEqual(report["data_version"], 4903)
            self.assertEqual(report["chunk_data_versions"], [4903])
            self.assertEqual(report["placements"], expected_counts(layout)[0])
            self.assertEqual(report["applied_placements"], expected_counts(layout)[0])
            self.assertEqual(report["block_entities_added"], expected_counts(layout)[1])
            self.assertEqual(report["skipped_placements"], 0)
            self.assertEqual(report["missing_chunks"], [])
            self.assertEqual(report["edited_chunks"], 4)
            self.assertEqual(fingerprints(), before)
            self.assertFalse((copied / "showcase-region-backup").exists())


if __name__ == "__main__":
    unittest.main()
