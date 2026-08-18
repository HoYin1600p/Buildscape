package net.minecraft.client.gui.screens.worldselection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.WorldPresetTags;
import net.minecraft.util.FileUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelStorageException;
import org.jspecify.annotations.Nullable;

public class WorldCreationUiState {
   private static final Component DEFAULT_WORLD_NAME = Component.translatable("selectWorld.newWorld");
   private final List listeners = new ArrayList();
   private String name = DEFAULT_WORLD_NAME.getString();
   private WorldCreationUiState.SelectedGameMode gameMode = WorldCreationUiState.SelectedGameMode.SURVIVAL;
   private Difficulty difficulty = Difficulty.NORMAL;
   private @Nullable Boolean allowCommands;
   private String seed;
   private boolean generateStructures;
   private boolean bonusChest;
   private final Path savesFolder;
   private String targetFolder;
   private WorldCreationContext settings;
   private WorldCreationUiState.WorldTypeEntry worldType;
   private final List normalPresetList = new ArrayList();
   private final List altPresetList = new ArrayList();
   private GameRules gameRules;

   public WorldCreationUiState(final Path savesFolder, final WorldCreationContext settings, final Optional preset, final OptionalLong seed) {
      this.savesFolder = savesFolder;
      this.settings = settings;
      this.worldType = new WorldCreationUiState.WorldTypeEntry((Holder)findPreset(settings, preset).orElse((Object)null));
      this.updatePresetLists();
      this.seed = seed.isPresent() ? Long.toString(seed.getAsLong()) : "";
      this.generateStructures = settings.options().generateStructures();
      this.bonusChest = settings.options().generateBonusChest();
      this.targetFolder = this.findResultFolder(this.name);
      this.gameMode = settings.initialWorldCreationOptions().selectedGameMode();
      this.gameRules = new GameRules(settings.dataConfiguration().enabledFeatures());
      this.gameRules.setAll(settings.initialWorldCreationOptions().gameRuleOverwrites(), (MinecraftServer)null);
      Optional.ofNullable(settings.initialWorldCreationOptions().flatLevelPreset()).flatMap((key) -> settings.worldgenLoadContext().lookup(Registries.FLAT_LEVEL_GENERATOR_PRESET).flatMap((registry) -> registry.get(key))).map((reference) -> ((FlatLevelGeneratorPreset)reference.value()).settings()).ifPresent((generatorSettings) -> this.updateDimensions(PresetEditor.flatWorldConfigurator(generatorSettings)));
   }

   public void addListener(final Consumer action) {
      this.listeners.add(action);
   }

   public void onChanged() {
      boolean bonusChest = this.isBonusChest();
      if (bonusChest != this.settings.options().generateBonusChest()) {
         this.settings = this.settings.withOptions((options) -> options.withBonusChest(bonusChest));
      }

      boolean generateStructures = this.isGenerateStructures();
      if (generateStructures != this.settings.options().generateStructures()) {
         this.settings = this.settings.withOptions((options) -> options.withStructures(generateStructures));
      }

      for(Consumer listener : this.listeners) {
         listener.accept(this);
      }

   }

   public void setName(final String name) {
      this.name = name;
      this.targetFolder = this.findResultFolder(name);
      this.onChanged();
   }

   private String findResultFolder(final String name) {
      if (!Files.exists(this.savesFolder, new LinkOption[0])) {
         try {
            Files.createDirectory(this.savesFolder);
         } catch (IOException var6) {
            throw new LevelStorageException(Component.literal("Could not create save folder"), var6);
         }
      }

      String trimmedName = name.trim();

      try {
         return FileUtil.findAvailableName(this.savesFolder, !trimmedName.isEmpty() ? trimmedName : DEFAULT_WORLD_NAME.getString(), "");
      } catch (Exception var5) {
         try {
            return FileUtil.findAvailableName(this.savesFolder, "World", "");
         } catch (IOException var4) {
            throw new LevelStorageException(Component.literal("Could not create world folder"), var4);
         }
      }
   }

   public String getName() {
      return this.name;
   }

   public String getTargetFolder() {
      return this.targetFolder;
   }

   public void setGameMode(final WorldCreationUiState.SelectedGameMode gameMode) {
      this.gameMode = gameMode;
      this.onChanged();
   }

   public WorldCreationUiState.SelectedGameMode getGameMode() {
      return this.isDebug() ? WorldCreationUiState.SelectedGameMode.DEBUG : this.gameMode;
   }

   public void setDifficulty(final Difficulty difficulty) {
      this.difficulty = difficulty;
      this.onChanged();
   }

   public Difficulty getDifficulty() {
      return this.isHardcore() ? Difficulty.HARD : this.difficulty;
   }

   public boolean isHardcore() {
      return this.getGameMode() == WorldCreationUiState.SelectedGameMode.HARDCORE;
   }

   public void setAllowCommands(final boolean allowCommands) {
      this.allowCommands = allowCommands;
      this.onChanged();
   }

   public boolean isAllowCommands() {
      if (this.isDebug()) {
         return true;
      } else if (this.isHardcore()) {
         return false;
      } else if (this.allowCommands == null) {
         return this.getGameMode() == WorldCreationUiState.SelectedGameMode.CREATIVE;
      } else {
         return this.allowCommands;
      }
   }

   public void setSeed(final String seed) {
      this.seed = seed;
      this.settings = this.settings.withOptions((options) -> options.withSeed(WorldOptions.parseSeed(this.getSeed())));
      this.onChanged();
   }

   public String getSeed() {
      return this.seed;
   }

   public void setGenerateStructures(final boolean generateStructures) {
      this.generateStructures = generateStructures;
      this.onChanged();
   }

   public boolean isGenerateStructures() {
      return this.isDebug() ? false : this.generateStructures;
   }

