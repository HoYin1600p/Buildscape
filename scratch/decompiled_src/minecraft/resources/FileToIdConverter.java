package net.minecraft.resources;

import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.packs.resources.ResourceManager;

public record FileToIdConverter(String prefix, String extension) {
   public static FileToIdConverter json(final String prefix) {
      return new FileToIdConverter(prefix, ".json");
   }

   public static FileToIdConverter registry(final ResourceKey registry) {
      return json(Registries.elementsDirPath(registry));
   }

   public Identifier idToFile(final Identifier id) {
      return id.withPath(this.prefix + "/" + id.getPath() + this.extension);
   }

   public Identifier fileToId(final Identifier file) {
      String path = file.getPath();
      return file.withPath(path.substring(this.prefix.length() + 1, path.length() - this.extension.length()));
   }

   public boolean extensionMatches(final Identifier id) {
      return id.getPath().endsWith(this.extension);
   }

   private ResourceManager.Selector extensionSelector() {
      return this::extensionMatches;
   }

   public Map listMatchingResources(final ResourceManager manager) {
      return manager.listResources(this.prefix, this.extensionSelector());
   }

   public Map listMatchingResourceStacks(final ResourceManager manager) {
      return manager.listResourceStacks(this.prefix, this.extensionSelector());
   }
}
