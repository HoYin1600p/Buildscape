package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.block.entity.IBlockEntityReadData;
import com.kingodogo.buildscape.block.entity.IBlockEntityWriteData;
import com.kingodogo.buildscape.block.entity.IDataSerializable;
import com.kingodogo.buildscape.block.entity.DataBlockEntity;
import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.particle.ModParticles;
import com.kingodogo.buildscape.particle.PillarSparkleDataQueue;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import java.util.Random;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
public class PillarBlockEntity extends DataBlockEntity implements Container, IDataSerializable {

    public static final int MAX_DYE_COLORS = 5;

    public static final String[] PATTERNS = {
            "none",
            "default",
            "beam",
            "spiral",
            "fountain",
            "pulse",
            "ring",
            "burst",
            "snowflake"
    };

    public static final String[] RAINBOW_COLORS = {
            "#FF0000",
            "#FF7F00",
            "#FFFF00",
            "#00FF00",
            "#0000FF",
            "#4B0082",
            "#9400D3"
    };

    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#[0-9A-Fa-f]{6}$");

    private ItemStack displayedItem = ItemStack.EMPTY;
    private float facingYaw = 0.0f;
    private String particlePattern = null;
    private Double patternSpeed = null;
    private Double patternSpread = null;
    private Double patternIntensity = null;
    private Boolean usePattern = null;
    private Integer maxParticleColor = null;

    private String pillarId = null;
    private List<String> particleColors = null;
    private boolean colorsInitialized = false;
    private long lastParticleTick = 0L;
    private int particleColorCounter = 0;
    private int colorsVersion = 0;

