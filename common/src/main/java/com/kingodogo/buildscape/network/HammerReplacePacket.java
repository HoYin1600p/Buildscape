package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.event.AdvancementMilestoneLogic;
import com.kingodogo.buildscape.item.HammerItem;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

public class HammerReplacePacket implements CommonPacket {

    public static final CommonId ID = new CommonId("buildscape", "hammer_replace");

    private final BlockPos pos;

    public HammerReplacePacket(BlockPos pos) {
        this.pos = pos;
    }

    public static HammerReplacePacket decode(FriendlyByteBuf buf) {
        return new HammerReplacePacket(buf.readBlockPos());
    }

    @Override
    public CommonId getId() {
        return ID;
    }

    @Override
    public PacketDirection getDirection() {
        return PacketDirection.CLIENT_TO_SERVER;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public void handle(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        ServerLevel level = (ServerLevel) com.kingodogo.buildscape.platform.Services.PLATFORM.getEntityLevel(serverPlayer);
        if (level == null || !level.isLoaded(pos)) return;
        if (serverPlayer.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 64.0) return;
        if (!serverPlayer.mayBuild() || serverPlayer.blockActionRestricted(level, pos, serverPlayer.gameMode.getGameModeForPlayer())) return;
        if (!level.mayInteract(serverPlayer, pos)) return;

        ItemStack hammerStack = serverPlayer.getMainHandItem();
        if (hammerStack.isEmpty() || !(hammerStack.getItem() instanceof HammerItem hammer)) return;

        ItemStack offhandStack = serverPlayer.getOffhandItem();
        if (offhandStack.isEmpty() || !(offhandStack.getItem() instanceof BlockItem blockItem)) return;

        BlockState targetState = level.getBlockState(pos);
        if (targetState.isAir()) return;

        float destroyTime = targetState.getDestroySpeed(level, pos);
        if (destroyTime < 0) return;

        HammerItem.HammerTier tier = hammer.getHammerTier();
        if (!tier.canReplaceObsidianLevel() && destroyTime >= 50.0f) return;

        Block replacementBlock = blockItem.getBlock();
        if (targetState.getBlock() == replacementBlock) return;
        if (isMultiBlockPart(targetState)) return;

        BlockState newState = replacementBlock.defaultBlockState();
        if (isMultiBlockPart(newState) || !newState.canSurvive(level, pos)) return;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        List<ItemStack> drops = Block.getDrops(targetState, level, pos, blockEntity);

        level.setBlock(pos, newState, 3);
        AdvancementMilestoneLogic.onHammerReplace(serverPlayer);

        if (!serverPlayer.isCreative()) {
            offhandStack.shrink(1);
        }

        for (ItemStack drop : drops) {
            Block.popResource(level, pos, drop);
        }

        if (!serverPlayer.isCreative()) {
            Services.PLATFORM.hurtAndBreak(hammerStack, 1, serverPlayer, InteractionHand.MAIN_HAND);
        }

        level.playSound(null, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 0.5f, 1.2f);
        level.sendParticles(
                ParticleTypes.CRIT,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                8, 0.3, 0.3, 0.3, 0.05
        );
    }

    private static boolean isMultiBlockPart(BlockState state) {
        return state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)
                || state.hasProperty(BlockStateProperties.BED_PART)
                || state.hasProperty(BlockStateProperties.CHEST_TYPE)
                || state.hasProperty(BlockStateProperties.DOOR_HINGE);
    }

    public BlockPos getPos() {
        return pos;
    }
}
