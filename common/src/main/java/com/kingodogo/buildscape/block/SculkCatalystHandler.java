package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.Random;

public class SculkCatalystHandler {

    public static void onMobKilledNearCatalyst(Level level, BlockPos catalystPos, BlockPos deathPos, LivingEntity entity) {
        if (level.isClientSide()) return;

        BlockState catalystState = level.getBlockState(catalystPos);
        if (catalystState.hasProperty(SculkCatalystBlock.BLOOM)) {
            level.setBlock(catalystPos, catalystState.setValue(SculkCatalystBlock.BLOOM, true), 3);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.scheduleTick(catalystPos, catalystState.getBlock(), 40);
            }
        }

        Services.PLATFORM.playBlockSound(level, catalystPos, new com.kingodogo.buildscape.util.CommonId("minecraft", "entity.soul.escape"));

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    catalystPos.getX() + 0.5, catalystPos.getY() + 1.1, catalystPos.getZ() + 0.5,
                    20, 0.3, 0.3, 0.3, 0.05);

            serverLevel.sendParticles(ParticleTypes.SOUL,
                    deathPos.getX() + 0.5, deathPos.getY() + 0.5, deathPos.getZ() + 0.5,
                    15, 0.4, 0.4, 0.4, 0.02);
        }

        Random rand = new Random();
        int spreadRadius = 4;
        for (BlockPos targetPos : BlockPos.betweenClosed(deathPos.offset(-spreadRadius, -2, -spreadRadius), deathPos.offset(spreadRadius, 2, spreadRadius))) {
            if (targetPos.distSqr(deathPos) > (spreadRadius * spreadRadius)) continue;

            BlockState targetState = level.getBlockState(targetPos);
            Block targetBlock = targetState.getBlock();

            if (isConvertibleToSculk(targetBlock)) {
                if (rand.nextFloat() < 0.65F) {
                    Block sculkBlock = ModBlocks.SCULK != null && ModBlocks.SCULK.get() != null ? ModBlocks.SCULK.get().getBlock() : null;
                    if (sculkBlock != null) {
                        level.setBlock(targetPos, sculkBlock.defaultBlockState(), 3);
                    }

                    float rareRoll = rand.nextFloat();
                    BlockPos abovePos = targetPos.above();
                    if (level.isEmptyBlock(abovePos)) {
                        if (rareRoll < 0.04F) {
                            Block shrieker = ModBlocks.SCULK_SHRIEKER != null && ModBlocks.SCULK_SHRIEKER.get() != null ? ModBlocks.SCULK_SHRIEKER.get().getBlock() : null;
                            if (shrieker != null) {
                                level.setBlock(abovePos, shrieker.defaultBlockState(), 3);
                            }
                        } else if (rareRoll < 0.12F) {
                            Block sensor = ModBlocks.SCULK_SENSOR != null && ModBlocks.SCULK_SENSOR.get() != null ? ModBlocks.SCULK_SENSOR.get().getBlock() : null;
                            if (sensor != null) {
                                level.setBlock(abovePos, sensor.defaultBlockState(), 3);
                            }
                        } else if (rand.nextFloat() < 0.40F) {
                            tryPlaceSculkVein(level, abovePos, rand);
                        }
                    }

                    for (Direction dir : Direction.values()) {
                        if (rand.nextFloat() < 0.25F) {
                            BlockPos adjPos = targetPos.relative(dir);
                            tryPlaceSculkVein(level, adjPos, rand);
                        }
                    }
                }
            }
        }
    }

    private static void tryPlaceSculkVein(Level level, BlockPos pos, Random rand) {
        Block sculkVein = ModBlocks.SCULK_VEIN != null && ModBlocks.SCULK_VEIN.get() != null ? ModBlocks.SCULK_VEIN.get().getBlock() : null;
        if (sculkVein == null) return;

        BlockState currentState = level.getBlockState(pos);
        if (!currentState.isAir() && !Services.PLATFORM.isReplaceable(currentState) && !currentState.is(sculkVein)) {
            return;
        }

        BlockState veinState = currentState.is(sculkVein) ? currentState : sculkVein.defaultBlockState();
        boolean placedAnyFace = false;

        for (Direction dir : Direction.values()) {
            BooleanProperty faceProp = MultifaceBlock.getFaceProperty(dir);
            if (veinState.hasProperty(faceProp) && veinState.getValue(faceProp)) {
                placedAnyFace = true;
                continue;
            }

            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);

            if (neighborState.isFaceSturdy(level, neighborPos, dir.getOpposite())) {
                if (veinState.hasProperty(faceProp)) {
                    veinState = veinState.setValue(faceProp, true);
                    placedAnyFace = true;
                }
            }
        }

        if (placedAnyFace) {
            level.setBlock(pos, veinState, 3);
        }
    }

    private static boolean isConvertibleToSculk(Block block) {
        return block == Blocks.STONE || block == Blocks.DEEPSLATE || block == Blocks.DIRT ||
               block == Blocks.GRASS_BLOCK || block == Blocks.PODZOL || block == Blocks.COARSE_DIRT ||
               block == Blocks.ROOTED_DIRT || block == Blocks.SAND ||
               block == Blocks.RED_SAND || block == Blocks.GRAVEL || block == Blocks.GRANITE ||
               block == Blocks.DIORITE || block == Blocks.ANDESITE || block == Blocks.TUFF ||
               block == Blocks.TERRACOTTA || block == Blocks.NETHERRACK || block == Blocks.END_STONE ||
               block == Blocks.BASALT || block == Blocks.BLACKSTONE || block == Blocks.MOSS_BLOCK;
    }
}
