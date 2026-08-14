import os
import json

BASE_DIR = r"I:\Buildscape\Buildscape\src\main\resources"
COLORS = [
    "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
    "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
]

def make_dirs():
    os.makedirs(os.path.join(BASE_DIR, "assets", "buildscape", "blockstates"), exist_ok=True)
    os.makedirs(os.path.join(BASE_DIR, "assets", "buildscape", "models", "block"), exist_ok=True)
    os.makedirs(os.path.join(BASE_DIR, "assets", "buildscape", "models", "item"), exist_ok=True)
    os.makedirs(os.path.join(BASE_DIR, "data", "buildscape", "loot_tables", "blocks"), exist_ok=True)
    os.makedirs(os.path.join(BASE_DIR, "data", "buildscape", "recipes"), exist_ok=True)
    os.makedirs(os.path.join(BASE_DIR, "data", "buildscape", "recipes", "vertical_variants"), exist_ok=True)
    os.makedirs(os.path.join(BASE_DIR, "data", "minecraft", "tags", "blocks", "mineable"), exist_ok=True)

def write_json(path, data):
    with open(path, "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2)

def generate_blockstates(color):
    # 1. Slab
    slab_state = {
        "variants": {
            "type=bottom": { "model": f"buildscape:block/{color}_layered_wool_slab" },
            "type=top": { "model": f"buildscape:block/{color}_layered_wool_slab_top" },
            "type=double": { "model": f"buildscape:block/{color}_layered_wool" }
        }
    }
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "blockstates", f"{color}_layered_wool_slab.json"), slab_state)

    # 2. Carpet
    carpet_state = {
        "variants": {
            "": { "model": f"buildscape:block/{color}_layered_wool_carpet" }
        }
    }
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "blockstates", f"{color}_layered_wool_carpet.json"), carpet_state)

    # 3. Vertical Slab
    vslab_state = {
        "variants": {
            "axis=z,type=bottom": { "model": f"buildscape:block/{color}_layered_wool_vertical_slab" },
            "axis=z,type=top": { "model": f"buildscape:block/{color}_layered_wool_vertical_slab", "y": 180 },
            "axis=x,type=bottom": { "model": f"buildscape:block/{color}_layered_wool_vertical_slab", "y": 270 },
            "axis=x,type=top": { "model": f"buildscape:block/{color}_layered_wool_vertical_slab", "y": 90 },
            "axis=z,type=double": { "model": f"buildscape:block/{color}_layered_wool" },
            "axis=x,type=double": { "model": f"buildscape:block/{color}_layered_wool" }
        }
    }
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "blockstates", f"{color}_layered_wool_vertical_slab.json"), vslab_state)

    # 4. Layers
    layers_state = {
        "variants": {
            f"layers={i}": {
                "particle": f"buildscape:block/{color}_layered_wool",
                "model": f"buildscape:block/{color}_layered_wool_layers_layer{i}"
            } for i in range(1, 9)
        }
    }
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "blockstates", f"{color}_layered_wool_layers.json"), layers_state)

    # 5. Stairs
    stairs_state = {
        "variants": {
            "facing=south,half=top,shape=outer_left":   { "y": 90,  "model": f"buildscape:block/{color}_layered_wool_stairs_outer_top", "x": 180 },
            "facing=south,half=top,shape=straight":     { "y": 90,  "model": f"buildscape:block/{color}_layered_wool_stairs_top", "x": 180 },
            "facing=west,half=bottom,shape=outer_right":{ "model": f"buildscape:block/{color}_layered_wool_stairs_outer", "y": 270 },
            "facing=east,half=bottom,shape=outer_left": { "model": f"buildscape:block/{color}_layered_wool_stairs_outer" },
            "facing=west,half=top,shape=inner_right":   { "y": 270, "model": f"buildscape:block/{color}_layered_wool_stairs_inner_top", "x": 180 },
            "facing=east,half=top,shape=outer_right":   { "y": 90,  "model": f"buildscape:block/{color}_layered_wool_stairs_outer_top", "x": 180 },
            "facing=west,half=top,shape=straight":      { "y": 180, "model": f"buildscape:block/{color}_layered_wool_stairs_top", "x": 180 },
            "facing=east,half=top,shape=straight":      { "model": f"buildscape:block/{color}_layered_wool_stairs_top", "x": 180 },
            "facing=north,half=top,shape=straight":     { "y": 270, "model": f"buildscape:block/{color}_layered_wool_stairs_top", "x": 180 },
            "facing=west,half=bottom,shape=outer_left": { "model": f"buildscape:block/{color}_layered_wool_stairs_outer", "y": 180 },
            "facing=north,half=bottom,shape=outer_right":{ "model": f"buildscape:block/{color}_layered_wool_stairs_outer" },
            "facing=south,half=bottom,shape=inner_right":{ "model": f"buildscape:block/{color}_layered_wool_stairs_inner", "y": 180 },
            "facing=south,half=bottom,shape=outer_left": { "model": f"buildscape:block/{color}_layered_wool_stairs_outer", "y": 90 },
            "facing=north,half=bottom,shape=inner_right":{ "model": f"buildscape:block/{color}_layered_wool_stairs_inner" },
            "facing=west,half=top,shape=inner_left":    { "y": 180, "model": f"buildscape:block/{color}_layered_wool_stairs_inner_top", "x": 180 },
            "facing=west,half=top,shape=outer_left":    { "y": 180, "model": f"buildscape:block/{color}_layered_wool_stairs_outer_top", "x": 180 },
            "facing=north,half=top,shape=outer_left":   { "y": 270, "model": f"buildscape:block/{color}_layered_wool_stairs_outer_top", "x": 180 },
            "facing=east,half=bottom,shape=inner_left": { "model": f"buildscape:block/{color}_layered_wool_stairs_inner" },
            "facing=north,half=top,shape=outer_right":  { "model": f"buildscape:block/{color}_layered_wool_stairs_outer_top", "x": 180 },
            "facing=west,half=bottom,shape=inner_right":{ "model": f"buildscape:block/{color}_layered_wool_stairs_inner", "y": 270 },
            "facing=south,half=bottom,shape=inner_left":{ "model": f"buildscape:block/{color}_layered_wool_stairs_inner", "y": 90 },
            "facing=south,half=top,shape=inner_left":   { "y": 90,  "model": f"buildscape:block/{color}_layered_wool_stairs_inner_top", "x": 180 },
            "facing=north,half=top,shape=inner_right":  { "model": f"buildscape:block/{color}_layered_wool_stairs_inner_top", "x": 180 },
            "facing=south,half=top,shape=inner_right":  { "y": 180, "model": f"buildscape:block/{color}_layered_wool_stairs_inner_top", "x": 180 },
            "facing=east,half=bottom,shape=inner_right":{ "model": f"buildscape:block/{color}_layered_wool_stairs_inner", "y": 90 },
            "facing=north,half=bottom,shape=straight":  { "model": f"buildscape:block/{color}_layered_wool_stairs", "y": 270 },
            "facing=east,half=top,shape=outer_left":    { "model": f"buildscape:block/{color}_layered_wool_stairs_outer_top", "x": 180 },
            "facing=east,half=bottom,shape=straight":   { "model": f"buildscape:block/{color}_layered_wool_stairs" },
            "facing=west,half=bottom,shape=straight":   { "model": f"buildscape:block/{color}_layered_wool_stairs", "y": 180 },
            "facing=south,half=bottom,shape=outer_right":{ "model": f"buildscape:block/{color}_layered_wool_stairs_outer", "y": 180 },
            "facing=north,half=bottom,shape=outer_left": { "model": f"buildscape:block/{color}_layered_wool_stairs_outer", "y": 270 },
            "facing=west,half=bottom,shape=inner_left":  { "model": f"buildscape:block/{color}_layered_wool_stairs_inner", "y": 180 },
            "facing=north,half=top,shape=inner_left":    { "y": 270, "model": f"buildscape:block/{color}_layered_wool_stairs_inner_top", "x": 180 },
            "facing=west,half=top,shape=outer_right":    { "y": 270, "model": f"buildscape:block/{color}_layered_wool_stairs_outer_top", "x": 180 },
            "facing=south,half=bottom,shape=straight":   { "model": f"buildscape:block/{color}_layered_wool_stairs", "y": 90 },
            "facing=south,half=top,shape=outer_right":   { "y": 180, "model": f"buildscape:block/{color}_layered_wool_stairs_outer_top", "x": 180 },
            "facing=east,half=bottom,shape=outer_right": { "model": f"buildscape:block/{color}_layered_wool_stairs_outer", "y": 90 },
            "facing=east,half=top,shape=inner_right":    { "y": 90,  "model": f"buildscape:block/{color}_layered_wool_stairs_inner_top", "x": 180 },
            "facing=north,half=bottom,shape=inner_left": { "model": f"buildscape:block/{color}_layered_wool_stairs_inner", "y": 270 },
            "facing=east,half=top,shape=inner_left":     { "model": f"buildscape:block/{color}_layered_wool_stairs_inner_top", "x": 180 }
        }
    }
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "blockstates", f"{color}_layered_wool_stairs.json"), stairs_state)

    # 6. Wall
    wall_state = {
        "multipart": [
            { "when": { "up": "true" }, "apply": { "model": f"buildscape:block/{color}_layered_wool_wall_post" } },
            { "when": { "north": "low" }, "apply": { "model": f"buildscape:block/{color}_layered_wool_wall_side", "uvlock": True } },
            { "when": { "east": "low" }, "apply": { "y": 90, "uvlock": True, "model": f"buildscape:block/{color}_layered_wool_wall_side" } },
            { "when": { "south": "low" }, "apply": { "y": 180, "uvlock": True, "model": f"buildscape:block/{color}_layered_wool_wall_side" } },
            { "when": { "west": "low" }, "apply": { "y": 270, "uvlock": True, "model": f"buildscape:block/{color}_layered_wool_wall_side" } },
            { "when": { "north": "tall" }, "apply": { "model": f"buildscape:block/{color}_layered_wool_wall_side_tall", "uvlock": True } },
            { "when": { "east": "tall" }, "apply": { "y": 90, "uvlock": True, "model": f"buildscape:block/{color}_layered_wool_wall_side_tall" } },
            { "when": { "south": "tall" }, "apply": { "y": 180, "uvlock": True, "model": f"buildscape:block/{color}_layered_wool_wall_side_tall" } },
            { "when": { "west": "tall" }, "apply": { "y": 270, "uvlock": True, "model": f"buildscape:block/{color}_layered_wool_wall_side_tall" } }
        ]
    }
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "blockstates", f"{color}_layered_wool_wall.json"), wall_state)


