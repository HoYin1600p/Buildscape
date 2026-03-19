package com.kingodogo.buildscape.client.guidebook.screen;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.client.guidebook.data.*;
import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 * High-Density Holographic Guidebook.
 * Clean, OS-metaphor design with advanced tree-navigation and eased transitions.
 */
public class GuideBookScreen extends Screen {

    private static final double SIDEBAR_WIDTH_PCT = 0.15; 
    private static final double LEFT_GAP_PCT = 0.012;      
    private static final double CONTENT_GAP_PCT = 0.03;   

    private int sidebarW, contentX, contentY, contentW, contentH;
    
    private final List<GuideCategory> cats = new ArrayList<>();
    private GuideCategory selCat = null;
    private GuideEntry selEntry = null, prevEntry = null;
    private int pageIdx = 0, prevPageIdx = 0;

    // Animation System
    private float flipProgress = 0; 
    private static final float FLIP_SPEED = 0.06f; 
    private boolean isForward = true;
    private float globalTicks = 0;

    private int sidebarScroll = 0, sidebarMaxScr = 0;
    private int contentScroll = 0, contentTotalHeight = 0;

    private EditBox searchBox;
    private final List<AbstractNavButton> navButtons = new ArrayList<>();
    private SpecialNavButton prevBtn, nextBtn;

    public GuideBookScreen() {
        super(new TextComponent("BuildScape Guide"));
    }

    @Override
    protected void init() {
        calculateLayout();
        rebuildList("");
        if (selCat == null && !cats.isEmpty()) {
            selCat = cats.get(0);
            if (!selCat.getEntries().isEmpty()) selEntry = selCat.getEntries().get(0);
        }
        rebuildWidgets();
    }

    private void calculateLayout() {
        sidebarW = (int) (width * SIDEBAR_WIDTH_PCT);
        int sx = (int) (width * LEFT_GAP_PCT);
        contentX = sx + sidebarW + (int) (width * CONTENT_GAP_PCT);
        contentY = (int)(height * 0.18); 
        contentW = (int) (width * (1.0 - (LEFT_GAP_PCT + SIDEBAR_WIDTH_PCT + CONTENT_GAP_PCT + 0.02)));
        contentH = (int)(height * 0.65);
    }

    private void rebuildWidgets() {
        this.clearWidgets();
        navButtons.clear();

        int sx = (int) (width * LEFT_GAP_PCT);
        int bw = sidebarW - 8;
        int bx = sx + 4;

        // Elegant Search Line
        int searchY = (int)(height * 0.11);
        searchBox = new EditBox(font, bx + 22, searchY, bw - 26, 12, new TextComponent(""));
        searchBox.setResponder(q -> { rebuildList(q); sidebarScroll = 0; rebuildWidgets(); });
        searchBox.setSuggestion("Search...");
        searchBox.setBordered(false);
        searchBox.setTextColor(0xFF00E5FF);
        addRenderableWidget(searchBox);

        // Sidebar Tree
        int curY = (int)(height * 0.18) - sidebarScroll;
        int itemH = BuildScapeConfigScreen.scaleSize(22);

        for (GuideCategory cat : cats) {
            boolean active = (cat == selCat);
            CategoryButton cBtn = new CategoryButton(bx, curY, bw, itemH, new TranslatableComponent(cat.getDisplayNameKey()), cat.getIconItem(), b -> {
                if (selCat != cat) {
                    selCat = cat;
                    if (!cat.getEntries().isEmpty()) selectEntry(cat.getEntries().get(0), 0, true);
                    rebuildWidgets();
                }
            });
            cBtn.setSelected(active);
            if (curY > height * 0.16 && curY < height * 0.9) {
                addRenderableWidget(cBtn);
                navButtons.add(cBtn);
            }
            curY += itemH + 3;

            if (active) {
                List<GuideEntry> entries = cat.getEntries();
                for (int i = 0; i < entries.size(); i++) {
                    GuideEntry ent = entries.get(i);
                    boolean isLast = (i == entries.size() - 1);
                    EntryButton eBtn = new EntryButton(bx + 16, curY, bw - 16, itemH - 4, new TranslatableComponent(ent.getTitleKey()), isLast, b -> {
                        selectEntry(ent, 0, true);
                        rebuildWidgets();
                    });
                    eBtn.setSelected(ent == selEntry);
                    if (curY > height * 0.16 && curY < height * 0.9) {
                        addRenderableWidget(eBtn);
                        navButtons.add(eBtn);
                    }
                    curY += itemH - 4;
                }
            }
        }
        sidebarMaxScr = Math.max(0, curY + sidebarScroll - (int)(height * 0.9));

        // Navigation Footer
        int fy = (int)(height * 0.88);
        prevBtn = new SpecialNavButton(contentX + contentW - 80, fy, 32, 24, "<", b -> changePage(-1));
        nextBtn = new SpecialNavButton(contentX + contentW - 38, fy, 32, 24, ">", b -> changePage(1));
        addRenderableWidget(prevBtn);
        addRenderableWidget(nextBtn);
        syncNav();
    }

