"""Plan and apply source-line updates into local, reviewable port branches."""
import argparse
import csv
import io
import json
import os
from pathlib import Path, PurePosixPath
import re
import subprocess
import sys
import tempfile

sys.dont_write_bytecode = True


class SyncError(RuntimeError):
    pass


def run(args, cwd, check=True):
    env = dict(os.environ, PYTHONDONTWRITEBYTECODE="1", GIT_TERMINAL_PROMPT="0")
    result = subprocess.run([str(a) for a in args], cwd=cwd, env=env,
                            stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    if check and result.returncode:
        raise SyncError("Command failed: {}\n{}\n{}".format(
            args, result.stdout.decode("utf-8", "replace"),
            result.stderr.decode("utf-8", "replace")))
    return result


def git(repo, *args, check=True):
    return run(["git", *args], repo, check).stdout


def text(data):
    return data.decode("utf-8-sig")


def relative(path):
    p = PurePosixPath(path)
    if not path or "\\" in path or p.is_absolute() or ".." in p.parts or ":" in path:
        raise SyncError("Expected a safe repository-relative path: " + path)
    return p.as_posix()


def local(root, path):
    p = root / relative(path)
    if not p.resolve().is_relative_to(root.resolve()):
        raise SyncError("Path escapes repository: " + path)
    return p


def blob(repo, rev, path, optional=False):
    relative(path)
    result = run(["git", "show", f"{rev}:{path}"], repo, check=not optional)
    return None if result.returncode else result.stdout


def json_blob(repo, rev, path, default=None):
    data = blob(repo, rev, path, optional=default is not None)
    return default if data is None else json.loads(text(data))


def dump(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def registry(repo, registry_path):
    data = json.loads(local(repo, registry_path).read_text(encoding="utf-8-sig"))
    if data.get("schema_version") != 1:
        raise SyncError("Unsupported registry schema")
    return data


def target_config(repo, config, target_id, registry_path):
    target = next((t for t in config["targets"] if t["id"] == target_id), None)
    if not target:
        raise SyncError("Unknown target: " + target_id)
    if target["status"] != "active" or not target.get("base_commit"):
        raise SyncError("Target must be active with a reviewed base commit: " + target_id)
    # The target branch is authoritative, even when invoked from another checkout.
    branch_config = json_blob(repo, target["branch"], registry_path)
    target = next(t for t in branch_config["targets"] if t["id"] == target_id)
    if target["status"] != "active" or not target.get("base_commit"):
        raise SyncError("Target branch is not activated: " + target_id)
    for key in ("reference_snapshot", "mapping_file", "java_root", "resource_root", "state_file"):
        relative(target[key])
    return target, branch_config


def source_revision(repo, config, source_repo=None, fetch=False):
    source = config["source"]
    root = Path(source_repo).resolve() if source_repo else repo
    branch = source["branch"]
    if fetch:
        if source_repo:
            raise SyncError("--source-repo is read-only; omit --fetch for local sources")
        ref = "refs/sync/source/" + branch
        git(repo, "check-ref-format", ref)
        git(repo, "fetch", "--no-tags", source["remote_url"], f"{branch}:{ref}")
    else:
        ref = "refs/heads/" + branch
        if run(["git", "rev-parse", "--verify", ref], root, False).returncode:
            ref = "refs/sync/source/" + branch
    sha = text(git(root, "rev-parse", "--verify", ref + "^{commit}")).strip()
    return root, sha


def scope(source):
    return [relative(source["subtree"]), *[relative(p) for p in source["metadata_files"]]]


def changes(repo, base, head, source):
    tokens = git(repo, "diff", "--name-status", "-z", "--find-renames", base, head,
                 "--", *scope(source)).decode("utf-8").split("\0")
    result, i = [], 0
    while i < len(tokens) and tokens[i]:
        status, old = tokens[i], tokens[i + 1]
        i += 2
        new = old
        if status[0] in "RC":
            new = tokens[i]
            i += 1
        result.append({"change": {"A": "added", "M": "modified", "D": "deleted",
                                   "R": "renamed", "T": "modified", "C": "added"}[status[0]],
                       "old_path": relative(old), "path": relative(new)})
    return result


def read_ledger(repo, rev, path):
    reader = csv.DictReader(io.StringIO(text(blob(repo, rev, path))))
    rows = list(reader)
    if not reader.fieldnames or not {"reference_path", "status", "active_paths"}.issubset(reader.fieldnames):
        raise SyncError("Ledger lacks required columns")
    return reader.fieldnames, rows


def metadata(path):
    name = PurePosixPath(path).name
    return (name in {"build.gradle", "build.gradle.kts", "settings.gradle", "settings.gradle.kts",
                     "gradle.properties", "mods.toml", "neoforge.mods.toml", "fabric.mod.json",
                     "accesstransformer.cfg", "gradlew", "gradlew.bat"}
            or "mixin" in name.lower() or path.endswith(".accesswidener")
            or path.startswith("gradle/"))


def removal_policy(repo, target):
    if target["id"] != "mc26.2":
        return {}
    policy = json_blob(repo, target["branch"], "sync/removals26x.json")
    if policy.get("schema_version") != 1 or policy.get("target") != target["id"]:
        raise SyncError("Invalid mc26.2 removal policy")
    for path in policy.get("excluded_target_paths", []):
        relative(path)
    return policy


def retired_path(path, target, policy, source=None):
    if not policy:
        return False
    if source:
        for kind in ("java", "resources"):
            prefix = source["subtree"] + "/main/" + kind + "/"
            if path.startswith(prefix):
                path = target["java_root" if kind == "java" else "resource_root"] + "/" + path[len(prefix):]
                break
    # Old source data folders are renamed by the resource converter.
    path = re.sub(r"(/data/[^/]+/)(loot_tables|recipes|advancements)/",
                  lambda m: m[1] + {"loot_tables": "loot_table", "recipes": "recipe",
                                    "advancements": "advancement"}[m[2]] + "/", path)
    if path in policy.get("excluded_target_paths", []):
        return True
    prefix = target["resource_root"] + "/"
    if not path.startswith(prefix):
        return False
    resource = path[len(prefix):]
    match = re.fullmatch(r"(?:assets/buildscape/(?:blockstates|items)|"
                         r"data/buildscape/loot_table/blocks)/([^/]+)\.json", resource)
    return bool(match and any(entry["id"] == "buildscape:" + match[1]
                              for entry in policy["removed_ids"]))


def classify(repo, source_repo, target, base, head, change, rows, source, policy=None):
    entry = dict(change)
    old, path = entry["old_path"], entry["path"]
    row = next((r for r in rows if r["reference_path"] == old), None)
    mapped = [relative(p.strip()) for p in (row or {}).get("active_paths", "").split(";")
              if p.strip() and p.strip() != "n/a"]
    java_prefix = source["subtree"] + "/main/java/"
    resource_prefix = source["subtree"] + "/main/resources/"
    canonical = target["java_root"] + "/" + old[len(java_prefix):] if old.startswith(java_prefix) else None
    if not mapped and canonical and blob(repo, target["branch"], canonical, True) is not None:
        mapped = [canonical]
    entry["target_paths"] = mapped
    entry["classification"] = "review"
    entry["reason"] = "Build or metadata change requires review"
    is_java = old.endswith(".java") or path.endswith(".java")
    if entry["change"] == "deleted":
        entry["reason"] = "Deleted source: retain target code and mark ledger for review"
    elif metadata(old) or metadata(path):
        pass
    elif path.startswith(resource_prefix):
        entry.update(classification="automatic", reason="Resource conversion and validation", kind="resource")
    elif is_java:
        old_bytes = blob(source_repo, base, old, True)
        identical = (old_bytes is not None and len(mapped) == 1 and
                     blob(repo, target["branch"], mapped[0], True) == old_bytes)
        rename_ok = entry["change"] != "renamed" or (mapped == [canonical] and path.startswith(java_prefix))
        if identical and rename_ok:
            destination = (target["java_root"] + "/" + path[len(java_prefix):]
                           if entry["change"] == "renamed" else mapped[0])
            if destination != mapped[0] and blob(repo, target["branch"], destination, True) is not None:
                identical = False
            else:
                entry["destination"] = destination
        if identical and rename_ok:
            entry.update(classification="automatic", reason="Single counterpart equals old source", kind="java")
        else:
            entry.update(classification="port-candidate" if mapped else "new-feature port-candidate",
                         reason="Mapped Java needs porting" if mapped else "New Java feature has no mapping",
                         kind="java")
    else:
        entry["reason"] = "Unrecognized source file requires review"
    if retired_path(path, target, policy, source) or (mapped and all(
            retired_path(p, target, policy) for p in mapped)):
        entry.update(classification="retired", reason="Intentionally removed by mc26.2 policy")
    entry["source_diff"] = text(git(source_repo, "diff", "--no-ext-diff", "--no-textconv",
                                     base, head, "--", old, path))
    return entry


def make_plan(repo, config, target_id, registry_path="sync/targets.json", source_repo=None, fetch=False, source_head=None):
    target, branch_config = target_config(repo, config, target_id, registry_path)
    source = config["source"]
    source_root, head = source_revision(repo, config, source_repo, fetch)
    if source_head:
        head = source_head
    base = target["base_commit"]
    if run(["git", "merge-base", "--is-ancestor", base, head], source_root, False).returncode:
        raise SyncError("Base is not an ancestor of source head; review the source-line change")
    _, rows = read_ledger(repo, target["branch"], target["mapping_file"])
    policy = removal_policy(repo, target)
    items = [classify(repo, source_root, target, base, head, c, rows, source, policy)
             for c in changes(source_root, base, head, source)]
    pending = json_blob(repo, target["branch"], target["state_file"], {"pending": []})["pending"]
    pending = [c for c in pending if not retired_path(c["path"], target, policy, source)
               and not (c["target_paths"] and all(retired_path(p, target, policy) for p in c["target_paths"]))]
    for c in items + pending:
        # A brief can precede apply: new source files may not yet exist in the snapshot.
        c["reference_context"] = []
        for path in dict.fromkeys((c["path"], c["old_path"])):
            candidate = target["reference_snapshot"] + "/" + path
            if blob(repo, target["branch"], candidate, True) is not None:
                c["reference_context"].append(candidate)
    commits = text(git(source_root, "log", "--reverse", "--format=%H %s", f"{base}..{head}")).splitlines()
    plan = {"schema_version": 1, "target": target_id, "target_branch": target["branch"],
            "target_commit": text(git(repo, "rev-parse", target["branch"] + "^{commit}")).strip(),
            "source": source, "base_commit": base, "source_commit": head,
            "commits": commits, "changes": items, "pending": pending, "removal_policy": policy}
    return plan, target, branch_config, source_root


def write_report(plan, output):
    output.mkdir(parents=True, exist_ok=True)
    stem = safe_id(plan["target"]) + "-" + plan["source_commit"][:12]
    dump(output / (stem + ".json"), plan)
    lines = [f"# Sync {plan['target']}", "", f"Source: `{plan['base_commit']}..{plan['source_commit']}`",
             f"Target: `{plan['target_branch']}` at `{plan['target_commit']}`", "",
             "This plan advances the source baseline; pending ports still require review.", "", "## Commits", ""]
    lines += ["- " + c for c in plan["commits"]] or ["No new source commits."]
    for c in plan["changes"]:
        lines += ["", f"## {c['path']}", "", f"{c['change']}: **{c['classification']}**. {c['reason']}.",
                  "Targets: " + (", ".join(c["target_paths"]) or "unmapped"), "",
                  "```diff", c["source_diff"].rstrip(), "```"]
    if plan["pending"]:
        lines += ["", "## Outstanding review queue", ""]
        lines += [f"- {c['path']}: {c['classification']} ({c['source_commit']})" for c in plan["pending"]]
    if "apply" in plan:
        lines += ["", "## Apply result", "", "```json", json.dumps(plan["apply"], indent=2), "```"]
    (output / (stem + ".md")).write_text("\n".join(lines) + "\n", encoding="utf-8")


def safe_id(value):
    if not re.fullmatch(r"[A-Za-z0-9_.-]+", value) or value in {".", ".."}:
        raise SyncError("Unsafe target ID: " + value)
    return value


def files_at(repo, rev, paths):
    records = git(repo, "ls-tree", "-r", "-z", rev, "--", *paths).split(b"\0")
    names, hashes = [], []
    for record in records:
        if not record:
            continue
        info, name = record.split(b"\t", 1)
        mode, kind, sha = info.split()
        if kind != b"blob" or mode not in {b"100644", b"100755"}:
            raise SyncError("Source snapshot cannot contain symlinks or submodules")
        path = relative(name.decode("utf-8"))
        names.append(path)
        hashes.append(sha)
    if not hashes:
        return {}
    batch = subprocess.run(["git", "cat-file", "--batch"], cwd=repo,
                           input=b"\n".join(hashes) + b"\n", stdout=subprocess.PIPE,
                           stderr=subprocess.PIPE)
    if batch.returncode:
        raise SyncError(text(batch.stderr))
    result, offset = {}, 0
    for path in names:
        end = batch.stdout.index(b"\n", offset)
        header = batch.stdout[offset:end].split()
        size = int(header[2])
        start = end + 1
        result[path] = batch.stdout[start:start + size]
        offset = start + size + 1
    return result


def write_file(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(data)


def command(template, values, cwd, log):
    args = [a.format_map(values) for a in template]
    # Live, durable output is useful for long builds; no shell interpolation.
    with log.open("ab") as stream:
        stream.write((repr(args) + "\n").encode("utf-8"))
        stream.flush()
        code = subprocess.run(args, cwd=cwd, env=dict(os.environ, PYTHONDONTWRITEBYTECODE="1"),
                              stdout=stream, stderr=subprocess.STDOUT).returncode
    if code:
        raise SyncError(f"Validation/conversion failed ({code}); see {log}")


def resource_changes(worktree, source_repo, plan, target, values, log, temp):
    prefix = plan["source"]["subtree"] + "/main/resources/"
    old_dir, new_dir = temp / "old-resources", temp / "new-resources"
    policy = plan.get("removal_policy", {})
    for rev, dest in ((plan["base_commit"], old_dir), (plan["source_commit"], new_dir)):
        dest.mkdir()
        for path, data in files_at(source_repo, rev, [prefix.rstrip("/")]).items():
            if not metadata(path) and not retired_path(path, target, policy, plan["source"]):
                write_file(local(dest, path[len(prefix):]), data)
        if rev == plan["source_commit"]:
            # Source deletions require review. Keep converted outputs until accepted.
            for c in plan["changes"]:
                if (c["change"] == "deleted" and c["old_path"].startswith(prefix)
                        and not metadata(c["old_path"])
                        and not retired_path(c["old_path"], target, policy, plan["source"])):
                    write_file(local(dest, c["old_path"][len(prefix):]),
                               blob(source_repo, plan["base_commit"], c["old_path"]))
        command(target["converter"]["command"], dict(values, resources=str(dest)), worktree, log)
    before = {p.relative_to(old_dir).as_posix(): p.read_bytes() for p in old_dir.rglob("*") if p.is_file()}
    after = {p.relative_to(new_dir).as_posix(): p.read_bytes() for p in new_dir.rglob("*") if p.is_file()}
    resource_root = local(worktree, target["resource_root"])
    changed = []
    for path in sorted(before.keys() | after.keys()):
        if retired_path(target["resource_root"] + "/" + path, target, policy):
            continue
        if before.get(path) == after.get(path):
            continue
        dest = local(resource_root, path)
        # Do not silently replace port-specific resource edits or unrelated files.
        current = dest.read_bytes() if dest.exists() else None
        if current not in (before.get(path), after.get(path)):
            raise SyncError("Converted resource conflicts with target edits: " + str(dest))
        if path in after:
            write_file(dest, after[path])
        elif dest.exists():
            dest.unlink()
        changed.append(target["resource_root"] + "/" + path)
    return changed


def update_ledger(worktree, plan, target, fields, rows):
    by_path = {r["reference_path"]: r for r in rows}
    for c in plan["changes"]:
        row = by_path.get(c["old_path"])
        if row is None and c.get("kind") in {"java", "resource"}:
            row = dict.fromkeys(fields, "")
            row.update(kind=c["kind"], reference_path=c["path"], status="pending")
            rows.append(row)
        if row is None:
            continue
        if c["classification"] == "retired":
            row["active_paths"] = "n/a"
            row["verification"] = "Retired by sync/removals26x.json; source " + plan["source_commit"]
        elif c["classification"] == "automatic":
            row["reference_path"] = c["path"]
            if c["kind"] == "java":
                row["active_paths"] = c["destination"]
            row["verification"] = "Sync validation PASS for source " + plan["source_commit"]
        else:
            row["status"] = "needs_resync"
            row["verification"] = "Review required for source " + plan["source_commit"]
    with local(worktree, target["mapping_file"]).open("w", encoding="utf-8", newline="") as stream:
        writer = csv.DictWriter(stream, fieldnames=fields)
        writer.writeheader()
        writer.writerows(rows)


def apply_plan(repo, plan, target, branch_config, source_repo, registry_path, output, mc_jar=None):
    if not plan["commits"]:
        plan["apply"] = {"status": "up-to-date"}
        write_report(plan, output)
        return plan
    branch = f"sync/{safe_id(target['id'])}/{plan['source_commit'][:12]}"
    existing = run(["git", "rev-parse", "--verify", "refs/heads/" + branch], repo, False)
    if existing.returncode == 0:
        previous = json_blob(repo, branch, registry_path)
        previous_target = next(t for t in previous["targets"] if t["id"] == target["id"])
        if previous_target["base_commit"] != plan["source_commit"]:
            raise SyncError("An incomplete sync branch already exists; inspect it: " + branch)
        if run(["git", "merge-base", "--is-ancestor", plan["target_commit"], branch], repo, False).returncode:
            raise SyncError("Existing sync branch predates target changes; review before retrying")
        plan["apply"] = {"status": "already-created", "branch": branch,
                         "commit": text(existing.stdout).strip()}
        write_report(plan, output)
        return plan
    output.mkdir(parents=True, exist_ok=True)
    worktree = output / "worktrees" / (safe_id(target["id"]) + "-" + plan["source_commit"][:12])
    worktree.parent.mkdir(parents=True, exist_ok=True)
    git(repo, "worktree", "add", "-b", branch, str(worktree), plan["target_commit"])
    log = output / (safe_id(target["id"]) + "-" + plan["source_commit"][:12] + ".log")
    jar = mc_jar or os.environ.get("BUILDSCAPE_SYNC_MC_JAR") or target.get("mc_jar", "")
    jar_path = Path(jar) if jar else None
    if jar_path is not None and not jar_path.is_absolute():
        jar_path = repo / jar_path
    values = {"python": sys.executable, "repo": str(worktree), "resources": str(local(worktree, target["resource_root"])),
              "mc_jar": str(jar_path.resolve()) if jar_path else "",
              "gradle": str(worktree / ("gradlew.bat" if os.name == "nt" else "gradlew"))}
    plan["apply"] = {"status": "in-progress", "branch": branch, "worktree": str(worktree), "log": str(log)}
    write_report(plan, output)
    try:
        resource_paths = []
        with tempfile.TemporaryDirectory(prefix="buildscape-sync-") as tmp:
            temp = Path(tmp)
            if any(c.get("kind") == "resource" and c["classification"] == "automatic" for c in plan["changes"]):
                resource_paths = resource_changes(worktree, source_repo, plan, target, values, log, temp)
            for c in plan["changes"]:
                if c.get("kind") != "java" or c["classification"] != "automatic":
                    continue
                current = local(worktree, c["target_paths"][0])
                old, new = temp / "old.java", temp / "new.java"
                old.write_bytes(blob(source_repo, plan["base_commit"], c["old_path"]))
                new.write_bytes(blob(source_repo, plan["source_commit"], c["path"]))
                merged = git(worktree, "merge-file", "--stdout", str(current), str(old), str(new))
                dest = local(worktree, c["destination"])
                write_file(dest, merged)
                if dest != current:
                    current.unlink()
        # Replace only declared source scope in the frozen snapshot; target code is untouched for deletions.
        snapshot = local(worktree, target["reference_snapshot"])
        wanted = files_at(source_repo, plan["source_commit"], scope(plan["source"]))
        previous = files_at(repo, plan["target_commit"],
                            [target["reference_snapshot"] + "/" + p for p in scope(plan["source"])])
        for path in previous:
            rel = path[len(target["reference_snapshot"]) + 1:]
            if rel not in wanted:
                local(snapshot, rel).unlink()
        for path, data in wanted.items():
            write_file(local(snapshot, path), data)
        if not target.get("validation"):
            raise SyncError("Active target must declare validation commands")
        for check in target["validation"]:
            command(check, values, worktree, log)
        fields, rows = read_ledger(repo, plan["target_commit"], target["mapping_file"])
        update_ledger(worktree, plan, target, fields, rows)
        pending = list(plan["pending"])
        pending.extend(dict(c, source_commit=plan["source_commit"], base_commit=plan["base_commit"])
                       for c in plan["changes"] if c["classification"] not in {"automatic", "retired"})
        dump(local(worktree, target["state_file"]), {"schema_version": 1, "pending": pending})
        for t in branch_config["targets"]:
            if t["id"] == target["id"]:
                t["base_commit"] = plan["source_commit"]
        # Keep the invoked configurable source line in the new branch's registry.
        branch_config["source"] = plan["source"]
        dump(local(worktree, registry_path), branch_config)
        git(worktree, "add", "--", registry_path, target["mapping_file"], target["reference_snapshot"],
            target["java_root"], target["resource_root"], target["state_file"])
        git(worktree, "add", "-f", "--", target["reference_snapshot"])
        git(worktree, "-c", "user.name=HoY", "-c", "user.email=hoyin1600p@gmail.com", "commit", "-m",
            f"Sync {target['id']} from {plan['base_commit'][:12]} to {plan['source_commit'][:12]}")
        plan["apply"].update(status="complete", commit=text(git(worktree, "rev-parse", "HEAD")).strip())
        write_report(plan, output)
    except Exception as exc:
        plan["apply"].update(status="failed", error=str(exc))
        write_report(plan, output)
        raise
    return plan


def briefs(plan, target, output):
    # Connected groups share a destination file (including adapters); avoid conflicting tasks.
    candidates = [dict(c, source_commit=plan["source_commit"], base_commit=plan["base_commit"])
                  for c in plan["changes"] if "port-candidate" in c["classification"]]
    candidates += [c for c in plan["pending"] if "port-candidate" in c["classification"]]
    groups = []
    for c in candidates:
        paths = set(c["target_paths"])
        overlaps = [g for g in groups if paths & {p for item in g for p in item["target_paths"]}]
        group = [c]
        for g in overlaps:
            group.extend(g)
            groups.remove(g)
        groups.append(group)
    output.mkdir(parents=True, exist_ok=True)
    for i, group in enumerate(groups, 1):
        context = sorted({target["mapping_file"]} | {p for c in group for p in c.get("reference_context", [])}
                         | {p for c in group for p in c["target_paths"]})
        allowed = sorted({p for c in group for p in c["target_paths"]})
        # New features need a destination proposal approved by the lead, not blanket module access.
        task = {"task_id": f"sync-{safe_id(target['id'])}-{plan['source_commit'][:12]}-{i:03d}",
                "mode": "implement" if allowed else "review",
                "objective": "Port the listed source changes; preserve target adapters and resolve ledger review with the lead.",
                "context_paths": context, "allowed_changed_paths": allowed,
                "acceptance_criteria": ["Preserve source behavior on the target VersionCluster and loaders",
                                        "Run target validation and report unresolved source changes"],
                "locked_decisions": ["Do not commit or push; source diffs are supplied below",
                                     "New unmapped features require a scoped destination contract before implementation"],
                "risk": "medium", "validation_command": [a.format_map({"python": sys.executable,
                    "repo": ".", "resources": target["resource_root"],
                    "mc_jar": os.environ.get("BUILDSCAPE_SYNC_MC_JAR") or target.get("mc_jar", ""),
                    "gradle": "gradlew.bat" if os.name == "nt" else "./gradlew"})
                    for a in target["validation"][-1]],
                "source_changes": group}
        dump(output / (task["task_id"] + ".json"), task)
    return len(groups)


def default_output(repo=None):
    if os.environ.get("BUILDSCAPE_SYNC_OUTPUT"):
        return Path(os.environ["BUILDSCAPE_SYNC_OUTPUT"])
    # build/ is git-ignored, so reports never end up in a commit.
    return Path(repo or Path.cwd()) / "build" / "sync-reports"


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--repo", type=Path, default=Path.cwd())
    parser.add_argument("--registry", default="sync/targets.json")
    parser.add_argument("--source-repo", type=Path)
    sub = parser.add_subparsers(dest="command", required=True)
    for name in ("status", "plan", "apply", "briefs"):
        p = sub.add_parser(name)
        p.add_argument("--target", required=name != "status")
        p.add_argument("--fetch", action="store_true")
        p.add_argument("--output-dir", type=Path, required=name == "briefs")
        p.add_argument("--json", action="store_true")
        if name == "apply":
            p.add_argument("--mc-jar", type=Path)
    args = parser.parse_args(argv)
    repo = args.repo.resolve()
    try:
        config = registry(repo, args.registry)
        ids = [args.target] if args.target else [t["id"] for t in config["targets"] if t["status"] == "active"]
        # Fetch once per invocation. All targets see the same source ref.
        fetched_head = None
        if args.fetch:
            _, fetched_head = source_revision(repo, config, args.source_repo, fetch=True)
        results = []
        for target_id in ids:
            plan, target, branch_config, source_repo = make_plan(repo, config, target_id, args.registry,
                                                                args.source_repo, source_head=fetched_head)
            output = ((args.output_dir or default_output(args.repo)).resolve()
                      if args.command != "status" or args.output_dir else None)
            if args.command == "plan":
                write_report(plan, output)
            elif args.command == "apply":
                apply_plan(repo, plan, target, branch_config, source_repo, args.registry, output, args.mc_jar)
            elif args.command == "briefs":
                plan["brief_count"] = briefs(plan, target, output)
            elif args.output_dir:
                write_report(plan, output)
            results.append(plan)
        if args.json:
            print(json.dumps(results, indent=2))
        else:
            for plan in results:
                print(f"{plan['target']}: {len(plan['commits'])} source commits, {len(plan['changes'])} changed files, "
                      f"{len(plan['pending'])} outstanding reviews")
                for c in plan["changes"]:
                    print(f"  {c['change']:8} {c['classification']:27} {c['path']}")
        return 0
    except (SyncError, OSError, ValueError, KeyError, StopIteration) as exc:
        print("Sync failed: " + str(exc), file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
