package com.kingodogo.buildscape.block.sculk;

import com.kingodogo.buildscape.block.SculkVeinBlock;
import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.registry.ModBlocks;
import com.kingodogo.buildscape.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class SculkSpreader {
    public static final TagKey<Block> REPLACEABLE = TagKey.create(Registry.BLOCK_REGISTRY,
            new ResourceLocation("buildscape", "sculk_replaceable"));
    private static final int GROWTH_COST = 10;
    private static final int NO_GROWTH_RADIUS = 4;
    private static final int CHARGE_DECAY_RATE = 10;
    private static final int ADDITIONAL_DECAY_RATE = 5;
    private final List<ChargeCursor> cursors = new ArrayList<>();

    public void addCursors(BlockPos pos, int experience) {
        for (int charge : SculkCharge.split(experience, SculkCharge.MAX_CURSORS - cursors.size())) {
            cursors.add(new ChargeCursor(pos, charge));
        }
    }

    public boolean updateCursors(ServerLevel level, BlockPos origin) {
        if (cursors.isEmpty()) return false;
        boolean changed = false;
        for (ChargeCursor cursor : cursors) {
            if (!isAreaLoaded(level, cursor.pos)) continue;
            changed = true;
            cursor.update(level, origin);
        }
        cursors.removeIf(cursor -> cursor.charge <= 0);
        for (int i = 0; i < cursors.size(); i++) {
            ChargeCursor first = cursors.get(i);
            for (int j = cursors.size() - 1; j > i; j--) {
                ChargeCursor second = cursors.get(j);
                if (first.pos.equals(second.pos) && first.charge + second.charge <= SculkCharge.MAX_CHARGE) {
                    first.charge += second.charge;
                    first.updateDelay = Math.min(first.updateDelay, second.updateDelay);
                    first.decayDelay = Math.min(first.decayDelay, second.decayDelay);
                    cursors.remove(j);
                    changed = true;
                }
            }
        }
        return changed;
    }

    private static boolean isAreaLoaded(ServerLevel level, BlockPos pos) {
        return !level.isOutsideBuildHeight(pos)
                && level.hasChunkAt(pos.offset(-2, 0, -2)) && level.hasChunkAt(pos.offset(-2, 0, 2))
                && level.hasChunkAt(pos.offset(2, 0, -2)) && level.hasChunkAt(pos.offset(2, 0, 2));
    }

    private static boolean hasSubstrate(ServerLevel level, BlockPos pos, BlockState state) {
        if (!state.is(ModBlocks.SCULK_VEIN.get())) return false;
        for (Direction face : Direction.values()) {
            BlockPos target = pos.relative(face);
            if (state.getValue(GlowLichenBlock.getFaceProperty(face)) && level.hasChunkAt(target)
                    && level.getBlockState(target).is(REPLACEABLE)) return true;
        }
        return false;
    }

    private static boolean convertSubstrate(ServerLevel level, BlockPos pos, Random random) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(ModBlocks.SCULK_VEIN.get())) return false;
        List<Direction> faces = Arrays.asList(Direction.values());
        Collections.shuffle(faces, random);
        for (Direction face : faces) {
            BlockPos target = pos.relative(face);
            if (state.getValue(GlowLichenBlock.getFaceProperty(face)) && level.hasChunkAt(target)
                    && level.getBlockState(target).is(REPLACEABLE)
                    && level.setBlock(target, ModBlocks.SCULK.get().defaultBlockState(), 3)) {
                level.playSound(null, target, ModSounds.SCULK_SPREAD.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                for (Direction direction : Direction.values()) SculkVeinBlock.regrow(level, target.relative(direction));
                return true;
            }
        }
        return false;
    }

    private static boolean canPlaceGrowth(ServerLevel level, BlockPos pos) {
        if (!level.isEmptyBlock(pos.above())) return false;
        int nearby = 0;
        for (BlockPos check : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 2, 4))) {
            if (!level.hasChunkAt(check)) return false;
            BlockState state = level.getBlockState(check);
            if ((state.is(ModBlocks.SCULK_SENSOR.get()) || state.is(ModBlocks.SCULK_SHRIEKER.get())) && ++nearby > 2) return false;
        }
        return true;
    }

    private static BlockPos movementPos(ServerLevel level, BlockPos pos, Random random) {
        List<BlockPos> candidates = new ArrayList<>(18);
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    int distance = Math.abs(x) + Math.abs(y) + Math.abs(z);
                    if (distance == 0 || distance > 2) continue;
                    BlockPos target = pos.offset(x, y, z);
                    if (level.isOutsideBuildHeight(target) || !level.hasChunkAt(target)) continue;
                    BlockState state = level.getBlockState(target);
                    if (!state.is(ModBlocks.SCULK.get()) && !state.is(ModBlocks.SCULK_VEIN.get())) continue;
                    if (distance == 2) {
                        boolean blocked = true;
                        if (x != 0) blocked &= level.getBlockState(pos.offset(x, 0, 0)).isCollisionShapeFullBlock(level, pos.offset(x, 0, 0));
                        if (y != 0) blocked &= level.getBlockState(pos.offset(0, y, 0)).isCollisionShapeFullBlock(level, pos.offset(0, y, 0));
                        if (z != 0) blocked &= level.getBlockState(pos.offset(0, 0, z)).isCollisionShapeFullBlock(level, pos.offset(0, 0, z));
                        if (blocked) continue;
                    }
                    candidates.add(target);
                }
            }
        }
        Collections.shuffle(candidates, random);
        for (BlockPos candidate : candidates) {
            if (hasSubstrate(level, candidate, level.getBlockState(candidate))) return candidate;
        }
        return candidates.isEmpty() ? null : candidates.get(0);
    }

    public void save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (ChargeCursor cursor : cursors) {
            CompoundTag saved = new CompoundTag();
            saved.put(NbtKeys.POS, NbtUtils.writeBlockPos(cursor.pos));
            saved.putInt("charge", cursor.charge);
            saved.putInt("update_delay", cursor.updateDelay);
            saved.putInt("decay_delay", cursor.decayDelay);
            list.add(saved);
        }
        tag.put("cursors", list);
    }

    public void load(CompoundTag tag) {
        cursors.clear();
        ListTag list = tag.getList("cursors", Tag.TAG_COMPOUND);
        for (int i = 0; i < Math.min(list.size(), SculkCharge.MAX_CURSORS); i++) {
            CompoundTag saved = list.getCompound(i);
            int charge = Math.max(0, Math.min(saved.getInt("charge"), SculkCharge.MAX_CHARGE));
            if (charge == 0 || !saved.contains(NbtKeys.POS, Tag.TAG_COMPOUND)) continue;
            ChargeCursor cursor = new ChargeCursor(NbtUtils.readBlockPos(saved.getCompound(NbtKeys.POS)), charge);
            cursor.updateDelay = Math.max(0, Math.min(saved.getInt("update_delay"), 1));
            cursor.decayDelay = Math.max(0, Math.min(saved.getInt("decay_delay"), 1));
            cursors.add(cursor);
        }
    }

    private static final class ChargeCursor {
        private BlockPos pos;
        private int charge;
        private int updateDelay;
        private int decayDelay = 1;

        private ChargeCursor(BlockPos pos, int charge) {
            this.pos = pos.immutable();
            this.charge = charge;
        }

        private void update(ServerLevel level, BlockPos origin) {
            if (updateDelay > 0) {
                updateDelay--;
                return;
            }
            Random random = level.random;
            BlockState state = level.getBlockState(pos);
            boolean spreadVeins = false;
            if (!state.is(ModBlocks.SCULK.get()) && !state.is(ModBlocks.SCULK_VEIN.get())) {
                spreadVeins = SculkVeinBlock.regrow(level, pos);
                state = level.getBlockState(pos);
            }
            if (state.is(ModBlocks.SCULK_VEIN.get())) {
                spreadVeins |= SculkVeinBlock.spread(level, pos, random);
                if (spreadVeins) {
                    level.playSound(null, pos, ModSounds.SCULK_SPREAD.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                if (convertSubstrate(level, pos, random)) charge--;
                else if (random.nextInt(CHARGE_DECAY_RATE) == 0) charge /= 2;
            } else if (state.is(ModBlocks.SCULK.get())) {
                if (random.nextInt(CHARGE_DECAY_RATE) == 0) {
                    if (pos.distSqr(origin) > NO_GROWTH_RADIUS * NO_GROWTH_RADIUS && canPlaceGrowth(level, pos)) {
                        if (random.nextInt(GROWTH_COST) < charge) {
                            Block growth = random.nextInt(11) == 0 ? ModBlocks.SCULK_SHRIEKER.get() : ModBlocks.SCULK_SENSOR.get();
                            if (level.setBlock(pos.above(), growth.defaultBlockState(), 3)) {
                                level.playSound(null, pos, ModSounds.SCULK_CHARGE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                            }
                        }
                        charge = Math.max(0, charge - GROWTH_COST);
                    } else if (random.nextInt(ADDITIONAL_DECAY_RATE) == 0) {
                        charge /= 2;
                    }
                }
            }
            // Any other cell (for example the air a mob died in, above sculk that refuses a vein) keeps its
            // charge: it moves onto adjacent sculk below, or halves with the normal decay delay, as in vanilla 1.19.
            BlockPos next = charge > 0 ? movementPos(level, pos, random) : null;
            if (charge <= 0 || next != null) {
                SculkVeinBlock.discharge(level, pos);
                if (next != null) pos = next;
            } else if (decayDelay > 0) {
                decayDelay--;
            } else {
                charge /= 2;
                decayDelay = 1;
            }
            if (charge > 0 && random.nextInt(5) == 0) {
                level.sendParticles(ParticleTypes.SOUL, pos.getX() + 0.5D, pos.getY() + 0.5D,
                        pos.getZ() + 0.5D, 1, 0.2D, 0.2D, 0.2D, 0.0D);
            }
            updateDelay = 1;
        }
    }
}
