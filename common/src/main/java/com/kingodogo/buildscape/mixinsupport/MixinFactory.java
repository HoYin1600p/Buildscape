package com.kingodogo.buildscape.mixinsupport;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Consumer;

public final class MixinFactory {

    public static void renderFilterPlaceholder(AbstractContainerMenu menu, Slot slot, Object graphics) {
        INSTANCE.renderFilterPlaceholder(menu, slot, graphics);
    }

    private static final IMixinFactory INSTANCE = loadFactory();

    private MixinFactory() {}

    private static IMixinFactory loadFactory() {
        return ServiceLoader.load(IMixinFactory.class)
                .findFirst()
                .orElseGet(DefaultMixinFactory::new);
    }

    public static IMixinFactory get() {
        return INSTANCE;
    }

    public static boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return get().shouldApplyMixin(targetClassName, mixinClassName);
    }

    public static void handleLeafDecayTick(LeavesBlock block, BlockState state, ServerLevel level, BlockPos pos, Object random) {
        get().handleLeafDecayTick(block, state, level, pos, random);
    }

    public static boolean isVanillaWaterloggedLeaves() {
        return get().isVanillaWaterloggedLeaves();
    }

    public static int getItemRawId(Item item) {
        return get().getItemRawId(item);
    }

    public static Item getItemByRawId(int id) {
        return get().getItemByRawId(id);
    }

    public static void handleStonecutterCutAll(StonecutterMenu menu, Player player) {
        get().handleStonecutterCutAll(menu, player);
    }

    public static void renderSignFrame(SignBlockEntity blockEntity, float partialTicks, PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay) {
        get().renderSignFrame(blockEntity, partialTicks, poseStack, bufferSource, combinedLight, combinedOverlay);
    }

    public static void addPauseScreenButton(PauseScreen screen, int width, int height, List<? extends GuiEventListener> children) {
        get().addPauseScreenButton(screen, width, height, children);
    }

    public static void addStonecutterCutAllButton(StonecutterScreen screen, int x, int y, StonecutterMenu menu) {
        get().addStonecutterCutAllButton(screen, x, y, menu);
    }

    public static void renderClippedBeaconBeam(BeaconBlockEntity blockEntity, float partialTicks, PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay) {
        get().renderClippedBeaconBeam(blockEntity, partialTicks, poseStack, bufferSource, combinedLight, combinedOverlay);
    }

    public static void renderFilterPlaceholder(AbstractContainerMenu menu, Slot slot, PoseStack poseStack) {
        get().renderFilterPlaceholder(menu, slot, poseStack);
    }

    public static void renderAnvilZeroCostLabel(Object anvilScreen, Object poseStack) {
        get().renderAnvilZeroCostLabel(anvilScreen, poseStack);
    }

    public static void arrangeCreativeTabs(CreativeModeTab tab, NonNullList<ItemStack> items) {
        get().arrangeCreativeTabs(tab, items);
    }

    public static VertexConsumer getFestiveFoilBufferDirect(Object bufferSource, Object renderType, boolean noEntity) {
        return get().getFestiveFoilBufferDirect(bufferSource, renderType, noEntity);
    }

    public static VertexConsumer getFestiveFoilBuffer(Object bufferSource, Object renderType, boolean isItem) {
        return get().getFestiveFoilBuffer(bufferSource, renderType, isItem);
    }

    public static VertexConsumer getFestiveArmorFoilBuffer(Object bufferSource, Object renderType, boolean isItem) {
        return get().getFestiveArmorFoilBuffer(bufferSource, renderType, isItem);
    }

    public static VertexConsumer getFestiveCompassFoilBuffer(Object bufferSource, Object renderType, PoseStack.Pose pose) {
        return get().getFestiveCompassFoilBuffer(bufferSource, renderType, pose);
    }

    public static VertexConsumer getFestiveCompassFoilBufferDirect(Object bufferSource, Object renderType, PoseStack.Pose pose) {
        return get().getFestiveCompassFoilBufferDirect(bufferSource, renderType, pose);
    }

    public static void renderCustomScreenTooltip(net.minecraft.client.gui.screens.Screen screen, PoseStack poseStack, java.util.List<?> components, int mouseX, int mouseY, ItemStack hoverStack, net.minecraft.client.gui.Font font, Object itemRenderer, int screenWidth, int screenHeight) {
        get().renderCustomScreenTooltip(screen, poseStack, components, mouseX, mouseY, hoverStack, font, itemRenderer, screenWidth, screenHeight);
    }

    public static void registerFixedRenderBuffers(Object renderBuffers) {
        get().registerFixedRenderBuffers(renderBuffers);
    }

    public static ItemStack cycleAdvancementIcon(Object widget, Object displayInfo) {
        return get().cycleAdvancementIcon(widget, displayInfo);
    }

    public static boolean renderStatsEntry(Object stat, PoseStack poseStack, int left, int top, int width) {
        return get().renderStatsEntry(stat, poseStack, left, top, width);
    }

    public static void setScreen(Object screen) {
        get().setScreen(screen);
    }

    public static void sortGeneralStatsList(Object list, java.util.Comparator<?> originalComparator) {
        get().sortGeneralStatsList(list, originalComparator);
    }

    public static boolean isPlanterPlant(Block block) {
        return get().isPlanterPlant(block);
    }

    public static void playComposterPlanterSound(Level level, BlockPos pos, Block block) {
        get().playComposterPlanterSound(level, pos, block);
    }

    public static ItemStack applyFireworkShotYaw(ItemStack stack, float yaw) {
        return get().applyFireworkShotYaw(stack, yaw);
    }

    public static Object wrapGhostBufferSource(Object bufferSource, ItemStack stack) {
        return get().wrapGhostBufferSource(bufferSource, stack);
    }

    public static VertexConsumer wrapPipeSpillVertexConsumer(VertexConsumer original, Object level, BlockPos pos, BlockState state, FluidState fluid) {
        return get().wrapPipeSpillVertexConsumer(original, level, pos, state, fluid);
    }

    public static void preserveGhostFilters(ItemStack stack, ListTag filterList) {
        get().preserveGhostFilters(stack, filterList);
    }

    public static void onModelBakeryPreload(Object bakery, Object resourceManager) {
        get().onModelBakeryPreload(bakery, resourceManager);
    }

    public static Object onModelBakeryLoadModel(Object location) {
        return get().onModelBakeryLoadModel(location);
    }

    public static void onModelBakeryDetectCustomGeometry(Object location, Object model) {
        get().onModelBakeryDetectCustomGeometry(location, model);
    }

    public static void onModelBakeryRelease() {
        get().onModelBakeryRelease();
    }

    public static void onModelBakeryBakeParallel(Object bakery) {
        get().onModelBakeryBakeParallel(bakery);
    }

    public static void migrateStoredGhostItems(Object tag) {
        get().migrateStoredGhostItems(tag);
    }

    public static void readGhostFilters(Object tag, String[] filters) {
        get().readGhostFilters(tag, filters);
    }

    public static void writeGhostFilters(Object tag, String[] filters) {
        get().writeGhostFilters(tag, filters);
    }

    public static boolean appendFireworkCustomHoverText(Object tag, List<net.minecraft.network.chat.Component> tooltip) {
        return get().appendFireworkCustomHoverText(tag, tooltip);
    }

    public static void handleFireworkStarterTick(Object starter) {
        get().handleFireworkStarterTick(starter);
    }

    public static net.minecraft.world.item.ItemStack getThrownTridentItem(Object thrownTrident) {
        return get().getThrownTridentItem(thrownTrident);
    }

    private static final class DefaultMixinFactory implements IMixinFactory {
        @Override
        public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
            return true;
        }

        @Override
        public void handleLeafDecayTick(LeavesBlock block, BlockState state, ServerLevel level, BlockPos pos, Object random) {}

        @Override
        public boolean isVanillaWaterloggedLeaves() {
            return false;
        }

        @Override
        public int getItemRawId(Item item) {
            return 0;
        }

        @Override
        public Item getItemByRawId(int id) {
            return null;
        }

        @Override
        public void handleStonecutterCutAll(StonecutterMenu menu, Player player) {}

        @Override
        public void renderSignFrame(SignBlockEntity blockEntity, float partialTicks, PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay) {}

        @Override
        public void addPauseScreenButton(PauseScreen screen, int width, int height, List<? extends GuiEventListener> children) {}

        @Override
        public void addStonecutterCutAllButton(StonecutterScreen screen, int x, int y, StonecutterMenu menu) {}

        @Override
        public void renderClippedBeaconBeam(BeaconBlockEntity blockEntity, float partialTicks, PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay) {}

        @Override
        public void renderFilterPlaceholder(AbstractContainerMenu menu, Slot slot, PoseStack poseStack) {}

        @Override
        public void renderAnvilZeroCostLabel(Object anvilScreen, Object poseStack) {}

        @Override
        public void arrangeCreativeTabs(CreativeModeTab tab, NonNullList<ItemStack> items) {}

        @Override
        public VertexConsumer getFestiveFoilBufferDirect(Object bufferSource, Object renderType, boolean noEntity) {
            return null;
        }

        @Override
        public VertexConsumer getFestiveFoilBuffer(Object bufferSource, Object renderType, boolean isItem) {
            return null;
        }

        @Override
        public VertexConsumer getFestiveArmorFoilBuffer(Object bufferSource, Object renderType, boolean isItem) {
            return null;
        }

        @Override
        public VertexConsumer getFestiveCompassFoilBuffer(Object bufferSource, Object renderType, PoseStack.Pose pose) {
            return null;
        }

        @Override
        public VertexConsumer getFestiveCompassFoilBufferDirect(Object bufferSource, Object renderType, PoseStack.Pose pose) {
            return null;
        }
    }
}
