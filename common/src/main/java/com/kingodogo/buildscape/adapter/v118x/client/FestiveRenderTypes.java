package com.kingodogo.buildscape.adapter.v118x.client;


import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class FestiveRenderTypes extends RenderType {

    public static final ResourceLocation FESTIVE_GLINT_LOCATION = new ResourceLocation("buildscape", "textures/misc/festive_glint.png");

    private static final java.lang.reflect.Method CREATE_RENDER_TYPE = findCreateRenderType();

    // Vanilla's seven-argument factory and its return class are private/package-private.
    // Match the signature instead of a method name so this also works after SRG remapping.
    private static java.lang.reflect.Method findCreateRenderType() {
        Class<?>[] parameters = {String.class, VertexFormat.class, VertexFormat.Mode.class,
                int.class, boolean.class, boolean.class, RenderType.CompositeState.class};
        for (java.lang.reflect.Method method : RenderType.class.getDeclaredMethods()) {
            if (java.lang.reflect.Modifier.isStatic(method.getModifiers())
                    && RenderType.class.isAssignableFrom(method.getReturnType())
                    && java.util.Arrays.equals(parameters, method.getParameterTypes())) {
                method.setAccessible(true);
                return method;
            }
        }
        throw new IllegalStateException("Vanilla render type factory was not found");
    }

    private static RenderType createFestive(String name, VertexFormat format, VertexFormat.Mode mode,
                                            int bufferSize, boolean affectsCrumbling, boolean sortOnUpload,
                                            RenderType.CompositeState state) {
        try {
            return (RenderType) CREATE_RENDER_TYPE.invoke(null, name, format, mode, bufferSize,
                    affectsCrumbling, sortOnUpload, state);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not create festive render type " + name, exception);
        }
    }

    private static final RenderType FESTIVE_GLINT = createFestive(
            "festive_glint",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_GLINT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(FESTIVE_GLINT_LOCATION, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(GLINT_TRANSPARENCY)
                    .setTexturingState(GLINT_TEXTURING)
                    .createCompositeState(false)
    );

    private static final RenderType FESTIVE_GLINT_DIRECT = createFestive(
            "festive_glint_direct",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_GLINT_DIRECT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(FESTIVE_GLINT_LOCATION, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(GLINT_TRANSPARENCY)
                    .setTexturingState(GLINT_TEXTURING)
                    .createCompositeState(false)
    );

    private static final RenderType FESTIVE_GLINT_TRANSLUCENT = createFestive(
            "festive_glint_translucent",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_GLINT_TRANSLUCENT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(FESTIVE_GLINT_LOCATION, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(GLINT_TRANSPARENCY)
                    .setTexturingState(GLINT_TEXTURING)
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .createCompositeState(false)
    );

    private static final RenderType FESTIVE_ENTITY_GLINT = createFestive(
            "festive_entity_glint",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_ENTITY_GLINT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(FESTIVE_GLINT_LOCATION, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(GLINT_TRANSPARENCY)
                    .setTexturingState(ENTITY_GLINT_TEXTURING)
                    .setOutputState(ITEM_ENTITY_TARGET)
                    .createCompositeState(false)
    );

    private static final RenderType FESTIVE_ENTITY_GLINT_DIRECT = createFestive(
            "festive_entity_glint_direct",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_ENTITY_GLINT_DIRECT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(FESTIVE_GLINT_LOCATION, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(GLINT_TRANSPARENCY)
                    .setTexturingState(ENTITY_GLINT_TEXTURING)
                    .createCompositeState(false)
    );

    private static final RenderType FESTIVE_ARMOR_GLINT = createFestive(
            "festive_armor_glint",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_ARMOR_GLINT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(FESTIVE_GLINT_LOCATION, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(GLINT_TRANSPARENCY)
                    .setTexturingState(GLINT_TEXTURING)
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .createCompositeState(false)
    );

    private static final RenderType FESTIVE_ARMOR_ENTITY_GLINT = createFestive(
            "festive_armor_entity_glint",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_ARMOR_ENTITY_GLINT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(FESTIVE_GLINT_LOCATION, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(GLINT_TRANSPARENCY)
                    .setTexturingState(ENTITY_GLINT_TEXTURING)
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .createCompositeState(false)
    );

    private FestiveRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static RenderType festiveGlint() {
        return FESTIVE_GLINT;
    }

    public static RenderType festiveGlintDirect() {
        return FESTIVE_GLINT_DIRECT;
    }

    public static RenderType festiveGlintTranslucent() {
        return FESTIVE_GLINT_TRANSLUCENT;
    }

    public static RenderType festiveEntityGlint() {
        return FESTIVE_ENTITY_GLINT;
    }

    public static RenderType festiveEntityGlintDirect() {
        return FESTIVE_ENTITY_GLINT_DIRECT;
    }

    public static RenderType festiveArmorGlint() {
        return FESTIVE_ARMOR_GLINT;
    }

    public static RenderType festiveArmorEntityGlint() {
        return FESTIVE_ARMOR_ENTITY_GLINT;
    }
}
