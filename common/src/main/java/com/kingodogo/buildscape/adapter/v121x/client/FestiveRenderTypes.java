package com.kingodogo.buildscape.adapter.v121x.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/** Festive copies of the vanilla 1.21.1 enchantment glint render types (same states, festive texture). */
public final class FestiveRenderTypes extends RenderType {

    public static final ResourceLocation FESTIVE_GLINT_LOCATION =
            ResourceLocation.fromNamespaceAndPath("buildscape", "textures/misc/festive_glint.png");

    private static final RenderType FESTIVE_GLINT = createFestive(
            "festive_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 1536, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_GLINT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(FESTIVE_GLINT_LOCATION, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(GLINT_TRANSPARENCY)
                    .setTexturingState(GLINT_TEXTURING)
                    .createCompositeState(false));

    private static final RenderType FESTIVE_GLINT_TRANSLUCENT = createFestive(
            "festive_glint_translucent", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 1536, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_GLINT_TRANSLUCENT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(FESTIVE_GLINT_LOCATION, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(GLINT_TRANSPARENCY)
                    .setTexturingState(GLINT_TEXTURING)
                    .setOutputState(ITEM_ENTITY_TARGET)
                    .createCompositeState(false));

    private static final RenderType FESTIVE_ENTITY_GLINT = createFestive(
            "festive_entity_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 1536, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_ENTITY_GLINT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(FESTIVE_GLINT_LOCATION, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(GLINT_TRANSPARENCY)
                    .setOutputState(ITEM_ENTITY_TARGET)
                    .setTexturingState(ENTITY_GLINT_TEXTURING)
                    .createCompositeState(false));

    private static final RenderType FESTIVE_ENTITY_GLINT_DIRECT = createFestive(
            "festive_entity_glint_direct", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 1536, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_ENTITY_GLINT_DIRECT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(FESTIVE_GLINT_LOCATION, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(GLINT_TRANSPARENCY)
                    .setTexturingState(ENTITY_GLINT_TEXTURING)
                    .createCompositeState(false));

    private static final RenderType FESTIVE_ARMOR_ENTITY_GLINT = createFestive(
            "festive_armor_entity_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 1536, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_ARMOR_ENTITY_GLINT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(FESTIVE_GLINT_LOCATION, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(GLINT_TRANSPARENCY)
                    .setTexturingState(ENTITY_GLINT_TEXTURING)
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .createCompositeState(false));

    // Vanilla's create(...) is package-private. These glint states do not take part in outlines, so use
    // RenderType's public constructor with the same ordered setup/clear as CompositeRenderType.
    private static RenderType createFestive(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                                            boolean affectsCrumbling, boolean sortOnUpload, RenderType.CompositeState state) {
        var states = ((com.kingodogo.buildscape.mixin.RenderCompositeStateV121xAccessor) (Object) state).buildscape$getStates();
        return new FestiveRenderTypes(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload,
                () -> states.forEach(RenderStateShard::setupRenderState),
                () -> states.forEach(RenderStateShard::clearRenderState));
    }

    private FestiveRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                               boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setup, clear);
    }

    public static RenderType festiveGlint() { return FESTIVE_GLINT; }
    public static RenderType festiveGlintTranslucent() { return FESTIVE_GLINT_TRANSLUCENT; }
    public static RenderType festiveEntityGlint() { return FESTIVE_ENTITY_GLINT; }
    public static RenderType festiveEntityGlintDirect() { return FESTIVE_ENTITY_GLINT_DIRECT; }
    public static RenderType festiveArmorEntityGlint() { return FESTIVE_ARMOR_ENTITY_GLINT; }
}
