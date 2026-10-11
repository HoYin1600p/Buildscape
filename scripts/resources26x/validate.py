"""Offline structural/reference validation against the real Minecraft 26.2 jar.

This is a resource validator, not a substitute for running Minecraft's codecs
with Buildscape's Java registries. Abstract model texture parameters may remain
unbound, but concrete item/blockstate models must bind every used parameter.
"""
import argparse
from functools import lru_cache
import json
from pathlib import Path
import re
import zipfile

from convert import FOLDERS, TAG_FOLDERS, PACK, SPECIAL_ITEMS

ID = re.compile(r"^[a-z0-9_.-]+:[a-z0-9/._-]+$")
BUILTINS = {"minecraft:builtin/generated"}


def item_model(model):
    """Model id of an item definition: a plain model, or the base of a special renderer."""
    if not isinstance(model, dict):
        return None
    key = {"minecraft:model": "model", "minecraft:special": "base"}.get(model.get("type"))
    value = model.get(key) if key else None
    return value if isinstance(value, str) else None


def namespaced(value):
    return value if ":" in value else "minecraft:" + value


def asset_path(value, kind, suffix=".json"):
    ns, name = namespaced(value).split(":", 1)
    return "assets/" + ns + "/" + kind + "/" + name + suffix


def objects(value):
    if isinstance(value, dict):
        yield value
        for v in value.values():
            yield from objects(v)
    elif isinstance(value, list):
        for v in value:
            yield from objects(v)


def strings(value):
    if isinstance(value, str):
        yield value
    elif isinstance(value, dict):
        for v in value.values():
            yield from strings(v)
    elif isinstance(value, list):
        for v in value:
            yield from strings(v)


