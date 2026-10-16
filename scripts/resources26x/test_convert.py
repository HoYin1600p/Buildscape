"""Small independent fixtures for each format migration rule."""
import json
from pathlib import Path
import tempfile
import unittest
import zipfile

import convert as c


class ConversionTests(unittest.TestCase):
    def test_folders_in_every_namespace(self):
        for namespace in ("minecraft", "buildscape", "other"):
            for old, new in c.FOLDERS.items():
                self.assertEqual(c.converted_path(Path("data", namespace, old, "x.json")),
                                 Path("data", namespace, new, "x.json"))
            for old, new in c.TAG_FOLDERS.items():
                self.assertEqual(c.converted_path(Path("data", namespace, "tags", old, "x.json")),
                                 Path("data", namespace, "tags", new, "x.json"))

    def test_common_tags_and_references(self):
        self.assertEqual(c.converted_path(Path("data/forge/tags/items/stained_glass.json")),
                         Path("data/c/tags/item/glass_blocks/dyed.json"))
        self.assertEqual(c.references(["#forge:glass", "#F:stone", "forge:ingots/steel"]),
                         ["#c:glass_blocks", "#F:stones", "c:ingots/steel"])
        self.assertEqual(c.references("forge:conditional"), "forge:conditional")

    def test_recipe_shapes(self):
        d = {"type": "minecraft:crafting_shaped", "pattern": ["   ", " # ", "   "],
             "key": {"#": {"tag": "c:ingots/steel"}}, "result": {"item": "buildscape:x", "count": 2}}
        result = c.recipe(d)
        self.assertEqual(result["key"], {"#": "#c:ingots/steel"})
        self.assertEqual(result["pattern"], ["#"])
        self.assertEqual(result["result"], {"id": "buildscape:x", "count": 2})
        self.assertEqual(c.ingredient([{ "item": "minecraft:stone"}, {"tag": "minecraft:logs"}]),
                         ["minecraft:stone", "#minecraft:logs"])
        self.assertIn("item", d["result"], "conversion must not mutate its argument")

    def test_cooking_stonecutting_smithing(self):
        d = {"type": "minecraft:stonecutting", "ingredient": {"item": "minecraft:stone"},
             "result": "minecraft:stone_slab", "count": 2}
        result = c.recipe(d)
        self.assertEqual(result["result"], {"id": "minecraft:stone_slab", "count": 2})
        self.assertNotIn("count", result)
        d = {"type": "minecraft:smithing_transform", "base": {"item": "minecraft:iron_sword"},
             "addition": {"tag": "c:ingots/steel"}, "template": {"item": "minecraft:diamond"},
             "result": {"item": "buildscape:steel_sword"}}
        self.assertEqual(c.recipe(d)["addition"], "#c:ingots/steel")

    def test_custom_serializers_unchanged(self):
        for kind in ("custom_firework_star", "infinite_phoenix_firework_star"):
            d = {"type": "buildscape:" + kind, "custom": [1, 2], "result": {"item": "custom"}}
            self.assertEqual(c.recipe(d), d)

    def test_nested_loot_predicates(self):
        d = {"condition": "minecraft:alternative", "terms": [
            {"condition": "minecraft:match_tool", "predicate": {"enchantments": [
                {"enchantment": "minecraft:silk_touch", "levels": {"min": 1}}]}}]}
        out = c.loot(d)
        self.assertEqual(out["condition"], "minecraft:any_of")
        check = out["terms"][0]["predicate"]["predicates"]["minecraft:enchantments"][0]
        self.assertEqual(check, {"enchantments": "minecraft:silk_touch", "levels": {"min": 1}})
        self.assertEqual(c.loot(out), out)

    def test_enchant_functions(self):
        d = {"function": "minecraft:enchant_with_levels", "levels": 30, "treasure": True}
        self.assertEqual(c.loot(d), {"function": d["function"], "levels": 30,
                                    "options": "#minecraft:on_random_loot"})
        d["treasure"] = False
        self.assertEqual(c.loot(d)["options"], "#minecraft:in_enchanting_table")
        self.assertEqual(c.loot({"function": "minecraft:enchant_randomly", "enchantments": ["minecraft:mending"]})["options"],
                         ["minecraft:mending"])

    def test_advancement(self):
        d = {"display": {"icon": {"item": "minecraft:stone"},
                         "background": "minecraft:textures/gui/advancements/backgrounds/stone.png"}}
        self.assertEqual(c.advancement(d), {"display": {"icon": {"id": "minecraft:stone"},
                                                       "background": "minecraft:gui/advancements/backgrounds/stone"}})

    def test_door_hinge_open_uses_vanilla_rotations(self):
        vanilla = {"variants": {}}
        old = {"variants": {}}
        for half, part in (("lower", "bottom"), ("upper", "top")):
            for hinge in ("left", "right"):
                for opened in ("false", "true"):
                    key = "facing=north,half=" + half + ",hinge=" + hinge + ",open=" + opened
                    suffix = part + "_" + hinge + ("_open" if opened == "true" else "")
                    vanilla["variants"][key] = {"model": "minecraft:block/oak_door_" + suffix, "y": 180}
                    old["variants"][key] = {"model": "buildscape:block/old"}
        result, models = c.door_state(old, "cherry_door", vanilla)
        self.assertEqual(len(models), 8)
        for key, v in result["variants"].items():
            self.assertEqual(v["y"], 180)
            self.assertTrue(v["model"].startswith("buildscape:block/cherry_door_"))
        self.assertEqual(c.door_parent("minecraft:block/door_top_rh"), "minecraft:block/door_top_right")

    def test_item_definitions_and_special_fallback(self):
        self.assertEqual(c.item_definition("x", True)["model"]["model"], "buildscape:item/x")
        self.assertEqual(c.item_definition("x", False)["model"]["model"], "buildscape:block/x")
        self.assertEqual(c.SPECIAL_ITEMS, ("template_glass_jar",))
        for name in c.SPECIAL_ITEMS:
            self.assertTrue(c.item_definition(name, True)["model"]["model"].startswith("buildscape:block/"))

    def test_existing_special_item_definitions_are_kept(self):
        special = {"model": {"type": "minecraft:special", "base": "buildscape:item/copper_chest",
                             "model": {"type": "buildscape:copper_chest",
                                       "texture": "buildscape:textures/entity/chest/copper_chest.png"}}}
        jar_special = {"model": {"type": "minecraft:special", "base": "buildscape:item/red_glass_jar",
                                 "model": {"type": "buildscape:glass_jar"}}}
        self.assertTrue(c.is_special_definition(special))
        self.assertFalse(c.is_special_definition(c.item_definition("x", True)))
        with tempfile.TemporaryDirectory() as tmp:
            root, jar = Path(tmp) / "resources", Path(tmp) / "vanilla.jar"
            c.write(root / "assets/buildscape/items/copper_chest.json", special)
            c.write(root / "assets/buildscape/items/red_glass_jar.json", jar_special)
            c.write(root / "assets/buildscape/models/item/red_glass_jar.json", {"parent": "minecraft:block/block"})
            with zipfile.ZipFile(jar, "w") as z:
                z.writestr("version.json", '{"id":"26.2"}')
                z.writestr("assets/minecraft/blockstates/oak_door.json", '{"variants":{}}')
            c.convert(root, jar)
            self.assertEqual(c.load(root / "assets/buildscape/items/copper_chest.json"), special)
            self.assertEqual(c.load(root / "assets/buildscape/items/red_glass_jar.json"), jar_special)
            # Special items without a definition still get the plain fallback.
            self.assertEqual(c.load(root / "assets/buildscape/items/template_glass_jar.json"),
                             c.item_definition("template_glass_jar", True))
            self.assertEqual(c.convert(root, jar), 0)

    def test_vanilla_chest_baseline_language_and_bom(self):
        with tempfile.TemporaryDirectory() as tmp:
            root, jar = Path(tmp) / "resources", Path(tmp) / "vanilla.jar"
            baseline = {"type": "minecraft:chest", "pools": [{"entries": [{"name": "minecraft:diamond"}]}]}
            bonus = {"entries": [{"name": "buildscape:ancient_ashen_scroll"}]}
            c.write(root / "data/minecraft/loot_tables/chests/end_city_treasure.json",
                    {"type": "minecraft:chest", "pools": [{"entries": [{"name": "minecraft:iron_ingot"}]}, bonus]})
            c.write(root / "assets/minecraft/lang/en_us.json",
                    {"block.minecraft.chain": "Iron Chain", "item.minecraft.chain": "Iron Chain"})
            order = root / "data/buildscape/creative_tab_order.txt"
            order.parent.mkdir(parents=True, exist_ok=True)
            order.write_bytes(b"\xef\xbb\xbfbit_copper_block\r\n")
            with zipfile.ZipFile(jar, "w") as z:
                z.writestr("version.json", '{"id":"26.2"}')
                z.writestr("assets/minecraft/blockstates/oak_door.json", '{"variants":{}}')
                z.writestr("data/minecraft/loot_table/chests/end_city_treasure.json", json.dumps(baseline))
            c.convert(root, jar)
            expected = dict(baseline, pools=baseline["pools"] + [bonus])
            self.assertEqual(c.load(root / "data/minecraft/loot_table/chests/end_city_treasure.json"), expected)
            self.assertEqual(c.load(root / "assets/minecraft/lang/en_us.json"), {"block.minecraft.iron_chain": "Iron Chain"})
            self.assertTrue((root / "assets/buildscape/items/bit_copper_block.json").exists())
            self.assertEqual(c.convert(root, jar), 0)

    def test_in_place_idempotency_newlines_alias_quartz_and_empty_modifiers(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp) / "resources"
            jar = Path(tmp) / "vanilla.jar"
            fixtures = {
                "data/forge/loot_modifiers/global_loot_modifiers.json": {"entries": []},
                "data/forge/tags/items/glass.json": {"values": ["minecraft:glass"]},
                "data/buildscape/recipes_pack/crafting.json": {"aliases": {"F": "forge:"}, "x": "#F:glass"},
                "assets/buildscape/models/block/x.json": {"textures": {"side": "minecraft:block/quartz_pillar"}},
                "assets/buildscape/models/item/x.json": {"parent": "buildscape:block/x"},
            }
            for name, d in fixtures.items():
                p = root / name
                p.parent.mkdir(parents=True, exist_ok=True)
                p.write_bytes(json.dumps(d, indent=2).replace("\n", "\r\n").encode())
            with zipfile.ZipFile(jar, "w") as z:
                z.writestr("version.json", '{"id":"26.2"}')
                z.writestr("assets/minecraft/blockstates/oak_door.json", '{"variants":{}}')
            self.assertGreater(c.convert(root, jar), 0)
            before = {p.relative_to(root): p.read_bytes() for p in root.rglob("*") if p.is_file()}
            self.assertEqual(c.convert(root, jar), 0)
            self.assertEqual(before, {p.relative_to(root): p.read_bytes() for p in root.rglob("*") if p.is_file()})
            self.assertFalse((root / "data/forge").exists())
            self.assertEqual(c.load(root / "data/buildscape/recipes_pack/crafting.json")["aliases"]["F"], "c:")
            quartz = root / "assets/buildscape/models/block/x.json"
            self.assertIn(b"\r\n", quartz.read_bytes())
            self.assertEqual(c.load(quartz)["textures"]["side"], "minecraft:block/quartz_pillar_side")
            self.assertEqual(c.load(root / "pack.mcmeta"), c.PACK)

    def test_nonempty_modifiers_fail(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp) / "resources"
            p = root / "data/forge/loot_modifiers/global_loot_modifiers.json"
            c.write(p, {"entries": ["buildscape:x"]})
            jar = Path(tmp) / "vanilla.jar"
            with zipfile.ZipFile(jar, "w") as z:
                z.writestr("version.json", '{"id":"26.2"}')
            with self.assertRaisesRegex(ValueError, "Nonempty"):
                c.convert(root, jar)