    private void selectEntry(GuideEntry e, int p, boolean fw) {
        if (e == selEntry && p == pageIdx) return;
        prevEntry = selEntry;
        prevPageIdx = pageIdx;
        selEntry = e;
        pageIdx = p;
        flipProgress = 1.0f;
        isForward = fw;
        contentScroll = 0;
        syncNav();
    }

    private void changePage(int d) {
        if (selEntry == null) return;
        int n = pageIdx + d;
        if (n >= 0 && n < selEntry.pageCount()) selectEntry(selEntry, n, d > 0);
    }

    private void syncNav() {
        if (prevBtn != null) prevBtn.active = (pageIdx > 0);
        if (nextBtn != null) nextBtn.active = (selEntry != null && pageIdx < selEntry.pageCount() - 1);
    }

    @Override
    public void render(PoseStack ps, int mx, int my, float pt) {
        renderBackground(ps);
        globalTicks += pt;
        if (flipProgress > 0) flipProgress = Math.max(0, flipProgress - FLIP_SPEED);

        int sx = (int)(width * LEFT_GAP_PCT);
        
        // --- TOP BRANDING ---
        ps.pushPose();
        ps.translate(sx + 5, height * 0.04, 0);
        ps.scale(1.8f, 1.8f, 1);
        font.draw(ps, "BUILD", 0, 0, 0xFFFFFFFF);
        font.draw(ps, "SCAPE", font.width("BUILD"), 0, 0xFF00FFFF);
        ps.translate(0, 10, 0); ps.scale(0.35f, 0.35f, 1);
        font.draw(ps, "CENTRAL TERMINAL / GUIDEBOOK v2.0", 0, 0, 0x8000FFFF);
        ps.popPose();

        // Search underline and icon
        fill(ps, sx + 8, (int)(height * 0.13), sx + sidebarW - 8, (int)(height * 0.13) + 1, 0x3000FFFF);
        font.draw(ps, "\u2315", sx + 12, (int)(height * 0.11), 0xFFAAAAAA);

        // Sidebar Surface Shadow
        fill(ps, sx, (int)(height * 0.16), sx + sidebarW, (int)(height * 0.95), 0xA0020202);
        fill(ps, sx + sidebarW - 1, (int)(height * 0.16), sx + sidebarW, (int)(height * 0.9), 0x2000FFFF);

        // --- MAIN SURFACE ---
        drawGlitchFrame(ps, contentX - 6, contentY - 6, contentW + 12, contentH + 12);
        fill(ps, contentX, contentY, contentX + contentW, contentY + contentH, 0xFD050505);

        // Scanline effect
        float scanY = (globalTicks * 0.6f) % contentH;
        fill(ps, contentX, contentY + (int)scanY, contentX + contentW, contentY + (int)scanY + 2, 0x1000FFFF);

        if (selCat != null && selEntry != null) {
            // Header Crumbs
            String ctx = resolve(selCat.getDisplayNameKey()).toUpperCase() + " \u00AB " + resolve(selEntry.getTitleKey());
            font.draw(ps, ctx, contentX + 5, contentY - 14, 0xFF00E5FF);
            String pgNum = (pageIdx + 1) + " OF " + selEntry.pageCount();
            font.draw(ps, pgNum, contentX + contentW - font.width(pgNum) - 5, contentY - 14, 0xFF555555);

            enableScissor(contentX, contentY, contentX + contentW, contentY + contentH);
            if (flipProgress > 0) {
                renderExtremeFlip(ps, flipProgress);
            } else {
                renderMainContent(ps);
            }
            disableScissor();
        }

        super.render(ps, mx, my, pt);
    }

