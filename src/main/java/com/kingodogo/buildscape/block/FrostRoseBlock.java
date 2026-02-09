package com.kingodogo.buildscape.block;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
        if (!level.isClientSide && entity instanceof LivingEntity livingEntity) {
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
        if (!level.isClientSide && entity instanceof LivingEntity livingEntity) {
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
}
