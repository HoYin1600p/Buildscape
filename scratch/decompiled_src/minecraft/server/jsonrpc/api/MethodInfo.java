package net.minecraft.server.jsonrpc.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public record MethodInfo(String description, Optional params, Optional result) {
   public MethodInfo(final String description, final @Nullable ParamInfo paramInfo, final @Nullable ResultInfo resultInfo) {
      this(description, Optional.ofNullable(paramInfo), Optional.ofNullable(resultInfo));
   }

   private static Optional toOptional(final List list) {
      return list.isEmpty() ? Optional.empty() : Optional.of((ParamInfo)list.getFirst());
   }

   private static List toList(final Optional opt) {
      return opt.isPresent() ? List.of((ParamInfo)opt.get()) : List.of();
   }

   private static Codec paramsTypedCodec() {
      return ParamInfo.typedCodec().codec().listOf().xmap(MethodInfo::toOptional, MethodInfo::toList);
   }

   private static MapCodec typedCodec() {
      return RecordCodecBuilder.mapCodec((i) -> i.group(Codec.STRING.fieldOf("description").forGetter(MethodInfo::description), paramsTypedCodec().fieldOf("params").forGetter(MethodInfo::params), ResultInfo.typedCodec().optionalFieldOf("result").forGetter(MethodInfo::result)).apply(i, MethodInfo::new));
   }

   public MethodInfo.Named named(final Identifier name) {
      return new MethodInfo.Named(name, this);
   }

   public static record Named(Identifier name, MethodInfo contents) {
      public static final Codec CODEC = typedCodec();

      public static Codec typedCodec() {
         return RecordCodecBuilder.create((i) -> i.group(Identifier.CODEC.fieldOf("name").forGetter(MethodInfo.Named::name), MethodInfo.typedCodec().forGetter(MethodInfo.Named::contents)).apply(i, MethodInfo.Named::new));
      }
   }
}
