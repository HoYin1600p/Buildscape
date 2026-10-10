package com.kingodogo.buildscape.adapter.v26x;

import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;
import com.kingodogo.buildscape.config.BuildscapeClientConfig;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameAttachment;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameType;
import com.kingodogo.buildscape.mixinsupport.IMixinFactory;
import com.kingodogo.buildscape.mixin.StonecutterMenuAccessor;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.StonecutterMenuExtension;
import com.kingodogo.buildscape.world.ModGameRules;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
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

        ((StonecutterMenuAccessor) menu).buildscape$setupRecipeList(menu.getSlot(0).getItem());
        menu.broadcastChanges();
    }

    @Override public void renderSignFrame(SignBlockEntity blockEntity, float partialTicks, PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay) { com.kingodogo.buildscape.adapter.v26x.client.ClientMixinHooks.renderSignFrame(blockEntity, partialTicks, poseStack, bufferSource, combinedLight, combinedOverlay); }



    @Override public void addPauseScreenButton(net.minecraft.client.gui.screens.PauseScreen screen, int width, int height, List<? extends net.minecraft.client.gui.components.events.GuiEventListener> children) { com.kingodogo.buildscape.adapter.v26x.client.ClientMixinHooks.addPauseScreenButton(screen, width, height, children); }

    @Override public void addStonecutterCutAllButton(net.minecraft.client.gui.screens.inventory.StonecutterScreen screen, int x, int y, StonecutterMenu menu) { com.kingodogo.buildscape.adapter.v26x.client.ClientMixinHooks.addStonecutterCutAllButton(screen, x, y, menu); }

    @Override public void renderClippedBeaconBeam(BeaconBlockEntity blockEntity, float partialTicks, PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay) { com.kingodogo.buildscape.adapter.v26x.client.ClientMixinHooks.renderClippedBeaconBeam(blockEntity, partialTicks, poseStack, bufferSource, combinedLight, combinedOverlay); }

    @Override public void renderFilterPlaceholder(AbstractContainerMenu menu, Slot slot, PoseStack poseStack) { com.kingodogo.buildscape.adapter.v26x.client.ClientMixinHooks.renderFilterPlaceholder(menu, slot, poseStack); }

    @Override public void renderAnvilZeroCostLabel(Object anvilScreen, Object poseStack) { com.kingodogo.buildscape.adapter.v26x.client.ClientMixinHooks.renderAnvilZeroCostLabel(anvilScreen, poseStack); }

    @Override
    public void arrangeCreativeTabs(CreativeModeTab tab, NonNullList<ItemStack> items) {
        var key = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab);
        if (key == null || !key.getNamespace().equals("minecraft")
                || !(key.getPath().equals("building_blocks") || key.getPath().equals("functional_blocks"))) return;
        java.util.Map<net.minecraft.resources.Identifier, ItemStack> byId = new java.util.HashMap<>();
        for (ItemStack stack : items) byId.put(BuiltInRegistries.ITEM.getKey(stack.getItem()), stack);
        java.util.Map<ItemStack, ItemStack> insertAfter = new java.util.IdentityHashMap<>();
        Set<ItemStack> moved = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
        for (ItemStack stack : items) {
            var id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (!id.getNamespace().equals("buildscape") || !id.getPath().endsWith("_vertical_slab")) continue;
            String base = id.getPath().substring(0, id.getPath().length() - "_vertical_slab".length());
            ItemStack anchor = byId.get(net.minecraft.resources.Identifier.withDefaultNamespace(base + "_slab"));
            if (anchor == null) anchor = byId.get(net.minecraft.resources.Identifier.withDefaultNamespace(base));
            if (anchor != null) { insertAfter.put(anchor, stack); moved.add(stack); }
        }
        NonNullList<ItemStack> ordered = NonNullList.create();
        for (ItemStack stack : items) {
            if (moved.contains(stack)) continue;
            ordered.add(stack);
            ItemStack following = insertAfter.get(stack);
            if (following != null) ordered.add(following);
        }
        items.clear();
        items.addAll(ordered);
    }

    @Override public void renderFilterPlaceholder(AbstractContainerMenu menu, Slot slot, Object graphics) {
        com.kingodogo.buildscape.adapter.v26x.client.ClientMixinHooks.renderFilterPlaceholder(menu, slot, graphics);
    }

    @Override public VertexConsumer getFestiveFoilBufferDirect(Object bufferSource, Object renderType, boolean noEntity) { return com.kingodogo.buildscape.adapter.v26x.client.ClientMixinHooks.getFestiveFoilBufferDirect(bufferSource, renderType, noEntity); }

    @Override public VertexConsumer getFestiveFoilBuffer(Object bufferSource, Object renderType, boolean isItem) { return com.kingodogo.buildscape.adapter.v26x.client.ClientMixinHooks.getFestiveFoilBuffer(bufferSource, renderType, isItem); }

    @Override public VertexConsumer getFestiveArmorFoilBuffer(Object bufferSource, Object renderType, boolean isItem) { return com.kingodogo.buildscape.adapter.v26x.client.ClientMixinHooks.getFestiveArmorFoilBuffer(bufferSource, renderType, isItem); }

    @Override public VertexConsumer getFestiveCompassFoilBuffer(Object bufferSource, Object renderType, PoseStack.Pose pose) { return com.kingodogo.buildscape.adapter.v26x.client.ClientMixinHooks.getFestiveCompassFoilBuffer(bufferSource, renderType, pose); }

    @Override public VertexConsumer getFestiveCompassFoilBufferDirect(Object bufferSource, Object renderType, PoseStack.Pose pose) { return com.kingodogo.buildscape.adapter.v26x.client.ClientMixinHooks.getFestiveCompassFoilBufferDirect(bufferSource, renderType, pose); }

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

    @Override public void setScreen(Object screen) { com.kingodogo.buildscape.adapter.v26x.client.ClientMixinHooks.setScreen(screen); }

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

            tooltip.add(net.minecraft.network.chat.Component.translatable(shapeTranslationKey).withStyle(net.minecraft.ChatFormatting.GRAY));

            int[] colors = tag.getIntArray("Colors").orElse(new int[0]);
            if (colors.length > 0) {
                tooltip.add(appendFireworkColors(net.minecraft.network.chat.Component.literal("").withStyle(net.minecraft.ChatFormatting.GRAY), colors));
            }

            int[] fadeColors = tag.getIntArray("FadeColors").orElse(new int[0]);
            if (fadeColors.length > 0) {
                tooltip.add(appendFireworkColors(net.minecraft.network.chat.Component.translatable("item.minecraft.firework_star.fade_to").append(" ").withStyle(net.minecraft.ChatFormatting.GRAY), fadeColors));
            }

            if (tag.getBooleanOr("Flicker", false)) {
                tooltip.add(net.minecraft.network.chat.Component.translatable("item.minecraft.firework_star.flicker").withStyle(net.minecraft.ChatFormatting.GRAY));
            }

            if (tag.getBooleanOr("Trail", false)) {
                tooltip.add(net.minecraft.network.chat.Component.translatable("item.minecraft.firework_star.trail").withStyle(net.minecraft.ChatFormatting.GRAY));
            }

            return true;
        }
        return false;
    }

    private static net.minecraft.network.chat.Component appendFireworkColors(net.minecraft.network.chat.MutableComponent component, int[] colors) {
        net.minecraft.network.chat.MutableComponent textcomponent = net.minecraft.network.chat.Component.literal("");
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
            return net.minecraft.network.chat.Component.translatable("item.minecraft.firework_star.custom_color");
        } else {
            return net.minecraft.network.chat.Component.translatable("item.minecraft.firework_star." + dyecolor.getName());
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
