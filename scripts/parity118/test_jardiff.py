import contextlib
import io
import json
from pathlib import Path
import tempfile
import unittest
import warnings
import zipfile

import jardiff


class JarDiffTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)

    def jar(self, name, entries):
        path = self.root / name
        with zipfile.ZipFile(path, 'w') as archive:
            archive.writestr('empty/', b'')
            for entry, data in entries.items():
                archive.writestr(entry, data)
        return path

    def compare(self, a, b):
        return jardiff.compare(self.jar('a.jar', a), self.jar('b.jar', b))

    def test_class_bytes_are_informational_but_names_must_match(self):
        result = self.compare({'x.class': b'a'}, {'x.class': b'b'})
        self.assertTrue(result['equal'])
        self.assertEqual(['x.class'], result['classes_different_bytes'])
        result = self.compare({'x.class': b'a'}, {'y.class': b'a'})
        self.assertFalse(result['equal'])
        self.assertFalse(result['class_names_equal'])
        self.assertEqual(['x.class'], result['classes_only_a'])
        self.assertEqual(['y.class'], result['only_b'])

    def test_text_line_endings_and_trailing_newlines(self):
        for suffix in jardiff.TEXT_TYPES:
            with self.subTest(suffix=suffix):
                self.assertTrue(self.compare({'x' + suffix: b'a\r\nb\r\n\r\n'},
                                             {'x' + suffix: b'a\nb'})['equal'])
        self.assertFalse(self.compare({'x.txt': b'a '}, {'x.txt': b'a'})['equal'])
        self.assertFalse(self.compare({'x.txt': b'a\rb'}, {'x.txt': b'a\nb'})['equal'])

    def test_binary_resources_are_exact(self):
        result = self.compare({'x.bscb': b'x\r\n', 'x.png': b'\0\n'},
                              {'x.bscb': b'x\n', 'x.png': b'\0'})
        self.assertEqual(['x.bscb', 'x.png'], result['non_class_differences'])

    def test_manifest_timestamps_folded_lines_and_metadata(self):
        a = b'Manifest-Version: 1.0\r\nImplementation-Timestamp: yesterday\r\n folded\r\nImplementation-Version: 4.0.3\r\n\r\nName: x\r\nSHA-256-Digest: same\r\n'
        b = b'Manifest-Version: 1.0\nImplementation-Timestamp: today\nImplementation-Version: 4.0.3\n\nName: x\nSHA-256-Digest: same\n'
        self.assertTrue(self.compare({'META-INF/MANIFEST.MF': a}, {'META-INF/MANIFEST.MF': b})['equal'])
        self.assertFalse(self.compare({'META-INF/MANIFEST.MF': a},
                                      {'META-INF/MANIFEST.MF': b.replace(b'4.0.3', b'4.0.4')})['equal'])
        self.assertFalse(self.compare({'META-INF/MANIFEST.MF': a},
                                      {'META-INF/MANIFEST.MF': b.replace(b'Digest: same', b'Digest: different')})['equal'])

    def test_refmap_compares_only_mappings_semantically(self):
        a = b'{"mappings":{"X":{"b":"B","a":"A"}},"data":{"searge":{}}}'
        b = b'{"data":{}, "mappings":{"X":{"a":"A","b":"B"}}}'
        name = 'buildscape.refmap.json'
        self.assertTrue(self.compare({name: a}, {name: b})['equal'])
        self.assertFalse(self.compare({name: a}, {name: b.replace(b'"B"', b'"C"')})['equal'])
        for malformed in (b'{}', b'bad', b'{"mappings":null}'):
            with self.assertRaises(ValueError):
                self.compare({name: malformed}, {name: malformed})

    def test_entry_additions_sorted_and_directories_ignored(self):
        result = self.compare({'z.txt': b'x', 'a.txt': b'x'}, {'b.txt': b'x'})
        self.assertEqual(['a.txt', 'z.txt'], result['only_a'])
        self.assertEqual(2, result['entry_count_a'])

    def test_duplicate_entries_are_rejected(self):
        a = self.jar('a.jar', {'x': b'1'})
        with warnings.catch_warnings():
            warnings.simplefilter('ignore', UserWarning)
            with zipfile.ZipFile(a, 'a') as archive:
                archive.writestr('x', b'2')
        with self.assertRaisesRegex(ValueError, 'duplicate entry'):
            jardiff.load(a)

    def test_cli_json_and_exit_codes(self):
        a, b = self.jar('a.jar', {'x.txt': b'a'}), self.jar('b.jar', {'x.txt': b'b'})
        out = self.root / 'report.json'
        with contextlib.redirect_stdout(io.StringIO()), contextlib.redirect_stderr(io.StringIO()):
            self.assertEqual(1, jardiff.main([str(a), str(b), '--json', str(out)]))
            self.assertEqual(0, jardiff.main([str(a), str(a)]))
            self.assertEqual(2, jardiff.main([str(a), str(self.root / 'absent')]))
        self.assertFalse(json.loads(out.read_text())['equal'])


if __name__ == '__main__':
    unittest.main()
