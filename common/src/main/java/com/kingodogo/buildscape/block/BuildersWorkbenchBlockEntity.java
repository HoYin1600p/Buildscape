package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.item.BuildersPouchItem;
import com.kingodogo.buildscape.menu.BuildersWorkbenchMenu;
import com.kingodogo.buildscape.util.ColorGradientSolver;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class BuildersWorkbenchBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {

    public static final int SLOT_COLOR_PICKER = 0;
    public static final int SLOT_PRESETS_START = 1;
    public static final int SLOT_PRESETS_END = 9;
    public static final int SLOT_INPUT_POUCH = 10;
    public static final int SLOT_OUTPUT_POUCH = 11;
    public static final int SLOT_GRADIENT_START = 12;
    public static final int SLOT_GRADIENT_END = 20;
    public static final int SLOT_GRADIENT_INPUT_START = 21;
    public static final int SLOT_GRADIENT_INPUT_END = 29;
    public static final int TOTAL_SLOTS = 30;

    private static final int TAB_COUNT = 2;
    private static final int RESULT_COUNT = 9;
    private static final int FILTER_MASK_VERSION = 3;

    private final NonNullList<ItemStack> items = NonNullList.withSize(TOTAL_SLOTS, ItemStack.EMPTY);
    private final int[][] resultOffsetsByTab = new int[TAB_COUNT][RESULT_COUNT];
    private int activeTab = 0;
    private int filterMask = ColorGradientSolver.FILTER_DEFAULT;
    private int copyProgress = 0;
    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> copyProgress;
                case 1 -> activeTab;
                case 2 -> filterMask;
                default -> {
                    if (index >= 3 && index < 3 + TAB_COUNT * RESULT_COUNT) {
                        int offsetIndex = index - 3;
                        yield resultOffsetsByTab[offsetIndex / RESULT_COUNT][offsetIndex % RESULT_COUNT];
                    }
                    yield 0;
                }
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> copyProgress = value;
                case 1 -> activeTab = value;
                case 2 -> filterMask = value;
                default -> {
                    if (index >= 3 && index < 3 + TAB_COUNT * RESULT_COUNT) {
                        int offsetIndex = index - 3;
                        resultOffsetsByTab[offsetIndex / RESULT_COUNT][offsetIndex % RESULT_COUNT] = Math.max(0, value);
                    }
                }
            }
        }

        @Override
        public int getCount() {
            return 3 + TAB_COUNT * RESULT_COUNT;
        }
    };
    public BuildersWorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BUILDERS_WORKBENCH_TYPE, pos, state);
    }
    @Override
    public Component getDisplayName() {
        return ComponentHelper.translatable("block.buildscape.builders_workbench");
    }
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
        return new BuildersWorkbenchMenu(windowId, playerInv, this);
    }
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
        ItemStack result = com.kingodogo.buildscape.platform.Services.PLATFORM.removeItem(items, slot, amount);
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
        return com.kingodogo.buildscape.platform.Services.PLATFORM.takeItem(items, slot);
    }
    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack oldStack = items.get(slot);
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
        setChanged();

        if (slot == SLOT_COLOR_PICKER) {
            if (oldStack.getItem() != stack.getItem()) {
                resetResultOffsets(0);
            }
            updateColorPickerResults();
        } else if (slot >= SLOT_GRADIENT_INPUT_START && slot <= SLOT_GRADIENT_INPUT_END) {
            resetResultOffsets(1);
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

        List<ItemStack> solved = ColorGradientSolver.solveColorPicker(
                target, filterMask, resultOffsetsByTab[0]);

        for (int i = 0; i < 9; i++) {
            this.items.set(SLOT_PRESETS_START + i, solved.get(i));
        }
        setChanged();
    }
    public void updateGradientResults() {
        if (this.level == null || this.level.isClientSide()) return;

        List<ItemStack> anchors = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            anchors.add(this.getItem(SLOT_GRADIENT_INPUT_START + i));
        }
        List<ItemStack> solved = ColorGradientSolver.solveGradient(
                anchors, filterMask, resultOffsetsByTab[1]);
        for (int i = 0; i < 9; i++) {
            this.items.set(SLOT_GRADIENT_START + i, solved.get(i));
        }
        setChanged();
    }
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        ItemStack inPouch = this.getItem(SLOT_INPUT_POUCH);
        ItemStack outPouch = this.getItem(SLOT_OUTPUT_POUCH);

        boolean canCopy = !inPouch.isEmpty()
                && isPouch(inPouch)
                && outPouch.isEmpty()
                && hasSolvedItems();

        if (canCopy) {
            ItemStack pouchCopy = inPouch.copy();
            if (!writeSolvedToPouch(pouchCopy)) {
                if (copyProgress != -1) {
                    copyProgress = -1;
                    setChanged();
                }
                return;
            }

            copyProgress = Math.max(0, copyProgress) + 1;
            setChanged();
            if (copyProgress >= 40) {
                this.setItem(SLOT_OUTPUT_POUCH, pouchCopy);
                this.setItem(SLOT_INPUT_POUCH, ItemStack.EMPTY);
                com.kingodogo.buildscape.platform.Services.PLATFORM.playExperienceOrbPickup(level, pos);
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
        if (pouch.getItem() instanceof BuildersPouchItem) {
            BuildersPouchItem.setFilters(pouch, solved);
            return true;
        }

        if (pouch.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock) {
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
        CompoundTag beTag = com.kingodogo.buildscape.platform.Services.PLATFORM.getCustomData(shulker, true);
        if (beTag == null) return false;
        List<String> savedFilters = com.kingodogo.buildscape.platform.Services.PLATFORM.getTagStringList(beTag, "GhostFilters");

        List<String> filters = new ArrayList<>(27);
        for (int i = 0; i < 27; i++) {
            filters.add(i < savedFilters.size() ? savedFilters.get(i) : "");
        }

        int targetRow = 0;
        boolean wroteAnyFilter = false;
        for (int i = 0; i < 9; i++) {
            int slot = targetRow * 9 + i;
            ItemStack solvedStack = solved.get(i);
            if (!solvedStack.isEmpty()) {
                com.kingodogo.buildscape.util.CommonId itemId = com.kingodogo.buildscape.platform.Services.PLATFORM.getItemId(solvedStack.getItem());
                if (itemId != null) {
                    filters.set(slot, itemId.toString());
                    wroteAnyFilter = true;
                }
            }
        }

        if (!wroteAnyFilter) return false;

        com.kingodogo.buildscape.platform.Services.PLATFORM.updateCustomData(shulker, root -> {
            com.kingodogo.buildscape.platform.Services.PLATFORM.putTagStringList(root, "GhostFilters", filters);
        });
        return true;
    }
    @Override
    public boolean stillValid(Player player) {
        if (level == null) return false;
        if (level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5) <= 64.0;
    }

    private static final int[] AUTOMATION_SLOTS = {SLOT_INPUT_POUCH, SLOT_OUTPUT_POUCH};
    private static boolean isResultSlot(int slot) {
        return (slot >= SLOT_PRESETS_START && slot <= SLOT_PRESETS_END)
                || (slot >= SLOT_GRADIENT_START && slot <= SLOT_GRADIENT_END);
    }
    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (isResultSlot(slot) || slot == SLOT_OUTPUT_POUCH) return false;
        if (slot == SLOT_INPUT_POUCH) return isPouch(stack);
        return true;
    }
    @Override
    public int[] getSlotsForFace(Direction side) {
        return AUTOMATION_SLOTS;
    }
    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == SLOT_INPUT_POUCH && isPouch(stack);
    }
    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == SLOT_OUTPUT_POUCH;
    }
    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }
    public int getActiveTab() {
        return activeTab;
    }
    public void setActiveTab(int tab) {
        if (tab < 0 || tab > 1) return;
        this.activeTab = tab;
        setChanged();
        if (tab == 0) {
            updateColorPickerResults();
        } else {
            updateGradientResults();
        }
    }
    public int getFilterMask() {
        return filterMask;
    }
    public void setFilterMask(int mask) {
        if (mask < 0 || (mask & ~ColorGradientSolver.FILTER_STATE_MASK) != 0) return;
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
    public void incrementResultOffset(int index) {
        if (index < 0 || index >= RESULT_COUNT) return;
        if (activeTab == 1 && (index == 0 || index == RESULT_COUNT - 1)) return;
        resultOffsetsByTab[activeTab][index]++;
        setChanged();
        if (activeTab == 0) {
            updateColorPickerResults();
        } else {
            updateGradientResults();
        }
    }
    public void resetResultOffsets() {
        resetResultOffsets(activeTab);
    }
    public void resetResultOffsets(int tab) {
        Arrays.fill(resultOffsetsByTab[validTab(tab)], 0);
        setChanged();
    }

    public void resetAllResultOffsets() {
        for (int tab = 0; tab < TAB_COUNT; tab++) {
            Arrays.fill(resultOffsetsByTab[tab], 0);
        }
        setChanged();
    }

    public void setResultOffsets(int tab, int[] offsets) {
        int[] storedOffsets = resultOffsetsByTab[validTab(tab)];
        for (int i = 0; i < storedOffsets.length; i++) {
            storedOffsets[i] = offsets != null && i < offsets.length ? Math.max(0, offsets[i]) : 0;
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
                || (mask & ~com.kingodogo.buildscape.util.ColorGradientSolver.FILTER_STATE_MASK) != 0
                || results == null || results.size() != 9) {
            return;
        }
        if (this.filterMask != mask) {
            resetAllResultOffsets();
        }
        this.filterMask = mask;
        setResultOffsets(tab, offsets);
        int firstSlot = tab == 0 ? SLOT_PRESETS_START : SLOT_GRADIENT_START;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = results.get(i);
            ItemStack stored = stack == null || stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
            if (!stored.isEmpty()) stored.setCount(1);
            this.items.set(firstSlot + i, stored);
        }
        setChanged();
    }
    private static int validTab(int tab) {
        return tab == 1 ? 1 : 0;
    }
    public void dropContents(BlockPos pos) {
        if (this.level != null) {
            for (int slotIdx : new int[]{0, 10, 11, 21, 22, 23, 24, 25, 26, 27, 28, 29}) {
                ItemStack stack = this.getItem(slotIdx);
                if (!stack.isEmpty()) {
                    Containers.dropItemStack(this.level, pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }
        }
    }
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        loadItems(input, items);
        SavedSettings saved = SavedSettings.read(input);
        activeTab = saved.activeTab();
        filterMask = saved.filterMask();
        copyProgress = saved.copyProgress();
        System.arraycopy(saved.colorOffsets(), 0, resultOffsetsByTab[0], 0, RESULT_COUNT);
        System.arraycopy(saved.gradientOffsets(), 0, resultOffsetsByTab[1], 0, RESULT_COUNT);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        saveItems(output, items);
        new SavedSettings(activeTab, filterMask, copyProgress, resultOffsetsByTab[0], resultOffsetsByTab[1]).save(output);
    }

    static void loadItems(ValueInput input, NonNullList<ItemStack> items) {
        items.clear();
        for (ValueInput child : input.childrenListOrEmpty("Items")) {
            int slot = child.getByteOr("Slot", (byte) -1) & 255;
            if (slot < items.size()) items.set(slot, PillarBlockEntity.SavedItem.read(child));
        }
    }

    static void saveItems(ValueOutput output, NonNullList<ItemStack> items) {
        var list = output.childrenList("Items");
        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack item = items.get(slot);
            if (!item.isEmpty()) {
                ValueOutput child = list.addChild();
                child.putByte("Slot", (byte) slot);
                child.store(ItemStack.MAP_CODEC, item);
            }
        }
    }

    record SavedSettings(int activeTab, int filterMask, int copyProgress, int[] colorOffsets, int[] gradientOffsets) {
        void save(ValueOutput output) {
            output.putInt("ActiveTab", activeTab);
            output.putInt("FilterMask", filterMask);
            output.putInt("FilterMaskVersion", FILTER_MASK_VERSION);
            output.putInt("CopyProgress", copyProgress);
            output.putIntArray("ColorResultOffsets", colorOffsets);
            output.putIntArray("GradientResultOffsets", gradientOffsets);
        }

        static SavedSettings read(ValueInput input) {
            int tab = validTab(input.getIntOr("ActiveTab", 0));
            int mask = input.getIntOr("FilterMaskVersion", 0) >= FILTER_MASK_VERSION
                    ? input.getIntOr("FilterMask", 0) & ColorGradientSolver.FILTER_STATE_MASK
                    : ColorGradientSolver.FILTER_DEFAULT;
            var color = input.getIntArray("ColorResultOffsets");
            var gradient = input.getIntArray("GradientResultOffsets");
            int[] colorOffsets = offsets(color.orElseGet(() -> new int[0]));
            int[] gradientOffsets = offsets(gradient.orElseGet(() -> new int[0]));
            if (color.isEmpty() && gradient.isEmpty()) {
                int[] legacy = offsets(input.getIntArray("ResultOffsets").orElseGet(() -> new int[0]));
                if (tab == 0) colorOffsets = legacy;
                else gradientOffsets = legacy;
            }
            return new SavedSettings(tab, mask, input.getIntOr("CopyProgress", 0), colorOffsets, gradientOffsets);
        }

        private static int[] offsets(int[] saved) {
            int[] result = new int[RESULT_COUNT];
            for (int i = 0; i < Math.min(saved.length, result.length); i++) result[i] = Math.max(0, saved[i]);
            return result;
        }
    }
    public static boolean isPouch(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.getItem() instanceof BuildersPouchItem) return true;
        return stack.getItem() instanceof BlockItem bi
                && bi.getBlock() instanceof ShulkerBoxBlock;
    }
}
