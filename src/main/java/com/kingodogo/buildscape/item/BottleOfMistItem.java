package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.particle.ModParticles;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.Random;

public class BottleOfMistItem extends Item {

    private static final Random RANDOM = new Random();

    public BottleOfMistItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            // Spawn a burst of cascade particles that last ~2 seconds (40 ticks)
            double px = player.getX();
            double py = player.getEyeY();
            double pz = player.getZ();

            // Look direction for forward offset
            net.minecraft.world.phys.Vec3 look = player.getLookAngle();
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

                level.addAlwaysVisibleParticle(ModParticles.CASCADE.get(), true, x, y, z, xSpeed, ySpeed, zSpeed);
            }
        }

        if (!level.isClientSide) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.1f, 0.1f);
        }

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, java.util.List<net.minecraft.network.chat.Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
        tooltip.add(new net.minecraft.network.chat.TranslatableComponent("tooltip.buildscape.bottle_of_mist.use").withStyle(net.minecraft.ChatFormatting.GRAY));
        tooltip.add(new net.minecraft.network.chat.TranslatableComponent("tooltip.buildscape.mist_toggle").withStyle(net.minecraft.ChatFormatting.DARK_AQUA));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
