package com.kingodogo.buildscape.resources.bundle;

import com.kingodogo.buildscape.resources.bundle.ResourceBundleFormat.SectionType;
import com.kingodogo.buildscape.resources.bundle.blockstate.BlockStateIR;
import com.kingodogo.buildscape.resources.bundle.model.ModelIR;
import com.kingodogo.buildscape.resources.bundle.tag.TagIR;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ResourceBundleContainer {

    public static class StringPoolWriter {
        private final Map<String, Integer> stringToId = new HashMap<>();
        private final List<String> idToString = new ArrayList<>();

        public int getOrAdd(String s) {
            if (s == null) {
                return -1;
            }
            Integer id = stringToId.get(s);
            if (id != null) {
                return id;
            }
            if (idToString.size() >= ResourceBundleFormat.MAX_STRING_POOL_COUNT) {
                throw new IllegalStateException(
                        "String pool limit exceeded: " + ResourceBundleFormat.MAX_STRING_POOL_COUNT);
            }
            int newId = idToString.size();
            stringToId.put(s, newId);
            idToString.add(s);
            return newId;
        }

        public void write(DataOutputStream out) throws IOException {
            out.writeInt(idToString.size());
            for (String s : idToString) {
                byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
                if (bytes.length > ResourceBundleFormat.MAX_STRING_LENGTH_BYTES) {
                    throw new IOException("String too long for string pool: " + bytes.length);
                }
                out.writeShort(bytes.length);
                out.write(bytes);
            }
        }
    }

    public static class StringPoolReader {
        private final String[] pool;

        public StringPoolReader(DataInputStream in) throws IOException {
            int count = in.readInt();
            if (count < 0 || count > ResourceBundleFormat.MAX_STRING_POOL_COUNT) {
                throw new IOException("Invalid string pool count: " + count);
            }
            java.nio.charset.CharsetDecoder utf8Decoder = StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(java.nio.charset.CodingErrorAction.REPORT)
                    .onUnmappableCharacter(java.nio.charset.CodingErrorAction.REPORT);
            pool = new String[count];
            for (int i = 0; i < count; i++) {
                int len = in.readUnsignedShort();
                if (len > ResourceBundleFormat.MAX_STRING_LENGTH_BYTES) {
                    throw new IOException("String byte length exceeds limit: " + len);
                }
                byte[] bytes = in.readNBytes(len);
                if (bytes.length != len) {
                    throw new EOFException("Truncated string in pool at index " + i);
                }
                try {
                    pool[i] = utf8Decoder.decode(java.nio.ByteBuffer.wrap(bytes)).toString();
                } catch (java.nio.charset.CharacterCodingException e) {
                    throw new IOException("Malformed UTF-8 string at pool index " + i + ": " + e.getMessage(), e);
                }
            }
        }

        public String get(int id) throws IOException {
            if (id == -1) {
                return null;
            }
            if (id < 0 || id >= pool.length) {
                throw new IOException("String pool index out of bounds: " + id);
            }
            return pool[id];
        }

        public int size() {
            return pool.length;
        }
    }

    public static final class Writer {
        private final Map<String, ModelIR> models = new LinkedHashMap<>();
        private final Map<String, BlockStateIR> blockStates = new LinkedHashMap<>();
        private final List<TagIR> tags = new ArrayList<>();
        private final Map<Integer, List<ModelIR.ElementIR>> geometryTemplates = new HashMap<>();
        private final Map<Integer, Map<String, List<BlockStateIR.VariantModelIR>>> variantTemplates = new HashMap<>();

        public Writer() {}

        public void addModel(ModelIR model) {
            models.put(model.id(), model);
        }

        public void addBlockState(BlockStateIR blockState) {
            blockStates.put(blockState.id(), blockState);
        }

        public void addTag(TagIR tag) {
            tags.add(tag);
        }

        public void addGeometryTemplate(int id, List<ModelIR.ElementIR> elements) {
            geometryTemplates.put(id, elements);
        }

        public void addVariantTemplate(int id, Map<String, List<BlockStateIR.VariantModelIR>> template) {
            variantTemplates.put(id, template);
        }

        public byte[] write(byte[] contentDigest) throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writeTo(out, contentDigest);
            return out.toByteArray();
        }

        public void writeTo(OutputStream rawOut, byte[] contentDigest) throws IOException {
            if (contentDigest == null || contentDigest.length != 32) {
                throw new IllegalArgumentException("Content digest must be exactly 32 bytes SHA-256");
            }
            StringPoolWriter pool = new StringPoolWriter();

            // Prepare serialized sections in memory
            Map<SectionType, byte[]> sectionData = new LinkedHashMap<>();

            if (!models.isEmpty() || !geometryTemplates.isEmpty()) {
                sectionData.put(SectionType.MODELS, serializeModels(pool));
            }
            if (!blockStates.isEmpty() || !variantTemplates.isEmpty()) {
                sectionData.put(SectionType.BLOCKSTATES, serializeBlockStates(pool));
            }
            if (!tags.isEmpty()) {
                sectionData.put(SectionType.TAGS, serializeTags(pool));
            }

            DataOutputStream out = new DataOutputStream(rawOut);
            out.writeInt(ResourceBundleFormat.MAGIC);
            out.writeShort(ResourceBundleFormat.CURRENT_VERSION);
            out.write(contentDigest);

            pool.write(out);

            out.writeShort(sectionData.size());
            for (Map.Entry<SectionType, byte[]> entry : sectionData.entrySet()) {
                out.writeShort(entry.getKey().getId());
                byte[] data = entry.getValue();
                out.writeInt(data.length);
                out.write(data);
            }
            out.flush();
        }

        private byte[] serializeModels(StringPoolWriter pool) throws IOException {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bytes);

            // 1. Templates
            out.writeShort(geometryTemplates.size());
            for (Map.Entry<Integer, List<ModelIR.ElementIR>> tmpl : geometryTemplates.entrySet()) {
                out.writeShort(tmpl.getKey());
                writeElements(out, tmpl.getValue(), pool);
            }

            // 2. Models
            out.writeInt(models.size());
            for (ModelIR model : models.values()) {
                out.writeInt(pool.getOrAdd(model.id()));
                out.writeInt(pool.getOrAdd(model.parent()));
                byte aoByte = model.ambientOcclusion() == null
                        ? (byte) 0
                        : (model.ambientOcclusion() ? (byte) 2 : (byte) 1);
                out.writeByte(aoByte);
                out.writeByte(model.guiLight());
                out.writeShort(model.geometryTemplateId());

                out.writeShort(model.textures().size());
                for (Map.Entry<String, String> tex : model.textures().entrySet()) {
                    out.writeInt(pool.getOrAdd(tex.getKey()));
                    out.writeInt(pool.getOrAdd(tex.getValue()));
                }

                if (model.geometryTemplateId() < 0) {
                    writeElements(out, model.elements(), pool);
                }

                out.writeShort(model.overrides().size());
                for (ModelIR.OverrideIR ov : model.overrides()) {
                    out.writeInt(pool.getOrAdd(ov.model()));
                    out.writeShort(ov.predicates().size());
                    for (Map.Entry<String, Float> pred : ov.predicates().entrySet()) {
                        out.writeInt(pool.getOrAdd(pred.getKey()));
                        out.writeFloat(pred.getValue());
                    }
                }

                Map<String, ModelIR.TransformIR> transforms = model.transforms() != null
                        ? model.transforms()
                        : Collections.emptyMap();
                out.writeShort(transforms.size());
                for (Map.Entry<String, ModelIR.TransformIR> tr : transforms.entrySet()) {
                    out.writeInt(pool.getOrAdd(tr.getKey()));
                    ModelIR.TransformIR t = tr.getValue();
                    for (int k = 0; k < 3; k++) {
                        out.writeFloat(t.rotation()[k]);
                    }
                    for (int k = 0; k < 3; k++) {
                        out.writeFloat(t.translation()[k]);
                    }
                    for (int k = 0; k < 3; k++) {
                        out.writeFloat(t.scale()[k]);
                    }
                }
            }
            out.flush();
            return bytes.toByteArray();
        }

        private void writeElements(DataOutputStream out, List<ModelIR.ElementIR> elements, StringPoolWriter pool)
                throws IOException {
            if (elements == null) {
                out.writeShort(0);
                return;
            }
            out.writeShort(elements.size());
            for (ModelIR.ElementIR el : elements) {
                for (int i = 0; i < 3; i++) {
                    out.writeFloat(el.from()[i]);
                }
                for (int i = 0; i < 3; i++) {
                    out.writeFloat(el.to()[i]);
                }

                if (el.rotation() != null) {
                    out.writeBoolean(true);
                    for (int i = 0; i < 3; i++) {
                        out.writeFloat(el.rotation().origin()[i]);
                    }
                    out.writeByte(el.rotation().axis());
                    out.writeFloat(el.rotation().angle());
                    out.writeBoolean(el.rotation().rescale());
                } else {
                    out.writeBoolean(false);
                }

                out.writeBoolean(el.shade());

                // Faces bitmask
                byte mask = 0;
                for (Byte dir : el.faces().keySet()) {
                    if (dir >= 0 && dir < 6) {
                        mask |= (1 << dir);
                    }
                }
                out.writeByte(mask);

                for (byte dir = 0; dir < 6; dir++) {
                    if ((mask & (1 << dir)) != 0) {
                        ModelIR.FaceIR face = el.faces().get(dir);
                        if (face.uv() != null) {
                            out.writeBoolean(true);
                            for (int i = 0; i < 4; i++) {
                                out.writeFloat(face.uv()[i]);
                            }
                        } else {
                            out.writeBoolean(false);
                        }
                        out.writeInt(pool.getOrAdd(face.texture()));
                        if (face.cullface() != null) {
                            out.writeBoolean(true);
                            out.writeByte(face.cullface());
                        } else {
                            out.writeBoolean(false);
                        }
                        out.writeByte(face.rotation() / 90);
                        out.writeShort(face.tintIndex());
                    }
                }
            }
        }

        private byte[] serializeBlockStates(StringPoolWriter pool) throws IOException {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bytes);

            // Templates
            out.writeShort(variantTemplates.size());
            for (Map.Entry<Integer, Map<String, List<BlockStateIR.VariantModelIR>>> tmpl : variantTemplates
                    .entrySet()) {
                out.writeShort(tmpl.getKey());
                writeVariantsMap(out, tmpl.getValue(), pool);
            }

            // Blockstates
            out.writeInt(blockStates.size());
            for (BlockStateIR bs : blockStates.values()) {
                out.writeInt(pool.getOrAdd(bs.id()));
                out.writeByte(bs.type());

                if (bs.type() == BlockStateIR.TYPE_TEMPLATE) {
                    out.writeShort(bs.templateId());
                    out.writeInt(pool.getOrAdd(bs.templateParameter()));
                } else if (bs.type() == BlockStateIR.TYPE_VARIANTS) {
                    writeVariantsMap(out, bs.variants(), pool);
                } else if (bs.type() == BlockStateIR.TYPE_MULTIPART) {
                    out.writeShort(bs.multiparts().size());
                    for (BlockStateIR.MultiPartIR mp : bs.multiparts()) {
                        writeCondition(out, mp.condition(), pool);
                        writeVariantModelList(out, mp.models(), pool);
                    }
                }
            }
            out.flush();
            return bytes.toByteArray();
        }

        private void writeVariantsMap(DataOutputStream out, Map<String, List<BlockStateIR.VariantModelIR>> map,
                StringPoolWriter pool) throws IOException {
            if (map == null) {
                out.writeShort(0);
                return;
            }
            out.writeShort(map.size());
            for (Map.Entry<String, List<BlockStateIR.VariantModelIR>> entry : map.entrySet()) {
                out.writeInt(pool.getOrAdd(entry.getKey()));
                writeVariantModelList(out, entry.getValue(), pool);
            }
        }

        private void writeVariantModelList(DataOutputStream out, List<BlockStateIR.VariantModelIR> list,
                StringPoolWriter pool) throws IOException {
            if (list == null) {
                out.writeShort(0);
                return;
            }
            out.writeShort(list.size());
            for (BlockStateIR.VariantModelIR m : list) {
                out.writeInt(pool.getOrAdd(m.model()));
                out.writeByte(m.x() / 90);
                out.writeShort(m.y());
                out.writeBoolean(m.uvLock());
                out.writeShort(m.weight());
            }
        }

        private void writeCondition(DataOutputStream out, BlockStateIR.ConditionIR cond, StringPoolWriter pool)
                throws IOException {
            if (cond == null) {
                out.writeByte(-1);
                return;
            }
            out.writeByte(cond.kind());
            if (cond.kind() == BlockStateIR.ConditionIR.KIND_KEY_VALUE) {
                out.writeInt(pool.getOrAdd(cond.key()));
                out.writeInt(pool.getOrAdd(cond.value()));
            } else {
                out.writeShort(cond.terms().size());
                for (BlockStateIR.ConditionIR sub : cond.terms()) {
                    writeCondition(out, sub, pool);
                }
            }
        }

        private byte[] serializeTags(StringPoolWriter pool) throws IOException {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bytes);

            out.writeInt(tags.size());
            for (TagIR tag : tags) {
                out.writeInt(pool.getOrAdd(tag.registry()));
                out.writeInt(pool.getOrAdd(tag.id()));
                out.writeBoolean(tag.replace());
                out.writeShort(tag.entries().size());
                for (TagIR.TagEntryIR entry : tag.entries()) {
                    out.writeInt(pool.getOrAdd(entry.id()));
                    out.writeBoolean(entry.tag());
                    out.writeBoolean(entry.required());
                }
            }
            out.flush();
            return bytes.toByteArray();
        }
    }

    public static final class Reader {
        private final short version;
        private final byte[] digest;
        private final Map<String, ModelIR> models = new LinkedHashMap<>();
        private final Map<String, BlockStateIR> blockStates = new LinkedHashMap<>();
        private final List<TagIR> tags = new ArrayList<>();
        private final Map<Integer, List<ModelIR.ElementIR>> geometryTemplates = new HashMap<>();
        private final Map<Integer, Map<String, List<BlockStateIR.VariantModelIR>>> variantTemplates = new HashMap<>();
        private int totalRecords = 0;

        public Reader(InputStream in) throws IOException {
            this(in, null);
        }

        public Reader(InputStream in, byte[] expectedDigest) throws IOException {
            DataInputStream data = in instanceof DataInputStream dis ? dis : new DataInputStream(in);

            int magic = data.readInt();
            if (magic != ResourceBundleFormat.MAGIC) {
                throw new IOException("Invalid bundle magic header: 0x" + Integer.toHexString(magic));
            }
            this.version = data.readShort();
            if (this.version != ResourceBundleFormat.CURRENT_VERSION) {
                throw new IOException("Unsupported bundle format version: " + this.version);
            }

            this.digest = data.readNBytes(32);
            if (this.digest.length != 32) {
                throw new EOFException("Truncated digest in bundle header");
            }
            if (expectedDigest != null && !Arrays.equals(this.digest, expectedDigest)) {
                throw new IOException("Bundle header digest mismatch");
            }

            StringPoolReader pool = new StringPoolReader(data);

            int sectionCount = data.readUnsignedShort();
            if (sectionCount > ResourceBundleFormat.MAX_SECTION_COUNT) {
                throw new IOException("Too many sections: " + sectionCount);
            }

            java.util.Set<Short> seenSections = new java.util.HashSet<>();
            for (int s = 0; s < sectionCount; s++) {
                short sectionId = data.readShort();
                if (!seenSections.add(sectionId)) {
                    throw new IOException("Duplicate section ID: " + sectionId);
                }
                int sectionLength = data.readInt();
                if (sectionLength < 0 || sectionLength > ResourceBundleFormat.MAX_PAYLOAD_SIZE) {
                    throw new IOException("Invalid section length: " + sectionLength);
                }
                byte[] sectionBytes = data.readNBytes(sectionLength);
                if (sectionBytes.length != sectionLength) {
                    throw new EOFException(
                            "Truncated section payload: expected " + sectionLength + ", got " + sectionBytes.length);
                }

                SectionType sectionType = SectionType.fromIdOrNull(sectionId);
                if (sectionType == null) {
                    throw new IOException("Unknown SectionType id: " + sectionId);
                }
                DataInputStream sectionStream = new DataInputStream(new ByteArrayInputStream(sectionBytes));

                switch (sectionType) {
                    case MODELS -> readModels(sectionStream, pool);
                    case BLOCKSTATES -> readBlockStates(sectionStream, pool);
                    case TAGS -> readTags(sectionStream, pool);
                }
            }
        }

        private void trackRecords(int count) throws IOException {
            totalRecords += count;
            if (totalRecords > ResourceBundleFormat.MAX_TOTAL_RECORDS) {
                throw new IOException("Total bundle records budget exceeded (limit "
                        + ResourceBundleFormat.MAX_TOTAL_RECORDS + "): " + totalRecords);
            }
        }

        private void readModels(DataInputStream in, StringPoolReader pool) throws IOException {
            int templateCount = in.readUnsignedShort();
            if (templateCount > ResourceBundleFormat.MAX_GEOMETRY_TEMPLATES) {
                throw new IOException("Geometry template count exceeds limit: " + templateCount);
            }
            for (int t = 0; t < templateCount; t++) {
                int id = in.readUnsignedShort();
                geometryTemplates.put(id, readElements(in, pool));
            }

            int count = in.readInt();
            if (count < 0 || count > ResourceBundleFormat.MAX_SECTION_RECORDS) {
                throw new IOException("Model count exceeds limit: " + count);
            }
            trackRecords(count);
            for (int i = 0; i < count; i++) {
                String id = pool.get(in.readInt());
                String parent = pool.get(in.readInt());
                byte aoByte = in.readByte();
                Boolean ao = switch (aoByte) {
                    case 1 -> Boolean.FALSE;
                    case 2 -> Boolean.TRUE;
                    default -> null;
                };
                byte guiLight = in.readByte();
                int tmplId = in.readShort();

                int texCount = in.readUnsignedShort();
                Map<String, String> textures = new LinkedHashMap<>(texCount);
                for (int t = 0; t < texCount; t++) {
                    String slot = pool.get(in.readInt());
                    String target = pool.get(in.readInt());
                    textures.put(slot, target);
                }

                List<ModelIR.ElementIR> elements;
                if (tmplId >= 0) {
                    List<ModelIR.ElementIR> tmpl = geometryTemplates.get(tmplId);
                    if (tmpl == null) {
                        throw new IOException("Model references missing geometry template: " + tmplId);
                    }
                    elements = tmpl;
                } else {
                    elements = readElements(in, pool);
                }

                int overrideCount = in.readUnsignedShort();
                List<ModelIR.OverrideIR> overrides = new ArrayList<>(overrideCount);
                for (int o = 0; o < overrideCount; o++) {
                    String ovModel = pool.get(in.readInt());
                    int predCount = in.readUnsignedShort();
                    Map<String, Float> preds = new HashMap<>(predCount);
                    for (int p = 0; p < predCount; p++) {
                        preds.put(pool.get(in.readInt()), in.readFloat());
                    }
                    overrides.add(new ModelIR.OverrideIR(ovModel, preds));
                }

                int transformCount = in.readUnsignedShort();
                Map<String, ModelIR.TransformIR> transforms = new LinkedHashMap<>(transformCount);
                for (int tr = 0; tr < transformCount; tr++) {
                    String trKey = pool.get(in.readInt());
                    float[] rot = new float[]{in.readFloat(), in.readFloat(), in.readFloat()};
                    float[] trans = new float[]{in.readFloat(), in.readFloat(), in.readFloat()};
                    float[] scale = new float[]{in.readFloat(), in.readFloat(), in.readFloat()};
                    transforms.put(trKey, new ModelIR.TransformIR(rot, trans, scale));
                }

                if (models.putIfAbsent(id, new ModelIR(id, parent, ao, guiLight, textures, tmplId, elements, overrides,
                        transforms)) != null) {
                    throw new IOException("Duplicate model ID: " + id);
                }
            }
        }

        private List<ModelIR.ElementIR> readElements(DataInputStream in, StringPoolReader pool) throws IOException {
            int count = in.readUnsignedShort();
            if (count > ResourceBundleFormat.MAX_ELEMENTS_PER_MODEL) {
                throw new IOException("Element count exceeds limit: " + count);
            }
            List<ModelIR.ElementIR> elements = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                float[] from = new float[]{in.readFloat(), in.readFloat(), in.readFloat()};
                float[] to = new float[]{in.readFloat(), in.readFloat(), in.readFloat()};

                ModelIR.RotationIR rot = null;
                if (in.readBoolean()) {
                    float[] origin = new float[]{in.readFloat(), in.readFloat(), in.readFloat()};
                    byte axis = in.readByte();
                    float angle = in.readFloat();
                    boolean rescale = in.readBoolean();
                    rot = new ModelIR.RotationIR(origin, axis, angle, rescale);
                }

                boolean shade = in.readBoolean();
                byte mask = in.readByte();
                Map<Byte, ModelIR.FaceIR> faces = new HashMap<>();

                for (byte dir = 0; dir < 6; dir++) {
                    if ((mask & (1 << dir)) != 0) {
                        float[] uv = null;
                        if (in.readBoolean()) {
                            uv = new float[]{in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat()};
                        }
                        String texture = pool.get(in.readInt());
                        Byte cull = in.readBoolean() ? in.readByte() : null;
                        int rotation = in.readByte() * 90;
                        int tint = in.readShort();
                        faces.put(dir, new ModelIR.FaceIR(uv, texture, cull, rotation, tint));
                    }
                }
                elements.add(new ModelIR.ElementIR(from, to, rot, shade, faces));
            }
            return elements;
        }

        private void readBlockStates(DataInputStream in, StringPoolReader pool) throws IOException {
            int templateCount = in.readUnsignedShort();
            if (templateCount > ResourceBundleFormat.MAX_VARIANT_TEMPLATES) {
                throw new IOException("Variant template count exceeds limit: " + templateCount);
            }
            for (int t = 0; t < templateCount; t++) {
                int id = in.readUnsignedShort();
                variantTemplates.put(id, readVariantsMap(in, pool));
            }

            int count = in.readInt();
            if (count < 0 || count > ResourceBundleFormat.MAX_SECTION_RECORDS) {
                throw new IOException("BlockState count exceeds limit: " + count);
            }
            trackRecords(count);
            for (int i = 0; i < count; i++) {
                String id = pool.get(in.readInt());
                byte type = in.readByte();

                BlockStateIR bs;
                if (type == BlockStateIR.TYPE_TEMPLATE) {
                    int tmplId = in.readShort();
                    String param = pool.get(in.readInt());
                    Map<String, List<BlockStateIR.VariantModelIR>> tmpl = variantTemplates.get(tmplId);
                    if (tmpl == null) {
                        throw new IOException("BlockState references missing variant template: " + tmplId);
                    }
                    // Expand template with parameter substituting '{model}' in model IDs
                    Map<String, List<BlockStateIR.VariantModelIR>> expanded = new LinkedHashMap<>(tmpl.size());
                    for (Map.Entry<String, List<BlockStateIR.VariantModelIR>> entry : tmpl.entrySet()) {
                        List<BlockStateIR.VariantModelIR> list = new ArrayList<>(entry.getValue().size());
                        for (BlockStateIR.VariantModelIR m : entry.getValue()) {
                            String resolvedModel = m.model().replace("{model}", param);
                            list.add(new BlockStateIR.VariantModelIR(resolvedModel, m.x(), m.y(), m.uvLock(),
                                    m.weight()));
                        }
                        expanded.put(entry.getKey(), list);
                    }
                    bs = new BlockStateIR(id, type, tmplId, param, expanded, Collections.emptyList());
                } else if (type == BlockStateIR.TYPE_VARIANTS) {
                    Map<String, List<BlockStateIR.VariantModelIR>> vars = readVariantsMap(in, pool);
                    bs = new BlockStateIR(id, type, -1, null, vars, Collections.emptyList());
                } else if (type == BlockStateIR.TYPE_MULTIPART) {
                    int mpCount = in.readUnsignedShort();
                    List<BlockStateIR.MultiPartIR> multiparts = new ArrayList<>(mpCount);
                    for (int m = 0; m < mpCount; m++) {
                        BlockStateIR.ConditionIR cond = readCondition(in, pool, 0);
                        List<BlockStateIR.VariantModelIR> modelsList = readVariantModelList(in, pool);
                        multiparts.add(new BlockStateIR.MultiPartIR(cond, modelsList));
                    }
                    bs = new BlockStateIR(id, type, -1, null, Collections.emptyMap(), multiparts);
                } else {
                    throw new IOException("Unknown blockstate type: " + type);
                }

                if (blockStates.putIfAbsent(id, bs) != null) {
                    throw new IOException("Duplicate blockstate ID: " + id);
                }
            }
        }

        private Map<String, List<BlockStateIR.VariantModelIR>> readVariantsMap(DataInputStream in,
                StringPoolReader pool) throws IOException {
            int count = in.readUnsignedShort();
            if (count > ResourceBundleFormat.MAX_VARIANTS_PER_BLOCKSTATE) {
                throw new IOException("Variants count exceeds limit: " + count);
            }
            Map<String, List<BlockStateIR.VariantModelIR>> map = new LinkedHashMap<>(count);
            for (int i = 0; i < count; i++) {
                String key = pool.get(in.readInt());
                map.put(key, readVariantModelList(in, pool));
            }
            return map;
        }

        private List<BlockStateIR.VariantModelIR> readVariantModelList(DataInputStream in, StringPoolReader pool)
                throws IOException {
            int count = in.readUnsignedShort();
            List<BlockStateIR.VariantModelIR> list = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                String model = pool.get(in.readInt());
                int x = in.readByte() * 90;
                int y = in.readShort();
                boolean uvLock = in.readBoolean();
                int weight = in.readShort();
                list.add(new BlockStateIR.VariantModelIR(model, x, y, uvLock, weight));
            }
            return list;
        }

        private BlockStateIR.ConditionIR readCondition(DataInputStream in, StringPoolReader pool, int depth)
                throws IOException {
            if (depth > 16) {
                throw new IOException("Condition recursion depth limit exceeded (max 16): " + depth);
            }
            byte kind = in.readByte();
            if (kind == -1) {
                return null;
            }
            if (kind == BlockStateIR.ConditionIR.KIND_KEY_VALUE) {
                String key = pool.get(in.readInt());
                String value = pool.get(in.readInt());
                return new BlockStateIR.ConditionIR(kind, key, value, Collections.emptyList());
            } else if (kind == BlockStateIR.ConditionIR.KIND_AND || kind == BlockStateIR.ConditionIR.KIND_OR) {
                int count = in.readUnsignedShort();
                List<BlockStateIR.ConditionIR> terms = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    terms.add(readCondition(in, pool, depth + 1));
                }
                return new BlockStateIR.ConditionIR(kind, null, null, terms);
            }
            throw new IOException("Unknown condition kind: " + kind);
        }

        private void readTags(DataInputStream in, StringPoolReader pool) throws IOException {
            int count = in.readInt();
            if (count < 0 || count > ResourceBundleFormat.MAX_SECTION_RECORDS) {
                throw new IOException("Tag count exceeds limit: " + count);
            }
            trackRecords(count);
            for (int i = 0; i < count; i++) {
                String registry = pool.get(in.readInt());
                String id = pool.get(in.readInt());
                boolean replace = dataBoolean(in);
                int entryCount = in.readUnsignedShort();
                if (entryCount > ResourceBundleFormat.MAX_TAG_ENTRIES) {
                    throw new IOException("Tag entry count exceeds limit: " + entryCount);
                }
                List<TagIR.TagEntryIR> entries = new ArrayList<>(entryCount);
                for (int e = 0; e < entryCount; e++) {
                    String targetId = pool.get(in.readInt());
                    boolean isTag = in.readBoolean();
                    boolean isReq = in.readBoolean();
                    entries.add(new TagIR.TagEntryIR(targetId, isTag, isReq));
                }
                tags.add(new TagIR(registry, id, replace, entries));
            }
        }

        private boolean dataBoolean(DataInputStream in) throws IOException {
            return in.readBoolean();
        }

        public short getVersion() {
            return version;
        }

        public byte[] getDigest() {
            return Arrays.copyOf(digest, digest.length);
        }

        public Map<String, ModelIR> getModels() {
            return Collections.unmodifiableMap(models);
        }

        public Map<String, BlockStateIR> getBlockStates() {
            return Collections.unmodifiableMap(blockStates);
        }

        public List<TagIR> getTags() {
            return Collections.unmodifiableList(tags);
        }

        public List<ModelIR.ElementIR> getGeometryTemplate(int id) {
            return geometryTemplates.get(id);
        }

        public Map<String, List<BlockStateIR.VariantModelIR>> getVariantTemplate(int id) {
            return variantTemplates.get(id);
        }
    }
}
