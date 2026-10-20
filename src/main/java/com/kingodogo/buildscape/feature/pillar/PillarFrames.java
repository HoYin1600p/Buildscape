package com.kingodogo.buildscape.feature.pillar;

import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.core.ParticlePatterns;
import com.kingodogo.buildscape.entity.ColoredItemFrameEntity;
import com.kingodogo.buildscape.feature.pillar.PillarIdManager.PillarData;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * Registers item frames (vanilla and coloured) with a {@link PillarIdManager} and reads and writes the
 * particle data a frame keeps in its entity.
 */
final class PillarFrames {

    private PillarFrames() {
    }


    static void clearFrameData(Entity frame) {
        clearFrameData(frame.getPersistentData());
        clearColoredFrameData(frame);
    }

    static void clearFrameData(CompoundTag tag) {
        tag.remove(NbtKeys.BUILD_SCAPE_FRAME_ID);
        tag.remove(NbtKeys.BUILD_SCAPE_PARTICLE_PATTERN);
        tag.remove(NbtKeys.BUILD_SCAPE_PARTICLE_COLORS);
        tag.remove("BuildScapeParticleEnabled");
        tag.remove(NbtKeys.PILLAR_PATTERN);
        tag.remove(NbtKeys.PILLAR_COLORS);
    }

    static void clearColoredFrameData(Entity frame) {
        if (frame instanceof ColoredItemFrameEntity coloredFrame) {
            coloredFrame.setParticlePattern(ParticlePatterns.NONE);
            coloredFrame.setParticleColorsRaw("");
        }
    }

