package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.block.CushionBlock;
import com.kingodogo.buildscape.block.CushionSurface;
import com.kingodogo.buildscape.entity.CushionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CushionBlockItem extends BlockItem {

    private final DyeColor color;

    public CushionBlockItem(Block block, Properties properties, DyeColor color) {
        super(block, properties);
        this.color = color;
    }

    public DyeColor getColor() {
        return color;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!CushionBlock.USE_CUSHION_ENTITIES || (context.getPlayer() != null && context.getPlayer().isShiftKeyDown())) {
            return super.useOn(context);
        }

        Level level = context.getLevel();
        BlockPos supportPos = context.getClickedPos();
        BlockState support = level.getBlockState(supportPos);
        double localX = context.getClickLocation().x - supportPos.getX();
        double localZ = context.getClickLocation().z - supportPos.getZ();
        double supportHeight = CushionSurface.topAt(support.getShape(level, supportPos), localX, localZ);
        Player player = context.getPlayer();

        if (Double.isNaN(supportHeight) || (player != null && !player.mayUseItemAt(supportPos, context.getClickedFace(), context.getItemInHand()))) {
            return super.useOn(context);
        }

        if (!level.isClientSide()) {
            Entity entity = CushionEntity.create(level, supportPos, localX, localZ, supportHeight, this.color);
            if (entity != null) {
                level.addFreshEntity(entity);
            }
        }
        context.getItemInHand().shrink(1);
        return com.kingodogo.buildscape.platform.Services.PLATFORM.sidedSuccess(level.isClientSide());
    }
}
