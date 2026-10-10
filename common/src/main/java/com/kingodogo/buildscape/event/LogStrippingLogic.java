package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.block.HollowLogBlockEntity;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
public final class LogStrippingLogic {

    private LogStrippingLogic() {}
    public static InteractionResult handleAxeStrip(Player player, Level level, InteractionHand hand, BlockPos pos) {
        if (player == null || level == null || pos == null) return InteractionResult.PASS;
        ItemStack held = player.getItemInHand(hand);
        if (!(held.getItem() instanceof AxeItem)) return InteractionResult.PASS;

        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        CommonId blockId = Services.PLATFORM.getBlockId(block);
        if (blockId == null) return InteractionResult.PASS;

        String path = blockId.getPath();
        String strippedPath = null;
        if (path.startsWith("hollow_")) {
            strippedPath = "stripped_hollow_" + path.substring("hollow_".length());
        } else if (!path.startsWith("stripped_")) {
            strippedPath = "stripped_" + path;
        }

        if (strippedPath == null) return InteractionResult.PASS;

        Block targetBlock = Services.PLATFORM.getBlock(CommonId.of(blockId.getNamespace(), strippedPath));
        if (targetBlock == null || targetBlock == Blocks.AIR || targetBlock == block) {
            return InteractionResult.PASS;
        }

        BlockState nextState = copyStateProperties(state, targetBlock.defaultBlockState());

        if (!level.isClientSide()) {
            BlockState glassNeg = null;
            BlockState glassPos = null;
            BlockState deco = null;
            String fluid = null;
            if (level.getBlockEntity(pos) instanceof HollowLogBlockEntity oldBe) {
                glassNeg = oldBe.getGlassCoverNeg();
                glassPos = oldBe.getGlassCoverPos();
                deco = oldBe.getDecorationState();
                fluid = oldBe.getFluidType();
                oldBe.setGlassCoverNeg(null);
                oldBe.setGlassCoverPos(null);
                oldBe.setDecorationState(null);
            }

            level.setBlock(pos, nextState, 11);

            if (level.getBlockEntity(pos) instanceof HollowLogBlockEntity newBe) {
                if (glassNeg != null) newBe.setGlassCoverNeg(glassNeg);
                if (glassPos != null) newBe.setGlassCoverPos(glassPos);
                if (deco != null) newBe.setDecorationState(deco);
                if (fluid != null) newBe.setFluidType(fluid);
                newBe.setChanged();
                newBe.syncToClient();
            }

            Services.PLATFORM.playAxeStrip(level, pos);
            if (!player.getAbilities().instabuild) {
                Services.PLATFORM.hurtAndBreak(held, 1, player, hand);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static BlockState copyStateProperties(BlockState from, BlockState to) {
        for (Property prop : from.getProperties()) {
            if (to.hasProperty(prop)) {
                to = to.setValue(prop, from.getValue(prop));
            }
        }
        return to;
    }
}
