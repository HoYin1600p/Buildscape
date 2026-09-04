package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.ModBlocks;
import com.kingodogo.buildscape.block.PlanterHelper;
import com.kingodogo.buildscape.block.PlanterHelper.PlanterType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.IPlantable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ComposterBlock.class)
public abstract class ComposterBlockMixin extends Block {

    public ComposterBlockMixin(Properties pProperties) {
        super(pProperties);
    }

    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    protected void buildscape$createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder, CallbackInfo ci) {
        pBuilder.add(PlanterHelper.PLANTER);
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, net.minecraft.core.Direction facing, IPlantable plantable) {
        if (state.hasProperty(PlanterHelper.PLANTER) && state.getValue(PlanterHelper.PLANTER) != PlanterType.NONE) {
            return true;
        }
        return super.canSustainPlant(state, world, pos, facing, plantable);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void buildscape$use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (state.hasProperty(PlanterHelper.PLANTER)) {
            PlanterType planter = state.getValue(PlanterHelper.PLANTER);
            if (planter != PlanterType.NONE) {
                cir.setReturnValue(InteractionResult.PASS);
                return;
            }

            if (state.getValue(ComposterBlock.LEVEL) == 0) {
                ItemStack heldItem = player.getItemInHand(hand);
                if (heldItem.getItem() instanceof BlockItem blockItem) {
                    Block block = blockItem.getBlock();
                    PlanterType newType = null;

                    if (block == Blocks.DIRT) newType = PlanterType.DIRT;
                    else if (block == Blocks.COARSE_DIRT) newType = PlanterType.COARSE_DIRT;
                    else if (block == ModBlocks.MUD.get()) newType = PlanterType.MUD;
                    else if (block == Blocks.MOSS_BLOCK) newType = PlanterType.MOSS_BLOCK;
                    else if (block == Blocks.ROOTED_DIRT) newType = PlanterType.ROOTED_DIRT;
                    else if (block == ModBlocks.RED_MOSS_BLOCK.get()) newType = PlanterType.RED_MOSS_BLOCK;
                    else if (block == ModBlocks.YELLOW_MOSS_BLOCK.get()) newType = PlanterType.YELLOW_MOSS_BLOCK;
                    else if (block == ModBlocks.ORANGE_MOSS_BLOCK.get()) newType = PlanterType.ORANGE_MOSS_BLOCK;
                    else if (block == ModBlocks.PALE_MOSS_BLOCK.get()) newType = PlanterType.PALE_MOSS_BLOCK;
                    else if (block == ModBlocks.MUDDY_MANGROVE_ROOTS.get()) newType = PlanterType.MUDDY_MANGROVE_ROOTS;
                    else if (block == Blocks.GRASS_BLOCK) newType = PlanterType.GRASS_BLOCK;
                    else if (block == ModBlocks.SNOWY_GRASS_BLOCK.get()) newType = PlanterType.SNOWY_GRASS_BLOCK;
                    else if (block == Blocks.MYCELIUM) newType = PlanterType.MYCELIUM;
                    else if (block == Blocks.PODZOL) newType = PlanterType.PODZOL;
                    else if (block == Blocks.CRIMSON_NYLIUM) newType = PlanterType.CRIMSON_NYLIUM;
                    else if (block == Blocks.WARPED_NYLIUM) newType = PlanterType.WARPED_NYLIUM;
                    else if (block == Blocks.SAND) newType = PlanterType.SAND;

                    if (newType != null) {
                        if (!level.isClientSide) {
                            if (newType == PlanterType.GRASS_BLOCK) {
                                boolean isSnowy = level.getBlockState(pos.above()).is(net.minecraft.tags.BlockTags.SNOW);
                                if (isSnowy) {
                                    newType = PlanterType.SNOWY_GRASS_BLOCK;
                                }
                            }

                            level.setBlock(pos, state.setValue(PlanterHelper.PLANTER, newType), 3);

                            net.minecraft.world.level.block.SoundType soundType = block.getSoundType(block.defaultBlockState(), level, pos, player);
                            level.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);

                            if (!player.getAbilities().instabuild) {
                                heldItem.shrink(1);
                            }
                        }
                        cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
                    }
                }
            }
        }
    }


    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos) {
        if (facing == Direction.UP && state.hasProperty(PlanterHelper.PLANTER)) {
            PlanterType planter = state.getValue(PlanterHelper.PLANTER);
            if (planter == PlanterType.GRASS_BLOCK || planter == PlanterType.SNOWY_GRASS_BLOCK) {
                boolean isSnowy = facingState.is(net.minecraft.tags.BlockTags.SNOW);
                if (isSnowy) {
                    if (planter != PlanterType.SNOWY_GRASS_BLOCK) {
                        state = state.setValue(PlanterHelper.PLANTER, PlanterType.SNOWY_GRASS_BLOCK);
                    }
                } else {
                    if (planter == PlanterType.SNOWY_GRASS_BLOCK) {
                        state = state.setValue(PlanterHelper.PLANTER, PlanterType.GRASS_BLOCK);
                    }
                }
            }
        }
        return super.updateShape(state, facing, facingState, level, pos, facingPos);
    }
}
