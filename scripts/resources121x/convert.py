"""Generate 1.21.1 resources from the frozen 1.18.2 tree, without editing it.

Usage: python -B scripts/resources121x/convert.py MINECRAFT_CLIENT_JAR
Optional --source and --output select fixture trees. Unsupported loader data
and stale binary recipe caches are dropped with a report on stdout.
"""
import argparse
import copy
import importlib.util
import json
from pathlib import Path
import sys
import zipfile

sys.dont_write_bytecode = True
REPO = Path(__file__).resolve().parents[2]


def shared_module(name):
    spec = importlib.util.spec_from_file_location(
        "resources26x_" + name, REPO / "scripts/resources26x" / (name + ".py"))
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


shared = shared_module("convert")
load, write, door_parent, door_state = (
    shared.load, shared.write, shared.door_parent, shared.door_state)
FOLDERS = shared.FOLDERS | {"structures": "structure"}
TAG_FOLDERS = shared.TAG_FOLDERS | {"game_events": "game_event"}
# The shared folder/common-tag and door-parent mappings already apply in
# 1.21.1. Its recipe ingredients and advancement backgrounds differ (handled
# below); quartz_pillar_side, iron_chain and item definitions are later changes
# and must not be copied from the 26.2 conversion pipeline.
SPECIAL_ITEMS = ()  # 1.21.1 uses ordinary item models, including BEWLR items.
# A single pack.mcmeta serves both packs. 1.21.1 supports ranges: resource
# format 34 and data format 48 must both be included in supported_formats.
PACK = {"pack": {"description": "Buildscape resources for Minecraft 1.21.1",
                 "pack_format": 34,
                 "supported_formats": {"min_inclusive": 34, "max_inclusive": 48}}}

# LootItemFunctions' vanilla registry in 1.21.1, including functions absent from
# the source pack. Do not infer support from which functions vanilla tables use.
LOOT_FUNCTIONS = frozenset("minecraft:" + name for name in """
    set_count set_item enchant_with_levels enchant_randomly set_enchantments
    furnace_smelt enchanted_count_increase set_damage set_attributes set_name exploration_map
    set_stew_effect banner_duplicate set_lore fill_player_head copy_name copy_state
    set_banner_pattern set_potion set_contents set_loot_table set_instrument
    set_book_cover set_writable_book_pages set_written_book_pages filtered sequence
    reference set_custom_data copy_custom_data set_components copy_components modify_contents
    set_fireworks set_firework_explosion set_ominous_bottle_amplifier toggle_tooltips
    apply_bonus explosion_decay limit_count
""".split())


def snbt_parts(text, delimiter):
    """Split at top level only, preserving nested SNBT and quoted delimiters."""
    parts, start, nesting, quote, escaped = [], 0, [], None, False
    for index, char in enumerate(text):
        if quote:
            if escaped:
                escaped = False
            elif char == "\\":
                escaped = True
            elif char == quote:
                quote = None
        elif char in "\"'":
            quote = char
        elif char in "{[":
            nesting.append(char)
        elif char in "}]":
            if not nesting or nesting.pop() != {"}": "{", "]": "["}[char]:
                raise ValueError("Unbalanced SNBT: " + text)
        elif char == delimiter and not nesting:
            parts.append(text[start:index].strip())
            start = index + 1
    if quote or nesting:
        raise ValueError("Unterminated SNBT: " + text)
    parts.append(text[start:].strip())
    return parts


def snbt_string(text):
    text = text.strip()
    if not text:
        raise ValueError("Empty SNBT string")
    if text[0] not in "\"'":
        if any(char.isspace() or char in "{}[],:\\\"'" for char in text):
            raise ValueError("Expected scalar SNBT string: " + text)
        return text
    quote, result, index = text[0], [], 1
    while index < len(text):
        char = text[index]
        if char == quote:
            if index != len(text) - 1:
                raise ValueError("Trailing SNBT string content: " + text)
            return "".join(result)
        if char == "\\":
            index += 1
            if index >= len(text) or text[index] not in (quote, "\\"):
                raise ValueError("Invalid SNBT escape: " + text)
            char = text[index]
        result.append(char)
        index += 1
    raise ValueError("Unterminated SNBT string: " + text)


