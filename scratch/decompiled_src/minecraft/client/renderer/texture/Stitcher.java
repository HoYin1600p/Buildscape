package net.minecraft.client.renderer.texture;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

public class Stitcher {
   private static final Comparator HOLDER_COMPARATOR = Comparator.comparing((h) -> -h.height).thenComparing((h) -> -h.width).thenComparing((h) -> h.entry.name());
   private final int mipLevel;
   private final List texturesToBeStitched = new ArrayList();
   private final List storage = new ArrayList();
   private int storageX;
   private int storageY;
   private final int maxWidth;
   private final int maxHeight;
   private final int padding;

   public Stitcher(final int maxWidth, final int maxHeight, final int mipLevel, final int anisotropyBit) {
      this.mipLevel = mipLevel;
      this.maxWidth = maxWidth;
      this.maxHeight = maxHeight;
      this.padding = 1 << mipLevel << Mth.clamp(anisotropyBit - 1, 0, 4);
   }

   public int getWidth() {
      return this.storageX;
   }

   public int getHeight() {
      return this.storageY;
   }

   public void registerSprite(final Stitcher.Entry entry) {
      Stitcher.Holder holder = new Stitcher.Holder(entry, smallestFittingMinTexel(entry.width() + this.padding * 2, this.mipLevel), smallestFittingMinTexel(entry.height() + this.padding * 2, this.mipLevel));
      this.texturesToBeStitched.add(holder);
   }

   public void stitch() {
      List holders = new ArrayList(this.texturesToBeStitched);
      holders.sort(HOLDER_COMPARATOR);

      for(Stitcher.Holder holder : holders) {
         if (!this.addToStorage(holder)) {
            throw new StitcherException(holder.entry, (Collection)holders.stream().map((h) -> h.entry).collect(ImmutableList.toImmutableList()));
         }
      }

   }

   public void gatherSprites(final Stitcher.SpriteLoader loader) {
      for(Stitcher.Region topRegion : this.storage) {
         topRegion.walk(loader, this.padding);
      }

   }

   private static int smallestFittingMinTexel(final int input, final int maxMipLevel) {
      return (input >> maxMipLevel) + ((input & (1 << maxMipLevel) - 1) == 0 ? 0 : 1) << maxMipLevel;
   }

   private boolean addToStorage(final Stitcher.Holder holder) {
      for(Stitcher.Region region : this.storage) {
         if (region.add(holder)) {
            return true;
         }
      }

      return this.expand(holder);
   }

   private boolean expand(final Stitcher.Holder holder) {
      int xCurrentSize = Mth.smallestEncompassingPowerOfTwo(this.storageX);
      int yCurrentSize = Mth.smallestEncompassingPowerOfTwo(this.storageY);
      int xNewSize = Mth.smallestEncompassingPowerOfTwo(this.storageX + holder.width);
      int yNewSize = Mth.smallestEncompassingPowerOfTwo(this.storageY + holder.height);
      boolean xCanGrow = xNewSize <= this.maxWidth;
      boolean yCanGrow = yNewSize <= this.maxHeight;
      if (!xCanGrow && !yCanGrow) {
         return false;
      } else {
         boolean xWillGrow = xCanGrow && xCurrentSize != xNewSize;
         boolean yWillGrow = yCanGrow && yCurrentSize != yNewSize;
         boolean growOnX;
         if (xWillGrow ^ yWillGrow) {
            growOnX = xWillGrow;
         } else {
            growOnX = xCanGrow && xCurrentSize <= yCurrentSize;
         }

         Stitcher.Region slot;
         if (growOnX) {
            if (this.storageY == 0) {
               this.storageY = yNewSize;
            }

            slot = new Stitcher.Region(this.storageX, 0, xNewSize - this.storageX, this.storageY);
            this.storageX = xNewSize;
         } else {
            slot = new Stitcher.Region(0, this.storageY, this.storageX, yNewSize - this.storageY);
            this.storageY = yNewSize;
         }

         slot.add(holder);
         this.storage.add(slot);
         return true;
      }
   }

   public interface Entry {
      int width();

      int height();

      Identifier name();
   }

   private static record Holder(Stitcher.Entry entry, int width, int height) {
   }

   public static class Region {
      private final int originX;
      private final int originY;
      private final int width;
      private final int height;
      private @Nullable List subSlots;
      private Stitcher.@Nullable Holder holder;

      public Region(final int originX, final int originY, final int width, final int height) {
         this.originX = originX;
         this.originY = originY;
         this.width = width;
         this.height = height;
      }

      public int getX() {
         return this.originX;
      }

      public int getY() {
         return this.originY;
      }

      public boolean add(final Stitcher.Holder holder) {
         if (this.holder != null) {
            return false;
         } else {
            int textureWidth = holder.width;
            int textureHeight = holder.height;
            if (textureWidth <= this.width && textureHeight <= this.height) {
               if (textureWidth == this.width && textureHeight == this.height) {
                  this.holder = holder;
                  return true;
               } else {
                  if (this.subSlots == null) {
                     this.subSlots = new ArrayList(1);
                     this.subSlots.add(new Stitcher.Region(this.originX, this.originY, textureWidth, textureHeight));
                     int spareWidth = this.width - textureWidth;
                     int spareHeight = this.height - textureHeight;
                     if (spareHeight > 0 && spareWidth > 0) {
                        int right = Math.max(this.height, spareWidth);
                        int bottom = Math.max(this.width, spareHeight);
                        if (right >= bottom) {
                           this.subSlots.add(new Stitcher.Region(this.originX, this.originY + textureHeight, textureWidth, spareHeight));
                           this.subSlots.add(new Stitcher.Region(this.originX + textureWidth, this.originY, spareWidth, this.height));
                        } else {
                           this.subSlots.add(new Stitcher.Region(this.originX + textureWidth, this.originY, spareWidth, textureHeight));
                           this.subSlots.add(new Stitcher.Region(this.originX, this.originY + textureHeight, this.width, spareHeight));
                        }
                     } else if (spareWidth == 0) {
                        this.subSlots.add(new Stitcher.Region(this.originX, this.originY + textureHeight, textureWidth, spareHeight));
                     } else if (spareHeight == 0) {
                        this.subSlots.add(new Stitcher.Region(this.originX + textureWidth, this.originY, spareWidth, textureHeight));
                     }
                  }

                  for(Stitcher.Region subSlot : this.subSlots) {
                     if (subSlot.add(holder)) {
                        return true;
                     }
                  }

                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public void walk(final Stitcher.SpriteLoader output, final int padding) {
         if (this.holder != null) {
            output.load(this.holder.entry, this.getX(), this.getY(), padding);
         } else if (this.subSlots != null) {
            for(Stitcher.Region subSlot : this.subSlots) {
               subSlot.walk(output, padding);
            }
         }

      }

      public String toString() {
         return "Slot{originX=" + this.originX + ", originY=" + this.originY + ", width=" + this.width + ", height=" + this.height + ", texture=" + String.valueOf(this.holder) + ", subSlots=" + String.valueOf(this.subSlots) + "}";
      }
   }

   public interface SpriteLoader {
      void load(Stitcher.Entry entry, int x, int z, int padding);
   }
}
