package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.block.CreakingHeartBlock;
import com.kingodogo.buildscape.block.HangingMossBlock;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class BackportBonemealHandler {
    private BackportBonemealHandler() {}

    private static Block block(String name) {
        return Services.PLATFORM.getBlock(name.contains(":") ? CommonId.parse(name) : new CommonId("buildscape", name));
    }

    private static boolean is(BlockState state, String name) {
        var id = Services.PLATFORM.getBlockId(state.getBlock());
        return id != null && "buildscape".equals(id.getNamespace()) && name.equals(id.getPath());
    }

    private static boolean place(Level level, BlockPos pos, String name) {
        Block block = block(name);
        return block != null && block != Blocks.AIR && level.setBlock(pos, block.defaultBlockState(), 3);
    }

    public static InteractionResult use(Player player, Level level, InteractionHand hand, BlockPos pos, Direction face) {
        ItemStack held = player.getItemInHand(hand);
        if (!held.is(Items.BONE_MEAL)) return InteractionResult.PASS;
        BlockState state = level.getBlockState(pos);
        if (face == Direction.UP && state.is(Blocks.PODZOL)) {
            var positions = new java.util.ArrayList<BlockPos>();
            if (level.isEmptyBlock(pos.above())) positions.add(pos.above());
            else for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                BlockPos ground = pos.offset(dx, 0, dz);
                if (level.getBlockState(ground).is(Blocks.PODZOL) && level.isEmptyBlock(ground.above())) positions.add(ground.above());
            }
            if (!positions.isEmpty()) {
                if (!level.isClientSide()) {
                    level.setBlock(positions.get(level.getRandom().nextInt(positions.size())),
                            com.kingodogo.buildscape.block.ModBlocks.WHITE_HAZE_BUSH.get().defaultBlockState(), 3);
                }
                return consume(player, level, held, pos);
            }
        }
        if (face == Direction.UP && (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.MOSS_BLOCK))) {
            if (level instanceof ServerLevel serverLevel) {
                boolean moss = state.is(Blocks.MOSS_BLOCK);
                BlockPos origin = pos.immutable();
                serverLevel.getServer().execute(() -> {
                    for (BlockPos check : BlockPos.betweenClosed(origin.offset(-3, -1, -3), origin.offset(3, 1, 3))) {
                        if (!level.getBlockState(check).is(moss ? Blocks.MOSS_BLOCK : Blocks.GRASS_BLOCK)
                                || !level.isEmptyBlock(check.above())) continue;
                        float roll = level.getRandom().nextFloat();
                        String plant = moss ? (roll < .15F ? "minecraft:cherry_sapling"
                                : roll < .20F ? "minecraft:mangrove_propagule" : null) : null;
                        if (plant != null) {
                            Block candidate = block(plant);
                            if (candidate != null && candidate != Blocks.AIR
                                    && candidate.defaultBlockState().canSurvive(level, check.above())) place(level, check.above(), plant);
                        }
                    }
                });
            }
            // Vanilla still performs and consumes its normal grass/moss bonemeal operation.
            return InteractionResult.PASS;
        }
        if (state.is(block("minecraft:pale_oak_leaves"))) {
            BlockPos target = pos.below();
            while (is(level.getBlockState(target), "pale_hanging_moss")) target = target.below();
            Block moss = block("pale_hanging_moss");
            if (level.isEmptyBlock(target) && moss != null && moss != Blocks.AIR) {
                if (!level.isClientSide()) {
                    BlockState newState = moss.defaultBlockState();
                    if (newState.hasProperty(HangingMossBlock.TIP)) newState = newState.setValue(HangingMossBlock.TIP, true);
                    level.setBlock(target, newState, 3);
                }
                return consume(player, level, held, pos);
            }
        }
        if (is(state, "creaking_heart")) {
            if (!level.isClientSide()) {
                if (state.hasProperty(CreakingHeartBlock.ACTIVE)) {
                    level.setBlock(pos, state.setValue(CreakingHeartBlock.ACTIVE, true), 3);
                    level.scheduleTick(pos, state.getBlock(), 40);
                }
                var directions = new java.util.ArrayList<>(java.util.List.of(Direction.values()));
                java.util.Collections.shuffle(directions, new java.util.Random(level.getRandom().nextLong()));
                Block resin = block("resin_clump");
                if (resin != null && resin != Blocks.AIR) {
                    int spawned = 0;
                    for (Direction direction : directions) {
                        BlockPos adjacent = pos.relative(direction);
                        BlockState adjacentState = level.getBlockState(adjacent);
                        var property = MultifaceBlock.getFaceProperty(direction.getOpposite());
                        BlockState resinState = is(adjacentState, "resin_clump") ? adjacentState : resin.defaultBlockState();
                        if ((adjacentState.isAir() || is(adjacentState, "resin_clump"))
                                && resinState.hasProperty(property) && !resinState.getValue(property)) {
                            level.setBlock(adjacent, resinState.setValue(property, true), 3);
                            if (++spawned >= 2) break;
                        }
                    }
                }
            }
            return consume(player, level, held, pos);
        }
        return InteractionResult.PASS;
    }

    private static InteractionResult consume(Player player, Level level, ItemStack held, BlockPos pos) {
        if (!level.isClientSide()) {
            level.levelEvent(2005, pos, 0);
            if (!player.getAbilities().instabuild) held.shrink(1);
        }
        return Services.PLATFORM.sidedSuccess(level.isClientSide());
    }
}
