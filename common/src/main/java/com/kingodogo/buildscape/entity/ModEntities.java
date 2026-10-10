package com.kingodogo.buildscape.entity;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.registry.RegistrySupplier;

public class ModEntities {

    public static final RegistrySupplier<EntityDefinition> FALLING_ICICLE = register("falling_icicle");
    public static final RegistrySupplier<EntityDefinition> FESTIVE_STOCKING = register("festive_stocking");
    public static final RegistrySupplier<EntityDefinition> MANGROVE_BOAT = register("mangrove_boat");
    public static final RegistrySupplier<EntityDefinition> COLORED_ITEM_FRAME = register("colored_item_frame");
    public static final RegistrySupplier<EntityDefinition> SEAT_ENTITY = register("seat");
    public static final RegistrySupplier<EntityDefinition> POPLAR_BOAT = register("poplar_boat");
    public static final RegistrySupplier<EntityDefinition> WANDERING_HOMEMAKER = register("wandering_homemaker");
    public static final RegistrySupplier<EntityDefinition> FESTIVE_WANDERING_HOMEMAKER = register("festive_wandering_homemaker");

    public static RegistrySupplier<EntityDefinition> register(String name) {
        return Services.REGISTRY.registerEntity(name, () -> new EntityDefinition(name));
    }

    public static void init() {
        System.out.println("ModEntities initialized: " + Services.REGISTRY.getRegisteredEntities().size() + " entities registered.");
    }
}
