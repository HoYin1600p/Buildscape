package com.kingodogo.buildscape.setup;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.sign.ModWoodTypes;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.feature.copper.CopperRodHandler;
import com.kingodogo.buildscape.network.ModMessages;
import com.kingodogo.buildscape.network.SyncConfigPacket;
import com.kingodogo.buildscape.registry.ModBlocks;
import com.kingodogo.buildscape.registry.ModItems;
import com.kingodogo.buildscape.registry.ModParticles;
import com.kingodogo.buildscape.registry.ModSounds;
import com.kingodogo.buildscape.registry.ModStats;
import com.kingodogo.buildscape.world.ModGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Common (client and server) mod setup, run from {@code FMLCommonSetupEvent}.
 */
public final class CommonSetup {

    private static final AtomicBoolean CONFIG_CALLBACK_REGISTERED = new AtomicBoolean();

    private CommonSetup() {}

    public static void onCommonSetup(final FMLCommonSetupEvent event) {
        // Potion and cake stack sizes are decided per world by the isWaterbottleStack / isCakeStack
        // game rules through ItemMixin (ItemStack.getMaxStackSize). The Item field stays vanilla (1) so
        // /give and other Item-level callers add single items that merge under those rules.
        event.enqueueWork(() -> {
            registerCoreSystems();
            registerPillarConfigCallback();
            registerMusicDiscs();
        });

        event.enqueueWork(CommonSetup::moveVerticalSlabsToVanillaTabs);

        event.enqueueWork(CommonSetup::registerDispenserBehaviours);

        event.enqueueWork(() -> {
            initialiseSoundsAndWoodTypes();
            registerFlowerPots();
            registerCompostables();
            registerCauldronInteractions();
        });
    }

    private static void registerCoreSystems() {
        ModGameRules.register();
        ModStats.registerStats();
        ModMessages.register();
    }

    private static void registerPillarConfigCallback() {
        if (CONFIG_CALLBACK_REGISTERED.compareAndSet(false, true)) {
            PillarParticleConfig.addConfigReloadCallback(
                    CommonSetup::onPillarConfigReload);
        }
    }

    private static void registerMusicDiscs() {
        try {
            Map<SoundEvent, RecordItem> recordMap = ObfuscationReflectionHelper.getPrivateValue(
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
            // reflection on private vanilla members
            BuildScape.LOGGER.error("Failed to register custom records into RecordItem.BY_NAME", e);
        }
    }

    private static void moveVerticalSlabsToVanillaTabs() {
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
                        // reflection on private vanilla members
                        BuildScape.LOGGER.error("Failed to change creative tab for vertical slab: {}", path, e);
                    }
                }
            }
        }
    }

    private static void registerDispenserBehaviours() {
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
                });

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
                                } else if (matchSlot == -1 && !itemInSlot.isEmpty()
                                        && itemInSlot.getItem() == stack.getItem()) {
                                    matchSlot = i;
                                }
                            }
                            if (matchSlot != -1) {
                                slotIndex = matchSlot;
                            }
                        }

                        double yawOffset = (slotIndex - 4) * 0.15;

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
                        if (len > 0.0001) {
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
                            double speedMultiplier = 1.0 + (burstLevel - 1) * 0.2;
                            double spreadMultiplier = 1.0 + (burstLevel - 1) * 0.1;

                            for (int i = 0; i < particleCount; i++) {
                                double speed = (0.08 + rand.nextDouble() * 0.12) * speedMultiplier;
                                double spread = (0.04 + rand.nextDouble() * 0.06) * spreadMultiplier;

                                double vx = dirX * speed + (rand.nextDouble() - 0.5) * spread;
                                double vy = dirY * speed + (rand.nextDouble() - 0.5) * spread
                                        + (facing.getAxis().isHorizontal() ? 0.05 : 0.0);
                                double vz = dirZ * speed + (rand.nextDouble() - 0.5) * spread;

                                double px = startX + (rand.nextDouble() - 0.5) * 0.15;
                                double py = startY + (rand.nextDouble() - 0.5) * 0.15;
                                double pz = startZ + (rand.nextDouble() - 0.5) * 0.15;

                                serverLevel.sendParticles(
                                        (SimpleParticleType) ModParticles.CONFETTI.get(),
                                        px, py, pz, 0, vx, vy, vz, 1.0);
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
                });
    }

    private static void initialiseSoundsAndWoodTypes() {
        CopperRodHandler.registerPoi();
        ModSounds.COPPER_GRATE_SOUNDS();
        ModSounds.COPPER_BULB_SOUNDS();
        ModSounds.MANGROVE_ROOTS_SOUNDS();
        ModSounds.MUDDY_MANGROVE_ROOTS_SOUNDS();

        PillarParticleConfig.get();

        ModWoodTypes.MANGROVE.getClass();
        ModWoodTypes.BAMBOO.getClass();
    }

    private static void registerFlowerPots() {
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
    }

    private static void registerCompostables() {
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
    }

    private static void registerCauldronInteractions() {
        CauldronInteraction.EMPTY.put(ModItems.EXPERIENCE_BUCKET.get(),
                (state, level, pos, player, hand, stack) -> {
                    if (!level.isClientSide) {
                        player.awardStat(Stats.FILL_CAULDRON);
                        level.setBlockAndUpdate(pos, ModBlocks.EXPERIENCE_CAULDRON.get().defaultBlockState()
                                .setValue(LayeredCauldronBlock.LEVEL, 3));
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
                level.setBlockAndUpdate(pos, ModBlocks.EXPERIENCE_CAULDRON.get().defaultBlockState()
                        .setValue(LayeredCauldronBlock.LEVEL, 1));
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

        BuildScape.EXPERIENCE_CAULDRON_INTERACTIONS.put(Items.BUCKET, (state, level, pos, player, hand, stack) -> {
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

        BuildScape.EXPERIENCE_CAULDRON_INTERACTIONS.put(Items.GLASS_BOTTLE,
                (state, level, pos, player, hand, stack) -> {
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

        BuildScape.EXPERIENCE_CAULDRON_INTERACTIONS.put(Items.EXPERIENCE_BOTTLE,
                (state, level, pos, player, hand, stack) -> {
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

        BuildScape.EXPERIENCE_CAULDRON_INTERACTIONS.put(ModItems.EXPERIENCE_BUCKET.get(),
                (state, level, pos, player, hand, stack) -> {
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
}
