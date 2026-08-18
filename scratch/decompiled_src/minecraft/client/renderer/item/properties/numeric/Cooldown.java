package net.minecraft.client.renderer.item.properties.numeric;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record Cooldown() implements RangeSelectItemModelProperty {
   public static final MapCodec MAP_CODEC = MapCodec.unit(new Cooldown());

   public float get(final ItemStack itemStack, final @Nullable ClientLevel level, final @Nullable ItemOwner owner, final int seed) {
      if (owner != null) {
         LivingEntity var6 = owner.asLivingEntity();
         if (var6 instanceof Player) {
            Player player = (Player)var6;
            return player.getCooldowns().getCooldownPercent(itemStack, 0.0F);
         }
      }

      return 0.0F;
   }

   public MapCodec type() {
      return MAP_CODEC;
   }
}
