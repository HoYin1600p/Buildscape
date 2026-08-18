package net.minecraft.client.resources.model;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BlockStateDefinitions {
   private static final StateDefinition ITEM_FRAME_FAKE_DEFINITION = createItemFrameFakeState();
   private static final StateDefinition GLOW_ITEM_FRAME_FAKE_DEFINITION = createItemFrameFakeState();
   private static final Identifier GLOW_ITEM_FRAME_LOCATION = Identifier.withDefaultNamespace("glow_item_frame");
   private static final Identifier ITEM_FRAME_LOCATION = Identifier.withDefaultNamespace("item_frame");
   private static final Map STATIC_DEFINITIONS = Map.of(ITEM_FRAME_LOCATION, ITEM_FRAME_FAKE_DEFINITION, GLOW_ITEM_FRAME_LOCATION, GLOW_ITEM_FRAME_FAKE_DEFINITION);

   private static StateDefinition createItemFrameFakeState() {
      return (new StateDefinition.Builder(Blocks.AIR)).add(BlockStateProperties.MAP).create(Block::defaultBlockState, BlockState::new);
   }

   public static BlockState getItemFrameFakeState(final boolean isGlowing, final boolean map) {
      return (BlockState)((BlockState)(isGlowing ? GLOW_ITEM_FRAME_FAKE_DEFINITION : ITEM_FRAME_FAKE_DEFINITION).any()).setValue(BlockStateProperties.MAP, Boolean.valueOf(map));
   }

   public static Function definitionLocationToBlockStateMapper() {
      Map result = new HashMap(STATIC_DEFINITIONS);

      for(Block block : BuiltInRegistries.BLOCK) {
         result.put(block.builtInRegistryHolder().key().identifier(), block.getStateDefinition());
      }

      return result::get;
   }
}
