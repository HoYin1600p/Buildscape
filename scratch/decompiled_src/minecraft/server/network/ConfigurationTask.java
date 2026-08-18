package net.minecraft.server.network;

import java.util.function.Consumer;

public interface ConfigurationTask {
   void start(Consumer connection);

   default boolean tick() {
      return false;
   }

   ConfigurationTask.Type type();

   public static record Type(String id) {
      public String toString() {
         return this.id;
      }
   }
}
