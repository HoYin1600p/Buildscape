package com.kingodogo.buildscape.resources.bundle;

import com.kingodogo.buildscape.resources.bundle.blockstate.BlockStateIR;
import com.kingodogo.buildscape.resources.bundle.model.ModelIR;
import com.kingodogo.buildscape.resources.bundle.tag.TagIR;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResourceBundleContainerTest {

    private static final byte[] DUMMY_DIGEST = new byte[32];
    static {
        for (int i = 0; i < 32; i++) {
            DUMMY_DIGEST[i] = (byte) (i + 1);
        }
    }

    @Test
    void testContainerRoundTrip() throws IOException {
        ResourceBundleContainer.Writer writer = new ResourceBundleContainer.Writer();

        // Add a geometry template
        ModelIR.ElementIR el = new ModelIR.ElementIR(
                new float[]{0, 0, 0},
                new float[]{16, 8, 16},
                null,
                true,
                Map.of((byte) 0, new ModelIR.FaceIR(new float[]{0, 0, 16, 16}, "#bottom", (byte) 0, 0, -1)));
        writer.addGeometryTemplate(1, List.of(el));

        // Add a model referencing the template
        ModelIR model = new ModelIR(
                "buildscape:block/acacia_log_slab",
                "minecraft:block/slab",
                true,
                ModelIR.GUI_LIGHT_SIDE,
                Map.of("bottom", "minecraft:block/acacia_log_top"),
                1,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyMap());
        writer.addModel(model);

        // Add a blockstate with variants
        BlockStateIR bs = new BlockStateIR(
                "buildscape:acacia_log_slab",
                BlockStateIR.TYPE_VARIANTS,
                -1,
                null,
                Map.of("type=bottom",
                        List.of(new BlockStateIR.VariantModelIR("buildscape:block/acacia_log_slab", 0, 0, false, 1))),
                Collections.emptyList());
        writer.addBlockState(bs);

        // Add a tag
        TagIR tag = new TagIR(
                "blocks",
                "buildscape:slabs",
                false,
                List.of(new TagIR.TagEntryIR("buildscape:acacia_log_slab", false, true)));
        writer.addTag(tag);

        byte[] bytes = writer.write(DUMMY_DIGEST);
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);

        // Read back
        ResourceBundleContainer.Reader reader = new ResourceBundleContainer.Reader(new ByteArrayInputStream(bytes),
                DUMMY_DIGEST);
        assertEquals(ResourceBundleFormat.CURRENT_VERSION, reader.getVersion());
        assertArrayEquals(DUMMY_DIGEST, reader.getDigest());

        assertEquals(1, reader.getModels().size());
        ModelIR readModel = reader.getModels().get("buildscape:block/acacia_log_slab");
        assertNotNull(readModel);
        assertEquals("minecraft:block/slab", readModel.parent());
        assertTrue(readModel.ambientOcclusion());
        assertEquals(ModelIR.GUI_LIGHT_SIDE, readModel.guiLight());
        assertEquals("minecraft:block/acacia_log_top", readModel.textures().get("bottom"));
        assertEquals(1, readModel.elements().size()); // Resolved from template!
        assertEquals(16, readModel.elements().get(0).to()[0]);

        assertEquals(1, reader.getBlockStates().size());
        BlockStateIR readBs = reader.getBlockStates().get("buildscape:acacia_log_slab");
        assertNotNull(readBs);
        assertEquals(BlockStateIR.TYPE_VARIANTS, readBs.type());
        assertTrue(readBs.variants().containsKey("type=bottom"));

        assertEquals(1, reader.getTags().size());
        TagIR readTag = reader.getTags().get(0);
        assertEquals("blocks", readTag.registry());
        assertEquals("buildscape:slabs", readTag.id());
        assertEquals(1, readTag.entries().size());
        assertEquals("buildscape:acacia_log_slab", readTag.entries().get(0).id());
    }

    @Test
    void testCorruptedHeaderThrowsException() {
        byte[] badMagic = new byte[64];
        assertThrows(IOException.class, () -> new ResourceBundleContainer.Reader(new ByteArrayInputStream(badMagic)));
    }

    @Test
    void testInvalidUtf8InStringPoolThrowsException() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        out.writeInt(ResourceBundleFormat.MAGIC);
        out.writeShort(ResourceBundleFormat.CURRENT_VERSION);
        out.write(DUMMY_DIGEST);

        // String pool with invalid UTF-8 bytes
        out.writeInt(1); // 1 string
        out.writeShort(2); // 2 bytes
        out.writeByte((byte) 0xFF); // Invalid UTF-8 leading byte
        out.writeByte((byte) 0xFF);
        out.writeShort(0); // 0 sections
        out.flush();

        byte[] data = baos.toByteArray();
        assertThrows(IOException.class, () -> new ResourceBundleContainer.Reader(new ByteArrayInputStream(data)));
    }
}
