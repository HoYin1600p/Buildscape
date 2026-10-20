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
 * The item {@link DeferredRegister} and the block-item helpers, kept in a class of its own that depends on no holder.
 * The holders in this package register into it, and {@code ModItems.ITEMS} is the same object. Because it needs no
 * holder to load, any holder can be the first one touched: each holder's static block initialises the previous holder
 * before its own fields, so registration always runs in the order of the holder chain.
 */
public final class ItemsRegister {

    private ItemsRegister() {}

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(
            ForgeRegistries.ITEMS,
            BuildScape.MODID);

    public static Item.Properties createBlockItemProperties() {
        return new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB);
    }

    public static RegistryObject<Item> registerBlockItem(String name, Supplier<? extends Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), createBlockItemProperties()));
    }
}
