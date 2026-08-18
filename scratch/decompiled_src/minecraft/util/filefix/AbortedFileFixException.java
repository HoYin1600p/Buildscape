package net.minecraft.util.filefix;

import java.util.List;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.util.filefix.virtualfilesystem.FileMove;
import org.jspecify.annotations.Nullable;

public final class AbortedFileFixException extends FileFixException {
   private final List notRevertedMoves;

   public AbortedFileFixException(final Exception cause, final List notRevertedMoves, final @Nullable FileSystemCapabilities fileSystemCapabilities) {
      super(cause, fileSystemCapabilities);
      this.notRevertedMoves = notRevertedMoves;
   }

   public AbortedFileFixException(final Exception cause) {
      this(cause, List.of(), (FileSystemCapabilities)null);
   }

   public List notRevertedMoves() {
      return this.notRevertedMoves;
   }

   protected CrashReport createCrashReport() {
      CrashReport crashReport = super.createCrashReport();
      CrashReportCategory failedReverts = crashReport.addCategory("Moves that failed to revert");

      for(int i = 0; i < this.notRevertedMoves.size(); ++i) {
         FileMove notRevertedMove = (FileMove)this.notRevertedMoves.get(i);
         failedReverts.setDetail(String.valueOf(i), String.valueOf(notRevertedMove.from()) + " -> " + String.valueOf(notRevertedMove.to()));
      }

      return crashReport;
   }
}
