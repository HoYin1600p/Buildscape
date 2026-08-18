import os
import shutil
import json

SOURCE_DIR = r"I:\MOD-Dev\26.3"
DEST_DIR = r"I:\Buildscape\Buildscape"

WOODS = [
    "acacia", "bamboo", "birch", "cherry", "crimson", "dark_oak",
    "jungle", "mangrove", "oak", "pale_oak", "poplar", "spruce", "warped"
]

TEMPLATES = [
    "template_shelf_body", "template_shelf_center", "template_shelf_inventory",
    "template_shelf_left", "template_shelf_right", "template_shelf_unconnected",
    "template_shelf_unpowered"
]

def main():
    # 1. Copy textures
    tex_src = os.path.join(SOURCE_DIR, "block")
    tex_dest = os.path.join(DEST_DIR, "src", "main", "resources", "assets", "buildscape", "textures", "block")
    os.makedirs(tex_dest, exist_ok=True)
    for wood in WOODS:
        filename = f"{wood}_shelf.png"
        src_file = os.path.join(tex_src, filename)
        dest_file = os.path.join(tex_dest, filename)
        if os.path.exists(src_file):
            shutil.copy(src_file, dest_file)
            print(f"Copied texture: {filename}")
        else:
            print(f"Warning: Texture not found: {src_file}")

    # 2. Copy and adapt blockstates
    bs_src = os.path.join(SOURCE_DIR, "assets", "minecraft", "blockstates")
    bs_dest = os.path.join(DEST_DIR, "src", "main", "resources", "assets", "buildscape", "blockstates")
    os.makedirs(bs_dest, exist_ok=True)
    for wood in WOODS:
        filename = f"{wood}_shelf.json"
        src_file = os.path.join(bs_src, filename)
        dest_file = os.path.join(bs_dest, filename)
        if os.path.exists(src_file):
            with open(src_file, "r") as f:
                content = f.read()
            content = content.replace("minecraft:block/", "buildscape:block/")
            with open(dest_file, "w") as f:
                f.write(content)
            print(f"Adapted blockstate: {filename}")
        else:
            print(f"Warning: Blockstate not found: {src_file}")

    # 3. Copy and adapt template models
    model_src = os.path.join(SOURCE_DIR, "models", "block")
    model_dest = os.path.join(DEST_DIR, "src", "main", "resources", "assets", "buildscape", "models", "block")
    os.makedirs(model_dest, exist_ok=True)
    for template in TEMPLATES:
        filename = f"{template}.json"
        src_file = os.path.join(model_src, filename)
        dest_file = os.path.join(model_dest, filename)
        if os.path.exists(src_file):
            with open(src_file, "r") as f:
                content = f.read()
            content = content.replace("minecraft:", "buildscape:")
            with open(dest_file, "w") as f:
                f.write(content)
            print(f"Adapted template model: {filename}")
        else:
            print(f"Warning: Template model not found: {src_file}")

    # 4. Copy and adapt block models for all woods
    suffixes = ["", "_center", "_inventory", "_left", "_right", "_unconnected", "_unpowered"]
    for wood in WOODS:
        for suffix in suffixes:
            filename = f"{wood}_shelf{suffix}.json"
            src_file = os.path.join(model_src, filename)
            dest_file = os.path.join(model_dest, filename)
            if os.path.exists(src_file):
                with open(src_file, "r") as f:
                    content = f.read()
                content = content.replace("minecraft:", "buildscape:")
                with open(dest_file, "w") as f:
                    f.write(content)
                print(f"Adapted block model: {filename}")
            else:
                print(f"Warning: Block model not found: {src_file}")

    # 5. Generate item models (parent pointing to block inventory model)
    item_dest = os.path.join(DEST_DIR, "src", "main", "resources", "assets", "buildscape", "models", "item")
    os.makedirs(item_dest, exist_ok=True)
    for wood in WOODS:
        filename = f"{wood}_shelf.json"
        dest_file = os.path.join(item_dest, filename)
        model_data = {
            "parent": f"buildscape:block/{wood}_shelf_inventory"
        }
        with open(dest_file, "w") as f:
            json.dump(model_data, f, indent=2)
        print(f"Generated item model: {filename}")

    # 6. Generate loot tables
    loot_dest = os.path.join(DEST_DIR, "src", "main", "resources", "data", "buildscape", "loot_tables", "blocks")
    os.makedirs(loot_dest, exist_ok=True)
    for wood in WOODS:
        filename = f"{wood}_shelf.json"
        dest_file = os.path.join(loot_dest, filename)
        loot_data = {
            "type": "minecraft:block",
            "pools": [
                {
                    "rolls": 1,
                    "entries": [
                        {
                            "type": "minecraft:item",
                            "name": f"buildscape:{wood}_shelf"
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
        with open(dest_file, "w") as f:
            json.dump(loot_data, f, indent=2)
        print(f"Generated loot table: {filename}")

if __name__ == "__main__":
    main()
