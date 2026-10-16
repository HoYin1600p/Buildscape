package com.kingodogo.buildscape.adapter.v121x;

import com.kingodogo.buildscape.block.CopperChestBlockEntity;
import com.kingodogo.buildscape.block.PotentSulfurBlockEntity;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Types retained on 1.21.1, constructed after blocks and before loader registration. */
public final class RestoredContentTypes {
    private static final Map<String, BlockEntityType<?>> TYPES = new LinkedHashMap<>();
    private RestoredContentTypes() {}

    public static Map<String, BlockEntityType<?>> createBlockEntities(Map<String, Block> blocks) {
        if (TYPES.isEmpty()) Services.PLATFORM.wrapRegistryAction(() -> {
            TYPES.put("copper_chest", Services.PLATFORM.createBlockEntityTypeForBlocks(
                    CopperChestBlockEntity::new,
                    blocks.get("copper_chest"), blocks.get("exposed_copper_chest"),
                    blocks.get("weathered_copper_chest"), blocks.get("oxidized_copper_chest"),
                    blocks.get("waxed_copper_chest"), blocks.get("waxed_exposed_copper_chest"),
                    blocks.get("waxed_weathered_copper_chest"), blocks.get("waxed_oxidized_copper_chest")));
            TYPES.put("potent_sulfur", Services.PLATFORM.createBlockEntityTypeForBlocks(
                    PotentSulfurBlockEntity::new, blocks.get("potent_sulfur")));
        });
        return Collections.unmodifiableMap(TYPES);
    }

    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> BlockEntityType<T> blockEntity(String path) {
        var id = ResourceLocation.fromNamespaceAndPath("buildscape", path);
        BlockEntityType<?> type = BuiltInRegistries.BLOCK_ENTITY_TYPE.getOptional(id).orElse(TYPES.get(path));
        if (type == null) throw new IllegalStateException("Block entity type is not constructed: " + id);
        return (BlockEntityType<T>) type;
    }
}
