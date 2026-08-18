import os
import json
import shutil

# Paths
ASSETS_DIR = "src/main/resources/assets/buildscape"
DATA_DIR = "src/main/resources/data/buildscape"
SOURCE_TEXTURES_DIR = r"I:\MOD-Dev\DGA DEV\Cardboard"

BLOCKSTATES_DIR = os.path.join(ASSETS_DIR, "blockstates")
BLOCK_MODELS_DIR = os.path.join(ASSETS_DIR, "models/block")
ITEM_MODELS_DIR = os.path.join(ASSETS_DIR, "models/item")
LOOT_TABLES_DIR = os.path.join(DATA_DIR, "loot_tables/blocks")
TEXTURES_DEST_DIR = os.path.join(ASSETS_DIR, "textures/block")

MINECRAFT_TAGS_DIR = "src/main/resources/data/minecraft/tags/blocks"
MINEABLE_AXE_PATH = os.path.join(MINECRAFT_TAGS_DIR, "mineable/axe.json")
MC_SLABS_TAG_PATH = os.path.join(MINECRAFT_TAGS_DIR, "slabs.json")
MC_STAIRS_TAG_PATH = os.path.join(MINECRAFT_TAGS_DIR, "stairs.json")
MC_WALLS_TAG_PATH = os.path.join(MINECRAFT_TAGS_DIR, "walls.json")

# Additional MC Tags
MC_BUTTONS_BLOCK_TAG = "src/main/resources/data/minecraft/tags/blocks/buttons.json"
MC_BUTTONS_ITEM_TAG = "src/main/resources/data/minecraft/tags/items/buttons.json"
MC_PRESSURE_PLATES_BLOCK_TAG = "src/main/resources/data/minecraft/tags/blocks/pressure_plates.json"
MC_PRESSURE_PLATES_ITEM_TAG = "src/main/resources/data/minecraft/tags/items/pressure_plates.json"
MC_TRAPDOORS_BLOCK_TAG = "src/main/resources/data/minecraft/tags/blocks/trapdoors.json"
MC_TRAPDOORS_ITEM_TAG = "src/main/resources/data/minecraft/tags/items/trapdoors.json"

BS_TAGS_DIR = os.path.join(DATA_DIR, "tags")
BS_SLABS_BLOCK_TAG = os.path.join(BS_TAGS_DIR, "blocks/slabs.json")
BS_SLABS_ITEM_TAG = os.path.join(BS_TAGS_DIR, "items/slabs.json")
BS_STAIRS_BLOCK_TAG = os.path.join(BS_TAGS_DIR, "blocks/stairs.json")
BS_STAIRS_ITEM_TAG = os.path.join(BS_TAGS_DIR, "items/stairs.json")
BS_WALLS_BLOCK_TAG = os.path.join(BS_TAGS_DIR, "blocks/walls.json")
BS_WALLS_ITEM_TAG = os.path.join(BS_TAGS_DIR, "items/walls.json")

# Additional BS Tags
BS_BUTTONS_BLOCK_TAG = os.path.join(BS_TAGS_DIR, "blocks/buttons.json")
BS_BUTTONS_ITEM_TAG = os.path.join(BS_TAGS_DIR, "items/buttons.json")
BS_WOOD_BUTTONS_BLOCK_TAG = os.path.join(BS_TAGS_DIR, "blocks/wooden_buttons.json")
BS_WOOD_BUTTONS_ITEM_TAG = os.path.join(BS_TAGS_DIR, "items/wooden_buttons.json")

BS_PRESSURE_PLATES_BLOCK_TAG = os.path.join(BS_TAGS_DIR, "blocks/pressure_plates.json")
BS_PRESSURE_PLATES_ITEM_TAG = os.path.join(BS_TAGS_DIR, "items/pressure_plates.json")
BS_WOOD_PRESSURE_PLATES_BLOCK_TAG = os.path.join(BS_TAGS_DIR, "blocks/wooden_pressure_plates.json")
BS_WOOD_PRESSURE_PLATES_ITEM_TAG = os.path.join(BS_TAGS_DIR, "items/wooden_pressure_plates.json")

BS_VERTICAL_SLABS_BLOCK_TAG = os.path.join(BS_TAGS_DIR, "blocks/vertical_slabs.json")
BS_VERTICAL_SLABS_ITEM_TAG = os.path.join(BS_TAGS_DIR, "items/vertical_slabs.json")

