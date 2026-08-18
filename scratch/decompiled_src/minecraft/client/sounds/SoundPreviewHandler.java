package net.minecraft.client.sounds;

import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.cow.CowSoundVariant;
import net.minecraft.world.entity.animal.cow.CowSoundVariants;
import org.jspecify.annotations.Nullable;

public final class SoundPreviewHandler {
   private static @Nullable SoundInstance activePreview;
   private static @Nullable SoundSource previousCategory;

   public static void preview(final SoundManager soundManager, final SoundSource category, final float volume) {
      stopOtherCategoryPreview(soundManager, category);
      if (canPlaySound(soundManager)) {
         SoundEvent var10000;
         switch (category) {
            case RECORDS:
               var10000 = (SoundEvent)SoundEvents.NOTE_BLOCK_GUITAR.value();
               break;
            case WEATHER:
               var10000 = SoundEvents.LIGHTNING_BOLT_THUNDER;
               break;
            case BLOCKS:
               var10000 = SoundEvents.GRASS_PLACE;
               break;
            case HOSTILE:
               var10000 = SoundEvents.ZOMBIE_AMBIENT;
               break;
            case NEUTRAL:
               var10000 = (SoundEvent)((CowSoundVariant)SoundEvents.COW_SOUNDS.get(CowSoundVariants.SoundSet.CLASSIC)).ambientSound().value();
               break;
            case PLAYERS:
               var10000 = (SoundEvent)SoundEvents.GENERIC_EAT.value();
               break;
            case AMBIENT:
               var10000 = (SoundEvent)SoundEvents.AMBIENT_CAVE.value();
               break;
            case UI:
               var10000 = (SoundEvent)SoundEvents.UI_BUTTON_CLICK.value();
               break;
            default:
               var10000 = SoundEvents.EMPTY;
         }

         SoundEvent previewSound = var10000;
         if (previewSound != SoundEvents.EMPTY) {
            activePreview = SimpleSoundInstance.forUI(previewSound, 1.0F, volume);
            soundManager.play(activePreview);
         }
      }

   }

   private static void stopOtherCategoryPreview(final SoundManager soundManager, final SoundSource category) {
      if (previousCategory != category) {
         previousCategory = category;
         if (activePreview != null) {
            soundManager.stop(activePreview);
         }
      }

   }

   private static boolean canPlaySound(final SoundManager soundManager) {
      return activePreview == null || !soundManager.isActive(activePreview);
   }
}
