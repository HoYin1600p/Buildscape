package com.kingodogo.buildscape.block;

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
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.WeatheringCopper;
import com.kingodogo.buildscape.item.ModItems;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class CopperOxidationHandler {

    private static final Map<Supplier<Block>, Supplier<Block>> NEXT_STAGE = new HashMap<>();
    private static final Map<Supplier<Block>, Supplier<Block>> PREV_STAGE = new HashMap<>();
    private static final Map<Supplier<Block>, Supplier<Block>> WAXED_MAP = new HashMap<>();
    private static final Map<Supplier<Block>, Supplier<Block>> UNWAXED_MAP = new HashMap<>();

    public static void init() {
        if (!NEXT_STAGE.isEmpty()) return;

        // 1. Chiseled Copper
        registerChain(ModBlocks.CHISELED_COPPER, ModBlocks.EXPOSED_CHISELED_COPPER, ModBlocks.WEATHERED_CHISELED_COPPER, ModBlocks.OXIDIZED_CHISELED_COPPER);
        registerWaxPair(ModBlocks.CHISELED_COPPER, ModBlocks.WAXED_CHISELED_COPPER);
        registerWaxPair(ModBlocks.EXPOSED_CHISELED_COPPER, ModBlocks.WAXED_EXPOSED_CHISELED_COPPER);
        registerWaxPair(ModBlocks.WEATHERED_CHISELED_COPPER, ModBlocks.WAXED_WEATHERED_CHISELED_COPPER);
        registerWaxPair(ModBlocks.OXIDIZED_CHISELED_COPPER, ModBlocks.WAXED_OXIDIZED_CHISELED_COPPER);

        // 2. Copper Grate
        registerChain(ModBlocks.COPPER_GRATE, ModBlocks.EXPOSED_COPPER_GRATE, ModBlocks.WEATHERED_COPPER_GRATE, ModBlocks.OXIDIZED_COPPER_GRATE);
        registerWaxPair(ModBlocks.COPPER_GRATE, ModBlocks.WAXED_COPPER_GRATE);
        registerWaxPair(ModBlocks.EXPOSED_COPPER_GRATE, ModBlocks.WAXED_EXPOSED_COPPER_GRATE);
        registerWaxPair(ModBlocks.WEATHERED_COPPER_GRATE, ModBlocks.WAXED_WEATHERED_COPPER_GRATE);
        registerWaxPair(ModBlocks.OXIDIZED_COPPER_GRATE, ModBlocks.WAXED_OXIDIZED_COPPER_GRATE);

        // 3. Copper Bulb
        registerChain(ModBlocks.COPPER_BULB, ModBlocks.EXPOSED_COPPER_BULB, ModBlocks.WEATHERED_COPPER_BULB, ModBlocks.OXIDIZED_COPPER_BULB);
        registerWaxPair(ModBlocks.COPPER_BULB, ModBlocks.WAXED_COPPER_BULB);
        registerWaxPair(ModBlocks.EXPOSED_COPPER_BULB, ModBlocks.WAXED_EXPOSED_COPPER_BULB);
        registerWaxPair(ModBlocks.WEATHERED_COPPER_BULB, ModBlocks.WAXED_WEATHERED_COPPER_BULB);
        registerWaxPair(ModBlocks.OXIDIZED_COPPER_BULB, ModBlocks.WAXED_OXIDIZED_COPPER_BULB);

        // 4. Copper Rod
        registerChain(ModBlocks.COPPER_ROD, ModBlocks.EXPOSED_COPPER_ROD, ModBlocks.WEATHERED_COPPER_ROD, ModBlocks.OXIDIZED_COPPER_ROD);
        registerWaxPair(ModBlocks.COPPER_ROD, ModBlocks.WAXED_COPPER_ROD);
        registerWaxPair(ModBlocks.EXPOSED_COPPER_ROD, ModBlocks.WAXED_EXPOSED_COPPER_ROD);
        registerWaxPair(ModBlocks.WEATHERED_COPPER_ROD, ModBlocks.WAXED_WEATHERED_COPPER_ROD);
        registerWaxPair(ModBlocks.OXIDIZED_COPPER_ROD, ModBlocks.WAXED_OXIDIZED_COPPER_ROD);

        // 5. Copper Lantern
        registerChain(ModBlocks.COPPER_LANTERN, ModBlocks.EXPOSED_COPPER_LANTERN, ModBlocks.WEATHERED_COPPER_LANTERN, ModBlocks.OXIDIZED_COPPER_LANTERN);
        registerWaxPair(ModBlocks.COPPER_LANTERN, ModBlocks.WAXED_COPPER_LANTERN);
        registerWaxPair(ModBlocks.EXPOSED_COPPER_LANTERN, ModBlocks.WAXED_EXPOSED_COPPER_LANTERN);
        registerWaxPair(ModBlocks.WEATHERED_COPPER_LANTERN, ModBlocks.WAXED_WEATHERED_COPPER_LANTERN);
        registerWaxPair(ModBlocks.OXIDIZED_COPPER_LANTERN, ModBlocks.WAXED_OXIDIZED_COPPER_LANTERN);

        // 6. Copper Door
        registerChain(ModBlocks.COPPER_DOOR, ModBlocks.EXPOSED_COPPER_DOOR, ModBlocks.WEATHERED_COPPER_DOOR, ModBlocks.OXIDIZED_COPPER_DOOR);
        registerWaxPair(ModBlocks.COPPER_DOOR, ModBlocks.WAXED_COPPER_DOOR);
        registerWaxPair(ModBlocks.EXPOSED_COPPER_DOOR, ModBlocks.WAXED_EXPOSED_COPPER_DOOR);
        registerWaxPair(ModBlocks.WEATHERED_COPPER_DOOR, ModBlocks.WAXED_WEATHERED_COPPER_DOOR);
        registerWaxPair(ModBlocks.OXIDIZED_COPPER_DOOR, ModBlocks.WAXED_OXIDIZED_COPPER_DOOR);

        // 7. Copper Trapdoor
        registerChain(ModBlocks.COPPER_TRAPDOOR, ModBlocks.EXPOSED_COPPER_TRAPDOOR, ModBlocks.WEATHERED_COPPER_TRAPDOOR, ModBlocks.OXIDIZED_COPPER_TRAPDOOR);
        registerWaxPair(ModBlocks.COPPER_TRAPDOOR, ModBlocks.WAXED_COPPER_TRAPDOOR);
        registerWaxPair(ModBlocks.EXPOSED_COPPER_TRAPDOOR, ModBlocks.WAXED_EXPOSED_COPPER_TRAPDOOR);
        registerWaxPair(ModBlocks.WEATHERED_COPPER_TRAPDOOR, ModBlocks.WAXED_WEATHERED_COPPER_TRAPDOOR);
        registerWaxPair(ModBlocks.OXIDIZED_COPPER_TRAPDOOR, ModBlocks.WAXED_OXIDIZED_COPPER_TRAPDOOR);

        // 8. Copper Bars
        registerChain(ModBlocks.COPPER_BARS, ModBlocks.EXPOSED_COPPER_BARS, ModBlocks.WEATHERED_COPPER_BARS, ModBlocks.OXIDIZED_COPPER_BARS);
        registerWaxPair(ModBlocks.COPPER_BARS, ModBlocks.WAXED_COPPER_BARS);
        registerWaxPair(ModBlocks.EXPOSED_COPPER_BARS, ModBlocks.WAXED_EXPOSED_COPPER_BARS);
        registerWaxPair(ModBlocks.WEATHERED_COPPER_BARS, ModBlocks.WAXED_WEATHERED_COPPER_BARS);
        registerWaxPair(ModBlocks.OXIDIZED_COPPER_BARS, ModBlocks.WAXED_OXIDIZED_COPPER_BARS);

        // 9. Cut Copper Vertical Slab
        registerChain(ModBlocks.CUT_COPPER_VERTICAL_SLAB, ModBlocks.EXPOSED_CUT_COPPER_VERTICAL_SLAB, ModBlocks.WEATHERED_CUT_COPPER_VERTICAL_SLAB, ModBlocks.OXIDIZED_CUT_COPPER_VERTICAL_SLAB);
        registerWaxPair(ModBlocks.CUT_COPPER_VERTICAL_SLAB, ModBlocks.WAXED_CUT_COPPER_VERTICAL_SLAB);
        registerWaxPair(ModBlocks.EXPOSED_CUT_COPPER_VERTICAL_SLAB, ModBlocks.WAXED_EXPOSED_CUT_COPPER_VERTICAL_SLAB);
        registerWaxPair(ModBlocks.WEATHERED_CUT_COPPER_VERTICAL_SLAB, ModBlocks.WAXED_WEATHERED_CUT_COPPER_VERTICAL_SLAB);
        registerWaxPair(ModBlocks.OXIDIZED_CUT_COPPER_VERTICAL_SLAB, ModBlocks.WAXED_OXIDIZED_CUT_COPPER_VERTICAL_SLAB);

        // 10. Copper Button
        registerChain(ModBlocks.COPPER_BUTTON, ModBlocks.EXPOSED_COPPER_BUTTON, ModBlocks.WEATHERED_COPPER_BUTTON, ModBlocks.OXIDIZED_COPPER_BUTTON);
        registerWaxPair(ModBlocks.COPPER_BUTTON, ModBlocks.WAXED_COPPER_BUTTON);
        registerWaxPair(ModBlocks.EXPOSED_COPPER_BUTTON, ModBlocks.WAXED_EXPOSED_COPPER_BUTTON);
        registerWaxPair(ModBlocks.WEATHERED_COPPER_BUTTON, ModBlocks.WAXED_WEATHERED_COPPER_BUTTON);
        registerWaxPair(ModBlocks.OXIDIZED_COPPER_BUTTON, ModBlocks.WAXED_OXIDIZED_COPPER_BUTTON);

        // 11. Copper Pressure Plate
        registerChain(ModBlocks.COPPER_PRESSURE_PLATE, ModBlocks.EXPOSED_COPPER_PRESSURE_PLATE, ModBlocks.WEATHERED_COPPER_PRESSURE_PLATE, ModBlocks.OXIDIZED_COPPER_PRESSURE_PLATE);
        registerWaxPair(ModBlocks.COPPER_PRESSURE_PLATE, ModBlocks.WAXED_COPPER_PRESSURE_PLATE);
        registerWaxPair(ModBlocks.EXPOSED_COPPER_PRESSURE_PLATE, ModBlocks.WAXED_EXPOSED_COPPER_PRESSURE_PLATE);
        registerWaxPair(ModBlocks.WEATHERED_COPPER_PRESSURE_PLATE, ModBlocks.WAXED_WEATHERED_COPPER_PRESSURE_PLATE);
        registerWaxPair(ModBlocks.OXIDIZED_COPPER_PRESSURE_PLATE, ModBlocks.WAXED_OXIDIZED_COPPER_PRESSURE_PLATE);
    }

    private static void registerChain(Supplier<Block> b0, Supplier<Block> b1, Supplier<Block> b2, Supplier<Block> b3) {
        NEXT_STAGE.put(b0, b1);
        NEXT_STAGE.put(b1, b2);
        NEXT_STAGE.put(b2, b3);

        PREV_STAGE.put(b3, b2);
        PREV_STAGE.put(b2, b1);
        PREV_STAGE.put(b1, b0);
    }

    private static void registerWaxPair(Supplier<Block> unwaxed, Supplier<Block> waxed) {
        WAXED_MAP.put(unwaxed, waxed);
        UNWAXED_MAP.put(waxed, unwaxed);
    }

    public static boolean handleRightClick(PlayerInteractEvent.RightClickBlock event) {
        init();
        Level level = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        ItemStack held = event.getItemStack();
        Player player = event.getPlayer();

        // 0. BOTTLE OF MIST OXIDATION SPEEDUP
        if (held.is(ModItems.BOTTLE_OF_MIST.get())) {
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
                            player.setItemInHand(event.getHand(), emptyBottle);
                        } else if (!player.getInventory().add(emptyBottle)) {
                            player.drop(emptyBottle, false);
                        }
                    }
                }
                
                level.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.6f, 0.8f);
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                return true;
            }
        }

        // 1. HONEYCOMB WAXING
        if (held.is(Items.HONEYCOMB)) {
            if (block instanceof EyeblossomBlock) {
                if (!state.getValue(EyeblossomBlock.WAXED)) {
                    if (!level.isClientSide) {
                        level.setBlock(pos, state.setValue(EyeblossomBlock.WAXED, true), 3);
                        level.levelEvent(3003, pos, 0); // Wax on particles
                        if (player != null && !player.getAbilities().instabuild) {
                            held.shrink(1);
                        }
                    }
                    level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0f, 1.0f);
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                    return true;
                }
            }
            for (Map.Entry<Supplier<Block>, Supplier<Block>> entry : WAXED_MAP.entrySet()) {
                if (entry.getKey().get() == block) {
                    Block targetBlock = entry.getValue().get();
                    if (!level.isClientSide) {
                        setBlockStateOrDoor(level, pos, state, targetBlock);
                        level.levelEvent(3003, pos, 0); // Wax on particles
                        if (player != null && !player.getAbilities().instabuild) {
                            held.shrink(1);
                        }
                    }
                    level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0f, 1.0f);
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                    return true;
                }
            }
        }

        // 2. AXE INTERACTION (UNWAXING & DE-OXIDIZING / SCRAPING)
        if (held.getItem() instanceof AxeItem) {
            // Check Eyeblossom unwaxing
            if (block instanceof EyeblossomBlock) {
                if (state.getValue(EyeblossomBlock.WAXED)) {
                    if (!level.isClientSide) {
                        level.setBlock(pos, state.setValue(EyeblossomBlock.WAXED, false), 3);
                        level.levelEvent(3004, pos, 0); // Wax off particles
                        if (player != null && !player.getAbilities().instabuild) {
                            held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));
                        }
                    }
                    level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0f, 1.0f);
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                    return true;
                }
            }
            // A) Check Unwaxing
            for (Map.Entry<Supplier<Block>, Supplier<Block>> entry : UNWAXED_MAP.entrySet()) {
                if (entry.getKey().get() == block) {
                    Block targetBlock = entry.getValue().get();
                    if (!level.isClientSide) {
                        setBlockStateOrDoor(level, pos, state, targetBlock);
                        level.levelEvent(3004, pos, 0); // Wax off particles
                        if (player != null && !player.getAbilities().instabuild) {
                            held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));
                        }
                    }
                    level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0f, 1.0f);
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                    return true;
                }
            }

            // B) Check De-oxidizing / Scraping
            for (Map.Entry<Supplier<Block>, Supplier<Block>> entry : PREV_STAGE.entrySet()) {
                if (entry.getKey().get() == block) {
                    Block targetBlock = entry.getValue().get();
                    if (!level.isClientSide) {
                        setBlockStateOrDoor(level, pos, state, targetBlock);
                        level.levelEvent(3005, pos, 0); // Scrape particles
                        if (player != null && !player.getAbilities().instabuild) {
                            held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));
                        }
                    }
                    level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0f, 1.0f);
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                    return true;
                }
            }
        }

        return false;
    }

    public static void tryOxidize(Level level, BlockPos pos, BlockState state) {
        init();
        if (state.hasProperty(DoorBlock.HALF) && state.getValue(DoorBlock.HALF) != DoubleBlockHalf.LOWER) {
            return;
        }
        Block block = state.getBlock();
        for (Map.Entry<Supplier<Block>, Supplier<Block>> entry : NEXT_STAGE.entrySet()) {
            if (entry.getKey().get() == block) {
                // 1 in 16 chance on random tick to progress oxidation
                if (level.random.nextFloat() < 0.05688889F) {
                    Block targetBlock = entry.getValue().get();
                    setBlockStateOrDoor(level, pos, state, targetBlock);
                }
                break;
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
        } else {
            BlockState nextState = copyStateProperties(state, targetBlock.defaultBlockState());
            level.setBlock(pos, nextState, 3);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BlockState copyStateProperties(BlockState from, BlockState to) {
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
        
        for (Map.Entry<Supplier<Block>, Supplier<Block>> entry : NEXT_STAGE.entrySet()) {
            if (entry.getKey().get() == block) {
                Block targetBlock = entry.getValue().get();
                return copyStateProperties(state, targetBlock.defaultBlockState());
            }
        }
        
        Optional<Block> vanillaNext = WeatheringCopper.getNext(block);
        if (vanillaNext.isPresent()) {
            return copyStateProperties(state, vanillaNext.get().defaultBlockState());
        }
        
        return null;
    }
}
