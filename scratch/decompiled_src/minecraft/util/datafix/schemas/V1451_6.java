package net.minecraft.util.datafix.schemas;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.fixes.References;

public class V1451_6 extends NamespacedSchema {
   public static final String SPECIAL_OBJECTIVE_MARKER = "_special";
   protected static final Hook.HookFunction UNPACK_OBJECTIVE_ID = new Hook.HookFunction() {
      public Object apply(final DynamicOps ops, final Object value) {
         Dynamic input = new Dynamic(ops, value);
         return ((Dynamic)DataFixUtils.orElse(input.get("CriteriaName").asString().result().map((name) -> {
            int colonPos = name.indexOf(58);
            if (colonPos < 0) {
               return Pair.of("_special", name);
            } else {
               try {
                  Identifier statType = Identifier.bySeparator(name.substring(0, colonPos), '.');
                  Identifier statId = Identifier.bySeparator(name.substring(colonPos + 1), '.');
                  return Pair.of(statType.toString(), statId.toString());
               } catch (Exception var4) {
                  return Pair.of("_special", name);
               }
            }
         }).map((explodedId) -> input.set("CriteriaType", input.createMap(ImmutableMap.of(input.createString("type"), input.createString((String)explodedId.getFirst()), input.createString("id"), input.createString((String)explodedId.getSecond()))))), input)).getValue();
      }
   };
   protected static final Hook.HookFunction REPACK_OBJECTIVE_ID = new Hook.HookFunction() {
      public Object apply(final DynamicOps ops, final Object value) {
         Dynamic input = new Dynamic(ops, value);
         Optional repackedId = input.get("CriteriaType").get().result().flatMap((type) -> {
            Optional statType = type.get("type").asString().result();
            Optional statId = type.get("id").asString().result();
            if (statType.isPresent() && statId.isPresent()) {
               String unpackedType = (String)statType.get();
               return unpackedType.equals("_special") ? Optional.of(input.createString((String)statId.get())) : Optional.of(type.createString(V1451_6.packNamespacedWithDot(unpackedType) + ":" + V1451_6.packNamespacedWithDot((String)statId.get())));
            } else {
               return Optional.empty();
            }
         });
         return ((Dynamic)DataFixUtils.orElse(repackedId.map((id) -> input.set("CriteriaName", id).remove("CriteriaType")), input)).getValue();
      }
   };

   public V1451_6(final int versionKey, final Schema parent) {
      super(versionKey, parent);
   }

   public void registerTypes(final Schema schema, final Map entityTypes, final Map blockEntityTypes) {
      super.registerTypes(schema, entityTypes, blockEntityTypes);
      Supplier ITEM_STATS = () -> DSL.compoundList(References.ITEM_NAME.in(schema), DSL.constType(DSL.intType()));
      schema.registerType(false, References.STATS, () -> DSL.optionalFields("stats", DSL.optionalFields(new Pair[]{Pair.of("minecraft:mined", DSL.compoundList(References.BLOCK_NAME.in(schema), DSL.constType(DSL.intType()))), Pair.of("minecraft:crafted", (TypeTemplate)ITEM_STATS.get()), Pair.of("minecraft:used", (TypeTemplate)ITEM_STATS.get()), Pair.of("minecraft:broken", (TypeTemplate)ITEM_STATS.get()), Pair.of("minecraft:picked_up", (TypeTemplate)ITEM_STATS.get()), Pair.of("minecraft:dropped", (TypeTemplate)ITEM_STATS.get()), Pair.of("minecraft:killed", DSL.compoundList(References.ENTITY_NAME.in(schema), DSL.constType(DSL.intType()))), Pair.of("minecraft:killed_by", DSL.compoundList(References.ENTITY_NAME.in(schema), DSL.constType(DSL.intType()))), Pair.of("minecraft:custom", DSL.compoundList(DSL.constType(namespacedString()), DSL.constType(DSL.intType())))})));
      Map criterionTypes = createCriterionTypes(schema);
      schema.registerType(false, References.OBJECTIVE, () -> DSL.hook(DSL.optionalFields("CriteriaType", DSL.taggedChoiceLazy("type", DSL.string(), criterionTypes), "DisplayName", References.TEXT_COMPONENT.in(schema)), UNPACK_OBJECTIVE_ID, REPACK_OBJECTIVE_ID));
   }

   protected static Map createCriterionTypes(final Schema schema) {
      Supplier itemCriterion = () -> DSL.optionalFields("id", References.ITEM_NAME.in(schema));
      Supplier blockCriterion = () -> DSL.optionalFields("id", References.BLOCK_NAME.in(schema));
      Supplier entityCriterion = () -> DSL.optionalFields("id", References.ENTITY_NAME.in(schema));
      Map criterionTypes = Maps.newHashMap();
      criterionTypes.put("minecraft:mined", blockCriterion);
      criterionTypes.put("minecraft:crafted", itemCriterion);
      criterionTypes.put("minecraft:used", itemCriterion);
      criterionTypes.put("minecraft:broken", itemCriterion);
      criterionTypes.put("minecraft:picked_up", itemCriterion);
      criterionTypes.put("minecraft:dropped", itemCriterion);
      criterionTypes.put("minecraft:killed", entityCriterion);
      criterionTypes.put("minecraft:killed_by", entityCriterion);
      criterionTypes.put("minecraft:custom", (Supplier)() -> DSL.optionalFields("id", DSL.constType(namespacedString())));
      criterionTypes.put("_special", (Supplier)() -> DSL.optionalFields("id", DSL.constType(DSL.string())));
      return criterionTypes;
   }

   public static String packNamespacedWithDot(final String location) {
      Identifier parsedLoc = Identifier.tryParse(location);
      return parsedLoc != null ? parsedLoc.getNamespace() + "." + parsedLoc.getPath() : location;
   }
}
