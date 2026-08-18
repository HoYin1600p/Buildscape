package net.minecraft.world.level.block;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NetherFungusBlock extends VegetationBlock implements BonemealableBlock {
   private static final double BONEMEAL_SUCCESS_PROBABILITY = 0.4D;
   private static final VoxelShape SHAPE = Block.column(8.0D, 0.0D, 9.0D);
   private final Block requiredBlock;
   private final ResourceKey feature;
   private final TagKey supportBlocks;

   protected NetherFungusBlock(final ResourceKey feature, final Block requiredBlock, final TagKey supportBlocks, final BlockBehaviour.Properties properties) {
      super(properties);
      this.feature = feature;
      this.requiredBlock = requiredBlock;
      this.supportBlocks = supportBlocks;
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(final BlockState state, final BlockGetter level, final BlockPos pos) {
      return state.is(this.supportBlocks);
   }

   private Optional getFeature(final LevelReader level) {
      return level.registryAccess().lookupOrThrow(Registries.FEATURE).get(this.feature);
   }

   public boolean isValidBonemealTarget(final LevelReader level, final BlockPos pos, final BlockState state, final BonemealSource source) {
      BlockState belowState = level.getBlockState(pos.below());
      return belowState.is(this.requiredBlock) && level.isInsideBuildHeight(pos.above());
   }

   public boolean isBonemealSuccess(final Level level, final RandomSource random, final BlockPos pos, final BlockState state, final BonemealSource source) {
      return (double)random.nextFloat() < 0.4D;
   }

   public void performBonemeal(final ServerLevel level, final RandomSource random, final BlockPos pos, final BlockState state, final BonemealSource source) {
      this.getFeature(level).ifPresent((feature) -> ((Feature)feature.value()).place(level, level.getChunkSource().getGenerator(), random, pos));
   }
}
