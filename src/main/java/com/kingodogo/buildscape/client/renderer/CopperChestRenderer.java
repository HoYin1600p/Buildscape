package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.CopperChestBlockEntity;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.ChestType;

public class CopperChestRenderer extends ChestRenderer<CopperChestBlockEntity> {
    private static final Material SINGLE = new Material(Sheets.CHEST_SHEET, new ResourceLocation("buildscape", "entity/chest/copper_chest"));
    private static final Material LEFT = new Material(Sheets.CHEST_SHEET, new ResourceLocation("buildscape", "entity/chest/copper_chest_left"));
    private static final Material RIGHT = new Material(Sheets.CHEST_SHEET, new ResourceLocation("buildscape", "entity/chest/copper_chest_right"));

    private static final Material EXPOSED = new Material(Sheets.CHEST_SHEET, new ResourceLocation("buildscape", "entity/chest/exposed_copper_chest"));
    private static final Material EXPOSED_LEFT = new Material(Sheets.CHEST_SHEET, new ResourceLocation("buildscape", "entity/chest/exposed_copper_chest_left"));
    private static final Material EXPOSED_RIGHT = new Material(Sheets.CHEST_SHEET, new ResourceLocation("buildscape", "entity/chest/exposed_copper_chest_right"));

    private static final Material WEATHERED = new Material(Sheets.CHEST_SHEET, new ResourceLocation("buildscape", "entity/chest/weathered_copper_chest"));
    private static final Material WEATHERED_LEFT = new Material(Sheets.CHEST_SHEET, new ResourceLocation("buildscape", "entity/chest/weathered_copper_chest_left"));
    private static final Material WEATHERED_RIGHT = new Material(Sheets.CHEST_SHEET, new ResourceLocation("buildscape", "entity/chest/weathered_copper_chest_right"));

    private static final Material OXIDIZED = new Material(Sheets.CHEST_SHEET, new ResourceLocation("buildscape", "entity/chest/oxidized_copper_chest"));
    private static final Material OXIDIZED_LEFT = new Material(Sheets.CHEST_SHEET, new ResourceLocation("buildscape", "entity/chest/oxidized_copper_chest_left"));
    private static final Material OXIDIZED_RIGHT = new Material(Sheets.CHEST_SHEET, new ResourceLocation("buildscape", "entity/chest/oxidized_copper_chest_right"));

    public CopperChestRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected Material getMaterial(CopperChestBlockEntity blockEntity, ChestType chestType) {
        net.minecraft.world.level.block.Block block = blockEntity.getBlockState().getBlock();
        String path = net.minecraftforge.registries.ForgeRegistries.BLOCKS.getKey(block).getPath();
        
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
