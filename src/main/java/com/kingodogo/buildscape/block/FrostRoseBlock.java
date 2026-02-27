package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrostRoseBlock extends BushBlock implements BonemealableBlock {

    protected static final VoxelShape SHAPE = Block.box(
            5.0D,
            0.0D,
            5.0D,
            11.0D,
            10.0D,
            11.0D
    );

    private static final Map<String, Long> entityDamageStart = new HashMap<>();
    private static final Map<String, Long> entityLastDamage = new HashMap<>();

    public FrostRoseBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return Shapes.empty();
    }

    @Override
    protected boolean mayPlaceOn(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return (
                state.is(BlockTags.DIRT) ||
                        state.is(Blocks.GRASS_BLOCK) ||
                        state.is(Blocks.ICE) ||
                        state.is(Blocks.PACKED_ICE) ||
                        state.is(Blocks.BLUE_ICE) ||
                        state.is(Blocks.SNOW) ||
                        state.is(Blocks.SNOW_BLOCK) ||
                        state.is(Blocks.POWDER_SNOW) ||
                        state.is(com.kingodogo.buildscape.block.ModBlocks.SNOW_BRICKS.get())
        );
    }

    @Override
    public boolean isValidBonemealTarget(
            BlockGetter level,
            BlockPos pos,
            BlockState state,
            boolean isClient
    ) {
        return false;
    }

    @Override
    public boolean isBonemealSuccess(
            Level level,
            java.util.Random random,
            BlockPos pos,
            BlockState state
    ) {
        return false;
    }

    @Override
    public void performBonemeal(
            ServerLevel level,
            java.util.Random random,
            BlockPos pos,
            BlockState state
    ) {
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        return this.mayPlaceOn(belowState, level, belowPos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (
                state != null &&
                        this.canSurvive(state, context.getLevel(), context.getClickedPos())
        ) {
            return state;
        }
        return null;
    }

    @Override
    public void stepOn(
            Level level,
            BlockPos pos,
            BlockState state,
            Entity entity
    ) {
        if (!level.isClientSide && entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            String key =
                    pos.getX() +
                            "," +
                            pos.getY() +
                            "," +
                            pos.getZ() +
                            "," +
                            entity.getUUID();

            if (!entityDamageStart.containsKey(key)) {
                long currentTime = level.getGameTime();
                entityDamageStart.put(key, currentTime);
                entityLastDamage.put(key, currentTime);

                DamageSource freezeDamage = DamageSource.GENERIC;
                livingEntity.hurt(freezeDamage, 1.0F);

                level.playSound(
                        null,
                        pos,
                        net.minecraft.sounds.SoundEvents.POWDER_SNOW_STEP,
                        net.minecraft.sounds.SoundSource.BLOCKS,
                        0.5f,
                        1.0f
                );

                if (livingEntity.canFreeze()) {
                    livingEntity.setTicksFrozen(
                            Math.min(livingEntity.getTicksFrozen() + 140, 300)
                    );
                }
            }
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void entityInside(
            BlockState state,
            Level level,
            BlockPos pos,
            Entity entity
    ) {
        if (!level.isClientSide && entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            String key =
                    pos.getX() +
                            "," +
                            pos.getY() +
                            "," +
                            pos.getZ() +
                            "," +
                            entity.getUUID();

            long currentTime = level.getGameTime();
            Long startTime = entityDamageStart.get(key);
            Long lastDamageTime = entityLastDamage.get(key);

            if (startTime == null) {
                entityDamageStart.put(key, currentTime);
                entityLastDamage.put(key, currentTime);

                DamageSource freezeDamage = DamageSource.GENERIC;
                livingEntity.hurt(freezeDamage, 1.0F);

                level.playSound(
                        null,
                        pos,
                        net.minecraft.sounds.SoundEvents.POWDER_SNOW_STEP,
                        net.minecraft.sounds.SoundSource.BLOCKS,
                        0.5f,
                        1.0f
                );

                if (livingEntity.canFreeze()) {
                    livingEntity.setTicksFrozen(
                            Math.min(livingEntity.getTicksFrozen() + 140, 300)
                    );
                }
            } else {
                long elapsedTicks = currentTime - startTime;

                int damageDuration = 40;

                if (elapsedTicks < damageDuration) {
                    long ticksSinceLastDamage = currentTime - lastDamageTime;
                    if (ticksSinceLastDamage >= 20) {
                        DamageSource freezeDamage = DamageSource.GENERIC;
                        livingEntity.hurt(freezeDamage, 1.0F);

                        level.playSound(
                                null,
                                pos,
                                net.minecraft.sounds.SoundEvents.POWDER_SNOW_STEP,
                                net.minecraft.sounds.SoundSource.BLOCKS,
                                0.5f,
                                1.0f
                        );

                        if (livingEntity.canFreeze()) {
                            livingEntity.setTicksFrozen(
                                    Math.min(livingEntity.getTicksFrozen() + 140, 300)
                            );
                        }

                        entityLastDamage.put(key, currentTime);
                    }
                } else {
                    entityDamageStart.remove(key);
                    entityLastDamage.remove(key);
                }
            }
        }
    }

    @Override
    public VoxelShape getVisualShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return Shapes.empty();
    }

    @Override
    public float getShadeBrightness(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(
            BlockState state,
            BlockGetter reader,
            BlockPos pos
    ) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    @Override
    public boolean skipRendering(
            BlockState state,
            BlockState adjacentState,
            Direction side
    ) {
        return (
                adjacentState.is(this) || super.skipRendering(state, adjacentState, side)
        );
    }

    @Override
    public List<ItemStack> getDrops(
            BlockState state,
            LootContext.Builder builder
    ) {
        return Collections.singletonList(new ItemStack(this));
    }

    @net.minecraftforge.api.distmarker.OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, java.util.Random random) {
        super.animateTick(state, level, pos, random);

        // Check for 5 Frost Roses in a 7x7x7 block radius (so offset by 3)
        int radius = 3;
        int count = 0;
        
        for (BlockPos p : BlockPos.betweenClosed(pos.offset(-radius, -radius, -radius), pos.offset(radius, radius, radius))) {
            if (level.getBlockState(p).is(this)) {
                count++;
            }
        }

        // Target "perfect" rate: A 7x7 field (49 roses) with a 1/15 chance.
        // This calculates the exact relative probability so ANY size cluster
        // generates the exact same total amount of snow per second.
        double normalizedProbability = (49.0 / 15.0) / count;

        if (count >= 5 && random.nextDouble() < normalizedProbability) {
            // Mostly 15-20 particles, but sometimes randomly higher up to 50
            int particlesToSpawn = 15 + random.nextInt(6); // 15 to 20
            if (random.nextDouble() < 0.20) { // 20% chance to be higher
                particlesToSpawn = 21 + random.nextInt(30); // 21 to 50
            }
            
            // Spawn snowfall over an 80 block diameter (40 block radius)
            double maxRadius = 40.0;
            for (int i = 0; i < particlesToSpawn; i++) {
                // Uniformly distribute in the circle
                double r = maxRadius * Math.sqrt(random.nextDouble());
                double angle = random.nextDouble() * Math.PI * 2;

                double spawnX = pos.getX() + 0.5 + Math.cos(angle) * r;
                double spawnZ = pos.getZ() + 0.5 + Math.sin(angle) * r;

                // Spawn above the area so it falls down
                double spawnY = pos.getY() + 10.0 + random.nextDouble() * 15.0;

                double xSpeed = (random.nextDouble() - 0.5) * 0.1;
                double ySpeed = -0.05 - random.nextDouble() * 0.05;
                double zSpeed = (random.nextDouble() - 0.5) * 0.1;

                level.addParticle(
                        com.kingodogo.buildscape.particle.ModParticles.SNOWFLAKE.get(),
                        spawnX, spawnY, spawnZ,
                        xSpeed, ySpeed, zSpeed
                );
            }
        }
    }
}
