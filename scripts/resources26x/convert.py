"""Convert the shared resource tree in place using the supplied vanilla jar.

Only changed JSON is serialized; existing newline conventions are retained.
The compact recipes_pack is a private Java format, not a vanilla recipe folder.

--profile selects the target version: 26.2 (default, the original behaviour) or
26.3, which adds the 26.3 data formats on top (see "26.3 formats" below). For 26.3,
--derive-from BASE treats the resources root as an overlay folder: BASE (the 26.2
resources) is converted in a temporary copy and only the files that change are written,
e.g.  convert.py --profile 26.3 --derive-from common/src/main/resources \\
common/src/v263/resources minecraft-26.3-client.jar
"""
import argparse
import copy
import json
from pathlib import Path
import re
import tempfile
import zipfile

FOLDERS = {"loot_tables": "loot_table", "recipes": "recipe",
           "advancements": "advancement"}
TAG_FOLDERS = {"blocks": "block", "items": "item",
               "entity_types": "entity_type", "fluids": "fluid"}
# Common-tag names shared by modern loaders. Material tags retain their names.
COMMON_TAGS = {"glass": "glass_blocks", "stained_glass": "glass_blocks/dyed",
               "stained_glass_panes": "glass_panes/dyed", "stone": "stones",
               "sand": "sands"}
# The copper chests were retired on 26.2 (vanilla has them), so only the glass jar remains special.
SPECIAL_ITEMS = (
    "template_glass_jar",
)
PACK = {"pack": {"description": "Buildscape resources for Minecraft 26.2",
                  "min_format": [88, 0], "max_format": [107, 1]}}
PACK_263 = {"pack": {"description": "Buildscape resources for Minecraft 26.3",
                     "min_format": [97, 1], "max_format": [121, 0]}}

# Explicit version profiles. 26.2 is the default and is exactly the original
# behaviour. 26.3 adds the data-format step in the "26.3 formats" section.
# pack_version is checked against the jar's version.json when the jar has one
# (the pack is [resource major.minor] .. [data major.minor], see PACK above).
PROFILES = {
    "26.2": {"version": "26.2", "pack": PACK,
             "pack_version": {"resource_major": 88, "resource_minor": 0,
                              "data_major": 107, "data_minor": 1}},
    "26.3": {"version": "26.3", "pack": PACK_263,
             "pack_version": {"resource_major": 97, "resource_minor": 1,
                              "data_major": 121, "data_minor": 0}},
}
DEFAULT_PROFILE = "26.2"


def profile_named(name):
    try:
        return PROFILES[name]
    except KeyError:
        raise ValueError("Unknown profile {!r}; expected one of {}".format(name, ", ".join(PROFILES))) from None


def check_jar_version(version, profile):
    if version["id"] != profile["version"]:
        raise ValueError("Expected a {} jar, got {}".format(profile["version"], version["id"]))
    declared = version.get("pack_version")
    if declared is not None and declared != profile["pack_version"]:
        raise ValueError("Jar pack_version {} differs from the {} profile {}".format(
            declared, profile["version"], profile["pack_version"]))


def load(path):
    return json.loads(path.read_bytes().decode("utf-8-sig"))


def write(path, data, strict=False):
    """Write changed JSON. strict also treats 1.0 vs 1 and key order as changes."""
    old = path.read_bytes() if path.exists() else b""
    if old:
        previous = json.loads(old.decode("utf-8-sig"))
        if previous == data and (not strict or json.dumps(previous) == json.dumps(data)):
            return 0
    newline = "\r\n" if b"\r\n" in old else "\n"
    encoded = (json.dumps(data, indent=2, ensure_ascii=False) + "\n").replace("\n", newline)
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(encoded.encode("utf-8"))
    return 1


def common_tag(name):
    return COMMON_TAGS.get(name, name)


def references(value):
    if isinstance(value, str):
        for prefix in ("#forge:", "#F:"):
            if value.startswith(prefix):
                return ("#c:" if prefix == "#forge:" else "#F:") + common_tag(value[len(prefix):])
        if value.startswith("forge:") and value not in ("forge:conditional", "forge:mod_loaded"):
            return "c:" + common_tag(value[6:])
        return value
    if isinstance(value, list):
        return [references(v) for v in value]
    if isinstance(value, dict):
        return {k: references(v) for k, v in value.items()}
    return value


