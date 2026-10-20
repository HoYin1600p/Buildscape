package com.kingodogo.buildscape.block.plant;

import com.kingodogo.buildscape.feature.pipe.HollowLogBlockEntity;
import com.kingodogo.buildscape.registry.blocks.CardboardBlocks;
import com.kingodogo.buildscape.registry.blocks.DecorBlocks;
import com.kingodogo.buildscape.registry.blocks.ShapedStoneBlocks;
import com.kingodogo.buildscape.registry.blocks.ShapedWoodBlocks;
import com.kingodogo.buildscape.registry.blocks.SteelBlocks;
import com.kingodogo.buildscape.registry.blocks.WoodBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
        if (!STRIP_MAP.isEmpty()) {
            return;
        }

        registerPair(ShapedWoodBlocks.POPLAR_LOG, ShapedWoodBlocks.STRIPPED_POPLAR_LOG);
        registerPair(ShapedWoodBlocks.POPLAR_WOOD, ShapedWoodBlocks.STRIPPED_POPLAR_WOOD);
        registerPair(ShapedWoodBlocks.POPLAR_LOG_SLAB, ShapedWoodBlocks.STRIPPED_POPLAR_LOG_SLAB);
        registerPair(ShapedWoodBlocks.POPLAR_LOG_VERTICAL_SLAB, ShapedWoodBlocks.STRIPPED_POPLAR_LOG_VERTICAL_SLAB);

        registerPair(ShapedStoneBlocks.ASHPEN_LOG, ShapedStoneBlocks.STRIPPED_ASHPEN_LOG);
        registerPair(ShapedStoneBlocks.ASHPEN_WOOD, ShapedStoneBlocks.STRIPPED_ASHPEN_WOOD);
        registerPair(ShapedStoneBlocks.ASHPEN_LOG_SLAB, ShapedStoneBlocks.STRIPPED_ASHPEN_LOG_SLAB);
        registerPair(ShapedStoneBlocks.ASHPEN_LOG_VERTICAL_SLAB, ShapedWoodBlocks.STRIPPED_ASHPEN_LOG_VERTICAL_SLAB);
        registerPair(ShapedStoneBlocks.ASHPEN_WOOD_SLAB, ShapedStoneBlocks.STRIPPED_ASHPEN_WOOD_SLAB);
        registerPair(ShapedStoneBlocks.ASHPEN_WOOD_VERTICAL_SLAB, ShapedWoodBlocks.STRIPPED_ASHPEN_WOOD_VERTICAL_SLAB);

        registerPair(ShapedWoodBlocks.PALE_OAK_LOG, ShapedWoodBlocks.STRIPPED_PALE_OAK_LOG);
        registerPair(ShapedWoodBlocks.PALE_OAK_WOOD, ShapedWoodBlocks.STRIPPED_PALE_OAK_WOOD);
        registerPair(ShapedWoodBlocks.PALE_OAK_LOG_SLAB, ShapedWoodBlocks.STRIPPED_PALE_OAK_LOG_SLAB);
        registerPair(ShapedWoodBlocks.PALE_OAK_LOG_VERTICAL_SLAB, ShapedWoodBlocks.STRIPPED_PALE_OAK_LOG_VERTICAL_SLAB);
        registerPair(ShapedWoodBlocks.PALE_OAK_WOOD_SLAB, ShapedWoodBlocks.STRIPPED_PALE_OAK_WOOD_SLAB);
        registerPair(ShapedWoodBlocks.PALE_OAK_WOOD_VERTICAL_SLAB,
                ShapedWoodBlocks.STRIPPED_PALE_OAK_WOOD_VERTICAL_SLAB);

        registerPair(ShapedWoodBlocks.CHERRY_LOG, ShapedWoodBlocks.STRIPPED_CHERRY_LOG);
        registerPair(ShapedWoodBlocks.CHERRY_WOOD, ShapedWoodBlocks.STRIPPED_CHERRY_WOOD);
        registerPair(ShapedWoodBlocks.CHERRY_LOG_SLAB, ShapedWoodBlocks.STRIPPED_CHERRY_LOG_SLAB);
        registerPair(ShapedWoodBlocks.CHERRY_LOG_VERTICAL_SLAB, ShapedWoodBlocks.STRIPPED_CHERRY_LOG_VERTICAL_SLAB);
        registerPair(ShapedWoodBlocks.CHERRY_WOOD_SLAB, ShapedWoodBlocks.STRIPPED_CHERRY_WOOD_SLAB);
        registerPair(ShapedWoodBlocks.CHERRY_WOOD_VERTICAL_SLAB, ShapedWoodBlocks.STRIPPED_CHERRY_WOOD_VERTICAL_SLAB);

        registerPair(DecorBlocks.MANGROVE_LOG, DecorBlocks.STRIPPED_MANGROVE_LOG);
        registerPair(DecorBlocks.MANGROVE_WOOD, DecorBlocks.STRIPPED_MANGROVE_WOOD);
        registerPair(ShapedStoneBlocks.MANGROVE_LOG_SLAB, ShapedStoneBlocks.STRIPPED_MANGROVE_LOG_SLAB);
        registerPair(ShapedStoneBlocks.MANGROVE_LOG_VERTICAL_SLAB,
                ShapedStoneBlocks.STRIPPED_MANGROVE_LOG_VERTICAL_SLAB);

        registerPair(WoodBlocks.BAMBOO_BLOCK, WoodBlocks.STRIPPED_BAMBOO_BLOCK);

        registerPair(ShapedStoneBlocks.OAK_LOG_SLAB, ShapedStoneBlocks.STRIPPED_OAK_LOG_SLAB);
        registerPair(ShapedStoneBlocks.SPRUCE_LOG_SLAB, ShapedStoneBlocks.STRIPPED_SPRUCE_LOG_SLAB);
        registerPair(ShapedStoneBlocks.BIRCH_LOG_SLAB, ShapedStoneBlocks.STRIPPED_BIRCH_LOG_SLAB);
        registerPair(ShapedStoneBlocks.JUNGLE_LOG_SLAB, ShapedStoneBlocks.STRIPPED_JUNGLE_LOG_SLAB);
        registerPair(ShapedStoneBlocks.ACACIA_LOG_SLAB, ShapedStoneBlocks.STRIPPED_ACACIA_LOG_SLAB);
        registerPair(ShapedStoneBlocks.DARK_OAK_LOG_SLAB, ShapedStoneBlocks.STRIPPED_DARK_OAK_LOG_SLAB);

        registerPair(ShapedStoneBlocks.OAK_LOG_VERTICAL_SLAB, ShapedStoneBlocks.STRIPPED_OAK_LOG_VERTICAL_SLAB);
        registerPair(ShapedStoneBlocks.SPRUCE_LOG_VERTICAL_SLAB, ShapedStoneBlocks.STRIPPED_SPRUCE_LOG_VERTICAL_SLAB);
        registerPair(ShapedStoneBlocks.BIRCH_LOG_VERTICAL_SLAB, ShapedStoneBlocks.STRIPPED_BIRCH_LOG_VERTICAL_SLAB);
        registerPair(ShapedStoneBlocks.JUNGLE_LOG_VERTICAL_SLAB, ShapedStoneBlocks.STRIPPED_JUNGLE_LOG_VERTICAL_SLAB);
        registerPair(ShapedStoneBlocks.ACACIA_LOG_VERTICAL_SLAB, ShapedStoneBlocks.STRIPPED_ACACIA_LOG_VERTICAL_SLAB);
        registerPair(ShapedStoneBlocks.DARK_OAK_LOG_VERTICAL_SLAB,
                ShapedStoneBlocks.STRIPPED_DARK_OAK_LOG_VERTICAL_SLAB);

        registerPair(WoodBlocks.OAK_WOOD_WALL, WoodBlocks.STRIPPED_OAK_WOOD_WALL);
        registerPair(WoodBlocks.SPRUCE_WOOD_WALL, WoodBlocks.STRIPPED_SPRUCE_WOOD_WALL);
        registerPair(WoodBlocks.BIRCH_WOOD_WALL, WoodBlocks.STRIPPED_BIRCH_WOOD_WALL);
        registerPair(WoodBlocks.DARK_OAK_WOOD_WALL, WoodBlocks.STRIPPED_DARK_OAK_WOOD_WALL);
        registerPair(WoodBlocks.JUNGLE_WOOD_WALL, WoodBlocks.STRIPPED_JUNGLE_WOOD_WALL);
        registerPair(WoodBlocks.ACACIA_WOOD_WALL, WoodBlocks.STRIPPED_ACACIA_WOOD_WALL);
        registerPair(WoodBlocks.MANGROVE_WOOD_WALL, WoodBlocks.STRIPPED_MANGROVE_WOOD_WALL);
        registerPair(WoodBlocks.BAMBOO_BLOCK_WALL, WoodBlocks.STRIPPED_BAMBOO_BLOCK_WALL);
        registerPair(WoodBlocks.CHERRY_WOOD_WALL, WoodBlocks.STRIPPED_CHERRY_WOOD_WALL);
        registerPair(WoodBlocks.PALE_OAK_WOOD_WALL, WoodBlocks.STRIPPED_PALE_OAK_WOOD_WALL);
        registerPair(WoodBlocks.ASHPEN_WOOD_WALL, WoodBlocks.STRIPPED_ASHPEN_WOOD_WALL);

        registerPair(SteelBlocks.HOLLOW_OAK_LOG, CardboardBlocks.STRIPPED_HOLLOW_OAK_LOG);
        registerPair(CardboardBlocks.HOLLOW_SPRUCE_LOG, CardboardBlocks.STRIPPED_HOLLOW_SPRUCE_LOG);
        registerPair(CardboardBlocks.HOLLOW_BIRCH_LOG, CardboardBlocks.STRIPPED_HOLLOW_BIRCH_LOG);
        registerPair(CardboardBlocks.HOLLOW_JUNGLE_LOG, CardboardBlocks.STRIPPED_HOLLOW_JUNGLE_LOG);
        registerPair(CardboardBlocks.HOLLOW_ACACIA_LOG, CardboardBlocks.STRIPPED_HOLLOW_ACACIA_LOG);
        registerPair(CardboardBlocks.HOLLOW_DARK_OAK_LOG, CardboardBlocks.STRIPPED_HOLLOW_DARK_OAK_LOG);
        registerPair(CardboardBlocks.HOLLOW_CRIMSON_STEM, CardboardBlocks.STRIPPED_HOLLOW_CRIMSON_STEM);
        registerPair(CardboardBlocks.HOLLOW_WARPED_STEM, CardboardBlocks.STRIPPED_HOLLOW_WARPED_STEM);
        registerPair(CardboardBlocks.HOLLOW_MANGROVE_LOG, CardboardBlocks.STRIPPED_HOLLOW_MANGROVE_LOG);
        registerPair(CardboardBlocks.HOLLOW_ASHPEN_LOG, CardboardBlocks.STRIPPED_HOLLOW_ASHPEN_LOG);
        registerPair(CardboardBlocks.HOLLOW_POPLAR_LOG, CardboardBlocks.STRIPPED_HOLLOW_POPLAR_LOG);
        registerPair(CardboardBlocks.HOLLOW_PALE_OAK_LOG, CardboardBlocks.STRIPPED_HOLLOW_PALE_OAK_LOG);
        registerPair(CardboardBlocks.HOLLOW_CHERRY_LOG, CardboardBlocks.STRIPPED_HOLLOW_CHERRY_LOG);
    }

    private static void registerPair(Supplier<Block> unstripped, Supplier<Block> stripped) {
        if (unstripped != null && stripped != null) {
            STRIP_MAP.put(unstripped, stripped);
        }
    }

    public static boolean handleAxeStrip(PlayerInteractEvent.RightClickBlock event) {
        ItemStack held = event.getItemStack();
        if (!(held.getItem() instanceof AxeItem)) {
            return false;
        }

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
                    CompoundTag carriedData = null;
                    if (level.getBlockEntity(pos) instanceof HollowLogBlockEntity oldBe) {
                        carriedData = oldBe.saveWithoutMetadata();
                        oldBe.setGlassCoverNeg(null);
                        oldBe.setGlassCoverPos(null);
                        oldBe.setDecorationState(null);
                    }
                    level.setBlock(pos, nextState, 11);
                    if (carriedData != null && level.getBlockEntity(pos) instanceof HollowLogBlockEntity newBe) {
                        newBe.load(carriedData);
                        newBe.setChanged();
                        newBe.syncToClient();
                    }
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
