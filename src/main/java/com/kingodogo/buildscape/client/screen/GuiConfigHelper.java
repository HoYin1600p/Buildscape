package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.config.GuiConfigData;
import com.kingodogo.buildscape.config.GuiConfigManager;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import java.util.HashMap;
import java.util.Map;

public class GuiConfigHelper {
    private static final GuiConfigManager configManager = GuiConfigManager.get();
    
    public static GuiConfigData getConfig(String tabName) {
        return configManager.loadConfig(tabName);
    }
    
    public static void applyConfigToWidget(String elementId, AbstractWidget widget, GuiConfigData config,
                                          int contentX, int contentY, int screenWidth, int screenHeight) {
        if (widget == null || config == null) {
            return;
        }

        String tabName = "";
        GuiConfigData.ElementConfig defaultConfig = null;

        for (Map.Entry<String, Map<String, GuiConfigData.ElementConfig>> tabEntry : DEFAULT_CONFIGS.entrySet()) {
            if (tabEntry.getValue().containsKey(elementId)) {
                defaultConfig = tabEntry.getValue().get(elementId);
                break;
            }
        }

        GuiConfigData.ElementConfig elementConfig = (defaultConfig != null) ? defaultConfig : config.getElementConfig(elementId);
        if (elementConfig != null) {
            try {
                int x, y, width, height;

                if (elementConfig.percentX != null && screenWidth > 0) {
                    x = (int)(screenWidth * elementConfig.percentX);
                } else {
                    x = elementConfig.x + contentX;
                    if (screenWidth > 0) {
                        elementConfig.percentX = (double)x / screenWidth;
                    }
                }
                
                if (elementConfig.percentY != null && screenHeight > 0) {
                    y = (int)(screenHeight * elementConfig.percentY);
                } else {
                    y = elementConfig.y + contentY;
                    if (screenHeight > 0) {
                        elementConfig.percentY = (double)y / screenHeight;
                    }
                }

                if (elementConfig.percentWidth != null && screenWidth > 0) {
                    width = (int)(screenWidth * elementConfig.percentWidth);
                } else if (screenWidth > 0) {
                    if (config.screen != null && config.screen.width > 0 && config.screen.width != screenWidth) {
                        double scaleRatio = (double)screenWidth / config.screen.width;
                        width = (int)(elementConfig.width * scaleRatio);
                        elementConfig.percentWidth = (double)width / screenWidth;
                    } else {
                        width = elementConfig.width;
                        elementConfig.percentWidth = (double)width / screenWidth;
                    }
                } else {
                    width = elementConfig.width;
                }
                
                if (elementConfig.percentHeight != null && screenHeight > 0) {
                    height = (int)(screenHeight * elementConfig.percentHeight);
                } else if (screenHeight > 0) {
                    if (config.screen != null && config.screen.height > 0 && config.screen.height != screenHeight) {
                        double scaleRatio = (double)screenHeight / config.screen.height;
                        height = (int)(elementConfig.height * scaleRatio);
                        elementConfig.percentHeight = (double)height / screenHeight;
                    } else {
                        height = elementConfig.height;
                        elementConfig.percentHeight = (double)height / screenHeight;
                    }
                } else {
                    height = elementConfig.height;
                }

                widget.x = x;
                widget.y = y;

                int oldWidth = widget.getWidth();
                widget.setWidth(width);

                try {
                    try {
                        java.lang.reflect.Method setHeightMethod = widget.getClass().getMethod("setHeight", int.class);
                        setHeightMethod.invoke(widget, height);
                    } catch (NoSuchMethodException e) {
                        java.lang.reflect.Field heightField = AbstractWidget.class.getDeclaredField("height");
                        heightField.setAccessible(true);
                        int oldHeight = heightField.getInt(widget);
                        heightField.setInt(widget, height);

                        if (oldHeight != height) {
                            try {
                                java.lang.reflect.Method refreshMethod = widget.getClass().getMethod("refresh");
                                refreshMethod.invoke(widget);
                            } catch (Exception ex) {
                            }
                            try {
                                java.lang.reflect.Method updateMethod = widget.getClass().getMethod("updateDisplayEntries");
                                updateMethod.invoke(widget);
                            } catch (Exception ex) {
                            }
                        }
                    }
                } catch (Exception e) {
                    com.kingodogo.buildscape.BuildScape.getLogger().warn("Failed to set widget height for '{}': {}", elementId, e.getMessage());
                }
                
                widget.visible = elementConfig.visible;
            } catch (Exception e) {
                com.kingodogo.buildscape.BuildScape.getLogger().warn("Failed to apply config to widget '{}': {}", elementId, e.getMessage());
            }
        }
    }
    
