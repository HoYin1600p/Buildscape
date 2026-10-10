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
        self.assertEqual(len(c.SPECIAL_ITEMS), 9)
        for name in c.SPECIAL_ITEMS:
            self.assertTrue(c.item_definition(name, True)["model"]["model"].startswith("buildscape:block/"))
        self.assertEqual(c.item_definition("waxed_copper_chest", True)["model"]["model"],
                         "buildscape:block/copper_chest")

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
            self.assertEqual(c.load(root / "assets/buildscape/items/weathered_copper_chest.json"),
                             c.item_definition("weathered_copper_chest", True))
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


if __name__ == "__main__":
    unittest.main()
