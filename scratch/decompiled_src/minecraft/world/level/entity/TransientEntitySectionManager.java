package net.minecraft.world.level.entity;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;

public class TransientEntitySectionManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final LevelCallback callbacks;
   private final EntityLookup entityStorage;
   private final EntitySectionStorage sectionStorage;
   private final LongSet tickingChunks = new LongOpenHashSet();
   private final LevelEntityGetter entityGetter;

   public TransientEntitySectionManager(final Class entityClass, final LevelCallback callbacks) {
      this.entityStorage = new EntityLookup();
      this.sectionStorage = new EntitySectionStorage(entityClass, (key) -> this.tickingChunks.contains(key) ? Visibility.TICKING : Visibility.TRACKED);
      this.callbacks = callbacks;
      this.entityGetter = new LevelEntityGetterAdapter(this.entityStorage, this.sectionStorage);
   }

   public void startTicking(final ChunkPos pos) {
      long chunkKey = pos.pack();
      this.tickingChunks.add(chunkKey);
      this.sectionStorage.getExistingSectionsInChunk(chunkKey).forEach((section) -> {
         Visibility previousStatus = section.updateChunkStatus(Visibility.TICKING);
         if (!previousStatus.isTicking()) {
            section.getEntities().filter((e) -> !e.isAlwaysTicking()).forEach(this.callbacks::onTickingStart);
         }

      });
   }

   public void stopTicking(final ChunkPos pos) {
      long chunkKey = pos.pack();
      this.tickingChunks.remove(chunkKey);
      this.sectionStorage.getExistingSectionsInChunk(chunkKey).forEach((section) -> {
         Visibility previousStatus = section.updateChunkStatus(Visibility.TRACKED);
         if (previousStatus.isTicking()) {
            section.getEntities().filter((e) -> !e.isAlwaysTicking()).forEach(this.callbacks::onTickingEnd);
         }

      });
   }

   public LevelEntityGetter getEntityGetter() {
      return this.entityGetter;
   }

   public void addEntity(final EntityAccess entity) {
      this.entityStorage.add(entity);
      long sectionKey = SectionPos.asLong(entity.blockPosition());
      EntitySection entitySection = this.sectionStorage.getOrCreateSection(sectionKey);
      entitySection.add(entity);
      entity.setLevelCallback(new TransientEntitySectionManager.Callback(entity, sectionKey, entitySection));
      this.callbacks.onCreated(entity);
      this.callbacks.onTrackingStart(entity);
      if (entity.isAlwaysTicking() || entitySection.getStatus().isTicking()) {
         this.callbacks.onTickingStart(entity);
      }

   }

   @VisibleForDebug
   public int count() {
      return this.entityStorage.count();
   }

   private void removeSectionIfEmpty(final long sectionPos, final EntitySection section) {
      if (section.isEmpty()) {
         this.sectionStorage.remove(sectionPos);
      }

   }

   @VisibleForDebug
   public String gatherStats() {
      return this.entityStorage.count() + "," + this.sectionStorage.count() + "," + this.tickingChunks.size();
   }

   private class Callback implements EntityInLevelCallback {
      private final EntityAccess entity;
      private long currentSectionKey;
      private EntitySection currentSection;

      private Callback(final EntityAccess entity, final long currentSectionKey, final EntitySection currentSection) {
         Objects.requireNonNull(TransientEntitySectionManager.this);
         super();
         this.entity = entity;
         this.currentSectionKey = currentSectionKey;
         this.currentSection = currentSection;
      }

      public void onMove() {
         BlockPos pos = this.entity.blockPosition();
         long newSectionPos = SectionPos.asLong(pos);
         if (newSectionPos != this.currentSectionKey) {
            Visibility previousStatus = this.currentSection.getStatus();
            if (!this.currentSection.remove(this.entity)) {
               TransientEntitySectionManager.LOGGER.warn("Entity {} wasn't found in section {} (moving to {})", new Object[]{this.entity, SectionPos.of(this.currentSectionKey), newSectionPos});
            }

            TransientEntitySectionManager.this.removeSectionIfEmpty(this.currentSectionKey, this.currentSection);
            EntitySection newSection = TransientEntitySectionManager.this.sectionStorage.getOrCreateSection(newSectionPos);
            newSection.add(this.entity);
            this.currentSection = newSection;
            this.currentSectionKey = newSectionPos;
            TransientEntitySectionManager.this.callbacks.onSectionChange(this.entity);
            if (!this.entity.isAlwaysTicking()) {
               boolean wasTicking = previousStatus.isTicking();
               boolean isTicking = newSection.getStatus().isTicking();
               if (wasTicking && !isTicking) {
                  TransientEntitySectionManager.this.callbacks.onTickingEnd(this.entity);
               } else if (!wasTicking && isTicking) {
                  TransientEntitySectionManager.this.callbacks.onTickingStart(this.entity);
               }
            }
         }

      }

      public void onRemove(final Entity.RemovalReason reason) {
         if (!this.currentSection.remove(this.entity)) {
            TransientEntitySectionManager.LOGGER.warn("Entity {} wasn't found in section {} (destroying due to {})", new Object[]{this.entity, SectionPos.of(this.currentSectionKey), reason});
         }

         Visibility status = this.currentSection.getStatus();
         if (status.isTicking() || this.entity.isAlwaysTicking()) {
            TransientEntitySectionManager.this.callbacks.onTickingEnd(this.entity);
         }

         TransientEntitySectionManager.this.callbacks.onTrackingEnd(this.entity);
         TransientEntitySectionManager.this.callbacks.onDestroyed(this.entity);
         TransientEntitySectionManager.this.entityStorage.remove(this.entity);
         this.entity.setLevelCallback(NULL);
         TransientEntitySectionManager.this.removeSectionIfEmpty(this.currentSectionKey, this.currentSection);
      }
   }
}
