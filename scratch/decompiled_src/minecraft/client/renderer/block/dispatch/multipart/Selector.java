package net.minecraft.client.renderer.block.dispatch.multipart;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.state.StateDefinition;

public record Selector(Optional condition, BlockStateModel.Unbaked variant) {
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(Condition.CODEC.optionalFieldOf("when").forGetter(Selector::condition), BlockStateModel.Unbaked.CODEC.fieldOf("apply").forGetter(Selector::variant)).apply(i, Selector::new));

   public Predicate instantiate(final StateDefinition definition) {
      return (Predicate)this.condition.map((c) -> c.instantiate(definition)).orElse((Predicate)(state) -> true);
   }
}
