package net.minecraft.world.level.block;

import com.mojang.datafixers.DataFixUtils;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StemBlock extends VegetationBlock implements BonemealableBlock {
   public static final int MAX_AGE = 7;
   public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
   private static final VoxelShape[] SHAPES = Block.boxes(7, (age) -> Block.column(2.0D, 0.0D, (double)(2 + age * 2)));
   private final ResourceKey fruit;
   private final ResourceKey attachedStem;
   private final ResourceKey seed;
   private final TagKey stemSupportBlocks;
   private final TagKey fruitSupportBlocks;

   protected StemBlock(final ResourceKey fruit, final ResourceKey attachedStem, final ResourceKey seed, final TagKey stemSupportBlocks, final TagKey fruitSupportBlocks, final BlockBehaviour.Properties properties) {
      super(properties);
      this.fruit = fruit;
      this.attachedStem = attachedStem;
      this.seed = seed;
      this.stemSupportBlocks = stemSupportBlocks;
      this.fruitSupportBlocks = fruitSupportBlocks;
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, Integer.valueOf(0)));
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPES[state.getValue(AGE)];
   }

   protected boolean mayPlaceOn(final BlockState state, final BlockGetter level, final BlockPos pos) {
      return state.is(this.stemSupportBlocks);
   }

   protected void randomTick(BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (level.getRawBrightness(pos, 0) >= 9) {
         float growthSpeed = CropBlock.getGrowthSpeed(this, level, pos);
         if (random.nextInt((int)(25.0F / growthSpeed) + 1) == 0) {
            int age = state.getValue(AGE);
            if (age < 7) {
               state = (BlockState)state.setValue(AGE, Integer.valueOf(age + 1));
               level.setBlock(pos, state, 2);
            } else {
               Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
               BlockPos relative = pos.relative(direction);
               BlockState stateBelow = level.getBlockState(relative.below());
               if (level.getBlockState(relative).isAir() && stateBelow.is(this.fruitSupportBlocks)) {
                  Registry blocks = level.registryAccess().lookupOrThrow(Registries.BLOCK);
                  Optional fruit = blocks.getOptional(this.fruit);
                  Optional stem = blocks.getOptional(this.attachedStem);
                  if (fruit.isPresent() && stem.isPresent()) {
                     level.setBlockAndUpdate(relative, ((Block)fruit.get()).defaultBlockState());
                     level.setBlockAndUpdate(pos, (BlockState)((Block)stem.get()).defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, direction));
                  }
               }
            }
         }

      }
   }

   protected ItemStack getCloneItemStack(final LevelReader level, final BlockPos pos, final BlockState state, final boolean includeData) {
      return new ItemStack((ItemLike)DataFixUtils.orElse(level.registryAccess().lookupOrThrow(Registries.ITEM).getOptional(this.seed), this));
   }

   public boolean isValidBonemealTarget(final LevelReader level, final BlockPos pos, final BlockState state, final BonemealSource source) {
      return state.getValue(AGE) != 7;
   }

   public boolean isBonemealSuccess(final Level level, final RandomSource random, final BlockPos pos, final BlockState state, final BonemealSource source) {
      return true;
   }

   public void performBonemeal(final ServerLevel level, final RandomSource random, final BlockPos pos, final BlockState state, final BonemealSource source) {
      int age = Math.min(7, state.getValue(AGE) + Mth.nextInt(random, 2, 5));
      BlockState newState = (BlockState)state.setValue(AGE, Integer.valueOf(age));
      level.setBlock(pos, newState, 2);
      if (age == 7) {
         newState.randomTick(level, pos, random);
      }

   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
      builder.add(AGE);
   }
}
