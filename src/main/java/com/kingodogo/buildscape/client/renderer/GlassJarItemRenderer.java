package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.GlassJarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class GlassJarItemRenderer extends BlockEntityWithoutLevelRenderer {

    public static final GlassJarItemRenderer INSTANCE = new GlassJarItemRenderer();
    private GlassJarBlockEntity dummyBE;

    public GlassJarItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(
            ItemStack stack,
            ItemTransforms.TransformType transformType,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int combinedLight,
            int combinedOverlay
    ) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            BlockState state = blockItem.getBlock().defaultBlockState();

            if (dummyBE == null) {
                dummyBE = new GlassJarBlockEntity(BlockPos.ZERO, state);
            } else {
                dummyBE.setBlockState(state);
            }

            if (Minecraft.getInstance().level != null) {
                dummyBE.setLevel(Minecraft.getInstance().level);
            }

            if (stack.hasTag() && stack.getTag().contains("BlockEntityTag", 10)) {
                dummyBE.load(stack.getTag().getCompound("BlockEntityTag"));
            } else {
                dummyBE.load(new CompoundTag());
            }

            GlassJarBlockEntityRenderer renderer = (GlassJarBlockEntityRenderer) Minecraft.getInstance()
                    .getBlockEntityRenderDispatcher()
                    .getRenderer(dummyBE);
            if (renderer != null) {
                renderer.render(dummyBE, 0.0F, poseStack, bufferSource, combinedLight, combinedOverlay);
            }
        }
    }
}
