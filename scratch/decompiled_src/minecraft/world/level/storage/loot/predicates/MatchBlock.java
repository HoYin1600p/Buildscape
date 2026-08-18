package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.minecraft.advancements.predicates.BlockPredicate;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record MatchBlock(BlockPredicate predicate) implements LootItemCondition {
   public static final MapCodec MAP_CODEC = BlockPredicate.MAP_CODEC.xmap(MatchBlock::new, MatchBlock::predicate);

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public Set getReferencedContextParams() {
      return Set.of(LootContextParams.BLOCK_STATE, LootContextParams.BLOCK_ENTITY);
   }

   public boolean test(final LootContext context) {
      BlockState blockState = (BlockState)context.getOptionalParameter(LootContextParams.BLOCK_STATE);
      BlockEntity blockEntity = (BlockEntity)context.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
      return blockState != null && this.predicate.matchesState(blockState) && this.predicate.matchesBlockEntity(context.getLevel(), blockEntity);
   }

   public static LootItemCondition.Builder blockMatches(final BlockPredicate.Builder predicate) {
      return () -> new MatchBlock(predicate.build());
   }

   public static LootItemCondition.Builder blockMatches(final HolderGetter lookup, final Block block) {
      return blockMatches(BlockPredicate.Builder.block().of(lookup, block));
   }

   public static LootItemCondition.Builder blockMatches(final HolderGetter lookup, final Block block, final StatePropertiesPredicate.Builder properties) {
      return blockMatches(BlockPredicate.Builder.block().of(lookup, block).setProperties(properties));
   }
}
