package net.minecraft.client.renderer.block.dispatch.multipart;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.StateDefinition;

@FunctionalInterface
public interface Condition {
   Codec CODEC = Codec.recursive("condition", (self) -> {
      Codec combinerCodec = Codec.simpleMap(CombinedCondition.Operation.CODEC, self.listOf(), StringRepresentable.keys(CombinedCondition.Operation.values())).codec().comapFlatMap((map) -> {
         if (map.size() != 1) {
            return DataResult.error(() -> "Invalid map size for combiner condition, expected exactly one element");
         } else {
            Map.Entry entry = (Map.Entry)map.entrySet().iterator().next();
            return DataResult.success(new CombinedCondition((CombinedCondition.Operation)entry.getKey(), (List)entry.getValue()));
         }
      }, (condition) -> Map.of(condition.operation(), condition.terms()));
      return Codec.either(combinerCodec, KeyValueCondition.CODEC).flatComapMap((either) -> (Condition)either.map((l) -> l, (r) -> r), (condition) -> {
         Objects.requireNonNull(condition);
         int index$1 = 0;
         DataResult var10000;
         switch (condition.typeSwitch<invokedynamic>(condition, index$1)) {
            case 0:
               CombinedCondition combiner = (CombinedCondition)condition;
               var10000 = DataResult.success(Either.left(combiner));
               break;
            case 1:
               KeyValueCondition keyValue = (KeyValueCondition)condition;
               var10000 = DataResult.success(Either.right(keyValue));
               break;
            default:
               var10000 = DataResult.error(() -> "Unrecognized condition");
         }

         return var10000;
      });
   });

   Predicate instantiate(StateDefinition definition);
}
