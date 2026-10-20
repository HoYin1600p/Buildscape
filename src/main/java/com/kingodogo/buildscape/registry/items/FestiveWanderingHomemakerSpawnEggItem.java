package com.kingodogo.buildscape.registry.items;

import com.kingodogo.buildscape.feature.homemaker.HomemakerCooldownTracker;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;

public class FestiveWanderingHomemakerSpawnEggItem extends ForgeSpawnEggItem {
    public FestiveWanderingHomemakerSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type,
            int backgroundColor, int highlightColor, Item.Properties properties) {
        super(type, backgroundColor, highlightColor, properties);
    }

    public int getColor(int tintIndex) {
        return -1;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(
                new TranslatableComponent("tooltip.buildscape.festive_wandering_homemaker_spawn_egg.spawn_info"));
        if (level != null) {
            long currentTime = System.currentTimeMillis();
            long endTime = HomemakerCooldownTracker.getCooldownEndTime();
            if (endTime > currentTime) {
                long remainingMs = endTime - currentTime;
                long totalSeconds = remainingMs / 1000;
                long minutes = totalSeconds / 60;
                long seconds = totalSeconds % 60;
                tooltip.add(new TranslatableComponent(
                        "tooltip.buildscape.wandering_homemaker_spawn_egg.cooldown_active", minutes, seconds)
                        .withStyle(ChatFormatting.RED));
            } else {
                tooltip.add(
                        new TranslatableComponent("tooltip.buildscape.wandering_homemaker_spawn_egg.cooldown_ready")
                                .withStyle(ChatFormatting.GREEN));
            }
        }
    }
}
