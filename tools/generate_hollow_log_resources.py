import os
import json

external_dir = "I:\\MOD-Dev\\DGA DEV\\Hollowed Logs"

# Mappings of wood types (id, name, is_stem, is_custom, base_log, stripped_log)
WOODS = [
    ("oak", "Oak", False, False, "minecraft:oak_log", "minecraft:stripped_oak_log"),
    ("spruce", "Spruce", False, False, "minecraft:spruce_log", "minecraft:stripped_spruce_log"),
    ("birch", "Birch", False, False, "minecraft:birch_log", "minecraft:stripped_birch_log"),
    ("jungle", "Jungle", False, False, "minecraft:jungle_log", "minecraft:stripped_jungle_log"),
    ("acacia", "Acacia", False, False, "minecraft:acacia_log", "minecraft:stripped_acacia_log"),
    ("dark_oak", "Dark Oak", False, False, "minecraft:dark_oak_log", "minecraft:stripped_dark_oak_log"),
    ("crimson", "Crimson", True, False, "minecraft:crimson_stem", "minecraft:stripped_crimson_stem"),
    ("warped", "Warped", True, False, "minecraft:warped_stem", "minecraft:stripped_warped_stem"),
    ("mangrove", "Mangrove", False, True, "buildscape:mangrove_log", "buildscape:stripped_mangrove_log"),
    ("ashpen", "Ashpen", False, True, "buildscape:ashpen_log", "buildscape:stripped_ashpen_log"),
    ("poplar", "Poplar", False, True, "buildscape:poplar_log", "buildscape:stripped_poplar_log"),
    ("pale_oak", "Pale Oak", False, True, "buildscape:pale_oak_log", "buildscape:stripped_pale_oak_log"),
    ("cherry", "Cherry", False, True, "buildscape:cherry_log", "buildscape:stripped_cherry_log")
]

# Output Directories
BLOCKSTATES_DIR = "src/main/resources/assets/buildscape/blockstates"
BLOCK_MODELS_DIR = "src/main/resources/assets/buildscape/models/block"
ITEM_MODELS_DIR = "src/main/resources/assets/buildscape/models/item"
LOOT_TABLES_DIR = "src/main/resources/data/buildscape/loot_tables/blocks"
RECIPES_DIR = "src/main/resources/data/buildscape/recipes"

os.makedirs(BLOCKSTATES_DIR, exist_ok=True)
os.makedirs(BLOCK_MODELS_DIR, exist_ok=True)
os.makedirs(ITEM_MODELS_DIR, exist_ok=True)
os.makedirs(LOOT_TABLES_DIR, exist_ok=True)
os.makedirs(RECIPES_DIR, exist_ok=True)
os.makedirs("src/main/resources/assets/minecraft/models/block", exist_ok=True)

# 1. Copy template_hollow_log.json to minecraft assets as parent
template_src = os.path.join(external_dir, "template_hollow_log.json")
template_dest = "src/main/resources/assets/minecraft/models/block/hollow_log.json"
with open(template_src, "r", encoding="utf-8") as f:
    template_data = json.load(f)
with open(template_dest, "w", encoding="utf-8") as f:
    json.dump(template_data, f, indent=2)
print("Copied template_hollow_log.json to minecraft assets.")

