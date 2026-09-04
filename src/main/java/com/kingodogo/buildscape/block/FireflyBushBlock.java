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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
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
    @SuppressWarnings("deprecation")
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;
            boolean isNight = !serverLevel.isDay();
            boolean noSkylight = serverLevel.getBrightness(LightLayer.SKY, pos) == 0;
            if (isNight || noSkylight) {
                double x = pos.getX() + 0.5D;
                double y = pos.getY() + 0.5D;
                double z = pos.getZ() + 0.5D;
                double xSpeed = (serverLevel.random.nextFloat() - 0.5F) * 0.01D;
                double ySpeed = (serverLevel.random.nextFloat() - 0.5F) * 0.005D;
                double zSpeed = (serverLevel.random.nextFloat() - 0.5F) * 0.01D;
                serverLevel.sendParticles(ModParticles.FIREFLY.get(), x, y, z, 0, xSpeed, ySpeed, zSpeed, 1.0D);
            }

            serverLevel.scheduleTick(pos, this, 1);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        super.tick(state, level, pos, random);

        if (!hasPlayerNearby(level, pos)) {
            level.scheduleTick(pos, this, 20);
            return;
        }

        boolean isNight = !level.isDay();
        boolean noSkylight = level.getBrightness(LightLayer.SKY, pos) == 0;
        if (isNight || noSkylight) {
            if (random.nextFloat() < 0.70F) {
                for (int i = 0; i < 10; i++) {
                    int dx = random.nextInt(11) - 5;
                    int dy = random.nextInt(6);
                    int dz = random.nextInt(11) - 5;
                    BlockPos targetPos = pos.offset(dx, dy, dz);
                    if (level.getBlockState(targetPos).isAir()) {
                        double x = targetPos.getX() + random.nextDouble();
                        double y = targetPos.getY() + random.nextDouble();
                        double z = targetPos.getZ() + random.nextDouble();

                        double xSpeed = (random.nextFloat() - 0.5F) * 0.01D;
                        double ySpeed = (random.nextFloat() - 0.5F) * 0.005D;
                        double zSpeed = (random.nextFloat() - 0.5F) * 0.01D;

                        level.sendParticles(ModParticles.FIREFLY.get(), x, y, z, 0, xSpeed, ySpeed, zSpeed, 1.0D);
                        break;
                    }
                }
            }
        }

        level.scheduleTick(pos, this, 1);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!level.isClientSide && !level.getBlockTicks().hasScheduledTick(pos, this)) {
            level.scheduleTick(pos, this, 1);
        }
    }

    private boolean hasPlayerNearby(Level level, BlockPos pos) {
        AABB searchBox = new AABB(pos).inflate(24.0D);
        List<Player> players = level.getEntitiesOfClass(Player.class, searchBox);
        return !players.isEmpty();
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
