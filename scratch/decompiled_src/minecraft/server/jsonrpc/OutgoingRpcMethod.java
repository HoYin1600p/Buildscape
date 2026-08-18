package net.minecraft.server.jsonrpc;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.jsonrpc.api.MethodInfo;
import net.minecraft.server.jsonrpc.api.ParamInfo;
import net.minecraft.server.jsonrpc.api.ResultInfo;
import net.minecraft.server.jsonrpc.api.Schema;
import org.jspecify.annotations.Nullable;

public interface OutgoingRpcMethod {
   String NOTIFICATION_PREFIX = "notification/";

   MethodInfo info();

   OutgoingRpcMethod.Attributes attributes();

   default @Nullable JsonElement encodeParams(final Object params) {
      return null;
   }

   default @Nullable Object decodeResult(final JsonElement result) {
      return null;
   }

   static OutgoingRpcMethod.OutgoingRpcMethodBuilder notification() {
      return new OutgoingRpcMethod.OutgoingRpcMethodBuilder(OutgoingRpcMethod.ParmeterlessNotification::new);
   }

   static OutgoingRpcMethod.OutgoingRpcMethodBuilder notificationWithParams() {
      return new OutgoingRpcMethod.OutgoingRpcMethodBuilder(OutgoingRpcMethod.Notification::new);
   }

   static OutgoingRpcMethod.OutgoingRpcMethodBuilder request() {
      return new OutgoingRpcMethod.OutgoingRpcMethodBuilder(OutgoingRpcMethod.ParameterlessMethod::new);
   }

   static OutgoingRpcMethod.OutgoingRpcMethodBuilder requestWithParams() {
      return new OutgoingRpcMethod.OutgoingRpcMethodBuilder(OutgoingRpcMethod.Method::new);
   }

   public static record Attributes(boolean discoverable, boolean allowPreServerInit) {
   }

   @FunctionalInterface
   public interface Factory {
      OutgoingRpcMethod create(MethodInfo info, OutgoingRpcMethod.Attributes attributes);
   }

   public static record Method(MethodInfo info, OutgoingRpcMethod.Attributes attributes) implements OutgoingRpcMethod {
      public @Nullable JsonElement encodeParams(final Object params) {
         if (this.info.params().isEmpty()) {
            throw new IllegalStateException("Method defined as having no parameters");
         } else {
            return (JsonElement)((ParamInfo)this.info.params().get()).schema().codec().encodeStart(JsonOps.INSTANCE, params).getOrThrow();
         }
      }

      public Object decodeResult(final JsonElement result) {
         if (this.info.result().isEmpty()) {
            throw new IllegalStateException("Method defined as having no result");
         } else {
            return ((ResultInfo)this.info.result().get()).schema().codec().parse(JsonOps.INSTANCE, result).getOrThrow();
         }
      }
   }

   public static record Notification(MethodInfo info, OutgoingRpcMethod.Attributes attributes) implements OutgoingRpcMethod {
      public @Nullable JsonElement encodeParams(final Object params) {
         if (this.info.params().isEmpty()) {
            throw new IllegalStateException("Method defined as having no parameters");
         } else {
            return (JsonElement)((ParamInfo)this.info.params().get()).schema().codec().encodeStart(JsonOps.INSTANCE, params).getOrThrow();
         }
      }
   }

   public static class OutgoingRpcMethodBuilder {
      public static final OutgoingRpcMethod.Attributes DEFAULT_ATTRIBUTES = new OutgoingRpcMethod.Attributes(true, false);
      private final OutgoingRpcMethod.Factory method;
      private String description = "";
      private @Nullable ParamInfo paramInfo;
      private @Nullable ResultInfo resultInfo;
      private boolean allowPreServerInit = false;

      public OutgoingRpcMethodBuilder(final OutgoingRpcMethod.Factory method) {
         this.method = method;
      }

      public OutgoingRpcMethod.OutgoingRpcMethodBuilder description(final String description) {
         this.description = description;
         return this;
      }

      public OutgoingRpcMethod.OutgoingRpcMethodBuilder response(final String resultName, final Schema resultSchema) {
         this.resultInfo = new ResultInfo(resultName, resultSchema);
         return this;
      }

      public OutgoingRpcMethod.OutgoingRpcMethodBuilder param(final String paramName, final Schema paramSchema) {
         this.paramInfo = new ParamInfo(paramName, paramSchema);
         return this;
      }

      public OutgoingRpcMethod.OutgoingRpcMethodBuilder allowPreServerInit() {
         this.allowPreServerInit = true;
         return this;
      }

      private OutgoingRpcMethod build() {
         MethodInfo methodInfo = new MethodInfo(this.description, this.paramInfo, this.resultInfo);
         OutgoingRpcMethod.Attributes attributes;
         if (this.allowPreServerInit) {
            attributes = new OutgoingRpcMethod.Attributes(DEFAULT_ATTRIBUTES.discoverable(), true);
         } else {
            attributes = DEFAULT_ATTRIBUTES;
         }

         return this.method.create(methodInfo, attributes);
      }

      public Holder.Reference register(final String key) {
         return this.register(Identifier.withDefaultNamespace("notification/" + key));
      }

      private Holder.Reference register(final Identifier id) {
         return Registry.registerForHolder(BuiltInRegistries.OUTGOING_RPC_METHOD, id, this.build());
      }
   }

   public static record ParameterlessMethod(MethodInfo info, OutgoingRpcMethod.Attributes attributes) implements OutgoingRpcMethod {
      public Object decodeResult(final JsonElement result) {
         if (this.info.result().isEmpty()) {
            throw new IllegalStateException("Method defined as having no result");
         } else {
            return ((ResultInfo)this.info.result().get()).schema().codec().parse(JsonOps.INSTANCE, result).getOrThrow();
         }
      }
   }

   public static record ParmeterlessNotification(MethodInfo info, OutgoingRpcMethod.Attributes attributes) implements OutgoingRpcMethod {
   }
}
