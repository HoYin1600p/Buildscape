package com.kingodogo.buildscape.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GlassJarBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 14.0D, 12.0D);

    public GlassJarBlock(Properties properties) {
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
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GlassJarBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (stack.hasTag() && stack.getTag().contains("BlockEntityTag", 10)) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof GlassJarBlockEntity jarBE) {
                jarBE.load(stack.getTag().getCompound("BlockEntityTag"));
                jarBE.sync();
            }
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        ItemStack jarStack = new ItemStack(this);

        if (be instanceof GlassJarBlockEntity jarBE && !jarBE.isEmpty()) {
            CompoundTag tag = new CompoundTag();
            jarBE.saveAdditional(tag);

            CompoundTag stackTag = new CompoundTag();
            stackTag.put("BlockEntityTag", tag);
            jarStack.setTag(stackTag);
        }

        return List.of(jarStack);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            @Nullable BlockGetter level,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (stack.hasTag() && stack.getTag().contains("BlockEntityTag", 10)) {
            CompoundTag beTag = stack.getTag().getCompound("BlockEntityTag");
            if (beTag.contains("StoredItem", 10)) {
                ItemStack stored = ItemStack.of(beTag.getCompound("StoredItem"));
                int count = beTag.getCompound("StoredItem").getInt("RealCount");
                if (count <= 0) count = stored.getCount();
                if (!stored.isEmpty() && count > 0) {
                    tooltip.add(new TextComponent("Stored: ").append(stored.getHoverName()).append(" x" + count).withStyle(ChatFormatting.GRAY));
                }
            } else if (beTag.contains("StoredLiquidItem", 10)) {
                ItemStack liquid = ItemStack.of(beTag.getCompound("StoredLiquidItem"));
                int levelAmount = beTag.getInt("LiquidLevel");
                if (!liquid.isEmpty() && levelAmount > 0) {
                    int maxLevel = GlassJarBlockEntity.isXpLiquid(liquid)
                            ? GlassJarBlockEntity.XP_BOTTLE_MAX
                            : GlassJarBlockEntity.MAX_LIQUID_LEVEL;
                    tooltip.add(new TextComponent("Stored Liquid: ").append(liquid.getHoverName()).append(" (" + levelAmount + "/" + maxLevel + ")").withStyle(ChatFormatting.GRAY));
                }
            }
        }
    }

    @Override
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof GlassJarBlockEntity jarBE)) {
            return InteractionResult.PASS;
        }

        ItemStack handStack = player.getItemInHand(hand);
        boolean isSneaking = player.isShiftKeyDown();

        // -------------------------------------------------------------
        // 1. INSERTION (Normal Right Click holding food or liquid item)
        // -------------------------------------------------------------
        if (!isSneaking) {
            if (GlassJarBlockEntity.isLiquidItem(handStack) && jarBE.canAcceptLiquid(handStack)) {
                if (level.isClientSide) {
                    return InteractionResult.SUCCESS;
                }
                boolean isBucket = handStack.getItem() instanceof net.minecraft.world.item.BucketItem
                        || handStack.getItem() instanceof net.minecraft.world.item.MilkBucketItem;
                ItemStack copy = handStack.copy();

                if (jarBE.addLiquid(copy)) {
                    if (!player.getAbilities().instabuild) {
                        handStack.shrink(1);
                        ItemStack returnItem = isBucket ? new ItemStack(Items.BUCKET) : new ItemStack(Items.GLASS_BOTTLE);
                        if (!player.getInventory().add(returnItem)) {
                            player.drop(returnItem, false);
                        }
                    }
                    net.minecraft.sounds.SoundEvent sound = isBucket ? SoundEvents.BUCKET_EMPTY : SoundEvents.BOTTLE_EMPTY;
                    level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return InteractionResult.SUCCESS;
                }
            }

            if (GlassJarBlockEntity.isFoodItem(handStack) && jarBE.canAcceptFood(handStack)) {
                if (level.isClientSide) {
                    return InteractionResult.SUCCESS;
                }
                ItemStack singleInsert = handStack.copy();
                singleInsert.setCount(1);

                int added = jarBE.addFood(singleInsert);
                if (added > 0) {
                    if (!player.getAbilities().instabuild) {
                        handStack.shrink(added);
                    }
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.8F, 1.2F);
                    return InteractionResult.SUCCESS;
                }
            }
        }

        // -------------------------------------------------------------
        // 2. EXTRACTION TO HAND (Sneak + Right Click)
        // -------------------------------------------------------------
        if (isSneaking) {
            // A. Extract Food (Empty hand OR holding same stored food item)
            if (!jarBE.isEmpty() && !jarBE.hasLiquid()) {
                ItemStack stored = jarBE.getStoredItem();
                if (handStack.isEmpty()) {
                    if (level.isClientSide) return InteractionResult.SUCCESS;
                    ItemStack item = jarBE.extractFood(1);
                    if (!item.isEmpty()) {
                        player.setItemInHand(hand, item);
                        level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.8F, 0.9F);
                        return InteractionResult.SUCCESS;
                    }
                } else if (ItemStack.isSameItemSameTags(handStack, stored)) {
                    if (level.isClientSide) return InteractionResult.SUCCESS;
                    ItemStack item = jarBE.extractFood(1);
                    if (!item.isEmpty()) {
                        if (handStack.getCount() < handStack.getMaxStackSize()) {
                            handStack.grow(1);
                        } else {
                            if (!player.getInventory().add(item)) {
                                player.drop(item, false);
                            }
                        }
                        level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.8F, 0.9F);
                        return InteractionResult.SUCCESS;
                    }
                }
            }

            // B. Extract Potion/Honey bottle (Empty hand)
            if (handStack.isEmpty() && jarBE.hasLiquid() && jarBE.getLiquidLevel() > 0) {
                ItemStack bottleRepresentation = jarBE.getBottleRepresentation();
                if (!bottleRepresentation.isEmpty()) {
                    if (level.isClientSide) return InteractionResult.SUCCESS;
                    ItemStack bottle = jarBE.extractBottle();
                    if (!bottle.isEmpty()) {
                        player.setItemInHand(hand, bottle);
                        level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 2.5. EXTRACTION WITH BUCKET/BOTTLE (Does not require sneaking)
        // -------------------------------------------------------------
        // C. Empty Bucket extraction (XP: requires XP_BOTTLE_MAX; others: requires full 16 levels)
        if (handStack.is(Items.BUCKET) && jarBE.hasLiquid()) {
            int required = GlassJarBlockEntity.isXpLiquid(jarBE.getStoredLiquidItem())
                    ? GlassJarBlockEntity.XP_BOTTLE_MAX
                    : GlassJarBlockEntity.MAX_LIQUID_LEVEL;
            if (jarBE.getLiquidLevel() >= required) {
                ItemStack bucketRepresentation = jarBE.getBucketRepresentation();
                if (!bucketRepresentation.isEmpty()) {
                    if (level.isClientSide) return InteractionResult.SUCCESS;
                    ItemStack filledBucket = jarBE.extractBucket();
                    if (!filledBucket.isEmpty()) {
                        if (!player.getAbilities().instabuild) {
                            handStack.shrink(1);
                            if (!player.getInventory().add(filledBucket)) {
                                player.drop(filledBucket, false);
                            }
                        }
                        level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        // D. Empty Bottle extraction (Requires liquid level > 0, excluding Lava)
        if (handStack.is(Items.GLASS_BOTTLE) && jarBE.hasLiquid() && jarBE.getLiquidLevel() > 0) {
            ItemStack bottleRepresentation = jarBE.getBottleRepresentation();
            if (!bottleRepresentation.isEmpty()) {
                if (level.isClientSide) return InteractionResult.SUCCESS;
                ItemStack filledBottle = jarBE.extractBottle();
                if (!filledBottle.isEmpty()) {
                    if (!player.getAbilities().instabuild) {
                        handStack.shrink(1);
                        if (!player.getInventory().add(filledBottle)) {
                            player.drop(filledBottle, false);
                        }
                    }
                    level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return InteractionResult.SUCCESS;
                }
            }
        }

        // -------------------------------------------------------------
        // 3. NORMAL RIGHT CLICK WITH EMPTY HAND (Direct Consumption/Drinking from jar)
        // -------------------------------------------------------------
        if (!isSneaking && handStack.isEmpty()) {
            // A. Consume food from jar if player can eat
            if (!jarBE.isEmpty() && !jarBE.hasLiquid()) {
                if (player.canEat(false) || player.getAbilities().instabuild) {
                    if (level.isClientSide) return InteractionResult.SUCCESS;

                    ItemStack stored = jarBE.getStoredItem();
                    if (!stored.isEmpty()) {
                        ItemStack singleFood = stored.copy();
                        singleFood.setCount(1);

                        player.eat(level, singleFood);
                        jarBE.extractFood(1);

                        level.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.8F, 1.0F);
                        return InteractionResult.SUCCESS;
                    }
                }
            }

            // B. Drink potion / liquid directly from jar
            if (jarBE.hasLiquid()) {
                ItemStack liquidItem = jarBE.getStoredLiquidItem();
                if (!liquidItem.isEmpty()) {
                    // Do not allow consuming lava or XP liquid from jars
                    if (liquidItem.is(Items.LAVA_BUCKET) 
                            || (liquidItem.getItem() instanceof net.minecraft.world.item.BucketItem bucket && bucket.getFluid() == net.minecraft.world.level.material.Fluids.LAVA)
                            || GlassJarBlockEntity.isXpLiquid(liquidItem)) {
                        return InteractionResult.PASS;
                    }

                    if (level.isClientSide) return InteractionResult.SUCCESS;

                    if (liquidItem.getItem() instanceof PotionItem) {
                        for (MobEffectInstance effect : PotionUtils.getMobEffects(liquidItem)) {
                            if (effect.getEffect().isInstantenous()) {
                                effect.getEffect().applyInstantenousEffect(player, player, player, effect.getAmplifier(), 1.0D);
                            } else {
                                player.addEffect(new MobEffectInstance(effect));
                            }
                        }
                        jarBE.extractBottle();
                        level.playSound(null, pos, SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.8F, 1.0F);
                        return InteractionResult.SUCCESS;
                    } else if (liquidItem.getItem() instanceof HoneyBottleItem) {
                        player.getFoodData().eat(6, 0.6F);
                        player.removeEffect(MobEffects.POISON);
                        jarBE.extractBottle();
                        level.playSound(null, pos, SoundEvents.HONEY_DRINK, SoundSource.PLAYERS, 0.8F, 1.0F);
                        return InteractionResult.SUCCESS;
                    } else if (liquidItem.is(Items.MILK_BUCKET)) {
                        player.removeAllEffects();
                        jarBE.extractBottle();
                        level.playSound(null, pos, SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.8F, 1.0F);
                        return InteractionResult.SUCCESS;
                    } else {
                        if (player.isOnFire()) {
                            player.clearFire();
                        }
                        jarBE.extractBottle();
                        level.playSound(null, pos, SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.8F, 1.0F);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean isMoving
    ) {
        if (!state.is(newState.getBlock())) {
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}
