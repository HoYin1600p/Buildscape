"""Compare jar entries and resources; class byte differences are informational.

Exit 0: parity, 1: differences, 2: bad input.
"""
import argparse
import json
from pathlib import Path
import sys
import zipfile

TEXT_TYPES = {'.json', '.cfg', '.toml', '.mcmeta', '.txt', '.lang'}


def load(path):
    entries = {}
    with zipfile.ZipFile(path) as jar:
        for info in jar.infolist():
            if info.is_dir():
                continue
            if info.filename in entries:
                raise ValueError(f'{path}: duplicate entry {info.filename}')
            entries[info.filename] = jar.read(info)
    return entries


def manifest(data):
    """Unfold manifest attributes; ignore timestamp attributes, retain sections."""
    lines = []
    for line in data.decode('utf-8').replace('\r\n', '\n').split('\n'):
        if line.startswith(' '):
            if not lines or not lines[-1]:
                raise ValueError('Invalid manifest continuation')
            lines[-1] += line[1:]
        else:
            lines.append(line)
    sections, section = [], {}
    for line in lines + ['']:
        if not line:
            if section:
                sections.append(section)
                section = {}
            continue
        key, separator, value = line.partition(': ')
        if not separator:
            raise ValueError(f'Invalid manifest attribute: {line}')
        key = key.lower()
        if 'timestamp' not in key:
            if key in section:
                raise ValueError(f'Duplicate manifest attribute: {key}')
            section[key] = value
    return sections


def normalized(name, data):
    if name.upper() == 'META-INF/MANIFEST.MF':
        return manifest(data)
    if name.lower().endswith('.refmap.json'):
        refmap = json.loads(data)
        if not isinstance(refmap, dict) or not isinstance(refmap.get('mappings'), dict):
            raise ValueError(f'{name}: missing refmap mappings object')
        return refmap['mappings']
    if Path(name).suffix.lower() in TEXT_TYPES:
        return data.replace(b'\r\n', b'\n').rstrip(b'\n')
    return data


def compare(a_path, b_path):
    a, b = load(a_path), load(b_path)
    ca = {name for name in a if name.endswith('.class')}
    cb = {name for name in b if name.endswith('.class')}
    differences, ignored = [], []
    for name in sorted(a.keys() & b.keys()):
        if name.endswith('.class'):
            continue
        # Validate special formats even when their raw bytes are identical.
        if normalized(name, a[name]) != normalized(name, b[name]):
            differences.append(name)
        elif a[name] != b[name]:
            ignored.append(name)
    only_a, only_b = sorted(a.keys() - b.keys()), sorted(b.keys() - a.keys())
    return {
        'equal': not (only_a or only_b or differences),
        'only_a': only_a, 'only_b': only_b,
        'class_names_equal': ca == cb,
        'classes_only_a': sorted(ca - cb), 'classes_only_b': sorted(cb - ca),
        'classes_different_bytes': sorted(n for n in ca & cb if a[n] != b[n]),
        'non_class_differences': differences, 'normalized_equal': ignored,
        'entry_count_a': len(a), 'entry_count_b': len(b),
    }


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('a', type=Path)
    parser.add_argument('b', type=Path)
    parser.add_argument('--json', type=Path, metavar='OUT')
    args = parser.parse_args(argv)
    try:
        result = compare(args.a, args.b)
        if args.json:
            args.json.write_text(json.dumps(result, indent=2, sort_keys=True) + '\n', encoding='utf-8')
    except (OSError, ValueError, zipfile.BadZipFile, RuntimeError) as error:
        print(f'jardiff: {error}', file=sys.stderr)
        return 2
    print(f'A={args.a} ({result["entry_count_a"]} entries)')
    print(f'B={args.b} ({result["entry_count_b"]} entries)')
    for key in ('only_a', 'only_b', 'non_class_differences', 'normalized_equal'):
        print(f'{key} ({len(result[key])}):')
        for name in result[key]:
            print(f'  {name}')
    print(f'Class name sets equal: {result["class_names_equal"]}')
    print(f'Classes with different bytes (informational): {len(result["classes_different_bytes"])}')
    print('PASS' if result['equal'] else 'FAIL')
    return 0 if result['equal'] else 1


if __name__ == '__main__':
    sys.exit(main())
