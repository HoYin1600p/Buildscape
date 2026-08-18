package net.minecraft.advancements;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public record AdvancementHolder(Identifier id, Advancement value) {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(Identifier.STREAM_CODEC, AdvancementHolder::id, Advancement.STREAM_CODEC, AdvancementHolder::value, AdvancementHolder::new);
   public static final StreamCodec LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());

   public void register(final BootstrapContext output) {
      output.register(ResourceKey.create(Registries.ADVANCEMENT, this.id), this.value);
   }

   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      } else {
         if (obj instanceof AdvancementHolder) {
            AdvancementHolder holder = (AdvancementHolder)obj;
            if (this.id.equals(holder.id)) {
               return true;
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public String toString() {
      return this.id.toString();
   }
}
