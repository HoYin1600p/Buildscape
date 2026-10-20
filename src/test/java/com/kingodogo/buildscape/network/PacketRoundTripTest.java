package com.kingodogo.buildscape.network;

import com.google.gson.Gson;
import com.kingodogo.buildscape.feature.biome.ClearBiomeBrushPacket;
import com.kingodogo.buildscape.feature.homemaker.SyncHomemakerCooldownPacket;
import com.kingodogo.buildscape.feature.pillar.PillarIdManager;
import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Encode/decode round trips for the packets that no other test covers: the decoded packet must hold the same field
 * values as the original, the whole buffer must be consumed, and the limits in {@link NetworkPacketLimits} must
 * reject oversized input on both the encode and the decode side. Packets covered elsewhere: SyncConfig, UpdateConfig,
 * EditRefused and SyncGameRules (SyncConfigProtocolTest), SyncPillarIds, EditPillarRows, EditPillarRowsResult and
 * RemovePillar (PillarRegistryPacketTest), UpdatePillarData (PillarDetailPacketTest), SyncBiomeTint
 * (SyncBiomeTintPacketTest).
 */
public class PacketRoundTripTest extends BootstrappedGameTest {

    private static FriendlyByteBuf newBuffer() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }

    /** Encodes with {@code encoder}, decodes with {@code decoder}, checks the buffer was fully read. */
    private static <T> T roundTrip(T packet, Consumer<FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder) {
        FriendlyByteBuf buffer = newBuffer();
        try {
            encoder.accept(buffer);
            T decoded = decoder.apply(buffer);
            assertEquals("unread bytes after decoding " + packet.getClass().getSimpleName(), 0,
                    buffer.readableBytes());
            return decoded;
        } finally {
            buffer.release();
        }
    }

    /** Compares every instance field of two packets reflectively; arrays are compared by content. */
    private static void assertSameFields(Object expected, Object actual) {
        assertEquals(expected.getClass(), actual.getClass());
        for (Field field : expected.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            try {
                assertTrue("field " + field.getName() + " differs: " + field.get(expected) + " vs "
                        + field.get(actual), Objects.deepEquals(field.get(expected), field.get(actual)));
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            }
        }
    }

    private static Object field(Object target, String name) {
        try {
            Field field = target.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(target);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static String repeat(int length) {
        return "x".repeat(length);
    }

    @Test
    public void actionBarMessagePacketRoundTrips() {
        Component message = new TextComponent("Built ").append(new TranslatableComponent("block.minecraft.stone"));
        ActionBarMessagePacket original = new ActionBarMessagePacket(message);
        assertSameFields(original, roundTrip(original, original::encode, ActionBarMessagePacket::decode));
    }

    @Test
    public void buildersWorkbenchResultsPacketRoundTripsWithEmptySlots() {
        List<ItemStack> results = new ArrayList<>(Collections.nCopies(9, ItemStack.EMPTY));
        results.set(0, new ItemStack(Items.STONE));
        results.set(4, new ItemStack(Items.OAK_PLANKS));
        results.set(8, new ItemStack(Items.DIAMOND));
        int[] offsets = {0, 1, 2, 3, 4, 5, 6, 7, NetworkPacketLimits.MAX_RESULT_OFFSET};
        BlockPos pos = new BlockPos(-30000000, -64, 29999999);
        BuildersWorkbenchResultsPacket original = new BuildersWorkbenchResultsPacket(pos, 7, 255, offsets, results);
        BuildersWorkbenchResultsPacket decoded = roundTrip(original, original::encode,
                BuildersWorkbenchResultsPacket::decode);
        assertSameFields(original, decoded);
        assertNull(((List<?>) field(decoded, "results")).get(1));
    }

    @Test
    public void buildersWorkbenchResultsPacketPadsShortInputsAndClampsNegativeOffsets() {
        BuildersWorkbenchResultsPacket original = new BuildersWorkbenchResultsPacket(BlockPos.ZERO, 0, 0,
                new int[]{-5, 3}, List.of(new ItemStack(Items.STONE)));
        BuildersWorkbenchResultsPacket decoded = roundTrip(original, original::encode,
                BuildersWorkbenchResultsPacket::decode);
        assertSameFields(original, decoded);
        assertTrue(Arrays.equals(new int[]{0, 3, 0, 0, 0, 0, 0, 0, 0}, (int[]) field(decoded, "offsets")));
    }

    @Test
    public void buildersWorkbenchResultsPacketRejectsOffsetsAboveTheLimit() {
        BuildersWorkbenchResultsPacket original = new BuildersWorkbenchResultsPacket(BlockPos.ZERO, 0, 0,
                new int[]{NetworkPacketLimits.MAX_RESULT_OFFSET + 1}, List.of());
        FriendlyByteBuf buffer = newBuffer();
        try {
            original.encode(buffer);
            try {
                BuildersWorkbenchResultsPacket.decode(buffer);
                fail("an offset above MAX_RESULT_OFFSET must be rejected");
            } catch (DecoderException expected) {
                // limit enforced
            }
        } finally {
            buffer.release();
        }
    }

    @Test
    public void confettiBurstPacketRoundTripsAndClampsTheBurstLevel() {
        ConfettiBurstPacket original = new ConfettiBurstPacket(1.5, -64.25, 1.0e7, 0.25f, -1.0f, 0.5f, 3,
                Long.MIN_VALUE);
        ConfettiBurstPacket decoded = roundTrip(original, buffer -> ConfettiBurstPacket.encode(original, buffer),
                ConfettiBurstPacket::decode);
        assertSameFields(original, decoded);

        assertEquals(5, field(new ConfettiBurstPacket(0, 0, 0, 0, 0, 0, 99, 1L), "burstLevel"));
        assertEquals(1, field(new ConfettiBurstPacket(0, 0, 0, 0, 0, 0, -4, 1L), "burstLevel"));
        ConfettiBurstPacket top = new ConfettiBurstPacket(0, 0, 0, 0, 0, 0, 5, Long.MAX_VALUE);
        assertSameFields(top, roundTrip(top, buffer -> ConfettiBurstPacket.encode(top, buffer),
                ConfettiBurstPacket::decode));
    }

    @Test
    public void requestPillarIdsPacketHasNoPayload() {
        RequestPillarIdsPacket original = new RequestPillarIdsPacket();
        FriendlyByteBuf buffer = newBuffer();
        try {
            original.encode(buffer);
            assertEquals(0, buffer.readableBytes());
            assertTrue(RequestPillarIdsPacket.decode(buffer) != null);
        } finally {
            buffer.release();
        }
    }

    @Test
    public void resetPillarsPacketRoundTrips() {
        List<String> ids = List.of("A-1", "B-2", repeat(NetworkPacketLimits.MAX_PILLAR_ID_LENGTH));
        ResetPillarsPacket original = new ResetPillarsPacket(ids);
        assertSameFields(original, roundTrip(original, original::encode, ResetPillarsPacket::decode));

        ResetPillarsPacket empty = new ResetPillarsPacket(List.of());
        assertSameFields(empty, roundTrip(empty, empty::encode, ResetPillarsPacket::decode));
        assertEquals(List.of(), field(new ResetPillarsPacket((java.util.Collection<String>) null), "pillarIds"));
    }

    @Test
    public void resetPillarsPacketEnforcesIdLengthAndCountLimits() {
        FriendlyByteBuf buffer = newBuffer();
        try {
            new ResetPillarsPacket(List.of(repeat(NetworkPacketLimits.MAX_PILLAR_ID_LENGTH + 1))).encode(buffer);
            fail("an over-long pillar id must be rejected when encoding");
        } catch (EncoderException expected) {
            // limit enforced
        } finally {
            buffer.release();
        }

        List<String> tooMany = Collections.nCopies(NetworkPacketLimits.MAX_PILLARS + 1, "A");
        FriendlyByteBuf tooManyBuffer = newBuffer();
        try {
            new ResetPillarsPacket(tooMany).encode(tooManyBuffer);
            fail("more than MAX_PILLARS ids must be rejected when encoding");
        } catch (EncoderException expected) {
            // limit enforced
        } finally {
            tooManyBuffer.release();
        }

        for (int count : new int[]{NetworkPacketLimits.MAX_PILLARS + 1, -1}) {
            FriendlyByteBuf hostile = newBuffer();
            try {
                hostile.writeInt(count);
                ResetPillarsPacket.decode(hostile);
                fail("count " + count + " must be rejected when decoding");
            } catch (DecoderException expected) {
                // limit enforced
            } finally {
                hostile.release();
            }
        }
    }

    @Test
    public void syncSignFramePacketRoundTripsAndEnforcesTheFrameIdLimit() {
        SyncSignFramePacket original = new SyncSignFramePacket(new BlockPos(12, 70, -9),
                repeat(NetworkPacketLimits.MAX_FRAME_ID_LENGTH));
        assertSameFields(original, roundTrip(original, original::encode, SyncSignFramePacket::decode));

        SyncSignFramePacket noFrame = new SyncSignFramePacket(BlockPos.ZERO, null);
        SyncSignFramePacket decoded = roundTrip(noFrame, noFrame::encode, SyncSignFramePacket::decode);
        assertSameFields(noFrame, decoded);
        assertEquals("", field(decoded, "frameId"));

        FriendlyByteBuf buffer = newBuffer();
        try {
            new SyncSignFramePacket(BlockPos.ZERO, repeat(NetworkPacketLimits.MAX_FRAME_ID_LENGTH + 1))
                    .encode(buffer);
            fail("an over-long frame id must be rejected when encoding");
        } catch (EncoderException expected) {
            // limit enforced
        } finally {
            buffer.release();
        }
    }

    @Test
    public void treeChopPacketRoundTrips() {
        for (BlockPos pos : new BlockPos[]{BlockPos.ZERO, new BlockPos(-1, -64, 1), new BlockPos(1000, 319, -2000)}) {
            TreeChopPacket original = new TreeChopPacket(pos);
            assertSameFields(original, roundTrip(original, original::encode, TreeChopPacket::decode));
        }
    }

    @Test
    public void updateAllPillarIdsPacketRoundTripsPillarRecords() {
        PillarIdManager.PillarData full = new PillarIdManager.PillarData("S-P1", "minecraft:overworld",
                new BlockPos(5, 64, -7));
        full.dyeColors = new ArrayList<>(List.of("#112233", "#445566"));
        full.createdTime = 123L;
        full.modifiedTime = 456L;
        full.use_pattern = true;
        full.pattern = "spiral";
        full.pattern_speed = 1.5;
        full.max_particle_color = 2;
        full.displayedItem = "minecraft:stone";
        full.facing = "north";
        full.itemYaw = 90.0f;
        PillarIdManager.PillarData minimal = new PillarIdManager.PillarData("B-2", "minecraft:the_nether",
                BlockPos.ZERO);

        UpdateAllPillarIdsPacket original = new UpdateAllPillarIdsPacket(List.of(full, minimal));
        UpdateAllPillarIdsPacket decoded = roundTrip(original, original::encode, UpdateAllPillarIdsPacket::decode);
        Gson gson = new Gson();
        assertEquals(gson.toJson(field(original, "pillarDataList")), gson.toJson(field(decoded, "pillarDataList")));
        assertEquals(2, ((List<?>) field(decoded, "pillarDataList")).size());

        UpdateAllPillarIdsPacket empty = new UpdateAllPillarIdsPacket(List.<PillarIdManager.PillarData>of());
        UpdateAllPillarIdsPacket decodedEmpty = roundTrip(empty, empty::encode, UpdateAllPillarIdsPacket::decode);
        assertEquals(0, ((List<?>) field(decodedEmpty, "pillarDataList")).size());
    }

    @Test
    public void updateAllPillarIdsPacketEnforcesCountAndRecordLimits() {
        List<PillarIdManager.PillarData> tooMany = new ArrayList<>();
        for (int i = 0; i <= NetworkPacketLimits.MAX_PILLARS; i++) {
            tooMany.add(new PillarIdManager.PillarData());
        }
        FriendlyByteBuf buffer = newBuffer();
        try {
            new UpdateAllPillarIdsPacket(tooMany).encode(buffer);
            fail("more than MAX_PILLARS records must be rejected when encoding");
        } catch (EncoderException expected) {
            // limit enforced
        } finally {
            buffer.release();
        }

        FriendlyByteBuf hostile = newBuffer();
        try {
            hostile.writeInt(NetworkPacketLimits.MAX_PILLARS + 1);
            UpdateAllPillarIdsPacket.decode(hostile);
            fail("an oversized count must be rejected when decoding");
        } catch (DecoderException expected) {
            // limit enforced
        } finally {
            hostile.release();
        }

        FriendlyByteBuf missingId = newBuffer();
        try {
            missingId.writeInt(1);
            missingId.writeUtf("{\"dimension\":\"minecraft:overworld\"}", NetworkPacketLimits.MAX_PILLAR_JSON_LENGTH);
            UpdateAllPillarIdsPacket.decode(missingId);
            fail("a record without an id must be rejected when decoding");
        } catch (IllegalArgumentException expected) {
            // record rejected
        } finally {
            missingId.release();
        }
    }

    @Test
    public void updateGameRulePacketRoundTripsAndEnforcesTheNameLimit() {
        for (boolean value : new boolean[]{true, false}) {
            UpdateGameRulePacket original = new UpdateGameRulePacket("fastLeafDecay", value);
            assertSameFields(original, roundTrip(original, original::encode, UpdateGameRulePacket::decode));
        }
        UpdateGameRulePacket longest = new UpdateGameRulePacket(repeat(NetworkPacketLimits.MAX_RULE_NAME_LENGTH),
                true);
        assertSameFields(longest, roundTrip(longest, longest::encode, UpdateGameRulePacket::decode));

        FriendlyByteBuf buffer = newBuffer();
        try {
            new UpdateGameRulePacket(repeat(NetworkPacketLimits.MAX_RULE_NAME_LENGTH + 1), true).encode(buffer);
            fail("an over-long rule name must be rejected when encoding");
        } catch (EncoderException expected) {
            // limit enforced
        } finally {
            buffer.release();
        }

        FriendlyByteBuf nullName = newBuffer();
        try {
            new UpdateGameRulePacket(null, true).encode(nullName);
            fail("a null rule name must be rejected when encoding");
        } catch (EncoderException expected) {
            // limit enforced
        } finally {
            nullName.release();
        }
    }

    @Test
    public void clearBiomeBrushPacketHasNoPayload() {
        FriendlyByteBuf buffer = newBuffer();
        try {
            ClearBiomeBrushPacket.encode(new ClearBiomeBrushPacket(), buffer);
            assertEquals(0, buffer.readableBytes());
            assertTrue(ClearBiomeBrushPacket.decode(buffer) != null);
        } finally {
            buffer.release();
        }
    }

    @Test
    public void syncHomemakerCooldownPacketRoundTrips() {
        for (long time : new long[]{0L, 1L, -1L, 24_000L, Long.MIN_VALUE, Long.MAX_VALUE}) {
            SyncHomemakerCooldownPacket original = new SyncHomemakerCooldownPacket(time);
            assertSameFields(original, roundTrip(original, original::encode, SyncHomemakerCooldownPacket::decode));
        }
    }

    @Test
    public void rotateBlockPacketRoundTripsEveryArrowAndFacing() {
        for (RotateBlockPacket.ArrowDirection arrow : RotateBlockPacket.ArrowDirection.values()) {
            for (Direction facing : Direction.values()) {
                RotateBlockPacket original = new RotateBlockPacket(new BlockPos(-7, 100, 3000), arrow, facing);
                assertSameFields(original, roundTrip(original, buffer -> RotateBlockPacket.encode(original, buffer),
                        RotateBlockPacket::decode));
            }
        }
    }

    @Test
    public void rotateBlockPacketFallsBackToUpForAnUnknownArrow() {
        FriendlyByteBuf buffer = newBuffer();
        try {
            buffer.writeBlockPos(BlockPos.ZERO);
            buffer.writeByte(99);
            buffer.writeByte(Direction.NORTH.get3DDataValue());
            RotateBlockPacket decoded = RotateBlockPacket.decode(buffer);
            assertEquals(RotateBlockPacket.ArrowDirection.UP, field(decoded, "direction"));
            assertEquals(Direction.NORTH, field(decoded, "playerFacing"));
            assertEquals(0, buffer.readableBytes());
        } finally {
            buffer.release();
        }
    }

    @Test
    public void hammerReplacePacketRoundTrips() {
        for (BlockPos pos : new BlockPos[]{BlockPos.ZERO, new BlockPos(-30000000, -64, 29999999),
                new BlockPos(123, 319, -456)}) {
            HammerReplacePacket original = new HammerReplacePacket(pos);
            assertSameFields(original, roundTrip(original, buffer -> HammerReplacePacket.encode(original, buffer),
                    HammerReplacePacket::decode));
        }
    }
}
