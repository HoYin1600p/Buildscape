package com.kingodogo.buildscape.adapter.v118x;

import com.kingodogo.buildscape.block.CopperChestBlock;
import com.kingodogo.buildscape.block.EyeblossomBlock;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.world.InteractionHand;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;


import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.WeatheringCopper;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class RestoredCopperOxidation {

    private RestoredCopperOxidation() {}

    private static Supplier<Block> block(String path) {
        return () -> Services.PLATFORM.getBlock(new CommonId("buildscape", path));
    }

    // TODO S7a: route copper and eyeblossom interactions here for v118x.
    private static final Map<Supplier<Block>, Supplier<Block>> NEXT_STAGE = new HashMap<>();
    private static final Map<Supplier<Block>, Supplier<Block>> PREV_STAGE = new HashMap<>();
    private static final Map<Supplier<Block>, Supplier<Block>> WAXED_MAP = new HashMap<>();
    private static final Map<Supplier<Block>, Supplier<Block>> UNWAXED_MAP = new HashMap<>();

    public static synchronized void init() {
        if (!NEXT_STAGE.isEmpty()) return;

        registerChain(block("chiseled_copper"), block("exposed_chiseled_copper"), block("weathered_chiseled_copper"), block("oxidized_chiseled_copper"));
        registerWaxPair(block("chiseled_copper"), block("waxed_chiseled_copper"));
        registerWaxPair(block("exposed_chiseled_copper"), block("waxed_exposed_chiseled_copper"));
        registerWaxPair(block("weathered_chiseled_copper"), block("waxed_weathered_chiseled_copper"));
        registerWaxPair(block("oxidized_chiseled_copper"), block("waxed_oxidized_chiseled_copper"));

        registerChain(block("copper_grate"), block("exposed_copper_grate"), block("weathered_copper_grate"), block("oxidized_copper_grate"));
        registerWaxPair(block("copper_grate"), block("waxed_copper_grate"));
        registerWaxPair(block("exposed_copper_grate"), block("waxed_exposed_copper_grate"));
        registerWaxPair(block("weathered_copper_grate"), block("waxed_weathered_copper_grate"));
        registerWaxPair(block("oxidized_copper_grate"), block("waxed_oxidized_copper_grate"));

        registerChain(block("copper_bulb"), block("exposed_copper_bulb"), block("weathered_copper_bulb"), block("oxidized_copper_bulb"));
        registerWaxPair(block("copper_bulb"), block("waxed_copper_bulb"));
        registerWaxPair(block("exposed_copper_bulb"), block("waxed_exposed_copper_bulb"));
        registerWaxPair(block("weathered_copper_bulb"), block("waxed_weathered_copper_bulb"));
        registerWaxPair(block("oxidized_copper_bulb"), block("waxed_oxidized_copper_bulb"));

        registerChain(block("copper_rod"), block("exposed_copper_rod"), block("weathered_copper_rod"), block("oxidized_copper_rod"));
        registerWaxPair(block("copper_rod"), block("waxed_copper_rod"));
        registerWaxPair(block("exposed_copper_rod"), block("waxed_exposed_copper_rod"));
        registerWaxPair(block("weathered_copper_rod"), block("waxed_weathered_copper_rod"));
        registerWaxPair(block("oxidized_copper_rod"), block("waxed_oxidized_copper_rod"));

        registerChain(block("copper_lantern"), block("exposed_copper_lantern"), block("weathered_copper_lantern"), block("oxidized_copper_lantern"));
        registerWaxPair(block("copper_lantern"), block("waxed_copper_lantern"));
        registerWaxPair(block("exposed_copper_lantern"), block("waxed_exposed_copper_lantern"));
        registerWaxPair(block("weathered_copper_lantern"), block("waxed_weathered_copper_lantern"));
        registerWaxPair(block("oxidized_copper_lantern"), block("waxed_oxidized_copper_lantern"));

        registerChain(block("copper_door"), block("exposed_copper_door"), block("weathered_copper_door"), block("oxidized_copper_door"));
        registerWaxPair(block("copper_door"), block("waxed_copper_door"));
        registerWaxPair(block("exposed_copper_door"), block("waxed_exposed_copper_door"));
        registerWaxPair(block("weathered_copper_door"), block("waxed_weathered_copper_door"));
        registerWaxPair(block("oxidized_copper_door"), block("waxed_oxidized_copper_door"));

        registerChain(block("copper_trapdoor"), block("exposed_copper_trapdoor"), block("weathered_copper_trapdoor"), block("oxidized_copper_trapdoor"));
        registerWaxPair(block("copper_trapdoor"), block("waxed_copper_trapdoor"));
        registerWaxPair(block("exposed_copper_trapdoor"), block("waxed_exposed_copper_trapdoor"));
        registerWaxPair(block("weathered_copper_trapdoor"), block("waxed_weathered_copper_trapdoor"));
        registerWaxPair(block("oxidized_copper_trapdoor"), block("waxed_oxidized_copper_trapdoor"));

        registerChain(block("copper_bars"), block("exposed_copper_bars"), block("weathered_copper_bars"), block("oxidized_copper_bars"));
        registerWaxPair(block("copper_bars"), block("waxed_copper_bars"));
        registerWaxPair(block("exposed_copper_bars"), block("waxed_exposed_copper_bars"));
        registerWaxPair(block("weathered_copper_bars"), block("waxed_weathered_copper_bars"));
        registerWaxPair(block("oxidized_copper_bars"), block("waxed_oxidized_copper_bars"));

        registerChain(block("copper_mesh"), block("exposed_copper_mesh"), block("weathered_copper_mesh"), block("oxidized_copper_mesh"));
        registerWaxPair(block("copper_mesh"), block("waxed_copper_mesh"));
        registerWaxPair(block("exposed_copper_mesh"), block("waxed_exposed_copper_mesh"));
        registerWaxPair(block("weathered_copper_mesh"), block("waxed_weathered_copper_mesh"));
        registerWaxPair(block("oxidized_copper_mesh"), block("waxed_oxidized_copper_mesh"));

        registerChain(block("copper_bolts"), block("exposed_copper_bolts"), block("weathered_copper_bolts"), block("oxidized_copper_bolts"));
        registerWaxPair(block("copper_bolts"), block("waxed_copper_bolts"));
        registerWaxPair(block("exposed_copper_bolts"), block("waxed_exposed_copper_bolts"));
        registerWaxPair(block("weathered_copper_bolts"), block("waxed_weathered_copper_bolts"));
        registerWaxPair(block("oxidized_copper_bolts"), block("waxed_oxidized_copper_bolts"));

        registerChain(block("cut_copper_vertical_slab"), block("exposed_cut_copper_vertical_slab"), block("weathered_cut_copper_vertical_slab"), block("oxidized_cut_copper_vertical_slab"));
        registerWaxPair(block("cut_copper_vertical_slab"), block("waxed_cut_copper_vertical_slab"));
        registerWaxPair(block("exposed_cut_copper_vertical_slab"), block("waxed_exposed_cut_copper_vertical_slab"));
        registerWaxPair(block("weathered_cut_copper_vertical_slab"), block("waxed_weathered_cut_copper_vertical_slab"));
        registerWaxPair(block("oxidized_cut_copper_vertical_slab"), block("waxed_oxidized_cut_copper_vertical_slab"));

        registerChain(block("copper_button"), block("exposed_copper_button"), block("weathered_copper_button"), block("oxidized_copper_button"));
        registerWaxPair(block("copper_button"), block("waxed_copper_button"));
        registerWaxPair(block("exposed_copper_button"), block("waxed_exposed_copper_button"));
        registerWaxPair(block("weathered_copper_button"), block("waxed_weathered_copper_button"));
        registerWaxPair(block("oxidized_copper_button"), block("waxed_oxidized_copper_button"));

        registerChain(block("copper_pressure_plate"), block("exposed_copper_pressure_plate"), block("weathered_copper_pressure_plate"), block("oxidized_copper_pressure_plate"));
        registerWaxPair(block("copper_pressure_plate"), block("waxed_copper_pressure_plate"));
        registerWaxPair(block("exposed_copper_pressure_plate"), block("waxed_exposed_copper_pressure_plate"));
        registerWaxPair(block("weathered_copper_pressure_plate"), block("waxed_weathered_copper_pressure_plate"));
        registerWaxPair(block("oxidized_copper_pressure_plate"), block("waxed_oxidized_copper_pressure_plate"));

        registerChain(block("copper_chest"), block("exposed_copper_chest"), block("weathered_copper_chest"), block("oxidized_copper_chest"));
        registerWaxPair(block("copper_chest"), block("waxed_copper_chest"));
        registerWaxPair(block("exposed_copper_chest"), block("waxed_exposed_copper_chest"));
        registerWaxPair(block("weathered_copper_chest"), block("waxed_weathered_copper_chest"));
        registerWaxPair(block("oxidized_copper_chest"), block("waxed_oxidized_copper_chest"));

        registerChain(block("copper_chain"), block("exposed_copper_chain"), block("weathered_copper_chain"), block("oxidized_copper_chain"));

        registerChain(block("large_copper_chain"), block("large_exposed_copper_chain"), block("large_weathered_copper_chain"), block("large_oxidized_copper_chain"));

        registerChain(block("slit_copper"), block("exposed_slit_copper"), block("weathered_slit_copper"), block("oxidized_slit_copper"));
        registerWaxPair(block("slit_copper"), block("waxed_slit_copper"));
        registerWaxPair(block("exposed_slit_copper"), block("waxed_exposed_slit_copper"));
        registerWaxPair(block("weathered_slit_copper"), block("waxed_weathered_slit_copper"));
        registerWaxPair(block("oxidized_slit_copper"), block("waxed_oxidized_slit_copper"));

        registerChain(block("slit_copper_stairs"), block("exposed_slit_copper_stairs"), block("weathered_slit_copper_stairs"), block("oxidized_slit_copper_stairs"));
        registerWaxPair(block("slit_copper_stairs"), block("waxed_slit_copper_stairs"));
        registerWaxPair(block("exposed_slit_copper_stairs"), block("waxed_exposed_slit_copper_stairs"));
        registerWaxPair(block("weathered_slit_copper_stairs"), block("waxed_weathered_slit_copper_stairs"));
        registerWaxPair(block("oxidized_slit_copper_stairs"), block("waxed_oxidized_slit_copper_stairs"));

        registerChain(block("slit_copper_slab"), block("exposed_slit_copper_slab"), block("weathered_slit_copper_slab"), block("oxidized_slit_copper_slab"));
        registerWaxPair(block("slit_copper_slab"), block("waxed_slit_copper_slab"));
        registerWaxPair(block("exposed_slit_copper_slab"), block("waxed_exposed_slit_copper_slab"));
        registerWaxPair(block("weathered_slit_copper_slab"), block("waxed_weathered_slit_copper_slab"));
        registerWaxPair(block("oxidized_slit_copper_slab"), block("waxed_oxidized_slit_copper_slab"));

        registerChain(block("slit_copper_vertical_slab"), block("exposed_slit_copper_vertical_slab"), block("weathered_slit_copper_vertical_slab"), block("oxidized_slit_copper_vertical_slab"));
        registerWaxPair(block("slit_copper_vertical_slab"), block("waxed_slit_copper_vertical_slab"));
        registerWaxPair(block("exposed_slit_copper_vertical_slab"), block("waxed_exposed_slit_copper_vertical_slab"));
        registerWaxPair(block("weathered_slit_copper_vertical_slab"), block("waxed_weathered_slit_copper_vertical_slab"));
        registerWaxPair(block("oxidized_slit_copper_vertical_slab"), block("waxed_oxidized_slit_copper_vertical_slab"));
    }

    private static void registerChain(Supplier<Block> b0, Supplier<Block> b1, Supplier<Block> b2, Supplier<Block> b3) {
        registerChain(NEXT_STAGE, PREV_STAGE, b0, b1, b2, b3);
    }

    static <T> void registerChain(Map<Supplier<T>, Supplier<T>> next, Map<Supplier<T>, Supplier<T>> previous,
                                  Supplier<T> b0, Supplier<T> b1, Supplier<T> b2, Supplier<T> b3) {
        next.put(b0, b1);
        next.put(b1, b2);
        next.put(b2, b3);

        previous.put(b3, b2);
        previous.put(b2, b1);
        previous.put(b1, b0);
    }

    private static void registerWaxPair(Supplier<Block> unwaxed, Supplier<Block> waxed) {
        WAXED_MAP.put(unwaxed, waxed);
        UNWAXED_MAP.put(waxed, unwaxed);
    }

    public static InteractionResult handleRightClick(Level level, BlockPos pos, BlockState state, ItemStack held, Player player, InteractionHand hand) {
        init();



        Block block = state.getBlock();



        if (held.is(Services.PLATFORM.getItem(new CommonId("buildscape", "bottle_of_mist")))) {
            BlockState nextState = getNextOxidationState(state);
            if (nextState != null) {
                if (!level.isClientSide) {
                    setBlockStateOrDoor(level, pos, state, nextState.getBlock());

                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(
                            net.minecraft.core.particles.ParticleTypes.SMOKE,
                            (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D,
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

                level.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.6f, 0.8f);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        if (held.is(Items.HONEYCOMB)) {
            if (block instanceof EyeblossomBlock) {
                if (!state.getValue(EyeblossomBlock.WAXED)) {
                    if (!level.isClientSide) {
                        level.setBlock(pos, state.setValue(EyeblossomBlock.WAXED, true), 3);
                        level.levelEvent(3003, pos, 0);
                        if (player != null && !player.getAbilities().instabuild) {
                            held.shrink(1);
                        }
                    }
                    level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0f, 1.0f);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
            for (Map.Entry<Supplier<Block>, Supplier<Block>> entry : WAXED_MAP.entrySet()) {
                if (entry.getKey().get() == block) {
                    Block targetBlock = entry.getValue().get();
                    if (!level.isClientSide) {
                        setBlockStateOrDoor(level, pos, state, targetBlock);
                        level.levelEvent(3003, pos, 0);
                        if (player != null && !player.getAbilities().instabuild) {
                            held.shrink(1);
                        }
                    }
                    level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0f, 1.0f);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }

        if (held.getItem() instanceof AxeItem) {
            if (block instanceof EyeblossomBlock) {
                if (state.getValue(EyeblossomBlock.WAXED)) {
                    if (!level.isClientSide) {
                        level.setBlock(pos, state.setValue(EyeblossomBlock.WAXED, false), 3);
                        level.levelEvent(3004, pos, 0);
                        if (player != null && !player.getAbilities().instabuild) {
                            held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                        }
                    }
                    level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0f, 1.0f);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
            for (Map.Entry<Supplier<Block>, Supplier<Block>> entry : UNWAXED_MAP.entrySet()) {
                if (entry.getKey().get() == block) {
                    Block targetBlock = entry.getValue().get();
                    if (!level.isClientSide) {
                        setBlockStateOrDoor(level, pos, state, targetBlock);
                        level.levelEvent(3004, pos, 0);
                        if (player != null && !player.getAbilities().instabuild) {
                            held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                        }
                    }
                    level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0f, 1.0f);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }

            for (Map.Entry<Supplier<Block>, Supplier<Block>> entry : PREV_STAGE.entrySet()) {
                if (entry.getKey().get() == block) {
                    Block targetBlock = entry.getValue().get();
                    if (!level.isClientSide) {
                        setBlockStateOrDoor(level, pos, state, targetBlock);
                        level.levelEvent(3005, pos, 0);
                        if (player != null && !player.getAbilities().instabuild) {
                            held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                        }
                    }
                    level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0f, 1.0f);
                    return InteractionResult.sidedSuccess(level.isClientSide);
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
        for (Map.Entry<Supplier<Block>, Supplier<Block>> entry : NEXT_STAGE.entrySet()) {
            if (entry.getKey().get() == block) {
                if (level.random.nextFloat() < 0.05688889F) {
                    Block targetBlock = entry.getValue().get();
                    setBlockStateOrDoor(level, pos, state, targetBlock);
                }
                break;
            }
        }
    }

    public static void setBlockStateOrDoor(Level level, BlockPos pos, BlockState state, Block targetBlock) {
        if (state.hasProperty(DoorBlock.HALF)) {
            DoubleBlockHalf half = state.getValue(DoorBlock.HALF);
            BlockPos lowerPos = half == DoubleBlockHalf.LOWER ? pos : pos.below();
            BlockPos upperPos = half == DoubleBlockHalf.LOWER ? pos.above() : pos;

            BlockState lowerState = copyStateProperties(level.getBlockState(lowerPos), targetBlock.defaultBlockState()).setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER);
            BlockState upperState = copyStateProperties(level.getBlockState(upperPos), targetBlock.defaultBlockState()).setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);

            level.setBlock(lowerPos, lowerState, 2 | 16);
            level.setBlock(upperPos, upperState, 3);
        } else if (state.getBlock() instanceof CopperChestBlock && targetBlock instanceof CopperChestBlock) {
            setCopperChestState(level, pos, state, targetBlock);
        } else {
            BlockState nextState = copyStateProperties(state, targetBlock.defaultBlockState());
            // Scheduled ticks belong to a block type, so the old block's tick cannot reset its replacement.
            // Schedule before placement: LightningRodBlock.onPlace otherwise clears POWERED without
            // notifying neighbours, leaving the circuit powered while the rod itself is unpowered.
            if (!level.isClientSide && state.getBlock() != targetBlock) {
                if (targetBlock instanceof LightningRodBlock && nextState.getValue(LightningRodBlock.POWERED)) {
                    level.scheduleTick(pos, targetBlock, 8);
                } else if (targetBlock instanceof ButtonBlock && nextState.getValue(ButtonBlock.POWERED)) {
                    level.scheduleTick(pos, targetBlock, 20);
                } else if (targetBlock instanceof WeightedPressurePlateBlock
                        && nextState.getValue(WeightedPressurePlateBlock.POWER) > 0) {
                    level.scheduleTick(pos, targetBlock, 10);
                }
            }
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

    public static Block getPrevStage(Block block) {
        init();
        return getMappedStage(block, PREV_STAGE);
    }

    static <T> T getMappedStage(T stage, Map<Supplier<T>, Supplier<T>> stages) {
        if (stage == net.minecraft.world.level.block.Blocks.AIR) return null;
        for (Map.Entry<Supplier<T>, Supplier<T>> entry : stages.entrySet()) {
            if (entry.getKey().get() == stage) {
                return entry.getValue().get();
            }
        }
        return null;
    }

    public static Block getFirstStage(Block block) {
        init();
        return getFirstStage(block, NEXT_STAGE, PREV_STAGE);
    }

    public static boolean isWeatheringCopper(Block block) {
        init();
        return block instanceof WeatheringCopper || isWeatheringStage(block, NEXT_STAGE, PREV_STAGE);
    }

    static <T> boolean isWeatheringStage(T stage, Map<Supplier<T>, Supplier<T>> next,
                                         Map<Supplier<T>, Supplier<T>> previous) {
        return getMappedStage(stage, next) != null || getMappedStage(stage, previous) != null;
    }

    static <T> T getFirstStage(T stage, Map<Supplier<T>, Supplier<T>> next,
                               Map<Supplier<T>, Supplier<T>> previous) {
        if (!isWeatheringStage(stage, next, previous)) return null;
        T current = stage;
        while (true) {
            T prev = getMappedStage(current, previous);
            if (prev == null) break;
            current = prev;
        }
        return current;
    }

    public static BlockState getNextOxidationState(BlockState state) {
        init();
        Block block = state.getBlock();

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

        for (Map.Entry<Supplier<Block>, Supplier<Block>> entry : NEXT_STAGE.entrySet()) {
            if (entry.getKey().get() == block) {
                Block targetBlock = entry.getValue().get();
                return copyStateProperties(state, targetBlock.defaultBlockState());
            }
        }

        CommonId rl = Services.PLATFORM.getBlockId(block);
        if (rl != null && rl.getNamespace().equals("buildscape")) {
            String path = rl.getPath();
            String nextPath = null;

            if (path.startsWith("bit_oxidized_")) {
                nextPath = null;
            } else if (path.startsWith("bit_weathered_")) {
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

            if (nextPath != null) {
                Block targetBlock = Services.PLATFORM.getBlock(new CommonId("buildscape", nextPath));
                if (targetBlock != null && targetBlock != net.minecraft.world.level.block.Blocks.AIR) {
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
        for (Map.Entry<Supplier<Block>, Supplier<Block>> entry : UNWAXED_MAP.entrySet()) {
            if (entry.getKey().get() == block) {
                Block unwaxedBlock = entry.getValue().get();
                return Optional.of(copyStateProperties(state, unwaxedBlock.defaultBlockState()));
            }
        }
        Block unwaxedVanilla = net.minecraft.world.item.HoneycombItem.WAX_OFF_BY_BLOCK.get().get(block);
        if (unwaxedVanilla != null) {
            return Optional.of(copyStateProperties(state, unwaxedVanilla.defaultBlockState()));
        }
        return Optional.empty();
    }

    private static Optional<BlockState> getWaxedState(BlockState state) {
        Block block = state.getBlock();
        for (Map.Entry<Supplier<Block>, Supplier<Block>> entry : WAXED_MAP.entrySet()) {
            if (entry.getKey().get() == block) {
                Block waxedBlock = entry.getValue().get();
                return Optional.of(copyStateProperties(state, waxedBlock.defaultBlockState()));
            }
        }
        return net.minecraft.world.item.HoneycombItem.getWaxed(state);
    }
}
