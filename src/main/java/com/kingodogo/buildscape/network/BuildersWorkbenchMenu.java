package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.block.BuildersWorkbenchBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BuildersWorkbenchMenu extends AbstractContainerMenu {

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
        this.addSlot(new Slot(be, 0, 27, 42) {
            @Override
            public boolean isActive() {
                return be.getActiveTab() == 0;
            }
        });

        // Slots 1–9: Color Presets grid (indices 1–9, active on Tab 0) - Read-Only
        for (int i = 0; i < 9; i++) {
            int col = i % 3;
            int row = i / 3;
            this.addSlot(new Slot(be, 1 + i, 86 + col * 20, 22 + row * 20) {
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
        this.addSlot(new Slot(be, 10, 47, 124) {
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
        this.addSlot(new Slot(be, 11, 175, 124) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean isActive() {
                return be.getActiveTab() == 0;
            }
        });

        // Player Inventory slots for Tab 0 (Indices 12 to 47)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 39 + col * 18, 175 + row * 18) {
                    @Override
                    public boolean isActive() {
                        return be.getActiveTab() == 0;
                    }
                });
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 39 + col * 18, 233) {
                @Override
                public boolean isActive() {
                    return be.getActiveTab() == 0;
                }
            });
        }

        // ── Tab 1: Gradient Builder Layout Slots (Indices 48 to 103) ───────────

        // Slots 12–20: Gradient Output (indices 12–20, active on Tab 1)
        for (int i = 0; i < 9; i++) {
            this.addSlot(new WbOutputSlot(be, 12 + i, 9 + i * 18, 78) {
                @Override
                public boolean isActive() {
                    return be.getActiveTab() == 1;
                }
            });
        }

        // Slot 10: Input Pouch (index 10, active on Tab 1) - Pouch Only
        this.addSlot(new Slot(be, 10, 9, 112) {
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
        this.addSlot(new Slot(be, 11, 153, 112) {
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
            this.addSlot(new WbInputSlot(be, 21 + i, 9 + i * 18, 48) {
                @Override
                public boolean isActive() {
                    return be.getActiveTab() == 1;
                }
            });
        }

        // Player Inventory slots for Tab 1 (Indices 68 to 103)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 39 + col * 18, 159 + row * 18) {
                    @Override
                    public boolean isActive() {
                        return be.getActiveTab() == 1;
                    }
                });
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 39 + col * 18, 217) {
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
