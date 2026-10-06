#!/usr/bin/env python3
# Formats BDRE recipe JSON files into compact format single-line recipe entries.

import os
import sys
import json
import glob
import argparse
from pathlib import Path

PRETTY_TOP_LEVEL_KEYS = {
    "_comment",
    "aliases",
    "wood_families",
    "stone_families",
    "templates",
    "families",
}

def find_workspace_root() -> Path:
    current = Path(__file__).resolve().parent
    while current != current.parent:
        if (current / "src" / "main" / "resources" / "data" / "buildscape" / "recipes_pack").exists():
            return current
        if (current / "build.gradle").exists():
            return current
        current = current.parent
    return Path.cwd()

def compactize_content(json_str: str) -> str:
    data = json.loads(json_str)
    if not isinstance(data, dict):
        return json_str

    lines = ["{"]
    keys = list(data.keys())

    for k_idx, key in enumerate(keys):
        val = data[key]
        comma_end = "," if k_idx < len(keys) - 1 else ""

        if key in PRETTY_TOP_LEVEL_KEYS:
            formatted_val = json.dumps(val, indent=2, ensure_ascii=False)
            indented = "\n".join("  " + line if line else "" for line in formatted_val.split("\n"))
            lines.append(f'  "{key}": ' + indented.strip() + comma_end)
        elif isinstance(val, list):
            lines.append(f'  "{key}": [')
            for item_idx, item in enumerate(val):
                compact_item = json.dumps(item, separators=(",", ":"), ensure_ascii=False)
                comma = "," if item_idx < len(val) - 1 else ""
                lines.append(f"    {compact_item}{comma}")
            lines.append(f"  ]{comma_end}")
        else:
            formatted_val = json.dumps(val, separators=(",", ":"), ensure_ascii=False)
            lines.append(f'  "{key}": {formatted_val}{comma_end}')

    lines.append("}")
    return "\n".join(lines) + "\n"

def compactize_file(filepath: Path, check_only: bool = False) -> bool:
    try:
        with open(filepath, "r", encoding="utf-8") as f:
            original = f.read()

        compacted = compactize_content(original)

        if original == compacted:
            return False

        if not check_only:
            with open(filepath, "w", encoding="utf-8", newline="\n") as f:
                f.write(compacted)

        return True
    except Exception as e:
        print(f"Error processing {filepath}: {e}", file=sys.stderr)
        return False

def main():
    parser = argparse.ArgumentParser(description="Compactize BDRE Recipe Pack JSON files.")
    parser.add_argument(
        "paths",
        nargs="*",
        help="Optional specific file or directory paths to compactize.",
    )
    parser.add_argument(
        "--check",
        action="store_true",
        help="Check if files are compactized without modifying them.",
    )

    args = parser.parse_args()
    workspace_root = find_workspace_root()
    default_dir = workspace_root / "src" / "main" / "resources" / "data" / "buildscape" / "recipes_pack"

    target_files = []
    if args.paths:
        for p_str in args.paths:
            p = Path(p_str)
            if not p.is_absolute():
                p = (Path.cwd() / p).resolve()
            if p.is_file() and p.suffix == ".json":
                target_files.append(p)
            elif p.is_dir():
                target_files.extend(p.glob("*.json"))
            else:
                print(f"Warning: path not found or not json: {p_str}", file=sys.stderr)
    else:
        if default_dir.exists():
            target_files = sorted(default_dir.glob("*.json"))
        else:
            print(f"Recipe pack directory not found at {default_dir}", file=sys.stderr)
            sys.exit(1)

    if not target_files:
        print("No JSON files found to process.")
        sys.exit(0)

    changed_count = 0
    for fpath in target_files:
        changed = compactize_file(fpath, check_only=args.check)
        rel_path = fpath.relative_to(workspace_root) if fpath.is_relative_to(workspace_root) else fpath
        if changed:
            changed_count += 1
            action = "Needs compacting" if args.check else "Compactified"
            print(f"[{action}] {rel_path}")
        else:
            print(f"[OK] {rel_path}")

    print(f"\nSummary: {len(target_files)} files checked, {changed_count} {'would be modified' if args.check else 'modified'}.")

    if args.check and changed_count > 0:
        sys.exit(1)

if __name__ == "__main__":
    main()
