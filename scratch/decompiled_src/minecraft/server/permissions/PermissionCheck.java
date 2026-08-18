package net.minecraft.server.permissions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;

public interface PermissionCheck {
   Codec CODEC = BuiltInRegistries.PERMISSION_CHECK_TYPE.byNameCodec().dispatch(PermissionCheck::codec, (c) -> c);

   boolean check(PermissionSet source);

   MapCodec codec();

   public static class AlwaysPass implements PermissionCheck {
      public static final PermissionCheck.AlwaysPass INSTANCE = new PermissionCheck.AlwaysPass();
      public static final MapCodec MAP_CODEC = MapCodec.unit(INSTANCE);

      private AlwaysPass() {
      }

      public boolean check(final PermissionSet source) {
         return true;
      }

      public MapCodec codec() {
         return MAP_CODEC;
      }
   }

   public static record Require(Permission permission) implements PermissionCheck {
      public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Permission.CODEC.fieldOf("permission").forGetter(PermissionCheck.Require::permission)).apply(i, PermissionCheck.Require::new));

      public MapCodec codec() {
         return MAP_CODEC;
      }

      public boolean check(final PermissionSet source) {
         return source.hasPermission(this.permission);
      }
   }
}
