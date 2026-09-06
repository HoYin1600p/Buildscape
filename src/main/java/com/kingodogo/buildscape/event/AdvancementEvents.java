package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.PillarBlock;
import com.kingodogo.buildscape.block.AshenKingPillarBlock;
import com.kingodogo.buildscape.block.FrostRoseBlock;
import com.kingodogo.buildscape.block.CascadeBlock;
import com.kingodogo.buildscape.block.CascadeBlockNoMist;
import com.kingodogo.buildscape.block.SmokeVentBlock;
import com.kingodogo.buildscape.block.MuffBlock;
import com.kingodogo.buildscape.block.SteelBoltBlock;
import com.kingodogo.buildscape.block.WeatheringBoltBlock;
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
import com.kingodogo.buildscape.trophy.Trophies;

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
        if (player == null || item == null || count <= 0) return;
        ItemStack stack = new ItemStack(item, count);
        if (item instanceof com.kingodogo.buildscape.trophy.TrophyBlockItem
                || item == ModItems.GOLDEN_JAR.get() || item == ModItems.FESTIVE_STAR.get()
                || item == ModItems.GOLDEN_JAR_PATTERN.get() || item == ModItems.FESTIVE_STAR_PATTERN.get()
                || item == ModItems.STRINGLIGHT_FRAME_PATTERN.get()) {
            net.minecraft.nbt.CompoundTag tag = stack.getOrCreateTag();
            tag.putString("ObtainedBy", player.getScoreboardName());
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            tag.putString("ObtainedOn", java.time.LocalDateTime.now().format(formatter));
        }
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    @SubscribeEvent
    public static void onAdvancementEarned(net.minecraftforge.event.entity.player.AdvancementEvent event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
            if (event.getAdvancement() != null) {
                ResourceLocation id = event.getAdvancement().getId();
                if ("buildscape".equals(id.getNamespace())) {
                    String path = id.getPath();

                    // 1. Award trophy if associated with this advancement
                    Item trophyItem = Trophies.getRewardForAdvancement(path);
                    if (trophyItem != null) {
                        giveItemReward(serverPlayer, trophyItem, 1);
                    }

                    // 2. Award custom item rewards as defined in docs/advancements.md
                    switch (path) {
                        case "put_it_on_display" -> giveItemReward(serverPlayer, com.kingodogo.buildscape.block.ModBlocks.ASHENKING_GOLD_PILLAR.get().asItem(), 1);
                        case "columnist" -> giveItemReward(serverPlayer, com.kingodogo.buildscape.block.ModBlocks.ASHENKING_EMERALD_PILLAR.get().asItem(), 1);
                        case "art_collector" -> giveItemReward(serverPlayer, com.kingodogo.buildscape.block.ModBlocks.ASHENKING_DIAMOND_PILLAR.get().asItem(), 1);
                        case "buildscape_museum" -> giveItemReward(serverPlayer, com.kingodogo.buildscape.block.ModBlocks.ASHENKING_NETHERITE_PILLAR.get().asItem(), 1);
                        case "ornamental" -> giveItemReward(serverPlayer, com.kingodogo.buildscape.block.ModBlocks.RED_ORNAMENT.get().asItem(), 1);
                        case "jar_ring_display" -> giveItemReward(serverPlayer, ModItems.GOLDEN_JAR_PATTERN.get(), 1);
                        case "christmas_every_day" -> giveItemReward(serverPlayer, ModItems.MUSIC_DISC_SNOWFALL.get(), 1);
                        case "light_em_up" -> giveItemReward(serverPlayer, ModItems.STRINGLIGHT_FRAME_PATTERN.get(), 1);
                        case "santas_little_helper" -> giveItemReward(serverPlayer, ModItems.FESTIVE_STAR_PATTERN.get(), 1);
                        case "grand_celebration" -> {
                            ItemStack reward = com.kingodogo.buildscape.item.InfinitePhoenixFireworkStarItem.createDefaultStack();
                            if (!serverPlayer.getInventory().add(reward)) {
                                serverPlayer.drop(reward, false);
                            }
                        }
                        case "celebrate_in_style" -> giveItemReward(serverPlayer, ModItems.MUSIC_DISC_CELEBRATION.get(), 1);
                        case "a_white_christmas" -> giveItemReward(serverPlayer, ModItems.SNOWY_SPRUCE_LEAVES.get(), 1);
                        case "a_very_buildscape_christmas" -> giveItemReward(serverPlayer, ModItems.FESTIVE_GLINT_SHARD.get(), 1);
                        case "a_full_buildscape_cube" -> giveItemReward(serverPlayer, ModItems.MUSIC_DISC_BUILDER.get(), 1);
                        default -> {}
                    }

                    // 3. Update "A Full Buildscape Cube" progress
                    if (!"a_full_buildscape_cube".equals(path) && !path.startsWith("recipes/")) {
                        checkFullCubeAdvancement(serverPlayer);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
            checkFullCubeAdvancement(serverPlayer);
        }
    }

    public static void checkFullCubeAdvancement(ServerPlayer serverPlayer) {
        if (serverPlayer == null || serverPlayer.getServer() == null) return;
        Advancement cubeAdv = serverPlayer.getServer().getAdvancements().getAdvancement(new ResourceLocation("buildscape", "a_full_buildscape_cube"));
        if (cubeAdv == null) return;
        AdvancementProgress cubeProgress = serverPlayer.getAdvancements().getOrStartProgress(cubeAdv);
        if (cubeProgress.isDone()) return;

        for (String criterion : cubeAdv.getCriteria().keySet()) {
            net.minecraft.advancements.CriterionProgress cp = cubeProgress.getCriterion(criterion);
            if (cp != null && cp.isDone()) continue;

            Advancement reqAdv = serverPlayer.getServer().getAdvancements().getAdvancement(new ResourceLocation("buildscape", criterion));
            if (reqAdv != null) {
                AdvancementProgress reqProgress = serverPlayer.getAdvancements().getOrStartProgress(reqAdv);
                if (reqProgress.isDone()) {
                    serverPlayer.getAdvancements().award(cubeAdv, criterion);
                }
            }
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

    private static boolean checkRelativeMilestone(ServerPlayer player, String advId, int targetCount, int currentStat) {
        Advancement adv = player.getServer().getAdvancements().getAdvancement(new ResourceLocation("buildscape", advId));
        if (adv == null) return false;
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(adv);
        if (progress.isDone()) return false;

        CompoundTag tag = player.getPersistentData();
        String baseKey = "BS_Base_" + advId;

        if (!tag.contains(baseKey)) {
            tag.putInt(baseKey, Math.max(0, currentStat - 1));
        }

        int baseStat = tag.getInt(baseKey);
        int delta = currentStat - baseStat;

        if (delta > targetCount) {
            tag.putInt(baseKey, Math.max(0, currentStat - 1));
            baseStat = tag.getInt(baseKey);
            delta = currentStat - baseStat;
        }

        if (delta >= targetCount) {
            boolean granted = grant(player, advId);
            if (granted) {
                tag.remove(baseKey);
            }
            return granted;
        }
        return false;
    }

    public static void onPillarItemInserted(ServerPlayer serverPlayer) {
        if (serverPlayer == null) return;

        serverPlayer.awardStat(com.kingodogo.buildscape.stat.ModStats.INTERACT_WITH_PILLAR);
        int count = serverPlayer.getStats().getValue(net.minecraft.stats.Stats.CUSTOM, com.kingodogo.buildscape.stat.ModStats.INTERACT_WITH_PILLAR);

        if (checkRelativeMilestone(serverPlayer, "put_it_on_display", 10, count)) {
            return;
        }
        if (checkRelativeMilestone(serverPlayer, "columnist", 69, count)) {
            return;
        }
        if (checkRelativeMilestone(serverPlayer, "art_collector", 100, count)) {
            return;
        }
        if (checkRelativeMilestone(serverPlayer, "buildscape_museum", 1000, count)) {
            return;
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

        if ("buildscape".equals(modId)) {
            serverPlayer.awardStat(com.kingodogo.buildscape.stat.ModStats.BLOCKS_PLACED);
            int placedCount = serverPlayer.getStats().getValue(net.minecraft.stats.Stats.CUSTOM, com.kingodogo.buildscape.stat.ModStats.BLOCKS_PLACED);

            if (checkRelativeMilestone(serverPlayer, "one_more_block", 100, placedCount)) {
            } else if (checkRelativeMilestone(serverPlayer, "okay_one_more", 1000, placedCount)) {
            } else if (checkRelativeMilestone(serverPlayer, "actually_one_last", 10000, placedCount)) {
            } else if (checkRelativeMilestone(serverPlayer, "one_last_one_i_promise", 100000, placedCount)) {
            }
        }

        if (path.contains("stained_brick")) {
            grant(serverPlayer, "brick_by_brick");
        }

        if (path.startsWith("hollow_")) {
            serverPlayer.awardStat(com.kingodogo.buildscape.stat.ModStats.HOLLOW_LOGS_PLACED);
            int hollowCount = serverPlayer.getStats().getValue(net.minecraft.stats.Stats.CUSTOM, com.kingodogo.buildscape.stat.ModStats.HOLLOW_LOGS_PLACED);
            if (checkRelativeMilestone(serverPlayer, "i_vented", 10, hollowCount)) {
            }
        }

        if (path.contains("icicle")) {
            serverPlayer.awardStat(com.kingodogo.buildscape.stat.ModStats.ICICLES_PLACED);
            int icicleCount = serverPlayer.getStats().getValue(net.minecraft.stats.Stats.CUSTOM, com.kingodogo.buildscape.stat.ModStats.ICICLES_PLACED);
            if (checkRelativeMilestone(serverPlayer, "chill_out", 10, icicleCount)) {
            }
        }

        if (path.contains("ornament")) {
            serverPlayer.awardStat(com.kingodogo.buildscape.stat.ModStats.ORNAMENTS_PLACED);
            int ornCount = serverPlayer.getStats().getValue(net.minecraft.stats.Stats.CUSTOM, com.kingodogo.buildscape.stat.ModStats.ORNAMENTS_PLACED);
            if (checkRelativeMilestone(serverPlayer, "ornamental", 100, ornCount)) {
            }
        }

        if (path.contains("string_light")) {
            serverPlayer.awardStat(com.kingodogo.buildscape.stat.ModStats.STRING_LIGHTS_PLACED);
            int lightCount = serverPlayer.getStats().getValue(net.minecraft.stats.Stats.CUSTOM, com.kingodogo.buildscape.stat.ModStats.STRING_LIGHTS_PLACED);
            if (checkRelativeMilestone(serverPlayer, "light_em_up", 100, lightCount)) {
            }
        }

        if (path.contains("_star")) {
            serverPlayer.awardStat(com.kingodogo.buildscape.stat.ModStats.STARS_PLACED);
            int starCount = serverPlayer.getStats().getValue(net.minecraft.stats.Stats.CUSTOM, com.kingodogo.buildscape.stat.ModStats.STARS_PLACED);
            if (checkRelativeMilestone(serverPlayer, "santas_little_helper", 100, starCount)) {
            }
        }

        if (path.startsWith("snowy_")) {
            serverPlayer.awardStat(com.kingodogo.buildscape.stat.ModStats.SNOWY_LEAVES_PLACED);
            int snowCount = serverPlayer.getStats().getValue(net.minecraft.stats.Stats.CUSTOM, com.kingodogo.buildscape.stat.ModStats.SNOWY_LEAVES_PLACED);
            if (checkRelativeMilestone(serverPlayer, "a_white_christmas", 100, snowCount)) {
            }
        }

        if (block instanceof FrostRoseBlock || path.equals("frost_rose")) {
            serverPlayer.awardStat(com.kingodogo.buildscape.stat.ModStats.FROSTY_ROSES_PLACED);
            int radius = 3;
            int roseCount = 0;
            for (BlockPos p : BlockPos.betweenClosed(pos.offset(-radius, -radius, -radius), pos.offset(radius, radius, radius))) {
                if (level.getBlockState(p).getBlock() instanceof FrostRoseBlock) {
                    roseCount++;
                }
            }
            if (roseCount >= 5) {
                grant(serverPlayer, "let_it_snow");
            }
        }

        if (block instanceof CascadeBlock || block instanceof CascadeBlockNoMist || path.contains("cascade_block")) {
            serverPlayer.awardStat(com.kingodogo.buildscape.stat.ModStats.CASCADE_BLOCKS_PLACED);
            grant(serverPlayer, "let_it_cascade");
        }

        if (block instanceof SmokeVentBlock || path.equals("smoke_vent")) {
            serverPlayer.awardStat(com.kingodogo.buildscape.stat.ModStats.SMOKE_VENTS_PLACED);
            int ventCount = serverPlayer.getStats().getValue(net.minecraft.stats.Stats.CUSTOM, com.kingodogo.buildscape.stat.ModStats.SMOKE_VENTS_PLACED);
            if (checkRelativeMilestone(serverPlayer, "let_it_out", 5, ventCount)) {
            }
        }

        if (block instanceof MuffBlock || path.equals("muff_block")) {
            if (level.hasNeighborSignal(pos)) {
                serverPlayer.awardStat(com.kingodogo.buildscape.stat.ModStats.MUFF_BLOCKS_ACTIVATED);
                grant(serverPlayer, "can_you_hear_me_now");
            }
        }

        if (path.endsWith("_bolts") || block instanceof SteelBoltBlock || block instanceof WeatheringBoltBlock) {
            serverPlayer.awardStat(com.kingodogo.buildscape.stat.ModStats.BOLTS_PLACED);
            int boltCount = serverPlayer.getStats().getValue(net.minecraft.stats.Stats.CUSTOM, com.kingodogo.buildscape.stat.ModStats.BOLTS_PLACED);
            if (checkRelativeMilestone(serverPlayer, "are_you_nuts", 20, boltCount)) {
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
                grant(player, "support_system");
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
            grant(player, "thats_a_tall_order");
        }

        if (pos.getY() >= level.getMaxBuildHeight() - 1) {
            grant(player, "reach_for_the_sky");
        }
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer serverPlayer)) return;

        ItemStack itemStack = event.getCrafting();
        ResourceLocation reg = itemStack.getItem().getRegistryName();
        if (reg == null) return;
        String path = reg.getPath();

        if (path.contains("jar") && !path.contains("pattern")) {
            serverPlayer.awardStat(com.kingodogo.buildscape.stat.ModStats.JARS_CRAFTED, itemStack.getCount());
            int jars = serverPlayer.getStats().getValue(net.minecraft.stats.Stats.CUSTOM, com.kingodogo.buildscape.stat.ModStats.JARS_CRAFTED);
            checkRelativeMilestone(serverPlayer, "jar_ring_display", 100, jars);
        }

        if (path.contains("festive_stocking") || path.contains("stocking")) {
            serverPlayer.awardStat(com.kingodogo.buildscape.stat.ModStats.STOCKINGS_CRAFTED, itemStack.getCount());
            int stockings = serverPlayer.getStats().getValue(net.minecraft.stats.Stats.CUSTOM, com.kingodogo.buildscape.stat.ModStats.STOCKINGS_CRAFTED);
            checkRelativeMilestone(serverPlayer, "christmas_every_day", 365, stockings);
        }
    }

    public static void onHammerReplace(ServerPlayer player) {
        if (player == null) return;
        player.awardStat(com.kingodogo.buildscape.stat.ModStats.HAMMER_USED);
        int count = player.getStats().getValue(net.minecraft.stats.Stats.CUSTOM, com.kingodogo.buildscape.stat.ModStats.HAMMER_USED);

        grant(player, "fixer_upper");
        checkRelativeMilestone(player, "hammer_time", 1000, count);
    }

    public static void onConfettiUsed(ServerPlayer player) {
        if (player == null) return;
        player.awardStat(com.kingodogo.buildscape.stat.ModStats.CONFETTI_USED);
        int count = player.getStats().getValue(net.minecraft.stats.Stats.CUSTOM, com.kingodogo.buildscape.stat.ModStats.CONFETTI_USED);
        checkRelativeMilestone(player, "celebrate_in_style", 15, count);
    }
}