def snbt_compound(text):
    """Return decoded keys and raw values; custom data retains its NBT types."""
    text = text.strip()
    if not text.startswith("{") or not text.endswith("}"):
        raise ValueError("Expected SNBT compound: " + text)
    result = {}
    for entry in snbt_parts(text[1:-1], ",") if text[1:-1].strip() else []:
        fields = snbt_parts(entry, ":")
        if len(fields) != 2 or not fields[1]:
            raise ValueError("Invalid SNBT compound entry: " + entry)
        key = snbt_string(fields[0])
        if key in result:
            raise ValueError("Duplicate SNBT key: " + key)
        result[key] = fields[1]
    return result


def set_nbt_functions(function):
    root = snbt_compound(function["tag"])
    state = root.pop("BlockStateTag", None)
    common = {key: copy.deepcopy(value) for key, value in function.items()
              if key not in {"function", "tag"}}
    result = []
    if state is not None:
        properties = {key: snbt_string(value) for key, value in snbt_compound(state).items()}
        result.append(dict(copy.deepcopy(common), function="minecraft:set_components",
                           components={"minecraft:block_state": properties}))
    if root or state is None:
        tag = "{" + ",".join('"' + key.replace("\\", "\\\\").replace('"', '\\"') + '":' + value
                             for key, value in root.items()) + "}"
        result.append(dict(copy.deepcopy(common), function="minecraft:set_custom_data", tag=tag))
    return result


def loot(value):
    """Apply common migrations, expanding legacy functions without nested lists."""
    def expand(value):
        if isinstance(value, list):
            result = []
            for child in value:
                if isinstance(child, dict) and child.get("function") in {"minecraft:set_nbt", "set_nbt"}:
                    result.extend(set_nbt_functions({key: expand(v) for key, v in child.items()}))
                else:
                    result.append(expand(child))
            return result
        if isinstance(value, dict):
            result = {key: expand(child) for key, child in value.items()}
            if result.get("function") in {"minecraft:set_nbt", "set_nbt"}:
                # A filtered function holds one function rather than a list.
                functions = set_nbt_functions(result)
                return functions[0] if len(functions) == 1 else {
                    "function": "minecraft:sequence", "functions": functions}
            return result
        return value

    return expand(shared.loot(value))


def unsupported_loot_functions(data):
    return sorted({str(obj["function"]) for obj in objects(data) if "function" in obj
                   and (not isinstance(obj["function"], str)
                        or (obj["function"] if ":" in obj["function"] else "minecraft:" + obj["function"])
                        not in LOOT_FUNCTIONS)})


def removals(path=None):
    manifest = load(Path(path) if path else REPO / "sync/removals121x.json")
    if manifest["schema_version"] != 1 or manifest["target"] != "mc1.21.1":
        raise ValueError("Expected 1.21.1 removal manifest")
    result = {}
    for entry in manifest["removed_ids"]:
        value = entry["vanilla_equivalent"]
        if not isinstance(value, str) or not value.startswith("minecraft:"):
            raise ValueError("Every removal must have a vanilla equivalent")
        if entry["id"] in result:
            raise ValueError("Duplicate removal: " + entry["id"])
        result[entry["id"]] = value
    return result


def converted_path(rel):
    result = shared.converted_path(rel)
    parts = list(result.parts)
    if len(parts) > 2 and parts[0] == "data":
        parts[2] = FOLDERS.get(parts[2], parts[2])
        if parts[2] == "tags" and len(parts) > 4:
            parts[3] = TAG_FOLDERS.get(parts[3], parts[3])
    return Path(*parts)


