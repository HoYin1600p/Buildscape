package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;

public class SkyLightSectionStorage extends LayerLightSectionStorage {
   protected SkyLightSectionStorage(final LightChunkGetter chunkSource) {
      super(LightLayer.SKY, chunkSource, new SkyLightSectionStorage.SkyDataLayerStorageMap(new Long2ObjectOpenHashMap(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
   }

   protected int getLightValue(final long blockNode) {
      return this.getLightValue(blockNode, false);
   }

   protected int getLightValue(long blockNode, final boolean updating) {
      long sectionNode = SectionPos.blockToSection(blockNode);
      int sectionY = SectionPos.y(sectionNode);
      SkyLightSectionStorage.SkyDataLayerStorageMap sections = updating ? (SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData : (SkyLightSectionStorage.SkyDataLayerStorageMap)this.visibleSectionData;
      int topSection = sections.topSections.get(SectionPos.getZeroNode(sectionNode));
      if (topSection != sections.currentLowestY && sectionY < topSection) {
         DataLayer layer = this.getDataLayer(sections, sectionNode);
         if (layer == null) {
            for(blockNode = BlockPos.getFlatIndex(blockNode); layer == null; layer = this.getDataLayer(sections, sectionNode)) {
               ++sectionY;
               if (sectionY >= topSection) {
                  return 15;
               }

               sectionNode = SectionPos.offset(sectionNode, Direction.UP);
            }
         }

         return layer.get(SectionPos.sectionRelative(BlockPos.getX(blockNode)), SectionPos.sectionRelative(BlockPos.getY(blockNode)), SectionPos.sectionRelative(BlockPos.getZ(blockNode)));
      } else {
         return updating && !this.lightOnInSection(sectionNode) ? 0 : 15;
      }
   }

   protected void onNodeAdded(final long sectionNode) {
      int y = SectionPos.y(sectionNode);
      if (((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY > y) {
         ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY = y;
         ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.defaultReturnValue(((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY);
      }

      long zeroNode = SectionPos.getZeroNode(sectionNode);
      int oldTop = ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(zeroNode);
      if (oldTop < y + 1) {
         ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.put(zeroNode, y + 1);
      }

   }

   protected void onNodeRemoved(final long sectionNode) {
      long zeroNode = SectionPos.getZeroNode(sectionNode);
      int y = SectionPos.y(sectionNode);
      if (((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(zeroNode) == y + 1) {
         long newTopSection;
         for(newTopSection = sectionNode; !this.storingLightForSection(newTopSection) && this.hasLightDataAtOrBelow(y); newTopSection = SectionPos.offset(newTopSection, Direction.DOWN)) {
            --y;
         }

         if (this.storingLightForSection(newTopSection)) {
            ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.put(zeroNode, y + 1);
         } else {
            ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.remove(zeroNode);
         }
      }

   }

   protected DataLayer createDataLayer(final long sectionNode) {
      DataLayer queuedLayer = (DataLayer)this.queuedSections.get(sectionNode);
      if (queuedLayer != null) {
         return queuedLayer;
      } else {
         int topSection = ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(SectionPos.getZeroNode(sectionNode));
         if (topSection != ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY && SectionPos.y(sectionNode) < topSection) {
            DataLayer aboveData;
            for(long aboveSection = SectionPos.offset(sectionNode, Direction.UP); (aboveData = this.getDataLayer(aboveSection, true)) == null; aboveSection = SectionPos.offset(aboveSection, Direction.UP)) {
            }

            return repeatFirstLayer(aboveData);
         } else {
            return this.lightOnInSection(sectionNode) ? new DataLayer(15) : new DataLayer();
         }
      }
   }

   private static DataLayer repeatFirstLayer(final DataLayer data) {
      if (data.isDefinitelyHomogenous()) {
         return data.copy();
      } else {
         byte[] input = data.getData();
         byte[] output = new byte[2048];

         for(int i = 0; i < 16; ++i) {
            System.arraycopy(input, 0, output, i * 128, 128);
         }

         return new DataLayer(output);
      }
   }

   protected boolean hasLightDataAtOrBelow(final int sectionY) {
      return sectionY >= ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY;
   }

   protected boolean isAboveData(final long sectionNode) {
      long zeroNode = SectionPos.getZeroNode(sectionNode);
      int topSection = ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(zeroNode);
      return topSection == ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY || SectionPos.y(sectionNode) >= topSection;
   }

   protected int getTopSectionY(final long zeroNode) {
      return ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(zeroNode);
   }

   protected int getBottomSectionY() {
      return ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY;
   }

   protected static final class SkyDataLayerStorageMap extends DataLayerStorageMap {
      private int currentLowestY;
      private final Long2IntOpenHashMap topSections;

      public SkyDataLayerStorageMap(final Long2ObjectOpenHashMap map, final Long2IntOpenHashMap topSections, final int currentLowestY) {
         super(map);
         this.topSections = topSections;
         topSections.defaultReturnValue(currentLowestY);
         this.currentLowestY = currentLowestY;
      }

      public SkyLightSectionStorage.SkyDataLayerStorageMap copy() {
         return new SkyLightSectionStorage.SkyDataLayerStorageMap(this.map.clone(), this.topSections.clone(), this.currentLowestY);
      }
   }
}
