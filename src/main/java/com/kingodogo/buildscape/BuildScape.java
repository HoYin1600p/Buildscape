package com.kingodogo.buildscape;

import com.kingodogo.buildscape.event.BlockInteractionEvents;
import com.kingodogo.buildscape.event.MobGriefingEvents;
import com.kingodogo.buildscape.event.ModCommandEvents;
import com.kingodogo.buildscape.event.PillarServerEvents;
import com.kingodogo.buildscape.event.PlayerEvents;
import com.kingodogo.buildscape.event.SculkEvents;
import com.kingodogo.buildscape.event.WandererTradeEvents;
import com.kingodogo.buildscape.feature.trophy.Trophies;
import com.kingodogo.buildscape.registry.ModBlockEntities;
import com.kingodogo.buildscape.registry.ModBlocks;
import com.kingodogo.buildscape.registry.ModEntities;
import com.kingodogo.buildscape.registry.ModFluids;
import com.kingodogo.buildscape.registry.ModItems;
import com.kingodogo.buildscape.registry.ModMenuTypes;
import com.kingodogo.buildscape.registry.ModParticles;
import com.kingodogo.buildscape.registry.ModRecipeSerializers;
import com.kingodogo.buildscape.registry.ModSounds;
import com.kingodogo.buildscape.setup.CommonSetup;
import com.kingodogo.buildscape.worldgen.ModBlockStateProviderTypes;
import com.kingodogo.buildscape.worldgen.ModConfiguredFeatures;
import com.kingodogo.buildscape.worldgen.ModFoliagePlacerTypes;
import com.kingodogo.buildscape.worldgen.ModPlacementModifiers;
import com.kingodogo.buildscape.worldgen.ModTreeDecoratorTypes;
import com.kingodogo.buildscape.worldgen.ModTrunkPlacerTypes;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mod(BuildScape.MODID)
public class BuildScape {

    public static final String MODID = "buildscape";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final Map<Item, CauldronInteraction> EXPERIENCE_CAULDRON_INTERACTIONS = CauldronInteraction
            .newInteractionMap();

    private static final ExecutorService ASYNC_POOL = Executors
            .newCachedThreadPool(r -> {
                Thread t = new Thread(r, "BuildScape-Async");
                t.setDaemon(true);
                return t;
            });

    public BuildScape() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(CommonSetup::onCommonSetup);

        ModSounds.SOUND_EVENTS.register(modEventBus);
        Trophies.init();
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModParticles.PARTICLES.register(
                modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(
                modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(
                modEventBus);
        ModMenuTypes.register(modEventBus);

        ModBlockStateProviderTypes.BLOCK_STATE_PROVIDER_TYPES.register(
                modEventBus);
        ModTrunkPlacerTypes.TRUNK_PLACER_TYPES.register(
                modEventBus);
        ModFoliagePlacerTypes.FOLIAGE_PLACER_TYPES.register(
                modEventBus);
        ModTreeDecoratorTypes.TREE_DECORATOR_TYPES.register(
                modEventBus);
        ModConfiguredFeatures.CONFIGURED_FEATURES.register(
                modEventBus);
        ModPlacementModifiers.PLACEMENT_MODIFIERS.register(
                modEventBus);

        MinecraftForge.EVENT_BUS.register(new PillarServerEvents());
        MinecraftForge.EVENT_BUS.register(new ModCommandEvents());
        MinecraftForge.EVENT_BUS.register(new MobGriefingEvents());
        MinecraftForge.EVENT_BUS.register(new WandererTradeEvents());
        MinecraftForge.EVENT_BUS.register(new BlockInteractionEvents());
        MinecraftForge.EVENT_BUS.register(new PlayerEvents());
        MinecraftForge.EVENT_BUS.register(new SculkEvents());
    }

    public static boolean isServerFullyInitialized() {
        return PillarServerEvents.isServerFullyInitialized();
    }

    public static ExecutorService getAsyncPool() {
        return ASYNC_POOL;
    }
}
