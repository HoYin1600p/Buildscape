package com.kingodogo.buildscape.mixinsupport;

import com.kingodogo.buildscape.client.renderer.FestiveGlintHandler;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

/** Carries stack identity through deferred extraction, submission and glint preparation. */
public final class FestiveSubmission {
    private static final ReferenceQueue<Object> QUEUE = new ReferenceQueue<>();
    private static final Map<IdentityReference, ItemStack> STACKS = new HashMap<>();
    private static final Map<RenderType, RenderType> TYPES = new IdentityHashMap<>();
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("buildscape", "textures/misc/festive_glint.png");

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
        if (original != RenderTypes.glint() && original != RenderTypes.glintTranslucent()
                && original != RenderTypes.entityGlint() && original != RenderTypes.armorEntityGlint()) return original;
        return TYPES.computeIfAbsent(original, FestiveSubmission::copyGlint);
    }

    // The private render-type factory has no public counterpart in 26.2. Copy the verified
    // setup so blending, decal transforms, output targets and sampler behavior stay intact.
    private static RenderType copyGlint(RenderType original) {
        try {
            RenderSetup setup = (RenderSetup) field(original, "state");
            Map<?, ?> textures = (Map<?, ?>) field(setup, "textures");
            Map<Object, Object> replacements = new HashMap<>();
            for (var entry : textures.entrySet()) {
                Object binding = entry.getValue();
                Constructor<?> constructor = binding.getClass().getDeclaredConstructor(Identifier.class, Supplier.class);
                constructor.setAccessible(true);
                replacements.put(entry.getKey(), constructor.newInstance(TEXTURE, field(binding, "sampler")));
            }
            Constructor<?> setupConstructor = RenderSetup.class.getDeclaredConstructor(
                    com.mojang.blaze3d.pipeline.RenderPipeline.class, Map.class, boolean.class, boolean.class,
                    net.minecraft.client.renderer.rendertype.LayeringTransform.class,
                    net.minecraft.client.renderer.rendertype.OutputTarget.class,
                    net.minecraft.client.renderer.rendertype.TextureTransform.class,
                    RenderSetup.OutlineProperty.class, boolean.class, boolean.class);
            setupConstructor.setAccessible(true);
            Object replacement = setupConstructor.newInstance(field(setup, "pipeline"), replacements,
                    field(setup, "useLightmap"), field(setup, "useOverlay"), field(setup, "layeringTransform"),
                    field(setup, "outputTarget"), field(setup, "textureTransform"), field(setup, "outlineProperty"),
                    field(setup, "affectsCrumbling"), field(setup, "sortOnUpload"));
            Constructor<RenderType> typeConstructor = RenderType.class.getDeclaredConstructor(String.class, RenderSetup.class);
            typeConstructor.setAccessible(true);
            return typeConstructor.newInstance("buildscape_festive_" + field(original, "name"), replacement);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Cannot construct the Minecraft 26.2 festive glint render setup", exception);
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
