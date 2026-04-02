package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.entity.pet.PetAnimation;
import com.kingodogo.buildscape.entity.pet.PetEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PetMenu extends AbstractContainerMenu {
    private final Container container;
    private final PetEntity pet;

    // Client-side constructor
    public PetMenu(int windowId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(windowId, playerInventory, new SimpleContainer(10), null);
    }

    // Server-side constructor
    public PetMenu(int windowId, Inventory playerInventory, Container container, PetEntity pet) {
        super(ModMenuTypes.PET_MENU.get(), windowId);
        checkContainerSize(container, 10);
        this.container = container;
        this.pet = pet;
        container.startOpen(playerInventory.player);

        // The Pyramid 5-3-2 Array Geometry
        int[] xPositions = {44, 62, 80, 98, 116, 44, 80, 116, 44, 116};
        int[] yPositions = {18, 18, 18, 18, 18, 36, 36, 36, 54, 54};

        for (int i = 0; i < 10; ++i) {
            this.addSlot(new Slot(container, i, xPositions[i], yPositions[i]));
        }

        // Standard Player Inventory (y=84)
        for(int row = 0; row < 3; ++row) {
            for(int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for(int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        super.clicked(slotId, dragType, clickTypeIn, player);
        if (this.pet != null && slotId >= 0 && slotId < 10) {
            if (slotId == 9) {
                this.pet.setAnimation(PetAnimation.SWAP_MAINHAND);
            } else {
                this.pet.setAnimation(PetAnimation.PROJECT_HOLOGRAM);
            }
            this.pet.animTimer = 20; // 1 second interaction physical play
        }
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            
            if (index < 10) {
                if (!this.moveItemStackTo(itemstack1, 10, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 10, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
        if (this.pet != null) {
            this.pet.setAnimation(PetAnimation.IDLE); // Return to idle when UI fully closed
            if (!this.pet.level.isClientSide) {
                this.pet.saveInventoryToPlayer(); // Exclusively dump NBT safely upon terminating the screen!
            }
        }
    }
}
