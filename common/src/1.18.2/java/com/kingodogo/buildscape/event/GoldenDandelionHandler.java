package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.kingodogo.buildscape.util.GoldenDandelionGrowth;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Freezes or thaws a baby animal's growth with a golden dandelion, as the reference's entity interaction handler does. */
public final class GoldenDandelionHandler {
    private GoldenDandelionHandler() {}

    public static InteractionResult onEntityInteract(Player player, Level level, InteractionHand hand, Entity target) {
        if (!(target instanceof AgeableMob mob) || target instanceof Monster) return InteractionResult.PASS;
        ItemStack held = player.getItemInHand(hand);
        if (!held.is(Services.PLATFORM.getItem(new CommonId("buildscape", "golden_dandelion")))) return InteractionResult.PASS;
        boolean frozen = GoldenDandelionGrowth.isFrozen(mob);
        if (!GoldenDandelionGrowth.canToggle(mob.isBaby(), frozen)) return InteractionResult.PASS;
        if (!level.isClientSide) {
            if (!frozen) {
                if (!GoldenDandelionGrowth.setFrozen(mob, true)) return InteractionResult.PASS;
                ((ServerLevel) level).sendParticles(ParticleTypes.WAX_OFF,
                        mob.getX(), mob.getY() + mob.getBbHeight() * 0.5D, mob.getZ(), 12, 0.3D, 0.3D, 0.3D, 0.05D);
                level.playSound(null, mob.blockPosition(), SoundEvents.HONEYCOMB_WAX_ON, SoundSource.NEUTRAL, 1.0F, 1.0F);
            } else {
                GoldenDandelionGrowth.setFrozen(mob, false);
                ((ServerLevel) level).sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        mob.getX(), mob.getY() + mob.getBbHeight() * 0.5D, mob.getZ(), 12, 0.3D, 0.3D, 0.3D, 0.05D);
                level.playSound(null, mob.blockPosition(), SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
            if (!player.getAbilities().instabuild) held.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
