package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class ColoredItemFrameItem extends Item {

    private final String colorVariant;

    public ColoredItemFrameItem(Properties properties, String colorVariant) {
        super(properties);
        this.colorVariant = colorVariant;
    }

    public String getColorVariant() {
        return colorVariant;
    }

    public void appendColoredItemFrameTooltip(ItemStack stack, Consumer<Component> tooltip) {
        if ("invisible".equals(this.colorVariant)) {
            tooltip.accept(ComponentHelper.translatable("item.buildscape.invisible_item_frame.tooltip").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public net.minecraft.world.InteractionResult useOn(net.minecraft.world.item.context.UseOnContext context) {
        net.minecraft.core.BlockPos clickedPos = context.getClickedPos();
        net.minecraft.core.Direction face = context.getClickedFace();
        net.minecraft.core.BlockPos placePos = clickedPos.relative(face);
        net.minecraft.world.entity.player.Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();
        net.minecraft.world.level.Level level = context.getLevel();

        if (player != null && !player.mayUseItemAt(placePos, face, itemStack)) {
            return net.minecraft.world.InteractionResult.FAIL;
        }

        net.minecraft.world.entity.Entity itemFrame = com.kingodogo.buildscape.platform.Services.PLATFORM.createColoredItemFrameEntity(level, placePos, face, colorVariant);
        if (itemFrame != null) {
            if (!level.isClientSide()) {
                com.kingodogo.buildscape.platform.Services.PLATFORM.playItemFrameAdd(level, placePos);
                level.gameEvent(player, net.minecraft.world.level.gameevent.GameEvent.ENTITY_PLACE, placePos);
                level.addFreshEntity(itemFrame);
                if (player != null && !player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
            }
            return net.minecraft.world.InteractionResult.SUCCESS;
        }
        return net.minecraft.world.InteractionResult.PASS;
    }
}
