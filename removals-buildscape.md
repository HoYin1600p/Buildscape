# Buildscape GUI Editor & Panel Mover Removal Audit

This document outlines the complete inventory of temporary GUI editor screens, panel movers, coordinate serializers, adapter hooks, and localization keys intended for removal.

---

## 1. Executive Summary & Context

During early mod initialization and layout prototyping, a visual WYSIWYG GUI Editor subsystem was implemented to manually position, drag, resize, and inspect GUI panels and widgets across screen coordinates. This subsystem exported and loaded custom percentage-based coordinates (`percentX`, `percentY`, `percentWidth`, `percentHeight`) to and from per-tab `<TabName>-GUI.json` configuration files.

Once the hardcoded responsive layout engine (`WidgetLayoutHelper`, `scaleSize()`, `SIDEBAR_WIDTH_PERCENT`, and dynamic category recalculation) matured, this visual mover and editor became obsolete. However, its files, adapter hooks, reflection callers, and localization keys were left in the codebase.

### Scope Distinction: What Stays vs. What Goes

| Category | Components | Action | Rationale |
| :--- | :--- | :--- | :--- |
| **GUI Editor & Panel Mover** | `GuiEditorScreen`, `GuiConfigHelper`, `GuiConfigManager`, `GuiConfigData`, `createGuiEditorScreen` | **REMOVE** | Temporary layout initialization tool; orphaned and unused in production. |
| **Interactive Config Widgets** | `ColorPickerWidget`, `IntSliderWidget`, `CustomButton`, `CustomCheckBox`, `ItemSelectorWidget` | **KEEP** | Intended functional gameplay and configuration widgets used by player-facing tabs. |
| **Responsive Layout System** | `WidgetLayoutHelper`, `IScreenDelegate`, `AbstractConfigTab` | **KEEP** | Active responsive UI layout engine powering the config screen across all resolutions. |

---

## 2. Files Slated for Complete Deletion

The following 4 source files (totalling ~3,450 lines of code) belong exclusively to the temporary GUI editor and panel mover subsystem and should be completely deleted from the project:

### 1. `common/src/main/java/com/kingodogo/buildscape/client/screen/GuiEditorScreen.java`
- **Lines of Code:** 2,736 lines
- **Package:** `com.kingodogo.buildscape.client.screen`
- **Why It Needs To Be Removed:**
  - This is the interactive WYSIWYG editor screen.
  - It contains drag handles (`ToolboxMode.MOVE`, `EDIT`, `CREATE_WIDGET`, `CREATE_BACKGROUND`), a floating draggable toolbox, an 8-point drag-to-resize system for bounding boxes, a floating layer management panel, widget scanning routines, and JSON export/import actions.
  - It was used exclusively during early development to visually align widgets and save coordinate offsets. It has no runtime purpose for players or modpack configurators.

### 2. `common/src/main/java/com/kingodogo/buildscape/client/screen/GuiConfigHelper.java`
- **Lines of Code:** 506 lines
- **Package:** `com.kingodogo.buildscape.client.screen`
- **Why It Needs To Be Removed:**
  - Acts as the runtime layout adapter between `GuiConfigData` and Minecraft widgets (`AbstractWidget`, `Button`).
  - Contains methods such as `registerWidgetDefaults()`, `applyConfigToWidget()`, `findWidgetConfig()`, and `getWidgetBounds()`.
  - Its only consumer in the entire codebase is `GuiEditorScreen.java`. Once `GuiEditorScreen` is removed, this file is completely dead code.

### 3. `common/src/main/java/com/kingodogo/buildscape/config/GuiConfigData.java`
- **Lines of Code:** 114 lines
- **Package:** `com.kingodogo.buildscape.config`
- **Why It Needs To Be Removed:**
  - Data transfer model holding serialized element layouts (`ElementConfig` with `percentX`, `percentY`, `percentWidth`, `percentHeight`, `scale`, `color`, `visible`, `layer`) and screen dimensions (`ScreenConfig`).
  - Sole purpose is Gson serialization/deserialization of the editor's visual layouts. Not used by game configuration or pillar definitions.

### 4. `common/src/main/java/com/kingodogo/buildscape/config/GuiConfigManager.java`
- **Lines of Code:** 116 lines
- **Package:** `com.kingodogo.buildscape.config`
- **Why It Needs To Be Removed:**
  - File I/O manager that manages the `config/buildscape/gui/` folder and loads/saves `<TabName>-GUI.json` files.
  - Caches and writes `GuiConfigData` instances. Exists solely to persist GUI layouts produced by `GuiEditorScreen`.

---

