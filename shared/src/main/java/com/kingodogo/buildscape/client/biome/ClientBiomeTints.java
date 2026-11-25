package com.kingodogo.buildscape.client.biome;

import com.kingodogo.buildscape.biome.BiomeTintStore;
import com.kingodogo.buildscape.network.BiomeTintPayload;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.world.level.Level;

/** Client-side snapshots; the level object prevents data leaking between worlds. */
public final class ClientBiomeTints {
    private static final BiomeTintCache<CommonId> CACHE = new BiomeTintCache<>();

    private ClientBiomeTints() { }

    public static boolean receive(Level level, BiomeTintPayload payload) {
        if (level == null || !payload.dimension().equals(Services.PLATFORM.getDimensionId(level))) return false;
        return CACHE.replace(level, BiomeTintStore.chunkKey(payload.chunkX(), payload.chunkZ()), payload.columns());
    }

    public static BiomeTintCache<CommonId> cache() { return CACHE; }
    public static void clear() { CACHE.clear(); }
}
