package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.ConfigSetting;
import com.kingodogo.buildscape.feature.pillar.PillarBlockEntity;
import com.kingodogo.buildscape.feature.pillar.PillarIdManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class UpdatePillarDataPacket {

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
            List<String> dyeColors) {
        this.pillarId = pillarId;
        this.pattern = pattern;
        this.usePattern = usePattern;
        this.patternSpeed = patternSpeed != null
                ? NetworkPacketLimits.clamp(patternSpeed, NetworkPacketLimits.MIN_PARTICLE_RATE,
                        NetworkPacketLimits.MAX_PARTICLE_SPEED)
                : null;
        this.patternSpread = patternSpread != null
                ? NetworkPacketLimits.clamp(patternSpread, NetworkPacketLimits.MIN_PARTICLE_RATE,
                        NetworkPacketLimits.MAX_PARTICLE_SPREAD)
                : null;
        this.patternIntensity = patternIntensity != null
                ? NetworkPacketLimits.clamp(patternIntensity, NetworkPacketLimits.MIN_PARTICLE_RATE,
                        NetworkPacketLimits.MAX_PARTICLE_INTENSITY)
                : null;
        this.maxParticleColor = maxParticleColor != null
                ? Math.max(0, Math.min(NetworkPacketLimits.MAX_DYE_COLORS, maxParticleColor))
                : null;
        this.dyeColors = dyeColors != null ? new ArrayList<>(dyeColors) : new ArrayList<>();
    }

    public UpdatePillarDataPacket(PillarIdManager.PillarData data) {
        this(
                data != null ? data.id : null,
                data != null ? data.pattern : null,
                data != null ? data.use_pattern : null,
                data != null ? data.pattern_speed : null,
                data != null ? data.pattern_spread : null,
                data != null ? data.pattern_intensity : null,
                data != null ? data.max_particle_color : null,
                data != null ? data.dyeColors : null);
    }

    public UpdatePillarDataPacket(FriendlyByteBuf buf) {
        this.pillarId = NetworkPacketLimits.readUtf(buf, NetworkPacketLimits.MAX_PILLAR_ID_LENGTH, "pillar id");

        this.pattern = buf.readBoolean()
                ? NetworkPacketLimits.readUtf(buf, NetworkPacketLimits.MAX_PATTERN_LENGTH, "pattern")
                : null;

        this.usePattern = buf.readBoolean() ? buf.readBoolean() : null;

        this.patternSpeed = buf.readBoolean()
                ? NetworkPacketLimits.readBoundedDouble(buf,
                        NetworkPacketLimits.MIN_PARTICLE_RATE, NetworkPacketLimits.MAX_PARTICLE_SPEED, "pattern speed")
                : null;
        this.patternSpread = buf.readBoolean()
                ? NetworkPacketLimits.readBoundedDouble(buf,
                        NetworkPacketLimits.MIN_PARTICLE_RATE, NetworkPacketLimits.MAX_PARTICLE_SPREAD,
                        "pattern spread")
                : null;
        this.patternIntensity = buf.readBoolean()
                ? NetworkPacketLimits.readBoundedDouble(buf,
                        NetworkPacketLimits.MIN_PARTICLE_RATE, NetworkPacketLimits.MAX_PARTICLE_INTENSITY,
                        "pattern intensity")
                : null;

        this.maxParticleColor = buf.readBoolean()
                ? NetworkPacketLimits.readBoundedInt(buf, 0, NetworkPacketLimits.MAX_DYE_COLORS,
                        "maximum particle colors")
                : null;

        int colorCount = NetworkPacketLimits.readCount(buf, NetworkPacketLimits.MAX_DYE_COLORS, "dye color");
        this.dyeColors = new ArrayList<>();
        for (int i = 0; i < colorCount; i++) {
            this.dyeColors
                    .add(NetworkPacketLimits.readUtf(buf, NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "dye color"));
        }
    }

    public static UpdatePillarDataPacket decode(FriendlyByteBuf buf) {
        return new UpdatePillarDataPacket(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        NetworkPacketLimits.writeUtf(buf, pillarId,
                NetworkPacketLimits.MAX_PILLAR_ID_LENGTH, "pillar id");

        buf.writeBoolean(pattern != null);
        if (pattern != null) {
            NetworkPacketLimits.writeUtf(buf, pattern,
                    NetworkPacketLimits.MAX_PATTERN_LENGTH, "pattern");
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

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null || !EditGuard.require(player, ConfigSetting.PILLAR_EDIT)) {
                return;
            }

            PillarIdManager manager = PillarIdManager.get();
            PillarIdManager.PillarData data = manager.getPillarData(pillarId);

            if (data == null) {
                return;
            }

            boolean changed = false;

            if (pattern != null && !pattern.equals(data.pattern)) {
                data.pattern = pattern;
                changed = true;
            }

            if (patternSpeed != null && !patternSpeed.equals(data.pattern_speed)) {
                data.pattern_speed = patternSpeed;
                changed = true;
            }

            if (patternSpread != null && !patternSpread.equals(data.pattern_spread)) {
                data.pattern_spread = patternSpread;
                changed = true;
            }

            if (patternIntensity != null && !patternIntensity.equals(data.pattern_intensity)) {
                data.pattern_intensity = patternIntensity;
                changed = true;
            }

            if (usePattern != null && !usePattern.equals(data.use_pattern)) {
                data.use_pattern = usePattern;
                changed = true;
            }

            if (maxParticleColor != null && !maxParticleColor.equals(data.max_particle_color)) {
                data.max_particle_color = maxParticleColor;
                changed = true;
            }

            if (dyeColors != null && !dyeColors.equals(data.dyeColors)) {
                data.dyeColors = new ArrayList<>(dyeColors);
                changed = true;
            }

            if (changed) {
                data.modifiedTime = System.currentTimeMillis();
                // Flush the explicit detail editor save before returning to gameplay.
                updateBlockEntity(player.server, data);
                manager.applyFrameEdit(data);
                manager.saveImmediate();
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private void updateBlockEntity(MinecraftServer server, PillarIdManager.PillarData data) {
        if (server == null || !server.isRunning()) {
            return;
        }

        for (ServerLevel level : server.getAllLevels()) {
            if (level == null) {
                continue;
            }

            String dimensionKey = PillarIdManager.getDimensionKey(level);
            if (!dimensionKey.equals(data.dimension)) {
                continue;
            }

            BlockPos pos = new BlockPos(data.x, data.y, data.z);

            if (!level.isLoaded(pos)) {
                continue;
            }

            BlockEntity be = level.getBlockEntity(pos);
            if (!(be instanceof PillarBlockEntity pillarBE)) {
                continue;
            }

            BlockPos bottomPos = pillarBE.findStackBottom();
            BlockEntity bottomBE = level.getBlockEntity(bottomPos);

            if (!(bottomBE instanceof PillarBlockEntity bottomPillarBE)) {
                continue;
            }

            boolean patternChanged = data.pattern != null && !data.pattern.isEmpty()
                    && !data.pattern.equals(bottomPillarBE.getParticlePattern());
            bottomPillarBE.syncFromData(data);
            if (patternChanged) {
                bottomPillarBE.setParticlePattern(data.pattern);
            }

            break;
        }
    }
}
