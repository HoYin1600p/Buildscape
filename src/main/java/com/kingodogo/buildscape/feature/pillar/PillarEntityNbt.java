package com.kingodogo.buildscape.feature.pillar;

import com.kingodogo.buildscape.core.NbtKeys;
import java.util.ArrayList;
import java.util.Locale;
import net.minecraft.ResourceLocationException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Reads and writes the saved and synced NBT of a pillar block entity.
 */
final class PillarEntityNbt {

    private PillarEntityNbt() {}

    static void read(PillarBlockEntity self, CompoundTag tag) {

        String oldColor = (self.particleColors != null && !self.particleColors.isEmpty())
                ? self.particleColors.get(0)
                : null;

        try {
            if (tag.contains(NbtKeys.DISPLAYED_ITEM, 10)) {
                ItemStack loaded = ItemStack.of(tag.getCompound(NbtKeys.DISPLAYED_ITEM));
                self.displayedItem = loaded == null ? ItemStack.EMPTY : loaded;
            } else if (tag.contains(NbtKeys.PILLAR_ITEM, 8)) {
                String itemId = tag.getString(NbtKeys.PILLAR_ITEM);
                try {
                    ResourceLocation itemLocation = new ResourceLocation(
                            itemId);
                    Item item = ForgeRegistries.ITEMS
                            .getValue(itemLocation);
                    if (item != null && item != Items.AIR) {
                        self.displayedItem = new ItemStack(item);
                    } else {
                        self.displayedItem = ItemStack.EMPTY;
                    }
                } catch (ResourceLocationException e) {
                    self.displayedItem = ItemStack.EMPTY;
                }
            } else {
                self.displayedItem = ItemStack.EMPTY;
            }
        } catch (Exception e) {
            // displayed item NBT may be malformed in any way; show no item
            self.displayedItem = ItemStack.EMPTY;
        }

        if (tag.contains(NbtKeys.PARTICLE_PATTERN, 8)) {
            self.particlePattern = tag.getString(NbtKeys.PARTICLE_PATTERN);
            boolean valid = false;
            for (String p : PillarBlockEntity.PATTERNS) {
                if (p.equals(self.particlePattern)) {
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                self.particlePattern = null;
            }
        } else if (tag.contains(NbtKeys.PILLAR_PATTERN, 8)) {
            self.particlePattern = tag.getString(NbtKeys.PILLAR_PATTERN);
            boolean valid = false;
            for (String p : PillarBlockEntity.PATTERNS) {
                if (p.equals(self.particlePattern)) {
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                self.particlePattern = null;
            }
        } else {
            self.particlePattern = null;
        }

        if (tag.contains(NbtKeys.PATTERN_SPEED, 6)) {
            self.patternSpeed = tag.getDouble(NbtKeys.PATTERN_SPEED);
        } else {
            self.patternSpeed = null;
        }
        if (tag.contains(NbtKeys.PATTERN_SPREAD, 6)) {
            self.patternSpread = tag.getDouble(NbtKeys.PATTERN_SPREAD);
        } else {
            self.patternSpread = null;
        }
        if (tag.contains(NbtKeys.PATTERN_INTENSITY, 6)) {
            self.patternIntensity = tag.getDouble(NbtKeys.PATTERN_INTENSITY);
        } else {
            self.patternIntensity = null;
        }

        if (tag.contains(NbtKeys.MAX_PARTICLE_COLOR, 3)) {
            self.maxParticleColor = tag.getInt(NbtKeys.MAX_PARTICLE_COLOR);
        } else {
            self.maxParticleColor = null;
        }

        try {
            if (tag.contains(NbtKeys.PARTICLE_COLORS, 9)) {
                ListTag colorList = tag.getList(NbtKeys.PARTICLE_COLORS, 8);
                if (colorList.size() > 0) {
                    self.particleColors = new ArrayList<>();
                    int maxColors = Math.min(7, colorList.size());
                    for (int i = 0; i < maxColors; i++) {
                        try {
                            String color = colorList.getString(i);
                            if (color != null &&
                                    !color.isEmpty() &&
                                    color.matches("^#[0-9A-Fa-f]{6}$")) {
                                self.particleColors.add(color.toUpperCase(Locale.ROOT));
                            }
                        } catch (Exception e) {
                            // defensive guard around one colour entry
                            // skip a malformed colour entry
                        }
                    }
                    if (self.particleColors.isEmpty()) {
                        self.particleColors = null;
                    }
                } else {
                    self.particleColors = null;
                }
            } else {
                self.particleColors = null;
            }
        } catch (Exception e) {
            // colour list NBT may be malformed in any way; use the default colours
            self.particleColors = null;
        }

        if (tag.contains(NbtKeys.PARTICLE_COLOR_COUNTER, 3)) {
            self.particleColorCounter = tag.getInt(NbtKeys.PARTICLE_COLOR_COUNTER);
        } else {
            self.particleColorCounter = 0;
        }

        if (tag.contains(NbtKeys.COLORS_INITIALIZED, 1)) {
            self.colorsInitialized = tag.getBoolean(NbtKeys.COLORS_INITIALIZED);
        } else {
            self.colorsInitialized = (self.particleColors != null &&
                    !self.particleColors.isEmpty());
        }

        if (self.particleColors != null && !self.particleColors.isEmpty() && !self.colorsInitialized) {
            self.colorsInitialized = true;
        }

        if (tag.contains(NbtKeys.PILLAR_ID, 8)) {
            self.pillarId = tag.getString(NbtKeys.PILLAR_ID);
            if (self.pillarId.isEmpty()) {
                self.pillarId = null;
            }
        } else {
            self.pillarId = null;
        }

        if (tag.contains(NbtKeys.USE_PATTERN, 1)) {
            self.usePattern = tag.getBoolean(NbtKeys.USE_PATTERN);
        } else {
            self.usePattern = null;
        }

        if (tag.contains(NbtKeys.FACING_YAW, 5)) {
            self.facingYaw = tag.getFloat(NbtKeys.FACING_YAW);
            self.facingYaw = self.facingYaw % 360.0f;
            if (self.facingYaw < 0) {
                self.facingYaw += 360.0f;
            }
        } else {
            self.facingYaw = 0.0f;
        }

        String newColor = (self.particleColors != null && !self.particleColors.isEmpty())
                ? self.particleColors.get(0)
                : null;
        if (oldColor != newColor || (oldColor != null && !oldColor.equals(newColor))) {
            self.lastParticleTick = 0;
            self.particleColorCounter = 0;
        }

        if (self.pillarId != null &&
                !self.pillarId.isEmpty() &&
                (self.particleColors == null || self.particleColors.isEmpty())) {
            self.needsManagerSync = true;
        }
    }

    static void write(PillarBlockEntity self, CompoundTag tag) {

        if (!self.displayedItem.isEmpty()) {
            tag.put(NbtKeys.DISPLAYED_ITEM, self.displayedItem.save(new CompoundTag()));
        }

        if (self.particlePattern != null) {
            tag.putString(NbtKeys.PARTICLE_PATTERN, self.particlePattern);
        }

        if (self.patternSpeed != null) {
            tag.putDouble(NbtKeys.PATTERN_SPEED, self.patternSpeed);
        }
        if (self.patternSpread != null) {
            tag.putDouble(NbtKeys.PATTERN_SPREAD, self.patternSpread);
        }
        if (self.patternIntensity != null) {
            tag.putDouble(NbtKeys.PATTERN_INTENSITY, self.patternIntensity);
        }

        if (self.usePattern != null) {
            tag.putBoolean(NbtKeys.USE_PATTERN, self.usePattern);
        }

        if (self.maxParticleColor != null) {
            tag.putInt(NbtKeys.MAX_PARTICLE_COLOR, self.maxParticleColor);
        }

        if (self.particleColors != null && !self.particleColors.isEmpty()) {
            ListTag colorList = new ListTag();
            for (String color : self.particleColors) {
                colorList.add(StringTag.valueOf(color));
            }
            tag.put(NbtKeys.PARTICLE_COLORS, colorList);
        }

        if (self.pillarId != null && !self.pillarId.isEmpty()) {
            tag.putString(NbtKeys.PILLAR_ID, self.pillarId);
        }

        tag.putInt(NbtKeys.PARTICLE_COLOR_COUNTER, self.particleColorCounter);

        tag.putBoolean(NbtKeys.COLORS_INITIALIZED, self.colorsInitialized);

        tag.putFloat(NbtKeys.FACING_YAW, self.facingYaw);
    }
}
