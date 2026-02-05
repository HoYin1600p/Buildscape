package com.kingodogo.buildscape.entity;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITIES, BuildScape.MODID);

    public static final RegistryObject<
            EntityType<FallingIcicleEntity>
            > FALLING_ICICLE = ENTITIES.register("falling_icicle", () ->
            EntityType.Builder.<FallingIcicleEntity>of(
                            FallingIcicleEntity::new,
                            MobCategory.MISC
                    )
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(10)
                    .updateInterval(20)
                    .build("falling_icicle")
    );

    public static final RegistryObject<
            EntityType<FestiveStockingEntity>
            > FESTIVE_STOCKING = ENTITIES.register("festive_stocking", () ->
            EntityType.Builder.<FestiveStockingEntity>of(
                            FestiveStockingEntity::new,
                            MobCategory.MISC
                    )
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(20)
                    .build("festive_stocking")
    );

    public static final RegistryObject<
            EntityType<MangroveBoatEntity>
            > MANGROVE_BOAT = ENTITIES.register("mangrove_boat", () ->
            EntityType.Builder.<MangroveBoatEntity>of(
                            MangroveBoatEntity::new,
                            MobCategory.MISC
                    )
                    .sized(1.375F, 0.5625F)
                    .clientTrackingRange(10)
                    .updateInterval(3)
                    .build("mangrove_boat")
    );

    public static final RegistryObject<
            EntityType<ColoredItemFrameEntity>
            > COLORED_ITEM_FRAME = ENTITIES.register("colored_item_frame", () ->
            EntityType.Builder.<ColoredItemFrameEntity>of(
                            ColoredItemFrameEntity::new,
                            MobCategory.MISC
                    )
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(20)
                    .build("colored_item_frame")
    );
}
