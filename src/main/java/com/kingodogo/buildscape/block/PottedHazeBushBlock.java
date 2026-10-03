package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.item.ModItems;
import com.kingodogo.buildscape.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Supplier;

public class PottedHazeBushBlock extends FlowerPotBlock {

    public static final BooleanProperty HAS_HAZE = BooleanProperty.create("has_haze");
    private final DyeColor color;

    public PottedHazeBushBlock(DyeColor color, Supplier<FlowerPotBlock> emptyPot, Supplier<? extends Block> plant, BlockBehaviour.Properties properties) {
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

        com.kingodogo.buildscape.client.HazeBushParticleHandler.track(pos);

        float[] rgb = HazeBushBlock.getPastelColor(this.color);

        int count = 1 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            double offsetX = (random.nextDouble() - 0.5D) * 5.0D;
            double offsetZ = (random.nextDouble() - 0.5D) * 5.0D;
            double offsetY = 0.02D + random.nextDouble() * 0.30D;

            double px = pos.getX() + 0.5D + offsetX;
            double py = pos.getY() + offsetY;
            double pz = pos.getZ() + 0.5D + offsetZ;

            level.addAlwaysVisibleParticle(ModParticles.HAZE.get(), true, px, py, pz, rgb[0], rgb[1], rgb[2]);
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (level.isClientSide) {
            com.kingodogo.buildscape.client.HazeBushParticleHandler.track(pos);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.isClientSide) {
                com.kingodogo.buildscape.client.HazeBushParticleHandler.untrack(pos);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
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
                    net.minecraftforge.registries.RegistryObject<Block> targetBlock = ModBlocks.POTTED_COLORED_HAZE_BUSHES.get(dyeColor);
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

        if (held.is(Items.BONE_MEAL) && !state.getValue(HAS_HAZE)) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(HAS_HAZE, true), 3);
                level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
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