## 3. Specific Methods, Fields & Dead Hooks in Remaining Classes

The following classes will remain in the codebase, but contain dead fields, methods, and hooks that directly reference the editor subsystem:

### 1. `common/src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java`

* **Field to Remove:**
  ```java
  // Line 147
  private Button editGuiButton;
  ```
  *Why:* Unused button field originally allocated to open `GuiEditorScreen`. Never added to the screen widget list during `init()`.

* **Resize Positioning Block to Remove:**
  ```java
  // Lines 366-369
  if (editGuiButton != null) {
      WidgetLayoutHelper.setPosition(editGuiButton, buttonX, this.height - scaleSize(60));
      editGuiButton.setWidth(buttonWidth);
  }
  ```
  *Why:* Dead code checking an uninitialized null reference during screen resize events.

* **Method to Remove:**
  ```java
  // Lines 582-587
  private void openGuiEditor() {
      if (activeTab != null) {
          String tabName = activeTab.getTabName();
          Services.PLATFORM.openScreen(Services.PLATFORM.createGuiEditorScreen(this.getWrapperScreen(), tabName, activeTab));
      }
  }
  ```
  *Why:* Uncalled private method designed to launch `GuiEditorScreen` via `IPlatformAdapter`.

---

### 2. `common/src/main/java/com/kingodogo/buildscape/client/screen/AbstractConfigTab.java` & Subclasses

* **Method in Base Class:**
  ```java
  // Line 80
  public String getTabName() {
      return this.getClass().getSimpleName();
  }
  ```
* **Overrides in Subclasses:**
  - `PillarIdDetailConfigTab.java:731`: `public String getTabName()`
  - `PillarIdsConfigTab.java:1835`: `public String getTabName()`
  - `PillarParticlesConfigTab.java:2082`: `public String getTabName()`
* **Why It Needs To Be Removed:**
  - `getTabName()` was only introduced to construct the JSON file name (`<tabName>-GUI.json`) and pass the string identifier to `GuiEditorScreen(parent, tabName, activeTab)`.
  - Normal tab navigation uses category button references and type checks (`instanceof PillarItemsConfigTab`, etc.). `getTabName()` is unused by any other gameplay or rendering logic.

---

### 3. Platform Adapter Contract: `common/src/main/java/com/kingodogo/buildscape/platform/IPlatformAdapter.java`

* **Default Interface Method to Remove:**
  ```java
  // Line 561
  default net.minecraft.client.gui.screens.Screen createGuiEditorScreen(net.minecraft.client.gui.screens.Screen parent, String tabName, com.kingodogo.buildscape.client.screen.AbstractConfigTab sourceTab) { return null; }
  ```
  *Why:* Neutral contract hook providing access to `GuiEditorScreen`. With the screen deleted, this contract method is obsolete.

---

### 4. VersionCluster Adapters

Each VersionCluster adapter implements the platform contract and contains routing logic for `createGuiEditorScreen`:

#### A. Target Cluster `v26x` (NeoForge 26.2 / Snapshot)
* **`common/src/main/java/com/kingodogo/buildscape/adapter/v26x/GuiProvider.java` (Lines 204-214):**
  - Remove `public static Screen createGuiEditorScreen(Screen parent, String tabName, AbstractConfigTab sourceTab)` (uses reflection to load `GuiEditorScreen`).
* **`common/src/main/java/com/kingodogo/buildscape/adapter/v26x/PlatformAdapterBase.java` (Line 2521):**
  - Remove `@Override public Screen createGuiEditorScreen(...)` delegation.
* **`common/src/main/java/com/kingodogo/buildscape/adapter/v26x/client/ClientPlatformHooks.java` (Lines 819-821):**
  - Remove `public static Screen createGuiEditorScreen(...)` delegation.

#### B. VersionCluster `v121x` (Minecraft 1.21.1)
* **`common/src/main/java/com/kingodogo/buildscape/adapter/v121x/client/GuiProvider.java` (Lines 199-201):**
  - Remove `public static Screen createGuiEditorScreen(...)`.
* **`common/src/main/java/com/kingodogo/buildscape/adapter/v121x/client/ClientAccess.java` (Lines 722-724):**
  - Remove `public static Screen createGuiEditorScreen(...)`.
* **`common/src/main/java/com/kingodogo/buildscape/adapter/v121x/PlatformAdapterBase.java` (Lines 3669-3671):**
  - Remove `@Override public Screen createGuiEditorScreen(...)`.

#### C. VersionCluster `v118x` (Forge 1.18.2)
* **`common/src/main/java/com/kingodogo/buildscape/adapter/v118x/GuiProvider.java` (Lines 188-190):**
  - Remove `public static Screen createGuiEditorScreen(...)`.
