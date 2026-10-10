#!/usr/bin/env python3
"""Deterministic Minecraft 26.2 showcase layout and bounded command files (stdlib)."""

import argparse
from collections import defaultdict
import json
import math
from pathlib import Path
import re


CATEGORIES = ("woods", "stones", "metals", "colours", "nature", "misc")
WOODS = ("oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove",
         "cherry", "bamboo", "crimson", "warped", "pale_oak", "poplar", "ashpen",
         "ashenking")
STONES = ("stone", "cobblestone", "deepslate", "tuff", "calcite", "granite",
          "diorite", "andesite", "sandstone", "red_sandstone", "blackstone",
          "basalt", "prismarine", "quartz", "amethyst", "mud", "brick", "bricks",
          "nether_brick", "nether_bricks", "end_stone", "purpur", "obsidian",
          "ice", "snow", "icicle", "glass", "resin", "sulfur", "dripstone",
          "bedrock", "sand", "gravel", "dirt", "netherrack", "end_stone_brick")
METALS = ("copper", "steel", "flaming_steel", "iron", "gold", "golden",
          "diamond", "emerald", "netherite", "lapis", "redstone", "coal")
COLOURS = ("white", "orange", "magenta", "light_blue", "yellow", "lime", "pink",
           "gray", "light_gray", "cyan", "purple", "blue", "brown", "green",
           "red", "black")
NATURE = ("leaves", "hedge", "hedges", "leaf", "leaf_litter", "moss", "flower", "flowers",
          "wildflowers", "dandelion", "rose", "tulip", "orchid", "allium",
          "poppy", "daisy", "cornflower", "lily", "lilac", "peony", "eyeblossom",
          "spore_blossom", "clover", "fern", "grass", "bush", "sapling",
          "propagule", "roots", "vine", "vines", "petal", "petals", "cactus",
          "mushroom", "fungus", "azalea", "monet", "hay", "straw", "sculk")
FACING = {"north": (0, -1), "south": (0, 1), "east": (1, 0), "west": (-1, 0)}
MAX_COMMANDS = 400
# Verified in the official 26.2 GameRules bytecode; legacy camelCase IDs are invalid.
GAME_RULES = (("random_tick_speed", "0"), ("fire_spread_radius_around_player", "0"),
              ("advance_time", "false"), ("advance_weather", "false"),
              ("spawn_mobs", "false"), ("water_source_conversion", "false"),
              ("lava_source_conversion", "false"), ("spread_vines", "false"))


def token_matches(path, token):
    return re.search(r"(?:^|_)" + re.escape(token) + r"(?:_|$)", path) is not None


def material_tokens(blocks):
    """Restrict known tokens to actual IDs, discovering both ashpen colour spellings."""
    paths = [block["id"].split(":")[-1] for block in blocks]
    candidates = {token: category for category, tokens in
                  (("woods", WOODS), ("stones", STONES), ("metals", METALS),
                   ("colours", COLOURS), ("nature", NATURE)) for token in tokens}
    for colour in COLOURS:
        for token in ("ashpen_" + colour, colour + "_ashpen"):
            candidates[token] = "woods"
    return {token: category for token, category in candidates.items()
            if any(token_matches(path, token) for path in paths)}


def material_for(block, tokens):
    path = block["id"].split(":")[-1]
    matches = [token for token in tokens if token_matches(path, token)]
    if token_matches(path, "grass_block"):
        return "stones", "dirt"
    # Leaves, hedges and living plants stay together rather than under their tree/colour.
    nature = [token for token in matches if tokens[token] == "nature"]
    if nature and not token_matches(path, "grass_block") and "grass_block_slab" not in path:
        longest = max(nature, key=lambda token: (len(token), token))
        if longest in ("leaf", "leaf_litter", "leaves", "hedge", "hedges"):
            return "nature", "leaves_and_hedges"
        return "nature", "flowers_and_plants"
    # Dye families share a material even when their name also contains glass or stone.
    colours = [token for token in matches if tokens[token] == "colours"]
    if colours and any(token_matches(path, family) for family in
                       ("wool", "concrete", "terracotta", "wallpaper", "lamp", "lamps")):
        return "colours", max(colours, key=lambda token: (len(token), token))
    if not matches:
        return "misc", "misc"
    token = min(matches, key=lambda item: (-len(item), CATEGORIES.index(tokens[item]), item))
    # Oxidized/waxed copper IDs all match the copper token and remain one material.
    canonical = {"bricks": "brick", "nether_bricks": "nether_brick", "golden": "gold"}
    return tokens[token], canonical.get(token, token)


def state_key(state):
    return tuple(sorted(state.items()))


