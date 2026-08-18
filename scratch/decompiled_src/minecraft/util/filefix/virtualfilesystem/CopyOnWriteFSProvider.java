package net.minecraft.util.filefix.virtualfilesystem;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileTime;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.util.DummyFileAttributes;
import net.minecraft.util.filefix.virtualfilesystem.exception.CowFSDirectoryNotEmptyException;
import net.minecraft.util.filefix.virtualfilesystem.exception.CowFSFileAlreadyExistsException;
import net.minecraft.util.filefix.virtualfilesystem.exception.CowFSFileSystemException;
import net.minecraft.util.filefix.virtualfilesystem.exception.CowFSNoSuchFileException;
import org.jspecify.annotations.Nullable;

public class CopyOnWriteFSProvider extends FileSystemProvider {
   public static final String SCHEME = "x-mc-copy-on-write";
   private static final BasicFileAttributeView DUMMY_DIRECTORY_VIEW = new BasicFileAttributeView() {
      public String name() {
         return "basic";
      }

      public BasicFileAttributes readAttributes() {
         return DummyFileAttributes.DIRECTORY;
      }

      public void setTimes(final FileTime lastModifiedTime, final FileTime lastAccessTime, final FileTime createTime) throws IOException {
      }
   };
   private final CopyOnWriteFileSystem fs;

   public CopyOnWriteFSProvider(final CopyOnWriteFileSystem fileSystem) {
      this.fs = fileSystem;
   }

   public String getScheme() {
      return "x-mc-copy-on-write";
   }

   public FileSystem newFileSystem(final URI uri, final Map env) throws IOException {
      throw new UnsupportedOperationException();
   }

   public FileSystem getFileSystem(final URI uri) {
      throw new UnsupportedOperationException();
   }

   public Path getPath(final URI uri) {
      throw new UnsupportedOperationException();
   }

   public SeekableByteChannel newByteChannel(final Path path, final Set options, final FileAttribute... attrs) throws IOException {
      return (SeekableByteChannel)this.newChannel(path, options, attrs, Files::newByteChannel);
   }

   public FileChannel newFileChannel(final Path path, final Set options, final FileAttribute... attrs) throws IOException {
      return (FileChannel)this.newChannel(path, options, attrs, FileChannel::open);
   }