* **`common/src/main/java/com/kingodogo/buildscape/adapter/v118x/PlatformAdapterBase.java` (Lines 4056-4058):**
  - Remove `@Override public Screen createGuiEditorScreen(...)`.

---

## 4. Localization Keys to Clean Up

The following 16 localization keys in language resource files exist solely for `GuiEditorScreen` UI labels, instructions, dialog prompts, and buttons:

### Files:
- `common/src/main/resources/assets/buildscape/lang/en_us.json` (Lines 1661-1676)
- `common/src/v121x/resources/assets/buildscape/lang/en_us.json` (Lines 1696-1711)

### Keys to Delete:
```json
"buildscape.gui.editor.title": "GUI Editor - %s",
"buildscape.gui.editor.instructions": "Edit element positions, sizes, and scales. Use mouse wheel to scroll.",
"buildscape.gui.editor.instructions_visual": "Click to select, drag to move, drag corners/edges to resize. Right-click empty space to add widget.",
"buildscape.gui.editor.add_widget": "Add Widget",
"buildscape.gui.editor.add_widget_title": "Create New Widget",
"buildscape.gui.editor.enter_name": "Enter widget name:",
"buildscape.gui.editor.delete": "Delete Selected",
"buildscape.gui.editor.save": "Save",
"buildscape.gui.editor.reset": "Reset",
"buildscape.gui.editor.saved": "GUI layout saved for %s",
"buildscape.gui.editor.scantab": "Scan Current Tab for Widgets",
"buildscape.gui.editor.no_elements": "No GUI elements configured yet. Register widgets in your tab code first.",
"buildscape.gui.editor.no_elements_scan": "No GUI elements found. Click 'Scan Current Tab' to discover widgets.",
"buildscape.gui.editor.scanned": "Discovered %s widget(s). You can now edit them.",
"buildscape.gui.editor.nothing_found": "No widgets found in this tab.",
"buildscape.gui.editor.layers": "Layers"
```

*Why:* With `GuiEditorScreen` removed, these keys will never be queried. Removing them prevents dead strings in the translation assets.

---

## 5. Documentation & Ledger Maintenance

After removing the source files, update the migration ledger and project manifest to keep tracking synchronized:

1. **`migration/ledger.csv`**:
   - Update entries for:
     - `GuiConfigHelper.java` (Line 230)
     - `GuiEditorScreen.java` (Line 231)
     - `GuiConfigData.java` (Line 266)
     - `GuiConfigManager.java` (Line 267)
   - Mark status as `deleted` / `retired-editor-subsystem` or remove entries so they are not tracked as active migration counterparts.

2. **`buildscape-com.md`**:
   - Remove directory tree entries and descriptions for `GuiConfigHelper.java`, `GuiEditorScreen.java`, `GuiConfigData.java`, and `GuiConfigManager.java`.

---

## 6. Execution Order & Verification Strategy

When performing the actual deletion, follow this sequence to maintain a compiling state:

1. **Phase 1: Adapter Layer Pruning**
   - Remove `createGuiEditorScreen` from `IPlatformAdapter.java`.
   - Remove implementations in `v26x` (`GuiProvider.java`, `ClientPlatformHooks.java`, `PlatformAdapterBase.java`).
   - Remove implementations in `v121x` and `v118x`.
2. **Phase 2: Common Screen & Tab Cleanup**
   - Clean `BuildScapeConfigScreen.java` (`editGuiButton`, resize block, `openGuiEditor()`).
   - Remove `getTabName()` from `AbstractConfigTab.java`, `PillarIdDetailConfigTab.java`, `PillarIdsConfigTab.java`, and `PillarParticlesConfigTab.java`.
3. **Phase 3: File Deletions**
   - Delete `GuiEditorScreen.java`.
   - Delete `GuiConfigHelper.java`.
   - Delete `GuiConfigData.java`.
   - Delete `GuiConfigManager.java`.
4. **Phase 4: Resource & Metadata Cleanup**
   - Prune the 16 `buildscape.gui.editor.*` strings from `en_us.json` files.
   - Update `migration/ledger.csv` and `buildscape-com.md`.
5. **Phase 5: Offline Build Verification**
   - Run compilation offline for target cluster:
     ```powershell
     ./gradlew compileJava compileV26xJava --offline --console=plain
     ```
   - Verify that all active config screens and tabs (`PillarItemsConfigTab`, `PillarParticlesConfigTab`, `PillarIdsConfigTab`, `WorldSettingsConfigTab`) build cleanly without reference errors.
