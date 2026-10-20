package com.kingodogo.buildscape.registry.blocks;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * The block {@link DeferredRegister}, kept in a class of its own that depends on no holder. The holders in this package
 * register into it, and {@code ModBlocks.BLOCKS} is the same object. Because it needs no holder to load, any holder
 * can be the first one touched: each holder's static block initialises the previous holder before its own fields, so
 * registration always runs in the order of the holder chain.
 */
public final class BlocksRegister {

    private BlocksRegister() {}

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(
            ForgeRegistries.BLOCKS,
            BuildScape.MODID);
}
