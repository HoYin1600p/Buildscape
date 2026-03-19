package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.client.guidebook.screen.GuideBookScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects the BuildScape Guidebook button into the vanilla Inventory screen.
 *
 * NOTE: addRenderableWidget is NOT declared on InventoryScreen — it is inherited
 * from Screen. We therefore must NOT use @Shadow (which only targets the mixin
 * target class). Instead we call it through `this`, which resolves correctly at
 * runtime because our mixin class extends Screen.
 */
@Mixin(InventoryScreen.class)
public abstract class InventoryGuideButtonMixin extends Screen {

    protected InventoryGuideButtonMixin() { super(null); }

    @Inject(method = "init", at = @At("TAIL"))
    private void buildscape$addGuideBookButton(CallbackInfo ci) {

        // Determine button position — attempt to anchor next to the recipe book button
        int guideX = -1, guideY = -1;

        for (net.minecraft.client.gui.components.events.GuiEventListener child : children()) {
            if (child instanceof ImageButton btn) {
                // The vanilla recipe-book toggle button has the texture
                // "textures/gui/recipe_button.png" — match by size (20×18)
                // In 1.18.2 AbstractWidget exposes x, y, width, height directly.
                try {
                    // width / height are protected AbstractWidget fields but ARE accessible
                    // here because our mixin class extends Screen which uses the same
                    // package hierarchy — the JVM allows this.
                    java.lang.reflect.Field wf = net.minecraft.client.gui.components.AbstractWidget.class
                            .getDeclaredField("width");
                    java.lang.reflect.Field hf = net.minecraft.client.gui.components.AbstractWidget.class
                            .getDeclaredField("height");
                    wf.setAccessible(true);
                    hf.setAccessible(true);
                    int w = wf.getInt(btn);
                    int h = hf.getInt(btn);
                    if (w == 20 && h == 18) {
                        guideX = btn.x + 22;
                        guideY = btn.y;
                        break;
                    }
                } catch (Exception ignored) {}
            }
        }

        // Fallback position
        if (guideX < 0) {
            int leftPos = (width  - 176) / 2;
            int topPos  = (height - 166) / 2;
            guideX = leftPos - 22;
            guideY = topPos  + 30;
        }

        final int finalX = guideX;
        final int finalY = guideY;

        ImageButton guideButton = new ImageButton(
                finalX, finalY, 20, 18,
                0, 0, 0,
                new ResourceLocation(BuildScape.MODID, "textures/gui/guidebook/icons.png"),
                128, 128,
                btn -> Minecraft.getInstance().setScreen(new GuideBookScreen()),
                new TranslatableComponent("guidebook.button.tooltip")
        ) {
            @Override
            public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
                if (isHoveredOrFocused()) {
                    fill(poseStack, x - 1, y - 1, x + 21, y + 19, 0x40FFD080);
                }
                RenderSystem.setShaderTexture(0,
                        new ResourceLocation(BuildScape.MODID, "textures/gui/guidebook/icons.png"));
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                blit(poseStack, x + 2, y + 1, 0, 0, 16, 16, 128, 128);
            }
        };

        // `this` IS-A Screen which extends GuiComponent which declares addRenderableWidget.
        // Calling through `this` at runtime is legal — no @Shadow needed.
        this.addRenderableWidget(guideButton);
    }
}
