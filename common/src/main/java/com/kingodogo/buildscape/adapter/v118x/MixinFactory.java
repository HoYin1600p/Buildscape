package com.kingodogo.buildscape.adapter.v118x;

import com.kingodogo.buildscape.client.renderer.FestiveGlintHandler;
import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;
import com.kingodogo.buildscape.config.BuildscapeClientConfig;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameAttachment;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameType;
import com.kingodogo.buildscape.mixinsupport.IMixinFactory;
import com.kingodogo.buildscape.mixin.StonecutterMenuLegacyAccessor;
import com.kingodogo.buildscape.util.BeaconBeamScanState;
import com.kingodogo.buildscape.util.BeaconScanContext;
import com.kingodogo.buildscape.util.GhostFilterMenu;
import com.kingodogo.buildscape.util.StonecutterMenuExtension;
import com.kingodogo.buildscape.stat.ModStats;
import com.kingodogo.buildscape.world.ModGameRules;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

public class MixinFactory implements IMixinFactory {

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleLeafDecayTick(LeavesBlock block, BlockState state, ServerLevel level, BlockPos pos, Object random) {
        if (ModGameRules.FAST_LEAF_DECAY instanceof GameRules.Key<?> key) {
            GameRules.Key<GameRules.BooleanValue> boolKey = (GameRules.Key<GameRules.BooleanValue>) key;
            if (level.getGameRules().getBoolean(boolKey)) {
                BlockState currentState = level.getBlockState(pos);
                if (currentState.getBlock() instanceof LeavesBlock) {
                    BooleanProperty persistent = LeavesBlock.PERSISTENT;
                    if (currentState.hasProperty(persistent) && !currentState.getValue(persistent)) {
                        block.randomTick(currentState, level, pos, (Random) random);
                    }
                }
            }
        }
    }

    @Override
    public boolean isVanillaWaterloggedLeaves() {
        return false;
    }

    @Override
    public int getItemRawId(Item item) {
        return Registry.ITEM.getId(item);
    }

    @Override
    public Item getItemByRawId(int id) {
        return Registry.ITEM.byId(id);
    }

    @Override
    public void handleStonecutterCutAll(StonecutterMenu menu, Player player) {
        ItemStack inputStack = menu.getSlot(0).getItem();
        if (inputStack.isEmpty()) return;

        int recipeIndex = menu.getSelectedRecipeIndex();
        if (recipeIndex < 0 || recipeIndex >= menu.getRecipes().size()) return;

        StonecutterRecipe recipe = menu.getRecipes().get(recipeIndex);
        ItemStack resultPrototype = recipe.assemble(menu.container);
        if (resultPrototype.isEmpty()) return;

        Item inputItem = inputStack.getItem();
        int totalInputCount = inputStack.getCount();
        List<Integer> playerInvSlots = new ArrayList<>();

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack invStack = player.getInventory().getItem(i);
            if (!invStack.isEmpty() && invStack.getItem() == inputItem && ItemStack.isSameItemSameTags(invStack, inputStack)) {
                totalInputCount += invStack.getCount();
                playerInvSlots.add(i);
            }
        }

        if (totalInputCount <= 0) return;

        int outputMultiplier = resultPrototype.getCount();
        int toConsume = totalInputCount;

        int fromInput = Math.min(toConsume, inputStack.getCount());
        inputStack.shrink(fromInput);
        menu.getSlot(0).set(inputStack);
        toConsume -= fromInput;

        for (int slotIdx : playerInvSlots) {
            if (toConsume <= 0) break;
            ItemStack invStack = player.getInventory().getItem(slotIdx);
            int fromInv = Math.min(toConsume, invStack.getCount());
            invStack.shrink(fromInv);
            player.getInventory().setItem(slotIdx, invStack);
            toConsume -= fromInv;
        }

        int totalConsumed = totalInputCount - toConsume;
        int totalOutput = totalConsumed * outputMultiplier;
        int maxStackSize = resultPrototype.getMaxStackSize();
        int remainingOutput = totalOutput;