def block_string(block_id, state):
    suffix = ",".join(f"{key}={value}" for key, value in sorted(state.items()))
    return block_id + (f"[{suffix}]" if suffix else "")


def make_cells(block, include_waterlogged=False, include_lava_logged=False):
    """One display per ordinary state, lower-half state or foot state."""
    states = [{key: str(value).lower() if isinstance(value, bool) else str(value)
               for key, value in state.items()} for state in block["states"]]
    available = {state_key(state) for state in states}
    cells = []
    filtered = 0
    for state in sorted(states, key=state_key):
        if ((state.get("waterlogged") == "true" and not include_waterlogged)
                or (state.get("lava_logged") == "true" and not include_lava_logged)):
            filtered += 1
            continue
        if state.get("half") == "upper" or state.get("part") == "head":
            continue
        parts = [{"offset": [0, 0, 0], "state": state}]
        kind = "block"
        width = depth = height = 1
        if state.get("half") == "lower":
            upper = dict(state, half="upper")
            if state_key(upper) not in available:
                raise ValueError(f"Missing upper partner: {block['id']} {state}")
            parts.append({"offset": [0, 1, 0], "state": upper})
            height, kind = 2, "vertical_pair"
        elif state.get("part") == "foot":
            head = dict(state, part="head")
            if state_key(head) not in available or state.get("facing") not in FACING:
                raise ValueError(f"Missing head partner/facing: {block['id']} {state}")
            dx, dz = FACING[state["facing"]]
            parts[0]["offset"] = [-min(dx, 0), 0, -min(dz, 0)]
            parts.append({"offset": [max(dx, 0), 0, max(dz, 0)], "state": head})
            width, depth, kind = 1 + abs(dx), 1 + abs(dz), "horizontal_pair"
        elif "level" in state and (block["id"] == "buildscape:experience_liquid"
                                   or any(name in block.get("className", "") + block.get("blockType", "")
                                          for name in ("FluidBlock", "LiquidBlock"))):
            parts[0]["offset"] = [1, 0, 1]
            width = depth = 3
            height, kind = 2, "fluid"
        cells.append({"id": block["id"], "state": state, "kind": kind,
                      "className": block.get("className", ""),
                      "blockType": block.get("blockType", ""),
                      "width": width, "depth": depth, "height": height, "parts": parts})
    return cells, filtered


def rectangle(cells, start, width_limit, depth_limit):
    """Pack a contiguous state sequence; reserve one air coordinate between cells/rows.

    Rows have a single cell depth so no short cell gets an oversized row slot.
    Very large groups continue as separate rectangles on later layers.
    """
    x = z = row_depth = used_width = used_depth = 0
    packed = []
    for cell in cells[start:]:
        w, d = cell["width"], cell["depth"]
        if w > width_limit or d > depth_limit:
            raise ValueError("Layer size is too small for a display cell and its sign")
        if x and (x + w > width_limit or d != row_depth):
            z += row_depth + 1
            x = row_depth = 0
        if z + d > depth_limit:
            break
        packed.append(dict(cell, local=[x, z]))
        row_depth = d
        used_width = max(used_width, x + w)
        used_depth = max(used_depth, z + d)
        x += w + 1
    if not packed:
        raise ValueError("No cell fits this layer")
    return packed, used_width, used_depth


