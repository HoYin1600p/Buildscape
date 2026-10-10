package com.kingodogo.buildscape.adapter.v118x;

import com.kingodogo.buildscape.client.renderer.FestiveGlintHandler;
import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;
import com.kingodogo.buildscape.config.BuildscapeClientConfig;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameAttachment;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameType;
import com.kingodogo.buildscape.mixin.IMixinFactory;
import com.kingodogo.buildscape.mixin.StonecutterMenuAccessor;
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
        ((StonecutterMenuAccessor) menu).callSetupRecipeList(menu.container, menu.getSlot(0).getItem());
        menu.broadcastChanges();
    }

    @Override
    public void renderSignFrame(SignBlockEntity blockEntity, float partialTicks, PoseStack poseStack, Object bufferSourceObj, int combinedLight, int combinedOverlay) {
        if (blockEntity == null || blockEntity.getLevel() == null) return;
        MultiBufferSource bufferSource = (MultiBufferSource) bufferSourceObj;

        SignFrameType frameType = SignFrameAttachment.getFrame(blockEntity);
        if (frameType == SignFrameType.NONE || frameType.getModelLocation() == null) return;

        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        BakedModel frameModel = modelManager.getModel(new net.minecraft.client.resources.model.ModelResourceLocation(
                new net.minecraft.resources.ResourceLocation(frameType.getModelLocation().getNamespace(), frameType.getModelLocation().getPath()), ""
        ));
        if (frameModel == null || frameModel == modelManager.getMissingModel()) return;

        BlockState state = blockEntity.getBlockState();
        Block block = state.getBlock();

        poseStack.pushPose();

        if (block instanceof WallSignBlock) {
            Direction facing = state.getValue(WallSignBlock.FACING);
            float yRot = (facing.toYRot() + 180.0F) % 360.0F;
            poseStack.translate(0.5D, 0.5D, 0.5D);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(-yRot));
            poseStack.translate(-0.5D, -0.5D, -0.5D);
        } else if (block instanceof StandingSignBlock) {
            float rotation = (float) (state.getValue(StandingSignBlock.ROTATION) * 360) / 16.0F;
            float yRot = (rotation + 180.0F) % 360.0F;
            poseStack.translate(0.5D, 0.5D, 0.5D);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(-yRot));
            poseStack.translate(0.0D, 0.3125D, -0.4375D);
            poseStack.translate(-0.5D, -0.5D, -0.5D);
        } else {
            poseStack.popPose();
            return;
        }

        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        dispatcher.getModelRenderer().renderModel(
                poseStack.last(),
                bufferSource.getBuffer(RenderType.cutout()),
                state,
                frameModel,
                1.0F, 1.0F, 1.0F,
                combinedLight,
                combinedOverlay
        );

        poseStack.popPose();
    }

    private static void addWidgetToScreen(net.minecraft.client.gui.screens.Screen screen, AbstractWidget widget) {
        try {
            for (java.lang.reflect.Method m : net.minecraft.client.gui.screens.Screen.class.getDeclaredMethods()) {
                if (m.getParameterCount() == 1 && (m.getName().equals("addRenderableWidget") || m.getName().equals("m_142416_"))) {
                    m.setAccessible(true);
                    m.invoke(screen, widget);
                    return;
                }
            }
            for (java.lang.reflect.Field f : net.minecraft.client.gui.screens.Screen.class.getDeclaredFields()) {
                if (java.util.List.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    java.util.List list = (java.util.List) f.get(screen);
                    if (list != null) list.add(widget);
                }
            }
        } catch (Throwable ignored) {}
    }

    @Override
    public void addPauseScreenButton(PauseScreen screen, int width, int height, List<? extends GuiEventListener> children) {
        if (BuildscapeClientConfig.get().isConfigButtonHidden()) return;

        int targetX = width / 2 + 102;
        int targetY = height / 4 + 48;
        boolean isFullPauseMenu = false;
        AbstractWidget statsButton = null;

        for (GuiEventListener listener : children) {
            if (listener instanceof AbstractWidget widget) {
                Component msg = widget.getMessage();
                if (msg instanceof TranslatableComponent tc) {
                    String key = tc.getKey();
                    if (key.equals("menu.returnToGame")) {
                        isFullPauseMenu = true;
                    } else if (statsButton == null && (key.equals("menu.statistics") || key.equals("gui.stats"))) {
                        statsButton = widget;
                    }
                }
            }
        }

        if (!isFullPauseMenu) return;

        if (statsButton != null) {
            targetX = statsButton.x + statsButton.getWidth() + 4;
            targetY = statsButton.y;
        }

        Button configButton = new Button(
                targetX - 2,
                targetY,
                20,
                20,
                new TextComponent("B"),
                button -> Minecraft.getInstance().setScreen(com.kingodogo.buildscape.platform.Services.PLATFORM.createConfigScreen(screen))
        );
        addWidgetToScreen(screen, configButton);
    }

    @Override
    public void addStonecutterCutAllButton(StonecutterScreen screen, int x, int y, StonecutterMenu menu) {
        addWidgetToScreen(screen, new Button(x, y, 18, 10, TextComponent.EMPTY, button -> {
            boolean current = ((StonecutterMenuExtension) menu).buildscape$isCutAll();
            ((StonecutterMenuExtension) menu).buildscape$setCutAll(!current);
            Minecraft mc = Minecraft.getInstance();
            if (mc.gameMode != null) {
                mc.gameMode.handleInventoryButtonClick(menu.containerId, -123);
            }
        }) {
            @Override
            public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
                boolean active = ((StonecutterMenuExtension) menu).buildscape$isCutAll();
                int borderColor = 0xFF000000;
                int bgColor = active ? 0xFF107C41 : 0xFF4A4A4A;

                fill(poseStack, this.x, this.y, this.x + this.width, this.y + this.height, borderColor);
                fill(poseStack, this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, bgColor);

                int thumbX = active ? this.x + this.width - 7 : this.x + 1;
                int thumbColor = 0xFFFFFFFF;
                fill(poseStack, thumbX, this.y + 1, thumbX + 6, this.y + this.height - 1, thumbColor);

                if (this.isHovered) {
                    this.renderToolTip(poseStack, mouseX, mouseY);
                }
            }

            @Override
            public void renderToolTip(PoseStack poseStack, int mouseX, int mouseY) {
                boolean active = ((StonecutterMenuExtension) menu).buildscape$isCutAll();
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(new TranslatableComponent("tooltip.buildscape.stonecutter.title")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
                tooltip.add(new TranslatableComponent(active
                        ? "tooltip.buildscape.cut_all.on"
                        : "tooltip.buildscape.cut_stack.off").withStyle(ChatFormatting.BOLD));
                tooltip.add(new TranslatableComponent(active
                        ? "tooltip.buildscape.cut_all.desc"
                        : "tooltip.buildscape.cut_stack.desc").withStyle(ChatFormatting.GRAY));
                screen.renderComponentTooltip(poseStack, tooltip, mouseX, mouseY);
            }
        });
    }

    @Override
    public void renderClippedBeaconBeam(BeaconBlockEntity blockEntity, float partialTicks, PoseStack poseStack, Object bufferSourceObj, int combinedLight, int combinedOverlay) {
        int cutoff = BeaconScanContext.confirmedHeight(blockEntity.getLevel(), blockEntity.getBlockPos());
        if (cutoff >= BeaconBeamScanState.UNLIMITED) return;
        MultiBufferSource bufferSource = (MultiBufferSource) bufferSourceObj;

        long gameTime = blockEntity.getLevel().getGameTime();
        List<BeaconBlockEntity.BeaconBeamSection> sections = blockEntity.getBeamSections();
        int yOffset = 0;
        try {
            java.lang.reflect.Method targetMethod = null;
            for (java.lang.reflect.Method m : net.minecraft.client.renderer.blockentity.BeaconRenderer.class.getDeclaredMethods()) {
                if (m.getParameterCount() == 7 && m.getParameterTypes()[0] == PoseStack.class) {
                    m.setAccessible(true);
                    targetMethod = m;
                    break;
                }
            }
            if (targetMethod != null) {
                for (BeaconBlockEntity.BeaconBeamSection section : sections) {
                    int remaining = cutoff - yOffset;
                    if (remaining <= 0) break;

                    int sectionHeight = section.getHeight();
                    targetMethod.invoke(
                            null,
                            poseStack,
                            bufferSource,
                            partialTicks,
                            gameTime,
                            yOffset,
                            Math.min(sectionHeight, remaining),
                            section.getColor()
                    );
                    yOffset += sectionHeight;
                }
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void renderFilterPlaceholder(AbstractContainerMenu menu, Slot slot, PoseStack poseStack) {
        if (slot.hasItem() || !slot.isActive() || !(menu instanceof GhostFilterMenu filters)) return;
        Item filter = filters.buildscape$getFilterItem(slot.index);
        if (filter == null) return;

        ItemStack placeholder = new ItemStack(filter);
        placeholder.getOrCreateTag().putBoolean("ghost", true);
        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer renderer = minecraft.getItemRenderer();
        float previousBlitOffset = renderer.blitOffset;
        float[] previousShaderColor = RenderSystem.getShaderColor();
        float previousRed = previousShaderColor[0];
        float previousGreen = previousShaderColor[1];
        float previousBlue = previousShaderColor[2];
        float previousAlpha = previousShaderColor[3];

        try {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            renderer.blitOffset = 100.0F;
            renderer.renderAndDecorateItem(minecraft.player, placeholder, slot.x, slot.y,
                    slot.x + slot.y * 176);
        } finally {
            renderer.blitOffset = previousBlitOffset;
            RenderSystem.setShaderColor(previousRed, previousGreen, previousBlue, previousAlpha);
        }
    }

    @Override
    public void renderAnvilZeroCostLabel(Object anvilScreen, Object poseStack) {
        if (anvilScreen instanceof net.minecraft.client.gui.screens.inventory.AnvilScreen screen && poseStack instanceof PoseStack ps) {
            AnvilMenu menu = screen.getMenu();
            if (menu.getCost() == 0 && menu.getSlot(2).hasItem()) {
                Font font = Minecraft.getInstance().font;
                int imageWidth = 176;
                try {
                    java.lang.reflect.Field f = net.minecraft.client.gui.screens.inventory.AbstractContainerScreen.class.getDeclaredField("imageWidth");
                    f.setAccessible(true);
                    imageWidth = f.getInt(screen);
                } catch (Throwable ignored) {}
                Component component = new TranslatableComponent("container.repair.cost", 0);
                int k = imageWidth - 8 - font.width(component) - 2;
                GuiComponentBridge.fill(ps, k - 2, 67, imageWidth - 8, 79, 0x4F000000);
                font.drawShadow(ps, component, (float) k, 69.0F, 0x80FF20);
            }
        }
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
        return ((MultiBufferSource) bufferSource).getBuffer((RenderType) renderType);
    }

    @Override
    public VertexConsumer getFestiveFoilBuffer(Object bufferSource, Object renderType, boolean isItem) {
        return ((MultiBufferSource) bufferSource).getBuffer((RenderType) renderType);
    }

    @Override
    public VertexConsumer getFestiveArmorFoilBuffer(Object bufferSource, Object renderType, boolean isItem) {
        return ((MultiBufferSource) bufferSource).getBuffer((RenderType) renderType);
    }

    @Override
    public VertexConsumer getFestiveCompassFoilBuffer(Object bufferSource, Object renderType, PoseStack.Pose pose) {
        return ((MultiBufferSource) bufferSource).getBuffer((RenderType) renderType);
    }

    @Override
    public VertexConsumer getFestiveCompassFoilBufferDirect(Object bufferSource, Object renderType, PoseStack.Pose pose) {
        return ((MultiBufferSource) bufferSource).getBuffer((RenderType) renderType);
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
        if (bufferSource instanceof MultiBufferSource source && !stack.isEmpty() && stack.hasTag() && stack.getTag().getBoolean("ghost")) {
            return new RenderFactory.TransparentMultiBufferSource(source, 0.3F);
        }
        return bufferSource;
    }

    @Override
    public VertexConsumer wrapPipeSpillVertexConsumer(VertexConsumer original, Object level, BlockPos pos, BlockState state, net.minecraft.world.level.material.FluidState fluid) {
        if (level instanceof net.minecraft.world.level.BlockAndTintGetter tintGetter) {
            return RenderFactory.PipeSpillVertexConsumer.wrap(original, tintGetter, pos, state, fluid);
        }
        return original;
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
        if (statObj instanceof net.minecraft.stats.Stat<?> stat) {
            if (stat == ModStats.HEADER_MINECRAFT_STAT || stat == ModStats.HEADER_BUILDSCAPE_STAT || stat == ModStats.HEADER_OTHER_STAT) {
                Minecraft mc = Minecraft.getInstance();
                net.minecraft.network.chat.Component text;
                int color;
                if (stat == ModStats.HEADER_MINECRAFT_STAT) {
                    text = new net.minecraft.network.chat.TextComponent("§6§l═══ Minecraft Statistics ═══");
                    color = 0xFFFFAA00;
                } else if (stat == ModStats.HEADER_BUILDSCAPE_STAT) {
                    text = new net.minecraft.network.chat.TextComponent("§b§l═══ Buildscape Statistics ═══");
                    color = 0xFF55FFFF;
                } else {
                    text = new net.minecraft.network.chat.TextComponent("§e§l═══ Other Mod Statistics ═══");
                    color = 0xFFFFFF55;
                }
                int textWidth = mc.font.width(text);
                float centerX = left + width / 2.0f - textWidth / 2.0f;
                mc.font.draw(poseStack, text, centerX, (float) (top + 1), color);
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack cycleAdvancementIcon(Object widget, Object displayInfo) {
        if (displayInfo instanceof net.minecraft.advancements.DisplayInfo di) {
            return di.getIcon();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setScreen(Object screen) {
        if (Minecraft.getInstance() != null) {
            Minecraft.getInstance().setScreen((net.minecraft.client.gui.screens.Screen) screen);
        }
    }

    @Override
    public void sortGeneralStatsList(Object listObj, java.util.Comparator<?> originalComparatorObj) {
        if (listObj instanceof it.unimi.dsi.fastutil.objects.ObjectArrayList<?> list) {
            @SuppressWarnings("unchecked")
            java.util.Comparator<net.minecraft.stats.Stat<net.minecraft.resources.ResourceLocation>> originalComparator =
                    (java.util.Comparator<net.minecraft.stats.Stat<net.minecraft.resources.ResourceLocation>>) originalComparatorObj;
            @SuppressWarnings("unchecked")
            it.unimi.dsi.fastutil.objects.ObjectArrayList<net.minecraft.stats.Stat<net.minecraft.resources.ResourceLocation>> typedList =
                    (it.unimi.dsi.fastutil.objects.ObjectArrayList<net.minecraft.stats.Stat<net.minecraft.resources.ResourceLocation>>) list;
            boolean hasOtherMods = false;
            for (net.minecraft.stats.Stat<net.minecraft.resources.ResourceLocation> s : typedList) {
                String ns = s.getValue().getNamespace();
                if (!"minecraft".equals(ns) && !"buildscape".equals(ns)) {
                    hasOtherMods = true;
                    break;
                }
            }
            if (!hasOtherMods && ModStats.HEADER_OTHER_STAT != null) {
                typedList.remove(ModStats.HEADER_OTHER_STAT);
            }

            typedList.sort((a, b) -> {
                int pA = getStatsPriority(a);
                int pB = getStatsPriority(b);
                if (pA != pB) {
                    return Integer.compare(pA, pB);
                }
                return originalComparator.compare(a, b);
            });
        }
    }

    private static int getStatsPriority(net.minecraft.stats.Stat<net.minecraft.resources.ResourceLocation> stat) {
        if (stat == ModStats.HEADER_MINECRAFT_STAT) return 0;
        net.minecraft.resources.ResourceLocation id = stat.getValue();
        if ("minecraft".equals(id.getNamespace())) return 1;
        if (stat == ModStats.HEADER_BUILDSCAPE_STAT) return 2;
        if ("buildscape".equals(id.getNamespace())) return 3;
        if (stat == ModStats.HEADER_OTHER_STAT) return 4;
        return 5;
    }

    private static boolean buildscape$isRenderingCustomTooltip = false;

    @Override
    public void renderCustomScreenTooltip(net.minecraft.client.gui.screens.Screen screen, PoseStack poseStack, List<?> components, int mouseX, int mouseY, ItemStack hoverStack, Font font, Object itemRendererObj, int screenWidth, int screenHeight) {
        if (buildscape$isRenderingCustomTooltip) {
            return;
        }
        if (!com.kingodogo.buildscape.event.TagTooltipHandler.isShulkerPreviewEnabled()) {
            return;
        }
        if (!net.minecraft.client.gui.screens.Screen.hasShiftDown() || hoverStack == null || hoverStack.isEmpty()) {
            return;
        }

        try {
            buildscape$isRenderingCustomTooltip = true;

            com.kingodogo.buildscape.client.tooltip.ShulkerBoxTooltipData shulkerData = com.kingodogo.buildscape.event.TagTooltipHandler.getShulkerTooltipData(hoverStack);
            com.kingodogo.buildscape.client.tooltip.BuildersPouchTooltipData pouchData = shulkerData == null ? com.kingodogo.buildscape.event.TagTooltipHandler.getBuildersPouchTooltipData(hoverStack) : null;

            if (shulkerData == null && pouchData == null) {
                return;
            }

            int textTooltipWidth = 0;
            int textTooltipHeight = (components != null && components.size() == 1) ? -2 : 0;
            if (components != null) {
                for (Object compObj : components) {
                    if (compObj instanceof net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent comp) {
                        int k = comp.getWidth(font);
                        if (k > textTooltipWidth) {
                            textTooltipWidth = k;
                        }
                        textTooltipHeight += comp.getHeight();
                    }
                }
            }

            int textX = mouseX + 12;
            int textY = mouseY - 12;

            if (textX + textTooltipWidth > screenWidth) {
                textX -= 28 + textTooltipWidth;
            }
            if (textY + textTooltipHeight + 6 > screenHeight) {
                textY = screenHeight - textTooltipHeight - 6;
            }
            if (textY < 4) {
                textY = 4;
            }

            int customWidth = 9 * 18 + 14;
            int customHeight = shulkerData != null ? (3 * 18 + 14) : (1 * 18 + 14);

            int customX = textX - 3;
            int customY = textY + textTooltipHeight + 6;

            if (customY + customHeight > screenHeight - 4) {
                customY = (textY - 3) - customHeight - 3;
            }
            if (customY < 4) {
                customY = 4;
            }

            if (customX + customWidth > screenWidth - 4) {
                customX = screenWidth - customWidth - 4;
            }
            if (customX < 4) {
                customX = 4;
            }

            ItemRenderer itemRenderer = itemRendererObj instanceof ItemRenderer r ? r : (net.minecraft.client.Minecraft.getInstance() != null ? net.minecraft.client.Minecraft.getInstance().getItemRenderer() : null);
            if (itemRenderer == null) {
                return;
            }
            poseStack.pushPose();
            if (shulkerData != null) {
                float[] tint = com.kingodogo.buildscape.client.tooltip.ShulkerBoxTooltipData.getTint(shulkerData.getColor());
                renderTooltipBox(font, customX, customY, poseStack, itemRenderer, 400, 3, tint, shulkerData.getFilterStacks(), shulkerData.getRealStacks());
            } else {
                float[] tint = com.kingodogo.buildscape.client.tooltip.BuildersPouchTooltipData.hexToRgb(com.kingodogo.buildscape.client.tooltip.BuildersPouchTooltipData.COLOR_GOLD);
                renderTooltipBox(font, customX, customY, poseStack, itemRenderer, 400, 1, tint, pouchData.getFilterStacks(), pouchData.getRealStacks());
            }
            poseStack.popPose();
        } catch (Throwable t) {
        } finally {
            buildscape$isRenderingCustomTooltip = false;
        }
    }

    private static void renderTooltipBox(Font font, int x, int y, PoseStack poseStack, ItemRenderer itemRenderer, int blitOffset,
                                         int rows, float[] tint, NonNullList<ItemStack> filterStacks, NonNullList<ItemStack> realStacks) {
        Minecraft mc = Minecraft.getInstance();
        int width = 9 * 18 + 14;
        int height = rows * 18 + 14;
        ResourceLocation texture = new ResourceLocation("buildscape", "textures/gui/shulker_box_tooltip.png");

        RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(tint[0], tint[1], tint[2], 1.0F);
        RenderSystem.setShaderTexture(0, texture);

        net.minecraft.client.gui.GuiComponent.blit(poseStack, x, y, 0, 0, 7, 7, 32, 32);
        net.minecraft.client.gui.GuiComponent.blit(poseStack, x + width - 7, y, 25, 0, 7, 7, 32, 32);
        net.minecraft.client.gui.GuiComponent.blit(poseStack, x, y + height - 7, 0, 25, 7, 7, 32, 32);
        net.minecraft.client.gui.GuiComponent.blit(poseStack, x + width - 7, y + height - 7, 25, 25, 7, 7, 32, 32);

        for (int col = 0; col < 9; col++) {
            net.minecraft.client.gui.GuiComponent.blit(poseStack, x + 7 + col * 18, y, 7, 0, 18, 7, 32, 32);
            net.minecraft.client.gui.GuiComponent.blit(poseStack, x + 7 + col * 18, y + height - 7, 7, 25, 18, 7, 32, 32);
        }

        for (int row = 0; row < rows; row++) {
            net.minecraft.client.gui.GuiComponent.blit(poseStack, x, y + 7 + row * 18, 0, 7, 7, 18, 32, 32);
            net.minecraft.client.gui.GuiComponent.blit(poseStack, x + width - 7, y + 7 + row * 18, 25, 7, 7, 18, 32, 32);
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 9; col++) {
                net.minecraft.client.gui.GuiComponent.blit(poseStack, x + 7 + col * 18, y + 7 + row * 18, 7, 7, 18, 18, 32, 32);
            }
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 9; col++) {
                int index = row * 9 + col;
                int slotX = x + 7 + col * 18;
                int slotY = y + 7 + row * 18;

                ItemStack filter = index < filterStacks.size() ? filterStacks.get(index) : ItemStack.EMPTY;
                ItemStack real = index < realStacks.size() ? realStacks.get(index) : ItemStack.EMPTY;

                int itemX = slotX + 1;
                int itemY = slotY + 1;

                try {
                    if (!real.isEmpty()) {
                        float prevBlit = itemRenderer.blitOffset;
                        itemRenderer.blitOffset = blitOffset + 100.0F;
                        itemRenderer.renderAndDecorateItem(mc.player, real, itemX, itemY, index);
                        itemRenderer.renderGuiItemDecorations(font, real, itemX, itemY);
                        itemRenderer.blitOffset = prevBlit;
                    } else if (!filter.isEmpty()) {
                        float prevBlit = itemRenderer.blitOffset;
                        itemRenderer.blitOffset = blitOffset + 100.0F;
                        itemRenderer.renderAndDecorateItem(mc.player, filter, itemX, itemY, index);
                        itemRenderer.blitOffset = prevBlit;
                    }
                } catch (Throwable ignored) {
                }
            }
        }
    }

    private static final class GuiComponentBridge extends net.minecraft.client.gui.GuiComponent {
        public static void fill(PoseStack poseStack, int minX, int minY, int maxX, int maxY, int color) {
            net.minecraft.client.gui.GuiComponent.fill(poseStack, minX, minY, maxX, maxY, color);
        }
    }
    private static final java.util.regex.Pattern BUILDSCAPE_CUSTOM_LOADER =
            java.util.regex.Pattern.compile("\"loader\"\\s*:");

    private Map<ResourceLocation, net.minecraft.client.renderer.block.model.BlockModel> buildscape$parsedModels;
    private volatile boolean buildscape$hasCustomGeometry;

    @Override
    public void onModelBakeryPreload(Object bakeryObj, Object resourceManagerObj) {
        if (!BuildscapeClientConfig.get().isParallelModelLoadingEnabled() ||
                com.kingodogo.buildscape.client.performance.LaunchFasterInterop.isParallelModelLoadingEnabled()) {
            return;
        }

        long startedAt = System.nanoTime();
        List<ResourceLocation> modelFiles = buildscape$listBundledModels();
        if (modelFiles.isEmpty()) return;

        net.minecraft.server.packs.resources.ResourceManager resourceManager = (net.minecraft.server.packs.resources.ResourceManager) resourceManagerObj;
        java.util.concurrent.ConcurrentMap<ResourceLocation, net.minecraft.client.renderer.block.model.BlockModel> parsedModels = new java.util.concurrent.ConcurrentHashMap<>();
        java.util.concurrent.atomic.AtomicInteger fallbackCount = new java.util.concurrent.atomic.AtomicInteger();

        com.kingodogo.buildscape.client.performance.BuildscapeStartupWork.forEachIndex(modelFiles.size(), index -> {
            ResourceLocation fileLocation = modelFiles.get(index);
            try (
                    net.minecraft.server.packs.resources.Resource resource = resourceManager.getResource(fileLocation);
                    java.io.InputStream stream = resource.getInputStream()
            ) {
                String json = new String(stream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                if (BUILDSCAPE_CUSTOM_LOADER.matcher(json).find()) {
                    fallbackCount.incrementAndGet();
                    return;
                }

                ResourceLocation modelLocation = buildscape$toModelLocation(fileLocation);
                net.minecraft.client.renderer.block.model.BlockModel model = net.minecraft.client.renderer.block.model.BlockModel.fromString(json);
                model.name = modelLocation.toString();
                parsedModels.put(modelLocation, model);
            } catch (Exception exception) {
                fallbackCount.incrementAndGet();
                com.kingodogo.buildscape.BuildscapeCommon.LOGGER.debug(
                        "Buildscape model preload deferred {} to the normal loader",
                        fileLocation,
                        exception
                );
            }
        });

        buildscape$parsedModels = parsedModels;
        long elapsedMillis = (System.nanoTime() - startedAt) / 1_000_000L;
        com.kingodogo.buildscape.BuildscapeCommon.LOGGER.info(
                "Buildscape startup parsed {} model files in parallel ({} ms, {} sequential fallbacks)",
                parsedModels.size(),
                elapsedMillis,
                fallbackCount.get()
        );
    }

    @Override
    public Object onModelBakeryLoadModel(Object locationObj) {
        if (!(locationObj instanceof ResourceLocation location)) return null;
        Map<ResourceLocation, net.minecraft.client.renderer.block.model.BlockModel> parsed = buildscape$parsedModels;
        if (parsed == null || !"buildscape".equals(location.getNamespace())) {
            return null;
        }
        return parsed.get(location);
    }

    @Override
    public void onModelBakeryDetectCustomGeometry(Object locationObj, Object modelObj) {
        if (locationObj instanceof ResourceLocation location && modelObj instanceof net.minecraft.client.renderer.block.model.BlockModel model) {
            if ("buildscape".equals(location.getNamespace())) {
                try {
                    java.lang.reflect.Field customDataField = model.getClass().getDeclaredField("customData");
                    customDataField.setAccessible(true);
                    Object customData = customDataField.get(model);
                    if (customData != null) {
                        java.lang.reflect.Method hasCustom = customData.getClass().getMethod("hasCustomGeometry");
                        if ((boolean) hasCustom.invoke(customData)) {
                            buildscape$hasCustomGeometry = true;
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
        }
    }

    @Override
    public void onModelBakeryRelease() {
        buildscape$parsedModels = null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onModelBakeryBakeParallel(Object bakeryObj) {
        if (!BuildscapeClientConfig.get().isParallelModelBakingEnabled() ||
                com.kingodogo.buildscape.client.performance.LaunchFasterInterop.isParallelModelBakingEnabled()) {
            return;
        }

        if (buildscape$hasCustomGeometry) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.info(
                    "Buildscape startup kept model baking sequential because custom geometry was detected"
            );
            return;
        }

        try {
            ModelBakery bakery = (ModelBakery) bakeryObj;
            java.lang.reflect.Field topLevelField = ModelBakery.class.getDeclaredField("topLevelModels");
            topLevelField.setAccessible(true);
            Map<ResourceLocation, ?> topLevelModels = (Map<ResourceLocation, ?>) topLevelField.get(bakery);

            List<ResourceLocation> candidates = topLevelModels.keySet().stream()
                    .filter(location -> "buildscape".equals(location.getNamespace()))
                    .toList();
            if (candidates.isEmpty()) return;

            java.lang.reflect.Field bakedCacheField = ModelBakery.class.getDeclaredField("bakedCache");
            bakedCacheField.setAccessible(true);
            Map<?, ?> origCache = (Map<?, ?>) bakedCacheField.get(bakery);
            Map newCache = new java.util.concurrent.ConcurrentHashMap<>(origCache);
            bakedCacheField.set(bakery, newCache);

            long startedAt = System.nanoTime();
            java.util.Set<ResourceLocation> completed = java.util.concurrent.ConcurrentHashMap.newKeySet();
            java.util.concurrent.atomic.AtomicInteger fallbackCount = new java.util.concurrent.atomic.AtomicInteger();

            java.lang.reflect.Method bakeMethod = ModelBakery.class.getDeclaredMethod("bake", ResourceLocation.class, net.minecraft.client.resources.model.ModelState.class);
            bakeMethod.setAccessible(true);

            com.kingodogo.buildscape.client.performance.BuildscapeStartupWork.forEachIndex(candidates.size(), index -> {
                ResourceLocation location = candidates.get(index);
                try {
                    Object bakedModel = bakeMethod.invoke(bakery, location, net.minecraft.client.resources.model.BlockModelRotation.X0_Y0);
                    if (bakedModel != null) {
                        completed.add(location);
                    } else {
                        fallbackCount.incrementAndGet();
                    }
                } catch (Throwable exception) {
                    fallbackCount.incrementAndGet();
                    com.kingodogo.buildscape.BuildscapeCommon.LOGGER.debug(
                            "Buildscape model bake deferred {} to the normal loader",
                            location,
                            exception
                    );
                }
            });

            long elapsedMillis = (System.nanoTime() - startedAt) / 1_000_000L;
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.info(
                    "Buildscape startup baked {} top-level models in parallel ({} ms, {} sequential fallbacks)",
                    completed.size(),
                    elapsedMillis,
                    fallbackCount.get()
            );
        } catch (Throwable t) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Buildscape parallel model baking encountered error: {}", t.getMessage());
        }
    }

    private static ResourceLocation buildscape$toModelLocation(ResourceLocation fileLocation) {
        String path = fileLocation.getPath();
        String modelPath = path.substring("models/".length(), path.length() - ".json".length());
        return new ResourceLocation(fileLocation.getNamespace(), modelPath);
    }

    private static List<ResourceLocation> buildscape$listBundledModels() {
        try {
            Class<?> modListClass = Class.forName("net.minecraftforge.fml.ModList");
            Object modList = modListClass.getMethod("get").invoke(null);
            Object modFile = modListClass.getMethod("getModFileById", String.class).invoke(modList, "buildscape");
            if (modFile != null) {
                Object fileObj = modFile.getClass().getMethod("getFile").invoke(modFile);
                java.nio.file.Path modelRoot = (java.nio.file.Path) fileObj.getClass().getMethod("findResource", String[].class).invoke(fileObj, (Object) new String[]{"assets", "buildscape", "models"});
                if (modelRoot != null && java.nio.file.Files.isDirectory(modelRoot)) {
                    try (var paths = java.nio.file.Files.walk(modelRoot)) {
                        return paths
                                .filter(java.nio.file.Files::isRegularFile)
                                .map(modelRoot::relativize)
                                .map(java.nio.file.Path::toString)
                                .filter(path -> path.endsWith(".json"))
                                .map(path -> new ResourceLocation(
                                        "buildscape",
                                        "models/" + path.replace('\\', '/')
                                ))
                                .sorted(java.util.Comparator.comparing(ResourceLocation::toString))
                                .toList();
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        return List.of();
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
        if (!(starterObj instanceof net.minecraft.client.particle.Particle particle)) return;
        try {
            java.lang.reflect.Field ageField = net.minecraft.client.particle.Particle.class.getDeclaredField("age");
            ageField.setAccessible(true);
            int age = ageField.getInt(particle);
            if (age != 0) return;

            java.lang.reflect.Field explosionsField = starterObj.getClass().getDeclaredField("explosions");
            explosionsField.setAccessible(true);
            net.minecraft.nbt.ListTag explosions = (net.minecraft.nbt.ListTag) explosionsField.get(starterObj);
            if (explosions == null || explosions.isEmpty()) return;

            java.lang.reflect.Method createParticleMethod = starterObj.getClass().getDeclaredMethod("createParticle",
                    double.class, double.class, double.class, double.class, double.class, double.class,
                    int[].class, int[].class, boolean.class, boolean.class);
            createParticleMethod.setAccessible(true);

            net.minecraft.nbt.ListTag vanillaExplosions = new net.minecraft.nbt.ListTag();

            for (int i = 0; i < explosions.size(); ++i) {
                net.minecraft.nbt.CompoundTag tag = explosions.getCompound(i);
                byte type = tag.getByte("Type");

                if (com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry.isCustomShape(type)) {
                    var shapeOpt = com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry.getByNumericId(type);
                    if (shapeOpt.isPresent()) {
                        var shape = shapeOpt.get();
                        boolean trail = tag.getBoolean("Trail");
                        boolean flicker = tag.getBoolean("Flicker");
                        int[] colors = tag.getIntArray("Colors");
                        int[] fadeColors = tag.getIntArray("FadeColors");

                        java.lang.reflect.Field xField = net.minecraft.client.particle.Particle.class.getDeclaredField("x");
                        xField.setAccessible(true);
                        double pxVal = xField.getDouble(particle);

                        java.lang.reflect.Field yField = net.minecraft.client.particle.Particle.class.getDeclaredField("y");
                        yField.setAccessible(true);
                        double pyVal = yField.getDouble(particle);

                        java.lang.reflect.Field zField = net.minecraft.client.particle.Particle.class.getDeclaredField("z");
                        zField.setAccessible(true);
                        double pzVal = zField.getDouble(particle);

                        java.lang.reflect.Field xdField = net.minecraft.client.particle.Particle.class.getDeclaredField("xd");
                        xdField.setAccessible(true);
                        double pxdVal = xdField.getDouble(particle);

                        java.lang.reflect.Field zdField = net.minecraft.client.particle.Particle.class.getDeclaredField("zd");
                        zdField.setAccessible(true);
                        double pzdVal = zdField.getDouble(particle);

                        float yaw = 0.0F;
                        if (tag.contains("ShotYaw")) {
                            yaw = tag.getFloat("ShotYaw");
                        } else if (Math.hypot(pxdVal, pzdVal) > 0.05) {
                            yaw = (float) (net.minecraft.util.Mth.atan2(pxdVal, pzdVal) * (180.0F / (float) Math.PI));
                        } else if (Minecraft.getInstance().player != null) {
                            yaw = Minecraft.getInstance().player.getYRot();
                        }

                        particle.setBoundingBox(particle.getBoundingBox().inflate(120.0D, 120.0D, 120.0D));

                        com.kingodogo.buildscape.firework.CustomFireworkRenderer.renderExplosion(
                                shape,
                                pxVal, pyVal, pzVal,
                                colors, fadeColors,
                                trail, flicker,
                                yaw,
                                (px, py, pz, vx, vy, vz, c, fc, tr, fl) -> {
                                    try {
                                        createParticleMethod.invoke(starterObj, px, py, pz, vx, vy, vz, c, fc, tr, fl);
                                    } catch (Throwable ignored) {}
                                }
                        );
                    }
                } else {
                    vanillaExplosions.add(tag);
                }
            }

            explosionsField.set(starterObj, vanillaExplosions);
        } catch (Throwable t) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.debug("Custom firework starter tick failed: {}", t.getMessage());
        }
    }
}
