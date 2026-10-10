package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.block.PillarBlockEntity;
import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

public class UpdatePillarDataPacket implements CommonPacket {

    public static final CommonId ID = new CommonId("buildscape", "update_pillar_data");

    private final String pillarId;
    private final String pattern;
    private final Boolean usePattern;
    private final Double patternSpeed;
    private final Double patternSpread;
    private final Double patternIntensity;
    private final Integer maxParticleColor;
    private final List<String> dyeColors;

    public UpdatePillarDataPacket(
            String pillarId,
            String pattern,
            Boolean usePattern,
            Double patternSpeed,
            Double patternSpread,
            Double patternIntensity,
            Integer maxParticleColor,
            List<String> dyeColors
    ) {
        this.pillarId = pillarId;
        this.pattern = pattern;
        this.usePattern = usePattern;
        this.patternSpeed = patternSpeed != null
                ? NetworkPacketLimits.clamp(patternSpeed, NetworkPacketLimits.MIN_PARTICLE_RATE,
                NetworkPacketLimits.MAX_PARTICLE_SPEED) : null;
        this.patternSpread = patternSpread != null
                ? NetworkPacketLimits.clamp(patternSpread, NetworkPacketLimits.MIN_PARTICLE_RATE,
                NetworkPacketLimits.MAX_PARTICLE_SPREAD) : null;
        this.patternIntensity = patternIntensity != null
                ? NetworkPacketLimits.clamp(patternIntensity, NetworkPacketLimits.MIN_PARTICLE_RATE,
                NetworkPacketLimits.MAX_PARTICLE_INTENSITY) : null;
        this.maxParticleColor = maxParticleColor != null
                ? Math.max(0, Math.min(NetworkPacketLimits.MAX_DYE_COLORS, maxParticleColor)) : null;
        this.dyeColors = dyeColors != null ? new ArrayList<>(dyeColors) : new ArrayList<>();
    }

    public UpdatePillarDataPacket(FriendlyByteBuf buf) {
        this.pillarId = NetworkPacketLimits.readUtf(buf, NetworkPacketLimits.MAX_PILLAR_ID_LENGTH, "pillar id");

        this.pattern = buf.readBoolean()
                ? NetworkPacketLimits.readUtf(buf, NetworkPacketLimits.MAX_PATTERN_LENGTH, "pattern") : null;

        this.usePattern = buf.readBoolean() ? buf.readBoolean() : null;

        this.patternSpeed = buf.readBoolean()
                ? NetworkPacketLimits.readBoundedDouble(buf, NetworkPacketLimits.MIN_PARTICLE_RATE,
                NetworkPacketLimits.MAX_PARTICLE_SPEED, "pattern speed") : null;

        this.patternSpread = buf.readBoolean()
                ? NetworkPacketLimits.readBoundedDouble(buf, NetworkPacketLimits.MIN_PARTICLE_RATE,
                NetworkPacketLimits.MAX_PARTICLE_SPREAD, "pattern spread") : null;

        this.patternIntensity = buf.readBoolean()
                ? NetworkPacketLimits.readBoundedDouble(buf, NetworkPacketLimits.MIN_PARTICLE_RATE,
                NetworkPacketLimits.MAX_PARTICLE_INTENSITY, "pattern intensity") : null;

        this.maxParticleColor = buf.readBoolean()
                ? NetworkPacketLimits.readBoundedInt(buf, 0, NetworkPacketLimits.MAX_DYE_COLORS, "max particle color")
                : null;

        int colorCount = NetworkPacketLimits.readCount(buf, NetworkPacketLimits.MAX_DYE_COLORS, "dye color");
        this.dyeColors = new ArrayList<>(colorCount);
        for (int i = 0; i < colorCount; i++) {
            this.dyeColors.add(NetworkPacketLimits.readUtf(buf, NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "dye color"));
        }
    }

    public static UpdatePillarDataPacket decode(FriendlyByteBuf buf) {
        return new UpdatePillarDataPacket(buf);
    }

    @Override
    public CommonId getId() {
        return ID;
    }

    @Override
    public PacketDirection getDirection() {
        return PacketDirection.CLIENT_TO_SERVER;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        NetworkPacketLimits.writeUtf(buf, pillarId, NetworkPacketLimits.MAX_PILLAR_ID_LENGTH, "pillar id");

        buf.writeBoolean(pattern != null);
        if (pattern != null) {
            NetworkPacketLimits.writeUtf(buf, pattern, NetworkPacketLimits.MAX_PATTERN_LENGTH, "pattern");
        }

        buf.writeBoolean(usePattern != null);
        if (usePattern != null) {
            buf.writeBoolean(usePattern);
        }

        buf.writeBoolean(patternSpeed != null);
        if (patternSpeed != null) {
            buf.writeDouble(patternSpeed);
        }

        buf.writeBoolean(patternSpread != null);
        if (patternSpread != null) {
            buf.writeDouble(patternSpread);
        }

        buf.writeBoolean(patternIntensity != null);
        if (patternIntensity != null) {
            buf.writeDouble(patternIntensity);
        }

        buf.writeBoolean(maxParticleColor != null);
        if (maxParticleColor != null) {
            buf.writeInt(maxParticleColor);
        }

        NetworkPacketLimits.checkCount(dyeColors.size(), NetworkPacketLimits.MAX_DYE_COLORS, "dye color");
        buf.writeInt(dyeColors.size());
        for (String color : dyeColors) {
            NetworkPacketLimits.writeUtf(buf, color,
                    NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "dye color");
        }
    }

