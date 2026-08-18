package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class PlayerHeadBlockProfileFix extends NamedEntityFix {
   public PlayerHeadBlockProfileFix(final Schema outputSchema) {
      super(outputSchema, false, "PlayerHeadBlockProfileFix", References.BLOCK_ENTITY, "minecraft:skull");
   }

   protected Typed fix(final Typed entity) {
      return entity.update(DSL.remainderFinder(), this::fix);
   }

   private Dynamic fix(Dynamic entity) {
      Optional skullOwner = entity.get("SkullOwner").result();
      Optional extraType = entity.get("ExtraType").result();
      Optional profile = skullOwner.or(() -> extraType);
      if (profile.isEmpty()) {
         return entity;
      } else {
         entity = entity.remove("SkullOwner").remove("ExtraType");
         return entity.set("profile", ItemStackComponentizationFix.fixProfile((Dynamic)profile.get()));
      }
   }
}
