"""Offline 1.21.1 resource/reference checks against its client jar.

Asset checks reuse the existing validator's parent and texture resolution.
This does not replace registry bootstrap or Minecraft codec validation.
"""
import argparse
import json
import math
from pathlib import Path
import sys
import zipfile

import convert as c

sys.dont_write_bytecode = True
shared = c.shared_module("validate")
objects, strings, namespaced, asset_path = (
    shared.objects, shared.strings, shared.namespaced, shared.asset_path)


class Validator(shared.Validator):
    @staticmethod
    def number(value):
        return isinstance(value, (int, float)) and not isinstance(value, bool) and math.isfinite(value)

    def vector(self, path, value, size, label, bounds=None):
        if (not isinstance(value, list) or len(value) != size
                or not all(self.number(v) for v in value)):
            self.error(path, label + " needs " + str(size) + " numbers")
        elif bounds and any(v < bounds[0] or v > bounds[1] for v in value):
            self.error(path, label + " outside " + str(bounds))

    def model_fields(self, path, data):
        """Check the strict BlockElement/Face/UV deserializer fields in 1.21.1."""
        start = len(self.errors)
        for obj in objects(data):
            if "forge_data" in obj:
                self.error(path, "forge_data should be replaced by neoforge_data")
        elements = data.get("elements", [])
        if not isinstance(elements, list):
            self.error(path, "elements must be an array")
            return False
        for element in elements:
            if not isinstance(element, dict):
                self.error(path, "element must be an object")
                continue
            for field in ("from", "to"):
                self.vector(path, element.get(field), 3, "element " + field, (-16, 32))
            if "shade" in element and not isinstance(element["shade"], bool):
                self.error(path, "element shade must be a boolean")
            if "rotation" in element:
                rotation = element["rotation"]
                if not isinstance(rotation, dict):
                    self.error(path, "element rotation must be an object")
                else:
                    if rotation.get("axis") not in ("x", "y", "z"):
                        self.error(path, "element rotation needs axis x, y or z")
                    angle = rotation.get("angle")
                    if not self.number(angle) or angle not in (-45, -22.5, 0, 22.5, 45):
                        self.error(path, "element rotation needs angle 0, +/-22.5 or +/-45")
                    self.vector(path, rotation.get("origin"), 3, "element rotation origin")
                    if "rescale" in rotation and not isinstance(rotation["rescale"], bool):
                        self.error(path, "element rotation rescale must be a boolean")
            faces = element.get("faces")
            if not isinstance(faces, dict) or not 1 <= len(faces) <= 6:
                self.error(path, "element needs 1 to 6 faces")
                continue
            for direction, face in faces.items():
                if direction not in ("down", "up", "north", "south", "west", "east"):
                    self.error(path, "invalid face direction " + direction)
                if not isinstance(face, dict):
                    self.error(path, "face must be an object")
                    continue
                if not isinstance(face.get("texture"), str):
                    self.error(path, "face needs a texture string")
                if "uv" in face:
                    self.vector(path, face["uv"], 4, "face uv")
                rotation = face.get("rotation", 0)
                if not self.number(rotation) or rotation not in (0, 90, 180, 270):
                    self.error(path, "face rotation must be 0, 90, 180 or 270")
        textures = data.get("textures", {})
        if not isinstance(textures, dict) or not all(isinstance(v, str) for v in textures.values()):
            self.error(path, "1.21.1 textures must map names to strings")
        overrides = data.get("overrides", [])
        if not isinstance(overrides, list):
            self.error(path, "overrides must be an array")
        else:
            for override in overrides:
                if not isinstance(override, dict) or not isinstance(override.get("model"), str):
                    self.error(path, "override needs a model string")
                elif (not isinstance(override.get("predicate"), dict)
                      or not all(self.number(v) for v in override["predicate"].values())):
                    self.error(path, "override needs numeric predicates")
        if "parent" in data and not isinstance(data["parent"], str):
            self.error(path, "parent must be a string")
        return len(self.errors) == start

    def blockstate_fields(self, path, data):
        def variant(value):
            if isinstance(value, list):
                if not value:
                    self.error(path, "variant alternatives must not be empty")
                for child in value:
                    if isinstance(child, dict):
                        variant(child)
                    else:
                        self.error(path, "variant must be an object")
                return
            if not isinstance(value, dict):
                self.error(path, "variant must be an object")
                return
            if not isinstance(value.get("model"), str):
                self.error(path, "variant needs a model string")
            for axis in ("x", "y"):
                angle = value.get(axis, 0)
                if not self.number(angle) or angle % 90 != 0:
                    self.error(path, "variant " + axis + " must be a multiple of 90")
            weight = value.get("weight", 1)
            if not isinstance(weight, int) or isinstance(weight, bool) or weight < 1:
                self.error(path, "variant weight must be a positive integer")
            if "uvlock" in value and not isinstance(value["uvlock"], bool):
                self.error(path, "variant uvlock must be a boolean")

        if "variants" in data:
            if not isinstance(data["variants"], dict):
                self.error(path, "variants must be an object")
            else:
                for value in data["variants"].values():
                    variant(value)
        if "multipart" in data:
            if not isinstance(data["multipart"], list):
                self.error(path, "multipart must be an array")
            else:
                for part in data["multipart"]:
                    if not isinstance(part, dict) or "apply" not in part:
                        self.error(path, "multipart entry needs apply")
                    else:
                        variant(part["apply"])

    def sounds(self):
        keys = c.translation_keys(self.get_vanilla("assets/minecraft/lang/en_us.json"), self.json)
        for path, data in self.json.items():
            if not path.startswith("assets/") or not path.endswith("/sounds.json"):
                continue
            if not isinstance(data, dict):
                self.error(path, "sounds must be an object")
                continue
            for event, definition in data.items():
                if not isinstance(definition, dict):
                    self.error(path, event + ": sound event must be an object")
                elif "subtitle" in definition:
                    subtitle = definition["subtitle"]
                    if not isinstance(subtitle, str) or subtitle not in keys:
                        self.error(path, event + ": unknown subtitle " + repr(subtitle))

    def registry_id(self, kind, value):
        if kind == "item":
            if not isinstance(value, str) or not shared.ID.fullmatch(value):
                return False
            return self.exists(asset_path(value, "models/item"))
        if kind == "entity_type":
            # 1.21.1's obfuscated client does not carry EntityTypeIds.class.
            # Entity loot tables cover vanilla's ordinary registered entities.
            return (value in {"minecraft:player", "minecraft:fishing_bobber"}
                    or self.exists("data/" + value.replace(":", "/loot_table/entities/") + ".json"))
        return super().registry_id(kind, value)

    def tag(self, path, kind, value, required=True):
        # c/neoforge tags are supplied by the loader, absent from a vanilla jar.
        # Their syntax is checked; unknown vanilla ids remain errors.
        if isinstance(value, str) and value.startswith(("#c:", "#neoforge:")):
            if not shared.ID.fullmatch(value[1:]):
                self.error(path, "invalid external tag " + value)
            return
        super().tag(path, kind, value, required)

    def parse(self):
        for path, file in self.files.items():
            if file.suffix == ".json" or file.name.endswith(".mcmeta"):
                try:
                    self.json[path] = json.loads(file.read_bytes().decode("utf-8-sig"))
                except (ValueError, UnicodeError) as error:
                    self.error(path, "JSON parse error: " + str(error))
            parts = path.split("/")
            if parts[0] == "data" and len(parts) > 2:
                if parts[2] in c.FOLDERS or parts[2] == "loot_modifiers":
                    self.error(path, "legacy or unsupported data folder")
                if parts[2] == "tags" and len(parts) > 3 and parts[3] in c.TAG_FOLDERS:
                    self.error(path, "legacy tag folder")
                if parts[1] == "forge":
                    self.error(path, "legacy Forge namespace")
            if parts[0] == "assets" and len(parts) > 2 and parts[2] == "items":
                self.error(path, "1.21.4+ item definition in a 1.21.1 tree")

    def models(self):
        for path, data in self.json.items():
            if "/blockstates/" in path:
                if not isinstance(data, dict) or not ("variants" in data or "multipart" in data):
                    self.error(path, "expected variants or multipart")
                    continue
                self.blockstate_fields(path, data)
                for obj in objects(data):
                    if "model" in obj and self.reference(path, obj["model"], "models"):
                        self.concrete_models.add(namespaced(obj["model"]))
            if "/models/" in path:
                if not isinstance(data, dict):
                    self.error(path, "model must be an object")
                    continue
                if not self.model_fields(path, data):
                    continue
                parent = data.get("parent")
                if parent and namespaced(parent) not in shared.BUILTINS | {"minecraft:builtin/entity"}:
                    self.reference(path, parent, "models")
                self.textures(path, data)
                # Overrides are valid in 1.21.1, and their model targets matter.
                for override in data.get("overrides", []):
                    if self.reference(path, override.get("model"), "models"):
                        self.concrete_models.add(namespaced(override["model"]))
                if "/models/item/" in path:
                    ns, name = path.split("/")[1], path.split("/models/", 1)[1][:-5]
                    self.concrete_models.add(ns + ":" + name)
        for value in sorted(self.concrete_models):
            path = asset_path(value, "models")
            # The shared merge assumes well-formed parent and texture fields.
            # Diagnose malformed ancestors before they can raise TypeError.
            ancestor, seen, valid = value, set(), True
            while ancestor:
                ancestor = namespaced(ancestor)
                if ancestor in seen or ancestor in shared.BUILTINS | {"minecraft:builtin/entity"}:
                    break
                seen.add(ancestor)
                ancestor_path = asset_path(ancestor, "models")
                data = self.get(ancestor_path)
                if not isinstance(data, dict) or not self.model_fields(ancestor_path, data):
                    valid = False
                    break
                ancestor = data.get("parent")
            if not valid:
                continue
            merged = self.merged_model(value)
            if self.model_fields(path, merged):
                self.textures(path, merged, True)

    def ingredient(self, path, value):
        if isinstance(value, list):
            if not value:
                self.error(path, "empty ingredient alternatives")
            for alternative in value:
                self.ingredient(path, alternative)
        elif isinstance(value, dict) and set(value) in ({"item"}, {"tag"}):
            field = next(iter(value))
            target = value[field]
            if not isinstance(target, str):
                self.error(path, "ingredient id must be a string")
            else:
                self.tag(path, "item", ("#" if field == "tag" else "") + target)
        else:
            self.error(path, "1.21.1 ingredient needs an item/tag object")

    def loot(self, path, data):
        super().loot(path, data)
        for function in c.unsupported_loot_functions(data):
            self.error(path, "unsupported 1.21.1 loot function " + function)

    def advancement(self, path, data):
        display = data.get("display", {})
        if display:
            icon = display.get("icon", {})
            if "item" in icon or "id" not in icon:
                self.error(path, "display icon needs id")
            else:
                self.tag(path, "item", icon["id"])
            background = display.get("background")
            if background:
                ns, name = namespaced(background).split(":", 1)
                if not self.exists("assets/" + ns + "/" + name):
                    self.error(path, "missing advancement background " + background)
        if "parent" in data:
            self.tag(path, "advancement", data["parent"])
        criteria = data.get("criteria")
        if not isinstance(criteria, dict) or not criteria:
            self.error(path, "advancement needs criteria")
        if "requirements" in data:
            if {v for group in data["requirements"] for v in group} != set(criteria or {}):
                self.error(path, "requirements/criteria mismatch")
        for name in data.get("rewards", {}).get("recipes", []):
            self.tag(path, "recipe", name)

    def run(self):
        if self.get_vanilla("version.json").get("id") != "1.21.1":
            return ["--mc-jar: expected Minecraft 1.21.1 client jar"]
        self.parse()
        if self.json.get("pack.mcmeta") != c.PACK:
            self.error("pack.mcmeta", "expected resource 34 / data 48 supported range")
        self.models()
        self.sounds()
        removed = c.removals()
        files = {Path(path) for path in self.files} | {Path(path) for path in self.jar_files}
        for path, data in self.json.items():
            if path.startswith("data/"):
                for kind, value in c.missing_data_references(Path(path), data, files):
                    self.error(path, "missing " + kind + " dependency " + value)
                reason = c.unsupported_loader_data(data)
                if reason:
                    self.error(path, reason)
                for value in strings(data):
                    if value in removed or value.startswith("#forge:"):
                        self.error(path, "retired or legacy reference " + value)
                if "/tags/" in path:
                    parts = path.split("/")
                    kind = "/".join(parts[3:5]) if parts[3] == "worldgen" else parts[3]
                    if not isinstance(data, dict) or not isinstance(data.get("values"), list):
                        self.error(path, "tag needs values list")
                        continue
                    for value in data["values"]:
                        if isinstance(value, dict):
                            self.tag(path, kind, value.get("id"), value.get("required", True))
                        else:
                            self.tag(path, kind, value)
                elif "/recipe/" in path:
                    self.recipe(path, data)
                elif "/loot_table/" in path:
                    self.loot(path, data)
                elif "/advancement/" in path:
                    self.advancement(path, data)
                elif "/worldgen/" in path:
                    for obj in objects(data):
                        if "Name" in obj:
                            self.tag(path, "block", obj["Name"])
                    for kind, value in c.data_references(Path(path), data):
                        self.tag(path, kind, value)
                elif "/recipes_pack/" in path:
                    aliases = data.get("aliases", {})
                    if aliases.get("F") != "c:":
                        self.error(path, "compact recipe F alias must be c:")
                    for value in strings(data):
                        raw = value.removeprefix("#")
                        ns, sep, name = raw.partition(":")
                        if sep and ns in aliases:
                            resolved = aliases[ns] + name
                            if value.startswith("#"):
                                self.tag(path, "item", "#" + resolved)
                            elif resolved in removed:
                                self.error(path, "retired compact id " + resolved)
                            elif resolved.startswith("minecraft:") and name:
                                self.tag(path, "item", resolved)
                        elif raw.startswith("minecraft:") and name:
                            self.tag(path, "item", ("#" if value.startswith("#") else "") + raw)
        return sorted(set(self.errors))


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
    parser.add_argument("resources_root", nargs="?", type=Path)
    parser.add_argument("mc_jar", nargs="?", type=Path)
    parser.add_argument("--resources-root", dest="root_option", type=Path)
    parser.add_argument("--mc-jar", dest="jar_option", type=Path)
    args = parser.parse_args()
    root = args.root_option or args.resources_root or c.REPO / "common/src/v121x/resources"
    jar = args.jar_option or args.mc_jar
    if not jar:
        parser.error("a Minecraft 1.21.1 client jar is required")
    return validate(root, jar)


if __name__ == "__main__":
    raise SystemExit(main())
