package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record IsSelected() implements ConditionalItemModelProperty {
   public static final MapCodec MAP_CODEC = MapCodec.unit(new IsSelected());

   public boolean get(final ItemStack itemStack, final @Nullable ClientLevel level, final @Nullable LivingEntity owner, final int seed, final ItemDisplayContext displayContext) {
      if (owner instanceof LocalPlayer player) {
         if (player.getInventory().getSelectedItem() == itemStack) {
            return true;
         }
      }

      return false;
   }

   public MapCodec type() {
      return MAP_CODEC;
   }
}
