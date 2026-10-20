package com.kingodogo.buildscape.feature.sculk;

import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.registry.ModSounds;
import com.kingodogo.buildscape.registry.blocks.CopperBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public final class SculkSpreader {
    public static final TagKey<Block> REPLACEABLE = TagKey.create(Registry.BLOCK_REGISTRY,
            new ResourceLocation("buildscape", "sculk_replaceable"));
    private final List<ChargeCursor> cursors = new ArrayList<>();

    public void addCursors(BlockPos pos, int experience) {
        for (int charge : SculkCharge.split(experience, SculkCharge.MAX_CURSORS - cursors.size())) {
            cursors.add(new ChargeCursor(pos, charge));
        }
    }

    public boolean updateCursors(ServerLevel level, BlockPos origin) {
        if (cursors.isEmpty()) {
            return false;
        }
        boolean changed = false;
        for (ChargeCursor cursor : cursors) {
            if (Math.max(Math.abs((long) cursor.pos.getX() - origin.getX()),
                    Math.max(Math.abs((long) cursor.pos.getY() - origin.getY()),
                            Math.abs((long) cursor.pos.getZ() - origin.getZ()))) > 1024) {
                cursor.charge = 0;
                changed = true;
                continue;
            }
            if (!isAreaLoaded(level, cursor.pos)) {
                continue;
            }
            changed = true;
            cursor.update(level, origin);
        }
        cursors.removeIf(cursor -> cursor.charge <= 0);
        Map<BlockPos, ChargeCursor> mergeTargets = new HashMap<>();
        List<ChargeCursor> remaining = new ArrayList<>();
        for (ChargeCursor cursor : cursors) {
            ChargeCursor target = mergeTargets.get(cursor.pos);
            if (target != null && target.charge + cursor.charge <= SculkCharge.MAX_CHARGE) {
                target.charge += cursor.charge;
                target.updateDelay = Math.min(target.updateDelay, cursor.updateDelay);
                changed = true;
            } else {
                remaining.add(cursor);
                if (target == null || cursor.charge < target.charge) {
                    mergeTargets.put(cursor.pos, cursor);
                }
            }
        }
        cursors.clear();
        cursors.addAll(remaining);
        return changed;
    }

    private static boolean isAreaLoaded(ServerLevel level, BlockPos pos) {
        return !level.isOutsideBuildHeight(pos)
                && level.hasChunkAt(pos.offset(-2, 0, -2)) && level.hasChunkAt(pos.offset(-2, 0, 2))
                && level.hasChunkAt(pos.offset(2, 0, -2)) && level.hasChunkAt(pos.offset(2, 0, 2));
    }

    private static boolean hasSubstrate(ServerLevel level, BlockPos pos, BlockState state) {
        if (!state.is(CopperBlocks.SCULK_VEIN.get())) {
            return false;
        }
        for (Direction face : Direction.values()) {
            BlockPos target = pos.relative(face);
            if (state.getValue(GlowLichenBlock.getFaceProperty(face)) && level.hasChunkAt(target)
                    && level.getBlockState(target).is(REPLACEABLE)) {
                return true;
            }
        }
        return false;
    }

    private static boolean convertSubstrate(ServerLevel level, BlockPos pos, Random random) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(CopperBlocks.SCULK_VEIN.get())) {
            return false;
        }
        List<Direction> faces = Arrays.asList(Direction.values());
        Collections.shuffle(faces, random);
        for (Direction face : faces) {
            BlockPos target = pos.relative(face);
            if (state.getValue(GlowLichenBlock.getFaceProperty(face)) && level.hasChunkAt(target)
                    && level.getBlockState(target).is(REPLACEABLE)
                    && level.setBlock(target, CopperBlocks.SCULK.get().defaultBlockState(), 3)) {
                level.playSound(null, target, ModSounds.SCULK_SPREAD.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                SculkVeinBlock.spread(level, target, random);
                for (Direction direction : Direction.values()) {
                    if (direction != face.getOpposite()) {
                        SculkVeinBlock.discharge(level, target.relative(direction));
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static boolean canPlaceGrowth(ServerLevel level, BlockPos pos) {
        if (!isGrowthSpace(level.getBlockState(pos.above()))) {
            return false;
        }
        int nearby = 0;
        for (BlockPos check : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 2, 4))) {
            if (!level.hasChunkAt(check)) {
                return false;
            }
            BlockState state = level.getBlockState(check);
            if (isGrowth(state) && !SculkGrowthRules.hasGrowthCapacity(++nearby)) {
                return false;
            }
        }
        return true;
    }

    static boolean isGrowthSpace(BlockState state) {
        return state.isAir() || state.is(Blocks.WATER) && state.getFluidState().is(Fluids.WATER)
                && state.getFluidState().isSource();
    }

    static boolean isGrowth(BlockState state) {
        return state.is(Blocks.SCULK_SENSOR) || state.is(CopperBlocks.SCULK_SENSOR.get())
                || state.is(CopperBlocks.SCULK_SHRIEKER.get());
    }

    static BlockState growthState(int choice, boolean water) {
        BlockState state = choice == 0
                ? CopperBlocks.SCULK_SHRIEKER.get().defaultBlockState()
                : Blocks.SCULK_SENSOR.defaultBlockState();
        return water && state.hasProperty(BlockStateProperties.WATERLOGGED)
                ? state.setValue(BlockStateProperties.WATERLOGGED, true)
                : state;
    }

    private static BlockPos movementPos(ServerLevel level, BlockPos pos, Random random) {
        List<BlockPos> candidates = new ArrayList<>(18);
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    int distance = Math.abs(x) + Math.abs(y) + Math.abs(z);
                    if (distance == 0 || distance > 2) {
                        continue;
                    }
                    BlockPos target = pos.offset(x, y, z);
                    if (level.isOutsideBuildHeight(target) || !level.hasChunkAt(target)) {
                        continue;
                    }
                    BlockState state = level.getBlockState(target);
                    if (!state.is(CopperBlocks.SCULK.get()) && !state.is(CopperBlocks.SCULK_VEIN.get())) {
                        continue;
                    }
                    if (distance == 2) {
                        boolean blocked = true;
                        if (x != 0) {
                            blocked &= isMovementBlocked(level, pos, x < 0 ? Direction.WEST : Direction.EAST);
                        }
                        if (y != 0) {
                            blocked &= isMovementBlocked(level, pos, y < 0 ? Direction.DOWN : Direction.UP);
                        }
                        if (z != 0) {
                            blocked &= isMovementBlocked(level, pos, z < 0 ? Direction.NORTH : Direction.SOUTH);
                        }
                        if (blocked) {
                            continue;
                        }
                    }
                    candidates.add(target);
                }
            }
        }
        Collections.shuffle(candidates, random);
        for (BlockPos candidate : candidates) {
            if (hasSubstrate(level, candidate, level.getBlockState(candidate))) {
                return candidate;
            }
        }
        return candidates.isEmpty() ? null : candidates.get(candidates.size() - 1);
    }

    private static boolean isMovementBlocked(ServerLevel level, BlockPos pos, Direction direction) {
        BlockPos adjacent = pos.relative(direction);
        return level.getBlockState(adjacent).isFaceSturdy(level, adjacent, direction.getOpposite());
    }

    public void save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (ChargeCursor cursor : cursors) {
            CompoundTag saved = new CompoundTag();
            saved.put(NbtKeys.POS, NbtUtils.writeBlockPos(cursor.pos));
            saved.putInt("charge", cursor.charge);
            saved.putInt("update_delay", cursor.updateDelay);
            saved.putInt("decay_delay", cursor.decayDelay);
            if (cursor.facings != null) {
                ListTag facings = new ListTag();
                for (Direction face : cursor.facings) {
                    facings.add(StringTag.valueOf(face.getName()));
                }
                saved.put("facings", facings);
            }
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
            if (charge == 0 || !saved.contains(NbtKeys.POS, Tag.TAG_COMPOUND)) {
                continue;
            }
            ChargeCursor cursor = new ChargeCursor(NbtUtils.readBlockPos(saved.getCompound(NbtKeys.POS)), charge);
            cursor.updateDelay = Math.max(0, saved.getInt("update_delay"));
            cursor.decayDelay = saved.contains("decay_delay", Tag.TAG_ANY_NUMERIC)
                    ? Math.max(0, Math.min(saved.getInt("decay_delay"), 1))
                    : 1;
            if (saved.contains("facings", Tag.TAG_LIST)) {
                cursor.facings = EnumSet.noneOf(Direction.class);
                ListTag facings = saved.getList("facings", Tag.TAG_STRING);
                for (int j = 0; j < facings.size(); j++) {
                    Direction face = Direction.byName(facings.getString(j));
                    if (face != null) {
                        cursor.facings.add(face);
                    }
                }
            }
            cursors.add(cursor);
        }
    }

    private static final class ChargeCursor {
        private BlockPos pos;
        private int charge;
        private int updateDelay;
        private int decayDelay = 1;
        private Set<Direction> facings;

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
            boolean spreadFromBlock = state.is(CopperBlocks.SCULK.get()) || state.is(CopperBlocks.SCULK_VEIN.get());
            boolean spreadVeins = false;
            if (!spreadFromBlock) {
                spreadVeins = SculkVeinBlock.regrow(level, pos, facings);
                state = level.getBlockState(pos);
            }
            boolean sculk = state.is(CopperBlocks.SCULK.get());
            boolean vein = state.is(CopperBlocks.SCULK_VEIN.get());
            if (spreadFromBlock) {
                spreadVeins |= SculkVeinBlock.spread(level, pos, random);
            }
            if (vein) {
                if (convertSubstrate(level, pos, random)) {
                    charge--;
                } else if (random.nextInt(SculkGrowthRules.CHARGE_DECAY_RATE) == 0) {
                    charge /= 2;
                }
            } else if (sculk) {
                double distanceSquared = pos.distSqr(origin);
                SculkGrowthRules.ChargeUse use = SculkGrowthRules.useCharge(charge, distanceSquared,
                        () -> canPlaceGrowth(level, pos), random);
                if (use.grow()) {
                    BlockState growth = growthState(random.nextInt(11), !level.getFluidState(pos.above()).isEmpty());
                    level.setBlock(pos.above(), growth, 3);
                    level.playSound(null, pos, growth.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                charge = use.remainingCharge();
            } else {
                charge = SculkGrowthRules.chargeOnDefaultCell(charge, decayDelay);
            }
            if (spreadVeins) {
                level.playSound(null, pos, ModSounds.SCULK_SPREAD.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            BlockPos next = charge > 0 ? movementPos(level, pos, random) : null;
            if (charge <= 0 || next != null) {
                SculkVeinBlock.discharge(level, pos);
                if (next != null) {
                    pos = next;
                }
            }
            if (charge > 0) {
                BlockState current = level.getBlockState(pos);
                if (current.is(CopperBlocks.SCULK.get()) || current.is(CopperBlocks.SCULK_VEIN.get())) {
                    facings = EnumSet.noneOf(Direction.class);
                    if (current.is(CopperBlocks.SCULK_VEIN.get())) {
                        for (Direction face : Direction.values()) {
                            if (current.getValue(GlowLichenBlock.getFaceProperty(face))) {
                                facings.add(face);
                            }
                        }
                    }
                }
                decayDelay = SculkGrowthRules.nextDecayDelay(sculk || vein, decayDelay);
            }
            if (charge > 0 && random.nextInt(5) == 0) {
                level.sendParticles(ParticleTypes.SOUL, pos.getX() + 0.5, pos.getY() + 0.5,
                        pos.getZ() + 0.5, 1, 0.2, 0.2, 0.2, 0.0);
            }
            updateDelay = 1;
        }
    }
}
