package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.client.tooltip.BuildersPouchTooltipData;
import com.kingodogo.buildscape.client.tooltip.ShulkerBoxTooltipData;
import com.kingodogo.buildscape.config.CosmeticsConfig;
import com.kingodogo.buildscape.item.BuildersPouchItem;
import com.kingodogo.buildscape.item.FestiveStockingItem;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TagTooltipHandler {

    public static boolean isShulkerPreviewEnabled() {
        try {
            Player player = Services.PLATFORM.getClientPlayer();
            if (player != null) {
                return CosmeticsConfig.get().getShulkerPreview(player.getUUID());
            }
            return CosmeticsConfig.get().getShulkerPreview(null);
        } catch (Throwable t) {
            return true;
        }
    }

    public static void handleItemTooltip(ItemStack stack, List<Component> tooltips) {
        try {
            if (stack == null || stack.isEmpty()) return;

            if (stack.getItem() instanceof FestiveStockingItem) {
                CompoundTag tag = Services.PLATFORM.getCustomData(stack, false);
                if (tag != null) {
                    CompoundTag storedTag = Services.PLATFORM.getTagCompound(tag, "StoredItem");
                    if (storedTag != null && !storedTag.isEmpty()) {
                        ItemStack storedItem = Services.PLATFORM.loadSingleItemStack(storedTag);
                        if (!storedItem.isEmpty()) {
                            tooltips.add(
                                Services.PLATFORM.translatable(
                                    "tooltip.buildscape.festive_stocking.contains",
                                    storedItem.getCount(),
                                    storedItem.getDisplayName()
                                )
                            );
                        }
                    }
                }
            }

            if (!isShulkerPreviewEnabled()) {
                return;
            }

            if (stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock) {
                CompoundTag tag = Services.PLATFORM.getCustomData(stack, false);
                CompoundTag blockEntityTag = tag != null ? Services.PLATFORM.getTagCompound(tag, "BlockEntityTag") : null;
                if (blockEntityTag != null && (blockEntityTag.contains("GhostFilters") || blockEntityTag.contains("Items"))) {
                    if (tooltips.size() > 1) {
                        tooltips.removeIf(comp -> {
                            String str = comp.getString();
                            return str.contains(" x") || str.matches(".*x\\d+.*") || str.startsWith("and ") || str.contains("more...");
                        });
                    }
                    if (!Services.PLATFORM.hasShiftDown()) {
                        tooltips.add(Services.PLATFORM.translatable("tooltip.buildscape.hold_shift_contents"));
                    }
                }
            } else if (stack.getItem() instanceof BuildersPouchItem) {
                if (!Services.PLATFORM.hasShiftDown()) {
                    tooltips.add(Services.PLATFORM.translatable("tooltip.buildscape.hold_shift_contents"));
                }
            }
        } catch (Throwable t) {
            BuildscapeCommon.logError("TagTooltipHandler: Suppressed tooltip error", t);
        }
    }

    @Nullable
    public static ShulkerBoxTooltipData getShulkerTooltipData(ItemStack stack) {
        try {
            if (stack == null || stack.isEmpty()) return null;
            if (stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock sbb) {
                CompoundTag tag = Services.PLATFORM.getCustomData(stack, false);
                CompoundTag blockEntityTag = tag != null ? Services.PLATFORM.getTagCompound(tag, "BlockEntityTag") : null;
                if (blockEntityTag != null && (blockEntityTag.contains("GhostFilters") || blockEntityTag.contains("Items"))) {
                    NonNullList<ItemStack> filterStacks = NonNullList.withSize(27, ItemStack.EMPTY);
                    NonNullList<ItemStack> realStacks = NonNullList.withSize(27, ItemStack.EMPTY);
                    boolean hasAnyData = false;

                    List<String> ghostFilters = Services.PLATFORM.getTagStringList(blockEntityTag, "GhostFilters");
                    if (ghostFilters != null && !ghostFilters.isEmpty()) {
                        for (int i = 0; i < 27 && i < ghostFilters.size(); i++) {
                            String id = ghostFilters.get(i);
                            CommonId cid = CommonId.tryParse(id);
                            if (cid != null) {
                                Item item = Services.PLATFORM.getItem(cid);
                                if (item != null && item != Items.AIR) {
                                    ItemStack ghostStack = new ItemStack(item);
                                    Services.PLATFORM.updateCustomData(ghostStack, t -> t.putBoolean("ghost", true));
                                    filterStacks.set(i, ghostStack);
                                    hasAnyData = true;
                                }
                            }
                        }
                    }

                    if (blockEntityTag.contains("Items")) {
                        Services.PLATFORM.loadAllItems(blockEntityTag, realStacks);
                        for (ItemStack item : realStacks) {
                            if (!item.isEmpty()) {
                                hasAnyData = true;
                                break;
                            }
                        }
                    }

                    if (hasAnyData) {
                        return new ShulkerBoxTooltipData(filterStacks, realStacks, sbb.getColor());
                    }
                }
            }
        } catch (Throwable t) {
            BuildscapeCommon.logError("TagTooltipHandler: Error reading shulker tooltip data", t);
        }
        return null;
    }

    @Nullable
    public static BuildersPouchTooltipData getBuildersPouchTooltipData(ItemStack stack) {
        try {
            if (stack == null || stack.isEmpty()) return null;
            if (stack.getItem() instanceof BuildersPouchItem) {
                NonNullList<ItemStack> filterStacks = NonNullList.withSize(BuildersPouchItem.SLOT_COUNT, ItemStack.EMPTY);
                NonNullList<ItemStack> realStacks = NonNullList.withSize(BuildersPouchItem.SLOT_COUNT, ItemStack.EMPTY);

                List<String> filters = BuildersPouchItem.getFilters(stack);
                for (int i = 0; i < BuildersPouchItem.SLOT_COUNT && i < filters.size(); i++) {
                    String id = filters.get(i);
                    CommonId cid = CommonId.tryParse(id);
                    if (cid != null) {
                        Item item = Services.PLATFORM.getItem(cid);
                        if (item != null && item != Items.AIR) {
                            ItemStack ghostStack = new ItemStack(item);
                            Services.PLATFORM.updateCustomData(ghostStack, t -> t.putBoolean("ghost", true));
                            filterStacks.set(i, ghostStack);
                        }
                    }
                }

                CompoundTag data = BuildersPouchItem.getData(stack, false);
                if (data != null && data.contains("Items")) {
                    Services.PLATFORM.loadAllItems(data, realStacks);
                }

                return new BuildersPouchTooltipData(filterStacks, realStacks);
            }
        } catch (Throwable t) {
            BuildscapeCommon.logError("TagTooltipHandler: Error reading builder pouch tooltip data", t);
        }
        return null;
    }
}
