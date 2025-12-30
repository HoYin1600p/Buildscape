package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.config.GuiConfigData;
import com.kingodogo.buildscape.config.GuiConfigManager;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for applying GUI configurations to widgets and screens.
 * Provides utilities to load and apply saved GUI layouts.
 */
public class GuiConfigHelper {
    private static final GuiConfigManager configManager = GuiConfigManager.get();
    
    /**
     * Get the GUI configuration for a tab
     * @param tabName The name of the tab
     * @return The GUI configuration data
     */
    public static GuiConfigData getConfig(String tabName) {
        return configManager.loadConfig(tabName);
    }
    
    /**
     * Apply configuration to a widget
     * @param elementId The ID of the element (should match what's in the config)
     * @param widget The widget to apply configuration to
     * @param config The GUI configuration data
     * @param contentX X offset of content area (0 if positions are already absolute)
     * @param contentY Y offset of content area (0 if positions are already absolute)
     * @param screenWidth Screen width for percentage calculations (0 to use absolute values)
     * @param screenHeight Screen height for percentage calculations (0 to use absolute values)
     */
    public static void applyConfigToWidget(String elementId, AbstractWidget widget, GuiConfigData config, 
                                          int contentX, int contentY, int screenWidth, int screenHeight) {
        if (widget == null || config == null) {
            return;
        }
        
        // ALWAYS use Java code defaults for sizing - ignore saved config file sizes
        // Get the tab name from config (we need to pass it, but for now use elementId context)
        String tabName = ""; // We'll need to pass this, but for now use defaults if available
        GuiConfigData.ElementConfig defaultConfig = null;
        
        // Try to find default config from registered defaults
        for (Map.Entry<String, Map<String, GuiConfigData.ElementConfig>> tabEntry : DEFAULT_CONFIGS.entrySet()) {
            if (tabEntry.getValue().containsKey(elementId)) {
                defaultConfig = tabEntry.getValue().get(elementId);
                break;
            }
        }
        
        // Use default config if available, otherwise fall back to saved config
        GuiConfigData.ElementConfig elementConfig = (defaultConfig != null) ? defaultConfig : config.getElementConfig(elementId);
        if (elementConfig != null) {
            // Apply position and size
            try {
                int x, y, width, height;
                
                // Use percentage-based values if available, otherwise use absolute values
                // Percentages are relative to screen dimensions (absolute positions)
                // Absolute values are content-relative and need content offset added
                if (elementConfig.percentX != null && screenWidth > 0) {
                    // Percentage is already absolute screen position, no need to add contentX
                    x = (int)(screenWidth * elementConfig.percentX);
                } else {
                    // Absolute values are content-relative, so add content offset
                    x = elementConfig.x + contentX;
                    // Calculate and save percentage for future scaling if we have screen dimensions
                    if (screenWidth > 0) {
                        elementConfig.percentX = (double)x / screenWidth;
                    }
                }
                
                if (elementConfig.percentY != null && screenHeight > 0) {
                    // Percentage is already absolute screen position, no need to add contentY
                    y = (int)(screenHeight * elementConfig.percentY);
                } else {
                    // Absolute values are content-relative, so add content offset
                    y = elementConfig.y + contentY;
                    // Calculate and save percentage for future scaling if we have screen dimensions
                    if (screenHeight > 0) {
                        elementConfig.percentY = (double)y / screenHeight;
                    }
                }
                
                // ALWAYS use percentages for sizes - this ensures all components auto-resize properly
                // If percentages don't exist, calculate them from absolute values
                if (elementConfig.percentWidth != null && screenWidth > 0) {
                    // Use saved percentage directly
                    width = (int)(screenWidth * elementConfig.percentWidth);
                } else if (screenWidth > 0) {
                    // No percentage - need to calculate it
                    // If we have saved screen dimensions, scale proportionally
                    if (config.screen != null && config.screen.width > 0 && config.screen.width != screenWidth) {
                        // Scale the absolute width based on screen size ratio
                        double scaleRatio = (double)screenWidth / config.screen.width;
                        width = (int)(elementConfig.width * scaleRatio);
                        // Calculate and save percentage for future use
                        elementConfig.percentWidth = (double)width / screenWidth;
                    } else {
                        // No saved screen size or same size - use absolute value and calculate percentage
                        width = elementConfig.width;
                        elementConfig.percentWidth = (double)width / screenWidth;
                    }
                } else {
                    // No screen width available - fallback to absolute
                    width = elementConfig.width;
                }
                
                if (elementConfig.percentHeight != null && screenHeight > 0) {
                    // Use saved percentage directly
                    height = (int)(screenHeight * elementConfig.percentHeight);
                } else if (screenHeight > 0) {
                    // No percentage - need to calculate it
                    // If we have saved screen dimensions, scale proportionally
                    if (config.screen != null && config.screen.height > 0 && config.screen.height != screenHeight) {
                        // Scale the absolute height based on screen size ratio
                        double scaleRatio = (double)screenHeight / config.screen.height;
                        height = (int)(elementConfig.height * scaleRatio);
                        // Calculate and save percentage for future use
                        elementConfig.percentHeight = (double)height / screenHeight;
                    } else {
                        // No saved screen size or same size - use absolute value and calculate percentage
                        height = elementConfig.height;
                        elementConfig.percentHeight = (double)height / screenHeight;
                    }
                } else {
                    // No screen height available - fallback to absolute
                    height = elementConfig.height;
                }
                
                // Apply position and size - FORCE the widget to update
                widget.x = x;
                widget.y = y;
                
                // Set width - this triggers recalculation in widgets that override setWidth
                int oldWidth = widget.getWidth();
                widget.setWidth(width);
                
                // Set height using reflection or widget's setHeight method if available
                try {
                    // First try to use widget's setHeight method if it exists (for custom widgets)
                    try {
                        java.lang.reflect.Method setHeightMethod = widget.getClass().getMethod("setHeight", int.class);
                        setHeightMethod.invoke(widget, height);
                    } catch (NoSuchMethodException e) {
                        // No setHeight method, use reflection directly
                        java.lang.reflect.Field heightField = AbstractWidget.class.getDeclaredField("height");
                        heightField.setAccessible(true);
                        int oldHeight = heightField.getInt(widget);
                        heightField.setInt(widget, height);
                        
                        // If height changed, try to trigger widget recalculation
                        if (oldHeight != height) {
                            // For custom widgets, try to call a refresh/update method if it exists
                            try {
                                java.lang.reflect.Method refreshMethod = widget.getClass().getMethod("refresh");
                                refreshMethod.invoke(widget);
                            } catch (Exception ex) {
                                // No refresh method, that's okay
                            }
                            try {
                                java.lang.reflect.Method updateMethod = widget.getClass().getMethod("updateDisplayEntries");
                                updateMethod.invoke(widget);
                            } catch (Exception ex) {
                                // No updateDisplayEntries method, that's okay
                            }
                        }
                    }
                } catch (Exception e) {
                    // If reflection fails, try alternative methods
                    com.kingodogo.buildscape.BuildScape.getLogger().warn("Failed to set widget height for '{}': {}", elementId, e.getMessage());
                }
                
                widget.visible = elementConfig.visible;
            } catch (Exception e) {
                com.kingodogo.buildscape.BuildScape.getLogger().warn("Failed to apply config to widget '{}': {}", elementId, e.getMessage());
            }
        }
    }
    
