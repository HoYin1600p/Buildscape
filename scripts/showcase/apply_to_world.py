#!/usr/bin/env python3
"""Apply a showcase layout to existing chunks of a CLOSED Minecraft 26.2 save."""

import argparse
from collections import defaultdict
from contextlib import contextmanager
import copy
from dataclasses import dataclass
import gzip
import json
import os
from pathlib import Path
import re
import shutil
import sys
import zlib

import anvil
from block_entities import entity_id
import build_showcase
import nbt

DATA_VERSION = 4903  # Official 26.2 jar and pristine game-created world.


def parse_state(text):
    match = re.fullmatch(r"([a-z0-9_.-]+:[a-z0-9_./-]+)(?:\[([^\]]*)\])?", text)
    if not match:
        raise ValueError(f"Invalid block state: {text}")
    state = nbt.compound(Name=nbt.Tag(8, match[1]))
    if match[2]:
        properties = {}
        for entry in match[2].split(","):
            key, separator, value = entry.partition("=")
            if not separator or not key or not value or key in properties:
                raise ValueError(f"Invalid block properties: {text}")
            properties[key] = nbt.Tag(8, value)
        state.value["Properties"] = nbt.Tag(10, properties)
    return state


def sign_text(messages):
    """26.2 SignText.DIRECT_CODEC uses four native Component strings, not JSON."""
    if isinstance(messages, str):
        messages = [messages]
    if len(messages) > 4 or any(not isinstance(message, str) for message in messages):
        raise ValueError("Sign text must contain at most four plain text lines")
    return nbt.compound(messages=nbt.list_tag([nbt.Tag(8, message) for message in
                        list(messages) + [""] * (4 - len(messages))], 8),
                        color=nbt.Tag(8, "black"), has_glowing_text=nbt.Tag(1, 0))


def block_entity(identifier, position, messages=None):
    x, y, z = position
    result = nbt.compound(id=nbt.Tag(8, identifier), x=nbt.Tag(3, x), y=nbt.Tag(3, y),
                          z=nbt.Tag(3, z), keepPacked=nbt.Tag(1, 0))
    if identifier.endswith((":sign", ":hanging_sign", "_sign_block_entity")):
        result.value.update(front_text=sign_text(messages or []), back_text=sign_text([]),
                            is_waxed=nbt.Tag(1, 1))
    return result


@dataclass
class Placement:
    low: tuple
    high: tuple
    state: nbt.Tag
    entity: object = None

    @property
    def count(self):
        return ((self.high[0] - self.low[0] + 1) * (self.high[1] - self.low[1] + 1)
                * (self.high[2] - self.low[2] + 1))

    def contains(self, position):
        return all(a <= p <= b for a, p, b in zip(self.low, position, self.high))


def layout_placements(layout):
    """Same clear, floor, sign, glass and paired-part order as commands(layout)."""
    if layout["minecraftVersion"] != "26.2" or layout["schemaVersion"] != 1:
        raise ValueError("Requires a schemaVersion 1 Minecraft 26.2 layout")
    x, z = layout["origin"]
    layers = layout["layers"]
    if layers:
        width = max(layer["width"] for layer in layers)
        depth = max(layer["depth"] for layer in layers)
        yield Placement((x, layout["ground_top_y"] + 1, z),
                        (x + width - 1, layers[-1]["top_y"], z + depth - 1),
                        parse_state("minecraft:air"))
    for layer in layers:
        if layer["index"] > 1:
            yield Placement((x, layer["floor_y"], z),
                            (x + layer["width"] - 1, layer["floor_y"], z + layer["depth"] - 1),
                            parse_state("minecraft:grass_block[snowy=false]"))
    for layer in layers:
        for group in layer["groups"]:
            if group["sign"] is not None:
                position = tuple(group["sign"])
                messages = group.get("sign_text", [group["material"].replace("_", " ")])
                yield Placement(position, position, parse_state("minecraft:oak_sign[rotation=0,waterlogged=false]"),
                                block_entity("minecraft:sign", position, messages))
            for cell in group["cells"]:
                for position, text in build_showcase.cell_blocks(cell):
                    position = tuple(position)
                    state = parse_state(text)
                    identifier = entity_id(state.value["Name"].value, cell.get("className", ""),
                                           cell.get("blockType", "")) if text != "minecraft:glass" else None
                    entity = block_entity(identifier, position, cell.get("sign_text")) if identifier else None
                    yield Placement(position, position, state, entity)


