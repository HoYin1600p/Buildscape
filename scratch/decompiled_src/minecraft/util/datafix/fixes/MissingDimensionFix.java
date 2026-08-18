package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.FieldFinder;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.CompoundList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.List;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class MissingDimensionFix extends DataFix {
   public MissingDimensionFix(final Schema schema, final boolean changesType) {
      super(schema, changesType);
   }

   protected static Type fields(final String name, final Type type) {
      return DSL.and(DSL.field(name, type), DSL.remainderType());
   }

   protected static Type optionalFields(final String name, final Type type) {
      return DSL.and(DSL.optional(DSL.field(name, type)), DSL.remainderType());
   }

   protected static Type optionalFields(final String name1, final Type type1, final String name2, final Type type2) {
      return DSL.and(DSL.optional(DSL.field(name1, type1)), DSL.optional(DSL.field(name2, type2)), DSL.remainderType());
   }

   protected TypeRewriteRule makeRule() {
      Schema schema = this.getInputSchema();
      Type generatorType = DSL.taggedChoiceType("type", DSL.string(), ImmutableMap.of("minecraft:debug", DSL.remainderType(), "minecraft:flat", flatType(schema), "minecraft:noise", optionalFields("biome_source", DSL.taggedChoiceType("type", DSL.string(), ImmutableMap.of("minecraft:fixed", fields("biome", schema.getType(References.BIOME)), "minecraft:multi_noise", DSL.list(fields("biome", schema.getType(References.BIOME))), "minecraft:checkerboard", fields("biomes", DSL.list(schema.getType(References.BIOME))), "minecraft:vanilla_layered", DSL.remainderType(), "minecraft:the_end", DSL.remainderType())), "settings", DSL.or(DSL.string(), optionalFields("default_block", schema.getType(References.BLOCK_NAME), "default_fluid", schema.getType(References.BLOCK_NAME))))));
      CompoundList.CompoundListType dimensionsType = DSL.compoundList(NamespacedSchema.namespacedString(), fields("generator", generatorType));
      Type expectedDimensionsType = DSL.and(dimensionsType, DSL.remainderType());
      Type settings = schema.getType(References.WORLD_GEN_SETTINGS);
      FieldFinder dimensionsFinder = new FieldFinder("dimensions", expectedDimensionsType);
      if (!settings.findFieldType("dimensions").equals(expectedDimensionsType)) {
         throw new IllegalStateException();
      } else {
         OpticFinder dimensionListFinder = dimensionsType.finder();
         return this.fixTypeEverywhereTyped("MissingDimensionFix", settings, (input) -> input.updateTyped(dimensionsFinder, (dimensions) -> dimensions.updateTyped(dimensionListFinder, (generators) -> {
                  if (!(generators.getValue() instanceof List)) {
                     throw new IllegalStateException("List exptected");
                  } else if (((List)generators.getValue()).isEmpty()) {
                     Dynamic tag = (Dynamic)input.get(DSL.remainderFinder());
                     Dynamic newDimensions = this.recreateSettings(tag);
                     return (Typed)DataFixUtils.orElse(dimensionsType.readTyped(newDimensions).result().map(Pair::getFirst), generators);
                  } else {
                     return generators;
                  }
               })));
      }
   }

   protected static Type flatType(final Schema schema) {
      return optionalFields("settings", optionalFields("biome", schema.getType(References.BIOME), "layers", DSL.list(optionalFields("block", schema.getType(References.BLOCK_NAME)))));
   }

   private Dynamic recreateSettings(final Dynamic tag) {
      long seed = tag.get("seed").asLong(0L);
      return new Dynamic(tag.getOps(), WorldGenSettingsFix.vanillaLevels(tag, seed, WorldGenSettingsFix.defaultOverworld(tag, seed), false));
   }
}
