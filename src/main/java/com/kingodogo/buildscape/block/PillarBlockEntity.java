package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PillarBlockEntity extends BlockEntity {

    private ItemStack displayedItem = ItemStack.EMPTY;
    private long lastParticleTick = 0L;
    private String particlePattern = null;
    
    // Per-pillar pattern settings (null means use global config)
    private Double patternSpeed = null;
    private Double patternSpread = null;
    private Double patternIntensity = null;
    // Compiled pattern for hex colour validation — avoids re-compiling the regex
    // on every call to getParticleColor().
    private static final java.util.regex.Pattern HEX_COLOR_PATTERN =
            java.util.regex.Pattern.compile("^#[0-9A-Fa-f]{6}$");
    private Integer maxParticleColor = null; // Max number of colors for this pillar (1-5, null means use global config)

    private String pillarId = null;

    private java.util.List<String> particleColors = null;

    private boolean colorsInitialized = false;

    private int particleColorCounter = 0;

    /** Monotonically increasing version counter: incremented on every color/pattern
     *  change so that syncColorsFromManager() can skip its O(n) list-equality
     *  walk when nothing has changed since the last sync. */
    private int colorsVersion = 0;
    private final int lastSyncedColorsVersion = -1; // version at last successful sync

    private static final int MAX_SYNC_ATTEMPTS = 5;

    public static final int MAX_DYE_COLORS = 5;

    private float facingYaw = 0.0f;


    private static final String[] PATTERNS = {
            "none",
            "default",
            "beam",
            "spiral",
            "fountain",
            "pulse",
            "ring",
            "burst",
            "snowflake",
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
    private static final int SYNC_INTERVAL = 100; // Sync every 100 ticks (5 seconds) — colors/patterns change rarely
    private Boolean usePattern = null; // PER-PILLAR OVERRIDE for cfg.use_pattern

    @OnlyIn(Dist.CLIENT)
    public static void clientTick(
            net.minecraft.world.level.Level level,
            BlockPos pos,
            BlockState state,
            PillarBlockEntity be
    ) {
        if (level == null || !level.isClientSide) return;
        if (!be.hasDisplayItem()) return;

        long time = level.getGameTime();
        if ((time - be.lastParticleTick) < 5) return;

        // Use peek() — zero I/O, zero reflection; returns the last-known config
        // snapshot.  get() is still called the first time and lazily whenever
        // the watcher thread reloads, so hot-reloading still works.
        PillarParticleConfig cfg = PillarParticleConfig.peek();
        if (!cfg.matches(be.displayedItem)) return;

        // Early-out: skip all work if the per-pillar or global pattern is "none"
        String earlyPattern = be.getParticlePattern();
        if (earlyPattern == null) earlyPattern = cfg.pattern;
        if ("none".equals(earlyPattern)) return;

        be.lastParticleTick = time;

        int baseCount = cfg.particle_density;
        int count = Math.max(
                1,
                cfg.use_pattern
                        ? (int) Math.round(baseCount * cfg.pattern_intensity)
                        : baseCount
        );

        java.util.Random rand = level.random;
        double centerX = pos.getX() + 0.5;
        boolean isAshenKing = state.getBlock() instanceof AshenKingPillarBlock;
        double centerY = pos.getY() + (isAshenKing ? 0.75 : 1.0);
        double centerZ = pos.getZ() + 0.5;

        ClientParticleHelper.spawnParticles(
                level,
                pos,
                be,
                cfg,
                time,
                count,
                rand,
                centerX,
                centerY,
                centerZ
        );
    }

    private void syncPatternFromStack() {
        if (level == null || level.isClientSide) return;

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
                    3
            );
        }
    }

    public ItemStack getDisplayedItem() {
        return displayedItem;
    }

    @OnlyIn(Dist.CLIENT)
    private static ParticleSpawnData calculateParticleData(
            PillarBlockEntity be,
            PillarParticleConfig cfg,
            long time,
            int i,
            int count,
            java.util.Random rand
    ) {
        double sx, sy, sz, vx, vy, vz;
        float size = 1.0f;

        // Check for per-pillar 'use_pattern' override first, then global config
        boolean usePattern = be.usePattern != null ? be.usePattern : cfg.use_pattern;

        if (usePattern) {
            String pattern = be.getParticlePattern();
            if (pattern == null || pattern.isEmpty() || "default".equals(pattern)) {
                pattern = cfg.pattern != null ? cfg.pattern : "ring";
            }

            // Handle "none" pattern - no particles
            if ("none".equals(pattern)) {
                return null; // Return null to skip particle spawning
            }

            // Use pillar-specific pattern settings if available, otherwise use global config
            double patternSpeed = be.patternSpeed != null ? be.patternSpeed : cfg.pattern_speed;
            double patternIntensity = be.patternIntensity != null ? be.patternIntensity : cfg.pattern_intensity;
            double patternSpread = be.patternSpread != null ? be.patternSpread : cfg.pattern_spread;
            double speed = patternSpeed * patternIntensity;
            double spread = patternSpread;

            switch (pattern) {
                case "beam":
                    sx = (rand.nextDouble() - 0.5) * spread * 0.3;
                    sy = 0.0;
                    sz = (rand.nextDouble() - 0.5) * spread * 0.3;
                    vx = (rand.nextDouble() - 0.5) * speed * 0.2;
                    vy = speed * (0.8 + rand.nextDouble() * 0.4);
                    vz = (rand.nextDouble() - 0.5) * speed * 0.2;
                    break;
                case "spiral":
                    double angle =
                            (time * 0.1 + (i * 2.0 * Math.PI) / count) % (2.0 * Math.PI);
                    double radius = spread * 0.5;
                    sx = Math.cos(angle) * radius;
                    sy = 0.0;
                    sz = Math.sin(angle) * radius;
                    vx = Math.cos(angle) * speed * 0.3;
                    vy = speed * 0.6;
                    vz = Math.sin(angle) * speed * 0.3;
                    break;
                case "fountain":
                    double fAngle = rand.nextDouble() * 2.0 * Math.PI;
                    double fRadius = rand.nextDouble() * spread;
                    sx = Math.cos(fAngle) * fRadius;
                    sy = rand.nextDouble() * spread * 0.5;
                    sz = Math.sin(fAngle) * fRadius;
                    vx = Math.cos(fAngle) * speed * 0.5;
                    vy = speed * 0.3 - rand.nextDouble() * speed * 0.2;
                    vz = Math.sin(fAngle) * speed * 0.5;
                    break;
                case "pulse":
                    double pulsePhase = (time * 0.2) % (2.0 * Math.PI);
                    double pulseRadius = spread * (0.3 + Math.sin(pulsePhase) * 0.7);
                    double pAngle = rand.nextDouble() * 2.0 * Math.PI;
                    sx = Math.cos(pAngle) * pulseRadius;
                    sy = (rand.nextDouble() - 0.5) * spread * 0.5;
                    sz = Math.sin(pAngle) * pulseRadius;
                    vx = Math.cos(pAngle) * speed * Math.sin(pulsePhase);
                    vy = speed * 0.2;
                    vz = Math.sin(pAngle) * speed * Math.sin(pulsePhase);
                    break;
                case "ring":
                    double rAngle = ((i * 2.0 * Math.PI) / count) + (time * 0.05);
                    double rRadius = spread * 0.8;
                    sx = Math.cos(rAngle) * rRadius;
                    sy = (rand.nextDouble() - 0.5) * spread * 0.3;
                    sz = Math.sin(rAngle) * rRadius;
                    vx = Math.cos(rAngle + Math.PI / 2) * speed * 0.4;
                    vy = speed * 0.3;
                    vz = Math.sin(rAngle + Math.PI / 2) * speed * 0.4;
                    break;
                case "burst":
                    double bAngle = rand.nextDouble() * 2.0 * Math.PI;
                    double bElevation = (rand.nextDouble() - 0.5) * Math.PI * 0.5;
                    sx = Math.cos(bAngle) * Math.cos(bElevation) * spread * 0.3;
                    sy = Math.sin(bElevation) * spread * 0.3;
                    sz = Math.sin(bAngle) * Math.cos(bElevation) * spread * 0.3;
                    vx = Math.cos(bAngle) * Math.cos(bElevation) * speed;
                    vy = Math.sin(bElevation) * speed;
                    vz = Math.sin(bAngle) * Math.cos(bElevation) * speed;
                    size = 0.5f;
                    break;
                case "snowflake":
                    // Snowflake pattern: spawn particles 2 blocks above, falling down like rain
                    double sfAngle = rand.nextDouble() * 2.0 * Math.PI;
                    double sfRadius = rand.nextDouble() * spread;
                    sx = Math.cos(sfAngle) * sfRadius;
                    sy = 2.0; // 2 blocks above the pillar
                    sz = Math.sin(sfAngle) * sfRadius;
                    vx = (rand.nextDouble() - 0.5) * speed * 0.1; // Slight horizontal drift
                    vy = -speed * 0.3; // Falling down
                    vz = (rand.nextDouble() - 0.5) * speed * 0.1; // Slight horizontal drift
                    size = 0.3f; // Smaller particles for snowflakes
                    break;
                default:
                    sx = (rand.nextDouble() - 0.5) * spread;
                    sy = rand.nextDouble() * spread;
                    sz = (rand.nextDouble() - 0.5) * spread;
                    vx = (rand.nextDouble() - 0.5) * speed;
                    vy = rand.nextDouble() * speed;
                    vz = (rand.nextDouble() - 0.5) * speed;
                    break;
            }
        } else {
            // When use_pattern is false, default to ring pattern
            double rAngle = ((i * 2.0 * Math.PI) / count) + (time * 0.05);
            double rRadius = cfg.particle_spread * 0.8;
            sx = Math.cos(rAngle) * rRadius;
            sy = (rand.nextDouble() - 0.5) * cfg.particle_spread * 0.3;
            sz = Math.sin(rAngle) * rRadius;
            vx = Math.cos(rAngle + Math.PI / 2) * cfg.particle_speed * 0.4;
            vy = cfg.particle_speed * 0.3;
            vz = Math.sin(rAngle + Math.PI / 2) * cfg.particle_speed * 0.4;
        }

        return new ParticleSpawnData(sx, sy, sz, vx, vy, vz, size);
    }

    public void syncPatternFromManager() {
        if (level == null || level.isClientSide) return;

        if (
                level.getServer() == null ||
                        !level.getServer().isRunning() ||
                        !com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()
        ) {
            return;
        }

        if (level.getServer().getPlayerList().getPlayerCount() == 0) {
            return;
        }

        if (!level.hasChunkAt(worldPosition)) {
            return;
        }

        try {
            net.minecraft.world.level.chunk.ChunkAccess chunk = level.getChunk(
                    worldPosition
            );
            if (!(chunk instanceof net.minecraft.world.level.chunk.LevelChunk)) {
                return;
            }
            if (
                    !chunk
                            .getStatus()
                            .isOrAfter(net.minecraft.world.level.chunk.ChunkStatus.FULL)
            ) {
                return;
            }
        } catch (Exception e) {
            return;
        }

        PillarIdManager manager = PillarIdManager.get();

        // IMPORTANT: Don't sync until manager has loaded
        // Otherwise, patterns loaded from NBT might be overwritten
        if (!manager.hasLoaded()) {
            return;
        }

        String expectedPrefix = PillarIdManager.getVariantPrefix(
                level,
                worldPosition
        );

        String idToSync = this.pillarId;

        if (idToSync == null || idToSync.isEmpty()) {
            idToSync = getStackPillarId();
        }

        if (idToSync == null || idToSync.isEmpty()) {
            return;
        }

        if (!idToSync.startsWith(expectedPrefix)) {
            return;
        }

        PillarIdManager.PillarData data = manager.getPillarData(idToSync);

        if (data != null) {
            boolean needsUpdate = false;

            // Sync pattern
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

            // Sync pattern settings
            if (data.pattern_speed != null && (this.patternSpeed == null || !this.patternSpeed.equals(data.pattern_speed))) {
                this.patternSpeed = data.pattern_speed;
                needsUpdate = true;
            }
            if (data.pattern_spread != null && (this.patternSpread == null || !this.patternSpread.equals(data.pattern_spread))) {
                this.patternSpread = data.pattern_spread;
                needsUpdate = true;
            }
            if (data.pattern_intensity != null && (this.patternIntensity == null || !this.patternIntensity.equals(data.pattern_intensity))) {
                this.patternIntensity = data.pattern_intensity;
                needsUpdate = true;
            }

            if (needsUpdate) {
                this.setChanged();
                level.sendBlockUpdated(
                        worldPosition,
                        getBlockState(),
                        getBlockState(),
                        3
                );
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
                    3
            );
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
                    3
            );
        }
    }

    public boolean hasDisplayItem() {
        return !displayedItem.isEmpty();
    }
    private static final int globalParticleColorCounter = 0;
    private final int clientSyncAttempts = 0;
    private int syncTickCounter = 0;

    @Override
    public void onLoad() {
        super.onLoad();
        // Force manager to load when block entity loads to ensure removal detection works immediately
        if (level != null && !level.isClientSide) {
            PillarIdManager manager = PillarIdManager.get();
            try {
                manager.load();
                // Register this pillar with the manager to ensure it's tracked in the GUI
                manager.registerPillar(this);
            } catch (Exception e) {
                // Ignore errors during load, recovery will handle it
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
                    3
            );

            // Sync with manager
            if (this.pillarId != null) {
                PillarIdManager.get().updateDisplayedItem(this.pillarId, stack.isEmpty() ? null : net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
            } else {
                String stackId = getStackPillarId();
                if (stackId != null) {
                    PillarIdManager.get().updateDisplayedItem(stackId, stack.isEmpty() ? null : net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
                } else if (!stack.isEmpty()) {
                    // Force registration of newly placed pillar stack when an item is added
                    PillarIdManager.get().updateDisplayedItemByPosition(level, worldPosition, net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
                }
            }
        }
    }

    public static void serverTick(
            net.minecraft.world.level.Level level,
            BlockPos pos,
            BlockState state,
            PillarBlockEntity be
    ) {
        // Sync colors and pattern from manager periodically.
        // Both syncs share the same chunk-validity / manager-ready guards,
        // so we merge them into a single if-block to halve that overhead.
        be.syncTickCounter++;
        if (be.syncTickCounter >= SYNC_INTERVAL) {
            be.syncTickCounter = 0;
            be.syncBothFromManager(); // single combined sync — avoids duplicate guard checks
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
                    3
            );

            // Sync with manager
            if (this.pillarId != null) {
                PillarIdManager.get().updateDisplayedItem(this.pillarId, stack.isEmpty() ? null : net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
            } else {
                String stackId = getStackPillarId();
                if (stackId != null) {
                    PillarIdManager.get().updateDisplayedItem(stackId, stack.isEmpty() ? null : net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
                } else if (!stack.isEmpty()) {
                    // Force registration of newly placed pillar stack when an item is added
                    PillarIdManager.get().updateDisplayedItemByPosition(level, worldPosition, net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
                }
            }
        }
    }

    /**
     * Combined server-side sync: runs the expensive guard checks ONCE and then
     * syncs both colors and pattern in a single pass.
     * This replaces the previous pattern of calling syncColorsFromManager() and
     * syncPatternFromManager() sequentially, which duplicated every guard.
     */
    private void syncBothFromManager() {
        if (level == null || level.isClientSide) return;

        // ── Shared guard block ───────────────────────────────────────────
        if (level.getServer() == null ||
                !level.getServer().isRunning() ||
                !com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()) {
            return;
        }
        if (level.getServer().getPlayerList().getPlayerCount() == 0) return;
        if (!level.hasChunkAt(worldPosition)) return;
        try {
            net.minecraft.world.level.chunk.ChunkAccess chunk = level.getChunk(worldPosition);
            if (!(chunk instanceof net.minecraft.world.level.chunk.LevelChunk)) return;
            if (!chunk.getStatus().isOrAfter(net.minecraft.world.level.chunk.ChunkStatus.FULL)) return;
        } catch (Exception e) {
            return;
        }

        PillarIdManager manager = PillarIdManager.get();
        if (!manager.hasLoaded()) return;

        // Resolve the pillar ID once
        String expectedPrefix = PillarIdManager.getVariantPrefix(level, worldPosition);
        String idToSync = (this.pillarId != null && !this.pillarId.isEmpty())
                ? this.pillarId : getStackPillarId();
        if (idToSync == null || idToSync.isEmpty()) return;
        if (!idToSync.startsWith(expectedPrefix)) return;

        PillarIdManager.PillarData data = manager.getPillarData(idToSync);
        boolean changed = false;

        // ── Sync colors ───────────────────────────────────────────────
        if (data != null && data.hasColors()) {
            java.util.List<String> managerColors = data.getColors();
            boolean colorsDiffer = (this.particleColors == null ||
                    this.particleColors.isEmpty() ||
                    !this.pillarId.equals(idToSync) ||
                    managerColors.size() != this.particleColors.size());

            if (!colorsDiffer) {
                // Fine-grained compare only when sizes match
                for (int i = 0; i < managerColors.size(); i++) {
                    String mc = managerColors.get(i), cc = this.particleColors.get(i);
                    if (mc == null || !mc.equals(cc)) { colorsDiffer = true; break; }
                }
            }

            if (colorsDiffer) {
                this.pillarId = idToSync;
                this.particleColors = new java.util.ArrayList<>(managerColors);
                this.colorsInitialized = true;
                this.particleColorCounter = 0;
                this.lastParticleTick = 0;
                this.colorsVersion++;
                changed = true;
            }
        }

        // ── Sync pattern ───────────────────────────────────────────────
        if (data != null) {
            String managerPattern = data.pattern;
            if (managerPattern != null && !managerPattern.isEmpty()) {
                boolean validPattern = false;
                for (String p : PATTERNS) {
                    if (p.equals(managerPattern)) { validPattern = true; break; }
                }
                if (validPattern && (this.particlePattern == null || !this.particlePattern.equals(managerPattern))) {
                    this.particlePattern = managerPattern;
                    changed = true;
                }
            } else if (this.particlePattern != null) {
                // Manager pattern is null - reset to follow global
                this.particlePattern = null;
                changed = true;
            }

            // Sync pattern settings
            if (data.pattern_speed != null && (this.patternSpeed == null || !this.patternSpeed.equals(data.pattern_speed))) {
                this.patternSpeed = data.pattern_speed;  changed = true;
            } else if (data.pattern_speed == null && this.patternSpeed != null) {
                this.patternSpeed = null;
                changed = true;
            }

            if (data.pattern_spread != null && (this.patternSpread == null || !this.patternSpread.equals(data.pattern_spread))) {
                this.patternSpread = data.pattern_spread; changed = true;
            } else if (data.pattern_spread == null && this.patternSpread != null) {
                this.patternSpread = null;
                changed = true;
            }

            if (data.pattern_intensity != null && (this.patternIntensity != null && !this.patternIntensity.equals(data.pattern_intensity))) {
                this.patternIntensity = data.pattern_intensity; changed = true;
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

        PillarIdManager manager = PillarIdManager.get();
        // Always orient to bottom for ID consistency
        BlockPos bottomPos = findStackBottom();
        PillarIdManager.PillarData data = manager.getOrCreatePillarData(level, bottomPos);

        if (data != null) {
            // Sync all world state (item, type, etc.) alongside the pattern update
            manager.syncPatternSettingsFromNBT(this, data);
            data.pattern = pattern;
            data.modifiedTime = System.currentTimeMillis();
            manager.saveImmediate(); // This triggers immediate broadcast to all clients
        }
    }

    public void cycleParticlePattern() {
        PillarParticleConfig cfg = PillarParticleConfig.get();
        String currentPattern = getParticlePattern(); // Returns null for new/default pillars

        // If current pattern is null (default fallback state), we want to start cycling FROM the default
        // So the first click will set it to the first pattern after "default" in the array.
        if (currentPattern == null) {
            currentPattern = "default";
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
        if (level == null || level.isClientSide) return;

        if (
                level.getServer() == null ||
                        !level.getServer().isRunning() ||
                        !com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()
        ) {
            return;
        }

        if (level.getServer().getPlayerList().getPlayerCount() == 0) {
            return;
        }

        if (!level.hasChunkAt(worldPosition)) {
            return;
        }

        try {
            net.minecraft.world.level.chunk.ChunkAccess chunk = level.getChunk(
                    worldPosition
            );
            if (!(chunk instanceof net.minecraft.world.level.chunk.LevelChunk)) {
                return;
            }
            if (
                    !chunk
                            .getStatus()
                            .isOrAfter(net.minecraft.world.level.chunk.ChunkStatus.FULL)
            ) {
                return;
            }
        } catch (Exception e) {
            return;
        }

        // Allow sync even if colors exist - this enables updates from config GUI
        // Only skip if we're already synced and nothing changed
        // (Original behavior preserved for performance, but now allows forced updates)

        PillarIdManager manager = PillarIdManager.get();

        // IMPORTANT: Don't sync (or clear colors) until manager has loaded
        // Otherwise, colors loaded from NBT will be cleared before manager loads
        if (!manager.hasLoaded()) {
            return;
        }
        String expectedPrefix = PillarIdManager.getVariantPrefix(
                level,
                worldPosition
        );

        String idToSync = this.pillarId;

        if (idToSync == null || idToSync.isEmpty()) {
            idToSync = getStackPillarId();
        }

        if (idToSync == null || idToSync.isEmpty()) {
            return;
        }

        if (!idToSync.startsWith(expectedPrefix)) {
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
                        3
                );
            }
            return;
        }

        PillarIdManager.PillarData data = manager.getPillarData(idToSync);

        if (data != null && data.hasColors()) {
            java.util.List<String> managerColors = data.getColors();
            boolean shouldSync = false;

            if (this.pillarId == null || this.pillarId.isEmpty()) {
                shouldSync = true;
            } else if (!this.pillarId.equals(idToSync)) {
                // ID changed, need to sync
                shouldSync = true;
            } else if (this.particleColors == null || this.particleColors.isEmpty()) {
                // No colors but manager has them
                shouldSync = true;
            } else {
                // Check if colors have changed by comparing lists
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
                this.particleColors = new java.util.ArrayList<>(managerColors);
                this.colorsInitialized = true;
                this.particleColorCounter = 0;
                this.lastParticleTick = 0;
                this.setChanged();

                level.sendBlockUpdated(
                        worldPosition,
                        getBlockState(),
                        getBlockState(),
                        3
                );
            }
        } else {
            // Data is null - pillar ID was removed from manager OR doesn't exist yet
            // IMPORTANT: NEVER clear colors that exist in NBT
            // Colors loaded from NBT are the source of truth for rendering
            // Only sync colors TO manager if they exist in NBT
            if (this.pillarId != null && this.pillarId.equals(idToSync) &&
                    this.particleColors != null && !this.particleColors.isEmpty()) {
                // Colors exist in NBT but manager doesn't have them - sync TO manager
                BlockPos bottomPos = findStackBottom();
                PillarIdManager.PillarData newData = manager.getPillarDataByPosition(level, bottomPos);
                if (newData == null) {
                    // Create new data entry at bottom position
                    newData = manager.getOrCreatePillarData(level, bottomPos);
                }
                // If the ID matches, sync colors FROM NBT TO manager
                if (newData != null && newData.id.equals(idToSync)) {
                    // Only sync if manager doesn't have colors or has different colors
                    boolean needsSync = false;
                    if (!newData.hasColors()) {
                        needsSync = true;
                    } else {
                        // Check if colors differ
                        java.util.List<String> managerColors = newData.getColors();
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
                        // Sync colors FROM NBT TO manager
                        newData.clearColors();
                        for (String color : this.particleColors) {
                            if (color != null && !color.isEmpty()) {
                                newData.addColor(color);
                            }
                        }
                        // Don't save immediately during sync - let recovery or explicit saves handle it
                        // Log sync only for debugging
                        // System.out.println("BuildScape: Synced " + this.particleColors.size() +
                        //         " colors from NBT to manager for " + idToSync);
                    }
                }
            }
            // DO NOT clear colors here - colors loaded from NBT are preserved
            // Only the reset handler should clear colors
        }
    }

    public String getParticlePattern() {
        if (particlePattern != null && !particlePattern.isEmpty() && !particlePattern.equals("default")) {
            return particlePattern;
        }

        if (level != null) {
            String stackPattern = getStackParticlePattern();
            if (stackPattern != null && !stackPattern.isEmpty() && !stackPattern.equals("default")) {
                return stackPattern;
            }
        }

        return null; // Signals fallback to global config
    }

    public void setParticlePattern(String pattern) {
        this.particlePattern = pattern;
        this.setChanged();
        if (level != null && !level.isClientSide) {
            propagatePatternToStack(pattern);

            // Update the pattern in PillarIdManager config
            updatePatternInConfig(pattern);

            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3
            );
        }
    }

    public BlockPos findStackTop() {
        if (level == null) return worldPosition;

        if (
                !level.isClientSide &&
                        !com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()
        ) {
            return worldPosition;
        }

        BlockPos current = worldPosition;
        int maxHeight = 256;
        int checked = 0;

        while (checked < maxHeight) {
            BlockPos above = current.above();
            if (!level.hasChunkAt(above)) {
                break;
            }

            try {
                if (!level.isClientSide) {
                    net.minecraft.world.level.chunk.ChunkAccess chunk = level.getChunk(
                            above
                    );
                    if (!(chunk instanceof net.minecraft.world.level.chunk.LevelChunk)) {
                        break;
                    }
                    if (
                            !chunk
                                    .getStatus()
                                    .isOrAfter(net.minecraft.world.level.chunk.ChunkStatus.FULL)
                    ) {
                        break;
                    }
                }
            } catch (Exception e) {
                break;
            }

            if (!(level.getBlockState(above).getBlock() instanceof PillarBlock)) {
                break;
            }
            current = above;
            checked++;
        }
        return current;
    }

    private String getStackParticlePattern() {
        if (level == null) return this.particlePattern;

        if (
                !level.isClientSide &&
                        !com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()
        ) {
            return this.particlePattern;
        }

        if (
                !level.isClientSide &&
                        (level.getServer() == null ||
                                !level.getServer().isRunning() ||
                                level.getServer().getPlayerList().getPlayerCount() == 0)
        ) {
            return this.particlePattern;
        }

        try {
            BlockPos bottom = findStackBottom();
            BlockPos current = bottom;
            int maxHeight = 256;
            int checked = 0;

            while (
                    checked < maxHeight &&
                            level.getBlockState(current).getBlock() instanceof PillarBlock
            ) {
                if (!level.hasChunkAt(current)) {
                    break;
                }

                try {
                    if (!level.isClientSide) {
                        net.minecraft.world.level.chunk.ChunkAccess chunk = level.getChunk(
                                current
                        );
                        if (
                                !(chunk instanceof net.minecraft.world.level.chunk.LevelChunk)
                        ) {
                            break;
                        }
                        if (
                                !chunk
                                        .getStatus()
                                        .isOrAfter(net.minecraft.world.level.chunk.ChunkStatus.FULL)
                        ) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    break;
                }

                net.minecraft.world.level.block.entity.BlockEntity be =
                        level.getBlockEntity(current);
                if (be instanceof PillarBlockEntity pillarBE) {
                    if (
                            pillarBE.particlePattern != null &&
                                    !pillarBE.particlePattern.isEmpty() &&
                                    !pillarBE.particlePattern.equals("default")
                    ) {
                        return pillarBE.particlePattern;
                    }
                }
                current = current.above();
                checked++;
            }
        } catch (Exception e) {
            return this.particlePattern;
        }

        return null;
    }

    String getParticleColor(PillarParticleConfig cfg) {
        if (particleColors != null && !particleColors.isEmpty()) {
            int numColors = particleColors.size();
            int colorIndex = particleColorCounter % numColors;
            particleColorCounter++;

            String customColor = particleColors.get(colorIndex);
            if (customColor != null && HEX_COLOR_PATTERN.matcher(customColor).matches()) {
                return customColor.toUpperCase();
            }
        }

        if (cfg == null) {
            return "#FFFFFF";
        }

        java.util.List<String> colorsToUse = cfg.particle_color;
        if (colorsToUse == null || colorsToUse.isEmpty()) {
            colorsToUse = new java.util.ArrayList<>();
            colorsToUse.add("#FFB81C");
            colorsToUse.add("#FFFFFF");
            colorsToUse.add("#FFFF00");
        }

        int maxColors = Math.max(
                1,
                Math.min(7, Math.min(cfg.max_particle_color, colorsToUse.size()))
        );
        int colorIndex = particleColorCounter % maxColors;
        particleColorCounter++;
        if (colorIndex < 0 || colorIndex >= colorsToUse.size()) {
            colorIndex = 0;
        }

        String configColor = colorsToUse.get(colorIndex);
        if (configColor != null && HEX_COLOR_PATTERN.matcher(configColor).matches()) {
            return configColor.toUpperCase();
        }
        return "#FFFFFF";
    }

    public BlockPos findStackBottom() {
        if (level == null) return worldPosition;

        if (
                !level.isClientSide &&
                        !com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()
        ) {
            return worldPosition;
        }

        BlockPos current = worldPosition;
        int maxDepth = 256;
        int checked = 0;

        while (checked < maxDepth) {
            BlockPos below = current.below();
            if (!level.hasChunkAt(below)) {
                break;
            }

            try {
                if (!level.isClientSide) {
                    net.minecraft.world.level.chunk.ChunkAccess chunk = level.getChunk(
                            below
                    );
                    if (!(chunk instanceof net.minecraft.world.level.chunk.LevelChunk)) {
                        break;
                    }
                    if (
                            !chunk
                                    .getStatus()
                                    .isOrAfter(net.minecraft.world.level.chunk.ChunkStatus.FULL)
                    ) {
                        break;
                    }
                }
            } catch (Exception e) {
                break;
            }

            if (!(level.getBlockState(below).getBlock() instanceof PillarBlock)) {
                break;
            }
            current = below;
            checked++;
        }
        return current;
    }

    public boolean addParticleColor(String color) {
        if (color == null || color.isEmpty()) {
            return false;
        }

        String normalizedColor = color.toUpperCase();
        if (!normalizedColor.startsWith("#")) {
            normalizedColor = "#" + normalizedColor;
        }

        if (!normalizedColor.matches("^#[0-9A-F]{6}$")) {
            return false;
        }

        if (level != null && !level.isClientSide) {
            PillarIdManager manager = PillarIdManager.get();
            // Important: always orient to foundation for ID consistency
            BlockPos bottomPos = findStackBottom();

            // Use manager's consolidated dyeing logic which also syncs current world properties (item/type)
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
            this.particleColors = new java.util.ArrayList<>();
        }
        if (this.particleColors.size() >= MAX_DYE_COLORS) {
            return false;
        }
        this.particleColors.add(normalizedColor);
        this.particleColorCounter = 0;
        this.lastParticleTick = 0;
        this.colorsInitialized = true;

        // Auto-update maxParticleColor to match current color count
        int currentColorCount = this.particleColors.size();
        if (this.maxParticleColor == null || this.maxParticleColor < currentColorCount) {
            this.maxParticleColor = Math.min(currentColorCount, MAX_DYE_COLORS);
        }

        this.setChanged();

        return true;
    }

    public String getStackPillarId() {
        if (level == null || level.isClientSide) return this.pillarId;

        if (!com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()) {
            return this.pillarId;
        }

        if (
                level.getServer() == null ||
                        !level.getServer().isRunning() ||
                        level.getServer().getPlayerList().getPlayerCount() == 0
        ) {
            return this.pillarId;
        }

        try {
            BlockPos bottom = findStackBottom();
            BlockPos current = bottom;
            int maxHeight = 256;
            int checked = 0;

            while (
                    checked < maxHeight &&
                            level.getBlockState(current).getBlock() instanceof PillarBlock
            ) {
                if (!level.hasChunkAt(current)) {
                    break;
                }

                try {
                    net.minecraft.world.level.chunk.ChunkAccess chunk = level.getChunk(
                            current
                    );
                    if (!(chunk instanceof net.minecraft.world.level.chunk.LevelChunk)) {
                        break;
                    }
                    if (
                            !chunk
                                    .getStatus()
                                    .isOrAfter(net.minecraft.world.level.chunk.ChunkStatus.FULL)
                    ) {
                        break;
                    }
                } catch (Exception e) {
                    break;
                }

                net.minecraft.world.level.block.entity.BlockEntity be =
                        level.getBlockEntity(current);
                if (be instanceof PillarBlockEntity pillarBE) {
                    if (pillarBE.pillarId != null && !pillarBE.pillarId.isEmpty()) {
                        return pillarBE.pillarId;
                    }
                }
                current = current.above();
                checked++;
            }
        } catch (Exception e) {
            return this.pillarId;
        }

        return null;
    }

    /**
     * Resets the pillar to default appearance (freshly placed state).
     * Clears all custom particle colors, patterns, and settings.
     * Removes the pillar ID association completely.
     * Keeps the displayed item intact.
     * This is called when a pillar ID is removed from the manager.
     */
    public Boolean getUsePattern() {
        return usePattern;
    }

    private void propagatePatternToStack(String pattern) {
        if (level == null || level.isClientSide) return;

        BlockPos bottom = findStackBottom();
        BlockPos current = bottom;

        while (level.getBlockState(current).getBlock() instanceof PillarBlock) {
            net.minecraft.world.level.block.entity.BlockEntity be =
                    level.getBlockEntity(current);
            if (be instanceof PillarBlockEntity pillarBE) {
                pillarBE.particlePattern = pattern;
                pillarBE.setChanged();

                level.sendBlockUpdated(
                        current,
                        level.getBlockState(current),
                        level.getBlockState(current),
                        3
                );
            }
            current = current.above();
        }
    }

    private void propagateToStack(String stackId, java.util.List<String> colors) {
        if (level == null || level.isClientSide) return;

        BlockPos bottom = findStackBottom();
        BlockPos current = bottom;

        while (level.getBlockState(current).getBlock() instanceof PillarBlock) {
            net.minecraft.world.level.block.entity.BlockEntity be =
                    level.getBlockEntity(current);
            if (be instanceof PillarBlockEntity pillarBE) {
                pillarBE.pillarId = stackId;
                pillarBE.particleColors = colors != null
                        ? new java.util.ArrayList<>(colors)
                        : null;
                pillarBE.colorsInitialized = (colors != null && !colors.isEmpty());
                pillarBE.particleColorCounter = 0;
                pillarBE.lastParticleTick = 0;
                pillarBE.setChanged();

                level.sendBlockUpdated(
                        current,
                        level.getBlockState(current),
                        level.getBlockState(current),
                        3
                );
            }
            current = current.above();
        }
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
                    3
            );
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
                PillarIdManager.PillarData data = PillarIdManager.get()
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

    public void setParticleColors(java.util.List<String> colors) {
        if (colors == null || colors.isEmpty()) {
            this.particleColors = null;
            this.colorsInitialized = false;
        } else {
            this.particleColors = new java.util.ArrayList<>();
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
                // Colors are set, mark as initialized to prevent re-dyeing
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
                    3
            );
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
        this.pillarId = null; // Remove pillar ID association - make it freshly placed
        this.particleColorCounter = 0;
        this.colorsInitialized = false;
        this.lastParticleTick = 0L; // Reset particle tick to restart particle effects immediately

        // Mark as changed so NBT is saved
        // When saveAdditional is called, it won't write null fields, effectively removing them from NBT
        this.setChanged();

        // Force immediate save and sync
        if (level != null && !level.isClientSide) {
            // Mark chunk as needing save - this ensures NBT is written with cleared values
            if (level.getChunkAt(worldPosition) != null) {
                level.getChunkAt(worldPosition).setUnsaved(true);
            }

            // Force block update to sync changes to clients immediately
            // This sends the update packet which includes the cleared NBT
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    /**
     * Synchronizes ALL settings from PillarData to this block entity.
     * This includes colors, pattern, speed, spread, and intensity.
     */
    public void syncFromData(PillarIdManager.PillarData data) {
        if (data == null) return;

        boolean changed = false;

        // Sync ID
        if (this.pillarId == null || !this.pillarId.equals(data.id)) {
            this.pillarId = data.id;
            changed = true;
        }

        // Sync colors
        if (data.hasColors()) {
            java.util.List<String> newColors = data.getColors();
            if (this.particleColors == null || this.particleColors.size() != newColors.size()) {
                this.particleColors = new java.util.ArrayList<>(newColors);
                this.colorsInitialized = true;
                this.particleColorCounter = 0;
                this.colorsVersion++;
                changed = true;
            } else {
                for (int i = 0; i < newColors.size(); i++) {
                    if (!newColors.get(i).equals(this.particleColors.get(i))) {
                        this.particleColors = new java.util.ArrayList<>(newColors);
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

        // Sync pattern
        if (data.pattern != null && !data.pattern.equals(this.particlePattern)) {
            this.particlePattern = data.pattern;
            changed = true;
        } else if (data.pattern == null && this.particlePattern != null) {
            this.particlePattern = null;
            changed = true;
        }

        // Sync use_pattern override
        if (data.use_pattern != null && (!data.use_pattern.equals(this.usePattern))) {
            this.usePattern = data.use_pattern;
            changed = true;
        } else if (data.use_pattern == null && this.usePattern != null) {
            this.usePattern = null;
            changed = true;
        }

        // Sync settings
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
            if (tag.contains("DisplayedItem", 10)) {
                ItemStack loaded = ItemStack.of(tag.getCompound("DisplayedItem"));
                this.displayedItem = loaded == null ? ItemStack.EMPTY : loaded;
            }
            // Handle ITEM tag from /fill or /give commands (custom NBT format)
            else if (tag.contains("ITEM", 8)) {
                String itemId = tag.getString("ITEM");
                try {
                    net.minecraft.resources.ResourceLocation itemLocation =
                            new net.minecraft.resources.ResourceLocation(itemId);
                    net.minecraft.world.item.Item item =
                            net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(itemLocation);
                    if (item != null && item != net.minecraft.world.item.Items.AIR) {
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

        if (tag.contains("ParticlePattern", 8)) {
            this.particlePattern = tag.getString("ParticlePattern");
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
        // Handle PATTERN tag from /fill or /give commands (custom NBT format)
        else if (tag.contains("PATTERN", 8)) {
            this.particlePattern = tag.getString("PATTERN");
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

        // Load pattern settings
        if (tag.contains("PatternSpeed", 6)) {
            this.patternSpeed = tag.getDouble("PatternSpeed");
        } else {
            this.patternSpeed = null;
        }
        if (tag.contains("PatternSpread", 6)) {
            this.patternSpread = tag.getDouble("PatternSpread");
        } else {
            this.patternSpread = null;
        }
        if (tag.contains("PatternIntensity", 6)) {
            this.patternIntensity = tag.getDouble("PatternIntensity");
        } else {
            this.patternIntensity = null;
        }

        if (tag.contains("MaxParticleColor", 3)) {
            this.maxParticleColor = tag.getInt("MaxParticleColor");
        } else {
            this.maxParticleColor = null;
        }

        try {
            if (tag.contains("ParticleColors", 9)) {
                net.minecraft.nbt.ListTag colorList = tag.getList("ParticleColors", 8);
                if (colorList.size() > 0) {
                    this.particleColors = new java.util.ArrayList<>();
                    int maxColors = Math.min(7, colorList.size());
                    for (int i = 0; i < maxColors; i++) {
                        try {
                            String color = colorList.getString(i);
                            if (
                                    color != null &&
                                            !color.isEmpty() &&
                                            color.matches("^#[0-9A-Fa-f]{6}$")
                            ) {
                                this.particleColors.add(color.toUpperCase());
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

        if (tag.contains("ParticleColorCounter", 3)) {
            this.particleColorCounter = tag.getInt("ParticleColorCounter");
        } else {
            this.particleColorCounter = 0;
        }

        if (tag.contains("ColorsInitialized", 1)) {
            this.colorsInitialized = tag.getBoolean("ColorsInitialized");
        } else {
            // Default: colors are initialized if they exist
            this.colorsInitialized = (this.particleColors != null &&
                    !this.particleColors.isEmpty());
        }

        // If we have colors loaded from NBT, ensure colorsInitialized is true
        // This prevents re-dyeing after world reload
        if (this.particleColors != null && !this.particleColors.isEmpty() && !this.colorsInitialized) {
            this.colorsInitialized = true;
        }

        if (tag.contains("PillarId", 8)) {
            this.pillarId = tag.getString("PillarId");
            if (this.pillarId.isEmpty()) {
                this.pillarId = null;
            }
        } else {
            this.pillarId = null;
        }

        if (tag.contains("UsePattern", 1)) {
            this.usePattern = tag.getBoolean("UsePattern");
        } else {
            this.usePattern = null;
        }

        if (tag.contains("FacingYaw", 5)) {
            this.facingYaw = tag.getFloat("FacingYaw");
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
        if (
                oldColor != newColor || (oldColor != null && !oldColor.equals(newColor))
        ) {
            this.lastParticleTick = 0;
            this.particleColorCounter = 0;
        }

        if (
                pillarId != null &&
                        !pillarId.isEmpty() &&
                        (particleColors == null || particleColors.isEmpty())
        ) {
            needsManagerSync = true;
        }
    }

    public void clearParticleColors() {
        String idToRemove = this.pillarId;

        clearLocalStateOnly();

        if (level != null && !level.isClientSide) {
            PillarIdManager manager = PillarIdManager.get();

            if (idToRemove != null && !idToRemove.isEmpty()) {
                manager.removePillar(idToRemove);
            }

            manager.removePillarByPosition(level, worldPosition);

            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3
            );
        }
    }

    public java.util.List<String> getParticleColors() {
        return particleColors;
    }

    public void forceSetColors(java.util.List<String> colors, String id) {
        if (colors == null || colors.isEmpty()) {
            return;
        }

        this.particleColors = new java.util.ArrayList<>(colors);
        this.pillarId = id;
        this.colorsInitialized = true;
        this.particleColorCounter = 0;
        this.lastParticleTick = 0;
        this.colorsVersion++; // notify syncer that data changed

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
            tag.put("DisplayedItem", displayedItem.save(new CompoundTag()));
        }

        if (particlePattern != null) {
            tag.putString("ParticlePattern", particlePattern);
        }

        if (patternSpeed != null) {
            tag.putDouble("PatternSpeed", patternSpeed);
        }
        if (patternSpread != null) {
            tag.putDouble("PatternSpread", patternSpread);
        }
        if (patternIntensity != null) {
            tag.putDouble("PatternIntensity", patternIntensity);
        }

        if (usePattern != null) {
            tag.putBoolean("UsePattern", usePattern);
        }

        if (maxParticleColor != null) {
            tag.putInt("MaxParticleColor", maxParticleColor);
        }

        if (particleColors != null && !particleColors.isEmpty()) {
            net.minecraft.nbt.ListTag colorList = new net.minecraft.nbt.ListTag();
            for (String color : particleColors) {
                colorList.add(net.minecraft.nbt.StringTag.valueOf(color));
            }
            tag.put("ParticleColors", colorList);
        }

        if (pillarId != null && !pillarId.isEmpty()) {
            tag.putString("PillarId", pillarId);
        }

        tag.putInt("ParticleColorCounter", particleColorCounter);

        tag.putBoolean("ColorsInitialized", colorsInitialized);

        tag.putFloat("FacingYaw", facingYaw);
    }

    public void initializeDefaultColors() {
        if (!colorsInitialized && particleColors == null) {
            colorsInitialized = true;
            this.particleColorCounter = 0;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static class ClientParticleHelper {
        // ── Particle reflection cache ────────────────────────────────────────────────
        // Look up the providers map and the internal ParticleEngine.add() once per
        // particle type rather than on every spawn tick.  Keyed by particle type so
        // both GLOW_LIME_SPARKLE and SNOWFLAKE each get their own cached provider.
        private static final java.util.concurrent.ConcurrentHashMap<
                net.minecraft.core.particles.ParticleType<?>,
                net.minecraft.client.particle.ParticleProvider<?>>
                CACHED_PROVIDERS = new java.util.concurrent.ConcurrentHashMap<>();
        private static volatile java.lang.reflect.Method CACHED_ADD_METHOD = null;
        private static volatile boolean REFLECTION_INIT_DONE = false;

        private static void spawnParticles(
                net.minecraft.world.level.Level level,
                BlockPos pos,
                PillarBlockEntity be,
                PillarParticleConfig cfg,
                long time,
                int count,
                java.util.Random rand,
                double centerX,
                double centerY,
                double centerZ
        ) {

            // Get pattern to determine particle type
            String pattern = be.getParticlePattern();
            if (pattern == null) {
                pattern = cfg.pattern != null ? cfg.pattern : "ring";
            }
            boolean isSnowflake = "snowflake".equals(pattern);
            SimpleParticleType particleType = isSnowflake ? ModParticles.SNOWFLAKE.get() : ModParticles.GLOW_LIME_SPARKLE.get();

            net.minecraft.client.Minecraft mc =
                    net.minecraft.client.Minecraft.getInstance();
            if (mc == null || mc.particleEngine == null) {
                for (int i = 0; i < count; i++) {
                    ParticleSpawnData data = calculateParticleData(
                            be,
                            cfg,
                            time,
                            i,
                            count,
                            rand
                    );
                    if (data == null) continue;

                    double particleX = centerX + data.sx;
                    double particleY = centerY + data.sy;
                    double particleZ = centerZ + data.sz;

                    // Only queue color for non-snowflake particles
                    if (!isSnowflake) {
                        String colorCode = be.getParticleColor(cfg);
                        com.kingodogo.buildscape.particle.PillarSparkleParticle.queueColor(
                                particleX,
                                particleY,
                                particleZ,
                                colorCode
                        );
                        if (data.size != 1.0f) {
                            com.kingodogo.buildscape.particle.PillarSparkleParticle.queueSize(
                                    particleX,
                                    particleY,
                                    particleZ,
                                    data.size
                            );
                        }
                    }

                    level.addParticle(
                            particleType,
                            particleX,
                            particleY,
                            particleZ,
                            data.vx,
                            data.vy,
                            data.vz
                    );
                }
                return;
            }

            net.minecraft.client.particle.ParticleEngine particleEngine =
                    mc.particleEngine;

            // ── Cached reflection lookup (runs at most once per particle type) ───────
            if (!REFLECTION_INIT_DONE) {
                try {
                    java.lang.reflect.Method addM =
                            net.minecraft.client.particle.ParticleEngine.class
                                    .getDeclaredMethod("add",
                                            net.minecraft.client.particle.Particle.class);
                    addM.setAccessible(true);
                    CACHED_ADD_METHOD = addM;
                } catch (Exception ignored) {
                }
                REFLECTION_INIT_DONE = true;
            }

            @SuppressWarnings("unchecked")
            net.minecraft.client.particle.ParticleProvider<SimpleParticleType> provider =
                    (net.minecraft.client.particle.ParticleProvider<SimpleParticleType>)
                            CACHED_PROVIDERS.computeIfAbsent(particleType, pt -> {
                                try {
                                    java.lang.reflect.Field f =
                                            net.minecraft.client.particle.ParticleEngine.class
                                                    .getDeclaredField("providers");
                                    f.setAccessible(true);
                                    @SuppressWarnings("unchecked")
                                    java.util.Map<net.minecraft.core.particles.ParticleType<?>,
                                            net.minecraft.client.particle.ParticleProvider<?>> map =
                                            (java.util.Map<net.minecraft.core.particles.ParticleType<?>,
                                                    net.minecraft.client.particle.ParticleProvider<?>>) f.get(particleEngine);
                                    return map.get(pt);
                                } catch (Exception e) {
                                    return null;
                                }
                            });

            for (int i = 0; i < count; i++) {
                ParticleSpawnData data = calculateParticleData(
                        be,
                        cfg,
                        time,
                        i,
                        count,
                        rand
                );
                if (data == null) continue;

                double particleX = centerX + data.sx;
                double particleY = centerY + data.sy;
                double particleZ = centerZ + data.sz;

                // Only queue color for non-snowflake particles
                if (!isSnowflake) {
                    String colorCode = be.getParticleColor(cfg);

                    com.kingodogo.buildscape.particle.PillarSparkleParticle.queueColor(
                            particleX,
                            particleY,
                            particleZ,
                            colorCode
                    );
                    if (data.size != 1.0f) {
                        com.kingodogo.buildscape.particle.PillarSparkleParticle.queueSize(
                                particleX,
                                particleY,
                                particleZ,
                                data.size
                        );
                    }
                }

                java.lang.reflect.Method addMethod = CACHED_ADD_METHOD;
                if (provider != null && addMethod != null) {
                    try {
                        net.minecraft.client.particle.Particle particle =
                                provider.createParticle(
                                        particleType,
                                        (net.minecraft.client.multiplayer.ClientLevel) level,
                                        particleX,
                                        particleY,
                                        particleZ,
                                        data.vx,
                                        data.vy,
                                        data.vz
                                );

                        if (particle != null) {
                            addMethod.invoke(particleEngine, particle);
                        }
                    } catch (Exception e) {
                        level.addParticle(
                                particleType,
                                particleX,
                                particleY,
                                particleZ,
                                data.vx,
                                data.vy,
                                data.vz
                        );
                    }
                } else {
                    level.addParticle(
                            particleType,
                            particleX,
                            particleY,
                            particleZ,
                            data.vx,
                            data.vy,
                            data.vz
                    );
                }
            }
        }
    }

    private boolean needsManagerSync = false;

    @Override
    public void setLevel(net.minecraft.world.level.Level level) {
        super.setLevel(level);

        if (needsManagerSync) {
            needsManagerSync = false;
        }
    }

    @OnlyIn(Dist.CLIENT)
        private record ParticleSpawnData(double sx, double sy, double sz, double vx, double vy, double vz, float size) {

    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);

        if (displayedItem.isEmpty() && !tag.contains("DisplayedItem")) {
            tag.put("DisplayedItem", ItemStack.EMPTY.save(new CompoundTag()));
        }

        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
        this.lastParticleTick = 0;
    }

    @Override
    public void onDataPacket(
            net.minecraft.network.Connection net,
            ClientboundBlockEntityDataPacket pkt
    ) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            load(tag);
            this.lastParticleTick = 0;
        }
    }
}
