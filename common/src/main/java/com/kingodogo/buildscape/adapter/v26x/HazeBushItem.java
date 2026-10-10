package com.kingodogo.buildscape.adapter.v26x;

import com.kingodogo.buildscape.block.HazeBushBlock;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;

public final class HazeBushItem extends com.kingodogo.buildscape.item.HazeBushItem {
    public HazeBushItem(Block block, Properties properties) { super(block, properties); }

    @Override
    protected boolean hasDrainedState(ItemStack stack) {
        var state = stack.get(DataComponents.BLOCK_STATE);
        if (state != null && state.properties().containsKey("has_haze")) {
            return "false".equalsIgnoreCase(state.properties().get("has_haze"));
        }
        var data = Services.PLATFORM.getCustomData(stack, false);
        if (data == null) return false;
        var legacy = data.getCompound("BlockStateTag").flatMap(tag -> tag.getString("has_haze"));
        return legacy.isPresent() ? "false".equalsIgnoreCase(legacy.get()) : !data.getBoolean("HasHaze").orElse(true);
    }

    @Override
    protected void writeDrainedState(ItemStack stack) {
        var state = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
        stack.set(DataComponents.BLOCK_STATE, state.with(HazeBushBlock.HAS_HAZE, false));
        var data = Services.PLATFORM.getCustomData(stack, false);
        if (data != null) {
            data.remove("HasHaze");
            data.getCompound("BlockStateTag").ifPresent(legacy -> {
                legacy.remove("has_haze");
                if (legacy.isEmpty()) data.remove("BlockStateTag");
            });
            if (data.isEmpty()) stack.remove(DataComponents.CUSTOM_DATA);
            else stack.set(DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(data));
        }
    }

    @Override
    public net.minecraft.world.InteractionResult useOn(UseOnContext context) {
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
                        !isDrained(context.getItemInHand())), 3);
                player.awardStat(net.minecraft.stats.Stats.POT_FLOWER);
                if (!player.getAbilities().instabuild) context.getItemInHand().shrink(1);
            }
            return Services.PLATFORM.sidedSuccess(level.isClientSide());
        }
        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
            java.util.function.Consumer<Component> tooltip, TooltipFlag flag) {
        if (isDrained(stack)) tooltip.accept(Component.translatable("tooltip.buildscape.haze_bush.drained")
                .withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, display, tooltip, flag);
    }
}
