package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.CopperChestBlockEntity;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.client.event.TextureStitchEvent;

import java.util.List;

public class CopperChestRenderer extends ChestRenderer<CopperChestBlockEntity> {
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
        super(context);
    }

    public static void registerTextures(TextureStitchEvent.Pre event) {
        if (Sheets.CHEST_SHEET.equals(event.getAtlas().location())) {
            TEXTURES.forEach(event::addSprite);
        }
    }

    private static ResourceLocation texture(String name) {
        return new ResourceLocation("buildscape", "entity/chest/" + name);
    }

    private static Material material(int index) {
        return new Material(Sheets.CHEST_SHEET, TEXTURES.get(index));
    }

    @Override
    protected Material getMaterial(CopperChestBlockEntity blockEntity, ChestType chestType) {
        net.minecraft.world.level.block.Block block = blockEntity.getBlockState().getBlock();
        ResourceLocation blockId = net.minecraftforge.registries.ForgeRegistries.BLOCKS.getKey(block);
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