os.makedirs(BLOCKSTATES_DIR, exist_ok=True)
os.makedirs(BLOCK_MODELS_DIR, exist_ok=True)
os.makedirs(ITEM_MODELS_DIR, exist_ok=True)
os.makedirs(LOOT_TABLES_DIR, exist_ok=True)
os.makedirs(TEXTURES_DEST_DIR, exist_ok=True)

# Families and styles
FAMILIES = [
    {"prefix": "", "name": "Normal", "color": "MaterialColor.WOOD"},
    {"prefix": "stripped_", "name": "Stripped", "color": "MaterialColor.SAND"},
    {"prefix": "tinted_", "name": "Tinted", "color": "MaterialColor.COLOR_BROWN"},
    {"prefix": "washed_", "name": "Washed", "color": "MaterialColor.CLAY"},
    {"prefix": "burnt_", "name": "Burnt", "color": "MaterialColor.COLOR_BLACK"}
]

STYLES = [
    "cardboard_block",
    "smooth_cardboard_block",
    "bundled_cardboard",
    "pressed_cardboard",
    "thick_cardboard_block"
]

SUB_VARIANTS = ["slab", "stairs", "wall", "vertical_slab"]

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

# Lists for tags
all_axe_blocks = []
all_slabs = []
all_stairs = []
all_walls = []
all_buttons = []
all_pressure_plates = []
all_trapdoors = []
all_vertical_slabs = []

# Snippet lists for Java, Lang, Recipes
java_blocks_snippets = []
java_items_snippets = []
lang_entries = {}
recipes_crafting_snippets = []
recipes_crafting_shapeless_snippets = []
recipes_stonecutting_snippets = []
recipes_smelting_snippets = []

