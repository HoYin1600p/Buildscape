package com.kingodogo.buildscape.feature.pillar;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.core.ParticlePatterns;
import com.kingodogo.buildscape.feature.pillar.PillarIdManager.PillarData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * The pillar and item frame records of one {@link PillarIdManager}: the id and position maps and the
 * operations that need nothing but those maps, plus the id prefix a block gets. Anything that changes
 * the world, the files or the save flags stays in the manager.
 */
final class PillarRegistry {

    static final int MAX_REMOVED_FRAMES = 4096;

    final Map<String, PillarData> pillarData = new ConcurrentHashMap<>();
    final Map<String, PillarData> removedFrames = new ConcurrentHashMap<>();
    final Map<String, String> positionIndex = new ConcurrentHashMap<>();

    public String generatePillarId(String expectedPrefix) {
        if (expectedPrefix == null || expectedPrefix.isEmpty()) {
            expectedPrefix = PillarIdManager.PREFIX_STONE + "-P";
        }

        String prefix = expectedPrefix;
        Random random = new Random();

        for (int attempt = 0; attempt < 50; attempt++) {
            int num = random.nextInt(9999) + 1;
            String id = prefix + String.format(Locale.ROOT, "%04d", num);
            if (!pillarData.containsKey(id)) {
                return id;
            }
        }

        for (int num = 1; num <= 9999; num++) {
            String id = prefix + String.format(Locale.ROOT, "%04d", num);
            if (!pillarData.containsKey(id)) {
                return id;
            }
        }

        for (int attempt = 0; attempt < 100; attempt++) {
            char c1 = (char) ('a' + random.nextInt(26));
            char c2 = ALPHANUMERIC.charAt(random.nextInt(36));
            char c3 = ALPHANUMERIC.charAt(random.nextInt(36));
            char c4 = ALPHANUMERIC.charAt(random.nextInt(36));
            String id = prefix + c1 + c2 + c3 + c4;
            if (!pillarData.containsKey(id)) {
                return id;
            }
        }

        for (int i = 10; i < 36; i++) {
            char c1 = ALPHANUMERIC.charAt(i);
            for (int j = 0; j < 36; j++) {
                for (int k = 0; k < 36; k++) {
                    for (int l = 0; l < 36; l++) {
                        String id = prefix +
                                c1 +
                                ALPHANUMERIC.charAt(j) +
                                ALPHANUMERIC.charAt(k) +
                                ALPHANUMERIC.charAt(l);
                        if (!pillarData.containsKey(id)) {
                            return id;
                        }
                    }
                }
            }
        }

        return prefix + Long.toString(System.currentTimeMillis(), 36);
    }

    static final String ALPHANUMERIC = "0123456789abcdefghijklmnopqrstuvwxyz";

    static String positionKey(String dimension, BlockPos pos) {
        return positionKey(dimension, pos, null);
    }

    static String positionKey(String dimension, BlockPos pos, Direction facing) {
        String key = dimension + ":" + pos.getX() + ":" + pos.getY() + ":" + pos.getZ();
        if (facing != null) {
            key += ":" + facing.getSerializedName();
        }
        return key;
    }

    public static String getDimensionKey(Level level) {
        if (level == null) {
            return "unknown";
        }
        return level.dimension().location().toString();
    }

    public String getIdForPosition(String posKey) {
        if (posKey == null) {
            return null;
        }
        return positionIndex.get(posKey);
    }

    public Collection<PillarData> getAllData() {
        return pillarData.values();
    }

    public PillarData getPillarData(String pillarId) {
        if (pillarId == null) {
            return null;
        }
        return pillarData.get(pillarId);
    }

    public PillarData getPillarDataByPosition(Level level, BlockPos pos) {
        String dimension = getDimensionKey(level);
        String id = positionIndex.get(positionKey(dimension, pos));
        return id != null ? pillarData.get(id) : null;
    }

    public String getPillarIdByPosition(Level level, BlockPos pos) {
        PillarData data = getPillarDataByPosition(level, pos);
        return data != null ? data.id : null;
    }

