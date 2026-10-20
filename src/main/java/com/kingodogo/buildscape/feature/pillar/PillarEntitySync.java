package com.kingodogo.buildscape.feature.pillar;

import com.kingodogo.buildscape.BuildScape;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * Copies pattern settings and colours between a pillar block entity and the record the
 * {@link PillarIdManager} keeps for its stack.
 */
final class PillarEntitySync {

    private PillarEntitySync() {
    }


    static void syncPatternFromStack(PillarBlockEntity self) {
        final Level level = self.getLevel();
        final BlockPos worldPosition = self.getBlockPos();
        if (level == null || level.isClientSide)
            return;

        if (self.particlePattern != null) {
            self.propagatePatternToStack(self.particlePattern);
            return;
        }

        String stackPattern = self.getStackParticlePattern();
        if (stackPattern != null && !stackPattern.equals(self.particlePattern)) {
            self.particlePattern = stackPattern;
            self.setChanged();
            level.sendBlockUpdated(
                    worldPosition,
                    self.getBlockState(),
                    self.getBlockState(),
                    3);
        }
    }

    static void syncPatternFromManager(PillarBlockEntity self) {
        final Level level = self.getLevel();
        final BlockPos worldPosition = self.getBlockPos();
        if (level == null || level.isClientSide)
            return;

        if (level.getServer() == null ||
                !level.getServer().isRunning() ||
                !BuildScape.isServerFullyInitialized()) {
            return;
        }

        if (level.getServer().getPlayerList().getPlayerCount() == 0) {
            return;
        }

        if (!level.hasChunkAt(worldPosition)) {
            return;
        }

        try {
            ChunkAccess chunk = level.getChunk(
                    worldPosition);
            if (!(chunk instanceof LevelChunk)) {
                return;
            }
            if (!chunk
                    .getStatus()
                    .isOrAfter(ChunkStatus.FULL)) {
                return;
            }
        } catch (Exception e) {
            return;
        }

        PillarIdManager manager = PillarIdManager.get(level);

        if (!manager.hasLoaded()) {
            return;
        }

        String expectedPrefix = PillarIdManager.getVariantPrefix(
                level,
                worldPosition);

        String idToSync = self.pillarId;

        if (idToSync == null || idToSync.isEmpty()) {
            idToSync = self.getStackPillarId();
        }

        if (idToSync == null || idToSync.isEmpty()) {
            return;
        }

        if (!idToSync.startsWith(expectedPrefix) && manager.getPillarData(idToSync) == null) {
            return;
        }

        PillarIdManager.PillarData data = manager.getPillarData(idToSync);

        if (data != null) {
            boolean needsUpdate = false;

            String managerPattern = data.pattern;
            if (managerPattern != null && !managerPattern.isEmpty()) {
                boolean validPattern = false;
                for (String p : PillarBlockEntity.PATTERNS) {
                    if (p.equals(managerPattern)) {
                        validPattern = true;
                        break;
                    }
                }

                if (validPattern && (self.particlePattern == null || !self.particlePattern.equals(managerPattern))) {
                    self.particlePattern = managerPattern;
                    needsUpdate = true;
                }
            }

            if (data.pattern_speed != null
                    && (self.patternSpeed == null || !self.patternSpeed.equals(data.pattern_speed))) {
                self.patternSpeed = data.pattern_speed;
                needsUpdate = true;
            }
            if (data.pattern_spread != null
                    && (self.patternSpread == null || !self.patternSpread.equals(data.pattern_spread))) {
                self.patternSpread = data.pattern_spread;
                needsUpdate = true;
            }
            if (data.pattern_intensity != null
                    && (self.patternIntensity == null || !self.patternIntensity.equals(data.pattern_intensity))) {
                self.patternIntensity = data.pattern_intensity;
                needsUpdate = true;
            }

            if (needsUpdate) {
                self.setChanged();
                level.sendBlockUpdated(
                        worldPosition,
                        self.getBlockState(),
                        self.getBlockState(),
                        3);
            }
        }
    }

