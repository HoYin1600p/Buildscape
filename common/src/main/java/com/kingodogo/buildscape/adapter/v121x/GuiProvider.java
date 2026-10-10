package com.kingodogo.buildscape.adapter.v121x;

import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;
import com.kingodogo.buildscape.client.screen.BuildersPouchScreen;
import com.kingodogo.buildscape.client.screen.BuildersWorkbenchScreen;
import com.kingodogo.buildscape.menu.BuildersPouchMenu;
import com.kingodogo.buildscape.menu.BuildersWorkbenchMenu;
import com.kingodogo.buildscape.menu.ModMenuTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
public final class GuiProvider {
    private GuiProvider() {}

    /**
     * Resolves the graphics context: maps GuiGraphics -> PoseStack via gfx.pose().
     */
    public static PoseStack toPoseStack(Object poseStackOrGraphics) {
        if (poseStackOrGraphics instanceof GuiGraphics gfx) {
            return gfx.pose();
        }
        if (poseStackOrGraphics instanceof PoseStack ps) {
            return ps;
        }
        return null;
    }

    public static void registerMenuScreens() {
        try {
            java.lang.reflect.Field field = MenuScreens.class.getDeclaredField("SCREENS");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.Map<net.minecraft.world.inventory.MenuType<?>, Object> screens =
                    (java.util.Map<net.minecraft.world.inventory.MenuType<?>, Object>) field.get(null);

            MenuScreens.ScreenConstructor<BuildersWorkbenchMenu, AbstractContainerScreen<BuildersWorkbenchMenu>> wbConstructor =
                    (menu, inventory, title) -> {
                        BuildersWorkbenchScreen delegate = new BuildersWorkbenchScreen(menu, inventory, title);
                        return new AbstractContainerScreen<BuildersWorkbenchMenu>(menu, inventory, title) {
                            @Override
                            protected void init() {
                                this.imageWidth = delegate.getImageWidth();
                                this.imageHeight = delegate.getImageHeight();
                                super.init();
                                delegate.init(this.leftPos, this.topPos, this.width, this.height);
                            }

                            @Override
                            public void containerTick() {
                                super.containerTick();
                                delegate.containerTick();
                            }

                            @Override
                            protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
                                delegate.renderBg(guiGraphics, partialTick, mouseX, mouseY, this.leftPos, this.topPos);
                            }

                            @Override
                            public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                                super.render(guiGraphics, mouseX, mouseY, partialTick);
                                delegate.renderRerollControls(guiGraphics, mouseX, mouseY, this.leftPos, this.topPos);
                                renderTooltip(guiGraphics, mouseX, mouseY);
                            }

                            @Override
                            protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
                                if (delegate.renderCustomTooltip(guiGraphics, mouseX, mouseY, this.leftPos, this.topPos)) {
                                    return;
                                }
                                super.renderTooltip(guiGraphics, mouseX, mouseY);
                            }

                            @Override
                            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                                if (delegate.mouseClicked(mouseX, mouseY, button, this.leftPos, this.topPos)) {
                                    return true;
                                }
                                return super.mouseClicked(mouseX, mouseY, button);
                            }
                        };
                    };
            screens.put(ModMenuTypes.BUILDERS_WORKBENCH_MENU, wbConstructor);

            MenuScreens.ScreenConstructor<BuildersPouchMenu, AbstractContainerScreen<BuildersPouchMenu>> pouchConstructor =
                    (menu, inventory, title) -> {
                        BuildersPouchScreen delegate = new BuildersPouchScreen(menu, inventory, title);
                        return new AbstractContainerScreen<BuildersPouchMenu>(menu, inventory, title) {
                            @Override
                            protected void init() {
                                this.imageWidth = BuildersPouchScreen.GUI_WIDTH;
                                this.imageHeight = BuildersPouchScreen.GUI_HEIGHT;
                                super.init();
                            }

                            @Override
                            protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
                                delegate.renderBg(guiGraphics.pose(), partialTick, mouseX, mouseY, this.leftPos, this.topPos);
                            }

                            @Override
                            public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                                super.render(guiGraphics, mouseX, mouseY, partialTick);
                                renderTooltip(guiGraphics, mouseX, mouseY);
                            }
                        };
                    };
            screens.put(ModMenuTypes.BUILDERS_POUCH_MENU, pouchConstructor);
        } catch (Throwable ignored) {
        }
    }

    public static Screen wrapScreen(net.minecraft.network.chat.Component title, Screen parent, com.kingodogo.buildscape.client.screen.IScreenDelegate delegate) {
        Screen screen = new Screen(title) {
            @Override
            protected void init() {
                super.init();
                delegate.init(this.minecraft, this.width, this.height, this::addRenderableWidget, this::removeWidget);
            }

            @Override
            public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                super.render(guiGraphics, mouseX, mouseY, partialTick);
                delegate.render(guiGraphics, mouseX, mouseY, partialTick);
                delegate.renderAfterWidgets(guiGraphics, mouseX, mouseY, partialTick);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (delegate.mouseClicked(mouseX, mouseY, button)) return true;
                return super.mouseClicked(mouseX, mouseY, button);
            }

            @Override
            public boolean mouseReleased(double mouseX, double mouseY, int button) {
                if (delegate.mouseReleased(mouseX, mouseY, button)) return true;
                return super.mouseReleased(mouseX, mouseY, button);
            }

            @Override
            public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
                if (delegate.mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;
                return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
            }

            @Override
            public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
                if (delegate.mouseScrolled(mouseX, mouseY, verticalAmount)) return true;
                return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
            }

            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                if (delegate.keyPressed(keyCode, scanCode, modifiers)) return true;
                return super.keyPressed(keyCode, scanCode, modifiers);
            }

            @Override
            public boolean charTyped(char codePoint, int modifiers) {
                if (delegate.charTyped(codePoint, modifiers)) return true;
                return super.charTyped(codePoint, modifiers);
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
            public void resize(net.minecraft.client.Minecraft mc, int width, int height) {
                super.resize(mc, width, height);
                delegate.resize(mc, width, height);
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
        return wrapScreen(com.kingodogo.buildscape.util.ComponentHelper.translatable("buildscape.config.title"), parent, new BuildScapeConfigScreen(parent));
    }

    public static Screen createGuiEditorScreen(Screen parent, String tabName, com.kingodogo.buildscape.client.screen.AbstractConfigTab sourceTab) {
        return wrapScreen(com.kingodogo.buildscape.util.ComponentHelper.translatable("buildscape.gui.editor.title"), parent, new com.kingodogo.buildscape.client.screen.GuiEditorScreen(parent, tabName, sourceTab));
    }

    public static Screen createInventoryItemSelectorScreen(Screen parent, com.kingodogo.buildscape.client.screen.PillarItemsConfigTab configTab) {
        return wrapScreen(com.kingodogo.buildscape.util.ComponentHelper.translatable("buildscape.config.select_inventory"), parent, new com.kingodogo.buildscape.client.screen.InventoryItemSelectorScreen(parent, configTab));
    }

    public static void renderGuiItem(Object poseStackOrGraphics, net.minecraft.world.item.ItemStack stack, int x, int y) {
        if (poseStackOrGraphics instanceof GuiGraphics gfx) {
            gfx.renderItem(stack, x, y);
        }
    }

    public static void renderGuiItemDecorations(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, net.minecraft.world.item.ItemStack stack, int x, int y) {
        if (poseStackOrGraphics instanceof GuiGraphics gfx) {
            gfx.renderItemDecorations(font, stack, x, y);
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
            protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                renderer.render(this, guiGraphics, mouseX, mouseY, partialTick);
            }
        };
    }

    public static void renderWidget(Object poseStackOrGraphics, net.minecraft.client.gui.components.AbstractWidget widget, int mouseX, int mouseY, float partialTick) {
        if (widget != null && poseStackOrGraphics instanceof GuiGraphics gg) {
            widget.render(gg, mouseX, mouseY, partialTick);
        }
    }

    public static net.minecraft.client.gui.components.AbstractWidget wrapCustomWidget(int x, int y, int width, int height, net.minecraft.network.chat.Component message, com.kingodogo.buildscape.client.screen.widget.ICustomWidget customWidget) {
        return new net.minecraft.client.gui.components.AbstractWidget(x, y, width, height, message) {
            @Override
            protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                customWidget.render(guiGraphics, mouseX, mouseY, partialTick);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (customWidget.mouseClicked(mouseX, mouseY, button)) return true;
                return super.mouseClicked(mouseX, mouseY, button);
            }

            @Override
            public boolean mouseReleased(double mouseX, double mouseY, int button) {
                if (customWidget.mouseReleased(mouseX, mouseY, button)) return true;
                return super.mouseReleased(mouseX, mouseY, button);
            }

            @Override
            public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
                if (customWidget.mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;
                return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
            }

            @Override
            public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
                if (customWidget.mouseScrolled(mouseX, mouseY, scrollY)) return true;
                return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            }

            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                if (customWidget.keyPressed(keyCode, scanCode, modifiers)) return true;
                return super.keyPressed(keyCode, scanCode, modifiers);
            }

            @Override
            public boolean charTyped(char codePoint, int modifiers) {
                if (customWidget.charTyped(codePoint, modifiers)) return true;
                return super.charTyped(codePoint, modifiers);
            }

            @Override
            protected void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput narrationElementOutput) {}
        };
    }
}
