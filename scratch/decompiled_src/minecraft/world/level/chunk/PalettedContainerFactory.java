package net.minecraft.world.level.chunk;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public record PalettedContainerFactory(Strategy blockStatesStrategy, BlockState defaultBlockState, Codec blockStatesContainerCodec, Strategy biomeStrategy, Holder defaultBiome, Codec biomeContainerCodec) {
   public static PalettedContainerFactory create(final RegistryAccess registries) {
      Strategy blockStateStrategy = Strategy.createForBlockStates(Block.BLOCK_STATE_REGISTRY);
      BlockState defaultBlockState = Blocks.AIR.defaultBlockState();
      Registry biomes = registries.lookupOrThrow(Registries.BIOME);
      Strategy biomeStrategy = Strategy.createForBiomes(biomes.asHolderIdMap());
      Holder.Reference defaultBiome = biomes.getOrThrow(Biomes.PLAINS);
      return new PalettedContainerFactory(blockStateStrategy, defaultBlockState, PalettedContainer.codecRW(BlockState.CODEC, blockStateStrategy, defaultBlockState), biomeStrategy, defaultBiome, PalettedContainer.codecRO(biomes.holderByNameCodec(), biomeStrategy, defaultBiome));
   }

   public PalettedContainer createForBlockStates() {
      return new PalettedContainer(this.defaultBlockState, this.blockStatesStrategy);
   }

   public PalettedContainer createForBiomes() {
      return new PalettedContainer(this.defaultBiome, this.biomeStrategy);
   }
}
