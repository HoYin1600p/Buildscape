package com.kingodogo.buildscape.feature.copper;

import com.kingodogo.buildscape.block.plant.EyeblossomBlock;
import com.kingodogo.buildscape.registry.ModBlocks;
import com.kingodogo.buildscape.registry.ModItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class CopperOxidationHandler {

    private static final CopperTransitions<Block> STAGES = new CopperTransitions<>();
    private static final Map<Block, WeatheringCopper.WeatherState> AGES = new IdentityHashMap<>();
    private static volatile boolean initialized;

    public static void init() {
        if (initialized) {
            return;
        }
        synchronized (STAGES) {
            if (initialized) {
                return;
            }
            registerChain(ModBlocks.CHISELED_COPPER, ModBlocks.EXPOSED_CHISELED_COPPER,
                    ModBlocks.WEATHERED_CHISELED_COPPER, ModBlocks.OXIDIZED_CHISELED_COPPER);
            registerWaxPair(ModBlocks.CHISELED_COPPER, ModBlocks.WAXED_CHISELED_COPPER);
            registerWaxPair(ModBlocks.EXPOSED_CHISELED_COPPER, ModBlocks.WAXED_EXPOSED_CHISELED_COPPER);
            registerWaxPair(ModBlocks.WEATHERED_CHISELED_COPPER, ModBlocks.WAXED_WEATHERED_CHISELED_COPPER);
            registerWaxPair(ModBlocks.OXIDIZED_CHISELED_COPPER, ModBlocks.WAXED_OXIDIZED_CHISELED_COPPER);

            registerChain(ModBlocks.COPPER_GRATE, ModBlocks.EXPOSED_COPPER_GRATE, ModBlocks.WEATHERED_COPPER_GRATE,
                    ModBlocks.OXIDIZED_COPPER_GRATE);
            registerWaxPair(ModBlocks.COPPER_GRATE, ModBlocks.WAXED_COPPER_GRATE);
            registerWaxPair(ModBlocks.EXPOSED_COPPER_GRATE, ModBlocks.WAXED_EXPOSED_COPPER_GRATE);
            registerWaxPair(ModBlocks.WEATHERED_COPPER_GRATE, ModBlocks.WAXED_WEATHERED_COPPER_GRATE);
            registerWaxPair(ModBlocks.OXIDIZED_COPPER_GRATE, ModBlocks.WAXED_OXIDIZED_COPPER_GRATE);

            registerChain(ModBlocks.COPPER_BULB, ModBlocks.EXPOSED_COPPER_BULB, ModBlocks.WEATHERED_COPPER_BULB,
                    ModBlocks.OXIDIZED_COPPER_BULB);
            registerWaxPair(ModBlocks.COPPER_BULB, ModBlocks.WAXED_COPPER_BULB);
            registerWaxPair(ModBlocks.EXPOSED_COPPER_BULB, ModBlocks.WAXED_EXPOSED_COPPER_BULB);
            registerWaxPair(ModBlocks.WEATHERED_COPPER_BULB, ModBlocks.WAXED_WEATHERED_COPPER_BULB);
            registerWaxPair(ModBlocks.OXIDIZED_COPPER_BULB, ModBlocks.WAXED_OXIDIZED_COPPER_BULB);

            registerChain(ModBlocks.COPPER_ROD, ModBlocks.EXPOSED_COPPER_ROD, ModBlocks.WEATHERED_COPPER_ROD,
                    ModBlocks.OXIDIZED_COPPER_ROD);
            registerWaxPair(ModBlocks.COPPER_ROD, ModBlocks.WAXED_COPPER_ROD);
            registerWaxPair(ModBlocks.EXPOSED_COPPER_ROD, ModBlocks.WAXED_EXPOSED_COPPER_ROD);
            registerWaxPair(ModBlocks.WEATHERED_COPPER_ROD, ModBlocks.WAXED_WEATHERED_COPPER_ROD);
            registerWaxPair(ModBlocks.OXIDIZED_COPPER_ROD, ModBlocks.WAXED_OXIDIZED_COPPER_ROD);

            registerChain(ModBlocks.COPPER_LANTERN, ModBlocks.EXPOSED_COPPER_LANTERN,
                    ModBlocks.WEATHERED_COPPER_LANTERN, ModBlocks.OXIDIZED_COPPER_LANTERN);
            registerWaxPair(ModBlocks.COPPER_LANTERN, ModBlocks.WAXED_COPPER_LANTERN);
            registerWaxPair(ModBlocks.EXPOSED_COPPER_LANTERN, ModBlocks.WAXED_EXPOSED_COPPER_LANTERN);
            registerWaxPair(ModBlocks.WEATHERED_COPPER_LANTERN, ModBlocks.WAXED_WEATHERED_COPPER_LANTERN);
            registerWaxPair(ModBlocks.OXIDIZED_COPPER_LANTERN, ModBlocks.WAXED_OXIDIZED_COPPER_LANTERN);

            registerChain(ModBlocks.COPPER_DOOR, ModBlocks.EXPOSED_COPPER_DOOR, ModBlocks.WEATHERED_COPPER_DOOR,
                    ModBlocks.OXIDIZED_COPPER_DOOR);
            registerWaxPair(ModBlocks.COPPER_DOOR, ModBlocks.WAXED_COPPER_DOOR);
            registerWaxPair(ModBlocks.EXPOSED_COPPER_DOOR, ModBlocks.WAXED_EXPOSED_COPPER_DOOR);
            registerWaxPair(ModBlocks.WEATHERED_COPPER_DOOR, ModBlocks.WAXED_WEATHERED_COPPER_DOOR);
            registerWaxPair(ModBlocks.OXIDIZED_COPPER_DOOR, ModBlocks.WAXED_OXIDIZED_COPPER_DOOR);

            registerChain(ModBlocks.COPPER_TRAPDOOR, ModBlocks.EXPOSED_COPPER_TRAPDOOR,
                    ModBlocks.WEATHERED_COPPER_TRAPDOOR, ModBlocks.OXIDIZED_COPPER_TRAPDOOR);
            registerWaxPair(ModBlocks.COPPER_TRAPDOOR, ModBlocks.WAXED_COPPER_TRAPDOOR);
            registerWaxPair(ModBlocks.EXPOSED_COPPER_TRAPDOOR, ModBlocks.WAXED_EXPOSED_COPPER_TRAPDOOR);
            registerWaxPair(ModBlocks.WEATHERED_COPPER_TRAPDOOR, ModBlocks.WAXED_WEATHERED_COPPER_TRAPDOOR);
            registerWaxPair(ModBlocks.OXIDIZED_COPPER_TRAPDOOR, ModBlocks.WAXED_OXIDIZED_COPPER_TRAPDOOR);

            registerChain(ModBlocks.COPPER_BARS, ModBlocks.EXPOSED_COPPER_BARS, ModBlocks.WEATHERED_COPPER_BARS,
                    ModBlocks.OXIDIZED_COPPER_BARS);
            registerWaxPair(ModBlocks.COPPER_BARS, ModBlocks.WAXED_COPPER_BARS);
            registerWaxPair(ModBlocks.EXPOSED_COPPER_BARS, ModBlocks.WAXED_EXPOSED_COPPER_BARS);
            registerWaxPair(ModBlocks.WEATHERED_COPPER_BARS, ModBlocks.WAXED_WEATHERED_COPPER_BARS);
            registerWaxPair(ModBlocks.OXIDIZED_COPPER_BARS, ModBlocks.WAXED_OXIDIZED_COPPER_BARS);

            registerChain(ModBlocks.COPPER_MESH, ModBlocks.EXPOSED_COPPER_MESH, ModBlocks.WEATHERED_COPPER_MESH,
                    ModBlocks.OXIDIZED_COPPER_MESH);
            registerWaxPair(ModBlocks.COPPER_MESH, ModBlocks.WAXED_COPPER_MESH);
            registerWaxPair(ModBlocks.EXPOSED_COPPER_MESH, ModBlocks.WAXED_EXPOSED_COPPER_MESH);
            registerWaxPair(ModBlocks.WEATHERED_COPPER_MESH, ModBlocks.WAXED_WEATHERED_COPPER_MESH);
            registerWaxPair(ModBlocks.OXIDIZED_COPPER_MESH, ModBlocks.WAXED_OXIDIZED_COPPER_MESH);

            registerChain(ModBlocks.COPPER_BOLTS, ModBlocks.EXPOSED_COPPER_BOLTS, ModBlocks.WEATHERED_COPPER_BOLTS,
                    ModBlocks.OXIDIZED_COPPER_BOLTS);
            registerWaxPair(ModBlocks.COPPER_BOLTS, ModBlocks.WAXED_COPPER_BOLTS);
            registerWaxPair(ModBlocks.EXPOSED_COPPER_BOLTS, ModBlocks.WAXED_EXPOSED_COPPER_BOLTS);
            registerWaxPair(ModBlocks.WEATHERED_COPPER_BOLTS, ModBlocks.WAXED_WEATHERED_COPPER_BOLTS);
            registerWaxPair(ModBlocks.OXIDIZED_COPPER_BOLTS, ModBlocks.WAXED_OXIDIZED_COPPER_BOLTS);

            registerChain(ModBlocks.CUT_COPPER_VERTICAL_SLAB, ModBlocks.EXPOSED_CUT_COPPER_VERTICAL_SLAB,
                    ModBlocks.WEATHERED_CUT_COPPER_VERTICAL_SLAB, ModBlocks.OXIDIZED_CUT_COPPER_VERTICAL_SLAB);
            registerWaxPair(ModBlocks.CUT_COPPER_VERTICAL_SLAB, ModBlocks.WAXED_CUT_COPPER_VERTICAL_SLAB);
            registerWaxPair(ModBlocks.EXPOSED_CUT_COPPER_VERTICAL_SLAB,
                    ModBlocks.WAXED_EXPOSED_CUT_COPPER_VERTICAL_SLAB);
            registerWaxPair(ModBlocks.WEATHERED_CUT_COPPER_VERTICAL_SLAB,
                    ModBlocks.WAXED_WEATHERED_CUT_COPPER_VERTICAL_SLAB);
            registerWaxPair(ModBlocks.OXIDIZED_CUT_COPPER_VERTICAL_SLAB,
                    ModBlocks.WAXED_OXIDIZED_CUT_COPPER_VERTICAL_SLAB);

            registerChain(ModBlocks.COPPER_BUTTON, ModBlocks.EXPOSED_COPPER_BUTTON, ModBlocks.WEATHERED_COPPER_BUTTON,
                    ModBlocks.OXIDIZED_COPPER_BUTTON);
            registerWaxPair(ModBlocks.COPPER_BUTTON, ModBlocks.WAXED_COPPER_BUTTON);
            registerWaxPair(ModBlocks.EXPOSED_COPPER_BUTTON, ModBlocks.WAXED_EXPOSED_COPPER_BUTTON);
            registerWaxPair(ModBlocks.WEATHERED_COPPER_BUTTON, ModBlocks.WAXED_WEATHERED_COPPER_BUTTON);
            registerWaxPair(ModBlocks.OXIDIZED_COPPER_BUTTON, ModBlocks.WAXED_OXIDIZED_COPPER_BUTTON);

            registerChain(ModBlocks.COPPER_PRESSURE_PLATE, ModBlocks.EXPOSED_COPPER_PRESSURE_PLATE,
                    ModBlocks.WEATHERED_COPPER_PRESSURE_PLATE, ModBlocks.OXIDIZED_COPPER_PRESSURE_PLATE);
            registerWaxPair(ModBlocks.COPPER_PRESSURE_PLATE, ModBlocks.WAXED_COPPER_PRESSURE_PLATE);
            registerWaxPair(ModBlocks.EXPOSED_COPPER_PRESSURE_PLATE, ModBlocks.WAXED_EXPOSED_COPPER_PRESSURE_PLATE);
            registerWaxPair(ModBlocks.WEATHERED_COPPER_PRESSURE_PLATE, ModBlocks.WAXED_WEATHERED_COPPER_PRESSURE_PLATE);
            registerWaxPair(ModBlocks.OXIDIZED_COPPER_PRESSURE_PLATE, ModBlocks.WAXED_OXIDIZED_COPPER_PRESSURE_PLATE);

            registerChain(ModBlocks.COPPER_CHEST, ModBlocks.EXPOSED_COPPER_CHEST, ModBlocks.WEATHERED_COPPER_CHEST,
                    ModBlocks.OXIDIZED_COPPER_CHEST);
            registerWaxPair(ModBlocks.COPPER_CHEST, ModBlocks.WAXED_COPPER_CHEST);
            registerWaxPair(ModBlocks.EXPOSED_COPPER_CHEST, ModBlocks.WAXED_EXPOSED_COPPER_CHEST);
            registerWaxPair(ModBlocks.WEATHERED_COPPER_CHEST, ModBlocks.WAXED_WEATHERED_COPPER_CHEST);
            registerWaxPair(ModBlocks.OXIDIZED_COPPER_CHEST, ModBlocks.WAXED_OXIDIZED_COPPER_CHEST);

            registerChain(ModBlocks.COPPER_CHAIN, ModBlocks.EXPOSED_COPPER_CHAIN, ModBlocks.WEATHERED_COPPER_CHAIN,
                    ModBlocks.OXIDIZED_COPPER_CHAIN);

            registerChain(ModBlocks.LARGE_COPPER_CHAIN, ModBlocks.LARGE_EXPOSED_COPPER_CHAIN,
                    ModBlocks.LARGE_WEATHERED_COPPER_CHAIN, ModBlocks.LARGE_OXIDIZED_COPPER_CHAIN);

            registerChain(ModBlocks.SLIT_COPPER, ModBlocks.EXPOSED_SLIT_COPPER, ModBlocks.WEATHERED_SLIT_COPPER,
                    ModBlocks.OXIDIZED_SLIT_COPPER);
            registerWaxPair(ModBlocks.SLIT_COPPER, ModBlocks.WAXED_SLIT_COPPER);
            registerWaxPair(ModBlocks.EXPOSED_SLIT_COPPER, ModBlocks.WAXED_EXPOSED_SLIT_COPPER);
            registerWaxPair(ModBlocks.WEATHERED_SLIT_COPPER, ModBlocks.WAXED_WEATHERED_SLIT_COPPER);
            registerWaxPair(ModBlocks.OXIDIZED_SLIT_COPPER, ModBlocks.WAXED_OXIDIZED_SLIT_COPPER);

            registerChain(ModBlocks.SLIT_COPPER_STAIRS, ModBlocks.EXPOSED_SLIT_COPPER_STAIRS,
                    ModBlocks.WEATHERED_SLIT_COPPER_STAIRS, ModBlocks.OXIDIZED_SLIT_COPPER_STAIRS);
            registerWaxPair(ModBlocks.SLIT_COPPER_STAIRS, ModBlocks.WAXED_SLIT_COPPER_STAIRS);
            registerWaxPair(ModBlocks.EXPOSED_SLIT_COPPER_STAIRS, ModBlocks.WAXED_EXPOSED_SLIT_COPPER_STAIRS);
            registerWaxPair(ModBlocks.WEATHERED_SLIT_COPPER_STAIRS, ModBlocks.WAXED_WEATHERED_SLIT_COPPER_STAIRS);
            registerWaxPair(ModBlocks.OXIDIZED_SLIT_COPPER_STAIRS, ModBlocks.WAXED_OXIDIZED_SLIT_COPPER_STAIRS);

            registerChain(ModBlocks.SLIT_COPPER_SLAB, ModBlocks.EXPOSED_SLIT_COPPER_SLAB,
                    ModBlocks.WEATHERED_SLIT_COPPER_SLAB, ModBlocks.OXIDIZED_SLIT_COPPER_SLAB);
            registerWaxPair(ModBlocks.SLIT_COPPER_SLAB, ModBlocks.WAXED_SLIT_COPPER_SLAB);
            registerWaxPair(ModBlocks.EXPOSED_SLIT_COPPER_SLAB, ModBlocks.WAXED_EXPOSED_SLIT_COPPER_SLAB);
            registerWaxPair(ModBlocks.WEATHERED_SLIT_COPPER_SLAB, ModBlocks.WAXED_WEATHERED_SLIT_COPPER_SLAB);
            registerWaxPair(ModBlocks.OXIDIZED_SLIT_COPPER_SLAB, ModBlocks.WAXED_OXIDIZED_SLIT_COPPER_SLAB);

            registerChain(ModBlocks.SLIT_COPPER_VERTICAL_SLAB, ModBlocks.EXPOSED_SLIT_COPPER_VERTICAL_SLAB,
                    ModBlocks.WEATHERED_SLIT_COPPER_VERTICAL_SLAB, ModBlocks.OXIDIZED_SLIT_COPPER_VERTICAL_SLAB);
            registerWaxPair(ModBlocks.SLIT_COPPER_VERTICAL_SLAB, ModBlocks.WAXED_SLIT_COPPER_VERTICAL_SLAB);
            registerWaxPair(ModBlocks.EXPOSED_SLIT_COPPER_VERTICAL_SLAB,
                    ModBlocks.WAXED_EXPOSED_SLIT_COPPER_VERTICAL_SLAB);
            registerWaxPair(ModBlocks.WEATHERED_SLIT_COPPER_VERTICAL_SLAB,
                    ModBlocks.WAXED_WEATHERED_SLIT_COPPER_VERTICAL_SLAB);
            registerWaxPair(ModBlocks.OXIDIZED_SLIT_COPPER_VERTICAL_SLAB,
                    ModBlocks.WAXED_OXIDIZED_SLIT_COPPER_VERTICAL_SLAB);

            registerChain(ModBlocks.BIT_CHISELED_COPPER, ModBlocks.BIT_EXPOSED_CHISELED_COPPER,
                    ModBlocks.BIT_WEATHERED_CHISELED_COPPER, ModBlocks.BIT_OXIDIZED_CHISELED_COPPER);
            registerChain(ModBlocks.BIT_COPPER_BLOCK, ModBlocks.BIT_EXPOSED_COPPER_BLOCK,
                    ModBlocks.BIT_WEATHERED_COPPER_BLOCK, ModBlocks.BIT_OXIDIZED_COPPER_BLOCK);
            registerChain(ModBlocks.BIT_COPPER_BLOCK_STAIRS, ModBlocks.BIT_EXPOSED_COPPER_BLOCK_STAIRS,
                    ModBlocks.BIT_WEATHERED_COPPER_BLOCK_STAIRS, ModBlocks.BIT_OXIDIZED_COPPER_BLOCK_STAIRS);
            registerChain(ModBlocks.BIT_COPPER_BLOCK_SLAB, ModBlocks.BIT_EXPOSED_COPPER_BLOCK_SLAB,
                    ModBlocks.BIT_WEATHERED_COPPER_BLOCK_SLAB, ModBlocks.BIT_OXIDIZED_COPPER_BLOCK_SLAB);
            registerChain(ModBlocks.BIT_COPPER_BLOCK_WALL, ModBlocks.BIT_EXPOSED_COPPER_BLOCK_WALL,
                    ModBlocks.BIT_WEATHERED_COPPER_BLOCK_WALL, ModBlocks.BIT_OXIDIZED_COPPER_BLOCK_WALL);
            registerChain(ModBlocks.BIT_COPPER_BLOCK_VERTICAL_SLAB, ModBlocks.BIT_EXPOSED_COPPER_BLOCK_VERTICAL_SLAB,
                    ModBlocks.BIT_WEATHERED_COPPER_BLOCK_VERTICAL_SLAB,
                    ModBlocks.BIT_OXIDIZED_COPPER_BLOCK_VERTICAL_SLAB);
            registerChain(ModBlocks.BIT_CUT_COPPER, ModBlocks.BIT_EXPOSED_CUT_COPPER,
                    ModBlocks.BIT_WEATHERED_CUT_COPPER, ModBlocks.BIT_OXIDIZED_CUT_COPPER);
            registerChain(ModBlocks.BIT_CUT_COPPER_STAIRS, ModBlocks.BIT_EXPOSED_CUT_COPPER_STAIRS,
                    ModBlocks.BIT_WEATHERED_CUT_COPPER_STAIRS, ModBlocks.BIT_OXIDIZED_CUT_COPPER_STAIRS);
            registerChain(ModBlocks.BIT_CUT_COPPER_SLAB, ModBlocks.BIT_EXPOSED_CUT_COPPER_SLAB,
                    ModBlocks.BIT_WEATHERED_CUT_COPPER_SLAB, ModBlocks.BIT_OXIDIZED_CUT_COPPER_SLAB);
            registerChain(ModBlocks.BIT_CUT_COPPER_WALL, ModBlocks.BIT_EXPOSED_CUT_COPPER_WALL,
                    ModBlocks.BIT_WEATHERED_CUT_COPPER_WALL, ModBlocks.BIT_OXIDIZED_CUT_COPPER_WALL);
            registerChain(ModBlocks.BIT_CUT_COPPER_VERTICAL_SLAB, ModBlocks.BIT_EXPOSED_CUT_COPPER_VERTICAL_SLAB,
                    ModBlocks.BIT_WEATHERED_CUT_COPPER_VERTICAL_SLAB, ModBlocks.BIT_OXIDIZED_CUT_COPPER_VERTICAL_SLAB);
            registerChain(ModBlocks.BIT_COPPER_BULB, ModBlocks.BIT_EXPOSED_COPPER_BULB,
                    ModBlocks.BIT_WEATHERED_COPPER_BULB, ModBlocks.BIT_OXIDIZED_COPPER_BULB);
            registerChain(ModBlocks.BIT_COPPER_GRATE, ModBlocks.BIT_EXPOSED_COPPER_GRATE,
                    ModBlocks.BIT_WEATHERED_COPPER_GRATE, ModBlocks.BIT_OXIDIZED_COPPER_GRATE);
            initialized = true;
        }
    }

    private static void registerChain(Supplier<Block> b0, Supplier<Block> b1, Supplier<Block> b2, Supplier<Block> b3) {
        Block[] stages = {b0.get(), b1.get(), b2.get(), b3.get()};
        STAGES.addChain(stages[0], stages[1], stages[2], stages[3]);
        for (int i = 0; i < stages.length; i++) {
            Block block = stages[i];
            AGES.put(block, WeatheringCopper.WeatherState.values()[i]);
        }
    }

    private static void registerWaxPair(Supplier<Block> unwaxed, Supplier<Block> waxed) {
        Block unwaxedBlock = unwaxed.get();
        Block waxedBlock = waxed.get();
        STAGES.addWaxPair(unwaxedBlock, waxedBlock);
    }

    public static WeatheringCopper.WeatherState getAge(Block block) {
        init();
        return AGES.get(block);
    }

    public static Optional<BlockState> getNext(BlockState state) {
        init();
        Block target = STAGES.next(state.getBlock());
        return Optional.ofNullable(target).or(() -> WeatheringCopper.getNext(state.getBlock()))
                .map(block -> copyStateProperties(state, block.defaultBlockState()));
    }

    public static boolean canOxidize(BlockState state) {
        init();
        return STAGES.next(state.getBlock()) != null
                && (!(state.getBlock() instanceof DoorBlock)
                        || state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER);
    }

    public static BlockState getToolModifiedState(BlockState state, ToolAction action) {
        init();
        Block target = action == ToolActions.AXE_SCRAPE
                ? STAGES.previous(state.getBlock())
                : action == ToolActions.AXE_WAX_OFF ? STAGES.unwaxed(state.getBlock()) : null;
        return target == null ? null : copyStateProperties(state, target.defaultBlockState());
    }

    public static boolean handleRightClick(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        ItemStack held = event.getItemStack();
        Player player = event.getPlayer();

        if (held.is(ModItems.BOTTLE_OF_MIST.get())) {
            BlockState nextState = getNextOxidationState(state);
            if (nextState != null && nextState.getBlock() != block) {
                if (!level.isClientSide) {
                    setBlockStateOrDoor(level, pos, state, nextState.getBlock());
                    if (level.getBlockState(pos).getBlock() != nextState.getBlock()) {
                        return false;
                    }
                    ((ServerLevel) level).sendParticles(ParticleTypes.SMOKE,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            8, 0.25, 0.25, 0.25, 0.05);
                    if (!player.getAbilities().instabuild) {
                        held.shrink(1);
                        ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);
                        if (held.isEmpty()) {
                            player.setItemInHand(event.getHand(), emptyBottle);
                        } else if (!player.getInventory().add(emptyBottle)) {
                            player.drop(emptyBottle, false);
                        }
                    }
                }
                level.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.6F, 0.8F);
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                return true;
            }
        }

        if (held.is(Items.HONEYCOMB)) {
            if (block instanceof EyeblossomBlock && !state.getValue(EyeblossomBlock.WAXED)) {
                if (!level.isClientSide) {
                    level.setBlock(pos, state.setValue(EyeblossomBlock.WAXED, true), 3);
                    level.levelEvent(3003, pos, 0);
                    if (!player.getAbilities().instabuild) {
                        held.shrink(1);
                    }
                }
                level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                return true;
            }
            init();
            Block target = STAGES.waxed(block);
            if (target != null) {
                if (!level.isClientSide) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, held);
                    }
                    setBlockStateOrDoor(level, pos, state, target);
                    level.levelEvent(3003, pos, 0);
                    if (!player.getAbilities().instabuild) {
                        held.shrink(1);
                    }
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                return true;
            }
        }

        if (held.getItem() instanceof AxeItem && block instanceof EyeblossomBlock
                && state.getValue(EyeblossomBlock.WAXED)) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(EyeblossomBlock.WAXED, false), 3);
                level.levelEvent(3004, pos, 0);
                if (!player.getAbilities().instabuild) {
                    held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));
                }
            }
            level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
            return true;
        }

        if ((held.canPerformAction(ToolActions.AXE_SCRAPE)
                && getToolModifiedState(state, ToolActions.AXE_SCRAPE) != null)
                || (held.canPerformAction(ToolActions.AXE_WAX_OFF)
                        && getToolModifiedState(state, ToolActions.AXE_WAX_OFF) != null)) {
            event.setUseBlock(Event.Result.DENY);
        }
        return false;
    }

    public static void setBlockStateOrDoor(Level level, BlockPos pos, BlockState state, Block targetBlock) {
        if (state.hasProperty(DoorBlock.HALF)) {
            DoubleBlockHalf half = state.getValue(DoorBlock.HALF);
            BlockPos lowerPos = half == DoubleBlockHalf.LOWER ? pos : pos.below();
            BlockPos upperPos = half == DoubleBlockHalf.LOWER ? pos.above() : pos;

            BlockState lowerState = copyStateProperties(level.getBlockState(lowerPos), targetBlock.defaultBlockState())
                    .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER);
            BlockState upperState = copyStateProperties(level.getBlockState(upperPos), targetBlock.defaultBlockState())
                    .setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);

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
        return STAGES.previous(block);
    }

    public static Block getFirstStage(Block block) {
        init();
        return STAGES.first(block);
    }

    public static boolean isWeatheringCopper(Block block) {
        init();
        return AGES.containsKey(block) || block instanceof WeatheringCopper;
    }

    public static BlockState getNextOxidationState(BlockState state) {
        return getNext(state).orElse(null);
    }

    static <T> void registerChain(Map<Supplier<T>, Supplier<T>> next, Map<Supplier<T>, Supplier<T>> previous,
            Supplier<T> b0, Supplier<T> b1, Supplier<T> b2, Supplier<T> b3) {
        Supplier<T> s0 = new StageSupplier<>(b0.get());
        Supplier<T> s1 = new StageSupplier<>(b1.get());
        Supplier<T> s2 = new StageSupplier<>(b2.get());
        Supplier<T> s3 = new StageSupplier<>(b3.get());
        next.put(s0, s1);
        next.put(s1, s2);
        next.put(s2, s3);
        previous.put(s3, s2);
        previous.put(s2, s1);
        previous.put(s1, s0);
    }

    static <T> T getMappedStage(T stage, Map<Supplier<T>, Supplier<T>> stages) {
        Supplier<T> result = stages.get(new StageSupplier<>(stage));
        return result == null ? null : result.get();
    }

    private static final class StageSupplier<T> implements Supplier<T> {
        private final T stage;

        private StageSupplier(T stage) {
            this.stage = stage;
        }

        @Override
        public T get() {
            return stage;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(stage);
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof StageSupplier<?> supplier && stage == supplier.stage;
        }
    }

    static <T> boolean isWeatheringStage(T stage, Map<Supplier<T>, Supplier<T>> next,
            Map<Supplier<T>, Supplier<T>> previous) {
        return getMappedStage(stage, next) != null || getMappedStage(stage, previous) != null;
    }

    static <T> T getFirstStage(T stage, Map<Supplier<T>, Supplier<T>> next,
            Map<Supplier<T>, Supplier<T>> previous) {
        if (!isWeatheringStage(stage, next, previous)) {
            return null;
        }
        T current = stage;
        while (true) {
            T prev = getMappedStage(current, previous);
            if (prev == null) {
                break;
            }
            current = prev;
        }
        return current;
    }
}