    static void resetLoadedFrame(PillarIdManager m, PillarData data) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        for (ServerLevel level : server.getAllLevels()) {
            if (!Objects.equals(PillarIdManager.getDimensionKey(level), data.dimension) || !level.hasChunkAt(data.getBlockPos())) continue;
            for (Entity entity : level.getEntities(null,
                    new AABB(data.getBlockPos()).inflate(1.0))) {
                if (!(entity instanceof ItemFrame)
                        && !(entity instanceof ColoredItemFrameEntity)) continue;
                Direction facing = entity instanceof ItemFrame itemFrame
                        ? itemFrame.getDirection()
                        : ((ColoredItemFrameEntity) entity).getDirection();
                if (m.registry.isRemovedFrame(entity.getPersistentData().getString(NbtKeys.BUILD_SCAPE_FRAME_ID),
                        data.dimension, entity.blockPosition(), facing)) clearFrameData(entity);
            }
        }
    }

    /**
     * Writes a frame edited in the detail editor to the entity's data. A frame keeps its pattern and colours
     * in the entity and registers them again when it loads, so the entity must carry the edit. Frames that are
     * not loaded are marked pending and take the registry values when they register.
     */
    static void applyFrameEdit(PillarIdManager m, PillarData data) {
        if (!m.serverAuthoritative || data == null || !PillarRegistry.isItemFrameData(data)) return;
        data.frameEditPending = writeLoadedFrame(data) ? null : Boolean.TRUE;
    }

    static boolean writeLoadedFrame(PillarData data) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return false;
        boolean written = false;
        for (ServerLevel level : server.getAllLevels()) {
            if (!Objects.equals(PillarIdManager.getDimensionKey(level), data.dimension) || !level.hasChunkAt(data.getBlockPos())) continue;
            for (Entity entity : level.getEntities(null,
                    new AABB(data.getBlockPos()).inflate(1.0))) {
                if (!(entity instanceof ItemFrame)
                        && !(entity instanceof ColoredItemFrameEntity)) continue;
                Direction facing = entity instanceof ItemFrame itemFrame
                        ? itemFrame.getDirection()
                        : ((ColoredItemFrameEntity) entity).getDirection();
                if (!PillarRegistry.isSameFrameLocation(data, data.dimension, entity.blockPosition(), facing)) continue;
                writeFrameData(entity.getPersistentData(), data.pattern, data.dyeColors);
                if (entity instanceof ColoredItemFrameEntity coloredFrame) {
                    coloredFrame.setParticlePattern(framePattern(data.pattern));
                    coloredFrame.setParticleColorsRaw(String.join(";", data.dyeColors));
                }
                written = true;
            }
        }
        return written;
    }

    static String framePattern(String pattern) {
        return pattern == null || pattern.isEmpty() ? ParticlePatterns.NONE : pattern;
    }

    /** Puts a frame's pattern and colours into its entity data, replacing the previous values. */
    static void writeFrameData(CompoundTag tag, String pattern, List<String> colors) {
        ListTag colorList = new ListTag();
        if (colors != null) {
            for (String color : colors) colorList.add(StringTag.valueOf(color));
        }
        tag.putString(NbtKeys.BUILD_SCAPE_PARTICLE_PATTERN, framePattern(pattern));
        tag.put(NbtKeys.BUILD_SCAPE_PARTICLE_COLORS, colorList);
        if (tag.contains(NbtKeys.PILLAR_PATTERN)) tag.putString(NbtKeys.PILLAR_PATTERN, framePattern(pattern));
        if (tag.contains(NbtKeys.PILLAR_COLORS)) tag.put(NbtKeys.PILLAR_COLORS, colorList.copy());
    }

    /**
     * Settles an edit made while the frame was unloaded. When the frame registers by itself the registry
     * wins and its values are written to the entity; when a player changed the frame (immediate), the
     * entity is newer, so the pending edit is dropped. Returns true when the entity data was rewritten.
     */
    static boolean applyPendingFrameEdit(PillarIdManager m, PillarData existing, CompoundTag tag, boolean immediate) {
        if (existing == null || existing.frameEditPending == null) return false;
        existing.frameEditPending = null;
        m.requestSave();
        if (immediate) return false;
        writeFrameData(tag, existing.pattern, existing.dyeColors);
        return true;
    }

    static boolean prepareFrameRegistration(PillarIdManager m, Entity frame, boolean immediate) {
        Direction facing = frame instanceof ItemFrame itemFrame
                ? itemFrame.getDirection()
                : ((ColoredItemFrameEntity) frame).getDirection();
        if (immediate) {
            if (m.registry.isRemovedFrame(frame.getPersistentData().getString(NbtKeys.BUILD_SCAPE_FRAME_ID),
                    PillarIdManager.getDimensionKey(frame.level), frame.blockPosition(), facing)) {
                ItemFrameParticleHandler.reassignFrameId(frame);
            }
            return true;
        }
        if (prepareFrameRegistration(m, frame.getPersistentData(), PillarIdManager.getDimensionKey(frame.level),
                frame.blockPosition(), facing)) return true;
        clearColoredFrameData(frame);
        return false;
    }

    static boolean prepareFrameRegistration(PillarIdManager m, CompoundTag tag, String dimension, BlockPos pos,
                                             Direction facing) {
        if (!m.registry.isRemovedFrame(tag.getString(NbtKeys.BUILD_SCAPE_FRAME_ID), dimension, pos, facing)) return true;
        clearFrameData(tag);
        return false;
    }

    static void registerItemFrame(PillarIdManager m, ItemFrame frame) {
        registerItemFrame(m, frame, false);
    }

    static void registerItemFrame(PillarIdManager m, ItemFrame frame, boolean immediate) {
        if (frame == null || frame.level == null || frame.level.isClientSide) {
            return;
        }

        if (!prepareFrameRegistration(m, frame, immediate)) return;
        CompoundTag dataTag = frame.getPersistentData();
        String id = dataTag.getString(NbtKeys.BUILD_SCAPE_FRAME_ID);

        String pattern = ItemFrameParticleHandler.getParticlePattern(frame);
        List<String> colors = new ArrayList<>();
        if (dataTag.contains(NbtKeys.BUILD_SCAPE_PARTICLE_COLORS)) {
            ListTag colorList = dataTag.getList(NbtKeys.BUILD_SCAPE_PARTICLE_COLORS, 8);
            for (int i = 0; i < colorList.size(); i++) {
                colors.add(colorList.getString(i));
            }
        }

        if (id == null || id.isEmpty()) {
            if (!PillarRegistry.hasFrameParticleData(pattern, colors)) return;
            id = ItemFrameParticleHandler.getFrameId(frame);
        }

        String dimension = PillarIdManager.getDimensionKey(frame.level);
        BlockPos pos = frame.blockPosition();
        Direction facing = frame.getDirection();
        String posKey = PillarRegistry.positionKey(dimension, pos, facing);

        id = m.registry.reconcileFrameId(id, dimension, pos, facing);
        dataTag.putString(NbtKeys.BUILD_SCAPE_FRAME_ID, id);
        PillarData existing = m.registry.pillarData.get(id);
        if (existing == null && !PillarRegistry.hasFrameParticleData(pattern, colors)) return;
        if (existing != null && !PillarRegistry.isSameFrameLocation(existing, dimension, pos, facing)) {
            id = ItemFrameParticleHandler.reassignFrameId(frame);
            existing = null;
        }
        if (applyPendingFrameEdit(m, existing, dataTag, immediate)) {
            pattern = framePattern(existing.pattern);
            colors = new ArrayList<>(existing.dyeColors);
        }
        boolean removedDuplicate = m.registry.removeOtherFrameAtPosition(posKey, id);
        if (existing == null) {
            PillarData data = new PillarData(id, dimension, pos);
            data.pattern = pattern;
            data.dyeColors = colors;

            data.pillarType = "minecraft:item_frame";
            data.facing = facing != null ? facing.getSerializedName() : null;

            if (!frame.getItem().isEmpty()) {
                ResourceLocation itemRL = ForgeRegistries.ITEMS.getKey(frame.getItem().getItem());
                if (itemRL != null) data.displayedItem = itemRL.toString();
            }

            m.registry.pillarData.put(id, data);
            m.registry.positionIndex.put(posKey, id);

            m.requestSave();
        } else {
            m.registry.positionIndex.put(posKey, id);
            boolean changed = removedDuplicate;

            if (existing.pillarType == null || !existing.pillarType.equals("minecraft:item_frame")) {
                existing.pillarType = "minecraft:item_frame";
                changed = true;
            }
            if (!Objects.equals(existing.facing, facing != null ? facing.getSerializedName() : null)) {
                existing.facing = facing != null ? facing.getSerializedName() : null;
                changed = true;
            }

            if (!Objects.equals(existing.pattern, pattern)) {
                existing.pattern = pattern;
                changed = true;
            }
            if (!Objects.equals(existing.dyeColors, colors)) {
                existing.dyeColors = colors;
                changed = true;
            }

            if (!frame.getItem().isEmpty()) {
                ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(frame.getItem().getItem());
                String itemId = itemKey != null ? itemKey.toString() : null;
                if (!Objects.equals(existing.displayedItem, itemId)) {
                    existing.displayedItem = itemId;
                    changed = true;
                }
            } else if (existing.displayedItem != null) {
                existing.displayedItem = null;
                changed = true;
            }

            if (changed) {
                existing.modifiedTime = System.currentTimeMillis();
                m.requestSave();
            }
        }
    }


    static void registerColoredItemFrame(PillarIdManager m, ColoredItemFrameEntity frame) {
        registerColoredItemFrame(m, frame, false);
    }

    static void registerColoredItemFrame(PillarIdManager m, ColoredItemFrameEntity frame, boolean immediate) {
        if (frame == null || frame.level == null || frame.level.isClientSide) {
            return;
        }

        if (!prepareFrameRegistration(m, frame, immediate)) return;
        CompoundTag dataTag = frame.getPersistentData();
        String id = dataTag.getString(NbtKeys.BUILD_SCAPE_FRAME_ID);

        String pattern = ItemFrameParticleHandler.getParticlePatternColored(frame);
        List<String> colors = new ArrayList<>();
        if (dataTag.contains(NbtKeys.BUILD_SCAPE_PARTICLE_COLORS)) {
            ListTag colorList = dataTag.getList(NbtKeys.BUILD_SCAPE_PARTICLE_COLORS, 8);
            for (int i = 0; i < colorList.size(); i++) {
                colors.add(colorList.getString(i));
            }
        }

        if (id == null || id.isEmpty()) {
            if (!PillarRegistry.hasFrameParticleData(pattern, colors)) return;
            id = ItemFrameParticleHandler.getFrameIdColored(frame);
        }

        String dimension = PillarIdManager.getDimensionKey(frame.level);
        BlockPos pos = frame.blockPosition();
        Direction facing = frame.getDirection();
        String posKey = PillarRegistry.positionKey(dimension, pos, facing);

        id = m.registry.reconcileFrameId(id, dimension, pos, facing);
        dataTag.putString(NbtKeys.BUILD_SCAPE_FRAME_ID, id);
        PillarData existing = m.registry.pillarData.get(id);
        if (existing == null && !PillarRegistry.hasFrameParticleData(pattern, colors)) return;
        if (existing != null && !PillarRegistry.isSameFrameLocation(existing, dimension, pos, facing)) {
            id = ItemFrameParticleHandler.reassignFrameId(frame);
            existing = null;
        }
        if (applyPendingFrameEdit(m, existing, dataTag, immediate)) {
            pattern = framePattern(existing.pattern);
            colors = new ArrayList<>(existing.dyeColors);
            frame.setParticlePattern(pattern);
            frame.setParticleColorsRaw(String.join(";", colors));
        }
        boolean removedDuplicate = m.registry.removeOtherFrameAtPosition(posKey, id);
        if (existing == null) {
            PillarData data = new PillarData(id, dimension, pos);
            data.pattern = pattern;
            data.dyeColors = colors;

            String color = frame.getColorVariant();
            if (color == null || color.isEmpty()) color = "white";
            else color = color.toLowerCase(Locale.ROOT);

            ResourceLocation typeRL = new ResourceLocation("buildscape", color + "_item_frame");
            data.pillarType = typeRL.toString();
            data.facing = facing != null ? facing.getSerializedName() : null;

            if (!frame.getItem().isEmpty()) {
                ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(frame.getItem().getItem());
                if (itemKey != null) {
                    data.displayedItem = itemKey.toString();
                }
            }

            m.registry.pillarData.put(id, data);
            m.registry.positionIndex.put(posKey, id);

            m.requestSave();
        } else {
            m.registry.positionIndex.put(posKey, id);
            boolean changed = removedDuplicate;

            String color = frame.getColorVariant();
            if (color == null || color.isEmpty()) color = "white";
            else color = color.toLowerCase(Locale.ROOT);
            String expectedType = "buildscape:" + color + "_item_frame";

            if (existing.pillarType == null || !existing.pillarType.equals(expectedType)) {
                existing.pillarType = expectedType;
                changed = true;
            }
            if (!Objects.equals(existing.facing, facing != null ? facing.getSerializedName() : null)) {
                existing.facing = facing != null ? facing.getSerializedName() : null;
                changed = true;
            }

            if (!Objects.equals(existing.pattern, pattern)) {
                existing.pattern = pattern;
                changed = true;
            }
            if (!Objects.equals(existing.dyeColors, colors)) {
                existing.dyeColors = colors;
                changed = true;
            }

            if (!frame.getItem().isEmpty()) {
                ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(frame.getItem().getItem());
                String itemId = itemKey != null ? itemKey.toString() : null;
                if (!Objects.equals(existing.displayedItem, itemId)) {
                    existing.displayedItem = itemId;
                    changed = true;
                }
            } else if (existing.displayedItem != null) {
                existing.displayedItem = null;
                changed = true;
            }

            if (changed) {
                existing.modifiedTime = System.currentTimeMillis();
                m.requestSave();
            }
        }
    }
}