def build_layout(dump, origin_x=0, origin_z=0, ground_top_y=-61, layer_size=200,
                 include_waterlogged=False, include_lava_logged=False):
    if not 4 <= layer_size <= 200:
        raise ValueError("layer size must be between 4 and 200")
    if ground_top_y < -64:
        raise ValueError("ground top is below the Overworld build limit (-64)")
    if dump.get("minecraftVersion", "26.2") != "26.2":
        raise ValueError("This generator requires a Minecraft 26.2 dump")
    blocks = dump["blocks"]
    ids = [block["id"] for block in blocks]
    if len(set(ids)) != len(ids) or any(not name.startswith("buildscape:") for name in ids):
        raise ValueError("Block IDs must be unique and namespaced as buildscape:")
    tokens = material_tokens(blocks)
    grouped = defaultdict(list)
    counts = defaultdict(lambda: {"blocks": 0, "cells": 0, "placed_states": 0})
    filtered = 0
    for block in sorted(blocks, key=lambda entry: entry["id"]):
        category, material = material_for(block, tokens)
        cells, excluded = make_cells(block, include_waterlogged, include_lava_logged)
        filtered += excluded
        if cells:
            grouped[category, material].extend(cells)
            count = counts[category, material]
            count["blocks"] += 1
            count["cells"] += len(cells)
            count["placed_states"] += sum(len(cell["parts"]) for cell in cells)
    layers = []
    layer = None
    shelf_x, shelf_z, shelf_depth = 0, 1, 0  # z=0 is the first sign gap.

    def next_layer():
        if layers:
            floor = layers[-1]["top_y"] + 9  # top+1 ... top+8 are air.
        else:
            floor = ground_top_y
        result = {"index": len(layers) + 1, "floor_y": floor, "base_y": floor + 1,
                  "top_y": floor + 1, "width": 0, "depth": 0, "groups": []}
        layers.append(result)
        return result

    for category, material in sorted(grouped, key=lambda key: (CATEGORIES.index(key[0]), key[1])):
        cells = grouped[category, material]
        # Small groups use compact rectangles; large groups use the whole layer width.
        area = sum((cell["width"] + 1) * (cell["depth"] + 1) for cell in cells)
        target_width = min(layer_size, max(max(cell["width"] for cell in cells), math.ceil(math.sqrt(area))))
        cursor = 0
        while cursor < len(cells):
            packed, width, depth = rectangle(cells, cursor, target_width, layer_size - 1)
            if layer is None:
                layer = next_layer()
            if shelf_x + width > layer_size:
                shelf_x = 0
                shelf_z += shelf_depth + 2
                shelf_depth = 0
            if shelf_z + depth > layer_size:
                layer = next_layer()
                shelf_x, shelf_z, shelf_depth = 0, 1, 0
            group = {"material": material, "category": category,
                     "continued": cursor > 0,
                     "bounds": [origin_x + shelf_x, origin_z + shelf_z,
                                origin_x + shelf_x + width - 1, origin_z + shelf_z + depth - 1],
                     "sign": [origin_x + shelf_x, layer["base_y"], origin_z + shelf_z - 1]
                     if cursor == 0 else None, "cells": []}
            for cell in packed:
                x, z = cell.pop("local")
                x += origin_x + shelf_x
                z += origin_z + shelf_z
                cell["position"] = [x, layer["base_y"], z]
                for part in cell["parts"]:
                    dx, dy, dz = part.pop("offset")
                    part["position"] = [x + dx, layer["base_y"] + dy, z + dz]
                layer["top_y"] = max(layer["top_y"], layer["base_y"] + cell["height"] - 1)
                group["cells"].append(cell)
            layer["groups"].append(group)
            layer["width"] = max(layer["width"], shelf_x + width)
            layer["depth"] = max(layer["depth"], shelf_z + depth)
            shelf_x += width + 2
            shelf_depth = max(shelf_depth, depth)
            cursor += len(packed)
    if layers and layers[-1]["top_y"] >= 320:
        raise ValueError("Showcase exceeds Overworld y=319; increase layer size or reduce included states")
    return {"schemaVersion": 1, "minecraftVersion": "26.2", "origin": [origin_x, origin_z],
            "ground_top_y": ground_top_y, "layer_size": layer_size,
            "include_waterlogged": include_waterlogged, "include_lava_logged": include_lava_logged,
            "filtered_states": filtered, "material_tokens": tokens,
            "groups": [dict(category=key[0], material=key[1], **counts[key]) for key in
                       sorted(counts, key=lambda key: (CATEGORIES.index(key[0]), key[1]))],
            "unavailable": dump.get("unavailable", []), "layers": layers}


def fills(x1, y1, z1, x2, y2, z2, block):
    """Tile fills below the vanilla 32,768 block limit without changing a gamerule."""
    for x in range(x1, x2 + 1, 32):
        for z in range(z1, z2 + 1, 32):
            for y in range(y1, y2 + 1, 32):
                yield (f"fill {x} {y} {z} {min(x + 31, x2)} {min(y + 31, y2)} "
                       f"{min(z + 31, z2)} {block} replace")


def setblock(position, block, mode="replace"):
    return "setblock " + " ".join(map(str, position)) + f" {block} {mode}"


def cell_blocks(cell):
    x, y, z = cell["position"]
    if cell["kind"] == "fluid":
        for dx, dy, dz in ((0, 0, 1), (2, 0, 1), (1, 0, 0), (1, 0, 2), (1, 1, 1)):
            yield [x + dx, y + dy, z + dz], "minecraft:glass"
        # The existing ground or the complete upper-layer floor seals the bottom.
    for part in cell["parts"]:
        yield part["position"], block_string(cell["id"], part["state"])


