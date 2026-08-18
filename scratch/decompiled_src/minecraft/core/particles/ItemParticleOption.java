package net.minecraft.core.particles;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;

public class ItemParticleOption implements ParticleOptions {
   private final ParticleType type;
   private final ItemStackTemplate itemStack;

   public static MapCodec codec(final ParticleType type) {
      return ItemStackTemplate.CODEC.xmap((stack) -> new ItemParticleOption(type, stack), (o) -> o.itemStack).fieldOf("item");
   }

   public static StreamCodec streamCodec(final ParticleType type) {
      return ItemStackTemplate.STREAM_CODEC.map((stack) -> new ItemParticleOption(type, stack), (o) -> o.itemStack);
   }

   public ItemParticleOption(final ParticleType type, final Item item) {
      this(type, new ItemStackTemplate(item));
   }

   public ItemParticleOption(final ParticleType type, final ItemStackTemplate itemStack) {
      this.type = type;
      this.itemStack = itemStack;
   }

   public ParticleType getType() {
      return this.type;
   }

   public ItemStackTemplate getItem() {
      return this.itemStack;
   }
}
