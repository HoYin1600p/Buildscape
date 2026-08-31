package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.client.tooltip.BuildersPouchTooltipData;
import com.kingodogo.buildscape.client.tooltip.ShulkerBoxTooltipData;
import com.kingodogo.buildscape.config.CosmeticsConfig;
import com.kingodogo.buildscape.item.BuildersPouchItem;
import com.kingodogo.buildscape.item.FestiveStockingItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(
        modid = BuildScape.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class TagTooltipHandler {

    /**
     * Checks if the player has enabled Shulker Preview in Player Rules.
     */
    public static boolean isShulkerPreviewEnabled() {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                return CosmeticsConfig.get().getShulkerPreview(mc.player.getUUID());
            }
            return CosmeticsConfig.get().getShulkerPreview(null);
        } catch (Throwable t) {
            return true;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @OnlyIn(Dist.CLIENT)
    public static void onItemTooltip(ItemTooltipEvent event) {
        try {
            ItemStack stack = event.getItemStack();
            if (stack.isEmpty()) return;

            // Festive stocking tooltip handling
            if (stack.getItem() instanceof FestiveStockingItem) {
                CompoundTag tag = stack.getTag();
                if (tag != null && tag.contains("StoredItem", 10)) {
                    CompoundTag storedTag = tag.getCompound("StoredItem");
                    if (!storedTag.isEmpty()) {
                        ItemStack storedItem = ItemStack.of(storedTag);
                        if (!storedItem.isEmpty()) {
                            List<Component> tooltip = event.getToolTip();
                            tooltip.add(
                                    new TranslatableComponent(
                                            "tooltip.buildscape.festive_stocking.contains",
                                            storedItem.getCount(),
                                            storedItem.getDisplayName()
                                    )
                            );
                        }
                    }
                }
            }

            // If player disabled Shulker Preview in Player Rules, do not touch or modify tooltips at all.
            // This leaves vanilla, Shulker Plus (Iskallia), Tweakeroo, etc. completely untouched.
            if (!isShulkerPreviewEnabled()) {
                return;
            }

            if (stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock) {
                CompoundTag blockEntityTag = stack.getTagElement("BlockEntityTag");
                if (blockEntityTag != null && (blockEntityTag.contains("GhostFilters", 9) || blockEntityTag.contains("Items", 9))) {
                    List<Component> tooltips = event.getToolTip();
                    if (tooltips.size() > 1) {
                        tooltips.removeIf(comp -> {
                            String str = comp.getString();
                            return str.contains(" x") || str.matches(".*x\\d+.*") || str.startsWith("and ") || str.contains("more...");
                        });
                    }
                    if (!Screen.hasShiftDown()) {
                        tooltips.add(new TranslatableComponent("tooltip.buildscape.hold_shift_contents"));
                    }
                }
            } else if (stack.getItem() instanceof BuildersPouchItem) {
                if (!Screen.hasShiftDown()) {
                    event.getToolTip().add(new TranslatableComponent("tooltip.buildscape.hold_shift_contents"));
                }
            }
        } catch (Throwable t) {
            BuildScape.getLogger().debug("TagTooltipHandler: Suppressed tooltip error", t);
        }
    }

    @Nullable
    public static ShulkerBoxTooltipData getShulkerTooltipData(ItemStack stack) {
        try {
            if (stack == null || stack.isEmpty()) return null;
            if (stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock sbb) {
                CompoundTag blockEntityTag = stack.getTagElement("BlockEntityTag");
                if (blockEntityTag != null && (blockEntityTag.contains("GhostFilters", 9) || blockEntityTag.contains("Items", 9))) {
                    NonNullList<ItemStack> filterStacks = NonNullList.withSize(27, ItemStack.EMPTY);
                    NonNullList<ItemStack> realStacks = NonNullList.withSize(27, ItemStack.EMPTY);
                    boolean hasAnyData = false;

                    if (blockEntityTag.contains("GhostFilters", 9)) {
                        net.minecraft.nbt.ListTag list = blockEntityTag.getList("GhostFilters", 8);
                        for (int i = 0; i < 27 && i < list.size(); i++) {
                            String id = list.getString(i);
                            if (!id.isEmpty() && ResourceLocation.isValidResourceLocation(id)) {
                                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
                                if (item != null && item != Items.AIR) {
                                    ItemStack ghostStack = new ItemStack(item);
                                    ghostStack.getOrCreateTag().putBoolean("ghost", true);
                                    filterStacks.set(i, ghostStack);
                                    hasAnyData = true;
                                }
                            }
                        }
                    }

                    if (blockEntityTag.contains("Items", 9)) {
                        ContainerHelper.loadAllItems(blockEntityTag, realStacks);
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
            BuildScape.getLogger().debug("TagTooltipHandler: Error reading shulker tooltip data", t);
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
                    if (!id.isEmpty() && ResourceLocation.isValidResourceLocation(id)) {
                        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
                        if (item != null && item != Items.AIR) {
                            ItemStack ghostStack = new ItemStack(item);
                            ghostStack.getOrCreateTag().putBoolean("ghost", true);
                            filterStacks.set(i, ghostStack);
                        }
                    }
                }

                CompoundTag data = BuildersPouchItem.getData(stack, false);
                if (data != null && data.contains("Items", 9)) {
                    ContainerHelper.loadAllItems(data, realStacks);
                }

                return new BuildersPouchTooltipData(filterStacks, realStacks);
            }
        } catch (Throwable t) {
            BuildScape.getLogger().debug("TagTooltipHandler: Error reading builder pouch tooltip data", t);
        }
        return null;
    }
}