def ingredient(value):
    if isinstance(value, dict):
        if set(value) == {"item"}:
            return value["item"]
        if set(value) == {"tag"}:
            return "#" + value["tag"]
    if isinstance(value, list):
        return [ingredient(v) for v in value]
    return value


def recipe(data):
    data = copy.deepcopy(data)
    # Custom serializers keep their ids and serializer-owned fields intact.
    if not data.get("type", "").startswith("minecraft:"):
        return data
    for key in ("ingredient", "base", "addition", "template"):
        if key in data:
            data[key] = ingredient(data[key])
    if "ingredients" in data:
        data["ingredients"] = [ingredient(v) for v in data["ingredients"]]
    if "key" in data:
        data["key"] = {k: ingredient(v) for k, v in data["key"].items()}
    if "result" in data:
        if isinstance(data["result"], str):
            data["result"] = {"id": data["result"]}
        if "item" in data["result"]:
            data["result"]["id"] = data["result"].pop("item")
        if "count" in data:
            data["result"]["count"] = data.pop("count")
    if "pattern" in data:
        rows = data["pattern"]
        while rows and not rows[0].strip():
            rows.pop(0)
        while rows and not rows[-1].strip():
            rows.pop()
        if rows:
            left = min(len(s) - len(s.lstrip()) for s in rows if s.strip())
            right = max(len(s.rstrip()) for s in rows)
            data["pattern"] = [s[left:right] for s in rows]
    return data


def loot(value):
    if isinstance(value, list):
        return [loot(v) for v in value]
    if not isinstance(value, dict):
        return value
    d = {k: loot(v) for k, v in value.items()}
    if d.get("condition") == "minecraft:alternative":
        d["condition"] = "minecraft:any_of"
    if d.get("condition") == "minecraft:match_tool":
        predicate = d.setdefault("predicate", {})
        for field in ("enchantments", "stored_enchantments"):
            if field in predicate:
                checks = predicate.pop(field)
                for check in checks:
                    if "enchantment" in check:
                        check["enchantments"] = check.pop("enchantment")
                predicate.setdefault("predicates", {})["minecraft:" + field] = checks
    if d.get("function") == "minecraft:enchant_with_levels":
        treasure = d.pop("treasure", None)
        if "options" not in d:
            d["options"] = "#minecraft:on_random_loot" if treasure is not False else "#minecraft:in_enchanting_table"
    if d.get("function") == "minecraft:enchant_randomly":
        if "enchantments" in d:
            d["options"] = d.pop("enchantments")
        d.setdefault("options", "#minecraft:on_random_loot")
    return d


def advancement(data):
    data = copy.deepcopy(data)
    display = data.get("display", {})
    icon = display.get("icon", {})
    if "item" in icon:
        icon["id"] = icon.pop("item")
    if "background" in display:
        ns, sep, name = display["background"].partition(":")
        if not sep:
            ns, name = "minecraft", ns
        display["background"] = ns + ":" + name.removeprefix("textures/").removesuffix(".png")
    return data


def converted_path(rel):
    parts = list(rel.parts)
    if len(parts) > 2 and parts[0] == "data":
        parts[2] = FOLDERS.get(parts[2], parts[2])
        if parts[2] == "tags" and len(parts) > 4:
            parts[3] = TAG_FOLDERS.get(parts[3], parts[3])
            if parts[1] == "forge":
                parts[1] = "c"
                name = "/".join(parts[4:]).removesuffix(".json")
                parts[4:] = (common_tag(name) + ".json").split("/")
    return Path(*parts)


def door_parent(parent):
    ns, sep, name = parent.partition(":")
    if not sep:
        ns, name = "minecraft", ns
    if ns != "minecraft":
        return parent
    return {"block/door_bottom": "minecraft:block/door_bottom_left",
            "block/door_bottom_rh": "minecraft:block/door_bottom_right",
            "block/door_top": "minecraft:block/door_top_left",
            "block/door_top_rh": "minecraft:block/door_top_right"}.get(name, parent)


