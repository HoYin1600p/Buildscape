package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.util.StonecutterMenuExtension;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
    @Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
    private void onBeforeClicked(int slotId, int buttonId, ClickType clickType, Player player, CallbackInfo ci) {
        AbstractContainerMenu containerMenu = (AbstractContainerMenu) (Object) this;

        // Custom Quick Move (Shift-Click) handling to insert items matching ghost filters
        if (clickType == ClickType.QUICK_MOVE && slotId >= 0 && slotId < containerMenu.slots.size()) {
            net.minecraft.world.inventory.Slot clickedSlot = containerMenu.getSlot(slotId);
            ItemStack clickedStack = clickedSlot.getItem();
            if (!clickedStack.isEmpty() && !(clickedStack.hasTag() && clickedStack.getTag().getBoolean("ghost"))) {
                // Search for matching empty/ghost filter slot in the container
                for (int i = 0; i < containerMenu.slots.size(); i++) {
                    net.minecraft.world.inventory.Slot targetSlot = containerMenu.getSlot(i);
                    if (targetSlot.container instanceof com.kingodogo.buildscape.util.GhostFilterable filterable) {
                        String filterId = filterable.buildscape$getGhostFilters()[targetSlot.getContainerSlot()];
                        if (filterId != null && !filterId.isEmpty()) {
                            net.minecraft.resources.ResourceLocation resLoc = new net.minecraft.resources.ResourceLocation(filterId);
                            net.minecraft.world.item.Item filterItem = net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(resLoc);
                            if (filterItem != null && clickedStack.getItem() == filterItem) {
                                ItemStack targetStack = targetSlot.getItem();
                                if (targetStack.isEmpty() || (targetStack.hasTag() && targetStack.getTag().getBoolean("ghost"))) {
                                    // Move item to the filter slot
                                    ItemStack newSlotStack = clickedStack.copy();
                                    targetSlot.set(newSlotStack);
                                    clickedSlot.set(ItemStack.EMPTY);
                                    containerMenu.broadcastChanges();
                                    ci.cancel();
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (slotId >= 0 && slotId < containerMenu.slots.size()) {
            net.minecraft.world.inventory.Slot slot = containerMenu.getSlot(slotId);
            if (slot.container instanceof com.kingodogo.buildscape.util.GhostFilterable filterable) {
                String filterId = filterable.buildscape$getGhostFilters()[slot.getContainerSlot()];
                if (filterId != null && !filterId.isEmpty()) {
                    net.minecraft.resources.ResourceLocation resLoc = new net.minecraft.resources.ResourceLocation(filterId);
                    net.minecraft.world.item.Item filterItem = net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(resLoc);
                    if (filterItem != null) {
                        ItemStack slotStack = slot.getItem();
                        // Slot is showing the ghost item
                        if (slotStack.isEmpty() || (slotStack.hasTag() && slotStack.getTag().getBoolean("ghost"))) {
                            if (clickType == ClickType.PICKUP) {
                                ItemStack carried = containerMenu.getCarried();
                                if (!carried.isEmpty() && carried.getItem() == filterItem && !(carried.hasTag() && carried.getTag().getBoolean("ghost"))) {
                                    if (buttonId == 0) { // Left click: place all
                                        ItemStack newSlotStack = carried.copy();
                                        slot.set(newSlotStack);
                                        containerMenu.setCarried(ItemStack.EMPTY);
                                    } else if (buttonId == 1) { // Right click: place 1
                                        ItemStack newSlotStack = carried.copy();
                                        newSlotStack.setCount(1);
                                        slot.set(newSlotStack);
                                        carried.shrink(1);
                                        containerMenu.setCarried(carried);
                                    }
                                    containerMenu.broadcastChanges();
                                    ci.cancel();
                                    return;
                                }
                            }
                            // Cancel all other interactions when slot is showing ghost filter
                            ci.cancel();
                            return;
                        }
                    }
                }
            }
        }

        ItemStack carried = containerMenu.getCarried();
        if (!carried.isEmpty() && carried.hasTag() && carried.getTag().getBoolean("ghost")) {
            ci.cancel();
            return;
        }

        if ((Object) this instanceof StonecutterMenu) {
            StonecutterMenu menu = (StonecutterMenu) (Object) this;
            if (slotId == 1 && ((StonecutterMenuExtension) menu).buildscape$isCutAll()) {
                ci.cancel();

                if (!player.level.isClientSide) {
                    ItemStack inputStack = menu.getSlot(0).getItem();
                    if (!inputStack.isEmpty()) {
                        int recipeIndex = menu.getSelectedRecipeIndex();
                        if (recipeIndex >= 0 && recipeIndex < menu.getRecipes().size()) {
                            StonecutterRecipe recipe = menu.getRecipes().get(recipeIndex);
                            ItemStack resultPrototype = recipe.assemble(menu.container);
                            if (!resultPrototype.isEmpty()) {
                                Item inputItem = inputStack.getItem();
                                int totalInputCount = inputStack.getCount();
                                List<Integer> playerInvSlots = new ArrayList<>();

                                for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                                    ItemStack invStack = player.getInventory().getItem(i);
                                    if (!invStack.isEmpty() && invStack.getItem() == inputItem && ItemStack.isSameItemSameTags(invStack, inputStack)) {
                                        totalInputCount += invStack.getCount();
                                        playerInvSlots.add(i);
                                    }
                                }

                                if (totalInputCount > 0) {
                                    int outputMultiplier = resultPrototype.getCount();
                                    int toConsume = totalInputCount;

                                    // 1. Consume from input slot first
                                    int fromInput = Math.min(toConsume, inputStack.getCount());
                                    inputStack.shrink(fromInput);
                                    menu.getSlot(0).set(inputStack);
                                    toConsume -= fromInput;

                                    // 2. Consume from player inventory
                                    for (int slotIdx : playerInvSlots) {
                                        if (toConsume <= 0) break;
                                        ItemStack invStack = player.getInventory().getItem(slotIdx);
                                        int fromInv = Math.min(toConsume, invStack.getCount());
                                        invStack.shrink(fromInv);
                                        player.getInventory().setItem(slotIdx, invStack);
                                        toConsume -= fromInv;
                                    }

                                    int totalConsumed = totalInputCount - toConsume;
                                    int totalOutput = totalConsumed * outputMultiplier;
                                    int maxStackSize = resultPrototype.getMaxStackSize();
                                    int remainingOutput = totalOutput;

                                    // 3. Give output stacks to the player
                                    while (remainingOutput > 0) {
                                        int toGive = Math.min(remainingOutput, maxStackSize);
                                        ItemStack outputStack = resultPrototype.copy();
                                        outputStack.setCount(toGive);
                                        if (!player.getInventory().add(outputStack)) {
                                            player.drop(outputStack, false);
                                        }
                                        remainingOutput -= toGive;
                                    }

                                    // 4. Award recipe
                                    player.awardRecipes(java.util.Collections.singleton(recipe));

                                    // 5. Refresh the recipe list
                                    ((StonecutterMenuAccessor) menu).callSetupRecipeList(menu.container, menu.getSlot(0).getItem());

                                    // 6. Broadcast changes to container
                                    menu.broadcastChanges();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "clicked", at = @At("TAIL"))
    private void onAfterClicked(int slotId, int buttonId, ClickType clickType, Player player, CallbackInfo ci) {
        AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;
        for (int i = 0; i < menu.slots.size(); i++) {
            net.minecraft.world.inventory.Slot slot = menu.getSlot(i);
            if (slot.container instanceof com.kingodogo.buildscape.util.GhostFilterable filterable) {
                String filterId = filterable.buildscape$getGhostFilters()[slot.getContainerSlot()];
                if (filterId != null && !filterId.isEmpty()) {
                    ItemStack slotStack = slot.getItem();
                    if (slotStack.isEmpty()) {
                        net.minecraft.resources.ResourceLocation resLoc = new net.minecraft.resources.ResourceLocation(filterId);
                        net.minecraft.world.item.Item filterItem = net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(resLoc);
                        if (filterItem != null) {
                            ItemStack ghostStack = new ItemStack(filterItem);
                            ghostStack.setCount(1);
                            ghostStack.getOrCreateTag().putBoolean("ghost", true);
                            slot.set(ghostStack);
                        }
                    }
                }
            }
        }
    }
}
