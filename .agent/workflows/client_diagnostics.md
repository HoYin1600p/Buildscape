# Workflow: Client Execution & Diagnostics

Use this workflow to test client launch, diagnose mod loading failures, and inspect logs non-blockingly without hanging the development session.

---

## Important Notice on Mod Loading Crashes
When NeoForge or Forge encounters a mod loading error, Minecraft does not exit immediately. It renders an in-game graphical error screen ("Mod loading failures have occurred; consult the issue messages for more details"). The JVM continues to run and display this screen until a user clicks "Quit Game".

**Do NOT wait synchronously for the task to finish.** Always execute client runs asynchronously and monitor logs.

---

## Steps

### 1. Launch Client in Background
Start the client asynchronously using a small `WaitMsBeforeAsync` (e.g. 500ms):
```powershell
.\gradlew.bat :neoforge:1.21.1:runClient --offline --console=plain
```

### 2. Schedule Non-Blocking Inspection
Set a one-shot timer for 15–20 seconds to allow the client to pass early display and enter mod loading.

### 3. Inspect Runtime Logs
Read the tail of `run/logs/latest.log`:
```powershell
Get-Content -Path "neoforge/1.21.1/run/logs/latest.log" -Tail 60
```

### 4. Check for Key Milestones or Errors
- **Success Markers:**
  - `[SoundEngine/SOUNDS]: Sound engine started`
  - `[TextureAtlas/]: Created: 2048x1024x4 minecraft:textures/atlas/blocks.png-atlas`
  - `[AnimationLoader/]: Loaded 0 entity animations`
  *(Indicates the client has reached the Title Screen)*
- **Crash / Failure Markers:**
  - `[modloading-worker-0/FATAL]`
  - `CompletionException: java.lang.IllegalStateException: Trying to access unbound value ...`
  - `Registry is already frozen`
  - Inspect `run/crash-reports/` if an unhandled crash occurred:
    ```powershell
    Get-ChildItem -Path "neoforge/1.21.1/run/crash-reports" | Sort-Object LastWriteTime -Descending | Select-Object -First 1
    ```

### 5. Clean Termination
Once the status is confirmed (either title screen reached or error diagnosed), immediately cancel the background task using `manage_task` (`kill`) to free system resources and prevent screen lock.