    public static void applyConfigToWidget(String elementId, AbstractWidget widget, GuiConfigData config, int contentX, int contentY) {
        applyConfigToWidget(elementId, widget, config, contentX, contentY, 0, 0);
    }
    
    public static void applyConfigToWidget(String elementId, AbstractWidget widget, GuiConfigData config) {
        applyConfigToWidget(elementId, widget, config, 0, 0);
    }
    
    public static void applyConfigToEditBox(String elementId, EditBox editBox, GuiConfigData config,
                                           int contentX, int contentY, int screenWidth, int screenHeight) {
        if (editBox == null || config == null) {
            return;
        }
        
        GuiConfigData.ElementConfig elementConfig = config.getElementConfig(elementId);
        if (elementConfig != null) {
            int x, y, width, height;

            if (elementConfig.percentX != null && screenWidth > 0) {
                x = (int)(screenWidth * elementConfig.percentX);
            } else {
                x = elementConfig.x + contentX;
                if (screenWidth > 0) {
                    elementConfig.percentX = (double)x / screenWidth;
                }
            }
            
            if (elementConfig.percentY != null && screenHeight > 0) {
                y = (int)(screenHeight * elementConfig.percentY);
            } else {
                y = elementConfig.y + contentY;
                if (screenHeight > 0) {
                    elementConfig.percentY = (double)y / screenHeight;
                }
            }

            if (elementConfig.percentWidth != null && screenWidth > 0) {
                width = (int)(screenWidth * elementConfig.percentWidth);
            } else if (screenWidth > 0) {
                if (config.screen != null && config.screen.width > 0 && config.screen.width != screenWidth) {
                    double scaleRatio = (double)screenWidth / config.screen.width;
                    width = (int)(elementConfig.width * scaleRatio);
                    elementConfig.percentWidth = (double)width / screenWidth;
                } else {
                    width = elementConfig.width;
                    elementConfig.percentWidth = (double)width / screenWidth;
                }
            } else {
                width = elementConfig.width;
            }
            
            if (elementConfig.percentHeight != null && screenHeight > 0) {
                height = (int)(screenHeight * elementConfig.percentHeight);
            } else if (screenHeight > 0) {
                if (config.screen != null && config.screen.height > 0 && config.screen.height != screenHeight) {
                    double scaleRatio = (double)screenHeight / config.screen.height;
                    height = (int)(elementConfig.height * scaleRatio);
                    elementConfig.percentHeight = (double)height / screenHeight;
                } else {
                    height = elementConfig.height;
                    elementConfig.percentHeight = (double)height / screenHeight;
                }
            } else {
                height = elementConfig.height;
            }

            editBox.x = x;
            editBox.y = y;
            editBox.setWidth(width);
            editBox.visible = elementConfig.visible;

            try {
                java.lang.reflect.Field heightField = EditBox.class.getDeclaredField("height");
                heightField.setAccessible(true);
                heightField.setInt(editBox, height);
            } catch (Exception e) {
                com.kingodogo.buildscape.BuildScape.getLogger().debug("Failed to set EditBox height for '{}': {}", elementId, e.getMessage());
            }

            if (elementConfig.properties != null && elementConfig.properties.containsKey("searchTarget")) {
                String searchTarget = (String) elementConfig.properties.get("searchTarget");
            }
        }
    }
    
    public static void applyConfigToEditBox(String elementId, EditBox editBox, GuiConfigData config) {
        applyConfigToEditBox(elementId, editBox, config, 0, 0, 0, 0);
    }
    
    public static void applyConfigToEditBox(String elementId, EditBox editBox, GuiConfigData config, int contentX, int contentY) {
        applyConfigToEditBox(elementId, editBox, config, contentX, contentY, 0, 0);
    }
    
