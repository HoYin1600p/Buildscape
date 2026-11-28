"""Generate ModVerticalStairs.java and template-based blockstate/model/item/loot/lang resources."""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SHARED_JAVA = ROOT / "shared" / "src" / "main" / "java" / "com" / "kingodogo" / "buildscape" / "block"
SHARED_RES = ROOT / "shared" / "src" / "main" / "resources"

STAIR_RE = re.compile(
    r'^\s*([A-Z0-9_]+_STAIRS)\s*=\s*reg\("([a-z0-9_]+_stairs)",\s*"([^"]+)",\s*(.+)\);\s*$'
)


def extract_textures(stair_id: str, base_hint: str) -> dict[str, str]:
    models_dir = SHARED_RES / "assets" / "buildscape" / "models" / "block"
    candidates = [models_dir / f"{stair_id}.json"]
    base_slug = stair_id[:-len("_stairs")]
    candidates.append(models_dir / f"{base_slug}_vertical_slab.json")
    candidates.append(models_dir / f"{base_slug}.json")
    if base_hint and base_hint not in ("null",):
        candidates.append(models_dir / f"{base_hint.strip('\"').lower()}.json")

    for cand in candidates:
        if cand.is_file():
            try:
                data = json.loads(cand.read_text(encoding="utf-8"))
                tex = data.get("textures", {})
                if isinstance(tex, dict) and tex:
                    side = tex.get("side") or tex.get("all") or tex.get("texture") or tex.get("top") or f"buildscape:block/{base_slug}"
                    top = tex.get("top") or tex.get("end") or side
                    bottom = tex.get("bottom") or tex.get("end") or top
                    return {"bottom": bottom, "top": top, "side": side, "particle": "#side"}
            except (json.JSONDecodeError, OSError, KeyError):
                continue

    fallback = f"buildscape:block/{base_slug}"
    return {"bottom": fallback, "top": fallback, "side": fallback, "particle": "#side"}


def main() -> None:
    mod_blocks_text = (SHARED_JAVA / "ModBlocks.java").read_text(encoding="utf-8")
    entries = []
    seen = set()
    for line in mod_blocks_text.splitlines():
        m = STAIR_RE.match(line)
        if not m:
            continue
        const_name, stair_id, stair_type, rest = m.groups()
        v_const = const_name[:-len("_STAIRS")] + "_VERTICAL_STAIRS"
        v_id = stair_id[:-len("_stairs")] + "_vertical_stairs"
        v_type = "WeatheringVerticalStairBlock" if stair_type == "WeatheringStairBlock" else "VerticalStairBlock"
        if v_id in seen:
            continue
        seen.add(v_id)
        base_hint = rest.split(",")[-1].strip()
        entries.append((v_const, v_id, stair_id, v_type, rest, base_hint))

    # 1. Write ModVerticalStairs.java
    java_lines = [
        "package com.kingodogo.buildscape.block;",
        "",
        "import com.kingodogo.buildscape.item.ModItems;",
        "import com.kingodogo.buildscape.registry.RegistrySupplier;",
        "",
        "/** Separate registry class for vertical stair blocks and items derived from registered stairs. */",
        "public final class ModVerticalStairs {",
        "    private ModVerticalStairs() {}",
        "",
    ]
    for v_const, v_id, _, v_type, rest, _ in entries:
        java_lines.append(
            f'    public static final RegistrySupplier<BlockDefinition> {v_const} = ModBlocks.reg("{v_id}", "{v_type}", {rest});'
        )
    java_lines.extend([
        "",
        "    private static final String[] ITEM_IDS = {",
        *[f'        "{v_id}",' for _, v_id, _, _, _, _ in entries],
        "    };",
        "",
        "    public static void init() {}",
        "",
        "    public static void initItems() {",
        "        for (String id : ITEM_IDS) {",
        "            ModItems.register(id);",
        "        }",
        "    }",
        "}",
    ])
    (SHARED_JAVA / "ModVerticalStairs.java").write_text("\n".join(java_lines) + "\n", encoding="utf-8")

    # 2. Generate template-based JSONs
    bs_dir = SHARED_RES / "assets" / "buildscape" / "blockstates"
    mb_dir = SHARED_RES / "assets" / "buildscape" / "models" / "block"
    mi_dir = SHARED_RES / "assets" / "buildscape" / "models" / "item"
    it_dir = SHARED_RES / "assets" / "buildscape" / "items"
    lt_dir = SHARED_RES / "data" / "buildscape" / "loot_table" / "blocks"
    for d in (bs_dir, mb_dir, mi_dir, it_dir, lt_dir):
        d.mkdir(parents=True, exist_ok=True)

    lang_path = SHARED_RES / "assets" / "buildscape" / "lang" / "en_us.json"
    lang = json.loads(lang_path.read_text(encoding="utf-8"))

    for _, v_id, stair_id, _, _, base_hint in entries:
        bs_json = {
            "variants": {
                "facing=north": {"model": f"buildscape:block/{v_id}"},
                "facing=east": {"model": f"buildscape:block/{v_id}", "y": 90},
                "facing=south": {"model": f"buildscape:block/{v_id}", "y": 180},
                "facing=west": {"model": f"buildscape:block/{v_id}", "y": 270},
            }
        }
        (bs_dir / f"{v_id}.json").write_text(json.dumps(bs_json, indent=2) + "\n", encoding="utf-8")

        mb_json = {
            "parent": "buildscape:block/template_vertical_stair",
            "textures": extract_textures(stair_id, base_hint),
        }
        (mb_dir / f"{v_id}.json").write_text(json.dumps(mb_json, indent=2) + "\n", encoding="utf-8")

        mi_json = {"parent": f"buildscape:block/{v_id}"}
        (mi_dir / f"{v_id}.json").write_text(json.dumps(mi_json, indent=2) + "\n", encoding="utf-8")

        it_json = {"model": {"type": "minecraft:model", "model": f"buildscape:item/{v_id}"}}
        (it_dir / f"{v_id}.json").write_text(json.dumps(it_json, indent=2) + "\n", encoding="utf-8")

        lt_json = {
            "type": "minecraft:block",
            "pools": [
                {
                    "rolls": 1.0,
                    "bonus_rolls": 0.0,
                    "entries": [{"type": "minecraft:item", "name": f"buildscape:{v_id}"}],
                    "conditions": [{"condition": "minecraft:survives_explosion"}],
                }
            ],
        }
        (lt_dir / f"{v_id}.json").write_text(json.dumps(lt_json, indent=2) + "\n", encoding="utf-8")

        stair_key = f"block.buildscape.{stair_id}"
        v_key = f"block.buildscape.{v_id}"
        if v_key not in lang:
            if stair_key in lang and lang[stair_key].endswith(" Stairs"):
                lang[v_key] = lang[stair_key][:-len(" Stairs")] + " Vertical Stairs"
            else:
                lang[v_key] = v_id.replace("_", " ").title()

    lang_path.write_text(json.dumps(lang, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    print(f"Generated ModVerticalStairs.java and template resources for {len(entries)} vertical stairs.")


if __name__ == "__main__":
    main()
