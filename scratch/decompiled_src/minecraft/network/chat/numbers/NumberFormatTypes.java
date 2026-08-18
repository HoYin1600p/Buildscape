package net.minecraft.network.chat.numbers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class NumberFormatTypes {
   public static final MapCodec MAP_CODEC = BuiltInRegistries.NUMBER_FORMAT_TYPE.byNameCodec().dispatchMap(NumberFormat::type, NumberFormatType::mapCodec);
   public static final Codec CODEC = MAP_CODEC.codec();
   public static final StreamCodec STREAM_CODEC = ByteBufCodecs.registry(Registries.NUMBER_FORMAT_TYPE).dispatch(NumberFormat::type, NumberFormatType::streamCodec);
   public static final StreamCodec OPTIONAL_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs::optional);

   public static NumberFormatType bootstrap(final Registry registry) {
      Registry.register(registry, "blank", BlankFormat.TYPE);
      Registry.register(registry, "styled", StyledFormat.TYPE);
      return (NumberFormatType)Registry.register(registry, "fixed", FixedFormat.TYPE);
   }
}