def door_state(data, name, vanilla):
    """Use vanilla's hinge geometry and rotations, retaining Buildscape textures."""
    variants = data.get("variants", {})
    if not variants or not all("hinge=" in k and "half=" in k and "open=" in k for k in variants):
        return None
    result = copy.deepcopy(data)
    models = {}
    for key in variants:
        if key not in vanilla["variants"]:
            raise ValueError("Non-vanilla door state: " + key)
        v = copy.deepcopy(vanilla["variants"][key])
        suffix = v["model"].split("oak_door_", 1)[1]
        v["model"] = "buildscape:block/" + name + "_" + suffix
        result["variants"][key] = v
        models[suffix] = "minecraft:block/door_" + suffix
    return result, models


def is_special_definition(data):
    model = data.get("model") if isinstance(data, dict) else None
    return isinstance(model, dict) and model.get("type") == "minecraft:special"


def item_definition(item, model_exists):
    if item in SPECIAL_ITEMS:
        target = "block/glass_jar" if item == "template_glass_jar" else "block/" + item.removeprefix("waxed_")
    else:
        target = ("item/" if model_exists else "block/") + item
    return {"model": {"type": "minecraft:model", "model": "buildscape:" + target}}


# ---------------------------------------------------------------------------
# 26.3 formats. Every rule below was read from the 26.3 client jar (data and
# class constants) and is verified by applying it to the 26.2 vanilla data and
# comparing with the 26.3 vanilla data (see test_convert.py for fixtures).
# All steps are idempotent: they leave 26.3-format data unchanged.
# ---------------------------------------------------------------------------
# Loot predicates and functions are now keyed "type" (was "condition" and
# "function"); "conditions"/"functions" lists became one "condition" (several
# are wrapped in all_of) and one "modifier" (an object, or a list for several).
NAMED_TOOL_PREDICATES = (
    ({"items": "minecraft:shears"}, "minecraft:tool/can_shear"),
    ({"predicates": {"minecraft:enchantments": [
        {"enchantments": "minecraft:silk_touch", "levels": {"min": 1}}]}}, "minecraft:tool/can_silk_touch"),
)
# These keep float notation in vanilla 26.3; every other integral float is an int.
FLOAT_NUMBER_TYPES_263 = {"minecraft:set_damage", "minecraft:enchanted_count_increase"}
# BlockStateProvider types lost their suffix; simple_state_provider is just a state.
PROVIDER_TYPES_263 = {
    "minecraft:weighted_state_provider": "minecraft:weighted",
    "minecraft:noise_threshold_provider": "minecraft:noise_threshold",
    "minecraft:noise_provider": "minecraft:noise",
    "minecraft:dual_noise_provider": "minecraft:dual_noise",
    "minecraft:rotated_block_provider": "minecraft:rotated",
    "minecraft:randomized_int_state_provider": "minecraft:randomized_int",
    "minecraft:rule_based_state_provider": "minecraft:rule_based",
}
WORLDGEN_FOLDERS_263 = {"configured_feature": "feature"}
# A string "condition" is a legacy predicate only if it names one of these types;
# in 26.3 data it can instead name a predicate resource ("minecraft:tool/can_shear").
LEGACY_CONDITION_TYPES_263 = {
    "minecraft:" + name for name in (
        "inverted", "any_of", "all_of", "alternative", "random_chance", "random_chance_with_enchanted_bonus",
        "entity_properties", "killed_by_player", "entity_scores", "block_state_property", "match_tool",
        "table_bonus", "survives_explosion", "damage_source_properties", "location_check", "weather_check",
        "reference", "time_check", "value_check", "enchantment_active_check", "random_chance_with_looting")}
FEATURE_FILE = re.compile(r"data/[^/]+/worldgen/(?:feature|placed_feature)/")


def predicate_condition_263(conditions):
    """A "conditions" list as the single "condition" of 26.3."""
    if len(conditions) == 1:
        return conditions[0]
    return {"type": "minecraft:all_of", "terms": conditions}


