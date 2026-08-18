package net.minecraft.world.level.block.state.predicate;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

public class BlockStatePredicate implements Predicate {
   public static final Predicate ANY = (input) -> true;
   private final StateDefinition definition;
   private final Map properties = Maps.newHashMap();

   private BlockStatePredicate(final StateDefinition definition) {
      this.definition = definition;
   }

   public static BlockStatePredicate forBlock(final Block block) {
      return new BlockStatePredicate(block.getStateDefinition());
   }

   public boolean test(final @Nullable BlockState input) {
      if (input != null && input.getBlock().equals(this.definition.getOwner())) {
         if (this.properties.isEmpty()) {
            return true;
         } else {
            for(Map.Entry entry : this.properties.entrySet()) {
               if (!this.applies(input, (Property)entry.getKey(), (Predicate)entry.getValue())) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   protected boolean applies(final BlockState input, final Property key, final Predicate predicate) {
      Comparable value = (T)input.getValue(key);
      return predicate.test(value);
   }

   public BlockStatePredicate where(final Property property, final Predicate predicate) {
      if (!this.definition.getProperties().contains(property)) {
         throw new IllegalArgumentException(String.valueOf(this.definition) + " cannot support property " + String.valueOf(property));
      } else {
         this.properties.put(property, predicate);
         return this;
      }
   }
}
