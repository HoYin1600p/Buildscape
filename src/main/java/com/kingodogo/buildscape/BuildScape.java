package com.kingodogo.buildscape;

import com.kingodogo.buildscape.block.ModBlocks;
import com.kingodogo.buildscape.item.ModItems;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BuildScape.MODID)
public class BuildScape {

    public static final String MODID = "buildscape";
    public static final Logger LOGGER = LogManager.getLogger();

    public static Logger getLogger() {
        return LOGGER;
    }


    private static final java.util.concurrent.ExecutorService ASYNC_POOL = java.util.concurrent.Executors
            .newCachedThreadPool(r -> {
                Thread t = new Thread(r, "BuildScape-Async");
                t.setDaemon(true);
                return t;
            });
    private static final int WORLD_LOAD_WAIT_TICKS = 40;
    private static final int PILLAR_SYNC_INTERVAL = 100;
    private static final int RECOVERY_DELAY_TICKS = 600;
    private static boolean serverFullyInitialized = false;
    private static boolean pillarDataLoadStarted = false;
    private static int worldLoadWaitTicks = 0;
    private static int pillarSyncTickCounter = 0;
    private static int recoveryDelayTicks = 0;
    private static boolean recoveryAttempted = false;

    public BuildScape() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

        com.kingodogo.buildscape.sound.ModSounds.SOUND_EVENTS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        com.kingodogo.buildscape.particle.ModParticles.PARTICLES.register(
                modEventBus);
        com.kingodogo.buildscape.block.ModBlockEntities.BLOCK_ENTITIES.register(
                modEventBus);
        com.kingodogo.buildscape.entity.ModEntities.ENTITIES.register(modEventBus);
        com.kingodogo.buildscape.recipe.ModRecipeSerializers.RECIPE_SERIALIZERS.register(
                modEventBus);
        com.kingodogo.buildscape.network.ModMenuTypes.MENUS.register(modEventBus);


        com.kingodogo.buildscape.worldgen.ModBlockStateProviderTypes.BLOCK_STATE_PROVIDER_TYPES.register(
                modEventBus);
        com.kingodogo.buildscape.worldgen.ModTrunkPlacerTypes.TRUNK_PLACER_TYPES.register(
                modEventBus);
        com.kingodogo.buildscape.worldgen.ModFoliagePlacerTypes.FOLIAGE_PLACER_TYPES.register(
                modEventBus);
        com.kingodogo.buildscape.worldgen.ModTreeDecoratorTypes.TREE_DECORATOR_TYPES.register(
                modEventBus);
        com.kingodogo.buildscape.worldgen.ModConfiguredFeatures.CONFIGURED_FEATURES.register(
                modEventBus);
        com.kingodogo.buildscape.worldgen.ModPlacementModifiers.PLACEMENT_MODIFIERS.register(
                modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static boolean isServerFullyInitialized() {
        return serverFullyInitialized;
    }

    public static java.util.concurrent.ExecutorService getAsyncPool() {
        return ASYNC_POOL;
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

        com.kingodogo.buildscape.world.ModGameRules.register();

        event.enqueueWork(() -> {
            com.kingodogo.buildscape.network.ModMessages.register();
        });

        event.enqueueWork(() -> {
            net.minecraft.world.level.block.DispenserBlock.registerBehavior(
                    com.kingodogo.buildscape.item.ModItems.BOTTLE_OF_MIST.get(),
                    new net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior() {
                        @Override
                        protected net.minecraft.world.entity.projectile.Projectile getProjectile(net.minecraft.world.level.Level level, net.minecraft.core.Position pos, net.minecraft.world.item.ItemStack stack) {
                            return null; // Not used - we override execute instead
                        }

                        @Override
                        public net.minecraft.world.item.ItemStack execute(net.minecraft.core.BlockSource source, net.minecraft.world.item.ItemStack stack) {
                            net.minecraft.world.level.Level level = source.getLevel();
                            net.minecraft.core.Direction facing = source.getBlockState().getValue(net.minecraft.world.level.block.DispenserBlock.FACING);
                            net.minecraft.core.BlockPos pos = source.getPos().relative(facing);

                            double cx = pos.getX() + 0.5 + facing.getStepX() * 0.5;
                            double cy = pos.getY() + 0.5 + facing.getStepY() * 0.5;
                            double cz = pos.getZ() + 0.5 + facing.getStepZ() * 0.5;

                            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                                java.util.Random rand = new java.util.Random();
                                for (int i = 0; i < 40; i++) {
                                    double x = cx + (rand.nextDouble() - 0.5) * 2.0;
                                    double y = cy + (rand.nextDouble() - 0.5);
                                    double z = cz + (rand.nextDouble() - 0.5) * 2.0;

                                    double xSpeed = (rand.nextDouble() - 0.5) * 0.2;
                                    double ySpeed = rand.nextDouble() * 0.05;
                                    double zSpeed = (rand.nextDouble() - 0.5) * 0.2;

                                    serverLevel.sendParticles(
                                            com.kingodogo.buildscape.particle.ModParticles.CASCADE.get(),
                                            x, y, z, 1, xSpeed, ySpeed, zSpeed, 0.0);
                                }
                            }

                            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.FIRE_EXTINGUISH,
                                    net.minecraft.sounds.SoundSource.BLOCKS, 0.1f, 0.1f);

                            stack.shrink(1);
                            return stack;
                        }
                    }
            );
        });

