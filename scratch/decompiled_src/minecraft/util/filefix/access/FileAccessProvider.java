package net.minecraft.util.filefix.access;

import java.util.ArrayList;
import java.util.List;

public class FileAccessProvider implements AutoCloseable {
   private final List accessedFiles = new ArrayList();
   private final ScopedValue baseDirectory = ScopedValue.newInstance();
   private final int dataVersion;
   private boolean frozen = false;

   public FileAccessProvider(final int dataVersion) {
      this.dataVersion = dataVersion;
   }

   public FileAccess getFileAccess(final FileResourceType type, final FileRelation fileRelation) {
      if (this.frozen) {
         throw new IllegalStateException("Cannot request new file access here.");
      } else {
         FileAccess fileAccess = new FileAccess(this, type, fileRelation);
         this.accessedFiles.add(fileAccess);
         return fileAccess;
      }
   }

   public void freeze() {
      this.frozen = true;
   }

   public ScopedValue baseDirectory() {
      return this.baseDirectory;
   }

   public int dataVersion() {
      return this.dataVersion;
   }

   public void close() {
      for(FileAccess accessedFile : this.accessedFiles) {
         accessedFile.close();
      }

   }
}
