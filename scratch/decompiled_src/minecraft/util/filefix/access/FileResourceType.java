package net.minecraft.util.filefix.access;

import java.nio.file.Path;

public class FileResourceType {
   private final FileResourceType.AccessFactory factory;

   public FileResourceType(final FileResourceType.AccessFactory factory) {
      this.factory = factory;
   }

   public AutoCloseable create(final Path path, final int dataVersion) {
      return (AutoCloseable)this.factory.create(path, dataVersion);
   }

   @FunctionalInterface
   public interface AccessFactory {
      Object create(Path path, int dataVersion);
   }
}
