import json
from pathlib import Path
import tempfile
import unittest

import dumpdiff


class DumpDiffTests(unittest.TestCase):
    def test_objects_ignore_order_lists_preserve_order_and_types(self):
        self.assertEqual([], dumpdiff.differences({'a': 1, 'b': 2}, {'b': 2, 'a': 1}))
        self.assertEqual('/0', dumpdiff.differences(['a', 'b'], ['b', 'a'])[0]['path'])
        self.assertTrue(dumpdiff.differences(True, 1))
        self.assertEqual('/a~1b~0c', dumpdiff.differences({'a/b~c': 1}, {})[0]['path'])

    def test_complete_folder_comparison_and_missing_file(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            for name in ('a', 'b'):
                folder = root / name
                folder.mkdir()
                (folder / 'manifest.json').write_text(json.dumps(
                    {'schema': 1, 'complete': True, 'files': ['items.json']}), encoding='utf-8')
                (folder / 'items.json').write_text('{"buildscape:x":{"stack":64}}', encoding='utf-8')
            self.assertTrue(dumpdiff.compare(root / 'a', root / 'b')['equal'])
            (root / 'b/items.json').write_text('{"buildscape:x":{"stack":16}}', encoding='utf-8')
            changes = dumpdiff.compare(root / 'a', root / 'b')['differences']['items.json']
            self.assertEqual('/buildscape:x/stack', changes[0]['path'])
            (root / 'b/items.json').unlink()
            with self.assertRaisesRegex(ValueError, 'Missing dump file'):
                dumpdiff.compare(root / 'a', root / 'b')

    def test_empty_and_failed_dumps_rejected(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            with self.assertRaises(ValueError):
                dumpdiff.load(root)
            (root / 'manifest.json').write_text('{"schema":1,"complete":false}', encoding='utf-8')
            with self.assertRaisesRegex(ValueError, 'Incomplete'):
                dumpdiff.load(root)


if __name__ == '__main__':
    unittest.main()
