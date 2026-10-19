package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.block.CloverBlock;
import com.kingodogo.buildscape.block.CreakingHeartBlock;
import com.kingodogo.buildscape.block.CustomSoundType;
import com.kingodogo.buildscape.block.HangingMossBlock;
import com.kingodogo.buildscape.block.HazeBushBlock;
import com.kingodogo.buildscape.block.LogStrippingHandler;
import com.kingodogo.buildscape.block.ModBlockProperties;
import com.kingodogo.buildscape.block.PetalBlock;
import com.kingodogo.buildscape.block.PottedHazeBushBlock;
import com.kingodogo.buildscape.block.RoseVinesBlock;
import com.kingodogo.buildscape.feature.copper.CopperOxidationHandler;
import com.kingodogo.buildscape.item.HazeBushItem;
import com.kingodogo.buildscape.registry.ModBlocks;
import com.kingodogo.buildscape.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockInteractionEvents {

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
}
