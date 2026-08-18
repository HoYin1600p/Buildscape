import json
import os

TAG_PATH = "src/main/resources/data/minecraft/tags/blocks/wool.json"

COLORS = [
    "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
    "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
]

with open(TAG_PATH, "r", encoding="utf-8") as f:
    data = json.load(f)

values = set(data.get("values", []))

for color in COLORS:
    values.add(f"buildscape:{color}_carpet_layers")
    values.add(f"buildscape:{color}_layered_wool_layers")

data["values"] = sorted(list(values))

with open(TAG_PATH, "w", encoding="utf-8") as f:
    json.dump(data, f, indent=2)

print("Layered wool block IDs successfully added to minecraft:wool block tag!")
