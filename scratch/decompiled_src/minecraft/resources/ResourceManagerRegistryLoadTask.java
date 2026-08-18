package net.minecraft.resources;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.Util;
import net.minecraft.util.thread.ParallelMapTransform;

public class ResourceManagerRegistryLoadTask extends RegistryLoadTask {
   private static final Function REGISTRATION_INFO_CACHE = Util.memoize((Function)((knownPack) -> {
      Lifecycle lifecycle = (Lifecycle)knownPack.map(KnownPack::isVanilla).map((info) -> Lifecycle.stable()).orElse(Lifecycle.experimental());
      return new RegistrationInfo(knownPack, lifecycle);
   }));
   private final ResourceManager resourceManager;

   public ResourceManagerRegistryLoadTask(final RegistryDataLoader.RegistryData data, final Lifecycle lifecycle, final Map loadingErrors, final ResourceManager resourceManager) {
      super(data, lifecycle, loadingErrors);
      this.resourceManager = resourceManager;
   }

   public CompletableFuture load(final RegistryOps.RegistryInfoLookup context, final Executor executor) {
      FileToIdConverter lister = FileToIdConverter.registry(this.registryKey());
      return CompletableFuture.supplyAsync(() -> lister.listMatchingResources(this.resourceManager), executor).thenCompose((registryResources) -> {
         RegistryOps ops = RegistryOps.create(JsonOps.INSTANCE, context);
         return ParallelMapTransform.schedule(registryResources, (resourceId, thunk) -> {
            ResourceKey elementKey = ResourceKey.create(this.registryKey(), lister.fileToId(resourceId));
            RegistrationInfo registrationInfo = (RegistrationInfo)REGISTRATION_INFO_CACHE.apply(thunk.knownPackInfo());
            return new RegistryLoadTask.PendingRegistration(elementKey, RegistryLoadTask.PendingRegistration.loadFromResource(this.data.elementCodec(), ops, elementKey, thunk), registrationInfo);
         }, executor);
      }).thenAcceptAsync((loadedEntries) -> {
         this.registerElements(loadedEntries.entrySet().stream().sorted(Entry.comparingByKey()).map(Map.Entry::getValue));
         TagLoader.ElementLookup tagElementLookup = TagLoader.ElementLookup.fromGetters(this.registryKey(), this.concurrentRegistrationGetter, this.readOnlyRegistry());
         Map pendingTags = TagLoader.loadTagsForRegistry(this.resourceManager, this.registryKey(), tagElementLookup);
         this.registerTags(pendingTags);
      }, executor);
   }
}
