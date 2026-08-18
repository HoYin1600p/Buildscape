import os
import json
import re

# Paths
MOD_BLOCKS_PATH = "src/main/java/com/kingodogo/buildscape/block/ModBlocks.java"
MOD_ITEMS_PATH = "src/main/java/com/kingodogo/buildscape/item/ModItems.java"
LANG_PATH = "src/main/resources/assets/buildscape/lang/en_us.json"
CRAFTING_PATH = "src/main/resources/data/buildscape/recipes_pack/crafting.json"
STONECUTTING_PATH = "src/main/resources/data/buildscape/recipes_pack/stonecutting.json"
SMELTING_PATH = "src/main/resources/data/buildscape/recipes_pack/smelting.json"

def clean_recipes(recipes_list):
    prefixes = [
        "BS:cardboard", "BS:stripped_cardboard", "BS:tinted_cardboard", "BS:washed_cardboard", "BS:burnt_cardboard",
        "BS:smooth_cardboard", "BS:stripped_smooth_cardboard", "BS:tinted_smooth_cardboard", "BS:washed_smooth_cardboard", "BS:burnt_smooth_cardboard",
        "BS:bundled_cardboard", "BS:stripped_bundled_cardboard", "BS:tinted_bundled_cardboard", "BS:washed_bundled_cardboard", "BS:burnt_bundled_cardboard",
        "BS:pressed_cardboard", "BS:stripped_pressed_cardboard", "BS:tinted_pressed_cardboard", "BS:washed_pressed_cardboard", "BS:burnt_pressed_cardboard",
        "BS:thick_cardboard", "BS:stripped_thick_cardboard", "BS:tinted_thick_cardboard", "BS:washed_thick_cardboard", "BS:burnt_thick_cardboard"
    ]
    cleaned = []
    for r in recipes_list:
        if isinstance(r, list) and len(r) > 0 and isinstance(r[0], str):
            is_cardboard = False
            for p in prefixes:
                if r[0].startswith(p):
                    is_cardboard = True
                    break
            if not is_cardboard:
                cleaned.append(r)
        else:
            cleaned.append(r)
    return cleaned

# 1. Integrate Java Blocks
print("Integrating Java Blocks...")
with open("scratch/generated_java_blocks.txt", "r", encoding="utf-8") as f:
    blocks_code = f.read()

with open(MOD_BLOCKS_PATH, "r", encoding="utf-8") as f:
    java_blocks = f.read()

pattern_blocks = r"(//\s*CARDBOARD\s*BLOCKS\s*BEGIN\r?\n).*?(\r?\n\s*//\s*CARDBOARD\s*BLOCKS\s*END)"
match = re.search(pattern_blocks, java_blocks, re.DOTALL)
if match:
    replacement = match.group(1) + blocks_code + match.group(2)
    java_blocks = java_blocks[:match.start()] + replacement + java_blocks[match.end():]
    with open(MOD_BLOCKS_PATH, "w", encoding="utf-8") as f:
        f.write(java_blocks)
    print(" - Block integration completed successfully.")
else:
    print("Error: Could not locate CARDBOARD BLOCKS BEGIN/END in ModBlocks.java!")

# 2. Integrate Java Items
print("Integrating Java Items...")
with open("scratch/generated_java_items.txt", "r", encoding="utf-8") as f:
    items_code = f.read()

with open(MOD_ITEMS_PATH, "r", encoding="utf-8") as f:
    java_items = f.read()

pattern_items = r"(//\s*CARDBOARD\s*ITEMS\s*BEGIN\r?\n).*?(\r?\n\s*//\s*CARDBOARD\s*ITEMS\s*END)"
match = re.search(pattern_items, java_items, re.DOTALL)
if match:
    replacement = match.group(1) + items_code + match.group(2)
    java_items = java_items[:match.start()] + replacement + java_items[match.end():]
    with open(MOD_ITEMS_PATH, "w", encoding="utf-8") as f:
        f.write(java_items)
    print(" - Item integration completed successfully.")
else:
    print("Error: Could not locate CARDBOARD ITEMS BEGIN/END in ModItems.java!")

# 3. Integrate Translations
print("Integrating Lang Translations...")
with open(LANG_PATH, "r", encoding="utf-8") as f:
    lang = json.load(f)