    public boolean isIdTaken(String id) {
        return id != null && (pillarData.containsKey(id) || removedFrames.containsKey(id));
    }

    static boolean isSameFrameLocation(PillarData data, String dimension, BlockPos pos, Direction facing) {
        if (!Objects.equals(data.dimension, dimension)) {
            return false;
        }
        if (data.x != pos.getX() || data.y != pos.getY() || data.z != pos.getZ()) {
            return false;
        }
        return data.facing == null || facing == null || data.facing.equals(facing.getSerializedName());
    }

    public static void clearPillarOverrides(PillarData data) {
        if (data == null) {
            return;
        }
        if (data.dyeColors == null) {
            data.dyeColors = new ArrayList<>();
        } else {
            data.dyeColors.clear();
        }
        data.use_pattern = null;
        data.pattern = null;
        data.pattern_speed = null;
        data.pattern_spread = null;
        data.pattern_intensity = null;
        data.max_particle_color = null;
    }

    boolean pruneRemovedFrames() {
        int excess = removedFrames.size() - MAX_REMOVED_FRAMES;
        if (excess <= 0) {
            return false;
        }
        List<PillarData> oldest = new ArrayList<>(removedFrames.values());
        oldest.sort(Comparator.comparingLong((PillarData data) -> data.modifiedTime)
                .thenComparing(data -> data.id));
        for (int i = 0; i < excess; i++) {
            PillarData data = oldest.get(i);
            removedFrames.remove(data.id, data);
        }
        return true;
    }

    public Set<String> getAllPillarIds() {
        return Collections.unmodifiableSet(pillarData.keySet());
    }

    public int getPillarCount() {
        return pillarData.size();
    }

    public Map<String, PillarData> copyDataSnapshot() {
        Map<String, PillarData> snapshot = new HashMap<>();
        for (Map.Entry<String, PillarData> entry : pillarData.entrySet()) {
            PillarData original = entry.getValue();
            if (original != null) {
                PillarData copy = new PillarData();
                copy.id = original.id;
                copy.dimension = original.dimension;
                copy.x = original.x;
                copy.y = original.y;
                copy.z = original.z;
                copy.createdTime = original.createdTime;
                copy.modifiedTime = original.modifiedTime;
                copy.pattern = original.pattern;
                copy.pattern_speed = original.pattern_speed;
                copy.pattern_spread = original.pattern_spread;
                copy.pattern_intensity = original.pattern_intensity;
                copy.max_particle_color = original.max_particle_color;
                copy.use_pattern = original.use_pattern;
                copy.displayedItem = original.displayedItem;
                copy.pillarType = original.pillarType;
                copy.facing = original.facing;
                copy.itemYaw = original.itemYaw;
                copy.frameEditPending = original.frameEditPending;
                if (original.dyeColors != null && !original.dyeColors.isEmpty()) {
                    copy.dyeColors = new ArrayList<>(original.dyeColors);
                } else {
                    copy.dyeColors = new ArrayList<>();
                }
                snapshot.put(entry.getKey(), copy);
            }
        }
        return snapshot;
    }

    void rebuildPositionIndex() {
        deduplicateFramePositions();
        positionIndex.clear();
        for (PillarData data : pillarData.values()) {
            if (data == null || data.id == null || data.dimension == null) {
                continue;
            }
            try {
                indexPosition(data);
            } catch (Exception e) {
                // indexing one pillar must not stop the rest
                BuildScape.LOGGER.debug("Skipped position index for pillar {}", data.id, e);
            }
        }
    }

    void indexPosition(PillarData data) {
        Direction facing = data.facing != null
                ? Direction.byName(data.facing)
                : null;
        String key = positionKey(data.dimension, data.getBlockPos(), facing);
        String previousId = positionIndex.get(key);
        PillarData previous = previousId != null ? pillarData.get(previousId) : null;
        if (previous == null || data.modifiedTime > previous.modifiedTime
                || (data.modifiedTime == previous.modifiedTime && data.id.compareTo(previous.id) < 0)) {
            positionIndex.put(key, data.id);
        }
    }

