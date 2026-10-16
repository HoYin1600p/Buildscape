"""Run converter tests and offline 1.21.1 model, subtitle and data checks."""
import sys
import unittest
from pathlib import Path

sys.dont_write_bytecode = True
import validate


def main():
    suite = unittest.defaultTestLoader.discover(str(Path(__file__).parent), pattern="test_*.py")
    if not unittest.TextTestRunner(verbosity=2).run(suite).wasSuccessful():
        return 1
    return validate.main()


if __name__ == "__main__":
    raise SystemExit(main())
