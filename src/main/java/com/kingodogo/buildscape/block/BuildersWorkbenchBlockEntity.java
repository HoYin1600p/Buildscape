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
import java.util.ArrayList;
import java.util.List;

public class BuildersWorkbenchBlockEntity extends BlockEntity implements MenuProvider, Container {

    /**
     * Slot layout (30 total workbench slots):
     * 0        – Color Picker tool slot
     * 1–9      – Color result slots
     * 10        – Input Pouch slot
     * 11        – Output Pouch slot
     * 12–20     – Gradient Output slots (server-written, read-only for players)
     * 21–29     – Gradient Input slots (user-editable)
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
    private int filterMask = com.kingodogo.buildscape.util.ColorGradientSolver.FILTER_ALL;
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
                    return index >= 3 && index < 12 ? resultOffsets[index - 3] : 0;
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
                default:
                    if (index >= 3 && index < 12) resultOffsets[index - 3] = Math.max(0, value);
                    break;
            }
        }

        @Override
        public int getCount() {
            return 12;
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

        java.util.List<ItemStack> anchors = new java.util.ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            anchors.add(this.getItem(SLOT_GRADIENT_INPUT_START + i));
        }
        java.util.List<ItemStack> solved = com.kingodogo.buildscape.util.ColorGradientSolver
                .solveGradient(anchors, filterMask, resultOffsets);
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
                if (writeSolvedToPouch(pouchCopy)) {
                    this.setItem(SLOT_OUTPUT_POUCH, pouchCopy);
                    this.setItem(SLOT_INPUT_POUCH, ItemStack.EMPTY);
                    level.playSound(null, pos, net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP,
                            net.minecraft.sounds.SoundSource.BLOCKS, 0.5f, 1.0f);
                }
                copyProgress = 0;
                setChanged();
            }
        } else {
            if (copyProgress != 0) {
                copyProgress = 0;
                setChanged();
            }
        }
    }

    public boolean hasSolvedItems() {
        if (activeTab == 0) {
            return !this.getItem(SLOT_COLOR_PICKER).isEmpty();
        } else {
            for (int i = SLOT_GRADIENT_START; i <= SLOT_GRADIENT_END; i++) {
                if (!this.getItem(i).isEmpty()) return true;
            }
            return false;
        }
    }

    public boolean writeSolvedToPouch(ItemStack pouch) {
        List<ItemStack> solved = getSolvedItems();
        if (pouch.getItem() instanceof com.kingodogo.buildscape.item.BuildersPouchItem) {
            com.kingodogo.buildscape.item.BuildersPouchInventory inventory =
                    new com.kingodogo.buildscape.item.BuildersPouchInventory(pouch);
            for (int i = 0; i < solved.size(); i++) {
                ItemStack stored = inventory.getItem(i);
                ItemStack filter = solved.get(i);
                if (!stored.isEmpty() && (filter.isEmpty() || stored.getItem() != filter.getItem())) {
                    return false;
                }
            }
            com.kingodogo.buildscape.item.BuildersPouchItem.setFilters(pouch, solved);
            return true;
        }

        if (pouch.getItem() instanceof net.minecraft.world.item.BlockItem bi
                && bi.getBlock() instanceof net.minecraft.world.level.block.ShulkerBoxBlock) {
            return writeSolvedToShulker(pouch, solved);
        }
        return false;
    }

    private List<ItemStack> getSolvedItems() {
        List<ItemStack> solved = new ArrayList<>(9);
        int firstSlot = activeTab == 0 ? SLOT_PRESETS_START : SLOT_GRADIENT_START;
        for (int i = 0; i < 9; i++) solved.add(this.getItem(firstSlot + i));
        return solved;
    }

    private boolean writeSolvedToShulker(ItemStack shulker, List<ItemStack> solved) {
        NonNullList<ItemStack> shulkerItems = NonNullList.withSize(27, ItemStack.EMPTY);
        CompoundTag blockEntityTag = shulker.getTagElement("BlockEntityTag");
        if (blockEntityTag != null && blockEntityTag.contains("Items", 9)) {
            ContainerHelper.loadAllItems(blockEntityTag, shulkerItems);
        }

        CompoundTag beTag = shulker.getOrCreateTagElement("BlockEntityTag");
        ListTag savedFilters = beTag.getList("GhostFilters", 8);
        List<String> filters = new ArrayList<>(27);
        for (int i = 0; i < 27; i++) {
            filters.add(i < savedFilters.size() ? savedFilters.getString(i) : "");
            ItemStack item = shulkerItems.get(i);
            if (!item.isEmpty() && item.hasTag() && item.getTag().getBoolean("ghost")) {
                net.minecraft.resources.ResourceLocation id =
                        net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(item.getItem());
                if (filters.get(i).isEmpty() && id != null) filters.set(i, id.toString());
                shulkerItems.set(i, ItemStack.EMPTY);
            }
        }

        int targetRow = -1;
        for (int row = 0; row < 3 && targetRow < 0; row++) {
            boolean available = true;
            for (int col = 0; col < 9; col++) {
                int slot = row * 9 + col;
                if (!shulkerItems.get(slot).isEmpty() || !filters.get(slot).isEmpty()) {
                    available = false;
                    break;
                }
            }
            if (available) targetRow = row;
        }
        if (targetRow < 0) return false;

        for (int i = 0; i < 9; i++) {
            int slot = targetRow * 9 + i;
            ItemStack solvedStack = solved.get(i);
            net.minecraft.resources.ResourceLocation itemId = solvedStack.isEmpty() ? null
                    : net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(solvedStack.getItem());
            filters.set(slot, itemId == null ? "" : itemId.toString());
        }

        ListTag newGhostFiltersList = new ListTag();
        for (String filter : filters) newGhostFiltersList.add(net.minecraft.nbt.StringTag.valueOf(filter));
        ContainerHelper.saveAllItems(beTag, shulkerItems);
        beTag.put("GhostFilters", newGhostFiltersList);
        return true;
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
        if (tab < 0 || tab > 1) return;
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
        if (mask < 0 || mask > com.kingodogo.buildscape.util.ColorGradientSolver.FILTER_ALL) return;
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

    public void setResultOffsets(int[] offsets) {
        for (int i = 0; i < resultOffsets.length; i++) {
            resultOffsets[i] = offsets != null && i < offsets.length ? Math.max(0, offsets[i]) : 0;
        }
        setChanged();
    }

    public boolean resultsMatchInputs(int tab, List<ItemStack> results) {
        if (results == null || results.size() != 9) return false;
        if (tab == 0) {
            ItemStack input = this.getItem(SLOT_COLOR_PICKER);
            if (input.isEmpty()) return results.stream().allMatch(ItemStack::isEmpty);
            return input.getItem() instanceof net.minecraft.world.item.BlockItem;
        }

        int anchors = 0;
        int first = -1;
        int last = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack input = this.getItem(SLOT_GRADIENT_INPUT_START + i);
            if (!input.isEmpty()) {
                anchors++;
                if (first < 0) first = i;
                last = i;
                if (results.get(i).getItem() != input.getItem()) return false;
            }
        }
        if (anchors < 2) return results.stream().allMatch(ItemStack::isEmpty);
        for (int i = 0; i < first; i++) if (!results.get(i).isEmpty()) return false;
        for (int i = last + 1; i < 9; i++) if (!results.get(i).isEmpty()) return false;
        return true;
    }

    public void applyClientResults(int tab, int mask, int[] offsets, List<ItemStack> results) {
        if (tab < 0 || tab > 1 || mask < 0
                || mask > com.kingodogo.buildscape.util.ColorGradientSolver.FILTER_ALL
                || results == null || results.size() != 9) {
            return;
        }
        this.filterMask = mask;
        setResultOffsets(offsets);
        int firstSlot = tab == 0 ? SLOT_PRESETS_START : SLOT_GRADIENT_START;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = results.get(i);
            ItemStack stored = stack == null || stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
            if (!stored.isEmpty()) stored.setCount(1);
            this.items.set(firstSlot + i, stored);
        }
        setChanged();
    }

    public void incrementResultOffset(int index) {
        if (index < 0 || index >= resultOffsets.length) return;
        if (activeTab == 1 && (index == 0 || index == resultOffsets.length - 1)) return;
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
        if (tag.contains("FilterMaskVersion")) {
            this.filterMask = tag.getInt("FilterMask")
                    & com.kingodogo.buildscape.util.ColorGradientSolver.FILTER_ALL;
        } else {
            this.filterMask = com.kingodogo.buildscape.util.ColorGradientSolver.FILTER_ALL;
        }
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
        tag.putInt("FilterMaskVersion", 1);
        tag.putInt("CopyProgress", copyProgress);
        tag.putIntArray("ResultOffsets", resultOffsets);
     }

     public static boolean isPouch(ItemStack stack) {
         if (stack.isEmpty()) return false;
         if (stack.getItem() instanceof com.kingodogo.buildscape.item.BuildersPouchItem) return true;
         return stack.getItem() instanceof net.minecraft.world.item.BlockItem bi
                 && bi.getBlock() instanceof net.minecraft.world.level.block.ShulkerBoxBlock;
     }
}