    static void syncBothFromManager(PillarBlockEntity self) {
        final Level level = self.getLevel();
        final BlockPos worldPosition = self.getBlockPos();
        if (level == null || level.isClientSide)
            return;

        if (level.getServer() == null ||
                !level.getServer().isRunning() ||
                !BuildScape.isServerFullyInitialized()) {
            return;
        }
        if (level.getServer().getPlayerList().getPlayerCount() == 0)
            return;
        if (!level.hasChunkAt(worldPosition))
            return;
        try {
            ChunkAccess chunk = level.getChunk(worldPosition);
            if (!(chunk instanceof LevelChunk))
                return;
            if (!chunk.getStatus().isOrAfter(ChunkStatus.FULL))
                return;
        } catch (Exception e) {
            return;
        }

        PillarIdManager manager = PillarIdManager.get(level);
        if (!manager.hasLoaded())
            return;

        String expectedPrefix = PillarIdManager.getVariantPrefix(level, worldPosition);
        String idToSync = (self.pillarId != null && !self.pillarId.isEmpty())
                ? self.pillarId
                : self.getStackPillarId();
        if (idToSync == null || idToSync.isEmpty())
            return;
        if (!idToSync.startsWith(expectedPrefix) && manager.getPillarData(idToSync) == null)
            return;

        PillarIdManager.PillarData data = manager.getPillarData(idToSync);
        boolean changed = false;

        if (data != null && data.hasColors()) {
            List<String> managerColors = data.getColors();
            boolean colorsDiffer = (self.particleColors == null ||
                    self.particleColors.isEmpty() ||
                    !idToSync.equals(self.pillarId) ||
                    managerColors.size() != self.particleColors.size());

            if (!colorsDiffer) {
                for (int i = 0; i < managerColors.size(); i++) {
                    String mc = managerColors.get(i), cc = self.particleColors.get(i);
                    if (mc == null || !mc.equals(cc)) {
                        colorsDiffer = true;
                        break;
                    }
                }
            }

            if (colorsDiffer) {
                self.pillarId = idToSync;
                self.particleColors = new ArrayList<>(managerColors);
                self.colorsInitialized = true;
                self.particleColorCounter = 0;
                self.lastParticleTick = 0;
                self.colorsVersion++;
                changed = true;
            }
        }

        if (data != null) {
            String managerPattern = data.pattern;
            if (managerPattern != null && !managerPattern.isEmpty()) {
                boolean validPattern = false;
                for (String p : PillarBlockEntity.PATTERNS) {
                    if (p.equals(managerPattern)) {
                        validPattern = true;
                        break;
                    }
                }
                if (validPattern && (self.particlePattern == null || !self.particlePattern.equals(managerPattern))) {
                    self.particlePattern = managerPattern;
                    changed = true;
                }
            } else if (self.particlePattern != null) {
                self.particlePattern = null;
                changed = true;
            }

            if (data.pattern_speed != null
                    && (self.patternSpeed == null || !self.patternSpeed.equals(data.pattern_speed))) {
                self.patternSpeed = data.pattern_speed;
                changed = true;
            } else if (data.pattern_speed == null && self.patternSpeed != null) {
                self.patternSpeed = null;
                changed = true;
            }

            if (data.pattern_spread != null
                    && (self.patternSpread == null || !self.patternSpread.equals(data.pattern_spread))) {
                self.patternSpread = data.pattern_spread;
                changed = true;
            } else if (data.pattern_spread == null && self.patternSpread != null) {
                self.patternSpread = null;
                changed = true;
            }

            if (data.pattern_intensity != null
                    && (self.patternIntensity == null || !self.patternIntensity.equals(data.pattern_intensity))) {
                self.patternIntensity = data.pattern_intensity;
                changed = true;
            } else if (data.pattern_intensity == null && self.patternIntensity != null) {
                self.patternIntensity = null;
                changed = true;
            }
        }

        if (changed) {
            self.setChanged();
            level.sendBlockUpdated(worldPosition, self.getBlockState(), self.getBlockState(), 3);
        }
    }