def jar263(path, extra=None, version=None):
    """A minimal 26.3 jar: version.json (with the real pack_version) plus the given entries."""
    version = version or {"id": "26.3", "pack_version": c.PROFILES["26.3"]["pack_version"]}
    with zipfile.ZipFile(path, "w") as z:
        z.writestr("version.json", json.dumps(version))
        z.writestr("assets/minecraft/blockstates/oak_door.json", '{"variants":{}}')
        for name, text in (extra or {}).items():
            z.writestr(name, text)
    return path


class Profile263Tests(unittest.TestCase):
    def test_profiles(self):
        self.assertEqual(c.DEFAULT_PROFILE, "26.2")
        self.assertEqual(c.PROFILES["26.2"]["pack"], c.PACK)
        self.assertEqual(c.PROFILES["26.3"]["pack"]["pack"]["min_format"], [97, 1])
        self.assertEqual(c.PROFILES["26.3"]["pack"]["pack"]["max_format"], [121, 0])
        with self.assertRaisesRegex(ValueError, "Unknown profile"):
            c.profile_named("26.4")

    def test_profile_must_match_jar(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp) / "resources"
            root.mkdir()
            old = Path(tmp) / "old.jar"
            with zipfile.ZipFile(old, "w") as z:
                z.writestr("version.json", '{"id":"26.2"}')
            with self.assertRaisesRegex(ValueError, "Expected a 26.3 jar, got 26.2"):
                c.convert(root, old, "26.3")
            with self.assertRaisesRegex(ValueError, "Expected a 26.2 jar, got 26.3"):
                c.convert(root, jar263(Path(tmp) / "new.jar"))
            wrong = jar263(Path(tmp) / "wrong.jar", version={"id": "26.3", "pack_version": {"resource_major": 1}})
            with self.assertRaisesRegex(ValueError, "pack_version"):
                c.convert(root, wrong, "26.3")

    def test_loot_conditions_and_functions(self):
        shears = {"condition": "minecraft:match_tool", "predicate": {"items": "minecraft:shears"}}
        silk = {"condition": "minecraft:match_tool", "predicate": {"predicates": {"minecraft:enchantments": [
            {"enchantments": "minecraft:silk_touch", "levels": {"min": 1}}]}}}
        old = {"type": "minecraft:block", "pools": [{
            "rolls": 1.0, "bonus_rolls": 0.0,
            "conditions": [{"condition": "minecraft:survives_explosion"}, {"condition": "minecraft:inverted",
                           "term": {"condition": "minecraft:any_of", "terms": [shears, silk]}}],
            "entries": [{"type": "minecraft:item", "name": "buildscape:x",
                         "conditions": [{"condition": "minecraft:block_state_property", "block": "buildscape:x",
                                         "properties": {"half": "lower"}}],
                         "functions": [{"function": "minecraft:set_count", "add": False,
                                        "count": {"type": "minecraft:uniform", "min": 1.0, "max": 2.0},
                                        "conditions": [{"condition": "minecraft:random_chance", "chance": 0.5}]},
                                       {"function": "minecraft:explosion_decay"}]},
                        {"type": "minecraft:item", "name": "buildscape:y",
                         "functions": [{"function": "minecraft:set_damage", "damage": {"type": "minecraft:uniform", "min": 0.0, "max": 1.0}}]}]}]}
        new = c.loot_263(old)
        pool = new["pools"][0]
        self.assertEqual(pool["rolls"], 1)
        self.assertIsInstance(pool["rolls"], int)
        self.assertEqual(pool["condition"], {"type": "minecraft:all_of", "terms": [
            {"type": "minecraft:survives_explosion"},
            {"type": "minecraft:inverted", "term": {"type": "minecraft:any_of",
                                                      "terms": ["minecraft:tool/can_shear", "minecraft:tool/can_silk_touch"]}}]})
        first, second = pool["entries"]
        self.assertEqual(first["condition"], {"type": "minecraft:match_block", "blocks": "buildscape:x",
                                              "state": {"half": "lower"}})
        self.assertEqual(first["modifier"][0], {"type": "minecraft:set_count", "add": False,
                                                 "count": {"type": "minecraft:uniform", "min": 1, "max": 2},
                                                 "condition": {"type": "minecraft:random_chance", "chance": 0.5}})
        self.assertEqual(first["modifier"][1], {"type": "minecraft:explosion_decay"})
        # One function is an object; set_damage keeps its float notation.
        self.assertEqual(second["modifier"], {"type": "minecraft:set_damage",
                                              "damage": {"type": "minecraft:uniform", "min": 0.0, "max": 1.0}})
        self.assertIsInstance(second["modifier"]["damage"]["min"], float)
        for key in ("conditions", "functions"):
            self.assertNotIn(key, json.dumps(new))
        self.assertEqual(c.loot_263(new), new)
        self.assertEqual(json.dumps(c.loot_263(new)), json.dumps(new))

    def test_loot_other_predicates(self):
        self.assertEqual(c.loot_263({"conditions": [{"condition": "minecraft:table_bonus", "chances": [0.1, 1.0],
                                                   "enchantment": "minecraft:fortune"}]})["condition"]["chances"], [0.1, 1.0])
        tool = {"condition": "minecraft:match_tool", "predicate": {"items": ["minecraft:wooden_hoe"]}}
        self.assertEqual(c.loot_263({"conditions": [tool]})["condition"],
                         {"type": "minecraft:match_tool", "predicate": {"items": ["minecraft:wooden_hoe"]}})
        damage = {"condition": "minecraft:damage_source_properties",
                  "predicate": {"tags": [{"id": "minecraft:is_projectile", "expected": True}]}}
        self.assertEqual(c.loot_263({"conditions": [damage]})["condition"]["predicate"]["tags"][0]["id"],
                         "#minecraft:is_projectile")
        # A string condition naming a predicate resource is 26.3 data, not a legacy type.
        data = {"entries": [{"type": "minecraft:item", "condition": "minecraft:tool/can_shear", "name": "minecraft:x"}]}
        self.assertEqual(c.loot_263(data), data)

    def test_advancement(self):
        d = {"criteria": {
            "a": {"trigger": "minecraft:recipe_unlocked", "conditions": {"recipe": "buildscape:x"}},
            "b": {"trigger": "minecraft:recipe_crafted", "conditions": {"recipe_id": "minecraft:crafter"}},
            "c": {"trigger": "minecraft:enter_block", "conditions": {"block": "minecraft:water"}},
            "d": {"trigger": "minecraft:player_killed_entity", "conditions": {"entity": [
                {"condition": "minecraft:entity_properties", "entity": "this", "predicate": {}}]}},
            "e": {"trigger": "minecraft:impossible"}}}
        out = c.advancement_263(d)
        self.assertEqual(out["criteria"]["a"]["conditions"], {"recipes": "buildscape:x"})
        self.assertEqual(out["criteria"]["b"]["conditions"], {"recipes": "minecraft:crafter"})
        self.assertEqual(out["criteria"]["c"]["conditions"], {"blocks": "minecraft:water"})
        self.assertEqual(out["criteria"]["d"]["conditions"]["entity"],
                         {"type": "minecraft:entity_properties", "entity": "this", "predicate": {}})
        self.assertIn("recipe", d["criteria"]["a"]["conditions"], "conversion must not mutate its argument")
        self.assertEqual(c.advancement_263(out), out)

    def test_worldgen_schema(self):
        old = {"type": "minecraft:tree", "config": {
            "below_trunk_provider": {"type": "minecraft:rule_based_state_provider", "rules": [{
                "if_true": {"type": "minecraft:not", "predicate": {"type": "minecraft:matching_block_tag",
                                                                    "tag": "minecraft:cannot_replace_below_tree_trunk"}},
                "then": {"type": "minecraft:simple_state_provider", "state": {"Name": "minecraft:dirt"}}}]},
            "foliage_provider": {"type": "minecraft:weighted_state_provider", "entries": [
                {"data": {"Name": "buildscape:a", "Properties": {"x": "1"}}, "weight": 1}]},
            "trunk_provider": {"type": "minecraft:simple_state_provider",
                               "state": {"Name": "minecraft:oak_log", "Properties": {"axis": "y"}}},
            "ignore_vines": True}}
        new = c.worldgen_node_263(old)
        self.assertEqual(new["type"], "minecraft:tree")
        self.assertNotIn("config", new)
        self.assertTrue(new["ignore_vines"])
        self.assertEqual(new["trunk_provider"], {"id": "minecraft:oak_log", "properties": {"axis": "y"}})
        self.assertEqual(new["foliage_provider"], {"type": "minecraft:weighted", "entries": [
            {"data": {"id": "buildscape:a", "properties": {"x": "1"}}, "weight": 1}]})
        self.assertEqual(new["below_trunk_provider"]["type"], "minecraft:rule_based")
        self.assertEqual(new["below_trunk_provider"]["rules"][0]["then"], {"id": "minecraft:dirt"})
        self.assertEqual(c.worldgen_node_263(new), new)

    def test_placed_feature_schema(self):
        old = {"feature": "buildscape:x", "placement": [
            {"type": "minecraft:random_offset", "xz_spread": {"type": "minecraft:trapezoid", "min": -7, "max": 7, "plateau": 0},
             "y_spread": 0},
            {"type": "minecraft:random_offset", "xz_spread": 0, "y_spread": -1},
            {"type": "minecraft:block_predicate_filter", "predicate": {"type": "minecraft:would_survive",
             "state": {"Name": "minecraft:mangrove_propagule", "Properties": {"age": "0"}}}}]}
        new = c.worldgen_node_263(old)
        spread = {"type": "minecraft:trapezoid", "min": -7, "max": 7, "plateau": 0}
        self.assertEqual(new["placement"][0], {"type": "minecraft:offset", "x": spread, "y": 0, "z": spread})
        self.assertEqual(new["placement"][1], {"type": "minecraft:offset", "x": 0, "y": -1, "z": 0})
        self.assertEqual(new["placement"][2]["predicate"]["state"],
                         {"id": "minecraft:mangrove_propagule", "properties": {"age": "0"}})
        self.assertEqual(c.worldgen_node_263(new), new)

    def test_model_shade(self):
        old = {"elements": [{"from": [0, 0, 0], "shade": False}, {"from": [1, 1, 1], "shade": True}, {"from": [2, 2, 2]}]}
        new = c.model_263(old)
        self.assertEqual(new["elements"][0], {"from": [0, 0, 0], "shade_direction_override": "up"})
        self.assertEqual(new["elements"][1], {"from": [1, 1, 1]})
        self.assertEqual(new["elements"][2], {"from": [2, 2, 2]})
        self.assertIn("shade", old["elements"][0], "conversion must not mutate its argument")
        self.assertEqual(c.model_263(new), new)

    def test_worldgen_folders(self):
        self.assertEqual(c.converted_path_263(Path("data/buildscape/worldgen/configured_feature/x.json")),
                         Path("data/buildscape/worldgen/feature/x.json"))
        self.assertEqual(c.converted_path_263(Path("data/minecraft/tags/worldgen/configured_feature/x.json")),
                         Path("data/minecraft/tags/worldgen/feature/x.json"))
        self.assertEqual(c.converted_path_263(Path("data/buildscape/worldgen/placed_feature/x.json")),
                         Path("data/buildscape/worldgen/placed_feature/x.json"))
        with self.assertRaisesRegex(ValueError, "carver"):
            c.converted_path_263(Path("data/x/worldgen/configured_carver/y.json"))

    def test_convert_tree_for_263_is_idempotent(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp) / "resources"
            baseline = {"type": "minecraft:chest", "pools": [{"rolls": 1, "entries": [{"type": "minecraft:item", "name": "minecraft:diamond"}]}]}
            bonus = {"rolls": 1.0, "entries": [{"type": "minecraft:item", "name": "buildscape:ancient_ashen_scroll"}],
                     "conditions": [{"condition": "minecraft:survives_explosion"}]}
            c.write(root / "data/minecraft/loot_table/chests/end_city_treasure.json",
                    {"type": "minecraft:chest", "pools": [baseline["pools"][0], bonus]})
            c.write(root / "data/buildscape/loot_table/blocks/x.json", {"type": "minecraft:block", "pools": [bonus]})
            c.write(root / "data/buildscape/worldgen/configured_feature/f.json",
                    {"type": "minecraft:simple_block", "config": {"to_place": {"type": "minecraft:simple_state_provider",
                                                                            "state": {"Name": "buildscape:x"}}}})
            c.write(root / "data/minecraft/tags/worldgen/configured_feature/t.json", {"values": ["buildscape:f"]})
            c.write(root / "assets/buildscape/models/block/m.json", {"elements": [{"shade": False}]})
            jar = jar263(Path(tmp) / "vanilla.jar", {
                "data/minecraft/loot_table/chests/end_city_treasure.json": json.dumps(baseline)})
            self.assertGreater(c.convert(root, jar, "26.3"), 0)
            before = {p.relative_to(root): p.read_bytes() for p in root.rglob("*") if p.is_file()}
            self.assertEqual(c.convert(root, jar, "26.3"), 0)
            self.assertEqual(before, {p.relative_to(root): p.read_bytes() for p in root.rglob("*") if p.is_file()})
            self.assertEqual(c.load(root / "pack.mcmeta"), c.PACK_263)
            self.assertFalse((root / "data/buildscape/worldgen/configured_feature").exists())
            self.assertEqual(c.load(root / "data/buildscape/worldgen/feature/f.json"),
                             {"type": "minecraft:simple_block", "to_place": {"id": "buildscape:x"}})
            self.assertTrue((root / "data/minecraft/tags/worldgen/feature/t.json").exists())
            chest = c.load(root / "data/minecraft/loot_table/chests/end_city_treasure.json")
            self.assertEqual(chest["pools"][0], baseline["pools"][0])
            self.assertEqual(chest["pools"][1]["condition"], {"type": "minecraft:survives_explosion"})
            self.assertEqual(c.load(root / "assets/buildscape/models/block/m.json"),
                             {"elements": [{"shade_direction_override": "up"}]})

    def test_derive_overlay_keeps_hand_maintained_files(self):
        with tempfile.TemporaryDirectory() as tmp:
            base, out = Path(tmp) / "base", Path(tmp) / "overlay"
            c.write(base / "data/buildscape/loot_table/blocks/x.json",
                    {"type": "minecraft:block", "pools": [{"rolls": 1, "conditions": [{"condition": "minecraft:survives_explosion"}],
                                                          "entries": []}]})
            c.write(base / "data/buildscape/recipe/same.json", {"type": "buildscape:custom_firework_star"})
            c.write(base / "data/buildscape/worldgen/configured_feature/f.json",
                    {"type": "minecraft:simple_block", "config": {"to_place": {"Name": "buildscape:x"}}})
            c.write(base / "pack.mcmeta", c.PACK)
            c.write(out / "buildscape.mixins.json", {"keep": True})
            c.write(out / "data/buildscape/stale.json", {"stale": True})
            jar = jar263(Path(tmp) / "vanilla.jar")
            written, removed, retired = c.derive_overlay(base, out, jar, "26.3")
            produced = sorted(p.relative_to(out).as_posix() for p in out.rglob("*") if p.is_file())
            self.assertEqual(produced, ["assets/buildscape/items/template_glass_jar.json", "buildscape.mixins.json",
                                        "data/buildscape/loot_table/blocks/x.json",
                                        "data/buildscape/worldgen/feature/f.json", "pack.mcmeta"])
            self.assertEqual(c.load(out / "buildscape.mixins.json"), {"keep": True})
            self.assertEqual((written, removed), (4, 1))
            self.assertEqual(retired, ["data/buildscape/worldgen/configured_feature/f.json"])
            self.assertEqual(c.load(out / "pack.mcmeta"), c.PACK_263)
            self.assertEqual(c.derive_overlay(base, out, jar, "26.3"), (0, 0, retired))
            # The base layer is never modified.
            self.assertEqual(c.load(base / "pack.mcmeta"), c.PACK)

    def test_validator_flags_legacy_263_forms(self):
        import validate as v
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp) / "resources"
            root.mkdir()
            jar = jar263(Path(tmp) / "vanilla.jar", {
                "data/minecraft/predicate/tool/can_shear.json": "{}",
                "data/minecraft/worldgen/feature/oak.json": "{}"})
            with zipfile.ZipFile(jar) as archive:
                validator = v.Validator(root, archive, "26.3")
                validator.loot_263("t.json", {"pools": [{"conditions": [{"condition": "minecraft:survives_explosion"}],
                                                          "entries": [{"functions": [{"function": "minecraft:set_count"}]}]}]})
                self.assertEqual(len(validator.errors), 4, validator.errors)
                validator.errors.clear()
                validator.loot_263("t.json", {"pools": [{"condition": "minecraft:tool/can_shear",
                                                          "entries": [{"condition": {"type": "minecraft:any_of",
                                                                       "terms": ["minecraft:tool/can_shear"]}}]}]})
                self.assertEqual(validator.errors, [])
                validator.loot_263("t.json", {"condition": "minecraft:tool/missing"})
                self.assertEqual(len(validator.errors), 1)
                validator.errors.clear()
                validator.advancement_263("a.json", {"criteria": {"a": {"trigger": "minecraft:recipe_unlocked",
                                                                         "conditions": {"recipe": "x:y"}}}})
                self.assertEqual(len(validator.errors), 1)
                validator.errors.clear()
                validator.worldgen_263("data/x/worldgen/placed_feature/p.json", {
                    "feature": "minecraft:missing", "placement": [{"type": "minecraft:random_offset"},
                                                                  {"state": {"Name": "a:b"}}]})
                self.assertEqual(len(validator.errors), 3, validator.errors)
                validator.errors.clear()
                validator.worldgen_263("data/x/worldgen/placed_feature/p.json", {
                    "feature": "minecraft:oak", "placement": [{"type": "minecraft:offset", "x": 0, "y": 0, "z": 0}]})
                self.assertEqual(validator.errors, [])

    def test_validator_layers(self):
        import validate as v
        with tempfile.TemporaryDirectory() as tmp:
            base, overlay = Path(tmp) / "base", Path(tmp) / "overlay"
            c.write(base / "data/b/loot_table/a.json", {"v": "base"})
            c.write(base / "data/b/worldgen/configured_feature/old.json", {"v": "old"})
            c.write(overlay / "data/b/loot_table/a.json", {"v": "overlay"})
            jar = jar263(Path(tmp) / "vanilla.jar")
            with zipfile.ZipFile(jar) as archive:
                validator = v.Validator(overlay, archive, "26.3", base)
                self.assertEqual(set(validator.files), {"data/b/loot_table/a.json"})
                self.assertEqual(validator.files["data/b/loot_table/a.json"].parent.parent.parent.parent, overlay)


if __name__ == "__main__":
    unittest.main()
