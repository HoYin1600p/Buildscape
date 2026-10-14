package com.kingodogo.buildscape.adapter.v121x;

import com.kingodogo.buildscape.mixinsupport.IMixinFactory;
import com.kingodogo.buildscape.mixin.StonecutterMenuAccessor;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.world.ModGameRules;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MixinFactory implements IMixinFactory {

    private static final Set<String> OBSOLETE_118_MIXINS = Set.of(
            "com.kingodogo.buildscape.mixin.BuildscapeBlockModelMixin",
            "com.kingodogo.buildscape.mixin.BuildscapeBlockStateCacheMixin",
            "com.kingodogo.buildscape.mixin.BuildscapeForgeRegistryMixin",
            "com.kingodogo.buildscape.mixin.BuildscapeModelBakeryMixin",
            "com.kingodogo.buildscape.mixin.RenderBuffersMixin"
    );

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return !OBSOLETE_118_MIXINS.contains(mixinClassName);
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
        List<RecipeHolder<StonecutterRecipe>> recipes = menu.getRecipes();
        if (recipeIndex < 0 || recipeIndex >= recipes.size()) return;

        StonecutterRecipe recipe = recipes.get(recipeIndex).value();
        SingleRecipeInput recipeInput = new SingleRecipeInput(inputStack);
        ItemStack resultPrototype = recipe.assemble(recipeInput, player.level().registryAccess());
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

        player.awardRecipes(Collections.singleton(recipes.get(recipeIndex)));
        ((StonecutterMenuAccessor) menu).buildscape$setupRecipeList(menu.getSlot(0).getItem());
        menu.broadcastChanges();
    }

    @Override
    public void renderSignFrame(SignBlockEntity blockEntity, float partialTicks, PoseStack poseStack, Object bufferSourceObj, int combinedLight, int combinedOverlay) {
        com.kingodogo.buildscape.adapter.v121x.client.ClientMixins.renderSignFrame(blockEntity, partialTicks, poseStack, bufferSourceObj, combinedLight, combinedOverlay);
    }

