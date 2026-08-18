package net.minecraft.server.packs.resources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@FunctionalInterface
public interface IoSupplier {
   static IoSupplier create(final Path path) {
      return () -> Files.newInputStream(path);
   }

   static IoSupplier create(final ZipFile zipFile, final ZipEntry entry) {
      return () -> zipFile.getInputStream(entry);
   }

   Object get() throws IOException;
}