for family in FAMILIES:
    f_prefix = family["prefix"]
    f_name = family["name"]
    f_color = family["color"]
    
    for style in STYLES:
        block_name = f"{f_prefix}{style}"
        texture_path = f"buildscape:block/{block_name}"
        
        # 1. Copy Texture
        src_tex = os.path.join(SOURCE_TEXTURES_DIR, f"{block_name}.png")
        dest_tex = os.path.join(TEXTURES_DEST_DIR, f"{block_name}.png")
        if os.path.exists(src_tex):
            shutil.copy2(src_tex, dest_tex)
        else:
            print(f"Warning: Source texture not found at {src_tex}")
            
        all_axe_blocks.append(f"buildscape:{block_name}")
        
        # 2. Base Blockstate
        write_json(os.path.join(BLOCKSTATES_DIR, f"{block_name}.json"), {
            "variants": {
                "": { "model": f"buildscape:block/{block_name}" }
            }
        })
        
        # 3. Base Block Model
        write_json(os.path.join(BLOCK_MODELS_DIR, f"{block_name}.json"), {
            "parent": "minecraft:block/cube_all",
            "textures": {
                "all": texture_path
            }
        })
        
        # 4. Base Item Model
        write_json(os.path.join(ITEM_MODELS_DIR, f"{block_name}.json"), {
            "parent": f"buildscape:block/{block_name}"
        })
        
        # 5. Base Loot Table
        write_json(os.path.join(LOOT_TABLES_DIR, f"{block_name}.json"), {
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
        })
        
        # 6. Format translation name (Normal cardboard blocks omit the "Normal" prefix)
        formatted_style = style.replace("_", " ").title()
        formatted_style = formatted_style.replace("Cardboard Block", "Cardboard Block").replace("Bundled Cardboard", "Bundled Cardboard").replace("Pressed Cardboard", "Pressed Cardboard")
        if f_prefix == "":
            full_trans_name = formatted_style
        else:
            full_trans_name = f"{f_name} {formatted_style}"
            
        lang_entries[f"block.buildscape.{block_name}"] = full_trans_name
        lang_entries[f"item.buildscape.{block_name}"] = full_trans_name
        
        # 7. Java Snippet for Base Block and Item
        java_const = block_name.upper()
        java_blocks_snippets.append(
            f'        public static final RegistryObject<Block> {java_const} = BLOCKS.register("{block_name}",\n'
            f'                        () -> new ModBlock(\n'
            f'                                         BlockBehaviour.Properties.of(Material.WOOD, {f_color})\n'
            f'                                                         .strength(0.5f, 0.5f)\n'
            f'                                                         .sound(SoundType.WOOD)));'
        )
        java_items_snippets.append(
            f'    public static final RegistryObject<Item> {java_const} = ITEMS.register(\n'
            f'            "{block_name}",\n'
            f'            () -> new BlockItem(ModBlocks.{java_const}.get(), createBlockItemProperties())\n'
            f'    );'
        )
        
        # 8. Slabs, Stairs, Walls, Vertical Slabs Sub-variants
        for sub in SUB_VARIANTS:
            if style.endswith("_block"):
                sub_name = f"{f_prefix}{style[:-6]}_{sub}"
            else:
                sub_name = f"{f_prefix}{style}_{sub}"
                
            all_axe_blocks.append(f"buildscape:{sub_name}")
            
            # 8a. Slabs
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
                # Block Model (bottom)
                write_json(os.path.join(BLOCK_MODELS_DIR, f"{sub_name}.json"), {
                    "parent": "minecraft:block/slab",
                    "textures": {
                        "bottom": texture_path,
                        "top": texture_path,
                        "side": texture_path,
                        "particle": "#side"
                    }
                })
                # Block Model (top)
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
                # Loot Table
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
                
                # Java Registry
                sub_const = sub_name.upper()
                java_blocks_snippets.append(
                    f'        public static final RegistryObject<Block> {sub_const} = BLOCKS.register("{sub_name}",\n'
                    f'                        () -> new SlabBlock(\n'
                    f'                                         BlockBehaviour.Properties.of(Material.WOOD, {f_color})\n'
                    f'                                                         .strength(0.5f, 0.5f)\n'
                    f'                                                         .sound(SoundType.WOOD)));'
                )
                
            # 8b. Stairs
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
                write_json(os.path.join(LOOT_TABLES_DIR, f"{sub_name}.json"), {
                    "type": "minecraft:block",
                    "pools": [
                        {
                            "rolls": 1.0,
                            "bonus_rolls": 0.0,
                            "entries": [
                                {
                                    "type": "minecraft:item",
                                    "name": f"buildscape:{sub_name}"
                                }
                            ],
                            "conditions": [
                                {
                                    "condition": "minecraft:survives_explosion"
                                }
                            ]
                        }
                    ]
                })
                
                # Java Registry
                sub_const = sub_name.upper()
                java_blocks_snippets.append(
                    f'        public static final RegistryObject<Block> {sub_const} = BLOCKS.register("{sub_name}",\n'
                    f'                        () -> new ModStairBlock(\n'
                    f'                                         {block_name.upper()}.get().defaultBlockState(),\n'
                    f'                                         BlockBehaviour.Properties.of(Material.WOOD, {f_color})\n'
                    f'                                                         .strength(0.5f, 0.5f)\n'
                    f'                                                         .sound(SoundType.WOOD)));'
                )
                
            # 8c. Walls
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
                write_json(os.path.join(LOOT_TABLES_DIR, f"{sub_name}.json"), {
                    "type": "minecraft:block",
                    "pools": [
                        {
                            "rolls": 1.0,
                            "bonus_rolls": 0.0,
                            "entries": [
                                {
                                    "type": "minecraft:item",
                                    "name": f"buildscape:{sub_name}"
                                }
                            ],
                            "conditions": [
                                {
                                    "condition": "minecraft:survives_explosion"
                                }
                            ]
                        }
                    ]
                })
                
                # Java Registry
                sub_const = sub_name.upper()
                java_blocks_snippets.append(
                    f'        public static final RegistryObject<Block> {sub_const} = BLOCKS.register("{sub_name}",\n'
                    f'                        () -> new WallBlock(\n'
                    f'                                         BlockBehaviour.Properties.of(Material.WOOD, {f_color})\n'
                    f'                                                         .strength(0.5f, 0.5f)\n'
                    f'                                                         .sound(SoundType.WOOD)));'
                )
                
            # 8d. Vertical Slabs
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
                # Loot Table
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
                
                # Java Registry
                sub_const = sub_name.upper()
                slab_const = (f"{f_prefix}{style[:-6]}_slab" if style.endswith("_block") else f"{f_prefix}{style}_slab").upper()
                java_blocks_snippets.append(
                    f'        public static final RegistryObject<Block> {sub_const} = BLOCKS.register("{sub_name}",\n'
                    f'                        () -> new VerticalSlabBlock(ModBlocks.{block_name.upper()}.get(),\n'
                    f'                                         BlockBehaviour.Properties.copy(ModBlocks.{slab_const}.get())));'
                )

            # Lang Translation formatting
            formatted_sub = sub.replace("_", " ").title()
            if f_prefix == "":
                sub_trans_name = f"{formatted_style} {formatted_sub}"
            else:
                sub_trans_name = f"{f_name} {formatted_style} {formatted_sub}"
                
            sub_trans_name = sub_trans_name.replace("Block Slab", "Slab").replace("Block Stairs", "Stairs").replace("Block Wall", "Wall").replace("Block Vertical Slab", "Vertical Slab").replace("Block Button", "Button").replace("Block Pressure Plate", "Pressure Plate").replace("Block Trapdoor", "Trapdoor")
            lang_entries[f"block.buildscape.{sub_name}"] = sub_trans_name
            lang_entries[f"item.buildscape.{sub_name}"] = sub_trans_name
            
            # Java Item Snippet
            java_items_snippets.append(
                f'    public static final RegistryObject<Item> {sub_const} = ITEMS.register(\n'
                f'            "{sub_name}",\n'
                f'            () -> new BlockItem(ModBlocks.{sub_const}.get(), createBlockItemProperties())\n'
                f'    );'
            )
            
            # Recipes
            if sub == "slab":
                recipes_crafting_snippets.append(f'    ["BS:{sub_name}", ["###"], {{"#": "BS:{block_name}"}}, 6],')
            elif sub == "stairs":
                recipes_crafting_snippets.append(f'    ["BS:{sub_name}", ["#  ", "## ", "###"], {{"#": "BS:{block_name}"}}, 4],')
            elif sub == "wall":
                recipes_crafting_snippets.append(f'    ["BS:{sub_name}", ["   ", "###", "###"], {{"#": "BS:{block_name}"}}, 6],')
            elif sub == "vertical_slab":
                # Shapeless slab <-> vertical slab 1-to-1 recipes
                slab_base_name = f"{f_prefix}{style[:-6]}_slab" if style.endswith("_block") else f"{f_prefix}{style}_slab"
                recipes_crafting_shapeless_snippets.append(f'    ["BS:{sub_name}", "BS:{slab_base_name}", 1],')
                recipes_crafting_shapeless_snippets.append(f'    ["BS:{slab_base_name}", "BS:{sub_name}", 1],')
                
            # Stonecutting recipes
            if sub == "vertical_slab":
                slab_base_name = f"{f_prefix}{style[:-6]}_slab" if style.endswith("_block") else f"{f_prefix}{style}_slab"
                recipes_stonecutting_snippets.append(f'    ["BS:{sub_name}", "BS:{block_name}", 2],')
                recipes_stonecutting_snippets.append(f'    ["BS:{sub_name}", "BS:{slab_base_name}", 1],')
                recipes_stonecutting_snippets.append(f'    ["BS:{slab_base_name}", "BS:{sub_name}", 1],')
            else:
                recipes_stonecutting_snippets.append(
                    f'    ["BS:{sub_name}", "BS:{block_name}", 1],' if sub != "slab" else f'    ["BS:{sub_name}", "BS:{block_name}", 2],'
                )

        # 10. Styles crafting recipes (Shaped)
        if style == "smooth_cardboard_block":
            recipes_crafting_snippets.append(
                f'    ["BS:{block_name}", ["##", "##"], {{"#": "BS:{f_prefix}cardboard_block"}}, 4],'
            )
        elif style == "bundled_cardboard":
            recipes_crafting_snippets.append(
                f'    ["BS:{block_name}", ["#", "#", "#"], {{"#": "BS:{f_prefix}cardboard_block"}}, 3],'
            )
        elif style == "pressed_cardboard":
            slab_base = f"{f_prefix}cardboard_slab"
            recipes_crafting_snippets.append(
                f'    ["BS:{block_name}", ["#", "#"], {{"#": "BS:{slab_base}"}}, 2],'
            )
        elif style == "thick_cardboard_block":
            recipes_crafting_snippets.append(
                f'    ["BS:{block_name}", ["#", "#"], {{"#": "BS:{f_prefix}cardboard_block"}}, 2],'
            )
            
        # 11. Stonecutting recipes for variants from main base
        if f_prefix != "" and style == "cardboard_block":
            recipes_stonecutting_snippets.append(
                f'    ["BS:{block_name}", "BS:cardboard_block", 1],'
            )
            
        # 12. Smelting recipes for Burnt family
        if f_prefix == "burnt_":
            normal_block_name = block_name[6:]
            recipes_smelting_snippets.append(
                f'    ["BS:{block_name}", "BS:{normal_block_name}", 1],'
            )

        # 13. Trapdoors, Buttons, and Pressure Plates for all base cardboard blocks
        if style == "cardboard_block":
            for sub in ["trapdoor", "button", "pressure_plate"]:
                sub_name = f"{f_prefix}cardboard_{sub}"
                all_axe_blocks.append(f"buildscape:{sub_name}")
                
                # 13a. Trapdoor
                if sub == "trapdoor":
                    all_trapdoors.append(f"buildscape:{sub_name}")
                    write_json(os.path.join(BLOCKSTATES_DIR, f"{sub_name}.json"), {
                        "variants": {
                            "facing=north,half=bottom,open=false": { "model": f"buildscape:block/{sub_name}_bottom" },
                            "facing=south,half=bottom,open=false": { "model": f"buildscape:block/{sub_name}_bottom", "y": 180 },
                            "facing=east,half=bottom,open=false": { "model": f"buildscape:block/{sub_name}_bottom", "y": 90 },
                            "facing=west,half=bottom,open=false": { "model": f"buildscape:block/{sub_name}_bottom", "y": 270 },
                            "facing=north,half=top,open=false": { "model": f"buildscape:block/{sub_name}_top" },
                            "facing=south,half=top,open=false": { "model": f"buildscape:block/{sub_name}_top", "y": 180 },
                            "facing=east,half=top,open=false": { "model": f"buildscape:block/{sub_name}_top", "y": 90 },
                            "facing=west,half=top,open=false": { "model": f"buildscape:block/{sub_name}_top", "y": 270 },
                            "facing=north,half=bottom,open=true": { "model": f"buildscape:block/{sub_name}_open" },
                            "facing=south,half=bottom,open=true": { "model": f"buildscape:block/{sub_name}_open", "y": 180 },
                            "facing=east,half=bottom,open=true": { "model": f"buildscape:block/{sub_name}_open", "y": 90 },
                            "facing=west,half=bottom,open=true": { "model": f"buildscape:block/{sub_name}_open", "y": 270 },
                            "facing=north,half=top,open=true": { "model": f"buildscape:block/{sub_name}_open", "x": 180, "y": 180 },
                            "facing=south,half=top,open=true": { "model": f"buildscape:block/{sub_name}_open", "x": 180 },
                            "facing=east,half=top,open=true": { "model": f"buildscape:block/{sub_name}_open", "x": 180, "y": 90 },
                            "facing=west,half=top,open=true": { "model": f"buildscape:block/{sub_name}_open", "x": 180, "y": 270 }
                        }
                    })
                    write_json(os.path.join(BLOCK_MODELS_DIR, f"{sub_name}_bottom.json"), {
                        "parent": "minecraft:block/template_trapdoor_bottom",
                        "textures": {
                            "texture": texture_path
                        }
                    })
                    write_json(os.path.join(BLOCK_MODELS_DIR, f"{sub_name}_top.json"), {
                        "parent": "minecraft:block/template_trapdoor_top",
                        "textures": {
                            "texture": texture_path
                        }
                    })
                    write_json(os.path.join(BLOCK_MODELS_DIR, f"{sub_name}_open.json"), {
                        "parent": "minecraft:block/template_trapdoor_open",
                        "textures": {
                            "texture": texture_path
                        }
                    })
                    write_json(os.path.join(ITEM_MODELS_DIR, f"{sub_name}.json"), {
                        "parent": f"buildscape:block/{sub_name}_bottom"
                    })
                    
                    sub_const = sub_name.upper()
                    java_blocks_snippets.append(
                        f'        public static final RegistryObject<Block> {sub_const} = BLOCKS.register("{sub_name}",\n'
                        f'                        () -> new ModTrapdoorBlock(\n'
                        f'                                         BlockBehaviour.Properties.of(Material.WOOD, {f_color})\n'
                        f'                                                         .strength(0.5f)\n'
                        f'                                                         .sound(SoundType.WOOD)\n'
                        f'                                                         .noOcclusion()));'
                    )
                    recipes_crafting_snippets.append(
                        f'    ["BS:{sub_name}", ["###", "###"], {{"#": "BS:{block_name}"}}, 2],'
                    )
                    recipes_stonecutting_snippets.append(
                        f'    ["BS:{sub_name}", "BS:{block_name}", 1],'
                    )
                    
                # 13b. Button
                elif sub == "button":
                    all_buttons.append(f"buildscape:{sub_name}")
                    write_json(os.path.join(BLOCKSTATES_DIR, f"{sub_name}.json"), {
                        "variants": {
                            "face=floor,facing=north,powered=false": { "model": f"buildscape:block/{sub_name}", "y": 180 },
                            "face=floor,facing=east,powered=false": { "model": f"buildscape:block/{sub_name}", "y": 270 },
                            "face=floor,facing=south,powered=false": { "model": f"buildscape:block/{sub_name}" },
                            "face=floor,facing=west,powered=false": { "model": f"buildscape:block/{sub_name}", "y": 90 },
                            "face=floor,facing=north,powered=true": { "model": f"buildscape:block/{sub_name}_pressed", "y": 180 },
                            "face=floor,facing=east,powered=true": { "model": f"buildscape:block/{sub_name}_pressed", "y": 270 },
                            "face=floor,facing=south,powered=true": { "model": f"buildscape:block/{sub_name}_pressed" },
                            "face=floor,facing=west,powered=true": { "model": f"buildscape:block/{sub_name}_pressed", "y": 90 },
                            "face=wall,facing=north,powered=false": { "model": f"buildscape:block/{sub_name}", "x": 90 },
                            "face=wall,facing=east,powered=false": { "model": f"buildscape:block/{sub_name}", "x": 90, "y": 90 },
                            "face=wall,facing=south,powered=false": { "model": f"buildscape:block/{sub_name}", "x": 90, "y": 180 },
                            "face=wall,facing=west,powered=false": { "model": f"buildscape:block/{sub_name}", "x": 90, "y": 270 },
                            "face=wall,facing=north,powered=true": { "model": f"buildscape:block/{sub_name}_pressed", "x": 90 },
                            "face=wall,facing=east,powered=true": { "model": f"buildscape:block/{sub_name}_pressed", "x": 90, "y": 90 },
                            "face=wall,facing=south,powered=true": { "model": f"buildscape:block/{sub_name}_pressed", "x": 90, "y": 180 },
                            "face=wall,facing=west,powered=true": { "model": f"buildscape:block/{sub_name}_pressed", "x": 90, "y": 270 },
                            "face=ceiling,facing=north,powered=false": { "model": f"buildscape:block/{sub_name}", "x": 180, "y": 180 },
                            "face=ceiling,facing=east,powered=false": { "model": f"buildscape:block/{sub_name}", "x": 180, "y": 270 },
                            "face=ceiling,facing=south,powered=false": { "model": f"buildscape:block/{sub_name}", "x": 180 },
                            "face=ceiling,facing=west,powered=false": { "model": f"buildscape:block/{sub_name}", "x": 180, "y": 90 },
                            "face=ceiling,facing=north,powered=true": { "model": f"buildscape:block/{sub_name}_pressed", "x": 180, "y": 180 },
                            "face=ceiling,facing=east,powered=true": { "model": f"buildscape:block/{sub_name}_pressed", "x": 180, "y": 270 },
                            "face=ceiling,facing=south,powered=true": { "model": f"buildscape:block/{sub_name}_pressed", "x": 180 },
                            "face=ceiling,facing=west,powered=true": { "model": f"buildscape:block/{sub_name}_pressed", "x": 180, "y": 90 }
                        }
                    })
                    write_json(os.path.join(BLOCK_MODELS_DIR, f"{sub_name}.json"), {
                        "parent": "minecraft:block/button",
                        "textures": {
                            "texture": texture_path
                        }
                    })
                    write_json(os.path.join(BLOCK_MODELS_DIR, f"{sub_name}_pressed.json"), {
                        "parent": "minecraft:block/button_pressed",
                        "textures": {
                            "texture": texture_path
                        }
                    })
                    write_json(os.path.join(ITEM_MODELS_DIR, f"{sub_name}.json"), {
                        "parent": "minecraft:block/button_inventory",
                        "textures": {
                            "texture": texture_path
                        }
                    })
                    
                    sub_const = sub_name.upper()
                    java_blocks_snippets.append(
                        f'        public static final RegistryObject<Block> {sub_const} = BLOCKS.register("{sub_name}",\n'
                        f'                        () -> new WoodButtonBlock(\n'
                        f'                                         BlockBehaviour.Properties.of(Material.DECORATION, {f_color})\n'
                        f'                                                         .strength(0.5f)\n'
                        f'                                                         .sound(SoundType.WOOD)\n'
                        f'                                                         .noCollission()));'
                    )
                    recipes_crafting_snippets.append(
                        f'    ["BS:{sub_name}", ["#"], {{"#": "BS:{block_name}"}}, 1],'
                    )
                    recipes_stonecutting_snippets.append(
                        f'    ["BS:{sub_name}", "BS:{block_name}", 1],'
                    )
                    
                # 13c. Pressure Plate
                elif sub == "pressure_plate":
                    all_pressure_plates.append(f"buildscape:{sub_name}")
                    write_json(os.path.join(BLOCKSTATES_DIR, f"{sub_name}.json"), {
                        "variants": {
                            "powered=false": { "model": f"buildscape:block/{sub_name}" },
                            "powered=true": { "model": f"buildscape:block/{sub_name}_down" }
                        }
                    })
                    write_json(os.path.join(BLOCK_MODELS_DIR, f"{sub_name}.json"), {
                        "parent": "minecraft:block/pressure_plate_up",
                        "textures": {
                            "texture": texture_path
                        }
                    })
                    write_json(os.path.join(BLOCK_MODELS_DIR, f"{sub_name}_down.json"), {
                        "parent": "minecraft:block/pressure_plate_down",
                        "textures": {
                            "texture": texture_path
                        }
                    })
                    write_json(os.path.join(ITEM_MODELS_DIR, f"{sub_name}.json"), {
                        "parent": f"buildscape:block/{sub_name}"
                    })
                    
                    sub_const = sub_name.upper()
                    java_blocks_snippets.append(
                        f'        public static final RegistryObject<Block> {sub_const} = BLOCKS.register("{sub_name}",\n'
                        f'                        () -> new PressurePlateBlock(\n'
                        f'                                         PressurePlateBlock.Sensitivity.EVERYTHING,\n'
                        f'                                         BlockBehaviour.Properties.of(Material.WOOD, {f_color})\n'
                        f'                                                         .strength(0.5f)\n'
                        f'                                                         .sound(SoundType.WOOD)\n'
                        f'                                                         .noCollission()));'
                    )
                    recipes_crafting_snippets.append(
                        f'    ["BS:{sub_name}", ["##"], {{"#": "BS:{block_name}"}}, 1],'
                    )
                    recipes_stonecutting_snippets.append(
                        f'    ["BS:{sub_name}", "BS:{block_name}", 1],'
                    )
                    
                # Loot Table
                write_json(os.path.join(LOOT_TABLES_DIR, f"{sub_name}.json"), {
                    "type": "minecraft:block",
                    "pools": [
                        {
                            "rolls": 1.0,
                            "bonus_rolls": 0.0,
                            "entries": [
                                {
                                    "type": "minecraft:item",
                                    "name": f"buildscape:{sub_name}"
                                }
                            ],
                            "conditions": [
                                {
                                    "condition": "minecraft:survives_explosion"
                                }
                            ]
                        }
                    ]
                })
                
                # Item Registry
                java_items_snippets.append(
                    f'    public static final RegistryObject<Item> {sub_const} = ITEMS.register(\n'
                    f'            "{sub_name}",\n'
                    f'            () -> new BlockItem(ModBlocks.{sub_const}.get(), createBlockItemProperties())\n'
                    f'    );'
                )
                
                # Lang Translation
                formatted_sub = sub.replace("_", " ").title()
                if f_prefix == "":
                    sub_trans_name = f"Cardboard {formatted_sub}"
                else:
                    sub_trans_name = f"{f_name} Cardboard {formatted_sub}"
                lang_entries[f"block.buildscape.{sub_name}"] = sub_trans_name
                lang_entries[f"item.buildscape.{sub_name}"] = sub_trans_name
                
                # Smelting
                if f_prefix == "burnt_":
                    normal_sub_name = sub_name[6:]
                    recipes_smelting_snippets.append(
                        f'    ["BS:{sub_name}", "BS:{normal_sub_name}", 1],'
                    )

