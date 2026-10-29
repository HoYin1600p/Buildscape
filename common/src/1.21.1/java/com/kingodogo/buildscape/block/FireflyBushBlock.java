package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraft.util.RandomSource;

public class FireflyBushBlock extends BushBlock {
    public static final com.mojang.serialization.MapCodec<FireflyBushBlock> CODEC = simpleCodec(FireflyBushBlock::new);

    @Override
    protected com.mojang.serialization.MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    protected static final VoxelShape SHAPE = box(2.0D, 0.0D, 2.0D, 14.0D, 14.0D, 14.0D);

    public FireflyBushBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        boolean isNight = !level.isDay();
        boolean noSkylight = level.getBrightness(LightLayer.SKY, pos) == 0;
        if (!(isNight || noSkylight) || random.nextFloat() >= 0.70F) {
            return;
        }
        for (int i = 0; i < 10; i++) {
            BlockPos targetPos = pos.offset(random.nextInt(11) - 5, random.nextInt(6), random.nextInt(11) - 5);
            if (level.getBlockState(targetPos).isAir()) {
                level.addParticle(
                        ModParticles.FIREFLY.get(),
                        targetPos.getX() + random.nextDouble(),
                        targetPos.getY() + random.nextDouble(),
                        targetPos.getZ() + random.nextDouble(),
                        (random.nextFloat() - 0.5F) * 0.01D,
                        (random.nextFloat() - 0.5F) * 0.005D,
                        (random.nextFloat() - 0.5F) * 0.01D);
                return;
            }
        }
    }

    @Override
    public net.minecraft.world.ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (held.is(Items.BONE_MEAL)) {
            if (!level.isClientSide) {
                popResource(level, pos, new ItemStack(this.asItem()));
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                level.levelEvent(1505, pos, 0);
            }
            return net.minecraft.world.ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
