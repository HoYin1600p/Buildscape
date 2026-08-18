package net.minecraft.world.level.gamerules;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jspecify.annotations.Nullable;

public final class GameRuleMap extends SavedData {
   public static final Codec CODEC = Codec.dispatchedMap(BuiltInRegistries.GAME_RULE.byNameCodec(), GameRule::valueCodec).xmap(GameRuleMap::ofTrusted, GameRuleMap::map);
   public static final SavedDataType TYPE = new SavedDataType(Identifier.withDefaultNamespace("game_rules"), GameRuleMap::of, CODEC, DataFixTypes.SAVED_DATA_GAME_RULES);
   private final Reference2ObjectMap map;

   private GameRuleMap(final Reference2ObjectMap map) {
      this.map = map;
   }

   private static GameRuleMap ofTrusted(final Map map) {
      return new GameRuleMap(new Reference2ObjectOpenHashMap(map));
   }

   public static GameRuleMap of() {
      return new GameRuleMap(new Reference2ObjectOpenHashMap());
   }

   public static GameRuleMap of(final Stream gameRuleTypeStream) {
      Reference2ObjectOpenHashMap map = new Reference2ObjectOpenHashMap();
      gameRuleTypeStream.forEach((gameRule) -> map.put(gameRule, gameRule.defaultValue()));
      return new GameRuleMap(map);
   }

   public static GameRuleMap copyOf(final GameRuleMap gameRuleMap) {
      return new GameRuleMap(new Reference2ObjectOpenHashMap(gameRuleMap.map));
   }

   public boolean has(final GameRule gameRule) {
      return this.map.containsKey(gameRule);
   }

   public @Nullable Object get(final GameRule gameRule) {
      return this.map.get(gameRule);
   }

   public void set(final GameRule gameRule, final Object value) {
      this.setDirty();
      this.map.put(gameRule, value);
   }

   public void reset(final GameRule gameRule) {
      this.set(gameRule, gameRule.defaultValue());
   }

   public @Nullable Object remove(final GameRule gameRule) {
      this.setDirty();
      return this.map.remove(gameRule);
   }

   public Set keySet() {
      return this.map.keySet();
   }

   public int size() {
      return this.map.size();
   }

   public String toString() {
      return this.map.toString();
   }

   public GameRuleMap withOther(final GameRuleMap other) {
      GameRuleMap result = copyOf(this);
      result.setFromIf(other, (r) -> true);
      return result;
   }

   public void setFromIf(final GameRuleMap other, final Predicate predicate) {
      for(GameRule gameRule : other.keySet()) {
         if (predicate.test(gameRule)) {
            setGameRule(other, gameRule, this);
         }
      }

   }

   private static void setGameRule(final GameRuleMap other, final GameRule gameRule, final GameRuleMap result) {
      result.set(gameRule, Objects.requireNonNull(other.get(gameRule)));
   }

   private Reference2ObjectMap map() {
      return this.map;
   }

   public boolean equals(final Object obj) {
      if (obj == this) {
         return true;
      } else if (obj != null && obj.getClass() == this.getClass()) {
         GameRuleMap that = (GameRuleMap)obj;
         return Objects.equals(this.map, that.map);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.map});
   }

   public static class Builder {
      private final Reference2ObjectMap map = new Reference2ObjectOpenHashMap();

      public GameRuleMap.Builder set(final GameRule gameRule, final Object value) {
         this.map.put(gameRule, value);
         return this;
      }

      public GameRuleMap build() {
         return new GameRuleMap(this.map);
      }
   }
}
