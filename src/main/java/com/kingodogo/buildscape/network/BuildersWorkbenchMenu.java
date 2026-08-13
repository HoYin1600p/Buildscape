package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.block.BuildersWorkbenchBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BuildersWorkbenchMenu extends AbstractContainerMenu {
    public static final int MENU_COLOR_RESULT_START = 1;
    public static final int MENU_GRADIENT_OUTPUT_START = 48;

    // ── LAYOUT ────────────────────────────────────────────────────────────────
    // Slot coordinates are the *item* positions (the slot frame is drawn at -1/-1).
    // They are taken straight from the background artwork and MUST stay in sync
    // with the layout constants in BuildersWorkbenchScreen.
    // Colour Builder (184 x 203)
    private static final int C_PICKER_X = 25, C_PICKER_Y = 52;
    private static final int C_RESULT_X = 66, C_RESULT_Y = 34;
    private static final int C_POUCH_IN_X = 51, C_POUCH_IN_Y = 94;
    private static final int C_POUCH_OUT_X = 117, C_POUCH_OUT_Y = 94;
    // Gradient Builder (206 x 203)
    private static final int G_INPUT_X = 12, G_INPUT_Y = 37;
    private static final int G_OUTPUT_X = 12, G_OUTPUT_Y = 65;
    private static final int G_POUCH_IN_X = 51, G_POUCH_IN_Y = 94;
    private static final int G_POUCH_OUT_X = 117, G_POUCH_OUT_Y = 94;
    // Player inventory - identical on both tabs
    private static final int INV_X = 12, INV_Y = 121, HOTBAR_Y = 179;

    private final BuildersWorkbenchBlockEntity blockEntity;

    // ── Client-side constructor ────────────────────────────────
    public BuildersWorkbenchMenu(int windowId, Inventory playerInv, FriendlyByteBuf data) {
        this(windowId, playerInv, (BuildersWorkbenchBlockEntity)
                playerInv.player.level.getBlockEntity(data.readBlockPos()));
    }

    // ── Server-side constructor ───────────────────────────────────────────────
    public BuildersWorkbenchMenu(int windowId, Inventory playerInv, BuildersWorkbenchBlockEntity be) {
        super(ModMenuTypes.BUILDERS_WORKBENCH_MENU.get(), windowId);
        this.blockEntity = be;

        checkContainerSize(be, BuildersWorkbenchBlockEntity.TOTAL_SLOTS);
        this.addDataSlots(be.dataAccess);

        // ── Tab 0: Color Picker Layout Slots (Indices 0 to 47) ─────────────────

        // Slot 0: Pipette/tool slot (index 0, active on Tab 0)
        this.addSlot(new Slot(be, 0, C_PICKER_X, C_PICKER_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof net.minecraft.world.item.BlockItem;
            }

            @Override
            public boolean isActive() {
                return be.getActiveTab() == 0;
            }
        });

        // Slots 1–9: Color Presets grid (indices 1–9, active on Tab 0) - Read-Only
        for (int i = 0; i < 9; i++) {
            int col = i % 3;
            int row = i / 3;
            this.addSlot(new Slot(be, 1 + i, C_RESULT_X + col * 18, C_RESULT_Y + row * 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }

                @Override
                public boolean mayPickup(Player player) {
                    return false;
                }

                @Override
                public boolean isActive() {
                    return be.getActiveTab() == 0;
                }
            });
        }

        // Slot 10: Input Pouch (index 10, active on Tab 0) - Pouch Only
        this.addSlot(new Slot(be, 10, C_POUCH_IN_X, C_POUCH_IN_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return com.kingodogo.buildscape.block.BuildersWorkbenchBlockEntity.isPouch(stack);
            }

            @Override
            public boolean isActive() {
                return be.getActiveTab() == 0;
            }
        });

        // Slot 11: Output Pouch (index 11, active on Tab 0)
        this.addSlot(new Slot(be, 11, C_POUCH_OUT_X, C_POUCH_OUT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean isActive() {
                return be.getActiveTab() == 0;
            }
        });

        // Player Inventory slots for Tab 0 (Indices 12 to 47) - aligned with H=192 / W=176 layout
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, INV_X + col * 18, INV_Y + row * 18) {
                    @Override
                    public boolean isActive() {
                        return be.getActiveTab() == 0;
                    }
                });
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, INV_X + col * 18, HOTBAR_Y) {
                @Override
                public boolean isActive() {
                    return be.getActiveTab() == 0;
                }
            });
        }

        // ── Tab 1: Gradient Builder Layout Slots (Indices 48 to 103) ───────────

        // Slots 12–20: Gradient Output (indices 12–20, active on Tab 1)
        for (int i = 0; i < 9; i++) {
            this.addSlot(new WbOutputSlot(be, 12 + i, G_OUTPUT_X + i * 18, G_OUTPUT_Y) {
                @Override
                public boolean isActive() {
                    return be.getActiveTab() == 1;
                }
            });
        }

        // Slot 10: Input Pouch (index 10, active on Tab 1) - Pouch Only
        this.addSlot(new Slot(be, 10, G_POUCH_IN_X, G_POUCH_IN_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return com.kingodogo.buildscape.block.BuildersWorkbenchBlockEntity.isPouch(stack);
            }

            @Override
            public boolean isActive() {
                return be.getActiveTab() == 1;
            }
        });

        // Slot 11: Output Pouch (index 11, active on Tab 1)
        this.addSlot(new Slot(be, 11, G_POUCH_OUT_X, G_POUCH_OUT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean isActive() {
                return be.getActiveTab() == 1;
            }
        });

        // Slots 21–29: Gradient Inputs row (indices 21–29, active on Tab 1)
        for (int i = 0; i < 9; i++) {
            this.addSlot(new WbInputSlot(be, 21 + i, G_INPUT_X + i * 18, G_INPUT_Y) {
                @Override
                public boolean isActive() {
                    return be.getActiveTab() == 1;
                }
            });
        }

        // Player Inventory slots for Tab 1 (Indices 68 to 103)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, INV_X + col * 18, INV_Y + row * 18) {
                    @Override
                    public boolean isActive() {
                        return be.getActiveTab() == 1;
                    }
                });
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, INV_X + col * 18, HOTBAR_Y) {
                @Override
                public boolean isActive() {
                    return be.getActiveTab() == 1;
                }
            });
        }
    }

    // This method is now a no-op as all coordinates are statically set in the constructor
    public void setupSlotPositions() {
    }

    public int getCopyProgress() {
        return this.blockEntity.dataAccess.get(0);
    }

    public int getActiveTab() {
        return this.blockEntity.dataAccess.get(1);
    }

    public int getFilterMask() {
        return this.blockEntity.dataAccess.get(2);
    }

    public int getResultOffset(int slot) {
        return getResultOffset(getActiveTab(), slot);
    }

    public int getResultOffset(int tab, int slot) {
        return tab >= 0 && tab < 2 && slot >= 0 && slot < 9
                ? this.blockEntity.dataAccess.get(3 + tab * 9 + slot) : 0;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0 || id == 1) {
            this.blockEntity.setActiveTab(id);
            return true;
        }
        return false;
    }

    public BuildersWorkbenchBlockEntity getBlockEntity() {
        return blockEntity;
    }

    // ── Quick-move (shift-click) ──────────────────────────────────────────────

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            int tab = this.blockEntity.getActiveTab();
            if (tab == 0 && index >= 1 && index <= 9) {
                return ItemStack.EMPTY;
            }
            if (tab == 1 && index >= 48 && index <= 56) {
                return ItemStack.EMPTY;
            }

            ItemStack stack = slot.getItem();
            result = stack.copy();

            boolean isPlayerInv = (tab == 0) ? (index >= 12 && index < 48) : (index >= 68 && index < 104);

            if (isPlayerInv) {
                if (com.kingodogo.buildscape.block.BuildersWorkbenchBlockEntity.isPouch(stack)) {
                    int targetSlot = (tab == 0) ? 10 : 57;
                    if (!this.moveItemStackTo(stack, targetSlot, targetSlot + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stack.getItem() instanceof net.minecraft.world.item.BlockItem) {
                    if (tab == 0) {
                        // On Tab 0, route block items only to Slot 0 (Pipette input)
                        if (!this.moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
                    } else {
                        // On Tab 1, route block items to Gradient Inputs (indices 59 to 67)
                        if (!this.moveItemStackTo(stack, 59, 68, false)) return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            } else {
                int start = (tab == 0) ? 12 : 68;
                int end = start + 36;
                if (!this.moveItemStackTo(stack, start, end, true)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
            if (stack.getCount() == result.getCount()) return ItemStack.EMPTY;
            slot.onTake(player, stack);
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity.stillValid(player);
    }

    // ── Inner slot types ──────────────────────────────────────────────────────

    private static class WbInputSlot extends Slot {
        WbInputSlot(BuildersWorkbenchBlockEntity be, int index, int x, int y) {
            super(be, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof net.minecraft.world.item.BlockItem;
        }
    }

    private static class WbOutputSlot extends Slot {
        WbOutputSlot(BuildersWorkbenchBlockEntity be, int index, int x, int y) {
            super(be, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }
    }
}
