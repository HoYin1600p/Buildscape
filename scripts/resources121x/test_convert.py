"""Independent 1.21.1 fixtures, including boundaries with later formats."""
import contextlib
import copy
import io
import json
from pathlib import Path
import tempfile
import unittest
import zipfile

import convert as c
import validate as v


class ConversionTests(unittest.TestCase):
    def test_model_migration_preserves_face_data_and_nonzero_rotations(self):
        original = {"elements": [
            {"rotation": {"x": 0, "y": 0, "z": 0, "origin": [14, 13, 14]},
             "faces": {"north": {"texture": "#light", "forge_data": {
                 "block_light": 15, "sky_light": 15}}}},
            {"rotation": {"angle": 0, "origin": [0, 0, 0]}, "faces": {}},
            {"rotation": {"angle": 22.5, "axis": "y", "origin": [8, 8, 8]}, "faces": {}},
            {"rotation": {"angle": 22.5, "origin": [8, 8, 8]}, "faces": {}},
            {"rotation": {"x": 15, "origin": [8, 8, 8]}, "faces": {}}]}
        before = copy.deepcopy(original)
        result = c.model(original)
        self.assertEqual(original, before)
        for element in result["elements"][:2]:
            self.assertNotIn("rotation", element)
        for index in (2, 3, 4):
            self.assertEqual(result["elements"][index], original["elements"][index])
        face = result["elements"][0]["faces"]["north"]
        self.assertEqual(face, {"texture": "#light", "neoforge_data": {"block_light": 15, "sky_light": 15}})
        self.assertEqual(c.model(result), result)
        with self.assertRaisesRegex(ValueError, "Conflicting"):
            c.model({"elements": [{"faces": {"up": {"forge_data": {}, "neoforge_data": {"block_light": 1}}}}]})

    def test_validator_model_and_blockstate_deserializer_fields(self):
        good = {"elements": [{"from": [0, 0, 0], "to": [16, 16, 16],
                "rotation": {"axis": "y", "angle": 22.5, "origin": [8, 8, 8]},
                "faces": {"north": {"texture": "#side", "uv": [0, 0, 16, 16], "rotation": 270,
                                      "neoforge_data": {"block_light": 15}}}}]}
        cases = [
            ("axis", lambda d: d["elements"][0]["rotation"].pop("axis")),
            ("angle", lambda d: d["elements"][0]["rotation"].update(angle=30)),
            ("origin", lambda d: d["elements"][0]["rotation"].update(origin=[0, 0])),
            ("rescale", lambda d: d["elements"][0]["rotation"].update(rescale="yes")),
            ("shade", lambda d: d["elements"][0].update(shade=1)),
            ("outside", lambda d: d["elements"][0].update(to=[33, 16, 16])),
            ("face rotation", lambda d: d["elements"][0]["faces"]["north"].update(rotation=45)),
            ("face uv", lambda d: d["elements"][0]["faces"]["north"].update(uv=[0, 1])),
            ("forge_data", lambda d: d["elements"][0]["faces"]["north"].update(forge_data={})),
            ("textures", lambda d: d.update(textures={"side": {"sprite": "minecraft:block/stone"}})),
            ("predicate", lambda d: d.update(overrides=[{"model": "minecraft:block/stone", "predicate": {"a": "bad"}}]))]
        with tempfile.TemporaryDirectory() as tmp:
            jar = Path(tmp) / "vanilla.jar"
            with zipfile.ZipFile(jar, "w"):
                pass
            with zipfile.ZipFile(jar) as archive:
                validator = v.Validator(Path(tmp) / "resources", archive)
                self.assertTrue(validator.model_fields("model", good))
                self.assertEqual(validator.errors, [])
                for expected, mutate in cases:
                    with self.subTest(field=expected):
                        validator.errors.clear()
                        bad = copy.deepcopy(good)
                        mutate(bad)
                        self.assertFalse(validator.model_fields("model", bad))
                        self.assertTrue(any(expected in error for error in validator.errors))
                validator.errors.clear()
                validator.blockstate_fields("state", {"variants": {"": [
                    {"model": "minecraft:block/stone", "x": 90, "y": -90, "weight": 2}]},
                    "multipart": [{"apply": {"model": "minecraft:block/stone", "y": 360}}]})
                self.assertEqual(validator.errors, [])
                for data in ({"variants": {"": []}}, {"variants": {"": {"model": "m", "x": 45}}},
                             {"multipart": [{"apply": {"model": "m", "weight": 0}}]},
                             {"variants": {"": {"model": "m", "uvlock": "true"}}},
                             {"multipart": [{}]}, {"variants": []}):
                    with self.subTest(data=data):
                        validator.errors.clear()
                        validator.blockstate_fields("state", data)
                        self.assertTrue(validator.errors)

    def test_subtitle_lookup_and_offline_validation(self):
        with tempfile.TemporaryDirectory() as tmp:
            jar = Path(tmp) / "vanilla.jar"
            with zipfile.ZipFile(jar, "w") as archive:
                archive.writestr("assets/minecraft/lang/en_us.json", '{"subtitles.block.generic.hit":"Hit"}')
            with zipfile.ZipFile(jar) as archive:
                validator = v.Validator(Path(tmp) / "resources", archive)
                validator.json = {
                    "assets/buildscape/lang/fr_fr.json": {"subtitles.buildscape.light": "Lumiere"},
                    "assets/buildscape/sounds.json": {
                        "vanilla": {"subtitle": "subtitles.block.generic.hit", "sounds": ["block/stone/hit1"]},
                        "custom": {"subtitle": "subtitles.buildscape.light", "sounds": []},
                        "silent": {"sounds": []},
                        **{event: {"subtitle": "subtitles.block.generic.fall", "sounds": []} for event in (
                            "block_packed_mud_fall", "block_muddy_mangrove_roots_fall",
                            "block_mud_bricks_fall", "block_mangrove_roots_fall")}}}
                validator.sounds()
                self.assertEqual(len(validator.errors), 4)
                generated = {Path(path): copy.deepcopy(data) for path, data in validator.json.items()}
                report = []
                c.prune_subtitles(generated, c.translation_keys(
                    validator.get_vanilla("assets/minecraft/lang/en_us.json"), generated), report)
                self.assertEqual(len(report), 4)
                for error in validator.errors:
                    event = error.split(": ")[1]
                    self.assertTrue(any(event in line and "subtitles.block.generic.fall" in line for line in report))
                sounds = generated[Path("assets/buildscape/sounds.json")]
                self.assertEqual(sounds["vanilla"], validator.json["assets/buildscape/sounds.json"]["vanilla"])
                self.assertEqual(sounds["custom"], validator.json["assets/buildscape/sounds.json"]["custom"])
                validator.json = {path.as_posix(): data for path, data in generated.items()}
                validator.errors.clear()
                validator.sounds()
                self.assertEqual(validator.errors, [])

    def test_haze_set_nbt_splits_components_and_custom_data_with_conditions(self):
        path = c.REPO / "ref/forge/1.18.2/src/main/resources/data/buildscape/loot_tables/blocks/red_haze_bush.json"
        original = c.load(path)
        before = json.dumps(original)
        functions = c.loot(original)["pools"][0]["entries"][0]["functions"]
        self.assertEqual(len(functions), 2)
        self.assertEqual(functions[0]["function"], "minecraft:set_components")
        self.assertEqual(functions[0]["components"], {"minecraft:block_state": {"has_haze": "false"}})
        self.assertEqual(functions[1]["function"], "minecraft:set_custom_data")
        self.assertEqual(c.snbt_compound(functions[1]["tag"]), {"HasHaze": "0b"})
        conditions = original["pools"][0]["entries"][0]["functions"][0]["conditions"]
        for function in functions:
            self.assertEqual(function["conditions"], conditions)
            self.assertNotIn("BlockStateTag", json.dumps(function))
        functions[0]["conditions"][0]["properties"]["has_haze"] = "true"
        self.assertEqual(functions[1]["conditions"], conditions)
        self.assertEqual(json.dumps(original), before)

    def test_set_nbt_preserves_nested_snbt_types_and_quoted_delimiters(self):
        tag = "{Other:{Text:'a,b:c',List:[{x:1b},{x:2s}]},BlockStateTag:{'has_haze':'false',age:'2'},Bytes:[B;1b,0b]}"
        functions = c.loot({"functions": [{"function": "minecraft:set_nbt", "tag": tag}]})["functions"]
        self.assertEqual(functions[0]["components"]["minecraft:block_state"], {"has_haze": "false", "age": "2"})
        self.assertEqual(c.snbt_compound(functions[1]["tag"]), {
            "Other": "{Text:'a,b:c',List:[{x:1b},{x:2s}]}", "Bytes": "[B;1b,0b]"})
        self.assertNotIn("conditions", functions[0])
        for tag, expected in (("{BlockStateTag:{has_haze:false}}", "minecraft:set_components"),
                              ("{HasHaze:0b}", "minecraft:set_custom_data"),
                              ("{}", "minecraft:set_custom_data")):
            functions = c.loot({"functions": [{"function": "set_nbt", "tag": tag}]})["functions"]
            self.assertEqual(len(functions), 1)
            self.assertEqual(functions[0]["function"], expected)

    def test_set_nbt_single_function_slot_and_invalid_snbt(self):
        converted = c.loot({"function": "minecraft:filtered", "item_filter": {}, "modifier": {
            "function": "minecraft:set_nbt", "tag": '{BlockStateTag:{has_haze:"false"},HasHaze:0b}'}})
        self.assertEqual(converted["modifier"]["function"], "minecraft:sequence")
        self.assertEqual(len(converted["modifier"]["functions"]), 2)
        self.assertEqual(c.unsupported_loot_functions(converted), [])
        for tag in ("{BlockStateTag:[]}", "{BlockStateTag:{has_haze:{bad:1}}}", "{Other:[1,2}",
                    "{Other:'unfinished}", "{HasHaze:0b,HasHaze:1b}"):
            with self.subTest(tag=tag), self.assertRaises(ValueError):
                c.loot({"functions": [{"function": "minecraft:set_nbt", "tag": tag}]})

    def test_every_source_loot_table_converts_to_known_121x_functions(self):
        root = c.REPO / "ref/forge/1.18.2/src/main/resources/data"
        tables = list(root.rglob("loot_tables/**/*.json"))
        self.assertTrue(tables)
        for path in tables:
            if "loot_modifiers" in path.parts:
                continue
            with self.subTest(path=path.relative_to(root)):
                self.assertEqual(c.unsupported_loot_functions(c.loot(c.load(path))), [])
        for function in ("minecraft:set_nbt", "minecraft:looting_enchant", "minecraft:future_function", "other:unknown"):
            self.assertEqual(c.unsupported_loot_functions({"functions": [{"function": function}]}), [function])
        self.assertEqual(c.unsupported_loot_functions({"functions": [{"function": "set_count"},
            {"function": "minecraft:copy_custom_data"}, {"function": "minecraft:enchanted_count_increase"}]}), [])

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

    def test_reference_check_and_pruning_without_client_jar(self):
        resources = {
            "data/buildscape/advancement/child.json": {"parent": "buildscape:orphan"},
            "data/buildscape/advancement/orphan.json": {"rewards": {"recipes": ["buildscape:copper_chest"]}},
            "data/buildscape/advancement/keep.json": {"rewards": {"recipes": ["buildscape:keep"]}},
            "data/buildscape/recipe/keep.json": {},
            "data/buildscape/recipes_pack/crafting.json": {"crafting_shaped": [["buildscape:copper_chest"]]},
            "data/buildscape/worldgen/placed_feature/mangrove_checked.json": {"feature": "buildscape:mangrove"},
            "data/buildscape/worldgen/placed_feature/tall_mangrove_checked.json": {"feature": "buildscape:tall_mangrove"},
            "data/buildscape/worldgen/configured_feature/selector.json": {
                "config": {"features": [{"feature": "buildscape:mangrove_checked"}],
                           "default": "buildscape:tall_mangrove_checked"}},
            "data/buildscape/worldgen/placed_feature/selector.json": {"feature": "buildscape:selector"},
            "data/buildscape/worldgen/configured_feature/keep.json": {},
            "data/buildscape/worldgen/placed_feature/keep.json": {"feature": "buildscape:keep"},
            "data/buildscape/tags/worldgen/placed_feature/trees.json": {
                "values": ["buildscape:selector", {"id": "buildscape:mangrove_checked", "required": False}]},
            "data/buildscape/tags/worldgen/placed_feature/mixed.json": {
                "values": ["#buildscape:trees", "buildscape:keep", "minecraft:oak_checked"]},
            "data/buildscape/biome_modifier/trees.json": {"features": "#buildscape:trees"}}
        generated = {Path(path): data for path, data in resources.items()}
        errors = [(path.as_posix(), kind, value) for path, data in generated.items()
                  for kind, value in c.missing_data_references(path, data, generated)]
        self.assertIn(("data/buildscape/advancement/orphan.json", "recipe", "buildscape:copper_chest"), errors)
        self.assertIn(("data/buildscape/worldgen/placed_feature/mangrove_checked.json",
                       "worldgen/configured_feature", "buildscape:mangrove"), errors)
        report = []
        c.prune_dependencies(generated, report)
        self.assertEqual({path.as_posix() for path in generated}, {
            "data/buildscape/advancement/keep.json", "data/buildscape/recipe/keep.json",
            "data/buildscape/recipes_pack/crafting.json",
            "data/buildscape/worldgen/configured_feature/keep.json",
            "data/buildscape/worldgen/placed_feature/keep.json",
            "data/buildscape/tags/worldgen/placed_feature/mixed.json"})
        self.assertEqual(generated[Path("data/buildscape/tags/worldgen/placed_feature/mixed.json")]["values"],
                         ["buildscape:keep", "minecraft:oak_checked"])
        self.assertFalse([ref for path, data in generated.items()
                          for ref in c.missing_data_references(path, data, generated)])
        self.assertTrue(any("copper_chest" in line and "dropped" in line for line in report))
        self.assertTrue(any("mangrove" in line and "dropped" in line for line in report))
        count = len(report)
        c.prune_dependencies(generated, report)
        self.assertEqual(len(report), count)

    def test_generation_is_repeatable_preserves_source_and_mixin_reports_drops(self):
        with tempfile.TemporaryDirectory() as tmp:
            base = Path(tmp)
            source, output, jar = base / "source", base / "output", base / "vanilla.jar"
            manifest = base / "removals.json"
            c.write(manifest, {"schema_version": 1, "target": "mc1.21.1", "removed_ids": [
                {"id": "buildscape:cherry_planks", "vanilla_equivalent": "minecraft:cherry_planks"}]})
            fixtures = {
                "assets/buildscape/models/block/quartz_pillar_slab.json": {
                    "textures": {"side": "minecraft:block/quartz_pillar", "end": "block/quartz_pillar_top"}},
                "assets/buildscape/models/item/cherry_planks.json": {"parent": "buildscape:block/cherry_planks"},
                "assets/buildscape/models/item/sculk_slab.json": {"parent": "buildscape:block/sculk_slab"},
                "assets/buildscape/models/block/light.json": {"elements": [{
                    "from": [0, 0, 0], "to": [16, 16, 16], "rotation": {"x": 0, "y": 0, "z": 0},
                    "faces": {"up": {"texture": "#light", "forge_data": {"block_light": 15}}}}]},
                "assets/buildscape/lang/en_us.json": {"subtitles.buildscape.light": "Light"},
                "assets/buildscape/sounds.json": {
                    "missing": {"subtitle": "subtitles.block.generic.fall", "sounds": []},
                    "custom": {"subtitle": "subtitles.buildscape.light", "sounds": []},
                    "vanilla": {"subtitle": "subtitles.block.generic.hit", "sounds": []}},
                "data/buildscape/recipes/keep.json": {"type": "minecraft:stonecutting", "ingredient": {"item": "buildscape:cherry_planks"},
                                                     "result": "buildscape:sculk_slab", "count": 2},
                "data/forge/loot_modifiers/global_loot_modifiers.json": {"entries": []},
                "data/buildscape/recipes/conditional.json": {"type": "forge:conditional"},
                "data/buildscape/loot_tables/blocks/haze.json": {"pools": [{"entries": [{"functions": [
                    {"function": "minecraft:set_nbt", "tag": '{BlockStateTag:{has_haze:"false"},HasHaze:0b}'}]}]}]},
                "data/buildscape/advancements/recipes/copper_chest.json": {
                    "rewards": {"recipes": ["buildscape:copper_chest"]}},
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
                archive.writestr("assets/minecraft/lang/en_us.json", '{"subtitles.block.generic.hit":"Hit"}')
            report = io.StringIO()
            with contextlib.redirect_stdout(report):
                self.assertGreater(c.convert(source, output, jar, manifest), 0)
                self.assertEqual(c.convert(source, output, jar, manifest), 0)
            self.assertIn("dropped", report.getvalue())
            self.assertIn("missing: dropped unknown subtitle subtitles.block.generic.fall", report.getvalue())
            light = c.load(output / "assets/buildscape/models/block/light.json")
            self.assertEqual(light, c.model(fixtures["assets/buildscape/models/block/light.json"]))
            sounds = c.load(output / "assets/buildscape/sounds.json")
            self.assertNotIn("subtitle", sounds["missing"])
            self.assertEqual(sounds["missing"]["sounds"], [])
            self.assertIn("subtitle", sounds["custom"])
            self.assertIn("subtitle", sounds["vanilla"])
            self.assertEqual(c.load(output / "pack.mcmeta"), c.PACK)
            self.assertTrue((output / "buildscape.mixins.json").exists())
            self.assertFalse((output / "assets/buildscape/items").exists())
            self.assertFalse((output / "assets/buildscape/models/item/cherry_planks.json").exists())
            self.assertTrue((output / "assets/buildscape/models/item/sculk_slab.json").exists())
            self.assertEqual(c.load(output / "assets/buildscape/models/block/quartz_pillar_slab.json"),
                             fixtures["assets/buildscape/models/block/quartz_pillar_slab.json"])
            self.assertFalse((output / "data/buildscape/advancement/recipes/copper_chest.json").exists())
            self.assertFalse((output / "data/buildscape/worldgen/placed_feature/cherry.json").exists())
            self.assertEqual(c.load(output / "data/buildscape/recipe/keep.json")["ingredient"], {"item": "minecraft:cherry_planks"})
            converted = c.load(output / "data/buildscape/loot_table/blocks/haze.json")
            self.assertEqual(len(converted["pools"][0]["entries"][0]["functions"]), 2)
            self.assertEqual(c.unsupported_loot_functions(converted), [])
            c.write(source / "data/buildscape/loot_tables/unknown.json", {"functions": [
                {"function": "minecraft:future_function"}]})
            with self.assertRaisesRegex(ValueError, "unknown.json.*unsupported 1.21.1 loot function minecraft:future_function"):
                c.convert(source, output, jar, manifest)
            (source / "data/buildscape/loot_tables/unknown.json").unlink()
            self.assertEqual(before, {p.relative_to(source): p.read_bytes() for p in source.rglob("*") if p.is_file()})

    def test_validator_rejects_later_ingredients_and_unknown_vanilla_ids(self):
        with tempfile.TemporaryDirectory() as tmp:
            jar = Path(tmp) / "vanilla.jar"
            with zipfile.ZipFile(jar, "w") as archive:
                archive.writestr("version.json", '{"id":"1.21.1"}')
                archive.writestr("assets/minecraft/models/item/stone.json", '{}')
            with zipfile.ZipFile(jar) as archive:
                validator = v.Validator(Path(tmp) / "resources", archive)
                validator.loot("fixture", {"functions": [{"function": "minecraft:set_nbt"}]})
                self.assertTrue(any("unsupported 1.21.1 loot function minecraft:set_nbt" in error
                                    for error in validator.errors))
                validator.errors.clear()
                validator.ingredient("fixture", {"item": "minecraft:stone"})
                self.assertEqual(validator.errors, [])
                validator.ingredient("fixture", "minecraft:stone")
                validator.ingredient("fixture", {"item": "minecraft:copper_chest"})
                self.assertEqual(len(validator.errors), 2)


if __name__ == "__main__":
    unittest.main()