# Add base cardboard recipe: 8 paper around 1 slimeball -> 8 cardboard_block
recipes_crafting_snippets.insert(0,
    '    ["BS:cardboard_block", ["PPP", "PSP", "PPP"], {"P": "MC:paper", "S": "MC:slime_ball"}, 8],'
)

# Append to block tags
append_to_tag(MC_SLABS_TAG_PATH, all_slabs)
append_to_tag(MC_STAIRS_TAG_PATH, all_stairs)
append_to_tag(MC_WALLS_TAG_PATH, all_walls)
append_to_tag(MINEABLE_AXE_PATH, all_axe_blocks)

append_to_tag(BS_SLABS_BLOCK_TAG, all_slabs)
append_to_tag(BS_SLABS_ITEM_TAG, all_slabs)
append_to_tag(BS_STAIRS_BLOCK_TAG, all_stairs)
append_to_tag(BS_STAIRS_ITEM_TAG, all_stairs)
append_to_tag(BS_WALLS_BLOCK_TAG, all_walls)
append_to_tag(BS_WALLS_ITEM_TAG, all_walls)

# Additional Tag Files
MC_BUTTONS_BLOCK_TAG = "src/main/resources/data/minecraft/tags/blocks/buttons.json"
MC_BUTTONS_ITEM_TAG = "src/main/resources/data/minecraft/tags/items/buttons.json"
MC_PRESSURE_PLATES_BLOCK_TAG = "src/main/resources/data/minecraft/tags/blocks/pressure_plates.json"
MC_PRESSURE_PLATES_ITEM_TAG = "src/main/resources/data/minecraft/tags/items/pressure_plates.json"
MC_TRAPDOORS_BLOCK_TAG = "src/main/resources/data/minecraft/tags/blocks/trapdoors.json"
MC_TRAPDOORS_ITEM_TAG = "src/main/resources/data/minecraft/tags/items/trapdoors.json"

