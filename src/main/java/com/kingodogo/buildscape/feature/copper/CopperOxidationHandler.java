package com.kingodogo.buildscape.feature.copper;

import com.kingodogo.buildscape.block.plant.EyeblossomBlock;
import com.kingodogo.buildscape.registry.blocks.CopperBlocks;
import com.kingodogo.buildscape.registry.blocks.SandstoneBlocks;
import com.kingodogo.buildscape.registry.blocks.ShapedStoneBlocks;
import com.kingodogo.buildscape.registry.blocks.ShapedWoodBlocks;
import com.kingodogo.buildscape.registry.blocks.StoneBlocks;
import com.kingodogo.buildscape.registry.blocks.WoodBlocks;
import com.kingodogo.buildscape.registry.blocks.WoolBlocks;
import com.kingodogo.buildscape.registry.items.MasonryItems;
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
            registerChain(CopperBlocks.CHISELED_COPPER, CopperBlocks.EXPOSED_CHISELED_COPPER,
                    CopperBlocks.WEATHERED_CHISELED_COPPER, CopperBlocks.OXIDIZED_CHISELED_COPPER);
            registerWaxPair(CopperBlocks.CHISELED_COPPER, CopperBlocks.WAXED_CHISELED_COPPER);
            registerWaxPair(CopperBlocks.EXPOSED_CHISELED_COPPER, CopperBlocks.WAXED_EXPOSED_CHISELED_COPPER);
            registerWaxPair(CopperBlocks.WEATHERED_CHISELED_COPPER, CopperBlocks.WAXED_WEATHERED_CHISELED_COPPER);
            registerWaxPair(CopperBlocks.OXIDIZED_CHISELED_COPPER, CopperBlocks.WAXED_OXIDIZED_CHISELED_COPPER);

            registerChain(CopperBlocks.COPPER_GRATE, CopperBlocks.EXPOSED_COPPER_GRATE,
                    CopperBlocks.WEATHERED_COPPER_GRATE,
                    CopperBlocks.OXIDIZED_COPPER_GRATE);
            registerWaxPair(CopperBlocks.COPPER_GRATE, CopperBlocks.WAXED_COPPER_GRATE);
            registerWaxPair(CopperBlocks.EXPOSED_COPPER_GRATE, CopperBlocks.WAXED_EXPOSED_COPPER_GRATE);
            registerWaxPair(CopperBlocks.WEATHERED_COPPER_GRATE, CopperBlocks.WAXED_WEATHERED_COPPER_GRATE);
            registerWaxPair(CopperBlocks.OXIDIZED_COPPER_GRATE, CopperBlocks.WAXED_OXIDIZED_COPPER_GRATE);

            registerChain(CopperBlocks.COPPER_BULB, CopperBlocks.EXPOSED_COPPER_BULB,
                    CopperBlocks.WEATHERED_COPPER_BULB,
                    CopperBlocks.OXIDIZED_COPPER_BULB);
            registerWaxPair(CopperBlocks.COPPER_BULB, CopperBlocks.WAXED_COPPER_BULB);
            registerWaxPair(CopperBlocks.EXPOSED_COPPER_BULB, CopperBlocks.WAXED_EXPOSED_COPPER_BULB);
            registerWaxPair(CopperBlocks.WEATHERED_COPPER_BULB, CopperBlocks.WAXED_WEATHERED_COPPER_BULB);
            registerWaxPair(CopperBlocks.OXIDIZED_COPPER_BULB, CopperBlocks.WAXED_OXIDIZED_COPPER_BULB);

            registerChain(CopperBlocks.COPPER_ROD, CopperBlocks.EXPOSED_COPPER_ROD, CopperBlocks.WEATHERED_COPPER_ROD,
                    CopperBlocks.OXIDIZED_COPPER_ROD);
            registerWaxPair(CopperBlocks.COPPER_ROD, CopperBlocks.WAXED_COPPER_ROD);
            registerWaxPair(CopperBlocks.EXPOSED_COPPER_ROD, CopperBlocks.WAXED_EXPOSED_COPPER_ROD);
            registerWaxPair(CopperBlocks.WEATHERED_COPPER_ROD, CopperBlocks.WAXED_WEATHERED_COPPER_ROD);
            registerWaxPair(CopperBlocks.OXIDIZED_COPPER_ROD, CopperBlocks.WAXED_OXIDIZED_COPPER_ROD);

            registerChain(CopperBlocks.COPPER_LANTERN, CopperBlocks.EXPOSED_COPPER_LANTERN,
                    CopperBlocks.WEATHERED_COPPER_LANTERN, CopperBlocks.OXIDIZED_COPPER_LANTERN);
            registerWaxPair(CopperBlocks.COPPER_LANTERN, CopperBlocks.WAXED_COPPER_LANTERN);
            registerWaxPair(CopperBlocks.EXPOSED_COPPER_LANTERN, CopperBlocks.WAXED_EXPOSED_COPPER_LANTERN);
            registerWaxPair(CopperBlocks.WEATHERED_COPPER_LANTERN, CopperBlocks.WAXED_WEATHERED_COPPER_LANTERN);
            registerWaxPair(CopperBlocks.OXIDIZED_COPPER_LANTERN, CopperBlocks.WAXED_OXIDIZED_COPPER_LANTERN);

            registerChain(CopperBlocks.COPPER_DOOR, CopperBlocks.EXPOSED_COPPER_DOOR,
                    CopperBlocks.WEATHERED_COPPER_DOOR,
                    CopperBlocks.OXIDIZED_COPPER_DOOR);
            registerWaxPair(CopperBlocks.COPPER_DOOR, CopperBlocks.WAXED_COPPER_DOOR);
            registerWaxPair(CopperBlocks.EXPOSED_COPPER_DOOR, CopperBlocks.WAXED_EXPOSED_COPPER_DOOR);
            registerWaxPair(CopperBlocks.WEATHERED_COPPER_DOOR, CopperBlocks.WAXED_WEATHERED_COPPER_DOOR);
            registerWaxPair(CopperBlocks.OXIDIZED_COPPER_DOOR, CopperBlocks.WAXED_OXIDIZED_COPPER_DOOR);

            registerChain(CopperBlocks.COPPER_TRAPDOOR, CopperBlocks.EXPOSED_COPPER_TRAPDOOR,
                    CopperBlocks.WEATHERED_COPPER_TRAPDOOR, CopperBlocks.OXIDIZED_COPPER_TRAPDOOR);
            registerWaxPair(CopperBlocks.COPPER_TRAPDOOR, CopperBlocks.WAXED_COPPER_TRAPDOOR);
            registerWaxPair(CopperBlocks.EXPOSED_COPPER_TRAPDOOR, CopperBlocks.WAXED_EXPOSED_COPPER_TRAPDOOR);
            registerWaxPair(CopperBlocks.WEATHERED_COPPER_TRAPDOOR, CopperBlocks.WAXED_WEATHERED_COPPER_TRAPDOOR);
            registerWaxPair(CopperBlocks.OXIDIZED_COPPER_TRAPDOOR, CopperBlocks.WAXED_OXIDIZED_COPPER_TRAPDOOR);

            registerChain(CopperBlocks.COPPER_BARS, CopperBlocks.EXPOSED_COPPER_BARS,
                    CopperBlocks.WEATHERED_COPPER_BARS,
                    CopperBlocks.OXIDIZED_COPPER_BARS);
            registerWaxPair(CopperBlocks.COPPER_BARS, CopperBlocks.WAXED_COPPER_BARS);
            registerWaxPair(CopperBlocks.EXPOSED_COPPER_BARS, CopperBlocks.WAXED_EXPOSED_COPPER_BARS);
            registerWaxPair(CopperBlocks.WEATHERED_COPPER_BARS, CopperBlocks.WAXED_WEATHERED_COPPER_BARS);
            registerWaxPair(CopperBlocks.OXIDIZED_COPPER_BARS, CopperBlocks.WAXED_OXIDIZED_COPPER_BARS);

            registerChain(CopperBlocks.COPPER_MESH, CopperBlocks.EXPOSED_COPPER_MESH,
                    CopperBlocks.WEATHERED_COPPER_MESH,
                    CopperBlocks.OXIDIZED_COPPER_MESH);
            registerWaxPair(CopperBlocks.COPPER_MESH, CopperBlocks.WAXED_COPPER_MESH);
            registerWaxPair(CopperBlocks.EXPOSED_COPPER_MESH, CopperBlocks.WAXED_EXPOSED_COPPER_MESH);
            registerWaxPair(CopperBlocks.WEATHERED_COPPER_MESH, CopperBlocks.WAXED_WEATHERED_COPPER_MESH);
            registerWaxPair(CopperBlocks.OXIDIZED_COPPER_MESH, CopperBlocks.WAXED_OXIDIZED_COPPER_MESH);

            registerChain(CopperBlocks.COPPER_BOLTS, CopperBlocks.EXPOSED_COPPER_BOLTS,
                    CopperBlocks.WEATHERED_COPPER_BOLTS,
                    CopperBlocks.OXIDIZED_COPPER_BOLTS);
            registerWaxPair(CopperBlocks.COPPER_BOLTS, CopperBlocks.WAXED_COPPER_BOLTS);
            registerWaxPair(CopperBlocks.EXPOSED_COPPER_BOLTS, CopperBlocks.WAXED_EXPOSED_COPPER_BOLTS);
            registerWaxPair(CopperBlocks.WEATHERED_COPPER_BOLTS, CopperBlocks.WAXED_WEATHERED_COPPER_BOLTS);
            registerWaxPair(CopperBlocks.OXIDIZED_COPPER_BOLTS, CopperBlocks.WAXED_OXIDIZED_COPPER_BOLTS);

            registerChain(ShapedWoodBlocks.CUT_COPPER_VERTICAL_SLAB, ShapedWoodBlocks.EXPOSED_CUT_COPPER_VERTICAL_SLAB,
                    ShapedWoodBlocks.WEATHERED_CUT_COPPER_VERTICAL_SLAB,
                    ShapedWoodBlocks.OXIDIZED_CUT_COPPER_VERTICAL_SLAB);
            registerWaxPair(ShapedWoodBlocks.CUT_COPPER_VERTICAL_SLAB, ShapedWoodBlocks.WAXED_CUT_COPPER_VERTICAL_SLAB);
            registerWaxPair(ShapedWoodBlocks.EXPOSED_CUT_COPPER_VERTICAL_SLAB,
                    ShapedWoodBlocks.WAXED_EXPOSED_CUT_COPPER_VERTICAL_SLAB);
            registerWaxPair(ShapedWoodBlocks.WEATHERED_CUT_COPPER_VERTICAL_SLAB,
                    ShapedWoodBlocks.WAXED_WEATHERED_CUT_COPPER_VERTICAL_SLAB);
            registerWaxPair(ShapedWoodBlocks.OXIDIZED_CUT_COPPER_VERTICAL_SLAB,
                    ShapedWoodBlocks.WAXED_OXIDIZED_CUT_COPPER_VERTICAL_SLAB);

            registerChain(CopperBlocks.COPPER_BUTTON, CopperBlocks.EXPOSED_COPPER_BUTTON,
                    CopperBlocks.WEATHERED_COPPER_BUTTON,
                    CopperBlocks.OXIDIZED_COPPER_BUTTON);
            registerWaxPair(CopperBlocks.COPPER_BUTTON, CopperBlocks.WAXED_COPPER_BUTTON);
            registerWaxPair(CopperBlocks.EXPOSED_COPPER_BUTTON, CopperBlocks.WAXED_EXPOSED_COPPER_BUTTON);
            registerWaxPair(CopperBlocks.WEATHERED_COPPER_BUTTON, CopperBlocks.WAXED_WEATHERED_COPPER_BUTTON);
            registerWaxPair(CopperBlocks.OXIDIZED_COPPER_BUTTON, CopperBlocks.WAXED_OXIDIZED_COPPER_BUTTON);

            registerChain(CopperBlocks.COPPER_PRESSURE_PLATE, CopperBlocks.EXPOSED_COPPER_PRESSURE_PLATE,
                    CopperBlocks.WEATHERED_COPPER_PRESSURE_PLATE, CopperBlocks.OXIDIZED_COPPER_PRESSURE_PLATE);
            registerWaxPair(CopperBlocks.COPPER_PRESSURE_PLATE, CopperBlocks.WAXED_COPPER_PRESSURE_PLATE);
            registerWaxPair(CopperBlocks.EXPOSED_COPPER_PRESSURE_PLATE,
                    CopperBlocks.WAXED_EXPOSED_COPPER_PRESSURE_PLATE);
            registerWaxPair(CopperBlocks.WEATHERED_COPPER_PRESSURE_PLATE,
                    CopperBlocks.WAXED_WEATHERED_COPPER_PRESSURE_PLATE);
            registerWaxPair(CopperBlocks.OXIDIZED_COPPER_PRESSURE_PLATE,
                    CopperBlocks.WAXED_OXIDIZED_COPPER_PRESSURE_PLATE);

            registerChain(CopperBlocks.COPPER_CHEST, CopperBlocks.EXPOSED_COPPER_CHEST,
                    CopperBlocks.WEATHERED_COPPER_CHEST,
                    CopperBlocks.OXIDIZED_COPPER_CHEST);
            registerWaxPair(CopperBlocks.COPPER_CHEST, CopperBlocks.WAXED_COPPER_CHEST);
            registerWaxPair(CopperBlocks.EXPOSED_COPPER_CHEST, CopperBlocks.WAXED_EXPOSED_COPPER_CHEST);
            registerWaxPair(CopperBlocks.WEATHERED_COPPER_CHEST, CopperBlocks.WAXED_WEATHERED_COPPER_CHEST);
            registerWaxPair(CopperBlocks.OXIDIZED_COPPER_CHEST, CopperBlocks.WAXED_OXIDIZED_COPPER_CHEST);

            registerChain(WoodBlocks.COPPER_CHAIN, WoodBlocks.EXPOSED_COPPER_CHAIN, WoodBlocks.WEATHERED_COPPER_CHAIN,
                    WoodBlocks.OXIDIZED_COPPER_CHAIN);

            registerChain(CopperBlocks.LARGE_COPPER_CHAIN, CopperBlocks.LARGE_EXPOSED_COPPER_CHAIN,
                    WoolBlocks.LARGE_WEATHERED_COPPER_CHAIN, WoolBlocks.LARGE_OXIDIZED_COPPER_CHAIN);

            registerChain(CopperBlocks.SLIT_COPPER, CopperBlocks.EXPOSED_SLIT_COPPER,
                    CopperBlocks.WEATHERED_SLIT_COPPER,
                    CopperBlocks.OXIDIZED_SLIT_COPPER);
            registerWaxPair(CopperBlocks.SLIT_COPPER, CopperBlocks.WAXED_SLIT_COPPER);
            registerWaxPair(CopperBlocks.EXPOSED_SLIT_COPPER, CopperBlocks.WAXED_EXPOSED_SLIT_COPPER);
            registerWaxPair(CopperBlocks.WEATHERED_SLIT_COPPER, CopperBlocks.WAXED_WEATHERED_SLIT_COPPER);
            registerWaxPair(CopperBlocks.OXIDIZED_SLIT_COPPER, CopperBlocks.WAXED_OXIDIZED_SLIT_COPPER);

            registerChain(CopperBlocks.SLIT_COPPER_STAIRS, CopperBlocks.EXPOSED_SLIT_COPPER_STAIRS,
                    CopperBlocks.WEATHERED_SLIT_COPPER_STAIRS, CopperBlocks.OXIDIZED_SLIT_COPPER_STAIRS);
            registerWaxPair(CopperBlocks.SLIT_COPPER_STAIRS, CopperBlocks.WAXED_SLIT_COPPER_STAIRS);
            registerWaxPair(CopperBlocks.EXPOSED_SLIT_COPPER_STAIRS, CopperBlocks.WAXED_EXPOSED_SLIT_COPPER_STAIRS);
            registerWaxPair(CopperBlocks.WEATHERED_SLIT_COPPER_STAIRS, CopperBlocks.WAXED_WEATHERED_SLIT_COPPER_STAIRS);
            registerWaxPair(CopperBlocks.OXIDIZED_SLIT_COPPER_STAIRS, CopperBlocks.WAXED_OXIDIZED_SLIT_COPPER_STAIRS);

            registerChain(CopperBlocks.SLIT_COPPER_SLAB, CopperBlocks.EXPOSED_SLIT_COPPER_SLAB,
                    CopperBlocks.WEATHERED_SLIT_COPPER_SLAB, CopperBlocks.OXIDIZED_SLIT_COPPER_SLAB);
            registerWaxPair(CopperBlocks.SLIT_COPPER_SLAB, CopperBlocks.WAXED_SLIT_COPPER_SLAB);
            registerWaxPair(CopperBlocks.EXPOSED_SLIT_COPPER_SLAB, CopperBlocks.WAXED_EXPOSED_SLIT_COPPER_SLAB);
            registerWaxPair(CopperBlocks.WEATHERED_SLIT_COPPER_SLAB, CopperBlocks.WAXED_WEATHERED_SLIT_COPPER_SLAB);
            registerWaxPair(CopperBlocks.OXIDIZED_SLIT_COPPER_SLAB, CopperBlocks.WAXED_OXIDIZED_SLIT_COPPER_SLAB);

            registerChain(CopperBlocks.SLIT_COPPER_VERTICAL_SLAB, CopperBlocks.EXPOSED_SLIT_COPPER_VERTICAL_SLAB,
                    CopperBlocks.WEATHERED_SLIT_COPPER_VERTICAL_SLAB, CopperBlocks.OXIDIZED_SLIT_COPPER_VERTICAL_SLAB);
            registerWaxPair(CopperBlocks.SLIT_COPPER_VERTICAL_SLAB, CopperBlocks.WAXED_SLIT_COPPER_VERTICAL_SLAB);
            registerWaxPair(CopperBlocks.EXPOSED_SLIT_COPPER_VERTICAL_SLAB,
                    CopperBlocks.WAXED_EXPOSED_SLIT_COPPER_VERTICAL_SLAB);
            registerWaxPair(CopperBlocks.WEATHERED_SLIT_COPPER_VERTICAL_SLAB,
                    CopperBlocks.WAXED_WEATHERED_SLIT_COPPER_VERTICAL_SLAB);
            registerWaxPair(CopperBlocks.OXIDIZED_SLIT_COPPER_VERTICAL_SLAB,
                    CopperBlocks.WAXED_OXIDIZED_SLIT_COPPER_VERTICAL_SLAB);

            registerChain(SandstoneBlocks.BIT_CHISELED_COPPER, SandstoneBlocks.BIT_EXPOSED_CHISELED_COPPER,
                    SandstoneBlocks.BIT_WEATHERED_CHISELED_COPPER, SandstoneBlocks.BIT_OXIDIZED_CHISELED_COPPER);
            registerChain(SandstoneBlocks.BIT_COPPER_BLOCK, SandstoneBlocks.BIT_EXPOSED_COPPER_BLOCK,
                    SandstoneBlocks.BIT_WEATHERED_COPPER_BLOCK, SandstoneBlocks.BIT_OXIDIZED_COPPER_BLOCK);
            registerChain(SandstoneBlocks.BIT_COPPER_BLOCK_STAIRS, SandstoneBlocks.BIT_EXPOSED_COPPER_BLOCK_STAIRS,
                    SandstoneBlocks.BIT_WEATHERED_COPPER_BLOCK_STAIRS,
                    SandstoneBlocks.BIT_OXIDIZED_COPPER_BLOCK_STAIRS);
            registerChain(StoneBlocks.BIT_COPPER_BLOCK_SLAB, StoneBlocks.BIT_EXPOSED_COPPER_BLOCK_SLAB,
                    StoneBlocks.BIT_WEATHERED_COPPER_BLOCK_SLAB, StoneBlocks.BIT_OXIDIZED_COPPER_BLOCK_SLAB);
            registerChain(StoneBlocks.BIT_COPPER_BLOCK_WALL, StoneBlocks.BIT_EXPOSED_COPPER_BLOCK_WALL,
                    StoneBlocks.BIT_WEATHERED_COPPER_BLOCK_WALL, StoneBlocks.BIT_OXIDIZED_COPPER_BLOCK_WALL);
            registerChain(ShapedStoneBlocks.BIT_COPPER_BLOCK_VERTICAL_SLAB,
                    ShapedStoneBlocks.BIT_EXPOSED_COPPER_BLOCK_VERTICAL_SLAB,
                    ShapedStoneBlocks.BIT_WEATHERED_COPPER_BLOCK_VERTICAL_SLAB,
                    ShapedStoneBlocks.BIT_OXIDIZED_COPPER_BLOCK_VERTICAL_SLAB);
            registerChain(SandstoneBlocks.BIT_CUT_COPPER, SandstoneBlocks.BIT_EXPOSED_CUT_COPPER,
                    SandstoneBlocks.BIT_WEATHERED_CUT_COPPER, SandstoneBlocks.BIT_OXIDIZED_CUT_COPPER);
            registerChain(SandstoneBlocks.BIT_CUT_COPPER_STAIRS, SandstoneBlocks.BIT_EXPOSED_CUT_COPPER_STAIRS,
                    SandstoneBlocks.BIT_WEATHERED_CUT_COPPER_STAIRS, SandstoneBlocks.BIT_OXIDIZED_CUT_COPPER_STAIRS);
            registerChain(StoneBlocks.BIT_CUT_COPPER_SLAB, StoneBlocks.BIT_EXPOSED_CUT_COPPER_SLAB,
                    StoneBlocks.BIT_WEATHERED_CUT_COPPER_SLAB, StoneBlocks.BIT_OXIDIZED_CUT_COPPER_SLAB);
            registerChain(StoneBlocks.BIT_CUT_COPPER_WALL, StoneBlocks.BIT_EXPOSED_CUT_COPPER_WALL,
                    StoneBlocks.BIT_WEATHERED_CUT_COPPER_WALL, StoneBlocks.BIT_OXIDIZED_CUT_COPPER_WALL);
            registerChain(ShapedStoneBlocks.BIT_CUT_COPPER_VERTICAL_SLAB,
                    ShapedStoneBlocks.BIT_EXPOSED_CUT_COPPER_VERTICAL_SLAB,
                    ShapedStoneBlocks.BIT_WEATHERED_CUT_COPPER_VERTICAL_SLAB,
                    ShapedStoneBlocks.BIT_OXIDIZED_CUT_COPPER_VERTICAL_SLAB);
            registerChain(SandstoneBlocks.BIT_COPPER_BULB, SandstoneBlocks.BIT_EXPOSED_COPPER_BULB,
                    SandstoneBlocks.BIT_WEATHERED_COPPER_BULB, SandstoneBlocks.BIT_OXIDIZED_COPPER_BULB);
            registerChain(SandstoneBlocks.BIT_COPPER_GRATE, SandstoneBlocks.BIT_EXPOSED_COPPER_GRATE,
                    SandstoneBlocks.BIT_WEATHERED_COPPER_GRATE, SandstoneBlocks.BIT_OXIDIZED_COPPER_GRATE);
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

        if (held.is(MasonryItems.BOTTLE_OF_MIST.get())) {
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
