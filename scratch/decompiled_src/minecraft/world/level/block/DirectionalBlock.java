package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public abstract class DirectionalBlock extends Block {
   public static final EnumProperty FACING = BlockStateProperties.FACING;

   protected DirectionalBlock(final BlockBehaviour.Properties properties) {
      super(properties);
   }
}
