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
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Random;

public class FireflyBushBlock extends BushBlock {
    protected static final VoxelShape SHAPE = box(2.0D, 0.0D, 2.0D, 14.0D, 14.0D, 14.0D);

    public FireflyBushBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        int effectiveLight = Math.max(blockLight, level.isNight() ? Math.max(0, skyLight - 11) : skyLight);

        if (effectiveLight <= 13) {
            // Local spawning inside/very close to the bush
            if (random.nextInt(2) == 0) {
                double x = (double) pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 1.0D;
                double y = (double) pos.getY() + 0.2D + random.nextDouble() * 1.0D;
                double z = (double) pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 1.0D;

                double xSpeed = (random.nextFloat() - 0.5F) * 0.02D;
                double ySpeed = random.nextFloat() * 0.015D;
                double zSpeed = (random.nextFloat() - 0.5F) * 0.02D;

                level.addParticle(ModParticles.FIREFLY.get(), x, y, z, xSpeed, ySpeed, zSpeed);
            }

            // Ambient/wider volume spawning (similar to Spore Blossom)
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            for (int i = 0; i < 8; i++) {
                int dx = random.nextInt(17) - 8; // X range [-8, 8]
                int dy = random.nextInt(7) - 2;  // Y range [-2, 4]
                int dz = random.nextInt(17) - 8; // Z range [-8, 8]
                mutablePos.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                BlockState targetState = level.getBlockState(mutablePos);
                if (!targetState.isCollisionShapeFullBlock(level, mutablePos)) {
                    double x = (double) mutablePos.getX() + random.nextDouble();
                    double y = (double) mutablePos.getY() + random.nextDouble();
                    double z = (double) mutablePos.getZ() + random.nextDouble();

                    double xSpeed = (random.nextFloat() - 0.5F) * 0.01D;
                    double ySpeed = (random.nextFloat() - 0.5F) * 0.005D;
                    double zSpeed = (random.nextFloat() - 0.5F) * 0.01D;

                    level.addParticle(ModParticles.FIREFLY.get(), x, y, z, xSpeed, ySpeed, zSpeed);
                }
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack held = player.getItemInHand(hand);
        if (held.is(Items.BONE_MEAL)) {
            if (!level.isClientSide) {
                popResource(level, pos, new ItemStack(this.asItem()));
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                level.levelEvent(2005, pos, 0);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.use(state, level, pos, player, hand, hitResult);
    }
}
