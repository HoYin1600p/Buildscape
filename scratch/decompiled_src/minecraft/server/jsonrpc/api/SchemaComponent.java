package net.minecraft.server.jsonrpc.api;

import java.net.URI;

public record SchemaComponent(String name, URI ref, Schema schema) {
   public Schema asRef() {
      return Schema.ofRef(this.ref, this.schema.codec());
   }

   public Schema asArray() {
      return Schema.arrayOf(this.asRef(), this.schema.codec());
   }
}