    /**
     * Apply configuration to a widget (backwards compatibility - uses absolute values)
     * @param elementId The ID of the element (should match what's in the config)
     * @param widget The widget to apply configuration to
     * @param config The GUI configuration data
     * @param contentX X offset of content area (0 if positions are already absolute)
     * @param contentY Y offset of content area (0 if positions are already absolute)
     */
    public static void applyConfigToWidget(String elementId, AbstractWidget widget, GuiConfigData config, int contentX, int contentY) {
        applyConfigToWidget(elementId, widget, config, contentX, contentY, 0, 0);
    }
    
    /**
     * Apply configuration to a widget (backwards compatibility - assumes content offset 0,0)
     * @param elementId The ID of the element (should match what's in the config)
     * @param widget The widget to apply configuration to
     * @param config The GUI configuration data
     */
    public static void applyConfigToWidget(String elementId, AbstractWidget widget, GuiConfigData config) {
        applyConfigToWidget(elementId, widget, config, 0, 0);
    }
    
    /**
     * Apply configuration to an EditBox
     * @param elementId The ID of the element
     * @param editBox The EditBox to apply configuration to
     * @param config The GUI configuration data
     * @param contentX X offset of content area (0 if positions are already absolute)
     * @param contentY Y offset of content area (0 if positions are already absolute)
     * @param screenWidth Screen width for percentage calculations (0 to use absolute values)
     * @param screenHeight Screen height for percentage calculations (0 to use absolute values)
     */
    public static void applyConfigToEditBox(String elementId, EditBox editBox, GuiConfigData config, 
                                           int contentX, int contentY, int screenWidth, int screenHeight) {
        if (editBox == null || config == null) {
            return;
        }
        
        GuiConfigData.ElementConfig elementConfig = config.getElementConfig(elementId);
        if (elementConfig != null) {
            int x, y, width, height;
            
            // Use percentage-based values if available, otherwise use absolute values
            // Percentages are relative to screen dimensions (absolute positions)
            // Absolute values are content-relative and need content offset added
            if (elementConfig.percentX != null && screenWidth > 0) {
                // Percentage is already absolute screen position, no need to add contentX
                x = (int)(screenWidth * elementConfig.percentX);
            } else {
                // Absolute values are content-relative, so add content offset
                x = elementConfig.x + contentX;
                // Calculate and save percentage for future scaling if we have screen dimensions
                if (screenWidth > 0) {
                    elementConfig.percentX = (double)x / screenWidth;
                }
            }
            
            if (elementConfig.percentY != null && screenHeight > 0) {
                // Percentage is already absolute screen position, no need to add contentY
                y = (int)(screenHeight * elementConfig.percentY);
            } else {
                // Absolute values are content-relative, so add content offset
                y = elementConfig.y + contentY;
                // Calculate and save percentage for future scaling if we have screen dimensions
                if (screenHeight > 0) {
                    elementConfig.percentY = (double)y / screenHeight;
                }
            }
            
            // ALWAYS use percentages for sizes - this ensures all components auto-resize properly
            // If percentages don't exist, calculate them from absolute values
            if (elementConfig.percentWidth != null && screenWidth > 0) {
                // Use saved percentage directly
                width = (int)(screenWidth * elementConfig.percentWidth);
            } else if (screenWidth > 0) {
                // No percentage - need to calculate it
                // If we have saved screen dimensions, scale proportionally
                if (config.screen != null && config.screen.width > 0 && config.screen.width != screenWidth) {
                    // Scale the absolute width based on screen size ratio
                    double scaleRatio = (double)screenWidth / config.screen.width;
                    width = (int)(elementConfig.width * scaleRatio);
                    // Calculate and save percentage for future use
                    elementConfig.percentWidth = (double)width / screenWidth;
                } else {
                    // No saved screen size or same size - use absolute value and calculate percentage
                    width = elementConfig.width;
                    elementConfig.percentWidth = (double)width / screenWidth;
                }
            } else {
                // No screen width available - fallback to absolute
                width = elementConfig.width;
            }
            
            if (elementConfig.percentHeight != null && screenHeight > 0) {
                // Use saved percentage directly
                height = (int)(screenHeight * elementConfig.percentHeight);
            } else if (screenHeight > 0) {
                // No percentage - need to calculate it
                // If we have saved screen dimensions, scale proportionally
                if (config.screen != null && config.screen.height > 0 && config.screen.height != screenHeight) {
                    // Scale the absolute height based on screen size ratio
                    double scaleRatio = (double)screenHeight / config.screen.height;
                    height = (int)(elementConfig.height * scaleRatio);
                    // Calculate and save percentage for future use
                    elementConfig.percentHeight = (double)height / screenHeight;
                } else {
                    // No saved screen size or same size - use absolute value and calculate percentage
                    height = elementConfig.height;
                    elementConfig.percentHeight = (double)height / screenHeight;
                }
            } else {
                // No screen height available - fallback to absolute
                height = elementConfig.height;
            }
            
            // Apply position and size - FORCE the EditBox to update
            editBox.x = x;
            editBox.y = y;
            editBox.setWidth(width);
            editBox.visible = elementConfig.visible;
            
            // Set height using reflection
            try {
                java.lang.reflect.Field heightField = EditBox.class.getDeclaredField("height");
                heightField.setAccessible(true);
                heightField.setInt(editBox, height);
            } catch (Exception e) {
                // Height setting might not be critical for EditBox, but log it
                com.kingodogo.buildscape.BuildScape.getLogger().debug("Failed to set EditBox height for '{}': {}", elementId, e.getMessage());
            }
            
            // Apply search target if specified in properties
            if (elementConfig.properties != null && elementConfig.properties.containsKey("searchTarget")) {
                String searchTarget = (String) elementConfig.properties.get("searchTarget");
                // Store the target ID in the EditBox's responder or a custom field
                // This will be handled by the tab when setting up the responder
            }
        }
    }
    
