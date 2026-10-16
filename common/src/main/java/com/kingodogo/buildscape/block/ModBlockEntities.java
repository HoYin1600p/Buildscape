package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.registry.RegistrySupplier;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Set;
public class ModBlockEntities {
    public static final RegistrySupplier<BlockEntityDefinition> PILLAR_BLOCK_ENTITY = register("pillar_block_entity");
    public static final RegistrySupplier<BlockEntityDefinition> DECORATED_POT_BLOCK_ENTITY = register("decorated_pot_block_entity");
    public static final RegistrySupplier<BlockEntityDefinition> TRAPPED_DECORATED_POT_BLOCK_ENTITY = register("trapped_decorated_pot_block_entity");
    public static final RegistrySupplier<BlockEntityDefinition> ICICLE_CAULDRON_BLOCK_ENTITY = register("icicle_cauldron_block_entity");
    public static final RegistrySupplier<BlockEntityDefinition> FESTIVE_STOCKING_BLOCK_ENTITY = register("festive_stocking_block_entity");
    public static final RegistrySupplier<BlockEntityDefinition> GLOW_LIGHTS_BLOCK_ENTITY = register("glow_lights_block_entity");
    public static final RegistrySupplier<BlockEntityDefinition> SMOKE_VENT_BLOCK_ENTITY = register("smoke_vent_block_entity");
    public static final RegistrySupplier<BlockEntityDefinition> CASCADE_BLOCK_ENTITY = register("cascade_block_entity");
    public static final RegistrySupplier<BlockEntityDefinition> MUFF_BLOCK_ENTITY = register("muff_block_entity");
    public static final RegistrySupplier<BlockEntityDefinition> GLASS_JAR_BLOCK_ENTITY = register("glass_jar_block_entity");
    public static final RegistrySupplier<BlockEntityDefinition> BUILDERS_WORKBENCH_BE = register("builders_workbench");

    public static final BlockEntityType<BuildersWorkbenchBlockEntity> BUILDERS_WORKBENCH_TYPE =
            Services.PLATFORM.createBlockEntityType(BuildersWorkbenchBlockEntity::new, state -> state.getBlock() instanceof BuildersWorkbenchBlock);

    public static final BlockEntityType<ShelfBlockEntity> SHELF_TYPE =
            Services.PLATFORM.createBlockEntityType(ShelfBlockEntity::new, state -> state.getBlock() instanceof ShelfBlock);

    public static final BlockEntityType<GlassJarBlockEntity> GLASS_JAR_TYPE =
            Services.PLATFORM.createBlockEntityType(GlassJarBlockEntity::new, state -> state.getBlock() instanceof GlassJarBlock);

    public static final BlockEntityType<SmokeVentBlockEntity> SMOKE_VENT_TYPE =
            Services.PLATFORM.createBlockEntityType(SmokeVentBlockEntity::new, state -> state.getBlock() instanceof SmokeVentBlock);

    public static final BlockEntityType<MuffBlockEntity> MUFF_TYPE =
            Services.PLATFORM.createBlockEntityType(MuffBlockEntity::new, state -> state.getBlock() instanceof MuffBlock);


    public static final BlockEntityType<DecoratedPotBlockEntity> DECORATED_POT_TYPE =
            Services.PLATFORM.createBlockEntityType(DecoratedPotBlockEntity::new, state -> state.getBlock() instanceof DecoratedPotBlock && !(state.getBlock() instanceof TrappedDecoratedPotBlock));

    public static final BlockEntityType<TrappedDecoratedPotBlockEntity> TRAPPED_DECORATED_POT_TYPE =
            Services.PLATFORM.createBlockEntityType(TrappedDecoratedPotBlockEntity::new, state -> state.getBlock() instanceof TrappedDecoratedPotBlock);

    public static final BlockEntityType<FestiveStockingBlockEntity> FESTIVE_STOCKING_TYPE =
            Services.PLATFORM.createBlockEntityType(FestiveStockingBlockEntity::new, state -> state.getBlock() instanceof FestiveStockingBlock);

    public static final BlockEntityType<CascadeBlockEntity> CASCADE_TYPE =
            Services.PLATFORM.createBlockEntityType(CascadeBlockEntity::new, state -> state.getBlock() instanceof CascadeBlock || state.getBlock() instanceof CascadeBlockNoMist);

    public static final BlockEntityType<IcicleCauldronBlockEntity> ICICLE_CAULDRON_TYPE =
            Services.PLATFORM.createBlockEntityType(IcicleCauldronBlockEntity::new, state -> state.getBlock() instanceof IcicleCauldronBlock);

    public static final BlockEntityType<com.kingodogo.buildscape.trophy.TrophyBlockEntity> TROPHY_TYPE =
            Services.PLATFORM.createBlockEntityType(com.kingodogo.buildscape.trophy.TrophyBlockEntity::new, state -> state.getBlock() instanceof com.kingodogo.buildscape.trophy.TrophyBlock);

    public static final BlockEntityType<PillarBlockEntity> PILLAR_TYPE =
            Services.PLATFORM.createBlockEntityType(PillarBlockEntity::new, state -> state.getBlock() instanceof PillarBlock);

    public static final BlockEntityType<GlowLightsBlockEntity> GLOW_LIGHTS_TYPE =
            Services.PLATFORM.createBlockEntityType(GlowLightsBlockEntity::new, state -> state.getBlock() instanceof GlowLightsBlock && !(state.getBlock() instanceof MulticolorGlowLightsBlock));

    public static final BlockEntityType<HollowLogBlockEntity> HOLLOW_LOG_TYPE =
            Services.PLATFORM.createBlockEntityType(HollowLogBlockEntity::new, state -> state.getBlock() instanceof HollowLogBlock || state.getBlock() instanceof HollowPipeBlock);

    public static final RegistrySupplier<BlockEntityDefinition> SHELF = register("shelf");
    public static final RegistrySupplier<BlockEntityDefinition> TROPHY_BLOCK_ENTITY = register("trophy_block_entity");
    public static final RegistrySupplier<BlockEntityDefinition> HOLLOW_LOG_BLOCK_ENTITY = register("hollow_log");

    public static RegistrySupplier<BlockEntityDefinition> register(String name) {
        return Services.REGISTRY.registerBlockEntity(name, () -> new BlockEntityDefinition(name));
    }

    public static void init() {
        System.out.println("ModBlockEntities initialized: " + Services.REGISTRY.getRegisteredBlockEntities().size() + " block entities registered.");
    }
}
