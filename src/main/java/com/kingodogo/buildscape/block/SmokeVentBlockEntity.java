package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.particle.SmokeColorRegistry;
import com.kingodogo.buildscape.registry.ModBlockEntities;
import com.kingodogo.buildscape.registry.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;

import java.util.Locale;
import java.util.Random;

public class SmokeVentBlockEntity extends BlockEntity {

    private String smokeColor = null;
    private boolean active = true;

    public SmokeVentBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SMOKE_VENT_BLOCK_ENTITY.get(), pos, state);
    }

    public static void particleTick(Level level, BlockPos pos, BlockState state, SmokeVentBlockEntity vent) {
        PillarPart part = state.getValue(SmokeVentBlock.PART);
        if (!level.isClientSide || !vent.isActive() || (part != PillarPart.TOP && part != PillarPart.SINGLE)) {
            return;
        }
        Random random = level.random;
        if (random.nextFloat() >= 0.11F) {
            return;
        }
        int count = random.nextInt(2) + 2;
        for (int i = 0; i < count; i++) {
            double x = pos.getX() + 0.5 + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1);
            double y = pos.getY() + SmokeVentBlock.SMOKE_SPAWN_BASE + random.nextDouble() * (2.0 - SmokeVentBlock.SMOKE_SPAWN_BASE);
            double z = pos.getZ() + 0.5 + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1);
            if (vent.getSmokeColor() != null) {
                SmokeColorRegistry.registerColorForPosition(x, y, z, vent.getSmokeColor());
                level.addAlwaysVisibleParticle(ModParticles.COLORED_SMOKE.get(), true, x, y, z, 0.0, 0.07, 0.0);
            } else {
                level.addAlwaysVisibleParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, true, x, y, z, 0.0, 0.07, 0.0);
            }
        }
    }

    public String getSmokeColor() {
        return smokeColor;
    }

    public void setSmokeColor(String color) {
        this.smokeColor = color;
        this.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        this.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains(NbtKeys.SMOKE_COLOR, 8)) {
            String color = tag.getString(NbtKeys.SMOKE_COLOR);
            if (color != null && !color.isEmpty() && color.matches("^#[0-9A-Fa-f]{6}$")) {
                this.smokeColor = color.toUpperCase(Locale.ROOT);
            } else {
                this.smokeColor = null;
            }
        } else {
            this.smokeColor = null;
        }

        if (tag.contains(NbtKeys.ACTIVE, 1)) {
            this.active = tag.getBoolean(NbtKeys.ACTIVE);
        } else {
            this.active = true;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        if (smokeColor != null && !smokeColor.isEmpty()) {
            tag.putString(NbtKeys.SMOKE_COLOR, smokeColor);
        }

        tag.putBoolean(NbtKeys.ACTIVE, active);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
