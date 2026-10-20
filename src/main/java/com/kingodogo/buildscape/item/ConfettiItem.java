package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.advancement.AdvancementGrants;
import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.network.ConfettiBurstPacket;
import com.kingodogo.buildscape.network.ModMessages;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import javax.annotation.Nullable;

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

            if (player instanceof ServerPlayer serverPlayer) {
                AdvancementGrants.onConfettiUsed(serverPlayer);
            }

            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            player.getCooldowns().addCooldown(this, USE_COOLDOWN_TICKS);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    private void spawnConfettiParticles(ServerLevel level, Player player, ItemStack stack) {
        Vec3 look = player.getLookAngle();
        double startX = player.getX() + look.x * 0.9;
        double startY = player.getEyeY() + look.y * 0.9;
        double startZ = player.getZ() + look.z * 0.9;

        int burstLevel = 1;
        if (stack.hasTag() && stack.getTag().contains(NbtKeys.BURST_LEVEL)) {
            burstLevel = Math.min(5, Math.max(1, stack.getTag().getInt(NbtKeys.BURST_LEVEL)));
        }

        ModMessages.INSTANCE.send(
                PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                        startX, startY, startZ, 32.0, level.dimension())),
                new ConfettiBurstPacket(
                        startX, startY, startZ, (float) look.x, (float) look.y, (float) look.z,
                        burstLevel, level.random.nextLong()));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        int burstLevel = 1;
        if (stack.hasTag() && stack.getTag().contains(NbtKeys.BURST_LEVEL)) {
            burstLevel = Math.min(5, Math.max(1, stack.getTag().getInt(NbtKeys.BURST_LEVEL)));
        }
        tooltip.add(new TranslatableComponent("tooltip.buildscape.confetti.burst_level", burstLevel)
                .withStyle(ChatFormatting.GRAY));
    }
}
