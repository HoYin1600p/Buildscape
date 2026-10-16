package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.mixinsupport.FestiveSubmission;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;

/** Reference names map to the four glint pipelines retained by Minecraft 26.2. */
public final class FestiveRenderTypes {
    private FestiveRenderTypes() {}
    public static RenderType festiveGlint() { return FestiveSubmission.festiveGlint(RenderTypes.glint()); }
    public static RenderType festiveGlintDirect() { return festiveGlint(); }
    public static RenderType festiveGlintTranslucent() { return FestiveSubmission.festiveGlint(RenderTypes.glintTranslucent()); }
    public static RenderType festiveEntityGlint() { return FestiveSubmission.festiveGlint(RenderTypes.entityGlint()); }
    public static RenderType festiveEntityGlintDirect() { return festiveEntityGlint(); }
    public static RenderType festiveArmorGlint() { return FestiveSubmission.festiveGlint(RenderTypes.armorEntityGlint()); }
    public static RenderType festiveArmorEntityGlint() { return festiveArmorGlint(); }
}
