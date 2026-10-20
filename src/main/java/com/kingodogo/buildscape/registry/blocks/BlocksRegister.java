package com.kingodogo.buildscape.registry.blocks;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * The block {@link DeferredRegister} and the single entry point to it. The holders in this package register into the
 * package-private {@link #BLOCKS} directly, so none of them has to load another holder first. Everything else reaches
 * the register through {@link #blocks()}, which starts the holder chain: each holder's static block initialises the
 * previous holder before its own fields, so touching the last one registers every block in the fixed order the ids
 * and the creative-tab layout rely on, whichever caller comes first.
 */
public final class BlocksRegister {

    private BlocksRegister() {}

    static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(
            ForgeRegistries.BLOCKS,
            BuildScape.MODID);

    /** The block register with every holder already loaded, so all family blocks come before anything added later. */
    public static DeferredRegister<Block> blocks() {
        CardboardBlocks.init();
        return BLOCKS;
    }
}
