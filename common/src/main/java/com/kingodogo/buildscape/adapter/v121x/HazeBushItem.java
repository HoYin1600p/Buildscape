package com.kingodogo.buildscape.adapter.v121x;

import com.kingodogo.buildscape.block.HazeBushBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;

import java.util.List;

/** Haze state uses block-state components, with custom data as a legacy fallback. */
public final class HazeBushItem extends com.kingodogo.buildscape.item.HazeBushItem {
    public HazeBushItem(Block block, Properties properties) { super(block, properties); }

    @Override
    protected boolean hasDrainedState(ItemStack stack) {
        return hasDrainedComponents(stack);
    }

    static boolean hasDrainedComponents(ItemStack stack) {
        var state = stack.get(DataComponents.BLOCK_STATE);
        if (state != null && state.properties().containsKey("has_haze")) {
            return "false".equalsIgnoreCase(state.properties().get("has_haze"));
        }
        var custom = stack.get(DataComponents.CUSTOM_DATA);
        if (custom == null) return false;
        var data = custom.copyTag();
        var legacy = data.getCompound("BlockStateTag");
        if (legacy.contains("has_haze")) {
            return "false".equalsIgnoreCase(legacy.getString("has_haze"));
        }
        return data.contains("HasHaze") && !data.getBoolean("HasHaze");
    }

    @Override
    protected void writeDrainedState(ItemStack stack) {
        writeDrainedComponents(stack);
    }

    static void writeDrainedComponents(ItemStack stack) {
        var state = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
        stack.set(DataComponents.BLOCK_STATE, state.with(HazeBushBlock.HAS_HAZE, false));
        CustomData.update(DataComponents.CUSTOM_DATA, stack, data -> {
            data.putBoolean("HasHaze", false);
            // Remove the obsolete property while retaining unrelated custom data.
            var legacy = data.getCompound("BlockStateTag");
            legacy.remove("has_haze");
            if (legacy.isEmpty()) data.remove("BlockStateTag");
            else data.put("BlockStateTag", legacy);
        });
    }

    @Override
    public net.minecraft.world.InteractionResult useOn(net.minecraft.world.item.context.UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var player = context.getPlayer();
        if (player != null && !player.isSecondaryUseActive()
                && level.getBlockState(pos).is(net.minecraft.world.level.block.Blocks.FLOWER_POT)) {
            var bush = (HazeBushBlock) getBlock();
            var pot = com.kingodogo.buildscape.block.ModBlocks.get(
                    com.kingodogo.buildscape.block.ModBlocks.POTTED_COLORED_HAZE_BUSHES.get(bush.getColor()));
            if (!level.isClientSide()) {
                level.setBlock(pos, pot.defaultBlockState().setValue(HazeBushBlock.HAS_HAZE,
                        !hasDrainedState(context.getItemInHand())), 3);
                player.awardStat(net.minecraft.stats.Stats.POT_FLOWER);
                if (!player.getAbilities().instabuild) context.getItemInHand().shrink(1);
            }
            return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide());
        }
        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (hasDrainedState(stack)) {
            tooltip.add(Component.translatable("tooltip.buildscape.haze_bush.drained").withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
