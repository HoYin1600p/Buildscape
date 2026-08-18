package net.minecraft.server.packs;

import java.io.IOException;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jspecify.annotations.Nullable;

public interface PackMetadataResources extends AutoCloseable {
   PackLocationInfo location();

   @Nullable IoSupplier getRootResource(String... path);

   @Nullable Object getMetadataSection(MetadataSectionType metadataSerializer) throws IOException;

   void close();
}
