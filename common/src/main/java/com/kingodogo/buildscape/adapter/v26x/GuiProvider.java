package com.kingodogo.buildscape.adapter.v26x;

import com.kingodogo.buildscape.menu.ModMenuTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
public final class GuiProvider {
    private GuiProvider() {}

    /**
     * Resolves the graphics context to PoseStack in 26.x.
     */
    public static PoseStack toPoseStack(Object poseStackOrGraphics) {
        if (poseStackOrGraphics instanceof PoseStack ps) {
            return ps;
        }
        return null;
    }

    public static void registerMenuScreens() {
        try {
            java.lang.reflect.Field field = net.minecraft.client.gui.screens.MenuScreens.class.getDeclaredField("SCREENS");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.Map<net.minecraft.world.inventory.MenuType<?>, Object> screens =
                    (java.util.Map<net.minecraft.world.inventory.MenuType<?>, Object>) field.get(null);

            Class<?> constructorType = Class.forName("net.minecraft.client.gui.screens.MenuScreens$ScreenConstructor");
            Object wbProxy = java.lang.reflect.Proxy.newProxyInstance(
                    constructorType.getClassLoader(), new Class<?>[]{constructorType},
                    (proxy, method, args) -> "create".equals(method.getName())
                            ? createWorkbenchScreen(
                                    (com.kingodogo.buildscape.menu.BuildersWorkbenchMenu) args[0],
                                    (net.minecraft.world.entity.player.Inventory) args[1],
                                    (net.minecraft.network.chat.Component) args[2])
                            : null);
            screens.put(ModMenuTypes.BUILDERS_WORKBENCH_MENU, wbProxy);

            Object pouchProxy = java.lang.reflect.Proxy.newProxyInstance(
                    constructorType.getClassLoader(), new Class<?>[]{constructorType},
                    (proxy, method, args) -> "create".equals(method.getName())
                            ? createPouchScreen(
                                    (com.kingodogo.buildscape.menu.BuildersPouchMenu) args[0],
                                    (net.minecraft.world.entity.player.Inventory) args[1],
                                    (net.minecraft.network.chat.Component) args[2])
                            : null);
            screens.put(ModMenuTypes.BUILDERS_POUCH_MENU, pouchProxy);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to register Buildscape 26.x menu screens", exception);
        }
    }