    public static void saveWidgetState(String tabName, String elementId, AbstractWidget widget) {
        if (widget == null) {
            return;
        }
        
        GuiConfigData config = configManager.loadConfig(tabName);
        GuiConfigData.ElementConfig elementConfig = config.getElementConfig(elementId, widget.x, widget.y, widget.getWidth(), 20);
        
        elementConfig.x = widget.x;
        elementConfig.y = widget.y;
        elementConfig.width = widget.getWidth();
        elementConfig.visible = widget.visible;
        
        try {
            java.lang.reflect.Field heightField = AbstractWidget.class.getDeclaredField("height");
            heightField.setAccessible(true);
            elementConfig.height = heightField.getInt(widget);
        } catch (Exception e) {
            elementConfig.height = 20;
        }
        
        config.setElementConfig(elementId, elementConfig);
        configManager.saveConfig(tabName, config);
    }
    
    private static final Map<String, Map<String, GuiConfigData.ElementConfig>> DEFAULT_CONFIGS = new HashMap<>();
    
    public static void registerWidgetDefaults(String tabName, Map<String, GuiConfigData.ElementConfig> widgetMap) {
        DEFAULT_CONFIGS.put(tabName.toLowerCase(), new HashMap<>(widgetMap));

        GuiConfigData config = configManager.loadConfig(tabName);

        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.getWindow() != null && mc.screen != null) {
            if (config.screen == null) {
                config.screen = new GuiConfigData.ScreenConfig();
            }
            config.screen.width = mc.screen.width;
            config.screen.height = mc.screen.height;
            config.screen.scale = (float)mc.getWindow().getGuiScale();
        }
        
        for (Map.Entry<String, GuiConfigData.ElementConfig> entry : widgetMap.entrySet()) {
            String elementId = entry.getKey();
            GuiConfigData.ElementConfig defaultConfig = entry.getValue();

            config.setElementConfig(elementId, new GuiConfigData.ElementConfig(defaultConfig));
        }

