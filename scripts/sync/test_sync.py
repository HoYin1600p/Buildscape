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

    def test_resource_conflict_is_reported_and_target_left_untouched(self):
        edited = '{"port_specific": true, "converted": true}\n'
        self.write(self.repo, "common/" + self.resources + "data/old.json", edited)
        commit = self.commit(self.repo, "Port-specific resource")
        self.git(self.repo, "branch", "-f", "port/test", commit)
        self.mutate_source()
        plan = self.apply()
        self.assertEqual(plan["apply"]["status"], "complete")
        branch = plan["apply"]["branch"]
        rel = "common/" + self.resources + "data/"
        conflict = "common/" + self.resources + "data/old.json"
        self.assertEqual(plan["apply"]["conflicts"], [{"path": conflict, "reason": "port-specific edit"}])
        # Conflicting file is untouched; the clean resource is applied.
        self.assertEqual(sync.blob(self.repo, branch, rel + "old.json"), edited.encode())
        self.assertEqual(json.loads(sync.blob(self.repo, branch, rel + "new.json")), {"value": 3, "converted": True})
        # Review material sits next to the report.
        review = self.output / "conflicts" / "data" / "old.json"
        self.assertEqual(json.loads(Path(str(review) + ".old").read_text()), {"value": 1, "converted": True})
        self.assertEqual(json.loads(Path(str(review) + ".new").read_text()), {"value": 2, "converted": True})
        self.assertEqual(Path(str(review) + ".target").read_text(), edited)
        # JSON and Markdown reports list it; the review queue carries it.
        report = json.loads(next(self.output.glob("*.json")).read_text())
        self.assertEqual(report["apply"]["conflicts"][0]["path"], conflict)
        self.assertIn(conflict, next(self.output.glob("*.md")).read_text())
        state = json.loads(sync.blob(self.repo, branch, "sync/state/tiny.json"))
        self.assertTrue(any(c["path"] == conflict and c["classification"] == "review" for c in state["pending"]))
        self.assertEqual(self.read_csv(branch)[self.resources + "data/old.json"]["status"], "needs_resync")

    def test_mc26_removals_are_retired_and_not_restored_by_apply(self):
        retired = self.resources + "assets/buildscape/blockstates/copper_rod.json"
        model = self.resources + "assets/buildscape/models/block/copper_rod_horizontal.json"
        self.write(self.source, retired, '{"variants": {}}\n')
        self.write(self.source, model, '{"parent": "minecraft:block/cube"}\n')
        base = self.commit(self.source, "Retired source baseline")
        target = self.config["targets"][0]
        target.update(id="mc26.2", base_commit=base)
        policy = {"schema_version": 1, "target": "mc26.2",
                  "removed_ids": [{"id": "buildscape:copper_rod",
                                   "vanilla_equivalent": "minecraft:lightning_rod"}],
                  "excluded_target_paths": ["common/" + model]}
        self.write(self.repo, "sync/removals26x.json", json.dumps(policy))
        self.write(self.repo, "sync/targets.json", json.dumps(self.config))
        self.write(self.repo, "tools/convert.py", (self.repo / "tools/convert.py").read_text() + """
# A converter may synthesize modern item definitions from old item models.
p = root / 'assets/buildscape/items/copper_rod.json'
p.parent.mkdir(parents=True, exist_ok=True)
p.write_text(json.dumps({'converted': True, 'new': (root / 'data/kept.json').exists()}))
""")
        commit = self.commit(self.repo, "Record removal policy")
        self.git(self.repo, "branch", "-f", "port/test", commit)
        self.write(self.source, retired, '{"variants": {}, "changed": true}\n')
        self.write(self.source, model, '{"parent": "minecraft:block/cube_all"}\n')
        self.write(self.source, self.resources + "data/kept.json", '{"kept": true}\n')
        self.commit(self.source, "Change retired and kept resources")
        plan, target, config, source = sync.make_plan(self.repo, self.config, "mc26.2",
                                                    source_repo=self.source)
        items = {c["path"]: c for c in plan["changes"]}
        self.assertEqual(items[retired]["classification"], "retired")
        self.assertEqual(items[model]["classification"], "retired")
        sync.apply_plan(self.repo, plan, target, config, source, "sync/targets.json", self.output)
        branch = plan["apply"]["branch"]
        for path in ("common/" + retired, "common/" + model,
                     "common/" + self.resources + "assets/buildscape/items/copper_rod.json"):
            self.assertIsNone(sync.blob(self.repo, branch, path, True), path)
        self.assertIsNotNone(sync.blob(self.repo, branch, "common/" + self.resources + "data/kept.json"))
        state = json.loads(sync.blob(self.repo, branch, target["state_file"]))
        self.assertFalse(any(c["path"] in {retired, model} for c in state["pending"]))
        self.assertEqual(sync.blob(self.repo, branch, "ref/source/" + retired),
                         sync.blob(self.source, "HEAD", retired))

    def test_retirement_paths_preserve_shared_assets_and_normalize_old_folders(self):
        target = self.config["targets"][0]
        resource_root = target["resource_root"]
        retired = resource_root + "/data/buildscape/loot_table/blocks/copper_rod.json"
        shared = resource_root + "/assets/buildscape/models/block/short_dry_grass.json"
        policy = {"removed_ids": [{"id": "buildscape:copper_rod"}],
                  "excluded_target_paths": [retired]}
        self.assertTrue(sync.retired_path(retired.replace("/loot_table/", "/loot_tables/"),
                                          target, policy))
        self.assertFalse(sync.retired_path(shared, target, policy))

    def test_removal_policy_does_not_affect_other_targets(self):
        target = self.config["targets"][0]
        self.assertEqual(sync.removal_policy(self.repo, target), {})
        self.assertFalse(sync.retired_path("common/" + self.resources
                                          + "assets/buildscape/items/copper_rod.json", target, {}))

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
        # The base advances with every applied sync, so only its shape is fixed.
        self.assertRegex(active[0]["base_commit"], r"^[0-9a-f]{40}$")
        self.assertEqual(active[0]["loaders"], ["fabric", "neoforge"])
        for t in config["targets"]:
            # Every version is a folder on the one multi-version branch.
            self.assertEqual(t["branch"], "port/26.2")
            if t["status"] != "active":
                self.assertIsNone(t["base_commit"])
            if t["minecraft"] == "26.3":
                # mc26.3 is the planned 26.3 target; the older per-loader placeholders stay paused.
                self.assertEqual(t["status"], "planned" if t["id"] == "mc26.3" else "paused")

    def test_registry_declares_inactive_mc263_target(self):
        root = Path(__file__).resolve().parents[2]
        config = json.loads((root / "sync/targets.json").read_text())
        target = next(t for t in config["targets"] if t["id"] == "mc26.3")
        self.assertEqual(target["status"], "planned")
        self.assertIsNone(target["base_commit"])
        self.assertEqual(target["loaders"], ["fabric", "neoforge"])
        self.assertEqual(target["version_cluster"], "v263")
        self.assertEqual(target["version_clusters"], ["v263"])
        self.assertEqual(target["java_roots"], ["common/src/main/java", "common/src/v263/java"])
        self.assertEqual(target["resource_root"], "common/src/v263/resources")
        self.assertEqual(target["state_file"], "sync/state/mc26.3.json")
        self.assertEqual(target["ledger_coverage"], ["263"])
        self.assertEqual(target["converter"]["profile"], "26.3")
        self.assertIn("26.3", target["converter"]["command"])
        self.assertIn("26.3", target["validation"][0])
        self.assertIn("build263", target["validation"][1])
        self.assertIn("-Pbuildscape.v263=true", target["validation"][1])
        self.assertEqual(target["removal_policy"]["file"], "sync/removals26x.json")
        self.assertEqual(target["removal_policy"]["inherits"], "mc26.2")
        # 26.2 keeps its legacy, implicit policy and its own roots.
        old = next(t for t in config["targets"] if t["id"] == "mc26.2")
        self.assertNotIn("removal_policy", old)
        self.assertEqual(old["resource_root"], "common/src/main/resources")
        self.assertEqual(old["version_clusters"], ["v26x", "v118x"])
        self.assertEqual(old["ledger_coverage"], ["26x", "118x"])
        state = json.loads((root / target["state_file"]).read_text())
        self.assertEqual(state, {"schema_version": 1, "pending": []})
        # Only active targets are synchronized by default.
        self.assertEqual([t["id"] for t in config["targets"] if t["status"] == "active"], ["mc26.2"])

    def test_mc263_inherits_exactly_the_approved_26x_removals(self):
        root = Path(__file__).resolve().parents[2]
        config = json.loads((root / "sync/targets.json").read_text())
        target = next(t for t in config["targets"] if t["id"] == "mc26.3")
        spec = target["removal_policy"]
        approved = json.loads((root / spec["file"]).read_text())
        inherited = sync.inherited_policy(approved, spec, target)
        self.assertEqual(inherited["removed_ids"], approved["removed_ids"])
        self.assertEqual(inherited["inherited_from"], "mc26.2")
        self.assertEqual(len(inherited["excluded_target_paths"]), len(approved["excluded_target_paths"]))
        self.assertEqual(len(set(inherited["excluded_target_paths"])), len(set(approved["excluded_target_paths"])))
        old_root, new_root = "common/src/main/resources/", "common/src/v263/resources/"
        resources = 0
        for old, new in zip(approved["excluded_target_paths"], inherited["excluded_target_paths"]):
            if old.startswith(old_root):
                resources += 1
                self.assertEqual(new, new_root + old[len(old_root):])
            else:
                # Shared Java and test sources keep their approved path.
                self.assertEqual(new, old)
        self.assertGreater(resources, 1000)
        # The approved file is only read, never rewritten, and the same object is not mutated.
        self.assertTrue(all(p.startswith(old_root) or p.startswith("common/src/")
                            for p in approved["excluded_target_paths"]))
        self.assertNotIn("inherited_from", approved)
        # Targets that own the policy get it back untouched.
        own = sync.inherited_policy(approved, {"inherits": "mc26.2"}, {"id": "mc26.2"})
        self.assertIs(own, approved)

    def policy_files(self):
        retired = self.resources + "assets/buildscape/blockstates/copper_rod.json"
        model = self.resources + "assets/buildscape/models/block/copper_rod_horizontal.json"
        self.write(self.source, retired, '{"variants": {}}\n')
        self.write(self.source, model, '{"parent": "minecraft:block/cube"}\n')
        base = self.commit(self.source, "Retired source baseline")
        policy = {"schema_version": 1, "target": "mc26.2",
                  "removed_ids": [{"id": "buildscape:copper_rod", "vanilla_equivalent": "minecraft:lightning_rod"}],
                  "excluded_target_paths": ["common/" + model,
                                            "common/src/main/java/example/Mapped.java"]}
        return retired, model, base, policy

    def test_26_2_removal_policy_is_returned_exactly_as_approved(self):
        retired, model, base, policy = self.policy_files()
        target = self.config["targets"][0]
        target.update(id="mc26.2", base_commit=base)
        self.write(self.repo, "sync/removals26x.json", json.dumps(policy))
        self.write(self.repo, "sync/targets.json", json.dumps(self.config))
        commit = self.commit(self.repo, "Record removal policy")
        self.git(self.repo, "branch", "-f", "port/test", commit)
        self.assertEqual(sync.removal_policy(self.repo, target), policy)
        # A policy approved for another target is rejected instead of silently applied.
        self.write(self.repo, "sync/removals26x.json", json.dumps(dict(policy, target="other")))
        commit = self.commit(self.repo, "Wrong policy owner")
        self.git(self.repo, "branch", "-f", "port/test", commit)
        with self.assertRaisesRegex(sync.SyncError, "Invalid mc26.2 removal policy"):
            sync.removal_policy(self.repo, target)

    def test_mc263_inherits_policy_plans_and_applies_in_the_v263_root(self):
        retired, model, base, policy = self.policy_files()
        target = self.config["targets"][0]
        target.update(id="mc26.3", base_commit=base, version_cluster="v263", version_clusters=["v263"],
                      java_roots=["common/src/main/java", "common/src/v263/java"],
                      resource_root="common/src/v263/resources", ledger_coverage=["263"],
                      removal_policy={"file": "sync/removals26x.json", "inherits": "mc26.2",
                                      "inherited_resource_root": "common/src/main/resources"})
        self.write(self.repo, "sync/removals26x.json", json.dumps(policy))
        self.write(self.repo, "sync/targets.json", json.dumps(self.config))
        self.write(self.repo, "tools/convert.py", (self.repo / "tools/convert.py").read_text() + """
# A converter may synthesize modern item definitions from old item models.
p = root / 'assets/buildscape/items/copper_rod.json'
p.parent.mkdir(parents=True, exist_ok=True)
p.write_text(json.dumps({'converted': True, 'new': (root / 'data/kept.json').exists()}))
""")
        for path in (self.repo / "common/src/main/resources").rglob("*"):
            if path.is_file():
                self.write(self.repo, "common/src/v263/resources/"
                           + path.relative_to(self.repo / "common/src/main/resources").as_posix(),
                           path.read_bytes())
        self.write(self.repo, "common/src/v263/java/example/Adapter.java", "class Adapter {}\n")
        commit = self.commit(self.repo, "Record removal policy and 26.3 resource root")
        self.git(self.repo, "branch", "-f", "port/test", commit)
        self.write(self.source, retired, '{"variants": {}, "changed": true}\n')
        self.write(self.source, model, '{"parent": "minecraft:block/cube_all"}\n')
        self.write(self.source, self.resources + "data/kept.json", '{"kept": true}\n')
        self.commit(self.source, "Change retired and kept resources")
        plan, target, config, source = sync.make_plan(self.repo, self.config, "mc26.3",
                                                    source_repo=self.source)
        approved = {k: v for k, v in policy.items() if k != "excluded_target_paths"}
        for key, value in approved.items():
            self.assertEqual(plan["removal_policy"][key], value)
        self.assertEqual(plan["removal_policy"]["inherited_from"], "mc26.2")
        self.assertEqual(plan["removal_policy"]["excluded_target_paths"],
                         ["common/src/v263/resources/" + model[len("src/main/resources/"):],
                          "common/src/main/java/example/Mapped.java"])
        self.assertEqual(plan["version_clusters"], ["v263"])
        items = {c["path"]: c for c in plan["changes"]}
        self.assertEqual(items[retired]["classification"], "retired")
        self.assertEqual(items[model]["classification"], "retired")
        self.assertEqual(items[self.resources + "data/kept.json"]["classification"], "automatic")
        before_main = sync.files_at(self.repo, plan["target_commit"], ["common/src/main/resources"])
        sync.apply_plan(self.repo, plan, target, config, source, "sync/targets.json", self.output)
        branch = plan["apply"]["branch"]
        v263 = "common/src/v263/resources/"
        for path in (retired, model, self.resources + "assets/buildscape/items/copper_rod.json"):
            self.assertIsNone(sync.blob(self.repo, branch, v263 + path[len("src/main/resources/"):], True), path)
        self.assertIsNotNone(sync.blob(self.repo, branch, v263 + "data/kept.json"))
        # 26.3 never writes into the 26.2 resource root.
        self.assertEqual(sync.files_at(self.repo, branch, ["common/src/main/resources"]), before_main)
        state = json.loads(sync.blob(self.repo, branch, "sync/state/tiny.json"))
        self.assertFalse(any(c["path"] in {retired, model} for c in state["pending"]))
        rows = self.read_csv(branch)
        self.assertTrue(all("263" in r["era_coverage"].split(";") for r in rows.values()))

    def test_target_without_policy_declaration_has_no_removals(self):
        target = self.config["targets"][0]
        for target_id in ("tiny", "mc26.3"):
            self.assertEqual(sync.removal_policy(self.repo, dict(target, id=target_id)), {})

    def test_foreign_cluster_ledger_paths_are_never_mapped(self):
        registry = {"targets": [
            {"id": "mc26.2", "version_cluster": "v26x", "version_clusters": ["v26x", "v118x"]},
            {"id": "mc26.3", "version_cluster": "v263", "version_clusters": ["v263"]},
            {"id": "mc1.21.1", "version_cluster": "v121x"},
            {"id": "nothing"}]}
        self.assertEqual(sync.foreign_clusters(registry, registry["targets"][0]), ["v121x", "v263"])
        self.assertEqual(sync.foreign_clusters(registry, registry["targets"][1]), ["v118x", "v121x", "v26x"])
        base = "common/src/main/java/example/Base.java"
        v26x = "common/src/v26x/java/example/adapter/v26x/Adapter.java"
        v118x = "common/src/main/java/example/adapter/v118x/Adapter.java"
        v263 = "common/src/v263/java/example/adapter/v263/Adapter.java"
        for path in (base, v26x, v118x, v263):
            self.write(self.repo, path, "class X {}\n")
        commit = self.commit(self.repo, "Cluster adapters")
        self.git(self.repo, "branch", "-f", "port/test", commit)
        row = {"active_paths": ";".join([base, v26x, v118x, v263])}
        source = self.config["source"]

        def mapped(clusters, foreign):
            target = {"branch": "port/test", "java_root": "common/src/main/java",
                      "java_roots": ["common/src/main/java", *["common/src/" + c + "/java" for c in clusters]],
                      "foreign_clusters": foreign}
            return sync.mapped_java(self.repo, target, "src/main/java/example/Unmapped.java", row, source)

        self.assertEqual(mapped(["v26x", "v118x"], ["v121x", "v263"]), [base, v26x, v118x])
        self.assertEqual(mapped(["v263"], ["v118x", "v121x", "v26x"]), [base, v263])
        # Without a declaration nothing is filtered, as before.
        self.assertEqual(mapped(["v26x"], []), [base, v26x, v118x, v263])

    def test_target_config_keeps_the_registry_unchanged(self):
        config = json.loads(json.dumps(self.config))
        config["targets"][0].update(version_cluster="v26x", version_clusters=["v26x"])
        config["targets"][1]["version_cluster"] = "v263"
        self.write(self.repo, "sync/targets.json", json.dumps(config))
        commit = self.commit(self.repo, "Cluster registry")
        self.git(self.repo, "branch", "-f", "port/test", commit)
        target, branch_config = sync.target_config(self.repo, config, "tiny", "sync/targets.json")
        self.assertEqual(target["foreign_clusters"], ["v263"])
        # apply writes branch_config back into the registry; the helper key must not leak into it.
        self.assertTrue(all("foreign_clusters" not in t for t in branch_config["targets"]))

    def test_real_ledger_v263_paths_exist_and_are_marked(self):
        root = Path(__file__).resolve().parents[2]
        with (root / "migration/ledger.csv").open(encoding="utf-8", newline="") as stream:
            rows = list(csv.DictReader(stream))
        marked = [r for r in rows if any("/v263/" in p or "/src/v263/" in p for p in r["active_paths"].split(";"))]
        self.assertGreater(len(marked), 200)
        for row in marked:
            self.assertIn("263", row["era_coverage"].split(";"), row["reference_path"])
            for path in row["active_paths"].split(";"):
                if "/v263/" in path:
                    self.assertTrue((root / path).is_file(), path)
        # Rows without a 26.3 counterpart stay on their existing coverage.
        marked_ids = {id(r) for r in marked}
        self.assertTrue(all("263" not in r["era_coverage"].split(";") for r in rows if id(r) not in marked_ids))

    def cluster_target(self):
        target = self.config["targets"][0]
        target.update(id="mc26.2", version_cluster="v26x", version_clusters=["v26x", "v118x"],
                      java_roots=["common/src/main/java", "common/src/v118x/java", "common/src/v26x/java"],
                      ledger_coverage=["26x", "118x"], reference_snapshot="ref/forge/1.18.2")
        (self.repo / "ref/source").rename(self.repo / "ref/forge-tmp")
        (self.repo / "ref/forge").mkdir()
        (self.repo / "ref/forge-tmp").rename(self.repo / "ref/forge/1.18.2")
        for name, cluster in (("Same", "v118x"), ("Fallback", "v26x"), ("Rename", "v118x")):
            old = "common/" + self.java + name + ".java"
            dest = "common/src/" + cluster + "/java/example/" + name + ".java"
            self.write(self.repo, dest, (self.repo / old).read_bytes())
            (self.repo / old).unlink()
        # A removal on 26.2 must not retire a restored v118x counterpart.
        self.write(self.repo, "sync/removals26x.json", json.dumps({"schema_version": 1, "target": "mc26.2",
            "removed_ids": [], "excluded_target_paths": ["common/" + self.java + "Same.java"]}))
        self.write(self.repo, "sync/targets.json", json.dumps(self.config))
        commit = self.commit(self.repo, "Cluster roots")
        self.git(self.repo, "branch", "-f", "port/test", commit)
        return target

    def test_one_plan_and_apply_cover_both_clusters_and_exact_reference(self):
        self.cluster_target()
        self.mutate_source()
        self.write(self.source, self.resources + "binary.dat", b"\x00\xff\r\nunchanged bytes\r\n")
        self.write(self.source, "src/test/java/example/Parity.java", b"class Parity {}\r\n")
        head = self.commit(self.source, "Reference binary and test inputs")
        plan, target, config, source = sync.make_plan(self.repo, self.config, "mc26.2", source_repo=self.source)
        changes = {c["path"]: c for c in plan["changes"]}
        for name, cluster in (("Same", "v118x"), ("Fallback", "v26x")):
            entry = changes[self.java + name + ".java"]
            self.assertEqual(entry["classification"], "automatic")
            self.assertEqual(entry["destination"], "common/src/" + cluster + "/java/example/" + name + ".java")
            self.assertIn(cluster, entry["affected_clusters"])
        self.assertEqual(changes[self.java + "Mapped.java"]["affected_clusters"], ["v118x", "v26x"])
        rename = changes[self.java + "Renamed.java"]
        self.assertEqual(rename["classification"], "automatic")
        self.assertEqual(rename["destination"], "common/src/v118x/java/example/Renamed.java")
        sync.apply_plan(self.repo, plan, target, config, source, "sync/targets.json", self.output)
        branch = plan["apply"]["branch"]
        snapshot = sync.files_at(self.repo, branch, [target["reference_snapshot"]])
        actual = {p[len(target["reference_snapshot"]) + 1:]: data for p, data in snapshot.items()}
        self.assertEqual(actual, sync.files_at(self.source, head, sync.scope(self.config["source"])))
        self.assertIsNone(sync.blob(self.repo, branch, "common/src/v118x/java/example/Rename.java", True))
        self.assertEqual(sync.blob(self.repo, branch, "common/src/v118x/java/example/Same.java"),
                         sync.blob(self.source, head, self.java + "Same.java"))
        rows = self.read_csv(branch)
        self.assertEqual(rows[self.java + "Same.java"]["active_paths"], "common/src/v118x/java/example/Same.java")
        self.assertTrue(all("118x" in r["era_coverage"].split(";") for r in rows.values()))

    def test_single_identical_v118x_mixin_can_sync(self):
        target = self.cluster_target()
        path = self.java + "ExampleMixin.java"
        old = b"class ExampleMixin {}\n"
        self.write(self.source, path, old)
        base = self.commit(self.source, "Mixin baseline")
        dest = "common/src/v118x/java/example/ExampleMixin.java"
        self.write(self.repo, dest, old)
        commit = self.commit(self.repo, "Mixin counterpart")
        self.git(self.repo, "branch", "-f", "port/test", commit)
        self.write(self.source, path, b"class ExampleMixin { int added; }\n")
        head = self.commit(self.source, "Mixin change")
        entry = sync.classify(self.repo, self.source, target, base, head,
                             {"path": path, "old_path": path, "change": "modified"}, [], self.config["source"])
        self.assertEqual(entry["classification"], "automatic")
        self.assertEqual(entry["destination"], dest)

    def test_validation_commands_cover_both_clusters_and_preserve_dotted_properties(self):
        root = Path(__file__).resolve().parents[2]
        target = json.loads((root / "sync/targets.json").read_text())["targets"][0]
        mandatory = target["validation"][-1]
        self.assertIn(":common:compileV118xJava", mandatory)
        self.assertIn(":fabric:26.2:build", mandatory)
        self.assertIn(":neoforge:26.2:build", mandatory)
        gated = target["gated_validation"][0]["command"]
        for token in (":forge:1.18.2:build", ":forge:1.18.2:check", "-Pbuildscape.v118x=true",
                      "-Pbuildscape.v118xSource=common", "--offline", "--console=plain", "--no-daemon"):
            self.assertIn(token, gated)
        values = {"gradle": str(self.root / "space's tools/gradlew.bat")}
        log = self.root / "validation.log"
        with mock.patch("sync.subprocess.run", return_value=subprocess.CompletedProcess([], 0)) as execute:
            sync.command(gated, values, self.root, log)
        self.assertEqual(execute.call_args.args[0], [a.format_map(values) for a in gated])
        # Argument arrays preserve dotted -P values; durable logs quote them for inspection.
        self.assertIn("'-Pbuildscape.v118x=true'", log.read_text())
        self.assertIn("'-Pbuildscape.v118xSource=common'", log.read_text())

    def test_gated_forge_cache_miss_is_partial_and_code_failure_is_fatal(self):
        log = self.root / "validation.log"
        target = {"validation": [["required"]], "gated_validation": [
            {"command": ["forge"], "skip_on_offline_cache_miss": True}]}
        def cache_miss(check, values, cwd, logfile):
            with logfile.open("a") as stream:
                stream.write("No cached version of forge:40.3.12 available for offline mode.\n")
            if check == ["forge"]:
                raise sync.SyncError("Validation/conversion failed")
        with mock.patch("sync.command", side_effect=cache_miss):
            results = sync.validate(target, {}, self.repo, log)
        self.assertEqual([r["status"] for r in results], ["passed", "skipped"])
        self.assertIn("needs network", results[-1]["reason"])
        # A previous cache warning must not hide a later compiler failure.
        def compiler_error(check, values, cwd, logfile):
            with logfile.open("a") as stream:
                stream.write("error: cannot find symbol\n")
            if check == ["forge"]:
                raise sync.SyncError("Validation/conversion failed")
        with mock.patch("sync.command", side_effect=compiler_error):
            with self.assertRaises(sync.SyncError):
                sync.validate(target, {}, self.repo, log)
        row = dict(self.rows[0])
        plan = {"source_commit": self.base, "changes": [{"old_path": row["reference_path"],
            "path": row["reference_path"], "classification": "automatic", "kind": "java",
            "destination": row["active_paths"]}], "apply": {"validation": results}}
        sync.update_ledger(self.repo, plan, self.config["targets"][0], list(row), [row])
        self.assertIn("PARTIAL", row["verification"])

    def test_default_output_is_ignored_build_folder_unless_overridden(self):
        with mock.patch.dict(os.environ, {}, clear=True):
            self.assertEqual(sync.default_output(self.root), self.root / "build" / "sync-reports")
        with mock.patch.dict(os.environ, {"BUILDSCAPE_SYNC_OUTPUT": str(self.output)}, clear=True):
            self.assertEqual(sync.default_output(self.root), self.output)


if __name__ == "__main__":
    unittest.main()