    /**
     * Apply configuration to an EditBox (backwards compatibility - assumes content offset 0,0 and no percentage calculations)
     * @param elementId The ID of the element
     * @param editBox The EditBox to apply configuration to
     * @param config The GUI configuration data
     */
    public static void applyConfigToEditBox(String elementId, EditBox editBox, GuiConfigData config) {
        applyConfigToEditBox(elementId, editBox, config, 0, 0, 0, 0);
    }
    
    /**
     * Apply configuration to an EditBox (backwards compatibility - assumes no percentage calculations)
     * @param elementId The ID of the element
     * @param editBox The EditBox to apply configuration to
     * @param config The GUI configuration data
     * @param contentX X offset of content area (0 if positions are already absolute)
     * @param contentY Y offset of content area (0 if positions are already absolute)
     */
    public static void applyConfigToEditBox(String elementId, EditBox editBox, GuiConfigData config, int contentX, int contentY) {
        applyConfigToEditBox(elementId, editBox, config, contentX, contentY, 0, 0);
    }
    
    /**
     * Save the current state of a widget to the configuration
     * @param tabName The name of the tab
     * @param elementId The ID of the element
     * @param widget The widget to save the state from
     */
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
            elementConfig.height = 20; // Default height
        }
        
        config.setElementConfig(elementId, elementConfig);
        configManager.saveConfig(tabName, config);
    }
    
    // Store defaults in memory - these are the Java code defaults, not from saved files
    private static final Map<String, Map<String, GuiConfigData.ElementConfig>> DEFAULT_CONFIGS = new HashMap<>();
    
    /**
     * Register default configurations for widgets when initializing a tab.
     * This should be called in the tab's init() method to register all widgets
     * with their initial positions so they can be edited later.
     * 
     * @param tabName The name of the tab
     * @param widgetMap Map of element IDs to their widgets and initial configs
     */
    public static void registerWidgetDefaults(String tabName, Map<String, GuiConfigData.ElementConfig> widgetMap) {
        // Store defaults in memory - these are the source of truth from Java code
        DEFAULT_CONFIGS.put(tabName.toLowerCase(), new HashMap<>(widgetMap));
        
        // Also update the saved config for editor compatibility, but we'll use defaults for sizing
        GuiConfigData config = configManager.loadConfig(tabName);
        
        // Update screen dimensions if available from Minecraft instance
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
            
            // Always update defaults in saved config (for editor), but we use in-memory defaults for sizing
            config.setElementConfig(elementId, new GuiConfigData.ElementConfig(defaultConfig));
        }
        
        // Save the updated config with any new widgets
        configManager.saveConfig(tabName, config);
    }
    
    /**
     * Get the Java code default config for an element (not from saved file)
     */
    private static GuiConfigData.ElementConfig getDefaultConfig(String tabName, String elementId) {
        Map<String, GuiConfigData.ElementConfig> tabDefaults = DEFAULT_CONFIGS.get(tabName.toLowerCase());
        if (tabDefaults != null) {
            return tabDefaults.get(elementId);
        }
        return null;
    }
    
    /**
     * Apply all saved configurations to widgets in a tab.
     * This is a convenience method that applies configs to multiple widgets at once.
     * 
     * @param tabName The name of the tab
     * @param widgetMap Map of element IDs to their AbstractWidget instances
     * @param contentX X offset of content area (0 if positions are already absolute)
     * @param contentY Y offset of content area (0 if positions are already absolute)
     * @param screenWidth Screen width for percentage calculations (0 to use absolute values)
     * @param screenHeight Screen height for percentage calculations (0 to use absolute values)
     */
    public static void applyAllConfigs(String tabName, Map<String, AbstractWidget> widgetMap, 
                                     int contentX, int contentY, int screenWidth, int screenHeight) {
        GuiConfigData config = getConfig(tabName);
        
        // Get defaults from Java code (in-memory, not from saved file)
        Map<String, GuiConfigData.ElementConfig> defaults = DEFAULT_CONFIGS.get(tabName.toLowerCase());
        
        // ALWAYS update screen dimensions to current values for proper scaling
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
                // Use default config from Java code if available, otherwise use saved config
                GuiConfigData.ElementConfig elementConfig = null;
                if (defaults != null && defaults.containsKey(elementId)) {
                    // Use Java code default - recalculate percentages for current screen size
                    elementConfig = new GuiConfigData.ElementConfig(defaults.get(elementId));
                    // Recalculate percentages based on current screen size
                    if (screenWidth > 0 && screenHeight > 0) {
                        int absoluteX = elementConfig.x + contentX;
                        int absoluteY = elementConfig.y + contentY;
                        elementConfig.percentX = (double)absoluteX / screenWidth;
                        elementConfig.percentY = (double)absoluteY / screenHeight;
                        elementConfig.percentWidth = (double)elementConfig.width / screenWidth;
                        elementConfig.percentHeight = (double)elementConfig.height / screenHeight;
                    }
                } else {
                    // Fall back to saved config
                    elementConfig = config.getElementConfig(elementId);
                }
                
                // Apply using the config (either default or saved)
                if (elementConfig != null) {
                    applyConfigToWidgetWithConfig(elementId, widget, elementConfig, config, contentX, contentY, screenWidth, screenHeight);
                }
            }
        }
    }
    
    /**
     * Apply a specific element config to a widget - ALWAYS uses Java defaults for sizing
     */
    private static void applyConfigToWidgetWithConfig(String elementId, AbstractWidget widget, 
                                                      GuiConfigData.ElementConfig elementConfig, GuiConfigData config,
                                                      int contentX, int contentY, int screenWidth, int screenHeight) {
        if (widget == null || elementConfig == null) {
            return;
        }
        
        try {
            int x, y, width, height;
            
            // For positions: use saved config if it has percentages, otherwise use defaults
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
            
            // For sizes: ALWAYS use percentages from defaults, recalculate for current screen
            if (elementConfig.percentWidth != null && screenWidth > 0) {
                width = (int)(screenWidth * elementConfig.percentWidth);
            } else if (screenWidth > 0) {
                // Calculate from absolute value
                width = elementConfig.width;
                elementConfig.percentWidth = (double)width / screenWidth;
                width = (int)(screenWidth * elementConfig.percentWidth);
            } else {
                width = elementConfig.width;
            }
            
            if (elementConfig.percentHeight != null && screenHeight > 0) {
                height = (int)(screenHeight * elementConfig.percentHeight);
            } else if (screenHeight > 0) {
                // Calculate from absolute value
                height = elementConfig.height;
                elementConfig.percentHeight = (double)height / screenHeight;
                height = (int)(screenHeight * elementConfig.percentHeight);
            } else {
                height = elementConfig.height;
            }
            
            // Apply position and size
            widget.x = x;
            widget.y = y;
            
            int oldWidth = widget.getWidth();
            widget.setWidth(width);
            
            // Set height using reflection or widget's setHeight method
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
    
    /**
     * Apply all saved configurations to widgets in a tab (backwards compatibility).
     * This is a convenience method that applies configs to multiple widgets at once.
     * 
     * @param tabName The name of the tab
     * @param widgetMap Map of element IDs to their AbstractWidget instances
     * @param contentX X offset of content area (0 if positions are already absolute)
     * @param contentY Y offset of content area (0 if positions are already absolute)
     */
    public static void applyAllConfigs(String tabName, Map<String, AbstractWidget> widgetMap, int contentX, int contentY) {
        applyAllConfigs(tabName, widgetMap, contentX, contentY, 0, 0);
    }
    
    /**
     * Apply all saved configurations to widgets in a tab (backwards compatibility).
     * This is a convenience method that applies configs to multiple widgets at once.
     * 
     * @param tabName The name of the tab
     * @param widgetMap Map of element IDs to their AbstractWidget instances
     */
    public static void applyAllConfigs(String tabName, Map<String, AbstractWidget> widgetMap) {
        applyAllConfigs(tabName, widgetMap, 0, 0, 0, 0);
    }
    
    /**
     * Apply saved configurations to EditBoxes in a tab.
     * 
     * @param tabName The name of the tab
     * @param editBoxMap Map of element IDs to their EditBox instances
     * @param contentX X offset of content area (0 if positions are already absolute)
     * @param contentY Y offset of content area (0 if positions are already absolute)
     * @param screenWidth Screen width for percentage calculations (0 to use absolute values)
     * @param screenHeight Screen height for percentage calculations (0 to use absolute values)
     */
    public static void applyAllEditBoxConfigs(String tabName, Map<String, EditBox> editBoxMap, 
                                             int contentX, int contentY, int screenWidth, int screenHeight) {
        GuiConfigData config = getConfig(tabName);
        
        // Get defaults from Java code (in-memory, not from saved file)
        Map<String, GuiConfigData.ElementConfig> defaults = DEFAULT_CONFIGS.get(tabName.toLowerCase());
        
        // ALWAYS update screen dimensions to current values for proper scaling
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
                // Use default config from Java code if available, otherwise use saved config
                GuiConfigData.ElementConfig elementConfig = null;
                if (defaults != null && defaults.containsKey(elementId)) {
                    // Use Java code default - recalculate percentages for current screen size
                    elementConfig = new GuiConfigData.ElementConfig(defaults.get(elementId));
                    // Recalculate percentages based on current screen size
                    if (screenWidth > 0 && screenHeight > 0) {
                        int absoluteX = elementConfig.x + contentX;
                        int absoluteY = elementConfig.y + contentY;
                        elementConfig.percentX = (double)absoluteX / screenWidth;
                        elementConfig.percentY = (double)absoluteY / screenHeight;
                        elementConfig.percentWidth = (double)elementConfig.width / screenWidth;
                        elementConfig.percentHeight = (double)elementConfig.height / screenHeight;
                    }
                } else {
                    // Fall back to saved config
                    elementConfig = config.getElementConfig(elementId);
                }
                
                // Apply using the config (either default or saved)
                if (elementConfig != null) {
                    applyConfigToEditBoxWithConfig(elementId, editBox, elementConfig, config, contentX, contentY, screenWidth, screenHeight);
                }
            }
        }
    }
    
    /**
     * Apply a specific element config to an EditBox - ALWAYS uses Java defaults for sizing
     */
    private static void applyConfigToEditBoxWithConfig(String elementId, EditBox editBox,
                                                      GuiConfigData.ElementConfig elementConfig, GuiConfigData config,
                                                      int contentX, int contentY, int screenWidth, int screenHeight) {
        if (editBox == null || elementConfig == null) {
            return;
        }
        
        int x, y, width, height;
        
        // For positions: use saved config if it has percentages, otherwise use defaults
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
        
        // For sizes: ALWAYS use percentages, recalculate for current screen
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
    
    /**
     * Apply saved configurations to EditBoxes in a tab (backwards compatibility).
     * 
     * @param tabName The name of the tab
     * @param editBoxMap Map of element IDs to their EditBox instances
     * @param contentX X offset of content area (0 if positions are already absolute)
     * @param contentY Y offset of content area (0 if positions are already absolute)
     */
    public static void applyAllEditBoxConfigs(String tabName, Map<String, EditBox> editBoxMap, int contentX, int contentY) {
        applyAllEditBoxConfigs(tabName, editBoxMap, contentX, contentY, 0, 0);
    }
    
    /**
     * Apply saved configurations to EditBoxes in a tab (backwards compatibility).
     * 
     * @param tabName The name of the tab
     * @param editBoxMap Map of element IDs to their EditBox instances
     */
    public static void applyAllEditBoxConfigs(String tabName, Map<String, EditBox> editBoxMap) {
        applyAllEditBoxConfigs(tabName, editBoxMap, 0, 0);
    }
}

