import os
import json

# Paths
ASSETS_DIR = "src/main/resources/assets/buildscape"
DATA_DIR = "src/main/resources/data/buildscape"

BLOCKSTATES_DIR = os.path.join(ASSETS_DIR, "blockstates")
BLOCK_MODELS_DIR = os.path.join(ASSETS_DIR, "models/block")
ITEM_MODELS_DIR = os.path.join(ASSETS_DIR, "models/item")
LOOT_TABLES_DIR = os.path.join(DATA_DIR, "loot_tables/blocks")
RECIPES_DIR = os.path.join(DATA_DIR, "recipes")

# Main categories of tags
MINECRAFT_TAGS_DIR = "src/main/resources/data/minecraft/tags/blocks"
MINEABLE_PICKAXE_PATH = os.path.join(MINECRAFT_TAGS_DIR, "mineable/pickaxe.json")
SLABS_TAG_PATH = os.path.join(MINECRAFT_TAGS_DIR, "slabs.json")
STAIRS_TAG_PATH = os.path.join(MINECRAFT_TAGS_DIR, "stairs.json")
WALLS_TAG_PATH = os.path.join(MINECRAFT_TAGS_DIR, "walls.json")

# Custom vertical slab tags
VERTICAL_SLABS_BLOCK_TAG = os.path.join(DATA_DIR, "tags/blocks/vertical_slabs.json")
VERTICAL_SLABS_ITEM_TAG = os.path.join(DATA_DIR, "tags/items/vertical_slabs.json")

os.makedirs(BLOCKSTATES_DIR, exist_ok=True)
os.makedirs(BLOCK_MODELS_DIR, exist_ok=True)
os.makedirs(ITEM_MODELS_DIR, exist_ok=True)
os.makedirs(LOOT_TABLES_DIR, exist_ok=True)
os.makedirs(RECIPES_DIR, exist_ok=True)

# List of colors in standard Minecraft color order (excluding white and orange)
COLORS = [
    "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan",
    "purple", "blue", "brown", "green", "red", "black"
]

# The 8 main block types
MAIN_VARIANTS = [
    "steel_block", "cut_steel", "polished_steel", "pressed_steel",
    "steel_casing", "steel_grate", "steel_pillar", "bolted_steel_pillar"
]

# The 4 sub-variant suffixes for the first 4 main block types
SUB_VARIANTS = ["stairs", "slab", "vertical_slab", "wall"]

# Helper to write json
def write_json(path, data):
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2)

def append_to_tag(tag_path, items):
    if not os.path.exists(tag_path):
        data = {"replace": False, "values": []}
    else:
        with open(tag_path, 'r', encoding='utf-8') as f:
            try:
                data = json.load(f)
            except Exception:
                data = {"replace": False, "values": []}
    
    values = set(data.get("values", []))
    for item in items:
        values.add(item)
    data["values"] = sorted(list(values))
    write_json(tag_path, data)

# 1. Non-block item models
write_json(os.path.join(ITEM_MODELS_DIR, "flaming_steel_ingot.json"), {
    "parent": "minecraft:item/generated",
    "textures": {
        "layer0": "buildscape:item/flaming_steel_ingot"
    }
})

write_json(os.path.join(ITEM_MODELS_DIR, "flaming_steel_nugget.json"), {
    "parent": "minecraft:item/generated",
    "textures": {
        "layer0": "buildscape:item/flaming_steel_nugget"
    }
})

# Lists to collect for tags
all_pickaxe_blocks = []
all_slabs = []
all_stairs = []
all_walls = []
all_vertical_slabs = []

# Generate blocks and variants
all_colors_and_base = [None] + COLORS