        while (remainingOutput > 0) {
            int toGive = Math.min(remainingOutput, maxStackSize);
            ItemStack outputStack = resultPrototype.copy();
            outputStack.setCount(toGive);
            if (!player.getInventory().add(outputStack)) {
                player.drop(outputStack, false);
            }
            remainingOutput -= toGive;
        }

        player.awardRecipes(Collections.singleton(recipe));
        ((StonecutterMenuLegacyAccessor) menu).buildscape$setupRecipeList(menu.container, menu.getSlot(0).getItem());
        menu.broadcastChanges();
    }
    @Override
    public void renderSignFrame(SignBlockEntity blockEntity, float partialTicks, PoseStack poseStack, Object bufferSourceObj, int combinedLight, int combinedOverlay) {
        com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.renderSignFrame(blockEntity, partialTicks, poseStack, bufferSourceObj, combinedLight, combinedOverlay);
    }

    @Override
    public void addPauseScreenButton(PauseScreen screen, int width, int height, List<? extends GuiEventListener> children) {
        com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.addPauseScreenButton(screen, width, height, children);
    }

    @Override
    public void addStonecutterCutAllButton(StonecutterScreen screen, int x, int y, StonecutterMenu menu) {
        com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.addStonecutterCutAllButton(screen, x, y, menu);
    }

    @Override
    public void renderClippedBeaconBeam(BeaconBlockEntity blockEntity, float partialTicks, PoseStack poseStack, Object bufferSourceObj, int combinedLight, int combinedOverlay) {
        com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.renderClippedBeaconBeam(blockEntity, partialTicks, poseStack, bufferSourceObj, combinedLight, combinedOverlay);
    }

    @Override
    public void renderFilterPlaceholder(AbstractContainerMenu menu, Slot slot, PoseStack poseStack) {
        com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.renderFilterPlaceholder(menu, slot, poseStack);
    }

    @Override
    public void renderAnvilZeroCostLabel(Object anvilScreen, Object poseStack) {
        com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.renderAnvilZeroCostLabel(anvilScreen, poseStack);
    }


    @Override
    public void arrangeCreativeTabs(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (tab == CreativeModeTab.TAB_BUILDING_BLOCKS || tab == CreativeModeTab.TAB_DECORATIONS) {
            Map<ResourceLocation, ItemStack> stacksById = new HashMap<>();
            for (ItemStack stack : items) {
                ResourceLocation id = Registry.ITEM.getKey(stack.getItem());
                if (id != null) {
                    stacksById.put(id, stack);
                }
            }

            Map<ItemStack, ItemStack> insertAfter = new IdentityHashMap<>();
            Set<ItemStack> moved = Collections.newSetFromMap(new IdentityHashMap<>());
            for (Map.Entry<ResourceLocation, ItemStack> entry : stacksById.entrySet()) {
                ResourceLocation id = entry.getKey();
                String path = id.getPath();
                if (!"buildscape".equals(id.getNamespace()) || !path.endsWith("_vertical_slab")) {
                    continue;
                }

                String base = path.substring(0, path.length() - "_vertical_slab".length());
                ItemStack anchor = stacksById.get(new ResourceLocation("minecraft", base + "_slab"));
                if (anchor == null) {
                    anchor = stacksById.get(new ResourceLocation("minecraft", base));
                }
                if (anchor != null) {
                    insertAfter.put(anchor, entry.getValue());
                    moved.add(entry.getValue());
                }
            }

            NonNullList<ItemStack> ordered = NonNullList.create();
            for (ItemStack stack : items) {
                if (!moved.contains(stack)) {
                    ordered.add(stack);
                    ItemStack verticalSlab = insertAfter.get(stack);
                    if (verticalSlab != null) {
                        ordered.add(verticalSlab);
                    }
                }
            }

            items.clear();
            items.addAll(ordered);
        }
    }
    @Override
    public VertexConsumer getFestiveFoilBufferDirect(Object bufferSource, Object renderType, boolean noEntity) {
        return com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.getFestiveFoilBufferDirect(bufferSource, renderType, noEntity);
    }

    @Override
    public VertexConsumer getFestiveFoilBuffer(Object bufferSource, Object renderType, boolean isItem) {
        return com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.getFestiveFoilBuffer(bufferSource, renderType, isItem);
    }

    @Override
    public VertexConsumer getFestiveArmorFoilBuffer(Object bufferSource, Object renderType, boolean isItem) {
        return com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.getFestiveArmorFoilBuffer(bufferSource, renderType, isItem);
    }

    @Override
    public VertexConsumer getFestiveCompassFoilBuffer(Object bufferSource, Object renderType, PoseStack.Pose pose) {
        return com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.getFestiveCompassFoilBuffer(bufferSource, renderType, pose);
    }

    @Override
    public VertexConsumer getFestiveCompassFoilBufferDirect(Object bufferSource, Object renderType, PoseStack.Pose pose) {
        return com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.getFestiveCompassFoilBufferDirect(bufferSource, renderType, pose);
    }


    @Override
    public boolean isPlanterPlant(Block block) {
        return block instanceof net.minecraft.world.level.block.BushBlock
                || block instanceof net.minecraft.world.level.block.BambooBlock
                || block instanceof net.minecraft.world.level.block.BambooSaplingBlock
                || block instanceof net.minecraft.world.level.block.BigDripleafBlock
                || block instanceof net.minecraft.world.level.block.BigDripleafStemBlock;
    }

    @Override
    public void playComposterPlanterSound(net.minecraft.world.level.Level level, BlockPos pos, Block block) {
        net.minecraft.world.level.block.SoundType soundType = block.getSoundType(block.defaultBlockState());
        level.playSound(null, pos, soundType.getPlaceSound(), net.minecraft.sounds.SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
    }

    @Override
    public ItemStack applyFireworkShotYaw(ItemStack stack, float yaw) {
        ItemStack copy = stack.copy();
        net.minecraft.nbt.CompoundTag fireworks = copy.getOrCreateTagElement("Fireworks");
        fireworks.putFloat("ShotYaw", yaw);
        net.minecraft.nbt.ListTag explosions = fireworks.getList("Explosions", 10);
        for (int i = 0; i < explosions.size(); i++) {
            explosions.getCompound(i).putFloat("ShotYaw", yaw);
        }
        return copy;
    }
    @Override
    public Object wrapGhostBufferSource(Object bufferSource, ItemStack stack) {
        return com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.wrapGhostBufferSource(bufferSource, stack);
    }

    @Override
    public VertexConsumer wrapPipeSpillVertexConsumer(VertexConsumer original, Object level, BlockPos pos, BlockState state, net.minecraft.world.level.material.FluidState fluid) {
        return com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.wrapPipeSpillVertexConsumer(original, level, pos, state, fluid);
    }


    @Override
    public void preserveGhostFilters(ItemStack stack, net.minecraft.nbt.ListTag filterList) {
        stack.getOrCreateTagElement("BlockEntityTag").put("GhostFilters", filterList.copy());
    }

    @Override
    public ItemStack getThrownTridentItem(Object thrownTrident) {
        if (thrownTrident instanceof net.minecraft.world.entity.projectile.ThrownTrident trident) {
            try {
                for (java.lang.reflect.Field f : net.minecraft.world.entity.projectile.ThrownTrident.class.getDeclaredFields()) {
                    if (f.getType() == ItemStack.class) {
                        f.setAccessible(true);
                        ItemStack res = (ItemStack) f.get(trident);
                        if (res != null && !res.isEmpty()) return res;
                    }
                }
            } catch (Throwable ignored) {
            }
            return new ItemStack(net.minecraft.world.item.Items.TRIDENT);
        }
        return ItemStack.EMPTY;
    }
    @Override
    public boolean renderStatsEntry(Object statObj, PoseStack poseStack, int left, int top, int width) {
        return com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.renderStatsEntry(statObj, poseStack, left, top, width);
    }

    @Override
    public ItemStack cycleAdvancementIcon(Object node, Object displayObject) {
        return com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.cycleAdvancementIcon(node, displayObject);
    }

    @Override
    public void setScreen(Object screen) {
        com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.setScreen(screen);
    }

    @Override
    public void sortGeneralStatsList(Object listObj, java.util.Comparator<?> originalComparatorObj) {
        com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.sortGeneralStatsList(listObj, originalComparatorObj);
    }

    @Override
    public void renderCustomScreenTooltip(net.minecraft.client.gui.screens.Screen screen, PoseStack poseStack, List<?> components, int mouseX, int mouseY, ItemStack hoverStack, Font font, Object itemRendererObj, int screenWidth, int screenHeight) {
        com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.renderCustomScreenTooltip(screen, poseStack, components, mouseX, mouseY, hoverStack, font, itemRendererObj, screenWidth, screenHeight);
    }

    @Override
    public void onModelBakeryPreload(Object bakeryObj, Object resourceManagerObj) {
        com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.onModelBakeryPreload(bakeryObj, resourceManagerObj);
    }

    @Override
    public Object onModelBakeryLoadModel(Object locationObj) {
        return com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.onModelBakeryLoadModel(locationObj);
    }

    @Override
    public void onModelBakeryDetectCustomGeometry(Object locationObj, Object modelObj) {
        com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.onModelBakeryDetectCustomGeometry(locationObj, modelObj);
    }

    @Override
    public void onModelBakeryRelease() {
        com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.onModelBakeryRelease();
    }

    @Override
    public void onModelBakeryBakeParallel(Object bakeryObj) {
        com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.onModelBakeryBakeParallel(bakeryObj);
    }


    @Override
    public void migrateStoredGhostItems(Object tagObj) {
        if (!(tagObj instanceof net.minecraft.nbt.CompoundTag tag)) return;
        net.minecraft.nbt.ListTag filters = tag.contains("GhostFilters", 9)
                ? tag.getList("GhostFilters", 8).copy()
                : new net.minecraft.nbt.ListTag();
        while (filters.size() < 27) filters.add(net.minecraft.nbt.StringTag.valueOf(""));

        if (tag.contains("Items", 9)) {
            net.minecraft.nbt.ListTag items = tag.getList("Items", 10);
            for (int i = items.size() - 1; i >= 0; i--) {
                net.minecraft.nbt.CompoundTag item = items.getCompound(i);
                int slot = item.getByte("Slot") & 255;
                if (slot >= 27 || !item.contains("tag", 10)
                        || !item.getCompound("tag").getBoolean("ghost")) continue;

                if (filters.getString(slot).isEmpty()) {
                    filters.set(slot, net.minecraft.nbt.StringTag.valueOf(item.getString("id")));
                }
                items.remove(i);
            }
            tag.put("Items", items);
        }
        tag.put("GhostFilters", filters);
    }

    @Override
    public void readGhostFilters(Object tagObj, String[] filters) {
        for (int i = 0; i < 27; i++) filters[i] = "";
        if (!(tagObj instanceof net.minecraft.nbt.CompoundTag tag)) return;
        if (tag.contains("GhostFilters", 9)) {
            net.minecraft.nbt.ListTag list = tag.getList("GhostFilters", 8);
            for (int i = 0; i < 27; i++) {
                if (i < list.size()) {
                    String str = list.getString(i);
                    filters[i] = str != null ? str : "";
                }
            }
        }
    }

    @Override
    public void writeGhostFilters(Object tagObj, String[] filters) {
        if (!(tagObj instanceof net.minecraft.nbt.CompoundTag tag)) return;
        boolean hasFilter = false;
        net.minecraft.nbt.ListTag list = new net.minecraft.nbt.ListTag();
        for (int i = 0; i < 27; i++) {
            String filter = filters[i] != null ? filters[i] : "";
            if (!filter.isEmpty()) hasFilter = true;
            list.add(net.minecraft.nbt.StringTag.valueOf(filter));
        }
        if (hasFilter) {
            tag.put("GhostFilters", list);
        } else {
            tag.remove("GhostFilters");
        }
    }

    @Override
    public boolean appendFireworkCustomHoverText(Object tagObj, List<Component> tooltip) {
        if (!(tagObj instanceof net.minecraft.nbt.CompoundTag tag)) return false;
        byte type = tag.getByte("Type");
        if (com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry.isCustomShape(type)) {
            String shapeTranslationKey = switch (type) {
                case com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry.CAKE_ID -> "item.minecraft.firework_star.shape.cake";
                case com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry.CROWN_ID -> "item.minecraft.firework_star.shape.crown";
                case com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry.TROPHY_ID -> "item.minecraft.firework_star.shape.trophy";
                case com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry.CHRISTMAS_TREE_ID -> "item.minecraft.firework_star.shape.christmas_tree";
                case com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry.PRESENTS_ID -> "item.minecraft.firework_star.shape.presents";
                case com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry.CANDY_CANE_ID -> "item.minecraft.firework_star.shape.candy_cane";
                case com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry.PHOENIX_ID -> "item.minecraft.firework_star.shape.phoenix";
                case com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry.SNOWFLAKE_ID -> "item.minecraft.firework_star.shape.snowflake";
                default -> "item.minecraft.firework_star.shape.custom";
            };

            tooltip.add(com.kingodogo.buildscape.platform.Services.PLATFORM.translatable(shapeTranslationKey).withStyle(ChatFormatting.GRAY));

            int[] colors = tag.getIntArray("Colors");
            if (colors.length > 0) {
                tooltip.add(appendFireworkColors(com.kingodogo.buildscape.platform.Services.PLATFORM.literal("").withStyle(ChatFormatting.GRAY), colors));
            }

            int[] fadeColors = tag.getIntArray("FadeColors");
            if (fadeColors.length > 0) {
                tooltip.add(appendFireworkColors(com.kingodogo.buildscape.platform.Services.PLATFORM.translatable("item.minecraft.firework_star.fade_to").append(" ").withStyle(ChatFormatting.GRAY), fadeColors));
            }

            if (tag.getBoolean("Flicker")) {
                tooltip.add(com.kingodogo.buildscape.platform.Services.PLATFORM.translatable("item.minecraft.firework_star.flicker").withStyle(ChatFormatting.GRAY));
            }

            if (tag.getBoolean("Trail")) {
                tooltip.add(com.kingodogo.buildscape.platform.Services.PLATFORM.translatable("item.minecraft.firework_star.trail").withStyle(ChatFormatting.GRAY));
            }

            return true;
        }
        return false;
    }

    private static Component appendFireworkColors(net.minecraft.network.chat.MutableComponent component, int[] colors) {
        net.minecraft.network.chat.MutableComponent textcomponent = com.kingodogo.buildscape.platform.Services.PLATFORM.literal("");
        for (int i = 0; i < colors.length; ++i) {
            if (i > 0) {
                textcomponent.append(", ");
            }
            textcomponent.append(getFireworkColorName(colors[i]));
        }
        return component.copy().append(textcomponent);
    }

    private static Component getFireworkColorName(int color) {
        net.minecraft.world.item.DyeColor dyecolor = net.minecraft.world.item.DyeColor.byFireworkColor(color);
        if (dyecolor == null) {
            return com.kingodogo.buildscape.platform.Services.PLATFORM.translatable("item.minecraft.firework_star.custom_color");
        } else {
            return com.kingodogo.buildscape.platform.Services.PLATFORM.translatable("item.minecraft.firework_star." + dyecolor.getName());
        }
    }
    @Override
    public void handleFireworkStarterTick(Object starterObj) {
        com.kingodogo.buildscape.adapter.v118x.client.MixinClientHooks.INSTANCE.handleFireworkStarterTick(starterObj);
    }
}
