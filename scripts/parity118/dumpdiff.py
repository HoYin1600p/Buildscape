"""Compare deterministic parity-dumper folders (server/ or client/).

JSON object order is immaterial; list order, types and all values are significant.
Exit 0: equal complete dumps, 1: differences, 2: invalid or incomplete dumps.
"""
import argparse
import json
from pathlib import Path
import sys


def load(folder):
    if not folder.is_dir():
        raise ValueError(f'Not a dump directory: {folder}')
    files = {p.relative_to(folder).as_posix(): json.loads(p.read_text(encoding='utf-8'))
             for p in sorted(folder.rglob('*.json'))}
    if not files:
        raise ValueError(f'No JSON dumps in {folder}')
    manifests = [name for name in files if Path(name).name == 'manifest.json']
    if not manifests:
        raise ValueError(f'Missing manifest.json in {folder}')
    for name in manifests:
        record = files[name]
        if record.get('schema') != 1 or record.get('complete') is not True:
            raise ValueError(f'Incomplete/unsupported dump: {folder / name}')
        parent = Path(name).parent
        expected = record.get('files')
        if not isinstance(expected, list) or not expected:
            raise ValueError(f'Missing file inventory: {folder / name}')
        for entry in expected:
            if (parent / entry).as_posix() not in files:
                raise ValueError(f'Missing dump file: {folder / parent / entry}')
    return files


def differences(a, b, path=''):
    """Return changes at JSON pointers, preserving creative-tab/list order."""
    if type(a) is not type(b):
        return [{'path': path, 'a': a, 'b': b}]
    if isinstance(a, dict):
        out = []
        for key in sorted(a.keys() | b.keys()):
            child = path + '/' + key.replace('~', '~0').replace('/', '~1')
            if key not in a:
                out.append({'path': child, 'only': 'b', 'value': b[key]})
            elif key not in b:
                out.append({'path': child, 'only': 'a', 'value': a[key]})
            else:
                out.extend(differences(a[key], b[key], child))
        return out
    if isinstance(a, list):
        out = []
        for index in range(max(len(a), len(b))):
            child = f'{path}/{index}'
            if index >= len(a):
                out.append({'path': child, 'only': 'b', 'value': b[index]})
            elif index >= len(b):
                out.append({'path': child, 'only': 'a', 'value': a[index]})
            else:
                out.extend(differences(a[index], b[index], child))
        return out
    return [] if a == b else [{'path': path, 'a': a, 'b': b}]


def compare(a, b):
    left, right = load(Path(a)), load(Path(b))
    changed = {name: differences(left[name], right[name]) for name in sorted(left.keys() & right.keys())}
    changed = {name: changes for name, changes in changed.items() if changes}
    only_a, only_b = sorted(left.keys() - right.keys()), sorted(right.keys() - left.keys())
    return {'equal': not (only_a or only_b or changed), 'only_a': only_a,
            'only_b': only_b, 'differences': changed}


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
    except (OSError, ValueError, AttributeError, TypeError) as error:
        print(f'dumpdiff: {error}', file=sys.stderr)
        return 2
    print(json.dumps(result, indent=2, sort_keys=True))
    return 0 if result['equal'] else 1


if __name__ == '__main__':
    sys.exit(main())
