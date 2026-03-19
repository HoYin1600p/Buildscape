package com.kingodogo.buildscape.client.guidebook.screen;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.client.guidebook.data.GuidePage;
import com.kingodogo.buildscape.client.guidebook.data.PageComponent;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 * High-Fidelity Guide Content Renderer.
 * Aesthetic improvements: Gradient separators, anti-collision padding, and glowing headers.
 */
public final class GuideContentRenderer {

    public static final int COLOR_BODY   = 0xFFE0E0E0; 
    public static final int COLOR_HEADER = 0xFF00FFFF; 
    public static final int COLOR_GHOST  = 0xFF777777;
    public static final int COLOR_SHADOW = 0x80000000; 

    private static final int LINE_HEIGHT = 10;
    private static final int HEADER_H    = 16;

    private GuideContentRenderer() {}

    public static int render(PoseStack ps, GuidePage page, int x, int y, int w, Font font, ItemRenderer ir) {
        int cy = y;
        for (PageComponent comp : page.getComponents()) {
            cy += renderComponent(ps, comp, x, cy, w, font, ir);
        }
        return cy - y;
    }

    private static int renderComponent(PoseStack ps, PageComponent comp, int x, int y, int w, Font font, ItemRenderer ir) {
        if (comp instanceof PageComponent.HeaderComponent h) {
            String text = resolve(h.textKey()).toUpperCase();
            // Glow Shadow
            font.draw(ps, text, x + 1, y + 1, COLOR_SHADOW);
            font.draw(ps, text, x, y, COLOR_HEADER);
            
            // Tech Separator (Gradient-like line)
            fill(ps, x, y + HEADER_H + 2, x + w, y + HEADER_H + 3, 0x4000FFFF);
            fill(ps, x, y + HEADER_H + 3, x + w/2, y + HEADER_H + 4, 0x8000FFFF);
            
            return HEADER_H + 12;

        } else if (comp instanceof PageComponent.TextComponent t) {
            String txt = applyMD(resolve(t.textKey()));
            List<String> lines = wrap(txt, w, font);
            for (int i = 0; i < lines.size(); i++) {
                font.draw(ps, lines.get(i), x, y + i * LINE_HEIGHT, COLOR_BODY);
            }
            return lines.size() * LINE_HEIGHT + 6;

        } else if (comp instanceof PageComponent.ImageComponent img) {
            try {
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, new ResourceLocation(img.texturePath()));
                RenderSystem.setShaderColor(1, 1, 1, 1);
                int h = img.width() / 2;
                GuiComponent.blit(ps, x, y, 0, 0, img.width(), h, img.width(), h);
                return h + 10;
            } catch (Exception e) { return 0; }

        } else if (comp instanceof PageComponent.ItemDisplayComponent itm) {
            ResourceLocation rid = new ResourceLocation(itm.itemId());
            Item item = ForgeRegistries.ITEMS.getValue(rid);
            if (item != null) {
                ir.renderGuiItem(new ItemStack(item), x, y);
                if (itm.captionKey() != null) {
                    font.draw(ps, resolve(itm.captionKey()), x + 22, y + 4, COLOR_GHOST);
                }
            }
            return 24;

        } else if (comp instanceof PageComponent.SpacerComponent s) {
            return s.pixels();
        }
        return 0;
    }

    private static String applyMD(String t) {
        return t.replaceAll("\\*\\*(.+?)\\*\\*", "§l$1§r").replaceAll("\\*(.+?)\\*", "§o$1§r");
    }

    private static String resolve(String k) { return net.minecraft.client.resources.language.I18n.get(k); }

    private static List<String> wrap(String t, int w, Font f) {
        List<String> l = new ArrayList<>();
        for (String r : t.split("\n", -1)) {
            f.split(new net.minecraft.network.chat.TextComponent(r), w).forEach(s -> {
                StringBuilder b = new StringBuilder();
                s.accept((i, st, c) -> { b.appendCodePoint(c); return true; });
                l.add(b.toString());
            });
        }
        return l;
    }

    private static void fill(PoseStack ps, int x, int y, int x2, int y2, int c) {
        GuiComponent.fill(ps, x, y, x2, y2, c);
    }
}
