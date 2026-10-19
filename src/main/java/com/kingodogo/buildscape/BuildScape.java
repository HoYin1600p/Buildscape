package com.kingodogo.buildscape;

import com.kingodogo.buildscape.block.CloverBlock;
import com.kingodogo.buildscape.block.CreakingHeartBlock;
import com.kingodogo.buildscape.block.CustomSoundType;
import com.kingodogo.buildscape.block.GlowLightsBlockEntity;
import com.kingodogo.buildscape.block.HangingMossBlock;
import com.kingodogo.buildscape.block.HazeBushBlock;
import com.kingodogo.buildscape.block.LogStrippingHandler;
import com.kingodogo.buildscape.block.ModBlockProperties;
import com.kingodogo.buildscape.block.ModWoodTypes;
import com.kingodogo.buildscape.block.OrnamentBlock;
import com.kingodogo.buildscape.block.PetalBlock;
import com.kingodogo.buildscape.block.PottedHazeBushBlock;
import com.kingodogo.buildscape.block.RoseVinesBlock;
import com.kingodogo.buildscape.block.StringLightBlock;
import com.kingodogo.buildscape.client.BuildscapeRenderLayers;
import com.kingodogo.buildscape.client.ClientEvents;
import com.kingodogo.buildscape.client.DryFoliageColor;
import com.kingodogo.buildscape.client.ModKeyBinds;
import com.kingodogo.buildscape.client.model.TintedLeafHedgeModel;
import com.kingodogo.buildscape.client.model.WanderingHomemakerModel;
import com.kingodogo.buildscape.client.renderer.ColoredItemFrameRenderer;
import com.kingodogo.buildscape.client.renderer.CopperChestRenderer;
import com.kingodogo.buildscape.client.renderer.DecoratedPotBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.FallingIcicleRenderer;
import com.kingodogo.buildscape.client.renderer.FestiveStockingBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.FestiveWanderingHomemakerRenderer;
import com.kingodogo.buildscape.client.renderer.GlassJarBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.HollowLogBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.IcicleCauldronBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.MangroveBoatRenderer;
import com.kingodogo.buildscape.client.renderer.PillarBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.PoplarBoatRenderer;
import com.kingodogo.buildscape.client.renderer.ShelfRenderer;
import com.kingodogo.buildscape.client.renderer.TrappedDecoratedPotBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.WanderingHomemakerRenderer;
import com.kingodogo.buildscape.client.screen.BuildersPouchScreen;
import com.kingodogo.buildscape.client.screen.BuildersWorkbenchScreen;
import com.kingodogo.buildscape.client.tooltip.ShulkerBoxTooltipData;
import com.kingodogo.buildscape.client.workbench.ClientBlockColorCatalog;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.feature.biome.BiomeBrushItem;
import com.kingodogo.buildscape.feature.copper.CopperOxidationHandler;
import com.kingodogo.buildscape.feature.copper.CopperRodHandler;
import com.kingodogo.buildscape.feature.cosmetic.CosmeticManager;
import com.kingodogo.buildscape.feature.firework.FireworkTestCommand;
import com.kingodogo.buildscape.feature.geyser.GeyserBaseParticle;
import com.kingodogo.buildscape.feature.geyser.GeyserEruptionParticle;
import com.kingodogo.buildscape.feature.geyser.GeyserPlumeParticle;
import com.kingodogo.buildscape.feature.geyser.SulfurBubbleParticle;
import com.kingodogo.buildscape.feature.homemaker.SyncHomemakerCooldownPacket;
import com.kingodogo.buildscape.feature.pillar.PillarIdManager;
import com.kingodogo.buildscape.feature.sculk.SculkBreakExperience;
import com.kingodogo.buildscape.feature.sculk.SculkCatalystHandler;
import com.kingodogo.buildscape.feature.trophy.Trophies;
import com.kingodogo.buildscape.item.HazeBushItem;
import com.kingodogo.buildscape.network.ModMessages;
import com.kingodogo.buildscape.network.SyncConfigPacket;
import com.kingodogo.buildscape.network.SyncGameRulesPacket;
import com.kingodogo.buildscape.network.SyncPillarIdsPacket;
import com.kingodogo.buildscape.particle.BubbleParticle;
import com.kingodogo.buildscape.particle.CakeParticle;
import com.kingodogo.buildscape.particle.CascadeParticle;
import com.kingodogo.buildscape.particle.CherryParticle;
import com.kingodogo.buildscape.particle.ColoredSmokeParticle;
import com.kingodogo.buildscape.particle.ConfettiParticle;
import com.kingodogo.buildscape.particle.FireflyParticle;
import com.kingodogo.buildscape.particle.HazeParticle;
import com.kingodogo.buildscape.particle.NoxiousGasParticle;
import com.kingodogo.buildscape.particle.PillarSparkleParticle;
import com.kingodogo.buildscape.particle.SnowflakeParticle;
import com.kingodogo.buildscape.particle.SnowflakeStillParticle;
import com.kingodogo.buildscape.particle.TintableHeartParticle;
import com.kingodogo.buildscape.particle.TintedDripParticle;
import com.kingodogo.buildscape.particle.TintedSporeParticle;
import com.kingodogo.buildscape.particle.TrailNoteParticle;
import com.kingodogo.buildscape.particle.XpParticle;
import com.kingodogo.buildscape.registry.ModBlockEntities;
import com.kingodogo.buildscape.registry.ModBlocks;
import com.kingodogo.buildscape.registry.ModEntities;
import com.kingodogo.buildscape.registry.ModFluids;
import com.kingodogo.buildscape.registry.ModItems;
import com.kingodogo.buildscape.registry.ModMenuTypes;
import com.kingodogo.buildscape.registry.ModParticles;
import com.kingodogo.buildscape.registry.ModRecipeSerializers;
import com.kingodogo.buildscape.registry.ModSounds;
import com.kingodogo.buildscape.registry.ModStats;
import com.kingodogo.buildscape.util.GoldenDandelionGrowth;
import com.kingodogo.buildscape.world.ModGameRules;
import com.kingodogo.buildscape.worldgen.ModBlockStateProviderTypes;
import com.kingodogo.buildscape.worldgen.ModConfiguredFeatures;
import com.kingodogo.buildscape.worldgen.ModFoliagePlacerTypes;
import com.kingodogo.buildscape.worldgen.ModPlacementModifiers;
import com.kingodogo.buildscape.worldgen.ModTreeDecoratorTypes;
import com.kingodogo.buildscape.worldgen.ModTrunkPlacerTypes;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Registry;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Mod(BuildScape.MODID)
public class BuildScape {

    public static final String MODID = "buildscape";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final Map<Item, CauldronInteraction> EXPERIENCE_CAULDRON_INTERACTIONS = CauldronInteraction.newInteractionMap();

    public static Logger getLogger() {
        return LOGGER;
    }


    private static final ExecutorService ASYNC_POOL = Executors
            .newCachedThreadPool(r -> {
                Thread t = new Thread(r, "BuildScape-Async");
                t.setDaemon(true);
                return t;
            });
    private static final int PILLAR_SAVE_INTERVAL = 600;
    private static final int PILLAR_BACKUP_INTERVAL = 6000;
    private static final int RECOVERY_DELAY_TICKS = 600;
    private static final AtomicBoolean CONFIG_CALLBACK_REGISTERED = new AtomicBoolean();
    private static boolean serverFullyInitialized = false;
    private static int pillarSaveTickCounter = 0;
    private static int pillarBackupTickCounter = 0;
    private static int recoveryDelayTicks = 0;
    private static boolean recoveryAttempted = false;

    public BuildScape() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

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

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static boolean isServerFullyInitialized() {
        return serverFullyInitialized;
    }

    public static ExecutorService getAsyncPool() {
        return ASYNC_POOL;
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

        event.enqueueWork(() -> {
            ModGameRules.register();
            ModStats.registerStats();
            ModMessages.register();
            if (CONFIG_CALLBACK_REGISTERED.compareAndSet(false, true)) {
                PillarParticleConfig.addConfigReloadCallback(
                        BuildScape::onPillarConfigReload);
            }
            // Potion and cake stack sizes are decided per world by the isWaterbottleStack / isCakeStack
            // game rules through ItemMixin (ItemStack.getMaxStackSize). The Item field stays vanilla (1) so
            // /give and other Item-level callers add single items that merge under those rules.

            try {
                Map<SoundEvent, RecordItem> recordMap =
                        ObfuscationReflectionHelper.getPrivateValue(
                                RecordItem.class, null, "f_43032_");
                if (recordMap != null) {
                    recordMap.put(ModSounds.MUSIC_DISC_CELEBRATION.get(),
                            (RecordItem) ModItems.MUSIC_DISC_CELEBRATION.get());
                    recordMap.put(ModSounds.MUSIC_DISC_SNOWFALL.get(),
                            (RecordItem) ModItems.MUSIC_DISC_SNOWFALL.get());
                    recordMap.put(ModSounds.MUSIC_DISC_BUILDER.get(),
                            (RecordItem) ModItems.MUSIC_DISC_BUILDER.get());
                }
            } catch (Exception e) {
                LOGGER.error("Failed to register custom records into RecordItem.BY_NAME", e);
            }
        });

        event.enqueueWork(() -> {
            for (RegistryObject<Item> entry : ModItems.ITEMS.getEntries()) {
                String path = entry.getId().getPath();
                if (path.endsWith("_vertical_slab")) {
                    String base = path.substring(0, path.length() - "_vertical_slab".length());
                    ResourceLocation vanillaBlockId = new ResourceLocation("minecraft", base);
                    ResourceLocation vanillaSlabId = new ResourceLocation("minecraft", base + "_slab");
                    boolean isVanilla = ForgeRegistries.ITEMS.containsKey(vanillaBlockId)
                            || ForgeRegistries.ITEMS.containsKey(vanillaSlabId);

                    if (isVanilla) {
                        CreativeModeTab targetTab = CreativeModeTab.TAB_BUILDING_BLOCKS;
                        if (base.endsWith("_wool") || base.equals("crying_obsidian") || base.equals("tinted_glass")) {
                            targetTab = CreativeModeTab.TAB_DECORATIONS;
                        }
                        try {
                            ObfuscationReflectionHelper.setPrivateValue(
                                    Item.class, entry.get(), targetTab, "f_41377_");
                        } catch (Exception e) {
                            LOGGER.error("Failed to change creative tab for vertical slab: {}", path, e);
                        }
                    }
                }
            }
        });

        event.enqueueWork(() -> {
            DispenserBlock.registerBehavior(
                    ModItems.BOTTLE_OF_MIST.get(),
                    new AbstractProjectileDispenseBehavior() {
                        @Override
                        protected Projectile getProjectile(Level level, Position pos, ItemStack stack) {
                            return null;
                        }

                        @Override
                        public ItemStack execute(BlockSource source, ItemStack stack) {
                            Level level = source.getLevel();
                            Direction facing = source.getBlockState().getValue(DispenserBlock.FACING);
                            BlockPos pos = source.getPos().relative(facing);

                            double cx = pos.getX() + 0.5 + facing.getStepX() * 0.5;
                            double cy = pos.getY() + 0.5 + facing.getStepY() * 0.5;
                            double cz = pos.getZ() + 0.5 + facing.getStepZ() * 0.5;

                            if (level instanceof ServerLevel serverLevel) {
                                Random rand = new Random();
                                for (int i = 0; i < 40; i++) {
                                    double x = cx + (rand.nextDouble() - 0.5) * 2.0;
                                    double y = cy + (rand.nextDouble() - 0.5);
                                    double z = cz + (rand.nextDouble() - 0.5) * 2.0;

                                    double xSpeed = (rand.nextDouble() - 0.5) * 0.2;
                                    double ySpeed = rand.nextDouble() * 0.05;
                                    double zSpeed = (rand.nextDouble() - 0.5) * 0.2;

                                    serverLevel.sendParticles(
                                            ModParticles.CASCADE.get(),
                                            x, y, z, 1, xSpeed, ySpeed, zSpeed, 0.0);
                                }
                            }

                            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH,
                                    SoundSource.BLOCKS, 0.1f, 0.1f);

                            return stack;
                        }
                    }
            );

            DispenserBlock.registerBehavior(
                    ModItems.CONFETTI_ITEM.get(),
                    new DefaultDispenseItemBehavior() {
                        @Override
                        protected ItemStack execute(BlockSource source, ItemStack stack) {
                            Level level = source.getLevel();
                            Direction facing = source.getBlockState().getValue(DispenserBlock.FACING);
                            BlockPos pos = source.getPos().relative(facing);

                            double startX = pos.getX() + 0.5;
                            double startY = pos.getY() + 0.5;
                            double startZ = pos.getZ() + 0.5;

                            int slotIndex = 4;
                            if (source.getEntity() instanceof DispenserBlockEntity dispenser) {
                                int matchSlot = -1;
                                for (int i = 0; i < dispenser.getContainerSize(); i++) {
                                    ItemStack itemInSlot = dispenser.getItem(i);
                                    if (itemInSlot == stack) {
                                        matchSlot = i;
                                        break;
                                    } else if (matchSlot == -1 && !itemInSlot.isEmpty() && itemInSlot.getItem() == stack.getItem()) {
                                        matchSlot = i;
                                    }
                                }
                                if (matchSlot != -1) {
                                    slotIndex = matchSlot;
                                }
                            }

                            double yawOffset = (slotIndex - 4) * 0.15D;

                            double forwardX = facing.getStepX();
                            double forwardY = facing.getStepY();
                            double forwardZ = facing.getStepZ();

                            double rightX = 0.0, rightY = 0.0, rightZ = 0.0;

                            if (facing.getAxis().isHorizontal()) {
                                Direction rightDir = facing.getClockWise();
                                rightX = rightDir.getStepX();
                                rightY = 0.0;
                                rightZ = rightDir.getStepZ();
                            } else {
                                rightX = 1.0;
                                rightY = 0.0;
                                rightZ = 0.0;
                            }

                            double dirX = forwardX + rightX * yawOffset;
                            double dirY = forwardY;
                            double dirZ = forwardZ + rightZ * yawOffset;

                            double len = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
                            if (len > 0.0001D) {
                                dirX /= len;
                                dirY /= len;
                                dirZ /= len;
                            }

                            if (level instanceof ServerLevel serverLevel) {
                                 Random rand = level.random;
                                 int burstLevel = 1;
                                 if (stack.hasTag() && stack.getTag().contains(NbtKeys.BURST_LEVEL)) {
                                     burstLevel = Math.min(5, Math.max(1, stack.getTag().getInt(NbtKeys.BURST_LEVEL)));
                                 }
                                 int particleCount = (65 + rand.nextInt(36)) * burstLevel;
                                 double speedMultiplier = 1.0 + (burstLevel - 1) * 0.2D;
                                 double spreadMultiplier = 1.0 + (burstLevel - 1) * 0.1D;

                                 for (int i = 0; i < particleCount; i++) {
                                     double speed = (0.08D + rand.nextDouble() * 0.12D) * speedMultiplier;
                                     double spread = (0.04D + rand.nextDouble() * 0.06D) * spreadMultiplier;

                                     double vx = dirX * speed + (rand.nextDouble() - 0.5D) * spread;
                                     double vy = dirY * speed + (rand.nextDouble() - 0.5D) * spread + (facing.getAxis().isHorizontal() ? 0.05D : 0.0D);
                                     double vz = dirZ * speed + (rand.nextDouble() - 0.5D) * spread;

                                     double px = startX + (rand.nextDouble() - 0.5D) * 0.15D;
                                     double py = startY + (rand.nextDouble() - 0.5D) * 0.15D;
                                     double pz = startZ + (rand.nextDouble() - 0.5D) * 0.15D;

                                     serverLevel.sendParticles(
                                             (SimpleParticleType) ModParticles.CONFETTI.get(),
                                             px, py, pz, 0, vx, vy, vz, 1.0D
                                     );
                                 }

                                level.playSound(null, pos,
                                        SoundEvents.FIREWORK_ROCKET_BLAST,
                                        SoundSource.BLOCKS, 0.8F, 1.4F);
                                level.playSound(null, pos,
                                        SoundEvents.FIREWORK_ROCKET_TWINKLE,
                                        SoundSource.BLOCKS, 0.6F, 1.6F);
                            }

                            stack.shrink(1);
                            return stack;
                        }
                    }
            );
        });

