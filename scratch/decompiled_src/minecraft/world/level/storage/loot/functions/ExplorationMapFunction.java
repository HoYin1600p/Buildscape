package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

public class ExplorationMapFunction extends LootItemConditionalFunction {
   public static final Holder DEFAULT_DECORATION = MapDecorationTypes.WOODLAND_MANSION;
   public static final byte DEFAULT_ZOOM = 2;
   public static final int DEFAULT_SEARCH_RADIUS = 50;
   public static final boolean DEFAULT_SKIP_EXISTING = true;
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(RegistryCodecs.holderSet(Registries.STRUCTURE).fieldOf("destination").forGetter((f) -> f.destination), MapDecorationType.CODEC.optionalFieldOf("decoration", DEFAULT_DECORATION).forGetter((f) -> f.mapDecoration), Codec.BYTE.optionalFieldOf("zoom", (byte)2).forGetter((f) -> f.zoom), Codec.INT.optionalFieldOf("search_radius", 50).forGetter((f) -> f.searchRadius), Codec.BOOL.optionalFieldOf("skip_existing_chunks", true).forGetter((f) -> f.skipKnownStructures))).apply(i, ExplorationMapFunction::new));
   private final HolderSet destination;
   private final Holder mapDecoration;
   private final byte zoom;
   private final int searchRadius;
   private final boolean skipKnownStructures;

   private ExplorationMapFunction(final Optional condition, final HolderSet destination, final Holder mapDecoration, final byte zoom, final int searchRadius, final boolean skipKnownStructures) {
      super(condition);
      this.destination = destination;
      this.mapDecoration = mapDecoration;
      this.zoom = zoom;
      this.searchRadius = searchRadius;
      this.skipKnownStructures = skipKnownStructures;
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public Set getReferencedContextParams() {
      return Set.of(LootContextParams.ORIGIN);
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      if (itemStack.isEmpty()) {
         return itemStack;
      } else {
         Vec3 lootPos = (Vec3)context.getOptionalParameter(LootContextParams.ORIGIN);
         if (lootPos == null) {
            return itemStack;
         } else {
            ServerLevel level = context.getLevel();
            BlockPos nearestMapStructure = level.findNearestMapStructure(this.destination, BlockPos.containing(lootPos), this.searchRadius, this.skipKnownStructures);
            if (nearestMapStructure == null) {
               return itemStack;
            } else {
               MapItem.applyNewSavedData(level, itemStack, nearestMapStructure.getX(), nearestMapStructure.getZ(), this.zoom, true, true);
               MapItem.renderBiomePreviewMap(level, itemStack);
               MapItemSavedData.addTargetDecoration(itemStack, nearestMapStructure, "+", this.mapDecoration);
               return itemStack;
            }
         }
      }
   }

   public static ExplorationMapFunction.Builder makeExplorationMap(final HolderSet destination) {
      return new ExplorationMapFunction.Builder(destination);
   }

   public static class Builder extends LootItemConditionalFunction.Builder {
      private final HolderSet destination;
      private Holder mapDecoration = ExplorationMapFunction.DEFAULT_DECORATION;
      private byte zoom = 2;
      private int searchRadius = 50;
      private boolean skipKnownStructures = true;

      public Builder(final HolderSet destination) {
         this.destination = destination;
      }

      protected ExplorationMapFunction.Builder getThis() {
         return this;
      }

      public ExplorationMapFunction.Builder setMapDecoration(final Holder mapDecoration) {
         this.mapDecoration = mapDecoration;
         return this;
      }

      public ExplorationMapFunction.Builder setZoom(final byte zoom) {
         this.zoom = zoom;
         return this;
      }

      public ExplorationMapFunction.Builder setSearchRadius(final int searchRadius) {
         this.searchRadius = searchRadius;
         return this;
      }

      public ExplorationMapFunction.Builder setSkipKnownStructures(final boolean skipKnownStructures) {
         this.skipKnownStructures = skipKnownStructures;
         return this;
      }

      public LootItemFunction build() {
         return new ExplorationMapFunction(this.getCondition(), this.destination, this.mapDecoration, this.zoom, this.searchRadius, this.skipKnownStructures);
      }
   }
}