def references(value, removed, vanilla_files=frozenset(), aliases=None):
    """Rewrite exact registry ids, compact aliases and extant vanilla assets.

    Asset rewrites require a real vanilla counterpart; custom template models
    and textures reused by retained blocks keep their original references.
    """
    if isinstance(value, list):
        return [references(v, removed, vanilla_files, aliases) for v in value]
    if isinstance(value, dict):
        return {k: references(v, removed, vanilla_files, aliases) for k, v in value.items()}
    if not isinstance(value, str):
        return value
    if value in removed:
        return removed[value]
    prefix = "#" if value.startswith("#") else ""
    raw = value.removeprefix("#")
    ns, sep, name = raw.partition(":")
    resolved = (aliases or {}).get(ns, ns + ":") + name if sep else raw
    if not prefix and resolved in removed:
        return removed[resolved]
    if raw.startswith("buildscape:"):
        directory, slash, leaf = name.rpartition("/")
        for old, new in sorted(removed.items(), key=lambda pair: len(pair[0]), reverse=True):
            old_name, new_name = old.split(":")[1], new.split(":")[1]
            if leaf != old_name and not leaf.startswith(old_name + "_"):
                continue
            target = directory + slash + new_name + leaf[len(old_name):]
            for kind, suffix in (("models", ".json"), ("textures", ".png")):
                if "assets/minecraft/" + kind + "/" + target + suffix in vanilla_files:
                    return prefix + "minecraft:" + target
    value = {"minecraft:grass": "minecraft:short_grass",
             "minecraft:block/grass": "minecraft:block/short_grass"}.get(value, value)
    # Never turn Forge serializer/condition types into common tags.
    if raw.startswith("forge:") and not prefix:
        return value
    return shared.references(value)


def recipe(data):
    """Reuse result/pattern normalization, restoring 1.21.1 ingredients."""
    result = shared.recipe(data)
    for key in ("ingredient", "ingredients", "base", "addition", "template", "key"):
        if key in data:
            result[key] = copy.deepcopy(data[key])
    if isinstance(result.get("result"), dict) and "id" in result["result"]:
        result["result"].setdefault("count", 1)
    if result.get("type") == "minecraft:smithing":
        result["type"] = "minecraft:smithing_transform"
        result["template"] = {"item": "minecraft:netherite_upgrade_smithing_template"}
    return result


def advancement(data):
    result = copy.deepcopy(data)
    icon = result.get("display", {}).get("icon", {})
    if "item" in icon:
        icon["id"] = icon.pop("item")
    # Unlike 26.2, 1.21.1 still expects textures/...png backgrounds.
    return result


def model(data):
    """Migrate face metadata and remove legacy identity element rotations."""
    result = copy.deepcopy(data)
    for element in result.get("elements", []):
        rotation = element.get("rotation")
        # Old exports use x/y/z instead of angle/axis. Only discard identities;
        # a nonzero rotation without an axis must be diagnosed, never guessed.
        if (isinstance(rotation, dict) and "axis" not in rotation
                and rotation.get("angle", 0) == 0
                and all(rotation.get(axis, 0) == 0 for axis in ("x", "y", "z"))):
            del element["rotation"]
        for face in element.get("faces", {}).values():
            if "forge_data" in face:
                legacy = face.pop("forge_data")
                if "neoforge_data" in face and face["neoforge_data"] != legacy:
                    raise ValueError("Conflicting forge_data and neoforge_data")
                face["neoforge_data"] = legacy
    if "parent" in result:
        result["parent"] = door_parent(result["parent"])
    return result


def translation_keys(vanilla_lang, resources):
    """Subtitle lookup: vanilla English plus the pack's own language files."""
    keys = set(vanilla_lang)
    for path, data in resources.items():
        parts = Path(path).parts
        if (len(parts) == 4 and parts[:3] == ("assets", "buildscape", "lang")
                and parts[3].endswith(".json") and isinstance(data, dict)):
            keys.update(data)
    return keys


def prune_subtitles(generated, keys, report):
    for rel, data in generated.items():
        if rel.parts[0] != "assets" or rel.name != "sounds.json":
            continue
        for event, definition in data.items():
            if "subtitle" in definition and definition["subtitle"] not in keys:
                subtitle = definition.pop("subtitle")
                report.append(str(rel) + ": " + event + ": dropped unknown subtitle " + str(subtitle))


