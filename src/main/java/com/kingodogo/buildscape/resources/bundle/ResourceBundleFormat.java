package com.kingodogo.buildscape.resources.bundle;

public final class ResourceBundleFormat {

    public static final int MAGIC = 0x42535242; // "BSRB"
    public static final short CURRENT_VERSION = 1;

    public enum SectionType {
        MODELS((short) 1),
        BLOCKSTATES((short) 2),
        TAGS((short) 3);

        private final short id;

        SectionType(short id) {
            this.id = id;
        }

        public short getId() {
            return id;
        }

        public static SectionType fromId(short id) {
            SectionType type = fromIdOrNull(id);
            if (type != null) {
                return type;
            }
            throw new IllegalArgumentException("Unknown SectionType id: " + id);
        }

        public static SectionType fromIdOrNull(short id) {
            for (SectionType type : values()) {
                if (type.id == id) {
                    return type;
                }
            }
            return null;
        }
    }

    // Decoder bounds
    public static final int MAX_TOTAL_RECORDS = 100_000;
    public static final int MAX_STRING_POOL_COUNT = 65_536;
    public static final int MAX_STRING_LENGTH_BYTES = 32_768;
    public static final int MAX_SECTION_COUNT = 16;
    public static final int MAX_SECTION_RECORDS = 65_536;
    public static final int MAX_PAYLOAD_SIZE = 64 * 1024 * 1024; // 64 MB
    public static final int MAX_ELEMENTS_PER_MODEL = 256;
    public static final int MAX_VARIANTS_PER_BLOCKSTATE = 512;
    public static final int MAX_TAG_ENTRIES = 8_192;
    public static final int MAX_GEOMETRY_TEMPLATES = 1_024;
    public static final int MAX_VARIANT_TEMPLATES = 1_024;

    private ResourceBundleFormat() {}
}
