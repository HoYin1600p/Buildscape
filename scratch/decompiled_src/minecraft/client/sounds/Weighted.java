package net.minecraft.client.sounds;

import net.minecraft.util.RandomSource;

public interface Weighted {
   int getWeight();

   Object getSound(RandomSource random);

   void preloadIfRequired(SoundEngine soundEngine);
}
