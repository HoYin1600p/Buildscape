package net.minecraft.world.level.timers;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

public class TimerCallbacks {
   public static final TimerCallbacks SERVER_CALLBACKS = (new TimerCallbacks()).register(Identifier.withDefaultNamespace("function"), FunctionCallback.CODEC).register(Identifier.withDefaultNamespace("function_tag"), FunctionTagCallback.CODEC);
   private final ExtraCodecs.LateBoundIdMapper idMapper = new ExtraCodecs.LateBoundIdMapper();
   private final Codec codec = this.idMapper.codec(Identifier.CODEC).dispatch(TimerCallback::codec, Function.identity());

   public TimerCallbacks register(final Identifier id, final MapCodec codec) {
      this.idMapper.put(id, codec);
      return this;
   }

   public Codec codec() {
      return this.codec;
   }
}
