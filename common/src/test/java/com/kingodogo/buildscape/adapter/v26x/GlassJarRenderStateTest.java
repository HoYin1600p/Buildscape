package com.kingodogo.buildscape.adapter.v26x;

import com.kingodogo.buildscape.adapter.v26x.client.GlassJarRenderer;
import com.kingodogo.buildscape.adapter.v26x.client.GlassJarSpecialRenderer;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GlassJarRenderStateTest {
    @Test void liquidNeverExtendsOutsideTheJarAndXpUsesItsOwnCapacity() {
        assertEquals(0.07F, GlassJarRenderer.fillHeight(-1, 16), 0.00001F);
        assertEquals(0.72F, GlassJarRenderer.fillHeight(64, 16), 0.00001F);
        assertEquals(0.72F, GlassJarRenderer.fillHeight(3, 3), 0.00001F);
        assertEquals(GlassJarRenderer.fillHeight(8, 16), GlassJarRenderer.fillHeight(1, 2), 0.00001F);
    }

    @Test void inventoryExtentsEncloseTheBodyAndLid() {
        var corners = new ArrayList<Vector3f>();
        new GlassJarSpecialRenderer().getExtents(point -> corners.add(new Vector3f(point)));
        assertEquals(8, corners.size());
        assertTrue(corners.contains(new Vector3f(0.25F, 0, 0.25F)));
        assertTrue(corners.contains(new Vector3f(0.75F, 0.875F, 0.75F)));
        assertEquals(8, corners.stream().distinct().count());
    }
}
