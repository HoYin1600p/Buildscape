package com.kingodogo.buildscape.feature.pillar;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.client.PillarClientParticles;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.core.ParticlePatterns;
import com.kingodogo.buildscape.core.SyncedBlockEntity;
import com.kingodogo.buildscape.registry.ModBlockEntities;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class PillarBlockEntity extends SyncedBlockEntity {

    ItemStack displayedItem = ItemStack.EMPTY;

    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> new IItemHandler() {
        @Override
        public int getSlots() {
            return 1;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            if (slot == 0) {
                return getTopPillar().getDisplayedItem();
            }
            return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot != 0 || amount <= 0) {
                return ItemStack.EMPTY;
            }
            PillarBlockEntity top = getTopPillar();
            ItemStack displayed = top.getDisplayedItem();
            if (displayed.isEmpty()) {
                return ItemStack.EMPTY;
            }
            ItemStack result = displayed.copy();
            result.setCount(1);
            if (!simulate) {
                top.setDisplayedItem(ItemStack.EMPTY);
            }
            return result;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return false;
        }
    });

    long lastParticleTick = 0L;
    String particlePattern = null;

    Double patternSpeed = null;
    Double patternSpread = null;
    Double patternIntensity = null;
    private static final Pattern HEX_COLOR_PATTERN = Pattern
            .compile("^#[0-9A-Fa-f]{6}$");
    Integer maxParticleColor = null;

    String pillarId = null;

    List<String> particleColors = null;

    boolean colorsInitialized = false;

    int particleColorCounter = 0;

    int colorsVersion = 0;
    private final int lastSyncedColorsVersion = -1;

    private static final int MAX_SYNC_ATTEMPTS = 5;

    public static final int MAX_DYE_COLORS = 5;

    float facingYaw = 0.0f;

    static final String[] PATTERNS = {
            ParticlePatterns.NONE,
            ParticlePatterns.DEFAULT,
            ParticlePatterns.BEAM,
            ParticlePatterns.SPIRAL,
            ParticlePatterns.FOUNTAIN,
            ParticlePatterns.PULSE,
            ParticlePatterns.RING,
            ParticlePatterns.BURST,
            ParticlePatterns.SNOWFLAKE,
    };

    private static final String[] RAINBOW_COLORS = {
            "#FF0000",
            "#FF7F00",
            "#FFFF00",
            "#00FF00",
            "#0000FF",
            "#4B0082",
            "#9400D3",
    };

    public PillarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PILLAR_BLOCK_ENTITY.get(), pos, state);
    }

    private static final int SYNC_INTERVAL = 100;
    Boolean usePattern = null;

    public static void clientTick(
            Level level,
            BlockPos pos,
            BlockState state,
            PillarBlockEntity be
    ) {
        if (level == null || !level.isClientSide) return;
        if (!PillarClientParticles.isWithinRange(pos)) return;
        if (!be.hasDisplayItem()) return;

        long time = level.getGameTime();
        if ((time - be.lastParticleTick) < 5) return;

        PillarParticleConfig cfg = PillarParticleConfig.peek();
        if (cfg.particle_density <= 0) return;
        if (!cfg.matches(be.displayedItem)) return;

        String pattern = be.getParticlePattern();
        if (pattern == null || pattern.isEmpty() || ParticlePatterns.DEFAULT.equals(pattern)) {
            pattern = cfg.pattern != null ? cfg.pattern : ParticlePatterns.RING;
        }
        if (ParticlePatterns.NONE.equals(pattern)) return;
        PillarParticleSettings settings = new PillarParticleSettings(
                pattern,
                be.usePattern != null ? be.usePattern : cfg.use_pattern,
                be.patternSpeed != null ? be.patternSpeed : cfg.pattern_speed,
                be.patternSpread != null ? be.patternSpread : cfg.pattern_spread,
                be.patternIntensity != null ? be.patternIntensity : cfg.pattern_intensity);

        be.lastParticleTick = time;

        int baseCount = cfg.particle_density;
        int count = (int) Math.min(64L, Math.max(
                0L,
                settings.usePattern()
                        ? Math.round(baseCount * settings.intensity())
                        : baseCount
        ));
        if (count == 0) return;

        Random rand = level.random;
        double centerX = pos.getX() + 0.5;
        boolean isAshenKing = state.getBlock() instanceof AshenKingPillarBlock;
        double centerY = pos.getY() + (isAshenKing ? 0.75 : 1.0);
        double centerZ = pos.getZ() + 0.5;

        PillarClientParticles.spawnParticles(
                level,
                pos,
                be,
                cfg,
                settings,
                time,
                count,
                rand,
                centerX,
                centerY,
                centerZ
        );
    }

    private void syncPatternFromStack() {
        if (level == null || level.isClientSide)
            return;

        if (this.particlePattern != null) {
            propagatePatternToStack(this.particlePattern);
            return;
        }

        String stackPattern = getStackParticlePattern();
        if (stackPattern != null && !stackPattern.equals(this.particlePattern)) {
            this.particlePattern = stackPattern;
            this.setChanged();
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3);
        }
    }

    public ItemStack getDisplayedItem() {
        return displayedItem;
    }

    public void syncPatternFromManager() {
        if (level == null || level.isClientSide)
            return;

        if (level.getServer() == null ||
                !level.getServer().isRunning() ||
                !BuildScape.isServerFullyInitialized()) {
            return;
        }

        if (level.getServer().getPlayerList().getPlayerCount() == 0) {
            return;
        }

        if (!level.hasChunkAt(worldPosition)) {
            return;
        }

        try {
            ChunkAccess chunk = level.getChunk(
                    worldPosition);
            if (!(chunk instanceof LevelChunk)) {
                return;
            }
            if (!chunk
                    .getStatus()
                    .isOrAfter(ChunkStatus.FULL)) {
                return;
            }
        } catch (Exception e) {
            return;
        }

        PillarIdManager manager = PillarIdManager.get(level);

        if (!manager.hasLoaded()) {
            return;
        }

        String expectedPrefix = PillarIdManager.getVariantPrefix(
                level,
                worldPosition);

        String idToSync = this.pillarId;

        if (idToSync == null || idToSync.isEmpty()) {
            idToSync = getStackPillarId();
        }

        if (idToSync == null || idToSync.isEmpty()) {
            return;
        }

        if (!idToSync.startsWith(expectedPrefix) && manager.getPillarData(idToSync) == null) {
            return;
        }

        PillarIdManager.PillarData data = manager.getPillarData(idToSync);

        if (data != null) {
            boolean needsUpdate = false;

            String managerPattern = data.pattern;
            if (managerPattern != null && !managerPattern.isEmpty()) {
                boolean validPattern = false;
                for (String p : PATTERNS) {
                    if (p.equals(managerPattern)) {
                        validPattern = true;
                        break;
                    }
                }

                if (validPattern && (this.particlePattern == null || !this.particlePattern.equals(managerPattern))) {
                    this.particlePattern = managerPattern;
                    needsUpdate = true;
                }
            }

            if (data.pattern_speed != null
                    && (this.patternSpeed == null || !this.patternSpeed.equals(data.pattern_speed))) {
                this.patternSpeed = data.pattern_speed;
                needsUpdate = true;
            }
            if (data.pattern_spread != null
                    && (this.patternSpread == null || !this.patternSpread.equals(data.pattern_spread))) {
                this.patternSpread = data.pattern_spread;
                needsUpdate = true;
            }
            if (data.pattern_intensity != null
                    && (this.patternIntensity == null || !this.patternIntensity.equals(data.pattern_intensity))) {
                this.patternIntensity = data.pattern_intensity;
                needsUpdate = true;
            }

            if (needsUpdate) {
                this.setChanged();
                level.sendBlockUpdated(
                        worldPosition,
                        getBlockState(),
                        getBlockState(),
                        3);
            }
        }
    }

    public float getFacingYaw() {
        return facingYaw;
    }

    public void setFacingYaw(float yaw) {
        this.facingYaw = yaw % 360.0f;
        if (this.facingYaw < 0) {
            this.facingYaw += 360.0f;
        }
        this.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3);
        }
    }

    public void rotateFacing() {
        this.facingYaw = (this.facingYaw + 180.0f) % 360.0f;
        this.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3);
        }
    }

    public boolean hasDisplayItem() {
        return !displayedItem.isEmpty();
    }

    private static final int globalParticleColorCounter = 0;
    private final int clientSyncAttempts = 0;
    private int syncTickCounter = 0;
    private boolean registryReconciled;

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) {
            PillarIdManager manager = PillarIdManager.get(level);
            try {
                manager.load();
                manager.registerPillar(this);
            } catch (Exception e) {
            }
        }
    }

    public void setDisplayedItem(ItemStack stack) {
        this.displayedItem = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        this.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3);

            if (this.pillarId != null) {
                PillarIdManager.get(level).updateDisplayedItem(this.pillarId, stack.isEmpty() ? null
                        : ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
            } else {
                String stackId = getStackPillarId();
                if (stackId != null) {
                    PillarIdManager.get(level).updateDisplayedItem(stackId, stack.isEmpty() ? null
                            : ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
                } else if (!stack.isEmpty()) {
                    PillarIdManager.get(level).updateDisplayedItemByPosition(level, worldPosition,
                            ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
                }
            }
        }
    }

    public static void serverTick(
            Level level,
            BlockPos pos,
            BlockState state,
            PillarBlockEntity be) {
        PillarIdManager manager = PillarIdManager.get(level);
        if (!be.registryReconciled && manager.hasLoaded()
                && BuildScape.isServerFullyInitialized()) {
            manager.registerPillar(be);
            be.registryReconciled = true;
        }
        be.syncTickCounter++;
        if (be.syncTickCounter >= SYNC_INTERVAL) {
            be.syncTickCounter = 0;
            be.syncBothFromManager();
        }
    }

    public void setDisplayedItem(ItemStack stack, float facingYaw) {
        this.displayedItem = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        this.facingYaw = facingYaw % 360.0f;
        if (this.facingYaw < 0) {
            this.facingYaw += 360.0f;
        }
        this.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3);

            if (this.pillarId != null) {
                PillarIdManager.get(level).updateDisplayedItem(this.pillarId, stack.isEmpty() ? null
                        : ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
            } else {
                String stackId = getStackPillarId();
                if (stackId != null) {
                    PillarIdManager.get(level).updateDisplayedItem(stackId, stack.isEmpty() ? null
                            : ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
                } else if (!stack.isEmpty()) {
                    PillarIdManager.get(level).updateDisplayedItemByPosition(level, worldPosition,
                            ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
                }
            }
        }
    }

    private void syncBothFromManager() {
        if (level == null || level.isClientSide)
            return;

        if (level.getServer() == null ||
                !level.getServer().isRunning() ||
                !BuildScape.isServerFullyInitialized()) {
            return;
        }
        if (level.getServer().getPlayerList().getPlayerCount() == 0)
            return;
        if (!level.hasChunkAt(worldPosition))
            return;
        try {
            ChunkAccess chunk = level.getChunk(worldPosition);
            if (!(chunk instanceof LevelChunk))
                return;
            if (!chunk.getStatus().isOrAfter(ChunkStatus.FULL))
                return;
        } catch (Exception e) {
            return;
        }

        PillarIdManager manager = PillarIdManager.get(level);
        if (!manager.hasLoaded())
            return;

        String expectedPrefix = PillarIdManager.getVariantPrefix(level, worldPosition);
        String idToSync = (this.pillarId != null && !this.pillarId.isEmpty())
                ? this.pillarId
                : getStackPillarId();
        if (idToSync == null || idToSync.isEmpty())
            return;
        if (!idToSync.startsWith(expectedPrefix) && manager.getPillarData(idToSync) == null)
            return;

        PillarIdManager.PillarData data = manager.getPillarData(idToSync);
        boolean changed = false;

        if (data != null && data.hasColors()) {
            List<String> managerColors = data.getColors();
            boolean colorsDiffer = (this.particleColors == null ||
                    this.particleColors.isEmpty() ||
                    !idToSync.equals(this.pillarId) ||
                    managerColors.size() != this.particleColors.size());

            if (!colorsDiffer) {
                for (int i = 0; i < managerColors.size(); i++) {
                    String mc = managerColors.get(i), cc = this.particleColors.get(i);
                    if (mc == null || !mc.equals(cc)) {
                        colorsDiffer = true;
                        break;
                    }
                }
            }

            if (colorsDiffer) {
                this.pillarId = idToSync;
                this.particleColors = new ArrayList<>(managerColors);
                this.colorsInitialized = true;
                this.particleColorCounter = 0;
                this.lastParticleTick = 0;
                this.colorsVersion++;
                changed = true;
            }
        }

        if (data != null) {
            String managerPattern = data.pattern;
            if (managerPattern != null && !managerPattern.isEmpty()) {
                boolean validPattern = false;
                for (String p : PATTERNS) {
                    if (p.equals(managerPattern)) {
                        validPattern = true;
                        break;
                    }
                }
                if (validPattern && (this.particlePattern == null || !this.particlePattern.equals(managerPattern))) {
                    this.particlePattern = managerPattern;
                    changed = true;
                }
            } else if (this.particlePattern != null) {
                this.particlePattern = null;
                changed = true;
            }

            if (data.pattern_speed != null
                    && (this.patternSpeed == null || !this.patternSpeed.equals(data.pattern_speed))) {
                this.patternSpeed = data.pattern_speed;
                changed = true;
            } else if (data.pattern_speed == null && this.patternSpeed != null) {
                this.patternSpeed = null;
                changed = true;
            }

            if (data.pattern_spread != null
                    && (this.patternSpread == null || !this.patternSpread.equals(data.pattern_spread))) {
                this.patternSpread = data.pattern_spread;
                changed = true;
            } else if (data.pattern_spread == null && this.patternSpread != null) {
                this.patternSpread = null;
                changed = true;
            }

            if (data.pattern_intensity != null
                    && (this.patternIntensity == null || !this.patternIntensity.equals(data.pattern_intensity))) {
                this.patternIntensity = data.pattern_intensity;
                changed = true;
            } else if (data.pattern_intensity == null && this.patternIntensity != null) {
                this.patternIntensity = null;
                changed = true;
            }
        }

        if (changed) {
            this.setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public Double getPatternSpeed() {
        return patternSpeed;
    }

    public void setPatternSpeed(Double speed) {
        this.patternSpeed = speed;
        this.setChanged();
    }

    public Double getPatternSpread() {
        return patternSpread;
    }

    public void setPatternSpread(Double spread) {
        this.patternSpread = spread;
        this.setChanged();
    }

    public Double getPatternIntensity() {
        return patternIntensity;
    }

    public void setPatternIntensity(Double intensity) {
        this.patternIntensity = intensity;
        this.setChanged();
    }

    public Integer getMaxParticleColor() {
        return maxParticleColor;
    }

    public void setMaxParticleColor(Integer maxColor) {
        this.maxParticleColor = maxColor;
        this.setChanged();
    }

    private void updatePatternInConfig(String pattern) {
        if (level == null || level.isClientSide) {
            return;
        }

        PillarIdManager manager = PillarIdManager.get(level);
        BlockPos bottomPos = findStackBottom();
        PillarIdManager.PillarData data = manager.getOrCreatePillarData(level, bottomPos);

        if (data != null) {
            manager.syncPatternSettingsFromNBT(this, data);
            data.pattern = pattern;
            data.modifiedTime = System.currentTimeMillis();
            manager.requestSave();
        }
    }

    public void cycleParticlePattern() {
        PillarParticleConfig cfg = PillarParticleConfig.get();
        String currentPattern = getParticlePattern();

        if (currentPattern == null) {
            currentPattern = ParticlePatterns.DEFAULT;
        }

        int currentIndex = -1;
        for (int i = 0; i < PATTERNS.length; i++) {
            if (PATTERNS[i].equals(currentPattern)) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) {
            currentIndex = 0;
        }

        int nextIndex = (currentIndex + 1) % PATTERNS.length;
        setParticlePattern(PATTERNS[nextIndex]);
    }

    public void resetParticleTick() {
        resetParticleTick(false);
    }

    public void resetParticleTick(boolean useConfigColors) {
        this.lastParticleTick = 0L;
        this.particleColorCounter = 0;
        if (useConfigColors) {
            if (particleColors != null && !colorsInitialized) {
                this.particleColors = null;
            }
        }
    }

    public void syncColorsFromManager() {
        if (level == null || level.isClientSide)
            return;

        if (level.getServer() == null ||
                !level.getServer().isRunning() ||
                !BuildScape.isServerFullyInitialized()) {
            return;
        }

        if (level.getServer().getPlayerList().getPlayerCount() == 0) {
            return;
        }

        if (!level.hasChunkAt(worldPosition)) {
            return;
        }

        try {
            ChunkAccess chunk = level.getChunk(
                    worldPosition);
            if (!(chunk instanceof LevelChunk)) {
                return;
            }
            if (!chunk
                    .getStatus()
                    .isOrAfter(ChunkStatus.FULL)) {
                return;
            }
        } catch (Exception e) {
            return;
        }


        PillarIdManager manager = PillarIdManager.get(level);

        if (!manager.hasLoaded()) {
            return;
        }
        String expectedPrefix = PillarIdManager.getVariantPrefix(
                level,
                worldPosition);

        String idToSync = this.pillarId;

        if (idToSync == null || idToSync.isEmpty()) {
            idToSync = getStackPillarId();
        }

        if (idToSync == null || idToSync.isEmpty()) {
            return;
        }

        if (!idToSync.startsWith(expectedPrefix) && manager.getPillarData(idToSync) == null) {
            if (this.particleColors == null || this.particleColors.isEmpty()) {
                this.pillarId = null;
                this.particleColors = null;
                this.colorsInitialized = false;
                this.particleColorCounter = 0;
                this.setChanged();
                level.sendBlockUpdated(
                        worldPosition,
                        getBlockState(),
                        getBlockState(),
                        3);
            }
            return;
        }

        PillarIdManager.PillarData data = manager.getPillarData(idToSync);

        if (data != null && data.hasColors()) {
            List<String> managerColors = data.getColors();
            boolean shouldSync = false;

            if (this.pillarId == null || this.pillarId.isEmpty()) {
                shouldSync = true;
            } else if (!this.pillarId.equals(idToSync)) {
                shouldSync = true;
            } else if (this.particleColors == null || this.particleColors.isEmpty()) {
                shouldSync = true;
            } else {
                if (managerColors.size() != this.particleColors.size()) {
                    shouldSync = true;
                } else {
                    for (int i = 0; i < managerColors.size(); i++) {
                        String managerColor = managerColors.get(i);
                        String currentColor = this.particleColors.get(i);
                        if (managerColor == null || !managerColor.equals(currentColor)) {
                            shouldSync = true;
                            break;
                        }
                    }
                }
            }

            if (shouldSync) {
                this.pillarId = idToSync;
                this.particleColors = new ArrayList<>(managerColors);
                this.colorsInitialized = true;
                this.particleColorCounter = 0;
                this.lastParticleTick = 0;
                this.setChanged();

                level.sendBlockUpdated(
                        worldPosition,
                        getBlockState(),
                        getBlockState(),
                        3);
            }
        } else {
            if (this.pillarId != null && this.pillarId.equals(idToSync) &&
                    this.particleColors != null && !this.particleColors.isEmpty()) {
                BlockPos bottomPos = findStackBottom();
                PillarIdManager.PillarData newData = manager.getPillarDataByPosition(level, bottomPos);
                if (newData == null) {
                    newData = manager.getOrCreatePillarData(level, bottomPos);
                }
                if (newData != null && idToSync.equals(newData.id)) {
                    boolean needsSync = false;
                    if (!newData.hasColors()) {
                        needsSync = true;
                    } else {
                        List<String> managerColors = newData.getColors();
                        if (managerColors.size() != this.particleColors.size()) {
                            needsSync = true;
                        } else {
                            for (int i = 0; i < this.particleColors.size(); i++) {
                                String nbtColor = this.particleColors.get(i);
                                String managerColor = i < managerColors.size() ? managerColors.get(i) : null;
                                if (nbtColor == null || !nbtColor.equals(managerColor)) {
                                    needsSync = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (needsSync) {
                        newData.clearColors();
                        for (String color : this.particleColors) {
                            if (color != null && !color.isEmpty()) {
                                newData.addColor(color);
                            }
                        }
                    }
                }
            }
        }
    }

    public String getParticlePattern() {
        if (particlePattern != null && !particlePattern.isEmpty() && !particlePattern.equals(ParticlePatterns.DEFAULT)) {
            return particlePattern;
        }

        if (level != null) {
            String stackPattern = getStackParticlePattern();
            if (stackPattern != null && !stackPattern.isEmpty() && !stackPattern.equals(ParticlePatterns.DEFAULT)) {
                return stackPattern;
            }
        }

        return null;
    }

    public void setParticlePattern(String pattern) {
        this.particlePattern = pattern;
        this.setChanged();
        if (level != null && !level.isClientSide) {
            propagatePatternToStack(pattern);

            updatePatternInConfig(pattern);

            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3);
        }
    }

    public String getParticleColor(PillarParticleConfig cfg) {
        if (particleColors != null && !particleColors.isEmpty()) {
            int numColors = particleColors.size();
            int colorIndex = particleColorCounter % numColors;
            particleColorCounter++;

            String customColor = particleColors.get(colorIndex);
            if (customColor != null && HEX_COLOR_PATTERN.matcher(customColor).matches()) {
                return customColor.toUpperCase(Locale.ROOT);
            }
        }

        if (cfg == null) {
            return "#FFFFFF";
        }

        List<String> colorsToUse = cfg.particle_color;
        if (colorsToUse == null || colorsToUse.isEmpty()) {
            colorsToUse = new ArrayList<>();
            colorsToUse.add("#FFB81C");
            colorsToUse.add("#FFFFFF");
            colorsToUse.add("#FFFF00");
        }

        int maxColors = Math.max(
                1,
                Math.min(7, Math.min(cfg.max_particle_color, colorsToUse.size())));
        int colorIndex = particleColorCounter % maxColors;
        particleColorCounter++;
        if (colorIndex < 0 || colorIndex >= colorsToUse.size()) {
            colorIndex = 0;
        }

        String configColor = colorsToUse.get(colorIndex);
        if (configColor != null && HEX_COLOR_PATTERN.matcher(configColor).matches()) {
            return configColor.toUpperCase(Locale.ROOT);
        }
        return "#FFFFFF";
    }

    public boolean addParticleColor(String color) {
        if (color == null || color.isEmpty()) {
            return false;
        }

        String normalizedColor = color.toUpperCase(Locale.ROOT);
        if (!normalizedColor.startsWith("#")) {
            normalizedColor = "#" + normalizedColor;
        }

        if (!normalizedColor.matches("^#[0-9A-F]{6}$")) {
            return false;
        }

        if (level != null && !level.isClientSide) {
            PillarIdManager manager = PillarIdManager.get(level);
            BlockPos bottomPos = findStackBottom();

            String stackId = manager.addDyeColor(level, bottomPos, normalizedColor);

            if (stackId != null) {
                PillarIdManager.PillarData data = manager.getPillarData(stackId);
                if (data != null) {
                    propagateToStack(stackId, data.getColors());
                    return true;
                }
            }
            return false;
        }

        if (this.particleColors == null) {
            this.particleColors = new ArrayList<>();
        }
        if (this.particleColors.size() >= MAX_DYE_COLORS) {
            return false;
        }
        this.particleColors.add(normalizedColor);
        this.particleColorCounter = 0;
        this.lastParticleTick = 0;
        this.colorsInitialized = true;

        int currentColorCount = this.particleColors.size();
        if (this.maxParticleColor == null || this.maxParticleColor < currentColorCount) {
            this.maxParticleColor = Math.min(currentColorCount, MAX_DYE_COLORS);
        }

        this.setChanged();

        return true;
    }

    public Boolean getUsePattern() {
        return usePattern;
    }

    public void setUsePattern(Boolean use) {
        this.usePattern = use;
    }

    public void setParticleColor(String color) {
        if (this.particleColors != null) {
            this.particleColors.clear();
        }

        addParticleColor(color);
    }

    public String getPillarId() {
        return pillarId;
    }

    public void setPillarId(String id) {
        this.pillarId = id;
        this.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3);
        }
    }

    public boolean hasCustomColors() {
        return particleColors != null && !particleColors.isEmpty();
    }

    public int getDyeColorCount() {
        if (particleColors != null && !particleColors.isEmpty()) {
            return particleColors.size();
        }

        if (level != null && !level.isClientSide) {
            String stackId = getStackPillarId();
            if (stackId != null) {
                PillarIdManager.PillarData data = PillarIdManager.get(level)
                        .getPillarData(stackId);
                if (data != null) {
                    return data.getColorCount();
                }
            }
        }

        return 0;
    }

    public boolean canAddMoreColors() {
        return getDyeColorCount() < MAX_DYE_COLORS;
    }

    public void setParticleColors(List<String> colors) {
        if (colors == null || colors.isEmpty()) {
            this.particleColors = null;
            this.colorsInitialized = false;
        } else {
            this.particleColors = new ArrayList<>();
            int count = Math.min(7, colors.size());
            for (int i = 0; i < count; i++) {
                String color = colors.get(i);
                if (color != null && !color.isEmpty()) {
                    this.particleColors.add(color);
                }
            }
            if (this.particleColors.isEmpty()) {
                this.particleColors = null;
                this.colorsInitialized = false;
            } else {
                this.colorsInitialized = true;
            }
        }
        this.particleColorCounter = 0;
        this.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3);
        }
    }

    public void clearLocalStateOnly() {
        this.particleColors = null;
        this.pillarId = null;
        this.particleColorCounter = 0;
        this.colorsInitialized = false;
        this.setChanged();
    }

    public void resetToDefaultAppearance() {
        this.particleColors = null;
        this.particlePattern = null;
        this.patternSpeed = null;
        this.patternSpread = null;
        this.patternIntensity = null;
        this.usePattern = null;
        this.pillarId = null;
        this.particleColorCounter = 0;
        this.colorsInitialized = false;
        this.lastParticleTick = 0L;

        this.setChanged();

        if (level != null && !level.isClientSide) {
            if (level.getChunkAt(worldPosition) != null) {
                level.getChunkAt(worldPosition).setUnsaved(true);
            }

            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    public void syncFromData(PillarIdManager.PillarData data) {
        if (data == null)
            return;

        boolean changed = false;

        if (this.pillarId == null || !this.pillarId.equals(data.id)) {
            this.pillarId = data.id;
            changed = true;
        }

        if (data.hasColors()) {
            List<String> newColors = data.getColors();
            if (this.particleColors == null || this.particleColors.size() != newColors.size()) {
                this.particleColors = new ArrayList<>(newColors);
                this.colorsInitialized = true;
                this.particleColorCounter = 0;
                this.colorsVersion++;
                changed = true;
            } else {
                for (int i = 0; i < newColors.size(); i++) {
                    if (!newColors.get(i).equals(this.particleColors.get(i))) {
                        this.particleColors = new ArrayList<>(newColors);
                        this.colorsInitialized = true;
                        this.particleColorCounter = 0;
                        this.colorsVersion++;
                        changed = true;
                        break;
                    }
                }
            }
        } else if (this.particleColors != null) {
            this.particleColors = null;
            this.colorsInitialized = false;
            this.particleColorCounter = 0;
            this.colorsVersion++;
            changed = true;
        }

        if (data.pattern != null && !data.pattern.equals(this.particlePattern)) {
            this.particlePattern = data.pattern;
            changed = true;
        } else if (data.pattern == null && this.particlePattern != null) {
            this.particlePattern = null;
            changed = true;
        }

        if (data.use_pattern != null && (!data.use_pattern.equals(this.usePattern))) {
            this.usePattern = data.use_pattern;
            changed = true;
        } else if (data.use_pattern == null && this.usePattern != null) {
            this.usePattern = null;
            changed = true;
        }

        if (data.pattern_speed != null && (!data.pattern_speed.equals(this.patternSpeed))) {
            this.patternSpeed = data.pattern_speed;
            changed = true;
        } else if (data.pattern_speed == null && this.patternSpeed != null) {
            this.patternSpeed = null;
            changed = true;
        }

        if (data.pattern_spread != null && (!data.pattern_spread.equals(this.patternSpread))) {
            this.patternSpread = data.pattern_spread;
            changed = true;
        } else if (data.pattern_spread == null && this.patternSpread != null) {
            this.patternSpread = null;
            changed = true;
        }

        if (data.pattern_intensity != null && (!data.pattern_intensity.equals(this.patternIntensity))) {
            this.patternIntensity = data.pattern_intensity;
            changed = true;
        } else if (data.pattern_intensity == null && this.patternIntensity != null) {
            this.patternIntensity = null;
            changed = true;
        }

        if (data.max_particle_color != null && (!data.max_particle_color.equals(this.maxParticleColor))) {
            this.maxParticleColor = data.max_particle_color;
            changed = true;
        } else if (data.max_particle_color == null && this.maxParticleColor != null) {
            this.maxParticleColor = null;
            changed = true;
        }

        if (changed) {
            this.lastParticleTick = 0;
            this.setChanged();
            if (level != null && !level.isClientSide) {
                BlockState currentState = getBlockState();
                level.sendBlockUpdated(worldPosition, currentState, currentState, 3);
                level.getChunkAt(worldPosition).setUnsaved(true);
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        String oldColor = (particleColors != null && !particleColors.isEmpty())
                ? particleColors.get(0)
                : null;

        try {
            if (tag.contains(NbtKeys.DISPLAYED_ITEM, 10)) {
                ItemStack loaded = ItemStack.of(tag.getCompound(NbtKeys.DISPLAYED_ITEM));
                this.displayedItem = loaded == null ? ItemStack.EMPTY : loaded;
            }
            else if (tag.contains(NbtKeys.PILLAR_ITEM, 8)) {
                String itemId = tag.getString(NbtKeys.PILLAR_ITEM);
                try {
                    ResourceLocation itemLocation = new ResourceLocation(
                            itemId);
                    Item item = ForgeRegistries.ITEMS
                            .getValue(itemLocation);
                    if (item != null && item != Items.AIR) {
                        this.displayedItem = new ItemStack(item);
                    } else {
                        this.displayedItem = ItemStack.EMPTY;
                    }
                } catch (Exception e) {
                    this.displayedItem = ItemStack.EMPTY;
                }
            } else {
                this.displayedItem = ItemStack.EMPTY;
            }
        } catch (Exception e) {
            this.displayedItem = ItemStack.EMPTY;
        }

        if (tag.contains(NbtKeys.PARTICLE_PATTERN, 8)) {
            this.particlePattern = tag.getString(NbtKeys.PARTICLE_PATTERN);
            boolean valid = false;
            for (String p : PATTERNS) {
                if (p.equals(this.particlePattern)) {
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                this.particlePattern = null;
            }
        }
        else if (tag.contains(NbtKeys.PILLAR_PATTERN, 8)) {
            this.particlePattern = tag.getString(NbtKeys.PILLAR_PATTERN);
            boolean valid = false;
            for (String p : PATTERNS) {
                if (p.equals(this.particlePattern)) {
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                this.particlePattern = null;
            }
        } else {
            this.particlePattern = null;
        }

        if (tag.contains(NbtKeys.PATTERN_SPEED, 6)) {
            this.patternSpeed = tag.getDouble(NbtKeys.PATTERN_SPEED);
        } else {
            this.patternSpeed = null;
        }
        if (tag.contains(NbtKeys.PATTERN_SPREAD, 6)) {
            this.patternSpread = tag.getDouble(NbtKeys.PATTERN_SPREAD);
        } else {
            this.patternSpread = null;
        }
        if (tag.contains(NbtKeys.PATTERN_INTENSITY, 6)) {
            this.patternIntensity = tag.getDouble(NbtKeys.PATTERN_INTENSITY);
        } else {
            this.patternIntensity = null;
        }

        if (tag.contains(NbtKeys.MAX_PARTICLE_COLOR, 3)) {
            this.maxParticleColor = tag.getInt(NbtKeys.MAX_PARTICLE_COLOR);
        } else {
            this.maxParticleColor = null;
        }

        try {
            if (tag.contains(NbtKeys.PARTICLE_COLORS, 9)) {
                ListTag colorList = tag.getList(NbtKeys.PARTICLE_COLORS, 8);
                if (colorList.size() > 0) {
                    this.particleColors = new ArrayList<>();
                    int maxColors = Math.min(7, colorList.size());
                    for (int i = 0; i < maxColors; i++) {
                        try {
                            String color = colorList.getString(i);
                            if (color != null &&
                                    !color.isEmpty() &&
                                    color.matches("^#[0-9A-Fa-f]{6}$")) {
                                this.particleColors.add(color.toUpperCase(Locale.ROOT));
                            }
                        } catch (Exception e) {
                        }
                    }
                    if (this.particleColors.isEmpty()) {
                        this.particleColors = null;
                    }
                } else {
                    this.particleColors = null;
                }
            } else {
                this.particleColors = null;
            }
        } catch (Exception e) {
            this.particleColors = null;
        }

        if (tag.contains(NbtKeys.PARTICLE_COLOR_COUNTER, 3)) {
            this.particleColorCounter = tag.getInt(NbtKeys.PARTICLE_COLOR_COUNTER);
        } else {
            this.particleColorCounter = 0;
        }

        if (tag.contains(NbtKeys.COLORS_INITIALIZED, 1)) {
            this.colorsInitialized = tag.getBoolean(NbtKeys.COLORS_INITIALIZED);
        } else {
            this.colorsInitialized = (this.particleColors != null &&
                    !this.particleColors.isEmpty());
        }

        if (this.particleColors != null && !this.particleColors.isEmpty() && !this.colorsInitialized) {
            this.colorsInitialized = true;
        }

        if (tag.contains(NbtKeys.PILLAR_ID, 8)) {
            this.pillarId = tag.getString(NbtKeys.PILLAR_ID);
            if (this.pillarId.isEmpty()) {
                this.pillarId = null;
            }
        } else {
            this.pillarId = null;
        }

        if (tag.contains(NbtKeys.USE_PATTERN, 1)) {
            this.usePattern = tag.getBoolean(NbtKeys.USE_PATTERN);
        } else {
            this.usePattern = null;
        }

        if (tag.contains(NbtKeys.FACING_YAW, 5)) {
            this.facingYaw = tag.getFloat(NbtKeys.FACING_YAW);
            this.facingYaw = this.facingYaw % 360.0f;
            if (this.facingYaw < 0) {
                this.facingYaw += 360.0f;
            }
        } else {
            this.facingYaw = 0.0f;
        }

        String newColor = (particleColors != null && !particleColors.isEmpty())
                ? particleColors.get(0)
                : null;
        if (oldColor != newColor || (oldColor != null && !oldColor.equals(newColor))) {
            this.lastParticleTick = 0;
            this.particleColorCounter = 0;
        }

        if (pillarId != null &&
                !pillarId.isEmpty() &&
                (particleColors == null || particleColors.isEmpty())) {
            needsManagerSync = true;
        }
    }

    public void clearParticleColors() {
        String idToRemove = this.pillarId;

        clearLocalStateOnly();

        if (level != null && !level.isClientSide) {
            PillarIdManager manager = PillarIdManager.get(level);

            if (idToRemove != null && !idToRemove.isEmpty()) {
                manager.removePillar(idToRemove);
            }

            manager.removePillarByPosition(level, worldPosition);

            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3);
        }
    }

    public List<String> getParticleColors() {
        return particleColors;
    }

    public void forceSetColors(List<String> colors, String id) {
        if (colors == null || colors.isEmpty()) {
            return;
        }

        this.particleColors = new ArrayList<>(colors);
        this.pillarId = id;
        this.colorsInitialized = true;
        this.particleColorCounter = 0;
        this.lastParticleTick = 0;
        this.colorsVersion++;

        this.setChanged();

        if (level != null && !level.isClientSide) {
            BlockState currentState = getBlockState();
            level.sendBlockUpdated(worldPosition, currentState, currentState, 3);

            level.getChunkAt(worldPosition).setUnsaved(true);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        if (!displayedItem.isEmpty()) {
            tag.put(NbtKeys.DISPLAYED_ITEM, displayedItem.save(new CompoundTag()));
        }

        if (particlePattern != null) {
            tag.putString(NbtKeys.PARTICLE_PATTERN, particlePattern);
        }

        if (patternSpeed != null) {
            tag.putDouble(NbtKeys.PATTERN_SPEED, patternSpeed);
        }
        if (patternSpread != null) {
            tag.putDouble(NbtKeys.PATTERN_SPREAD, patternSpread);
        }
        if (patternIntensity != null) {
            tag.putDouble(NbtKeys.PATTERN_INTENSITY, patternIntensity);
        }

        if (usePattern != null) {
            tag.putBoolean(NbtKeys.USE_PATTERN, usePattern);
        }

        if (maxParticleColor != null) {
            tag.putInt(NbtKeys.MAX_PARTICLE_COLOR, maxParticleColor);
        }

        if (particleColors != null && !particleColors.isEmpty()) {
            ListTag colorList = new ListTag();
            for (String color : particleColors) {
                colorList.add(StringTag.valueOf(color));
            }
            tag.put(NbtKeys.PARTICLE_COLORS, colorList);
        }

        if (pillarId != null && !pillarId.isEmpty()) {
            tag.putString(NbtKeys.PILLAR_ID, pillarId);
        }

        tag.putInt(NbtKeys.PARTICLE_COLOR_COUNTER, particleColorCounter);

        tag.putBoolean(NbtKeys.COLORS_INITIALIZED, colorsInitialized);

        tag.putFloat(NbtKeys.FACING_YAW, facingYaw);
    }

    public void initializeDefaultColors() {
        if (!colorsInitialized && particleColors == null) {
            colorsInitialized = true;
            this.particleColorCounter = 0;
        }
    }

    boolean needsManagerSync = false;

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);

        if (needsManagerSync) {
            needsManagerSync = false;
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        if (!hasDisplayItem()) {
            return super.getRenderBoundingBox();
        }
        BlockPos pos = getBlockPos();
        return new AABB(
                pos.getX() - 2, pos.getY(), pos.getZ() - 2,
                pos.getX() + 3, pos.getY() + 8, pos.getZ() + 3);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);

        if (displayedItem.isEmpty() && !tag.contains(NbtKeys.DISPLAYED_ITEM)) {
            tag.put(NbtKeys.DISPLAYED_ITEM, ItemStack.EMPTY.save(new CompoundTag()));
        }

        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
        this.lastParticleTick = 0;
    }

    @Override
    public void onDataPacket(
            Connection net,
            ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            load(tag);
            this.lastParticleTick = 0;
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (!this.remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == null || side == Direction.DOWN) {
                return itemHandler.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }

    public BlockPos findStackTop() {
        return PillarStack.findStackTop(this);
    }

    private String getStackParticlePattern() {
        return PillarStack.getStackParticlePattern(this);
    }

    public BlockPos findStackBottom() {
        return PillarStack.findStackBottom(this);
    }

    public String getStackPillarId() {
        return PillarStack.getStackPillarId(this);
    }

    private void propagatePatternToStack(String pattern) {
        PillarStack.propagatePatternToStack(this, pattern);
    }

    private void propagateToStack(String stackId, List<String> colors) {
        PillarStack.propagateToStack(this, stackId, colors);
    }

    public PillarBlockEntity getTopPillar() {
        return PillarStack.getTopPillar(this);
    }
}
