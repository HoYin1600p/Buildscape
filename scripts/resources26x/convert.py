"""Convert the shared resource tree in place using the supplied 26.2 vanilla jar.

Only changed JSON is serialized; existing newline conventions are retained.
The compact recipes_pack is a private Java format, not a vanilla recipe folder.
"""
import argparse
import copy
import json
from pathlib import Path
import zipfile

FOLDERS = {"loot_tables": "loot_table", "recipes": "recipe",
           "advancements": "advancement"}
TAG_FOLDERS = {"blocks": "block", "items": "item",
               "entity_types": "entity_type", "fluids": "fluid"}
# Common-tag names shared by modern loaders. Material tags retain their names.
COMMON_TAGS = {"glass": "glass_blocks", "stained_glass": "glass_blocks/dyed",
               "stained_glass_panes": "glass_panes/dyed", "stone": "stones",
               "sand": "sands"}
SPECIAL_ITEMS = (
    "copper_chest", "exposed_copper_chest", "oxidized_copper_chest",
    "waxed_copper_chest", "waxed_exposed_copper_chest",
    "waxed_oxidized_copper_chest", "waxed_weathered_copper_chest",
    "weathered_copper_chest", "template_glass_jar",
)
PACK = {"pack": {"description": "Buildscape resources for Minecraft 26.2",
                  "min_format": [88, 0], "max_format": [107, 1]}}


def load(path):
    return json.loads(path.read_bytes().decode("utf-8-sig"))


def write(path, data):
    old = path.read_bytes() if path.exists() else b""
    if old and json.loads(old.decode("utf-8-sig")) == data:
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


def item_definition(item, model_exists):
    if item in SPECIAL_ITEMS:
        target = "block/glass_jar" if item == "template_glass_jar" else "block/" + item.removeprefix("waxed_")
    else:
        target = ("item/" if model_exists else "block/") + item
    return {"model": {"type": "minecraft:model", "model": "buildscape:" + target}}


def convert(root, jar):
    root = Path(root)
    changed = 0
    with zipfile.ZipFile(jar) as vanilla:
        version = json.loads(vanilla.read("version.json"))
        if version["id"] != "26.2":
            raise ValueError("Expected a 26.2 jar, got " + version["id"])
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
        # Copy the current vanilla chest loot baseline, preserving only the
        # Buildscape-specific bonus pools. Thus new vanilla loot is not hidden.
        for name in ("end_city_treasure", "woodland_mansion"):
            rel = "data/minecraft/loot_table/chests/" + name + ".json"
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
            changed += write(root / "assets/buildscape/items" / (item + ".json"),
                             item_definition(item, (items_dir / (item + ".json")).exists()))
        changed += write(root / "pack.mcmeta", PACK)
    # Empty legacy directories are not part of Git but must not survive a run.
    for p in sorted((root / "data").rglob("*"), key=lambda p: len(p.parts), reverse=True):
        if p.is_dir() and not any(p.iterdir()):
            p.rmdir()
    return changed


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("resources_root", type=Path)
    parser.add_argument("mc_jar", type=Path)
    args = parser.parse_args()
    print("Converted resources: {} changes".format(convert(args.resources_root, args.mc_jar)))


if __name__ == "__main__":
    main()