def hash_tag_ids_263(tags):
    """Damage-type tag entries {"id", "expected"} now name the tag as #id."""
    return [dict(t, id="#" + t["id"]) if isinstance(t, dict) and "expected" in t
            and isinstance(t.get("id"), str) and not t["id"].startswith("#") else t for t in tags]


def tag_ids_263(value):
    if isinstance(value, list):
        return [tag_ids_263(v) for v in value]
    if isinstance(value, dict):
        return {k: hash_tag_ids_263(tag_ids_263(v)) if k == "tags" and isinstance(v, list) else tag_ids_263(v)
                for k, v in value.items()}
    return value


def loot_numbers_263(value):
    if isinstance(value, float) and value.is_integer():
        return int(value)
    if isinstance(value, list):
        return [loot_numbers_263(v) for v in value]
    if isinstance(value, dict):
        if value.get("type") in FLOAT_NUMBER_TYPES_263:
            return value
        return {k: v if k == "chances" else loot_numbers_263(v) for k, v in value.items()}
    return value


def loot_node_263(value):
    if isinstance(value, list):
        return [loot_node_263(v) for v in value]
    if not isinstance(value, dict):
        return value
    node = {k: loot_node_263(v) for k, v in value.items()}
    result = {}
    legacy_condition = isinstance(node.get("condition"), str) and node["condition"] in LEGACY_CONDITION_TYPES_263
    kind = node.get("function") if isinstance(node.get("function"), str) else (
        node["condition"] if legacy_condition else None)
    if kind is not None:
        result["type"] = kind
    for key, item in node.items():
        if key == "function" and kind is not None and item == kind:
            continue
        if key == "condition" and legacy_condition:
            continue
        if key == "conditions" and isinstance(item, list):
            if item:
                result["condition"] = predicate_condition_263(item)
        elif key == "functions" and isinstance(item, list):
            if item:
                result["modifier"] = item[0] if len(item) == 1 else item
        elif key == "tags" and isinstance(item, list):
            result[key] = hash_tag_ids_263(item)
        else:
            result[key] = item
    if result.get("type") == "minecraft:block_state_property":
        replacement = {"type": "minecraft:match_block", "blocks": result["block"]}
        if "properties" in result:
            replacement["state"] = result["properties"]
        result = replacement
    elif result.get("type") == "minecraft:exploration_map":
        result.setdefault("destination", "#minecraft:on_treasure_maps")
    elif result.get("type") == "minecraft:match_tool" and set(result) == {"type", "predicate"}:
        for shape, name in NAMED_TOOL_PREDICATES:
            if result["predicate"] == shape:
                return name
    return result


def loot_263(data):
    return loot_numbers_263(loot_node_263(data))


def advancement_263(data):
    data = copy.deepcopy(data)
    for criterion in data.get("criteria", {}).values():
        conditions = criterion.get("conditions")
        if not isinstance(conditions, dict):
            continue
        trigger = criterion.get("trigger")
        if trigger == "minecraft:recipe_unlocked" and "recipe" in conditions:
            conditions["recipes"] = conditions.pop("recipe")
        if "recipe_id" in conditions:  # crafter_recipe_crafted and recipe_crafted
            conditions["recipes"] = conditions.pop("recipe_id")
        if trigger == "minecraft:enter_block" and "block" in conditions:
            conditions["blocks"] = conditions.pop("block")
        for key, value in list(conditions.items()):
            if (isinstance(value, list) and value
                    and all(isinstance(v, dict) and isinstance(v.get("condition"), str) for v in value)):
                conditions[key] = predicate_condition_263([loot_node_263(v) for v in value])
            else:
                conditions[key] = tag_ids_263(value)
    return data


def state_263(value):
    state = {"id": value["Name"]}
    if value.get("Properties"):
        state["properties"] = dict(value["Properties"])
    return state