def make_plan(layout):
    """Split fill rectangles by chunk instead of materializing millions of positions."""
    plan = defaultdict(list)
    for placement in layout_placements(layout):
        x1, y1, z1 = placement.low
        x2, y2, z2 = placement.high
        if not all(isinstance(value, int) for value in placement.low + placement.high):
            raise ValueError("Placement coordinates must be integers")
        if y1 < -64 or y2 >= 320 or x2 < x1 or y2 < y1 or z2 < z1:
            raise ValueError("Invalid placement bounds or Overworld build height")
        for cx in range(x1 // 16, x2 // 16 + 1):
            for cz in range(z1 // 16, z2 // 16 + 1):
                plan[cx, cz].append(Placement((max(x1, cx * 16), y1, max(z1, cz * 16)),
                    (min(x2, cx * 16 + 15), y2, min(z2, cz * 16 + 15)), placement.state, placement.entity))
    return plan


class ChunkEditor:
    def __init__(self, chunk):
        self.chunk = chunk.value
        self.sections = {section.value["Y"].value: section for section in self.chunk["sections"].value}
        self.states = {}

    def section_states(self, y):
        if y not in self.states:
            section = self.sections.get(y)
            if section is None:
                neighbours = [other for other in self.sections if "biomes" in self.sections[other].value]
                if not neighbours:
                    raise ValueError("Cannot create a section without a neighbour biome container")
                neighbour = self.sections[min(neighbours, key=lambda other: abs(other - y))]
                section = nbt.compound(Y=nbt.Tag(1, y), biomes=copy.deepcopy(neighbour.value["biomes"]))
                self.sections[y] = section
            container = section.value.get("block_states")
            self.states[y] = (anvil.decode_states(container) if container else
                              [parse_state("minecraft:air")] * 4096)
        return self.states[y]

    def apply(self, placements):
        for placement in placements:
            x1, y1, z1 = placement.low
            x2, y2, z2 = placement.high
            for y in range(y1, y2 + 1):
                states = self.section_states(y // 16)
                for z in range(z1, z2 + 1):
                    start = anvil.block_index(x1, y, z)
                    states[start:start + x2 - x1 + 1] = [placement.state] * (x2 - x1 + 1)
        # Each fill also replaces block entities and scheduled ticks in its volume.
        def position(entry):
            return tuple(entry.value[key].value for key in ("x", "y", "z"))

        def replaced(entry):
            pos = position(entry)
            return any(placement.contains(pos) for placement in placements)

        existing = self.chunk.get("block_entities", nbt.list_tag()).value
        entities = [entry for entry in existing if not replaced(entry)]
        added = {}
        for placement in placements:
            for pos in list(added):
                if placement.contains(pos):
                    del added[pos]
            if placement.entity is not None:
                added[placement.low] = placement.entity
        entities.extend(added.values())
        self.chunk["block_entities"] = nbt.list_tag(entities)
        for key in ("block_ticks", "fluid_ticks"):
            if key in self.chunk:
                ticks = [entry for entry in self.chunk[key].value if not replaced(entry)]
                self.chunk[key] = nbt.list_tag(ticks)
        for y, states in self.states.items():
            section = self.sections[y].value
            section["block_states"] = anvil.encode_states(states)
            section.pop("BlockLight", None)
            section.pop("SkyLight", None)
        self.chunk["sections"] = nbt.list_tag([section for _, section in sorted(self.sections.items())])
        self.chunk["isLightOn"] = nbt.Tag(1, 0)
        self.chunk.pop("Heightmaps", None)
        return len(added)


@contextmanager
def closed_world(world):
    """Hold the game's session.lock for the whole operation, including dry runs.

    Java DirectoryLock uses a FileChannel lock, not the contents of this file.
    An open save holds this lock even when paused or on the in-game menu.
    """
    path = world / "session.lock"
    try:
        handle = path.open("r+b")  # Do not create/change the game's lock file.
    except OSError as error:
        raise ValueError("Cannot open session.lock; close the game for this save") from error
    acquired = False
    try:
        try:
            if os.name == "nt":
                import msvcrt
                msvcrt.locking(handle.fileno(), msvcrt.LK_NBLCK, 1)
            else:
                import fcntl
                fcntl.lockf(handle.fileno(), fcntl.LOCK_EX | fcntl.LOCK_NB)
            acquired = True
        except OSError as error:
            raise ValueError("session.lock is held; the game/server is running for this save") from error
        yield
    finally:
        if acquired:
            handle.seek(0)
            if os.name == "nt":
                msvcrt.locking(handle.fileno(), msvcrt.LK_UNLCK, 1)
            else:
                fcntl.lockf(handle.fileno(), fcntl.LOCK_UN)
        handle.close()


def overworld_region(world):
    modern = world / "dimensions/minecraft/overworld/region"
    legacy = world / "region"
    if modern.is_dir():
        return modern
    if legacy.is_dir():
        return legacy
    raise ValueError("No existing Overworld region folder; create and explore this world in the game first")


def ensure_backup(region_dir, backup_dir, explicit):
    source, destination = region_dir.resolve(), backup_dir.resolve()
    if source == destination or source in destination.parents or destination in source.parents:
        raise ValueError("Backup directory must be separate from the region folder")
    if destination.exists():
        if not explicit:
            raise ValueError("Default backup already exists; specify --backup-dir to reuse it or choose a new backup")
        if not destination.is_dir() or not any(destination.glob("*.mca")):
            raise ValueError("Existing --backup-dir must be a backup copy of the region folder")
        if any(not (destination / file.name).is_file() for file in source.iterdir() if file.is_file()):
            raise ValueError("Existing backup is missing region files")
    else:
        shutil.copytree(source, destination)


def apply_world(world, layout, dry_run=False, backup_dir=None, progress=None):
    world = Path(world).resolve()
    plan = make_plan(layout)
    with closed_world(world):
        _, level = nbt.loads(gzip.decompress((world / "level.dat").read_bytes()))
        version = level.value["Data"].value["DataVersion"].value
        if version != DATA_VERSION:
            raise ValueError(f"World DataVersion {version}; expected Minecraft 26.2 ({DATA_VERSION})")
        region_dir = overworld_region(world)
        grouped = defaultdict(list)
        for coords, placements in plan.items():
            cx, cz = coords
            grouped[cx // 32, cz // 32].append((coords, placements))
        report = {"dry_run": dry_run, "data_version": version, "chunk_data_versions": [],
                  "placements": sum(p.count for ps in plan.values() for p in ps),
                  "applied_placements": 0, "skipped_placements": 0, "edited_chunks": 0,
                  "block_entities_added": 0, "missing_chunks": [], "edited_regions": 0}
        prepared = []
        # Finish validation/encoding of every affected region before backing up or writing.
        for (rx, rz), entries in sorted(grouped.items()):
            path = region_dir / f"r.{rx}.{rz}.mca"
            region = anvil.Region.read(path) if path.exists() else None
            changed = False
            for (cx, cz), placements in entries:
                loaded = region.get_chunk(cx, cz) if region else None
                count = sum(p.count for p in placements)
                if loaded is None:
                    report["skipped_placements"] += count
                    report["missing_chunks"].append([cx, cz])
                    continue
                name, chunk = loaded
                values = chunk.value
                chunk_version = values["DataVersion"].value
                if chunk_version not in report["chunk_data_versions"]:
                    report["chunk_data_versions"].append(chunk_version)
                if chunk_version != version:
                    raise ValueError(f"Chunk {cx},{cz} DataVersion {chunk_version} differs from world {version}")
                if (values["xPos"].value, values["zPos"].value) != (cx, cz):
                    raise ValueError(f"Chunk coordinates disagree with region slot {cx},{cz}")
                if values["Status"].value not in ("minecraft:full", "full"):
                    raise ValueError(f"Chunk {cx},{cz} is not fully generated; visit it in the game first")
                report["block_entities_added"] += ChunkEditor(chunk).apply(placements)
                region.set_chunk(cx, cz, chunk, name)
                report["applied_placements"] += count
                report["edited_chunks"] += 1
                changed = True
            if changed:
                region.to_bytes()  # Also preflight sector sizes/addressing on dry runs.
                prepared.append((path, region))
            if progress:
                progress(f"Prepared r.{rx}.{rz}: {report['edited_chunks']} chunks, "
                         f"{report['applied_placements']} placements, {report['skipped_placements']} skipped")
        report["edited_regions"] = len(prepared)
        if not dry_run and prepared:
            backup = Path(backup_dir) if backup_dir is not None else world / "showcase-region-backup"
            ensure_backup(region_dir, backup, backup_dir is not None)
            report["backup_dir"] = str(backup.resolve())
            for path, region in prepared:
                region.write(path)
        return report


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("world", type=Path)
    parser.add_argument("layout", type=Path)
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--backup-dir", type=Path)
    args = parser.parse_args()
    try:
        layout = json.loads(args.layout.read_text(encoding="utf-8-sig"))
        report = apply_world(args.world, layout, args.dry_run, args.backup_dir,
                             lambda message: print(message, file=sys.stderr, flush=True))
        print(json.dumps(report, indent=2))
    except (OSError, ValueError, KeyError, TypeError, zlib.error) as error:
        parser.exit(2, f"showcase: {error}\n")


if __name__ == "__main__":
    main()
