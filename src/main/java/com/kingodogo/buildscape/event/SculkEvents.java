package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.feature.sculk.SculkBreakExperience;
import com.kingodogo.buildscape.feature.sculk.SculkCatalystHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SculkEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onSculkDeath(LivingDeathEvent event) {
        SculkCatalystHandler.onDeath(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onSculkExperienceDrop(LivingExperienceDropEvent event) {
        SculkCatalystHandler.onExperienceDrop(event);
    }

    @SubscribeEvent
    public void onSculkBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() != null && !event.getPlayer().isCreative()) {
            BlockState state = event.getState();
            ResourceLocation key = state.getBlock().getRegistryName();
            if (key == null || !"buildscape".equals(key.getNamespace())) {
                return;
            }
            int experience = SculkBreakExperience.forBlock(key.getPath());
            if (experience < 0) {
                return;
            }
            ItemStack tool = event.getPlayer().getMainHandItem();
            int silkTouch = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool);
            event.setExpToDrop(SculkBreakExperience.withTool(experience, silkTouch));
        }
    }
}