def worldgen_node_263(value):
    if isinstance(value, list):
        return [worldgen_node_263(v) for v in value]
    if not isinstance(value, dict):
        return value
    kind = value.get("type")
    if "Name" in value and kind is None and isinstance(value["Name"], str):
        return state_263(value)
    if kind == "minecraft:simple_state_provider" and "state" in value:
        return worldgen_node_263(value["state"])
    node = {k: worldgen_node_263(v) for k, v in value.items()}
    if isinstance(node.get("config"), dict) and isinstance(kind, str):
        # Feature configuration is no longer nested under "config".
        node = {"type": kind, **{k: v for k, v in node.items() if k not in ("type", "config")}, **node["config"]}
    if kind in PROVIDER_TYPES_263:
        node["type"] = PROVIDER_TYPES_263[kind]
    if kind == "minecraft:random_offset" and ("xz_spread" in node or "y_spread" in node):
        spread = node.get("xz_spread", 0)
        node = {"type": "minecraft:offset", "x": spread, "y": node.get("y_spread", 0), "z": copy.deepcopy(spread)}
    return node


def model_263(data):
    """Element "shade" was replaced by shade_direction_override ("up" is flat lighting)."""
    data = copy.deepcopy(data)
    for element in data.get("elements", []) if isinstance(data.get("elements"), list) else []:
        if isinstance(element, dict) and "shade" in element:
            if element.pop("shade") is False:
                element.setdefault("shade_direction_override", "up")
    return data


def converted_path_263(rel):
    parts = list(rel.parts)
    if len(parts) > 3 and parts[0] == "data":
        if parts[2] == "worldgen":
            if parts[3] == "configured_carver":
                raise ValueError("Unsupported 26.3 carver conversion for " + rel.as_posix())
            parts[3] = WORLDGEN_FOLDERS_263.get(parts[3], parts[3])
        elif parts[2] == "tags" and len(parts) > 4 and parts[3] == "worldgen":
            parts[4] = WORLDGEN_FOLDERS_263.get(parts[4], parts[4])
    return Path(*parts)


def upgrade_263(root):
    """Convert a 26.2-format tree in place to 26.3 formats."""
    changed = 0
    for p in sorted((root / "data").rglob("*.json")):
        rel = p.relative_to(root)
        target = root / converted_path_263(rel)
        if p == target:
            continue
        target.parent.mkdir(parents=True, exist_ok=True)
        if target.exists():
            a, b = load(p), load(target)
            if "/tags/" in rel.as_posix():
                b["values"] += [v for v in a["values"] if v not in b["values"]]
                changed += write(target, b)
            elif a != b:
                raise ValueError("Conflicting resource: " + str(rel))
            p.unlink()
        else:
            p.rename(target)
        changed += 1
    for p in sorted(root.rglob("*.json")):
        rel = p.relative_to(root).as_posix()
        if "/loot_table/" in rel:
            convert_one = loot_263
        elif "/advancement/" in rel:
            convert_one = advancement_263
        elif FEATURE_FILE.match(rel):
            convert_one = worldgen_node_263
        elif rel.startswith("assets/") and "/models/" in rel:
            convert_one = model_263
        else:
            continue
        changed += write(p, convert_one(load(p)), strict=True)
    for p in sorted((root / "data").rglob("*"), key=lambda p: len(p.parts), reverse=True):
        if p.is_dir() and not any(p.iterdir()):
            p.rmdir()
    return changed