        configManager.saveConfig(tabName, config);
    }
    
    private static GuiConfigData.ElementConfig getDefaultConfig(String tabName, String elementId) {
        Map<String, GuiConfigData.ElementConfig> tabDefaults = DEFAULT_CONFIGS.get(tabName.toLowerCase());
        if (tabDefaults != null) {
            return tabDefaults.get(elementId);
        }
        return null;
    }
    
    public static void applyAllConfigs(String tabName, Map<String, AbstractWidget> widgetMap,
                                     int contentX, int contentY, int screenWidth, int screenHeight) {
        GuiConfigData config = getConfig(tabName);

        Map<String, GuiConfigData.ElementConfig> defaults = DEFAULT_CONFIGS.get(tabName.toLowerCase());

        if (config.screen == null) {
            config.screen = new GuiConfigData.ScreenConfig();
        }
        if (screenWidth > 0 && screenHeight > 0) {
            config.screen.width = screenWidth;
            config.screen.height = screenHeight;
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.getWindow() != null) {
                config.screen.scale = (float)mc.getWindow().getGuiScale();
            }
        }
        
        for (Map.Entry<String, AbstractWidget> entry : widgetMap.entrySet()) {
            String elementId = entry.getKey();
            AbstractWidget widget = entry.getValue();
            
            if (widget != null) {
                GuiConfigData.ElementConfig elementConfig = null;
                if (defaults != null && defaults.containsKey(elementId)) {
                    elementConfig = new GuiConfigData.ElementConfig(defaults.get(elementId));
                    if (screenWidth > 0 && screenHeight > 0) {
                        int absoluteX = elementConfig.x + contentX;
                        int absoluteY = elementConfig.y + contentY;
                        elementConfig.percentX = (double)absoluteX / screenWidth;
                        elementConfig.percentY = (double)absoluteY / screenHeight;
                        elementConfig.percentWidth = (double)elementConfig.width / screenWidth;
                        elementConfig.percentHeight = (double)elementConfig.height / screenHeight;
                    }
                } else {
                    elementConfig = config.getElementConfig(elementId);
                }

                if (elementConfig != null) {
                    applyConfigToWidgetWithConfig(elementId, widget, elementConfig, config, contentX, contentY, screenWidth, screenHeight);
                }
            }
        }
    }
    
    private static void applyConfigToWidgetWithConfig(String elementId, AbstractWidget widget,
                                                      GuiConfigData.ElementConfig elementConfig, GuiConfigData config,
                                                      int contentX, int contentY, int screenWidth, int screenHeight) {
        if (widget == null || elementConfig == null) {
            return;
        }
        
        try {
            int x, y, width, height;

            if (elementConfig.percentX != null && screenWidth > 0) {
                x = (int)(screenWidth * elementConfig.percentX);
            } else {
                x = elementConfig.x + contentX;
                if (screenWidth > 0) {
                    elementConfig.percentX = (double)x / screenWidth;
                }
            }
            
            if (elementConfig.percentY != null && screenHeight > 0) {
                y = (int)(screenHeight * elementConfig.percentY);
            } else {
                y = elementConfig.y + contentY;
                if (screenHeight > 0) {
                    elementConfig.percentY = (double)y / screenHeight;
                }
            }

            if (elementConfig.percentWidth != null && screenWidth > 0) {
                width = (int)(screenWidth * elementConfig.percentWidth);
            } else if (screenWidth > 0) {
                width = elementConfig.width;
                elementConfig.percentWidth = (double)width / screenWidth;
                width = (int)(screenWidth * elementConfig.percentWidth);
            } else {
                width = elementConfig.width;
            }
            
            if (elementConfig.percentHeight != null && screenHeight > 0) {
                height = (int)(screenHeight * elementConfig.percentHeight);
            } else if (screenHeight > 0) {
                height = elementConfig.height;
                elementConfig.percentHeight = (double)height / screenHeight;
                height = (int)(screenHeight * elementConfig.percentHeight);
            } else {
                height = elementConfig.height;
            }

            widget.x = x;
            widget.y = y;
            
            int oldWidth = widget.getWidth();
            widget.setWidth(width);

            try {
                try {
                    java.lang.reflect.Method setHeightMethod = widget.getClass().getMethod("setHeight", int.class);
                    setHeightMethod.invoke(widget, height);
                } catch (NoSuchMethodException e) {
                    java.lang.reflect.Field heightField = AbstractWidget.class.getDeclaredField("height");
                    heightField.setAccessible(true);
                    int oldHeight = heightField.getInt(widget);
                    heightField.setInt(widget, height);
                    
                    if (oldHeight != height) {
                        try {
                            java.lang.reflect.Method refreshMethod = widget.getClass().getMethod("refresh");
                            refreshMethod.invoke(widget);
                        } catch (Exception ex) {}
                        try {
                            java.lang.reflect.Method updateMethod = widget.getClass().getMethod("updateDisplayEntries");
                            updateMethod.invoke(widget);
                        } catch (Exception ex) {}
                    }
                }
            } catch (Exception e) {
                com.kingodogo.buildscape.BuildScape.getLogger().warn("Failed to set widget height for '{}': {}", elementId, e.getMessage());
            }
            
            widget.visible = elementConfig.visible;
        } catch (Exception e) {
            com.kingodogo.buildscape.BuildScape.getLogger().warn("Failed to apply config to widget '{}': {}", elementId, e.getMessage());
        }
    }
    
    public static void applyAllConfigs(String tabName, Map<String, AbstractWidget> widgetMap, int contentX, int contentY) {
        applyAllConfigs(tabName, widgetMap, contentX, contentY, 0, 0);
    }
    
    public static void applyAllConfigs(String tabName, Map<String, AbstractWidget> widgetMap) {
        applyAllConfigs(tabName, widgetMap, 0, 0, 0, 0);
    }
    
    public static void applyAllEditBoxConfigs(String tabName, Map<String, EditBox> editBoxMap,
                                             int contentX, int contentY, int screenWidth, int screenHeight) {
        GuiConfigData config = getConfig(tabName);

        Map<String, GuiConfigData.ElementConfig> defaults = DEFAULT_CONFIGS.get(tabName.toLowerCase());

        if (config.screen == null) {
            config.screen = new GuiConfigData.ScreenConfig();
        }
        if (screenWidth > 0 && screenHeight > 0) {
            config.screen.width = screenWidth;
            config.screen.height = screenHeight;
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.getWindow() != null) {
                config.screen.scale = (float)mc.getWindow().getGuiScale();
            }
        }
        
        for (Map.Entry<String, EditBox> entry : editBoxMap.entrySet()) {
            String elementId = entry.getKey();
            EditBox editBox = entry.getValue();
            
            if (editBox != null) {
                GuiConfigData.ElementConfig elementConfig = null;
                if (defaults != null && defaults.containsKey(elementId)) {
                    elementConfig = new GuiConfigData.ElementConfig(defaults.get(elementId));
                    if (screenWidth > 0 && screenHeight > 0) {
                        int absoluteX = elementConfig.x + contentX;
                        int absoluteY = elementConfig.y + contentY;
                        elementConfig.percentX = (double)absoluteX / screenWidth;
                        elementConfig.percentY = (double)absoluteY / screenHeight;
                        elementConfig.percentWidth = (double)elementConfig.width / screenWidth;
                        elementConfig.percentHeight = (double)elementConfig.height / screenHeight;
                    }
                } else {
                    elementConfig = config.getElementConfig(elementId);
                }

                if (elementConfig != null) {
                    applyConfigToEditBoxWithConfig(elementId, editBox, elementConfig, config, contentX, contentY, screenWidth, screenHeight);
                }
            }
        }
    }
    
    private static void applyConfigToEditBoxWithConfig(String elementId, EditBox editBox,
                                                      GuiConfigData.ElementConfig elementConfig, GuiConfigData config,
                                                      int contentX, int contentY, int screenWidth, int screenHeight) {
        if (editBox == null || elementConfig == null) {
            return;
        }
        
        int x, y, width, height;

        if (elementConfig.percentX != null && screenWidth > 0) {
            x = (int)(screenWidth * elementConfig.percentX);
        } else {
            x = elementConfig.x + contentX;
            if (screenWidth > 0) {
                elementConfig.percentX = (double)x / screenWidth;
            }
        }
        
        if (elementConfig.percentY != null && screenHeight > 0) {
            y = (int)(screenHeight * elementConfig.percentY);
        } else {
            y = elementConfig.y + contentY;
            if (screenHeight > 0) {
                elementConfig.percentY = (double)y / screenHeight;
            }
        }

        if (elementConfig.percentWidth != null && screenWidth > 0) {
            width = (int)(screenWidth * elementConfig.percentWidth);
        } else if (screenWidth > 0) {
            width = elementConfig.width;
            elementConfig.percentWidth = (double)width / screenWidth;
            width = (int)(screenWidth * elementConfig.percentWidth);
        } else {
            width = elementConfig.width;
        }
        
        if (elementConfig.percentHeight != null && screenHeight > 0) {
            height = (int)(screenHeight * elementConfig.percentHeight);
        } else if (screenHeight > 0) {
            height = elementConfig.height;
            elementConfig.percentHeight = (double)height / screenHeight;
            height = (int)(screenHeight * elementConfig.percentHeight);
        } else {
            height = elementConfig.height;
        }
        
        editBox.x = x;
        editBox.y = y;
        editBox.setWidth(width);
        editBox.visible = elementConfig.visible;
        
        try {
            java.lang.reflect.Field heightField = EditBox.class.getDeclaredField("height");
            heightField.setAccessible(true);
            heightField.setInt(editBox, height);
        } catch (Exception e) {
            com.kingodogo.buildscape.BuildScape.getLogger().debug("Failed to set EditBox height for '{}': {}", elementId, e.getMessage());
        }
    }
    
    public static void applyAllEditBoxConfigs(String tabName, Map<String, EditBox> editBoxMap, int contentX, int contentY) {
        applyAllEditBoxConfigs(tabName, editBoxMap, contentX, contentY, 0, 0);
    }
    
    public static void applyAllEditBoxConfigs(String tabName, Map<String, EditBox> editBoxMap) {
        applyAllEditBoxConfigs(tabName, editBoxMap, 0, 0);
    }
}

