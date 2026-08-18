package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.tags.TagLoader;
import org.slf4j.Logger;

public class ServerFunctionLibrary implements PreparableReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final ResourceKey TYPE_KEY = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("function"));
   private static final FileToIdConverter LISTER = new FileToIdConverter(Registries.elementsDirPath(TYPE_KEY), ".mcfunction");
   private volatile Map functions = ImmutableMap.of();
   private final TagLoader tagsLoader = new TagLoader((id, required) -> this.getFunction(id), Registries.tagsDirPath(TYPE_KEY));
   private volatile Map tags = Map.of();
   private final PermissionSet functionCompilationPermissions;
   private final CommandDispatcher dispatcher;

   public Optional getFunction(final Identifier id) {
      return Optional.ofNullable((CommandFunction)this.functions.get(id));
   }

   public Map getFunctions() {
      return this.functions;
   }

   public List getTag(final Identifier tag) {
      return (List)this.tags.getOrDefault(tag, List.of());
   }

   public Iterable getAvailableTags() {
      return this.tags.keySet();
   }

   public ServerFunctionLibrary(final PermissionSet functionCompilationPermissions, final CommandDispatcher dispatcher) {
      this.functionCompilationPermissions = functionCompilationPermissions;
      this.dispatcher = dispatcher;
   }

   public CompletableFuture reload(final PreparableReloadListener.SharedState currentReload, final Executor taskExecutor, final PreparableReloadListener.PreparationBarrier preparationBarrier, final Executor reloadExecutor) {
      ResourceManager manager = currentReload.resourceManager();
      CompletableFuture tags = CompletableFuture.supplyAsync(() -> this.tagsLoader.load(manager), taskExecutor);
      CompletableFuture functions = CompletableFuture.supplyAsync(() -> LISTER.listMatchingResources(manager), taskExecutor).thenCompose((functionsToLoad) -> {
         Map result = Maps.newHashMap();
         CommandSourceStack compilationContext = Commands.createCompilationContext(this.functionCompilationPermissions);

         for(Map.Entry entry : functionsToLoad.entrySet()) {
            Identifier resourceId = (Identifier)entry.getKey();
            Identifier id = LISTER.fileToId(resourceId);
            result.put(id, CompletableFuture.supplyAsync(() -> {
               List lines = readLines((Resource)entry.getValue());
               return CommandFunction.fromLines(id, this.dispatcher, compilationContext, lines);
            }, taskExecutor));
         }

         CompletableFuture[] futuresToCollect = (CompletableFuture[])result.values().toArray(new CompletableFuture[0]);
         return CompletableFuture.allOf(futuresToCollect).handle((ignore, throwable) -> result);
      });
      return tags.thenCombine(functions, Pair::of).thenCompose(preparationBarrier::wait).thenAcceptAsync((data) -> {
         Map functionFutures = (Map)data.getSecond();
         ImmutableMap.Builder newFunctions = ImmutableMap.builder();
         functionFutures.forEach((id, functionFuture) -> functionFuture.handle((function, throwable) -> {
               if (throwable != null) {
                  LOGGER.error("Failed to load function {}", id, throwable);
               } else {
                  newFunctions.put(id, function);
               }

               return null;
            }).join());
         this.functions = newFunctions.build();
         this.tags = this.tagsLoader.build((Map)data.getFirst());
      }, reloadExecutor);
   }

   private static List readLines(final Resource resource) {
      try {
         BufferedReader reader = resource.openAsReader();

         List var2;
         try {
            var2 = reader.lines().toList();
         } catch (Throwable var5) {
            if (reader != null) {
               try {
                  reader.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (reader != null) {
            reader.close();
         }

         return var2;
      } catch (IOException var6) {
         throw new CompletionException(var6);
      }
   }
}