    static boolean isItemFrameData(PillarData data) {
        return data.pillarType != null && (data.pillarType.endsWith(":item_frame")
                || data.pillarType.endsWith("_item_frame"));
    }

    static boolean hasFrameParticleData(String pattern, List<String> colors) {
        return (colors != null && !colors.isEmpty()) || (pattern != null && !pattern.isEmpty()
                && !ParticlePatterns.NONE.equals(pattern) && !ParticlePatterns.NOT_SET.equals(pattern));
    }

    void deduplicateFramePositions() {
        Map<String, PillarData> frames = new HashMap<>();
        for (PillarData data : new ArrayList<>(pillarData.values())) {
            if (!isItemFrameData(data)) {
                continue;
            }
            Direction facing = data.facing != null
                    ? Direction.byName(data.facing)
                    : null;
            String key = positionKey(data.dimension, data.getBlockPos(), facing);
            PillarData previous = frames.get(key);
            if (previous == null) {
                frames.put(key, data);
            } else if (data.modifiedTime > previous.modifiedTime
                    || (data.modifiedTime == previous.modifiedTime && data.id.compareTo(previous.id) < 0)) {
                pillarData.remove(previous.id, previous);
                frames.put(key, data);
            } else {
                pillarData.remove(data.id, data);
            }
        }
    }

    boolean pruneRegistry() {
        int previousSize = pillarData.size();
        // Dimensions are never a pruning reason: mods register some dimensions dynamically after startup.
        pillarData.values().removeIf(data -> isItemFrameData(data) && data.frameEditPending == null
                && !hasFrameParticleData(data.pattern, data.dyeColors));
        rebuildPositionIndex();
        return previousSize != pillarData.size();
    }

    public String getFrameIdForPosition(String dimension, BlockPos pos, Direction facing) {
        return facing == null ? null : positionIndex.get(positionKey(dimension, pos, facing));
    }

    boolean removeOtherFrameAtPosition(String key, String id) {
        String previousId = positionIndex.get(key);
        if (previousId == null || previousId.equals(id)) {
            return false;
        }
        PillarData previous = pillarData.get(previousId);
        if (previous == null || !isItemFrameData(previous)) {
            return false;
        }
        pillarData.remove(previousId, previous);
        positionIndex.remove(key, previousId);
        return true;
    }

    String reconcilePillarId(String savedId, String dimension, BlockPos basePos) {
        return reconcilePillarId(savedId, dimension, basePos, basePos);
    }

    String reconcilePillarId(String savedId, String dimension, BlockPos pos, BlockPos basePos) {
        if (savedId == null || savedId.isEmpty()) {
            return savedId;
        }
        PillarData saved = pillarData.get(savedId);
        if (saved != null && Objects.equals(saved.dimension, dimension)
                && saved.x == pos.getX() && saved.z == pos.getZ()
                && saved.y >= basePos.getY() && saved.y <= pos.getY()) {
            return savedId;
        }
        for (BlockPos current = pos; current.getY() >= basePos.getY(); current = current.below()) {
            String authoritativeId = positionIndex.get(positionKey(dimension, current));
            if (authoritativeId != null) {
                return authoritativeId;
            }
        }
        return savedId;
    }

    String reconcileFrameId(String savedId, String dimension, BlockPos pos, Direction facing) {
        String authoritativeId = getFrameIdForPosition(dimension, pos, facing);
        return authoritativeId != null ? authoritativeId : savedId;
    }

    boolean isRemovedFrame(String id, String dimension, BlockPos pos, Direction facing) {
        boolean hasId = id != null && !id.isEmpty();
        if (hasId && removedFrames.containsKey(id)) {
            return true;
        }
        if (getFrameIdForPosition(dimension, pos, facing) != null) {
            return false;
        }
        for (PillarData data : removedFrames.values()) {
            if ((!hasId || id.equals(data.id)) && isSameFrameLocation(data, dimension, pos, facing)) {
                return true;
            }
        }
        return false;
    }

