package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.network.BuildersPouchMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BuildersPouchItem extends Item {
    public static final int SLOT_COUNT = 9;
    private static final String DATA_KEY = "BuildersPouch";
    private static final String FILTERS_KEY = "Filters";

    public BuildersPouchItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack pouch = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            Component title = new TranslatableComponent("container.buildscape.builders_pouch");
            NetworkHooks.openGui(serverPlayer,
                    new SimpleMenuProvider((id, inventory, ignored) ->
                            new BuildersPouchMenu(id, inventory, hand), title),
                    buffer -> buffer.writeEnum(hand));
        }
        return InteractionResultHolder.sidedSuccess(pouch, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        int configured = 0;
        for (String filter : getFilters(stack)) {
            if (!filter.isEmpty()) configured++;
        }
        tooltip.add(new TranslatableComponent("item.buildscape.builders_pouch.configured", configured, SLOT_COUNT));
    }

    public static List<String> getFilters(ItemStack pouch) {
        List<String> filters = new ArrayList<>(SLOT_COUNT);
        for (int i = 0; i < SLOT_COUNT; i++) filters.add("");

        CompoundTag data = getData(pouch, false);
        if (data == null || !data.contains(FILTERS_KEY, 9)) return filters;

        ListTag list = data.getList(FILTERS_KEY, 8);
        for (int i = 0; i < SLOT_COUNT && i < list.size(); i++) {
            String id = list.getString(i);
            if (ResourceLocation.isValidResourceLocation(id)) filters.set(i, id);
        }
        return filters;
    }

    public static void setFilters(ItemStack pouch, List<ItemStack> palette) {
        ListTag filters = new ListTag();
        for (int i = 0; i < SLOT_COUNT; i++) {
            ItemStack stack = i < palette.size() ? palette.get(i) : ItemStack.EMPTY;
            ResourceLocation id = stack.isEmpty() ? null : ForgeRegistries.ITEMS.getKey(stack.getItem());
            filters.add(StringTag.valueOf(id == null ? "" : id.toString()));
        }
        getData(pouch, true).put(FILTERS_KEY, filters);
    }

    public static boolean hasFilters(ItemStack pouch) {
        for (String filter : getFilters(pouch)) {
            if (!filter.isEmpty()) return true;
        }
        return false;
    }

    public static void clearFilters(ItemStack pouch) {
        CompoundTag data = getData(pouch, false);
        if (data != null) data.remove(FILTERS_KEY);
    }

    @Nullable
    public static CompoundTag getData(ItemStack pouch, boolean create) {
        CompoundTag root = create ? pouch.getOrCreateTag() : pouch.getTag();
        if (root == null) return null;
        if (create) return root.getCompound(DATA_KEY).isEmpty()
                ? createData(root)
                : root.getCompound(DATA_KEY);
        return root.contains(DATA_KEY, 10) ? root.getCompound(DATA_KEY) : null;
    }

    private static CompoundTag createData(CompoundTag root) {
        CompoundTag data = new CompoundTag();
        root.put(DATA_KEY, data);
        return data;
    }
}