    @Override
    public void handle(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) || !com.kingodogo.buildscape.platform.Services.PLATFORM.hasPermission(serverPlayer, 2)) return;

        PillarIdManager manager = PillarIdManager.get();
        PillarIdManager.PillarData data = manager.getPillarData(pillarId);
        if (data == null) return;

        boolean changed = false;

        if (pattern != null && !pattern.equals(data.pattern)) {
            data.pattern = pattern;
            changed = true;
        }

        if (patternSpeed != null && !patternSpeed.equals(data.patternSpeed)) {
            data.patternSpeed = patternSpeed;
            changed = true;
        }

        if (patternSpread != null && !patternSpread.equals(data.patternSpread)) {
            data.patternSpread = patternSpread;
            changed = true;
        }

        if (patternIntensity != null && !patternIntensity.equals(data.patternIntensity)) {
            data.patternIntensity = patternIntensity;
            changed = true;
        }

        if (usePattern != null && !usePattern.equals(data.usePattern)) {
            data.usePattern = usePattern;
            changed = true;
        }

        if (maxParticleColor != null && !maxParticleColor.equals(data.maxParticleColor)) {
            data.maxParticleColor = maxParticleColor;
            changed = true;
        }

        if (dyeColors != null && !dyeColors.equals(data.dyeColors)) {
            data.dyeColors = new ArrayList<>(dyeColors);
            changed = true;
        }

        if (changed) {
            data.modifiedTime = System.currentTimeMillis();
            manager.saveImmediate();
            updateBlockEntity(com.kingodogo.buildscape.platform.Services.PLATFORM.getServer(serverPlayer), data);
        }
    }

    private void updateBlockEntity(MinecraftServer server, PillarIdManager.PillarData data) {
        if (server == null || !server.isRunning()) return;

        for (ServerLevel level : server.getAllLevels()) {
            if (level == null) continue;

            String dimensionKey = PillarIdManager.getDimensionKey(level);
            if (!dimensionKey.equals(data.dimension)) continue;

            BlockPos pos = new BlockPos(data.x, data.y, data.z);
            if (!level.isLoaded(pos)) continue;

            BlockEntity be = level.getBlockEntity(pos);
            if (!(be instanceof PillarBlockEntity pillarBE)) continue;

            BlockPos bottomPos = pillarBE.findStackBottom();
            BlockEntity bottomBE = level.getBlockEntity(bottomPos);
            if (!(bottomBE instanceof PillarBlockEntity bottomPillarBE)) continue;

            boolean needsUpdate = false;

            if (data.pattern != null && !data.pattern.isEmpty()) {
                if (bottomPillarBE.getParticlePattern() == null ||
                        !bottomPillarBE.getParticlePattern().equals(data.pattern)) {
                    bottomPillarBE.setParticlePattern(data.pattern);
                    needsUpdate = true;
                }
            }

            if (data.patternSpeed != null) {
                if (bottomPillarBE.getPatternSpeed() == null ||
                        !bottomPillarBE.getPatternSpeed().equals(data.patternSpeed)) {
                    bottomPillarBE.setPatternSpeed(data.patternSpeed);
                    needsUpdate = true;
                }
            }

            if (data.patternSpread != null) {
                if (bottomPillarBE.getPatternSpread() == null ||
                        !bottomPillarBE.getPatternSpread().equals(data.patternSpread)) {
                    bottomPillarBE.setPatternSpread(data.patternSpread);
                    needsUpdate = true;
                }
            }

            if (data.patternIntensity != null) {
                if (bottomPillarBE.getPatternIntensity() == null ||
                        !bottomPillarBE.getPatternIntensity().equals(data.patternIntensity)) {
                    bottomPillarBE.setPatternIntensity(data.patternIntensity);
                    needsUpdate = true;
                }
            }

            if (data.usePattern != null) {
                if (bottomPillarBE.getUsePattern() == null ||
                        !bottomPillarBE.getUsePattern().equals(data.usePattern)) {
                    bottomPillarBE.setUsePattern(data.usePattern);
                    needsUpdate = true;
                }
            }

            if (data.maxParticleColor != null) {
                if (bottomPillarBE.getMaxParticleColor() == null ||
                        !bottomPillarBE.getMaxParticleColor().equals(data.maxParticleColor)) {
                    bottomPillarBE.setMaxParticleColor(data.maxParticleColor);
                    needsUpdate = true;
                }
            }

            if (data.dyeColors != null) {
                bottomPillarBE.setParticleColors(new ArrayList<>(data.dyeColors));
                needsUpdate = true;
            }

            if (needsUpdate) {
                bottomPillarBE.setChanged();
                level.sendBlockUpdated(
                        bottomPos,
                        level.getBlockState(bottomPos),
                        level.getBlockState(bottomPos),
                        3
                );
            }
            break;
        }
    }
}