append_to_tag(MC_BUTTONS_BLOCK_TAG, all_buttons)
append_to_tag(MC_BUTTONS_ITEM_TAG, all_buttons)
append_to_tag(MC_PRESSURE_PLATES_BLOCK_TAG, all_pressure_plates)
append_to_tag(MC_PRESSURE_PLATES_ITEM_TAG, all_pressure_plates)
append_to_tag(MC_TRAPDOORS_BLOCK_TAG, all_trapdoors)
append_to_tag(MC_TRAPDOORS_ITEM_TAG, all_trapdoors)

append_to_tag(BS_BUTTONS_BLOCK_TAG, all_buttons)
append_to_tag(BS_BUTTONS_ITEM_TAG, all_buttons)
append_to_tag(BS_WOOD_BUTTONS_BLOCK_TAG, all_buttons)
append_to_tag(BS_WOOD_BUTTONS_ITEM_TAG, all_buttons)

append_to_tag(BS_PRESSURE_PLATES_BLOCK_TAG, all_pressure_plates)
append_to_tag(BS_PRESSURE_PLATES_ITEM_TAG, all_pressure_plates)
append_to_tag(BS_WOOD_PRESSURE_PLATES_BLOCK_TAG, all_pressure_plates)
append_to_tag(BS_WOOD_PRESSURE_PLATES_ITEM_TAG, all_pressure_plates)

