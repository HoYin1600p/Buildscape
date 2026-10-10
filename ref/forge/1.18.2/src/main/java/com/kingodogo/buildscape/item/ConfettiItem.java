package com.kingodogo.buildscape.item;

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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.ChatFormatting;

import java.util.List;

public class ConfettiItem extends Item {

    private static final int USE_COOLDOWN_TICKS = 10;

    public ConfettiItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 0.8F, 1.4F);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIREWORK_ROCKET_TWINKLE, SoundSource.PLAYERS, 0.6F, 1.6F);

            spawnConfettiParticles((ServerLevel) level, player, itemstack);

            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                com.kingodogo.buildscape.event.AdvancementEvents.onConfettiUsed(serverPlayer);
            }

            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            player.getCooldowns().addCooldown(this, USE_COOLDOWN_TICKS);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    private void spawnConfettiParticles(ServerLevel level, Player player, ItemStack stack) {
        net.minecraft.world.phys.Vec3 look = player.getLookAngle();
        double startX = player.getX() + look.x * 0.9D;
        double startY = player.getEyeY() + look.y * 0.9D;
        double startZ = player.getZ() + look.z * 0.9D;

        int burstLevel = 1;
        if (stack.hasTag() && stack.getTag().contains("BurstLevel")) {
            burstLevel = Math.min(5, Math.max(1, stack.getTag().getInt("BurstLevel")));
        }

        com.kingodogo.buildscape.network.ModMessages.INSTANCE.send(
                net.minecraftforge.network.PacketDistributor.NEAR.with(() ->
                        new net.minecraftforge.network.PacketDistributor.TargetPoint(
                                startX, startY, startZ, 32.0D, level.dimension())),
                new com.kingodogo.buildscape.network.ConfettiBurstPacket(
                        startX, startY, startZ, (float) look.x, (float) look.y, (float) look.z,
                        burstLevel, level.random.nextLong()));
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        int burstLevel = 1;
        if (stack.hasTag() && stack.getTag().contains("BurstLevel")) {
            burstLevel = Math.min(5, Math.max(1, stack.getTag().getInt("BurstLevel")));
        }
        tooltip.add(new TranslatableComponent("tooltip.buildscape.confetti.burst_level", burstLevel).withStyle(ChatFormatting.GRAY));
    }
}
