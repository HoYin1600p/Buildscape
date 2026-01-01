package com.kingodogo.buildscape.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;

public class InventorySortDropdown extends Button {
    public enum SortMode {
        INVENTORY(new TranslatableComponent("buildscape.config.sort.inventory")),
        ALL(new TranslatableComponent("buildscape.config.sort.all")),
        MOD_ONLY(new TranslatableComponent("buildscape.config.sort.mod_only"));
        
        private final Component displayName;
        
        SortMode(Component displayName) {
            this.displayName = displayName;
        }
        
        public Component getDisplayName() {
            return displayName;
        }
    }
    
    private SortMode currentMode = SortMode.INVENTORY;
    private boolean dropdownOpen = false;
    private final Consumer<SortMode> onModeChanged;
    private final int dropdownHeight = 60; // Height for 3 options
    
    public InventorySortDropdown(int x, int y, int width, int height, Consumer<SortMode> onModeChanged) {
        super(x, y, width, height, new TranslatableComponent("buildscape.config.select_inventory"), (button) -> {
            InventorySortDropdown dropdown = (InventorySortDropdown) button;
            dropdown.dropdownOpen = !dropdown.dropdownOpen;
        });
        this.onModeChanged = onModeChanged;
    }
    
    public SortMode getCurrentMode() {
        return currentMode;
    }
    
    public void setMode(SortMode mode) {
        if (this.currentMode != mode) {
            this.currentMode = mode;
            if (onModeChanged != null) {
                onModeChanged.accept(mode);
            }
        }
        dropdownOpen = false;
    }
    
    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        // Background removed - too large
        // Border removed - no outer white borders
        
        // Draw text
        Minecraft.getInstance().font.draw(
            poseStack,
            getMessage(),
            x + width / 2 - Minecraft.getInstance().font.width(getMessage()) / 2,
            y + (height - 8) / 2,
            0xFFFFFF
        );
        
        // Draw dropdown arrow
        int arrowX = x + width - 15;
        int arrowY = y + height / 2;
        if (dropdownOpen) {
            // Draw up arrow
            fill(poseStack, arrowX, arrowY - 2, arrowX + 5, arrowY, 0xFFFFFF);
            fill(poseStack, arrowX + 2, arrowY - 4, arrowX + 3, arrowY - 2, 0xFFFFFF);
        } else {
            // Draw down arrow
            fill(poseStack, arrowX, arrowY, arrowX + 5, arrowY + 2, 0xFFFFFF);
            fill(poseStack, arrowX + 2, arrowY + 2, arrowX + 3, arrowY + 4, 0xFFFFFF);
        }
        
        // Render dropdown menu if open
        if (dropdownOpen) {
            int dropdownY = y + height;
            int optionHeight = 20;
            
            // Background and borders removed - no colorful backgrounds
            
            // Render options
            for (int i = 0; i < SortMode.values().length; i++) {
                SortMode mode = SortMode.values()[i];
                int optionY = dropdownY + i * optionHeight;
                boolean hovered = mouseX >= x && mouseX < x + width &&
                                mouseY >= optionY && mouseY < optionY + optionHeight;
                boolean selected = mode == currentMode;
                
                // Option background removed - no colorful backgrounds
                
                // Draw icon (simplified - using text for now)
                String icon = "";
                switch (mode) {
                    case INVENTORY:
                        icon = "📦";
                        break;
                    case ALL:
                        icon = "A";
                        break;
                    case MOD_ONLY:
                        icon = "M";
                        break;
                }
                
                Minecraft.getInstance().font.draw(
                    poseStack,
                    icon + " " + mode.getDisplayName().getString(),
                    x + 5,
                    optionY + (optionHeight - 8) / 2,
                    selected ? 0xFFFFFF : 0xCCCCCC
                );
            }
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.active || !this.visible) {
            return false;
        }
        
        if (isValidClickButton(button)) {
            boolean clicked = mouseX >= (double)this.x && mouseY >= (double)this.y && 
                            mouseX < (double)(this.x + this.width) && 
                            mouseY < (double)(this.y + this.height);
            
            if (clicked) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.onClick(mouseX, mouseY);
                return true;
            }
            
            // Check dropdown options
            if (dropdownOpen) {
                int dropdownY = y + height;
                int optionHeight = 20;
                
                for (int i = 0; i < SortMode.values().length; i++) {
                    SortMode mode = SortMode.values()[i];
                    int optionY = dropdownY + i * optionHeight;
                    
                    if (mouseX >= x && mouseX < x + width &&
                        mouseY >= optionY && mouseY < optionY + optionHeight) {
                        setMode(mode);
                        return true;
                    }
                }
                
                // Clicked outside dropdown, close it
                dropdownOpen = false;
            }
        }
        
        return false;
    }
}