class Validator:
    def __init__(self, root, vanilla):
        self.root = Path(root)
        self.vanilla = vanilla
        self.jar_files = set(vanilla.namelist())
        self.files = {p.relative_to(self.root).as_posix(): p
                      for p in self.root.rglob("*") if p.is_file()}
        self.json = {}
        self.errors = []
        self.concrete_models = set()

    def error(self, path, message):
        self.errors.append(str(path) + ": " + message)

    def exists(self, path):
        return path in self.files or path in self.jar_files

    @lru_cache(maxsize=None)
    def get(self, path):
        if path in self.json:
            return self.json[path]
        if path in self.jar_files:
            return json.loads(self.vanilla.read(path))
        return None

    def reference(self, path, value, kind, suffix=".json"):
        if not isinstance(value, str) or not ID.fullmatch(namespaced(value)):
            self.error(path, "invalid " + kind + " id " + repr(value))
            return False
        if not self.exists(asset_path(value, kind, suffix)):
            self.error(path, "missing " + kind + " reference " + value)
            return False
        return True

    def registry_id(self, kind, value):
        if not isinstance(value, str) or not ID.fullmatch(value):
            return False
        ns, name = value.split(":", 1)
        if kind == "block":
            return self.exists("assets/" + ns + "/blockstates/" + name + ".json")
        if kind == "item":
            return self.exists("assets/" + ns + "/items/" + name + ".json")
        if kind == "enchantment":
            return self.exists("data/" + ns + "/enchantment/" + name + ".json")
        if kind == "fluid":
            # These are the two registry names defined by ModFluids. Fluids
            # have no data/asset representation comparable to blockstates.
            return (ns == "minecraft" and name in {"water", "flowing_water", "lava", "flowing_lava", "empty"}
                    or ns == "buildscape" and name in {"experience_still", "experience_flowing"})
        if kind == "entity_type":
            # Entity registries have no resource files; use identifiers from the
            # vanilla EntityType registry's class constants, read from this jar.
            return value in self.entity_ids
        return self.exists("data/" + ns + "/" + kind + "/" + name + ".json")

    def tag(self, path, kind, value, required=True):
        if not isinstance(value, str) or not ID.fullmatch(value.removeprefix("#")):
            self.error(path, "invalid tag entry " + repr(value))
            return
        if value.startswith("#"):
            ns, name = value[1:].split(":", 1)
            target = "data/" + ns + "/tags/" + kind + "/" + name + ".json"
            found = self.exists(target)
        else:
            found = self.registry_id(kind, value)
        if not found and required:
            self.error(path, "missing " + kind + " id/tag " + value)

    def parse(self):
        for path, p in self.files.items():
            if p.suffix == ".json" or p.name == "pack.mcmeta" or p.name.endswith(".png.mcmeta"):
                try:
                    self.json[path] = json.loads(p.read_bytes().decode("utf-8-sig"))
                except (ValueError, UnicodeError) as e:
                    self.error(path, "JSON parse error: " + str(e))
            parts = path.split("/")
            if parts[0] == "data" and len(parts) > 2:
                if parts[2] in FOLDERS or parts[2] == "loot_modifiers":
                    self.error(path, "legacy data folder " + parts[2])
                if parts[2] == "tags" and len(parts) > 3 and parts[3] in TAG_FOLDERS:
                    self.error(path, "legacy tag folder " + parts[3])
                if parts[1] == "forge":
                    self.error(path, "legacy forge namespace")
        for p in (self.root / "data").rglob("*"):
            if p.is_dir():
                parts = p.relative_to(self.root).parts
                if len(parts) == 3 and parts[2] in FOLDERS:
                    self.error(p.relative_to(self.root), "legacy empty folder")

    @lru_cache(maxsize=None)
    def merged_model(self, value, stack=()):
        value = namespaced(value)
        if value in stack:
            self.error(asset_path(value, "models"), "cyclic model parent")
            return {}
        if value in BUILTINS:
            return {}
        path = asset_path(value, "models")
        d = self.get(path)
        if not isinstance(d, dict):
            return {}
        parent = self.merged_model(d["parent"], stack + (value,)) if "parent" in d else {}
        merged = dict(parent)
        merged.update(d)
        merged["textures"] = dict(parent.get("textures", {})) | d.get("textures", {})
        return merged

    def textures(self, path, d, concrete=False):
        bindings = d.get("textures", {})
        refs = list(bindings.values())
        for elem in d.get("elements", []):
            refs.extend(face.get("texture", "") for face in elem.get("faces", {}).values())
        for value in refs:
            if isinstance(value, dict):
                # 26.2 vanilla glass uses {sprite, force_translucent} bindings.
                if not isinstance(value.get("sprite"), str) or set(value) - {"sprite", "force_translucent"}:
                    self.error(path, "invalid structured texture " + repr(value))
                    continue
                value = value["sprite"]
            visited = set()
            while isinstance(value, str) and value.startswith("#"):
                key = value[1:]
                if key in visited:
                    if concrete:
                        self.error(path, "cyclic texture binding " + value)
                    break
                visited.add(key)
                if key not in bindings:
                    if concrete:
                        self.error(path, "unbound texture " + value)
                    value = None
                    break
                value = bindings[key]
                if isinstance(value, dict):
                    value = value.get("sprite")
            else:
                if value is not None:
                    self.reference(path, value, "textures", ".png")

    def models(self):
        for path, d in self.json.items():
            if "/blockstates/" in path:
                if not isinstance(d, dict) or not ("variants" in d or "multipart" in d):
                    self.error(path, "expected variants or multipart")
                    continue
                for obj in objects(d):
                    if "model" in obj:
                        value = obj["model"]
                        if self.reference(path, value, "models"):
                            self.concrete_models.add(namespaced(value))
            if "/models/" in path:
                if "overrides" in d:
                    self.error(path, "legacy item model overrides")
                if "parent" in d and namespaced(d["parent"]) not in BUILTINS:
                    self.reference(path, d["parent"], "models")
                self.textures(path, d)
            if "/items/" in path and path.startswith("assets/"):
                model = d.get("model") if isinstance(d, dict) else None
                target = item_model(model)
                if target is None:
                    self.error(path, "expected minecraft:model or minecraft:special item definition")
                elif self.reference(path, target, "models"):
                    self.concrete_models.add(namespaced(target))
        for value in sorted(self.concrete_models):
            self.textures(asset_path(value, "models"), self.merged_model(value), True)
        ids = {Path(path).stem for path in self.files
               if path.startswith("assets/buildscape/models/item/") and path.endswith(".json")
               and not Path(path).stem.startswith("template_")}
        ids.update(SPECIAL_ITEMS)
        order = self.root / "data/buildscape/creative_tab_order.txt"
        if not order.exists():
            self.error("data/buildscape/creative_tab_order.txt", "missing creative item order")
        else:
            ids.update(line.strip().removeprefix("buildscape:") for line in order.read_text(encoding="utf-8-sig").splitlines()
                       if line.strip() and not line.strip().startswith("#"))
        for item in sorted(ids):
            path = "assets/buildscape/items/" + item + ".json"
            if path not in self.files:
                self.error(path, "missing item definition")
            elif item not in SPECIAL_ITEMS:
                kind = "item" if "assets/buildscape/models/item/" + item + ".json" in self.files else "block"
                expected = "buildscape:" + kind + "/" + item
                if item_model(self.json.get(path, {}).get("model")) != expected:
                    self.error(path, "expected model " + expected)

    def ingredient(self, path, value):
        if isinstance(value, list):
            if not value:
                self.error(path, "empty ingredient alternatives")
            for v in value:
                self.ingredient(path, v)
        elif isinstance(value, str):
            self.tag(path, "item", value)
        else:
            self.error(path, "ingredient must be an item id, #tag or id list: " + repr(value))

    def recipe(self, path, d):
        kind = d.get("type", "")
        if kind in {"buildscape:custom_firework_star", "buildscape:infinite_phoenix_firework_star"}:
            return
        allowed = {"minecraft:crafting_shaped", "minecraft:crafting_shapeless", "minecraft:smelting",
                   "minecraft:blasting", "minecraft:smoking", "minecraft:campfire_cooking",
                   "minecraft:stonecutting", "minecraft:smithing_transform", "minecraft:smithing_trim"}
        if kind not in allowed:
            self.error(path, "unsupported recipe serializer " + kind)
        for key in ("ingredient", "base", "addition", "template"):
            if key in d:
                self.ingredient(path, d[key])
        for v in d.get("ingredients", []):
            self.ingredient(path, v)
        for v in d.get("key", {}).values():
            self.ingredient(path, v)
        if kind != "minecraft:smithing_trim":
            result = d.get("result")
            if not isinstance(result, dict) or "item" in result or not isinstance(result.get("id"), str):
                self.error(path, "result must be an item stack with id")
            else:
                self.tag(path, "item", result["id"])
                count = result.get("count", 1)
                if type(count) is not int or not 1 <= count <= 99:
                    self.error(path, "invalid result count")
        if kind == "minecraft:crafting_shaped":
            pattern = d.get("pattern", [])
            if not pattern or len(pattern) > 3 or len({len(s) for s in pattern}) != 1 or not 1 <= len(pattern[0]) <= 3:
                self.error(path, "invalid shaped pattern")
            used = set("".join(pattern)) - {" "}
            if used != set(d.get("key", {})):
                self.error(path, "pattern/key mismatch")
        if kind == "minecraft:crafting_shapeless" and not 1 <= len(d.get("ingredients", [])) <= 9:
            self.error(path, "shapeless recipe needs 1..9 ingredients")

    def loot(self, path, d):
        for obj in objects(d):
            if obj.get("condition") == "minecraft:alternative":
                self.error(path, "legacy alternative predicate")
            if obj.get("condition") in {"minecraft:any_of", "minecraft:all_of"} and not isinstance(obj.get("terms"), list):
                self.error(path, "predicate needs terms")
            if obj.get("condition") == "minecraft:match_tool":
                pred = obj.get("predicate", {})
                if "enchantments" in pred or "stored_enchantments" in pred:
                    self.error(path, "legacy item enchantment predicate")
                for field in ("minecraft:enchantments", "minecraft:stored_enchantments"):
                    checks = pred.get("predicates", {}).get(field, [])
                    for check in checks:
                        if "enchantment" in check or "enchantments" not in check:
                            self.error(path, "enchantment check needs enchantments")
                        else:
                            vals = check["enchantments"]
                            for v in vals if isinstance(vals, list) else [vals]:
                                self.tag(path, "enchantment", v)
                if "items" in pred:
                    vals = pred["items"]
                    for v in vals if isinstance(vals, list) else [vals]:
                        self.tag(path, "item", v)
            if obj.get("function") in {"minecraft:enchant_with_levels", "minecraft:enchant_randomly"}:
                if "treasure" in obj or "enchantments" in obj or "options" not in obj:
                    self.error(path, "legacy enchant function fields")
                else:
                    options = obj["options"]
                    for v in options if isinstance(options, list) else [options]:
                        self.tag(path, "enchantment", v)
            if obj.get("type") == "minecraft:item" and "name" in obj:
                self.tag(path, "item", obj["name"])
            if obj.get("condition") == "minecraft:block_state_property":
                self.tag(path, "block", obj.get("block"))

    def advancement(self, path, d):
        display = d.get("display", {})
        if display:
            icon = display.get("icon", {})
            if "item" in icon or "id" not in icon:
                self.error(path, "display icon needs id")
            else:
                self.tag(path, "item", icon["id"])
            background = display.get("background")
            if background:
                if ":textures/" in background or background.endswith(".png"):
                    self.error(path, "legacy advancement background")
                self.reference(path, background, "textures", ".png")
        if "parent" in d:
            self.tag(path, "advancement", d["parent"])
        criteria = d.get("criteria")
        if not isinstance(criteria, dict) or not criteria:
            self.error(path, "advancement needs criteria")
        if "requirements" in d:
            required = {v for group in d["requirements"] for v in group}
            if required != set(criteria or {}):
                self.error(path, "requirements/criteria mismatch")

    def overrides(self):
        # Compare the vanilla portions, not just whether a path still exists.
        path = "assets/minecraft/blockstates/composter.json"
        d = self.json.get(path)
        if d:
            base = self.get_vanilla(path)
            vanilla_parts = []
            for part in d.get("multipart", []):
                if part.get("when", {}).get("planter") == "none":
                    part = json.loads(json.dumps(part))
                    part["when"].pop("planter")
                    if not part["when"]:
                        part.pop("when")
                    vanilla_parts.append(part)
            if vanilla_parts != base["multipart"]:
                self.error(path, "composter vanilla levels differ from 26.2")
        for name in ("hollow_log", "stripped_hollow_log"):
            path = "assets/minecraft/models/block/" + name + ".json"
            if path in self.jar_files:
                self.error(path, "custom hollow-log template now collides with vanilla")
        path = "assets/minecraft/lang/en_us.json"
        lang = self.json.get(path, {})
        vanilla_lang = self.get_vanilla(path)
        for key in lang:
            if key not in vanilla_lang:
                self.error(path, "unknown vanilla translation key " + key)
        for name in ("end_city_treasure", "woodland_mansion"):
            path = "data/minecraft/loot_table/chests/" + name + ".json"
            d = self.json.get(path)
            if d:
                baseline = json.loads(json.dumps(d))
                baseline["pools"] = [p for p in d["pools"] if "buildscape:" not in json.dumps(p)]
                if baseline != self.get_vanilla(path):
                    self.error(path, "chest loot vanilla baseline differs from 26.2")
        # Snow and planter loot intentionally change gameplay, but keep the
        # vanilla recipe/table serializer and output/base composter drop.
        path = "data/minecraft/recipe/snow.json"
        if path in self.json:
            d, base = self.json[path], self.get_vanilla(path)
            if (d.get("type") != base.get("type") or d.get("result", {}).get("id") != base.get("result", {}).get("id")
                    or d.get("result", {}).get("count") != 8 or d.get("pattern") != ["#"]
                    or d.get("key") != base.get("key")):
                self.error(path, "snow override must preserve its one-block/eight-layer recipe and vanilla ingredient/type/output id")
        path = "data/minecraft/loot_table/blocks/composter.json"
        if path in self.json:
            d, base = self.json[path], self.get_vanilla(path)
            if d.get("type") != base.get("type") or not any(o.get("name") == "minecraft:composter" for o in objects(d)):
                self.error(path, "composter loot lost vanilla type/drop")

    def get_vanilla(self, path):
        return json.loads(self.vanilla.read(path)) if path in self.jar_files else {}

    def run(self):
        version = self.get_vanilla("version.json")
        if version.get("id") != "26.2":
            self.error("--mc-jar", "expected real Minecraft 26.2 jar")
            return self.errors
        self.parse()
        self.entity_ids = entity_registry_ids(self.vanilla)
        if self.json.get("pack.mcmeta") != PACK:
            self.error("pack.mcmeta", "expected min_format 88.0 / max_format 107.1 for resource 88 / data 107")
        self.models()
        for path, d in self.json.items():
            if path.startswith("data/"):
                if any(v.startswith("#forge:") for v in strings(d)):
                    self.error(path, "legacy #forge tag reference")
                if "/tags/" in path:
                    parts = path.split("/")
                    # Worldgen tags nest one level deeper: tags/worldgen/biome/...
                    kind = "/".join(parts[3:5]) if parts[3] == "worldgen" else parts[3]
                    if not isinstance(d, dict) or not isinstance(d.get("values"), list):
                        self.error(path, "tag needs values list")
                        continue
                    for value in d["values"]:
                        if isinstance(value, dict):
                            self.tag(path, kind, value.get("id"), value.get("required", True))
                        else:
                            self.tag(path, kind, value)
                elif "/recipe/" in path:
                    self.recipe(path, d)
                elif "/loot_table/" in path:
                    self.loot(path, d)
                elif "/advancement/" in path:
                    self.advancement(path, d)
                elif "/recipes_pack/" in path:
                    if d.get("aliases", {}).get("F") != "c:":
                        self.error(path, "compact recipe F alias must be c:")
                    aliases = d.get("aliases", {})
                    for v in strings(d):
                        if v.startswith("#") and ":" in v:
                            ns, name = v[1:].split(":", 1)
                            resolved = aliases.get(ns, ns + ":") + name
                            self.tag(path, "item", "#" + resolved)
        self.overrides()
        return sorted(set(self.errors))


