"""Run focused conversion tests, then validate the actual shared resource tree."""
import sys
import unittest
from pathlib import Path

import validate


def main():
    suite = unittest.defaultTestLoader.discover(str(Path(__file__).parent),
                                               pattern="test_*.py")
    result = unittest.TextTestRunner(verbosity=2).run(suite)
    if not result.wasSuccessful():
        return 1
    return validate.main()


if __name__ == "__main__":
    sys.dont_write_bytecode = True
    raise SystemExit(main())
