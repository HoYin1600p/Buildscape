package net.minecraft.tags;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.RegistryLayer;

public class TagNetworkSerialization {
   public static Map serializeTagsToNetwork(final LayeredRegistryAccess registries) {
      return (Map)RegistrySynchronization.networkSafeRegistries(registries).map((e) -> Pair.of(e.key(), serializeToNetwork(e.value()))).filter((e) -> !((TagNetworkSerialization.NetworkPayload)e.getSecond()).isEmpty()).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
   }

   private static TagNetworkSerialization.NetworkPayload serializeToNetwork(final Registry registry) {
      Map result = new HashMap();
      registry.getTags().forEach((tag) -> {
         IntList ids = new IntArrayList(tag.size());

         for(Holder holder : tag) {
            if (holder.kind() != Holder.Kind.REFERENCE) {
               throw new IllegalStateException("Can't serialize unregistered value " + String.valueOf(holder));
            }

            ids.add(registry.getId(holder.value()));
         }

         result.put(tag.key().location(), ids);
      });
      return new TagNetworkSerialization.NetworkPayload(result);
   }

   private static TagLoader.LoadResult deserializeTagsFromNetwork(final Registry registry, final TagNetworkSerialization.NetworkPayload payload) {
      ResourceKey registryKey = registry.key();
      Map tags = new HashMap();
      payload.tags.forEach((key, ids) -> {
         TagKey tagKey = TagKey.create(registryKey, key);
         List values = (List)ids.intStream().mapToObj(registry::get).flatMap(Optional::stream).collect(Collectors.toUnmodifiableList());
         tags.put(tagKey, values);
      });
      return new TagLoader.LoadResult(registryKey, tags);
   }

   public static record NetworkPayload(Map tags) {
      public static final TagNetworkSerialization.NetworkPayload EMPTY = new TagNetworkSerialization.NetworkPayload(Map.of());
      private static final StreamCodec ID_LIST_STREAM_CODEC = ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.collection(IntArrayList::new));
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, ID_LIST_STREAM_CODEC), TagNetworkSerialization.NetworkPayload::tags, TagNetworkSerialization.NetworkPayload::new);

      public boolean isEmpty() {
         return this.tags.isEmpty();
      }

      public int size() {
         return this.tags.size();
      }

      public TagLoader.LoadResult resolve(final Registry registry) {
         return TagNetworkSerialization.deserializeTagsFromNetwork(registry, this);
      }
   }
}
