package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.block.PillarBlockEntity;
import com.kingodogo.buildscape.config.PillarIdManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
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
            List<String> dyeColors
    ) {
        this.pillarId = pillarId;
        this.pattern = pattern;
        this.usePattern = usePattern;
        this.patternSpeed = patternSpeed;
        this.patternSpread = patternSpread;
        this.patternIntensity = patternIntensity;
        this.maxParticleColor = maxParticleColor;
        this.dyeColors = dyeColors != null ? new ArrayList<>(dyeColors) : new ArrayList<>();
    }

    public UpdatePillarDataPacket(FriendlyByteBuf buf) {
        this.pillarId = buf.readUtf();

        // Read nullable pattern
        this.pattern = buf.readBoolean() ? buf.readUtf() : null;

        // Read nullable boolean
        this.usePattern = buf.readBoolean() ? buf.readBoolean() : null;

        // Read nullable doubles
        this.patternSpeed = buf.readBoolean() ? buf.readDouble() : null;
        this.patternSpread = buf.readBoolean() ? buf.readDouble() : null;
        this.patternIntensity = buf.readBoolean() ? buf.readDouble() : null;

        // Read nullable integer
        this.maxParticleColor = buf.readBoolean() ? buf.readInt() : null;

        // Read color list
        int colorCount = buf.readInt();
        this.dyeColors = new ArrayList<>();
        for (int i = 0; i < colorCount; i++) {
            this.dyeColors.add(buf.readUtf());
        }
    }

    public static UpdatePillarDataPacket decode(FriendlyByteBuf buf) {
        return new UpdatePillarDataPacket(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(pillarId);

        // Write nullable pattern
        buf.writeBoolean(pattern != null);
        if (pattern != null) {
            buf.writeUtf(pattern);
        }

        // Write nullable boolean
        buf.writeBoolean(usePattern != null);
        if (usePattern != null) {
            buf.writeBoolean(usePattern);
        }

        // Write nullable doubles
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

        // Write nullable integer
        buf.writeBoolean(maxParticleColor != null);
        if (maxParticleColor != null) {
            buf.writeInt(maxParticleColor);
        }

        // Write color list
        buf.writeInt(dyeColors.size());
        for (String color : dyeColors) {
            buf.writeUtf(color);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            PillarIdManager manager = PillarIdManager.get();
            PillarIdManager.PillarData data = manager.getPillarData(pillarId);

            if (data == null) {
                return;
            }

            // Update the pillar data in the manager
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
                manager.saveImmediate();

                // Update the block entity NBT
                updateBlockEntity(player.server, data);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private void updateBlockEntity(net.minecraft.server.MinecraftServer server, PillarIdManager.PillarData data) {
        if (server == null || !server.isRunning()) {
            return;
        }

        // Find the level for this pillar's dimension
        for (ServerLevel level : server.getAllLevels()) {
            if (level == null) continue;

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

            // Find the bottom of the stack
            BlockPos bottomPos = pillarBE.findStackBottom();
            BlockEntity bottomBE = level.getBlockEntity(bottomPos);

            if (!(bottomBE instanceof PillarBlockEntity bottomPillarBE)) {
                continue;
            }

            // Update NBT with settings from manager
            boolean needsUpdate = false;

            // Update pattern
            if (data.pattern != null && !data.pattern.isEmpty()) {
                if (bottomPillarBE.getParticlePattern() == null ||
                        !bottomPillarBE.getParticlePattern().equals(data.pattern)) {
                    bottomPillarBE.setParticlePattern(data.pattern);
                    needsUpdate = true;
                }
            }

            // Update pattern speed
            if (data.pattern_speed != null) {
                if (bottomPillarBE.getPatternSpeed() == null ||
                        !bottomPillarBE.getPatternSpeed().equals(data.pattern_speed)) {
                    bottomPillarBE.setPatternSpeed(data.pattern_speed);
                    needsUpdate = true;
                }
            }

            // Update pattern spread
            if (data.pattern_spread != null) {
                if (bottomPillarBE.getPatternSpread() == null ||
                        !bottomPillarBE.getPatternSpread().equals(data.pattern_spread)) {
                    bottomPillarBE.setPatternSpread(data.pattern_spread);
                    needsUpdate = true;
                }
            }

            // Update pattern intensity
            if (data.pattern_intensity != null) {
                if (bottomPillarBE.getPatternIntensity() == null ||
                        !bottomPillarBE.getPatternIntensity().equals(data.pattern_intensity)) {
                    bottomPillarBE.setPatternIntensity(data.pattern_intensity);
                    needsUpdate = true;
                }
            }

            // Update use_pattern toggle
            if (data.use_pattern != null) {
                if (bottomPillarBE.getUsePattern() == null ||
                        !bottomPillarBE.getUsePattern().equals(data.use_pattern)) {
                    bottomPillarBE.setUsePattern(data.use_pattern);
                    needsUpdate = true;
                }
            }

            // Update max particle color
            if (data.max_particle_color != null) {
                if (bottomPillarBE.getMaxParticleColor() == null ||
                        !bottomPillarBE.getMaxParticleColor().equals(data.max_particle_color)) {
                    bottomPillarBE.setMaxParticleColor(data.max_particle_color);
                    needsUpdate = true;
                }
            }

            // Update colors
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
