package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
public final class CopperOxidationHandler {

    private static final Map<String, String> NEXT_STAGE = new HashMap<>();
    private static final Map<String, String> PREV_STAGE = new HashMap<>();
    private static final Map<String, String> WAXED_MAP = new HashMap<>();
    private static final Map<String, String> UNWAXED_MAP = new HashMap<>();
    private static boolean initialized = false;

    private CopperOxidationHandler() {}
    public static synchronized void init() {
        if (initialized) return;

        registerChain("copper_mesh", "exposed_copper_mesh", "weathered_copper_mesh", "oxidized_copper_mesh");
        registerWaxPair("copper_mesh", "waxed_copper_mesh");
        registerWaxPair("exposed_copper_mesh", "waxed_exposed_copper_mesh");
        registerWaxPair("weathered_copper_mesh", "waxed_weathered_copper_mesh");
        registerWaxPair("oxidized_copper_mesh", "waxed_oxidized_copper_mesh");

        registerChain("copper_bolts", "exposed_copper_bolts", "weathered_copper_bolts", "oxidized_copper_bolts");
        registerWaxPair("copper_bolts", "waxed_copper_bolts");
        registerWaxPair("exposed_copper_bolts", "waxed_exposed_copper_bolts");
        registerWaxPair("weathered_copper_bolts", "waxed_weathered_copper_bolts");
        registerWaxPair("oxidized_copper_bolts", "waxed_oxidized_copper_bolts");

        registerChain("cut_copper_vertical_slab", "exposed_cut_copper_vertical_slab", "weathered_cut_copper_vertical_slab", "oxidized_cut_copper_vertical_slab");
        registerWaxPair("cut_copper_vertical_slab", "waxed_cut_copper_vertical_slab");
        registerWaxPair("exposed_cut_copper_vertical_slab", "waxed_exposed_cut_copper_vertical_slab");
        registerWaxPair("weathered_cut_copper_vertical_slab", "waxed_weathered_cut_copper_vertical_slab");
        registerWaxPair("oxidized_cut_copper_vertical_slab", "waxed_oxidized_cut_copper_vertical_slab");

        registerChain("copper_button", "exposed_copper_button", "weathered_copper_button", "oxidized_copper_button");
        registerWaxPair("copper_button", "waxed_copper_button");
        registerWaxPair("exposed_copper_button", "waxed_exposed_copper_button");
        registerWaxPair("weathered_copper_button", "waxed_weathered_copper_button");
        registerWaxPair("oxidized_copper_button", "waxed_oxidized_copper_button");

        registerChain("copper_pressure_plate", "exposed_copper_pressure_plate", "weathered_copper_pressure_plate", "oxidized_copper_pressure_plate");
        registerWaxPair("copper_pressure_plate", "waxed_copper_pressure_plate");
        registerWaxPair("exposed_copper_pressure_plate", "waxed_exposed_copper_pressure_plate");
        registerWaxPair("weathered_copper_pressure_plate", "waxed_weathered_copper_pressure_plate");
        registerWaxPair("oxidized_copper_pressure_plate", "waxed_oxidized_copper_pressure_plate");

        registerChain("copper_chest", "exposed_copper_chest", "weathered_copper_chest", "oxidized_copper_chest");
        registerWaxPair("copper_chest", "waxed_copper_chest");
        registerWaxPair("exposed_copper_chest", "waxed_exposed_copper_chest");
        registerWaxPair("weathered_copper_chest", "waxed_weathered_copper_chest");
        registerWaxPair("oxidized_copper_chest", "waxed_oxidized_copper_chest");
        registerChain("large_copper_chain", "large_exposed_copper_chain", "large_weathered_copper_chain", "large_oxidized_copper_chain");

        registerChain("slit_copper", "exposed_slit_copper", "weathered_slit_copper", "oxidized_slit_copper");
        registerWaxPair("slit_copper", "waxed_slit_copper");
        registerWaxPair("exposed_slit_copper", "waxed_exposed_slit_copper");
        registerWaxPair("weathered_slit_copper", "waxed_weathered_slit_copper");
        registerWaxPair("oxidized_slit_copper", "waxed_oxidized_slit_copper");

        registerChain("slit_copper_stairs", "exposed_slit_copper_stairs", "weathered_slit_copper_stairs", "oxidized_slit_copper_stairs");
        registerWaxPair("slit_copper_stairs", "waxed_slit_copper_stairs");
        registerWaxPair("exposed_slit_copper_stairs", "waxed_exposed_slit_copper_stairs");
        registerWaxPair("weathered_slit_copper_stairs", "waxed_weathered_slit_copper_stairs");
        registerWaxPair("oxidized_slit_copper_stairs", "waxed_oxidized_slit_copper_stairs");

        registerChain("slit_copper_slab", "exposed_slit_copper_slab", "weathered_slit_copper_slab", "oxidized_slit_copper_slab");
        registerWaxPair("slit_copper_slab", "waxed_slit_copper_slab");
        registerWaxPair("exposed_slit_copper_slab", "waxed_exposed_slit_copper_slab");
        registerWaxPair("weathered_slit_copper_slab", "waxed_weathered_slit_copper_slab");
        registerWaxPair("oxidized_slit_copper_slab", "waxed_oxidized_slit_copper_slab");

        registerChain("slit_copper_vertical_slab", "exposed_slit_copper_vertical_slab", "weathered_slit_copper_vertical_slab", "oxidized_slit_copper_vertical_slab");
        registerWaxPair("slit_copper_vertical_slab", "waxed_slit_copper_vertical_slab");
        registerWaxPair("exposed_slit_copper_vertical_slab", "waxed_exposed_slit_copper_vertical_slab");
        registerWaxPair("weathered_slit_copper_vertical_slab", "waxed_weathered_slit_copper_vertical_slab");
        registerWaxPair("oxidized_slit_copper_vertical_slab", "waxed_oxidized_slit_copper_vertical_slab");

        initialized = true;
    }

