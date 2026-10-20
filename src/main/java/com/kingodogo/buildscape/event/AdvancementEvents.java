package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.advancement.AdvancementGrants;
import com.kingodogo.buildscape.block.CascadeBlock;
import com.kingodogo.buildscape.block.CascadeBlockNoMist;
import com.kingodogo.buildscape.block.FrostRoseBlock;
import com.kingodogo.buildscape.block.MuffBlock;
import com.kingodogo.buildscape.block.SmokeVentBlock;
import com.kingodogo.buildscape.block.SteelBoltBlock;
import com.kingodogo.buildscape.block.WeatheringBoltBlock;
import com.kingodogo.buildscape.feature.firework.InfinitePhoenixFireworkStarItem;
import com.kingodogo.buildscape.feature.homemaker.FestiveWanderingHomemakerEntity;
import com.kingodogo.buildscape.feature.homemaker.WanderingHomemakerEntity;
import com.kingodogo.buildscape.feature.pillar.AshenKingPillarBlock;
import com.kingodogo.buildscape.feature.pillar.PillarBlock;
import com.kingodogo.buildscape.feature.trophy.Trophies;
import com.kingodogo.buildscape.feature.trophy.TrophyBlockItem;
import com.kingodogo.buildscape.registry.ModBlocks;
import com.kingodogo.buildscape.registry.ModItems;
import com.kingodogo.buildscape.registry.ModStats;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AdvancementEvents {

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        CompoundTag original = event.getOriginal().getPersistentData();
        CompoundTag restored = event.getPlayer().getPersistentData();
        for (String key : original.getAllKeys()) {
            if (key.startsWith("BS_Base_") || key.startsWith("BS_PillarPlaced_")) {
                restored.put(key, original.get(key).copy());
            }
        }
    }

    public static void giveItemReward(ServerPlayer player, Item item, int count) {
        if (player == null || item == null || count <= 0) {
            return;
        }
        ItemStack stack = new ItemStack(item, count);
        if (item instanceof TrophyBlockItem
                || item == ModItems.GOLDEN_JAR.get() || item == ModItems.FESTIVE_STAR.get()
                || item == ModItems.GOLDEN_JAR_PATTERN.get() || item == ModItems.FESTIVE_STAR_PATTERN.get()
                || item == ModItems.STRINGLIGHT_FRAME_PATTERN.get()) {
            CompoundTag tag = stack.getOrCreateTag();
            tag.putString("ObtainedBy", player.getScoreboardName());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            tag.putString("ObtainedOn", LocalDateTime.now().format(formatter));
        }
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    @SubscribeEvent
    public static void onAdvancementEarned(AdvancementEvent event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
            if (event.getAdvancement() != null) {
                ResourceLocation id = event.getAdvancement().getId();
                if ("buildscape".equals(id.getNamespace())) {
                    String path = id.getPath();

                    Item trophyItem = Trophies.getRewardForAdvancement(path);
                    if (trophyItem != null) {
                        giveItemReward(serverPlayer, trophyItem, 1);
                    }

                    switch (path) {
                        case "put_it_on_display" -> giveItemReward(serverPlayer, ModBlocks.ASHENKING_GOLD_PILLAR.get().asItem(), 1);
                        case "columnist" -> giveItemReward(serverPlayer, ModBlocks.ASHENKING_EMERALD_PILLAR.get().asItem(), 1);
                        case "art_collector" -> giveItemReward(serverPlayer, ModBlocks.ASHENKING_DIAMOND_PILLAR.get().asItem(), 1);
                        case "buildscape_museum" -> giveItemReward(serverPlayer, ModBlocks.ASHENKING_NETHERITE_PILLAR.get().asItem(), 1);
                        case "ornamental" -> giveItemReward(serverPlayer, ModItems.BIG_ORNAMENT_TEMPLATE.get(), 1);
                        case "jar_ring_display" -> giveItemReward(serverPlayer, ModItems.GOLDEN_JAR_PATTERN.get(), 1);
                        case "christmas_every_day" -> giveItemReward(serverPlayer, ModItems.MUSIC_DISC_SNOWFALL.get(), 1);
                        case "light_em_up" -> giveItemReward(serverPlayer, ModItems.STRINGLIGHT_FRAME_PATTERN.get(), 1);
                        case "santas_little_helper" -> giveItemReward(serverPlayer, ModItems.FESTIVE_STAR_PATTERN.get(), 1);
                        case "grand_celebration" -> {
                            ItemStack reward = InfinitePhoenixFireworkStarItem.createDefaultStack();
                            if (!serverPlayer.getInventory().add(reward)) {
                                serverPlayer.drop(reward, false);
                            }
                        }
                        case "celebrate_in_style" -> giveItemReward(serverPlayer, ModItems.MUSIC_DISC_CELEBRATION.get(), 1);
                        case "a_very_buildscape_christmas" -> giveItemReward(serverPlayer, ModItems.FESTIVE_GLINT_SHARD.get(), 1);
                        case "a_full_buildscape_cube" -> giveItemReward(serverPlayer, ModItems.MUSIC_DISC_BUILDER.get(), 1);
                        default -> {}
                    }

                    if (!"a_full_buildscape_cube".equals(path) && !path.startsWith("recipes/")) {
                        AdvancementGrants.checkFullCubeAdvancement(serverPlayer);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
            AdvancementGrants.checkFullCubeAdvancement(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getPlayer() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (event.getTarget() instanceof WanderingHomemakerEntity) {
            AdvancementGrants.grant(serverPlayer, "the_homemaker_cometh");
        } else if (event.getTarget() instanceof FestiveWanderingHomemakerEntity) {
            AdvancementGrants.grant(serverPlayer, "its_beginning_to_look_a_lot_like_christmas");
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        Level level = (Level) event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = event.getPlacedBlock();
        Block block = state.getBlock();
        ResourceLocation reg = block.getRegistryName();

        if (reg == null) {
            return;
        }
        String modId = reg.getNamespace();
        String path = reg.getPath();

        CompoundTag tag = serverPlayer.getPersistentData();

        if ("buildscape".equals(modId)) {
            serverPlayer.awardStat(ModStats.BLOCKS_PLACED);
            int placedCount = serverPlayer.getStats().getValue(Stats.CUSTOM, ModStats.BLOCKS_PLACED);

            if (AdvancementGrants.checkRelativeMilestone(serverPlayer, "one_more_block", 100, placedCount)) {
            } else if (AdvancementGrants.checkRelativeMilestone(serverPlayer, "okay_one_more", 1000, placedCount)) {
            } else if (AdvancementGrants.checkRelativeMilestone(serverPlayer, "actually_one_last", 10000, placedCount)) {
            } else if (AdvancementGrants.checkRelativeMilestone(serverPlayer, "one_last_one_i_promise", 100000, placedCount)) {
            }
        }

        if (path.contains("stained_brick")) {
            AdvancementGrants.grant(serverPlayer, "brick_by_brick");
        }

        if (path.startsWith("hollow_")) {
            serverPlayer.awardStat(ModStats.HOLLOW_LOGS_PLACED);
            int hollowCount = serverPlayer.getStats().getValue(Stats.CUSTOM, ModStats.HOLLOW_LOGS_PLACED);
            if (AdvancementGrants.checkRelativeMilestone(serverPlayer, "i_vented", 10, hollowCount)) {
            }
        }

        if (path.contains("icicle")) {
            serverPlayer.awardStat(ModStats.ICICLES_PLACED);
            int icicleCount = serverPlayer.getStats().getValue(Stats.CUSTOM, ModStats.ICICLES_PLACED);
            if (AdvancementGrants.checkRelativeMilestone(serverPlayer, "chill_out", 10, icicleCount)) {
            }
        }

        if (path.contains("ornament")) {
            serverPlayer.awardStat(ModStats.ORNAMENTS_PLACED);
            int ornCount = serverPlayer.getStats().getValue(Stats.CUSTOM, ModStats.ORNAMENTS_PLACED);
            if (AdvancementGrants.checkRelativeMilestone(serverPlayer, "ornamental", 100, ornCount)) {
            }
        }

        if (path.contains("string_light")) {
            serverPlayer.awardStat(ModStats.STRING_LIGHTS_PLACED);
            int lightCount = serverPlayer.getStats().getValue(Stats.CUSTOM, ModStats.STRING_LIGHTS_PLACED);
            if (AdvancementGrants.checkRelativeMilestone(serverPlayer, "light_em_up", 100, lightCount)) {
            }
        }

        if (path.contains("_star")) {
            serverPlayer.awardStat(ModStats.STARS_PLACED);
            int starCount = serverPlayer.getStats().getValue(Stats.CUSTOM, ModStats.STARS_PLACED);
            if (AdvancementGrants.checkRelativeMilestone(serverPlayer, "santas_little_helper", 100, starCount)) {
            }
        }

        if (path.startsWith("snowy_")) {
            serverPlayer.awardStat(ModStats.SNOWY_LEAVES_PLACED);
            int snowCount = serverPlayer.getStats().getValue(Stats.CUSTOM, ModStats.SNOWY_LEAVES_PLACED);
            if (AdvancementGrants.checkRelativeMilestone(serverPlayer, "a_white_christmas", 100, snowCount)) {
            }
        }

        if (block instanceof FrostRoseBlock || path.equals("frost_rose")) {
            serverPlayer.awardStat(ModStats.FROSTY_ROSES_PLACED);
            int radius = 3;
            int roseCount = 0;
            for (BlockPos p : BlockPos.betweenClosed(pos.offset(-radius, -radius, -radius), pos.offset(radius, radius, radius))) {
                if (level.getBlockState(p).getBlock() instanceof FrostRoseBlock) {
                    roseCount++;
                }
            }
            if (roseCount >= 5) {
                AdvancementGrants.grant(serverPlayer, "let_it_snow");
            }
        }

        if (block instanceof CascadeBlock || block instanceof CascadeBlockNoMist || path.contains("cascade_block")) {
            serverPlayer.awardStat(ModStats.CASCADE_BLOCKS_PLACED);
            AdvancementGrants.grant(serverPlayer, "let_it_cascade");
        }

        if (block instanceof SmokeVentBlock || path.equals("smoke_vent")) {
            serverPlayer.awardStat(ModStats.SMOKE_VENTS_PLACED);
            int ventCount = serverPlayer.getStats().getValue(Stats.CUSTOM, ModStats.SMOKE_VENTS_PLACED);
            if (AdvancementGrants.checkRelativeMilestone(serverPlayer, "let_it_out", 5, ventCount)) {
            }
        }

        if (block instanceof MuffBlock || path.equals("muff_block")) {
            if (level.hasNeighborSignal(pos)) {
                serverPlayer.awardStat(ModStats.MUFF_BLOCKS_ACTIVATED);
                AdvancementGrants.grant(serverPlayer, "can_you_hear_me_now");
            }
        }

        if (path.endsWith("_bolts") || block instanceof SteelBoltBlock || block instanceof WeatheringBoltBlock) {
            serverPlayer.awardStat(ModStats.BOLTS_PLACED);
            int boltCount = serverPlayer.getStats().getValue(Stats.CUSTOM, ModStats.BOLTS_PLACED);
            if (AdvancementGrants.checkRelativeMilestone(serverPlayer, "are_you_nuts", 20, boltCount)) {
            }
        }

        if (block instanceof PillarBlock || block instanceof AshenKingPillarBlock) {
            checkPillars(serverPlayer, level, pos, block);
        }
    }

    private static void checkPillars(ServerPlayer player, Level level, BlockPos pos, Block pillarBlock) {
        CompoundTag tag = player.getPersistentData();
        ResourceLocation reg = pillarBlock.getRegistryName();
        if (reg != null) {
            String path = reg.getPath();
            int samePillarCount = tag.getInt("BS_PillarPlaced_" + path) + 1;
            tag.putInt("BS_PillarPlaced_" + path, samePillarCount);

            if (samePillarCount >= 4) {
                AdvancementGrants.grant(player, "support_system");
            }
        }

        int height = 1;
        BlockPos current = pos.below();
        while (level.getBlockState(current).getBlock() instanceof PillarBlock || level.getBlockState(current).getBlock() instanceof AshenKingPillarBlock) {
            height++;
            current = current.below();
        }
        current = pos.above();
        while (level.getBlockState(current).getBlock() instanceof PillarBlock || level.getBlockState(current).getBlock() instanceof AshenKingPillarBlock) {
            height++;
            current = current.above();
        }

        if (height >= 50) {
            AdvancementGrants.grant(player, "thats_a_tall_order");
        }

        if (pos.getY() >= level.getMaxBuildHeight() - 1) {
            AdvancementGrants.grant(player, "reach_for_the_sky");
        }
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        ItemStack itemStack = event.getCrafting();
        ResourceLocation reg = itemStack.getItem().getRegistryName();
        if (reg == null) {
            return;
        }
        String path = reg.getPath();

        if (path.contains("jar") && !path.contains("pattern")) {
            serverPlayer.awardStat(ModStats.JARS_CRAFTED, itemStack.getCount());
            int jars = serverPlayer.getStats().getValue(Stats.CUSTOM, ModStats.JARS_CRAFTED);
            AdvancementGrants.checkRelativeMilestone(serverPlayer, "jar_ring_display", 100, jars);
        }

        if (path.contains("festive_stocking") || path.contains("stocking")) {
            serverPlayer.awardStat(ModStats.STOCKINGS_CRAFTED, itemStack.getCount());
            int stockings = serverPlayer.getStats().getValue(Stats.CUSTOM, ModStats.STOCKINGS_CRAFTED);
            AdvancementGrants.checkRelativeMilestone(serverPlayer, "christmas_every_day", 365, stockings);
        }
    }
}
