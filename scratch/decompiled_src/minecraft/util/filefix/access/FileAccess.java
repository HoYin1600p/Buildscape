package net.minecraft.util.filefix.access;

import com.mojang.logging.LogUtils;
import java.nio.file.Path;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class FileAccess implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final FileAccessProvider fileAccessProvider;
   private final FileResourceType type;
   private final FileRelation fileRelation;
   private @Nullable List files;

   public FileAccess(final FileAccessProvider fileAccessProvider, final FileResourceType type, final FileRelation fileRelation) {
      this.fileAccessProvider = fileAccessProvider;
      this.type = type;
      this.fileRelation = fileRelation;
   }

   public List get() {
      if (this.files == null) {
         Path baseDirectory = (Path)this.fileAccessProvider.baseDirectory().get();
         if (baseDirectory == null) {
            throw new IllegalStateException("Cannot access world files");
         }

         this.files = this.fileRelation.getPaths(baseDirectory).stream().map((path) -> this.type.create(path, this.fileAccessProvider.dataVersion())).toList();
      }

      return this.files;
   }

   public AutoCloseable getOnlyFile() {
      List files = this.get();
      if (files.size() != 1) {
         throw new IllegalStateException("Trying to get only file, but there are " + files.size() + " files");
      } else {
         return (AutoCloseable)files.getFirst();
      }
   }

   public void close() {
      if (this.files != null) {
         for(AutoCloseable file : this.files) {
            try {
               file.close();
            } catch (Exception var4) {
               LOGGER.error("Failed to close file: ", var4);
            }
         }

         this.files = null;
      }

   }
}
