---
trigger: always_on
description: Master DOs and DONTs for agent work on the Buildscape repository.
---

# Buildscape Agent Rules: Master DOs and DONTs

All AI coding assistants (Antigravity, Codex, Claude Code) operating in this repository MUST strictly follow these rules without exception.

---

## 1. Top Invariants: DOs (What You MUST Do)

1. **Use `VersionCluster` and `Adapter` Terminology:**
   - Refer to version targets (`v118x`, `v121x`, `v26x`) strictly as **VersionClusters** or **Adapters**.
   - In Gradle scripts, use `versionCluster` or `activeCluster` (never `activeEra` or `era`).

2. **Enforce Common Neutrality:**
   - Keep code under `common/src/main/java/com/kingodogo/buildscape/` (outside `adapter/`) 100% loader-neutral and version-neutral.
   - Route all divergent Minecraft engine interactions through `Services.PLATFORM`, `MixinFactory`, or dedicated VersionCluster factories.

3. **Wrap All Registry Actions Safely:**
   - Any registration or block/item/block-entity instantiation MUST be executed inside:
     ```java
     Services.PLATFORM.wrapRegistryAction(() -> { ... });
     ```
   - Always preserve and check `prevHolders` to prevent nested `wrapRegistryAction` calls from prematurely destroying `unregisteredIntrusiveHolders`.
   - Only activate intrusive holder maps on registries that use intrusive holders (`BLOCK`, `ITEM`, `BLOCK_ENTITY_TYPE`, `ENTITY_TYPE`, `FLUID`). Standard registries (`BLOCKSTATE_PROVIDER_TYPE`, `CUSTOM_STAT`, `PARTICLE_TYPE`) must never have intrusive holder maps attached.

4. **Maintain a Single Unified Mixin Config:**
   - Maintain all mixin registrations inside `common/src/main/resources/buildscape.mixins.json`.
   - Use `@Dynamic` with `require = 0` for methods that only exist on specific versions.
   - Delegate version-sensitive bytecode manipulation to `MixinFactory.get().<method>()`.

5. **Always Run Gradle Offline:**
   - Always append `--offline --console=plain` to every `./gradlew` command.
   - Leverage daemon processes and parallel compilation where available.

6. **Monitor Client Runs Non-Blockingly:**
   - When launching `:neoforge:1.21.1:runClient`, always run as a background task.
   - Monitor `run/logs/latest.log` periodically using non-blocking timers.
   - Once verified (e.g., reaching Title Screen or detecting a startup exception), cleanly terminate the background task so developer resources and display focus are preserved.

7. **Modular Tools in `tools/kyro`:**
   - Always check `$REPOS/claude-skills/tools/kyro/` first before creating utilities.
   - Store diagnostic, analysis, or workspace automation modularly under `tools/kyro/`.

8. **Public Identity & Privacy Standards:**
   - Resolve user identity dynamically via `userDefinitions.md` (`@userHandle`, `@gitAuthor`, `@userEmail`).
   - Treat real names in `private-identity.txt` as release-blocking; never write them into tracked files, logs, or commit messages.

---

## 2. Strict Invariants: DONTs (What You MUST NEVER Do)

1. **NO Destructive Host Commands:**
   - NEVER execute unconstrained recursive deletions: `rm -rf /`, `rm -rf /*`, `rmdir /s /q C:\`, `del /f /s /q C:\*`, `Remove-Item C:\* -Recurse`, or code equivalents (`shutil.rmtree("/")`).
   - All cleanup operations must be strictly bounded within the project repository root. Path arguments must be verified for presence and non-emptiness before deletion.
   - **Millennium Live-Service Host Protection:** Never reboot, shut down, reset, restart, stop, kill, or disrupt `millennium`, `millennium-codex`, `ns16063.heavynodeusercontent.com`, its Minecraft servers/proxies, or Pterodactyl services without express operator instructions.

2. **NO Stubs or Fake Implementations:**
   - NEVER create empty stub classes or methods containing `throw new UnsupportedOperationException()`.
   - NEVER create dummy no-op implementations to appease the compiler. Resolve the proper VersionCluster adapter API.

3. **NO Temporary Exclusions or Commented Code:**
   - NEVER exclude problematic classes from compilation tasks (`exclude '**/*Mixin.java'`).
   - NEVER comment out real features, blocks, or renderers because of a compile failure. Fix the underlying contract.

4. **NO Loader Imports in Common Neutral Code:**
   - Prohibited in common neutral code: `net.minecraftforge.*`, `net.fabricmc.*`, `net.neoforged.*`.
   - Loader code belongs strictly in `forge/`, `fabric/`, and `neoforge/` subprojects.

5. **NO "Era" Terminology:**
   - NEVER use the word "Era" or "activeEra" in code, build scripts, variable names, or documentation. Use **`VersionCluster`** or **`Adapter`**.

6. **NO AI Attribution:**
   - NEVER add AI attribution to any commit, PR, git tag, release, or published file:
     - NO `Co-Authored-By: Claude / Antigravity / Gemini / Codex`.
     - NO "Generated with..." or "Assisted by..." lines.
   - Commit using `@gitAuthor` with plain, professional commit messages.

7. **NO Throwaway One-Off Scripts:**
   - Avoid creating throwaway Python or shell scripts and then abandoning them.
