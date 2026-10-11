package com.kingodogo.buildscape.adapter.v118x;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/** Deferred lookups for content whose registrations are restored in the next stage. */
public final class RestoredContentTypes {
    private RestoredContentTypes() {}

    // TODO S6b: register these types under the reference ids before constructing their instances.
    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> BlockEntityType<T> blockEntity(String path) {
        ResourceLocation id = new ResourceLocation("buildscape", path);
        return (BlockEntityType<T>) Registry.BLOCK_ENTITY_TYPE.getOptional(id)
                .orElseThrow(() -> new IllegalStateException("Block entity type is not registered: " + id));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> EntityType<T> entity(String path) {
        ResourceLocation id = new ResourceLocation("buildscape", path);
        return (EntityType<T>) Registry.ENTITY_TYPE.getOptional(id)
                .orElseThrow(() -> new IllegalStateException("Entity type is not registered: " + id));
    }
}
