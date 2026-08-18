package net.minecraft.server.packs.resources;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.Unit;
import org.slf4j.Logger;

public class ReloadableResourceManager implements AutoCloseable, ResourceManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private CloseableResourceManager resources;
   private final List listeners = Lists.newArrayList();
   private final PackType type;

   public ReloadableResourceManager(final PackType type) {
      this.type = type;
      this.resources = new MultiPackResourceManager(type, List.of());
   }

   public void close() {
      this.resources.close();
   }

   public void registerReloadListener(final PreparableReloadListener listener) {
      this.listeners.add(listener);
   }

   public ReloadInstance createReload(final Executor backgroundExecutor, final Executor mainThreadExecutor, final CompletableFuture initialTask, final List resourcePacks) {
      LOGGER.info("Reloading ResourceManager: {}", LogUtils.defer(() -> resourcePacks.stream().map(PackResources::packId).collect(Collectors.joining(", "))));
      this.resources.close();
      this.resources = new MultiPackResourceManager(this.type, resourcePacks);
      return SimpleReloadInstance.create(this.resources, this.listeners, backgroundExecutor, mainThreadExecutor, initialTask, LOGGER.isDebugEnabled());
   }

   public Optional getResource(final Identifier location) {
      return this.resources.getResource(location);
   }

   public Set getNamespaces() {
      return this.resources.getNamespaces();
   }

   public List getResourceStack(final Identifier location) {
      return this.resources.getResourceStack(location);
   }

   public Map listResources(final String directory, final ResourceManager.Selector selector) {
      return this.resources.listResources(directory, selector);
   }

   public Map listResourceStacks(final String directory, final ResourceManager.Selector selector) {
      return this.resources.listResourceStacks(directory, selector);
   }

   public Stream listPacks() {
      return this.resources.listPacks();
   }
}
