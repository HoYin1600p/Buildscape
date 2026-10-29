package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.block.GlassJarBlockEntity;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public final class GlassJarSpecialRenderer implements SpecialModelRenderer<GlassJarRenderer.State> {
    @Override public GlassJarRenderer.State extractArgument(ItemStack stack) {
        var block = stack.getItem() instanceof BlockItem item ? item.getBlock()
                : Services.PLATFORM.getBlock(CommonId.of("buildscape", "glass_jar"));
        GlassJarBlockEntity[] created = new GlassJarBlockEntity[1];
        Services.PLATFORM.wrapRegistryAction(() -> created[0] = new GlassJarBlockEntity(BlockPos.ZERO, block.defaultBlockState()));
        var jar = created[0];
        var client = Minecraft.getInstance();
        var data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data != null && client.level != null) data.loadInto(jar, client.level.registryAccess());
        GlassJarRenderer.State state = new GlassJarRenderer.State();
        GlassJarRenderer.extractContents(jar, state, 0, true);
        return state;
    }

    @Override public void submit(GlassJarRenderer.State state, PoseStack pose, SubmitNodeCollector collector,
            int light, int overlay, boolean foil, int outlineColor) {
        GlassJarRenderer.submitContents(state, pose, collector, light, overlay, outlineColor);
    }

    @Override public void getExtents(Consumer<Vector3fc> output) {
        for (float x : new float[] {0.25F, 0.75F}) {
            for (float y : new float[] {0, 0.875F}) {
                for (float z : new float[] {0.25F, 0.75F}) output.accept(new Vector3f(x, y, z));
            }
        }
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<GlassJarRenderer.State> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());
        @Override public MapCodec<Unbaked> type() { return MAP_CODEC; }
        @Override public GlassJarSpecialRenderer bake(BakingContext context) { return new GlassJarSpecialRenderer(); }
    }
}
