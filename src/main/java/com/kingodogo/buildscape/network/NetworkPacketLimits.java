package com.kingodogo.buildscape.network;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.FriendlyByteBuf;

final class NetworkPacketLimits {
    static final int MAX_PILLARS = 16_384;
    static final int MAX_CONFIG_ITEMS = 65_536;
    static final int MAX_DYE_COLORS = 16;
    static final int MAX_PILLAR_ID_LENGTH = 512;
    static final int MAX_PATTERN_LENGTH = 64;
    static final int MAX_RESOURCE_ID_LENGTH = 256;
    static final int MAX_FRAME_ID_LENGTH = 128;
    static final int MAX_RULE_NAME_LENGTH = 64;
    static final int MAX_PILLAR_JSON_LENGTH = 32_767;
    static final int MAX_RESULT_OFFSET = 1_000_000;

    private NetworkPacketLimits() {
    }

    static int readCount(FriendlyByteBuf buffer, int maximum, String field) {
        int count = buffer.readInt();
        if (count < 0 || count > maximum) {
            throw new DecoderException(field + " count outside allowed range: " + count);
        }
        return count;
    }

    static void checkCount(int count, int maximum, String field) {
        if (count < 0 || count > maximum) {
            throw new EncoderException(field + " count outside allowed range: " + count);
        }
    }

    static String readUtf(FriendlyByteBuf buffer, int maximum, String field) {
        String value = buffer.readUtf(maximum);
        if (value.length() > maximum) {
            throw new DecoderException(field + " exceeds maximum length");
        }
        return value;
    }

    static void writeUtf(FriendlyByteBuf buffer, String value, int maximum, String field) {
        if (value == null) {
            throw new EncoderException(field + " cannot be null");
        }
        if (value.length() > maximum) {
            throw new EncoderException(field + " exceeds maximum length");
        }
        buffer.writeUtf(value, maximum);
    }

    static double readFiniteDouble(FriendlyByteBuf buffer, String field) {
        double value = buffer.readDouble();
        if (!Double.isFinite(value)) {
            throw new DecoderException(field + " must be finite");
        }
        return value;
    }

    static int readBoundedInt(FriendlyByteBuf buffer, int minimum, int maximum, String field) {
        int value = buffer.readInt();
        if (value < minimum || value > maximum) {
            throw new DecoderException(field + " outside allowed range: " + value);
        }
        return value;
    }

    static int readResultOffset(FriendlyByteBuf buffer) {
        int value = buffer.readVarInt();
        if (value < 0 || value > MAX_RESULT_OFFSET) {
            throw new DecoderException("result offset outside allowed range: " + value);
        }
        return value;
    }
}
