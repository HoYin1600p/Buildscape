package net.minecraft.client.resources;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public class LegacyStuffWrapper {
   /** @deprecated */
   @Deprecated
   public static int[] getPixels(final ResourceManager resourceManager, final Identifier location) throws IOException {
      InputStream resource = resourceManager.open(location);

      int[] var4;
      try {
         NativeImage image = NativeImage.read(resource);

         try {
            var4 = image.makePixelArray();
         } catch (Throwable var8) {
            if (image != null) {
               try {
                  image.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (image != null) {
            image.close();
         }
      } catch (Throwable var9) {
         if (resource != null) {
            try {
               resource.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }
         }

         throw var9;
      }

      if (resource != null) {
         resource.close();
      }

      return var4;
   }
}
