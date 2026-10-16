package com.kingodogo.buildscape.parity;

import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.IRegistryDelegate;

final class ClientAccess {
    private ClientAccess() {}

    static Object key(Object key) { return key instanceof IRegistryDelegate<?> delegate ? delegate.get() : key; }

    static boolean canRenderInLayer(BlockState state, RenderType layer) {
        return ItemBlockRenderTypes.canRenderInLayer(state, layer);
    }

    static Map<?, ?> blockColors(Minecraft minecraft) {
        return DumpIO.table(BlockColors.class, minecraft.getBlockColors(), null, BlockColor.class, "blockColors", "f_92571_");
    }

    static Map<?, ?> itemColors(Minecraft minecraft) {
        return DumpIO.table(ItemColors.class, minecraft.getItemColors(), null, ItemColor.class, "itemColors", "f_92674_");
    }

    static Map<?, ?> particleProviders(Minecraft minecraft) {
        return DumpIO.table(ParticleEngine.class, minecraft.particleEngine, ResourceLocation.class, ParticleProvider.class, "providers", "f_107293_");
    }
}
