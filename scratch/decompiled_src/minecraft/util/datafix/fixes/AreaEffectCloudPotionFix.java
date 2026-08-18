package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class AreaEffectCloudPotionFix extends NamedEntityFix {
   public AreaEffectCloudPotionFix(final Schema outputSchema) {
      super(outputSchema, false, "AreaEffectCloudPotionFix", References.ENTITY, "minecraft:area_effect_cloud");
   }

   protected Typed fix(final Typed entity) {
      return entity.update(DSL.remainderFinder(), this::fix);
   }

   private Dynamic fix(Dynamic entity) {
      Optional color = entity.get("Color").result();
      Optional effects = entity.get("effects").result();
      Optional potion = entity.get("Potion").result();
      entity = entity.remove("Color").remove("effects").remove("Potion");
      if (color.isEmpty() && effects.isEmpty() && potion.isEmpty()) {
         return entity;
      } else {
         Dynamic potionContents = entity.emptyMap();
         if (color.isPresent()) {
            potionContents = potionContents.set("custom_color", (Dynamic)color.get());
         }

         if (effects.isPresent()) {
            potionContents = potionContents.set("custom_effects", (Dynamic)effects.get());
         }

         if (potion.isPresent()) {
            potionContents = potionContents.set("potion", (Dynamic)potion.get());
         }

         return entity.set("potion_contents", potionContents);
      }
   }
}
