package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Locale;
public class SpoolBlock extends RotatedPillarBlock {

    public SpoolBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);

        if (!entity.isSteppingCarefully() && level instanceof ServerLevel serverLevel) {
            int color = getDyeColor();
            java.util.concurrent.ThreadLocalRandom random = java.util.concurrent.ThreadLocalRandom.current();
            for (int i = 0; i < 4; i++) {
                double px = pos.getX() + 0.1D + random.nextDouble() * 0.8D;
                double py = pos.getY() + 1.0D + random.nextDouble() * 0.1D;
                double pz = pos.getZ() + 0.1D + random.nextDouble() * 0.8D;
                Services.PLATFORM.spawnDustParticles(serverLevel, px, py, pz, color, 0.5F);
            }
        }
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        super.attack(state, level, pos, player);

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            int color = getDyeColor();
            java.util.concurrent.ThreadLocalRandom random = java.util.concurrent.ThreadLocalRandom.current();
            for (int i = 0; i < 8; i++) {
                double px = pos.getX() + 0.1D + random.nextDouble() * 0.8D;
                double py = pos.getY() + 0.1D + random.nextDouble() * 0.8D;
                double pz = pos.getZ() + 0.1D + random.nextDouble() * 0.8D;
                Services.PLATFORM.spawnDustParticles(serverLevel, px, py, pz, color, 0.5F);
            }
        }
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        float destroySpeed = state.getDestroySpeed(level, pos);
        if (destroySpeed == -1.0F) {
            return 0.0F;
        }

        ItemStack tool = player.getMainHandItem();
        float speedMultiplier = 1.0F;

        if (tool.getItem() instanceof ShearsItem || tool.getItem() instanceof HoeItem) {
            if (tool.getItem() instanceof ShearsItem) {
                speedMultiplier = 5.0F;
            } else if (tool.getItem() instanceof HoeItem) {
                speedMultiplier = Services.PLATFORM.getHoeMiningSpeed(tool);
            } else {
                speedMultiplier = 2.0F;
            }

            int efficiencyLevel = Services.PLATFORM.getEfficiencyLevel(tool);
            if (efficiencyLevel > 0) {
                speedMultiplier += (float) (efficiencyLevel * efficiencyLevel + 1);
            }

            return speedMultiplier / destroySpeed / 30.0F;
        } else {
            speedMultiplier = 2.5F;
            return speedMultiplier / destroySpeed / 100.0F;
        }
    }

    public int getDyeColor() {
        CommonId id = Services.PLATFORM.getBlockId(this);
        if (id == null) {
            return 0xFFFFFF;
        }
        String path = id.getPath();
        if ("glow_ink_sack".equals(path) || "glowing_spool".equals(path)) {
            return 0x1AE5E5;
        }
        if (path.endsWith("_dye_sack")) {
            String colorName = path.substring(0, path.length() - "_dye_sack".length());
            try {
                DyeColor dyeColor = DyeColor.valueOf(colorName.toUpperCase(Locale.ROOT));
                float[] rgb = Services.PLATFORM.getDyeDiffuseColors(dyeColor);
                return ((int) (rgb[0] * 255.0F) << 16) | ((int) (rgb[1] * 255.0F) << 8) | (int) (rgb[2] * 255.0F);
            } catch (IllegalArgumentException ignored) {
                // The reference ignores unrecognized dye names and uses its default color.
            }
        }
        if (path.endsWith("_spool")) {
            String colorName = path.substring(0, path.length() - "_spool".length());
            if ("glowing".equals(colorName)) {
                return 0x1AE5E5;
            }
            if (colorName.isEmpty()) {
                return 0xFFFFFF;
            }
            try {
                DyeColor dyeColor = DyeColor.valueOf(colorName.toUpperCase(Locale.ROOT));
                float[] rgb = Services.PLATFORM.getDyeDiffuseColors(dyeColor);
                return ((int) (rgb[0] * 255.0F) << 16) | ((int) (rgb[1] * 255.0F) << 8) | (int) (rgb[2] * 255.0F);
            } catch (IllegalArgumentException ignored) {
                // The reference ignores unrecognized dye names and uses its default color.
            }
        }
        return 0xFFFFFF;
    }
}
