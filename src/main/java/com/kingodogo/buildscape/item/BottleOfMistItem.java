package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.registry.ModParticles;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class BottleOfMistItem extends Item {

    private static final Random RANDOM = new Random();

    public BottleOfMistItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level instanceof ServerLevel serverLevel) {
            double px = player.getX();
            double py = player.getEyeY();
            double pz = player.getZ();

            Vec3 look = player.getLookAngle();
            double cx = px + look.x * 2.0;
            double cy = py + look.y * 2.0;
            double cz = pz + look.z * 2.0;

            for (int i = 0; i < 40; i++) {
                double x = cx + (RANDOM.nextDouble() - 0.5) * 2.0;
                double y = cy + (RANDOM.nextDouble() - 0.5) * 1.0;
                double z = cz + (RANDOM.nextDouble() - 0.5) * 2.0;

                double xSpeed = (RANDOM.nextDouble() - 0.5) * 0.2;
                double ySpeed = RANDOM.nextDouble() * 0.05;
                double zSpeed = (RANDOM.nextDouble() - 0.5) * 0.2;

                serverLevel.sendParticles(ModParticles.CASCADE.get(), x, y, z, 0, xSpeed, ySpeed, zSpeed, 1.0);
            }
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.1f, 0.1f);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(new TranslatableComponent("tooltip.buildscape.bottle_of_mist.use").withStyle(ChatFormatting.GRAY));
        tooltip.add(
                new TranslatableComponent("tooltip.buildscape.bottle_of_mist.collect").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("tooltip.buildscape.mist_toggle").withStyle(ChatFormatting.DARK_AQUA));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
