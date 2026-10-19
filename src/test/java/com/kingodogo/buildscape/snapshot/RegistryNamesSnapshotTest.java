package com.kingodogo.buildscape.snapshot;

import com.kingodogo.buildscape.registry.ModBlockEntities;
import com.kingodogo.buildscape.registry.ModBlocks;
import com.kingodogo.buildscape.registry.ModEntities;
import com.kingodogo.buildscape.registry.ModFluids;
import com.kingodogo.buildscape.registry.ModItems;
import com.kingodogo.buildscape.registry.ModMenuTypes;
import com.kingodogo.buildscape.registry.ModParticles;
import com.kingodogo.buildscape.registry.ModRecipeSerializers;
import com.kingodogo.buildscape.registry.ModSounds;
import com.kingodogo.buildscape.worldgen.ModBlockStateProviderTypes;
import com.kingodogo.buildscape.worldgen.ModConfiguredFeatures;
import com.kingodogo.buildscape.worldgen.ModFoliagePlacerTypes;
import com.kingodogo.buildscape.worldgen.ModPlacementModifiers;
import com.kingodogo.buildscape.worldgen.ModTreeDecoratorTypes;
import com.kingodogo.buildscape.worldgen.ModTrunkPlacerTypes;
import net.minecraftforge.registries.DeferredRegister;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;

/** The registry names of every DeferredRegister, in registration order, must not change. */
public class RegistryNamesSnapshotTest {
    @BeforeClass
    public static void bootstrap() {
        SnapshotSupport.bootstrap();
    }

    private static void check(String name, DeferredRegister<?> register) throws IOException {
        assertFalse(name + " is empty", register.getEntries().isEmpty());
        SnapshotSupport.assertSnapshot("registry-" + name + ".txt", SnapshotSupport.ids(register));
    }

    @Test
    public void blocks() throws IOException {
        check("blocks", ModBlocks.BLOCKS);
    }

    @Test
    public void items() throws IOException {
        check("items", ModItems.ITEMS);
    }

    @Test
    public void blockEntities() throws IOException {
        check("block_entities", ModBlockEntities.BLOCK_ENTITIES);
    }

    @Test
    public void entities() throws IOException {
        check("entities", ModEntities.ENTITIES);
    }

    @Test
    public void menus() throws IOException {
        check("menus", ModMenuTypes.MENUS);
    }

    @Test
    public void particles() throws IOException {
        check("particles", ModParticles.PARTICLES);
    }

    @Test
    public void sounds() throws IOException {
        check("sounds", ModSounds.SOUND_EVENTS);
    }

    @Test
    public void fluids() throws IOException {
        check("fluids", ModFluids.FLUIDS);
    }

    @Test
    public void recipeSerializers() throws IOException {
        check("recipe_serializers", ModRecipeSerializers.RECIPE_SERIALIZERS);
    }

    @Test
    public void configuredFeatures() throws IOException {
        check("configured_features", ModConfiguredFeatures.CONFIGURED_FEATURES);
    }

    @Test
    public void worldgenTypes() throws IOException {
        check("block_state_provider_types", ModBlockStateProviderTypes.BLOCK_STATE_PROVIDER_TYPES);
        check("foliage_placer_types", ModFoliagePlacerTypes.FOLIAGE_PLACER_TYPES);
        check("placement_modifiers", ModPlacementModifiers.PLACEMENT_MODIFIERS);
        check("tree_decorator_types", ModTreeDecoratorTypes.TREE_DECORATOR_TYPES);
        check("trunk_placer_types", ModTrunkPlacerTypes.TRUNK_PLACER_TYPES);
    }
}
