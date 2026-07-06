package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.ModBlocks;
import com.kingodogo.buildscape.block.MuffBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MuffBlockManager {

    private static final Set<BlockPos> ACTIVE_MUFFS = ConcurrentHashMap.newKeySet();

    public static void register(BlockPos pos) {
        ACTIVE_MUFFS.add(pos);
    }

    public static Set<BlockPos> getActiveMuffs() {
        return ACTIVE_MUFFS;
    }

    public static void unregister(BlockPos pos) {
        ACTIVE_MUFFS.remove(pos);
    }

    public static void clear() {
        ACTIVE_MUFFS.clear();
    }

    @SubscribeEvent
    public static void onPlaySound(PlaySoundEvent event) {
        if (event.getSound() == null) return;

        SoundInstance sound = event.getSound();
        if (sound.getSource() == net.minecraft.sounds.SoundSource.PLAYERS || sound.getSource() == net.minecraft.sounds.SoundSource.MASTER) {
            return;
        }
        double sx = sound.getX();
        double sy = sound.getY();
        double sz = sound.getZ();

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        for (BlockPos pos : ACTIVE_MUFFS) {
            BlockState state = mc.level.getBlockState(pos);
            if (state.is(ModBlocks.MUFF_BLOCK.get())) {
                if (state.getValue(MuffBlock.POWERED)) {
                    int radius = state.getValue(MuffBlock.RADIUS);
                    double dx = Math.abs((double) pos.getX() + 0.5 - sx);
                    double dy = Math.abs((double) pos.getY() + 0.5 - sy);
                    double dz = Math.abs((double) pos.getZ() + 0.5 - sz);
                    if (dx <= (double) radius && dy <= (double) radius && dz <= (double) radius) {
                        event.setSound(null);
                        break;
                    }
                }
            }
        }
    }
}
