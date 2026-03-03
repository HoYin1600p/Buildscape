package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {
    private static final ResourceLocation BUILDSCAPE_BUTTON_TEXTURE = new ResourceLocation(BuildScape.MODID, "textures/gui/buildscape_config_button.png");
    private static final ResourceLocation BUILDSCAPE_BUTTON_HOVER_TEXTURE = new ResourceLocation(BuildScape.MODID, "textures/gui/buildscape_config_hover_button.png");

    protected PauseScreenMixin() {
        super(null);
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void addBuildScapeConfigButton(CallbackInfo ci) {
        PauseScreen screen = (PauseScreen) (Object) this;

        // Default fallback positions
        int targetX = this.width / 2 + 102;
        int targetY = this.height / 4 + 48;

        // Dynamically find the Statistics button to align perfectly next to it
        for (net.minecraft.client.gui.components.events.GuiEventListener listener : this.children()) {
            if (listener instanceof net.minecraft.client.gui.components.AbstractWidget widget) {
                net.minecraft.network.chat.Component msg = widget.getMessage();
                if (msg instanceof net.minecraft.network.chat.TranslatableComponent tc &&
                    (tc.getKey().equals("menu.statistics") || tc.getKey().equals("gui.stats"))) {
                    targetX = widget.x + widget.getWidth() + 4;
                    targetY = widget.y;
                    break;
                }
            }
        }

        // Create BuildScape Config button using the custom texture with "B" text overlay
        ImageButton configButton = new ImageButton(
                targetX - 2, targetY, 20, 20,
                0, 0, 0,
                BUILDSCAPE_BUTTON_TEXTURE, 20, 20,
                (button) -> net.minecraft.client.Minecraft.getInstance().setScreen(new BuildScapeConfigScreen(screen))
        ) {
            @Override
            public void renderButton(com.mojang.blaze3d.vertex.PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
                ResourceLocation texture = this.isHoveredOrFocused() ? BUILDSCAPE_BUTTON_HOVER_TEXTURE : BUILDSCAPE_BUTTON_TEXTURE;
                com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0, texture);
                blit(poseStack, this.x, this.y, 0, 0, this.width, this.height, 20, 20);
                drawCenteredString(poseStack, net.minecraft.client.Minecraft.getInstance().font, "B", this.x + this.width / 2, this.y + (this.height - 8) / 2, 0xFFFFFF);
            }
        };

        this.addRenderableWidget(configButton);
    }
}

