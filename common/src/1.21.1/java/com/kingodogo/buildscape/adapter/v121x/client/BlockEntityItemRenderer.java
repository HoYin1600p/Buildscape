package com.kingodogo.buildscape.adapter.v121x.client;

import com.kingodogo.buildscape.block.CopperChestBlockEntity;
import com.kingodogo.buildscape.block.GlassJarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

/** A fresh display entity prevents one stack's jar contents leaking into another. */
public final class BlockEntityItemRenderer extends BlockEntityWithoutLevelRenderer {
    private final boolean chest;

    public BlockEntityItemRenderer(boolean chest) {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.chest = chest;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack pose,
            MultiBufferSource buffers, int light, int overlay) {
        if (!(stack.getItem() instanceof BlockItem item)) return;
        var mc = Minecraft.getInstance();
        var state = item.getBlock().defaultBlockState();
        BlockEntity entity = chest ? new CopperChestBlockEntity(BlockPos.ZERO, state)
                : new GlassJarBlockEntity(BlockPos.ZERO, state);
        if (mc.level != null) {
            entity.setLevel(mc.level);
            var data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
            if (data != null) entity.loadWithComponents(data.copyTag(), mc.level.registryAccess());
        }
        pose.pushPose();
        if (chest) {
            pose.translate(0.5D, 0.5D, 0.5D);
            pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(270.0F));
            pose.translate(-0.5D, -0.5D, -0.5D);
        }
        mc.getBlockEntityRenderDispatcher().renderItem(entity, pose, buffers, light, overlay);
        pose.popPose();
    }
}
