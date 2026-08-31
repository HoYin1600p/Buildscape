package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.client.tooltip.ShulkerBoxTooltipData;
import com.kingodogo.buildscape.item.FestiveStockingItem;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

@Mod.EventBusSubscriber(
        modid = BuildScape.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class TagTooltipHandler {

    private static int getShulkerBoxColor(ShulkerBoxBlock block) {
        DyeColor dye = block.getColor();
        if (dye == null) return 0x975DA8;
        return switch (dye) {
            case WHITE -> 0xE4E9ED;
            case ORANGE -> 0xF07613;
            case MAGENTA -> 0xBD3BBE;
            case LIGHT_BLUE -> 0x3AAFD9;
            case YELLOW -> 0xF8C527;
            case LIME -> 0x70B919;
            case PINK -> 0xED8DAC;
            case GRAY -> 0x3E4447;
            case LIGHT_GRAY -> 0x8E8E86;
            case CYAN -> 0x158991;
            case PURPLE -> 0x792AAC;
            case BLUE -> 0x35399D;
            case BROWN -> 0x724728;
            case GREEN -> 0x546E1A;
            case RED -> 0xAC2020;
            case BLACK -> 0x1D1D21;
        };
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

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
            }
        }

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
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();
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
                        if (!id.isEmpty() && net.minecraft.resources.ResourceLocation.isValidResourceLocation(id)) {
                            Item item = ForgeRegistries.ITEMS.getValue(new net.minecraft.resources.ResourceLocation(id));
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
                    int boxColor = getShulkerBoxColor(sbb);
                    event.getTooltipElements().add(Either.right(
                            new ShulkerBoxTooltipData(filterStacks, realStacks, boxColor)
                    ));
                }
            }
        }
    }
}
