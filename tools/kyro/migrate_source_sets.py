#!/usr/bin/env python3
"""Move Buildscape version source trees into the version-directory layout.

The first phase intentionally preserves Java packages.  It only relocates files
and is therefore safe to run before package-by-package adapter cleanup.
"""

from __future__ import annotations

import argparse
import subprocess
from pathlib import Path


VERSIONS = {
    "v118x": "1.18.2",
    "v121x": "1.21.1",
    "v26x": "26.2",
    "v263": "26.3",
}


def git_move(repo: Path, source: Path, target: Path, apply: bool) -> None:
    if not source.exists():
        return
    if target.exists():
        raise RuntimeError(f"refusing to overwrite existing target: {target}")
    print(f"MOVE {source.relative_to(repo)} -> {target.relative_to(repo)}")
    if apply:
        target.parent.mkdir(parents=True, exist_ok=True)
        subprocess.run(["git", "mv", "--", str(source), str(target)], cwd=repo, check=True)


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--repo", type=Path, default=Path.cwd())
    parser.add_argument("--apply", action="store_true", help="perform moves; default is a preview")
    args = parser.parse_args()
    repo = args.repo.resolve()
    common = repo / "common" / "src"

    for source_set, version in VERSIONS.items():
        git_move(repo, common / source_set / "java", common / version / "java", args.apply)
        git_move(repo, common / source_set / "resources", common / version / "resources", args.apply)
        git_move(
            repo,
            common / "main" / "java" / "com" / "kingodogo" / "buildscape" / "adapter" / source_set,
            common / version / "java" / "com" / "kingodogo" / "buildscape" / "adapter" / source_set,
            args.apply,
        )

    if not args.apply:
        print("Preview only. Re-run with --apply after updating common/build.gradle.")


if __name__ == "__main__":
    main()
