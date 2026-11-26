package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.CopperChestBlockEntity;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.ChestType;

import java.util.List;
import java.util.function.Consumer;

public class CopperChestRenderer implements BlockEntityRenderer<CopperChestBlockEntity> {
    private final ModelPart single;
    private final ModelPart left;
    private final ModelPart right;
    private static final List<ResourceLocation> TEXTURES = List.of(
            texture("copper_chest"),
            texture("copper_chest_left"),
            texture("copper_chest_right"),
            texture("exposed_copper_chest"),
            texture("exposed_copper_chest_left"),
            texture("exposed_copper_chest_right"),
            texture("weathered_copper_chest"),
            texture("weathered_copper_chest_left"),
            texture("weathered_copper_chest_right"),
            texture("oxidized_copper_chest"),
            texture("oxidized_copper_chest_left"),
            texture("oxidized_copper_chest_right")
    );

    private static final Material SINGLE = material(0);
    private static final Material LEFT = material(1);
    private static final Material RIGHT = material(2);

    private static final Material EXPOSED = material(3);
    private static final Material EXPOSED_LEFT = material(4);
    private static final Material EXPOSED_RIGHT = material(5);

    private static final Material WEATHERED = material(6);
    private static final Material WEATHERED_LEFT = material(7);
    private static final Material WEATHERED_RIGHT = material(8);

    private static final Material OXIDIZED = material(9);
    private static final Material OXIDIZED_LEFT = material(10);
    private static final Material OXIDIZED_RIGHT = material(11);

    public CopperChestRenderer(BlockEntityRendererProvider.Context context) {
        single = context.bakeLayer(ModelLayers.CHEST);
        left = context.bakeLayer(ModelLayers.DOUBLE_CHEST_LEFT);
        right = context.bakeLayer(ModelLayers.DOUBLE_CHEST_RIGHT);
    }

    public static void registerTextures(ResourceLocation atlas, Consumer<ResourceLocation> addSprite) {
        if (Sheets.CHEST_SHEET.equals(atlas)) {
            TEXTURES.forEach(addSprite);
        }
    }

    private static ResourceLocation texture(String name) {
        return new ResourceLocation("buildscape", "entity/chest/" + name);
    }

    private static Material material(int index) {
        return new Material(Sheets.CHEST_SHEET, TEXTURES.get(index));
    }

    protected Material getMaterial(CopperChestBlockEntity blockEntity, ChestType chestType) {
        net.minecraft.world.level.block.Block block = blockEntity.getBlockState().getBlock();
        CommonId blockId = Services.PLATFORM.getBlockId(block);
        String path = blockId == null ? "" : blockId.getPath();

        if (path.contains("oxidized")) {
            return chooseMaterial(chestType, OXIDIZED, OXIDIZED_LEFT, OXIDIZED_RIGHT);
        } else if (path.contains("weathered")) {
            return chooseMaterial(chestType, WEATHERED, WEATHERED_LEFT, WEATHERED_RIGHT);
        } else if (path.contains("exposed")) {
            return chooseMaterial(chestType, EXPOSED, EXPOSED_LEFT, EXPOSED_RIGHT);
        } else {
            return chooseMaterial(chestType, SINGLE, LEFT, RIGHT);
        }
    }

    @Override
    public void render(CopperChestBlockEntity blockEntity, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffers, int light, int overlay) {
        BlockState state = blockEntity.getBlockState();
        if (!(state.getBlock() instanceof ChestBlock chest)) return;
        ChestType chestType = state.getValue(ChestBlock.TYPE);
        float openness = blockEntity.getOpenNess(partialTicks);
        if (blockEntity.getLevel() != null) {
            DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combined =
                    chest.combine(state, blockEntity.getLevel(), blockEntity.getBlockPos(), true);
            openness = combined.apply(ChestBlock.opennessCombiner(blockEntity)).get(partialTicks);
            light = combined.apply(new BrightnessCombiner<>()).applyAsInt(light);
        }
        openness = 1.0F - openness;
        openness = 1.0F - openness * openness * openness;
        ModelPart model = chestType == ChestType.LEFT ? left : chestType == ChestType.RIGHT ? right : single;
        ModelPart lid = model.getChild("lid");
        ModelPart lock = model.getChild("lock");
        ModelPart bottom = model.getChild("bottom");
        lid.xRot = -(openness * ((float) Math.PI / 2.0F));
        lock.xRot = lid.xRot;
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(-state.getValue(ChestBlock.FACING).toYRot()));
        poseStack.translate(-0.5D, -0.5D, -0.5D);
        VertexConsumer vertices = getMaterial(blockEntity, chestType).buffer(buffers, RenderType::entityCutout);
        lid.render(poseStack, vertices, light, overlay);
        lock.render(poseStack, vertices, light, overlay);
        bottom.render(poseStack, vertices, light, overlay);
        poseStack.popPose();
    }

    private Material chooseMaterial(ChestType chestType, Material single, Material left, Material right) {
        switch (chestType) {
            case LEFT:
                return left;
            case RIGHT:
                return right;
            default:
                return single;
        }
    }
}