def entity_registry_ids(vanilla):
    """Read UTF8 constants from the vanilla class without invoking Java."""
    data = vanilla.read("net/minecraft/world/entity/EntityTypeIds.class")
    i, index, limit = 10, 1, int.from_bytes(data[8:10], "big")
    ids = set()
    widths = {3: 4, 4: 4, 5: 8, 6: 8, 7: 2, 8: 2, 9: 4,
              10: 4, 11: 4, 12: 4, 15: 3, 16: 2, 17: 4, 18: 4, 19: 2, 20: 2}
    while index < limit:
        tag = data[i]
        i += 1
        if tag == 1:
            size = int.from_bytes(data[i:i+2], "big")
            i += 2
            s = data[i:i+size].decode("utf-8", "replace")
            i += size
            if re.fullmatch(r"[a-z][a-z0-9_]*", s):
                ids.add("minecraft:" + s)
        else:
            i += widths[tag]
            if tag in (5, 6):
                index += 1
        index += 1
    return ids


def validate(root, jar):
    with zipfile.ZipFile(jar) as vanilla:
        validator = Validator(root, vanilla)
        errors = validator.run()
        for error in errors:
            print(error)
        print("Validated {} JSON files; {} problems".format(len(validator.json), len(errors)))
        return 1 if errors else 0


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--resources-root", type=Path,
                        default=Path(__file__).resolve().parents[2] / "common/src/main/resources")
    parser.add_argument("--mc-jar", type=Path, required=True)
    args = parser.parse_args()
    return validate(args.resources_root, args.mc_jar)


if __name__ == "__main__":
    raise SystemExit(main())