        event.enqueueWork(() -> {
            com.kingodogo.buildscape.sound.ModSounds.COPPER_GRATE_SOUNDS();
            com.kingodogo.buildscape.sound.ModSounds.COPPER_BULB_SOUNDS();
            com.kingodogo.buildscape.sound.ModSounds.MANGROVE_ROOTS_SOUNDS();
            com.kingodogo.buildscape.sound.ModSounds.MUDDY_MANGROVE_ROOTS_SOUNDS();

            com.kingodogo.buildscape.config.PillarParticleConfig.get();

            com.kingodogo.buildscape.block.ModWoodTypes.MANGROVE.getClass();
            com.kingodogo.buildscape.block.ModWoodTypes.BAMBOO.getClass();


            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.RED_ROSE_VINES.get(),
                    0.5f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.BLACK_ROSE_VINES.get(),
                    0.5f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.BLUE_ROSE_VINES.get(),
                    0.5f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.WHITE_ROSE_VINES.get(),
                    0.5f);

            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.FROST_ROSE.get(),
                    0.65f);

            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.RED_MONETS.get(),
                    0.65f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.BLUE_MONETS.get(),
                    0.65f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.PURPLE_MONETS.get(),
                    0.65f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                    .put(ModItems.LIGHT_BLUE_MONETS.get(), 0.65f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.PINK_MONETS.get(),
                    0.65f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.YELLOW_MONETS.get(),
                    0.65f);

            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.RED_PETAL.get(),
                    0.65f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.BLUE_PETAL.get(),
                    0.65f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.ORANGE_PETAL.get(),
                    0.65f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.PINK_PETAL.get(),
                    0.65f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.PURPLE_PETAL.get(),
                    0.65f);

            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                    .put(ModItems.RED_SPORE_BLOSSOM.get(), 0.65f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                    .put(ModItems.CYAN_SPORE_BLOSSOM.get(), 0.65f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                    .put(ModItems.BLUE_SPORE_BLOSSOM.get(), 0.65f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                    .put(ModItems.PURPLE_SPORE_BLOSSOM.get(), 0.65f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                    .put(ModItems.ORANGE_SPORE_BLOSSOM.get(), 0.65f);

            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                    .put(ModItems.SNOWY_SHORT_GRASS.get(), 0.3f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.SNOWY_TALL_GRASS.get(),
                    0.3f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.SNOWY_FERN.get(),
                    0.3f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.SNOWY_LARGE_FERN.get(),
                    0.3f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.SNOWY_BUSH.get(),
                    0.3f);

            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.MANGROVE_LEAVES.get(),
                    0.3f);

            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.SNOWY_LEAVES.get(),
                    0.3f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.SNOWY_OAK_LEAVES.get(),
                    0.3f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                    .put(ModItems.SNOWY_SPRUCE_LEAVES.get(), 0.3f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                    .put(ModItems.SNOWY_BIRCH_LEAVES.get(), 0.3f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                    .put(ModItems.SNOWY_JUNGLE_LEAVES.get(), 0.3f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                    .put(ModItems.SNOWY_ACACIA_LEAVES.get(), 0.3f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                    .put(ModItems.SNOWY_DARK_OAK_LEAVES.get(), 0.3f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                    .put(ModItems.SNOWY_MANGROVE_LEAVES.get(), 0.3f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                    .put(ModItems.SNOWY_AZALEA_LEAVES.get(), 0.3f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                    .put(ModItems.SNOWY_FLOWERING_AZALEA_LEAVES.get(), 0.3f);

            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                    .put(ModItems.BROWN_MUSHROOM_SHELVES.get(), 0.65f);
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                    .put(ModItems.RED_MUSHROOM_SHELVES.get(), 0.65f);

            });
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

        serverFullyInitialized = false;
        pillarDataLoadStarted = false;
        worldLoadWaitTicks = 0;
        recoveryDelayTicks = 0;
        recoveryAttempted = false;

        // Reset the in-memory cache - file data will be loaded when first player joins
        com.kingodogo.buildscape.config.PillarIdManager.resetWorldCache();
    }


    @SubscribeEvent
    public void onRegisterCommands(net.minecraftforge.event.RegisterCommandsEvent event) {

        com.mojang.brigadier.builder.LiteralArgumentBuilder<net.minecraft.commands.CommandSourceStack> buildscapeCommand = com.mojang.brigadier.builder.LiteralArgumentBuilder.<net.minecraft.commands.CommandSourceStack>literal(
                        "buildscape")
                .then(com.mojang.brigadier.builder.LiteralArgumentBuilder.<net.minecraft.commands.CommandSourceStack>literal(
                                "recover")
                        .then(com.mojang.brigadier.builder.LiteralArgumentBuilder.<net.minecraft.commands.CommandSourceStack>literal(
                                        "PillarData")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> {
                                    net.minecraft.commands.CommandSourceStack source = context
                                            .getSource();
                                    net.minecraft.server.MinecraftServer server = source
                                            .getServer();

                                    if (server == null || !server.isRunning()) {
                                        source.sendFailure(
                                                new net.minecraft.network.chat.TextComponent(
                                                        "Server is not running"));
                                        return 0;
                                    }

                                    com.kingodogo.buildscape.config.PillarIdManager manager = com.kingodogo.buildscape.config.PillarIdManager
                                            .get();

                                    if (manager == null) {
                                        source.sendFailure(
                                                new net.minecraft.network.chat.TextComponent(
                                                        "PillarIdManager is not available"));
                                        return 0;
                                    }

                                    source.sendSuccess(
                                            new net.minecraft.network.chat.TextComponent(
                                                    "Starting pillar recovery..."),
                                            true);

                                    server.execute(() -> {
                                        try {
                                            manager.recoverPillarsFromWorld(
                                                    server, false);
                                            source.sendSuccess(
                                                    new net.minecraft.network.chat.TextComponent(
                                                            "Pillar recovery completed. Check console for details."),
                                                    true);
                                        } catch (Exception e) {
                                            source.sendFailure(
                                                    new net.minecraft.network.chat.TextComponent(
                                                            "Error during recovery: "
                                                                    + e.getMessage()));
                                            e.printStackTrace();
                                        }
                                    });

                                    return 1;
                                })))
                .then(com.mojang.brigadier.builder.LiteralArgumentBuilder.<net.minecraft.commands.CommandSourceStack>literal("cosmatics")
                        .then(com.mojang.brigadier.builder.LiteralArgumentBuilder.<net.minecraft.commands.CommandSourceStack>literal("UNLOCK")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> {
                                    com.kingodogo.buildscape.cosmetics.CosmeticManager.getInstance().setDevUnlockAll(true);
                                    context.getSource().sendSuccess(new net.minecraft.network.chat.TextComponent("All cosmetics unlocked for development!"), true);
                                    return 1;
                                })
                        )
                );

        event.getDispatcher().register(buildscapeCommand);
    }

    @SubscribeEvent
    public void onServerStarted(
            net.minecraftforge.event.server.ServerStartedEvent event) {

        pillarDataLoadStarted = false;
        worldLoadWaitTicks = 0;
    }

    @SubscribeEvent
    public void onServerStopped(
            net.minecraftforge.event.server.ServerStoppedEvent event) {

        try {
            net.minecraft.server.MinecraftServer server = event.getServer();
            if (server != null) {
                com.kingodogo.buildscape.config.PillarIdManager manager = com.kingodogo.buildscape.config.PillarIdManager
                        .get();
                if (manager != null && manager.hasLoaded()) {
                    // Use forceSaveImmediate because at this point all players have left
                    // and saveImmediate() would skip due to playerCount==0 guard
                    manager.forceSaveImmediate();
                    manager.saveBackupFile();
                }
            }
        } catch (Exception e) {
            LOGGER.error("BuildScape: Error saving pillar data on server stop: " + e.getMessage());
        }

        serverFullyInitialized = false;
        pillarDataLoadStarted = false;
        worldLoadWaitTicks = 0;
        recoveryDelayTicks = 0;
        recoveryAttempted = false;

        com.kingodogo.buildscape.config.PillarIdManager.fullReset();
    }

    @SubscribeEvent
    public void onPlayerJoin(
            net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {

            // REMOVED: Don't reset pillar data on player join - it clears saved pillar IDs!
            // PillarIdJoinSyncHandler will load the data if needed
            if (!serverFullyInitialized) {
                // com.kingodogo.buildscape.config.PillarIdManager.resetWorldCache();

                com.kingodogo.buildscape.config.PillarParticleConfig.addConfigReloadCallback((isRemote) -> {
                    if (!isRemote) {
                        net.minecraft.server.MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks
                                .getCurrentServer();
                        if (server != null && server.isRunning()
                                && server.getPlayerList().getPlayerCount() > 0) {
                            com.kingodogo.buildscape.config.PillarParticleConfig serverConfig = com.kingodogo.buildscape.config.PillarParticleConfig
                                    .get();
                            com.kingodogo.buildscape.network.SyncConfigPacket configPacket = new com.kingodogo.buildscape.network.SyncConfigPacket(
                                    serverConfig);
                            com.kingodogo.buildscape.network.ModMessages.INSTANCE.send(
                                    net.minecraftforge.network.PacketDistributor.ALL
                                            .noArg(),
                                    configPacket);
                        }
                    }
                });
            }

            com.kingodogo.buildscape.config.PillarIdManager manager = com.kingodogo.buildscape.config.PillarIdManager
                    .get();
            if (!manager.hasLoaded()) {
                manager.load();
            } else {
                serverFullyInitialized = true;
            }

            com.kingodogo.buildscape.config.PillarParticleConfig serverConfig = com.kingodogo.buildscape.config.PillarParticleConfig
                    .get();
            com.kingodogo.buildscape.network.SyncConfigPacket configPacket = new com.kingodogo.buildscape.network.SyncConfigPacket(
                    serverConfig);
            com.kingodogo.buildscape.network.ModMessages.INSTANCE.send(
                    net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> serverPlayer),
                    configPacket);

            // IMPORTANT: Sync pillar IDs to client so GUI works on servers
            // Use a robust delayed sync that actually waits for manager to be ready
            net.minecraft.server.MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
            if (server != null && server.isRunning()) {
                // Also Sync Gamerules
                com.kingodogo.buildscape.network.ModMessages.INSTANCE.send(
                    net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new com.kingodogo.buildscape.network.SyncGameRulesPacket(
                        serverPlayer.getLevel().getGameRules().getBoolean(com.kingodogo.buildscape.world.ModGameRules.FAST_LEAF_DECAY)
                    )
                );
                
                // Schedule pillar ID sync - try immediately, with retries if not loaded
                schedulePillarIdSync(server, serverPlayer, manager, 0);
            }

        }
    }

    /**
     * Schedule pillar ID sync to a player with retry logic.
     * Uses the async pool to wait for the manager to load, then dispatches on the main thread.
     */
    private void schedulePillarIdSync(net.minecraft.server.MinecraftServer server,
                                      net.minecraft.server.level.ServerPlayer player,
                                      com.kingodogo.buildscape.config.PillarIdManager manager,
                                      int attempt) {
        final int MAX_ATTEMPTS = 10;
        final int RETRY_DELAY_MS = 500;

        if (attempt >= MAX_ATTEMPTS) {
            // Give up after max attempts — send whatever we have 
            server.execute(() -> {
                if (player.hasDisconnected()) return;
                sendPillarIdsToPlayer(server, player, manager);
            });
            return;
        }

        if (manager.hasLoaded()) {
            // Manager ready — sync immediately on main thread
            server.execute(() -> {
                if (player.hasDisconnected()) return;
                sendPillarIdsToPlayer(server, player, manager);
            });
        } else {
            // Not loaded yet — wait on async pool and retry 
            ASYNC_POOL.submit(() -> {
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ignored) {
                }
                schedulePillarIdSync(server, player, manager, attempt + 1);
            });
        }
    }

    /**
     * Sends all pillar ID data to a specific player.
     */
    private void sendPillarIdsToPlayer(net.minecraft.server.MinecraftServer server,
                                       net.minecraft.server.level.ServerPlayer player,
                                       com.kingodogo.buildscape.config.PillarIdManager manager) {
        try {
            // Ensure latest colors from NBT before sending
            if (server.isRunning()) {
                manager.syncColorsFromNBTToManager(server);
            }

            java.util.List<com.kingodogo.buildscape.config.PillarIdManager.PillarData> pillarDataList = manager.getAllPillarDataForSync();
            com.kingodogo.buildscape.network.SyncPillarIdsPacket pillarIdsPacket = new com.kingodogo.buildscape.network.SyncPillarIdsPacket(
                    pillarDataList);
            com.kingodogo.buildscape.network.ModMessages.INSTANCE.send(
                    net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                    pillarIdsPacket);
        } catch (Exception e) {
            LOGGER.error("BuildScape: Error sending pillar IDs to player: " + e.getMessage());
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(
            net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) {


        serverFullyInitialized = false;
        pillarDataLoadStarted = false;
        worldLoadWaitTicks = 0;
        pillarSyncTickCounter = 0;
        recoveryDelayTicks = 0;
        recoveryAttempted = false;
    }

    @SubscribeEvent
    public void onWorldUnload(
            net.minecraftforge.event.world.WorldEvent.Unload event) {
        if (event.getWorld() instanceof net.minecraft.server.level.ServerLevel) {

            try {
                net.minecraft.server.MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks
                        .getCurrentServer();
                if (server != null) {
                    com.kingodogo.buildscape.config.PillarIdManager manager = com.kingodogo.buildscape.config.PillarIdManager
                            .get();
                    if (manager != null && manager.hasLoaded()) {
                        // Use forceSaveImmediate - players may have already left
                        manager.forceSaveImmediate();
                        manager.saveBackupFile();
                    }
                }
            } catch (Exception e) {
                LOGGER.error("BuildScape: Error syncing/saving colors before world unload: "
                        + e.getMessage());
                e.printStackTrace();
            }

            com.kingodogo.buildscape.config.PillarIdManager.resetWorldCache();

            serverFullyInitialized = false;
            pillarDataLoadStarted = false;
            worldLoadWaitTicks = 0;
            pillarSyncTickCounter = 0;
            recoveryDelayTicks = 0;
            recoveryAttempted = false;
        }
    }

    @SubscribeEvent
    public void onChunkLoad(
            net.minecraftforge.event.world.ChunkEvent.Load event) {
        if (event.getWorld() instanceof net.minecraft.server.level.ServerLevel serverLevel) {

            if (!serverFullyInitialized) {
                return;
            }

            net.minecraft.server.MinecraftServer server = serverLevel.getServer();
            if (server == null || !server.isRunning()) {
                return;
            }

            if (server.getPlayerList().getPlayerCount() == 0) {
                return;
            }

            if (event.getChunk() instanceof net.minecraft.world.level.chunk.LevelChunk chunk) {

                if (!chunk
                        .getStatus()
                        .isOrAfter(net.minecraft.world.level.chunk.ChunkStatus.FULL)) {
                }
            }
        }
    }

    @SubscribeEvent
    public void onServerTick(
            net.minecraftforge.event.TickEvent.ServerTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) {
            return;
        }

        net.minecraft.server.MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks
                .getCurrentServer();
        if (server == null
                || !server.isRunning()
                || server.getPlayerList().getPlayerCount() == 0) {
            return;
        }

        if (!serverFullyInitialized) {
            com.kingodogo.buildscape.config.PillarIdManager manager = com.kingodogo.buildscape.config.PillarIdManager
                    .get();
            if (manager != null && manager.hasLoaded()) {
                serverFullyInitialized = true;
            } else {
                return;
            }
        }

        com.kingodogo.buildscape.config.PillarIdManager.checkAndRunScheduledRecovery();

        recoveryDelayTicks++;
        if (recoveryDelayTicks >= RECOVERY_DELAY_TICKS
                && !recoveryAttempted
                && serverFullyInitialized) {
            recoveryAttempted = true;
            com.kingodogo.buildscape.config.PillarIdManager manager = com.kingodogo.buildscape.config.PillarIdManager
                    .get();
            if (manager != null) {
                try {
                    java.nio.file.Path worldPath = server.getWorldPath(
                            net.minecraft.world.level.storage.LevelResource.ROOT);
                    java.io.File dataFile = worldPath
                            .resolve("buildscape/pillar-ids.dat")
                            .toFile();

                    boolean needsRecovery = false;
                    if (!dataFile.exists()) {
                        needsRecovery = true;
                    } else if (dataFile.length() == 0) {
                        needsRecovery = true;
                    } else if (dataFile.length() <= 2) {
                        try (java.io.FileReader fr = new java.io.FileReader(dataFile)) {
                            char[] buffer = new char[10];
                            int read = fr.read(buffer);
                            String content = new String(buffer, 0, read).trim();
                            if (content.equals("{}") || content.isEmpty()) {
                                needsRecovery = true;
                            }
                        } catch (Exception e) {
                            needsRecovery = true;
                        }
                    } else {
                        if (manager.getPillarCount() == 0) {
                        }
                    }

                    if (needsRecovery) {

                    }
                } catch (Exception e) {
                    System.err.println(
                            "BuildScape: Error during delayed recovery: " + e.getMessage());
                }
            }
        }

        pillarSyncTickCounter++;
        if (pillarSyncTickCounter >= PILLAR_SYNC_INTERVAL) {
            pillarSyncTickCounter = 0;

            try {
                com.kingodogo.buildscape.config.PillarIdManager manager = com.kingodogo.buildscape.config.PillarIdManager
                        .get();

                for (net.minecraft.server.level.ServerLevel level : server.getAllLevels()) {
                    if (level == null) {
                        continue;
                    }

                    if (!level.getServer().isRunning()) {
                        continue;
                    }

                    String dimensionKey = com.kingodogo.buildscape.config.PillarIdManager
                            .getDimensionKey(
                                    level);

                    for (String pillarId : manager.getAllPillarIds()) {
                        try {
                            com.kingodogo.buildscape.config.PillarIdManager.PillarData data = manager
                                    .getPillarData(pillarId);
                            if (data == null || !data.dimension.equals(dimensionKey)) {
                                continue;
                            }
                            if (!data.hasColors()) {
                                continue;
                            }

                            net.minecraft.core.BlockPos pos = data.getBlockPos();

                            if (!level.hasChunkAt(pos)) {
                                continue;
                            }

                            net.minecraft.world.level.chunk.ChunkAccess chunk = level
                                    .getChunk(pos);
                            if (!(chunk instanceof net.minecraft.world.level.chunk.LevelChunk)) {
                                continue;
                            }

                            if (!chunk
                                    .getStatus()
                                    .isOrAfter(net.minecraft.world.level.chunk.ChunkStatus.FULL)) {
                                continue;
                            }

                            net.minecraft.world.level.block.entity.BlockEntity be = level
                                    .getBlockEntity(pos);
                            if (be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillarBE) {

                                if (pillarBE.getPillarId() == null
                                        || !pillarBE.getPillarId()
                                        .equals(data.id)) {
                                    pillarBE.forceSetColors(data.getColors(),
                                            data.id);

                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                }
            } catch (Exception e) {

            }
        }
    }

    @SubscribeEvent
    public void onWandererTrades(
            net.minecraftforge.event.village.WandererTradesEvent event) {
        event
                .getGenericTrades()
                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                        new net.minecraft.world.item.ItemStack(
                                net.minecraft.world.item.Items.EMERALD,
                                5),
                        new net.minecraft.world.item.ItemStack(com.kingodogo.buildscape.item.ModItems.MANGROVE_PROPAGULE.get(), 1),
                        8,
                        1,
                        0.05f));
        
        event
                .getGenericTrades()
                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                        new net.minecraft.world.item.ItemStack(
                                net.minecraft.world.item.Items.EMERALD,
                                1),
                        new net.minecraft.world.item.ItemStack(ModItems.RED_MONETS.get(), 1),
                        2,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                        new net.minecraft.world.item.ItemStack(
                                net.minecraft.world.item.Items.EMERALD,
                                1),
                        new net.minecraft.world.item.ItemStack(ModItems.BLUE_MONETS.get(), 1),
                        2,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                        new net.minecraft.world.item.ItemStack(
                                net.minecraft.world.item.Items.EMERALD,
                                1),
                        new net.minecraft.world.item.ItemStack(
                                ModItems.PURPLE_MONETS.get(),
                                1),
                        2,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                        new net.minecraft.world.item.ItemStack(
                                net.minecraft.world.item.Items.EMERALD,
                                1),
                        new net.minecraft.world.item.ItemStack(
                                ModItems.LIGHT_BLUE_MONETS.get(),
                                1),
                        2,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                        new net.minecraft.world.item.ItemStack(
                                net.minecraft.world.item.Items.EMERALD,
                                1),
                        new net.minecraft.world.item.ItemStack(ModItems.PINK_MONETS.get(), 1),
                        2,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                        new net.minecraft.world.item.ItemStack(
                                net.minecraft.world.item.Items.EMERALD,
                                1),
                        new net.minecraft.world.item.ItemStack(
                                ModItems.YELLOW_MONETS.get(),
                                1),
                        2,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                        new net.minecraft.world.item.ItemStack(
                                net.minecraft.world.item.Items.EMERALD,
                                1),
                        new net.minecraft.world.item.ItemStack(ModItems.CLOVER.get(), 4),
                        2,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                        new net.minecraft.world.item.ItemStack(
                                net.minecraft.world.item.Items.EMERALD,
                                1),
                        new net.minecraft.world.item.ItemStack(
                                ModItems.RED_ROSE_VINES.get(),
                                1),
                        2,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                        new net.minecraft.world.item.ItemStack(
                                net.minecraft.world.item.Items.EMERALD,
                                1),
                        new net.minecraft.world.item.ItemStack(
                                ModItems.BLACK_ROSE_VINES.get(),
                                1),
                        2,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                        new net.minecraft.world.item.ItemStack(
                                net.minecraft.world.item.Items.EMERALD,
                                1),
                        new net.minecraft.world.item.ItemStack(
                                ModItems.BLUE_ROSE_VINES.get(),
                                1),
                        2,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                        new net.minecraft.world.item.ItemStack(
                                net.minecraft.world.item.Items.EMERALD,
                                1),
                        new net.minecraft.world.item.ItemStack(
                                ModItems.WHITE_ROSE_VINES.get(),
                                1),
                        2,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                        new net.minecraft.world.item.ItemStack(
                                net.minecraft.world.item.Items.EMERALD,
                                1),
                        new net.minecraft.world.item.ItemStack(
                                ModItems.SNOWY_GRASS_BLOCK.get(),
                                1),
                        2,
                        1,
                        0.05f));
        event
                .getGenericTrades()
                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                        new net.minecraft.world.item.ItemStack(
                                net.minecraft.world.item.Items.EMERALD,
                                1),
                        new net.minecraft.world.item.ItemStack(
                                ModItems.RED_SPORE_BLOSSOM.get(),
                                1),
                        2,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                        new net.minecraft.world.item.ItemStack(
                                net.minecraft.world.item.Items.EMERALD,
                                1),
                        new net.minecraft.world.item.ItemStack(
                                ModItems.CYAN_SPORE_BLOSSOM.get(),
                                1),
                        2,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                        new net.minecraft.world.item.ItemStack(
                                net.minecraft.world.item.Items.EMERALD,
                                1),
                        new net.minecraft.world.item.ItemStack(
                                ModItems.BLUE_SPORE_BLOSSOM.get(),
                                1),
                        2,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                        new net.minecraft.world.item.ItemStack(
                                net.minecraft.world.item.Items.EMERALD,
                                1),
                        new net.minecraft.world.item.ItemStack(
                                ModItems.PURPLE_SPORE_BLOSSOM.get(),
                                1),
                        2,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                        new net.minecraft.world.item.ItemStack(
                                net.minecraft.world.item.Items.EMERALD,
                                1),
                        new net.minecraft.world.item.ItemStack(
                                ModItems.ORANGE_SPORE_BLOSSOM.get(),
                                1),
                        2,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                        new net.minecraft.world.item.ItemStack(
                                net.minecraft.world.item.Items.EMERALD,
                                1),
                        new net.minecraft.world.item.ItemStack(ModItems.ICICLE.get(), 2),
                        2,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> {
                    if (rand.nextFloat() <= 0.15f) { // 15% chance
                        return new net.minecraft.world.item.trading.MerchantOffer(
                                new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND, 12),
                                new net.minecraft.world.item.ItemStack(ModItems.ANCIENT_ASHEN_SCROLL.get(), 1),
                                1, // max uses
                                1, // xp reward
                                0.0f// multiplier
                        );
                    }
                    return null;
                });
    }

    @SubscribeEvent
    public void onLeftClickBlock(
            net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock event) {
        net.minecraft.world.level.block.state.BlockState state = event
                .getWorld()
                .getBlockState(event.getPos());
        net.minecraft.world.level.block.Block block = state.getBlock();

        if (block instanceof com.kingodogo.buildscape.block.PetalBlock
                || block instanceof com.kingodogo.buildscape.block.CloverBlock
                || block instanceof com.kingodogo.buildscape.block.RoseVinesBlock) {
            if (block.getSoundType(
                    state) instanceof com.kingodogo.buildscape.block.CustomSoundType customSound) {
                net.minecraft.core.BlockPos pos = event.getPos();
                net.minecraft.world.level.Level level = event.getWorld();

                level.playSound(
                        null,
                        pos,
                        block.getSoundType(state).getHitSound(),
                        net.minecraft.sounds.SoundSource.BLOCKS,
                        customSound.getHitVolume(),
                        customSound.getHitPitch());
            }
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(
            net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock event) {
        net.minecraft.world.level.block.state.BlockState state = event
                .getWorld()
                .getBlockState(event.getPos());
        net.minecraft.world.item.ItemStack heldItem = event
                .getPlayer()
                .getItemInHand(event.getHand());

        if (state.getBlock() instanceof net.minecraft.world.level.block.VineBlock) {
            if (heldItem.is(net.minecraft.world.item.Items.SHEARS)) {
                if (state.hasProperty(com.kingodogo.buildscape.block.ModBlockProperties.SHEARED)) {
                    if (!state.getValue(com.kingodogo.buildscape.block.ModBlockProperties.SHEARED)) {
                        if (!event.getWorld().isClientSide) {
                            event.getWorld().setBlockAndUpdate(event.getPos(), state.setValue(com.kingodogo.buildscape.block.ModBlockProperties.SHEARED, true));
                            event.getWorld().playSound(null, event.getPos(), net.minecraft.sounds.SoundEvents.GROWING_PLANT_CROP, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                            event.getWorld().gameEvent(event.getPlayer(), net.minecraft.world.level.gameevent.GameEvent.SHEAR, event.getPos());
                            heldItem.hurtAndBreak(1, event.getPlayer(), (p) -> p.broadcastBreakEvent(event.getHand()));
                        }
                        event.setCancellationResult(net.minecraft.world.InteractionResult.sidedSuccess(event.getWorld().isClientSide));
                        event.setCanceled(true);
                        return;
                    }
                }
            }
        }

        if (state.getBlock() == ModBlocks.MANGROVE_LEAVES.get()
                && heldItem.getItem() instanceof net.minecraft.world.item.BoneMealItem) {
            if (event.getFace() != net.minecraft.core.Direction.DOWN) {
                event.setCanceled(true);
                event.setCancellationResult(net.minecraft.world.InteractionResult.FAIL);
                return;
            }
        }

        if (state.getBlock() == ModBlocks.BAMBOO_BLOCK.get()
                && heldItem.getItem() instanceof net.minecraft.world.item.AxeItem) {
            net.minecraft.core.BlockPos pos = event.getPos();
            net.minecraft.world.level.Level level = event.getWorld();
            net.minecraft.world.entity.player.Player player = event.getPlayer();

            net.minecraft.core.Direction.Axis axis = state.getValue(
                    net.minecraft.world.level.block.RotatedPillarBlock.AXIS);

            level.setBlock(
                    pos,
                    ModBlocks.STRIPPED_BAMBOO_BLOCK.get()
                            .defaultBlockState()
                            .setValue(
                                    net.minecraft.world.level.block.RotatedPillarBlock.AXIS,
                                    axis),
                    11);

            level.playSound(
                    null,
                    pos,
                    net.minecraft.sounds.SoundEvents.AXE_STRIP,
                    net.minecraft.sounds.SoundSource.BLOCKS,
                    1.0f,
                    1.0f);

            if (!player.getAbilities().instabuild) {
                heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));
            }

            event.setCanceled(true);
            event.setCancellationResult(
                    net.minecraft.world.InteractionResult.SUCCESS);
            return;
        }

        if (state.getBlock() == ModBlocks.MANGROVE_LOG.get()
                && heldItem.getItem() instanceof net.minecraft.world.item.AxeItem) {
            net.minecraft.core.BlockPos pos = event.getPos();
            net.minecraft.world.level.Level level = event.getWorld();
            net.minecraft.world.entity.player.Player player = event.getPlayer();

            net.minecraft.core.Direction.Axis axis = state.getValue(
                    net.minecraft.world.level.block.RotatedPillarBlock.AXIS);

            level.setBlock(
                    pos,
                    ModBlocks.STRIPPED_MANGROVE_LOG.get()
                            .defaultBlockState()
                            .setValue(
                                    net.minecraft.world.level.block.RotatedPillarBlock.AXIS,
                                    axis),
                    11);

            level.playSound(
                    null,
                    pos,
                    net.minecraft.sounds.SoundEvents.AXE_STRIP,
                    net.minecraft.sounds.SoundSource.BLOCKS,
                    1.0f,
                    1.0f);

            if (!player.getAbilities().instabuild) {
                heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));
            }

            event.setCanceled(true);
            event.setCancellationResult(
                    net.minecraft.world.InteractionResult.SUCCESS);
            return;
        }

        if (state.getBlock() == ModBlocks.MANGROVE_WOOD.get()
                && heldItem.getItem() instanceof net.minecraft.world.item.AxeItem) {
            net.minecraft.core.BlockPos pos = event.getPos();
            net.minecraft.world.level.Level level = event.getWorld();
            net.minecraft.world.entity.player.Player player = event.getPlayer();

            net.minecraft.core.Direction.Axis axis = state.getValue(
                    net.minecraft.world.level.block.RotatedPillarBlock.AXIS);

            level.setBlock(
                    pos,
                    ModBlocks.STRIPPED_MANGROVE_WOOD.get()
                            .defaultBlockState()
                            .setValue(
                                    net.minecraft.world.level.block.RotatedPillarBlock.AXIS,
                                    axis),
                    11);

            level.playSound(
                    null,
                    pos,
                    net.minecraft.sounds.SoundEvents.AXE_STRIP,
                    net.minecraft.sounds.SoundSource.BLOCKS,
                    1.0f,
                    1.0f);

            if (!player.getAbilities().instabuild) {
                heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));
            }

            event.setCanceled(true);
            event.setCancellationResult(
                    net.minecraft.world.InteractionResult.SUCCESS);
            return;
        }

        if (state.getBlock() == net.minecraft.world.level.block.Blocks.DIRT
                && heldItem.getItem() == net.minecraft.world.item.Items.POTION) {
            if (net.minecraft.world.item.alchemy.PotionUtils
                    .getPotion(heldItem) == net.minecraft.world.item.alchemy.Potions.WATER) {
                net.minecraft.core.BlockPos pos = event.getPos();
                net.minecraft.world.level.Level level = event.getWorld();
                net.minecraft.world.entity.player.Player player = event.getPlayer();

                if (level.isClientSide) {
                    spawnSplashParticles(level, pos);
                    return;
                }

                level.setBlock(pos, ModBlocks.MUD.get().defaultBlockState(), 3);

                level.playSound(
                        null,
                        pos,
                        net.minecraft.sounds.SoundEvents.BOTTLE_EMPTY,
                        net.minecraft.sounds.SoundSource.BLOCKS,
                        1.0f,
                        1.0f);

                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                    net.minecraft.world.item.ItemStack emptyBottle = new net.minecraft.world.item.ItemStack(
                            net.minecraft.world.item.Items.GLASS_BOTTLE);
                    if (heldItem.isEmpty()) {
                        player.setItemInHand(event.getHand(), emptyBottle);
                    } else if (!player.getInventory().add(emptyBottle)) {
                        player.drop(emptyBottle, false);
                    }
                }

                spawnSplashParticles(level, pos);

                event.setCanceled(true);
                event.setCancellationResult(
                        net.minecraft.world.InteractionResult.SUCCESS);
            }
        }
    }

    private static class HitHelper extends net.minecraft.world.item.Item {
        public HitHelper() { super(new net.minecraft.world.item.Item.Properties()); }
        public static net.minecraft.world.phys.BlockHitResult getHit(net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player, net.minecraft.world.level.ClipContext.Fluid fluidMode) {
            return getPlayerPOVHitResult(level, player, fluidMode);
        }
    }

    @SubscribeEvent
    public void onRightClickItem(net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem event) {
        net.minecraft.world.item.ItemStack stack = event.getItemStack();
        if (stack.is(net.minecraft.world.item.Items.GLASS_BOTTLE)) {
            net.minecraft.world.level.Level level = event.getWorld();
            net.minecraft.world.entity.player.Player player = event.getPlayer();

            net.minecraft.world.phys.BlockHitResult hitResult = HitHelper.getHit(level, player, net.minecraft.world.level.ClipContext.Fluid.SOURCE_ONLY);

            if (hitResult.getType() == net.minecraft.world.phys.HitResult.Type.MISS) {
                if (!level.isClientSide) {
                    // Collect mist from air
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    net.minecraft.world.item.ItemStack mistBottle = new net.minecraft.world.item.ItemStack(com.kingodogo.buildscape.item.ModItems.BOTTLE_OF_MIST.get());
                    if (!player.getInventory().add(mistBottle.copy())) {
                        player.drop(mistBottle, false);
                    }
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), net.minecraft.sounds.SoundEvents.BOTTLE_FILL, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
                    
                    if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        serverLevel.sendParticles(com.kingodogo.buildscape.particle.ModParticles.CASCADE.get(), 
                            player.getX(), player.getEyeY(), player.getZ(), 
                            15, 0.5, 0.5, 0.5, 0.05);
                    }
                }
                event.setCancellationResult(net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onItemCrafted(
            net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent event) {
        net.minecraft.world.item.ItemStack crafted = event.getCrafting();

        if (crafted.getItem() == net.minecraft.world.item.Items.SUSPICIOUS_STEW) {
            boolean hasFrostRose = false;

            net.minecraft.world.Container inventory = event.getInventory();
            if (inventory instanceof net.minecraft.world.inventory.CraftingContainer container) {

                for (int i = 0; i < container.getContainerSize(); i++) {
                    net.minecraft.world.item.ItemStack stack = container.getItem(i);
                    if (stack.getItem() == ModItems.FROST_ROSE.get()) {
                        hasFrostRose = true;
                        break;
                    }
                }
            }

            if (hasFrostRose) {
                net.minecraft.nbt.CompoundTag nbt = crafted.getOrCreateTag();
                nbt.putInt("FrostRoseStew", 1);
            }
        }
    }

    @SubscribeEvent
    public void onItemUseFinish(
            net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof net.minecraft.world.entity.player.Player player)) {
            return;
        }

        net.minecraft.world.item.ItemStack itemStack = event.getItem();

        if (itemStack.getItem() == net.minecraft.world.item.Items.SUSPICIOUS_STEW) {
            net.minecraft.nbt.CompoundTag nbt = itemStack.getTag();
            if (nbt != null
                    && nbt.contains("FrostRoseStew")
                    && nbt.getInt("FrostRoseStew") == 1) {
                if (!player.level.isClientSide) {
                    net.minecraft.nbt.CompoundTag playerData = player.getPersistentData();
                    playerData.putInt("FrostRoseStewDamageTicks", 120);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(
            net.minecraftforge.event.TickEvent.PlayerTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) {
            return;
        }

        net.minecraft.world.entity.player.Player player = event.player;
        if (player.level.isClientSide) {
            return;
        }

        net.minecraft.nbt.CompoundTag playerData = player.getPersistentData();
        if (playerData.contains("FrostRoseStewDamageTicks")) {
            int ticksRemaining = playerData.getInt("FrostRoseStewDamageTicks");

            if (ticksRemaining > 0) {
                if (ticksRemaining % 20 == 0 && player.isAlive()) {
                    net.minecraft.world.damagesource.DamageSource freezeDamage = net.minecraft.world.damagesource.DamageSource.GENERIC;
                    player.hurt(freezeDamage, 1.0F);

                    net.minecraft.world.level.Level level = player.level;
                    net.minecraft.core.BlockPos playerPos = player.blockPosition();
                    level.playSound(
                            null,
                            playerPos,
                            net.minecraft.sounds.SoundEvents.POWDER_SNOW_STEP,
                            net.minecraft.sounds.SoundSource.PLAYERS,
                            0.5f,
                            1.0f);

                    if (player.canFreeze()) {
                        player.setTicksFrozen(Math.min(player.getTicksFrozen() + 140, 300));
                    }
                }

                ticksRemaining--;
                if (ticksRemaining > 0) {
                    playerData.putInt("FrostRoseStewDamageTicks", ticksRemaining);
                } else {
                    playerData.remove("FrostRoseStewDamageTicks");
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(
            net.minecraftforge.event.entity.EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof net.minecraft.world.entity.vehicle.Boat boat) {
            net.minecraft.nbt.CompoundTag nbt = boat.getPersistentData();
            if (nbt.contains("MangroveBoatType")
                    && nbt.getString("MangroveBoatType").equals("mangrove")) {
            }
        }
    }

    private void spawnSplashParticles(
            net.minecraft.world.level.Level level,
            net.minecraft.core.BlockPos pos) {
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    net.minecraft.core.particles.ParticleTypes.SPLASH,
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
                        net.minecraft.core.particles.ParticleTypes.SPLASH,
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
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                com.kingodogo.buildscape.block.ModWoodTypes.MANGROVE.getClass();
                com.kingodogo.buildscape.block.ModWoodTypes.BAMBOO.getClass();
            });


            event.enqueueWork(() -> {
                com.kingodogo.buildscape.client.ModKeyBinds.register();
            });

            event.enqueueWork(() -> {
                com.kingodogo.buildscape.client.ClientEvents.initializeConfigCallback();
            });

            event.enqueueWork(() -> {
                net.minecraft.client.gui.screens.MenuScreens.register(
                        com.kingodogo.buildscape.network.ModMenuTypes.PET_MENU.get(),
                        com.kingodogo.buildscape.client.screen.PetScreen::new
                );
            });


            event.enqueueWork(() -> {
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLACK_MOSAIC_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLUE_MOSAIC_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BROWN_MOSAIC_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.CYAN_MOSAIC_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GRAY_MOSAIC_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GREEN_MOSAIC_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_BLUE_MOSAIC_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_GRAY_MOSAIC_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIME_MOSAIC_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MAGENTA_MOSAIC_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ORANGE_MOSAIC_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PINK_MOSAIC_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PURPLE_MOSAIC_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.RED_MOSAIC_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.WHITE_MOSAIC_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.YELLOW_MOSAIC_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLACK_MOSAIC_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLUE_MOSAIC_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BROWN_MOSAIC_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.CYAN_MOSAIC_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GRAY_MOSAIC_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GREEN_MOSAIC_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_BLUE_MOSAIC_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_GRAY_MOSAIC_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIME_MOSAIC_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MAGENTA_MOSAIC_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ORANGE_MOSAIC_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PINK_MOSAIC_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PURPLE_MOSAIC_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.RED_MOSAIC_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.WHITE_MOSAIC_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.YELLOW_MOSAIC_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_WHITE_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_LIGHT_GRAY_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_GRAY_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_BLACK_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_BROWN_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_RED_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_ORANGE_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_YELLOW_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_LIME_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_GREEN_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_CYAN_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_LIGHT_BLUE_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_BLUE_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_PURPLE_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_MAGENTA_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_PINK_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_WHITE_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_LIGHT_GRAY_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_GRAY_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_BLACK_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_BROWN_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_RED_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_ORANGE_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_YELLOW_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_LIME_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_GREEN_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_CYAN_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_LIGHT_BLUE_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_BLUE_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_PURPLE_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_MAGENTA_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_PINK_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLACK_GLAZED_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLUE_GLAZED_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BROWN_GLAZED_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.CYAN_GLAZED_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GRAY_GLAZED_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GREEN_GLAZED_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_BLUE_GLAZED_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_GRAY_GLAZED_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIME_GLAZED_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MAGENTA_GLAZED_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ORANGE_GLAZED_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PINK_GLAZED_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PURPLE_GLAZED_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.RED_GLAZED_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.WHITE_GLAZED_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.YELLOW_GLAZED_GLASS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLACK_GLAZED_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLUE_GLAZED_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BROWN_GLAZED_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.CYAN_GLAZED_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GRAY_GLAZED_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GREEN_GLAZED_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_BLUE_GLAZED_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_GRAY_GLAZED_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIME_GLAZED_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MAGENTA_GLAZED_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ORANGE_GLAZED_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PINK_GLAZED_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PURPLE_GLAZED_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.RED_GLAZED_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.WHITE_GLAZED_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.YELLOW_GLAZED_GLASS_PANE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.WHITE_MOSAIC_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.WHITE_MOSAIC_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_GRAY_MOSAIC_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_GRAY_MOSAIC_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GRAY_MOSAIC_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GRAY_MOSAIC_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLACK_MOSAIC_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLACK_MOSAIC_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BROWN_MOSAIC_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BROWN_MOSAIC_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.RED_MOSAIC_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.RED_MOSAIC_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ORANGE_MOSAIC_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ORANGE_MOSAIC_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.YELLOW_MOSAIC_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.YELLOW_MOSAIC_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIME_MOSAIC_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIME_MOSAIC_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GREEN_MOSAIC_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GREEN_MOSAIC_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.CYAN_MOSAIC_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.CYAN_MOSAIC_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_BLUE_MOSAIC_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_BLUE_MOSAIC_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLUE_MOSAIC_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLUE_MOSAIC_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PURPLE_MOSAIC_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PURPLE_MOSAIC_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MAGENTA_MOSAIC_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MAGENTA_MOSAIC_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PINK_MOSAIC_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PINK_MOSAIC_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_WHITE_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_WHITE_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_LIGHT_GRAY_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_LIGHT_GRAY_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_GRAY_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_GRAY_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_BLACK_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_BLACK_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_BROWN_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_BROWN_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_RED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_RED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_ORANGE_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_ORANGE_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_YELLOW_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_YELLOW_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_LIME_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_LIME_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_GREEN_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_GREEN_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_CYAN_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_CYAN_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_LIGHT_BLUE_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_LIGHT_BLUE_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_BLUE_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_BLUE_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_PURPLE_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_PURPLE_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_MAGENTA_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_MAGENTA_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_PINK_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FACTORY_PINK_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.WHITE_STAINED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_GRAY_STAINED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GRAY_STAINED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLACK_STAINED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BROWN_STAINED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.RED_STAINED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ORANGE_STAINED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.YELLOW_STAINED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIME_STAINED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GREEN_STAINED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.CYAN_STAINED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_BLUE_STAINED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLUE_STAINED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PURPLE_STAINED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MAGENTA_STAINED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PINK_STAINED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.WHITE_STAINED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_GRAY_STAINED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GRAY_STAINED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLACK_STAINED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BROWN_STAINED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.RED_STAINED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ORANGE_STAINED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.YELLOW_STAINED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIME_STAINED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GREEN_STAINED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.CYAN_STAINED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_BLUE_STAINED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLUE_STAINED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PURPLE_STAINED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MAGENTA_STAINED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PINK_STAINED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.WHITE_GLAZED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.WHITE_GLAZED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_GRAY_GLAZED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_GRAY_GLAZED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GRAY_GLAZED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GRAY_GLAZED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLACK_GLAZED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLACK_GLAZED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BROWN_GLAZED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BROWN_GLAZED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.RED_GLAZED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.RED_GLAZED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ORANGE_GLAZED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ORANGE_GLAZED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.YELLOW_GLAZED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.YELLOW_GLAZED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIME_GLAZED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIME_GLAZED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GREEN_GLAZED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GREEN_GLAZED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.CYAN_GLAZED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.CYAN_GLAZED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_BLUE_GLAZED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_BLUE_GLAZED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLUE_GLAZED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLUE_GLAZED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PURPLE_GLAZED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PURPLE_GLAZED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MAGENTA_GLAZED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MAGENTA_GLAZED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PINK_GLAZED_GLASS_SLAB.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PINK_GLAZED_GLASS_STAIRS.get(),
                        net.minecraft.client.renderer.RenderType.translucent());


                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.DIAMOND_CHAIN.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GOLD_CHAIN.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.EMERALD_CHAIN.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ANCIENT_STEEL_CHAIN.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.NETHERITE_CHAIN.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ASHENKING_DIAMOND_PILLAR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ASHENKING_GOLD_PILLAR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ASHENKING_EMERALD_PILLAR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ASHENKING_NETHERITE_PILLAR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.COPPER_CHAIN.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.EXPOSED_COPPER_CHAIN.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.WEATHERED_COPPER_CHAIN.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.OXIDIZED_COPPER_CHAIN.get(),
                        net.minecraft.client.renderer.RenderType.cutout());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LARGE_IRON_CHAIN.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LARGE_GOLD_CHAIN.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LARGE_DIAMOND_CHAIN.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LARGE_EMERALD_CHAIN.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LARGE_ANCIENT_STEEL_CHAIN.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LARGE_NETHERITE_CHAIN.get(),
                        net.minecraft.client.renderer.RenderType.cutout());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.WHITE_ORNAMENT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ORANGE_ORNAMENT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MAGENTA_ORNAMENT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_BLUE_ORNAMENT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.YELLOW_ORNAMENT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIME_ORNAMENT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PINK_ORNAMENT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GRAY_ORNAMENT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_GRAY_ORNAMENT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.CYAN_ORNAMENT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PURPLE_ORNAMENT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLUE_ORNAMENT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BROWN_ORNAMENT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GREEN_ORNAMENT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.RED_ORNAMENT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLACK_ORNAMENT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GLASS_ORNAMENT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.TINTED_GLASS_ORNAMENT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.WHITE_STRING_LIGHT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ORANGE_STRING_LIGHT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MAGENTA_STRING_LIGHT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_BLUE_STRING_LIGHT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.YELLOW_STRING_LIGHT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIME_STRING_LIGHT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PINK_STRING_LIGHT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GRAY_STRING_LIGHT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_GRAY_STRING_LIGHT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.CYAN_STRING_LIGHT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PURPLE_STRING_LIGHT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLUE_STRING_LIGHT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BROWN_STRING_LIGHT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GREEN_STRING_LIGHT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.RED_STRING_LIGHT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLACK_STRING_LIGHT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MULTICOLOR_STRING_LIGHT.get(),
                        net.minecraft.client.renderer.RenderType.translucent());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_LEAVES.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_OAK_LEAVES.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_SPRUCE_LEAVES.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_BIRCH_LEAVES.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_JUNGLE_LEAVES.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_ACACIA_LEAVES.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_DARK_OAK_LEAVES.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_MANGROVE_LEAVES.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_AZALEA_LEAVES.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_FLOWERING_AZALEA_LEAVES.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_SHORT_GRASS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_TALL_GRASS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_FERN.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_LARGE_FERN.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_BUSH.get(),
                        net.minecraft.client.renderer.RenderType.cutout());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.WHITE_STAR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ORANGE_STAR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MAGENTA_STAR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_BLUE_STAR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.YELLOW_STAR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIME_STAR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PINK_STAR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GRAY_STAR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_GRAY_STAR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.CYAN_STAR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PURPLE_STAR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLUE_STAR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BROWN_STAR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GREEN_STAR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.RED_STAR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLACK_STAR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GLOW_STAR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GLOW_LIGHTS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MULTICOLOR_GLOW_LIGHTS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BIT_COPPER_GRATE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BIT_EXPOSED_COPPER_GRATE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BIT_WEATHERED_COPPER_GRATE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BIT_OXIDIZED_COPPER_GRATE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.STEEL_GRATE.get(),
                        net.minecraft.client.renderer.RenderType.translucent());

                net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(
                        com.kingodogo.buildscape.block.ModBlockEntities.PILLAR_BLOCK_ENTITY
                                .get(),
                        com.kingodogo.buildscape.client.renderer.PillarBlockEntityRenderer::new);
                net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(
                        com.kingodogo.buildscape.block.ModBlockEntities.DECORATED_POT_BLOCK_ENTITY
                                .get(),
                        com.kingodogo.buildscape.client.renderer.DecoratedPotBlockEntityRenderer::new);
                net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(
                        com.kingodogo.buildscape.block.ModBlockEntities.TRAPPED_DECORATED_POT_BLOCK_ENTITY
                                .get(),
                        com.kingodogo.buildscape.client.renderer.TrappedDecoratedPotBlockEntityRenderer::new);
                net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(
                        com.kingodogo.buildscape.block.ModBlockEntities.ICICLE_CAULDRON_BLOCK_ENTITY
                                .get(),
                        com.kingodogo.buildscape.client.renderer.IcicleCauldronBlockEntityRenderer::new);

                net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(
                        com.kingodogo.buildscape.block.ModBlockEntities.FESTIVE_STOCKING_BLOCK_ENTITY
                                .get(),
                        com.kingodogo.buildscape.client.renderer.FestiveStockingBlockEntityRenderer::new);

/*
                net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(
                        com.kingodogo.buildscape.block.ModBlockEntities.MIRROR_BLOCK_ENTITY
                                .get(),
                        com.kingodogo.buildscape.client.renderer.MirrorBlockEntityRenderer::new);
*/

                net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(
                        com.kingodogo.buildscape.block.ModBlockEntities.MANGROVE_SIGN_BLOCK_ENTITY
                                .get(),
                        net.minecraft.client.renderer.blockentity.SignRenderer::new);

                net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(
                        com.kingodogo.buildscape.block.ModBlockEntities.BAMBOO_SIGN_BLOCK_ENTITY
                                .get(),
                        net.minecraft.client.renderer.blockentity.SignRenderer::new);

                net.minecraft.client.renderer.entity.EntityRenderers.register(
                        com.kingodogo.buildscape.entity.ModEntities.FALLING_ICICLE.get(),
                        com.kingodogo.buildscape.client.renderer.FallingIcicleRenderer::new);

                net.minecraft.client.renderer.entity.EntityRenderers.register(
                        com.kingodogo.buildscape.entity.ModEntities.MANGROVE_BOAT.get(),
                        com.kingodogo.buildscape.client.renderer.MangroveBoatRenderer::new);

                net.minecraft.client.renderer.entity.EntityRenderers.register(
                        com.kingodogo.buildscape.entity.ModEntities.COLORED_ITEM_FRAME.get(),
                        com.kingodogo.buildscape.client.renderer.ColoredItemFrameRenderer::new);

                net.minecraft.client.renderer.entity.EntityRenderers.register(
                        com.kingodogo.buildscape.entity.ModEntities.SEAT_ENTITY.get(),
                        net.minecraft.client.renderer.entity.NoopRenderer::new);

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MANGROVE_SIGN.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MANGROVE_WALL_SIGN.get(),
                        net.minecraft.client.renderer.RenderType.cutout());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BAMBOO_SIGN.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BAMBOO_WALL_SIGN.get(),
                        net.minecraft.client.renderer.RenderType.cutout());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ICICLE.get(),
                        net.minecraft.client.renderer.RenderType.cutout());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ICICLE_BLOCK.get(),
                        net.minecraft.client.renderer.RenderType.translucent());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PACKED_ICICLE_BLOCK.get(),
                        net.minecraft.client.renderer.RenderType.translucent());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLACK_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLUE_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BROWN_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.CYAN_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GRAY_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GREEN_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_BLUE_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_GRAY_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIME_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MAGENTA_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ORANGE_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PINK_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PURPLE_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.RED_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.WHITE_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.YELLOW_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.TRAPPED_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLACK_TRAPPED_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLUE_TRAPPED_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BROWN_TRAPPED_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.CYAN_TRAPPED_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GRAY_TRAPPED_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GREEN_TRAPPED_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_BLUE_TRAPPED_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_GRAY_TRAPPED_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIME_TRAPPED_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MAGENTA_TRAPPED_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ORANGE_TRAPPED_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PINK_TRAPPED_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PURPLE_TRAPPED_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.RED_TRAPPED_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.WHITE_TRAPPED_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.YELLOW_TRAPPED_DECORATED_POT.get(),
                        net.minecraft.client.renderer.RenderType.cutout());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BROWN_MUSHROOM_SHELVES.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.RED_MUSHROOM_SHELVES.get(),
                        net.minecraft.client.renderer.RenderType.cutout());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.RED_ROSE_VINES.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLACK_ROSE_VINES.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLUE_ROSE_VINES.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.WHITE_ROSE_VINES.get(),
                        net.minecraft.client.renderer.RenderType.cutout());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FESTIVE_STOCKING.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLACK_FESTIVE_STOCKING.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLUE_FESTIVE_STOCKING.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BROWN_FESTIVE_STOCKING.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.CYAN_FESTIVE_STOCKING.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GRAY_FESTIVE_STOCKING.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.GREEN_FESTIVE_STOCKING.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_BLUE_FESTIVE_STOCKING.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_GRAY_FESTIVE_STOCKING.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIME_FESTIVE_STOCKING.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MAGENTA_FESTIVE_STOCKING.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ORANGE_FESTIVE_STOCKING.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PINK_FESTIVE_STOCKING.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PURPLE_FESTIVE_STOCKING.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.RED_FESTIVE_STOCKING.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.WHITE_FESTIVE_STOCKING.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.YELLOW_FESTIVE_STOCKING.get(),
                        net.minecraft.client.renderer.RenderType.cutout());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BAMBOO_DOOR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BAMBOO_TRAPDOOR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MANGROVE_DOOR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MANGROVE_TRAPDOOR.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MANGROVE_PROPAGULE.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MANGROVE_ROOTS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MUDDY_MANGROVE_ROOTS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MANGROVE_LEAVES.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.RED_MONETS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLUE_MONETS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PURPLE_MONETS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.LIGHT_BLUE_MONETS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PINK_MONETS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.YELLOW_MONETS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.CLOVER.get(),
                        net.minecraft.client.renderer.RenderType.cutout());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.RED_PETAL.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLUE_PETAL.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ORANGE_PETAL.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PINK_PETAL.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PURPLE_PETAL.get(),
                        net.minecraft.client.renderer.RenderType.cutout());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.RED_SPORE_BLOSSOM.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.CYAN_SPORE_BLOSSOM.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BLUE_SPORE_BLOSSOM.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PURPLE_SPORE_BLOSSOM.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ORANGE_SPORE_BLOSSOM.get(),
                        net.minecraft.client.renderer.RenderType.cutout());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.PURPLE_PETAL.get(),
                        net.minecraft.client.renderer.RenderType.cutout());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FROST_ROSE.get(),
                        net.minecraft.client.renderer.RenderType.cutout());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.OAK_LEAF_LAYERS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SPRUCE_LEAF_LAYERS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BIRCH_LEAF_LAYERS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.JUNGLE_LEAF_LAYERS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ACACIA_LEAF_LAYERS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.DARK_OAK_LEAF_LAYERS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.AZALEA_LEAF_LAYERS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FLOWERING_AZALEA_LEAF_LAYERS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_OAK_LEAF_LAYERS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_SPRUCE_LEAF_LAYERS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_BIRCH_LEAF_LAYERS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_JUNGLE_LEAF_LAYERS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_ACACIA_LEAF_LAYERS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_DARK_OAK_LEAF_LAYERS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_MANGROVE_LEAF_LAYERS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_AZALEA_LEAF_LAYERS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_FLOWERING_AZALEA_LEAF_LAYERS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MANGROVE_LEAF_LAYERS.get(),
                        net.minecraft.client.renderer.RenderType.cutout());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.OAK_LEAF_HEDGE.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SPRUCE_LEAF_HEDGE.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.BIRCH_LEAF_HEDGE.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.JUNGLE_LEAF_HEDGE.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.ACACIA_LEAF_HEDGE.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.DARK_OAK_LEAF_HEDGE.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.AZALEA_LEAF_HEDGE.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.FLOWERING_AZALEA_LEAF_HEDGE.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_OAK_LEAF_HEDGE.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_SPRUCE_LEAF_HEDGE.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_BIRCH_LEAF_HEDGE.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_JUNGLE_LEAF_HEDGE.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_ACACIA_LEAF_HEDGE.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_DARK_OAK_LEAF_HEDGE.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_MANGROVE_LEAF_HEDGE.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_AZALEA_LEAF_HEDGE.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.SNOWY_FLOWERING_AZALEA_LEAF_HEDGE.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MANGROVE_LEAF_HEDGE.get(),
                        net.minecraft.client.renderer.RenderType.cutoutMipped());
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.CASCADE_BLOCK.get(),
                        net.minecraft.client.renderer.RenderType.translucent()
                );
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.CASCADE_BLOCK_NO_MIST.get(),
                        net.minecraft.client.renderer.RenderType.translucent()
                );

                net.minecraft.client.color.block.BlockColors blockColors = net.minecraft.client.Minecraft
                        .getInstance().getBlockColors();
                blockColors.register(
                        (state, reader, pos, tintIndex) -> {
                            if (reader == null || pos == null) {
                                return 0x7FA832;
                            }
                            if (tintIndex == 1) {
                                if (reader instanceof net.minecraft.world.level.LevelReader levelReader) {
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
                                if (reader instanceof net.minecraft.world.level.LevelReader levelReader) {
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
                                    com.kingodogo.buildscape.block.OrnamentBlock.STRING_COLOR)) {
                                com.kingodogo.buildscape.block.OrnamentBlock.StringColor stringColor = state
                                        .getValue(
                                                com.kingodogo.buildscape.block.OrnamentBlock.STRING_COLOR);
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
                                    com.kingodogo.buildscape.block.StringLightBlock.STRING_COLOR)) {
                                com.kingodogo.buildscape.block.StringLightBlock.StringColor stringColor = state
                                        .getValue(
                                                com.kingodogo.buildscape.block.StringLightBlock.STRING_COLOR);
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
                                    && reader instanceof net.minecraft.world.level.LevelReader levelReader) {
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
                        ModBlocks.FLOWERING_AZALEA_LEAF_HEDGE.get());

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
                                    && reader instanceof net.minecraft.world.level.LevelReader levelReader) {
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
                                    && reader instanceof net.minecraft.world.level.LevelReader levelReader) {
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
                                return net.minecraft.client.renderer.BiomeColors.getAverageWaterColor(reader, pos);
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
                                return net.minecraft.client.renderer.BiomeColors.getAverageWaterColor(reader, pos);
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
                                net.minecraft.world.level.block.entity.BlockEntity be = reader
                                        .getBlockEntity(pos);
                                if (be instanceof com.kingodogo.buildscape.block.GlowLightsBlockEntity glowBE) {
                                    java.util.List<String> dyeColors = glowBE
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

                net.minecraft.client.color.item.ItemColors itemColors = net.minecraft.client.Minecraft
                        .getInstance().getItemColors();
                net.minecraft.client.color.item.ItemColors vanillaItemColors = net.minecraft.client.Minecraft
                        .getInstance().getItemColors();

                itemColors.register(
                        (stack, tintIndex) -> tintIndex == 0 ? 0x3F76E4 : -1,
                        ModItems.CASCADE_BLOCK.get()
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

                            net.minecraft.world.item.Item item = stack.getItem();
                            if (item instanceof net.minecraft.world.item.BlockItem) {
                                net.minecraft.world.level.block.Block block = ((net.minecraft.world.item.BlockItem) item)
                                        .getBlock();

                                int color = -1;
                                if (block == ModBlocks.OAK_LEAF_HEDGE.get()
                                        || block == ModBlocks.OAK_LEAF_LAYERS
                                        .get()) {
                                    color = vanillaItemColors.getColor(
                                            new net.minecraft.world.item.ItemStack(
                                                    net.minecraft.world.item.Items.OAK_LEAVES),
                                            0);
                                } else if (block == ModBlocks.JUNGLE_LEAF_HEDGE.get()
                                        || block == ModBlocks.JUNGLE_LEAF_LAYERS
                                        .get()) {
                                    color = vanillaItemColors.getColor(
                                            new net.minecraft.world.item.ItemStack(
                                                    net.minecraft.world.item.Items.JUNGLE_LEAVES),
                                            0);
                                } else if (block == ModBlocks.ACACIA_LEAF_HEDGE.get()
                                        || block == ModBlocks.ACACIA_LEAF_LAYERS
                                        .get()) {
                                    color = vanillaItemColors.getColor(
                                            new net.minecraft.world.item.ItemStack(
                                                    net.minecraft.world.item.Items.ACACIA_LEAVES),
                                            0);
                                } else if (block == ModBlocks.DARK_OAK_LEAF_HEDGE.get()
                                        || block == ModBlocks.DARK_OAK_LEAF_LAYERS
                                        .get()) {
                                    color = vanillaItemColors.getColor(
                                            new net.minecraft.world.item.ItemStack(
                                                    net.minecraft.world.item.Items.DARK_OAK_LEAVES),
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
            });
        }

    }

    @Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents {

        @SubscribeEvent
        public static void onModelBake(
                net.minecraftforge.client.event.ModelBakeEvent event) {
            java.util.Set<net.minecraft.resources.ResourceLocation> leafHedgeModels = new java.util.HashSet<>();
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/oak_leaf_hedge_post"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/oak_leaf_hedge_side"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/oak_leaf_hedge_side_tall"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/oak_leaf_hedge_inventory"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/spruce_leaf_hedge_post"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/spruce_leaf_hedge_side"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/spruce_leaf_hedge_side_tall"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/spruce_leaf_hedge_inventory"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/birch_leaf_hedge_post"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/birch_leaf_hedge_side"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/birch_leaf_hedge_side_tall"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/birch_leaf_hedge_inventory"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/jungle_leaf_hedge_post"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/jungle_leaf_hedge_side"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/jungle_leaf_hedge_side_tall"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/jungle_leaf_hedge_inventory"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/acacia_leaf_hedge_post"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/acacia_leaf_hedge_side"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/acacia_leaf_hedge_side_tall"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/acacia_leaf_hedge_inventory"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/dark_oak_leaf_hedge_post"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/dark_oak_leaf_hedge_side"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/dark_oak_leaf_hedge_side_tall"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/dark_oak_leaf_hedge_inventory"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/azalea_leaf_hedge_post"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/azalea_leaf_hedge_side"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/azalea_leaf_hedge_side_tall"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/azalea_leaf_hedge_inventory"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/flowering_azalea_leaf_hedge_post"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/flowering_azalea_leaf_hedge_side"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/flowering_azalea_leaf_hedge_side_tall"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "block/flowering_azalea_leaf_hedge_inventory"));

            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "item/oak_leaf_hedge"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "item/spruce_leaf_hedge"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "item/birch_leaf_hedge"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "item/jungle_leaf_hedge"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "item/acacia_leaf_hedge"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "item/dark_oak_leaf_hedge"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "item/azalea_leaf_hedge"));
            leafHedgeModels.add(
                    new net.minecraft.resources.ResourceLocation(
                            BuildScape.MODID,
                            "item/flowering_azalea_leaf_hedge"));

            int wrappedCount = 0;
            int notFoundCount = 0;
            for (net.minecraft.resources.ResourceLocation modelLocation : leafHedgeModels) {
                net.minecraft.client.resources.model.BakedModel originalModel = event
                        .getModelRegistry()
                        .get(modelLocation);
                if (originalModel != null) {
                    event
                            .getModelRegistry()
                            .put(
                                    modelLocation,
                                    new com.kingodogo.buildscape.client.model.TintedLeafHedgeModel(
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
                net.minecraftforge.client.event.ParticleFactoryRegisterEvent event) {
            net.minecraft.client.Minecraft.getInstance().particleEngine.register(
                    com.kingodogo.buildscape.particle.ModParticles.GLOW_LIME_SPARKLE.get(),
                    sprites -> new com.kingodogo.buildscape.particle.PillarSparkleParticle.Provider(
                            sprites));

            net.minecraft.client.Minecraft.getInstance().particleEngine.register(
                    com.kingodogo.buildscape.particle.ModParticles.TINTED_DRIP_FALL.get(),
                    sprites -> new com.kingodogo.buildscape.particle.TintedDripParticle.Provider(
                            sprites));

            net.minecraft.client.Minecraft.getInstance().particleEngine.register(
                    com.kingodogo.buildscape.particle.ModParticles.TINTED_SPORE.get(),
                    sprites -> new com.kingodogo.buildscape.particle.TintedSporeParticle.Provider(
                            sprites));

            net.minecraft.client.Minecraft.getInstance().particleEngine.register(
                    com.kingodogo.buildscape.particle.ModParticles.SNOWFLAKE.get(),
                    sprites -> new com.kingodogo.buildscape.particle.SnowflakeParticle.Provider(
                            sprites));

            net.minecraft.client.Minecraft.getInstance().particleEngine.register(
                    com.kingodogo.buildscape.particle.ModParticles.SNOWFLAKE_STILL.get(),
                    sprites -> new com.kingodogo.buildscape.particle.SnowflakeStillParticle.Provider(
                            sprites));

            net.minecraft.client.Minecraft.getInstance().particleEngine.register(
                    com.kingodogo.buildscape.particle.ModParticles.CONFETTI.get(),
                    sprites -> new com.kingodogo.buildscape.particle.ConfettiParticle.Provider(
                            sprites));

            net.minecraft.client.Minecraft.getInstance().particleEngine.register(
                    com.kingodogo.buildscape.particle.ModParticles.BUBBLE.get(),
                    sprites -> new com.kingodogo.buildscape.particle.BubbleParticle.Provider(
                            sprites));

            net.minecraft.client.Minecraft.getInstance().particleEngine.register(
                    com.kingodogo.buildscape.particle.ModParticles.TRAIL_NOTE.get(),
                    sprites -> new com.kingodogo.buildscape.particle.TrailNoteParticle.Provider(
                            sprites));

            net.minecraft.client.Minecraft.getInstance().particleEngine.register(
                    com.kingodogo.buildscape.particle.ModParticles.COLORED_SMOKE.get(),
                    sprites -> new com.kingodogo.buildscape.particle.ColoredSmokeParticle.Provider(
                            sprites));


            net.minecraft.client.Minecraft.getInstance().particleEngine.register(
                    com.kingodogo.buildscape.particle.ModParticles.TINTABLE_HEART.get(),
                    sprites -> new com.kingodogo.buildscape.particle.TintableHeartParticle.Provider(
                            sprites));

            net.minecraft.client.Minecraft.getInstance().particleEngine.register(
                    com.kingodogo.buildscape.particle.ModParticles.CAKE.get(),
                    sprites -> new com.kingodogo.buildscape.particle.CakeParticle.Provider(
                            sprites));

            net.minecraft.client.Minecraft.getInstance().particleEngine.register(
                    com.kingodogo.buildscape.particle.ModParticles.CHERRY.get(),
                    sprites -> new com.kingodogo.buildscape.particle.CherryParticle.Provider(
                            sprites));

            net.minecraft.client.Minecraft.getInstance()
                    .particleEngine.register(
                            com.kingodogo.buildscape.particle.ModParticles.CASCADE.get(),
                            sprites ->
                                    new com.kingodogo.buildscape.particle.CascadeParticle.Provider(
                                            sprites
                                    )
                    );


        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class UniversalCosmeticClientEvents {
        @SubscribeEvent
        public static void registerLayerDefinitions(net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(com.kingodogo.buildscape.client.model.BuildersHatModel.LAYER_LOCATION, com.kingodogo.buildscape.client.model.BuildersHatModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void registerLayers(net.minecraftforge.client.event.EntityRenderersEvent.AddLayers event) {
            String[] skins = {"default", "slim"};
            for (String skinName : skins) {
                net.minecraft.client.renderer.entity.player.PlayerRenderer renderer = event.getSkin(skinName);
                if (renderer != null) {
                    renderer.addLayer(new com.kingodogo.buildscape.client.renderer.layer.CosmeticLayer(renderer, event.getEntityModels()));
                }
            }
        }
    }
}