def generate_block_models(color):
    # 1. Slab & Slab Top
    tex = {
        "bottom": f"buildscape:block/{color}_layered_wool",
        "top": f"buildscape:block/{color}_layered_wool",
        "side": f"buildscape:block/{color}_layered_wool"
    }
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "models", "block", f"{color}_layered_wool_slab.json"), { "textures": tex, "parent": "minecraft:block/slab" })
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "models", "block", f"{color}_layered_wool_slab_top.json"), { "textures": tex, "parent": "minecraft:block/slab_top" })

    # 2. Stairs (6 files)
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "models", "block", f"{color}_layered_wool_stairs.json"), { "textures": tex, "parent": "minecraft:block/stairs" })
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "models", "block", f"{color}_layered_wool_stairs_top.json"), { "textures": tex, "parent": "minecraft:block/stairs" })
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "models", "block", f"{color}_layered_wool_stairs_inner.json"), { "textures": tex, "parent": "minecraft:block/inner_stairs" })
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "models", "block", f"{color}_layered_wool_stairs_inner_top.json"), { "textures": tex, "parent": "minecraft:block/inner_stairs" })
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "models", "block", f"{color}_layered_wool_stairs_outer.json"), { "textures": tex, "parent": "minecraft:block/outer_stairs" })
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "models", "block", f"{color}_layered_wool_stairs_outer_top.json"), { "textures": tex, "parent": "minecraft:block/outer_stairs" })

    # 3. Carpet
    carpet_model = {
        "parent": "minecraft:block/carpet",
        "textures": {
            "wool": f"buildscape:block/{color}_layered_wool",
            "particle": f"buildscape:block/{color}_layered_wool"
        }
    }
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "models", "block", f"{color}_layered_wool_carpet.json"), carpet_model)

    # 4. Layers (8 files)
    for i in range(1, 8):
        write_json(os.path.join(BASE_DIR, "assets", "buildscape", "models", "block", f"{color}_layered_wool_layers_layer{i}.json"), {
            "textures": {
                "particle": f"buildscape:block/{color}_layered_wool",
                "texture": f"buildscape:block/{color}_layered_wool"
            },
            "parent": f"minecraft:block/snow_height{i * 2}"
        })
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "models", "block", f"{color}_layered_wool_layers_layer8.json"), {
        "textures": {
            "particle": f"buildscape:block/{color}_layered_wool",
            "all": f"buildscape:block/{color}_layered_wool"
        },
        "parent": "minecraft:block/cube_all"
    })

    # 5. Wall (4 files)
    wall_tex = {
        "wall": f"buildscape:block/{color}_layered_wool",
        "particle": f"buildscape:block/{color}_layered_wool"
    }
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "models", "block", f"{color}_layered_wool_wall_post.json"), { "textures": wall_tex, "parent": "minecraft:block/template_wall_post" })
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "models", "block", f"{color}_layered_wool_wall_side.json"), { "textures": wall_tex, "parent": "minecraft:block/template_wall_side" })
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "models", "block", f"{color}_layered_wool_wall_side_tall.json"), { "textures": wall_tex, "parent": "minecraft:block/template_wall_side_tall" })
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "models", "block", f"{color}_layered_wool_wall_inventory.json"), { "textures": wall_tex, "parent": "minecraft:block/wall_inventory" })

    # 6. Vertical Slab
    vslab_model = {
        "parent": "buildscape:block/template_vertical_slab",
        "textures": {
            "bottom": f"buildscape:block/{color}_layered_wool",
            "top": f"buildscape:block/{color}_layered_wool",
            "side": f"buildscape:block/{color}_layered_wool",
            "particle": "#side"
        }
    }
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "models", "block", f"{color}_layered_wool_vertical_slab.json"), vslab_model)


