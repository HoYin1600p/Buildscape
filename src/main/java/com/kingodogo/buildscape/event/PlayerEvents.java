package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.registry.ModFluids;
import com.kingodogo.buildscape.registry.ModItems;
import com.kingodogo.buildscape.util.GoldenDandelionGrowth;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerEvents {

    @SubscribeEvent
    public void onItemCrafted(
            PlayerEvent.ItemCraftedEvent event) {
        ItemStack crafted = event.getCrafting();

        if (crafted.getItem() == Items.SUSPICIOUS_STEW) {
            boolean hasFrostRose = false;

            Container inventory = event.getInventory();
            if (inventory instanceof CraftingContainer container) {

                for (int i = 0; i < container.getContainerSize(); i++) {
                    ItemStack stack = container.getItem(i);
                    if (stack.getItem() == ModItems.FROST_ROSE.get()) {
                        hasFrostRose = true;
                        break;
                    }
                }
            }

            if (hasFrostRose) {
                CompoundTag nbt = crafted.getOrCreateTag();
                nbt.putInt(NbtKeys.FROST_ROSE_STEW, 1);
            }
        }
    }

    @SubscribeEvent
    public void onItemUseFinish(
            LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack itemStack = event.getItem();

        if (itemStack.getItem() == Items.SUSPICIOUS_STEW) {
            CompoundTag nbt = itemStack.getTag();
            if (nbt != null
                    && nbt.contains(NbtKeys.FROST_ROSE_STEW)
                    && nbt.getInt(NbtKeys.FROST_ROSE_STEW) == 1) {
                if (!player.level.isClientSide) {
                    CompoundTag playerData = player.getPersistentData();
                    playerData.putInt(NbtKeys.FROST_ROSE_STEW_DAMAGE_TICKS, 120);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(
            TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Player player = event.player;
        if (player.level.isClientSide) {
            return;
        }

        CompoundTag playerData = player.getPersistentData();
        if (playerData.contains(NbtKeys.FROST_ROSE_STEW_DAMAGE_TICKS)) {
            int ticksRemaining = playerData.getInt(NbtKeys.FROST_ROSE_STEW_DAMAGE_TICKS);

            if (ticksRemaining > 0) {
                if (ticksRemaining % 20 == 0 && player.isAlive()) {
                    DamageSource freezeDamage = DamageSource.GENERIC;
                    player.hurt(freezeDamage, 1.0F);

                    Level level = player.level;
                    BlockPos playerPos = player.blockPosition();
                    level.playSound(
                            null,
                            playerPos,
                            SoundEvents.POWDER_SNOW_STEP,
                            SoundSource.PLAYERS,
                            0.5f,
                            1.0f);

                    if (player.canFreeze()) {
                        player.setTicksFrozen(Math.min(player.getTicksFrozen() + 140, 300));
                    }
                }

                ticksRemaining--;
                if (ticksRemaining > 0) {
                    playerData.putInt(NbtKeys.FROST_ROSE_STEW_DAMAGE_TICKS, ticksRemaining);
                } else {
                    playerData.remove(NbtKeys.FROST_ROSE_STEW_DAMAGE_TICKS);
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof AgeableMob mob
                && !(event.getTarget() instanceof Monster)) {
            ItemStack held = event.getItemStack();
            if (held.is(ModItems.GOLDEN_DANDELION.get())) {
                boolean isFrozen = GoldenDandelionGrowth.isFrozen(mob);
                if (!GoldenDandelionGrowth.canToggle(mob.isBaby(), isFrozen)) {
                    return;
                }
                Level level = event.getWorld();
                if (!level.isClientSide) {
                    if (!isFrozen) {
                        if (!GoldenDandelionGrowth.setFrozen(mob, true)) {
                            return;
                        }
                        ((ServerLevel) level).sendParticles(
                                ParticleTypes.WAX_OFF,
                                mob.getX(), mob.getY() + mob.getBbHeight() * 0.5, mob.getZ(),
                                12, 0.3, 0.3, 0.3, 0.05);
                        level.playSound(null, mob.blockPosition(),
                                SoundEvents.HONEYCOMB_WAX_ON,
                                SoundSource.NEUTRAL, 1.0F, 1.0F);
                    } else {
                        GoldenDandelionGrowth.setFrozen(mob, false);
                        ((ServerLevel) level).sendParticles(
                                ParticleTypes.HAPPY_VILLAGER,
                                mob.getX(), mob.getY() + mob.getBbHeight() * 0.5, mob.getZ(),
                                12, 0.3, 0.3, 0.3, 0.05);
                        level.playSound(null, mob.blockPosition(),
                                SoundEvents.VILLAGER_YES,
                                SoundSource.NEUTRAL, 1.0F, 1.0F);
                    }
                    if (!event.getPlayer().getAbilities().instabuild) {
                        held.shrink(1);
                    }
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        LivingEntity living = event.getEntityLiving();
        FluidState fluid = living.level.getFluidState(living.blockPosition());
        if (ModFluids.isExperience(fluid.getType())) {
            if (living.isSwimming() || living.isInWater()) {
                Vec3 delta = living.getDeltaMovement();
                living.setDeltaMovement(delta.x * 1.175, delta.y * 1.06, delta.z * 1.175);
            }
        }
    }
}
