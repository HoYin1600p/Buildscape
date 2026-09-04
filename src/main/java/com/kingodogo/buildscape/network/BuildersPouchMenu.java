package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.item.BuildersPouchInventory;
import com.kingodogo.buildscape.item.BuildersPouchItem;
import com.kingodogo.buildscape.util.GhostFilterMenu;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public class BuildersPouchMenu extends AbstractContainerMenu implements GhostFilterMenu {
    private static final int POUCH_SLOTS = BuildersPouchItem.SLOT_COUNT;

    private static final int POUCH_X = 14;
    private static final int POUCH_Y = 21;
    private static final int INV_X = 14;
    private static final int INV_Y = 52;
    private static final int HOTBAR_Y = 110;

    private final BuildersPouchInventory pouchInventory;
    private final ContainerData filterData;
    private final InteractionHand hand;

    public BuildersPouchMenu(int id, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(id, playerInventory, buffer.readEnum(InteractionHand.class));
    }

    public BuildersPouchMenu(int id, Inventory playerInventory, InteractionHand hand) {
        super(ModMenuTypes.BUILDERS_POUCH_MENU.get(), id);
        this.hand = hand;
        ItemStack pouch = playerInventory.player.getItemInHand(hand);
        this.pouchInventory = new BuildersPouchInventory(pouch);
        List<String> filters = BuildersPouchItem.getFilters(pouch);

        if (playerInventory.player.level.isClientSide) {
            this.filterData = new SimpleContainerData(POUCH_SLOTS);
        } else {
            this.filterData = new ContainerData() {
                @Override
                public int get(int index) {
                    if (index < 0 || index >= filters.size() || filters.get(index).isEmpty()) return 0;
                    String filterStr = filters.get(index);
                    if (!net.minecraft.resources.ResourceLocation.isValidResourceLocation(filterStr)) return 0;
                    Item item = Registry.ITEM.get(new net.minecraft.resources.ResourceLocation(filterStr));
                    if (item == null || item == net.minecraft.world.item.Items.AIR) return 0;
                    return Registry.ITEM.getId(item) + 1;
                }

                @Override
                public void set(int index, int value) {
                }

                @Override
                public int getCount() {
                    return POUCH_SLOTS;
                }
            };
        }
        addDataSlots(filterData);

        for (int col = 0; col < POUCH_SLOTS; col++) {
            final int pouchSlot = col;
            addSlot(new Slot(pouchInventory, col, POUCH_X + col * 18, POUCH_Y) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    Item filter = BuildersPouchMenu.this.buildscape$getFilterItem(pouchSlot);
                    return filter != null && stack.getItem() == filter && !(stack.getItem() instanceof BuildersPouchItem);
                }
            });
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, INV_X + col * 18, INV_Y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            final int hotbarSlot = col;
            addSlot(new Slot(playerInventory, col, INV_X + col * 18, HOTBAR_Y) {
                private boolean isOpenPouchSlot() {
                    return BuildersPouchMenu.this.hand == InteractionHand.MAIN_HAND
                            && playerInventory.selected == hotbarSlot;
                }

                @Override
                public boolean mayPickup(Player player) {
                    return !isOpenPouchSlot();
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return !isOpenPouchSlot();
                }
            });
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack source = slot.getItem();
        ItemStack original = source.copy();
        boolean moved = index < POUCH_SLOTS
                ? moveItemStackTo(source, POUCH_SLOTS, slots.size(), true)
                : moveItemStackTo(source, 0, POUCH_SLOTS, false);
        if (!moved) return ItemStack.EMPTY;

        if (source.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, source);
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return pouchInventory.stillValid(player)
                && player.getItemInHand(hand).getItem() instanceof BuildersPouchItem;
    }

    @Nullable
    @Override
    public Item buildscape$getFilterItem(int menuSlot) {
        if (menuSlot < 0 || menuSlot >= POUCH_SLOTS) return null;
        int rawId = (filterData.get(menuSlot) & 0xFFFF) - 1;
        if (rawId < 0) return null;
        Item item = Registry.ITEM.byId(rawId);
        return (item == null || item == net.minecraft.world.item.Items.AIR) ? null : item;
    }

    @Override
    public int buildscape$getFilterSlotCount() {
        return POUCH_SLOTS;
    }
}
