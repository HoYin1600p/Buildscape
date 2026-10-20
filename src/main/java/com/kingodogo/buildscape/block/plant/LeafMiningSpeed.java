package com.kingodogo.buildscape.block.plant;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LeafMiningSpeed {
    private static final TagKey<Block> SHEARS_MINEABLE = BlockTags.create(
            new ResourceLocation(BuildScape.MODID, "shears_mineable"));

    private LeafMiningSpeed() {}

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        ItemStack tool = event.getPlayer().getMainHandItem();
        if (!(tool.getItem() instanceof ShearsItem) || !event.getState().is(SHEARS_MINEABLE)) {
            return;
        }

        float toolSpeed = tool.getDestroySpeed(event.getState());
        int efficiency = EnchantmentHelper.getBlockEfficiency(event.getPlayer());
        float bonus = efficiency > 0 ? efficiency * efficiency + 1.0F : 0.0F;
        float originalSpeed = toolSpeed > 1.0F ? toolSpeed + bonus : toolSpeed;
        if (originalSpeed > 0.0F) {
            float leafSpeed = Math.max(15.0F, toolSpeed) + bonus;
            event.setNewSpeed(event.getNewSpeed() * leafSpeed / originalSpeed);
        }
    }
}