   public void setBonusChest(final boolean bonusChest) {
      this.bonusChest = bonusChest;
      this.onChanged();
   }

   public boolean isBonusChest() {
      return !this.isDebug() && !this.isHardcore() ? this.bonusChest : false;
   }

   public void setSettings(final WorldCreationContext settings) {
      this.settings = settings;
      this.updatePresetLists();
      this.onChanged();
   }

   public WorldCreationContext getSettings() {
      return this.settings;
   }

   public void updateDimensions(final WorldCreationContext.DimensionsUpdater modifier) {
      this.settings = this.settings.withDimensions(modifier);
      this.onChanged();
   }

   protected boolean tryUpdateDataConfiguration(final WorldDataConfiguration newConfig) {
      WorldDataConfiguration oldConfig = this.settings.dataConfiguration();
      if (oldConfig.dataPacks().getEnabled().equals(newConfig.dataPacks().getEnabled()) && oldConfig.enabledFeatures().equals(newConfig.enabledFeatures())) {
         this.settings = new WorldCreationContext(this.settings.options(), this.settings.datapackDimensions(), this.settings.selectedDimensions(), this.settings.worldgenRegistries(), this.settings.dataPackResources(), newConfig, this.settings.initialWorldCreationOptions());
         return true;
      } else {
         return false;
      }
   }

   public boolean isDebug() {
      return this.settings.selectedDimensions().isDebug();
   }

   public void setWorldType(final WorldCreationUiState.WorldTypeEntry worldType) {
      this.worldType = worldType;
      Holder preset = worldType.preset();
      if (preset != null) {
         this.updateDimensions((registryAccess, dimensions) -> ((WorldPreset)preset.value()).createWorldDimensions());
      }

   }

   public WorldCreationUiState.WorldTypeEntry getWorldType() {
      return this.worldType;
   }

   public @Nullable PresetEditor getPresetEditor() {
      Holder preset = this.getWorldType().preset();
      return preset != null ? (PresetEditor)PresetEditor.EDITORS.get(preset.unwrapKey()) : null;
   }

   public List getNormalPresetList() {
      return this.normalPresetList;
   }

   public List getAltPresetList() {
      return this.altPresetList;
   }

   private void updatePresetLists() {
      Registry presetRegistry = this.getSettings().worldgenLoadContext().lookupOrThrow(Registries.WORLD_PRESET);
      this.normalPresetList.clear();
      this.normalPresetList.addAll((Collection)getNonEmptyList(presetRegistry, WorldPresetTags.NORMAL).orElseGet(() -> presetRegistry.listElements().map(WorldCreationUiState.WorldTypeEntry::new).toList()));
      this.altPresetList.clear();
      this.altPresetList.addAll((Collection)getNonEmptyList(presetRegistry, WorldPresetTags.EXTENDED).orElse(this.normalPresetList));
      Holder preset = this.worldType.preset();
      if (preset != null) {
         WorldCreationUiState.WorldTypeEntry newPreset = (WorldCreationUiState.WorldTypeEntry)findPreset(this.getSettings(), preset.unwrapKey()).map(WorldCreationUiState.WorldTypeEntry::new).orElse((WorldCreationUiState.WorldTypeEntry)this.normalPresetList.getFirst());
         boolean isCustomizablePreset = PresetEditor.EDITORS.get(preset.unwrapKey()) != null;
         if (isCustomizablePreset) {
            this.worldType = newPreset;
         } else {
            this.setWorldType(newPreset);
         }
      }

   }

   private static Optional findPreset(final WorldCreationContext settings, final Optional preset) {
      return preset.flatMap((k) -> settings.worldgenLoadContext().lookupOrThrow(Registries.WORLD_PRESET).get(k));
   }

   private static Optional getNonEmptyList(final Registry presetRegistry, final TagKey id) {
      return presetRegistry.get(id).map((tag) -> tag.stream().map(WorldCreationUiState.WorldTypeEntry::new).toList()).filter((l) -> !l.isEmpty());
   }

   public void setGameRules(final GameRules gameRules) {
      this.gameRules = gameRules;
      this.onChanged();
   }

   public GameRules getGameRules() {
      return this.gameRules;
   }

   public static enum SelectedGameMode {
      SURVIVAL("survival", GameType.SURVIVAL),
      HARDCORE("hardcore", GameType.SURVIVAL),
      CREATIVE("creative", GameType.CREATIVE),
      DEBUG("spectator", GameType.SPECTATOR);

      public final GameType gameType;
      public final Component displayName;
      private final Component info;

      private SelectedGameMode(final String name, final GameType gameType) {
         this.gameType = gameType;
         this.displayName = Component.translatable("selectWorld.gameMode." + name);
         this.info = Component.translatable("selectWorld.gameMode." + name + ".info");
      }

      public Component getInfo() {
         return this.info;
      }

      // $FF: synthetic method
      private static WorldCreationUiState.SelectedGameMode[] $values() {
         return new WorldCreationUiState.SelectedGameMode[]{SURVIVAL, HARDCORE, CREATIVE, DEBUG};
      }
   }

   public static record WorldTypeEntry(@Nullable Holder preset) {
      private static final Component CUSTOM_WORLD_DESCRIPTION = Component.translatable("generator.custom");

      public Component describePreset() {
         return (Component)Optional.ofNullable(this.preset).flatMap(Holder::unwrapKey).map((key) -> Component.translatable(key.identifier().toLanguageKey("generator"))).orElse(CUSTOM_WORLD_DESCRIPTION);
      }

      public boolean isAmplified() {
         return Optional.ofNullable(this.preset).flatMap(Holder::unwrapKey).filter((k) -> k.equals(WorldPresets.AMPLIFIED)).isPresent();
      }
   }
}
