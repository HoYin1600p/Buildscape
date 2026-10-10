package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;

import com.kingodogo.buildscape.util.GhostFilterMenu;
import com.kingodogo.buildscape.util.StonecutterMenuExtension;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {

    @Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
    private void onBeforeClicked(int slotId, int buttonId, net.minecraft.world.inventory.ContainerInput clickType, Player player, CallbackInfo ci) {
        AbstractContainerMenu containerMenu = (AbstractContainerMenu) (Object) this;
        String clickTypeName = clickType != null ? clickType.name() : "";

        if ((Object) this instanceof net.minecraft.world.inventory.ShulkerBoxMenu
                && (Object) this instanceof GhostFilterMenu filterMenu) {
            if ("QUICK_MOVE".equals(clickTypeName) && slotId >= filterMenu.buildscape$getFilterSlotCount()
                    && slotId < containerMenu.slots.size()) {
                Slot sourceSlot = containerMenu.getSlot(slotId);
                ItemStack source = sourceSlot.getItem();
                if (!source.isEmpty() && hasFilters(filterMenu)) {
                    moveToFilteredSlots(containerMenu, filterMenu, source);
                    if (source.isEmpty()) sourceSlot.set(ItemStack.EMPTY);
                    else sourceSlot.setChanged();
                    containerMenu.broadcastChanges();
                    ci.cancel();
                    return;
                }
            }

            if (slotId >= 0 && slotId < filterMenu.buildscape$getFilterSlotCount()) {
                Item filter = filterMenu.buildscape$getFilterItem(slotId);
                if (filter != null && wouldInsertMismatchedItem(containerMenu, player, clickTypeName, buttonId, filter)) {
                    ci.cancel();
                    return;
                }
            }
        }

        if ((Object) this instanceof StonecutterMenu menu) {
            if (slotId == 1 && ((StonecutterMenuExtension) menu).buildscape$isCutAll()) {
                ci.cancel();
                net.minecraft.world.level.Level level = com.kingodogo.buildscape.platform.Services.PLATFORM.getEntityLevel(player);
                if (level != null && !level.isClientSide()) {
                    MixinFactory.handleStonecutterCutAll(menu, player);
                }
            }
        }
    }

    private static boolean hasFilters(GhostFilterMenu menu) {
        for (int i = 0; i < menu.buildscape$getFilterSlotCount(); i++) {
            if (menu.buildscape$getFilterItem(i) != null) return true;
        }
        return false;
    }

    private static void moveToFilteredSlots(AbstractContainerMenu menu, GhostFilterMenu filters, ItemStack source) {
        for (int i = 0; i < filters.buildscape$getFilterSlotCount() && !source.isEmpty(); i++) {
            Item filter = filters.buildscape$getFilterItem(i);
            if (filter != null && source.getItem() == filter) {
                Slot target = menu.getSlot(i);
                if (!target.getItem().isEmpty()) {
                    target.safeInsert(source);
                }
            }
        }
        for (int i = 0; i < filters.buildscape$getFilterSlotCount() && !source.isEmpty(); i++) {
            Item filter = filters.buildscape$getFilterItem(i);
            if (filter == null) {
                Slot target = menu.getSlot(i);
                if (!target.getItem().isEmpty()) {
                    target.safeInsert(source);
                }
            }
        }
        for (int i = 0; i < filters.buildscape$getFilterSlotCount() && !source.isEmpty(); i++) {
            Item filter = filters.buildscape$getFilterItem(i);
            if (filter != null && source.getItem() == filter) {
                Slot target = menu.getSlot(i);
                if (target.getItem().isEmpty()) {
                    target.safeInsert(source);
                }
            }
        }
        for (int i = 0; i < filters.buildscape$getFilterSlotCount() && !source.isEmpty(); i++) {
            Item filter = filters.buildscape$getFilterItem(i);
            if (filter == null) {
                Slot target = menu.getSlot(i);
                if (target.getItem().isEmpty()) {
                    target.safeInsert(source);
                }
            }
        }
    }

    private static boolean wouldInsertMismatchedItem(AbstractContainerMenu menu, Player player,
                                                     String clickTypeName, int buttonId, Item filter) {
        if ("PICKUP".equals(clickTypeName) || "QUICK_CRAFT".equals(clickTypeName)) {
            ItemStack carried = menu.getCarried();
            return !carried.isEmpty() && carried.getItem() != filter;
        }
        if ("SWAP".equals(clickTypeName) && buttonId >= 0
                && buttonId < player.getInventory().getContainerSize()) {
            ItemStack swapped = player.getInventory().getItem(buttonId);
            return !swapped.isEmpty() && swapped.getItem() != filter;
        }
        return false;
    }
}
