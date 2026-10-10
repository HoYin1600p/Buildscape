package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ComposterBlock.class)
public abstract class ComposterBlockMixin {

    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    protected void buildscape$createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder, CallbackInfo ci) {
        pBuilder.add(PlanterHelper.PLANTER);
    }

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void buildscape$use(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (state.hasProperty(PlanterHelper.PLANTER)) {
            PlanterType planter = state.getValue(PlanterHelper.PLANTER);
            if (planter != PlanterType.NONE) {
                cir.setReturnValue(InteractionResult.PASS);
                return;
            }

            if (state.getValue(ComposterBlock.LEVEL) == 0) {
                if (heldItem.getItem() instanceof BlockItem blockItem) {
                    Block block = blockItem.getBlock();
                    PlanterType newType = null;

                    if (block == Blocks.DIRT) newType = PlanterType.DIRT;
                    else if (block == Blocks.COARSE_DIRT) newType = PlanterType.COARSE_DIRT;
                    else if (block == Blocks.MOSS_BLOCK) newType = PlanterType.MOSS_BLOCK;
                    else if (block == Blocks.ROOTED_DIRT) newType = PlanterType.ROOTED_DIRT;
                    else if (block == Blocks.GRASS_BLOCK) newType = PlanterType.GRASS_BLOCK;
                    else if (block == Blocks.MYCELIUM) newType = PlanterType.MYCELIUM;
                    else if (block == Blocks.PODZOL) newType = PlanterType.PODZOL;
                    else if (block == Blocks.CRIMSON_NYLIUM) newType = PlanterType.CRIMSON_NYLIUM;
                    else if (block == Blocks.WARPED_NYLIUM) newType = PlanterType.WARPED_NYLIUM;
                    else if (block == Blocks.SAND) newType = PlanterType.SAND;
                    else {
                        com.kingodogo.buildscape.util.CommonId blockId = com.kingodogo.buildscape.platform.Services.PLATFORM.getBlockId(block);
                        if (blockId != null && "buildscape".equals(blockId.getNamespace())) {
                            switch (blockId.getPath()) {
                                case "mud" -> newType = PlanterType.MUD;
                                case "red_moss_block" -> newType = PlanterType.RED_MOSS_BLOCK;
                                case "yellow_moss_block" -> newType = PlanterType.YELLOW_MOSS_BLOCK;
                                case "orange_moss_block" -> newType = PlanterType.ORANGE_MOSS_BLOCK;
                                case "pale_moss_block" -> newType = PlanterType.PALE_MOSS_BLOCK;
                                case "muddy_mangrove_roots" -> newType = PlanterType.MUDDY_MANGROVE_ROOTS;
                                case "snowy_grass_block" -> newType = PlanterType.SNOWY_GRASS_BLOCK;
                            }
                        }
                    }

                    if (newType != null) {
                        if (!level.isClientSide()) {
                            if (newType == PlanterType.GRASS_BLOCK) {
                                boolean isSnowy = level.getBlockState(pos.above()).is(net.minecraft.tags.BlockTags.SNOW);
                                if (isSnowy) {
                                    newType = PlanterType.SNOWY_GRASS_BLOCK;
                                }
                            }

                            level.setBlock(pos, state.setValue(PlanterHelper.PLANTER, newType), 3);

                            MixinFactory.playComposterPlanterSound(level, pos, block);

                            if (!player.getAbilities().instabuild) {
                                heldItem.shrink(1);
                            }
                        }
                        cir.setReturnValue(com.kingodogo.buildscape.platform.Services.PLATFORM.sidedSuccess(level.isClientSide()));
                    }
                }
            }
        }
    }
    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void buildscape$preventPlanterExtraction(BlockState state, Level level, BlockPos pos,
            Player player, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (state.hasProperty(PlanterHelper.PLANTER) && state.getValue(PlanterHelper.PLANTER) != PlanterType.NONE) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

}
