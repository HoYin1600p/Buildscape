package com.kingodogo.buildscape.network;

import net.minecraft.network.FriendlyByteBuf;

public final class NetworkPacketLimits {
    public static final int MAX_PILLARS = 16_384;
    public static final int MAX_CONFIG_ITEMS = 65_536;
    public static final int MAX_DYE_COLORS = 16;
    public static final int MAX_PILLAR_ID_LENGTH = 512;
    public static final int MAX_PATTERN_LENGTH = 64;
    public static final int MAX_RESOURCE_ID_LENGTH = 256;
    public static final int MAX_FRAME_ID_LENGTH = 128;
    public static final int MAX_RULE_NAME_LENGTH = 64;
    public static final int MAX_PILLAR_JSON_LENGTH = 32_767;
    public static final int MAX_RESULT_OFFSET = 1_000_000;
    public static final double MIN_PARTICLE_RATE = 0.001D;
    public static final double MAX_PARTICLE_SPEED = 4.0D;
    public static final double MAX_PARTICLE_SPREAD = 16.0D;
    public static final double MAX_PARTICLE_INTENSITY = 64.0D;
    public static final int MAX_PARTICLE_LIFETIME = 12_000;
    public static final int MAX_PARTICLE_DENSITY = 128;

    private NetworkPacketLimits() {}

    public static int readCount(FriendlyByteBuf buffer, int maximum, String field) {
        int count = buffer.readInt();
        if (count < 0 || count > maximum) {
            throw new IllegalArgumentException(field + " count outside allowed range: " + count);
        }
        return count;
    }

    public static void checkCount(int count, int maximum, String field) {
        if (count < 0 || count > maximum) {
            throw new IllegalArgumentException(field + " count outside allowed range: " + count);
        }
    }

    public static String readUtf(FriendlyByteBuf buffer, int maximum, String field) {
        String value = buffer.readUtf(maximum);
        if (value.length() > maximum) {
            throw new IllegalArgumentException(field + " exceeds maximum length");
        }
        return value;
    }

    public static void writeUtf(FriendlyByteBuf buffer, String value, int maximum, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " cannot be null");
        }
        if (value.length() > maximum) {
            throw new IllegalArgumentException(field + " exceeds maximum length");
        }
        buffer.writeUtf(value, maximum);
    }

    public static double readFiniteDouble(FriendlyByteBuf buffer, String field) {
        double value = buffer.readDouble();
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(field + " must be finite");
        }
        return value;
    }

    public static double readBoundedDouble(FriendlyByteBuf buffer, double minimum, double maximum, String field) {
        double value = readFiniteDouble(buffer, field);
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(field + " outside allowed range: " + value);
        }
        return value;
    }

    public static double clamp(double value, double minimum, double maximum) {
        if (!Double.isFinite(value)) {
            return minimum;
        }
        return Math.max(minimum, Math.min(maximum, value));
    }

    public static int readBoundedInt(FriendlyByteBuf buffer, int minimum, int maximum, String field) {
        int value = buffer.readInt();
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(field + " outside allowed range: " + value);
        }
        return value;
    }

    public static int readResultOffset(FriendlyByteBuf buffer) {
        int value = buffer.readVarInt();
        if (value < 0 || value > MAX_RESULT_OFFSET) {
            throw new IllegalArgumentException("result offset outside allowed range: " + value);
        }
        return value;
    }
}
