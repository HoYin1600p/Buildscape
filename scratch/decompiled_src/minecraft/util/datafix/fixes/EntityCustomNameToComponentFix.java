package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.ExtraDataFixUtils;
import net.minecraft.util.datafix.LegacyComponentDataFixUtils;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class EntityCustomNameToComponentFix extends DataFix {
   public EntityCustomNameToComponentFix(final Schema outputSchema) {
      super(outputSchema, true);
   }

   public TypeRewriteRule makeRule() {
      Type entityType = this.getInputSchema().getType(References.ENTITY);
      Type newEntityType = this.getOutputSchema().getType(References.ENTITY);
      OpticFinder idF = DSL.fieldFinder("id", NamespacedSchema.namespacedString());
      OpticFinder customNameF = entityType.findField("CustomName");
      Type newCustomNameType = newEntityType.findFieldType("CustomName");
      return this.fixTypeEverywhereTyped("EntityCustomNameToComponentFix", entityType, newEntityType, (entity) -> fixEntity(entity, newEntityType, idF, customNameF, newCustomNameType));
   }

   private static Typed fixEntity(final Typed entity, final Type newEntityType, final OpticFinder idF, final OpticFinder customNameF, final Type newCustomNameType) {
      Optional customName = entity.getOptional(customNameF);
      if (customName.isEmpty()) {
         return ExtraDataFixUtils.cast(newEntityType, entity);
      } else if (((String)customName.get()).isEmpty()) {
         return Util.writeAndReadTypedOrThrow(entity, newEntityType, (dynamic) -> dynamic.remove("CustomName"));
      } else {
         String id = (String)entity.getOptional(idF).orElse("");
         Dynamic component = fixCustomName(entity.getOps(), (String)customName.get(), id);
         return entity.set(customNameF, Util.readTypedOrThrow(newCustomNameType, component));
      }
   }

   private static Dynamic fixCustomName(final DynamicOps ops, final String customName, final String id) {
      return "minecraft:commandblock_minecart".equals(id) ? new Dynamic(ops, ops.createString(customName)) : LegacyComponentDataFixUtils.createPlainTextComponent(ops, customName);
   }
}