    static String getVariantPrefix(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return PillarIdManager.PREFIX_STONE + "-P";
        }

        if (!level.isClientSide &&
                !BuildScape.isServerFullyInitialized()) {
            return PillarIdManager.PREFIX_STONE + "-P";
        }

        if (!level.hasChunkAt(pos)) {
            return PillarIdManager.PREFIX_STONE + "-P";
        }

        try {
            if (!level.isClientSide) {
                ChunkAccess chunk = level.getChunk(pos);
                if (!(chunk instanceof LevelChunk)) {
                    return PillarIdManager.PREFIX_STONE + "-P";
                }
                if (!chunk
                        .getStatus()
                        .isOrAfter(ChunkStatus.FULL)) {
                    return PillarIdManager.PREFIX_STONE + "-P";
                }
            }
        } catch (Exception e) {
            // chunk access can fail while the area loads; treat the position as unavailable
            return PillarIdManager.PREFIX_STONE + "-P";
        }

        try {
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();
            String blockName = ForgeRegistries.BLOCKS.getKey(
                    block).getPath();

            if (blockName.contains("mossy")) {
                return PillarIdManager.PREFIX_MOSSY + "-P";
            } else if (blockName.contains("deepslate")) {
                return PillarIdManager.PREFIX_DEEPSLATE + "-P";
            } else if (blockName.contains("quartz")) {
                return PillarIdManager.PREFIX_QUARTZ + "-P";
            } else if (blockName.contains("ashenking_diamond")) {
                return "AK-D";
            } else if (blockName.contains("ashenking_emerald")) {
                return "AK-E";
            } else if (blockName.contains("ashenking_netherite")) {
                return "AK-N";
            } else if (blockName.contains("ashenking_gold")) {
                return "AK-G";
            } else if (blockName.contains("ashenking_pillar")) {
                return "AK-P";
            } else if (blockName.contains("amethyst")) {
                return "AM-P";
            } else if (blockName.contains("andesite")) {
                return "AN-P";
            } else if (blockName.contains("basalt")) {
                return "BS-P";
            } else if (blockName.contains("blackstone")) {
                return "BL-P";
            } else if (blockName.contains("blue_ice")) {
                return "BI-P";
            } else if (blockName.contains("calcite")) {
                return "CA-P";
            } else if (blockName.contains("cinnabar")) {
                return "CI-P";
            } else if (blockName.contains("exposed_copper")) {
                return "EC-P";
            } else if (blockName.contains("oxidized_copper")) {
                return "OC-P";
            } else if (blockName.contains("weathered_copper")) {
                return "WC-P";
            } else if (blockName.contains("copper")) {
                return "CO-P";
            } else if (blockName.contains("dark_prismarine")) {
                return "DP-P";
            } else if (blockName.contains("prismarine")) {
                return "PR-P";
            } else if (blockName.contains("diorite")) {
                return "DI-P";
            } else if (blockName.contains("dripstone")) {
                return "DR-P";
            } else if (blockName.contains("granite")) {
                return "GR-P";
            } else if (blockName.contains("netherrack")) {
                return "NE-P";
            } else if (blockName.contains("obsidian")) {
                return "OB-P";
            } else if (blockName.contains("packed_mud")) {
                return "PM-P";
            } else if (blockName.contains("sculk")) {
                return "SC-P";
            } else if (blockName.contains("sulfur")) {
                return "SU-P";
            } else if (blockName.contains("tuff")) {
                return "TU-P";
            } else if (blockName.contains("stone")) {
                return PillarIdManager.PREFIX_STONE + "-P";
            } else {
                return PillarIdManager.PREFIX_STONE + "-P";
            }
        } catch (Exception e) {
            // block lookup failed; use the stone prefix
            return PillarIdManager.PREFIX_STONE + "-P";
        }
    }
}
