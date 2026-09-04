package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.item.HammerItem;
import com.kingodogo.buildscape.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class HammerReplacePacket {

    private final BlockPos pos;

    public HammerReplacePacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(HammerReplacePacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static HammerReplacePacket decode(FriendlyByteBuf buf) {
        return new HammerReplacePacket(buf.readBlockPos());
    }

    public static void handle(HammerReplacePacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            ServerLevel level = player.getLevel();
            BlockPos pos = msg.pos;

            if (!level.isLoaded(pos)) return;
            if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 64.0) return;
            if (!player.mayBuild() || player.blockActionRestricted(level, pos, player.gameMode.getGameModeForPlayer())) return;

            ItemStack hammerStack = player.getMainHandItem();
            if (hammerStack.isEmpty() || !(hammerStack.getItem() instanceof HammerItem hammer)) return;

            ItemStack offhandStack = player.getOffhandItem();
            if (offhandStack.isEmpty() || !(offhandStack.getItem() instanceof BlockItem blockItem)) return;

            BlockState targetState = level.getBlockState(pos);
            if (targetState.isAir()) return;

            float destroyTime = targetState.getDestroySpeed(level, pos);
            if (destroyTime < 0) return;

            HammerItem.HammerTier tier = hammer.getHammerTier();
            if (!tier.canReplaceObsidianLevel() && destroyTime >= 50.0f) return;

            Block replacementBlock = blockItem.getBlock();
            if (targetState.getBlock() == replacementBlock) return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            boolean hasSilkTouch = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, hammerStack) > 0;

            List<ItemStack> drops;
            if (hasSilkTouch) {
                LootContext.Builder builder = new LootContext.Builder(level)
                        .withRandom(level.random)
                        .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                        .withParameter(LootContextParams.TOOL, createSilkTouchFakeTool())
                        .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
                drops = targetState.getDrops(builder);
            } else {
                LootContext.Builder builder = new LootContext.Builder(level)
                        .withRandom(level.random)
                        .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                        .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                        .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
                drops = targetState.getDrops(builder);
            }

            BlockState newState = replacementBlock.defaultBlockState();
            level.setBlock(pos, newState, 3);
            com.kingodogo.buildscape.event.AdvancementEvents.onHammerReplace(player);

            if (!player.isCreative()) {
                offhandStack.shrink(1);
            }

            for (ItemStack drop : drops) {
                Block.popResource(level, pos, drop);
            }

            if (!player.isCreative()) {
                hammerStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            }

            level.playSound(null, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 0.5f, 1.2f);
            level.sendParticles(
                    ParticleTypes.CRIT,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    8, 0.3, 0.3, 0.3, 0.05
            );
        });
        ctx.setPacketHandled(true);
    }

    private static ItemStack createSilkTouchFakeTool() {
        ItemStack fakeTool = new ItemStack(net.minecraft.world.item.Items.DIAMOND_PICKAXE);
        fakeTool.enchant(Enchantments.SILK_TOUCH, 1);
        return fakeTool;
    }
}
