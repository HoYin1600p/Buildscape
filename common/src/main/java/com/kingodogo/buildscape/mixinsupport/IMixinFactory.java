package com.kingodogo.buildscape.mixinsupport;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
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
import java.util.function.Consumer;
public interface IMixinFactory {

    boolean shouldApplyMixin(String targetClassName, String mixinClassName);

    void handleLeafDecayTick(LeavesBlock block, BlockState state, ServerLevel level, BlockPos pos, Object random);

    boolean isVanillaWaterloggedLeaves();

    int getItemRawId(Item item);

    Item getItemByRawId(int id);

    void handleStonecutterCutAll(StonecutterMenu menu, Player player);

    void renderSignFrame(SignBlockEntity blockEntity, float partialTicks, PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay);

    void addPauseScreenButton(PauseScreen screen, int width, int height, List<? extends GuiEventListener> children);

    void addStonecutterCutAllButton(StonecutterScreen screen, int x, int y, StonecutterMenu menu);

    void renderClippedBeaconBeam(BeaconBlockEntity blockEntity, float partialTicks, PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay);

    void renderFilterPlaceholder(AbstractContainerMenu menu, Slot slot, PoseStack poseStack);

    default void renderFilterPlaceholder(AbstractContainerMenu menu, Slot slot, Object graphics) {
        if (graphics instanceof PoseStack pose) renderFilterPlaceholder(menu, slot, pose);
    }

    void renderAnvilZeroCostLabel(Object anvilScreen, Object poseStack);

    void arrangeCreativeTabs(CreativeModeTab tab, NonNullList<ItemStack> items);

    VertexConsumer getFestiveFoilBufferDirect(Object bufferSource, Object renderType, boolean noEntity);

    VertexConsumer getFestiveFoilBuffer(Object bufferSource, Object renderType, boolean isItem);

    VertexConsumer getFestiveArmorFoilBuffer(Object bufferSource, Object renderType, boolean isItem);

    VertexConsumer getFestiveCompassFoilBuffer(Object bufferSource, Object renderType, PoseStack.Pose pose);

    VertexConsumer getFestiveCompassFoilBufferDirect(Object bufferSource, Object renderType, PoseStack.Pose pose);

    default void renderCustomScreenTooltip(Screen screen, PoseStack poseStack, List<?> components, int mouseX, int mouseY, ItemStack hoverStack, Font font, Object itemRenderer, int screenWidth, int screenHeight) {}

    default void registerFixedRenderBuffers(Object renderBuffers) {}

    default void renderCustomScreenTooltip(Object graphics, List<?> components, int mouseX, int mouseY, ItemStack hoverStack, Font font) {
        if (graphics instanceof PoseStack pose) renderCustomScreenTooltip(null, pose, components, mouseX, mouseY, hoverStack, font, null, 0, 0);
    }

    default List<net.minecraft.network.chat.Component> prepareCustomTooltipText(ItemStack stack, List<net.minecraft.network.chat.Component> text) {
        return text;
    }

    default Object createClientTooltipComponent(Object data) { return null; }

    default boolean renderStatsEntry(Object stat, Object graphics, int left, int top, int width) {
        return graphics instanceof PoseStack pose && renderStatsEntry(stat, pose, left, top, width);
    }

    default ItemStack cycleAdvancementIcon(Object widget, Object displayInfo) {
        return ItemStack.EMPTY;
    }

    default boolean renderStatsEntry(Object stat, PoseStack poseStack, int left, int top, int width) {
        return false;
    }

    default void setScreen(Object screen) {}

    default void sortGeneralStatsList(Object list, java.util.Comparator<?> originalComparator) {}

    default boolean isPlanterPlant(Block block) {
        return false;
    }

    default void playComposterPlanterSound(Level level, BlockPos pos, Block block) {}

    default ItemStack applyFireworkShotYaw(ItemStack stack, float yaw) {
        return stack;
    }

    default Object wrapGhostBufferSource(Object bufferSource, ItemStack stack) {
        return bufferSource;
    }

    default VertexConsumer wrapPipeSpillVertexConsumer(VertexConsumer original, Object level, BlockPos pos, BlockState state, FluidState fluid) {
        return original;
    }

    default void preserveGhostFilters(ItemStack stack, ListTag filterList) {}
    default void migrateStoredGhostItems(Object tag) {}
    default void readGhostFilters(Object tag, String[] filters) {}
    default void writeGhostFilters(Object tag, String[] filters) {}
    default boolean appendFireworkCustomHoverText(Object tag, List<net.minecraft.network.chat.Component> tooltip) { return false; }

    default net.minecraft.world.item.ItemStack getThrownTridentItem(Object thrownTrident) {
        return net.minecraft.world.item.ItemStack.EMPTY;
    }

    default void handleFireworkStarterTick(Object starter) {}

    default boolean renderCustomFireworkExplosion(Object starter, Object colors, Object fades, boolean trail, boolean flicker) {
        return false;
    }

    default void onModelBakeryPreload(Object bakery, Object resourceManager) {}
    default Object onModelBakeryLoadModel(Object location) { return null; }
    default void onModelBakeryDetectCustomGeometry(Object location, Object model) {}
    default void onModelBakeryRelease() {}
    default void onModelBakeryBakeParallel(Object bakery) {}
}
