package net.minecraft.world.level.validation;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ContentValidationException extends Exception {
   private final Path directory;
   private final List entries;

   public ContentValidationException(final Path directory, final List entries) {
      this.directory = directory;
      this.entries = entries;
   }

   public String getMessage() {
      return getMessage(this.directory, this.entries);
   }

   public static String getMessage(final Path directory, final List entries) {
      return "Failed to validate '" + String.valueOf(directory) + "'. Found forbidden symlinks: " + (String)entries.stream().map((e) -> String.valueOf(e.link()) + "->" + String.valueOf(e.target())).collect(Collectors.joining(", "));
   }
}
