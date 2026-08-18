package net.minecraft.server.permissions;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public interface Permission {
   Codec FULL_CODEC = BuiltInRegistries.PERMISSION_TYPE.byNameCodec().dispatch(Permission::codec, (c) -> c);
   Codec CODEC = Codec.either(FULL_CODEC, Identifier.CODEC).xmap((e) -> (Permission)e.map((permission) -> permission, Permission.Atom::create), (permission) -> {
      Either var10000;
      if (permission instanceof Permission.Atom atom) {
         var10000 = Either.right(atom.id());
      } else {
         var10000 = Either.left(permission);
      }

      return var10000;
   });

   MapCodec codec();

   public static record Atom(Identifier id) implements Permission {
      public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Identifier.CODEC.fieldOf("id").forGetter(Permission.Atom::id)).apply(i, Permission.Atom::new));

      public MapCodec codec() {
         return MAP_CODEC;
      }

      public static Permission.Atom create(final String name) {
         return create(Identifier.withDefaultNamespace(name));
      }

      public static Permission.Atom create(final Identifier id) {
         return new Permission.Atom(id);
      }
   }

   public static record HasCommandLevel(PermissionLevel level) implements Permission {
      public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(PermissionLevel.CODEC.fieldOf("level").forGetter(Permission.HasCommandLevel::level)).apply(i, Permission.HasCommandLevel::new));

      public MapCodec codec() {
         return MAP_CODEC;
      }
   }
}
