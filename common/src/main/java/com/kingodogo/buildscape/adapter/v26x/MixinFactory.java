package com.kingodogo.buildscape.adapter.v26x;

import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;
import com.kingodogo.buildscape.config.BuildscapeClientConfig;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameAttachment;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameType;
import com.kingodogo.buildscape.mixin.IMixinFactory;
import com.kingodogo.buildscape.mixin.StonecutterMenuAccessor;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.StonecutterMenuExtension;
import com.kingodogo.buildscape.world.ModGameRules;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gamerules.GameRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class MixinFactory implements IMixinFactory {

    private static final Set<String> OBSOLETE_118_MIXINS = Set.of(
            "com.kingodogo.buildscape.mixin.BuildscapeBlockModelMixin",
            "com.kingodogo.buildscape.mixin.BuildscapeBlockStateCacheMixin",
            "com.kingodogo.buildscape.mixin.BuildscapeForgeRegistryMixin",
            "com.kingodogo.buildscape.mixin.BuildscapeModelBakeryMixin",
            "com.kingodogo.buildscape.mixin.CreativeModeTabMixin",
            "com.kingodogo.buildscape.mixin.RenderBuffersMixin",
            "com.kingodogo.buildscape.mixin.ScreenMixin",
            "com.kingodogo.buildscape.mixin.AdvancementWidgetMixin",
            "com.kingodogo.buildscape.mixin.GeneralStatisticsListMixin",
            "com.kingodogo.buildscape.mixin.GeneralStatisticsListEntryMixin"
    );

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return !OBSOLETE_118_MIXINS.contains(mixinClassName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleLeafDecayTick(LeavesBlock block, BlockState state, ServerLevel level, BlockPos pos, Object random) {
        if (ModGameRules.FAST_LEAF_DECAY instanceof GameRule<?> rule) {
            GameRule<Boolean> boolRule = (GameRule<Boolean>) rule;
            if (level.getGameRules().get(boolRule)) {
                BlockState currentState = level.getBlockState(pos);
                if (currentState.getBlock() instanceof LeavesBlock) {
                    BooleanProperty persistent = LeavesBlock.PERSISTENT;
                    if (currentState.hasProperty(persistent) && !currentState.getValue(persistent)) {
                        currentState.randomTick(level, pos, (RandomSource) random);
                    }
                }
            }
        }
    }

    @Override
    public boolean isVanillaWaterloggedLeaves() {
        return true;
    }

    @Override
    public int getItemRawId(Item item) {
        return BuiltInRegistries.ITEM.getId(item);
    }

    @Override
    public Item getItemByRawId(int id) {
        return BuiltInRegistries.ITEM.byId(id);
    }

    @Override
    public void handleStonecutterCutAll(StonecutterMenu menu, Player player) {
        ItemStack inputStack = menu.getSlot(0).getItem();
        if (inputStack.isEmpty()) return;

        int recipeIndex = menu.getSelectedRecipeIndex();
        SelectableRecipe.SingleInputSet<StonecutterRecipe> visibleRecipes = menu.getVisibleRecipes();
        List<SelectableRecipe.SingleInputEntry<StonecutterRecipe>> entries = visibleRecipes.entries();
        if (recipeIndex < 0 || recipeIndex >= entries.size()) return;

        Optional<RecipeHolder<StonecutterRecipe>> recipeHolder = entries.get(recipeIndex).recipe().recipe();
        if (recipeHolder.isEmpty()) return;

        StonecutterRecipe recipe = recipeHolder.get().value();
        SingleRecipeInput recipeInput = new SingleRecipeInput(inputStack);
        ItemStack resultPrototype = recipe.assemble(recipeInput);
        if (resultPrototype.isEmpty()) return;

        Item inputItem = inputStack.getItem();
        int totalInputCount = inputStack.getCount();
        List<Integer> playerInvSlots = new ArrayList<>();

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack invStack = player.getInventory().getItem(i);
            if (!invStack.isEmpty() && invStack.getItem() == inputItem && Services.PLATFORM.isSameItemSameComponents(invStack, inputStack)) {
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

        ((StonecutterMenuAccessor) menu).callSetupRecipeList(menu.container, menu.getSlot(0).getItem());
        menu.broadcastChanges();
    }

    @Override
    public void renderSignFrame(SignBlockEntity blockEntity, float partialTicks, PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay) {
    }

    private static void addWidgetToScreen(Screen screen, AbstractWidget widget) {
        try {
            for (java.lang.reflect.Method m : Screen.class.getDeclaredMethods()) {
                if (m.getParameterCount() == 1 && (m.getName().equals("addRenderableWidget") || m.getName().equals("m_142416_"))) {
                    m.setAccessible(true);
                    m.invoke(screen, widget);
                    return;
                }
            }
            for (java.lang.reflect.Field f : Screen.class.getDeclaredFields()) {
                if (java.util.List.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    java.util.List list = (java.util.List) f.get(screen);
                    if (list != null) list.add(widget);
                }
            }
        } catch (Throwable exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Failed to add Buildscape screen widget", exception);
        }
    }

    @Override
    public void addPauseScreenButton(PauseScreen screen, int width, int height, List<? extends GuiEventListener> children) {
        if (BuildscapeClientConfig.get().isConfigButtonHidden()) return;

        int targetX = width / 2 + 104;
        int targetY = height / 4 + 48;

        for (GuiEventListener listener : children) {
            if (listener instanceof AbstractWidget widget) {
                String text = widget.getMessage().getString();
                if (text.contains("Stats") || text.contains("Statistics")) {
                    targetX = widget.getX() + widget.getWidth() + 4;
                    targetY = widget.getY();
                    break;
                }
            }
        }

        Button button = Button.builder(Component.literal("BS"), b -> {
            Minecraft.getInstance().setScreenAndShow(Services.PLATFORM.createConfigScreen(screen));
        }).bounds(targetX, targetY, 20, 20).build();
        addWidgetToScreen(screen, button);
    }

    @Override
    public void addStonecutterCutAllButton(StonecutterScreen screen, int x, int y, StonecutterMenu menu) {
        Button button = Button.builder(Component.literal("All"), b -> {
            boolean active = !((StonecutterMenuExtension) menu).buildscape$isCutAll();
            ((StonecutterMenuExtension) menu).buildscape$setCutAll(active);
            Minecraft mc = Minecraft.getInstance();
            if (mc.gameMode != null) {
                mc.gameMode.handleInventoryButtonClick(menu.containerId, -123);
            }
        }).bounds(x, y, 18, 10).build();
        addWidgetToScreen(screen, button);
    }

    @Override
    public void renderClippedBeaconBeam(BeaconBlockEntity blockEntity, float partialTicks, PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay) {
    }

    @Override
    public void renderFilterPlaceholder(AbstractContainerMenu menu, Slot slot, PoseStack poseStack) {
    }

    @Override
    public void renderAnvilZeroCostLabel(Object anvilScreen, Object poseStack) {
    }

    @Override
    public void arrangeCreativeTabs(CreativeModeTab tab, NonNullList<ItemStack> items) {
    }

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

    @Override
    public boolean isPlanterPlant(Block block) {
        return block instanceof net.minecraft.world.level.block.BushBlock
                || block instanceof net.minecraft.world.level.block.BambooStalkBlock
                || block instanceof net.minecraft.world.level.block.BambooSaplingBlock
                || block instanceof net.minecraft.world.level.block.BigDripleafBlock
                || block instanceof net.minecraft.world.level.block.BigDripleafStemBlock;
    }

    @Override
    public void playComposterPlanterSound(net.minecraft.world.level.Level level, BlockPos pos, Block block) {
        net.minecraft.world.level.block.SoundType soundType = block.defaultBlockState().getSoundType();
        level.playSound(null, pos, soundType.getPlaceSound(), net.minecraft.sounds.SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
    }

    @Override
    public ItemStack applyFireworkShotYaw(ItemStack stack, float yaw) {
        ItemStack copy = stack.copy();
        net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, copy, tag -> {
            tag.putFloat("ShotYaw", yaw);
        });
        return copy;
    }

    @Override
    public void preserveGhostFilters(ItemStack stack, net.minecraft.nbt.ListTag filterList) {
        var existing = stack.get(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA);
        if (existing != null) {
            net.minecraft.nbt.CompoundTag tag = existing.copyTagWithoutId();
            tag.put("GhostFilters", filterList.copy());
            stack.set(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA, net.minecraft.world.item.component.TypedEntityData.of(existing.type(), tag));
        } else {
            net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
            tag.put("GhostFilters", filterList.copy());
            net.minecraft.world.level.block.entity.BlockEntityType<?> type = BuiltInRegistries.BLOCK_ENTITY_TYPE.getValue(net.minecraft.resources.Identifier.withDefaultNamespace("shulker_box"));
            stack.set(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA, net.minecraft.world.item.component.TypedEntityData.of(type, tag));
        }
    }

    @Override
    public void setScreen(Object screen) {
        if (Minecraft.getInstance() != null) {
            Minecraft.getInstance().setScreenAndShow((net.minecraft.client.gui.screens.Screen) screen);
        }
    }

    @Override
    public void migrateStoredGhostItems(Object inputOrTag) {
        if (inputOrTag instanceof net.minecraft.nbt.CompoundTag tag) {
            net.minecraft.nbt.ListTag filters = tag.contains("GhostFilters")
                    ? tag.getListOrEmpty("GhostFilters").copy()
                    : new net.minecraft.nbt.ListTag();
            while (filters.size() < 27) filters.add(net.minecraft.nbt.StringTag.valueOf(""));

            if (tag.contains("Items")) {
                net.minecraft.nbt.ListTag items = tag.getListOrEmpty("Items");
                for (int i = items.size() - 1; i >= 0; i--) {
                    if (items.get(i) instanceof net.minecraft.nbt.CompoundTag item) {
                        int slot = item.getByteOr("Slot", (byte) 0) & 255;
                        var tagOpt = item.getCompound("tag");
                        if (slot >= 27 || tagOpt.isEmpty() || !tagOpt.get().getBooleanOr("ghost", false)) continue;
                        if (filters.getString(slot).orElse("").isEmpty()) {
                            filters.set(slot, net.minecraft.nbt.StringTag.valueOf(item.getStringOr("id", "")));
                        }
                        items.remove(i);
                    }
                }
                tag.put("Items", items);
            }
            tag.put("GhostFilters", filters);
        }
    }

    @Override
    public void readGhostFilters(Object inputOrTag, String[] filters) {
        for (int i = 0; i < 27; i++) filters[i] = "";
        if (inputOrTag instanceof net.minecraft.world.level.storage.ValueInput input) {
            var list = input.listOrEmpty("GhostFilters", com.mojang.serialization.Codec.STRING);
            int idx = 0;
            for (String s : list) {
                if (idx < 27) filters[idx++] = s != null ? s : "";
            }
        } else if (inputOrTag instanceof net.minecraft.nbt.CompoundTag tag) {
            if (tag.contains("GhostFilters")) {
                net.minecraft.nbt.ListTag list = tag.getListOrEmpty("GhostFilters");
                for (int i = 0; i < 27; i++) {
                    if (i < list.size()) {
                        filters[i] = list.getString(i).orElse("");
                    }
                }
            }
        }
    }

    @Override
    public void writeGhostFilters(Object outputOrTag, String[] filters) {
        boolean hasFilter = false;
        for (int i = 0; i < 27; i++) {
            if (filters[i] != null && !filters[i].isEmpty()) {
                hasFilter = true;
                break;
            }
        }
        if (outputOrTag instanceof net.minecraft.world.level.storage.ValueOutput output) {
            if (hasFilter) {
                var list = output.list("GhostFilters", com.mojang.serialization.Codec.STRING);
                for (int i = 0; i < 27; i++) {
                    list.add(filters[i] != null ? filters[i] : "");
                }
            } else {
                output.discard("GhostFilters");
            }
        } else if (outputOrTag instanceof net.minecraft.nbt.CompoundTag tag) {
            if (hasFilter) {
                net.minecraft.nbt.ListTag list = new net.minecraft.nbt.ListTag();
                for (int i = 0; i < 27; i++) {
                    list.add(net.minecraft.nbt.StringTag.valueOf(filters[i] != null ? filters[i] : ""));
                }
                tag.put("GhostFilters", list);
            } else {
                tag.remove("GhostFilters");
            }
        }
    }

    @Override
    public boolean appendFireworkCustomHoverText(Object tagObj, List<net.minecraft.network.chat.Component> tooltip) {
        if (!(tagObj instanceof net.minecraft.nbt.CompoundTag tag)) return false;
        byte type = tag.getByteOr("Type", (byte) 0);
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

            tooltip.add(com.kingodogo.buildscape.platform.Services.PLATFORM.translatable(shapeTranslationKey).withStyle(net.minecraft.ChatFormatting.GRAY));

            int[] colors = tag.getIntArray("Colors").orElse(new int[0]);
            if (colors.length > 0) {
                tooltip.add(appendFireworkColors(com.kingodogo.buildscape.platform.Services.PLATFORM.literal("").withStyle(net.minecraft.ChatFormatting.GRAY), colors));
            }

            int[] fadeColors = tag.getIntArray("FadeColors").orElse(new int[0]);
            if (fadeColors.length > 0) {
                tooltip.add(appendFireworkColors(com.kingodogo.buildscape.platform.Services.PLATFORM.translatable("item.minecraft.firework_star.fade_to").append(" ").withStyle(net.minecraft.ChatFormatting.GRAY), fadeColors));
            }

            if (tag.getBooleanOr("Flicker", false)) {
                tooltip.add(com.kingodogo.buildscape.platform.Services.PLATFORM.translatable("item.minecraft.firework_star.flicker").withStyle(net.minecraft.ChatFormatting.GRAY));
            }

            if (tag.getBooleanOr("Trail", false)) {
                tooltip.add(com.kingodogo.buildscape.platform.Services.PLATFORM.translatable("item.minecraft.firework_star.trail").withStyle(net.minecraft.ChatFormatting.GRAY));
            }

            return true;
        }
        return false;
    }

    private static net.minecraft.network.chat.Component appendFireworkColors(net.minecraft.network.chat.MutableComponent component, int[] colors) {
        net.minecraft.network.chat.MutableComponent textcomponent = com.kingodogo.buildscape.platform.Services.PLATFORM.literal("");
        for (int i = 0; i < colors.length; ++i) {
            if (i > 0) {
                textcomponent.append(", ");
            }
            textcomponent.append(getFireworkColorName(colors[i]));
        }
        return component.copy().append(textcomponent);
    }

    private static net.minecraft.network.chat.Component getFireworkColorName(int color) {
        net.minecraft.world.item.DyeColor dyecolor = net.minecraft.world.item.DyeColor.byFireworkColor(color);
        if (dyecolor == null) {
            return com.kingodogo.buildscape.platform.Services.PLATFORM.translatable("item.minecraft.firework_star.custom_color");
        } else {
            return com.kingodogo.buildscape.platform.Services.PLATFORM.translatable("item.minecraft.firework_star." + dyecolor.getName());
        }
    }

    @Override
    public ItemStack getThrownTridentItem(Object thrownTrident) {
        if (thrownTrident instanceof net.minecraft.world.entity.projectile.arrow.ThrownTrident trident) {
            return trident.getWeaponItem();
        }
        return ItemStack.EMPTY;
    }
}
