package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.level.WorldDataConfiguration;

public class WorldLoader {
   public static CompletableFuture load(final WorldLoader.InitConfig config, final WorldLoader.WorldDataSupplier worldDataSupplier, final WorldLoader.ResultFactory resultFactory, final Executor backgroundExecutor, final Executor mainThreadExecutor) {
      return CompletableFuture.supplyAsync(config.packConfig::createResourceManager, mainThreadExecutor).thenComposeAsync((packsAndResourceManager) -> {
         CloseableResourceManager resources = (CloseableResourceManager)packsAndResourceManager.getSecond();
         LayeredRegistryAccess initialLayers = RegistryLayer.createRegistryAccess();
         List staticLayerTags = TagLoader.loadTagsForExistingRegistries(resources, initialLayers.getLayer(RegistryLayer.STATIC));
         RegistryAccess.Frozen worldLoadContext = initialLayers.getAccessForLoading(RegistryLayer.WORLD);
         List worldContextRegistries = TagLoader.buildUpdatedLookups(worldLoadContext, staticLayerTags);
         return RegistryDataLoader.load(resources, worldContextRegistries, RegistryDataLoader.WORLD_REGISTRIES, backgroundExecutor).thenComposeAsync((loadedWorldRegistries) -> {
            List dimensionContextRegistries = Stream.concat(worldContextRegistries.stream(), loadedWorldRegistries.listRegistries()).toList();
            return RegistryDataLoader.load(resources, dimensionContextRegistries, RegistryDataLoader.DIMENSION_REGISTRIES, backgroundExecutor).thenComposeAsync((initialWorldgenDimensions) -> {
               WorldDataConfiguration worldDataConfiguration = (WorldDataConfiguration)packsAndResourceManager.getFirst();
               HolderLookup.Provider dimensionContextProvider = HolderLookup.Provider.create(dimensionContextRegistries.stream());
               WorldLoader.DataLoadOutput worldDataAndRegistries = worldDataSupplier.get(new WorldLoader.DataLoadContext(resources, worldDataConfiguration, dimensionContextProvider, initialWorldgenDimensions));
               LayeredRegistryAccess resourcesLoadContext = initialLayers.replaceFrom(RegistryLayer.WORLD, loadedWorldRegistries, worldDataAndRegistries.finalDimensions);
               return ReloadableServerResources.loadResources(resources, resourcesLoadContext, staticLayerTags, worldDataConfiguration.enabledFeatures(), config.commandSelection(), config.functionCompilationPermissions(), backgroundExecutor, mainThreadExecutor).whenComplete((managers, throwable) -> {
                  if (throwable != null) {
                     resources.close();
                  }

               }).thenApplyAsync((managers) -> {
                  managers.updateComponentsAndStaticRegistryTags();
                  return resultFactory.create(resources, managers, resourcesLoadContext, worldDataAndRegistries.cookie);
               }, mainThreadExecutor);
            }, backgroundExecutor);
         }, backgroundExecutor);
      }, backgroundExecutor);
   }

   public static record DataLoadContext(ResourceManager resources, WorldDataConfiguration dataConfiguration, HolderLookup.Provider datapackWorldRegistries, RegistryAccess.Frozen datapackDimensions) {
   }

   public static record DataLoadOutput(Object cookie, RegistryAccess.Frozen finalDimensions) {
   }

   public static record InitConfig(WorldLoader.PackConfig packConfig, Commands.CommandSelection commandSelection, PermissionSet functionCompilationPermissions) {
   }

   public static record PackConfig(PackRepository packRepository, WorldDataConfiguration initialDataConfig, boolean safeMode, boolean initMode) {
      public Pair createResourceManager() {
         WorldDataConfiguration newPackConfig = MinecraftServer.configurePackRepository(this.packRepository, this.initialDataConfig, this.initMode, this.safeMode);
         List openedPacks = this.packRepository.openAllSelected();
         CloseableResourceManager resources = new MultiPackResourceManager(PackType.SERVER_DATA, openedPacks);
         return Pair.of(newPackConfig, resources);
      }
   }

   @FunctionalInterface
   public interface ResultFactory {
      Object create(CloseableResourceManager resources, ReloadableServerResources managers, LayeredRegistryAccess registries, Object cookie);
   }

   @FunctionalInterface
   public interface WorldDataSupplier {
      WorldLoader.DataLoadOutput get(WorldLoader.DataLoadContext context);
   }
}
