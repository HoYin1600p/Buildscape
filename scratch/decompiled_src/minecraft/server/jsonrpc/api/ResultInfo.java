package net.minecraft.server.jsonrpc.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ResultInfo(String name, Schema schema) {
   public static Codec typedCodec() {
      return RecordCodecBuilder.create((i) -> i.group(Codec.STRING.fieldOf("name").forGetter(ResultInfo::name), Schema.typedCodec().fieldOf("schema").forGetter(ResultInfo::schema)).apply(i, ResultInfo::new));
   }
}
