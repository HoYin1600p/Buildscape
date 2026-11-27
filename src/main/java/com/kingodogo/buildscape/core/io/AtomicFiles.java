package com.kingodogo.buildscape.core.io;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

/**
 * Writes a file by filling a temporary sibling first and then moving it over the target, so a reader never
 * sees a half-written file.
 */
public final class AtomicFiles {
    private AtomicFiles() {}

    /** Fills the temporary file with the new content. */
    @FunctionalInterface
    public interface TempWriter {
        void write(Path temporary) throws IOException;
    }

    /** Moves {@code source} over {@code target}, atomically where the file system supports it. */
    public static void moveReplacing(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Creates the parent directory, lets {@code writer} fill a temporary file named {@code tempPrefix + random + .tmp}
     * next to {@code target}, then moves it over the target and deletes the temporary file if it is still there.
     *
     * @param onCleanupFailure called when deleting the temporary file fails; {@code null} lets that exception propagate
     */
    public static void write(Path target, String tempPrefix, TempWriter writer, Consumer<IOException> onCleanupFailure)
            throws IOException {
        Path parent = target.toAbsolutePath().getParent();
        if (parent == null) {
            throw new IOException("Target has no parent directory: " + target);
        }
        Files.createDirectories(parent);
        Path temporary = Files.createTempFile(parent, tempPrefix, ".tmp");
        try {
            writer.write(temporary);
            moveReplacing(temporary, target);
        } finally {
            if (onCleanupFailure == null) {
                Files.deleteIfExists(temporary);
            } else {
                try {
                    Files.deleteIfExists(temporary);
                } catch (IOException e) {
                    onCleanupFailure.accept(e);
                }
            }
        }
    }

    /** {@link #write} with the target's file name as temporary-file prefix; cleanup failures propagate. */
    public static void write(Path target, TempWriter writer) throws IOException {
        write(target, target.getFileName().toString(), writer, null);
    }

    /** Writes {@code data} to {@code target} atomically. */
    public static void write(Path target, byte[] data) throws IOException {
        write(target, temporary -> Files.write(temporary, data));
    }
}
