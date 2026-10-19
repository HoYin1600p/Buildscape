package com.kingodogo.buildscape.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector4f;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.SimpleBakedModel;
import org.junit.Test;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;

public class GuiItemGridBatchTest {
    @Test
    public void sharedTransformMatchesVanillaGuiItemAtEveryScale() {
        for (float scale : new float[]{1, 0.8f, 0.5f}) {
            for (int x : new int[]{0, 123, -12}) {
                PoseStack vanilla = new PoseStack();
                vanilla.scale(scale, scale, 1);
                vanilla.translate(x + 8, 81 + 8, 107);
                vanilla.scale(16, -16, 16);

                PoseStack common = new PoseStack();
                GuiItemGridBatch.applyGridTransform(common, scale);
                PoseStack local = new PoseStack();
                GuiItemGridBatch.positionIcon(local, x, 81, 107);
                Matrix4f combined = common.last().pose().copy();
                combined.multiply(local.last().pose());
                for (float z : new float[]{-0.5f, 0, 0.5f}) {
                    Vector4f expected = new Vector4f(0.25f, -0.4f, z, 1);
                    Vector4f actual = new Vector4f(0.25f, -0.4f, z, 1);
                    expected.transform(vanilla.last().pose());
                    actual.transform(combined);
                    assertEquals(expected.x(), actual.x(), 0.0001f);
                    assertEquals(expected.y(), actual.y(), 0.0001f);
                    assertEquals(expected.z(), actual.z(), 0.0001f);
                }
            }
        }
    }

    @Test
    public void modelWrappersAndCustomRenderersKeepVanillaPath() {
        SimpleBakedModel plain = new SimpleBakedModel(List.of(), Map.of(), false, true, true,
                null, ItemTransforms.NO_TRANSFORMS, ItemOverrides.EMPTY);
        SimpleBakedModel wrapped = new SimpleBakedModel(List.of(), Map.of(), false, true, true,
                null, ItemTransforms.NO_TRANSFORMS, ItemOverrides.EMPTY) {};
        assertTrue(GuiItemGridBatch.canBatch(plain));
        assertFalse(GuiItemGridBatch.canBatch(wrapped));
        assertFalse(GuiItemGridBatch.canBatch(null));
    }
}