    private void renderMainContent(PoseStack ps) {
        if (selEntry != null && !selEntry.getPages().isEmpty()) {
            contentTotalHeight = GuideContentRenderer.render(ps, 
                selEntry.getPages().get(pageIdx), contentX + 20, contentY + 20 - contentScroll, contentW - 40, 
                font, itemRenderer);
        }
    }

    private void renderExtremeFlip(PoseStack ps, float p) {
        // Advanced Sigmoid-Eased Flip
        float t = 1.0f - p;
        float eased = (float)(1.0 / (1.0 + Math.exp(-12.0 * (t - 0.5))));
        float angle = eased * 180f;
        if (!isForward) angle = -angle;

        ps.pushPose();
        // Layer 1: Under-page
        if (p > 0.5f) {
            if (prevEntry != null) GuideContentRenderer.render(ps, prevEntry.getPages().get(prevPageIdx), contentX + 20, contentY + 20, contentW - 40, font, itemRenderer);
        } else {
            renderMainContent(ps);
        }
        
        // Layer 2: Flipping Leaf
        ps.translate(contentX + contentW/2f, contentY + contentH/2f, 300);
        ps.mulPose(Vector3f.YP.rotationDegrees(angle));
        ps.mulPose(Vector3f.ZP.rotationDegrees((float)Math.sin(eased * 3.14) * 2f * (isForward ? 1 : -1)));
        ps.translate(-(contentX + contentW/2f), -(contentY + contentH/2f), -300);
        
        // Shading Logic
        int alpha = (int)(Math.abs(Math.cos(eased * 3.14 / 2.0)) * 60);
        fill(ps, contentX, contentY, contentX + contentW, contentY + contentH, (alpha << 24) | 0x00FFFF);
        ps.popPose();
    }