def generate_item_models(color):
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "models", "item", f"{color}_layered_wool_slab.json"), { "parent": f"buildscape:block/{color}_layered_wool_slab" })
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "models", "item", f"{color}_layered_wool_stairs.json"), { "parent": f"buildscape:block/{color}_layered_wool_stairs" })
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "models", "item", f"{color}_layered_wool_layers.json"), { "parent": f"buildscape:block/{color}_layered_wool_layers_layer1" })
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "models", "item", f"{color}_layered_wool_carpet.json"), { "parent": f"buildscape:block/{color}_layered_wool_carpet" })
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "models", "item", f"{color}_layered_wool_wall.json"), { "parent": f"buildscape:block/{color}_layered_wool_wall_inventory" })
    write_json(os.path.join(BASE_DIR, "assets", "buildscape", "models", "item", f"{color}_layered_wool_vertical_slab.json"), { "parent": f"buildscape:block/{color}_layered_wool_vertical_slab" })


def generate_loot_tables(color):
    def make_pool(item_name, is_slab=False):
        entry = {
            "type": "minecraft:item",
            "name": item_name
        }
        if is_slab:
            entry["functions"] = [
                {
                    "function": "minecraft:set_count",
                    "conditions": [
                        {
                            "condition": "minecraft:block_state_property",
                            "block": item_name,
                            "properties": { "type": "double" }
                        }
                    ],
                    "count": 2.0,
                    "add": False
                },
                { "function": "minecraft:explosion_decay" }
            ]
        
        return {
            "rolls": 1.0,
            "bonus_rolls": 0.0,
            "entries": [entry],
            "conditions": [
                { "condition": "minecraft:survives_explosion" }
            ]
        }

    for t in ["stairs", "carpet", "layers", "wall"]:
        item_name = f"buildscape:{color}_layered_wool_{t}"
        if t == "layers":
            item_name = f"buildscape:{color}_layered_wool_layers"
        data = { "type": "minecraft:block", "pools": [make_pool(item_name, is_slab=False)] }
        write_json(os.path.join(BASE_DIR, "data", "buildscape", "loot_tables", "blocks", f"{color}_layered_wool_{t}.json"), data)

    for t in ["slab", "vertical_slab"]:
        item_name = f"buildscape:{color}_layered_wool_{t}"
        data = { "type": "minecraft:block", "pools": [make_pool(item_name, is_slab=True)] }
        write_json(os.path.join(BASE_DIR, "data", "buildscape", "loot_tables", "blocks", f"{color}_layered_wool_{t}.json"), data)


