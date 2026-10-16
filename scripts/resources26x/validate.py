"""Offline structural/reference validation against the real Minecraft 26.2 jar.

This is a resource validator, not a substitute for running Minecraft's codecs
with Buildscape's Java registries. Abstract model texture parameters may remain
unbound, but concrete item/blockstate models must bind every used parameter.
"""
import argparse
from functools import lru_cache
import importlib.util
import json
from pathlib import Path
import re
import zipfile

from convert import FOLDERS, TAG_FOLDERS, PACK, SPECIAL_ITEMS


def _profile_support():
    """The version profiles live in this folder's convert.py. Load that file by path:
    resources121x loads this module while its own convert.py is the `convert` module."""
    spec = importlib.util.spec_from_file_location("resources26x_profiles", Path(__file__).with_name("convert.py"))
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


_profiles = _profile_support()
PROFILES, DEFAULT_PROFILE, profile_named = _profiles.PROFILES, _profiles.DEFAULT_PROFILE, _profiles.profile_named
FEATURE_FILE, LEGACY_CONDITION_TYPES_263, PROVIDER_TYPES_263 = (
    _profiles.FEATURE_FILE, _profiles.LEGACY_CONDITION_TYPES_263, _profiles.PROVIDER_TYPES_263)

ID = re.compile(r"^[a-z0-9_.-]+:[a-z0-9/._-]+$")
BUILTINS = {"minecraft:builtin/generated"}
REPO = Path(__file__).resolve().parents[2]
# Folders 26.3 renamed; a copy left in an older layer is superseded, not an error.
RETIRED_DIRS_263 = re.compile(r"data/[^/]+/(?:worldgen/configured_feature/|tags/worldgen/configured_feature/)")


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
    def __init__(self, root, vanilla, profile=DEFAULT_PROFILE, base=None):
        """root is the layer under test; base (optional) is the layer below it.

        Later layers replace same-path files, as in the Gradle resource source sets,
        so 26.3's overlay is validated together with the 26.2 resources it patches.
        """
        self.root = Path(root)
        self.profile = profile_named(profile)
        self.version = self.profile["version"]
        self.vanilla = vanilla
        self.jar_files = set(vanilla.namelist())
        self.files = {}
        if base is not None:
            for p in Path(base).rglob("*"):
                rel = p.relative_to(base).as_posix()
                if p.is_file() and not (self.version == "26.3" and RETIRED_DIRS_263.match(rel)):
                    self.files[rel] = p
        self.files.update({p.relative_to(self.root).as_posix(): p
                           for p in self.root.rglob("*") if p.is_file()})
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
                if self.version == "26.3":
                    if parts[2] == "worldgen" and len(parts) > 3 and parts[3] in ("configured_feature", "configured_carver"):
                        self.error(path, "legacy 26.3 worldgen folder " + parts[3])
                    if parts[2:5] == ["tags", "worldgen", "configured_feature"]:
                        self.error(path, "legacy 26.3 worldgen tag folder configured_feature")
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
                if self.version == "26.3":
                    for element in d.get("elements", []) if isinstance(d.get("elements"), list) else []:
                        if isinstance(element, dict) and "shade" in element:
                            self.error(path, "legacy element shade (26.3 uses shade_direction_override)")
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
        order = self.files.get("data/buildscape/creative_tab_order.txt")
        if order is None:
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
                self.error(path, "composter vanilla levels differ from " + self.version)
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
        for path in ("data/minecraft/loot_table/chests/end_city_treasure.json",
                     "data/minecraft/loot_table/chests/woodland_mansion.json",
                     "data/minecraft/loot_table/gameplay/sniffer_digging.json"):
            d = self.json.get(path)
            if d:
                baseline = json.loads(json.dumps(d))
                baseline["pools"] = [p for p in d["pools"] if "buildscape:" not in json.dumps(p)]
                if baseline != self.get_vanilla(path):
                    self.error(path, "vanilla loot baseline differs from " + self.version)
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

    def condition_263(self, path, node):
        if isinstance(node, str):
            ns, sep, name = node.partition(":")
            if not sep or not ID.fullmatch(node) or not self.exists("data/" + ns + "/predicate/" + name + ".json"):
                self.error(path, "missing loot predicate " + repr(node))
        elif isinstance(node, dict):
            if not isinstance(node.get("type"), str):
                self.error(path, "loot predicate needs a type")
        else:
            self.error(path, "loot predicate must be an id or an object: " + repr(node))

    def loot_263(self, path, d):
        """26.3 loot tables: "type" keys, one "condition", one "modifier" (object or list)."""
        for obj in objects(d):
            if "conditions" in obj:
                self.error(path, "legacy conditions list (26.3 has one condition)")
            if "functions" in obj:
                self.error(path, "legacy functions list (26.3 uses modifier)")
            if "function" in obj:
                self.error(path, "legacy function key (26.3 uses type)")
            if isinstance(obj.get("condition"), str) and obj["condition"] in LEGACY_CONDITION_TYPES_263:
                self.error(path, "legacy condition key " + obj["condition"] + " (26.3 uses type)")
            elif "condition" in obj:
                self.condition_263(path, obj["condition"])
            if "modifier" in obj and not isinstance(obj["modifier"], (dict, list)):
                self.error(path, "modifier must be a function or a list of functions")
            kind = obj.get("type")
            if kind in {"minecraft:any_of", "minecraft:all_of"}:
                if not isinstance(obj.get("terms"), list):
                    self.error(path, "predicate needs terms")
                else:
                    for term in obj["terms"]:
                        self.condition_263(path, term)
            if kind == "minecraft:inverted":
                self.condition_263(path, obj.get("term"))
            if kind == "minecraft:block_state_property":
                self.error(path, "legacy block_state_property (26.3 uses match_block)")
            if kind == "minecraft:match_block":
                vals = obj.get("blocks")
                for v in vals if isinstance(vals, list) else [vals]:
                    self.tag(path, "block", v)
            if kind == "minecraft:match_tool":
                pred = obj.get("predicate", {})
                if "enchantments" in pred or "stored_enchantments" in pred:
                    self.error(path, "legacy item enchantment predicate")
                for field in ("minecraft:enchantments", "minecraft:stored_enchantments"):
                    for check in pred.get("predicates", {}).get(field, []):
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
            if kind in {"minecraft:enchant_with_levels", "minecraft:enchant_randomly"}:
                if "treasure" in obj or "enchantments" in obj or "options" not in obj:
                    self.error(path, "legacy enchant function fields")
                else:
                    options = obj["options"]
                    for v in options if isinstance(options, list) else [options]:
                        self.tag(path, "enchantment", v)
            if kind == "minecraft:item" and "name" in obj:
                self.tag(path, "item", obj["name"])
            for tag in obj.get("tags", []) if isinstance(obj.get("tags"), list) else []:
                if isinstance(tag, dict) and "expected" in tag and not str(tag.get("id", "")).startswith("#"):
                    self.error(path, "damage tag id must start with #")

    def advancement_263(self, path, d):
        for name, criterion in (d.get("criteria") or {}).items():
            conditions = criterion.get("conditions", {}) if isinstance(criterion, dict) else {}
            if not isinstance(conditions, dict):
                continue
            for legacy in ("recipe", "recipe_id"):
                if legacy in conditions:
                    self.error(path, "criterion " + name + ": legacy " + legacy + " (26.3 uses recipes)")
            if criterion.get("trigger") == "minecraft:enter_block" and "block" in conditions:
                self.error(path, "criterion " + name + ": legacy block (26.3 uses blocks)")
            for key, value in conditions.items():
                if (isinstance(value, list) and value
                        and all(isinstance(v, dict) and "condition" in v for v in value)):
                    self.error(path, "criterion " + name + ": legacy predicate list in " + key)
            self.loot_263(path, conditions)

    def worldgen_263(self, path, d):
        """Feature and placed feature schema of 26.3: flat config, id/properties states, renamed providers."""
        for obj in objects(d):
            if "Name" in obj or "Properties" in obj:
                self.error(path, "legacy block state Name/Properties (26.3 uses id/properties)")
            if isinstance(obj.get("config"), dict) and isinstance(obj.get("type"), str):
                self.error(path, "legacy nested feature config")
            if obj.get("type") in PROVIDER_TYPES_263 or obj.get("type") == "minecraft:simple_state_provider":
                self.error(path, "legacy state provider type " + obj["type"])
            if obj.get("type") == "minecraft:random_offset":
                self.error(path, "legacy random_offset placement (26.3 uses offset)")
        if "/worldgen/placed_feature/" in path and isinstance(d.get("feature"), str):
            ns, _, name = d["feature"].partition(":")
            if not self.exists("data/" + ns + "/worldgen/feature/" + name + ".json"):
                self.error(path, "missing feature " + d["feature"])

    def biome_modifiers_263(self):
        for path, d in self.json.items():
            if "/neoforge/biome_modifier/" in path and isinstance(d, dict):
                features = d.get("features", [])
                for feature in features if isinstance(features, list) else [features]:
                    if isinstance(feature, str) and not feature.startswith("#"):
                        ns, _, name = feature.partition(":")
                        if not self.exists("data/" + ns + "/worldgen/placed_feature/" + name + ".json"):
                            self.error(path, "missing placed feature " + feature)

    def get_vanilla(self, path):
        return json.loads(self.vanilla.read(path)) if path in self.jar_files else {}

    def run(self):
        version = self.get_vanilla("version.json")
        if version.get("id") != self.version:
            self.error("--mc-jar", "expected real Minecraft " + self.version + " jar")
            return self.errors
        if version.get("pack_version") != self.profile["pack_version"]:
            self.error("--mc-jar", "jar pack_version differs from the " + self.version + " profile")
        self.parse()
        self.entity_ids = entity_registry_ids(self.vanilla)
        if self.json.get("pack.mcmeta") != self.profile["pack"]:
            pv = self.profile["pack_version"]
            self.error("pack.mcmeta", "expected min_format {}.{} / max_format {}.{} for resource {} / data {}".format(
                pv["resource_major"], pv["resource_minor"], pv["data_major"], pv["data_minor"],
                pv["resource_major"], pv["data_major"]))
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
                    if self.version == "26.3":
                        self.loot_263(path, d)
                    else:
                        self.loot(path, d)
                elif "/advancement/" in path:
                    self.advancement(path, d)
                    if self.version == "26.3":
                        self.advancement_263(path, d)
                elif self.version == "26.3" and FEATURE_FILE.match(path):
                    self.worldgen_263(path, d)
                elif "/recipes_pack/" in path:
                    if d.get("aliases", {}).get("F") != "c:":
                        self.error(path, "compact recipe F alias must be c:")
                    aliases = d.get("aliases", {})
                    for v in strings(d):
                        if v.startswith("#") and ":" in v:
                            ns, name = v[1:].split(":", 1)
                            resolved = aliases.get(ns, ns + ":") + name
                            self.tag(path, "item", "#" + resolved)
        if self.version == "26.3":
            self.biome_modifiers_263()
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