    private void drawGlitchFrame(PoseStack ps, int x, int y, int w, int h) {
        // High-end procedural frame
        fill(ps, x, y, x + w, y + 1, 0xFF00FFFF); 
        fill(ps, x, y + h - 1, x + w, y + h, 0xFF0088FF); 
        fill(ps, x-1, y, x, y + h, 0x8000FFFF); 
        fill(ps, x + w, y, x + w + 1, y + h, 0x800088FF); 
        
        // Corner tech-brackets
        int l = 12;
        fill(ps, x-4, y-4, x+l, y-1, 0xFFFFFFFF);
        fill(ps, x-4, y-4, x-1, y+l, 0xFFFFFFFF);
        fill(ps, x+w+1, y+h+1, x+w-l, y+h+4, 0xFFFFFFFF);
        fill(ps, x+w+1, y+h+1, x+w+4, y+h-l, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double delta) {
        if (mx < contentX) {
            sidebarScroll = Mth.clamp(sidebarScroll - (int)(delta * 22), 0, sidebarMaxScr);
            rebuildWidgets();
        } else {
            contentScroll = Mth.clamp(contentScroll - (int)(delta * 25), 0, Math.max(0, contentTotalHeight - contentH));
        }
        return true;
    }

    private void rebuildList(String q) {
        List<GuideRegistry.SearchResult> r = GuideRegistry.getInstance().search(q);
        cats.clear();
        for (GuideRegistry.SearchResult res : r) if (!cats.contains(res.category())) cats.add(res.category());
    }

    private String resolve(String k) { return net.minecraft.client.resources.language.I18n.get(k); }
    private void enableScissor(int x, int y, int w, int h) {
        double s = minecraft.getWindow().getGuiScale();
        RenderSystem.enableScissor((int)(x*s), (int)(minecraft.getWindow().getHeight()-(y+h)*s), (int)(w*s), (int)(h*s));
    }
    private void disableScissor() { RenderSystem.disableScissor(); }

    // --- BUTTON COMPONENTS ---

    private abstract class AbstractNavButton extends Button {
        protected boolean selected = false;
        protected float hoverLerp = 0;
        public AbstractNavButton(int x, int y, int w, int h, net.minecraft.network.chat.Component m, OnPress p) { super(x, y, w, h, m, p); }
        public void setSelected(boolean s) { this.selected = s; }
    }

    private class CategoryButton extends AbstractNavButton {
        private final String icon;
        public CategoryButton(int x, int y, int w, int h, net.minecraft.network.chat.Component m, String i, OnPress p) { super(x, y, w, h, m, p); this.icon = i; }
        @Override
        public void renderButton(PoseStack ps, int mx, int my, float pt) {
            boolean h = mx >= x && my >= y && mx < x + width && my < y + height;
            hoverLerp = Mth.lerp(0.18f * pt, hoverLerp, h ? 1.0f : 0.0f);
            
            int color = selected ? 0xFF00FFFF : (h ? 0xFFFFFFFF : 0xFFCCCCCC);
            if (icon != null) {
                ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(icon)));
                ps.pushPose(); ps.translate(x + 4 + (hoverLerp * 3), y + 3, 0); ps.scale(0.85f, 0.85f, 1);
                itemRenderer.renderGuiItem(stack, 0, 0); ps.popPose();
            }
            
            ps.pushPose();
            ps.translate(x + (icon != null ? 26 : 6) + (hoverLerp * 4), y + (height - 8)/2f, 0);
            if (selected) font.draw(ps, "\u25B6 ", -20, 0, 0xFF00FFFF);
            font.draw(ps, getMessage(), 0, 0, color);
            ps.popPose();
        }
    }

    private class EntryButton extends AbstractNavButton {
        private final boolean isLast;
        public EntryButton(int x, int y, int w, int h, net.minecraft.network.chat.Component m, boolean last, OnPress p) { super(x, y, w, h, m, p); this.isLast = last; }
        @Override
        public void renderButton(PoseStack ps, int mx, int my, float pt) {
            boolean h = mx >= x && my >= y && mx < x + width && my < y + height;
            hoverLerp = Mth.lerp(0.22f * pt, hoverLerp, h ? 1.0f : 0.0f);
            int color = selected ? 0xFFFFFFFF : (h ? 0xFF00FFFF : 0xFF606060);
            
            // Tree line logic (Bright Cyan)
            int lx = x - 10;
            fill(ps, lx, y - 2, lx + 1, isLast ? y + height / 2 : y + height, 0x6000FFFF); 
            fill(ps, lx, y + height / 2, lx + 8, y + height / 2 + 1, 0x6000FFFF); 
            
            ps.pushPose();
            float s = 0.9f;
            ps.translate(x + 5 + (hoverLerp * 3), y + (height - 8*s)/2f, 0);
            ps.scale(s, s, 1);
            if (selected) font.draw(ps, "\u00BB ", -12, 0, 0xFFFFFFFF);
            font.draw(ps, getMessage(), 0, 0, color);
            ps.popPose();
        }
    }

    private class SpecialNavButton extends Button {
        public SpecialNavButton(int x, int y, int w, int h, String msg, OnPress p) { super(x, y, w, h, new TextComponent(msg), p); }
        @Override
        public void renderButton(PoseStack ps, int mx, int my, float pt) {
            boolean h = mx >= x && my >= y && mx < x + width && my < y + height;
            int bg = active ? (h ? 0xC000FFFF : 0x8000FFFF) : 0x151515;
            fill(ps, x, y, x + width, y + height, active ? 0xFF00FFFF : 0x333333);
            fill(ps, x + 1, y + 1, x + width - 1, y + height - 1, bg);
            drawCenteredString(ps, font, getMessage(), x + width / 2, y + (height - 8) / 2, active ? 0xFFFFFFFF : 0x666666);
        }
    }
}
