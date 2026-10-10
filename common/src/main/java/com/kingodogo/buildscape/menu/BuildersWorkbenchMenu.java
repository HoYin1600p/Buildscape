package com.kingodogo.buildscape.menu;

import com.kingodogo.buildscape.block.BuildersWorkbenchBlockEntity;
import com.kingodogo.buildscape.util.ColorGradientSolver;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
public class BuildersWorkbenchMenu extends AbstractContainerMenu {
    public static final int MENU_COLOR_RESULT_START = 1;
    public static final int MENU_GRADIENT_OUTPUT_START = 48;

    private static final int C_PICKER_X = 25, C_PICKER_Y = 42;
    private static final int C_RESULT_X = 66, C_RESULT_Y = 24;
    private static final int C_POUCH_IN_X = 51, C_POUCH_IN_Y = 84;
    private static final int C_POUCH_OUT_X = 117, C_POUCH_OUT_Y = 84;
    private static final int G_INPUT_X = 12, G_INPUT_Y = 27;
    private static final int G_OUTPUT_X = 12, G_OUTPUT_Y = 55;
    private static final int G_POUCH_IN_X = 51, G_POUCH_IN_Y = 84;
    private static final int G_POUCH_OUT_X = 117, G_POUCH_OUT_Y = 84;
    private static final int INV_X = 12, INV_Y = 111, HOTBAR_Y = 169;

    private final Container container;
    private final ContainerData data;
    private final BuildersWorkbenchBlockEntity blockEntity;
    public BuildersWorkbenchMenu(int windowId, Inventory playerInv) {
        this(windowId, playerInv, new SimpleContainer(BuildersWorkbenchBlockEntity.TOTAL_SLOTS),
                new SimpleContainerData(3 + 2 * 9), null);
    }
    public BuildersWorkbenchMenu(int windowId, Inventory playerInv, BuildersWorkbenchBlockEntity be) {
        this(windowId, playerInv, be, be.dataAccess, be);
    }
    public BuildersWorkbenchMenu(int windowId, Inventory playerInv, Container container,
                                 ContainerData data, BuildersWorkbenchBlockEntity be) {
        super(ModMenuTypes.BUILDERS_WORKBENCH_MENU, windowId);
        this.container = container;
        this.data = data;
        this.blockEntity = be;

        checkContainerSize(container, BuildersWorkbenchBlockEntity.TOTAL_SLOTS);
        this.addDataSlots(data);
        this.addSlot(new Slot(container, 0, C_PICKER_X, C_PICKER_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof BlockItem;
            }

            @Override
            public boolean isActive() {
                return getActiveTab() == 0;
            }
        });
        for (int i = 0; i < 9; i++) {
            int col = i % 3;
            int row = i / 3;
            this.addSlot(new Slot(container, 1 + i, C_RESULT_X + col * 18, C_RESULT_Y + row * 18) {
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
                    return getActiveTab() == 0;
                }
            });
        }
        this.addSlot(new Slot(container, 10, C_POUCH_IN_X, C_POUCH_IN_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return BuildersWorkbenchBlockEntity.isPouch(stack);
            }

            @Override
            public boolean isActive() {
                return getActiveTab() == 0;
            }
        });
        this.addSlot(new Slot(container, 11, C_POUCH_OUT_X, C_POUCH_OUT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean isActive() {
                return getActiveTab() == 0;
            }
        });
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, INV_X + col * 18, INV_Y + row * 18) {
                    @Override
                    public boolean isActive() {
                        return getActiveTab() == 0;
                    }
                });
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, INV_X + col * 18, HOTBAR_Y) {
                @Override
                public boolean isActive() {
                    return getActiveTab() == 0;
                }
            });
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(container, 12 + i, G_OUTPUT_X + i * 18, G_OUTPUT_Y) {
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
                    return getActiveTab() == 1;
                }
            });
        }
        this.addSlot(new Slot(container, 10, G_POUCH_IN_X, G_POUCH_IN_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return BuildersWorkbenchBlockEntity.isPouch(stack);
            }

            @Override
            public boolean isActive() {
                return getActiveTab() == 1;
            }
        });
        this.addSlot(new Slot(container, 11, G_POUCH_OUT_X, G_POUCH_OUT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean isActive() {
                return getActiveTab() == 1;
            }
        });
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(container, 21 + i, G_INPUT_X + i * 18, G_INPUT_Y) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.getItem() instanceof BlockItem;
                }

                @Override
                public boolean isActive() {
                    return getActiveTab() == 1;
                }
            });
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, INV_X + col * 18, INV_Y + row * 18) {
                    @Override
                    public boolean isActive() {
                        return getActiveTab() == 1;
                    }
                });
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, INV_X + col * 18, HOTBAR_Y) {
                @Override
                public boolean isActive() {
                    return getActiveTab() == 1;
                }
            });
        }
    }
    public int getCopyProgress() {
        return this.data.get(0);
    }
    public int getActiveTab() {
        return this.data.get(1);
    }
    public int getFilterMask() {
        return this.data.get(2);
    }
    public int getResultOffset(int slot) {
        return getResultOffset(getActiveTab(), slot);
    }
    public int getResultOffset(int tab, int slot) {
        return tab >= 0 && tab < 2 && slot >= 0 && slot < 9
                ? this.data.get(3 + tab * 9 + slot) : 0;
    }
    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0 || id == 1) {
            if (this.blockEntity != null) {
                this.blockEntity.setActiveTab(id);
            } else {
                this.data.set(1, id);
            }
            return true;
        }
        if (id >= 10 && id <= 18) {
            if (this.blockEntity != null) {
                this.blockEntity.incrementResultOffset(id - 10);
            }
            return true;
        }
        if (id >= 20 && id <= 24) {
            int flag = switch (id) {
                case 20 -> ColorGradientSolver.FILTER_SOLID;
                case 21 -> ColorGradientSolver.FILTER_TRANSPARENT;
                case 22 -> ColorGradientSolver.FILTER_NON_FULL;
                case 23 -> ColorGradientSolver.FILTER_SINGLE_TEXTURE;
                case 24 -> ColorGradientSolver.FILTER_MATCH_SHAPE;
                default -> 0;
            };
            int newMask = getFilterMask() ^ flag;
            if (this.blockEntity != null) {
                this.blockEntity.setFilterMask(newMask);
            } else {
                this.data.set(2, newMask);
            }
            return true;
        }
        if (id == 30) {
            if (this.blockEntity != null) {
                this.blockEntity.resetResultOffsets();
            }
            return true;
        }

        return false;
    }
    public BuildersWorkbenchBlockEntity getBlockEntity() {
        return blockEntity;
    }
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            int tab = getActiveTab();
            if (tab == 0 && index >= 1 && index <= 9) return ItemStack.EMPTY;
            if (tab == 1 && index >= 48 && index <= 56) return ItemStack.EMPTY;

            ItemStack stack = slot.getItem();
            result = stack.copy();

            boolean isPlayerInv = (tab == 0) ? (index >= 12 && index < 48) : (index >= 68 && index < 104);

            if (isPlayerInv) {
                if (BuildersWorkbenchBlockEntity.isPouch(stack)) {
                    int targetSlot = (tab == 0) ? 10 : 57;
                    if (!this.moveItemStackTo(stack, targetSlot, targetSlot + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stack.getItem() instanceof BlockItem) {
                    if (tab == 0) {
                        if (!this.moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
                    } else {
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
        return this.container.stillValid(player);
    }
}