   private synchronized Object newChannel(final Path path, final Set options, final FileAttribute[] attrs, final CopyOnWriteFSProvider.ChannelFactory channelFactory) throws IOException {
      CopyOnWriteFSPath cowPath = CopyOnWriteFSPath.asCow(path);
      if (options.contains(StandardOpenOption.DELETE_ON_CLOSE)) {
         throw new UnsupportedOperationException("DELETE_ON_CLOSE is not supported by CowFS");
      } else {
         Node var6 = this.fs.fileTree().byPathOrNull(cowPath);
         byte var7 = 0;
         Object var10000;
         switch (var6.typeSwitch<invokedynamic>(var6, var7)) {
            case -1:
               if (!options.contains(StandardOpenOption.CREATE) && !options.contains(StandardOpenOption.CREATE_NEW)) {
                  throw new CowFSNoSuchFileException(cowPath.toString());
               }

               DirectoryNode directoryNode = this.fs.fileTree().directoryByPath((CopyOnWriteFSPath)Objects.requireNonNull(cowPath.getParent()));
               Path tempFile = this.fs.createTemporaryFilePath();
               Object result = (C)channelFactory.newChannel(tempFile, options, attrs);
               FileNode child = new FileNode(cowPath, tempFile, true);
               directoryNode.addChild(child);
               var10000 = result;
               break;
            case 0:
               FileNode fileNode = (FileNode)var6;
               if (wantsWrite(options)) {
                  fileNode.ensureCopy();
               }

               var10000 = channelFactory.newChannel(fileNode.storagePath(), options, attrs);
               break;
            case 1:
               throw new CowFSFileSystemException(String.valueOf(cowPath) + ": not a regular file");
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }
   }

   private static boolean wantsWrite(final Set options) {
      return options.contains(StandardOpenOption.WRITE) || !options.contains(StandardOpenOption.READ) && options.contains(StandardOpenOption.APPEND);
   }

   public synchronized DirectoryStream newDirectoryStream(final Path dir, final DirectoryStream.Filter filter) throws IOException {
      CopyOnWriteFSPath cowPath = CopyOnWriteFSPath.asCow(dir);
      DirectoryNode directoryNode = this.fs.fileTree().directoryByPath(cowPath);
      final List result = new ArrayList();

      for(Node childNode : directoryNode.children()) {
         Path path = childNode.path();
         if (filter.accept(path)) {
            result.add(path);
         }
      }

      return new DirectoryStream(this) {
         {
            Objects.requireNonNull(this$0);
         }

         public void close() {
         }

         public Iterator iterator() {
            return result.iterator();
         }
      };
   }

   public synchronized void createDirectory(final Path dir, final FileAttribute... attrs) throws IOException {
      CopyOnWriteFSPath cowPath = CopyOnWriteFSPath.asCow(dir);
      CopyOnWriteFSPath parent = cowPath.getParent();
      if (parent == null) {
         throw new CowFSFileAlreadyExistsException(cowPath.toString());
      } else {
         DirectoryNode parentFolder = this.fs.fileTree().directoryByPath(parent);
         String folderName = ((CopyOnWriteFSPath)Objects.requireNonNull(cowPath.getFileName())).toString();
         if (parentFolder.getChild(folderName) != null) {
            throw new CowFSFileAlreadyExistsException(cowPath.toString());
         } else {
            parentFolder.addChild(new DirectoryNode(cowPath));
         }
      }
   }

   public synchronized void delete(final Path path) throws IOException {
      CopyOnWriteFSPath cowPath = CopyOnWriteFSPath.asCow(path);
      Node node = this.fs.fileTree().byPath(cowPath);
      if (node.parent == null) {
         throw new CowFSFileSystemException("Can't remove root");
      } else {
         String name = (String)Objects.requireNonNull(node.name());
         if (node instanceof DirectoryNode) {
            DirectoryNode directoryNode = (DirectoryNode)node;
            if (!directoryNode.children().isEmpty()) {
               throw new CowFSDirectoryNotEmptyException(cowPath.toString());
            }
         } else if (node instanceof FileNode) {
            FileNode fileNode = (FileNode)node;
            fileNode.deleteCopy();
         }

         node.parent.removeChild(name);
      }
   }

   public void copy(final Path source, final Path target, final CopyOption... options) throws IOException {
      throw new UnsupportedOperationException();
   }

   public synchronized void move(final Path source, final Path target, final CopyOption... options) throws IOException {
      CopyOnWriteFSPath sourceCow = CopyOnWriteFSPath.asCow(source);
      CopyOnWriteFSPath targetCow = CopyOnWriteFSPath.asCow(target);
      if (sourceCow.isRoot()) {
         throw new CowFSFileSystemException(String.valueOf(sourceCow) + ": can't move root directory");
      } else {
         boolean replaceExisting = false;

         for(CopyOption option : options) {
            if (option.equals(StandardCopyOption.ATOMIC_MOVE)) {
               throw new AtomicMoveNotSupportedException(sourceCow.toString(), targetCow.toString(), "CowFs does not support atomic move");
            }

            if (option.equals(StandardCopyOption.REPLACE_EXISTING)) {
               replaceExisting = true;
            }
         }

         Node sourceNode = this.fs.fileTree().byPathOrNull(sourceCow);
         if (sourceNode == null) {
            throw new CowFSNoSuchFileException(sourceCow.toString());
         } else {
            CopyOnWriteFSPath parent = targetCow.toAbsolutePath().getParent();
            if (parent == null) {
               throw new CowFSFileAlreadyExistsException(targetCow.toString());
            } else {
               Node targetParentNode = this.fs.fileTree().byPathOrNull(parent);
               if (targetParentNode instanceof DirectoryNode) {
                  DirectoryNode folderTarget = (DirectoryNode)targetParentNode;
                  String newName = ((CopyOnWriteFSPath)Objects.requireNonNull(targetCow.getFileName())).toString();
                  Node oldChild = folderTarget.getChild(newName);
                  if (oldChild != null) {
                     if (oldChild.equals(sourceNode)) {
                        return;
                     }

                     if (!replaceExisting) {
                        throw new CowFSFileAlreadyExistsException(targetCow.toString());
                     }

                     folderTarget.removeChild(newName);
                  }

                  ((DirectoryNode)Objects.requireNonNull(sourceNode.parent)).removeChild((String)Objects.requireNonNull(sourceNode.name()));
                  sourceNode.setPath(targetCow);
                  folderTarget.addChild(sourceNode);
               } else {
                  throw new CowFSNoSuchFileException(targetCow.toString());
               }
            }
         }
      }
   }

   public boolean isSameFile(final Path path, final Path path2) {
      throw new UnsupportedOperationException();
   }

   public boolean isHidden(final Path path) {
      throw new UnsupportedOperationException();
   }

   public FileStore getFileStore(final Path path) {
      throw new UnsupportedOperationException();
   }

   public synchronized void checkAccess(final Path path, final AccessMode... modes) throws IOException {
      CopyOnWriteFSPath cowPath = CopyOnWriteFSPath.asCow(path);
      Node node = this.fs.fileTree().byPath(cowPath);
      Objects.requireNonNull(node);
      byte var7 = 0;
      Path var10000;
      switch (node.typeSwitch<invokedynamic>(node, var7)) {
         case 0:
            var10000 = this.fs.tmpDirectory();
            break;
         case 1:
            FileNode file = (FileNode)node;
            var10000 = file.storagePath();
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      Path checkPath = var10000;
      checkPath.getFileSystem().provider().checkAccess(checkPath, modes);
   }

   public synchronized @Nullable FileAttributeView getFileAttributeView(final Path path, final Class type, final LinkOption... options) {
      final CopyOnWriteFSPath cowPath = CopyOnWriteFSPath.asCow(path);
      Node node = this.fs.fileTree().byPathOrNull(cowPath);
      byte var7 = 0;
      Object var10000;
      switch (node.typeSwitch<invokedynamic>(node, var7)) {
         case -1:
            var10000 = type == BasicFileAttributeView.class ? new BasicFileAttributeView(this) {
               {
                  Objects.requireNonNull(this$0);
               }

               public String name() {
                  return "basic";
               }

               public BasicFileAttributes readAttributes() throws IOException {
                  throw new CowFSNoSuchFileException(cowPath.toString());
               }

               public void setTimes(final FileTime lastModifiedTime, final FileTime lastAccessTime, final FileTime createTime) throws IOException {
                  throw new CowFSNoSuchFileException(cowPath.toString());
               }
            } : null;
            break;
         case 0:
            var10000 = type == BasicFileAttributeView.class ? DUMMY_DIRECTORY_VIEW : null;
            break;
         case 1:
            FileNode file = (FileNode)node;
            var10000 = Files.getFileAttributeView(file.storagePath(), type, options);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return (FileAttributeView)var10000;
   }

   public synchronized BasicFileAttributes readAttributes(final Path path, final Class type, final LinkOption... options) throws IOException {
      CopyOnWriteFSPath cowPath = CopyOnWriteFSPath.asCow(path);
      Node node = this.fs.fileTree().byPath(cowPath);
      Objects.requireNonNull(node);
      byte var7 = 0;
      BasicFileAttributes var10000;
      switch (node.typeSwitch<invokedynamic>(node, var7)) {
         case 0:
            var10000 = DummyFileAttributes.DIRECTORY;
            break;
         case 1:
            FileNode file = (FileNode)node;
            var10000 = Files.readAttributes(file.storagePath(), type, options);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public Map readAttributes(final Path path, final String attributes, final LinkOption... options) {
      throw new UnsupportedOperationException();
   }

   public void setAttribute(final Path path, final String attribute, final Object value, final LinkOption... options) {
      throw new UnsupportedOperationException();
   }

   public synchronized CopyOnWriteFSPath getRealPath(final CopyOnWriteFSPath path) throws CowFSNoSuchFileException {
      return this.fs.fileTree().byPath(path.toAbsolutePath()).path;
   }

   @FunctionalInterface
   private interface ChannelFactory {
      Object newChannel(Path path, Set options, FileAttribute... attrs) throws IOException;
   }
}