        event.enqueueWork(() -> {
            CopperRodHandler.registerPoi();
            ModSounds.COPPER_GRATE_SOUNDS();
            ModSounds.COPPER_BULB_SOUNDS();
            ModSounds.MANGROVE_ROOTS_SOUNDS();
            ModSounds.MUDDY_MANGROVE_ROOTS_SOUNDS();

            PillarParticleConfig.get();

            ModWoodTypes.MANGROVE.getClass();
            ModWoodTypes.BAMBOO.getClass();

            FlowerPotBlock vanillaPot = (FlowerPotBlock) Blocks.FLOWER_POT;
            vanillaPot.addPlant(ModBlocks.CLOSED_EYEBLOSSOM.getId(), ModBlocks.POTTED_CLOSED_EYEBLOSSOM);
            vanillaPot.addPlant(ModBlocks.OPEN_EYEBLOSSOM.getId(), ModBlocks.POTTED_OPEN_EYEBLOSSOM);
            vanillaPot.addPlant(ModBlocks.GOLDEN_DANDELION.getId(), ModBlocks.POTTED_GOLDEN_DANDELION);
            vanillaPot.addPlant(ModBlocks.CACTUS_FLOWER.getId(), ModBlocks.POTTED_CACTUS_FLOWER);
            vanillaPot.addPlant(ModBlocks.BUSH.getId(), ModBlocks.POTTED_BUSH);
            vanillaPot.addPlant(ModBlocks.RED_BUSH.getId(), ModBlocks.POTTED_RED_BUSH);
            ModBlocks.COLORED_HAZE_BUSHES.forEach((color, bush) -> {
                vanillaPot.addPlant(bush.getId(), ModBlocks.POTTED_COLORED_HAZE_BUSHES.get(color));
            });
            vanillaPot.addPlant(ModBlocks.FIREFLY_BUSH.getId(), ModBlocks.POTTED_FIREFLY_BUSH);
            vanillaPot.addPlant(ModBlocks.DRY_GRASS.getId(), ModBlocks.POTTED_DRY_GRASS);
            vanillaPot.addPlant(ModBlocks.FROST_ROSE.getId(), ModBlocks.POTTED_FROST_ROSE);
            vanillaPot.addPlant(ModBlocks.RED_MONETS.getId(), ModBlocks.POTTED_RED_MONETS);
            vanillaPot.addPlant(ModBlocks.BLUE_MONETS.getId(), ModBlocks.POTTED_BLUE_MONETS);
            vanillaPot.addPlant(ModBlocks.PURPLE_MONETS.getId(), ModBlocks.POTTED_PURPLE_MONETS);
            vanillaPot.addPlant(ModBlocks.LIGHT_BLUE_MONETS.getId(), ModBlocks.POTTED_LIGHT_BLUE_MONETS);
            vanillaPot.addPlant(ModBlocks.PINK_MONETS.getId(), ModBlocks.POTTED_PINK_MONETS);
            vanillaPot.addPlant(ModBlocks.YELLOW_MONETS.getId(), ModBlocks.POTTED_YELLOW_MONETS);
            vanillaPot.addPlant(ModBlocks.POPLAR_SAPLING.getId(), ModBlocks.POTTED_POPLAR_SAPLING);
            vanillaPot.addPlant(ModBlocks.PALE_OAK_SAPLING.getId(), ModBlocks.POTTED_PALE_OAK_SAPLING);
            vanillaPot.addPlant(ModBlocks.CHERRY_SAPLING.getId(), ModBlocks.POTTED_CHERRY_SAPLING);
            vanillaPot.addPlant(ModBlocks.MANGROVE_PROPAGULE.getId(), ModBlocks.POTTED_MANGROVE_PROPAGULE);
            vanillaPot.addPlant(ModBlocks.WILDFLOWERS.getId(), ModBlocks.POTTED_WILDFLOWERS);
            vanillaPot.addPlant(ModBlocks.CLOVER.getId(), ModBlocks.POTTED_CLOVER);
            vanillaPot.addPlant(ModBlocks.LEAF_LITTER.getId(), ModBlocks.POTTED_LEAF_LITTER);
            vanillaPot.addPlant(ModBlocks.TALL_DRY_GRASS.getId(), ModBlocks.POTTED_TALL_DRY_GRASS);
            vanillaPot.addPlant(ModBlocks.SNOWY_BUSH.getId(), ModBlocks.POTTED_SNOWY_BUSH);
            vanillaPot.addPlant(ModBlocks.SNOWY_SHORT_GRASS.getId(), ModBlocks.POTTED_SNOWY_SHORT_GRASS);
            vanillaPot.addPlant(ModBlocks.SNOWY_TALL_GRASS.getId(), ModBlocks.POTTED_SNOWY_TALL_GRASS);
            vanillaPot.addPlant(ModBlocks.SNOWY_FERN.getId(), ModBlocks.POTTED_SNOWY_FERN);
            vanillaPot.addPlant(ModBlocks.SNOWY_LARGE_FERN.getId(), ModBlocks.POTTED_SNOWY_LARGE_FERN);


            ComposterBlock.COMPOSTABLES.put(ModItems.RED_ROSE_VINES.get(),
                    0.5f);
            ComposterBlock.COMPOSTABLES.put(ModItems.BLACK_ROSE_VINES.get(),
                    0.5f);
            ComposterBlock.COMPOSTABLES.put(ModItems.BLUE_ROSE_VINES.get(),
                    0.5f);
            ComposterBlock.COMPOSTABLES.put(ModItems.WHITE_ROSE_VINES.get(),
                    0.5f);

            ComposterBlock.COMPOSTABLES.put(ModItems.FROST_ROSE.get(),
                    0.65f);

            ComposterBlock.COMPOSTABLES.put(ModItems.RED_MONETS.get(),
                    0.65f);
            ComposterBlock.COMPOSTABLES.put(ModItems.BLUE_MONETS.get(),
                    0.65f);
            ComposterBlock.COMPOSTABLES.put(ModItems.PURPLE_MONETS.get(),
                    0.65f);
            ComposterBlock.COMPOSTABLES
                    .put(ModItems.LIGHT_BLUE_MONETS.get(), 0.65f);
            ComposterBlock.COMPOSTABLES.put(ModItems.PINK_MONETS.get(),
                    0.65f);
            ComposterBlock.COMPOSTABLES.put(ModItems.YELLOW_MONETS.get(),
                    0.65f);

            ComposterBlock.COMPOSTABLES.put(ModItems.RED_PETAL.get(),
                    0.65f);
            ComposterBlock.COMPOSTABLES.put(ModItems.BLUE_PETAL.get(),
                    0.65f);
            ComposterBlock.COMPOSTABLES.put(ModItems.ORANGE_PETAL.get(),
                    0.65f);
            ComposterBlock.COMPOSTABLES.put(ModItems.PINK_PETAL.get(),
                    0.65f);
            ComposterBlock.COMPOSTABLES.put(ModItems.PURPLE_PETAL.get(),
                    0.65f);

            ComposterBlock.COMPOSTABLES
                    .put(ModItems.RED_SPORE_BLOSSOM.get(), 0.65f);
            ComposterBlock.COMPOSTABLES
                    .put(ModItems.CYAN_SPORE_BLOSSOM.get(), 0.65f);
            ComposterBlock.COMPOSTABLES
                    .put(ModItems.BLUE_SPORE_BLOSSOM.get(), 0.65f);
            ComposterBlock.COMPOSTABLES
                    .put(ModItems.PURPLE_SPORE_BLOSSOM.get(), 0.65f);
            ComposterBlock.COMPOSTABLES
                    .put(ModItems.ORANGE_SPORE_BLOSSOM.get(), 0.65f);

            ComposterBlock.COMPOSTABLES
                    .put(ModItems.SNOWY_SHORT_GRASS.get(), 0.3f);
            ComposterBlock.COMPOSTABLES.put(ModItems.SNOWY_TALL_GRASS.get(),
                    0.3f);
            ComposterBlock.COMPOSTABLES.put(ModItems.SNOWY_FERN.get(),
                    0.3f);
            ComposterBlock.COMPOSTABLES.put(ModItems.SNOWY_LARGE_FERN.get(),
                    0.3f);
            ComposterBlock.COMPOSTABLES.put(ModItems.SNOWY_BUSH.get(),
                    0.3f);

            ComposterBlock.COMPOSTABLES.put(ModItems.MANGROVE_LEAVES.get(),
                    0.3f);

            ComposterBlock.COMPOSTABLES.put(ModItems.SNOWY_LEAVES.get(),
                    0.3f);
            ComposterBlock.COMPOSTABLES.put(ModItems.SNOWY_OAK_LEAVES.get(),
                    0.3f);
            ComposterBlock.COMPOSTABLES
                    .put(ModItems.SNOWY_SPRUCE_LEAVES.get(), 0.3f);
            ComposterBlock.COMPOSTABLES
                    .put(ModItems.SNOWY_BIRCH_LEAVES.get(), 0.3f);
            ComposterBlock.COMPOSTABLES
                    .put(ModItems.SNOWY_JUNGLE_LEAVES.get(), 0.3f);
            ComposterBlock.COMPOSTABLES
                    .put(ModItems.SNOWY_ACACIA_LEAVES.get(), 0.3f);
            ComposterBlock.COMPOSTABLES
                    .put(ModItems.SNOWY_DARK_OAK_LEAVES.get(), 0.3f);
            ComposterBlock.COMPOSTABLES
                    .put(ModItems.SNOWY_MANGROVE_LEAVES.get(), 0.3f);
            ComposterBlock.COMPOSTABLES
                    .put(ModItems.SNOWY_AZALEA_LEAVES.get(), 0.3f);
            ComposterBlock.COMPOSTABLES
                    .put(ModItems.SNOWY_FLOWERING_AZALEA_LEAVES.get(), 0.3f);

            ComposterBlock.COMPOSTABLES
                    .put(ModItems.BROWN_MUSHROOM_SHELVES.get(), 0.65f);
            ComposterBlock.COMPOSTABLES
                    .put(ModItems.RED_MUSHROOM_SHELVES.get(), 0.65f);

            CauldronInteraction.EMPTY.put(ModItems.EXPERIENCE_BUCKET.get(), (state, level, pos, player, hand, stack) -> {
                if (!level.isClientSide) {
                    player.awardStat(Stats.FILL_CAULDRON);
                    level.setBlockAndUpdate(pos, ModBlocks.EXPERIENCE_CAULDRON.get().defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3));
                    level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild) {
                        player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            });

            CauldronInteraction.EMPTY.put(Items.EXPERIENCE_BOTTLE, (state, level, pos, player, hand, stack) -> {
                if (!level.isClientSide) {
                    player.awardStat(Stats.FILL_CAULDRON);
                    level.setBlockAndUpdate(pos, ModBlocks.EXPERIENCE_CAULDRON.get().defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 1));
                    level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                        ItemStack returnStack = new ItemStack(Items.GLASS_BOTTLE);
                        if (!player.getInventory().add(returnStack)) {
                            player.drop(returnStack, false);
                        }
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            });

            EXPERIENCE_CAULDRON_INTERACTIONS.put(Items.BUCKET, (state, level, pos, player, hand, stack) -> {
                int cauldronLevel = state.getValue(LayeredCauldronBlock.LEVEL);
                if (cauldronLevel == 3) {
                    if (!level.isClientSide) {
                        player.awardStat(Stats.USE_CAULDRON);
                        level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
                        level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                        ItemStack filledBucket = new ItemStack(ModItems.EXPERIENCE_BUCKET.get());
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                            if (stack.isEmpty()) {
                                player.setItemInHand(hand, filledBucket);
                            } else {
                                if (!player.getInventory().add(filledBucket)) {
                                    player.drop(filledBucket, false);
                                }
                            }
                        }
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
                return InteractionResult.PASS;
            });

            EXPERIENCE_CAULDRON_INTERACTIONS.put(Items.GLASS_BOTTLE, (state, level, pos, player, hand, stack) -> {
                if (!level.isClientSide) {
                    player.awardStat(Stats.USE_CAULDRON);
                    int cauldronLevel = state.getValue(LayeredCauldronBlock.LEVEL);
                    if (cauldronLevel > 1) {
                        level.setBlockAndUpdate(pos, state.setValue(LayeredCauldronBlock.LEVEL, cauldronLevel - 1));
                    } else {
                        level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
                    }
                    level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                    ItemStack filledBottle = new ItemStack(Items.EXPERIENCE_BOTTLE);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                        if (stack.isEmpty()) {
                            player.setItemInHand(hand, filledBottle);
                        } else {
                            if (!player.getInventory().add(filledBottle)) {
                                player.drop(filledBottle, false);
                            }
                        }
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            });

            EXPERIENCE_CAULDRON_INTERACTIONS.put(Items.EXPERIENCE_BOTTLE, (state, level, pos, player, hand, stack) -> {
                int cauldronLevel = state.getValue(LayeredCauldronBlock.LEVEL);
                if (cauldronLevel < 3) {
                    if (!level.isClientSide) {
                        player.awardStat(Stats.FILL_CAULDRON);
                        level.setBlockAndUpdate(pos, state.setValue(LayeredCauldronBlock.LEVEL, cauldronLevel + 1));
                        level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                            ItemStack returnStack = new ItemStack(Items.GLASS_BOTTLE);
                            if (!player.getInventory().add(returnStack)) {
                                player.drop(returnStack, false);
                            }
                        }
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
                return InteractionResult.PASS;
            });

            EXPERIENCE_CAULDRON_INTERACTIONS.put(ModItems.EXPERIENCE_BUCKET.get(), (state, level, pos, player, hand, stack) -> {
                int cauldronLevel = state.getValue(LayeredCauldronBlock.LEVEL);
                if (cauldronLevel < 3) {
                    if (!level.isClientSide) {
                        player.awardStat(Stats.FILL_CAULDRON);
                        level.setBlockAndUpdate(pos, state.setValue(LayeredCauldronBlock.LEVEL, 3));
                        level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                        if (!player.getAbilities().instabuild) {
                            player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                        }
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
                return InteractionResult.PASS;
            });
        });
    }

    private static void onPillarConfigReload(boolean isRemote) {
        if (isRemote) {
            return;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null || !server.isRunning()) {
            return;
        }
        server.execute(() -> ModMessages.INSTANCE.send(
                PacketDistributor.ALL.noArg(),
                new SyncConfigPacket(
                        PillarParticleConfig.getServerConfig())));
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

        serverFullyInitialized = false;
        pillarSaveTickCounter = 0;
        pillarBackupTickCounter = 0;
        recoveryDelayTicks = 0;
        recoveryAttempted = false;

        PillarIdManager.resetWorldCache();
    }


    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        FireworkTestCommand.register(event.getDispatcher());
        LiteralArgumentBuilder<CommandSourceStack> buildscapeCommand = LiteralArgumentBuilder.<CommandSourceStack>literal(
                        "buildscape")
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal(
                                "recover")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal(
                                        "PillarData")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> {
                                    CommandSourceStack source = context
                                            .getSource();
                                    MinecraftServer server = source
                                            .getServer();

                                    if (server == null || !server.isRunning()) {
                                        source.sendFailure(
                                                new TextComponent(
                                                        "Server is not running"));
                                        return 0;
                                    }

                                    PillarIdManager manager = PillarIdManager
                                            .get();

                                    if (manager == null) {
                                        source.sendFailure(
                                                new TextComponent(
                                                        "PillarIdManager is not available"));
                                        return 0;
                                    }

                                    source.sendSuccess(
                                            new TextComponent(
                                                    "Starting pillar recovery..."),
                                            true);

                                    server.execute(() -> {
                                        try {
                                            manager.recoverPillarsFromWorld(
                                                    server, false);
                                            source.sendSuccess(
                                                    new TextComponent(
                                                            "Pillar recovery completed. Check console for details."),
                                                    true);
                                        } catch (Exception e) {
                                            source.sendFailure(
                                                    new TextComponent(
                                                            "Error during recovery: "
                                                                    + e.getMessage()));
                                            LOGGER.error("Error during pillar recovery command", e);
                                        }
                                    });

                                    return 1;
                                })))
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("cosmatics")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("UNLOCK")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> {
                                    CosmeticManager.getInstance().setDevUnlockAll(true);
                                    context.getSource().sendSuccess(new TextComponent("All cosmetics unlocked for development!"), true);
                                    return 1;
                                })
                        )
                );

        event.getDispatcher().register(buildscapeCommand);
    }

    @SubscribeEvent
    public void onServerStarted(
            ServerStartedEvent event) {
        PillarIdManager.get().load();
    }

    @SubscribeEvent
    public void onServerStopped(
            ServerStoppedEvent event) {

        try {
            MinecraftServer server = event.getServer();
            if (server != null) {
                PillarIdManager manager = PillarIdManager
                        .get();
                if (manager != null && manager.hasLoaded()) {
                    manager.savePeriodic(true);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error saving pillar data on server stop: {}", e.getMessage());
        }

        serverFullyInitialized = false;
        pillarSaveTickCounter = 0;
        pillarBackupTickCounter = 0;
        recoveryDelayTicks = 0;
        recoveryAttempted = false;

        PillarIdManager.fullReset();
    }

    @SubscribeEvent
    public void onPlayerJoin(
            PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
            PillarIdManager manager = PillarIdManager
                    .get();
            if (!manager.hasLoaded() && !manager.isLoadInProgress()) {
                manager.load();
            }

            PillarParticleConfig serverConfig = PillarParticleConfig
                    .getServerConfig();
            SyncConfigPacket configPacket = new SyncConfigPacket(
                    serverConfig);
            ModMessages.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                    configPacket);

            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null && server.isRunning()) {
                ModMessages.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new SyncGameRulesPacket(
                        serverPlayer.getLevel().getGameRules().getBoolean(ModGameRules.FAST_LEAF_DECAY),
                        serverPlayer.getLevel().getGameRules().getBoolean(ModGameRules.DISABLE_ENDERMAN_GRIEFING),
                        serverPlayer.getLevel().getGameRules().getBoolean(ModGameRules.DISABLE_CREEPER_GRIEFING),
                        serverPlayer.getLevel().getGameRules().getBoolean(ModGameRules.DISABLE_GHAST_GRIEFING),
                        serverPlayer.getLevel().getGameRules().getBoolean(ModGameRules.IS_CAKE_STACK),
                        serverPlayer.getLevel().getGameRules().getBoolean(ModGameRules.IS_WATER_BOTTLE_STACK)
                    )
                );

                schedulePillarIdSync(server, serverPlayer, manager, 0);

                long cooldown = serverPlayer.getPersistentData().contains(NbtKeys.WANDERING_HOMEMAKER_COOLDOWN_REAL_TIME)
                        ? serverPlayer.getPersistentData().getLong(NbtKeys.WANDERING_HOMEMAKER_COOLDOWN_REAL_TIME) : 0L;
                ModMessages.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new SyncHomemakerCooldownPacket(cooldown));
            }

        }
    }

    @SubscribeEvent
    public void onMobGriefing(EntityMobGriefingEvent event) {
        Entity entity = event.getEntity();
        if (entity != null && entity.level != null) {
            if (entity instanceof EnderMan) {
                if (entity.level.getGameRules().getBoolean(ModGameRules.DISABLE_ENDERMAN_GRIEFING)) {
                    event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
                }
            } else if (entity instanceof Creeper) {
                if (entity.level.getGameRules().getBoolean(ModGameRules.DISABLE_CREEPER_GRIEFING)) {
                    event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
                }
            } else if (entity instanceof Ghast ||
                       entity instanceof LargeFireball) {
                if (entity.level.getGameRules().getBoolean(ModGameRules.DISABLE_GHAST_GRIEFING)) {
                    event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
                }
            }
        }
    }

    private void schedulePillarIdSync(MinecraftServer server,
                                      ServerPlayer player,
                                      PillarIdManager manager,
                                      int attempt) {
        final int MAX_ATTEMPTS = 10;
        final int RETRY_DELAY_MS = 500;

        if (attempt >= MAX_ATTEMPTS) {
            server.execute(() -> {
                if (player.hasDisconnected()) return;
                sendPillarIdsToPlayer(player, manager);
            });
            return;
        }

        if (manager.hasLoaded()) {
            server.execute(() -> {
                if (player.hasDisconnected()) return;
                sendPillarIdsToPlayer(player, manager);
            });
        } else {
            ASYNC_POOL.submit(() -> {
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ignored) {
                }
                schedulePillarIdSync(server, player, manager, attempt + 1);
            });
        }
    }

    private void sendPillarIdsToPlayer(ServerPlayer player,
                                       PillarIdManager manager) {
        try {
            SyncPillarIdsPacket.sendToPlayer(
                    player, manager.getAllPillarDataForSync());
        } catch (Exception e) {
            LOGGER.error("Error sending pillar IDs to player: {}", e.getMessage());
        }
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(
            PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
            long cooldown = serverPlayer.getPersistentData().contains(NbtKeys.WANDERING_HOMEMAKER_COOLDOWN_REAL_TIME)
                    ? serverPlayer.getPersistentData().getLong(NbtKeys.WANDERING_HOMEMAKER_COOLDOWN_REAL_TIME) : 0L;
            ModMessages.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new SyncHomemakerCooldownPacket(cooldown));
        }
    }

    @SubscribeEvent
    public void onWorldUnload(
            WorldEvent.Unload event) {
        if (event.getWorld() instanceof ServerLevel) {

            try {
                MinecraftServer server = ServerLifecycleHooks
                        .getCurrentServer();
                if (server != null) {
                    PillarIdManager manager = PillarIdManager
                            .get();
                    if (manager != null && manager.hasLoaded()) {
                        manager.savePeriodic(true);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error syncing/saving colors before world unload", e);
            }

        }
    }

    @SubscribeEvent
    public void onServerTick(
            TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        MinecraftServer server = ServerLifecycleHooks
                .getCurrentServer();
        if (server == null || !server.isRunning()) {
            return;
        }

        PillarIdManager manager = PillarIdManager.get();
        if (!serverFullyInitialized) {
            if (manager.hasLoaded()) {
                serverFullyInitialized = true;
            } else {
                if (!manager.isLoadInProgress()) {
                    manager.load();
                }
                return;
            }
        }

        pillarSaveTickCounter++;
        pillarBackupTickCounter++;
        if (pillarBackupTickCounter >= PILLAR_BACKUP_INTERVAL) {
            pillarBackupTickCounter = 0;
            pillarSaveTickCounter = 0;
            manager.savePeriodic(true);
        } else if (pillarSaveTickCounter >= PILLAR_SAVE_INTERVAL) {
            pillarSaveTickCounter = 0;
            manager.savePeriodic(false);
        }
        if (pillarSaveTickCounter % 20 == 0) {
            manager.flushRequestedSave();
        }

        if (server.getPlayerList().getPlayerCount() == 0) {
            return;
        }

        PillarIdManager.checkAndRunScheduledRecovery();

        recoveryDelayTicks++;
        if (recoveryDelayTicks >= RECOVERY_DELAY_TICKS
                && !recoveryAttempted
                && serverFullyInitialized) {
            recoveryAttempted = true;
            if (manager.needsWorldRecovery()) {
                manager.scheduleRecoveryFromWorld(server, false);
            }
        }

    }

    @SubscribeEvent
    public void onWandererTrades(
            WandererTradesEvent event) {
        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                5),
                        new ItemStack(ModItems.MANGROVE_PROPAGULE.get(), 1),
                        8,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(Items.EMERALD, 1),
                        new ItemStack(ModItems.POPLAR_SAPLING.get(), 2),
                        8,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(Items.EMERALD, 1),
                        new ItemStack(ModItems.CHERRY_SAPLING.get(), 2),
                        8,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(Items.EMERALD, 1),
                        new ItemStack(ModItems.PALE_OAK_SAPLING.get(), 2),
                        8,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(ModItems.RED_MONETS.get(), 1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(ModItems.BLUE_MONETS.get(), 1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.PURPLE_MONETS.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.LIGHT_BLUE_MONETS.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(ModItems.PINK_MONETS.get(), 1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.YELLOW_MONETS.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(ModItems.CLOVER.get(), 4),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.RED_ROSE_VINES.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.BLACK_ROSE_VINES.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.BLUE_ROSE_VINES.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.WHITE_ROSE_VINES.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.SNOWY_GRASS_BLOCK.get(),
                                1),
                        2,
                        1,
                        0.05f));
        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.RED_SPORE_BLOSSOM.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.CYAN_SPORE_BLOSSOM.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.BLUE_SPORE_BLOSSOM.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.PURPLE_SPORE_BLOSSOM.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.ORANGE_SPORE_BLOSSOM.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(ModItems.ICICLE.get(), 2),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(ModItems.SULFUR_SPIKE.get(), 2),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(ModItems.SULFUR.get(), 1),
                        8,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(ModItems.CINNABAR.get(), 1),
                        8,
                        1,
                        0.05f));

        event
                .getRareTrades()
                .add((trader, rand) -> {
                    if (rand.nextInt(5000) == 0) {
                        return new MerchantOffer(
                                new ItemStack(Items.DIAMOND, 12),
                                new ItemStack(ModItems.ANCIENT_ASHEN_SCROLL.get(), 1),
                                1,
                                1,
                                0.0f
                        );
                    }
                    return null;
                });
    }

    @SubscribeEvent
    public void onLeftClickBlock(
            PlayerInteractEvent.LeftClickBlock event) {
        BlockState state = event
                .getWorld()
                .getBlockState(event.getPos());
        Block block = state.getBlock();

        if (block instanceof PetalBlock
                || block instanceof CloverBlock
                || block instanceof RoseVinesBlock) {
            if (block.getSoundType(
                    state) instanceof CustomSoundType customSound) {
                BlockPos pos = event.getPos();
                Level level = event.getWorld();

                level.playSound(
                        null,
                        pos,
                        block.getSoundType(state).getHitSound(),
                        SoundSource.BLOCKS,
                        customSound.getHitVolume(),
                        customSound.getHitPitch());
            }
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(
            PlayerInteractEvent.RightClickBlock event) {
        BlockState state = event
                .getWorld()
                .getBlockState(event.getPos());
        ItemStack heldItem = event
                .getPlayer()
                .getItemInHand(event.getHand());

        if (CopperOxidationHandler.handleRightClick(event)) {
            return;
        }

        if (state.is(Blocks.FLOWER_POT) && !event.getPlayer().isSecondaryUseActive()) {
            if (heldItem.getItem() instanceof HazeBushItem && HazeBushItem.isDrained(heldItem)) {
                if (heldItem.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof HazeBushBlock bushBlock) {
                    DyeColor color = bushBlock.getColor();
                    RegistryObject<Block> pottedObj = ModBlocks.POTTED_COLORED_HAZE_BUSHES.get(color);
                    if (pottedObj != null) {
                        if (!event.getWorld().isClientSide) {
                            BlockState newState = pottedObj.get().defaultBlockState().setValue(PottedHazeBushBlock.HAS_HAZE, false);
                            event.getWorld().setBlock(event.getPos(), newState, 3);
                            event.getPlayer().awardStat(Stats.POT_FLOWER);
                            if (!event.getPlayer().getAbilities().instabuild) {
                                heldItem.shrink(1);
                            }
                            event.getWorld().gameEvent(event.getPlayer(), GameEvent.BLOCK_CHANGE, event.getPos());
                        }
                        event.setCancellationResult(InteractionResult.sidedSuccess(event.getWorld().isClientSide));
                        event.setCanceled(true);
                        return;
                    }
                }
            }
        }

        if (state.getBlock() instanceof VineBlock) {
            if (heldItem.is(Items.SHEARS)) {
                if (state.hasProperty(ModBlockProperties.SHEARED)) {
                    if (!state.getValue(ModBlockProperties.SHEARED)) {
                        if (!event.getWorld().isClientSide) {
                            event.getWorld().setBlockAndUpdate(event.getPos(), state.setValue(ModBlockProperties.SHEARED, true));
                            event.getWorld().playSound(null, event.getPos(), SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0F, 1.0F);
                            event.getWorld().gameEvent(event.getPlayer(), GameEvent.SHEAR, event.getPos());
                            heldItem.hurtAndBreak(1, event.getPlayer(), (p) -> p.broadcastBreakEvent(event.getHand()));
                        }
                        event.setCancellationResult(InteractionResult.sidedSuccess(event.getWorld().isClientSide));
                        event.setCanceled(true);
                        return;
                    }
                }
            }
        }


        if (state.getBlock() == ModBlocks.BAMBOO_BLOCK.get()
                && heldItem.getItem() instanceof AxeItem) {
            BlockPos pos = event.getPos();
            Level level = event.getWorld();
            Player player = event.getPlayer();

            Direction.Axis axis = state.getValue(
                    RotatedPillarBlock.AXIS);

            level.setBlock(
                    pos,
                    ModBlocks.STRIPPED_BAMBOO_BLOCK.get()
                            .defaultBlockState()
                            .setValue(
                                    RotatedPillarBlock.AXIS,
                                    axis),
                    11);

            level.playSound(
                    null,
                    pos,
                    SoundEvents.AXE_STRIP,
                    SoundSource.BLOCKS,
                    1.0f,
                    1.0f);

            if (!player.getAbilities().instabuild) {
                heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));
            }

            event.setCanceled(true);
            event.setCancellationResult(
                    InteractionResult.SUCCESS);
            return;
        }

        if (state.getBlock() == ModBlocks.MANGROVE_LOG.get()
                && heldItem.getItem() instanceof AxeItem) {
            BlockPos pos = event.getPos();
            Level level = event.getWorld();
            Player player = event.getPlayer();

            Direction.Axis axis = state.getValue(
                    RotatedPillarBlock.AXIS);

            level.setBlock(
                    pos,
                    ModBlocks.STRIPPED_MANGROVE_LOG.get()
                            .defaultBlockState()
                            .setValue(
                                    RotatedPillarBlock.AXIS,
                                    axis),
                    11);

            level.playSound(
                    null,
                    pos,
                    SoundEvents.AXE_STRIP,
                    SoundSource.BLOCKS,
                    1.0f,
                    1.0f);

            if (!player.getAbilities().instabuild) {
                heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));
            }

            event.setCanceled(true);
            event.setCancellationResult(
                    InteractionResult.SUCCESS);
            return;
        }

        if (state.getBlock() == ModBlocks.MANGROVE_WOOD.get()
                && heldItem.getItem() instanceof AxeItem) {
            BlockPos pos = event.getPos();
            Level level = event.getWorld();
            Player player = event.getPlayer();

            Direction.Axis axis = state.getValue(
                    RotatedPillarBlock.AXIS);

            level.setBlock(
                    pos,
                    ModBlocks.STRIPPED_MANGROVE_WOOD.get()
                            .defaultBlockState()
                            .setValue(
                                    RotatedPillarBlock.AXIS,
                                    axis),
                    11);

            level.playSound(
                    null,
                    pos,
                    SoundEvents.AXE_STRIP,
                    SoundSource.BLOCKS,
                    1.0f,
                    1.0f);

            if (!player.getAbilities().instabuild) {
                heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));
            }

            event.setCanceled(true);
            event.setCancellationResult(
                    InteractionResult.SUCCESS);
            return;
        }

        if (LogStrippingHandler.handleAxeStrip(event)) {
            return;
        }

        if (state.getBlock() == Blocks.DIRT
                && heldItem.getItem() == Items.POTION) {
            if (PotionUtils
                    .getPotion(heldItem) == Potions.WATER) {
                BlockPos pos = event.getPos();
                Level level = event.getWorld();
                Player player = event.getPlayer();

                if (level.isClientSide) {
                    spawnSplashParticles(level, pos);
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                    return;
                }

                level.setBlock(pos, ModBlocks.MUD.get().defaultBlockState(), 3);

                level.playSound(
                        null,
                        pos,
                        SoundEvents.BOTTLE_EMPTY,
                        SoundSource.BLOCKS,
                        1.0f,
                        1.0f);

                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                    ItemStack emptyBottle = new ItemStack(
                            Items.GLASS_BOTTLE);
                    if (heldItem.isEmpty()) {
                        player.setItemInHand(event.getHand(), emptyBottle);
                    } else if (!player.getInventory().add(emptyBottle)) {
                        player.drop(emptyBottle, false);
                    }
                }

                spawnSplashParticles(level, pos);

                event.setCanceled(true);
                event.setCancellationResult(
                        InteractionResult.sidedSuccess(level.isClientSide));
            }
        }
    }


    @SubscribeEvent
    public void onItemCrafted(
            PlayerEvent.ItemCraftedEvent event) {
        ItemStack crafted = event.getCrafting();

        if (crafted.getItem() == Items.SUSPICIOUS_STEW) {
            boolean hasFrostRose = false;

            Container inventory = event.getInventory();
            if (inventory instanceof CraftingContainer container) {

                for (int i = 0; i < container.getContainerSize(); i++) {
                    ItemStack stack = container.getItem(i);
                    if (stack.getItem() == ModItems.FROST_ROSE.get()) {
                        hasFrostRose = true;
                        break;
                    }
                }
            }

            if (hasFrostRose) {
                CompoundTag nbt = crafted.getOrCreateTag();
                nbt.putInt(NbtKeys.FROST_ROSE_STEW, 1);
            }
        }
    }

    @SubscribeEvent
    public void onItemUseFinish(
            LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack itemStack = event.getItem();

        if (itemStack.getItem() == Items.SUSPICIOUS_STEW) {
            CompoundTag nbt = itemStack.getTag();
            if (nbt != null
                    && nbt.contains(NbtKeys.FROST_ROSE_STEW)
                    && nbt.getInt(NbtKeys.FROST_ROSE_STEW) == 1) {
                if (!player.level.isClientSide) {
                    CompoundTag playerData = player.getPersistentData();
                    playerData.putInt(NbtKeys.FROST_ROSE_STEW_DAMAGE_TICKS, 120);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(
            TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Player player = event.player;
        if (player.level.isClientSide) {
            return;
        }

        CompoundTag playerData = player.getPersistentData();
        if (playerData.contains(NbtKeys.FROST_ROSE_STEW_DAMAGE_TICKS)) {
            int ticksRemaining = playerData.getInt(NbtKeys.FROST_ROSE_STEW_DAMAGE_TICKS);

            if (ticksRemaining > 0) {
                if (ticksRemaining % 20 == 0 && player.isAlive()) {
                    DamageSource freezeDamage = DamageSource.GENERIC;
                    player.hurt(freezeDamage, 1.0F);

                    Level level = player.level;
                    BlockPos playerPos = player.blockPosition();
                    level.playSound(
                            null,
                            playerPos,
                            SoundEvents.POWDER_SNOW_STEP,
                            SoundSource.PLAYERS,
                            0.5f,
                            1.0f);

                    if (player.canFreeze()) {
                        player.setTicksFrozen(Math.min(player.getTicksFrozen() + 140, 300));
                    }
                }

                ticksRemaining--;
                if (ticksRemaining > 0) {
                    playerData.putInt(NbtKeys.FROST_ROSE_STEW_DAMAGE_TICKS, ticksRemaining);
                } else {
                    playerData.remove(NbtKeys.FROST_ROSE_STEW_DAMAGE_TICKS);
                }
            }
        }
    }

    private void spawnSplashParticles(
            Level level,
            BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.SPLASH,
                    pos.getX() + 0.5,
                    pos.getY() + 1.0,
                    pos.getZ() + 0.5,
                    8,
                    0.5,
                    0.3,
                    0.5,
                    0.1);
        } else if (level.isClientSide) {
            for (int i = 0; i < 8; ++i) {
                double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.5;
                double y = pos.getY() + 1.0 + level.random.nextDouble() * 0.3;
                double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.5;
                double vx = (level.random.nextDouble() - 0.5) * 0.1;
                double vy = level.random.nextDouble() * 0.1;
                double vz = (level.random.nextDouble() - 0.5) * 0.1;
                level.addParticle(
                        ParticleTypes.SPLASH,
                        x,
                        y,
                        z,
                        vx,
                        vy,
                        vz);
            }
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onRegisterClientReloadListeners(
                RegisterClientReloadListenersEvent event) {
            ResourceManagerReloadListener listener = resourceManager ->
                    ClientBlockColorCatalog.invalidate();
            event.registerReloadListener(listener);
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ModWoodTypes.MANGROVE.getClass();
                ModWoodTypes.BAMBOO.getClass();
            });


            event.enqueueWork(() -> {
                ModKeyBinds.register();
            });

            event.enqueueWork(() -> {
                ClientEvents.initializeConfigCallback();
            });

            event.enqueueWork(() -> {
                MinecraftForgeClient.registerTooltipComponentFactory(
                        ShulkerBoxTooltipData.class,
                        data -> data
                );
            });

            event.enqueueWork(() -> {
                BuildscapeRenderLayers.register();
            });

            event.enqueueWork(() -> {
                MenuScreens.register(
                        ModMenuTypes.BUILDERS_WORKBENCH_MENU.get(),
                        BuildersWorkbenchScreen::new
                );
                MenuScreens.register(
                        ModMenuTypes.BUILDERS_POUCH_MENU.get(),
                        BuildersPouchScreen::new
                );












                BlockEntityRenderers.register(
                        ModBlockEntities.PILLAR_BLOCK_ENTITY
                                .get(),
                        PillarBlockEntityRenderer::new);
                BlockEntityRenderers.register(
                        ModBlockEntities.DECORATED_POT_BLOCK_ENTITY
                                .get(),
                        DecoratedPotBlockEntityRenderer::new);
                BlockEntityRenderers.register(
                        ModBlockEntities.TRAPPED_DECORATED_POT_BLOCK_ENTITY
                                .get(),
                        TrappedDecoratedPotBlockEntityRenderer::new);
                BlockEntityRenderers.register(
                        ModBlockEntities.ICICLE_CAULDRON_BLOCK_ENTITY
                                .get(),
                        IcicleCauldronBlockEntityRenderer::new);

                BlockEntityRenderers.register(
                        ModBlockEntities.FESTIVE_STOCKING_BLOCK_ENTITY
                                .get(),
                        FestiveStockingBlockEntityRenderer::new);
                BlockEntityRenderers.register(
                        ModBlockEntities.GLASS_JAR_BLOCK_ENTITY
                                .get(),
                        GlassJarBlockEntityRenderer::new);
                BlockEntityRenderers.register(
                        ModBlockEntities.COPPER_CHEST
                                .get(),
                        CopperChestRenderer::new);
                BlockEntityRenderers.register(
                        ModBlockEntities.SHELF
                                .get(),
                        ShelfRenderer::new);

                BlockEntityRenderers.register(
                        ModBlockEntities.MANGROVE_SIGN_BLOCK_ENTITY
                                .get(),
                        SignRenderer::new);

                BlockEntityRenderers.register(
                        ModBlockEntities.BAMBOO_SIGN_BLOCK_ENTITY
                                .get(),
                        SignRenderer::new);

                BlockEntityRenderers.register(
                        ModBlockEntities.HOLLOW_LOG_BLOCK_ENTITY
                                .get(),
                        HollowLogBlockEntityRenderer::new);

                EntityRenderers.register(
                        ModEntities.FALLING_ICICLE.get(),
                        FallingIcicleRenderer::new);

                EntityRenderers.register(
                        ModEntities.MANGROVE_BOAT.get(),
                        MangroveBoatRenderer::new);

                EntityRenderers.register(
                        ModEntities.POPLAR_BOAT.get(),
                        PoplarBoatRenderer::new);

                EntityRenderers.register(
                        ModEntities.COLORED_ITEM_FRAME.get(),
                        ColoredItemFrameRenderer::new);

                EntityRenderers.register(
                        ModEntities.SEAT_ENTITY.get(),
                        NoopRenderer::new);

                EntityRenderers.register(
                        ModEntities.FESTIVE_STOCKING.get(),
                        NoopRenderer::new);

                EntityRenderers.register(
                        ModEntities.WANDERING_HOMEMAKER.get(),
                        WanderingHomemakerRenderer::new);
                EntityRenderers.register(
                        ModEntities.FESTIVE_WANDERING_HOMEMAKER.get(),
                        FestiveWanderingHomemakerRenderer::new);






















                BlockColors blockColors = Minecraft
                        .getInstance().getBlockColors();
                blockColors.register(
                        (state, reader, pos, tintIndex) -> {
                            if (reader == null || pos == null) {
                                return 0x7FA832;
                            }
                            if (tintIndex == 1) {
                                if (reader instanceof LevelReader levelReader) {
                                    return levelReader
                                            .getBiome(pos)
                                            .value()
                                            .getGrassColor(pos.getX(),
                                                    pos.getZ());
                                }
                                return 0x7FA832;
                            }
                            return -1;
                        },
                        ModBlocks.CLOVER.get());

                blockColors.register(
                        (state, reader, pos, tintIndex) -> {
                            if (reader == null || pos == null) {
                                return 0x7FA832;
                            }
                            if (tintIndex == 1) {
                                if (reader instanceof LevelReader levelReader) {
                                    return levelReader
                                            .getBiome(pos)
                                            .value()
                                            .getGrassColor(pos.getX(),
                                                    pos.getZ());
                                }
                                return 0x7FA832;
                            }
                            return -1;
                        },
                        ModBlocks.RED_PETAL.get(),
                        ModBlocks.BLUE_PETAL.get(),
                        ModBlocks.ORANGE_PETAL.get(),
                        ModBlocks.PINK_PETAL.get(),
                        ModBlocks.PURPLE_PETAL.get());

                blockColors.register(
                        (state, reader, pos, tintIndex) -> {
                            if (tintIndex != 0) {
                                return -1;
                            }

                            if (state.hasProperty(
                                    OrnamentBlock.STRING_COLOR)) {
                                OrnamentBlock.StringColor stringColor = state
                                        .getValue(
                                                OrnamentBlock.STRING_COLOR);
                                return stringColor.getColor();
                            }

                            return 0xFFFFFF;
                        },
                        ModBlocks.WHITE_ORNAMENT.get(),
                        ModBlocks.ORANGE_ORNAMENT.get(),
                        ModBlocks.MAGENTA_ORNAMENT.get(),
                        ModBlocks.LIGHT_BLUE_ORNAMENT.get(),
                        ModBlocks.YELLOW_ORNAMENT.get(),
                        ModBlocks.LIME_ORNAMENT.get(),
                        ModBlocks.PINK_ORNAMENT.get(),
                        ModBlocks.GRAY_ORNAMENT.get(),
                        ModBlocks.LIGHT_GRAY_ORNAMENT.get(),
                        ModBlocks.CYAN_ORNAMENT.get(),
                        ModBlocks.PURPLE_ORNAMENT.get(),
                        ModBlocks.BLUE_ORNAMENT.get(),
                        ModBlocks.BROWN_ORNAMENT.get(),
                        ModBlocks.GREEN_ORNAMENT.get(),
                        ModBlocks.RED_ORNAMENT.get(),
                        ModBlocks.BLACK_ORNAMENT.get(),
                        ModBlocks.GLASS_ORNAMENT.get(),
                        ModBlocks.TINTED_GLASS_ORNAMENT.get());

                blockColors.register(
                        (state, reader, pos, tintIndex) -> {
                            if (tintIndex != 0) {
                                return -1;
                            }

                            if (state.hasProperty(
                                    StringLightBlock.STRING_COLOR)) {
                                StringLightBlock.StringColor stringColor = state
                                        .getValue(
                                                StringLightBlock.STRING_COLOR);
                                return stringColor.getColor();
                            }

                            return 0xFFFFFF;
                        },
                        ModBlocks.WHITE_STRING_LIGHT.get(),
                        ModBlocks.ORANGE_STRING_LIGHT.get(),
                        ModBlocks.MAGENTA_STRING_LIGHT.get(),
                        ModBlocks.LIGHT_BLUE_STRING_LIGHT.get(),
                        ModBlocks.YELLOW_STRING_LIGHT.get(),
                        ModBlocks.LIME_STRING_LIGHT.get(),
                        ModBlocks.PINK_STRING_LIGHT.get(),
                        ModBlocks.GRAY_STRING_LIGHT.get(),
                        ModBlocks.LIGHT_GRAY_STRING_LIGHT.get(),
                        ModBlocks.CYAN_STRING_LIGHT.get(),
                        ModBlocks.PURPLE_STRING_LIGHT.get(),
                        ModBlocks.BLUE_STRING_LIGHT.get(),
                        ModBlocks.BROWN_STRING_LIGHT.get(),
                        ModBlocks.GREEN_STRING_LIGHT.get(),
                        ModBlocks.RED_STRING_LIGHT.get(),
                        ModBlocks.BLACK_STRING_LIGHT.get(),
                        ModBlocks.MULTICOLOR_STRING_LIGHT.get());

                blockColors.register(
                        (state, reader, pos, tintIndex) -> {
                            if (tintIndex != 0) {
                                return -1;
                            }

                            if (reader != null
                                    && pos != null
                                    && reader instanceof LevelReader levelReader) {
                                return levelReader.getBiome(pos).value()
                                        .getFoliageColor();
                            }

                            return 0x48B518;
                        },
                        ModBlocks.OAK_LEAF_LAYERS.get(),
                        ModBlocks.JUNGLE_LEAF_LAYERS.get(),
                        ModBlocks.ACACIA_LEAF_LAYERS.get(),
                        ModBlocks.DARK_OAK_LEAF_LAYERS.get(),
                        ModBlocks.OAK_LEAF_HEDGE.get(),
                        ModBlocks.JUNGLE_LEAF_HEDGE.get(),
                        ModBlocks.ACACIA_LEAF_HEDGE.get(),
                        ModBlocks.DARK_OAK_LEAF_HEDGE.get());

                blockColors.register(
                        (state, reader, pos, tintIndex) -> {
                            if (tintIndex != 0) return -1;
                            if (reader != null && pos != null) {
                                return BiomeColors.getAverageFoliageColor(reader, pos);
                            }
                            return FoliageColor.getDefaultColor();
                        },
                        ModBlocks.BUSH.get());

                blockColors.register(
                        (state, reader, pos, tintIndex) -> {
                            if (tintIndex != 0) return -1;
                            if (reader != null && pos != null) {
                                return DryFoliageColor.getDryFoliageColor(reader, pos);
                            }
                            return DryFoliageColor.getDefaultColor();
                        },
                        ModBlocks.LEAF_LITTER.get());

                blockColors.register(
                        (state, reader, pos, tintIndex) -> {
                            if (tintIndex == 1 || tintIndex == 0) {
                                if (reader != null && pos != null) {
                                    return BiomeColors.getAverageFoliageColor(reader, pos);
                                }
                                return FoliageColor.getDefaultColor();
                            }
                            return -1;
                        },
                        ModBlocks.WILDFLOWERS.get());

                blockColors.register(
                        (state, reader, pos, tintIndex) -> {
                            if (tintIndex != 0) {
                                return -1;
                            }
                            return 0x619961;
                        },
                        ModBlocks.SPRUCE_LEAF_LAYERS.get(),
                        ModBlocks.SPRUCE_LEAF_HEDGE.get());

                blockColors.register(
                        (state, reader, pos, tintIndex) -> {
                            if (tintIndex != 0) {
                                return -1;
                            }
                            return 0x80a755;
                        },
                        ModBlocks.BIRCH_LEAF_LAYERS.get(),
                        ModBlocks.BIRCH_LEAF_HEDGE.get());

                blockColors.register(
                        (state, reader, pos, tintIndex) -> {
                            return -1;
                        },
                        ModBlocks.AZALEA_LEAF_LAYERS.get(),
                        ModBlocks.FLOWERING_AZALEA_LEAF_LAYERS.get(),
                        ModBlocks.AZALEA_LEAF_HEDGE.get(),
                        ModBlocks.FLOWERING_AZALEA_LEAF_HEDGE.get(),
                        ModBlocks.ORANGE_POPLAR_LEAF_LAYERS.get(),
                        ModBlocks.RED_POPLAR_LEAF_LAYERS.get(),
                        ModBlocks.YELLOW_POPLAR_LEAF_LAYERS.get(),
                        ModBlocks.CHERRY_LEAF_LAYERS.get(),
                        ModBlocks.PALE_OAK_LEAF_LAYERS.get(),
                        ModBlocks.SNOWY_CHERRY_LEAF_LAYERS.get(),
                        ModBlocks.SNOWY_PALE_OAK_LEAF_LAYERS.get(),
                        ModBlocks.ORANGE_POPLAR_LEAF_HEDGE.get(),
                        ModBlocks.RED_POPLAR_LEAF_HEDGE.get(),
                        ModBlocks.YELLOW_POPLAR_LEAF_HEDGE.get(),
                        ModBlocks.CHERRY_LEAF_HEDGE.get(),
                        ModBlocks.PALE_OAK_LEAF_HEDGE.get(),
                        ModBlocks.SNOWY_CHERRY_LEAF_HEDGE.get(),
                        ModBlocks.SNOWY_PALE_OAK_LEAF_HEDGE.get(),
                        ModBlocks.SNOWY_CHERRY_LEAVES.get(),
                        ModBlocks.SNOWY_PALE_OAK_LEAVES.get(),
                        ModBlocks.SNOWY_ORANGE_POPLAR_LEAF_LAYERS.get(),
                        ModBlocks.SNOWY_RED_POPLAR_LEAF_LAYERS.get(),
                        ModBlocks.SNOWY_YELLOW_POPLAR_LEAF_LAYERS.get(),
                        ModBlocks.SNOWY_ORANGE_POPLAR_LEAF_HEDGE.get(),
                        ModBlocks.SNOWY_RED_POPLAR_LEAF_HEDGE.get(),
                        ModBlocks.SNOWY_YELLOW_POPLAR_LEAF_HEDGE.get(),
                        ModBlocks.SNOWY_ORANGE_POPLAR_LEAVES.get(),
                        ModBlocks.SNOWY_RED_POPLAR_LEAVES.get(),
                        ModBlocks.SNOWY_YELLOW_POPLAR_LEAVES.get());

                blockColors.register(
                        (state, reader, pos, tintIndex) -> {
                            return -1;
                        },
                        ModBlocks.SNOWY_OAK_LEAF_LAYERS.get(),
                        ModBlocks.SNOWY_SPRUCE_LEAF_LAYERS.get(),
                        ModBlocks.SNOWY_BIRCH_LEAF_LAYERS.get(),
                        ModBlocks.SNOWY_JUNGLE_LEAF_LAYERS.get(),
                        ModBlocks.SNOWY_ACACIA_LEAF_LAYERS.get(),
                        ModBlocks.SNOWY_DARK_OAK_LEAF_LAYERS.get(),
                        ModBlocks.SNOWY_MANGROVE_LEAF_LAYERS.get(),
                        ModBlocks.SNOWY_AZALEA_LEAF_LAYERS.get(),
                        ModBlocks.SNOWY_FLOWERING_AZALEA_LEAF_LAYERS.get(),
                        ModBlocks.SNOWY_OAK_LEAF_HEDGE.get(),
                        ModBlocks.SNOWY_SPRUCE_LEAF_HEDGE.get(),
                        ModBlocks.SNOWY_BIRCH_LEAF_HEDGE.get(),
                        ModBlocks.SNOWY_JUNGLE_LEAF_HEDGE.get(),
                        ModBlocks.SNOWY_ACACIA_LEAF_HEDGE.get(),
                        ModBlocks.SNOWY_DARK_OAK_LEAF_HEDGE.get(),
                        ModBlocks.SNOWY_MANGROVE_LEAF_HEDGE.get(),
                        ModBlocks.SNOWY_AZALEA_LEAF_HEDGE.get(),
                        ModBlocks.SNOWY_FLOWERING_AZALEA_LEAF_HEDGE.get());

                blockColors.register(
                        (state, reader, pos, tintIndex) -> {
                            if (tintIndex != 0) {
                                return -1;
                            }
                            if (reader != null
                                    && pos != null
                                    && reader instanceof LevelReader levelReader) {
                                return levelReader.getBiome(pos).value()
                                        .getFoliageColor();
                            }
                            return 0x92c648;
                        },
                        ModBlocks.MANGROVE_LEAF_LAYERS.get(),
                        ModBlocks.MANGROVE_LEAF_HEDGE.get());

                blockColors.register(
                        (state, reader, pos, tintIndex) -> {
                            if (tintIndex != 0) {
                                return -1;
                            }
                            if (reader != null
                                    && pos != null
                                    && reader instanceof LevelReader levelReader) {
                                return levelReader.getBiome(pos).value()
                                        .getFoliageColor();
                            }
                            return 0x92c648;
                        },
                        ModBlocks.MANGROVE_LEAVES.get());

                blockColors.register(
                        (state, reader, pos, tintIndex) -> {
                            if (tintIndex != 0) {
                                return -1;
                            }
                            if (reader != null && pos != null) {
                                return BiomeColors.getAverageWaterColor(reader, pos);
                            }
                            return 0x3F76E4;
                        },
                        ModBlocks.CASCADE_BLOCK.get()
                );

                blockColors.register(
                        (state, reader, pos, tintIndex) -> {
                            if (tintIndex != 0) {
                                return -1;
                            }
                            if (reader != null && pos != null) {
                                return BiomeColors.getAverageWaterColor(reader, pos);
                            }
                            return 0x3F76E4;
                        },
                        ModBlocks.CASCADE_BLOCK_NO_MIST.get()
                );

                blockColors.register(
                        (state, reader, pos, tintIndex) -> {
                            if (tintIndex != 0) {
                                return -1;
                            }

                            if (reader != null && pos != null) {
                                BlockEntity be = reader
                                        .getBlockEntity(pos);
                                if (be instanceof GlowLightsBlockEntity glowBE) {
                                    List<String> dyeColors = glowBE
                                            .getDyeColors();
                                    if (dyeColors != null && !dyeColors.isEmpty()) {
                                        int hash = pos.hashCode()
                                                ^ state.hashCode();
                                        int colorIndex = Math.abs(hash)
                                                % dyeColors.size();
                                        String colorCode = dyeColors
                                                .get(colorIndex);

                                        try {
                                            if (colorCode != null
                                                    && colorCode.startsWith(
                                                    "#")
                                                    && colorCode.length() == 7) {
                                                int color = Integer
                                                        .parseInt(colorCode
                                                                        .substring(1),
                                                                16);
                                                return color | 0xFF000000;
                                            }
                                        } catch (NumberFormatException e) {
                                        }
                                    }
                                }
                            }

                            return -1;
                        },
                        ModBlocks.GLOW_LIGHTS.get());

                blockColors.register(
                        (state, reader, pos, tintIndex) -> {
                            if (tintIndex == 0) {
                                if (reader != null && pos != null) {
                                    return BiomeColors.getAverageGrassColor(reader, pos);
                                }
                                return GrassColor.get(0.5D, 1.0D);
                            }
                            return -1;
                        },
                        Blocks.COMPOSTER);

                ItemColors itemColors = Minecraft
                        .getInstance().getItemColors();
                ItemColors vanillaItemColors = Minecraft
                        .getInstance().getItemColors();

                itemColors.register(
                        (stack, tintIndex) -> tintIndex == 0 ? 0x3F76E4 : -1,
                        ModItems.CASCADE_BLOCK.get()
                );

                itemColors.register(
                        (stack, tintIndex) -> {
                            if (tintIndex != 1) return -1;
                            CompoundTag tag = stack.getTagElement(NbtKeys.EXPLOSION);
                            int[] colors = tag != null ? tag.getIntArray(NbtKeys.COLORS) : null;
                            if (colors != null && colors.length > 0) {
                                if (colors.length == 1) return colors[0];
                                int r = 0, g = 0, b = 0;
                                for (int c : colors) {
                                    r += (c >> 16) & 0xFF;
                                    g += (c >> 8) & 0xFF;
                                    b += c & 0xFF;
                                }
                                return ((r / colors.length) << 16) | ((g / colors.length) << 8) | (b / colors.length);
                            }
                            return 0xFFB000;
                        },
                        ModItems.INFINITE_PHOENIX_FIREWORK_STAR.get()
                );

                itemColors.register(
                        (stack, tintIndex) -> tintIndex == 0 ? 0x3F76E4 : -1,
                        ModItems.CASCADE_BLOCK_NO_MIST.get()
                );

                itemColors.register(
                        (stack, tintIndex) -> {
                            if (tintIndex != 0) {
                                return -1;
                            }
                            Item item = stack.getItem();
                            if (item instanceof BlockItem) {
                                Block block = ((BlockItem) item)
                                        .getBlock();

                                int color = -1;
                                if (block == ModBlocks.LEAF_LITTER.get()) {
                                    color = DryFoliageColor.getDefaultColor();
                                } else if (block == ModBlocks.BUSH.get()
                                        || block == ModBlocks.OAK_LEAF_HEDGE.get()
                                        || block == ModBlocks.OAK_LEAF_LAYERS.get()) {
                                    color = vanillaItemColors.getColor(
                                            new ItemStack(
                                                    Items.OAK_LEAVES),
                                            0);
                                } else if (block == ModBlocks.JUNGLE_LEAF_HEDGE.get()
                                        || block == ModBlocks.JUNGLE_LEAF_LAYERS
                                        .get()) {
                                    color = vanillaItemColors.getColor(
                                            new ItemStack(
                                                    Items.JUNGLE_LEAVES),
                                            0);
                                } else if (block == ModBlocks.ACACIA_LEAF_HEDGE.get()
                                        || block == ModBlocks.ACACIA_LEAF_LAYERS
                                        .get()) {
                                    color = vanillaItemColors.getColor(
                                            new ItemStack(
                                                    Items.ACACIA_LEAVES),
                                            0);
                                } else if (block == ModBlocks.DARK_OAK_LEAF_HEDGE.get()
                                        || block == ModBlocks.DARK_OAK_LEAF_LAYERS
                                        .get()) {
                                    color = vanillaItemColors.getColor(
                                            new ItemStack(
                                                    Items.DARK_OAK_LEAVES),
                                            0);
                                } else if (block == ModBlocks.SPRUCE_LEAF_HEDGE.get()
                                        || block == ModBlocks.SPRUCE_LEAF_LAYERS
                                        .get()) {
                                    color = 0x619961;
                                } else if (block == ModBlocks.BIRCH_LEAF_HEDGE.get()
                                        || block == ModBlocks.BIRCH_LEAF_LAYERS
                                        .get()) {
                                    color = 0x80a755;
                                } else if (block == ModBlocks.MANGROVE_LEAVES.get()
                                        || block == ModBlocks.MANGROVE_LEAF_LAYERS
                                        .get()
                                        ||
                                        block == ModBlocks.MANGROVE_LEAF_HEDGE
                                                .get()) {
                                    color = 0x92c648;
                                }

                                return color;
                            }
                            return -1;
                        },
                        ModItems.OAK_LEAF_LAYERS.get(),
                        ModItems.LEAF_LITTER.get(),
                        ModItems.SPRUCE_LEAF_LAYERS.get(),
                        ModItems.BIRCH_LEAF_LAYERS.get(),
                        ModItems.JUNGLE_LEAF_LAYERS.get(),
                        ModItems.ACACIA_LEAF_LAYERS.get(),
                        ModItems.DARK_OAK_LEAF_LAYERS.get(),
                        ModItems.AZALEA_LEAF_LAYERS.get(),
                        ModItems.FLOWERING_AZALEA_LEAF_LAYERS.get(),
                        ModItems.SNOWY_OAK_LEAF_LAYERS.get(),
                        ModItems.SNOWY_SPRUCE_LEAF_LAYERS.get(),
                        ModItems.SNOWY_BIRCH_LEAF_LAYERS.get(),
                        ModItems.SNOWY_JUNGLE_LEAF_LAYERS.get(),
                        ModItems.SNOWY_ACACIA_LEAF_LAYERS.get(),
                        ModItems.SNOWY_DARK_OAK_LEAF_LAYERS.get(),
                        ModItems.SNOWY_MANGROVE_LEAF_LAYERS.get(),
                        ModItems.SNOWY_AZALEA_LEAF_LAYERS.get(),
                        ModItems.SNOWY_FLOWERING_AZALEA_LEAF_LAYERS.get(),
                        ModItems.MANGROVE_LEAF_LAYERS.get(),
                        ModItems.OAK_LEAF_HEDGE.get(),
                        ModItems.SPRUCE_LEAF_HEDGE.get(),
                        ModItems.BIRCH_LEAF_HEDGE.get(),
                        ModItems.JUNGLE_LEAF_HEDGE.get(),
                        ModItems.ACACIA_LEAF_HEDGE.get(),
                        ModItems.DARK_OAK_LEAF_HEDGE.get(),
                        ModItems.AZALEA_LEAF_HEDGE.get(),
                        ModItems.FLOWERING_AZALEA_LEAF_HEDGE.get(),
                        ModItems.BUSH.get(),
                        ModItems.SNOWY_OAK_LEAF_HEDGE.get(),
                        ModItems.SNOWY_SPRUCE_LEAF_HEDGE.get(),
                        ModItems.SNOWY_BIRCH_LEAF_HEDGE.get(),
                        ModItems.SNOWY_JUNGLE_LEAF_HEDGE.get(),
                        ModItems.SNOWY_ACACIA_LEAF_HEDGE.get(),
                        ModItems.SNOWY_DARK_OAK_LEAF_HEDGE.get(),
                        ModItems.SNOWY_MANGROVE_LEAF_HEDGE.get(),
                        ModItems.SNOWY_AZALEA_LEAF_HEDGE.get(),
                        ModItems.SNOWY_FLOWERING_AZALEA_LEAF_HEDGE.get(),
                        ModItems.MANGROVE_LEAF_HEDGE.get(),
                        ModItems.MANGROVE_LEAVES.get());

                itemColors.register(
                        (stack, tintIndex) -> {
                            if (tintIndex != 1) {
                                return -1;
                            }
                            if (stack.getItem() instanceof BiomeBrushItem brush) {
                                String biomeStr = brush.getCapturedBiome(stack);
                                if (biomeStr != null && !biomeStr.isEmpty()) {
                                    ResourceLocation biomeKey = ResourceLocation.tryParse(biomeStr);
                                    if (biomeKey != null) {
                                        ClientLevel level = Minecraft.getInstance().level;
                                        if (level != null) {
                                            Optional<? extends Registry<Biome>> registryOpt = level.registryAccess().registry(Registry.BIOME_REGISTRY);
                                            if (registryOpt.isPresent()) {
                                                Biome biome = registryOpt.get().get(biomeKey);
                                                if (biome != null) {
                                                    return biome.getFoliageColor();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            return -1;
                        },
                        ModItems.COPPER_BIOME_BRUSH.get(),
                        ModItems.DIAMOND_BIOME_BRUSH.get(),
                        ModItems.NETHERITE_BIOME_BRUSH.get()
                );
            });
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(WanderingHomemakerModel.LAYER_LOCATION, WanderingHomemakerModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void onModelRegistry(ModelRegistryEvent event) {
            ForgeModelBakery.addSpecialModel(
                    new ResourceLocation(BuildScape.MODID, "block/stringlight_frame")
            );
        }

        @SubscribeEvent
        public static void onModelBake(
                ModelBakeEvent event) {
            Set<ResourceLocation> leafHedgeModels = new HashSet<>();
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/oak_leaf_hedge_post"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/oak_leaf_hedge_side"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/oak_leaf_hedge_side_tall"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/oak_leaf_hedge_inventory"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/spruce_leaf_hedge_post"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/spruce_leaf_hedge_side"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/spruce_leaf_hedge_side_tall"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/spruce_leaf_hedge_inventory"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/birch_leaf_hedge_post"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/birch_leaf_hedge_side"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/birch_leaf_hedge_side_tall"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/birch_leaf_hedge_inventory"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/jungle_leaf_hedge_post"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/jungle_leaf_hedge_side"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/jungle_leaf_hedge_side_tall"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/jungle_leaf_hedge_inventory"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/acacia_leaf_hedge_post"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/acacia_leaf_hedge_side"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/acacia_leaf_hedge_side_tall"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/acacia_leaf_hedge_inventory"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/dark_oak_leaf_hedge_post"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/dark_oak_leaf_hedge_side"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/dark_oak_leaf_hedge_side_tall"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/dark_oak_leaf_hedge_inventory"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/azalea_leaf_hedge_post"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/azalea_leaf_hedge_side"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/azalea_leaf_hedge_side_tall"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/azalea_leaf_hedge_inventory"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/flowering_azalea_leaf_hedge_post"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/flowering_azalea_leaf_hedge_side"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/flowering_azalea_leaf_hedge_side_tall"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "block/flowering_azalea_leaf_hedge_inventory"));

            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "item/oak_leaf_hedge"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "item/spruce_leaf_hedge"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "item/birch_leaf_hedge"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "item/jungle_leaf_hedge"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "item/acacia_leaf_hedge"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "item/dark_oak_leaf_hedge"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "item/azalea_leaf_hedge"));
            leafHedgeModels.add(
                    new ResourceLocation(
                            BuildScape.MODID,
                            "item/flowering_azalea_leaf_hedge"));

            int wrappedCount = 0;
            int notFoundCount = 0;
            for (ResourceLocation modelLocation : leafHedgeModels) {
                BakedModel originalModel = event
                        .getModelRegistry()
                        .get(modelLocation);
                if (originalModel != null) {
                    event
                            .getModelRegistry()
                            .put(
                                    modelLocation,
                                    new TintedLeafHedgeModel(
                                            originalModel));
                    wrappedCount++;
                } else {
                    notFoundCount++;
                }
            }

        }

    }

    @Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEventsParticles {

        @SubscribeEvent
        public static void registerFactories(
                ParticleFactoryRegisterEvent event) {
            Minecraft.getInstance().particleEngine.register(
                    ModParticles.GLOW_LIME_SPARKLE.get(),
                    sprites -> new PillarSparkleParticle.Provider(
                            sprites));

            Minecraft.getInstance().particleEngine.register(
                    ModParticles.TINTED_DRIP_FALL.get(),
                    sprites -> new TintedDripParticle.Provider(
                            sprites));

            Minecraft.getInstance().particleEngine.register(
                    ModParticles.TINTED_SPORE.get(),
                    sprites -> new TintedSporeParticle.Provider(
                            sprites));

            Minecraft.getInstance().particleEngine.register(
                    ModParticles.SNOWFLAKE.get(),
                    sprites -> new SnowflakeParticle.Provider(
                            sprites));

            Minecraft.getInstance().particleEngine.register(
                    ModParticles.SNOWFLAKE_STILL.get(),
                    sprites -> new SnowflakeStillParticle.Provider(
                            sprites));

            Minecraft.getInstance().particleEngine.register(
                    ModParticles.CONFETTI.get(),
                    sprites -> new ConfettiParticle.Provider(
                            sprites));

            Minecraft.getInstance().particleEngine.register(
                    ModParticles.BUBBLE.get(),
                    sprites -> new BubbleParticle.Provider(
                            sprites));

            Minecraft.getInstance().particleEngine.register(
                    ModParticles.TRAIL_NOTE.get(),
                    sprites -> new TrailNoteParticle.Provider(
                            sprites));

            Minecraft.getInstance().particleEngine.register(
                    ModParticles.COLORED_SMOKE.get(),
                    sprites -> new ColoredSmokeParticle.Provider(
                            sprites));


            Minecraft.getInstance().particleEngine.register(
                    ModParticles.TINTABLE_HEART.get(),
                    sprites -> new TintableHeartParticle.Provider(
                            sprites));

            Minecraft.getInstance().particleEngine.register(
                    ModParticles.CAKE.get(),
                    sprites -> new CakeParticle.Provider(
                            sprites));

            Minecraft.getInstance().particleEngine.register(
                    ModParticles.CHERRY.get(),
                    sprites -> new CherryParticle.Provider(
                            sprites));

            Minecraft.getInstance()
                    .particleEngine.register(
                            ModParticles.CASCADE.get(),
                            sprites ->
                                    new CascadeParticle.Provider(
                                            sprites
                                    )
                    );

            Minecraft.getInstance()
                    .particleEngine.register(
                            ModParticles.NOXIOUS_GAS.get(),
                            sprites ->
                                    new NoxiousGasParticle.Provider(
                                            sprites
                                    )
                    );

            Minecraft.getInstance()
                    .particleEngine.register(
                            ModParticles.FIREFLY.get(),
                            sprites ->
                                    new FireflyParticle.Provider(
                                            sprites
                                    )
                    );

            Minecraft.getInstance()
                    .particleEngine.register(
                            ModParticles.HAZE.get(),
                            sprites ->
                                    new HazeParticle.Provider(
                                            sprites
                                    )
                    );

            Minecraft.getInstance()
                    .particleEngine.register(
                            ModParticles.SULFUR_BUBBLES.get(),
                            SulfurBubbleParticle.Provider::new
                    );

            Minecraft.getInstance()
                    .particleEngine.register(
                            ModParticles.GEYSER.get(),
                            new GeyserEruptionParticle.Provider()
                    );

            Minecraft.getInstance()
                    .particleEngine.register(
                            ModParticles.GEYSER_BASE.get(),
                            GeyserBaseParticle.Provider::new
                    );

            Minecraft.getInstance()
                    .particleEngine.register(
                            ModParticles.GEYSER_POOF.get(),
                            GeyserBaseParticle.Provider::new
                    );

            Minecraft.getInstance()
                    .particleEngine.register(
                            ModParticles.GEYSER_PLUME.get(),
                            GeyserPlumeParticle.Provider::new
                    );

            Minecraft.getInstance()
                    .particleEngine.register(
                            ModParticles.XP_PARTICLE.get(),
                            sprites -> new XpParticle.Provider(sprites)
                    );
        }
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof AgeableMob mob
                && !(event.getTarget() instanceof Monster)) {
            ItemStack held = event.getItemStack();
            if (held.is(ModItems.GOLDEN_DANDELION.get())) {
                boolean isFrozen = GoldenDandelionGrowth.isFrozen(mob);
                if (!GoldenDandelionGrowth.canToggle(mob.isBaby(), isFrozen)) {
                    return;
                }
                Level level = event.getWorld();
                if (!level.isClientSide) {
                    if (!isFrozen) {
                        if (!GoldenDandelionGrowth.setFrozen(mob, true)) {
                            return;
                        }
                        ((ServerLevel) level).sendParticles(
                                ParticleTypes.WAX_OFF,
                                mob.getX(), mob.getY() + mob.getBbHeight() * 0.5D, mob.getZ(),
                                12, 0.3D, 0.3D, 0.3D, 0.05D);
                        level.playSound(null, mob.blockPosition(),
                                SoundEvents.HONEYCOMB_WAX_ON,
                                SoundSource.NEUTRAL, 1.0F, 1.0F);
                    } else {
                        GoldenDandelionGrowth.setFrozen(mob, false);
                        ((ServerLevel) level).sendParticles(
                                ParticleTypes.HAPPY_VILLAGER,
                                mob.getX(), mob.getY() + mob.getBbHeight() * 0.5D, mob.getZ(),
                                12, 0.3D, 0.3D, 0.3D, 0.05D);
                        level.playSound(null, mob.blockPosition(),
                                SoundEvents.VILLAGER_YES,
                                SoundSource.NEUTRAL, 1.0F, 1.0F);
                    }
                    if (!event.getPlayer().getAbilities().instabuild) {
                        held.shrink(1);
                    }
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        LivingEntity living = event.getEntityLiving();
        FluidState fluid = living.level.getFluidState(living.blockPosition());
        if (ModFluids.isExperience(fluid.getType())) {
            if (living.isSwimming() || living.isInWater()) {
                Vec3 delta = living.getDeltaMovement();
                living.setDeltaMovement(delta.x * 1.175D, delta.y * 1.06D, delta.z * 1.175D);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onSculkDeath(LivingDeathEvent event) {
        SculkCatalystHandler.onDeath(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onSculkExperienceDrop(LivingExperienceDropEvent event) {
        SculkCatalystHandler.onExperienceDrop(event);
    }

    @SubscribeEvent
    public void onSculkBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() != null && !event.getPlayer().isCreative()) {
            BlockState state = event.getState();
            ResourceLocation key = state.getBlock().getRegistryName();
            if (key == null || !"buildscape".equals(key.getNamespace())) {
                return;
            }
            int experience = SculkBreakExperience.forBlock(key.getPath());
            if (experience < 0) {
                return;
            }
            ItemStack tool = event.getPlayer().getMainHandItem();
            int silkTouch = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool);
            event.setExpToDrop(SculkBreakExperience.withTool(experience, silkTouch));
        }
    }

    @SubscribeEvent
    public void onBackportBonemealRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack held = event.getItemStack();
        if (held.is(Items.BONE_MEAL)) {
            Level level = event.getWorld();
            BlockPos pos = event.getPos();
            BlockState state = level.getBlockState(pos);

            if (state.is(Blocks.CACTUS) && event.getFace() == Direction.UP) {
                BlockPos above = pos.above();
                if (level.isEmptyBlock(above)) {
                    if (!level.isClientSide) {
                        level.setBlock(above, ModBlocks.CACTUS_FLOWER.get().defaultBlockState(), 3);
                        level.levelEvent(2005, pos, 0);
                        if (!event.getPlayer().getAbilities().instabuild) held.shrink(1);
                    }
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    return;
                }
            }

            if ((state.is(Blocks.SAND) || state.is(Blocks.RED_SAND)) && event.getFace() == Direction.UP) {
                BlockPos above = pos.above();
                if (level.isEmptyBlock(above)) {
                    if (!level.isClientSide) {
                        level.setBlock(above, ModBlocks.DRY_GRASS.get().defaultBlockState(), 3);
                        level.levelEvent(2005, pos, 0);
                        if (!event.getPlayer().getAbilities().instabuild) held.shrink(1);
                    }
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    return;
                }
            }

            if (state.is(Blocks.PODZOL) && event.getFace() == Direction.UP) {
                BlockPos above = pos.above();
                if (level.isEmptyBlock(above)) {
                    if (!level.isClientSide) {
                        level.setBlock(above, ModBlocks.WHITE_HAZE_BUSH.get().defaultBlockState(), 3);
                        level.levelEvent(2005, pos, 0);
                        if (!event.getPlayer().getAbilities().instabuild) held.shrink(1);
                    }
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                    return;
                } else {
                    List<BlockPos> validPositions = new ArrayList<>();
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            if (dx == 0 && dz == 0) continue;
                            BlockPos nearbyGround = pos.offset(dx, 0, dz);
                            BlockPos nearbyAbove = nearbyGround.above();
                            if (level.getBlockState(nearbyGround).is(Blocks.PODZOL) && level.isEmptyBlock(nearbyAbove)) {
                                validPositions.add(nearbyAbove);
                            }
                        }
                    }
                    if (!validPositions.isEmpty()) {
                        if (!level.isClientSide) {
                            BlockPos target = validPositions.get(level.random.nextInt(validPositions.size()));
                            level.setBlock(target, ModBlocks.WHITE_HAZE_BUSH.get().defaultBlockState(), 3);
                            level.levelEvent(2005, pos, 0);
                            if (!event.getPlayer().getAbilities().instabuild) held.shrink(1);
                        }
                        event.setCanceled(true);
                        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                        return;
                    }
                }
            }

            if (state.is(Blocks.GRASS_BLOCK) && event.getFace() == Direction.UP) {
                if (!level.isClientSide) {
                    final BlockPos finalPos = pos;
                    ((ServerLevel) level).getServer().execute(() -> {
                        BlockPos.betweenClosedStream(finalPos.offset(-3, -1, -3), finalPos.offset(3, 1, 3)).forEach(checkPos -> {
                            if (level.getBlockState(checkPos).is(Blocks.GRASS_BLOCK) && level.isEmptyBlock(checkPos.above())) {
                                if (level.random.nextFloat() < 0.05F) {
                                    level.setBlock(checkPos.above(), ModBlocks.BUSH.get().defaultBlockState(), 3);
                                }
                            }
                        });
                    });
                }
            }

            if (state.is(Blocks.MOSS_BLOCK) && event.getFace() == Direction.UP) {
                if (!level.isClientSide) {
                    final BlockPos finalMossPos = pos;
                    ((ServerLevel) level).getServer().execute(() -> {
                        BlockPos.betweenClosedStream(finalMossPos.offset(-3, -1, -3), finalMossPos.offset(3, 1, 3)).forEach(checkPos -> {
                            if (level.getBlockState(checkPos).is(Blocks.MOSS_BLOCK) && level.isEmptyBlock(checkPos.above())) {
                                float roll = level.random.nextFloat();
                                if (roll < 0.10F) {
                                    level.setBlock(checkPos.above(), ModBlocks.FIREFLY_BUSH.get().defaultBlockState(), 3);
                                } else if (roll < 0.15F) {
                                    BlockState saplingState = ModBlocks.CHERRY_SAPLING.get().defaultBlockState();
                                    if (saplingState.canSurvive(level, checkPos.above())) {
                                        level.setBlock(checkPos.above(), saplingState, 3);
                                    }
                                } else if (roll < 0.20F) {
                                    BlockState propaguleState = ModBlocks.MANGROVE_PROPAGULE.get().defaultBlockState();
                                    if (propaguleState.canSurvive(level, checkPos.above())) {
                                        level.setBlock(checkPos.above(), propaguleState, 3);
                                    }
                                }
                            }
                        });
                    });
                }
            }

            if (state.is(ModBlocks.PALE_OAK_LEAVES.get())) {
                BlockPos targetPos = pos.below();
                while (level.getBlockState(targetPos).is(ModBlocks.PALE_HANGING_MOSS.get())) {
                    targetPos = targetPos.below();
                }
                if (level.isEmptyBlock(targetPos)) {
                    if (!level.isClientSide) {
                        level.setBlock(targetPos, ModBlocks.PALE_HANGING_MOSS.get().defaultBlockState().setValue(HangingMossBlock.TIP, true), 3);
                        level.levelEvent(2005, pos, 0);
                        if (!event.getPlayer().getAbilities().instabuild) held.shrink(1);
                    }
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    return;
                }
            }

            if (state.is(ModBlocks.CREAKING_HEART.get())) {
                if (!level.isClientSide) {
                    if (state.getBlock() instanceof CreakingHeartBlock heart) {
                        level.setBlock(pos, state.setValue(CreakingHeartBlock.ACTIVE, true), 3);
                        level.scheduleTick(pos, heart, 40);
                    }
                    List<Direction> directions = new ArrayList<>(List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN));
                    Collections.shuffle(directions, level.random);
                    int spawned = 0;
                    for (Direction dir : directions) {
                        BlockPos adjPos = pos.relative(dir);
                        Direction attachFace = dir.getOpposite();
                        BooleanProperty faceProp = GlowLichenBlock.getFaceProperty(attachFace);
                        BlockState adjState = level.getBlockState(adjPos);

                        if (adjState.is(ModBlocks.RESIN_CLUMP.get())) {
                            if (!adjState.getValue(faceProp)) {
                                level.setBlock(adjPos, adjState.setValue(faceProp, true), 3);
                                spawned++;
                            }
                        } else if (adjState.isAir()) {
                            BlockState newState = ModBlocks.RESIN_CLUMP.get().defaultBlockState().setValue(faceProp, true);
                            level.setBlock(adjPos, newState, 3);
                            spawned++;
                        }
                        if (spawned >= 2) break;
                    }
                    level.levelEvent(2005, pos, 0);
                    if (!event.getPlayer().getAbilities().instabuild) held.shrink(1);
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
                return;
            }

            if (state.is(ModBlocks.TALL_DRY_GRASS.get())) {
                if (!level.isClientSide) {
                    ItemEntity item = new ItemEntity(
                            level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                            new ItemStack(ModItems.DRY_GRASS.get(), 1));
                    level.addFreshEntity(item);
                    level.levelEvent(2005, pos, 0);
                    if (!event.getPlayer().getAbilities().instabuild) held.shrink(1);
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
                return;
            }
        }
    }

    @SubscribeEvent
    public void onBonemealEvent(BonemealEvent event) {
        if (event.getBlock().is(Blocks.PODZOL)) {
            Level level = event.getWorld();
            BlockPos pos = event.getPos();
            BlockPos above = pos.above();
            if (level.isEmptyBlock(above)) {
                if (!level.isClientSide) {
                    level.setBlock(above, ModBlocks.WHITE_HAZE_BUSH.get().defaultBlockState(), 3);
                }
                event.setResult(net.minecraftforge.eventbus.api.Event.Result.ALLOW);
            }
        }
    }
}
