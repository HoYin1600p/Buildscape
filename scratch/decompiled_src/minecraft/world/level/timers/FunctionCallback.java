package net.minecraft.world.level.timers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;

public record FunctionCallback(Identifier functionId) implements TimerCallback {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Identifier.CODEC.fieldOf("id").forGetter(FunctionCallback::functionId)).apply(i, FunctionCallback::new));

   public void handle(final MinecraftServer server, final TimerQueue queue, final long time) {
      ServerFunctionManager functionManager = server.getFunctions();
      functionManager.get(this.functionId).ifPresent((function) -> functionManager.execute(function, functionManager.getGameLoopSender()));
   }

   public MapCodec codec() {
      return CODEC;
   }
}