def objects(value):
    if isinstance(value, dict):
        yield value
        for child in value.values():
            yield from objects(child)
    elif isinstance(value, list):
        for child in value:
            yield from objects(child)


def unsupported_loader_data(data):
    for obj in objects(data):
        if "forge:conditions" in obj or "neoforge:conditions" in obj:
            return "legacy Forge conditions"
        for key in ("type", "condition", "function"):
            value = obj.get(key, "")
            if isinstance(value, str) and value.startswith(("forge:", "neoforge:")):
                return "loader-specific codec " + value
    return None


def owned_file(rel, removed):
    """Exclude registrations, not custom variants that share a name prefix."""
    parts = rel.parts
    if len(parts) < 3 or parts[1] != "buildscape":
        return False
    name = rel.stem
    if "buildscape:" + name in removed:
        return parts[2] in {"blockstates", "models", "textures", "recipe", "loot_table"}
    if parts[2] == "models":
        suffixes = ("bottom", "bottom_rh", "top", "top_rh", "bottom_left",
                    "bottom_right", "top_left", "top_right", "bottom_left_open",
                    "bottom_right_open", "top_left_open", "top_right_open", "open",
                    "inventory", "on", "powered", "on_powered", "item")
        return any(name == key.split(":")[1] + "_" + suffix
                   for key in removed for suffix in suffixes)
    return False


def compact_pack(data, removed, vanilla_files, report=None):
    aliases = data.get("aliases", {})
    result = copy.deepcopy(data)
    for key, rows in list(result.items()):
        if not isinstance(rows, list) or key == "wood_families":
            continue
        def retired(row):
            if not isinstance(row, list) or not row or not isinstance(row[0], str):
                return False
            ns, sep, name = row[0].partition(":")
            return (aliases.get(ns, ns + ":") + name if sep else row[0]) in removed
        kept = []
        for index, row in enumerate(rows):
            reason = unsupported_loader_data(row)
            if reason or retired(row):
                if report is not None:
                    report.append("compact " + key + "[" + str(index) + "]: dropped " +
                                  (reason or "replaced by vanilla"))
                continue
            kept.append(row)
        result[key] = kept
    result = references(result, removed, vanilla_files, aliases)
    if result.get("aliases", {}).get("F") == "forge:":
        result["aliases"]["F"] = "c:"
    if "_comment" in result:
        result["_comment"] = result["_comment"].replace("F=forge:", "F=c:")
    return result


def holder_ids(value):
    """Read holder ids from lists; inline objects are visited by objects()."""
    if isinstance(value, str):
        yield value
    elif isinstance(value, list):
        for child in value:
            yield from holder_ids(child)


def data_path(kind, value):
    raw = value.removeprefix("#")
    ns, sep, name = raw.partition(":")
    if not sep:
        ns, name = "minecraft", ns
    return Path("data", ns, "tags" if value.startswith("#") else "", kind, name + ".json")


def data_references(rel, data):
    """File-backed advancement/worldgen dependencies, without a client jar."""
    parts = rel.parts
    if len(parts) < 4 or parts[0] != "data" or not isinstance(data, dict):
        return
    if parts[2] == "advancement":
        for value in data.get("rewards", {}).get("recipes", []):
            yield "recipe", value
        if "parent" in data:
            yield "advancement", data["parent"]
    elif parts[2:4] == ("tags", "worldgen"):
        kind = "/".join(parts[3:5])
        for value in data.get("values", []):
            yield kind, value.get("id") if isinstance(value, dict) else value
    elif parts[2] == "worldgen":
        for obj in objects(data):
            if isinstance(obj.get("feature"), str):
                # Selector entries contain placed features; placed_feature's
                # root feature is a configured feature (or an inline object).
                kind = "configured_feature" if parts[3] == "placed_feature" and obj is data else "placed_feature"
                yield "worldgen/" + kind, obj["feature"]
            for value in holder_ids(obj.get("features", [])):
                yield "worldgen/placed_feature", value
            if isinstance(obj.get("default"), str):
                yield "worldgen/placed_feature", obj["default"]
    elif parts[2] in {"biome_modifier", "forge", "neoforge"}:
        for value in holder_ids(data.get("features", [])):
            yield "worldgen/placed_feature", value


