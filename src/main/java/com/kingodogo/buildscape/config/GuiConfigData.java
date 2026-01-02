package com.kingodogo.buildscape.config;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;

public class GuiConfigData {
    
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
        
        @SerializedName("percentX")
        public Double percentX = null;
        
        @SerializedName("percentY")
        public Double percentY = null;
        
        @SerializedName("percentWidth")
        public Double percentWidth = null;
        
        @SerializedName("percentHeight")
        public Double percentHeight = null;
        
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
    
    @SerializedName("screen")
    public ScreenConfig screen = new ScreenConfig();
    
    @SerializedName("elements")
    public Map<String, ElementConfig> elements = new HashMap<>();
    
    public static class ScreenConfig {
        @SerializedName("width")
        public int width = 0;

        @SerializedName("height")
        public int height = 0;

        @SerializedName("scale")
        public float scale = 1.0f;
        
        @SerializedName("contentX")
        public int contentX = 0;
        
        @SerializedName("contentY")
        public int contentY = 0;
        
        @SerializedName("contentWidth")
        public int contentWidth = 0;

        @SerializedName("contentHeight")
        public int contentHeight = 0;
    }
    
    public GuiConfigData() {}
    
    public ElementConfig getElementConfig(String elementId, int defaultX, int defaultY, int defaultWidth, int defaultHeight) {
        return elements.computeIfAbsent(elementId, k -> new ElementConfig(defaultX, defaultY, defaultWidth, defaultHeight));
    }
    
    public ElementConfig getElementConfig(String elementId) {
        return elements.get(elementId);
    }
    
    public void setElementConfig(String elementId, ElementConfig config) {
        elements.put(elementId, config);
    }
    
    public void removeElementConfig(String elementId) {
        elements.remove(elementId);
    }
    
    public boolean hasElementConfig(String elementId) {
        return elements.containsKey(elementId);
    }
}

