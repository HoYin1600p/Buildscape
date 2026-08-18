package net.minecraft.world.attribute.modifier;

import com.mojang.serialization.Codec;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.LerpFunction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

public interface MobSpawnSettingsModifier extends AttributeModifier {
   static MobSpawnSettingsModifier overlay() {
      return MobSpawnSettingsModifier.Overlay.INSTANCE;
   }

   default Codec argumentCodec(final EnvironmentAttribute attribute) {
      return attribute.valueCodec();
   }

   default LerpFunction argumentKeyframeLerp(final EnvironmentAttribute attribute) {
      return attribute.type().keyframeLerp();
   }

   public static record Overlay() implements MobSpawnSettingsModifier {
      private static final MobSpawnSettingsModifier.Overlay INSTANCE = new MobSpawnSettingsModifier.Overlay();
      private static final MobCategory[] ALL_CATEGORIES = MobCategory.values();

      public MobSpawnSettings apply(final MobSpawnSettings first, final MobSpawnSettings second) {
         Set firstCategories = first.definedCategories();
         Set secondCategories = second.definedCategories();
         Map firstSpawnCosts = first.allSpawnCosts();
         Map secondSpawnCosts = second.allSpawnCosts();
         if (firstCategories.isEmpty() && firstSpawnCosts.isEmpty()) {
            return second;
         } else if (secondCategories.isEmpty() && secondSpawnCosts.isEmpty()) {
            return first;
         } else if (secondCategories.containsAll(firstCategories) && secondSpawnCosts.keySet().containsAll(firstSpawnCosts.keySet())) {
            return second;
         } else {
            MobSpawnSettings.Builder builder = new MobSpawnSettings.Builder();

            for(MobCategory category : ALL_CATEGORIES) {
               WeightedList secondSpawns = second.getMobsInCategory(category);
               if (secondSpawns != null) {
                  builder.addAllSpawns(category, secondSpawns);
               } else {
                  WeightedList firstSpawns = first.getMobsInCategory(category);
                  if (firstSpawns != null) {
                     builder.addAllSpawns(category, firstSpawns);
                  }
               }
            }

            return builder.addAllCosts(firstSpawnCosts).addAllCosts(secondSpawnCosts).build();
         }
      }
   }
}
