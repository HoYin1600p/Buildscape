package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.item.HammerItem;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.network.NetworkEvent;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class HammerHarvestTest {
    @BeforeClass
    public static void bootstrap() {
        new NetworkEvent(() -> null).getListenerList();
        new NetworkEvent.GatherLoginPayloadsEvent(new ArrayList<>(), false).getListenerList();
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    public void tierMappingAndLootToolAgreeOnHarvestEligibility() {
        // Headless bootstrap does not load datapacks. Supply the vanilla tool tags explicitly.
        var pickaxe = BlockTags.MINEABLE_WITH_PICKAXE;
        var iron = BlockTags.NEEDS_IRON_TOOL;
        var diamond = BlockTags.NEEDS_DIAMOND_TOOL;
        var oreTags = Blocks.DIAMOND_ORE.builtInRegistryHolder().tags().toList();
        var obsidianTags = Blocks.OBSIDIAN.builtInRegistryHolder().tags().toList();
        // Headless bootstrap also skips the datapack reload that orders Forge's tiers; without the order
        // TierSortingRegistry accepts every tier for every block. Build the vanilla order, as a reload does.
        invokeTierSorting("recalculateItemTiers");
        assertEquals(List.of(Tiers.WOOD, Tiers.GOLD, Tiers.STONE, Tiers.IRON, Tiers.DIAMOND, Tiers.NETHERITE),
                TierSortingRegistry.getSortedTiers());
        try {
            Blocks.DIAMOND_ORE.builtInRegistryHolder().bindTags(List.of(pickaxe, iron));
            Blocks.OBSIDIAN.builtInRegistryHolder().bindTags(List.of(pickaxe, diamond));
            for (HammerItem.HammerTier tier : HammerItem.HammerTier.values()) {
                assertSame(switch (tier) {
                    case IRON -> Items.IRON_PICKAXE;
                    case DIAMOND -> Items.DIAMOND_PICKAXE;
                    case NETHERITE -> Items.NETHERITE_PICKAXE;
                }, tier.getHarvestToolItem());
                ItemStack held = new ItemStack(tier.getHarvestToolItem());
                ItemStack loot = HammerReplacePacket.createHarvestTool(tier, held);
                for (Block block : List.of(Blocks.DIAMOND_ORE, Blocks.OBSIDIAN, Blocks.DIRT)) {
                    assertEquals(tier + " " + block, loot.isCorrectToolForDrops(block.defaultBlockState()),
                            held.isCorrectToolForDrops(block.defaultBlockState()));
                }
                assertTrue(held.isCorrectToolForDrops(Blocks.DIAMOND_ORE.defaultBlockState()));
                assertTrue(Blocks.DIAMOND_ORE.defaultBlockState().getExpDrop(null, BlockPos.ZERO, 0,
                        EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, held)) > 0);
                held.enchant(Enchantments.SILK_TOUCH, 1);
                assertEquals(0, Blocks.DIAMOND_ORE.defaultBlockState().getExpDrop(null, BlockPos.ZERO, 0,
                        EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, held)));
                assertEquals(tier != HammerItem.HammerTier.IRON,
                        held.isCorrectToolForDrops(Blocks.OBSIDIAN.defaultBlockState()));
            }
        } finally {
            invokeTierSorting("clearTierOrder");
            Blocks.DIAMOND_ORE.builtInRegistryHolder().bindTags(oreTags);
            Blocks.OBSIDIAN.builtInRegistryHolder().bindTags(obsidianTags);
        }
    }

    private static void invokeTierSorting(String name) {
        try {
            if (name.equals("clearTierOrder")) {
                var field = TierSortingRegistry.class.getDeclaredField("sortedTiers");
                field.setAccessible(true);
                ((List<?>) field.get(null)).clear();
                return;
            }
            var method = TierSortingRegistry.class.getDeclaredMethod(name);
            method.setAccessible(true);
            method.invoke(null);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    @Test
    public void lootToolPreservesSilkTouchAndFortuneForEveryTier() {
        for (HammerItem.HammerTier tier : HammerItem.HammerTier.values()) {
            ItemStack held = new ItemStack(Items.STICK);
            assertTrue(EnchantmentHelper.getEnchantments(HammerReplacePacket.createHarvestTool(tier, held)).isEmpty());
            held.enchant(Enchantments.SILK_TOUCH, 1);
            ItemStack silk = HammerReplacePacket.createHarvestTool(tier, held);
            assertEquals(1, EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, silk));
            held.enchant(Enchantments.BLOCK_FORTUNE, 3);
            assertEquals(EnchantmentHelper.getEnchantments(held),
                    EnchantmentHelper.getEnchantments(HammerReplacePacket.createHarvestTool(tier, held)));
        }
    }
}
