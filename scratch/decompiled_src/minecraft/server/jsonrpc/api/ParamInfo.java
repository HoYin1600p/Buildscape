package net.minecraft.server.jsonrpc.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ParamInfo(String name, Schema schema, boolean required) {
   public ParamInfo(final String name, final Schema schema) {
      this(name, schema, true);
   }

   public static MapCodec typedCodec() {
      return RecordCodecBuilder.mapCodec((i) -> i.group(Codec.STRING.fieldOf("name").forGetter(ParamInfo::name), Schema.typedCodec().fieldOf("schema").forGetter(ParamInfo::schema), Codec.BOOL.fieldOf("required").forGetter(ParamInfo::required)).apply(i, ParamInfo::new));
   }
}
