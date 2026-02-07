package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.entity.ColoredItemFrameEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class ColoredItemFrameItem extends Item {

    private final String colorVariant;

    public ColoredItemFrameItem(Properties properties, String color) {
        super(properties);
        this.colorVariant = color;
    }

    public String getColorVariant() {
        return colorVariant;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos blockPos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockPos placePos = blockPos.relative(direction);
        net.minecraft.world.entity.player.Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();

        if (player != null && !this.canPlace(player, direction, itemStack, placePos)) {
            return InteractionResult.FAIL;
        }

        Level level = context.getLevel();
        ColoredItemFrameEntity itemFrame = new ColoredItemFrameEntity(level, placePos, direction, colorVariant);

        CompoundTag tag = itemStack.getTag();
        if (tag != null) {
            EntityType.updateCustomEntityTag(level, player, itemFrame, tag);
        }

        if (itemFrame.survives()) {
            if (!level.isClientSide) {
                itemFrame.playPlacementSound();
                level.gameEvent(player, GameEvent.ENTITY_PLACE, placePos);
                level.addFreshEntity(itemFrame);
            }

            itemStack.shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.CONSUME;
        }
    }

    protected boolean canPlace(net.minecraft.world.entity.player.Player player, Direction direction,
                               ItemStack stack, BlockPos pos) {
        return !player.level.isOutsideBuildHeight(pos) && player.mayUseItemAt(pos, direction, stack);
    }
}