# Clear old cardboard translation entries first to prevent stale "Normal" translations
clean_lang = {}
for k, v in lang.items():
    if not (k.startswith("block.buildscape.cardboard") or 
            k.startswith("item.buildscape.cardboard") or 
            k.startswith("block.buildscape.stripped_cardboard") or 
            k.startswith("item.buildscape.stripped_cardboard") or 
            k.startswith("block.buildscape.tinted_cardboard") or 
            k.startswith("item.buildscape.tinted_cardboard") or 
            k.startswith("block.buildscape.washed_cardboard") or 
            k.startswith("item.buildscape.washed_cardboard") or 
            k.startswith("block.buildscape.burnt_cardboard") or 
            k.startswith("item.buildscape.burnt_cardboard") or
            k.startswith("block.buildscape.smooth_cardboard") or 
            k.startswith("item.buildscape.smooth_cardboard") or 
            k.startswith("block.buildscape.stripped_smooth_cardboard") or 
            k.startswith("item.buildscape.stripped_smooth_cardboard") or 
            k.startswith("block.buildscape.tinted_smooth_cardboard") or 
            k.startswith("item.buildscape.tinted_smooth_cardboard") or 
            k.startswith("block.buildscape.washed_smooth_cardboard") or 
            k.startswith("item.buildscape.washed_smooth_cardboard") or 
            k.startswith("block.buildscape.burnt_smooth_cardboard") or 
            k.startswith("item.buildscape.burnt_smooth_cardboard") or
            k.startswith("block.buildscape.bundled_cardboard") or 
            k.startswith("item.buildscape.bundled_cardboard") or 
            k.startswith("block.buildscape.stripped_bundled_cardboard") or 
            k.startswith("item.buildscape.stripped_bundled_cardboard") or 
            k.startswith("block.buildscape.tinted_bundled_cardboard") or 
            k.startswith("item.buildscape.tinted_bundled_cardboard") or 
            k.startswith("block.buildscape.washed_bundled_cardboard") or 
            k.startswith("item.buildscape.washed_bundled_cardboard") or 
            k.startswith("block.buildscape.burnt_bundled_cardboard") or 
            k.startswith("item.buildscape.burnt_bundled_cardboard") or
            k.startswith("block.buildscape.pressed_cardboard") or 
            k.startswith("item.buildscape.pressed_cardboard") or 
            k.startswith("block.buildscape.stripped_pressed_cardboard") or 
            k.startswith("item.buildscape.stripped_pressed_cardboard") or 
            k.startswith("block.buildscape.tinted_pressed_cardboard") or 
            k.startswith("item.buildscape.tinted_pressed_cardboard") or 
            k.startswith("block.buildscape.washed_pressed_cardboard") or 
            k.startswith("item.buildscape.washed_pressed_cardboard") or 
            k.startswith("block.buildscape.burnt_pressed_cardboard") or 
            k.startswith("item.buildscape.burnt_pressed_cardboard") or
            k.startswith("block.buildscape.thick_cardboard") or 
            k.startswith("item.buildscape.thick_cardboard") or 
            k.startswith("block.buildscape.stripped_thick_cardboard") or 
            k.startswith("item.buildscape.stripped_thick_cardboard") or 
            k.startswith("block.buildscape.tinted_thick_cardboard") or 
            k.startswith("item.buildscape.tinted_thick_cardboard") or 
            k.startswith("block.buildscape.washed_thick_cardboard") or 
            k.startswith("item.buildscape.washed_thick_cardboard") or 
            k.startswith("block.buildscape.burnt_thick_cardboard") or 
            k.startswith("item.buildscape.burnt_thick_cardboard")):
        clean_lang[k] = v

with open("scratch/generated_lang.txt", "r", encoding="utf-8") as f:
    new_lang = json.load(f)

clean_lang.update(new_lang)

with open(LANG_PATH, "w", encoding="utf-8") as f:
    json.dump(clean_lang, f, indent=2, ensure_ascii=False)
print(" - Translations integrated successfully.")

# 4. Integrate Crafting Recipes (Shaped)
print("Integrating Crafting Shaped Recipes...")
with open(CRAFTING_PATH, "r", encoding="utf-8") as f:
    crafting = json.load(f)

new_crafting = []
with open("scratch/generated_recipes_crafting.txt", "r", encoding="utf-8") as f:
    for line in f:
        line = line.strip()
        if line.endswith(","):
            line = line[:-1]
        if line:
            new_crafting.append(json.loads(line))

# Clean previous shaped
crafting["crafting_shaped"] = clean_recipes(crafting["crafting_shaped"])
# Add new
crafting["crafting_shaped"].extend(new_crafting)

# 4b. Integrate Crafting Recipes (Shapeless)
print("Integrating Crafting Shapeless Recipes...")
new_crafting_shapeless = []
with open("scratch/generated_recipes_crafting_shapeless.txt", "r", encoding="utf-8") as f:
    for line in f:
        line = line.strip()
        if line.endswith(","):
            line = line[:-1]
        if line:
            new_crafting_shapeless.append(json.loads(line))

# Clean previous shapeless
crafting["crafting_shapeless"] = clean_recipes(crafting["crafting_shapeless"])
# Add new
crafting["crafting_shapeless"].extend(new_crafting_shapeless)

with open(CRAFTING_PATH, "w", encoding="utf-8") as f:
    json.dump(crafting, f, indent=2, ensure_ascii=False)
print(" - Crafting recipes integrated successfully.")

# 5. Integrate Stonecutting Recipes
print("Integrating Stonecutting Recipes...")
with open(STONECUTTING_PATH, "r", encoding="utf-8") as f:
    stonecutting = json.load(f)

new_stonecutting = []
with open("scratch/generated_recipes_stonecutting.txt", "r", encoding="utf-8") as f:
    for line in f:
        line = line.strip()
        if line.endswith(","):
            line = line[:-1]
        if line:
            new_stonecutting.append(json.loads(line))

# Clean previous
stonecutting["stonecutting"] = clean_recipes(stonecutting["stonecutting"])
# Add new
stonecutting["stonecutting"].extend(new_stonecutting)

with open(STONECUTTING_PATH, "w", encoding="utf-8") as f:
    json.dump(stonecutting, f, indent=2, ensure_ascii=False)
print(" - Stonecutting recipes integrated successfully.")

# 6. Integrate Smelting Recipes
print("Integrating Smelting Recipes...")
with open(SMELTING_PATH, "r", encoding="utf-8") as f:
    smelting = json.load(f)

new_smelting = []
with open("scratch/generated_recipes_smelting.txt", "r", encoding="utf-8") as f:
    for line in f:
        line = line.strip()
        if line.endswith(","):
            line = line[:-1]
        if line:
            new_smelting.append(json.loads(line))

# Clean previous
smelting["smelting"] = clean_recipes(smelting["smelting"])
# Add new
smelting["smelting"].extend(new_smelting)

with open(SMELTING_PATH, "w", encoding="utf-8") as f:
    json.dump(smelting, f, indent=2, ensure_ascii=False)
print(" - Smelting recipes integrated successfully.")

print("All integrations completed successfully!")