    public PillarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PILLAR_TYPE, pos, state);
    }
    public static void clientTick(Level level, BlockPos pos, BlockState state, PillarBlockEntity be) {
        if (level == null || !level.isClientSide()) {
            return;
        }
        if (!be.hasDisplayItem()) {
            return;
        }

        long time = level.getGameTime();
        if ((time - be.lastParticleTick) < 5) {
            return;
        }

        PillarParticleConfig cfg = PillarParticleConfig.peek();
        if (!cfg.matches(be.displayedItem)) {
            return;
        }

        String earlyPattern = be.getParticlePattern();
        if (earlyPattern == null) {
            earlyPattern = cfg.pattern;
        }
        if ("none".equals(earlyPattern)) {
            return;
        }

        be.lastParticleTick = time;

        int baseCount = cfg.particle_density;
        int count = Math.max(
                1,
                cfg.use_pattern
                        ? (int) Math.round(baseCount * cfg.pattern_intensity)
                        : baseCount
        );

        Random rand = java.util.concurrent.ThreadLocalRandom.current();
        double centerX = pos.getX() + 0.5D;
        boolean isAshen = state.getBlock() instanceof AshenKingPillarBlock;
        double centerY = pos.getY() + (isAshen ? 0.75D : 1.0D);
        double centerZ = pos.getZ() + 0.5D;

        ClientParticleHelper.spawnParticles(level, pos, be, cfg, time, count, rand, centerX, centerY, centerZ);
    }

    private static class ClientParticleHelper {
        private static void spawnParticles(
                Level level,
                BlockPos pos,
                PillarBlockEntity be,
                PillarParticleConfig cfg,
                long time,
                int count,
                Random rand,
                double centerX,
                double centerY,
                double centerZ
        ) {
            String pattern = be.getParticlePattern();
            if (pattern == null) {
                pattern = cfg.pattern != null ? cfg.pattern : "ring";
            }
            boolean isSnowflake = "snowflake".equals(pattern);
            ParticleOptions particleType = isSnowflake
                    ? (ModParticles.SNOWFLAKE.isPresent() ? ModParticles.SNOWFLAKE.get() : ParticleTypes.SNOWFLAKE)
                    : (ModParticles.GLOW_LIME_SPARKLE.isPresent() ? ModParticles.GLOW_LIME_SPARKLE.get() : ParticleTypes.ENCHANT);

            for (int i = 0; i < count; i++) {
                ParticleSpawnData data = calculateParticleData(be, cfg, time, i, count, rand);
                if (data == null) {
                    continue;
                }

                double particleX = centerX + data.sx();
                double particleY = centerY + data.sy();
                double particleZ = centerZ + data.sz();

                if (!isSnowflake) {
                    String colorCode = be.getParticleColor(cfg);
                    PillarSparkleDataQueue.queueColor(particleX, particleY, particleZ, colorCode);
                    if (data.size() != 1.0F) {
                        PillarSparkleDataQueue.queueSize(particleX, particleY, particleZ, data.size());
                    }
                }

                level.addParticle(particleType, particleX, particleY, particleZ, data.vx(), data.vy(), data.vz());
            }
        }
    }
    private static ParticleSpawnData calculateParticleData(
            PillarBlockEntity be,
            PillarParticleConfig cfg,
            long time,
            int i,
            int count,
            Random rand
    ) {
        double sx, sy, sz, vx, vy, vz;
        float size = 1.0f;

        String pillarPattern = be.getParticlePattern();
        boolean usePattern = be.usePattern != null ? be.usePattern : cfg.use_pattern;

        if (usePattern) {
            String pattern = pillarPattern;
            if (pattern == null || pattern.isEmpty() || "default".equals(pattern)) {
                pattern = cfg.pattern != null ? cfg.pattern : "ring";
            }

            if ("none".equals(pattern)) {
                return null;
            }

            double patternSpeed = be.patternSpeed != null ? be.patternSpeed : cfg.pattern_speed;
            double patternIntensity = be.patternIntensity != null ? be.patternIntensity : cfg.pattern_intensity;
            double patternSpread = be.patternSpread != null ? be.patternSpread : cfg.pattern_spread;
            double speed = patternSpeed * patternIntensity;
            double spread = patternSpread;

            switch (pattern) {
                case "beam" -> {
                    sx = (rand.nextDouble() - 0.5D) * spread * 0.3D;
                    sy = 0.0D;
                    sz = (rand.nextDouble() - 0.5D) * spread * 0.3D;
                    vx = (rand.nextDouble() - 0.5D) * speed * 0.2D;
                    vy = speed * (0.8D + rand.nextDouble() * 0.4D);
                    vz = (rand.nextDouble() - 0.5D) * speed * 0.2D;
                }
                case "spiral" -> {
                    double angle = (time * 0.1D + (i * 2.0D * Math.PI) / count) % (2.0D * Math.PI);
                    double radius = spread * 0.5D;
                    sx = Math.cos(angle) * radius;
                    sy = 0.0D;
                    sz = Math.sin(angle) * radius;
                    vx = Math.cos(angle) * speed * 0.3D;
                    vy = speed * 0.6D;
                    vz = Math.sin(angle) * speed * 0.3D;
                }
                case "fountain" -> {
                    double fAngle = rand.nextDouble() * 2.0D * Math.PI;
                    double fRadius = rand.nextDouble() * spread;
                    sx = Math.cos(fAngle) * fRadius;
                    sy = rand.nextDouble() * spread * 0.5D;
                    sz = Math.sin(fAngle) * fRadius;
                    vx = Math.cos(fAngle) * speed * 0.5D;
                    vy = speed * 0.3D - rand.nextDouble() * speed * 0.2D;
                    vz = Math.sin(fAngle) * speed * 0.5D;
                }
                case "pulse" -> {
                    double pulsePhase = (time * 0.2D) % (2.0D * Math.PI);
                    double pulseRadius = spread * (0.3D + Math.sin(pulsePhase) * 0.7D);
                    double pAngle = rand.nextDouble() * 2.0D * Math.PI;
                    sx = Math.cos(pAngle) * pulseRadius;
                    sy = (rand.nextDouble() - 0.5D) * spread * 0.5D;
                    sz = Math.sin(pAngle) * pulseRadius;
                    vx = Math.cos(pAngle) * speed * Math.sin(pulsePhase);
                    vy = speed * 0.2D;
                    vz = Math.sin(pAngle) * speed * Math.sin(pulsePhase);
                }
                case "ring" -> {
                    double rAngle = ((i * 2.0D * Math.PI) / count) + (time * 0.05D);
                    double rRadius = spread * 0.8D;
                    sx = Math.cos(rAngle) * rRadius;
                    sy = (rand.nextDouble() - 0.5D) * spread * 0.3D;
                    sz = Math.sin(rAngle) * rRadius;
                    vx = Math.cos(rAngle + Math.PI / 2.0D) * speed * 0.4D;
                    vy = speed * 0.3D;
                    vz = Math.sin(rAngle + Math.PI / 2.0D) * speed * 0.4D;
                }
                case "burst" -> {
                    double bAngle = rand.nextDouble() * 2.0D * Math.PI;
                    double bElevation = (rand.nextDouble() - 0.5D) * Math.PI * 0.5D;
                    sx = Math.cos(bAngle) * Math.cos(bElevation) * spread * 0.3D;
                    sy = Math.sin(bElevation) * spread * 0.3D;
                    sz = Math.sin(bAngle) * Math.cos(bElevation) * spread * 0.3D;
                    vx = Math.cos(bAngle) * Math.cos(bElevation) * speed;
                    vy = Math.sin(bElevation) * speed;
                    vz = Math.sin(bAngle) * Math.cos(bElevation) * speed;
                    size = 0.5F;
                }
                case "snowflake" -> {
                    double sfAngle = rand.nextDouble() * 2.0D * Math.PI;
                    double sfRadius = rand.nextDouble() * spread;
                    sx = Math.cos(sfAngle) * sfRadius;
                    sy = 2.0D;
                    sz = Math.sin(sfAngle) * sfRadius;
                    vx = (rand.nextDouble() - 0.5D) * speed * 0.1D;
                    vy = -speed * 0.3D;
                    vz = (rand.nextDouble() - 0.5D) * speed * 0.1D;
                    size = 0.3F;
                }
                default -> {
                    sx = (rand.nextDouble() - 0.5D) * spread;
                    sy = rand.nextDouble() * spread;
                    sz = (rand.nextDouble() - 0.5D) * spread;
                    vx = (rand.nextDouble() - 0.5D) * speed;
                    vy = rand.nextDouble() * speed;
                    vz = (rand.nextDouble() - 0.5D) * speed;
                }
            }
        } else {
            double rAngle = ((i * 2.0D * Math.PI) / count) + (time * 0.05D);
            double rRadius = cfg.particle_spread * 0.8D;
            sx = Math.cos(rAngle) * rRadius;
            sy = (rand.nextDouble() - 0.5D) * cfg.particle_spread * 0.3D;
            sz = Math.sin(rAngle) * rRadius;
            vx = Math.cos(rAngle + Math.PI / 2.0D) * cfg.particle_speed * 0.4D;
            vy = cfg.particle_speed * 0.3D;
            vz = Math.sin(rAngle + Math.PI / 2.0D) * cfg.particle_speed * 0.4D;
        }

        return new ParticleSpawnData(sx, sy, sz, vx, vy, vz, size);
    }
    public void cycleParticlePattern() {
        String current = getParticlePattern();
        if (current == null) {
            current = "default";
        }

        int currentIndex = 1;
        for (int i = 0; i < PATTERNS.length; i++) {
            if (PATTERNS[i].equals(current)) {
                currentIndex = i;
                break;
            }
        }

        int nextIndex = (currentIndex + 1) % PATTERNS.length;
        setParticlePattern(PATTERNS[nextIndex]);
        propagatePatternToStack(this.particlePattern);

        if (level != null && !level.isClientSide()) {
            PillarIdManager manager = PillarIdManager.get(level);
            if (this.pillarId != null) {
                PillarIdManager.PillarData data = manager.getPillarData(this.pillarId);
                if (data != null) {
                    data.pattern = this.particlePattern;
                    manager.saveImmediate(level.getServer());
                }
            }
        }
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

        List<String> colorsToUse = cfg.particle_color;
        if (colorsToUse == null || colorsToUse.isEmpty()) {
            colorsToUse = new ArrayList<>();
            colorsToUse.add("#FFB81C");
            colorsToUse.add("#FFFFFF");
            colorsToUse.add("#FFFF00");
        }

        int maxColors = Math.max(1, Math.min(7, Math.min(cfg.max_particle_color, colorsToUse.size())));
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
    public boolean addParticleColor(String hexColor) {
        if (hexColor == null || !canAddMoreColors()) {
            return false;
        }

        if (particleColors == null) {
            particleColors = new ArrayList<>();
        }
        if (!particleColors.contains(hexColor)) {
            particleColors.add(hexColor);
            colorsInitialized = true;
            particleColorCounter = 0;
            colorsVersion++;
            setChanged();

            if (level != null && !level.isClientSide()) {
                PillarIdManager manager = PillarIdManager.get(level);
                PillarIdManager.PillarData data = manager.getOrCreatePillarData(level, worldPosition);
                this.pillarId = data.id;
                data.addColor(hexColor);
                manager.saveImmediate(level.getServer());

                propagateToStack(this.particleColors, this.pillarId);

                BlockState currentState = getBlockState();
                level.sendBlockUpdated(worldPosition, currentState, currentState, 3);
            }
            return true;
        }
        return false;
    }

    public void clearParticleColors() {
        clearLocalStateOnly();
        if (level != null && !level.isClientSide()) {
            PillarIdManager manager = PillarIdManager.get(level);
            if (this.pillarId != null) {
                manager.removePillar(this.pillarId);
            }
            manager.removePillarByPosition(level, worldPosition);
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void setParticleColors(List<String> colors) {
        forceSetColors(colors, this.pillarId);
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

        if (level != null && !level.isClientSide()) {
            BlockState currentState = getBlockState();
            level.sendBlockUpdated(worldPosition, currentState, currentState, 3);
            Services.PLATFORM.markChunkUnsaved(level, worldPosition);
        }
    }
    public void propagateToStack(List<String> colors, String id) {
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockPos current = worldPosition.above();
        while (level.getBlockState(current).getBlock() instanceof PillarBlock) {
            BlockEntity be = level.getBlockEntity(current);
            if (be instanceof PillarBlockEntity pillarBE) {
                pillarBE.particleColors = new ArrayList<>(colors);
                pillarBE.pillarId = id;
                pillarBE.colorsInitialized = true;
                pillarBE.setChanged();
                level.sendBlockUpdated(current, level.getBlockState(current), level.getBlockState(current), 3);
            }
            current = current.above();
        }

        current = worldPosition.below();
        while (level.getBlockState(current).getBlock() instanceof PillarBlock) {
            BlockEntity be = level.getBlockEntity(current);
            if (be instanceof PillarBlockEntity pillarBE) {
                pillarBE.particleColors = new ArrayList<>(colors);
                pillarBE.pillarId = id;
                pillarBE.colorsInitialized = true;
                pillarBE.setChanged();
                level.sendBlockUpdated(current, level.getBlockState(current), level.getBlockState(current), 3);
            }
            current = current.below();
        }
    }
    public void propagatePatternToStack(String pattern) {
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockPos current = worldPosition.above();
        while (level.getBlockState(current).getBlock() instanceof PillarBlock) {
            BlockEntity be = level.getBlockEntity(current);
            if (be instanceof PillarBlockEntity pillarBE) {
                pillarBE.particlePattern = pattern;
                pillarBE.setChanged();
                level.sendBlockUpdated(current, level.getBlockState(current), level.getBlockState(current), 3);
            }
            current = current.above();
        }

        current = worldPosition.below();
        while (level.getBlockState(current).getBlock() instanceof PillarBlock) {
            BlockEntity be = level.getBlockEntity(current);
            if (be instanceof PillarBlockEntity pillarBE) {
                pillarBE.particlePattern = pattern;
                pillarBE.setChanged();
                level.sendBlockUpdated(current, level.getBlockState(current), level.getBlockState(current), 3);
            }
            current = current.below();
        }
    }

    public void resetToDefaultAppearance() {
        this.particleColors = null;
        this.colorsInitialized = false;
        this.pillarId = null;
        this.particlePattern = null;
        this.patternSpeed = null;
        this.patternSpread = null;
        this.patternIntensity = null;
        this.usePattern = null;
        this.maxParticleColor = null;
        this.particleColorCounter = 0;
        this.lastParticleTick = 0;
        this.colorsVersion++;

        this.setChanged();

        if (level != null && !level.isClientSide()) {
            BlockState currentState = getBlockState();
            level.sendBlockUpdated(worldPosition, currentState, currentState, 3);
            Services.PLATFORM.markChunkUnsaved(level, worldPosition);
        }
    }

    public void syncFromData(PillarIdManager.PillarData data) {
        if (data == null) {
            return;
        }

        boolean changed = false;

        if (data.hasColors()) {
            this.particleColors = new ArrayList<>(data.getColors());
            this.pillarId = data.id;
            this.colorsInitialized = true;
            this.particleColorCounter = 0;
            this.lastParticleTick = 0;
            this.colorsVersion++;
            changed = true;
        }

        if (data.pattern != null && !data.pattern.isEmpty()) {
            this.particlePattern = data.pattern;
            changed = true;
        }

        if (data.patternSpeed != null) {
            this.patternSpeed = data.patternSpeed;
            changed = true;
        }

        if (data.patternSpread != null) {
            this.patternSpread = data.patternSpread;
            changed = true;
        }

        if (data.patternIntensity != null) {
            this.patternIntensity = data.patternIntensity;
            changed = true;
        }

        if (data.usePattern != null) {
            this.usePattern = data.usePattern;
            changed = true;
        }

        if (data.maxParticleColor != null) {
            this.maxParticleColor = data.maxParticleColor;
            changed = true;
        }

        if (changed) {
            this.setChanged();
            if (level != null && !level.isClientSide()) {
                BlockState currentState = getBlockState();
                level.sendBlockUpdated(worldPosition, currentState, currentState, 3);
            }
        }
    }

    public String getParticlePattern() {
        if (particlePattern != null) {
            return particlePattern;
        }
        return getStackParticlePattern();
    }

    public void setParticlePattern(String pattern) {
        this.particlePattern = pattern;
        this.setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private String getStackParticlePattern() {
        if (level == null) {
            return null;
        }

        BlockPos bottom = findStackBottom();
        BlockPos current = bottom;
        int checked = 0;

        while (level.getBlockState(current).getBlock() instanceof PillarBlock && checked < 256) {
            BlockEntity be = level.getBlockEntity(current);
            if (be instanceof PillarBlockEntity pillarBE) {
                if (pillarBE.particlePattern != null && !pillarBE.particlePattern.isEmpty() && !"default".equals(pillarBE.particlePattern)) {
                    return pillarBE.particlePattern;
                }
            }
            current = current.above();
            checked++;
        }
        return null;
    }

    public String getStackPillarId() {
        if (level == null) {
            return this.pillarId;
        }

        BlockPos bottom = findStackBottom();
        BlockPos current = bottom;
        int checked = 0;

        while (level.getBlockState(current).getBlock() instanceof PillarBlock && checked < 256) {
            BlockEntity be = level.getBlockEntity(current);
            if (be instanceof PillarBlockEntity pillarBE) {
                if (pillarBE.pillarId != null && !pillarBE.pillarId.isEmpty()) {
                    return pillarBE.pillarId;
                }
            }
            current = current.above();
            checked++;
        }
        return this.pillarId;
    }

    public BlockPos findStackBottom() {
        if (level == null) {
            return worldPosition;
        }
        BlockPos current = worldPosition;
        while (level.getBlockState(current.below()).getBlock() instanceof PillarBlock &&
                !(level.getBlockState(current.below()).getBlock() instanceof AshenKingPillarBlock)) {
            current = current.below();
        }
        return current;
    }

    public BlockPos findStackTop() {
        if (level == null) {
            return worldPosition;
        }
        BlockPos current = worldPosition;
        while (level.getBlockState(current.above()).getBlock() instanceof PillarBlock &&
                !(level.getBlockState(current.above()).getBlock() instanceof AshenKingPillarBlock)) {
            current = current.above();
        }
        return current;
    }

    public PillarBlockEntity getTopPillar() {
        BlockPos topPos = findStackTop();
        if (level != null && level.getBlockEntity(topPos) instanceof PillarBlockEntity be) {
            return be;
        }
        return this;
    }

    public boolean isTopPillar() {
        if (level == null) {
            return true;
        }
        return !(level.getBlockState(worldPosition.above()).getBlock() instanceof PillarBlock);
    }

    public ItemStack getDisplayedItem() {
        return displayedItem;
    }

    public boolean hasDisplayItem() {
        return !displayedItem.isEmpty();
    }

    public void setDisplayedItem(ItemStack item) {
        this.displayedItem = item;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void setDisplayedItem(ItemStack item, float yaw) {
        this.displayedItem = item;
        this.facingYaw = yaw;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public float getFacingYaw() {
        return facingYaw;
    }

    public void setFacingYaw(float yaw) {
        this.facingYaw = yaw % 360.0f;
        if (this.facingYaw < 0.0f) {
            this.facingYaw += 360.0f;
        }
        setChanged();
    }

    public void rotateFacing() {
        this.facingYaw = (this.facingYaw + 90.0f) % 360.0f;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public String getPillarId() {
        return pillarId;
    }

    public void setPillarId(String id) {
        this.pillarId = id;
        setChanged();
    }

    public boolean hasCustomColors() {
        return particleColors != null && !particleColors.isEmpty();
    }

    public List<String> getParticleColors() {
        return particleColors;
    }

    public int getDyeColorCount() {
        return particleColors != null ? particleColors.size() : 0;
    }

    public boolean canAddMoreColors() {
        return getDyeColorCount() < MAX_DYE_COLORS;
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

    public void clearLocalStateOnly() {
        this.particleColors = null;
        this.colorsInitialized = false;
        this.pillarId = null;
        this.particleColorCounter = 0;
        this.lastParticleTick = 0;
        this.colorsVersion++;
        setChanged();
    }

    public Double getPatternSpeed() {
        return patternSpeed;
    }

    public void setPatternSpeed(Double speed) {
        this.patternSpeed = speed;
        setChanged();
    }

    public Double getPatternSpread() {
        return patternSpread;
    }

    public void setPatternSpread(Double spread) {
        this.patternSpread = spread;
        setChanged();
    }

    public Double getPatternIntensity() {
        return patternIntensity;
    }

    public void setPatternIntensity(Double intensity) {
        this.patternIntensity = intensity;
        setChanged();
    }

    public Boolean getUsePattern() {
        return usePattern;
    }

    public void setUsePattern(Boolean usePattern) {
        this.usePattern = usePattern;
        setChanged();
    }

    public Integer getMaxParticleColor() {
        return maxParticleColor;
    }

    public void setMaxParticleColor(Integer maxParticleColor) {
        this.maxParticleColor = maxParticleColor;
        setChanged();
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return displayedItem.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? displayedItem : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot == 0 && !displayedItem.isEmpty()) {
            ItemStack split = displayedItem.split(amount);
            if (displayedItem.isEmpty()) {
                displayedItem = ItemStack.EMPTY;
            }
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
            return split;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot == 0) {
            ItemStack res = displayedItem;
            displayedItem = ItemStack.EMPTY;
            return res;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            displayedItem = stack;
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level == null || this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clearContent() {
        displayedItem = ItemStack.EMPTY;
        setChanged();
    }

    public AABB getRenderBoundingBox() {
        if (!hasDisplayItem()) {
            return new AABB(worldPosition);
        }
        return new AABB(
                worldPosition.getX() - 2, worldPosition.getY(), worldPosition.getZ() - 2,
                worldPosition.getX() + 3, worldPosition.getY() + 8, worldPosition.getZ() + 3
        );
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void writeData(IBlockEntityWriteData data) {
        if (!displayedItem.isEmpty()) {
            data.putItem("DisplayedItem", displayedItem);
        }
        data.putFloat("FacingYaw", facingYaw);

        if (particlePattern != null) {
            data.putString("Pattern", particlePattern);
        }
        if (patternSpeed != null) {
            data.putFloat("PatternSpeed", patternSpeed.floatValue());
        }
        if (patternSpread != null) {
            data.putFloat("PatternSpread", patternSpread.floatValue());
        }
        if (patternIntensity != null) {
            data.putFloat("PatternIntensity", patternIntensity.floatValue());
        }
        if (usePattern != null) {
            data.putBoolean("UsePattern", usePattern);
        }
        if (maxParticleColor != null) {
            data.putInt("MaxParticleColor", maxParticleColor);
        }
        if (pillarId != null) {
            data.putString("PillarId", pillarId);
        }

        if (particleColors != null && !particleColors.isEmpty()) {
            data.putStringList("ParticleColors", particleColors);
        }
    }

    @Override
    public void readData(IBlockEntityReadData data) {
        displayedItem = data.getItemOrEmpty("DisplayedItem");
        facingYaw = data.getFloatOr("FacingYaw", 0.0f);
        particlePattern = data.contains("Pattern") ? data.getStringOr("Pattern", "none") : null;
        patternSpeed = data.contains("PatternSpeed") ? (double) data.getFloatOr("PatternSpeed", 1.0f) : null;
        patternSpread = data.contains("PatternSpread") ? (double) data.getFloatOr("PatternSpread", 1.0f) : null;
        patternIntensity = data.contains("PatternIntensity") ? (double) data.getFloatOr("PatternIntensity", 1.0f) : null;
        usePattern = data.contains("UsePattern") ? data.getBooleanOr("UsePattern", false) : null;
        maxParticleColor = data.contains("MaxParticleColor") ? data.getIntOr("MaxParticleColor", 0) : null;
        pillarId = data.contains("PillarId") ? data.getStringOr("PillarId", "") : null;

        List<String> colors = data.getStringListOrEmpty("ParticleColors");
        if (!colors.isEmpty()) {
            particleColors = new ArrayList<>(colors);
            colorsInitialized = true;
        } else {
            particleColors = null;
            colorsInitialized = false;
        }
    }

    private record ParticleSpawnData(double sx, double sy, double sz, double vx, double vy, double vz, float size) {
    }
}
