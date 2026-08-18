package net.minecraft.server.network.config;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.configuration.ClientboundCodeOfConductPacket;
import net.minecraft.server.network.ConfigurationTask;

public class ServerCodeOfConductConfigurationTask implements ConfigurationTask {
   public static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type("server_code_of_conduct");
   private final Supplier codeOfConduct;

   public ServerCodeOfConductConfigurationTask(final Supplier codeOfConduct) {
      this.codeOfConduct = codeOfConduct;
   }

   public void start(final Consumer connection) {
      connection.accept(new ClientboundCodeOfConductPacket((String)this.codeOfConduct.get()));
   }

   public ConfigurationTask.Type type() {
      return TYPE;
   }
}
