package com.kingodogo.buildscape.client.screen.tabs.supporters;

import com.kingodogo.buildscape.client.screen.AbstractConfigTab;
import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;
import com.kingodogo.buildscape.client.screen.widget.SortToggleButton;
import com.kingodogo.buildscape.config.VerticalConfig;
import com.kingodogo.buildscape.variantengine.family.BlockFamily;
import com.kingodogo.buildscape.variantengine.registry.VariantRegistrar;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class VerticalStuffManagerTab extends AbstractConfigTab {
    // Top Left (Panel 1) - Allowed
    private EditBox p1SearchBox;
    private FlatToggleButton p1ItemBtn, p1ModBtn;
    private VerticalFamilyGridWidget p1FamiliesWidget;
    private VerticalModsSelectorWidget p1ModsWidget;
    // Top Right (Panel 2) - Blocked
    private EditBox p2SearchBox;
    private FlatToggleButton p2ItemBtn, p2ModBtn;
    private VerticalFamilyGridWidget p2FamiliesWidget;
    private VerticalModsSelectorWidget p2ModsWidget;
    // Bottom Left (Panel 3) - All Items
    private EditBox p3SearchBox;
    private VerticalFamilyGridWidget availableItemsWidget;
    private SortToggleButton p3InventoryButton;
    private SortToggleButton p3AllItemsButton;
    private SortToggleButton p3ModOnlyButton;
    private SortToggleButton.SortType p3SortMode = SortToggleButton.SortType.ALL_ITEMS;
    private int p3CurrentModIndex = 0;

    // Bottom Right (Panel 4) - All Mods
    private EditBox p4SearchBox;
    private VerticalModsSelectorWidget modsSelector;

    private boolean p1ShowMods = false;
    private boolean p2ShowMods = false;
    private List<String> availableModNamespaces = new ArrayList<>();

    private final Minecraft mc = Minecraft.getInstance();
    public VerticalStuffManagerTab(BuildScapeConfigScreen parent) {
        super(parent);
    }

    private void loadAvailableModNamespaces() {
        Set<String> namespaces = new HashSet<>();
        for (BlockFamily cf : VariantRegistrar.getDetectedFamilies()) {
            namespaces.add(cf.getBaseBlock().getRegistryName().getNamespace());
        }
        availableModNamespaces = new ArrayList<>(namespaces);
        availableModNamespaces.sort(String::compareTo);
    }

    private void onP3SortModeChanged(SortToggleButton.SortType type, boolean isCtrlDown) {
        p3InventoryButton.setSelected(false);
        p3AllItemsButton.setSelected(false);
        p3ModOnlyButton.setSelected(false);

        switch (type) {
            case INVENTORY:
                p3InventoryButton.setSelected(true);
                break;
            case ALL_ITEMS:
                p3AllItemsButton.setSelected(true);
                break;
            case MOD_ONLY:
                p3ModOnlyButton.setSelected(true);
                if (p3SortMode == SortToggleButton.SortType.MOD_ONLY) {
                    if (isCtrlDown) {
                        if (!availableModNamespaces.isEmpty()) {
                            p3CurrentModIndex--;
                            if (p3CurrentModIndex < 0) p3CurrentModIndex = availableModNamespaces.size() - 1;
                        }
                    } else {
                        if (!availableModNamespaces.isEmpty()) {
                            p3CurrentModIndex++;
                            if (p3CurrentModIndex >= availableModNamespaces.size()) p3CurrentModIndex = 0;
                        }
                    }
                } else {
                    if (!availableModNamespaces.isEmpty()) {
                        if (availableModNamespaces.contains("buildscape")) {
                            p3CurrentModIndex = availableModNamespaces.indexOf("buildscape");
                        } else {
                            p3CurrentModIndex = 0;
                        }
                    }
                }
                if (!availableModNamespaces.isEmpty() && availableItemsWidget != null) {
                    availableItemsWidget.setModNamespace(availableModNamespaces.get(p3CurrentModIndex));
                }
                break;
        }

        p3SortMode = type;
        if (availableItemsWidget != null) {
            availableItemsWidget.setSortMode(type);
        }
        updateSearchBoxes();
    }

    @Override
    public void init() {
        this.onClose(); // Clean up existing widgets if re-initializing
        loadAvailableModNamespaces();
        int width = parent.width;
        int height = parent.height;

        int col1X = parent.getContentX();
        int colW = parent.getContentWidth();
        int col2X = parent.getRightPanelX();
        int col2W = parent.getRightPanelWidth();

        int topY = parent.getContentY();
        int fullHeight = parent.getContentHeight();
        int middleGap = parent.getVerticalPanelGap(); // 0.5% consistent gap between panels

        int buttonSize = BuildScapeConfigScreen.getScaledButtonHeight();

        // ======================= BACK BUTTON =======================
        int backBtnWidth = BuildScapeConfigScreen.scaleSize(60);
        Button backButton = new com.kingodogo.buildscape.client.screen.widget.ScaledTextButton(
                col1X, topY - buttonSize - BuildScapeConfigScreen.scaleSize(2),
                backBtnWidth, buttonSize,
                new TextComponent("Back"),
                (btn) -> parent.setActiveTab(new com.kingodogo.buildscape.client.screen.WorldSettingsConfigTab(parent))
        );
        addTabWidget(backButton);

        // Warning tip label
        net.minecraft.client.gui.components.AbstractWidget tipLabel = new net.minecraft.client.gui.components.AbstractWidget(
                col1X + backBtnWidth + BuildScapeConfigScreen.scaleSize(10), topY - buttonSize - BuildScapeConfigScreen.scaleSize(2),
                BuildScapeConfigScreen.scaleSize(220), buttonSize,
                new TextComponent("To Apply Changes Restart the Game/Server")
        ) {
            @Override
            public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
                drawString(poseStack, Minecraft.getInstance().font, this.getMessage(), this.x, this.y + (this.height - 8) / 2, 0xFFFFFF00); // Yellow
            }
            @Override public void updateNarration(net.minecraft.client.gui.narration.NarrationElementOutput n) {}
        };
        addTabWidget(tipLabel);
        
        // Panels 3 & 4 should be higher than 1 & 2
        int topHeight = (int) ((fullHeight - middleGap) * 0.40);
        int bottomHeight = fullHeight - middleGap - topHeight;
        int bottomY = topY + topHeight + middleGap;

        int searchBoxHeight = BuildScapeConfigScreen.getScaledEditBoxHeight();
        int buttonSpacing = BuildScapeConfigScreen.scaleSize(5);
        int labelPadding = BuildScapeConfigScreen.scaleSize(5);

        int titleHeight = BuildScapeConfigScreen.scaleSize(16);
        int internalPaddingY = (int) (height * 0.005) + 2;
        int headerHeight = buttonSize + internalPaddingY * 2;

        // ======================= PANEL 1 (TOP LEFT): ALLOWED =======================
        int p1BtnY = topY + internalPaddingY;
        
        int totalBtnsW = (buttonSize * 2) + buttonSpacing;
        int p1StartX = col1X + colW - totalBtnsW - 2;

        p1ItemBtn = new FlatToggleButton(p1StartX, p1BtnY, buttonSize, buttonSize, new TextComponent("I"), () -> !p1ShowMods, (b) -> {
            if (p1ShowMods) {
                p1ShowMods = false;
                toggleVisibility();
            }
        });
        p1ModBtn = new FlatToggleButton(p1StartX + buttonSize + buttonSpacing, p1BtnY, buttonSize, buttonSize, new TextComponent("M"), () -> p1ShowMods, (b) -> {
            if (!p1ShowMods) {
                p1ShowMods = true;
                toggleVisibility();
            }
        });
        addTabWidget(p1ItemBtn); addTabWidget(p1ModBtn);

        int p1SearchX = col1X + labelPadding;
        p1SearchBox = new EditBox(mc.font, p1SearchX, p1BtnY, 10, searchBoxHeight, new TextComponent("Search"));
        p1SearchBox.setResponder(t -> { if(p1FamiliesWidget != null) p1FamiliesWidget.setFilter(t); if(p1ModsWidget != null) p1ModsWidget.setFilter(t); });
        addTabWidget(p1SearchBox);

        p1FamiliesWidget = new VerticalFamilyGridWidget(col1X, topY, colW, topHeight, new TextComponent(""),
            () -> VariantRegistrar.getDetectedFamilies().stream()
                .filter(f -> {
                    String id = f.getBaseBlock().getRegistryName().toString();
                    if (VerticalConfig.get().getAllowedFamilies().contains(id)) return true;
                    if (VerticalConfig.get().getBlocklistedFamilies().contains(id)) return false;
                    
                    return f.getVariants().entrySet().stream().anyMatch(e -> {
                        net.minecraft.world.level.block.Block b = e.getValue();
                        return b != null && b.getRegistryName() != null && b.getRegistryName().getNamespace().equals("buildscape") && 
                               (e.getKey() == com.kingodogo.buildscape.variantengine.builder.BlockShape.VERTICAL_SLAB || e.getKey() == com.kingodogo.buildscape.variantengine.builder.BlockShape.VERTICAL_STAIRS);
                    });
                }).collect(Collectors.toList()),
            (family) -> {
                String id = family.getBaseBlock().getRegistryName().toString();
                VerticalConfig.get().getAllowedFamilies().remove(id);
                if (!VerticalConfig.get().getBlocklistedFamilies().contains(id)) {
                    VerticalConfig.get().getBlocklistedFamilies().add(id);
                }
                VerticalConfig.get().save();
                refreshAll();
            },
            (family) -> 1, VerticalFamilyGridWidget.RenderMode.BASE_BLOCK);
        p1FamiliesWidget.setHeaderAreaHeight(headerHeight);
        addTabWidget(p1FamiliesWidget);

        p1ModsWidget = new VerticalModsSelectorWidget(col1X, topY, colW, topHeight, 
            () -> new ArrayList<>(VerticalConfig.get().getAllowedMods()),
            (mod) -> {
                VerticalConfig.get().getAllowedMods().remove(mod);
                VerticalConfig.get().save();
                refreshAll();
            },
            (mod) -> 1);
        p1ModsWidget.setHeaderAreaHeight(headerHeight);
        addTabWidget(p1ModsWidget);

        // ======================= PANEL 2 (TOP RIGHT): BLOCKED =======================
        int p2BtnY = topY + internalPaddingY;

        int p2StartX = col2X + col2W - totalBtnsW - 2;
        p2ItemBtn = new FlatToggleButton(p2StartX, p2BtnY, buttonSize, buttonSize, new TextComponent("I"), () -> !p2ShowMods, (b) -> {
            if (p2ShowMods) {
                p2ShowMods = false;
                toggleVisibility();
            }
        });
        p2ModBtn = new FlatToggleButton(p2StartX + buttonSize + buttonSpacing, p2BtnY, buttonSize, buttonSize, new TextComponent("M"), () -> p2ShowMods, (b) -> {
            if (!p2ShowMods) {
                p2ShowMods = true;
                toggleVisibility();
            }
        });
        addTabWidget(p2ItemBtn); addTabWidget(p2ModBtn);

        int p2SearchX = col2X + labelPadding;
        p2SearchBox = new EditBox(mc.font, p2SearchX, p2BtnY, 10, searchBoxHeight, new TextComponent("Search"));
        p2SearchBox.setResponder(t -> { if(p2FamiliesWidget != null) p2FamiliesWidget.setFilter(t); if(p2ModsWidget != null) p2ModsWidget.setFilter(t); });
        addTabWidget(p2SearchBox);

        p2FamiliesWidget = new VerticalFamilyGridWidget(col2X, topY, col2W, topHeight, new TextComponent(""),
            () -> VariantRegistrar.getDetectedFamilies().stream().filter(f -> VerticalConfig.get().getBlocklistedFamilies().contains(f.getBaseBlock().getRegistryName().toString())).collect(Collectors.toList()),
            (family) -> {
                String id = family.getBaseBlock().getRegistryName().toString();
                VerticalConfig.get().getBlocklistedFamilies().remove(id);
                VerticalConfig.get().save();
                refreshAll();
            },
            (family) -> 2, VerticalFamilyGridWidget.RenderMode.BASE_BLOCK);
        p2FamiliesWidget.setHeaderAreaHeight(headerHeight);
        addTabWidget(p2FamiliesWidget);

        p2ModsWidget = new VerticalModsSelectorWidget(col2X, topY, col2W, topHeight, 
            () -> new ArrayList<>(VerticalConfig.get().getBlocklistedMods()),
            (mod) -> {
                VerticalConfig.get().getBlocklistedMods().remove(mod);
                VerticalConfig.get().save();
                refreshAll();
            },
            (mod) -> 2);
        p2ModsWidget.setHeaderAreaHeight(headerHeight);
        addTabWidget(p2ModsWidget);

        // ======================= PANEL 3 (BOTTOM LEFT): ALL ITEMS =======================
        int p3BtnY = bottomY + internalPaddingY;

        int totalP3BtnsW = (buttonSize * 3) + (buttonSpacing * 2);
        int p3ButtonsStartX = col1X + colW - totalP3BtnsW - 2;

        p3InventoryButton = new SortToggleButton(
                p3ButtonsStartX, p3BtnY,
                buttonSize, buttonSize,
                SortToggleButton.SortType.INVENTORY,
                (type, ctrl) -> onP3SortModeChanged(type, ctrl));
        p3InventoryButton.setTooltip(java.util.Arrays.asList(
                new TextComponent("Filter By Inventory"),
                new TextComponent("Show items only from your inventory").withStyle(net.minecraft.ChatFormatting.GRAY)
        ));
        addTabWidget(p3InventoryButton);

        p3AllItemsButton = new SortToggleButton(
                p3ButtonsStartX + buttonSize + buttonSpacing, p3BtnY,
                buttonSize, buttonSize,
                SortToggleButton.SortType.ALL_ITEMS,
                (type, ctrl) -> onP3SortModeChanged(type, ctrl));
        p3AllItemsButton.setSelected(true);
        p3AllItemsButton.setTooltip(java.util.Arrays.asList(
                new TextComponent("Filter By All Items"),
                new TextComponent("Show all available items").withStyle(net.minecraft.ChatFormatting.GRAY)
        ));
        addTabWidget(p3AllItemsButton);

        p3ModOnlyButton = new SortToggleButton(
                p3ButtonsStartX + (buttonSize + buttonSpacing) * 2, p3BtnY,
                buttonSize, buttonSize,
                SortToggleButton.SortType.MOD_ONLY,
                (type, ctrl) -> onP3SortModeChanged(type, ctrl));
        p3ModOnlyButton.setTooltip(java.util.Arrays.asList(
                new TextComponent("Filter By Mod"),
                new TextComponent("Click to cycle next mod").withStyle(net.minecraft.ChatFormatting.GRAY),
                new TextComponent("Ctrl Click to cycle Previous mod").withStyle(net.minecraft.ChatFormatting.GRAY)
        ));
        addTabWidget(p3ModOnlyButton);

        int p3SearchX = col1X + labelPadding;
        p3SearchBox = new EditBox(mc.font, p3SearchX, p3BtnY, 10, searchBoxHeight, new TextComponent("Search"));
        p3SearchBox.setResponder(t -> { if(availableItemsWidget != null) availableItemsWidget.setFilter(t); });
        addTabWidget(p3SearchBox);

        availableItemsWidget = new VerticalFamilyGridWidget(col1X, bottomY, colW, bottomHeight, new TextComponent(""),
            () -> VariantRegistrar.getDetectedFamilies(),
            (family) -> {
                com.kingodogo.buildscape.config.VerticalConfig config = com.kingodogo.buildscape.config.VerticalConfig.get();
                String name = family.getBaseBlock().getRegistryName().toString();
                
                boolean isAllowed = config.getAllowedFamilies().contains(name);
                boolean isBlocked = config.getBlocklistedFamilies().contains(name);
                boolean isGrey = config.getExplicitlyGreyFamilies().contains(name);

                boolean isDefaultAllowed = family.getVariants().entrySet().stream().anyMatch(e -> {
                    net.minecraft.world.level.block.Block b = e.getValue();
                    return b != null && b.getRegistryName() != null && b.getRegistryName().getNamespace().equals("buildscape") && 
                           (e.getKey() == com.kingodogo.buildscape.variantengine.builder.BlockShape.VERTICAL_SLAB || e.getKey() == com.kingodogo.buildscape.variantengine.builder.BlockShape.VERTICAL_STAIRS);
                });

                if (isDefaultAllowed) {
                    if (isAllowed) {
                        config.getAllowedFamilies().remove(name);
                        config.getBlocklistedFamilies().add(name);
                    } else if (isBlocked) {
                        config.getBlocklistedFamilies().remove(name);
                        config.getExplicitlyGreyFamilies().add(name);
                    } else if (isGrey) {
                        config.getExplicitlyGreyFamilies().remove(name);
                    } else { // Default (Green) -> Blacklisted (Red)
                        config.getBlocklistedFamilies().add(name);
                    }
                } else {
                    if (isAllowed) {
                        config.getAllowedFamilies().remove(name);
                        config.getBlocklistedFamilies().add(name);
                    } else if (isBlocked) {
                        config.getBlocklistedFamilies().remove(name);
                        config.getExplicitlyGreyFamilies().add(name);
                    } else if (isGrey) {
                        config.getExplicitlyGreyFamilies().remove(name);
                    } else { // Default (Grey) -> Whitelisted (Green)
                        config.getAllowedFamilies().add(name);
                    }
                }
                config.save();
                refreshAll();
            },
            (family) -> {
                com.kingodogo.buildscape.config.VerticalConfig config = com.kingodogo.buildscape.config.VerticalConfig.get();
                String name = family.getBaseBlock().getRegistryName().toString();
                if (config.getAllowedFamilies().contains(name)) return 1;
                if (config.getBlocklistedFamilies().contains(name)) return 2;
                if (config.getExplicitlyGreyFamilies().contains(name)) return 0;
                
                boolean hasBuildscape = family.getVariants().entrySet().stream().anyMatch(e -> {
                    net.minecraft.world.level.block.Block b = e.getValue();
                    return b != null && b.getRegistryName() != null && b.getRegistryName().getNamespace().equals("buildscape") && 
                           (e.getKey() == com.kingodogo.buildscape.variantengine.builder.BlockShape.VERTICAL_SLAB || e.getKey() == com.kingodogo.buildscape.variantengine.builder.BlockShape.VERTICAL_STAIRS);
                });
                if (hasBuildscape) return 1;
                return 0;
            },
            VerticalFamilyGridWidget.RenderMode.BASE_BLOCK);
        availableItemsWidget.setHeaderAreaHeight(headerHeight);
        addTabWidget(availableItemsWidget);

        // ======================= PANEL 4 (BOTTOM RIGHT): ALL MODS =======================
        int p4BtnY = bottomY + internalPaddingY;

        int p4SearchX = col2X + labelPadding;
        p4SearchBox = new EditBox(mc.font, p4SearchX, p4BtnY, 10, searchBoxHeight, new TextComponent("Search"));
        p4SearchBox.setResponder(t -> { if(modsSelector != null) modsSelector.setFilter(t); });
        addTabWidget(p4SearchBox);

        modsSelector = new VerticalModsSelectorWidget(col2X, bottomY, col2W, bottomHeight, 
            () -> new ArrayList<>(availableModNamespaces),
            (mod) -> {
                VerticalConfig config = VerticalConfig.get();
                if (config.getAllowedMods().contains(mod)) {
                    config.getAllowedMods().remove(mod);
                    config.getBlocklistedMods().add(mod);
                } else if (config.getBlocklistedMods().contains(mod)) {
                    config.getBlocklistedMods().remove(mod);
                } else {
                    config.getAllowedMods().add(mod);
                }
                config.save();
                refreshAll();
            },
            (mod) -> {
                if (VerticalConfig.get().getAllowedMods().contains(mod)) return 1;
                if (VerticalConfig.get().getBlocklistedMods().contains(mod)) return 2;
                return 0;
            });
        modsSelector.setHeaderAreaHeight(headerHeight);
        addTabWidget(modsSelector);

        updateSearchBoxes();
        toggleVisibility();
        
        // Wait one tick to refresh, allowing the components to size correctly
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new Object() {
            @net.minecraftforge.eventbus.api.SubscribeEvent
            public void onTick(net.minecraftforge.event.TickEvent.ClientTickEvent event) {
                if (event.phase == net.minecraftforge.event.TickEvent.Phase.END) {
                    refreshAll();
                    net.minecraftforge.common.MinecraftForge.EVENT_BUS.unregister(this);
                }
            }
        });
    }

    private void toggleVisibility() {
        if (p1FamiliesWidget != null) p1FamiliesWidget.visible = !p1ShowMods;
        if (p1ModsWidget != null) p1ModsWidget.visible = p1ShowMods;
        
        if (p2FamiliesWidget != null) p2FamiliesWidget.visible = !p2ShowMods;
        if (p2ModsWidget != null) p2ModsWidget.visible = p2ShowMods;
    }

    private int cycleMod(int idx, boolean back) {
        if (availableModNamespaces.isEmpty()) return 0;
        if (back) return (idx - 1 + availableModNamespaces.size()) % availableModNamespaces.size();
        return (idx + 1) % availableModNamespaces.size();
    }

    private void refreshAll() {
        if (p1FamiliesWidget != null) p1FamiliesWidget.refresh();
        if (p1ModsWidget != null) p1ModsWidget.reload();
        if (p2FamiliesWidget != null) p2FamiliesWidget.refresh();
        if (p2ModsWidget != null) p2ModsWidget.reload();
        if (availableItemsWidget != null) availableItemsWidget.refresh();
        if (modsSelector != null) modsSelector.reload();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (p1FamiliesWidget != null && p1FamiliesWidget.visible && p1FamiliesWidget.mouseClicked(mouseX, mouseY, button)) return true;
        if (p1ModsWidget != null && p1ModsWidget.visible && p1ModsWidget.mouseClicked(mouseX, mouseY, button)) return true;
        if (p2FamiliesWidget != null && p2FamiliesWidget.visible && p2FamiliesWidget.mouseClicked(mouseX, mouseY, button)) return true;
        if (p2ModsWidget != null && p2ModsWidget.visible && p2ModsWidget.mouseClicked(mouseX, mouseY, button)) return true;
        if (availableItemsWidget != null && availableItemsWidget.mouseClicked(mouseX, mouseY, button)) return true;
        if (modsSelector != null && modsSelector.mouseClicked(mouseX, mouseY, button)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (p1FamiliesWidget != null && p1FamiliesWidget.visible && p1FamiliesWidget.mouseScrolled(mouseX, mouseY, delta)) return true;
        if (p1ModsWidget != null && p1ModsWidget.visible && p1ModsWidget.mouseScrolled(mouseX, mouseY, delta)) return true;
        if (p2FamiliesWidget != null && p2FamiliesWidget.visible && p2FamiliesWidget.mouseScrolled(mouseX, mouseY, delta)) return true;
        if (p2ModsWidget != null && p2ModsWidget.visible && p2ModsWidget.mouseScrolled(mouseX, mouseY, delta)) return true;
        if (availableItemsWidget != null && availableItemsWidget.mouseScrolled(mouseX, mouseY, delta)) return true;
        if (modsSelector != null && modsSelector.mouseScrolled(mouseX, mouseY, delta)) return true;
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        boolean handled = false;
        if (p1FamiliesWidget != null && p1FamiliesWidget.visible) handled |= p1FamiliesWidget.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        if (p1ModsWidget != null && p1ModsWidget.visible) handled |= p1ModsWidget.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        if (p2FamiliesWidget != null && p2FamiliesWidget.visible) handled |= p2FamiliesWidget.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        if (p2ModsWidget != null && p2ModsWidget.visible) handled |= p2ModsWidget.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        if (availableItemsWidget != null) handled |= availableItemsWidget.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        if (modsSelector != null) handled |= modsSelector.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        return handled || super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean handled = false;
        if (p1FamiliesWidget != null && p1FamiliesWidget.visible) handled |= p1FamiliesWidget.mouseReleased(mouseX, mouseY, button);
        if (p1ModsWidget != null && p1ModsWidget.visible) handled |= p1ModsWidget.mouseReleased(mouseX, mouseY, button);
        if (p2FamiliesWidget != null && p2FamiliesWidget.visible) handled |= p2FamiliesWidget.mouseReleased(mouseX, mouseY, button);
        if (p2ModsWidget != null && p2ModsWidget.visible) handled |= p2ModsWidget.mouseReleased(mouseX, mouseY, button);
        if (availableItemsWidget != null) handled |= availableItemsWidget.mouseReleased(mouseX, mouseY, button);
        if (modsSelector != null) handled |= modsSelector.mouseReleased(mouseX, mouseY, button);
        return handled || super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        if (p1FamiliesWidget != null && p1FamiliesWidget.visible) p1FamiliesWidget.render(poseStack, mouseX, mouseY, partialTick);
        if (p1ModsWidget != null && p1ModsWidget.visible) p1ModsWidget.render(poseStack, mouseX, mouseY, partialTick);
        if (p2FamiliesWidget != null && p2FamiliesWidget.visible) p2FamiliesWidget.render(poseStack, mouseX, mouseY, partialTick);
        if (p2ModsWidget != null && p2ModsWidget.visible) p2ModsWidget.render(poseStack, mouseX, mouseY, partialTick);
        if (availableItemsWidget != null) availableItemsWidget.render(poseStack, mouseX, mouseY, partialTick);
        if (modsSelector != null) modsSelector.render(poseStack, mouseX, mouseY, partialTick);

        poseStack.pushPose();
        poseStack.translate(0, 0, 400);

        int labelPadding = BuildScapeConfigScreen.scaleSize(5);
        float textScale = BuildScapeConfigScreen.getStandardTextScale();

        int topY = parent.getContentY();
        int fullHeight = parent.getContentHeight();
        int middleGap = parent.getVerticalPanelGap(); // 0.5% consistent gap between panels
        int topHeight = (int) ((fullHeight - middleGap) * 0.40);
        int bottomY = topY + topHeight + middleGap;
        
        int btnPaddingY = (int) (parent.height * 0.005) + 2 + BuildScapeConfigScreen.getScaledButtonHeight() / 2 - mc.font.lineHeight / 2 + 1;
        int titleY = topY + btnPaddingY;
        int bTitleY = bottomY + btnPaddingY;

        if (p1FamiliesWidget != null) renderScaledText(poseStack, new TranslatableComponent("buildscape.config.vertical.selected"), p1FamiliesWidget.x + 2, titleY, textScale);
        if (p2FamiliesWidget != null) renderScaledText(poseStack, new TranslatableComponent("buildscape.config.vertical.blocked"), p2FamiliesWidget.x + 2, titleY, textScale);
        
        Component p3Title;
        if (p3SortMode == SortToggleButton.SortType.MOD_ONLY && !availableModNamespaces.isEmpty()) {
            p3Title = new TextComponent(availableModNamespaces.get(Math.min(p3CurrentModIndex, Math.max(0, availableModNamespaces.size() - 1))) + " Items");
        } else if (p3SortMode == SortToggleButton.SortType.INVENTORY) {
            p3Title = new TextComponent("Inventory Items");
        } else {
            p3Title = new TranslatableComponent("buildscape.config.all_items");
        }
        if (availableItemsWidget != null) renderScaledText(poseStack, p3Title, availableItemsWidget.x + 2, bTitleY, textScale);

        if (modsSelector != null) renderScaledText(poseStack, new TextComponent("Mods"), modsSelector.x + 2, bTitleY, textScale);
        
        poseStack.popPose();
    }
    
    @Override
    public void renderTooltips(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        if (p1FamiliesWidget != null && p1FamiliesWidget.visible) p1FamiliesWidget.renderTooltip(poseStack, mouseX, mouseY);
        if (p2FamiliesWidget != null && p2FamiliesWidget.visible) p2FamiliesWidget.renderTooltip(poseStack, mouseX, mouseY);
        if (availableItemsWidget != null) availableItemsWidget.renderTooltip(poseStack, mouseX, mouseY);
        super.renderTooltips(poseStack, mouseX, mouseY, partialTick);
    }

    private void updateSearchBoxes() {
        if (p1SearchBox == null) return;
        
        float textScale = BuildScapeConfigScreen.getStandardTextScale();
        int labelPadding = BuildScapeConfigScreen.scaleSize(5);

        if (p1FamiliesWidget != null) {
            int p1TextWidth = (int)(mc.font.width(new TranslatableComponent("buildscape.config.vertical.selected")) * textScale);
            p1SearchBox.x = p1FamiliesWidget.x + 2 + p1TextWidth + labelPadding;
            p1SearchBox.setWidth(p1ItemBtn.x - p1SearchBox.x - labelPadding);
        }

        if (p2FamiliesWidget != null) {
            int p2TextWidth = (int)(mc.font.width(new TranslatableComponent("buildscape.config.vertical.blocked")) * textScale);
            p2SearchBox.x = p2FamiliesWidget.x + 2 + p2TextWidth + labelPadding;
            p2SearchBox.setWidth(p2ItemBtn.x - p2SearchBox.x - labelPadding);
        }

        if (availableItemsWidget != null) {
            Component p3Title;
            if (p3SortMode == SortToggleButton.SortType.MOD_ONLY && !availableModNamespaces.isEmpty()) {
                p3Title = new TextComponent(availableModNamespaces.get(Math.min(p3CurrentModIndex, Math.max(0, availableModNamespaces.size() - 1))) + " Items");
            } else if (p3SortMode == SortToggleButton.SortType.INVENTORY) {
                p3Title = new TextComponent("Inventory Items");
            } else {
                p3Title = new TranslatableComponent("buildscape.config.all_items");
            }
            int p3TextWidth = (int)(mc.font.width(p3Title) * textScale);
            p3SearchBox.x = availableItemsWidget.x + 2 + p3TextWidth + labelPadding;
            p3SearchBox.setWidth(p3InventoryButton.x - p3SearchBox.x - labelPadding);
        }

        if (modsSelector != null) {
            int p4TextWidth = (int)(mc.font.width(new TextComponent("Mods")) * textScale);
            p4SearchBox.x = modsSelector.x + 2 + p4TextWidth + labelPadding;
            p4SearchBox.setWidth((modsSelector.x + modsSelector.getWidth() - 2) - p4SearchBox.x);
        }
    }

    private void renderScaledText(PoseStack poseStack, Component text, int x, int y, float scale) {
        poseStack.pushPose();
        poseStack.translate(x, y, 0);
        poseStack.scale(scale, scale, 1.0f);
        mc.font.drawShadow(poseStack, text, 0, 0, 0xFFFFFFFF);
        poseStack.popPose();
    }

    private static class FlatToggleButton extends Button {
        private final java.util.function.Supplier<Boolean> selectedSupplier;

        public FlatToggleButton(int x, int y, int width, int height, Component title, java.util.function.Supplier<Boolean> selectedSupplier, OnPress onPress) {
            super(x, y, width, height, title, onPress);
            this.selectedSupplier = selectedSupplier;
        }

        @Override
        public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            boolean selected = selectedSupplier.get();
            boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            int bgColor = selected ? 0xFF1E1E1E : (hovered ? 0xFF2A2A2A : 0xFF151515);
            int borderColor = selected ? 0xFF00FF00 : (hovered ? 0xFF888888 : 0xFF444444);
            int textColor = selected ? 0xFF00FF00 : 0xFFFFFFFF;

            fill(poseStack, this.x, this.y, this.x + this.width, this.y + this.height, bgColor);
            
            fill(poseStack, this.x, this.y, this.x + this.width, this.y + 1, borderColor);
            fill(poseStack, this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, borderColor);
            fill(poseStack, this.x, this.y, this.x + 1, this.y + this.height, borderColor);
            fill(poseStack, this.x + this.width - 1, this.y, this.x + this.width, this.y + this.height, borderColor);

            drawCenteredString(poseStack, Minecraft.getInstance().font, getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, textColor);
        }
    }
}