# Helper to write pretty json
def write_json(path, data):
    with open(path, "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2)

for wood, name, is_stem, is_custom, base_log, stripped_log in WOODS:
    suffix = "stem" if is_stem else "log"
    unstripped_id = f"hollow_{wood}_{suffix}"
    stripped_id = f"stripped_hollow_{wood}_{suffix}"
    
    # 2. Blockstate files
    for block_id in [unstripped_id, stripped_id]:
        blockstate = {
            "variants": {
                "axis=x": {
                    "model": f"buildscape:block/{block_id}",
                    "x": 90,
                    "y": 90
                },
                "axis=y": {
                    "model": f"buildscape:block/{block_id}"
                },
                "axis=z": {
                    "model": f"buildscape:block/{block_id}",
                    "x": 90
                }
            }
        }
        write_json(os.path.join(BLOCKSTATES_DIR, f"{block_id}.json"), blockstate)

    # 3. Block Model files
    # A. Unstripped hollow log model
    unstripped_model_path = os.path.join(BLOCK_MODELS_DIR, f"{unstripped_id}.json")
    # Determine textures
    wood_ns = "buildscape" if is_custom else "minecraft"
    stem_log = "stem" if is_stem else "log"
    
    outside_tex = f"{wood_ns}:block/{wood}_{stem_log}"
    inside_tex = f"{wood_ns}:block/stripped_{wood}_{stem_log}"
    end_tex = f"{wood_ns}:block/{wood}_{stem_log}_top"
    
    unstripped_model = {
        "parent": "minecraft:block/hollow_log",
        "textures": {
            "outside": outside_tex,
            "inside": inside_tex,
            "end": end_tex
        }
    }
    write_json(unstripped_model_path, unstripped_model)

    # B. Stripped hollow log model
    stripped_model_path = os.path.join(BLOCK_MODELS_DIR, f"{stripped_id}.json")
    stripped_outside_tex = f"{wood_ns}:block/stripped_{wood}_{stem_log}"
    stripped_end_tex = f"{wood_ns}:block/stripped_{wood}_{stem_log}_top"
    
    stripped_model = {
        "parent": "minecraft:block/hollow_log",
        "textures": {
            "outside": stripped_outside_tex,
            "inside": stripped_outside_tex,
            "end": stripped_end_tex
        }
    }
    write_json(stripped_model_path, stripped_model)

    # 4. Item Model files
    for block_id in [unstripped_id, stripped_id]:
        item_model = {
            "parent": f"buildscape:block/{block_id}"
        }
        write_json(os.path.join(ITEM_MODELS_DIR, f"{block_id}.json"), item_model)

    # 5. Loot Tables
    for block_id in [unstripped_id, stripped_id]:
        loot_table = {
            "type": "minecraft:block",
            "pools": [
                {
                    "rolls": 1.0,
                    "bonus_rolls": 0.0,
                    "entries": [
                        {
                            "type": "minecraft:item",
                            "name": f"buildscape:{block_id}"
                        }
                    ],
                    "conditions": [
                        {
                            "condition": "minecraft:survives_explosion"
                        }
                    ]
                }
            ]
        }
        write_json(os.path.join(LOOT_TABLES_DIR, f"{block_id}.json"), loot_table)

    # 6. Recipes
    # A. Stonecutter recipe (unstripped)
    sc_unstripped = {
        "type": "minecraft:stonecutting",
        "ingredient": {
            "item": base_log
        },
        "result": f"buildscape:{unstripped_id}",
        "count": 1
    }
    write_json(os.path.join(RECIPES_DIR, f"{unstripped_id}_from_stonecutting.json"), sc_unstripped)

    # B. Stonecutter recipe (stripped)
    sc_stripped = {
        "type": "minecraft:stonecutting",
        "ingredient": {
            "item": stripped_log
        },
        "result": f"buildscape:{stripped_id}",
        "count": 1
    }
    write_json(os.path.join(RECIPES_DIR, f"{stripped_id}_from_stonecutting.json"), sc_stripped)

    # C. Crafting recipe: 8 logs in a ring -> 8 hollow logs
    craft_unstripped = {
        "type": "minecraft:crafting_shaped",
        "pattern": [
            "###",
            "# #",
            "###"
        ],
        "key": {
            "#": {
                "item": base_log
            }
        },
        "result": {
            "item": f"buildscape:{unstripped_id}",
            "count": 8
        }
    }
    write_json(os.path.join(RECIPES_DIR, f"{unstripped_id}_crafting.json"), craft_unstripped)

    # D. Crafting recipe: 8 stripped logs in a ring -> 8 stripped hollow logs
    craft_stripped = {
        "type": "minecraft:crafting_shaped",
        "pattern": [
            "###",
            "# #",
            "###"
        ],
        "key": {
            "#": {
                "item": stripped_log
            }
        },
        "result": {
            "item": f"buildscape:{stripped_id}",
            "count": 8
        }
    }
    write_json(os.path.join(RECIPES_DIR, f"{stripped_id}_crafting.json"), craft_stripped)

print("Hollow logs resource generation complete!")
