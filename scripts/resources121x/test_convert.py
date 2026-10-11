"""Independent 1.21.1 fixtures, including boundaries with later formats."""
import contextlib
import io
import json
from pathlib import Path
import tempfile
import unittest
import zipfile

import convert as c
import validate as v


class ConversionTests(unittest.TestCase):
    def test_recipe_ingredients_are_objects_and_source_is_unchanged(self):
        original = {"type": "minecraft:crafting_shaped", "pattern": ["   ", " # ", "   "],
                    "key": {"#": {"tag": "c:ingots/steel"}}, "result": {"item": "buildscape:steel_block"}}
        before = json.dumps(original)
        converted = c.recipe(original)
        self.assertEqual(converted["key"], original["key"])
        self.assertEqual(converted["pattern"], ["#"])
        self.assertEqual(converted["result"], {"id": "buildscape:steel_block", "count": 1})
        self.assertEqual(json.dumps(original), before)

    def test_stonecutting_smithing_and_custom_serializer(self):
        stone = c.recipe({"type": "minecraft:stonecutting", "ingredient": {"item": "minecraft:stone"},
                          "result": "minecraft:stone_slab", "count": 2})
        self.assertEqual(stone["result"], {"id": "minecraft:stone_slab", "count": 2})
        self.assertEqual(stone["ingredient"], {"item": "minecraft:stone"})
        self.assertNotIn("count", stone)
        smithing = c.recipe({"type": "minecraft:smithing", "base": {"item": "minecraft:diamond_sword"},
                             "addition": {"item": "minecraft:netherite_ingot"},
                             "result": {"item": "minecraft:netherite_sword"}})
        self.assertEqual(smithing["type"], "minecraft:smithing_transform")
        self.assertEqual(smithing["template"], {"item": "minecraft:netherite_upgrade_smithing_template"})
        custom = {"type": "buildscape:custom_firework_star"}
        self.assertEqual(c.recipe(custom), custom)

    def test_folder_renames(self):
        for namespace in ("minecraft", "buildscape", "other"):
            for old, new in c.FOLDERS.items():
                self.assertEqual(c.converted_path(Path("data", namespace, old, "x.json")),
                                 Path("data", namespace, new, "x.json"))
            for old, new in c.TAG_FOLDERS.items():
                self.assertEqual(c.converted_path(Path("data", namespace, "tags", old, "x.json")),
                                 Path("data", namespace, "tags", new, "x.json"))
        self.assertEqual(c.converted_path(Path("data/forge/tags/items/glass.json")),
                         Path("data/c/tags/item/glass_blocks.json"))

    def test_removal_boundary(self):
        removed = c.removals()
        for prefix in ("", "exposed_", "weathered_", "oxidized_", "waxed_",
                       "waxed_exposed_", "waxed_weathered_", "waxed_oxidized_"):
            for name in ("copper_door", "copper_trapdoor", "copper_grate", "copper_bulb", "chiseled_copper"):
                self.assertEqual(removed["buildscape:" + prefix + name], "minecraft:" + prefix + name)
        self.assertEqual(removed["buildscape:copper_rod"], "minecraft:lightning_rod")
        self.assertEqual(removed["buildscape:pink_petal"], "minecraft:pink_petals")
        for name in ("sculk_pillar", "sculk_slab", "sculk_stairs", "sculk_vertical_slab", "sculk_wall",
                     "snowy_leaves", "potted_bush", "potted_wildflowers", "pale_oak_planks",
                     "potted_open_eyeblossom", "copper_lantern", "copper_bars", "copper_chain",
                     "copper_chest", "copper_torch", "copper_nugget", "oak_shelf", "sulfur", "cinnabar"):
            self.assertNotIn("buildscape:" + name, removed)

    def test_reference_rewrites_and_asset_fallback(self):
        removed = {"buildscape:mangrove_log": "minecraft:mangrove_log"}
        self.assertEqual(c.references(["buildscape:mangrove_log", "BS:mangrove_log", "buildscape:mangrove_log_slab"],
                                     removed, aliases={"BS": "buildscape:"}),
                         ["minecraft:mangrove_log", "minecraft:mangrove_log", "buildscape:mangrove_log_slab"])
        path = "assets/minecraft/models/block/mangrove_log.json"
        self.assertEqual(c.references("buildscape:block/mangrove_log", removed, {path}), "minecraft:block/mangrove_log")
        self.assertEqual(c.references("buildscape:block/mangrove_log", removed), "buildscape:block/mangrove_log")
        self.assertEqual(c.references("forge:mod_loaded", removed), "forge:mod_loaded")
        self.assertFalse(c.owned_file(Path("assets/buildscape/models/block/mangrove_log_slab.json"), removed))

    def test_advancement_background_and_loot(self):
        background = "minecraft:textures/gui/advancements/backgrounds/stone.png"
        data = c.advancement({"display": {"icon": {"item": "minecraft:stone"}, "background": background}})
        self.assertEqual(data["display"]["icon"], {"id": "minecraft:stone"})
        self.assertEqual(data["display"]["background"], background)
        self.assertIsNone(c.unsupported_loader_data({"criteria": {"stone": {
            "trigger": "minecraft:inventory_changed", "conditions": {"items": [{"items": ["minecraft:stone"]}]}}}}))
        loot = c.loot({"condition": "minecraft:alternative", "terms": [
            {"condition": "minecraft:match_tool", "predicate": {"enchantments": [
                {"enchantment": "minecraft:silk_touch", "levels": {"min": 1}}]}}]})
        self.assertEqual(loot["condition"], "minecraft:any_of")
        self.assertEqual(loot["terms"][0]["predicate"]["predicates"]["minecraft:enchantments"][0]["enchantments"],
                         "minecraft:silk_touch")

    def test_compact_pack_removes_results_rewrites_ingredients(self):
        removed = {"buildscape:cherry_planks": "minecraft:cherry_planks"}
        data = {"aliases": {"BS": "buildscape:", "F": "forge:"}, "wood_families": ["BS:cherry_planks"],
                "crafting_shaped": [["BS:cherry_planks", ["#"], [["#", "minecraft:cherry_log"]], 4],
                                    ["BS:cherry_vertical_slab", ["#"], [["#", "BS:cherry_planks"]], 2]]}
        out = c.compact_pack(data, removed, set())
        self.assertEqual(len(out["crafting_shaped"]), 1)
        self.assertEqual(out["crafting_shaped"][0][2][0][1], "minecraft:cherry_planks")
        self.assertEqual(out["wood_families"], ["minecraft:cherry_planks"])
        self.assertEqual(out["aliases"]["F"], "c:")

    def test_compact_loader_entries_are_dropped_without_losing_custom_recipes(self):
        report = []
        data = {"aliases": {"F": "forge:"}, "recipes": [
            {"type": "buildscape:clear_shulker_filters"},
            {"type": "forge:conditional", "recipes": []}]}
        out = c.compact_pack(data, {}, set(), report)
        self.assertEqual(out["recipes"], [{"type": "buildscape:clear_shulker_filters"}])
        self.assertEqual(len(report), 1)

    def test_generation_is_repeatable_preserves_source_and_mixin_reports_drops(self):
        with tempfile.TemporaryDirectory() as tmp:
            base = Path(tmp)
            source, output, jar = base / "source", base / "output", base / "vanilla.jar"
            manifest = base / "removals.json"
            c.write(manifest, {"schema_version": 1, "target": "mc1.21.1", "removed_ids": [
                {"id": "buildscape:cherry_planks", "vanilla_equivalent": "minecraft:cherry_planks"}]})
            fixtures = {
                "assets/buildscape/models/item/cherry_planks.json": {"parent": "buildscape:block/cherry_planks"},
                "assets/buildscape/models/item/sculk_slab.json": {"parent": "buildscape:block/sculk_slab"},
                "data/buildscape/recipes/keep.json": {"type": "minecraft:stonecutting", "ingredient": {"item": "buildscape:cherry_planks"},
                                                     "result": "buildscape:sculk_slab", "count": 2},
                "data/forge/loot_modifiers/global_loot_modifiers.json": {"entries": []},
                "data/buildscape/recipes/conditional.json": {"type": "forge:conditional"},
                "data/buildscape/worldgen/placed_feature/cherry.json": {"feature": "buildscape:cherry", "placement": []}}
            for path, data in fixtures.items():
                c.write(source / path, data)
            c.write(output / "buildscape.mixins.json", {"mixins": ["Keep"]})
            c.write(output / "assets/buildscape/items/stale.json", {})
            before = {p.relative_to(source): p.read_bytes() for p in source.rglob("*") if p.is_file()}
            with zipfile.ZipFile(jar, "w") as archive:
                archive.writestr("version.json", '{"id":"1.21.1"}')
                archive.writestr("assets/minecraft/models/item/cherry_planks.json", '{}')
                archive.writestr("assets/minecraft/blockstates/oak_door.json", '{"variants":{}}')
            report = io.StringIO()
            with contextlib.redirect_stdout(report):
                self.assertGreater(c.convert(source, output, jar, manifest), 0)
                self.assertEqual(c.convert(source, output, jar, manifest), 0)
            self.assertIn("dropped", report.getvalue())
            self.assertEqual(c.load(output / "pack.mcmeta"), c.PACK)
            self.assertTrue((output / "buildscape.mixins.json").exists())
            self.assertFalse((output / "assets/buildscape/items").exists())
            self.assertFalse((output / "assets/buildscape/models/item/cherry_planks.json").exists())
            self.assertTrue((output / "assets/buildscape/models/item/sculk_slab.json").exists())
            self.assertEqual(c.load(output / "data/buildscape/recipe/keep.json")["ingredient"], {"item": "minecraft:cherry_planks"})
            self.assertEqual(before, {p.relative_to(source): p.read_bytes() for p in source.rglob("*") if p.is_file()})

    def test_validator_rejects_later_ingredients_and_unknown_vanilla_ids(self):
        with tempfile.TemporaryDirectory() as tmp:
            jar = Path(tmp) / "vanilla.jar"
            with zipfile.ZipFile(jar, "w") as archive:
                archive.writestr("version.json", '{"id":"1.21.1"}')
                archive.writestr("assets/minecraft/models/item/stone.json", '{}')
            with zipfile.ZipFile(jar) as archive:
                validator = v.Validator(Path(tmp) / "resources", archive)
                validator.ingredient("fixture", {"item": "minecraft:stone"})
                self.assertEqual(validator.errors, [])
                validator.ingredient("fixture", "minecraft:stone")
                validator.ingredient("fixture", {"item": "minecraft:copper_chest"})
                self.assertEqual(len(validator.errors), 2)


if __name__ == "__main__":
    unittest.main()