for color in all_colors_and_base:
    prefix = f"{color}_" if color else ""
    
    # 2. Main Variants
    for main_type in MAIN_VARIANTS:
        block_name = f"{prefix}flaming_{main_type}"
        texture_path = f"buildscape:block/{block_name}"
        
        all_pickaxe_blocks.append(f"buildscape:{block_name}")
        
        # Blockstate
        if main_type in ["steel_pillar", "bolted_steel_pillar"]:
            blockstate = {
                "variants": {
                    "axis=y": { "model": f"buildscape:block/{block_name}" },
                    "axis=z": { "model": f"buildscape:block/{block_name}", "x": 90 },
                    "axis=x": { "model": f"buildscape:block/{block_name}", "x": 90, "y": 90 }
                }
            }
        else:
            blockstate = {
                "variants": {
                    "": { "model": f"buildscape:block/{block_name}" }
                }
            }
        write_json(os.path.join(BLOCKSTATES_DIR, f"{block_name}.json"), blockstate)
        
        # Block Model
        if main_type in ["steel_pillar", "bolted_steel_pillar"]:
            block_model = {
                "parent": "block/cube_column",
                "textures": {
                    "end": texture_path,
                    "side": texture_path,
                    "particle": "#side"
                }
            }
        else:
            block_model = {
                "parent": "minecraft:block/cube_all",
                "textures": {
                    "all": texture_path
                }
            }
            if main_type == "steel_grate":
                block_model["render_type"] = "translucent"
                
        write_json(os.path.join(BLOCK_MODELS_DIR, f"{block_name}.json"), block_model)
        
        # Item Model
        write_json(os.path.join(ITEM_MODELS_DIR, f"{block_name}.json"), {
            "parent": f"buildscape:block/{block_name}"
        })
        
        # Loot Table
        loot_table = {
            "type": "minecraft:block",
            "pools": [
                {
                    "rolls": 1.0,
                    "bonus_rolls": 0.0,
                    "entries": [
                        {
                            "type": "minecraft:item",
                            "name": f"buildscape:{block_name}"
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
        write_json(os.path.join(LOOT_TABLES_DIR, f"{block_name}.json"), loot_table)
        
        # Stonecutter recipes from base block to other main variants
        # e.g., red_flaming_steel_block -> red_flaming_cut_steel, etc.
        base_block_of_set = f"{prefix}flaming_steel_block"
        if block_name != base_block_of_set:
            stonecut_recipe = {
                "type": "minecraft:stonecutting",
                "ingredient": {
                    "item": f"buildscape:{base_block_of_set}"
                },
                "result": f"buildscape:{block_name}",
                "count": 1
            }
            write_json(os.path.join(RECIPES_DIR, f"{block_name}_from_{base_block_of_set}_stonecutting.json"), stonecut_recipe)
            
        # Dye recipes: 8 of base variant block + 1 dye -> 8 dyed variants
        # e.g., 8 flaming_cut_steel + 1 red_dye -> 8 red_flaming_cut_steel
        if color:
            dye_recipe = {
                "type": "minecraft:crafting_shaped",
                "category": "building",
                "key": {
                    "#": {
                        "item": f"buildscape:flaming_{main_type}"
                    },
                    "D": {
                        "item": f"minecraft:{color}_dye"
                    }
                },
                "pattern": [
                    "###",
                    "#D#",
                    "###"
                ],
                "result": {
                    "count": 8,
                    "item": f"buildscape:{block_name}"
                }
            }
            write_json(os.path.join(RECIPES_DIR, f"{block_name}_from_dyeing.json"), dye_recipe)

        # 3. Sub-variants
        if main_type in ["steel_block", "cut_steel", "polished_steel", "pressed_steel"]:
            for sub in SUB_VARIANTS:
                sub_name = f"{block_name}_{sub}"
                all_pickaxe_blocks.append(f"buildscape:{sub_name}")
                
                # Slabs
                if sub == "slab":
                    all_slabs.append(f"buildscape:{sub_name}")
                    # Blockstate
                    write_json(os.path.join(BLOCKSTATES_DIR, f"{sub_name}.json"), {
                        "variants": {
                            "type=bottom": { "model": f"buildscape:block/{sub_name}" },
                            "type=top": { "model": f"buildscape:block/{sub_name}_top" },
                            "type=double": { "model": f"buildscape:block/{block_name}" }
                        }
                    })
                    # Block Models
                    write_json(os.path.join(BLOCK_MODELS_DIR, f"{sub_name}.json"), {
                        "parent": "minecraft:block/slab",
                        "textures": {
                            "bottom": texture_path,
                            "top": texture_path,
                            "side": texture_path,
                            "particle": "#side"
                        }
                    })
                    write_json(os.path.join(BLOCK_MODELS_DIR, f"{sub_name}_top.json"), {
                        "parent": "minecraft:block/slab_top",
                        "textures": {
                            "bottom": texture_path,
                            "top": texture_path,
                            "side": texture_path,
                            "particle": "#side"
                        }
                    })
                    # Item Model
                    write_json(os.path.join(ITEM_MODELS_DIR, f"{sub_name}.json"), {
                        "parent": f"buildscape:block/{sub_name}"
                    })
                    # Loot Table (double slab drop function)
                    write_json(os.path.join(LOOT_TABLES_DIR, f"{sub_name}.json"), {
                        "type": "minecraft:block",
                        "pools": [
                            {
                                "rolls": 1.0,
                                "bonus_rolls": 0.0,
                                "entries": [
                                    {
                                        "type": "minecraft:item",
                                        "functions": [
                                            {
                                                "function": "minecraft:set_count",
                                                "conditions": [
                                                    {
                                                        "condition": "minecraft:block_state_property",
                                                        "block": f"buildscape:{sub_name}",
                                                        "properties": { "type": "double" }
                                                    }
                                                ],
                                                "count": 2.0,
                                                "add": False
                                            },
                                            { "function": "minecraft:explosion_decay" }
                                        ],
                                        "name": f"buildscape:{sub_name}"
                                    }
                                ],
                                "conditions": [
                                    { "condition": "minecraft:survives_explosion" }
                                ]
                            }
                        ]
                    })
                    # Crafting Recipe (3 blocks -> 6 slabs)
                    write_json(os.path.join(RECIPES_DIR, f"{sub_name}.json"), {
                        "type": "minecraft:crafting_shaped",
                        "category": "building",
                        "key": {
                            "#": { "item": f"buildscape:{block_name}" }
                        },
                        "pattern": [
                            "###"
                        ],
                        "result": {
                            "count": 6,
                            "item": f"buildscape:{sub_name}"
                        }
                    })
                    # Stonecutting Recipe
                    write_json(os.path.join(RECIPES_DIR, f"{sub_name}_from_{block_name}_stonecutting.json"), {
                        "type": "minecraft:stonecutting",
                        "ingredient": { "item": f"buildscape:{block_name}" },
                        "result": f"buildscape:{sub_name}",
                        "count": 2
                    })
                    
                # Stairs
                elif sub == "stairs":
                    all_stairs.append(f"buildscape:{sub_name}")
                    # Blockstate
                    write_json(os.path.join(BLOCKSTATES_DIR, f"{sub_name}.json"), {
                        "variants": {
                            "facing=east,half=bottom,shape=straight": { "model": f"buildscape:block/{sub_name}" },
                            "facing=west,half=bottom,shape=straight": { "model": f"buildscape:block/{sub_name}", "y": 180 },
                            "facing=south,half=bottom,shape=straight": { "model": f"buildscape:block/{sub_name}", "y": 90 },
                            "facing=north,half=bottom,shape=straight": { "model": f"buildscape:block/{sub_name}", "y": 270 },
                            "facing=east,half=bottom,shape=inner_left": { "model": f"buildscape:block/{sub_name}_inner" },
                            "facing=west,half=bottom,shape=inner_left": { "model": f"buildscape:block/{sub_name}_inner", "y": 180 },
                            "facing=south,half=bottom,shape=inner_left": { "model": f"buildscape:block/{sub_name}_inner", "y": 90 },
                            "facing=north,half=bottom,shape=inner_left": { "model": f"buildscape:block/{sub_name}_inner", "y": 270 },
                            "facing=east,half=bottom,shape=inner_right": { "model": f"buildscape:block/{sub_name}_inner", "y": 90 },
                            "facing=west,half=bottom,shape=inner_right": { "model": f"buildscape:block/{sub_name}_inner", "y": 270 },
                            "facing=south,half=bottom,shape=inner_right": { "model": f"buildscape:block/{sub_name}_inner", "y": 180 },
                            "facing=north,half=bottom,shape=inner_right": { "model": f"buildscape:block/{sub_name}_inner" },
                            "facing=east,half=bottom,shape=outer_left": { "model": f"buildscape:block/{sub_name}_outer" },
                            "facing=west,half=bottom,shape=outer_left": { "model": f"buildscape:block/{sub_name}_outer", "y": 180 },
                            "facing=south,half=bottom,shape=outer_left": { "model": f"buildscape:block/{sub_name}_outer", "y": 90 },
                            "facing=north,half=bottom,shape=outer_left": { "model": f"buildscape:block/{sub_name}_outer", "y": 270 },
                            "facing=east,half=bottom,shape=outer_right": { "model": f"buildscape:block/{sub_name}_outer", "y": 90 },
                            "facing=west,half=bottom,shape=outer_right": { "model": f"buildscape:block/{sub_name}_outer", "y": 270 },
                            "facing=south,half=bottom,shape=outer_right": { "model": f"buildscape:block/{sub_name}_outer", "y": 180 },
                            "facing=north,half=bottom,shape=outer_right": { "model": f"buildscape:block/{sub_name}_outer" },
                            "facing=east,half=top,shape=straight": { "model": f"buildscape:block/{sub_name}", "x": 180, "y": 180 },
                            "facing=west,half=top,shape=straight": { "model": f"buildscape:block/{sub_name}", "x": 180 },
                            "facing=south,half=top,shape=straight": { "model": f"buildscape:block/{sub_name}", "x": 180, "y": 90 },
                            "facing=north,half=top,shape=straight": { "model": f"buildscape:block/{sub_name}", "x": 180, "y": 270 },
                            "facing=east,half=top,shape=inner_left": { "model": f"buildscape:block/{sub_name}_inner", "x": 180, "y": 180 },
                            "facing=west,half=top,shape=inner_left": { "model": f"buildscape:block/{sub_name}_inner", "x": 180 },
                            "facing=south,half=top,shape=inner_left": { "model": f"buildscape:block/{sub_name}_inner", "x": 180, "y": 90 },
                            "facing=north,half=top,shape=inner_left": { "model": f"buildscape:block/{sub_name}_inner", "x": 180, "y": 270 },
                            "facing=east,half=top,shape=inner_right": { "model": f"buildscape:block/{sub_name}_inner", "x": 180, "y": 270 },
                            "facing=west,half=top,shape=inner_right": { "model": f"buildscape:block/{sub_name}_inner", "x": 180, "y": 90 },
                            "facing=south,half=top,shape=inner_right": { "model": f"buildscape:block/{sub_name}_inner", "x": 180, "y": 180 },
                            "facing=north,half=top,shape=inner_right": { "model": f"buildscape:block/{sub_name}_inner", "x": 180 },
                            "facing=east,half=top,shape=outer_left": { "model": f"buildscape:block/{sub_name}_outer", "x": 180, "y": 180 },
                            "facing=west,half=top,shape=outer_left": { "model": f"buildscape:block/{sub_name}_outer", "x": 180 },
                            "facing=south,half=top,shape=outer_left": { "model": f"buildscape:block/{sub_name}_outer", "x": 180, "y": 90 },
                            "facing=north,half=top,shape=outer_left": { "model": f"buildscape:block/{sub_name}_outer", "x": 180, "y": 270 },
                            "facing=east,half=top,shape=outer_right": { "model": f"buildscape:block/{sub_name}_outer", "x": 180, "y": 270 },
                            "facing=west,half=top,shape=outer_right": { "model": f"buildscape:block/{sub_name}_outer", "x": 180, "y": 90 },
                            "facing=south,half=top,shape=outer_right": { "model": f"buildscape:block/{sub_name}_outer", "x": 180, "y": 180 },
                            "facing=north,half=top,shape=outer_right": { "model": f"buildscape:block/{sub_name}_outer", "x": 180 }
                        }
                    })
                    # Block Models
                    write_json(os.path.join(BLOCK_MODELS_DIR, f"{sub_name}.json"), {
                        "parent": "minecraft:block/stairs",
                        "textures": {
                            "bottom": texture_path,
                            "top": texture_path,
                            "side": texture_path,
                            "particle": "#side"
                        }
                    })
                    write_json(os.path.join(BLOCK_MODELS_DIR, f"{sub_name}_inner.json"), {
                        "parent": "minecraft:block/stairs_inner",
                        "textures": {
                            "bottom": texture_path,
                            "top": texture_path,
                            "side": texture_path,
                            "particle": "#side"
                        }
                    })
                    write_json(os.path.join(BLOCK_MODELS_DIR, f"{sub_name}_outer.json"), {
                        "parent": "minecraft:block/stairs_outer",
                        "textures": {
                            "bottom": texture_path,
                            "top": texture_path,
                            "side": texture_path,
                            "particle": "#side"
                        }
                    })
                    # Item Model
                    write_json(os.path.join(ITEM_MODELS_DIR, f"{sub_name}.json"), {
                        "parent": f"buildscape:block/{sub_name}"
                    })
                    # Loot Table
                    write_json(os.path.join(LOOT_TABLES_DIR, f"{sub_name}.json"), loot_table)
                    # Crafting Recipe (6 blocks -> 4 stairs)
                    write_json(os.path.join(RECIPES_DIR, f"{sub_name}.json"), {
                        "type": "minecraft:crafting_shaped",
                        "category": "building",
                        "key": {
                            "#": { "item": f"buildscape:{block_name}" }
                        },
                        "pattern": [
                            "#  ",
                            "## ",
                            "###"
                        ],
                        "result": {
                            "count": 4,
                            "item": f"buildscape:{sub_name}"
                        }
                    })
                    # Stonecutting Recipe
                    write_json(os.path.join(RECIPES_DIR, f"{sub_name}_from_{block_name}_stonecutting.json"), {
                        "type": "minecraft:stonecutting",
                        "ingredient": { "item": f"buildscape:{block_name}" },
                        "result": f"buildscape:{sub_name}",
                        "count": 1
                    })
                    
                # Vertical Slabs
                elif sub == "vertical_slab":
                    all_vertical_slabs.append(f"buildscape:{sub_name}")
                    # Blockstate
                    write_json(os.path.join(BLOCKSTATES_DIR, f"{sub_name}.json"), {
                        "variants": {
                            "axis=z,type=bottom": { "model": f"buildscape:block/{sub_name}" },
                            "axis=z,type=top": { "model": f"buildscape:block/{sub_name}", "y": 180 },
                            "axis=x,type=bottom": { "model": f"buildscape:block/{sub_name}", "y": 270 },
                            "axis=x,type=top": { "model": f"buildscape:block/{sub_name}", "y": 90 },
                            "axis=z,type=double": { "model": f"buildscape:block/{block_name}" },
                            "axis=x,type=double": { "model": f"buildscape:block/{block_name}" }
                        }
                    })
                    # Block Model
                    write_json(os.path.join(BLOCK_MODELS_DIR, f"{sub_name}.json"), {
                        "parent": "buildscape:block/template_vertical_slab",
                        "textures": {
                            "bottom": texture_path,
                            "top": texture_path,
                            "side": texture_path,
                            "particle": "#side"
                        }
                    })
                    # Item Model
                    write_json(os.path.join(ITEM_MODELS_DIR, f"{sub_name}.json"), {
                        "parent": f"buildscape:block/{sub_name}"
                    })
                    # Loot Table (double vertical slab drop function)
                    write_json(os.path.join(LOOT_TABLES_DIR, f"{sub_name}.json"), {
                        "type": "minecraft:block",
                        "pools": [
                            {
                                "rolls": 1,
                                "bonus_rolls": 0,
                                "entries": [
                                    {
                                        "type": "minecraft:item",
                                        "functions": [
                                            {
                                                "function": "minecraft:set_count",
                                                "conditions": [
                                                    {
                                                        "condition": "minecraft:block_state_property",
                                                        "block": f"buildscape:{sub_name}",
                                                        "properties": { "type": "double" }
                                                    }
                                                ],
                                                "count": 2,
                                                "add": False
                                            },
                                            { "function": "minecraft:explosion_decay" }
                                        ],
                                        "name": f"buildscape:{sub_name}"
                                    }
                                ],
                                "conditions": [
                                    { "condition": "minecraft:survives_explosion" }
                                ]
                            }
                        ]
                    })
                    # Crafting recipes: slab <-> vertical slab (1-to-1 shapeless)
                    slab_item_id = f"buildscape:{block_name}_slab"
                    write_json(os.path.join(RECIPES_DIR, f"{sub_name}_from_slabs.json"), {
                        "type": "minecraft:crafting_shapeless",
                        "category": "building",
                        "ingredients": [
                            { "item": slab_item_id }
                        ],
                        "result": {
                            "item": f"buildscape:{sub_name}",
                            "count": 1
                        }
                    })
                    write_json(os.path.join(RECIPES_DIR, f"{block_name}_slab_from_{sub_name}.json"), {
                        "type": "minecraft:crafting_shapeless",
                        "category": "building",
                        "ingredients": [
                            { "item": f"buildscape:{sub_name}" }
                        ],
                        "result": {
                            "item": slab_item_id,
                            "count": 1
                        }
                    })
                    # Stonecutting recipes
                    write_json(os.path.join(RECIPES_DIR, f"{sub_name}_from_slab_stonecutting.json"), {
                        "type": "minecraft:stonecutting",
                        "ingredient": { "item": slab_item_id },
                        "result": f"buildscape:{sub_name}",
                        "count": 1
                    })
                    write_json(os.path.join(RECIPES_DIR, f"{block_name}_slab_from_{sub_name}_stonecutting.json"), {
                        "type": "minecraft:stonecutting",
                        "ingredient": { "item": f"buildscape:{sub_name}" },
                        "result": slab_item_id,
                        "count": 1
                    })
                    
                # Walls
                elif sub == "wall":
                    all_walls.append(f"buildscape:{sub_name}")
                    # Blockstate
                    write_json(os.path.join(BLOCKSTATES_DIR, f"{sub_name}.json"), {
                        "multipart": [
                            {
                                "when": { "up": "true" },
                                "apply": { "model": f"buildscape:block/{sub_name}_post" }
                            },
                            {
                                "when": { "north": "low" },
                                "apply": { "model": f"buildscape:block/{sub_name}_side", "uvlock": True }
                            },
                            {
                                "when": { "east": "low" },
                                "apply": { "model": f"buildscape:block/{sub_name}_side", "y": 90, "uvlock": True }
                            },
                            {
                                "when": { "south": "low" },
                                "apply": { "model": f"buildscape:block/{sub_name}_side", "y": 180, "uvlock": True }
                            },
                            {
                                "when": { "west": "low" },
                                "apply": { "model": f"buildscape:block/{sub_name}_side", "y": 270, "uvlock": True }
                            },
                            {
                                "when": { "north": "tall" },
                                "apply": { "model": f"buildscape:block/{sub_name}_side_tall", "uvlock": True }
                            },
                            {
                                "when": { "east": "tall" },
                                "apply": { "model": f"buildscape:block/{sub_name}_side_tall", "y": 90, "uvlock": True }
                            },
                            {
                                "when": { "south": "tall" },
                                "apply": { "model": f"buildscape:block/{sub_name}_side_tall", "y": 180, "uvlock": True }
                            },
                            {
                                "when": { "west": "tall" },
                                "apply": { "model": f"buildscape:block/{sub_name}_side_tall", "y": 270, "uvlock": True }
                            }
                        ]
                    })
                    # Block Models
                    write_json(os.path.join(BLOCK_MODELS_DIR, f"{sub_name}_post.json"), {
                        "parent": "minecraft:block/template_wall_post",
                        "textures": {
                            "wall": texture_path,
                            "particle": texture_path
                        }
                    })
                    write_json(os.path.join(BLOCK_MODELS_DIR, f"{sub_name}_side.json"), {
                        "parent": "minecraft:block/template_wall_side",
                        "textures": {
                            "wall": texture_path,
                            "particle": texture_path
                        }
                    })
                    write_json(os.path.join(BLOCK_MODELS_DIR, f"{sub_name}_side_tall.json"), {
                        "parent": "minecraft:block/template_wall_side_tall",
                        "textures": {
                            "wall": texture_path,
                            "particle": texture_path
                        }
                    })
                    # Item Model
                    write_json(os.path.join(ITEM_MODELS_DIR, f"{sub_name}.json"), {
                        "parent": "minecraft:block/wall_inventory",
                        "textures": {
                            "wall": texture_path
                        }
                    })
                    # Loot Table
                    write_json(os.path.join(LOOT_TABLES_DIR, f"{sub_name}.json"), loot_table)
                    # Crafting Recipe (6 blocks -> 6 walls)
                    write_json(os.path.join(RECIPES_DIR, f"{sub_name}.json"), {
                        "type": "minecraft:crafting_shaped",
                        "category": "building",
                        "key": {
                            "#": { "item": f"buildscape:{block_name}" }
                        },
                        "pattern": [
                            "###",
                            "###"
                        ],
                        "result": {
                            "count": 6,
                            "item": f"buildscape:{sub_name}"
                        }
                    })
                    # Stonecutting Recipe
                    write_json(os.path.join(RECIPES_DIR, f"{sub_name}_from_{block_name}_stonecutting.json"), {
                        "type": "minecraft:stonecutting",
                        "ingredient": { "item": f"buildscape:{block_name}" },
                        "result": f"buildscape:{sub_name}",
                        "count": 1
                    })
                    
                # Dye recipes for sub-variants too!
                # e.g., 8 flaming_cut_steel_stairs + 1 red_dye -> 8 red_flaming_cut_steel_stairs
                if color:
                    dye_recipe = {
                        "type": "minecraft:crafting_shaped",
                        "category": "building",
                        "key": {
                            "#": {
                                "item": f"buildscape:flaming_{main_type}_{sub}"
                            },
                            "D": {
                                "item": f"minecraft:{color}_dye"
                            }
                        },
                        "pattern": [
                            "###",
                            "#D#",
                            "###"
                        ],
                        "result": {
                            "count": 8,
                            "item": f"buildscape:{sub_name}"
                        }
                    }
                    write_json(os.path.join(RECIPES_DIR, f"{sub_name}_from_dyeing.json"), dye_recipe)

# 4. Ingot & nugget recipes
# Base ingot crafting: 2 magma cream + 2 steel ingot in cross -> 4 flaming steel ingot
write_json(os.path.join(RECIPES_DIR, "flaming_steel_ingot.json"), {
    "type": "minecraft:crafting_shaped",
    "key": {
        "M": { "item": "minecraft:magma_cream" },
        "S": { "item": "buildscape:steel_ingot" }
    },
    "pattern": [
        " M ",
        "S S",
        " M "
    ],
    "result": {
        "count": 4,
        "item": "buildscape:flaming_steel_ingot"
    }
})

# Base block crafting: 2 magma blocks + 2 steel blocks in cross -> 4 flaming steel blocks
write_json(os.path.join(RECIPES_DIR, "flaming_steel_block_from_magma_cross.json"), {
    "type": "minecraft:crafting_shaped",
    "key": {
        "M": { "item": "minecraft:magma_block" },
        "S": { "item": "buildscape:steel_block" }
    },
    "pattern": [
        " M ",
        "S S",
        " M "
    ],
    "result": {
        "count": 4,
        "item": "buildscape:flaming_steel_block"
    }
})

# Ingot / Nugget standard recipes
write_json(os.path.join(RECIPES_DIR, "flaming_steel_nugget.json"), {
    "type": "minecraft:crafting_shapeless",
    "ingredients": [
        { "item": "buildscape:flaming_steel_ingot" }
    ],
    "result": {
        "count": 9,
        "item": "buildscape:flaming_steel_nugget"
    }
})

write_json(os.path.join(RECIPES_DIR, "flaming_steel_ingot_from_nuggets.json"), {
    "type": "minecraft:crafting_shaped",
    "category": "misc",
    "key": {
        "N": { "item": "buildscape:flaming_steel_nugget" }
    },
    "pattern": [
        "NNN",
        "NNN",
        "NNN"
    ],
    "result": {
        "item": "buildscape:flaming_steel_ingot"
    }
})

# Ingot / Block standard recipes
write_json(os.path.join(RECIPES_DIR, "flaming_steel_ingot_from_block.json"), {
    "type": "minecraft:crafting_shapeless",
    "ingredients": [
        { "item": "buildscape:flaming_steel_block" }
    ],
    "result": {
        "count": 9,
        "item": "buildscape:flaming_steel_ingot"
    }
})

write_json(os.path.join(RECIPES_DIR, "flaming_steel_block_from_ingots.json"), {
    "type": "minecraft:crafting_shaped",
    "category": "building",
    "key": {
        "I": { "item": "buildscape:flaming_steel_ingot" }
    },
    "pattern": [
        "III",
        "III",
        "III"
    ],
    "result": {
        "item": "buildscape:flaming_steel_block"
    }
})

# 5. Append to tags
append_to_tag(MINEABLE_PICKAXE_PATH, all_pickaxe_blocks)
append_to_tag(SLABS_TAG_PATH, all_slabs)
append_to_tag(STAIRS_TAG_PATH, all_stairs)
append_to_tag(WALLS_TAG_PATH, all_walls)
append_to_tag(VERTICAL_SLABS_BLOCK_TAG, all_vertical_slabs)
append_to_tag(VERTICAL_SLABS_ITEM_TAG, all_vertical_slabs)

print("Flaming steel resources generation complete!")
