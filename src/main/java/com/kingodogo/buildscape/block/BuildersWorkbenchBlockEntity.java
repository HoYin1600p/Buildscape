package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.network.BuildersWorkbenchMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.ArrayList;

public class BuildersWorkbenchBlockEntity extends BlockEntity implements MenuProvider, Container {

    /**
     * Slot layout (22 total workbench slots):
     * 0        – Color Picker tool slot
     * 1–9      – Color Preset / Gradient Input slots (user-editable)
     * 10        – Input Pouch slot
     * 11        – Output Pouch slot
     * 12–20     – Gradient Output slots (server-written, read-only for players)
     */
    public static final int SLOT_COLOR_PICKER = 0;
    public static final int SLOT_PRESETS_START = 1;
    public static final int SLOT_PRESETS_END = 9;
    public static final int SLOT_INPUT_POUCH = 10;
    public static final int SLOT_OUTPUT_POUCH = 11;
    public static final int SLOT_GRADIENT_START = 12;
    public static final int SLOT_GRADIENT_END = 20;
    public static final int SLOT_GRADIENT_INPUT_START = 21;
    public static final int SLOT_GRADIENT_INPUT_END = 29;
    public static final int TOTAL_SLOTS = 30; // 0–29

    private final NonNullList<ItemStack> items = NonNullList.withSize(TOTAL_SLOTS, ItemStack.EMPTY);
    private final int[] resultOffsets = new int[9];
    // Persisted filter/tab state
    private int activeTab = 0; // 0=ColorPicker, 1=GradientBuilder
    private int filterMask = 0; // bitmask of active filter chips
    private int copyProgress = 0;
    public final net.minecraft.world.inventory.ContainerData dataAccess = new net.minecraft.world.inventory.ContainerData() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return copyProgress;
                case 1:
                    return activeTab;
                case 2:
                    return filterMask;
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:
                    copyProgress = value;
                    break;
                case 1:
                    activeTab = value;
                    break;
                case 2:
                    filterMask = value;
                    break;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    public BuildersWorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BUILDERS_WORKBENCH_BE.get(), pos, state);
    }

    // ── MenuProvider ──────────────────────────────────────────────────────────

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("block.buildscape.builders_workbench");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
        return new BuildersWorkbenchMenu(windowId, playerInv, this);
    }

    // ── Container ─────────────────────────────────────────────────────────────

    @Override
    public int getContainerSize() {
        return TOTAL_SLOTS;
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(items, slot, amount);
        if (!result.isEmpty()) {
            setChanged();
            if (slot == SLOT_COLOR_PICKER) {
                if (this.getItem(SLOT_COLOR_PICKER).isEmpty()) {
                    resetResultOffsets();
                }
                updateColorPickerResults();
            } else if (slot >= SLOT_GRADIENT_INPUT_START && slot <= SLOT_GRADIENT_INPUT_END) {
                resetResultOffsets();
                updateGradientResults();
            }
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack oldStack = items.get(slot);
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
        setChanged();

        if (slot == SLOT_COLOR_PICKER) {
            if (oldStack.getItem() != stack.getItem()) {
                resetResultOffsets();
            }
            updateColorPickerResults();
        } else if (slot >= SLOT_GRADIENT_INPUT_START && slot <= SLOT_GRADIENT_INPUT_END) {
            resetResultOffsets();
            updateGradientResults();
        }
    }

    public void updateColorPickerResults() {
        if (this.level == null || this.level.isClientSide()) return;

        ItemStack target = this.getItem(SLOT_COLOR_PICKER);
        if (target.isEmpty()) {
            for (int i = 0; i < 9; i++) {
                this.items.set(SLOT_PRESETS_START + i, ItemStack.EMPTY);
            }
            setChanged();
            return;
        }

        java.util.List<ItemStack> solved = com.kingodogo.buildscape.util.ColorGradientSolver.solveColorPicker(target, filterMask, resultOffsets);

        for (int i = 0; i < 9; i++) {
            this.items.set(SLOT_PRESETS_START + i, solved.get(i));
        }
        setChanged();
    }

    public void updateGradientResults() {
        if (this.level == null || this.level.isClientSide()) return;

        ItemStack start = ItemStack.EMPTY;
        ItemStack end = ItemStack.EMPTY;
        for (int i = 0; i < 9; i++) {
            ItemStack s = this.getItem(SLOT_GRADIENT_INPUT_START + i);
            if (!s.isEmpty()) {
                if (start.isEmpty()) start = s;
                end = s;
            }
        }

        if (start.isEmpty() && end.isEmpty()) {
            for (int i = 0; i < 9; i++) {
                this.items.set(SLOT_GRADIENT_START + i, ItemStack.EMPTY);
            }
            setChanged();
            return;
        }

        java.util.List<ItemStack> solved = com.kingodogo.buildscape.util.ColorGradientSolver.solveGradient(start, end, filterMask, resultOffsets);
        for (int i = 0; i < 9; i++) {
            this.items.set(SLOT_GRADIENT_START + i, solved.get(i));
        }
        setChanged();
    }

    public void tick(net.minecraft.world.level.Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        ItemStack inPouch = this.getItem(SLOT_INPUT_POUCH);
        ItemStack outPouch = this.getItem(SLOT_OUTPUT_POUCH);

        boolean canCopy = !inPouch.isEmpty()
                && isPouch(inPouch)
                && outPouch.isEmpty()
                && hasSolvedItems();

        if (canCopy) {
            copyProgress++;
            setChanged();
            if (copyProgress >= 40) { // 2 seconds
                ItemStack pouchCopy = inPouch.copy();
                writeSolvedToPouch(pouchCopy);

                this.setItem(SLOT_OUTPUT_POUCH, pouchCopy);
                this.setItem(SLOT_INPUT_POUCH, ItemStack.EMPTY);

                copyProgress = 0;
                setChanged();

                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP,
                        net.minecraft.sounds.SoundSource.BLOCKS, 0.5f, 1.0f);
            }
        } else {
            if (copyProgress != 0) {
                copyProgress = 0;
                setChanged();
            }
        }
    }

    private boolean hasSolvedItems() {
        if (activeTab == 0) {
            return !this.getItem(SLOT_COLOR_PICKER).isEmpty();
        } else {
            for (int i = SLOT_GRADIENT_START; i <= SLOT_GRADIENT_END; i++) {
                if (!this.getItem(i).isEmpty()) return true;
            }
            return false;
        }
    }

    public void writeSolvedToPouch(ItemStack pouch) {
        if (pouch.getItem() instanceof net.minecraft.world.item.BlockItem bi
                && bi.getBlock() instanceof net.minecraft.world.level.block.ShulkerBoxBlock) {
            writeSolvedToShulker(pouch);
            return;
        }

        CompoundTag pouchTag = pouch.getOrCreateTag();
        ListTag solvedList = new ListTag();

        if (activeTab == 0) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = this.getItem(SLOT_PRESETS_START + i);
                if (!stack.isEmpty()) {
                    CompoundTag slotTag = new CompoundTag();
                    slotTag.putInt("Slot", i);
                    slotTag.put("Item", stack.save(new CompoundTag()));
                    solvedList.add(slotTag);
                }
            }
        } else {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = this.getItem(SLOT_GRADIENT_START + i);
                if (!stack.isEmpty()) {
                    CompoundTag slotTag = new CompoundTag();
                    slotTag.putInt("Slot", i);
                    slotTag.put("Item", stack.save(new CompoundTag()));
                    solvedList.add(slotTag);
                }
            }
        }
        pouchTag.put("SolvedGradients", solvedList);
        pouchTag.putIntArray("StoredCounts", new int[9]);
    }

    public void writeSolvedToShulker(ItemStack shulker) {
        NonNullList<ItemStack> shulkerItems = NonNullList.withSize(27, ItemStack.EMPTY);
        CompoundTag blockEntityTag = shulker.getTagElement("BlockEntityTag");
        if (blockEntityTag != null && blockEntityTag.contains("Items", 9)) {
            ContainerHelper.loadAllItems(blockEntityTag, shulkerItems);
        }

        // Find the first row of 9 slots that doesn't have any items
        int targetRow = 0;
        for (int r = 0; r < 3; r++) {
            boolean rowHasItems = false;
            for (int c = 0; c < 9; c++) {
                int slotIdx = r * 9 + c;
                if (!shulkerItems.get(slotIdx).isEmpty()) {
                    rowHasItems = true;
                    break;
                }
            }
            if (!rowHasItems) {
                targetRow = r;
                break;
            }
            targetRow = r; // fallback to row 3 (idx 2)
        }

        // Get the 9 solved blocks based on the active tab
        List<ItemStack> solved = new ArrayList<>();
        if (this.activeTab == 0) {
            for (int i = 0; i < 9; i++) {
                solved.add(this.getItem(SLOT_PRESETS_START + i));
            }
        } else {
            for (int i = 0; i < 9; i++) {
                solved.add(this.getItem(SLOT_GRADIENT_START + i));
            }
        }

        // Populate the target row with solved items marked as ghost
        for (int i = 0; i < 9; i++) {
            int slotIdx = targetRow * 9 + i;
            ItemStack solvedStack = solved.get(i);
            if (!solvedStack.isEmpty()) {
                ItemStack ghostCopy = solvedStack.copy();
                ghostCopy.setCount(1);
                ghostCopy.getOrCreateTag().putBoolean("ghost", true);
                shulkerItems.set(slotIdx, ghostCopy);
            } else {
                shulkerItems.set(slotIdx, ItemStack.EMPTY);
            }
        }

        CompoundTag beTag = shulker.getOrCreateTagElement("BlockEntityTag");
        ContainerHelper.saveAllItems(beTag, shulkerItems);

        // Read or create GhostFilters list tag
        ListTag ghostFiltersList = new ListTag();
        if (beTag.contains("GhostFilters", 9)) {
            ghostFiltersList = beTag.getList("GhostFilters", 8);
        }
        
        // Pad to size 27 if it is smaller
        List<String> filters = new ArrayList<>();
        for (int i = 0; i < 27; i++) {
            if (i < ghostFiltersList.size()) {
                filters.add(ghostFiltersList.getString(i));
            } else {
                filters.add("");
            }
        }

        // Set the filters for the target row
        for (int i = 0; i < 9; i++) {
            int slotIdx = targetRow * 9 + i;
            ItemStack solvedStack = solved.get(i);
            if (!solvedStack.isEmpty()) {
                net.minecraft.resources.ResourceLocation itemId = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(solvedStack.getItem());
                if (itemId != null) {
                    filters.set(slotIdx, itemId.toString());
                } else {
                    filters.set(slotIdx, "");
                }
            } else {
                filters.set(slotIdx, "");
            }
        }

        // Save filters back to beTag
        ListTag newGhostFiltersList = new ListTag();
        for (int i = 0; i < 27; i++) {
            newGhostFiltersList.add(net.minecraft.nbt.StringTag.valueOf(filters.get(i)));
        }
        beTag.put("GhostFilters", newGhostFiltersList);
    }

    @Override
    public boolean stillValid(Player player) {
        if (level == null) return false;
        if (level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public int getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(int tab) {
        this.activeTab = tab;
        setChanged();
        if (tab == 0) {
            updateColorPickerResults();
        }
    }

    public int getFilterMask() {
        return filterMask;
    }

    public void setFilterMask(int mask) {
        this.filterMask = mask;
        setChanged();
        if (activeTab == 0) {
            updateColorPickerResults();
        } else {
            updateGradientResults();
        }
    }

    public int getCopyProgress() {
        return copyProgress;
    }

    public void setCopyProgress(int progress) {
        this.copyProgress = progress;
        setChanged();
    }

    public int[] getResultOffsets() {
        return resultOffsets;
    }

    public int getResultOffset(int index) {
        return resultOffsets[index];
    }

    public void incrementResultOffset(int index) {
        resultOffsets[index]++;
        setChanged();
        if (activeTab == 0) {
            updateColorPickerResults();
        } else {
            updateGradientResults();
        }
    }

    public void resetResultOffsets() {
        for (int i = 0; i < 9; i++) resultOffsets[i] = 0;
        setChanged();
    }

    // ── NBT ───────────────────────────────────────────────────────────────────

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, items);
        if (tag.contains("ActiveTab")) this.activeTab = tag.getInt("ActiveTab");
        if (tag.contains("FilterMask")) this.filterMask = tag.getInt("FilterMask");
        if (tag.contains("CopyProgress")) this.copyProgress = tag.getInt("CopyProgress");
        if (tag.contains("ResultOffsets")) {
            int[] saved = tag.getIntArray("ResultOffsets");
            for (int i = 0; i < 9 && i < saved.length; i++) {
                this.resultOffsets[i] = saved[i];
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
        tag.putInt("ActiveTab", activeTab);
        tag.putInt("FilterMask", filterMask);
        tag.putInt("CopyProgress", copyProgress);
        tag.putIntArray("ResultOffsets", resultOffsets);
     }

     public static boolean isPouch(ItemStack stack) {
         if (stack.isEmpty()) return false;
         net.minecraft.resources.ResourceLocation name = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem());
         if (name != null && name.getPath().equals("builders_pouch")) return true;
         return stack.getItem() instanceof net.minecraft.world.item.BlockItem bi
                 && bi.getBlock() instanceof net.minecraft.world.level.block.ShulkerBoxBlock;
     }
}
