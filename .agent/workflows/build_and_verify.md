# Workflow: Build & Verify

Use this workflow to compile, build, and verify all Buildscape targets offline.

---

## Steps

### 1. Multi-VersionCluster Compilation Check
Verify that all three VersionClusters compile without errors:
```powershell
.\gradlew.bat :common:compileV118xJava :common:compileV121xJava :common:compileV26xJava --offline --console=plain
```

### 2. Loader Compilation Check
Verify compilation of the active loader subproject (e.g. NeoForge 1.21.1):
```powershell
.\gradlew.bat :neoforge:1.21.1:compileJava --offline --console=plain
```

### 3. Verify VersionCluster Wiring
Run the wiring verification script to ensure all 11 subprojects are bound to their respective VersionCluster configurations:
```powershell
powershell -ExecutionPolicy Bypass -File scripts/test-era-wiring.ps1
```

### 4. Full Build (Optional/Comprehensive)
Build all subprojects across Forge, Fabric, and NeoForge:
```powershell
.\gradlew.bat build -x test --offline --console=plain
```
