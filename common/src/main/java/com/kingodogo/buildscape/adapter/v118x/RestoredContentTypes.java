package com.kingodogo.buildscape.adapter.v118x;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/** Types constructed before loader registration, with deferred lookups for restored blocks. */
public final class RestoredContentTypes {
    private RestoredContentTypes() {}

    private static final java.util.Map<String, BlockEntityType<?>> BLOCK_ENTITIES = new java.util.LinkedHashMap<>();
    private static final java.util.Map<String, EntityType<?>> ENTITIES = new java.util.LinkedHashMap<>();

    public static java.util.Map<String, BlockEntityType<?>> createBlockEntities(
            java.util.Map<String, net.minecraft.world.level.block.Block> blocks) {
        if (BLOCK_ENTITIES.isEmpty()) {
            com.kingodogo.buildscape.platform.Services.PLATFORM.wrapRegistryAction(() -> {
                BLOCK_ENTITIES.put("mangrove_sign_block_entity", com.kingodogo.buildscape.platform.Services.PLATFORM.createBlockEntityTypeForBlocks(
                        com.kingodogo.buildscape.block.MangroveSignBlockEntity::new,
                        blocks.get("mangrove_sign"), blocks.get("mangrove_wall_sign")));
                BLOCK_ENTITIES.put("bamboo_sign_block_entity", com.kingodogo.buildscape.platform.Services.PLATFORM.createBlockEntityTypeForBlocks(
                        com.kingodogo.buildscape.block.BambooSignBlockEntity::new,
                        blocks.get("bamboo_sign"), blocks.get("bamboo_wall_sign")));
                BLOCK_ENTITIES.put("copper_chest", com.kingodogo.buildscape.platform.Services.PLATFORM.createBlockEntityTypeForBlocks(
                        com.kingodogo.buildscape.block.CopperChestBlockEntity::new,
                        blocks.get("copper_chest"), blocks.get("exposed_copper_chest"),
                        blocks.get("weathered_copper_chest"), blocks.get("oxidized_copper_chest"),
                        blocks.get("waxed_copper_chest"), blocks.get("waxed_exposed_copper_chest"),
                        blocks.get("waxed_weathered_copper_chest"), blocks.get("waxed_oxidized_copper_chest")));
                BLOCK_ENTITIES.put("potent_sulfur", com.kingodogo.buildscape.platform.Services.PLATFORM.createBlockEntityTypeForBlocks(
                        com.kingodogo.buildscape.block.PotentSulfurBlockEntity::new,
                        blocks.get("potent_sulfur")));
            });
        }
        return java.util.Collections.unmodifiableMap(BLOCK_ENTITIES);
    }

    public static java.util.Map<String, EntityType<?>> createEntities() {
        if (ENTITIES.isEmpty()) {
            com.kingodogo.buildscape.platform.Services.PLATFORM.wrapRegistryAction(() ->
                    ENTITIES.put("mangrove_boat", EntityType.Builder
                            .<com.kingodogo.buildscape.entity.MangroveBoatEntity>of(
                                    com.kingodogo.buildscape.entity.MangroveBoatEntity::new,
                                    net.minecraft.world.entity.MobCategory.MISC)
                            .sized(1.375F, 0.5625F).clientTrackingRange(10).updateInterval(3)
                            .build("mangrove_boat")));
        }
        return java.util.Collections.unmodifiableMap(ENTITIES);
    }

    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> BlockEntityType<T> blockEntity(String path) {
        ResourceLocation id = new ResourceLocation("buildscape", path);
        BlockEntityType<?> type = Registry.BLOCK_ENTITY_TYPE.getOptional(id).orElse(BLOCK_ENTITIES.get(path));
        if (type == null) throw new IllegalStateException("Block entity type is not constructed: " + id);
        return (BlockEntityType<T>) type;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> EntityType<T> entity(String path) {
        ResourceLocation id = new ResourceLocation("buildscape", path);
        EntityType<?> type = Registry.ENTITY_TYPE.getOptional(id).orElse(ENTITIES.get(path));
        if (type == null) throw new IllegalStateException("Entity type is not constructed: " + id);
        return (EntityType<T>) type;
    }
}