    private static Screen createWorkbenchScreen(
            com.kingodogo.buildscape.menu.BuildersWorkbenchMenu menu,
            net.minecraft.world.entity.player.Inventory inventory,
            net.minecraft.network.chat.Component title) {
        com.kingodogo.buildscape.client.screen.BuildersWorkbenchScreen delegate =
                new com.kingodogo.buildscape.client.screen.BuildersWorkbenchScreen(menu, inventory, title);
        return new net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<com.kingodogo.buildscape.menu.BuildersWorkbenchMenu>(
                                menu, inventory, title,
                                com.kingodogo.buildscape.client.screen.BuildersWorkbenchScreen.GRADIENT_WIDTH,
                                com.kingodogo.buildscape.client.screen.BuildersWorkbenchScreen.GUI_HEIGHT) {
                            private int delegateLeft() {
                                return (this.width - delegate.getImageWidth()) / 2;
                            }

                            @Override
                            protected void init() {
                                super.init();
                                delegate.init(delegateLeft(), this.topPos, this.width, this.height);
                            }

                            @Override
                            protected void containerTick() {
                                super.containerTick();
                                delegate.containerTick();
                            }

                            @Override
                            public void extractContents(net.minecraft.client.gui.GuiGraphicsExtractor extractor,
                                                        int mouseX, int mouseY, float partialTick) {
                                delegate.renderBg(extractor, partialTick, mouseX, mouseY, delegateLeft(), this.topPos);
                                super.extractContents(extractor, mouseX, mouseY, partialTick);
                                delegate.renderRerollControls(extractor, mouseX, mouseY, delegateLeft(), this.topPos);
                            }

                            @Override
                            protected void extractTooltip(net.minecraft.client.gui.GuiGraphicsExtractor extractor,
                                                          int mouseX, int mouseY) {
                                if (!delegate.renderCustomTooltip(extractor, mouseX, mouseY, delegateLeft(), this.topPos)) {
                                    super.extractTooltip(extractor, mouseX, mouseY);
                                }
                            }

                            @Override
                            public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
                                if (delegate.mouseClicked(event.x(), event.y(), event.button(), delegateLeft(), this.topPos)) {
                                    return true;
                                }
                                return super.mouseClicked(event, doubleClick);
                            }
                        };
    }

    private static Screen createPouchScreen(
            com.kingodogo.buildscape.menu.BuildersPouchMenu menu,
            net.minecraft.world.entity.player.Inventory inventory,
            net.minecraft.network.chat.Component title) {
        com.kingodogo.buildscape.client.screen.BuildersPouchScreen delegate =
                new com.kingodogo.buildscape.client.screen.BuildersPouchScreen(menu, inventory, title);
        return new net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<com.kingodogo.buildscape.menu.BuildersPouchMenu>(
                                menu, inventory, title,
                                com.kingodogo.buildscape.client.screen.BuildersPouchScreen.GUI_WIDTH,
                                com.kingodogo.buildscape.client.screen.BuildersPouchScreen.GUI_HEIGHT) {
                            @Override
                            public void extractContents(net.minecraft.client.gui.GuiGraphicsExtractor extractor,
                                                        int mouseX, int mouseY, float partialTick) {
                                delegate.renderBg(extractor, partialTick, mouseX, mouseY, this.leftPos, this.topPos);
                                super.extractContents(extractor, mouseX, mouseY, partialTick);
                            }
                        };
    }

    public static Screen wrapScreen(net.minecraft.network.chat.Component title, Screen parent, com.kingodogo.buildscape.client.screen.IScreenDelegate delegate) {
        Screen screen = new Screen(title) {
            @Override
            protected void init() {
                super.init();
                delegate.init(this.minecraft, this.width, this.height, this::addRenderableWidget, this::removeWidget);
            }

            @Override
            public void extractRenderState(net.minecraft.client.gui.GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
                super.extractRenderState(extractor, mouseX, mouseY, partialTick);
                delegate.render(extractor, mouseX, mouseY, partialTick);
                delegate.renderAfterWidgets(extractor, mouseX, mouseY, partialTick);
            }

            @Override
            public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
                if (delegate.mouseClicked(event.x(), event.y(), event.button())) return true;
                return super.mouseClicked(event, doubleClick);
            }

            @Override
            public boolean mouseReleased(net.minecraft.client.input.MouseButtonEvent event) {
                if (delegate.mouseReleased(event.x(), event.y(), event.button())) return true;
                return super.mouseReleased(event);
            }

            @Override
            public boolean mouseDragged(net.minecraft.client.input.MouseButtonEvent event, double dragX, double dragY) {
                if (delegate.mouseDragged(event.x(), event.y(), event.button(), dragX, dragY)) return true;
                return super.mouseDragged(event, dragX, dragY);
            }

            @Override
            public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
                if (delegate.mouseScrolled(mouseX, mouseY, scrollY)) return true;
                return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            }

            @Override
            public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
                if (delegate.keyPressed(event.key(), event.scancode(), event.modifiers())) return true;
                return super.keyPressed(event);
            }

            @Override
            public boolean charTyped(net.minecraft.client.input.CharacterEvent event) {
                if (delegate.charTyped((char) event.codepoint(), 0)) return true;
                return super.charTyped(event);
            }

            @Override
            public void onClose() {
                delegate.onClose();
            }

            @Override
            public void tick() {
                super.tick();
                delegate.tick();
            }

            @Override
            public void resize(int width, int height) {
                super.resize(width, height);
                delegate.resize(this.minecraft, width, height);
            }

            @Override
            public boolean isPauseScreen() {
                return delegate.isPauseScreen();
            }
        };
        delegate.setWrapperScreen(screen);
        return screen;
    }

    public static Screen createConfigScreen(Screen parent) {
        try {
            Class<?> clazz = Class.forName("com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen");
            com.kingodogo.buildscape.client.screen.IScreenDelegate delegate =
                    (com.kingodogo.buildscape.client.screen.IScreenDelegate) clazz.getConstructor(Screen.class).newInstance(parent);
            return wrapScreen(com.kingodogo.buildscape.util.ComponentHelper.translatable("buildscape.config.title"), parent, delegate);
        } catch (Throwable t) {
            return null;
        }
    }

    public static Screen createGuiEditorScreen(Screen parent, String tabName, com.kingodogo.buildscape.client.screen.AbstractConfigTab sourceTab) {
        try {
            Class<?> clazz = Class.forName("com.kingodogo.buildscape.client.screen.GuiEditorScreen");
            com.kingodogo.buildscape.client.screen.IScreenDelegate delegate =
                    (com.kingodogo.buildscape.client.screen.IScreenDelegate) clazz.getConstructor(Screen.class, String.class, com.kingodogo.buildscape.client.screen.AbstractConfigTab.class).newInstance(parent, tabName, sourceTab);
            return wrapScreen(com.kingodogo.buildscape.util.ComponentHelper.translatable("buildscape.gui.editor.title"), parent, delegate);
        } catch (Throwable t) {
            return null;
        }
    }

    public static Screen createInventoryItemSelectorScreen(Screen parent, com.kingodogo.buildscape.client.screen.PillarItemsConfigTab configTab) {
        try {
            Class<?> clazz = Class.forName("com.kingodogo.buildscape.client.screen.InventoryItemSelectorScreen");
            com.kingodogo.buildscape.client.screen.IScreenDelegate delegate =
                    (com.kingodogo.buildscape.client.screen.IScreenDelegate) clazz.getConstructor(Screen.class, com.kingodogo.buildscape.client.screen.PillarItemsConfigTab.class).newInstance(parent, configTab);
            return wrapScreen(com.kingodogo.buildscape.util.ComponentHelper.translatable("buildscape.config.select_inventory"), parent, delegate);
        } catch (Throwable t) {
            return null;
        }
    }

    public static void renderGuiItem(Object poseStackOrGraphics, net.minecraft.world.item.ItemStack stack, int x, int y) {
        if (poseStackOrGraphics instanceof net.minecraft.client.gui.GuiGraphicsExtractor extractor
                && stack != null && !stack.isEmpty()) {
            extractor.item(stack, x, y);
        }
    }

    public static void renderGuiItemDecorations(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, net.minecraft.world.item.ItemStack stack, int x, int y) {
        if (poseStackOrGraphics instanceof net.minecraft.client.gui.GuiGraphicsExtractor extractor
                && stack != null && !stack.isEmpty()) {
            extractor.itemDecorations(font, stack, x, y);
        }
    }

    public static java.util.List<net.minecraft.network.chat.Component> getTooltipFromItem(net.minecraft.world.item.ItemStack stack) {
        return net.minecraft.client.gui.screens.Screen.getTooltipFromItem(net.minecraft.client.Minecraft.getInstance(), stack);
    }

    public static net.minecraft.client.gui.components.Button createButton(int x, int y, int width, int height, net.minecraft.network.chat.Component message, net.minecraft.client.gui.components.Button.OnPress onPress) {
        return net.minecraft.client.gui.components.Button.builder(message, onPress).bounds(x, y, width, height).build();
    }

    public static net.minecraft.client.gui.components.Button createCustomButton(int x, int y, int width, int height, net.minecraft.network.chat.Component message, net.minecraft.client.gui.components.Button.OnPress onPress, com.kingodogo.buildscape.client.screen.widget.CustomButtonRenderer renderer) {
        return new net.minecraft.client.gui.components.Button(x, y, width, height, message, onPress, supplier -> supplier.get()) {
            @Override
            protected void extractContents(net.minecraft.client.gui.GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
                renderer.render(this, extractor, mouseX, mouseY, partialTick);
            }
        };
    }

    public static void renderWidget(Object poseStackOrGraphics, net.minecraft.client.gui.components.AbstractWidget widget, int mouseX, int mouseY, float partialTick) {
        if (widget != null && poseStackOrGraphics instanceof net.minecraft.client.gui.GuiGraphicsExtractor extractor) {
            widget.extractRenderState(extractor, mouseX, mouseY, partialTick);
        }
    }

    public static net.minecraft.client.gui.components.AbstractWidget wrapCustomWidget(int x, int y, int width, int height, net.minecraft.network.chat.Component message, com.kingodogo.buildscape.client.screen.widget.ICustomWidget customWidget) {
        return new net.minecraft.client.gui.components.AbstractWidget(x, y, width, height, message) {
            @Override
            protected void extractWidgetRenderState(net.minecraft.client.gui.GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
                customWidget.render(extractor, mouseX, mouseY, partialTick);
            }

            @Override
            public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
                if (customWidget.mouseClicked(event.x(), event.y(), event.button())) return true;
                return super.mouseClicked(event, doubleClick);
            }

            @Override
            public boolean mouseReleased(net.minecraft.client.input.MouseButtonEvent event) {
                if (customWidget.mouseReleased(event.x(), event.y(), event.button())) return true;
                return super.mouseReleased(event);
            }

            @Override
            public boolean mouseDragged(net.minecraft.client.input.MouseButtonEvent event, double dragX, double dragY) {
                if (customWidget.mouseDragged(event.x(), event.y(), event.button(), dragX, dragY)) return true;
                return super.mouseDragged(event, dragX, dragY);
            }

            @Override
            public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
                if (customWidget.mouseScrolled(mouseX, mouseY, scrollY)) return true;
                return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            }

            @Override
            public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
                if (customWidget.keyPressed(event.key(), event.scancode(), event.modifiers())) return true;
                return super.keyPressed(event);
            }

            @Override
            public boolean charTyped(net.minecraft.client.input.CharacterEvent event) {
                if (customWidget.charTyped((char) event.codepoint(), 0)) return true;
                return super.charTyped(event);
            }

            @Override
            protected void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput narrationElementOutput) {}
        };
    }
}
