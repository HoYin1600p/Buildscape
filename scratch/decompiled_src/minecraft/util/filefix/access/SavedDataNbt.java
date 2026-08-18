package net.minecraft.util.filefix.access;

import com.mojang.datafixers.DSL;
import com.mojang.serialization.Dynamic;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixers;

public class SavedDataNbt extends CompressedNbt {
   private final DSL.TypeReference type;
   private final int targetVersion;

   public SavedDataNbt(final DSL.TypeReference type, final Path path, final int targetVersion, final CompressedNbt.MissingSeverity missingSeverity) {
      super(path, missingSeverity);
      this.type = type;
      this.targetVersion = targetVersion;
   }

   public Optional read() throws IOException {
      return this.readFile().map((readData) -> {
         int version = NbtUtils.getDataVersion(readData);
         return DataFixers.getDataFixer().update(this.type, readData, version, this.targetVersion).get("data").orElseEmptyMap();
      });
   }

   public void write(final Dynamic data) {
      Dynamic dataTag = data.emptyMap().set("data", data);
      Dynamic wrappedAndWithDataVersion = NbtUtils.addDataVersion(dataTag, this.targetVersion);
      this.writeFile(wrappedAndWithDataVersion);
   }
}
