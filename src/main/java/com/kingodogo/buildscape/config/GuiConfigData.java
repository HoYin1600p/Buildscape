package com.kingodogo.buildscape.config;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;

/**
 * Data class for storing GUI element configuration.
 * Stores position, size, and scale information for GUI elements.
 */
public class GuiConfigData {
    
    /**
     * Configuration for a single GUI element/widget.
     */
    public static class ElementConfig {
        @SerializedName("x")
        public int x = 0;
        
        @SerializedName("y")
        public int y = 0;
        
        @SerializedName("width")
        public int width = 100;
        
        @SerializedName("height")
        public int height = 20;
        
        @SerializedName("scale")
        public float scale = 1.0f;
        
        @SerializedName("visible")
        public boolean visible = true;
        
        /**
         * Percentage-based positioning (0.0 to 1.0, e.g., 0.2 = 20%)
         * If set, these override x/y/width/height when calculating positions
         */
        @SerializedName("percentX")
        public Double percentX = null;
        
        @SerializedName("percentY")
        public Double percentY = null;
        
        @SerializedName("percentWidth")
        public Double percentWidth = null;
        
        @SerializedName("percentHeight")
        public Double percentHeight = null;
        
        /**
         * Additional custom properties that can be stored for extensibility
         */
        @SerializedName("properties")
        public Map<String, Object> properties = new HashMap<>();
        
        public ElementConfig() {}
        
        public ElementConfig(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        
        public ElementConfig(int x, int y, int width, int height, float scale) {
            this(x, y, width, height);
            this.scale = scale;
        }
        
        /**
         * Copy constructor
         */
        public ElementConfig(ElementConfig other) {
            this.x = other.x;
            this.y = other.y;
            this.width = other.width;
            this.height = other.height;
            this.scale = other.scale;
            this.visible = other.visible;
            this.properties = new HashMap<>(other.properties);
        }
    }
    
    /**
     * Screen-level configuration
     */
    @SerializedName("screen")
    public ScreenConfig screen = new ScreenConfig();
    
    /**
     * Map of element IDs to their configurations
     * Element IDs are widget names or identifiers like "itemSelectionWidget", "searchBox", etc.
     */
    @SerializedName("elements")
    public Map<String, ElementConfig> elements = new HashMap<>();
    
    /**
     * Screen-level configuration
     */
    public static class ScreenConfig {
        @SerializedName("width")
        public int width = 0; // 0 means use default/screen width
        
        @SerializedName("height")
        public int height = 0; // 0 means use default/screen height
        
        @SerializedName("scale")
        public float scale = 1.0f;
        
        @SerializedName("contentX")
        public int contentX = 0;
        
        @SerializedName("contentY")
        public int contentY = 0;
        
        @SerializedName("contentWidth")
        public int contentWidth = 0; // 0 means use default
        
        @SerializedName("contentHeight")
        public int contentHeight = 0; // 0 means use default
    }
    
    public GuiConfigData() {}
    
    /**
     * Get configuration for an element, creating a default if it doesn't exist
     */
    public ElementConfig getElementConfig(String elementId, int defaultX, int defaultY, int defaultWidth, int defaultHeight) {
        return elements.computeIfAbsent(elementId, k -> new ElementConfig(defaultX, defaultY, defaultWidth, defaultHeight));
    }
    
    /**
     * Get configuration for an element, returning null if it doesn't exist
     */
    public ElementConfig getElementConfig(String elementId) {
        return elements.get(elementId);
    }
    
    /**
     * Set configuration for an element
     */
    public void setElementConfig(String elementId, ElementConfig config) {
        elements.put(elementId, config);
    }
    
    /**
     * Remove configuration for an element
     */
    public void removeElementConfig(String elementId) {
        elements.remove(elementId);
    }
    
    /**
     * Check if an element configuration exists
     */
    public boolean hasElementConfig(String elementId) {
        return elements.containsKey(elementId);
    }
}

