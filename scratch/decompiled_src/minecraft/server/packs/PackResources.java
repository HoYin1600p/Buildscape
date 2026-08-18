package net.minecraft.server.packs;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jspecify.annotations.Nullable;

public interface PackResources extends PackMetadataResources {
   String METADATA_EXTENSION = ".mcmeta";
   String PACK_META = "pack.mcmeta";

   @Nullable IoSupplier getResource(PackType type, Identifier location);

   void listResources(PackType type, String namespace, String directory, PackResources.ResourceOutput output);

   Set getNamespaces(PackType type);

   default String packId() {
      return this.location().id();
   }

   default Optional knownPackInfo() {
      return this.location().knownPackInfo();
   }

   @FunctionalInterface
   public interface Filter {
      boolean isFiltered(Identifier resourceId);
   }

   @FunctionalInterface
   public interface ResourceOutput extends BiConsumer {
   }
}