def convert(root, jar, profile=DEFAULT_PROFILE):
    root = Path(root)
    profile = profile_named(profile)
    changed = 0
    with zipfile.ZipFile(jar) as vanilla:
        version = json.loads(vanilla.read("version.json"))
        check_jar_version(version, profile)
        # Move files rather than recursively removing directories. Collisions must
        # be equivalent or tags whose values can safely be merged.
        for p in sorted((root / "data").rglob("*.json")):
            rel = p.relative_to(root)
            if rel.as_posix() == "data/forge/loot_modifiers/global_loot_modifiers.json":
                if load(p).get("entries"):
                    raise ValueError("Nonempty Forge loot modifiers cannot be discarded")
                p.unlink()
                changed += 1
                continue
            target = root / converted_path(rel)
            if p != target:
                target.parent.mkdir(parents=True, exist_ok=True)
                if target.exists():
                    a, b = load(p), load(target)
                    if "/tags/" in rel.as_posix():
                        b["values"] += [v for v in a["values"] if v not in b["values"]]
                        changed += write(target, b)
                    elif a != b:
                        raise ValueError("Conflicting resource: " + str(rel))
                    p.unlink()
                else:
                    p.rename(target)
                changed += 1
        for p in sorted(root.rglob("*.json")):
            rel = p.relative_to(root).as_posix()
            original = load(p)
            d = references(original)
            if "/recipe/" in rel:
                d = recipe(d)
            elif "/loot_table/" in rel:
                d = loot(d)
            elif "/advancement/" in rel:
                d = advancement(d)
            elif "/recipes_pack/" in rel:
                if d.get("aliases", {}).get("F") == "forge:":
                    d["aliases"]["F"] = "c:"
                if "_comment" in d:
                    d["_comment"] = d["_comment"].replace("F=forge:", "F=c:")
            elif "/models/" in rel:
                if "parent" in d:
                    d["parent"] = door_parent(d["parent"])
                for k, v in d.get("textures", {}).items():
                    if v in ("minecraft:block/quartz_pillar", "block/quartz_pillar"):
                        d["textures"][k] = "minecraft:block/quartz_pillar_side"
                if p.stem in SPECIAL_ITEMS and "/models/item/" in rel:
                    d["parent"] = item_definition(p.stem, True)["model"]["model"]
            changed += write(p, d)
        door_template = json.loads(vanilla.read("assets/minecraft/blockstates/oak_door.json"))
        for p in sorted((root / "assets/buildscape/blockstates").glob("*.json")):
            result = door_state(load(p), p.stem, door_template)
            if result:
                d, models = result
                changed += write(p, d)
                base = root / "assets/buildscape/models/block" / (p.stem + "_bottom.json")
                if not base.exists() and p.stem.startswith("waxed_"):
                    base = base.with_name(p.stem.removeprefix("waxed_") + "_bottom.json")
                template = load(base)
                for suffix, parent in models.items():
                    model = copy.deepcopy(template)
                    model["parent"] = parent
                    changed += write(base.with_name(p.stem + "_" + suffix + ".json"), model)
        # Copy the current vanilla loot baseline, preserving only the
        # Buildscape-specific bonus pools. Thus new vanilla loot is not hidden.
        for rel in ("data/minecraft/loot_table/chests/end_city_treasure.json",
                    "data/minecraft/loot_table/chests/woodland_mansion.json",
                    "data/minecraft/loot_table/gameplay/sniffer_digging.json"):
            p = root / rel
            if p.exists():
                d = load(p)
                bonuses = [pool for pool in d["pools"] if "buildscape:" in json.dumps(pool)]
                current = json.loads(vanilla.read(rel))
                current["pools"].extend(bonuses)
                changed += write(p, current)
        # 26.2 calls the vanilla chain iron_chain. Preserve the translation intent.
        p = root / "assets/minecraft/lang/en_us.json"
        if p.exists():
            d = load(p)
            for key in list(d):
                if key.endswith(".minecraft.chain"):
                    d[key.replace(".chain", ".iron_chain")] = d.pop(key)
            # Block items fall back to the block translation; this item key
            # does not exist in the vanilla language registry.
            d.pop("item.minecraft.iron_chain", None)
            changed += write(p, d)
        items_dir = root / "assets/buildscape/models/item"
        ids = {p.stem for p in items_dir.glob("*.json") if not p.stem.startswith("template_")}
        ids.update(SPECIAL_ITEMS)
        order = root / "data/buildscape/creative_tab_order.txt"
        if order.exists():
            ids.update(line.strip().removeprefix("buildscape:") for line in order.read_text(encoding="utf-8-sig").splitlines()
                       if line.strip() and not line.strip().startswith("#"))
        # Recover the sole erroneous output an older converter made when it
        # decoded the creative-order BOM using the Windows default encoding.
        for prefix in ("\ufeff", "\u00ef\u00bb\u00bf"):
            bad = root / "assets/buildscape/items" / (prefix + "bit_copper_block.json")
            if bad.exists():
                bad.unlink()
                changed += 1
        for item in sorted(ids):
            # Hand-maintained minecraft:special definitions (copper chests, glass
            # jars) render through a block entity model; never replace them.
            existing = root / "assets/buildscape/items" / (item + ".json")
            if existing.exists() and is_special_definition(load(existing)):
                continue
            changed += write(root / "assets/buildscape/items" / (item + ".json"),
                             item_definition(item, (items_dir / (item + ".json")).exists()))
        if profile["version"] == "26.3":
            changed += upgrade_263(root)
        changed += write(root / "pack.mcmeta", profile["pack"])
    # Empty legacy directories are not part of Git but must not survive a run.
    for p in sorted((root / "data").rglob("*"), key=lambda p: len(p.parts), reverse=True):
        if p.is_dir() and not any(p.iterdir()):
            p.rmdir()
    return changed