    private static void registerChain(String b0, String b1, String b2, String b3) {
        NEXT_STAGE.put(b0, b1);
        NEXT_STAGE.put(b1, b2);
        NEXT_STAGE.put(b2, b3);

        PREV_STAGE.put(b3, b2);
        PREV_STAGE.put(b2, b1);
        PREV_STAGE.put(b1, b0);
    }

    private static void registerWaxPair(String unwaxed, String waxed) {
        WAXED_MAP.put(unwaxed, waxed);
        UNWAXED_MAP.put(waxed, unwaxed);
    }

    private static Block getModBlock(String path) {
        return Services.PLATFORM.getBlock(new CommonId("buildscape", path));
    }
    public static InteractionResult handleRightClick(Level level, BlockPos pos, BlockState state, ItemStack held, Player player, InteractionHand hand) {
        init();
        Block block = state.getBlock();
        CommonId blockId = Services.PLATFORM.getBlockId(block);

        CommonId bottleOfMistId = new CommonId("buildscape", "bottle_of_mist");
        CommonId heldItemId = Services.PLATFORM.getItemId(held.getItem());

        if (heldItemId != null && heldItemId.equals(bottleOfMistId)) {
            BlockState nextState = getNextOxidationState(state);
            if (nextState != null) {
                if (!level.isClientSide()) {
                    setBlockStateOrDoor(level, pos, state, nextState.getBlock());

                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(
                                ParticleTypes.SMOKE,
                                pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                                8, 0.25D, 0.25D, 0.25D, 0.05D
                        );
                    }

                    if (player != null && !player.getAbilities().instabuild) {
                        held.shrink(1);
                        ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);
                        if (held.isEmpty()) {
                            player.setItemInHand(hand, emptyBottle);
                        } else if (!player.getInventory().add(emptyBottle)) {
                            player.drop(emptyBottle, false);
                        }
                    }
                }

                Services.PLATFORM.playFireExtinguish(level, pos);
                return InteractionResult.SUCCESS;
            }
        }

        if (held.is(Items.HONEYCOMB)) {

            if (blockId != null && blockId.getNamespace().equals("buildscape")) {
                String waxedPath = WAXED_MAP.get(blockId.getPath());
                if (waxedPath != null) {
                    Block targetBlock = getModBlock(waxedPath);
                    if (targetBlock != null && targetBlock != Blocks.AIR) {
                        if (!level.isClientSide()) {
                            setBlockStateOrDoor(level, pos, state, targetBlock);
                            level.levelEvent(3003, pos, 0);
                            if (player != null && !player.getAbilities().instabuild) {
                                held.shrink(1);
                            }
                        }
                        Services.PLATFORM.playWaxOn(level, pos, player);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        if (held.getItem() instanceof AxeItem) {

            if (blockId != null && blockId.getNamespace().equals("buildscape")) {
                String unwaxedPath = UNWAXED_MAP.get(blockId.getPath());
                if (unwaxedPath != null) {
                    Block targetBlock = getModBlock(unwaxedPath);
                    if (targetBlock != null && targetBlock != Blocks.AIR) {
                        if (!level.isClientSide()) {
                            setBlockStateOrDoor(level, pos, state, targetBlock);
                            level.levelEvent(3004, pos, 0);
                            if (player != null && !player.getAbilities().instabuild) {
                                Services.PLATFORM.hurtAndBreak(held, 1, player, hand);
                            }
                        }
                        Services.PLATFORM.playWaxOff(level, pos, player);
                        return InteractionResult.SUCCESS;
                    }
                }

                String prevPath = PREV_STAGE.get(blockId.getPath());
                if (prevPath != null) {
                    Block targetBlock = getModBlock(prevPath);
                    if (targetBlock != null && targetBlock != Blocks.AIR) {
                        if (!level.isClientSide()) {
                            setBlockStateOrDoor(level, pos, state, targetBlock);
                            level.levelEvent(3005, pos, 0);
                            if (player != null && !player.getAbilities().instabuild) {
                                Services.PLATFORM.hurtAndBreak(held, 1, player, hand);
                            }
                        }
                        Services.PLATFORM.playAxeScrape(level, pos, player);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        return InteractionResult.PASS;
    }
    public static void tryOxidize(Level level, BlockPos pos, BlockState state) {
        init();
        if (state.hasProperty(DoorBlock.HALF) && state.getValue(DoorBlock.HALF) != DoubleBlockHalf.LOWER) {
            return;
        }

        Block block = state.getBlock();
        CommonId blockId = Services.PLATFORM.getBlockId(block);
        if (blockId == null || !blockId.getNamespace().equals("buildscape")) {
            return;
        }

        String nextPath = NEXT_STAGE.get(blockId.getPath());
        if (nextPath != null) {
            if (level.getRandom().nextFloat() < 0.05688889F) {
                Block targetBlock = getModBlock(nextPath);
                if (targetBlock != null && targetBlock != Blocks.AIR) {
                    setBlockStateOrDoor(level, pos, state, targetBlock);
                }
            }
        }
    }

    private static void setBlockStateOrDoor(Level level, BlockPos pos, BlockState state, Block targetBlock) {
        if (state.hasProperty(DoorBlock.HALF)) {
            DoubleBlockHalf half = state.getValue(DoorBlock.HALF);
            BlockPos lowerPos = half == DoubleBlockHalf.LOWER ? pos : pos.below();
            BlockPos upperPos = half == DoubleBlockHalf.LOWER ? pos.above() : pos;

            BlockState lowerState = copyStateProperties(level.getBlockState(lowerPos), targetBlock.defaultBlockState()).setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER);
            BlockState upperState = copyStateProperties(level.getBlockState(upperPos), targetBlock.defaultBlockState()).setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);

            level.setBlock(lowerPos, lowerState, 2 | 16);
            level.setBlock(upperPos, upperState, 3);
        } else if (state.getBlock() instanceof ICopperChestBlock && targetBlock instanceof ICopperChestBlock) {
            setCopperChestState(level, pos, state, targetBlock);
        } else {
            BlockState nextState = copyStateProperties(state, targetBlock.defaultBlockState());
            level.setBlock(pos, nextState, 3);
        }
    }

    private static void setCopperChestState(Level level, BlockPos pos, BlockState state, Block targetBlock) {
        BlockState nextState = copyStateProperties(state, targetBlock.defaultBlockState());
        ChestType chestType = state.getValue(ChestBlock.TYPE);

        if (chestType == ChestType.SINGLE) {
            level.setBlock(pos, nextState, 3);
            return;
        }

        BlockPos partnerPos = pos.relative(ChestBlock.getConnectedDirection(state));
        BlockState partnerState = level.getBlockState(partnerPos);
        if (partnerState.getBlock() != state.getBlock()
                || partnerState.getValue(ChestBlock.TYPE) == ChestType.SINGLE) {
            level.setBlock(pos, nextState.setValue(ChestBlock.TYPE, ChestType.SINGLE), 3);
            return;
        }

        BlockState partnerNextState = copyStateProperties(partnerState, targetBlock.defaultBlockState());

        level.setBlock(pos, nextState, 2 | 16);
        level.setBlock(partnerPos, partnerNextState, 2 | 16);
        level.updateNeighborsAt(pos, targetBlock);
        level.updateNeighborsAt(partnerPos, targetBlock);
        level.updateNeighbourForOutputSignal(pos, targetBlock);
        level.updateNeighbourForOutputSignal(partnerPos, targetBlock);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static BlockState copyStateProperties(BlockState from, BlockState to) {
        for (Property prop : from.getProperties()) {
            if (to.hasProperty(prop)) {
                to = to.setValue(prop, from.getValue(prop));
            }
        }
        return to;
    }
    public static BlockState getNextOxidationState(BlockState state) {
        init();
        Block block = state.getBlock();
        CommonId blockId = Services.PLATFORM.getBlockId(block);

        Optional<BlockState> unwaxedOpt = getUnwaxedState(state);
        if (unwaxedOpt.isPresent()) {
            BlockState unwaxedState = unwaxedOpt.get();
            BlockState nextUnwaxedState = getNextOxidationState(unwaxedState);
            if (nextUnwaxedState != null) {
                Optional<BlockState> nextWaxedOpt = getWaxedState(nextUnwaxedState);
                if (nextWaxedOpt.isPresent()) {
                    return nextWaxedOpt.get();
                }
            }
            return null;
        }

        if (blockId != null && blockId.getNamespace().equals("buildscape")) {
            String path = blockId.getPath();
            String nextPath = NEXT_STAGE.get(path);

            if (nextPath == null) {
                if (path.startsWith("bit_weathered_")) {
                    nextPath = path.replace("bit_weathered_", "weathered_");
                } else if (path.startsWith("weathered_")) {
                    nextPath = path.replace("weathered_", "bit_oxidized_");
                } else if (path.startsWith("bit_exposed_")) {
                    nextPath = path.replace("bit_exposed_", "exposed_");
                } else if (path.startsWith("exposed_")) {
                    nextPath = path.replace("exposed_", "bit_weathered_");
                } else if (!path.startsWith("waxed_")) {
                    nextPath = "bit_exposed_" + path;
                }
            }

            if (nextPath != null) {
                Block targetBlock = getModBlock(nextPath);
                if (targetBlock != null && targetBlock != Blocks.AIR) {
                    return copyStateProperties(state, targetBlock.defaultBlockState());
                }
            }
        }

        Optional<Block> vanillaNext = WeatheringCopper.getNext(block);
        if (vanillaNext.isPresent()) {
            return copyStateProperties(state, vanillaNext.get().defaultBlockState());
        }

        return null;
    }

    private static Optional<BlockState> getUnwaxedState(BlockState state) {
        Block block = state.getBlock();
        CommonId blockId = Services.PLATFORM.getBlockId(block);
        if (blockId != null && blockId.getNamespace().equals("buildscape")) {
            String unwaxedPath = UNWAXED_MAP.get(blockId.getPath());
            if (unwaxedPath != null) {
                Block unwaxedBlock = getModBlock(unwaxedPath);
                if (unwaxedBlock != null && unwaxedBlock != Blocks.AIR) {
                    return Optional.of(copyStateProperties(state, unwaxedBlock.defaultBlockState()));
                }
            }
        }
        Block unwaxedVanilla = HoneycombItem.WAX_OFF_BY_BLOCK.get().get(block);
        if (unwaxedVanilla != null) {
            return Optional.of(copyStateProperties(state, unwaxedVanilla.defaultBlockState()));
        }
        return Optional.empty();
    }

    private static Optional<BlockState> getWaxedState(BlockState state) {
        Block block = state.getBlock();
        CommonId blockId = Services.PLATFORM.getBlockId(block);
        if (blockId != null && blockId.getNamespace().equals("buildscape")) {
            String waxedPath = WAXED_MAP.get(blockId.getPath());
            if (waxedPath != null) {
                Block waxedBlock = getModBlock(waxedPath);
                if (waxedBlock != null && waxedBlock != Blocks.AIR) {
                    return Optional.of(copyStateProperties(state, waxedBlock.defaultBlockState()));
                }
            }
        }
        return HoneycombItem.getWaxed(state);
    }
}
