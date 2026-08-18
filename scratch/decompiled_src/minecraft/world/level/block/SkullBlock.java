package net.minecraft.world.level.block;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SkullBlock extends AbstractSkullBlock {
   public static final int MAX = RotationSegment.getMaxSegmentIndex();
   private static final int ROTATIONS = MAX + 1;
   public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
   private static final VoxelShape SHAPE = Block.column(8.0D, 0.0D, 8.0D);
   private static final VoxelShape SHAPE_PIGLIN = Block.column(10.0D, 0.0D, 8.0D);
   private static final VoxelShape SHAPE_DRAGON_OUTLINE = Block.column(8.0D, 0.0D, 8.5D);

   protected SkullBlock(final SkullBlock.Type type, final BlockBehaviour.Properties properties) {
      super(type, properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(ROTATION, Integer.valueOf(0)));
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return this.getType() == SkullBlock.Types.DRAGON ? SHAPE_DRAGON_OUTLINE : this.getCollisionShape(state, level, pos, context);
   }

   protected VoxelShape getCollisionShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return this.getType() == SkullBlock.Types.PIGLIN ? SHAPE_PIGLIN : SHAPE;
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      return (BlockState)super.getStateForPlacement(context).setValue(ROTATION, Integer.valueOf(RotationSegment.convertToSegment(context.getRotation())));
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)state.setValue(ROTATION, Integer.valueOf(rotation.rotate(state.getValue(ROTATION), ROTATIONS)));
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      return (BlockState)state.setValue(ROTATION, Integer.valueOf(mirror.mirror(state.getValue(ROTATION), ROTATIONS)));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
      super.createBlockStateDefinition(builder);
      builder.add(ROTATION);
   }

   public interface Type extends StringRepresentable {
      Map TYPES = new Object2ObjectArrayMap();
      Codec CODEC = Codec.stringResolver(StringRepresentable::getSerializedName, TYPES::get);
   }

   public static enum Types implements SkullBlock.Type {
      SKELETON("skeleton"),
      WITHER_SKELETON("wither_skeleton"),
      PLAYER("player"),
      ZOMBIE("zombie"),
      CREEPER("creeper"),
      PIGLIN("piglin"),
      DRAGON("dragon");

      private final String name;

      private Types(final String name) {
         this.name = name;
         TYPES.put(name, this);
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static SkullBlock.Types[] $values() {
         return new SkullBlock.Types[]{SKELETON, WITHER_SKELETON, PLAYER, ZOMBIE, CREEPER, PIGLIN, DRAGON};
      }
   }
}