def generate_recipes(color):
    # 1. Slab crafting
    slab_crafting = {
        "key": { "#": { "item": f"buildscape:{color}_layered_wool" } },
        "category": "building",
        "pattern": [ "###" ],
        "result": { "count": 6, "item": f"buildscape:{color}_layered_wool_slab" },
        "type": "minecraft:crafting_shaped"
    }
    write_json(os.path.join(BASE_DIR, "data", "buildscape", "recipes", f"{color}_layered_wool_slab_crafting.json"), slab_crafting)

    # 2. Stairs crafting
    stairs_crafting = {
        "key": { "#": { "item": f"buildscape:{color}_layered_wool" } },
        "category": "building",
        "pattern": [ "#  ", "## ", "###" ],
        "result": { "count": 4, "item": f"buildscape:{color}_layered_wool_stairs" },
        "type": "minecraft:crafting_shaped"
    }
    write_json(os.path.join(BASE_DIR, "data", "buildscape", "recipes", f"{color}_layered_wool_stairs_crafting.json"), stairs_crafting)

    # 3. Wall crafting
    wall_crafting = {
        "key": { "#": { "item": f"buildscape:{color}_layered_wool" } },
        "category": "building",
        "pattern": [ "###", "###" ],
        "result": { "count": 6, "item": f"buildscape:{color}_layered_wool_wall" },
        "type": "minecraft:crafting_shaped"
    }
    write_json(os.path.join(BASE_DIR, "data", "buildscape", "recipes", f"{color}_layered_wool_wall_crafting.json"), wall_crafting)

    # 4. Carpet crafting
    carpet_crafting = {
        "key": { "#": { "item": f"buildscape:{color}_layered_wool" } },
        "category": "building",
        "pattern": [ "##" ],
        "result": { "count": 3, "item": f"buildscape:{color}_layered_wool_carpet" },
        "type": "minecraft:crafting_shaped"
    }
    write_json(os.path.join(BASE_DIR, "data", "buildscape", "recipes", f"{color}_layered_wool_carpet_crafting.json"), carpet_crafting)

    # 5. Layers crafting
    layers_crafting = {
        "key": { "#": { "item": f"buildscape:{color}_layered_wool_carpet" } },
        "category": "building",
        "pattern": [ "###" ],
        "result": { "count": 8, "item": f"buildscape:{color}_layered_wool_layers" },
        "type": "minecraft:crafting_shaped"
    }
    write_json(os.path.join(BASE_DIR, "data", "buildscape", "recipes", f"{color}_layered_wool_layers_crafting.json"), layers_crafting)

    # 6. Slab <-> Vertical Slab shapeless recipes
    write_json(os.path.join(BASE_DIR, "data", "buildscape", "recipes", "vertical_variants", f"{color}_layered_wool_slab_from_{color}_layered_wool_vertical_slab.json"), {
        "type": "minecraft:crafting_shapeless",
        "category": "building",
        "ingredients": [ { "item": f"buildscape:{color}_layered_wool_vertical_slab" } ],
        "result": { "item": f"buildscape:{color}_layered_wool_slab", "count": 1 }
    })
    write_json(os.path.join(BASE_DIR, "data", "buildscape", "recipes", "vertical_variants", f"{color}_layered_wool_vertical_slab_from_{color}_layered_wool_slab.json"), {
        "type": "minecraft:crafting_shapeless",
        "category": "building",
        "ingredients": [ { "item": f"buildscape:{color}_layered_wool_slab" } ],
        "result": { "item": f"buildscape:{color}_layered_wool_vertical_slab", "count": 1 }
    })

    # 7. Slab <-> Vertical Slab stonecutting recipes
    write_json(os.path.join(BASE_DIR, "data", "buildscape", "recipes", "vertical_variants", f"{color}_layered_wool_slab_from_{color}_layered_wool_vertical_slab_stonecutting.json"), {
        "type": "minecraft:stonecutting",
        "ingredient": { "item": f"buildscape:{color}_layered_wool_vertical_slab" },
        "result": f"buildscape:{color}_layered_wool_slab",
        "count": 1
    })
    write_json(os.path.join(BASE_DIR, "data", "buildscape", "recipes", "vertical_variants", f"{color}_layered_wool_vertical_slab_from_{color}_layered_wool_slab_stonecutting.json"), {
        "type": "minecraft:stonecutting",
        "ingredient": { "item": f"buildscape:{color}_layered_wool_slab" },
        "result": f"buildscape:{color}_layered_wool_vertical_slab",
        "count": 1
    })