@Override
    public void addPauseScreenButton(PauseScreen screen, int width, int height, List<? extends GuiEventListener> children) {
        com.kingodogo.buildscape.adapter.v121x.client.ClientMixins.addPauseScreenButton(screen, width, height, children);
    }

    @Override
    public void addStonecutterCutAllButton(StonecutterScreen screen, int x, int y, StonecutterMenu menu) {
        com.kingodogo.buildscape.adapter.v121x.client.ClientMixins.addStonecutterCutAllButton(screen, x, y, menu);
    }

    @Override
    public void renderClippedBeaconBeam(BeaconBlockEntity blockEntity, float partialTicks, PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay) {
        com.kingodogo.buildscape.adapter.v121x.client.ClientMixins.renderClippedBeaconBeam(blockEntity, partialTicks, poseStack, bufferSource, combinedLight, combinedOverlay);
    }

    @Override
    public void renderFilterPlaceholder(AbstractContainerMenu menu, Slot slot, PoseStack poseStack) {
    }

    @Override
    public void renderAnvilZeroCostLabel(Object anvilScreen, Object poseStack) {
        com.kingodogo.buildscape.adapter.v121x.client.ClientMixins.renderAnvilZeroCostLabel(anvilScreen, poseStack);
    }

    @Override
    public ItemStack cycleAdvancementIcon(Object node, Object displayObject) {
        return com.kingodogo.buildscape.adapter.v121x.client.ClientMixins.cycleAdvancementIcon(node, displayObject);
    }

    @Override
    public void sortGeneralStatsList(Object list, java.util.Comparator<?> originalComparator) {
        com.kingodogo.buildscape.adapter.v121x.client.ClientMixins.sortGeneralStatsList(list, originalComparator);
    }

    @Override
    public boolean renderStatsEntry(Object stat, Object graphics, int left, int top, int width) {
        return com.kingodogo.buildscape.adapter.v121x.client.ClientMixins.renderStatsEntry(stat, graphics, left, top, width);
    }

    @Override
    public List<Component> prepareCustomTooltipText(ItemStack stack, List<Component> text) {
        return com.kingodogo.buildscape.adapter.v121x.client.ClientMixins.prepareCustomTooltipText(stack, text);
    }

    @Override
    public void renderCustomScreenTooltip(Object graphics, List<?> components, int mouseX, int mouseY, ItemStack hoverStack, net.minecraft.client.gui.Font font) {
        com.kingodogo.buildscape.adapter.v121x.client.ClientMixins.renderCustomScreenTooltip(graphics, components, mouseX, mouseY, font);
    }

    @Override
    public void arrangeCreativeTabs(CreativeModeTab tab, NonNullList<ItemStack> items) {
        var key = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab);
        if (key == null || !key.getNamespace().equals("minecraft")
                || !(key.getPath().equals("building_blocks") || key.getPath().equals("colored_blocks")
                || key.getPath().equals("functional_blocks"))) return;
        java.util.Map<net.minecraft.resources.ResourceLocation, ItemStack> byId = new java.util.HashMap<>();
        for (ItemStack stack : items) byId.put(BuiltInRegistries.ITEM.getKey(stack.getItem()), stack);
        // On 1.18.2 the vertical slabs of vanilla materials live in the vanilla tabs; here only the
        // Buildscape tab holds them, so they are added to the vanilla tab that holds their anchor.
        for (Item item : BuiltInRegistries.ITEM) {
            var id = BuiltInRegistries.ITEM.getKey(item);
            if (!id.getNamespace().equals("buildscape") || !id.getPath().endsWith("_vertical_slab") || byId.containsKey(id)) continue;
            if (vanillaAnchor(byId, id) != null) byId.put(id, new ItemStack(item));
        }
        java.util.Map<ItemStack, ItemStack> insertAfter = new java.util.IdentityHashMap<>();
        Set<ItemStack> moved = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
        for (java.util.Map.Entry<net.minecraft.resources.ResourceLocation, ItemStack> entry : byId.entrySet()) {
            var id = entry.getKey();
            if (!id.getNamespace().equals("buildscape") || !id.getPath().endsWith("_vertical_slab")) continue;
            ItemStack anchor = vanillaAnchor(byId, id);
            if (anchor != null) {
                insertAfter.put(anchor, entry.getValue());
                moved.add(entry.getValue());
            }
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

    private static ItemStack vanillaAnchor(java.util.Map<net.minecraft.resources.ResourceLocation, ItemStack> byId, net.minecraft.resources.ResourceLocation id) {
        String base = id.getPath().substring(0, id.getPath().length() - "_vertical_slab".length());
        ItemStack anchor = byId.get(net.minecraft.resources.ResourceLocation.withDefaultNamespace(base + "_slab"));
        return anchor != null ? anchor : byId.get(net.minecraft.resources.ResourceLocation.withDefaultNamespace(base));
    }

    @Override
    public VertexConsumer getFestiveFoilBufferDirect(Object bufferSource, Object renderType, boolean noEntity) {
        return com.kingodogo.buildscape.adapter.v121x.client.ClientMixins.getFestiveFoilBufferDirect(bufferSource, renderType, noEntity);
    }

    @Override
    public VertexConsumer getFestiveFoilBuffer(Object bufferSource, Object renderType, boolean isItem) {
        return com.kingodogo.buildscape.adapter.v121x.client.ClientMixins.getFestiveFoilBuffer(bufferSource, renderType, isItem);
    }

    @Override
    public VertexConsumer getFestiveArmorFoilBuffer(Object bufferSource, Object renderType, boolean isItem) {
        return com.kingodogo.buildscape.adapter.v121x.client.ClientMixins.getFestiveArmorFoilBuffer(bufferSource, renderType, isItem);
    }

    @Override
    public VertexConsumer getFestiveCompassFoilBuffer(Object bufferSource, Object renderType, PoseStack.Pose pose) {
        return com.kingodogo.buildscape.adapter.v121x.client.ClientMixins.getFestiveCompassFoilBuffer(bufferSource, renderType, pose);
    }

    @Override
    public VertexConsumer getFestiveCompassFoilBufferDirect(Object bufferSource, Object renderType, PoseStack.Pose pose) {
        return com.kingodogo.buildscape.adapter.v121x.client.ClientMixins.getFestiveCompassFoilBufferDirect(bufferSource, renderType, pose);
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
            net.minecraft.nbt.CompoundTag fireworks = tag.getCompound("Fireworks");
            fireworks.putFloat("ShotYaw", yaw);
            net.minecraft.nbt.ListTag explosions = fireworks.getList("Explosions", net.minecraft.nbt.Tag.TAG_COMPOUND);
            for (int i = 0; i < explosions.size(); i++) {
                explosions.getCompound(i).putFloat("ShotYaw", yaw);
            }
            tag.put("Fireworks", fireworks);
        });
        return copy;
    }

    @Override
    public void preserveGhostFilters(ItemStack stack, net.minecraft.nbt.ListTag filterList) {
        net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA, stack, tag -> {
            if (!tag.contains("id", net.minecraft.nbt.Tag.TAG_STRING)) {
                tag.putString("id", "minecraft:shulker_box");
            }
            tag.put("GhostFilters", filterList.copy());
        });
    }

    @Override
    public void setScreen(Object screen) {
        com.kingodogo.buildscape.adapter.v121x.client.ClientMixins.setScreen(screen);
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
    public boolean appendFireworkCustomHoverText(Object tagObj, List<net.minecraft.network.chat.Component> tooltip) {
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

            tooltip.add(com.kingodogo.buildscape.platform.Services.PLATFORM.translatable(shapeTranslationKey).withStyle(net.minecraft.ChatFormatting.GRAY));

            int[] colors = tag.getIntArray("Colors");
            if (colors.length > 0) {
                tooltip.add(appendFireworkColors(com.kingodogo.buildscape.platform.Services.PLATFORM.literal("").withStyle(net.minecraft.ChatFormatting.GRAY), colors));
            }

            int[] fadeColors = tag.getIntArray("FadeColors");
            if (fadeColors.length > 0) {
                tooltip.add(appendFireworkColors(com.kingodogo.buildscape.platform.Services.PLATFORM.translatable("item.minecraft.firework_star.fade_to").append(" ").withStyle(net.minecraft.ChatFormatting.GRAY), fadeColors));
            }

            if (tag.getBoolean("Flicker")) {
                tooltip.add(com.kingodogo.buildscape.platform.Services.PLATFORM.translatable("item.minecraft.firework_star.flicker").withStyle(net.minecraft.ChatFormatting.GRAY));
            }

            if (tag.getBoolean("Trail")) {
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
        if (thrownTrident instanceof net.minecraft.world.entity.projectile.ThrownTrident trident) {
            return trident.getWeaponItem();
        }
        return ItemStack.EMPTY;
    }
}
