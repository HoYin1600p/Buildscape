package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.ExtraDataFixUtils;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class EntitySpawnerItemVariantComponentFix extends DataFix {
   public EntitySpawnerItemVariantComponentFix(final Schema outputSchema) {
      super(outputSchema, false);
   }

   public final TypeRewriteRule makeRule() {
      Type itemStackType = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder idFinder = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      OpticFinder componentsFinder = itemStackType.findField("components");
      return this.fixTypeEverywhereTyped("ItemStack bucket_entity_data variants to separate components", itemStackType, (input) -> {
         Typed var10000;
         switch ((String)input.getOptional(idFinder).map(Pair::getSecond).orElse("")) {
            case "minecraft:salmon_bucket":
               var10000 = input.updateTyped(componentsFinder, EntitySpawnerItemVariantComponentFix::fixSalmonBucket);
               break;
            case "minecraft:axolotl_bucket":
               var10000 = input.updateTyped(componentsFinder, EntitySpawnerItemVariantComponentFix::fixAxolotlBucket);
               break;
            case "minecraft:tropical_fish_bucket":
               var10000 = input.updateTyped(componentsFinder, EntitySpawnerItemVariantComponentFix::fixTropicalFishBucket);
               break;
            case "minecraft:painting":
               var10000 = input.updateTyped(componentsFinder, (components) -> Util.writeAndReadTypedOrThrow(components, components.getType(), EntitySpawnerItemVariantComponentFix::fixPainting));
               break;
            default:
               var10000 = input;
         }

         return var10000;
      });
   }

   private static String getBaseColor(final int packedVariant) {
      return ExtraDataFixUtils.dyeColorIdToName(packedVariant >> 16 & 255);
   }

   private static String getPatternColor(final int packedVariant) {
      return ExtraDataFixUtils.dyeColorIdToName(packedVariant >> 24 & 255);
   }

   private static String getPattern(final int packedVariant) {
      String var10000;
      switch (packedVariant & 65535) {
         case 1:
            var10000 = "flopper";
            break;
         case 256:
            var10000 = "sunstreak";
            break;
         case 257:
            var10000 = "stripey";
            break;
         case 512:
            var10000 = "snooper";
            break;
         case 513:
            var10000 = "glitter";
            break;
         case 768:
            var10000 = "dasher";
            break;
         case 769:
            var10000 = "blockfish";
            break;
         case 1024:
            var10000 = "brinely";
            break;
         case 1025:
            var10000 = "betty";
            break;
         case 1280:
            var10000 = "spotty";
            break;
         case 1281:
            var10000 = "clayfish";
            break;
         default:
            var10000 = "kob";
      }

      return var10000;
   }

   private static Dynamic fixTropicalFishBucket(final Dynamic remainder, final Dynamic bucketData) {
      Optional oldVariant = bucketData.get("BucketVariantTag").asNumber().result();
      if (oldVariant.isEmpty()) {
         return remainder;
      } else {
         int packedVariant = ((Number)oldVariant.get()).intValue();
         String pattern = getPattern(packedVariant);
         String baseColor = getBaseColor(packedVariant);
         String patternColor = getPatternColor(packedVariant);
         return remainder.update("minecraft:bucket_entity_data", (b) -> b.remove("BucketVariantTag")).set("minecraft:tropical_fish/pattern", remainder.createString(pattern)).set("minecraft:tropical_fish/base_color", remainder.createString(baseColor)).set("minecraft:tropical_fish/pattern_color", remainder.createString(patternColor));
      }
   }

   private static Dynamic fixAxolotlBucket(final Dynamic remainder, final Dynamic bucketData) {
      Optional oldVariant = bucketData.get("Variant").asNumber().result();
      if (oldVariant.isEmpty()) {
         return remainder;
      } else {
         String var10000;
         switch (((Number)oldVariant.get()).intValue()) {
            case 1:
               var10000 = "wild";
               break;
            case 2:
               var10000 = "gold";
               break;
            case 3:
               var10000 = "cyan";
               break;
            case 4:
               var10000 = "blue";
               break;
            default:
               var10000 = "lucy";
         }

         String newVariant = var10000;
         return remainder.update("minecraft:bucket_entity_data", (b) -> b.remove("Variant")).set("minecraft:axolotl/variant", remainder.createString(newVariant));
      }
   }

   private static Dynamic fixSalmonBucket(final Dynamic remainder, final Dynamic bucketData) {
      Optional type = bucketData.get("type").result();
      return type.isEmpty() ? remainder : remainder.update("minecraft:bucket_entity_data", (b) -> b.remove("type")).set("minecraft:salmon/size", (Dynamic)type.get());
   }

   private static Dynamic fixPainting(Dynamic components) {
      Optional entityData = components.get("minecraft:entity_data").result();
      if (entityData.isEmpty()) {
         return components;
      } else if (((Dynamic)entityData.get()).get("id").asString().result().filter((id) -> id.equals("minecraft:painting")).isEmpty()) {
         return components;
      } else {
         Optional result = ((Dynamic)entityData.get()).get("variant").result();
         Dynamic entityDataRemainder = ((Dynamic)entityData.get()).remove("variant");
         if (entityDataRemainder.remove("id").equals(entityDataRemainder.emptyMap())) {
            components = components.remove("minecraft:entity_data");
         } else {
            components = components.set("minecraft:entity_data", entityDataRemainder);
         }

         if (result.isPresent()) {
            components = components.set("minecraft:painting/variant", (Dynamic)result.get());
         }

         return components;
      }
   }

   @FunctionalInterface
   private interface Fixer extends Function {
      default Typed apply(final Typed components) {
         return components.update(DSL.remainderFinder(), this::fixRemainder);
      }

      default Dynamic fixRemainder(final Dynamic remainder) {
         return (Dynamic)remainder.get("minecraft:bucket_entity_data").result().map((bucketData) -> this.fixRemainder(remainder, bucketData)).orElse(remainder);
      }

      Dynamic fixRemainder(Dynamic remainder, Dynamic bucketData);
   }
}
