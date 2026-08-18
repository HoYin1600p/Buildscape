package net.minecraft.commands.synchronization;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.FriendlyByteBuf;

public class SingletonArgumentInfo implements ArgumentTypeInfo {
   private final SingletonArgumentInfo.Template template;

   private SingletonArgumentInfo(final Function constructor) {
      this.template = new SingletonArgumentInfo.Template(constructor);
   }

   public static SingletonArgumentInfo contextFree(final Supplier constructor) {
      return new SingletonArgumentInfo((context) -> (ArgumentType)constructor.get());
   }

   public static SingletonArgumentInfo contextAware(final Function constructor) {
      return new SingletonArgumentInfo(constructor);
   }

   public void serializeToNetwork(final SingletonArgumentInfo.Template template, final FriendlyByteBuf out) {
   }

   public void serializeToJson(final SingletonArgumentInfo.Template template, final JsonObject out) {
   }

   public SingletonArgumentInfo.Template deserializeFromNetwork(final FriendlyByteBuf in) {
      return this.template;
   }

   public SingletonArgumentInfo.Template unpack(final ArgumentType argument) {
      return this.template;
   }

   public final class Template implements ArgumentTypeInfo.Template {
      private final Function constructor;

      public Template(final Function constructor) {
         Objects.requireNonNull(SingletonArgumentInfo.this);
         super();
         this.constructor = constructor;
      }

      public ArgumentType instantiate(final CommandBuildContext context) {
         return (ArgumentType)this.constructor.apply(context);
      }

      public ArgumentTypeInfo type() {
         return SingletonArgumentInfo.this;
      }
   }
}
