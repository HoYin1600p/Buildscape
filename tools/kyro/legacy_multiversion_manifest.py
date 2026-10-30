#!/usr/bin/env python3
"""Compare a legacy single-version tree with a Buildscape version overlay.

The report is intentionally path-based: it identifies which legacy files changed
since a supplied base revision and whether a same-path target currently exists in
the selected version overlay or shared source tree.  It is an inventory aid, not
an automatic porting tool.
"""

from __future__ import annotations

import argparse
import hashlib
import io
import json
import subprocess
import tarfile
from pathlib import Path


SOURCE_ROOTS = (
    ("src/main/java", "common/src/{version}/java", "common/src/main/java"),
    ("src/main/resources", "common/src/{version}/resources", "common/src/main/resources"),
    ("src/test/java", "common/src/{version}Test/java", "common/src/test/java"),
    ("src/test/resources", "common/src/{version}Test/resources", "common/src/test/resources"),
)


def git(repo: Path, *arguments: str) -> str:
    return subprocess.check_output(["git", *arguments], cwd=repo, text=True)


def archived_sources(repo: Path, revision: str) -> dict[str, bytes]:
    roots = [root for root, _, _ in SOURCE_ROOTS]
    archive = subprocess.check_output(
        ["git", "archive", "--format=tar", revision, "--", *roots], cwd=repo
    )
    with tarfile.open(fileobj=io.BytesIO(archive)) as contents:
        return {
            member.name: contents.extractfile(member).read()
            for member in contents.getmembers()
            if member.isfile()
        }


def destination_for(
    repo: Path,
    relative: str,
    overlay_root: str,
    shared_root: str,
    names: dict[str, list[Path]],
) -> tuple[Path | None, str]:
    for root in (overlay_root, shared_root):
        candidate = repo / root / relative
        if candidate.is_file():
            return candidate, "direct"
    relocated = names.get(Path(relative).name, [])
    if len(relocated) == 1:
        return relocated[0], "relocated"
    return None, "none"


def target_names(repo: Path, overlay_root: str, shared_root: str) -> dict[str, list[Path]]:
    index: dict[str, list[Path]] = {}
    for root in (repo / overlay_root, repo / shared_root):
        if root.is_dir():
            for candidate in root.rglob("*"):
                if candidate.is_file():
                    index.setdefault(candidate.name, []).append(candidate)
    return index


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--repo", type=Path, default=Path.cwd())
    parser.add_argument("--base-ref", required=True, help="legacy common ancestor")
    parser.add_argument("--legacy-ref", required=True, help="single-version source revision")
    parser.add_argument("--version", default="1.18.2", help="target version overlay")
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()

    repo = args.repo.resolve()
    changed = set(git(repo, "diff", "--no-renames", "--name-only", f"{args.base_ref}..{args.legacy_ref}").splitlines())
    legacy_tree = set(git(repo, "ls-tree", "-r", "--name-only", args.legacy_ref).splitlines())
    legacy_contents = archived_sources(repo, args.legacy_ref)
    records: list[dict[str, str]] = []

    for legacy_root, overlay_pattern, shared_root in SOURCE_ROOTS:
        overlay_root = overlay_pattern.format(version=args.version)
        names = target_names(repo, overlay_root, shared_root)
        prefix = f"{legacy_root}/"
        for legacy_path in sorted(path for path in changed if path.startswith(prefix) and path in legacy_tree):
            relative = legacy_path.removeprefix(prefix)
            destination, target_kind = destination_for(repo, relative, overlay_root, shared_root, names)
            source_digest = hashlib.sha256(legacy_contents[legacy_path]).hexdigest()
            if destination is None:
                state = "missing"
                target_path = ""
            else:
                target_path = destination.relative_to(repo).as_posix()
                target_digest = hashlib.sha256(destination.read_bytes()).hexdigest()
                state = "identical" if source_digest == target_digest else "different"
            records.append(
                {
                    "legacyPath": legacy_path,
                    "targetPath": target_path,
                    "targetKind": target_kind,
                    "area": legacy_root,
                    "state": state,
                }
            )

    summary = {state: sum(record["state"] == state for record in records) for state in ("identical", "different", "missing")}
    payload = {
        "baseRef": args.base_ref,
        "legacyRef": args.legacy_ref,
        "version": args.version,
        "summary": summary,
        "records": records,
    }
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(payload, indent=2) + "\n", encoding="utf-8")
    print(json.dumps(summary, sort_keys=True))


if __name__ == "__main__":
    main()