def missing_data_references(rel, data, files):
    return [(kind, value) for kind, value in data_references(rel, data)
            if isinstance(value, str) and value.removeprefix("#").startswith("buildscape:")
            and data_path(kind, value) not in files]


def prune_dependencies(generated, report):
    """Remove orphan unlocks and worldgen chains after loader/removal filtering.

    Compact packs are runtime recipes, not vanilla recipe resources. They cannot
    satisfy a vanilla advancement reward. Keep their recipes and drop the unlock
    with a report instead. Repeat to handle parents and nested worldgen tags.
    """
    while True:
        changed = False
        for rel, data in list(generated.items()):
            missing = missing_data_references(rel, data, generated)
            if not missing:
                continue
            if rel.parts[2:4] == ("tags", "worldgen"):
                ids = {value for _, value in missing}
                data["values"] = [value for value in data["values"]
                                  if (value.get("id") if isinstance(value, dict) else value) not in ids]
                if data["values"]:
                    report.append(str(rel) + ": pruned removed worldgen entries " + ", ".join(sorted(ids)))
                    changed = True
                    continue
            del generated[rel]
            report.append(str(rel) + ": dropped missing dependency " +
                          ", ".join(kind + " " + value for kind, value in missing))
            changed = True
        if not changed:
            return


def convert(source, output, jar, manifest=None):
    source, output = Path(source).resolve(), Path(output).resolve()
    if source == output or source in output.parents or output in source.parents:
        raise ValueError("Source and output trees must be disjoint")
    removed = removals(manifest)
    report, generated = [], {}
    with zipfile.ZipFile(jar) as vanilla:
        if json.loads(vanilla.read("version.json")).get("id") != "1.21.1":
            raise ValueError("Expected Minecraft 1.21.1 client jar")
        vanilla_files = set(vanilla.namelist())
        vanilla_lang = json.loads(vanilla.read("assets/minecraft/lang/en_us.json"))
        for key, value in removed.items():
            name = value.split(":")[1]
            if not any("assets/minecraft/" + kind + "/" + name + ".json" in vanilla_files
                       for kind in ("blockstates", "models/item")):
                raise ValueError("Unknown vanilla equivalent for " + key + ": " + value)
        for path in sorted(source.rglob("*")):
            if not path.is_file():
                continue
            rel = converted_path(path.relative_to(source))
            if rel.parts[0] not in {"assets", "data"}:
                continue  # Loader metadata and mixin configs are cluster-owned.
            reason = None
            if owned_file(rel, removed):
                reason = "replaced by vanilla"
                # Some retained custom models share old textures with the removed
                # block. Keep a support texture if vanilla has no matching asset.
                if "textures" in rel.parts:
                    asset = "buildscape:" + "/".join(rel.parts[3:]).removesuffix(".png")
                    if references(asset, removed, vanilla_files) == asset:
                        reason = None
            elif "loot_modifiers" in rel.parts:
                reason = "Forge global loot modifiers (not portable across loaders)"
            elif path.suffix == ".bscb":
                reason = "stale 1.18.2 binary recipe cache; use converted JSON pack"
            if reason:
                report.append(str(rel) + ": dropped " + reason)
                continue
            if path.suffix == ".json":
                data = load(path)
                reason = (unsupported_loader_data(data)
                          if rel.parts[0] == "data" and "recipes_pack" not in rel.parts else None)
                if reason:
                    report.append(str(rel) + ": dropped " + reason)
                    continue
                if "recipes_pack" in rel.parts:
                    data = compact_pack(data, removed, vanilla_files, report)
                else:
                    data = references(data, removed, vanilla_files)
                if "recipe" in rel.parts:
                    data = recipe(data)
                elif "loot_table" in rel.parts:
                    data = loot(data)
                elif "advancement" in rel.parts:
                    data = advancement(data)
                elif "models" in rel.parts:
                    data = model(data)
                elif "lang" in rel.parts:
                    data = {k: v for k, v in data.items() if not any(
                        k in {"block." + old.replace(":", "."), "item." + old.replace(":", ".")}
                        for old in removed)}
                if rel in generated:
                    if "tags" in rel.parts:
                        previous = generated[rel]
                        previous["values"] += [v for v in data["values"] if v not in previous["values"]]
                        previous["replace"] = previous.get("replace", False) or data.get("replace", False)
                        continue
                    if generated[rel] != data:
                        raise ValueError("Conflicting converted resource: " + str(rel))
                generated[rel] = data
            else:
                content = path.read_bytes()
                if path.name == "creative_tab_order.txt":
                    lines = content.decode("utf-8-sig").splitlines()
                    content = ("\n".join(line for line in lines if
                        "buildscape:" + line.strip().removeprefix("buildscape:") not in removed) + "\n").encode()
                generated[rel] = content
        template = json.loads(vanilla.read("assets/minecraft/blockstates/oak_door.json"))
        for rel, data in list(generated.items()):
            if rel.parts[:3] != ("assets", "buildscape", "blockstates"):
                continue
            result = door_state(data, rel.stem, template)
            if not result:
                continue
            generated[rel], models = result
            base = Path("assets/buildscape/models/block") / (rel.stem + "_bottom.json")
            if base not in generated and rel.stem.startswith("waxed_"):
                base = base.with_name(rel.stem.removeprefix("waxed_") + "_bottom.json")
            door_bottom = generated.get(base)
            if door_bottom is None:
                raise ValueError("Missing retained door template: " + str(base))
            for suffix, parent in models.items():
                generated[base.with_name(rel.stem + "_" + suffix + ".json")] = dict(copy.deepcopy(door_bottom), parent=parent)
        for rel_str in ("data/minecraft/loot_table/chests/end_city_treasure.json",
                        "data/minecraft/loot_table/chests/woodland_mansion.json",
                        "data/minecraft/loot_table/gameplay/sniffer_digging.json"):
            rel = Path(rel_str)
            if rel in generated:
                bonuses = [pool for pool in generated[rel]["pools"] if "buildscape:" in json.dumps(pool)]
                generated[rel] = json.loads(vanilla.read(rel.as_posix()))
                generated[rel]["pools"].extend(bonuses)
    prune_subtitles(generated, translation_keys(vanilla_lang, generated), report)
    prune_dependencies(generated, report)
    problems = [str(rel) + ": unsupported 1.21.1 loot function " + function
                for rel, data in generated.items() if "loot_table" in rel.parts
                for function in unsupported_loot_functions(data)]
    if problems:
        raise ValueError("\n".join(problems))
    generated[Path("pack.mcmeta")] = PACK
    changed = 0
    for rel, data in sorted(generated.items()):
        target = output / rel
        if isinstance(data, bytes):
            if not target.exists() or target.read_bytes() != data:
                target.parent.mkdir(parents=True, exist_ok=True)
                target.write_bytes(data)
                changed += 1
        else:
            changed += write(target, data)
    # Prune only generator-owned asset/data files. Preserve cluster mixin configs.
    for name in ("assets", "data"):
        for path in sorted((output / name).rglob("*"), key=lambda p: len(p.parts), reverse=True):
            if path.is_file() and path.relative_to(output) not in generated:
                path.unlink()
                changed += 1
            elif path.is_dir() and not any(path.iterdir()):
                path.rmdir()
    for line in report:
        print(line)
    print("Generated {} resources; {} changes; {} dropped source files".format(len(generated), changed, len(report)))
    return changed


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("mc_jar", type=Path)
    parser.add_argument("--source", type=Path, default=REPO / "common/src/v118x/resources")
    parser.add_argument("--output", type=Path, default=REPO / "common/src/v121x/resources")
    args = parser.parse_args()
    convert(args.source, args.output, args.mc_jar)


if __name__ == "__main__":
    main()