# Append Vertical Slabs tags
append_to_tag(BS_VERTICAL_SLABS_BLOCK_TAG, all_vertical_slabs)
append_to_tag(BS_VERTICAL_SLABS_ITEM_TAG, all_vertical_slabs)

# Output text snippets for manual integration
scratch_dir = "scratch"
os.makedirs(scratch_dir, exist_ok=True)

with open(os.path.join(scratch_dir, "generated_java_blocks.txt"), "w", encoding="utf-8") as f:
    f.write("\n".join(java_blocks_snippets))

with open(os.path.join(scratch_dir, "generated_java_items.txt"), "w", encoding="utf-8") as f:
    f.write("\n".join(java_items_snippets))

with open(os.path.join(scratch_dir, "generated_lang.txt"), "w", encoding="utf-8") as f:
    f.write(json.dumps(lang_entries, indent=2))

with open(os.path.join(scratch_dir, "generated_recipes_crafting.txt"), "w", encoding="utf-8") as f:
    f.write("\n".join(recipes_crafting_snippets))

with open(os.path.join(scratch_dir, "generated_recipes_crafting_shapeless.txt"), "w", encoding="utf-8") as f:
    f.write("\n".join(recipes_crafting_shapeless_snippets))

with open(os.path.join(scratch_dir, "generated_recipes_stonecutting.txt"), "w", encoding="utf-8") as f:
    f.write("\n".join(recipes_stonecutting_snippets))

with open(os.path.join(scratch_dir, "generated_recipes_smelting.txt"), "w", encoding="utf-8") as f:
    f.write("\n".join(recipes_smelting_snippets))

print("Asset generation and code snippet compilation completed successfully!")
