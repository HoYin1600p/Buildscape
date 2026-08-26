import json
import os
import glob

def compactify_file(filepath):
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            data = json.load(f)

        lines = ['{']
        keys = list(data.keys())

        for k_idx, key in enumerate(keys):
            val = data[key]
            comma_end = ',' if k_idx < len(keys) - 1 else ''

            if key in ['_comment', 'aliases', 'wood_families']:
                formatted_val = json.dumps(val, indent=2)
                indented = '\n'.join('  ' + line if line else '' for line in formatted_val.split('\n'))
                lines.append(f'  "{key}": ' + indented.strip() + comma_end)
            elif isinstance(val, list):
                lines.append(f'  "{key}": [')
                for item_idx, item in enumerate(val):
                    compact_item = json.dumps(item, separators=(',', ':'))
                    comma = ',' if item_idx < len(val) - 1 else ''
                    lines.append(f'    {compact_item}{comma}')
                lines.append(f'  ]{comma_end}')
            else:
                formatted_val = json.dumps(val, separators=(',', ':'))
                lines.append(f'  "{key}": {formatted_val}{comma_end}')

        lines.append('}')
        content = '\n'.join(lines) + '\n'

        with open(filepath, 'w', encoding='utf-8', newline='\n') as f:
            f.write(content)

        print(f"Successfully compactified {filepath} ({len(lines)} lines)")
    except Exception as e:
        print(f"Error compactifying {filepath}: {e}")

if __name__ == '__main__':
    recipe_dir = os.path.join('src', 'main', 'resources', 'data', 'buildscape', 'recipes_pack')
    json_files = glob.glob(os.path.join(recipe_dir, '*.json'))
    for fpath in json_files:
        compactify_file(fpath)
