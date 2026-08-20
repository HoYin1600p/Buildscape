package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.PillarBlock;
import com.kingodogo.buildscape.block.AshenKingPillarBlock;
import com.kingodogo.buildscape.entity.WanderingHomemakerEntity;
import com.kingodogo.buildscape.entity.FestiveWanderingHomemakerEntity;
import com.kingodogo.buildscape.item.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles custom advancement triggers, progress counters, and reward events.
 * 
 * TODO: Physical Trophy blocks/items to be awarded once registered in Buildscape.
 * TODO: Special Tools/Templates (e.g. Golden Shears) to be awarded once registered in Buildscape.
 */
@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AdvancementEvents {

    public static boolean grant(ServerPlayer player, String id) {
        if (player == null || player.getServer() == null) return false;
        Advancement adv = player.getServer().getAdvancements().getAdvancement(new ResourceLocation("buildscape", id));
        if (adv != null) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(adv);
            if (!progress.isDone()) {
                for (String criterion : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(adv, criterion);
                }
                return true;
            }
        }
        return false;
    }

    public static void giveItemReward(ServerPlayer player, Item item, int count) {
        ItemStack stack = new ItemStack(item, count);
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getPlayer() instanceof ServerPlayer serverPlayer)) return;

        if (event.getTarget() instanceof WanderingHomemakerEntity) {
            grant(serverPlayer, "the_homemaker_cometh");
        } else if (event.getTarget() instanceof FestiveWanderingHomemakerEntity) {
            grant(serverPlayer, "its_beginning_to_look_a_lot_like_christmas");
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getPlayer() instanceof ServerPlayer serverPlayer)) return;

        BlockState state = event.getWorld().getBlockState(event.getPos());
        Block block = state.getBlock();

        if (block instanceof PillarBlock || block instanceof AshenKingPillarBlock) {
            CompoundTag tag = serverPlayer.getPersistentData();
            int count = tag.getInt("BS_PillarInteractions") + 1;
            tag.putInt("BS_PillarInteractions", count);

            // Ashenking Pillar Item Rewards
            if (count >= 10 && grant(serverPlayer, "put_it_on_display")) {
                giveItemReward(serverPlayer, ModItems.ASHENKING_GOLD_PILLAR.get(), 1);
                // TODO: Award Gold Trophy block when registered
            }
            if (count >= 69 && grant(serverPlayer, "columnist")) {
                giveItemReward(serverPlayer, ModItems.ASHENKING_EMERALD_PILLAR.get(), 1);
                // TODO: Award Emerald Trophy block when registered
            }
            if (count >= 100 && grant(serverPlayer, "art_collector")) {
                giveItemReward(serverPlayer, ModItems.ASHENKING_DIAMOND_PILLAR.get(), 1);
                // TODO: Award Diamond Trophy block when registered
            }
            if (count >= 1000 && grant(serverPlayer, "buildscape_museum")) {
                giveItemReward(serverPlayer, ModItems.ASHENKING_NETHERITE_PILLAR.get(), 1);
                // TODO: Award Netherite Trophy block when registered
            }
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;

        Level level = (Level) event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = event.getPlacedBlock();
        Block block = state.getBlock();
        ResourceLocation reg = block.getRegistryName();

        if (reg == null) return;
        String modId = reg.getNamespace();
        String path = reg.getPath();

        CompoundTag tag = serverPlayer.getPersistentData();

        // Check if placed block belongs to Buildscape
        if ("buildscape".equals(modId)) {
            int placedCount = tag.getInt("BS_PlacedBlocks") + 1;
            tag.putInt("BS_PlacedBlocks", placedCount);

            // TODO: Builder Trophy rewards (Tiers I, II, III) when registered
            if (placedCount >= 100) grant(serverPlayer, "one_more_block");
            if (placedCount >= 1000) grant(serverPlayer, "okay_one_more");
            if (placedCount >= 10000) grant(serverPlayer, "actually_one_last");
        }

        // Stained Brick
        if (path.contains("stained_brick")) {
            grant(serverPlayer, "brick_by_brick");
        }

        // Hollow Logs
        if (path.startsWith("hollow_")) {
            int hollowCount = tag.getInt("BS_HollowLogsPlaced") + 1;
            tag.putInt("BS_HollowLogsPlaced", hollowCount);
            if (hollowCount >= 10) grant(serverPlayer, "i_vented");
        }

        // Icicles
        if (path.contains("icicle")) {
            int icicleCount = tag.getInt("BS_IciclesPlaced") + 1;
            tag.putInt("BS_IciclesPlaced", icicleCount);
            if (icicleCount >= 10) grant(serverPlayer, "chill_out");
        }

        // Ornaments
        if (path.contains("ornament")) {
            int ornCount = tag.getInt("BS_OrnamentsPlaced") + 1;
            tag.putInt("BS_OrnamentsPlaced", ornCount);
            if (ornCount >= 100 && grant(serverPlayer, "ornamental")) {
                giveItemReward(serverPlayer, ModItems.RED_ORNAMENT.get(), 1);
                // TODO: Award Festive Scroll when registered
            }
        }

        // String Lights
        if (path.contains("string_light")) {
            int lightCount = tag.getInt("BS_StringLightsPlaced") + 1;
            tag.putInt("BS_StringLightsPlaced", lightCount);
            if (lightCount >= 100 && grant(serverPlayer, "light_em_up")) {
                giveItemReward(serverPlayer, ModItems.MULTICOLOR_STRING_LIGHT.get(), 1);
                // TODO: Award Prism Light Template when registered
            }
        }

        // Stars
        if (path.contains("_star")) {
            int starCount = tag.getInt("BS_StarsPlaced") + 1;
            tag.putInt("BS_StarsPlaced", starCount);
            if (starCount >= 100 && grant(serverPlayer, "santas_little_helper")) {
                giveItemReward(serverPlayer, ModItems.GLOW_STAR.get(), 1);
                // TODO: Award Starlight Template when registered
            }
        }

        // Snowy Leaves
        if (path.startsWith("snowy_")) {
            int snowCount = tag.getInt("BS_SnowyLeavesPlaced") + 1;
            tag.putInt("BS_SnowyLeavesPlaced", snowCount);
            if (snowCount >= 100 && grant(serverPlayer, "a_white_christmas")) {
                giveItemReward(serverPlayer, ModItems.SNOWY_SPRUCE_LEAVES.get(), 1);
                // TODO: Award Frost Scroll when registered
            }
        }

        // Pillar Placement & Height Tracking
        if (block instanceof PillarBlock || block instanceof AshenKingPillarBlock) {
            checkPillars(serverPlayer, level, pos, block);
        }
    }

    private static void checkPillars(ServerPlayer player, Level level, BlockPos pos, Block pillarBlock) {
        // Check 4 adjacent or stacked pillars of same type
        int sameTypeCount = 0;
        for (BlockPos check : new BlockPos[]{pos.above(), pos.below(), pos.north(), pos.south(), pos.east(), pos.west()}) {
            if (level.getBlockState(check).is(pillarBlock)) {
                sameTypeCount++;
            }
        }
        if (sameTypeCount >= 3) {
            grant(player, "support_system");
        }

        // Check vertical pillar height
        int height = 1;
        BlockPos current = pos.below();
        while (level.getBlockState(current).getBlock() instanceof PillarBlock) {
            height++;
            current = current.below();
        }
        current = pos.above();
        while (level.getBlockState(current).getBlock() instanceof PillarBlock) {
            height++;
            current = current.above();
        }

        if (height >= 50) {
            grant(player, "thats_a_tall_order");
        }

        if (pos.getY() >= level.getMaxBuildHeight() - 1) {
            if (grant(player, "reach_for_the_sky")) {
                // TODO: Award Pillar Master Trophy when registered
            }
        }
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer serverPlayer)) return;

        ItemStack itemStack = event.getCrafting();
        ResourceLocation reg = itemStack.getItem().getRegistryName();
        if (reg == null) return;
        String path = reg.getPath();

        CompoundTag tag = serverPlayer.getPersistentData();

        if (path.contains("jar")) {
            int jars = tag.getInt("BS_JarsCrafted") + itemStack.getCount();
            tag.putInt("BS_JarsCrafted", jars);
            if (jars >= 100 && grant(serverPlayer, "jar_ring_display")) {
                // TODO: Award Glassmaker Trophy when registered
            }
        }

        if (path.contains("festive_stocking") || path.contains("stocking")) {
            int stockings = tag.getInt("BS_StockingsCrafted") + itemStack.getCount();
            tag.putInt("BS_StockingsCrafted", stockings);
            if (stockings >= 365 && grant(serverPlayer, "christmas_every_day")) {
                giveItemReward(serverPlayer, ModItems.FESTIVE_STOCKING.get(), 1);
                // TODO: Award Golden Shears item (Special Tool to craft Golden Stocking) when registered
            }
        }
    }

    public static void onHammerReplace(ServerPlayer player) {
        if (player == null) return;
        CompoundTag tag = player.getPersistentData();
        int count = tag.getInt("BS_HammerReplaced") + 1;
        tag.putInt("BS_HammerReplaced", count);

        grant(player, "fixer_upper");
        if (count >= 1000 && grant(player, "hammer_time")) {
            // TODO: Award Architect Trophy when registered
        }
    }
}
