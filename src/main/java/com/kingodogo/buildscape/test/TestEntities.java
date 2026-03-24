package com.kingodogo.buildscape.test;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = "buildscape", bus = Mod.EventBusSubscriber.Bus.MOD)
public class TestEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITIES, BuildScape.MODID);

    public static final RegistryObject<EntityType<TestPetEntity>> TEST_PET = ENTITIES.register("test_pet", () ->
            EntityType.Builder.of(TestPetEntity::new, MobCategory.CREATURE)
                    .sized(0.3F, 0.85F) // Small size
                    .clientTrackingRange(10)
                    .build("test_pet")
    );

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(TEST_PET.get(), TestPetEntity.createAttributes().build());
    }

    @Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onRegisterCommands(net.minecraftforge.event.RegisterCommandsEvent event) {
            event.getDispatcher().register(
                    com.mojang.brigadier.builder.LiteralArgumentBuilder.<net.minecraft.commands.CommandSourceStack>literal("testpet")
                            .then(com.mojang.brigadier.builder.LiteralArgumentBuilder.<net.minecraft.commands.CommandSourceStack>literal("spawn")
                                    .executes(context -> {
                                        try {
                                            net.minecraft.commands.CommandSourceStack source = context.getSource();
                                            net.minecraft.server.level.ServerPlayer player = source.getPlayerOrException();
                                            net.minecraft.server.level.ServerLevel level = player.getLevel();
                                            TestPetEntity entity = TEST_PET.get().create(level);
                                            if (entity != null) {
                                                entity.moveTo(player.getX(), player.getY(), player.getZ(), 0.0F, 0.0F);
                                                entity.setOwnerUUID(player.getUUID());
                                                entity.setTame(true);
                                                level.addFreshEntity(entity);
                                                source.sendSuccess(new net.minecraft.network.chat.TextComponent("Pet spawned!"), false);
                                            } else {
                                                source.sendFailure(new net.minecraft.network.chat.TextComponent("Failed to create pet entity."));
                                            }
                                        } catch (Exception e) {
                                            context.getSource().sendFailure(new net.minecraft.network.chat.TextComponent("Error: " + e.getMessage()));
                                        }
                                        return 1;
                                    })
                            )
                            .then(com.mojang.brigadier.builder.LiteralArgumentBuilder.<net.minecraft.commands.CommandSourceStack>literal("kill")
                                    .executes(context -> {
                                        try {
                                            net.minecraft.server.level.ServerPlayer player = context.getSource().getPlayerOrException();
                                            net.minecraft.world.phys.Vec3 eyePos = player.getEyePosition(1.0F);
                                            net.minecraft.world.phys.Vec3 view = player.getViewVector(1.0F);
                                            net.minecraft.world.phys.Vec3 targetPos = eyePos.add(view.x * 10.0D, view.y * 10.0D, view.z * 10.0D);
                                            net.minecraft.world.phys.AABB aabb = player.getBoundingBox().expandTowards(view.scale(10.0D)).inflate(1.0D, 1.0D, 1.0D);
                                            
                                            java.util.List<TestPetEntity> pets = player.level.getEntitiesOfClass(TestPetEntity.class, aabb);
                                            TestPetEntity lookingAt = null;
                                            double closest = 100.0D;
                                            for (TestPetEntity p : pets) {
                                                net.minecraft.world.phys.AABB pAabb = p.getBoundingBox().inflate(0.3F);
                                                java.util.Optional<net.minecraft.world.phys.Vec3> clip = pAabb.clip(eyePos, targetPos);
                                                if (clip.isPresent()) {
                                                    double dist = eyePos.distanceToSqr(clip.get());
                                                    if (dist < closest) {
                                                        closest = dist;
                                                        lookingAt = p;
                                                    }
                                                }
                                            }
                                            
                                            if (lookingAt != null) {
                                                lookingAt.discard(); // Safely delete
                                                context.getSource().sendSuccess(new net.minecraft.network.chat.TextComponent("Deleted the pet you were looking at!"), false);
                                            } else {
                                                context.getSource().sendFailure(new net.minecraft.network.chat.TextComponent("You are not looking at a Pet."));
                                            }
                                        } catch(Exception e) {
                                            context.getSource().sendFailure(new net.minecraft.network.chat.TextComponent("Error: " + e.getMessage()));
                                        }
                                        return 1;
                                    })
                            )
            );
        }

        @SubscribeEvent
        public static void onPlayerTick(net.minecraftforge.event.TickEvent.PlayerTickEvent event) {
            if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;
            if (!(event.player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)) return;

            // Stop native server CPU lag by polling visually strictly at 1Hz instead of 20Hz
            if (serverPlayer.tickCount % 20 != 0) return;

            net.minecraft.server.level.ServerLevel level = serverPlayer.getLevel();
            com.kingodogo.buildscape.config.CosmeticsConfig config = com.kingodogo.buildscape.config.CosmeticsConfig.get();
            java.util.Map<Integer, String> equipped = config.getEquippedCosmetics(serverPlayer.getUUID());

            boolean hasPetEquipped = equipped.containsValue("buildscape:cosmatics/wings/test_pet");
            net.minecraft.nbt.CompoundTag pData = serverPlayer.getPersistentData();

            TestPetEntity activePet = null;
            if (pData.hasUUID("ActiveCosmeticPet")) {
                net.minecraft.world.entity.Entity e = level.getEntity(pData.getUUID("ActiveCosmeticPet"));
                if (e instanceof TestPetEntity) {
                    activePet = (TestPetEntity) e;
                }
            }

            if (hasPetEquipped) {
                if (activePet == null) {
                    // Impatient Spawning Fix: 
                    // Prevent clones by waiting 150 server ticks (7.5s) for the original pet's chunk to finish streaming to disk mapping
                    if (serverPlayer.tickCount < 150) return;

                    TestPetEntity entity = TEST_PET.get().create(level);
                    if (entity != null) {
                        entity.moveTo(serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), 0.0F, 0.0F);
                        entity.setOwnerUUID(serverPlayer.getUUID());
                        entity.setTame(true);
                        entity.getPersistentData().putBoolean("IsCosmetic", true);
                        pData.putUUID("ActiveCosmeticPet", entity.getUUID());
                        level.addFreshEntity(entity);
                    }
                } else {
                    // Valid native pet loaded natively! Maintain proximity automatically.
                    if (activePet.distanceToSqr(serverPlayer) > 900.0D) {
                        activePet.moveTo(serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), serverPlayer.getYRot(), serverPlayer.getXRot());
                    }
                }
            } else {
                // Player cleanly unequipped the Cosmetic!
                if (activePet != null && activePet.getPersistentData().getBoolean("IsCosmetic")) {
                    activePet.discard();
                }
                pData.remove("ActiveCosmeticPet");
            }
        }
    }
}
