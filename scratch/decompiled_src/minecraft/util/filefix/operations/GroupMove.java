package net.minecraft.util.filefix.operations;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import net.minecraft.util.worldupdate.UpgradeProgress;

public record GroupMove(Map fromTo, List fixers) implements FileFixOperation {
   public void fix(final Path baseDirectory, final UpgradeProgress upgradeProgress) throws IOException {
      for(Map.Entry entry : this.fromTo.entrySet()) {
         for(Move moveOperation : this.fixers) {
            Move relative = moveOperation.relative((String)entry.getKey(), (String)entry.getValue());
            relative.fix(baseDirectory, upgradeProgress);
         }
      }

   }
}
