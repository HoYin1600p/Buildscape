package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.core.ClientHooks;
import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.feature.pipe.HollowLogBlock;
import com.kingodogo.buildscape.registry.ModBlocks;
import com.kingodogo.buildscape.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class PottedHazeBushBlock extends FlowerPotBlock {

    public static final BooleanProperty HAS_HAZE = BooleanProperty.create("has_haze");
    private final DyeColor color;

    public PottedHazeBushBlock(DyeColor color, Supplier<FlowerPotBlock> emptyPot, Supplier<? extends Block> plant,
            BlockBehaviour.Properties properties) {
        super(emptyPot, plant, properties);
        this.color = color;
        this.registerDefaultState(this.stateDefinition.any().setValue(HAS_HAZE, true));
    }

    @Nullable
    public DyeColor getColor() {
        return this.color;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HAS_HAZE);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
        super.animateTick(state, level, pos, random);

        if (!state.getValue(HAS_HAZE)) {
            return;
        }

        ClientHooks.get().hazeBushAnimateTick(level, pos, random, this.color);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hitResult) {
        ItemStack held = player.getItemInHand(hand);

        if (held.is(Items.GLASS_BOTTLE) && state.getValue(HAS_HAZE)) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(HAS_HAZE, false), 3);

                level.playSound(null, pos, SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.4F, 1.2F);

                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }

                ItemStack bottleOfMist = new ItemStack(ModItems.BOTTLE_OF_MIST.get());
                if (held.isEmpty()) {
                    player.setItemInHand(hand, bottleOfMist);
                } else {
                    if (!player.getInventory().add(bottleOfMist)) {
                        player.drop(bottleOfMist, false);
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (held.getItem() instanceof DyeItem dyeItem) {
            DyeColor dyeColor = dyeItem.getDyeColor();
            if (this.color != dyeColor) {
                if (!level.isClientSide) {
                    RegistryObject<Block> targetBlock = ModBlocks.POTTED_COLORED_HAZE_BUSHES.get(dyeColor);
                    if (targetBlock != null) {
                        boolean hadHaze = state.getValue(HAS_HAZE);
                        level.setBlock(pos, targetBlock.get().defaultBlockState().setValue(HAS_HAZE, hadHaze), 3);
                        level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        if (!player.getAbilities().instabuild) {
                            held.shrink(1);
                        }
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        if (held.is(ModItems.BOTTLE_OF_MIST.get()) && !state.getValue(HAS_HAZE)) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(HAS_HAZE, true), 3);

                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.4F, 0.8F);

                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }

                ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);
                if (held.isEmpty()) {
                    player.setItemInHand(hand, emptyBottle);
                } else {
                    if (!player.getInventory().add(emptyBottle)) {
                        player.drop(emptyBottle, false);
                    }
                }
                level.levelEvent(2005, pos, 0);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (held.is(Items.BONE_MEAL)) {
            if (!level.isClientSide) {
                popResource(level, pos, new ItemStack(this.getContent()));
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                level.levelEvent(1505, pos, 0);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (HollowLogBlock.getPottedBlockState(held.getItem()) != null) {
            return InteractionResult.CONSUME;
        }

        ItemStack plantDrop = new ItemStack(this.getContent());
        if (!state.getValue(HAS_HAZE)) {
            CompoundTag blockStateTag = new CompoundTag();
            blockStateTag.putString(NbtKeys.HAS_HAZE_PROPERTY, "false");
            plantDrop.addTagElement("BlockStateTag", blockStateTag);
            plantDrop.getOrCreateTag().putBoolean(NbtKeys.HAS_HAZE, false);
        }

        if (held.isEmpty()) {
            player.setItemInHand(hand, plantDrop);
        } else if (!player.addItem(plantDrop)) {
            player.drop(plantDrop, false);
        }

        level.setBlock(pos, Blocks.FLOWER_POT.defaultBlockState(), 3);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        if (!state.getValue(HAS_HAZE)) {
            for (ItemStack drop : drops) {
                if (drop.getItem() instanceof BlockItem bi && bi.getBlock() == this.getContent()) {
                    CompoundTag blockStateTag = new CompoundTag();
                    blockStateTag.putString(NbtKeys.HAS_HAZE_PROPERTY, "false");
                    drop.addTagElement("BlockStateTag", blockStateTag);
                    drop.getOrCreateTag().putBoolean(NbtKeys.HAS_HAZE, false);
                }
            }
        }
        return drops;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(level, pos, state);
        if (!state.getValue(HAS_HAZE)) {
            CompoundTag blockStateTag = new CompoundTag();
            blockStateTag.putString(NbtKeys.HAS_HAZE_PROPERTY, "false");
            stack.addTagElement("BlockStateTag", blockStateTag);
            stack.getOrCreateTag().putBoolean(NbtKeys.HAS_HAZE, false);
        }
        return stack;
    }
}
