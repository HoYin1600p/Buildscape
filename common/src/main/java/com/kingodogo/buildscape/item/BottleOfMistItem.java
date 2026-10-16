package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.particle.ModParticles;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Random;
import java.util.function.Consumer;

public class BottleOfMistItem extends Item {
    private static final Random RANDOM = new Random();

    public BottleOfMistItem(Properties properties) {
        super(properties);
    }

    public void useBuildscape(Level level, Player player, ItemStack stack) {
        if (level.isClientSide()) {
            double px = player.getX();
            double py = player.getEyeY();
            double pz = player.getZ();
            net.minecraft.world.phys.Vec3 look = player.getLookAngle();
            double cx = px + look.x * 2.0D;
            double cy = py + look.y * 2.0D;
            double cz = pz + look.z * 2.0D;
            for (int i = 0; i < 40; i++) {
                double x = cx + (RANDOM.nextDouble() - 0.5D) * 2.0D;
                double y = cy + (RANDOM.nextDouble() - 0.5D);
                double z = cz + (RANDOM.nextDouble() - 0.5D) * 2.0D;
                double xSpeed = (RANDOM.nextDouble() - 0.5D) * 0.2D;
                double ySpeed = RANDOM.nextDouble() * 0.05D;
                double zSpeed = (RANDOM.nextDouble() - 0.5D) * 0.2D;
                level.addAlwaysVisibleParticle(ModParticles.CASCADE.get(), true, x, y, z, xSpeed, ySpeed, zSpeed);
            }
        }
        if (!level.isClientSide()) {
            Services.PLATFORM.playFireExtinguish(level, player.getX(), player.getY(), player.getZ());
        }
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }

    public void appendBuildscapeTooltip(Consumer<Component> tooltip) {
        tooltip.accept(com.kingodogo.buildscape.platform.Services.PLATFORM.translatable("tooltip.buildscape.bottle_of_mist.use").withStyle(ChatFormatting.GRAY));
        tooltip.accept(com.kingodogo.buildscape.platform.Services.PLATFORM.translatable("tooltip.buildscape.bottle_of_mist.collect").withStyle(ChatFormatting.GRAY));
        tooltip.accept(com.kingodogo.buildscape.platform.Services.PLATFORM.translatable("tooltip.buildscape.mist_toggle").withStyle(ChatFormatting.DARK_AQUA));
    }
}
