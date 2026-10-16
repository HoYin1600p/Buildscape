package com.kingodogo.buildscape.mixinsupport;

import com.kingodogo.buildscape.client.renderer.FestiveGlintHandler;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.GpuSampler;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.oit.OitPipelineSet;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/** Carries stack identity through deferred extraction, submission and glint preparation. */
public final class FestiveSubmission {
    private static final ReferenceQueue<Object> QUEUE = new ReferenceQueue<>();
    private static final Map<IdentityReference, ItemStack> STACKS = new HashMap<>();
    private static final Map<RenderType, RenderType> TYPES = new IdentityHashMap<>();
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("buildscape", "textures/misc/festive_glint.png");
    // Minecraft 26.3 builds glint render types per texture, so they are recognised by name. The
    // names are the item, entity-model and armor glint types plus the two fixed glint types.
    private static final Set<String> GLINT_NAMES = Set.of(
            "item_cutout_glint", "item_cutout_glint_special",
            "item_translucent_glint", "item_translucent_glint_special",
            "entity_solid_glint", "armor_cutout_no_cull_glint",
            "patterned_shield_glint", "trimmed_armor_glint");

    private FestiveSubmission() {
    }

    public static synchronized void remember(Object state, ItemStack stack) {
        drain();
        STACKS.put(new IdentityReference(state, QUEUE), stack.copy());
    }

    public static synchronized ItemStack stackFor(Object state) {
        drain();
        return STACKS.getOrDefault(new IdentityReference(state, null), ItemStack.EMPTY);
    }

    public static synchronized void forget(Object state) {
        drain();
        STACKS.remove(new IdentityReference(state, null));
    }

    private static void drain() {
        IdentityReference reference;
        while ((reference = (IdentityReference) QUEUE.poll()) != null) STACKS.remove(reference);
    }

    public static synchronized RenderType currentGlint(RenderType original) {
        if (!FestiveGlintHandler.isCurrentFestive()) return original;
        return festiveGlint(original);
    }

    public static synchronized RenderType festiveGlint(RenderType original) {
        if (!isGlint(original)) return original;
        return TYPES.computeIfAbsent(original, FestiveSubmission::copyGlint);
    }

    private static boolean isGlint(RenderType original) {
        try {
            return GLINT_NAMES.contains((String) field(original, "name"));
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Cannot read the Minecraft 26.3 render type name", exception);
        }
    }

    // The private render-type factory has no public counterpart in 26.3. Rebuild the verified
    // setup through the public builder so the pipeline, blending, texture transform, layering and
    // sampler behavior stay intact; only the glint texture binding is replaced.
    @SuppressWarnings("unchecked")
    private static RenderType copyGlint(RenderType original) {
        try {
            RenderSetup setup = (RenderSetup) field(original, "state");
            Map<?, ?> textures = (Map<?, ?>) field(setup, "textures");
            RenderSetup.RenderSetupBuilder builder = RenderSetup.builder((RenderPipeline) field(setup, "pipeline"));
            OitPipelineSet oitPipelineSet = (OitPipelineSet) field(setup, "oitPipelineSet");
            if (oitPipelineSet != null) builder.setOitPipelines(oitPipelineSet);
            for (var entry : textures.entrySet()) {
                Object binding = entry.getValue();
                Identifier location = (Identifier) field(binding, "location");
                boolean glintTexture = location.equals(ItemFeatureRenderer.ENCHANTED_GLINT_ITEM)
                        || location.equals(ItemFeatureRenderer.ENCHANTED_GLINT_ARMOR);
                builder.withTexture((String) entry.getKey(), glintTexture ? TEXTURE : location,
                        (Supplier<GpuSampler>) field(binding, "sampler"));
            }
            if ((boolean) field(setup, "useLightmap")) builder.useLightmap();
            if ((boolean) field(setup, "useOverlay")) builder.useOverlay();
            builder.setLayeringTransform((LayeringTransform) field(setup, "layeringTransform"));
            builder.setTextureTransform((TextureTransform) field(setup, "textureTransform"));
            RenderSetup.OutlineProperty outlineProperty = (RenderSetup.OutlineProperty) field(setup, "outlineProperty");
            String outlineTextureName = (String) field(setup, "outlineTextureName");
            if (outlineTextureName == null) builder.setOutline(outlineProperty);
            else builder.setOutline(outlineProperty, outlineTextureName);
            if ((boolean) field(setup, "affectsCrumbling")) builder.affectsCrumbling();
            if ((boolean) field(setup, "sortOnUpload")) builder.sortOnUpload();
            if ((boolean) field(setup, "forceSolidModelPhase")) builder.withForcedSolidModelPhase();
            Constructor<RenderType> typeConstructor = RenderType.class.getDeclaredConstructor(String.class, RenderSetup.class);
            typeConstructor.setAccessible(true);
            return typeConstructor.newInstance("buildscape_festive_" + field(original, "name"), builder.createRenderSetup());
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Cannot construct the Minecraft 26.3 festive glint render setup", exception);
        }
    }

    private static Object field(Object instance, String name) throws ReflectiveOperationException {
        Field field = instance.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(instance);
    }

    private static final class IdentityReference extends WeakReference<Object> {
        private final int hash;

        private IdentityReference(Object value, ReferenceQueue<Object> queue) {
            super(value, queue);
            hash = System.identityHashCode(value);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof IdentityReference reference
                    && get() != null && get() == reference.get();
        }
    }
}
