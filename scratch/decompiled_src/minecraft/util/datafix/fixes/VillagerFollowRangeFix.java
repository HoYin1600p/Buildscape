package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class VillagerFollowRangeFix extends NamedEntityFix {
   private static final double ORIGINAL_VALUE = 16.0D;
   private static final double NEW_BASE_VALUE = 48.0D;

   public VillagerFollowRangeFix(final Schema outputSchema) {
      super(outputSchema, false, "Villager Follow Range Fix", References.ENTITY, "minecraft:villager");
   }

   protected Typed fix(final Typed entity) {
      return entity.update(DSL.remainderFinder(), VillagerFollowRangeFix::fixValue);
   }

   private static Dynamic fixValue(final Dynamic tag) {
      return tag.update("Attributes", (attributes) -> tag.createList(attributes.asStream().map((attribute) -> attribute.get("Name").asString("").equals("generic.follow_range") && attribute.get("Base").asDouble(0.0D) == 16.0D ? attribute.set("Base", attribute.createDouble(48.0D)) : attribute)));
   }
}
