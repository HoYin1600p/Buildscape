package com.kingodogo.buildscape.registry.items;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.registry.ModCreativeModeTab;
import java.util.function.Supplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * The item {@link DeferredRegister}, its single entry point and the block-item helpers. The holders in this package
 * register into the package-private {@link #ITEMS} directly, so none of them has to load another holder first.
 * Everything else reaches the register through {@link #items()}, which starts the holder chain: each holder's static
 * block initialises the previous holder before its own fields, so registration always runs in the order of the chain.
 */
public final class ItemsRegister {

    private ItemsRegister() {}

    static final DeferredRegister<Item> ITEMS = DeferredRegister.create(
            ForgeRegistries.ITEMS,
            BuildScape.MODID);

    /** The item register with every holder already loaded, so all family items come before anything added later. */
    public static DeferredRegister<Item> items() {
        CardboardItems.init();
        return ITEMS;
    }

    public static Item.Properties createBlockItemProperties() {
        return new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB);
    }

    public static RegistryObject<Item> registerBlockItem(String name, Supplier<? extends Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), createBlockItemProperties()));
    }
}