    static void syncColorsFromManager(PillarBlockEntity self) {
        final Level level = self.getLevel();
        final BlockPos worldPosition = self.getBlockPos();
        if (level == null || level.isClientSide)
            return;

        if (level.getServer() == null ||
                !level.getServer().isRunning() ||
                !BuildScape.isServerFullyInitialized()) {
            return;
        }

        if (level.getServer().getPlayerList().getPlayerCount() == 0) {
            return;
        }

        if (!level.hasChunkAt(worldPosition)) {
            return;
        }

        try {
            ChunkAccess chunk = level.getChunk(
                    worldPosition);
            if (!(chunk instanceof LevelChunk)) {
                return;
            }
            if (!chunk
                    .getStatus()
                    .isOrAfter(ChunkStatus.FULL)) {
                return;
            }
        } catch (Exception e) {
            return;
        }


        PillarIdManager manager = PillarIdManager.get(level);

        if (!manager.hasLoaded()) {
            return;
        }
        String expectedPrefix = PillarIdManager.getVariantPrefix(
                level,
                worldPosition);

        String idToSync = self.pillarId;

        if (idToSync == null || idToSync.isEmpty()) {
            idToSync = self.getStackPillarId();
        }

        if (idToSync == null || idToSync.isEmpty()) {
            return;
        }

        if (!idToSync.startsWith(expectedPrefix) && manager.getPillarData(idToSync) == null) {
            if (self.particleColors == null || self.particleColors.isEmpty()) {
                self.pillarId = null;
                self.particleColors = null;
                self.colorsInitialized = false;
                self.particleColorCounter = 0;
                self.setChanged();
                level.sendBlockUpdated(
                        worldPosition,
                        self.getBlockState(),
                        self.getBlockState(),
                        3);
            }
            return;
        }

        PillarIdManager.PillarData data = manager.getPillarData(idToSync);

        if (data != null && data.hasColors()) {
            List<String> managerColors = data.getColors();
            boolean shouldSync = false;

            if (self.pillarId == null || self.pillarId.isEmpty()) {
                shouldSync = true;
            } else if (!self.pillarId.equals(idToSync)) {
                shouldSync = true;
            } else if (self.particleColors == null || self.particleColors.isEmpty()) {
                shouldSync = true;
            } else {
                if (managerColors.size() != self.particleColors.size()) {
                    shouldSync = true;
                } else {
                    for (int i = 0; i < managerColors.size(); i++) {
                        String managerColor = managerColors.get(i);
                        String currentColor = self.particleColors.get(i);
                        if (managerColor == null || !managerColor.equals(currentColor)) {
                            shouldSync = true;
                            break;
                        }
                    }
                }
            }

            if (shouldSync) {
                self.pillarId = idToSync;
                self.particleColors = new ArrayList<>(managerColors);
                self.colorsInitialized = true;
                self.particleColorCounter = 0;
                self.lastParticleTick = 0;
                self.setChanged();

                level.sendBlockUpdated(
                        worldPosition,
                        self.getBlockState(),
                        self.getBlockState(),
                        3);
            }
        } else {
            if (self.pillarId != null && self.pillarId.equals(idToSync) &&
                    self.particleColors != null && !self.particleColors.isEmpty()) {
                BlockPos bottomPos = self.findStackBottom();
                PillarIdManager.PillarData newData = manager.getPillarDataByPosition(level, bottomPos);
                if (newData == null) {
                    newData = manager.getOrCreatePillarData(level, bottomPos);
                }
                if (newData != null && idToSync.equals(newData.id)) {
                    boolean needsSync = false;
                    if (!newData.hasColors()) {
                        needsSync = true;
                    } else {
                        List<String> managerColors = newData.getColors();
                        if (managerColors.size() != self.particleColors.size()) {
                            needsSync = true;
                        } else {
                            for (int i = 0; i < self.particleColors.size(); i++) {
                                String nbtColor = self.particleColors.get(i);
                                String managerColor = i < managerColors.size() ? managerColors.get(i) : null;
                                if (nbtColor == null || !nbtColor.equals(managerColor)) {
                                    needsSync = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (needsSync) {
                        newData.clearColors();
                        for (String color : self.particleColors) {
                            if (color != null && !color.isEmpty()) {
                                newData.addColor(color);
                            }
                        }
                    }
                }
            }
        }
    }

    static void syncFromData(PillarBlockEntity self, PillarIdManager.PillarData data) {
        final Level level = self.getLevel();
        final BlockPos worldPosition = self.getBlockPos();
        if (data == null)
            return;

        boolean changed = false;

        if (self.pillarId == null || !self.pillarId.equals(data.id)) {
            self.pillarId = data.id;
            changed = true;
        }

        if (data.hasColors()) {
            List<String> newColors = data.getColors();
            if (self.particleColors == null || self.particleColors.size() != newColors.size()) {
                self.particleColors = new ArrayList<>(newColors);
                self.colorsInitialized = true;
                self.particleColorCounter = 0;
                self.colorsVersion++;
                changed = true;
            } else {
                for (int i = 0; i < newColors.size(); i++) {
                    if (!newColors.get(i).equals(self.particleColors.get(i))) {
                        self.particleColors = new ArrayList<>(newColors);
                        self.colorsInitialized = true;
                        self.particleColorCounter = 0;
                        self.colorsVersion++;
                        changed = true;
                        break;
                    }
                }
            }
        } else if (self.particleColors != null) {
            self.particleColors = null;
            self.colorsInitialized = false;
            self.particleColorCounter = 0;
            self.colorsVersion++;
            changed = true;
        }

        if (data.pattern != null && !data.pattern.equals(self.particlePattern)) {
            self.particlePattern = data.pattern;
            changed = true;
        } else if (data.pattern == null && self.particlePattern != null) {
            self.particlePattern = null;
            changed = true;
        }

        if (data.use_pattern != null && (!data.use_pattern.equals(self.usePattern))) {
            self.usePattern = data.use_pattern;
            changed = true;
        } else if (data.use_pattern == null && self.usePattern != null) {
            self.usePattern = null;
            changed = true;
        }

        if (data.pattern_speed != null && (!data.pattern_speed.equals(self.patternSpeed))) {
            self.patternSpeed = data.pattern_speed;
            changed = true;
        } else if (data.pattern_speed == null && self.patternSpeed != null) {
            self.patternSpeed = null;
            changed = true;
        }

        if (data.pattern_spread != null && (!data.pattern_spread.equals(self.patternSpread))) {
            self.patternSpread = data.pattern_spread;
            changed = true;
        } else if (data.pattern_spread == null && self.patternSpread != null) {
            self.patternSpread = null;
            changed = true;
        }

        if (data.pattern_intensity != null && (!data.pattern_intensity.equals(self.patternIntensity))) {
            self.patternIntensity = data.pattern_intensity;
            changed = true;
        } else if (data.pattern_intensity == null && self.patternIntensity != null) {
            self.patternIntensity = null;
            changed = true;
        }

        if (data.max_particle_color != null && (!data.max_particle_color.equals(self.maxParticleColor))) {
            self.maxParticleColor = data.max_particle_color;
            changed = true;
        } else if (data.max_particle_color == null && self.maxParticleColor != null) {
            self.maxParticleColor = null;
            changed = true;
        }

        if (changed) {
            self.lastParticleTick = 0;
            self.setChanged();
            if (level != null && !level.isClientSide) {
                BlockState currentState = self.getBlockState();
                level.sendBlockUpdated(worldPosition, currentState, currentState, 3);
                level.getChunkAt(worldPosition).setUnsaved(true);
            }
        }
    }
}
