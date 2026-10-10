"""Offline integration tests; every Git mutation is confined to temporary repos."""
import csv
import io
import json
import os
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest
from unittest import mock

import sync


class SyncTests(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory(prefix="sync test's ")
        self.addCleanup(self.tmp.cleanup)
        self.root = Path(self.tmp.name)
        self.source = self.root / "source repo's"
        self.repo = self.root / "target repo's"
        self.output = self.root / "reports"
        for root in (self.source, self.repo):
            root.mkdir()
            self.git(root, "init", "-b", "main")
            self.git(root, "config", "user.name", "HoY")
            self.git(root, "config", "user.email", "hoyin1600p@gmail.com")
            self.git(root, "config", "core.autocrlf", "false")
        self.java = "src/main/java/example/"
        self.resources = "src/main/resources/"
        for name in ("Same", "Mapped", "Fallback", "Deleted", "Rename"):
            self.write(self.source, self.java + name + ".java", "class " + name + " {}\n")
        self.write(self.source, self.resources + "data/old.json", '{"value": 1}\n')
        self.write(self.source, self.resources + "data/deleted.json", '{"removed": true}\n')
        self.write(self.source, self.resources + "data/rename.json", '{"renamed": true}\n')
        self.write(self.source, self.resources + "META-INF/mods.toml", 'modId="tiny"\n')
        self.write(self.source, self.resources + "tiny.mixins.json", '{}\n')
        self.write(self.source, self.resources + "META-INF/accesstransformer.cfg", 'public Thing\n')
        self.write(self.source, "build.gradle", "// original\n")
        self.write(self.source, "gradle.properties", "version=1\n")
        self.base = self.commit(self.source, "Source baseline")
        for path in sync.files_at(self.source, self.base, ["src", "build.gradle", "gradle.properties"]):
            data = sync.blob(self.source, self.base, path)
            self.write(self.repo, "ref/source/" + path, data)
            if path.endswith(".java"):
                self.write(self.repo, "common/" + path, data)
            elif path.startswith(self.resources) and not sync.metadata(path):
                self.write(self.repo, "common/" + path, data)
        self.write(self.repo, "common/" + self.java + "Mapped.java", "class Mapped { /* adapter */ }\n")
        fields = ["kind", "reference_path", "status", "active_paths", "era_coverage", "behavior_notes", "verification"]
        self.rows = []
        for name in ("Same", "Mapped", "Deleted", "Rename"):
            path = self.java + name + ".java"
            self.rows.append(dict(zip(fields, ["java", path, "common", "common/" + path, "test", "baseline", "PASS"])))
        for name in ("old", "deleted", "rename"):
            path = self.resources + "data/" + name + ".json"
            self.rows.append(dict(zip(fields, ["resource", path, "common", "common/" + path, "test", "baseline", "PASS"])))
        ledger = io.StringIO(newline="")
        writer = csv.DictWriter(ledger, fields)
        writer.writeheader()
        writer.writerows(self.rows)
        self.write(self.repo, "migration/ledger.csv", ledger.getvalue())
        # Converter creates derived output, allowing the test to check more than a raw copy.
        self.write(self.repo, "tools/convert.py", """import json, sys
from pathlib import Path
root = Path(sys.argv[1])
for p in root.rglob('*.json'):
    data = json.loads(p.read_text())
    data['converted'] = True
    p.write_text(json.dumps(data) + '\\n')
""")
        for path in (self.repo / "common/src/main/resources").rglob("*.json"):
            data = json.loads(path.read_text())
            data["converted"] = True
            path.write_text(json.dumps(data) + "\n")
        self.write(self.repo, "tools/validate.py", """import json, sys
from pathlib import Path
root = Path(sys.argv[1])
for p in root.rglob('*.json'):
    assert json.loads(p.read_text())['converted'] is True
if (Path.cwd() / 'fail-validation').exists():
    raise SystemExit(3)
""")
        self.config = {"schema_version": 1,
            "source": {"remote_url": "https://invalid.example/no-network.git", "branch": "main",
                       "subtree": "src", "metadata_files": ["build.gradle", "gradle.properties"],
                       "minecraft": "test", "loader": "forge"},
            "targets": [{"id": "tiny", "status": "active", "branch": "port/test", "base_commit": self.base,
                         "minecraft": "test", "loaders": ["test"], "modules": ["common"],
                         "reference_snapshot": "ref/source", "mapping_file": "migration/ledger.csv",
                         "java_root": "common/src/main/java", "resource_root": "common/src/main/resources",
                         "state_file": "sync/state/tiny.json",
                         "converter": {"path": "tools/convert.py", "command": ["{python}", "-B", "{repo}/tools/convert.py", "{resources}"]},
                         "validator": {"path": "tools/validate.py"},
                         "validation": [["{python}", "-B", "{repo}/tools/validate.py", "{resources}"]]},
                        {"id": "future", "status": "planned", "base_commit": None}]}
        self.write(self.repo, "sync/targets.json", json.dumps(self.config))
        self.commit(self.repo, "Target baseline")
        self.git(self.repo, "branch", "port/test")

    def git(self, root, *args):
        result = subprocess.run(["git", *args], cwd=root, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        if result.returncode:
            self.fail(result.stderr.decode("utf-8", "replace"))
        return result.stdout.decode().strip()

    def write(self, root, path, content):
        dest = root / path
        dest.parent.mkdir(parents=True, exist_ok=True)
        dest.write_bytes(content if isinstance(content, bytes) else content.encode())

    def commit(self, root, message):
        self.git(root, "add", ".")
        self.git(root, "commit", "-m", message)
        return self.git(root, "rev-parse", "HEAD")

    def mutate_source(self):
        for name in ("Same", "Mapped", "Fallback"):
            self.write(self.source, self.java + name + ".java", "class " + name + " { int added; }\n")
        self.write(self.source, self.java + "New.java", "class New {}\n")
        (self.source / (self.java + "Deleted.java")).unlink()
        (self.source / (self.java + "Rename.java")).rename(self.source / (self.java + "Renamed.java"))
        self.write(self.source, self.resources + "data/old.json", '{"value": 2}\n')
        self.write(self.source, self.resources + "data/new.json", '{"value": 3}\n')
        (self.source / (self.resources + "data/deleted.json")).unlink()
        (self.source / (self.resources + "data/rename.json")).rename(self.source / (self.resources + "data/renamed.json"))
        self.write(self.source, self.resources + "META-INF/mods.toml", 'modId="new"\n')
        self.write(self.source, self.resources + "tiny.mixins.json", '{"client": []}\n')
        self.write(self.source, self.resources + "META-INF/accesstransformer.cfg", 'public Other\n')
        self.write(self.source, "build.gradle", "// changed\n")
        self.write(self.source, "gradle.properties", "version=2\n")
        return self.commit(self.source, "Feature update")

    def plan(self):
        return sync.make_plan(self.repo, self.config, "tiny", source_repo=self.source)

    def apply(self):
        plan, target, config, source = self.plan()
        sync.apply_plan(self.repo, plan, target, config, source, "sync/targets.json", self.output)
        return plan

    def read_csv(self, branch):
        return {r["reference_path"]: r for r in csv.DictReader(io.StringIO(sync.text(
            sync.blob(self.repo, branch, "migration/ledger.csv"))))}

    def test_all_classifications_and_change_types(self):
        head = self.mutate_source()
        plan, _, _, _ = self.plan()
        items = {c["path"]: c for c in plan["changes"]}
        for name in ("Same", "Fallback"):
            self.assertEqual(items[self.java + name + ".java"]["classification"], "automatic")
        self.assertEqual(items[self.java + "Mapped.java"]["classification"], "port-candidate")
        self.assertEqual(items[self.java + "New.java"]["classification"], "new-feature port-candidate")
        for path in ("build.gradle", "gradle.properties", self.resources + "META-INF/mods.toml",
                     self.resources + "tiny.mixins.json", self.resources + "META-INF/accesstransformer.cfg"):
            self.assertEqual(items[path]["classification"], "review")
        for name in ("old", "new", "renamed"):
            self.assertEqual(items[self.resources + "data/" + name + ".json"]["classification"], "automatic")
        self.assertEqual(items[self.java + "Deleted.java"]["classification"], "review")
        rename = items[self.java + "Renamed.java"]
        self.assertEqual(rename["change"], "renamed")
        self.assertEqual(rename["old_path"], self.java + "Rename.java")
        self.assertEqual(rename["classification"], "automatic")
        self.assertEqual(items[self.resources + "data/renamed.json"]["change"], "renamed")
        self.assertEqual(items[self.resources + "data/new.json"]["change"], "added")
        self.assertEqual(items[self.resources + "data/old.json"]["change"], "modified")
        self.assertEqual(items[self.resources + "data/deleted.json"]["change"], "deleted")
        self.assertEqual(plan["source_commit"], head)
        self.assertIn("int added", items[self.java + "Mapped.java"]["source_diff"])

    def test_apply_advances_base_and_refreshes_snapshot_without_push(self):
        head = self.mutate_source()
        target_before = self.git(self.repo, "rev-parse", "port/test")
        source_before = self.git(self.source, "rev-parse", "HEAD")
        origin_run = subprocess.run
        calls = []
        def record(args, **kwargs):
            calls.append(list(args))
            self.assertNotIn("push", args)
            return origin_run(args, **kwargs)
        with mock.patch("subprocess.run", side_effect=record):
            plan = self.apply()
        branch = plan["apply"]["branch"]
        self.assertEqual(plan["apply"]["status"], "complete")
        self.assertTrue(any("merge-file" in c for c in calls))
        updated = json.loads(sync.blob(self.repo, branch, "sync/targets.json"))
        self.assertEqual(updated["targets"][0]["base_commit"], head)
        self.assertEqual(self.git(self.repo, "rev-parse", "port/test"), target_before)
        self.assertEqual(self.git(self.repo, "symbolic-ref", "HEAD"), "refs/heads/main")
        self.assertEqual(self.git(self.source, "rev-parse", "HEAD"), source_before)
        self.assertEqual(sync.blob(self.repo, branch, "common/" + self.java + "Same.java"),
                         sync.blob(self.source, head, self.java + "Same.java"))
        self.assertIsNone(sync.blob(self.repo, branch, "common/" + self.java + "Rename.java", True))
        self.assertIsNotNone(sync.blob(self.repo, branch, "common/" + self.java + "Renamed.java"))
        self.assertIsNotNone(sync.blob(self.repo, branch, "common/" + self.java + "Deleted.java"))
        self.assertIsNone(sync.blob(self.repo, branch, "ref/source/" + self.java + "Deleted.java", True))
        self.assertIn(b"adapter", sync.blob(self.repo, branch, "common/" + self.java + "Mapped.java"))
        resource = json.loads(sync.blob(self.repo, branch, "common/" + self.resources + "data/old.json"))
        self.assertEqual(resource, {"value": 2, "converted": True})
        self.assertIsNone(sync.blob(self.repo, branch, "common/" + self.resources + "data/rename.json", True))
        self.assertIsNotNone(sync.blob(self.repo, branch, "common/" + self.resources + "data/renamed.json"))
        self.assertIsNotNone(sync.blob(self.repo, branch, "common/" + self.resources + "data/deleted.json"))
        rows = self.read_csv(branch)
        for name in ("Mapped", "New", "Deleted"):
            self.assertEqual(rows[self.java + name + ".java"]["status"], "needs_resync")
        self.assertIn(head, rows[self.resources + "data/old.json"]["verification"])
        self.assertEqual(rows[self.resources + "data/deleted.json"]["status"], "needs_resync")
        self.assertIn(self.java + "Renamed.java", rows)
        # All managed reference bytes match the new source, including metadata.
        for path, data in sync.files_at(self.source, head, ["src", "build.gradle", "gradle.properties"]).items():
            self.assertEqual(sync.blob(self.repo, branch, "ref/source/" + path), data)
        self.assertEqual(self.apply()["apply"]["status"], "already-created")

    def test_pending_survives_acceptance_and_next_baseline(self):
        self.mutate_source()
        first = self.apply()
        self.git(self.repo, "branch", "-f", "port/test", first["apply"]["commit"])
        second, target, _, _ = self.plan()
        self.assertEqual(second["changes"], [])
        self.assertTrue(second["pending"])
        self.assertEqual(sync.briefs(second, target, self.output / "briefs"), 2)
        tasks = [json.loads(p.read_text()) for p in (self.output / "briefs").glob("*.json")]
        self.assertEqual({t["mode"] for t in tasks}, {"review", "implement"})
        self.assertTrue(all("source_diff" in c for t in tasks for c in t["source_changes"]))
        for task in tasks:
            for path in task["context_paths"]:
                self.assertIsNotNone(sync.blob(self.repo, "port/test", path, True), path)
        self.write(self.source, "build.gradle", "// another source update\n")
        self.commit(self.source, "Metadata change")
        second = self.apply()
        state = json.loads(sync.blob(self.repo, second["apply"]["branch"], "sync/state/tiny.json"))
        self.assertEqual(len(state["pending"]), len(second["pending"]) + 1)
        self.assertTrue(any(c["path"] == self.java + "Mapped.java" for c in state["pending"]))

    def test_validation_failure_does_not_advance_or_commit(self):
        self.write(self.repo, "fail-validation", "fail\n")
        commit = self.commit(self.repo, "Failing validator input")
        self.git(self.repo, "branch", "-f", "port/test", commit)
        self.mutate_source()
        with self.assertRaisesRegex(sync.SyncError, "Validation/conversion failed"):
            self.apply()
        branch = "sync/tiny/" + self.git(self.source, "rev-parse", "HEAD")[:12]
        self.assertEqual(self.git(self.repo, "rev-parse", branch), commit)
        self.assertEqual(json.loads(sync.blob(self.repo, branch, "sync/targets.json"))["targets"][0]["base_commit"], self.base)
        report = json.loads(next(self.output.glob("*.json")).read_text())
        self.assertEqual(report["apply"]["status"], "failed")
        worktree_config = json.loads((Path(report["apply"]["worktree"]) / "sync/targets.json").read_text())
        self.assertEqual(worktree_config["targets"][0]["base_commit"], self.base)
        with self.assertRaisesRegex(sync.SyncError, "incomplete sync branch"):
            self.apply()

    def test_resource_conflict_keeps_baseline(self):
        self.write(self.repo, "common/" + self.resources + "data/old.json", '{"port_specific": true}\n')
        commit = self.commit(self.repo, "Port-specific resource")
        self.git(self.repo, "branch", "-f", "port/test", commit)
        self.mutate_source()
        with self.assertRaisesRegex(sync.SyncError, "conflicts with target edits"):
            self.apply()
        branch = "sync/tiny/" + self.git(self.source, "rev-parse", "HEAD")[:12]
        self.assertEqual(self.git(self.repo, "rev-parse", branch), commit)

    def test_status_plan_briefs_cli_and_planned_target_gate(self):
        self.mutate_source()
        tool = Path(sync.__file__).resolve()
        args = [sys.executable, "-B", tool, "--repo", self.repo, "--source-repo", self.source]
        result = sync.run([*args, "status", "--json"], self.repo)
        self.assertEqual(len(json.loads(result.stdout)), 1)
        sync.run([*args, "plan", "--target", "tiny", "--output-dir", self.output], self.repo)
        self.assertTrue(list(self.output.glob("*.md")))
        self.assertTrue(list(self.output.glob("*.json")))
        sync.run([*args, "briefs", "--target", "tiny", "--output-dir", self.output / "briefs"], self.repo)
        self.assertEqual(len(list((self.output / "briefs").glob("*.json"))), 2)
        for path in (self.output / "briefs").glob("*.json"):
            for context in json.loads(path.read_text())["context_paths"]:
                self.assertIsNotNone(sync.blob(self.repo, "port/test", context, True), context)
        result = sync.run([*args, "plan", "--target", "future"], self.repo, False)
        self.assertNotEqual(result.returncode, 0)
        self.assertIn(b"must be active", result.stderr)

    def test_no_change_apply_does_not_create_branch(self):
        plan = self.apply()
        self.assertEqual(plan["apply"]["status"], "up-to-date")
        self.assertEqual(self.git(self.repo, "branch", "--list", "sync/*"), "")

    def test_fetch_uses_fetched_tip_even_with_stale_local_main(self):
        self.mutate_source()
        # Local-only fake transport: test fetch without any network access.
        self.config["source"]["remote_url"] = str(self.source)
        self.git(self.repo, "fetch", str(self.source), f"{self.base}:refs/heads/main-source")
        self.config["source"]["branch"] = "main-source"
        self.git(self.source, "branch", "main-source", "main")
        _, fetched = sync.source_revision(self.repo, self.config, fetch=True)
        plan, _, _, _ = sync.make_plan(self.repo, self.config, "tiny", source_head=fetched)
        self.assertEqual(plan["source_commit"], self.git(self.source, "rev-parse", "HEAD"))
        self.assertEqual(self.git(self.repo, "rev-parse", "main-source"), self.base)

    def test_unsafe_paths_and_non_ancestor_base_rejected(self):
        for path in ("../outside", "C:/outside", "/outside", "bad\\path"):
            with self.assertRaises(sync.SyncError):
                sync.relative(path)
        # Target baseline is deliberately not a commit in source history.
        self.config["targets"][0]["base_commit"] = self.git(self.repo, "rev-parse", "HEAD")
        self.write(self.repo, "sync/targets.json", json.dumps(self.config))
        commit = self.commit(self.repo, "Invalid source baseline")
        self.git(self.repo, "branch", "-f", "port/test", commit)
        with self.assertRaisesRegex(sync.SyncError, "not an ancestor"):
            self.plan()

    def test_candidate_groups_with_shared_destinations(self):
        plan, target, _, _ = self.plan()
        plan["changes"] = [{"classification": "port-candidate", "path": self.java + name + ".java",
                            "old_path": self.java + name + ".java", "target_paths": paths,
                            "source_diff": "+ change"}
                           for name, paths in (("A", ["common/A.java"]), ("B", ["common/B.java"]),
                                               ("C", ["common/A.java", "common/B.java"]))]
        self.assertEqual(sync.briefs(plan, target, self.output), 1)
        task = json.loads(next(self.output.glob("*.json")).read_text())
        self.assertEqual(task["allowed_changed_paths"], ["common/A.java", "common/B.java"])

    def test_registry_declares_active_and_scaffolded_targets(self):
        root = Path(__file__).resolve().parents[2]
        config = json.loads((root / "sync/targets.json").read_text())
        self.assertEqual(config["source"]["branch"], "main")
        self.assertEqual(config["source"]["remote_url"], "https://github.com/HoYin1600p/Buildscape.git")
        active = [t for t in config["targets"] if t["status"] == "active"]
        self.assertEqual([t["id"] for t in active], ["mc26.2"])
        self.assertEqual(active[0]["base_commit"], "c7d7112cbb351e3f50f9c147dde92ee628553bef")
        self.assertEqual(active[0]["loaders"], ["fabric", "neoforge"])
        for t in config["targets"]:
            if t["status"] != "active":
                self.assertIsNone(t["base_commit"])
            if t["minecraft"] == "26.3":
                self.assertEqual(t["status"], "paused")

    def test_default_output_is_ignored_build_folder_unless_overridden(self):
        with mock.patch.dict(os.environ, {}, clear=True):
            self.assertEqual(sync.default_output(self.root), self.root / "build" / "sync-reports")
        with mock.patch.dict(os.environ, {"BUILDSCAPE_SYNC_OUTPUT": str(self.output)}, clear=True):
            self.assertEqual(sync.default_output(self.root), self.output)


if __name__ == "__main__":
    unittest.main()
