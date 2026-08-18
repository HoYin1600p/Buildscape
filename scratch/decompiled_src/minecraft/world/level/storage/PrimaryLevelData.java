package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.CrashReportCategory;
import net.minecraft.SharedConstants;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.Util;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class PrimaryLevelData implements ServerLevelData, WorldData {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String LEVEL_NAME = "LevelName";
   protected static final String OLD_PLAYER = "Player";
   protected static final String SINGLEPLAYER_UUID = "singleplayer_uuid";
   protected static final String OLD_WORLD_GEN_SETTINGS = "WorldGenSettings";
   private LevelSettings settings;
   private final PrimaryLevelData.SpecialWorldProperty specialWorldProperty;
   private final Lifecycle worldGenSettingsLifecycle;
   private LevelData.RespawnData respawnData;
   private long gameTime;
   private final @Nullable UUID singlePlayerUUID;
   private final int version;
   private boolean initialized;
   private final Set knownServerBrands;
   private boolean wasModded;
   private final Set removedFeatureFlags;
   private List versionHistory;

   private PrimaryLevelData(final @Nullable UUID singlePlayerUUID, final boolean wasModded, final LevelData.RespawnData respawnData, final long gameTime, final int version, final boolean initialized, final Set knownServerBrands, final Set removedFeatureFlags, final LevelSettings settings, final PrimaryLevelData.SpecialWorldProperty specialWorldProperty, final Lifecycle worldGenSettingsLifecycle, final List versionHistory) {
      this.wasModded = wasModded;
      this.respawnData = respawnData;
      this.gameTime = gameTime;
      this.version = version;
      this.initialized = initialized;
      this.knownServerBrands = knownServerBrands;
      this.removedFeatureFlags = removedFeatureFlags;
      this.singlePlayerUUID = singlePlayerUUID;
      this.settings = settings;
      this.specialWorldProperty = specialWorldProperty;
      this.worldGenSettingsLifecycle = worldGenSettingsLifecycle;
      this.versionHistory = versionHistory;
   }

   public PrimaryLevelData(final LevelSettings levelSettings, final PrimaryLevelData.SpecialWorldProperty specialWorldProperty, final Lifecycle lifecycle) {
      this((UUID)null, false, LevelData.RespawnData.DEFAULT, 0L, 19133, false, Sets.newLinkedHashSet(), new HashSet(), levelSettings.copy(), specialWorldProperty, lifecycle, List.of());
   }

   public static PrimaryLevelData parse(final Dynamic input, final LevelSettings settings, final PrimaryLevelData.SpecialWorldProperty specialWorldProperty, final Lifecycle worldGenSettingsLifecycle) {
      long gameTime = input.get("Time").asLong(0L);
      LevelVersion levelVersion = LevelVersion.parse(input);
      return new PrimaryLevelData((UUID)input.get("singleplayer_uuid").flatMap(UUIDUtil.CODEC::parse).result().orElse((Object)null), input.get("WasModded").asBoolean(false), (LevelData.RespawnData)input.get("spawn").read(LevelData.RespawnData.CODEC).result().orElse(LevelData.RespawnData.DEFAULT), gameTime, levelVersion.levelDataVersion(), input.get("initialized").asBoolean(true), (Set)input.get("ServerBrands").asStream().flatMap((b) -> b.asString().result().stream()).collect(Collectors.toCollection(Sets::newLinkedHashSet)), (Set)input.get("removed_features").asStream().flatMap((b) -> b.asString().result().stream()).collect(Collectors.toSet()), settings, specialWorldProperty, worldGenSettingsLifecycle, input.get("version_history").orElseEmptyList().asList((d) -> d.asInt(-1)));
   }

   public CompoundTag createTag(@Nullable UUID singlePlayerUUID) {
      if (singlePlayerUUID == null) {
         singlePlayerUUID = this.singlePlayerUUID;
      }

      CompoundTag tag = new CompoundTag();
      this.setTagData(tag, singlePlayerUUID);
      return tag;
   }

   private void setTagData(final CompoundTag tag, final @Nullable UUID singlePlayerUUID) {
      tag.put("ServerBrands", stringCollectionToTag(this.knownServerBrands));
      tag.putBoolean("WasModded", this.wasModded);
      if (!this.removedFeatureFlags.isEmpty()) {
         tag.put("removed_features", stringCollectionToTag(this.removedFeatureFlags));
      }

      writeVersionTag(tag);
      this.writeVersionHistory(tag);
      NbtUtils.addCurrentDataVersion(tag);
      tag.putInt("GameType", this.settings.gameType().getId());
      tag.store("spawn", LevelData.RespawnData.CODEC, this.respawnData);
      tag.putLong("Time", this.gameTime);
      writeLastPlayed(tag);
      tag.putString("LevelName", this.settings.levelName());
      tag.putInt("version", 19133);
      tag.putBoolean("allowCommands", this.settings.allowCommands());
      tag.putBoolean("initialized", this.initialized);
      tag.store("difficulty_settings", LevelSettings.DifficultySettings.CODEC, this.settings.difficultySettings());
      if (singlePlayerUUID != null) {
         tag.storeNullable("singleplayer_uuid", UUIDUtil.CODEC, singlePlayerUUID);
      }

      tag.store(WorldDataConfiguration.MAP_CODEC, this.settings.dataConfiguration());
   }

   public static void writeLastPlayed(final CompoundTag tag) {
      tag.putLong("LastPlayed", Util.getEpochMillis());
   }

   public static Dynamic writeLastPlayed(final Dynamic tag) {
      return tag.set("LastPlayed", tag.createLong(Util.getEpochMillis()));
   }

   public static void writeVersionTag(final CompoundTag tag) {
      CompoundTag worldVersion = new CompoundTag();
      worldVersion.putString("Name", SharedConstants.getCurrentVersion().name());
      worldVersion.putInt("Id", SharedConstants.getCurrentVersion().dataVersion().version());
      worldVersion.putBoolean("Snapshot", !SharedConstants.getCurrentVersion().stable());
      worldVersion.putString("Series", SharedConstants.getCurrentVersion().dataVersion().series());
      tag.put("Version", worldVersion);
   }

   public static Dynamic writeVersionTag(final Dynamic tag) {
      Dynamic worldVersion = tag.emptyMap().set("Name", tag.createString(SharedConstants.getCurrentVersion().name())).set("Id", tag.createInt(SharedConstants.getCurrentVersion().dataVersion().version())).set("Snapshot", tag.createBoolean(!SharedConstants.getCurrentVersion().stable())).set("Series", tag.createString(SharedConstants.getCurrentVersion().dataVersion().series()));
      return tag.set("Version", worldVersion);
   }

   public void writeVersionHistory(final CompoundTag tag) {
      ListTag list = new ListTag();
      this.versionHistory.stream().map(IntTag::valueOf).forEach(list::add);
      int currentVersion = SharedConstants.getCurrentVersion().dataVersion().version();
      if (this.versionHistory.isEmpty() || this.versionHistory.getLast() != currentVersion) {
         list.add(IntTag.valueOf(currentVersion));
      }

      tag.put("version_history", list);
   }

   private static ListTag stringCollectionToTag(final Set values) {
      ListTag result = new ListTag();
      values.stream().map(StringTag::valueOf).forEach(result::add);
      return result;
   }

   public LevelData.RespawnData getRespawnData() {
      return this.respawnData;
   }

   public long getGameTime() {
      return this.gameTime;
   }

   public @Nullable UUID getSinglePlayerUUID() {
      return this.singlePlayerUUID;
   }

   public void setGameTime(final long time) {
      this.gameTime = time;
   }

   public void setSpawn(final LevelData.RespawnData respawnData) {
      this.respawnData = respawnData;
   }

   public String getLevelName() {
      return this.settings.levelName();
   }

   public int getVersion() {
      return this.version;
   }

   public GameType getGameType() {
      return this.settings.gameType();
   }

   public void setGameType(final GameType gameType) {
      this.settings = this.settings.withGameType(gameType);
   }

   public boolean isHardcore() {
      return this.settings.difficultySettings().hardcore();
   }

   public boolean isAllowCommands() {
      return this.settings.allowCommands();
   }

   public void setAllowCommands(final boolean allowCommands) {
      this.settings = this.settings.withAllowCommands(allowCommands);
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   public void setInitialized(final boolean initialized) {
      this.initialized = initialized;
   }

   public Difficulty getDifficulty() {
      return this.settings.difficultySettings().difficulty();
   }

   public void setDifficulty(final Difficulty difficulty) {
      this.settings = this.settings.withDifficulty(difficulty);
   }

   public boolean isDifficultyLocked() {
      return this.settings.difficultySettings().locked();
   }

   public void setDifficultyLocked(final boolean difficultyLocked) {
      this.settings = this.settings.withDifficultyLock(difficultyLocked);
   }

   public void fillCrashReportCategory(final CrashReportCategory category, final LevelHeightAccessor levelHeightAccessor) {
      ServerLevelData.super.fillCrashReportCategory(category, levelHeightAccessor);
      WorldData.super.fillCrashReportCategory(category);
   }

   public boolean isFlatWorld() {
      return this.specialWorldProperty == PrimaryLevelData.SpecialWorldProperty.FLAT;
   }

   public boolean isDebugWorld() {
      return this.specialWorldProperty == PrimaryLevelData.SpecialWorldProperty.DEBUG;
   }

   public Lifecycle worldGenSettingsLifecycle() {
      return this.worldGenSettingsLifecycle;
   }

   public WorldDataConfiguration getDataConfiguration() {
      return this.settings.dataConfiguration();
   }

   public void setDataConfiguration(final WorldDataConfiguration dataConfiguration) {
      this.settings = this.settings.withDataConfiguration(dataConfiguration);
   }

   public void setModdedInfo(final String serverBrand, final boolean isModded) {
      this.knownServerBrands.add(serverBrand);
      this.wasModded |= isModded;
   }

   public boolean wasModded() {
      return this.wasModded;
   }

   public Set getKnownServerBrands() {
      return ImmutableSet.copyOf(this.knownServerBrands);
   }

   public Set getRemovedFeatureFlags() {
      return Set.copyOf(this.removedFeatureFlags);
   }

   public ServerLevelData overworldData() {
      return this;
   }

   public LevelSettings getLevelSettings() {
      return this.settings.copy();
   }

   /** @deprecated */
   @Deprecated
   public static enum SpecialWorldProperty {
      NONE,
      FLAT,
      DEBUG;

      // $FF: synthetic method
      private static PrimaryLevelData.SpecialWorldProperty[] $values() {
         return new PrimaryLevelData.SpecialWorldProperty[]{NONE, FLAT, DEBUG};
      }
   }
}
