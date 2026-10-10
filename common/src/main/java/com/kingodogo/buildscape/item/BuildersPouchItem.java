package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.network.chat.Component;
public class BuildersPouchItem extends Item {
    public static final int SLOT_COUNT = 9;
    private static final String DATA_KEY = "BuildersPouch";
    private static final String FILTERS_KEY = "Filters";
    public BuildersPouchItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    public static List<String> getFilters(ItemStack pouch) {
        List<String> filters = new ArrayList<>(SLOT_COUNT);
        for (int i = 0; i < SLOT_COUNT; i++) filters.add("");

        CompoundTag data = getData(pouch, false);
        if (data == null || !data.contains(FILTERS_KEY)) return filters;

        List<String> rawList = Services.PLATFORM.getTagStringList(data, FILTERS_KEY);
        for (int i = 0; i < SLOT_COUNT && i < rawList.size(); i++) {
            String id = rawList.get(i);
            if (CommonId.tryParse(id) != null) {
                filters.set(i, id);
            }
        }
        return filters;
    }
    public static void setFilters(ItemStack pouch, List<ItemStack> palette) {
        List<String> idStrings = new ArrayList<>();
        for (int i = 0; i < SLOT_COUNT; i++) {
            ItemStack stack = i < palette.size() ? palette.get(i) : ItemStack.EMPTY;
            CommonId id = stack.isEmpty() ? null : Services.PLATFORM.getItemId(stack.getItem());
            idStrings.add(id == null ? "" : id.toString());
        }
        CompoundTag data = getData(pouch, true);
        if (data != null) {
            Services.PLATFORM.putTagStringList(data, FILTERS_KEY, idStrings);
            saveData(pouch, data);
        }
    }
    public static boolean hasFilters(ItemStack pouch) {
        for (String filter : getFilters(pouch)) {
            if (!filter.isEmpty()) return true;
        }
        return false;
    }
    public static void clearFilters(ItemStack pouch) {
        CompoundTag data = getData(pouch, false);
        if (data != null) {
            data.remove(FILTERS_KEY);
            saveData(pouch, data);
        }
    }
    public static CompoundTag getData(ItemStack pouch, boolean create) {
        CompoundTag root = Services.PLATFORM.getCustomData(pouch, create);
        if (root == null) return null;
        if (create && !root.contains(DATA_KEY)) {
            root.put(DATA_KEY, new CompoundTag());
        }
        return Services.PLATFORM.getTagCompound(root, DATA_KEY);
    }
    public static void saveData(ItemStack pouch, CompoundTag data) {
        Services.PLATFORM.updateCustomData(pouch, root -> root.put(DATA_KEY, data));
    }
    public static void appendBuildersPouchTooltip(ItemStack stack, Consumer<Component> tooltip) {
        int configured = 0;
        for (String filter : getFilters(stack)) {
            if (!filter.isEmpty()) configured++;
        }
        tooltip.accept(ComponentHelper.translatable("item.buildscape.builders_pouch.configured", configured, SLOT_COUNT));
    }
}
