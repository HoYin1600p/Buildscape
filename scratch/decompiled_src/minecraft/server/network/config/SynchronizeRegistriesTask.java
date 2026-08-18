package net.minecraft.server.network.config;

import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.configuration.ClientboundRegistryDataPacket;
import net.minecraft.network.protocol.configuration.ClientboundSelectKnownPacks;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.tags.TagNetworkSerialization;

public class SynchronizeRegistriesTask implements ConfigurationTask {
   public static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type("synchronize_registries");
   private final List requestedPacks;
   private final LayeredRegistryAccess registries;

   public SynchronizeRegistriesTask(final List knownPacks, final LayeredRegistryAccess registries) {
      this.requestedPacks = knownPacks;
      this.registries = registries;
   }

   public void start(final Consumer connection) {
      connection.accept(new ClientboundSelectKnownPacks(this.requestedPacks));
   }

   private void sendRegistries(final Consumer connection, final Set negotiatedPacks) {
      DynamicOps ops = this.registries.compositeAccess().createSerializationContext(NbtOps.INSTANCE);
      RegistrySynchronization.packRegistries(ops, this.registries.getAccessFrom(RegistryLayer.WORLD), negotiatedPacks, (registryKey, entries) -> connection.accept(new ClientboundRegistryDataPacket(registryKey, entries)));
      connection.accept(new ClientboundUpdateTagsPacket(TagNetworkSerialization.serializeTagsToNetwork(this.registries)));
   }

   public void handleResponse(final List acceptedPacks, final Consumer connection) {
      if (acceptedPacks.equals(this.requestedPacks)) {
         this.sendRegistries(connection, Set.copyOf(this.requestedPacks));
      } else {
         this.sendRegistries(connection, Set.of());
      }

   }

   public ConfigurationTask.Type type() {
      return TYPE;
   }
}