# Files in an overlay folder that no conversion produces (owned by hand).
OVERLAY_PROTECTED = {"buildscape.mixins.json"}
TEXT_SUFFIXES = {".json", ".mcmeta", ".txt"}


def derive_overlay(base, output, jar, profile):
    """Write only what converting the base tree for `profile` changes into `output`.

    The base tree (the 26.2 resources) is converted in a temporary copy. Files that
    differ from the base, or are new, form the overlay for the version profile and
    replace the previous generated contents of `output`; files in OVERLAY_PROTECTED
    are left alone. Returns (written, removed, retired): retired lists base files the
    conversion moved away, which a layer cannot delete, so builds must exclude them.
    """
    base, output = Path(base), Path(output)
    with tempfile.TemporaryDirectory() as tmp:
        tree = Path(tmp) / "tree"
        before = {}
        for p in sorted(base.rglob("*")):
            if p.is_file() and p.suffix in TEXT_SUFFIXES:
                rel = p.relative_to(base).as_posix()
                before[rel] = p.read_bytes()
                (tree / rel).parent.mkdir(parents=True, exist_ok=True)
                (tree / rel).write_bytes(before[rel])
        convert(tree, jar, profile)
        after = {p.relative_to(tree).as_posix(): p.read_bytes() for p in sorted(tree.rglob("*")) if p.is_file()}
    wanted = {rel: data for rel, data in after.items() if before.get(rel) != data}
    retired = sorted(set(before) - set(after))
    written = removed = 0
    output.mkdir(parents=True, exist_ok=True)
    for p in sorted(output.rglob("*")):
        rel = p.relative_to(output).as_posix()
        if p.is_file() and rel not in OVERLAY_PROTECTED and rel not in wanted:
            p.unlink()
            removed += 1
    for rel, data in wanted.items():
        target = output / rel
        if rel in OVERLAY_PROTECTED:
            raise ValueError("The conversion would overwrite hand-maintained " + rel)
        if not target.exists() or target.read_bytes() != data:
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_bytes(data)
            written += 1
    for p in sorted(output.rglob("*"), key=lambda p: len(p.parts), reverse=True):
        if p.is_dir() and not any(p.iterdir()):
            p.rmdir()
    return written, removed, retired


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("resources_root", type=Path)
    parser.add_argument("mc_jar", type=Path)
    parser.add_argument("--profile", choices=sorted(PROFILES), default=DEFAULT_PROFILE,
                        help="target Minecraft version profile (default: %(default)s)")
    parser.add_argument("--derive-from", type=Path, metavar="BASE",
                        help="treat resources_root as an overlay: convert BASE (the 26.2 resources) in a temporary "
                             "copy and write only the files that change into resources_root")
    args = parser.parse_args()
    if args.derive_from:
        written, removed, retired = derive_overlay(args.derive_from, args.resources_root, args.mc_jar, args.profile)
        print("Overlay for {}: {} written, {} stale removed, {} base files retired".format(
            args.profile, written, removed, len(retired)))
        for rel in retired:
            print("retired: " + rel)
        return
    print("Converted resources: {} changes".format(convert(args.resources_root, args.mc_jar, args.profile)))


if __name__ == "__main__":
    main()