def commands(layout):
    x, z = layout["origin"]
    for layer in layout["layers"]:
        yield f"forceload add {x} {z} {x + layer['width'] - 1} {z + layer['depth'] - 1}"
    for rule, value in GAME_RULES:
        yield f"gamerule minecraft:{rule} {value}"
    yield "time set day"
    # Freeze before mutations as well as at the end: scheduled ticks must not run
    # between command files, especially for fluids, falling blocks and paired blocks.
    yield "tick freeze"
    if layout["layers"]:
        width = max(layer["width"] for layer in layout["layers"])
        depth = max(layer["depth"] for layer in layout["layers"])
        yield from fills(x, layout["ground_top_y"] + 1, z,
                         x + width - 1, layout["layers"][-1]["top_y"], z + depth - 1,
                         "minecraft:air")
    for layer in layout["layers"]:
        if layer["index"] > 1:
            yield from fills(x, layer["floor_y"], z, x + layer["width"] - 1,
                             layer["floor_y"], z + layer["depth"] - 1, "minecraft:grass_block")
    for layer in layout["layers"]:
        for group in layer["groups"]:
            if group["sign"] is not None:
                # 26.2 SignText uses native Component NBT, not JSON encoded strings.
                label = json.dumps(group["material"].replace("_", " "), ensure_ascii=False)
                nbt = '{front_text:{messages:[' + label + ',"","",""],color:"black",has_glowing_text:0b},is_waxed:1b}'
                yield setblock(group["sign"], "minecraft:oak_sign[rotation=0,waterlogged=false]" + nbt)
            for cell in group["cells"]:
                for index, (position, block) in enumerate(cell_blocks(cell)):
                    yield setblock(position, block, "replace" if index == 0 else "strict")
    # The first command per cell uses replace as requested. That mode runs shape
    # updates; a final strict pass restores exact states without neighbour updates.
    # This also preserves unsupported wall/hanging states for static inspection.
    for layer in layout["layers"]:
        for group in layer["groups"]:
            for cell in group["cells"]:
                for position, block in cell_blocks(cell):
                    yield setblock(position, block, "strict")
    yield "tick freeze"


def summary(layout, files, command_count):
    lines = [f"Minecraft 26.2 showcase: {len(layout['layers'])} layers, {command_count} commands, {len(files)} files",
             f"Excluded logged states: {layout['filtered_states']}", "Material groups (blocks / cells / placed states):"]
    for group in layout["groups"]:
        lines.append(f"  {group['category']}/{group['material']}: {group['blocks']} / {group['cells']} / {group['placed_states']}")
    for layer in layout["layers"]:
        lines.append(f"Layer {layer['index']}: {layer['width']} x {layer['depth']}, floor y={layer['floor_y']}, "
                     f"placements y={layer['base_y']}..{layer['top_y']}")
    for block in layout["unavailable"]:
        lines.append(f"Headless: {block['id']}: {block['reason']} (manual coverage: {block.get('coveredManually', False)})")
    lines.extend(("Run command files in numeric order. Some repeated forceload/strict commands may report no change.",
                  "Ticks remain frozen; tick freeze does not persist across restarts.",
                  "Keep ticks frozen for viewing: thawing allows unsupported states and fluids to change."))
    return "\n".join(lines) + "\n"


def write_output(layout, output_dir):
    output = Path(output_dir)
    output.mkdir(parents=True, exist_ok=True)
    # Refuse stale trailing chunks instead of silently leaving an unsafe mixed plan.
    if any(output.glob("commands-*.mcfunction")):
        raise ValueError("Output contains command files; use a fresh output directory")
    filenames, batch, count = [], [], 0

    def flush():
        name = f"commands-{len(filenames) + 1:04d}.mcfunction"
        (output / name).write_text("\n".join(batch) + "\n", encoding="utf-8")
        filenames.append(name)
        batch.clear()

    for command in commands(layout):
        batch.append(command)
        count += 1
        if len(batch) == MAX_COMMANDS:
            flush()
    if batch:
        flush()
    layout["command_files"] = filenames
    layout["command_count"] = count
    (output / "layout.json").write_text(json.dumps(layout, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    report = summary(layout, filenames, count)
    (output / "summary.txt").write_text(report, encoding="utf-8")
    return report


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("dump", type=Path)
    parser.add_argument("output", type=Path)
    parser.add_argument("--origin-x", type=int, default=0)
    parser.add_argument("--origin-z", type=int, default=0)
    parser.add_argument("--ground-top-y", type=int, default=-61)
    parser.add_argument("--layer-size", type=int, default=200)
    parser.add_argument("--include-waterlogged", action="store_true")
    parser.add_argument("--include-lava-logged", action="store_true")
    args = parser.parse_args()
    try:
        dump = json.loads(args.dump.read_text(encoding="utf-8"))
        layout = build_layout(dump, args.origin_x, args.origin_z, args.ground_top_y,
                              args.layer_size, args.include_waterlogged, args.include_lava_logged)
        print(write_output(layout, args.output), end="")
    except (OSError, ValueError, KeyError, TypeError) as error:
        parser.exit(2, f"showcase: {error}\n")


if __name__ == "__main__":
    main()
