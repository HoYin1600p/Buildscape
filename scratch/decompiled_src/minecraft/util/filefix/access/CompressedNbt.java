package net.minecraft.util.filefix.access;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.FileUtil;
import org.slf4j.Logger;

public abstract class CompressedNbt implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Path path;
   private final CompressedNbt.MissingSeverity missingSeverity;

   public CompressedNbt(final Path path, final CompressedNbt.MissingSeverity missingSeverity) {
      this.path = path;
      this.missingSeverity = missingSeverity;
   }

   public abstract Optional read() throws IOException;

   protected final Optional readFile() throws IOException {
      try {
         return Optional.of(new Dynamic(NbtOps.INSTANCE, NbtIo.readCompressed(this.path, NbtAccounter.unlimitedHeap())));
      } catch (NoSuchFileException var2) {
         this.missingSeverity.log("Missing file: {}", this.path);
         return Optional.empty();
      }
   }

   public abstract void write(final Dynamic data);

   protected final void writeFile(final Dynamic data) {
      CompoundTag cast = (CompoundTag)data.cast(NbtOps.INSTANCE);

      try {
         FileUtil.createDirectoriesSafe(this.path.getParent());
         NbtIo.writeCompressed(cast, this.path);
      } catch (IOException var4) {
         LOGGER.error("Failed to write to {}", this.path, var4);
      }

   }

   public Path path() {
      return this.path;
   }

   public void close() {
   }

   public static enum MissingSeverity {
      IMPORTANT(CompressedNbt.LOGGER::error),
      NEUTRAL(CompressedNbt.LOGGER::info),
      MINOR(CompressedNbt.LOGGER::debug);

      private final BiConsumer logFunction;

      private MissingSeverity(final BiConsumer logFunction) {
         this.logFunction = logFunction;
      }

      public void log(final String message, final Path path) {
         this.logFunction.accept(message, path);
      }

      // $FF: synthetic method
      private static CompressedNbt.MissingSeverity[] $values() {
         return new CompressedNbt.MissingSeverity[]{IMPORTANT, NEUTRAL, MINOR};
      }
   }
}