def update_tag(file_path, new_entries):
    full_path = os.path.join(BASE_DIR, file_path)
    if os.path.exists(full_path):
        with open(full_path, "r", encoding="utf-8") as f:
            data = json.load(f)
    else:
        data = { "replace": False, "values": [] }
    
    values = data.get("values", [])
    updated = False
    for entry in new_entries:
        if entry not in values:
            values.append(entry)
            updated = True
    
    if updated:
        data["values"] = values
        write_json(full_path, data)
        print(f"Updated tag: {file_path}")


def update_all_tags():
    # 1. Slabs block tag
    slab_entries = []
    for c in COLORS:
        slab_entries.append(f"buildscape:{c}_layered_wool_slab")
        slab_entries.append(f"buildscape:{c}_layered_wool_vertical_slab")
    update_tag("data/minecraft/tags/blocks/slabs.json", slab_entries)

    # 2. Stairs block tag
    stairs_entries = []
    for c in COLORS:
        stairs_entries.append(f"buildscape:{c}_layered_wool_stairs")
    update_tag("data/minecraft/tags/blocks/stairs.json", stairs_entries)

    # 3. Walls block tag
    wall_entries = []
    for c in COLORS:
        wall_entries.append(f"buildscape:{c}_layered_wool_wall")
    update_tag("data/minecraft/tags/blocks/walls.json", wall_entries)

    # 4. Shears block tag
    shears_entries = []
    for c in COLORS:
        shears_entries.extend([
            f"buildscape:{c}_layered_wool_slab",
            f"buildscape:{c}_layered_wool_stairs",
            f"buildscape:{c}_layered_wool_layers",
            f"buildscape:{c}_layered_wool_carpet",
            f"buildscape:{c}_layered_wool_wall",
            f"buildscape:{c}_layered_wool_vertical_slab"
        ])
    update_tag("data/minecraft/tags/blocks/mineable/shears.json", shears_entries)


def main():
    print("Making directories...")
    make_dirs()
    print("Generating color files...")
    for color in COLORS:
        print(f"Color: {color}")
        generate_blockstates(color)
        generate_block_models(color)
        generate_item_models(color)
        generate_loot_tables(color)
        generate_recipes(color)
    print("Updating tags...")
    update_all_tags()
    print("Finished generating resources!")

if __name__ == "__main__":
    main()
