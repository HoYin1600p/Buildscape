package com.kingodogo.buildscape.adapter.v263.client;

import com.kingodogo.buildscape.mixinsupport.FestiveSubmission;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;

/**
 * Minecraft 26.3 turned the four 26.2 glint render types into per-texture factories. The base
 * accessors below name the 26.3 type that has the same pipeline, texture transform and glint
 * texture as each 26.2 type: item glint on the item atlas (cutout and translucent), the fixed
 * entity glint (patterned shield) and the fixed armor glint (trimmed armor).
 */
public final class FestiveRenderTypes {
    private FestiveRenderTypes() {}
    public static RenderType glint() { return Sheets.cutoutItemGlintSheet(); }
    public static RenderType glintTranslucent() { return Sheets.translucentItemGlintSheet(); }
    public static RenderType entityGlint() { return RenderTypes.patternedShieldGlint(); }
    public static RenderType armorEntityGlint() { return RenderTypes.trimmedArmorGlint(); }
    public static RenderType festiveGlint() { return FestiveSubmission.festiveGlint(glint()); }
    public static RenderType festiveGlintDirect() { return festiveGlint(); }
    public static RenderType festiveGlintTranslucent() { return FestiveSubmission.festiveGlint(glintTranslucent()); }
    public static RenderType festiveEntityGlint() { return FestiveSubmission.festiveGlint(entityGlint()); }
    public static RenderType festiveEntityGlintDirect() { return festiveEntityGlint(); }
    public static RenderType festiveArmorGlint() { return FestiveSubmission.festiveGlint(armorEntityGlint()); }
    public static RenderType festiveArmorEntityGlint() { return festiveArmorGlint(); }
}
