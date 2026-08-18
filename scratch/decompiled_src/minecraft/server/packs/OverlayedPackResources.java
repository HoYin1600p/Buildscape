package net.minecraft.server.packs;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jspecify.annotations.Nullable;

public class OverlayedPackResources implements PackResources {
   private final PackMetadataResources primaryPackMetadataResources;
   private final List packResourcesStack;

   public OverlayedPackResources(final PackResources primaryPackResources, final List overlayPackResources) {
      this.primaryPackMetadataResources = primaryPackResources;
      List stack = new ArrayList(overlayPackResources.size() + 1);
      stack.addAll(Lists.reverse(overlayPackResources));
      stack.add(primaryPackResources);
      this.packResourcesStack = List.copyOf(stack);
   }

   public @Nullable IoSupplier getRootResource(final String... path) {
      return this.primaryPackMetadataResources.getRootResource(path);
   }

   public @Nullable IoSupplier getResource(final PackType type, final Identifier location) {
      for(PackResources packResources : this.packResourcesStack) {
         IoSupplier resource = packResources.getResource(type, location);
         if (resource != null) {
            return resource;
         }
      }

      return null;
   }

   public void listResources(final PackType type, final String namespace, final String directory, final PackResources.ResourceOutput output) {
      Map result = new HashMap();

      for(PackResources packResources : this.packResourcesStack) {
         packResources.listResources(type, namespace, directory, result::putIfAbsent);
      }

      result.forEach(output);
   }

   public Set getNamespaces(final PackType type) {
      Set result = new HashSet();

      for(PackResources overlayPackResource : this.packResourcesStack) {
         result.addAll(overlayPackResource.getNamespaces(type));
      }

      return result;
   }

   public @Nullable Object getMetadataSection(final MetadataSectionType metadataSerializer) throws IOException {
      return this.primaryPackMetadataResources.getMetadataSection(metadataSerializer);
   }

   public PackLocationInfo location() {
      return this.primaryPackMetadataResources.location();
   }

   public void close() {
      this.packResourcesStack.forEach(PackMetadataResources::close);
   }
}
