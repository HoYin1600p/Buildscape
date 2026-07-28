package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class LogStrippingHandler {

    private static final Map<Supplier<Block>, Supplier<Block>> STRIP_MAP = new HashMap<>();

    public static void init() {
        if (!STRIP_MAP.isEmpty()) return;

        // 1. Poplar
        registerPair(ModBlocks.POPLAR_LOG, ModBlocks.STRIPPED_POPLAR_LOG);
        registerPair(ModBlocks.POPLAR_WOOD, ModBlocks.STRIPPED_POPLAR_WOOD);
        registerPair(ModBlocks.POPLAR_LOG_SLAB, ModBlocks.STRIPPED_POPLAR_LOG_SLAB);
        registerPair(ModBlocks.POPLAR_LOG_VERTICAL_SLAB, ModBlocks.STRIPPED_POPLAR_LOG_VERTICAL_SLAB);

        // Ashpen
        registerPair(ModBlocks.ASHPEN_LOG, ModBlocks.STRIPPED_ASHPEN_LOG);
        registerPair(ModBlocks.ASHPEN_WOOD, ModBlocks.STRIPPED_ASHPEN_WOOD);
        registerPair(ModBlocks.ASHPEN_LOG_SLAB, ModBlocks.STRIPPED_ASHPEN_LOG_SLAB);
        registerPair(ModBlocks.ASHPEN_LOG_VERTICAL_SLAB, ModBlocks.STRIPPED_ASHPEN_LOG_VERTICAL_SLAB);
        registerPair(ModBlocks.ASHPEN_WOOD_SLAB, ModBlocks.STRIPPED_ASHPEN_WOOD_SLAB);
        registerPair(ModBlocks.ASHPEN_WOOD_VERTICAL_SLAB, ModBlocks.STRIPPED_ASHPEN_WOOD_VERTICAL_SLAB);

        // 2. Pale Oak
        registerPair(ModBlocks.PALE_OAK_LOG, ModBlocks.STRIPPED_PALE_OAK_LOG);
        registerPair(ModBlocks.PALE_OAK_WOOD, ModBlocks.STRIPPED_PALE_OAK_WOOD);
        registerPair(ModBlocks.PALE_OAK_LOG_SLAB, ModBlocks.STRIPPED_PALE_OAK_LOG_SLAB);
        registerPair(ModBlocks.PALE_OAK_LOG_VERTICAL_SLAB, ModBlocks.STRIPPED_PALE_OAK_LOG_VERTICAL_SLAB);
        registerPair(ModBlocks.PALE_OAK_WOOD_SLAB, ModBlocks.STRIPPED_PALE_OAK_WOOD_SLAB);
        registerPair(ModBlocks.PALE_OAK_WOOD_VERTICAL_SLAB, ModBlocks.STRIPPED_PALE_OAK_WOOD_VERTICAL_SLAB);

        // 3. Cherry
        registerPair(ModBlocks.CHERRY_LOG, ModBlocks.STRIPPED_CHERRY_LOG);
        registerPair(ModBlocks.CHERRY_WOOD, ModBlocks.STRIPPED_CHERRY_WOOD);
        registerPair(ModBlocks.CHERRY_LOG_SLAB, ModBlocks.STRIPPED_CHERRY_LOG_SLAB);
        registerPair(ModBlocks.CHERRY_LOG_VERTICAL_SLAB, ModBlocks.STRIPPED_CHERRY_LOG_VERTICAL_SLAB);
        registerPair(ModBlocks.CHERRY_WOOD_SLAB, ModBlocks.STRIPPED_CHERRY_WOOD_SLAB);
        registerPair(ModBlocks.CHERRY_WOOD_VERTICAL_SLAB, ModBlocks.STRIPPED_CHERRY_WOOD_VERTICAL_SLAB);

        // 4. Mangrove
        registerPair(ModBlocks.MANGROVE_LOG, ModBlocks.STRIPPED_MANGROVE_LOG);
        registerPair(ModBlocks.MANGROVE_WOOD, ModBlocks.STRIPPED_MANGROVE_WOOD);
        registerPair(ModBlocks.MANGROVE_LOG_SLAB, ModBlocks.STRIPPED_MANGROVE_LOG_SLAB);
        registerPair(ModBlocks.MANGROVE_LOG_VERTICAL_SLAB, ModBlocks.STRIPPED_MANGROVE_LOG_VERTICAL_SLAB);

        // 5. Bamboo
        registerPair(ModBlocks.BAMBOO_BLOCK, ModBlocks.STRIPPED_BAMBOO_BLOCK);

        // 6. Vanilla Log Slabs
        registerPair(ModBlocks.OAK_LOG_SLAB, ModBlocks.STRIPPED_OAK_LOG_SLAB);
        registerPair(ModBlocks.SPRUCE_LOG_SLAB, ModBlocks.STRIPPED_SPRUCE_LOG_SLAB);
        registerPair(ModBlocks.BIRCH_LOG_SLAB, ModBlocks.STRIPPED_BIRCH_LOG_SLAB);
        registerPair(ModBlocks.JUNGLE_LOG_SLAB, ModBlocks.STRIPPED_JUNGLE_LOG_SLAB);
        registerPair(ModBlocks.ACACIA_LOG_SLAB, ModBlocks.STRIPPED_ACACIA_LOG_SLAB);
        registerPair(ModBlocks.DARK_OAK_LOG_SLAB, ModBlocks.STRIPPED_DARK_OAK_LOG_SLAB);

        // 7. Vanilla Log Vertical Slabs
        registerPair(ModBlocks.OAK_LOG_VERTICAL_SLAB, ModBlocks.STRIPPED_OAK_LOG_VERTICAL_SLAB);
        registerPair(ModBlocks.SPRUCE_LOG_VERTICAL_SLAB, ModBlocks.STRIPPED_SPRUCE_LOG_VERTICAL_SLAB);
        registerPair(ModBlocks.BIRCH_LOG_VERTICAL_SLAB, ModBlocks.STRIPPED_BIRCH_LOG_VERTICAL_SLAB);
        registerPair(ModBlocks.JUNGLE_LOG_VERTICAL_SLAB, ModBlocks.STRIPPED_JUNGLE_LOG_VERTICAL_SLAB);
        registerPair(ModBlocks.ACACIA_LOG_VERTICAL_SLAB, ModBlocks.STRIPPED_ACACIA_LOG_VERTICAL_SLAB);
        registerPair(ModBlocks.DARK_OAK_LOG_VERTICAL_SLAB, ModBlocks.STRIPPED_DARK_OAK_LOG_VERTICAL_SLAB);
    }

    private static void registerPair(Supplier<Block> unstripped, Supplier<Block> stripped) {
        if (unstripped != null && stripped != null) {
            STRIP_MAP.put(unstripped, stripped);
        }
    }

    public static boolean handleAxeStrip(PlayerInteractEvent.RightClickBlock event) {
        ItemStack held = event.getItemStack();
        if (!(held.getItem() instanceof AxeItem)) return false;

        init();
        Level level = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        Player player = event.getPlayer();

        for (Map.Entry<Supplier<Block>, Supplier<Block>> entry : STRIP_MAP.entrySet()) {
            if (entry.getKey().get() == block) {
                Block targetBlock = entry.getValue().get();
                BlockState nextState = copyStateProperties(state, targetBlock.defaultBlockState());

                if (!level.isClientSide) {
                    level.setBlock(pos, nextState, 11);
                    level.playSound(null, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (player != null && !player.getAbilities().instabuild) {
                        held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));
                    }
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                return true;
            }
        }
        return false;
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
}
