package com.kingodogo.buildscape.adapter.v26x.client;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

import java.util.function.BiConsumer;

/** Invoked exclusively from loader client entrypoints, before item definitions are decoded. */
public final class SpecialItemRenderers {
    private SpecialItemRenderers() {}

    public static void register(BiConsumer<Identifier, MapCodec<? extends SpecialModelRenderer.Unbaked<?>>> registrar) {
        registrar.accept(Identifier.fromNamespaceAndPath("buildscape", "glass_jar"), GlassJarSpecialRenderer.Unbaked.MAP_CODEC);
    }

    /** Fabric 26.2 has no special renderer registration event; extend vanilla's codec dispatch. */
    @SuppressWarnings("unchecked")
    public static void registerFabric() {
        try {
            var field = SpecialModelRenderers.class.getDeclaredField("ID_MAPPER");
            field.setAccessible(true);
            var mapper = (ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends SpecialModelRenderer.Unbaked<?>>>) field.get(null);
            register(mapper::put);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Cannot register Buildscape special item model codecs", exception);
        }
    }
}
