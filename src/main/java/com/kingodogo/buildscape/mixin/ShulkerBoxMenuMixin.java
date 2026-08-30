package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.util.GhostFilterMenu;
import com.kingodogo.buildscape.util.GhostFilterable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(ShulkerBoxMenu.class)
public abstract class ShulkerBoxMenuMixin extends AbstractContainerMenu implements GhostFilterMenu {
    @Unique
    private static final int BUILDSCAPE_FILTER_SLOTS = 27;
    @Unique
    private final int[] buildscape$filterIds = new int[BUILDSCAPE_FILTER_SLOTS];
    @Unique
    private GhostFilterable buildscape$filterSource;

    protected ShulkerBoxMenuMixin(@Nullable MenuType<?> menuType, int id) {
        super(menuType, id);
    }

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;)V",
            at = @At("TAIL"))
    private void addFilterData(int id, Inventory inventory, Container container, CallbackInfo ci) {
        if (container instanceof GhostFilterable filterable) buildscape$filterSource = filterable;
        for (int slot = 0; slot < BUILDSCAPE_FILTER_SLOTS; slot++) {
            final int filterSlot = slot;
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    if (buildscape$filterSource != null) {
                        String[] ghostFilters = buildscape$filterSource.buildscape$getGhostFilters();
                        if (ghostFilters != null && filterSlot < ghostFilters.length) {
                            String filter = ghostFilters[filterSlot];
                            if (filter != null && !filter.isEmpty() && ResourceLocation.isValidResourceLocation(filter)) {
                                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(filter));
                                if (item != null && item != Items.AIR) {
                                    return Registry.ITEM.getId(item) + 1;
                                }
                            }
                        }
                    }
                    return 0;
                }

                @Override
                public void set(int value) {
                    buildscape$filterIds[filterSlot] = value & 0xFFFF;
                }
            });
        }
    }

    @Nullable
    @Override
    public Item buildscape$getFilterItem(int menuSlot) {
        if (menuSlot < 0 || menuSlot >= BUILDSCAPE_FILTER_SLOTS) return null;
        if (buildscape$filterSource != null) {
            String[] ghostFilters = buildscape$filterSource.buildscape$getGhostFilters();
            if (ghostFilters != null && menuSlot < ghostFilters.length) {
                String filter = ghostFilters[menuSlot];
                if (filter != null && !filter.isEmpty() && ResourceLocation.isValidResourceLocation(filter)) {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(filter));
                    if (item != null && item != Items.AIR) return item;
                }
            }
            return null;
        }
        int rawId = buildscape$filterIds[menuSlot] - 1;
        if (rawId < 0) return null;
        Item item = Registry.ITEM.byId(rawId);
        return (item == null || item == Items.AIR) ? null : item;
    }

    @Override
    public int buildscape$getFilterSlotCount() {
        return BUILDSCAPE_FILTER_SLOTS;
    }
}