def validate(root, jar, profile=DEFAULT_PROFILE, base=None):
    with zipfile.ZipFile(jar) as vanilla:
        validator = Validator(root, vanilla, profile, base)
        errors = validator.run()
        for error in errors:
            print(error)
        print("Validated {} JSON files; {} problems".format(len(validator.json), len(errors)))
        return 1 if errors else 0


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--profile", choices=sorted(PROFILES), default=DEFAULT_PROFILE,
                        help="Minecraft version profile (default: %(default)s)")
    parser.add_argument("--resources-root", type=Path, default=None,
                        help="resource layer to validate (default: common/src/main/resources, or "
                             "common/src/v263/resources for --profile 26.3)")
    parser.add_argument("--base-root", type=Path, default=None,
                        help="layer below --resources-root (26.3 default: common/src/main/resources, so the "
                             "26.3 overlay is validated together with the resources it patches)")
    parser.add_argument("--mc-jar", type=Path, required=True)
    args = parser.parse_args()
    main_resources = REPO / "common/src/main/resources"
    root, base = args.resources_root, args.base_root
    if args.profile == "26.3":
        root = root or REPO / "common/src/v263/resources"
        base = base or main_resources
    else:
        root = root or main_resources
    return validate(root, args.mc_jar, args.profile, base)


if __name__ == "__main__":
    raise SystemExit(main())
