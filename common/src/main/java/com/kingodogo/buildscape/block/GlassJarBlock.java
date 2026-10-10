package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
public abstract class GlassJarBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, ICommonInteractable {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape SHAPE = Shapes.or(
            Block.box(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D),
            Block.box(5.0D, 12.0D, 5.0D, 11.0D, 13.0D, 11.0D),
            Block.box(4.5D, 13.0D, 4.5D, 11.5D, 14.0D, 11.5D)
    );

    public GlassJarBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GlassJarBlockEntity(pos, state);
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        return handleJarInteraction(player.getItemInHand(hand), state, level, pos, player, hand, hit);
    }
    private InteractionResult handleJarInteraction(ItemStack handStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof GlassJarBlockEntity jarBE)) {
            return InteractionResult.PASS;
        }

        ItemStack held = handStack.isEmpty() ? player.getItemInHand(hand) : handStack;
        boolean isSneaking = player.isShiftKeyDown();

        if (!isSneaking) {
            if (GlassJarBlockEntity.isLiquidItem(held) && jarBE.canAcceptLiquid(held)) {
                if (level.isClientSide()) {
                    return InteractionResult.SUCCESS;
                }
                boolean isBucket = held.getItem() instanceof BucketItem || held.is(Items.MILK_BUCKET);
                ItemStack copy = held.copy();

                if (jarBE.addLiquid(copy)) {
                    if (!player.getAbilities().instabuild) {
                        held.shrink(1);
                        ItemStack returnItem = isBucket ? new ItemStack(Items.BUCKET) : new ItemStack(Items.GLASS_BOTTLE);
                        if (!player.getInventory().add(returnItem)) {
                            player.drop(returnItem, false);
                        }
                    }
                    if (isBucket) {
                        Services.PLATFORM.playBucketEmpty(level, pos);
                    } else {
                        Services.PLATFORM.playBottleEmpty(level, pos);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
            if (GlassJarBlockEntity.isFoodItem(held) && jarBE.canAcceptFood(held)) {
                if (level.isClientSide()) {
                    return InteractionResult.SUCCESS;
                }
                ItemStack singleInsert = held.copy();
                singleInsert.setCount(1);

                int added = jarBE.addFood(singleInsert);
                if (added > 0) {
                    if (!player.getAbilities().instabuild) {
                        held.shrink(added);
                    }
                    Services.PLATFORM.playItemPickup(level, pos, 0.8F, 1.2F);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        if (isSneaking) {
            if (!jarBE.isEmpty() && !jarBE.hasLiquid()) {
                ItemStack stored = jarBE.getStoredItem();
                if (held.isEmpty()) {
                    if (level.isClientSide()) return InteractionResult.SUCCESS;
                    ItemStack item = jarBE.extractFood(1);
                    if (!item.isEmpty()) {
                        player.setItemInHand(hand, item);
                        Services.PLATFORM.playItemPickup(level, pos, 0.8F, 0.9F);
                        return InteractionResult.SUCCESS;
                    }
                } else if (com.kingodogo.buildscape.platform.Services.PLATFORM.isSameItemSameComponents(held, stored)) {
                    if (level.isClientSide()) return InteractionResult.SUCCESS;
                    ItemStack item = jarBE.extractFood(1);
                    if (!item.isEmpty()) {
                        if (held.getCount() < held.getMaxStackSize()) {
                            held.grow(1);
                        } else {
                            if (!player.getInventory().add(item)) {
                                player.drop(item, false);
                            }
                        }
                        Services.PLATFORM.playItemPickup(level, pos, 0.8F, 0.9F);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        if (held.is(Items.BUCKET) && jarBE.hasLiquid()) {
            int required = GlassJarBlockEntity.isXpLiquid(jarBE.getStoredLiquidItem())
                    ? GlassJarBlockEntity.XP_BOTTLE_MAX
                    : GlassJarBlockEntity.MAX_LIQUID_LEVEL;
            if (jarBE.getLiquidLevel() >= required) {
                ItemStack bucketRepresentation = jarBE.getBucketRepresentation();
                if (!bucketRepresentation.isEmpty()) {
                    if (level.isClientSide()) return InteractionResult.SUCCESS;
                    ItemStack filledBucket = jarBE.extractBucket();
                    if (!filledBucket.isEmpty()) {
                        if (!player.getAbilities().instabuild) {
                            held.shrink(1);
                            if (!player.getInventory().add(filledBucket)) {
                                player.drop(filledBucket, false);
                            }
                        }
                        Services.PLATFORM.playBucketFill(level, pos);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        if (held.is(Items.GLASS_BOTTLE) && jarBE.hasLiquid() && jarBE.getLiquidLevel() > 0) {
            ItemStack bottleRepresentation = jarBE.getBottleRepresentation();
            if (!bottleRepresentation.isEmpty()) {
                if (level.isClientSide()) return InteractionResult.SUCCESS;
                ItemStack filledBottle = jarBE.extractBottle();
                if (!filledBottle.isEmpty()) {
                    if (!player.getAbilities().instabuild) {
                        held.shrink(1);
                        if (!player.getInventory().add(filledBottle)) {
                            player.drop(filledBottle, false);
                        }
                    }
                    Services.PLATFORM.playBottleFill(level, pos);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        if (!isSneaking && held.isEmpty()) {
            if (!jarBE.isEmpty() && !jarBE.hasLiquid()) {
                if (player.canEat(false) || player.getAbilities().instabuild) {
                    if (level.isClientSide()) return InteractionResult.SUCCESS;

                    ItemStack stored = jarBE.getStoredItem();
                    if (!stored.isEmpty()) {
                        ItemStack singleFood = stored.copy();
                        singleFood.setCount(1);

                        com.kingodogo.buildscape.platform.Services.PLATFORM.applyFoodEffects(player, level, singleFood);
                        jarBE.extractFood(1);

                        com.kingodogo.buildscape.platform.Services.PLATFORM.playEatSound(level, pos);
                        return InteractionResult.SUCCESS;
                    }
                }
            }

            if (jarBE.hasLiquid()) {
                ItemStack liquidItem = jarBE.getStoredLiquidItem();
                if (!liquidItem.isEmpty()) {
                    if (liquidItem.is(Items.LAVA_BUCKET) || GlassJarBlockEntity.isXpLiquid(liquidItem)) {
                        return InteractionResult.PASS;
                    }

                    if (level.isClientSide()) return InteractionResult.SUCCESS;

                    if (liquidItem.getItem() instanceof PotionItem) {
                        com.kingodogo.buildscape.platform.Services.PLATFORM.applyPotionEffects(player, liquidItem);
                        jarBE.extractBottle();
                        com.kingodogo.buildscape.platform.Services.PLATFORM.playDrinkSound(level, pos);
                        return InteractionResult.SUCCESS;
                    } else if (liquidItem.is(Items.HONEY_BOTTLE)) {
                        player.getFoodData().eat(6, 0.6F);
                        player.removeEffect(MobEffects.POISON);
                        jarBE.extractBottle();
                        com.kingodogo.buildscape.platform.Services.PLATFORM.playHoneyDrinkSound(level, pos);
                        return InteractionResult.SUCCESS;
                    } else if (liquidItem.is(Items.MILK_BUCKET)) {
                        player.removeAllEffects();
                        jarBE.extractBottle();
                        com.kingodogo.buildscape.platform.Services.PLATFORM.playDrinkSound(level, pos);
                        return InteractionResult.SUCCESS;
                    } else {
                        if (player.isOnFire()) {
                            player.clearFire();
                        }
                        jarBE.extractBottle();
                        com.kingodogo.buildscape.platform.Services.PLATFORM.playDrinkSound(level, pos);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        return InteractionResult.PASS;
    }
}
