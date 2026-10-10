"""Small fixtures exercise layout invariants without a Minecraft installation."""

import json
from pathlib import Path
import sys
import tempfile
import unittest

sys.path.insert(0, str(Path(__file__).resolve().parent))
import build_showcase as showcase


def block(name, states=None):
    return {"id": "buildscape:" + name, "className": "FakeBlock", "blockType": "Block",
            "properties": {}, "states": [{}] if states is None else states}


def dump(*blocks):
    return {"minecraftVersion": "26.2", "blocks": list(blocks), "unavailable": []}


def all_cells(layout):
    return [cell for layer in layout["layers"] for group in layer["groups"] for cell in group["cells"]]


class LayoutTests(unittest.TestCase):
    def test_one_air_within_group_and_two_between_groups(self):
        layout = showcase.build_layout(dump(block("oak_cube", [{"axis": str(i)} for i in range(4)]),
                                            block("stone_cube")))
        woods, stone = layout["layers"][0]["groups"]
        cells = woods["cells"]
        self.assertEqual([cell["position"][0] for cell in cells[:2]], [0, 2])
        self.assertEqual(cells[2]["position"][2] - cells[0]["position"][2], 2)
        self.assertEqual(stone["bounds"][0] - woods["bounds"][2] - 1, 2)
        self.assertEqual(woods["sign"][2], woods["bounds"][1] - 1)
        self.assertEqual(woods["sign"][1], -60)

    def test_default_limit_group_continuation_and_eight_air_gap(self):
        states = [{"variant": f"{i:05d}"} for i in range(10001)]
        layout = showcase.build_layout(dump(block("oak_cube", states)))
        self.assertEqual(len(all_cells(layout)), 10001)
        self.assertGreaterEqual(len(layout["layers"]), 2)
        self.assertTrue(layout["layers"][1]["groups"][0]["continued"])
        self.assertIsNone(layout["layers"][1]["groups"][0]["sign"])
        for layer in layout["layers"]:
            self.assertLessEqual(layer["width"], 200)
            self.assertLessEqual(layer["depth"], 200)
        first, second = layout["layers"][:2]
        self.assertEqual(second["floor_y"] - first["top_y"] - 1, 8)
        self.assertEqual(second["base_y"], second["floor_y"] + 1)
        self.assertEqual(first["base_y"], -60)

    def test_vertical_pairs_and_stair_halves(self):
        states = [{"half": half, "facing": "north", "open": opened}
                  for half in ("lower", "upper") for opened in ("false", "true")]
        layout = showcase.build_layout(dump(block("oak_door", states),
                                            block("oak_stair", [{"half": "top"}, {"half": "bottom"}])))
        doors = [cell for cell in all_cells(layout) if cell["id"].endswith("door")]
        self.assertEqual(len(doors), 2)
        for cell in doors:
            lower, upper = cell["parts"]
            self.assertEqual(cell["height"], 2)
            self.assertEqual(lower["state"]["half"], "lower")
            self.assertEqual(upper["state"]["half"], "upper")
            self.assertEqual(lower["state"]["open"], upper["state"]["open"])
            self.assertEqual(upper["position"][1], lower["position"][1] + 1)
        self.assertEqual(len(all_cells(layout)), 4)
        self.assertEqual(layout["layers"][0]["top_y"], -59)

    def test_beds_all_facings(self):
        states = [{"part": part, "facing": facing, "occupied": "false"}
                  for facing in showcase.FACING for part in ("head", "foot")]
        layout = showcase.build_layout(dump(block("straw_bed", states)))
        self.assertEqual(len(all_cells(layout)), 4)
        for cell in all_cells(layout):
            foot, head = cell["parts"]
            dx, dz = showcase.FACING[foot["state"]["facing"]]
            self.assertEqual(head["position"][0] - foot["position"][0], dx)
            self.assertEqual(head["position"][2] - foot["position"][2], dz)
            self.assertEqual(head["position"][1], foot["position"][1])
            self.assertEqual(cell["width"] * cell["depth"], 2)

    def test_fluid_sealed_cells_spacing_and_height(self):
        layout = showcase.build_layout(dump(block("experience_liquid", [{"level": str(i)} for i in range(16)])))
        cells = all_cells(layout)
        self.assertEqual(len(cells), 16)
        self.assertEqual(cells[1]["position"][0] - cells[0]["position"][0], 4)
        for cell in cells:
            self.assertEqual((cell["width"], cell["depth"], cell["height"]), (3, 3, 2))
            placements = list(showcase.cell_blocks(cell))
            self.assertEqual(sum(name == "minecraft:glass" for _, name in placements), 5)
            x, y, z = cell["position"]
            self.assertIn(([x + 1, y + 1, z + 1], "minecraft:glass"), placements)
            self.assertEqual(cell["parts"][0]["position"], [x + 1, y, z + 1])
        self.assertEqual(layout["layers"][0]["top_y"], -59)

    def test_upper_floor_and_tallest_placement_stacking(self):
        states = [{"half": half, "facing": facing}
                  for facing in showcase.FACING for half in ("lower", "upper")]
        layout = showcase.build_layout(dump(block("oak_door", states), block("stone_cube")), layer_size=4)
        first, second = layout["layers"][:2]
        self.assertEqual(second["floor_y"] - first["top_y"] - 1, 8)
        output = list(showcase.commands(layout))
        floor_fills = [line for line in output if line.startswith("fill ") and "grass_block" in line]
        self.assertEqual(len(floor_fills), len(layout["layers"]) - 1)
        self.assertTrue(any(f" {second['floor_y']} " in line for line in floor_fills))
        self.assertTrue(all(cell["position"][1] == layer["base_y"] for layer in layout["layers"]
                            for group in layer["groups"] for cell in group["cells"]))

    def test_nonfluid_level_property_does_not_make_glass_cell(self):
        layout = showcase.build_layout(dump(block("icicle_cauldron", [{"level": "1"}, {"level": "2"}])))
        for cell in all_cells(layout):
            self.assertEqual(cell["kind"], "block")
            self.assertEqual((cell["width"], cell["depth"], cell["height"]), (1, 1, 1))

    def test_longest_tokens_categories_and_copper_variants(self):
        blocks = [block(name) for name in ("oak_cube", "dark_oak_cube", "pale_oak_cube",
                  "ashpen_light_blue_cube", "red_sandstone_cube", "waxed_oxidized_copper_cube",
                  "exposed_copper_cube", "flaming_steel_cube", "light_blue_wool",
                  "oak_leaves", "cherry_hedge", "rose_flower", "mysterious_widget")]
        tokens = showcase.material_tokens(blocks)
        expected = [("woods", "oak"), ("woods", "dark_oak"), ("woods", "pale_oak"),
                    ("woods", "ashpen_light_blue"), ("stones", "red_sandstone"),
                    ("metals", "copper"), ("metals", "copper"), ("metals", "flaming_steel"),
                    ("colours", "light_blue"), ("nature", "leaves_and_hedges"),
                    ("nature", "leaves_and_hedges"), ("nature", "flowers_and_plants"), ("misc", "misc")]
        self.assertEqual([showcase.material_for(entry, tokens) for entry in blocks], expected)
        self.assertNotIn("birch", tokens)
        groups = showcase.build_layout(dump(*blocks))["groups"]
        ranks = [showcase.CATEGORIES.index(group["category"]) for group in groups]
        self.assertEqual(ranks, sorted(ranks))

    def test_logged_states_excluded_independently(self):
        states = [{"waterlogged": water, "lava_logged": lava}
                  for water in ("false", "true") for lava in ("false", "true")]
        fixture = dump(block("oak_pipe", states))
        self.assertEqual(len(all_cells(showcase.build_layout(fixture))), 1)
        self.assertEqual(len(all_cells(showcase.build_layout(fixture, include_waterlogged=True))), 2)
        self.assertEqual(len(all_cells(showcase.build_layout(fixture, include_lava_logged=True))), 2)
        self.assertEqual(len(all_cells(showcase.build_layout(fixture, include_waterlogged=True,
                                                            include_lava_logged=True))), 4)

    def test_chunking_setup_full_states_and_last_freeze(self):
        fixture = dump(block("oak_cube", [{"axis": str(i)} for i in range(450)]))
        layout = showcase.build_layout(fixture, origin_x=-12, origin_z=17)
        with tempfile.TemporaryDirectory() as directory:
            showcase.write_output(layout, directory)
            files = sorted(Path(directory).glob("commands-*.mcfunction"))
            self.assertGreater(len(files), 2)
            batches = [file.read_text(encoding="utf-8").splitlines() for file in files]
            self.assertTrue(all(0 < len(lines) <= 400 for lines in batches))
            self.assertTrue(all(len(lines) == 400 for lines in batches[:-1]))
            lines = [line for batch in batches for line in batch]
            self.assertTrue(lines[0].startswith("forceload add -12 17 "))
            self.assertIn("gamerule minecraft:fire_spread_radius_around_player 0", lines)
            self.assertIn("gamerule minecraft:random_tick_speed 0", lines)
            self.assertEqual(lines[-1], "tick freeze")
            self.assertTrue(all(not line.startswith("/") for line in lines))
            self.assertTrue(any("front_text:{messages:[\"oak\",\"\",\"\",\"\"]" in line for line in lines))
            first = next(line for line in lines if "buildscape:oak_cube[" in line)
            self.assertTrue(first.endswith(" replace"))
            self.assertTrue(any("buildscape:oak_cube[axis=0] strict" in line for line in lines))
            saved = json.loads((Path(directory) / "layout.json").read_text(encoding="utf-8"))
            self.assertEqual(saved["command_count"], len(lines))
            self.assertEqual(saved["command_files"], [file.name for file in files])
            self.assertIn("woods/oak", (Path(directory) / "summary.txt").read_text(encoding="utf-8"))
            with self.assertRaisesRegex(ValueError, "fresh output"):
                showcase.write_output(layout, directory)

    def test_rejects_incomplete_pairs_and_invalid_sizes(self):
        with self.assertRaisesRegex(ValueError, "upper partner"):
            showcase.build_layout(dump(block("oak_door", [{"half": "lower"}])))
        with self.assertRaisesRegex(ValueError, "between 4 and 200"):
            showcase.build_layout(dump(block("oak_cube")), layer_size=201)

    def test_fills_respect_vanilla_limit(self):
        for command in showcase.fills(0, -60, 0, 199, 100, 199, "minecraft:air"):
            values = command.split()
            a = list(map(int, values[1:4]))
            b = list(map(int, values[4:7]))
            self.assertLessEqual(math_product(b[i] - a[i] + 1 for i in range(3)), 32768)


def math_product(values):
    result = 1
    for value in values:
        result *= value
    return result


if __name__ == "__main__":
    unittest.main()
