package net.minecraft.core;

import com.mojang.serialization.DynamicOps;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.packs.repository.KnownPack;

public class RegistrySynchronization {
   private static final Set NETWORKABLE_REGISTRIES = (Set)RegistryDataLoader.SYNCHRONIZED_REGISTRIES.stream().map(RegistryDataLoader.RegistryData::key).collect(Collectors.toUnmodifiableSet());

   public static void packRegistries(final DynamicOps ops, final RegistryAccess registries, final Set clientKnownPacks, final BiConsumer output) {
      RegistryDataLoader.SYNCHRONIZED_REGISTRIES.forEach((registryEntry) -> packRegistry(ops, registryEntry, registries, clientKnownPacks, output));
   }

   private static void packRegistry(final DynamicOps ops, final RegistryDataLoader.RegistryData registryData, final RegistryAccess registries, final Set clientKnownPacks, final BiConsumer output) {
      registries.lookup(registryData.key()).ifPresent((registry) -> {
         List packedElements = new ArrayList(registry.size());
         registry.listElements().forEach((element) -> {
            boolean canSkipContents = registry.registrationInfo(element.key()).flatMap(RegistrationInfo::knownPackInfo).filter(clientKnownPacks::contains).isPresent();
            Optional contents;
            if (canSkipContents) {
               contents = Optional.empty();
            } else {
               Tag encodedElement = (Tag)registryData.elementCodec().encodeStart(ops, element.value()).getOrThrow((s) -> new IllegalArgumentException("Failed to serialize " + String.valueOf(element.key()) + ": " + s));
               contents = Optional.of(encodedElement);
            }

            packedElements.add(new RegistrySynchronization.PackedRegistryEntry(element.key().identifier(), contents));
         });
         output.accept(registry.key(), packedElements);
      });
   }

   private static Stream ownedNetworkableRegistries(final RegistryAccess access) {
      return access.registries().filter((e) -> isNetworkable(e.key()));
   }

   public static Stream networkedRegistries(final LayeredRegistryAccess registries) {
      return ownedNetworkableRegistries(registries.getAccessFrom(RegistryLayer.WORLD));
   }

   public static Stream networkSafeRegistries(final LayeredRegistryAccess registries) {
      Stream staticRegistries = registries.getLayer(RegistryLayer.STATIC).registries();
      Stream networkedRegistries = networkedRegistries(registries);
      return Stream.concat(networkedRegistries, staticRegistries);
   }

   public static boolean isNetworkable(final ResourceKey key) {
      return NETWORKABLE_REGISTRIES.contains(key);
   }

   public static record PackedRegistryEntry(Identifier id, Optional data) {
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(Identifier.STREAM_CODEC, RegistrySynchronization.PackedRegistryEntry::id, ByteBufCodecs.TAG.apply(ByteBufCodecs::optional), RegistrySynchronization.PackedRegistryEntry::data, RegistrySynchronization.PackedRegistryEntry::new);
   }
}
