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
}
