package net.minecraft.client.resources.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.io.Reader;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.slf4j.Logger;

public class BlockStateModelLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final FileToIdConverter BLOCKSTATE_LISTER = FileToIdConverter.json("blockstates");

   public static CompletableFuture loadBlockStates(final ResourceManager manager, final Executor executor) {
      Function definitionToBlockState = BlockStateDefinitions.definitionLocationToBlockStateMapper();
      return CompletableFuture.supplyAsync(() -> BLOCKSTATE_LISTER.listMatchingResourceStacks(manager), executor).thenCompose((resources) -> {
         List result = new ArrayList(resources.size());

         for(Map.Entry resourceStack : resources.entrySet()) {
            result.add(CompletableFuture.supplyAsync(() -> {
               Identifier stateDefinitionId = BLOCKSTATE_LISTER.fileToId((Identifier)resourceStack.getKey());
               StateDefinition stateDefinition = (StateDefinition)definitionToBlockState.apply(stateDefinitionId);
               if (stateDefinition == null) {
                  LOGGER.debug("Discovered unknown block state definition {}, ignoring", stateDefinitionId);
                  return null;
               } else {
                  List stack = (List)resourceStack.getValue();
                  List loadedStack = new ArrayList(stack.size());

                  for(Resource resource : stack) {
                     try {
                        Reader reader = resource.openAsReader();

                        try {
                           JsonElement element = StrictJsonParser.parse(reader);
                           BlockStateModelDispatcher definition = (BlockStateModelDispatcher)BlockStateModelDispatcher.CODEC.parse(JsonOps.INSTANCE, element).getOrThrow(JsonParseException::new);
                           loadedStack.add(new BlockStateModelLoader.LoadedBlockStateModelDispatcher(resource.sourcePackId(), definition));
                        } catch (Throwable var13) {
                           if (reader != null) {
                              try {
                                 reader.close();
                              } catch (Throwable var12) {
                                 var13.addSuppressed(var12);
                              }
                           }

                           throw var13;
                        }

                        if (reader != null) {
                           reader.close();
                        }
                     } catch (Exception var14) {
                        LOGGER.error("Failed to load blockstate definition {} from pack {}", new Object[]{stateDefinitionId, resource.sourcePackId(), var14});
                     }
                  }

                  try {
                     return loadBlockStateDefinitionStack(stateDefinitionId, stateDefinition, loadedStack);
                  } catch (Exception var11) {
                     LOGGER.error("Failed to load blockstate definition {}", stateDefinitionId, var11);
                     return null;
                  }
               }
            }, executor));
         }

         return Util.sequence(result).thenApply((partialMaps) -> {
            Map fullMap = new IdentityHashMap();

            for(BlockStateModelLoader.LoadedModels partialMap : partialMaps) {
               if (partialMap != null) {
                  fullMap.putAll(partialMap.models());
               }
            }

            return new BlockStateModelLoader.LoadedModels(fullMap);
         });
      });
   }

   private static BlockStateModelLoader.LoadedModels loadBlockStateDefinitionStack(final Identifier stateDefinitionId, final StateDefinition stateDefinition, final List definitionStack) {
      Map result = new IdentityHashMap();

      for(BlockStateModelLoader.LoadedBlockStateModelDispatcher definition : definitionStack) {
         result.putAll(definition.contents.instantiate(stateDefinition, () -> String.valueOf(stateDefinitionId) + "/" + definition.source));
      }

      return new BlockStateModelLoader.LoadedModels(result);
   }

   private static record LoadedBlockStateModelDispatcher(String source, BlockStateModelDispatcher contents) {
   }

   public static record LoadedModels(Map models) {
   }
}
