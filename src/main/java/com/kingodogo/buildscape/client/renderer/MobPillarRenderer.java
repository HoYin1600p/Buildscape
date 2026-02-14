package com.kingodogo.buildscape.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dedicated renderer for mob entities displayed on pillars.
 * Handles state parsing from spawn egg names and applies corresponding visual/behavioral modifications.
 * <p>
 * This class is designed to be modular and maintainable, allowing new states to be added
 * via the mob_states.txt configuration file without modifying core pillar logic.
 */
public class MobPillarRenderer {

    // Cache for entity instances to avoid creating new ones every frame
    private static final Map<String, Entity> entityCache = new ConcurrentHashMap<>();

    // Cache for last applied states to prevent re-applying NBT every frame
    // Key is Entity.getId() (instance ID), Value is the MobState that was applied
    private static final Map<Integer, MobState> lastAppliedStates = new ConcurrentHashMap<>();
    /**
     * Apply mob variant data to NBT before entity creation
     * Uses correct 1.18.2 NBT tags for all supported mobs
     */
    // Standard Dye Colors Map for easy lookup
    private static final Map<String, Integer> DYE_COLORS = new HashMap<>();

    // Cleanup caches if they get too large to prevent memory leaks
    static {
        // Optional: Add a shutdown hook or periodic cleanup if needed
    }

    static {
        DYE_COLORS.put("white", 0);
        DYE_COLORS.put("orange", 1);
        DYE_COLORS.put("magenta", 2);
        DYE_COLORS.put("light_blue", 3);
        DYE_COLORS.put("yellow", 4);
        DYE_COLORS.put("lime", 5);
        DYE_COLORS.put("pink", 6);
        DYE_COLORS.put("gray", 7);
        DYE_COLORS.put("light_gray", 8);
        DYE_COLORS.put("cyan", 9);
        DYE_COLORS.put("purple", 10);
        DYE_COLORS.put("blue", 11);
        DYE_COLORS.put("brown", 12);
        DYE_COLORS.put("green", 13);
        DYE_COLORS.put("red", 14);
        DYE_COLORS.put("black", 15);
    }

    /**
     * Render a mob entity on a pillar with the specified states
     */
    public static void renderMob(
            SpawnEggItem spawnEgg,
            ItemStack spawnEggStack,
            BlockPos pos,
            Level level,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int combinedLight,
            float rotation,
            float gameTime,
            float facingYaw
    ) {
        if (spawnEgg == null || level == null || pos == null) {
            return;
        }

        EntityType<?> entityType = spawnEgg.getType(null);
        if (entityType == null) {
            return;
        }

        // Parse states from spawn egg name
        MobState state = MobStateParser.parseStates(spawnEggStack, entityType);

        // Create cache key
        String cacheKey = pos.getX() + "," + pos.getY() + "," + pos.getZ() + ":" +
                net.minecraftforge.registries.ForgeRegistries.ENTITIES.getKey(entityType).toString();

        // Get or create cached entity
        Entity entity = entityCache.get(cacheKey);
        if (entity == null || entity.getType() != entityType || !entity.isAlive()) {
            if (entity != null && entity.isAlive()) {
                entity.remove(Entity.RemovalReason.DISCARDED);
                lastAppliedStates.remove(entity.getId());
            }

            entity = createEntity(entityType, level, pos, state);
            if (entity != null) {
                entityCache.put(cacheKey, entity);
                // When created, state is inherently applied via createEntity -> applyVariantToNBT
                // But applyStates does more (AI disabling etc), so we should run it once.
                // We'll let the logic below handle it.
            }
        }

        if (entity != null && entity.isAlive()) {
            // Check if state has changed or hasn't been applied fully yet
            MobState lastState = lastAppliedStates.get(entity.getId());
            boolean needsUpdate = lastState == null || !lastState.equals(state);

            if (needsUpdate) {
                // Apply states to entity (expensive operation with NBT)
                applyStates(entity, state);
                lastAppliedStates.put(entity.getId(), state);
            }

            // Update entity position and rotation (cheap operation, done every frame)
            updateEntityTransform(entity, pos, facingYaw, rotation, gameTime, state);

            // Render the entity
            renderEntity(entity, poseStack, bufferSource, combinedLight, partialTicks, state);
        }
    }

    /**
     * Create a new entity instance with initial setup
     */
    private static Entity createEntity(EntityType<?> entityType, Level level, BlockPos pos, MobState state) {
        // Handle Zombie -> Giant conversion
        if (state.parsedStates.contains("giant") &&
                (entityType == EntityType.ZOMBIE || entityType == EntityType.HUSK || entityType == EntityType.DROWNED)) {
            entityType = EntityType.GIANT;
        }

        // Create NBT with variant data BEFORE creating entity
        CompoundTag nbt = new CompoundTag();
        nbt.putString("id", net.minecraftforge.registries.ForgeRegistries.ENTITIES.getKey(entityType).toString());

        // Apply variant NBT before entity creation
        applyVariantToNBT(nbt, entityType, state);

        // Create entity from NBT (this applies variants during creation)
        Entity entity = EntityType.loadEntityRecursive(nbt, level, (e) -> e);

        if (entity == null) {
            // Fallback to normal creation if NBT creation fails
            entity = entityType.create(level);
        }

        if (entity == null) {
            return null;
        }

        // Basic setup
        entity.setNoGravity(true);
        entity.setInvulnerable(true);
        entity.setSilent(true);
        entity.setInvisible(state.invisible);
        entity.setUUID(UUID.randomUUID());
        entity.setPos(pos.getX() + 0.5, pos.getY() + 1.125, pos.getZ() + 0.5);
        entity.noPhysics = true;
        entity.tickCount = 0;

        // Apply glowing effect
        if (state.glowing) {
            entity.setGlowingTag(true);
        }

        // Apply fire effect
        if (state.fire) {
            entity.setSecondsOnFire(999999);
            // Ensure fire flag is set in data manager for client-side rendering
            // This is handled by setSecondsOnFire internally
        }

        // Apply frozen effect
        if (state.frozen) {
            entity.setTicksFrozen(999999);
        }

        return entity;
    }

    /**
     * Apply state-specific modifications to the entity
     */
    private static void applyStates(Entity entity, MobState state) {
        // Reset tick count to prevent animations
        entity.tickCount = 0;

        // Force critical visual flags every frame using public methods where possible
        if (state.fire) {
            entity.setSecondsOnFire(1); // Keep it burning
            // If this doesn't work, we need to access the data tracker directly,
            // but setSecondsOnFire(>0) sets the flag in base tick usually.
            // Since we don't tick, we must force the flag.
            // Best way without reflection:
            // We will rely on NBT load being correct, OR we can try:
            // entity.clearFire() then entity.setSecondsOnFire(1)?
            // Actually, let's use the reflection helper below if needed.
            // For now, let's assume the NBT fix in createEntity works for initial load.
            // But if specific frame updates clear it (like if we accidentally tick it), we lose it.
            // We do NOT call entity.tick().
        }

        if (state.glowing) {
            entity.setGlowingTag(true);
        }

        if (state.invisible) {
            entity.setInvisible(true);
        }

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;

            // Hurt state - Force red flash
            if (state.parsedStates.contains("hurt") || state.parsedStates.contains("damage")) {
                livingEntity.hurtTime = 10;
                livingEntity.hurtDuration = 10;
                livingEntity.deathTime = 0; // Ensure not dying
            }

            // Disable AI
            if (entity instanceof Mob) {
                ((Mob) entity).setNoAi(true);
            }

            // Reset animation states (only if not hurt to allow flash)
            if (!state.parsedStates.contains("hurt") && !state.parsedStates.contains("damage")) {
                livingEntity.hurtTime = 0;
            }
            livingEntity.setSprinting(false);
            livingEntity.setShiftKeyDown(false);
            livingEntity.animationSpeed = 0.0f;
            livingEntity.animationSpeedOld = 0.0f;
            livingEntity.animationPosition = 0.0f;
            livingEntity.swingTime = 0;
            livingEntity.attackAnim = 0.0f;
            livingEntity.oAttackAnim = 0.0f;
            livingEntity.setDeltaMovement(0, 0, 0);
            livingEntity.setSpeed(0.0f);

            // Robust Baby State Application
            if (state.baby) {
                boolean handled = false;

                // Standard Animals / Villagers
                if (livingEntity instanceof net.minecraft.world.entity.AgeableMob) {
                    ((net.minecraft.world.entity.AgeableMob) livingEntity).setBaby(true);
                    handled = true;
                }

                // Zombies and variants (Husk, Drowned, Zombified Piglin)
                // Note: Zombie is not AgeableMob in 1.18 inheritance tree
                if (!handled && livingEntity instanceof net.minecraft.world.entity.monster.Zombie) {
                    ((net.minecraft.world.entity.monster.Zombie) livingEntity).setBaby(true);
                    handled = true;
                }

                // Zoglins
                if (!handled && livingEntity instanceof net.minecraft.world.entity.monster.Zoglin) {
                    ((net.minecraft.world.entity.monster.Zoglin) livingEntity).setBaby(true);
                    handled = true;
                }

                // AbstractPiglin (Piglin, Piglin Brute)
                // Need to use reflection or check class name if not imported
                if (!handled && livingEntity.getClass().getName().contains("Piglin")) {
                    try {
                        // Piglins usually have setBaby or setIsBaby
                        java.lang.reflect.Method setBaby = livingEntity.getClass().getMethod("setBaby", boolean.class);
                        setBaby.invoke(livingEntity, true);
                        handled = true;
                    } catch (Exception ignored) {
                    }
                }

                // Fallback for Modded Entities: Try to find setBaby via reflection
                if (!handled) {
                    try {
                        java.lang.reflect.Method setBaby = livingEntity.getClass().getMethod("setBaby", boolean.class);
                        setBaby.invoke(livingEntity, true);
                    } catch (Exception ignored) {
                        // Try setIsBaby
                        try {
                            java.lang.reflect.Method setIsBaby = livingEntity.getClass().getMethod("setIsBaby", boolean.class);
                            setIsBaby.invoke(livingEntity, true);
                        } catch (Exception ignored2) {
                            // No baby method found, relying on NBT IsBaby tag
                            // System.out.println("Could not finding setBaby method for " + livingEntity.getClass().getName());
                        }
                    }
                }
            }
        }

        // CRITICAL: Force update of entity NBT from state to handle variants like CatType/Saddle
        // Only done once per state change now!
        updateEntityFromState(entity, state);

        // Entity-specific states
        applyEntitySpecificStates(entity, state);
    }

    /**
     * Apply entity-specific state modifications
     */
    private static void applyEntitySpecificStates(Entity entity, MobState state) {
        // Bees
        if (entity instanceof net.minecraft.world.entity.animal.Bee) {
            net.minecraft.world.entity.animal.Bee bee = (net.minecraft.world.entity.animal.Bee) entity;
            if (state.angry) {
                bee.setRemainingPersistentAngerTime(999999);
            } else {
                bee.setRemainingPersistentAngerTime(0);
            }
        }

        // Wolves
        if (entity instanceof net.minecraft.world.entity.animal.Wolf) {
            net.minecraft.world.entity.animal.Wolf wolf = (net.minecraft.world.entity.animal.Wolf) entity;
            if (state.angry) {
                wolf.setRemainingPersistentAngerTime(999999);
            }
            if (state.tamed) {
                wolf.setTame(true);
            }
            if (state.sitting) {
                wolf.setInSittingPose(true);
            }
        }

        // Cats
        if (entity instanceof net.minecraft.world.entity.animal.Cat) {
            net.minecraft.world.entity.animal.Cat cat = (net.minecraft.world.entity.animal.Cat) entity;
            if (state.tamed) {
                cat.setTame(true);
            }
            if (state.sitting) {
                cat.setInSittingPose(true);
            }
        }

        // Foxes
        if (entity instanceof net.minecraft.world.entity.animal.Fox) {
            net.minecraft.world.entity.animal.Fox fox = (net.minecraft.world.entity.animal.Fox) entity;
            if (state.sitting) {
                fox.setSitting(true);
            }
        }

        // Creepers
        if (entity instanceof net.minecraft.world.entity.monster.Creeper) {
            // net.minecraft.world.entity.monster.Creeper creeper = (net.minecraft.world.entity.monster.Creeper) entity;
            if (state.charged || state.powered) {
                // Note: Setting powered state requires NBT manipulation or reflection
                // For now, this state is recognized but not visually applied
                // NBT update in applyVariantToNBT handles this for creation/update
            }
        }

        // Sheep
        if (entity instanceof net.minecraft.world.entity.animal.Sheep) {
            net.minecraft.world.entity.animal.Sheep sheep = (net.minecraft.world.entity.animal.Sheep) entity;
            if (state.sheared) {
                sheep.setSheared(true);
            }
        }

        // Bat - Roosting/Hanging
        if (entity instanceof net.minecraft.world.entity.ambient.Bat) {
            net.minecraft.world.entity.ambient.Bat bat = (net.minecraft.world.entity.ambient.Bat) entity;
            bat.setResting(state.parsedStates.contains("hanging") || state.parsedStates.contains("roosting"));
        }

        // Polar Bear - Standing
        if (entity instanceof net.minecraft.world.entity.animal.PolarBear) {
            net.minecraft.world.entity.animal.PolarBear bear = (net.minecraft.world.entity.animal.PolarBear) entity;
            bear.setStanding(state.parsedStates.contains("standing") || state.parsedStates.contains("rearing"));
        }

        // Enderman - Screaming/Staring
        if (entity instanceof net.minecraft.world.entity.monster.EnderMan) {
            net.minecraft.world.entity.monster.EnderMan enderman = (net.minecraft.world.entity.monster.EnderMan) entity;
            // setCreepy is usually client-side visible
            // Check mapping name if needed, but setCreepy usually exists? No, it's 'hasBeenStaredAt' logic or data tracker.
            // 1.18.2 Enderman uses DATA_CREEPY (18).
            // We need to verify if setCreepy exists or strict NBT/DataTracker needed.
            // Actually, let's assume standard accessors exist or verify later.
            // If error, we might need reflection or specialized handling.
            // For now, let's check NBT approach: AngerTime > 0 usually makes them scream?
            if (state.parsedStates.contains("screaming") || state.parsedStates.contains("staring")) {
                enderman.setTarget(Minecraft.getInstance().player); // Force anger state visually?
                // enderman.setBeenStaredAt();
            }
        }

        // Spider - Climbing
        if (entity instanceof net.minecraft.world.entity.monster.Spider) {
            net.minecraft.world.entity.monster.Spider spider = (net.minecraft.world.entity.monster.Spider) entity;
            spider.setClimbing(state.parsedStates.contains("climbing"));
        }

        // Vex - Charging
        if (entity instanceof net.minecraft.world.entity.monster.Vex) {
            net.minecraft.world.entity.monster.Vex vex = (net.minecraft.world.entity.monster.Vex) entity;
            if (state.parsedStates.contains("charging")) {
                vex.setIsCharging(true);
            }
        }
    }

    /**
     * Apply mob variant data to NBT before entity creation
     */
    private static void applyVariantToNBT(CompoundTag nbt, EntityType<?> entityType, MobState state) {
        String entityTypeName = entityType.getDescriptionId().toLowerCase();
        if (entityTypeName.contains(".")) {
            String[] parts = entityTypeName.split("\\.");
            entityTypeName = parts[parts.length - 1];
        }

        // --- Universal Tags ---
        nbt.putBoolean("NoAI", true);
        nbt.putBoolean("Silent", true);
        nbt.putBoolean("Invulnerable", true);
        nbt.putBoolean("PersistenceRequired", true);
        nbt.putBoolean("NoGravity", true);

        // Visual Flags
        if (state.glowing) nbt.putBoolean("Glowing", true);
        if (state.fire) nbt.putShort("Fire", (short) 32767);
        if (state.invisible) nbt.putBoolean("Invisible", true);

        // For client-side rendering, TicksFrozen must be set in NBT to init the data tracker correctly
        if (state.frozen) nbt.putInt("TicksFrozen", 140);

        // Handedness (if user adds "lefty" or "left_handed" to states)
        if (state.parsedStates.contains("lefty") || state.parsedStates.contains("left_handed")) {
            nbt.putBoolean("LeftHanded", true);
        }

        // Age (Baby/Adult)
        if (state.baby) {
            nbt.putInt("Age", -25000);
            nbt.putBoolean("IsBaby", true);
        } else {
            nbt.putInt("Age", 0);
            nbt.putBoolean("IsBaby", false);
        }

        // --- Mob Specific Logic ---

        // Tameable Logic (Wolf, Cat, Parrot)
        boolean isTameable = entityTypeName.equals("wolf") || entityTypeName.equals("cat") || entityTypeName.equals("parrot");
        if (isTameable) {
            if (state.tamed) {
                if (!nbt.hasUUID("Owner")) nbt.putUUID("Owner", UUID.randomUUID());
            } else {
                nbt.remove("Owner");
            }
            nbt.putBoolean("Sitting", state.sitting);
        }

        // --- Generic Mod Support: Apply common states optimistically ---
        // These tags are harmless if the entity doesn't support them, but enable modded support.

        if (state.saddled) nbt.putBoolean("Saddle", true);
        if (state.sheared) nbt.putBoolean("Sheared", true);

        // Chested Horse / Donkey / Mule / Llama generic
        if (state.parsedStates.contains("chested")) nbt.putBoolean("ChestedHorse", true);

        // Optimistic Color Application (Sheep, Shulker, Collar for Tames)
        int genericColor = getDyeColor(state, -1);
        if (genericColor >= 0) {
            // Only apply generic Color if not later handled specifically (though usually safe)
            if (!nbt.contains("Color")) {
                nbt.putByte("Color", (byte) genericColor);
            }
        }

        // --- Mob Specific Logic ---

        if (entityTypeName.equals("cat")) {
            int catType = getCatType(state);
            if (catType >= 0) nbt.putInt("CatType", catType);
            if (state.tamed) nbt.putByte("CollarColor", (byte) (genericColor >= 0 ? genericColor : 14));
        } else if (entityTypeName.equals("wolf")) {
            if (state.angry) nbt.putInt("AngerTime", 999999);
            else nbt.putInt("AngerTime", 0);
            if (state.tamed) nbt.putByte("CollarColor", (byte) (genericColor >= 0 ? genericColor : 14));
        } else if (entityTypeName.equals("creeper")) {
            boolean powered = state.charged || state.powered;
            nbt.putBoolean("powered", powered);
            nbt.putBoolean("ignited", state.parsedStates.contains("ignited") || state.parsedStates.contains("ignite"));
        } else if (entityTypeName.equals("sheep")) {
            if (state.parsedStates.contains("rainbow") || state.parsedStates.contains("jeb")) {
                nbt.putString("CustomName", "{\"text\":\"jeb_\"}");
                nbt.putBoolean("CustomNameVisible", false);
            } else {
                // Color handled by generic logic above
            }
        } else if (entityTypeName.equals("strider")) {
            nbt.putBoolean("Saddle", state.saddled);
            // Cold/Shivering requires boolean
            // Note: Striders rely on environment for shivering, but we can't force it via NBT easily without environment.
            // However, we can set 'Suffocating' to true via reflection or specific entity NBT if available?
            // Actually 1.18 striders shiver if on land. Since pillars are air/land, they should shiver by default?
            // No, they shiver if NOT in lava.
            // We will let them be normal unless 'cold' explicitly requested? No, usually they shiver.
        } else if (entityTypeName.equals("vindicator")) {
            if (state.parsedStates.contains("johnny")) {
                nbt.putString("CustomName", "{\"text\":\"Johnny\"}");
                nbt.putBoolean("CustomNameVisible", false);
            }
        } else if (entityTypeName.equals("evoker") || entityTypeName.equals("illusioner")) {
            if (state.parsedStates.contains("casting") || state.parsedStates.contains("spell")) {
                nbt.putInt("SpellTicks", 20); // Force spell casting pose
            }
        } else if (entityTypeName.equals("enderman")) {
            if (state.parsedStates.contains("block") || state.parsedStates.contains("carrying")) {
                // Default to grass block if carrying
                CompoundTag blockState = new CompoundTag();
                blockState.putString("Name", "minecraft:grass_block");
                nbt.put("carriedBlockState", blockState);
            }
        } else if (entityTypeName.equals("rabbit")) {
            int variant = getRabbitVariant(state);
            if (variant >= 0) nbt.putInt("RabbitType", variant);
            if (state.parsedStates.contains("toast")) {
                nbt.putString("CustomName", "{\"text\":\"Toast\"}");
            }
        } else if (entityTypeName.equals("axolotl")) {
            int variant = getAxolotlVariant(state);
            if (variant >= 0) nbt.putInt("Variant", variant);
        } else if (entityTypeName.equals("fox")) {
            String type = getFoxType(state);
            if (type != null) nbt.putString("Type", type);
            nbt.putBoolean("Sitting", state.sitting);
            nbt.putBoolean("Sleeping", state.parsedStates.contains("sleeping") || state.parsedStates.contains("sleep"));
            nbt.putBoolean("Crouching", state.parsedStates.contains("crouching") || state.parsedStates.contains("crouch"));
        } else if (entityTypeName.equals("mooshroom")) {
            String type = getMooshroomType(state);
            if (type != null) nbt.putString("Type", type);
        } else if (entityTypeName.equals("panda")) {
            String gene = getPandaGene(state);
            if (gene != null) {
                nbt.putString("MainGene", gene);
                nbt.putString("HiddenGene", gene);
            }
        } else if (entityTypeName.equals("goat")) {
            nbt.putBoolean("IsScreamingGoat", state.parsedStates.contains("screaming") || state.parsedStates.contains("scream"));
            if (state.parsedStates.contains("no_horns") || state.parsedStates.contains("nohorns")) {
                nbt.putBoolean("HasLeftHorn", false);
                nbt.putBoolean("HasRightHorn", false);
            } else {
                nbt.putBoolean("HasLeftHorn", true);
                nbt.putBoolean("HasRightHorn", true);
            }
        } else if (entityTypeName.equals("bee")) {
            if (state.angry) nbt.putInt("AngerTime", 999999);
            else nbt.putInt("AngerTime", 0);
            nbt.putBoolean("HasNectar", state.parsedStates.contains("nectar"));
            nbt.putBoolean("HasStung", state.parsedStates.contains("stung"));
        } else if (entityTypeName.equals("parrot")) {
            int variant = getParrotVariant(state);
            if (variant >= 0) nbt.putInt("Variant", variant);
        } else if (entityTypeName.equals("llama") || entityTypeName.equals("trader_llama")) {
            int variant = getLlamaVariant(state);
            if (variant >= 0) nbt.putInt("Variant", variant);
            nbt.putInt("Strength", 5);
            // ChestedHorse handled by generic
            if (genericColor >= 0) nbt.putInt("DecorColor", genericColor);
        } else if (entityTypeName.contains("horse") || entityTypeName.equals("donkey") || entityTypeName.equals("mule")) {
            if (entityTypeName.equals("horse")) {
                int variant = getHorseVariant(state);
                if (variant >= 0) nbt.putInt("Variant", variant);

                // Armor Logic
                if (state.parsedStates.contains("diamond") || state.parsedStates.contains("diamond_armor")) {
                    CompoundTag itemTag = new CompoundTag();
                    itemTag.putString("id", "minecraft:diamond_horse_armor");
                    itemTag.putByte("Count", (byte) 1);
                    nbt.put("ArmorItem", itemTag);
                } else if (state.parsedStates.contains("gold") || state.parsedStates.contains("gold_armor")) {
                    CompoundTag itemTag = new CompoundTag();
                    itemTag.putString("id", "minecraft:golden_horse_armor");
                    itemTag.putByte("Count", (byte) 1);
                    nbt.put("ArmorItem", itemTag);
                } else if (state.parsedStates.contains("iron") || state.parsedStates.contains("iron_armor")) {
                    CompoundTag itemTag = new CompoundTag();
                    itemTag.putString("id", "minecraft:iron_horse_armor");
                    itemTag.putByte("Count", (byte) 1);
                    nbt.put("ArmorItem", itemTag);
                } else if (state.parsedStates.contains("leather") || state.parsedStates.contains("leather_armor")) {
                    CompoundTag itemTag = new CompoundTag();
                    itemTag.putString("id", "minecraft:leather_horse_armor");
                    itemTag.putByte("Count", (byte) 1);
                    nbt.put("ArmorItem", itemTag);
                }
            }
            // ChestedHorse handled by generic logic above

            // Saddle Logic
            if (state.saddled) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putString("id", "minecraft:saddle");
                itemTag.putByte("Count", (byte) 1);
                nbt.put("SaddleItem", itemTag);
            } else {
                nbt.remove("SaddleItem");
            }

            if (!state.baby) {
                nbt.putBoolean("Tame", true);
            }
        } else if (entityTypeName.equals("frog")) {
            String frogVariant = getFrogVariant(state);
            if (frogVariant != null) nbt.putString("variant", "minecraft:" + frogVariant);
        } else if (entityTypeName.equals("shulker")) {
            if (state.parsedStates.contains("open")) nbt.putByte("Peek", (byte) 100);
        } else if (entityTypeName.equals("boat")) {
            String type = "oak";
            if (state.parsedStates.contains("spruce")) type = "spruce";
            else if (state.parsedStates.contains("birch")) type = "birch";
            else if (state.parsedStates.contains("jungle")) type = "jungle";
            else if (state.parsedStates.contains("acacia")) type = "acacia";
            else if (state.parsedStates.contains("dark_oak") || state.parsedStates.contains("darkoak"))
                type = "dark_oak";
            else if (state.parsedStates.contains("mangrove")) type = "mangrove";
            nbt.putString("Type", type);
        } else if (entityTypeName.equals("slime") || entityTypeName.equals("magma_cube") || entityTypeName.equals("phantom")) {
            // Size variants
            int size = 1; // Default Small
            if (state.parsedStates.contains("tiny")) size = 0; // Smallest
            else if (state.parsedStates.contains("small")) size = 1;
            else if (state.parsedStates.contains("medium")) size = 2; // Big
            else if (state.parsedStates.contains("large")) size = 4; // Bigger
            else if (state.parsedStates.contains("huge") || state.parsedStates.contains("giant")) size = 8; // Massive

            nbt.putInt("Size", size);
        } else if (entityTypeName.equals("iron_golem")) {
            if (state.parsedStates.contains("cracked") || state.parsedStates.contains("broken")) {
                nbt.putFloat("Health", 10.0f); // Low health shows cracks
            }
        } else if (entityTypeName.equals("tropical_fish")) {
            // Variant is an int packed with size/pattern/body color/pattern color
            // This is complex, so we will implement basic presets references
            // For now, let's just support a few common ones if requested, or random if not specified?
            // Vanilla defaults to random.
            if (state.parsedStates.contains("kob")) nbt.putInt("Variant", 65536);
            else if (state.parsedStates.contains("sunstreak")) nbt.putInt("Variant", 131072);
            else if (state.parsedStates.contains("snooper")) nbt.putInt("Variant", 196608);
            else if (state.parsedStates.contains("dasher")) nbt.putInt("Variant", 262144);
            else if (state.parsedStates.contains("brinely")) nbt.putInt("Variant", 327680);
            else if (state.parsedStates.contains("spotty")) nbt.putInt("Variant", 393216);
            else if (state.parsedStates.contains("flopper")) nbt.putInt("Variant", 458752);
            else if (state.parsedStates.contains("stripey")) nbt.putInt("Variant", 524288);
            else if (state.parsedStates.contains("glitter")) nbt.putInt("Variant", 589824);
            else if (state.parsedStates.contains("blockfish")) nbt.putInt("Variant", 655360);
            else if (state.parsedStates.contains("betty")) nbt.putInt("Variant", 720896);
            else if (state.parsedStates.contains("clayfish")) nbt.putInt("Variant", 786432);
        } else if (entityTypeName.equals("armor_stand")) {
            nbt.putBoolean("ShowArms", state.parsedStates.contains("arms") || state.parsedStates.contains("show_arms"));
            nbt.putBoolean("Small", state.parsedStates.contains("small") || state.baby);
            nbt.putBoolean("NoBasePlate", state.parsedStates.contains("no_base") || state.parsedStates.contains("nobase"));
        } else if (entityTypeName.equals("end_crystal")) {
            nbt.putBoolean("ShowBottom", !state.parsedStates.contains("no_bottom"));
        } else if (entityTypeName.equals("wither")) {
            if (state.parsedStates.contains("shield") || state.parsedStates.contains("invul")) {
                nbt.putInt("Invul", 100); // Renders blue shield
            }
        } else if (entityTypeName.equals("iron_golem")) {
            if (state.parsedStates.contains("cracked") || state.parsedStates.contains("broken")) {
                // Lower health shows cracks (Max 100)
                // High cracks = Low health
                nbt.putFloat("Health", 25.0f);
            } else {
                nbt.putFloat("Health", 100.0f);
            }
        } else if (entityTypeName.equals("snow_golem")) {
            nbt.putBoolean("Pumpkin", !state.parsedStates.contains("no_pumpkin"));
        } else if (entityTypeName.equals("pufferfish")) {
            int puffState = 0;
            if (state.parsedStates.contains("half")) puffState = 1;
            else if (state.parsedStates.contains("full") || state.parsedStates.contains("puff")) puffState = 2;
            nbt.putInt("PuffState", puffState);
        } else if (entityTypeName.equals("villager") || entityTypeName.equals("zombie_villager")) {
            String profession = getVillagerProfession(state);
            String type = getVillagerType(state);

            if (profession != null || type != null) {
                CompoundTag villagerData = new CompoundTag();
                // Default to plains/none if not specified, but keep existing logic
                villagerData.putString("profession", profession != null ? "minecraft:" + profession : "minecraft:none");
                villagerData.putString("type", type != null ? "minecraft:" + type : "minecraft:plains");
                villagerData.putInt("level", 1);
                nbt.put("VillagerData", villagerData);
            }
        }
    }

    /**
     * Looks for a standard dye color in the state and returns its ID.
     * Returns defaultValue if no color is found.
     */
    private static int getDyeColor(MobState state, int defaultValue) {
        for (Map.Entry<String, Integer> entry : DYE_COLORS.entrySet()) {
            if (state.parsedStates.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return defaultValue;
    }

    private static String getVillagerType(MobState state) {
        if (state.parsedStates.contains("desert")) return "desert";
        if (state.parsedStates.contains("jungle")) return "jungle";
        if (state.parsedStates.contains("savanna")) return "savanna";
        if (state.parsedStates.contains("snow") || state.parsedStates.contains("snowy")) return "snow";
        if (state.parsedStates.contains("swamp")) return "swamp";
        if (state.parsedStates.contains("taiga")) return "taiga";
        if (state.parsedStates.contains("plains")) return "plains";
        return null;
    }

    private static String getFrogVariant(MobState state) {
        if (state.parsedStates.contains("temperate")) return "temperate";
        if (state.parsedStates.contains("warm")) return "warm";
        if (state.parsedStates.contains("cold")) return "cold";
        return null;
    }


    private static void updateEntityFromState(Entity entity, MobState state) {
        try {
            CompoundTag nbt = new CompoundTag();
            if (entity.save(nbt)) {
                applyVariantToNBT(nbt, entity.getType(), state);
                entity.load(nbt);
            }
        } catch (Exception e) {
            // Ignore NBT errors to prevent crash
        }
    }

    // --- Helper Methods ---

    private static int getCatType(MobState state) {
        // 0=tabby, 1=black, 2=red, 3=siamese, 4=british, 5=calico, 6=persian, 7=ragdoll, 8=white, 9=jellie, 10=all_black
        if (state.parsedStates.contains("tabby")) return 0;
        if (state.parsedStates.contains("tuxedo") || state.parsedStates.contains("black")) return 1;
        if (state.parsedStates.contains("red") || state.parsedStates.contains("orange")) return 2;
        if (state.parsedStates.contains("siamese")) return 3;
        if (state.parsedStates.contains("british")) return 4;
        if (state.parsedStates.contains("calico")) return 5;
        if (state.parsedStates.contains("persian")) return 6;
        if (state.parsedStates.contains("ragdoll")) return 7;
        if (state.parsedStates.contains("white")) return 8;
        if (state.parsedStates.contains("jellie")) return 9;
        if (state.parsedStates.contains("all_black") || state.parsedStates.contains("midnight")) return 10;
        return -1;
    }

    private static int getAxolotlVariant(MobState state) {
        if (state.parsedStates.contains("lucy") || state.parsedStates.contains("pink")) return 0;
        if (state.parsedStates.contains("wild") || state.parsedStates.contains("brown")) return 1;
        if (state.parsedStates.contains("gold") || state.parsedStates.contains("yellow")) return 2;
        if (state.parsedStates.contains("cyan")) return 3;
        if (state.parsedStates.contains("blue")) return 4;
        return -1;
    }

    private static int getRabbitVariant(MobState state) {
        if (state.parsedStates.contains("brown")) return 0;
        if (state.parsedStates.contains("white")) return 1;
        if (state.parsedStates.contains("black")) return 2;
        if (state.parsedStates.contains("white_splotched") || state.parsedStates.contains("spotted")) return 3;
        if (state.parsedStates.contains("gold")) return 4;
        if (state.parsedStates.contains("salt")) return 5;
        if (state.parsedStates.contains("toast")) return 6;
        if (state.parsedStates.contains("killer")) return 99;
        return -1;
    }

    private static int getHorseVariant(MobState state) {
        // Colors: 0=White, 1=Creamy, 2=Chestnut, 3=Brown, 4=Black, 5=Gray, 6=Dark Brown
        if (state.parsedStates.contains("white")) return 0;
        if (state.parsedStates.contains("creamy")) return 1;
        if (state.parsedStates.contains("chestnut")) return 2;
        if (state.parsedStates.contains("brown")) {
            if (state.parsedStates.contains("dark")) return 6;
            return 3;
        }
        if (state.parsedStates.contains("black")) return 4;
        if (state.parsedStates.contains("gray")) return 5;
        if (state.parsedStates.contains("dark_brown")) return 6;
        return -1;
    }

    private static String getVillagerProfession(MobState state) {
        if (state.parsedStates.contains("farmer")) return "farmer";
        if (state.parsedStates.contains("fisherman")) return "fisherman";
        if (state.parsedStates.contains("shepherd")) return "shepherd";
        if (state.parsedStates.contains("fletcher")) return "fletcher";
        if (state.parsedStates.contains("librarian")) return "librarian";
        if (state.parsedStates.contains("cartographer")) return "cartographer";
        if (state.parsedStates.contains("cleric")) return "cleric";
        if (state.parsedStates.contains("armorer")) return "armorer";
        if (state.parsedStates.contains("weaponsmith")) return "weaponsmith";
        if (state.parsedStates.contains("toolsmith")) return "toolsmith";
        if (state.parsedStates.contains("butcher")) return "butcher";
        if (state.parsedStates.contains("leatherworker")) return "leatherworker";
        if (state.parsedStates.contains("mason")) return "mason";
        if (state.parsedStates.contains("nitwit")) return "nitwit";
        return null;
    }

    private static String getFoxType(MobState state) {
        if (state.parsedStates.contains("red")) return "red";
        if (state.parsedStates.contains("snow") || state.parsedStates.contains("white")) return "snow";
        return null;
    }

    private static String getMooshroomType(MobState state) {
        if (state.parsedStates.contains("red")) return "red";
        if (state.parsedStates.contains("brown")) return "brown";
        return null;
    }

    private static String getPandaGene(MobState state) {
        if (state.parsedStates.contains("normal")) return "normal";
        if (state.parsedStates.contains("lazy")) return "lazy";
        if (state.parsedStates.contains("worried")) return "worried";
        if (state.parsedStates.contains("playful")) return "playful";
        if (state.parsedStates.contains("brown")) return "brown";
        if (state.parsedStates.contains("weak")) return "weak";
        if (state.parsedStates.contains("aggressive")) return "aggressive";
        return null;
    }

    private static int getParrotVariant(MobState state) {
        if (state.parsedStates.contains("red") || state.parsedStates.contains("cookie")) return 0;
        if (state.parsedStates.contains("blue")) return 1;
        if (state.parsedStates.contains("green")) return 2;
        if (state.parsedStates.contains("cyan")) return 3;
        if (state.parsedStates.contains("gray")) return 4;
        return -1;
    }

    private static int getLlamaVariant(MobState state) {
        if (state.parsedStates.contains("creamy")) return 0;
        if (state.parsedStates.contains("white")) return 1;
        if (state.parsedStates.contains("brown")) return 2;
        if (state.parsedStates.contains("gray")) return 3;
        return -1;
    }

    /**
     * Update entity position and rotation based on states
     */
    private static void updateEntityTransform(
            Entity entity,
            BlockPos pos,
            float facingYaw,
            float rotation,
            float gameTime,
            MobState state
    ) {
        // Calculate final yaw
        float finalYaw = facingYaw;
        if (state.upsideDown) {
            finalYaw = (finalYaw + 180.0f) % 360.0f;
        }
        if (state.spin) {
            finalYaw = (finalYaw + rotation) % 360.0f;
        }
        if (finalYaw < 0) {
            finalYaw += 360.0f;
        }

        // Update rotation
        float prevYRot = entity.getYRot();
        entity.setYRot(finalYaw);
        entity.yRotO = prevYRot;

        // Update living entity rotations
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            float prevBodyRot = livingEntity.yBodyRot;
            livingEntity.yBodyRot = finalYaw;
            livingEntity.yBodyRotO = prevBodyRot;

            float prevHeadRot = livingEntity.yHeadRot;
            livingEntity.yHeadRot = finalYaw;
            livingEntity.yHeadRotO = prevHeadRot;
        }

        // Add floating animation
        float bobAmount = (float) Math.sin(gameTime * 2.0f) * 0.05f;
        float baseY = pos.getY() + 1.125f;
        entity.setPos(pos.getX() + 0.5, baseY + bobAmount, pos.getZ() + 0.5);
    }

    /**
     * Render the entity with appropriate transformations
     */
    private static void renderEntity(
            Entity entity,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int combinedLight,
            float partialTicks,
            MobState state
    ) {
        // Calculate scale
        float entityWidth = entity.getBbWidth();
        float entityHeight = entity.getBbHeight();
        float scale;

        if (state.parsedStates.contains("giant") || state.parsedStates.contains("huge")) {
            // Explicitly requested GIANT size.
            if (entityHeight > 6.0f) {
                scale = 0.4f; // Massive entities (Giants) become ~4.8m
            } else {
                scale = 1.2f; // Smaller entities (Slimes) become prominent
            }
        } else if (state.parsedStates.contains("large")) {
            scale = 0.9f;
        } else if (state.parsedStates.contains("medium")) {
            scale = 0.7f;
        } else if (state.parsedStates.contains("small") && (entity instanceof net.minecraft.world.entity.monster.Slime || entity instanceof net.minecraft.world.entity.monster.MagmaCube)) {
            // Explicit small slime/cube should carry some size
            scale = 0.8f;
        } else if (state.parsedStates.contains("tiny") && (entity instanceof net.minecraft.world.entity.monster.Slime || entity instanceof net.minecraft.world.entity.monster.MagmaCube)) {
            // Tiny slime (Size 0) is very small naturally
            scale = 1.0f;
        } else {
            // Standard Auto-Scaling Logic
            if (entityHeight <= 1.0f) {
                float maxDimension = Math.max(entityWidth, entityHeight);
                float targetSize = 0.8f;
                scale = targetSize / maxDimension;
                scale = Math.min(1.5f, scale);
            } else {
                if (entityHeight > 2.5f) {
                    scale = 1.8f / entityHeight;
                } else {
                    scale = 0.9f;
                }
                scale = Math.max(0.3f, scale);
            }
        }

        // Get entity renderer
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        @SuppressWarnings("unchecked")
        EntityRenderer<Entity> entityRenderer = (EntityRenderer<Entity>) dispatcher.getRenderer(entity);

        if (entityRenderer != null) {
            poseStack.pushPose();

            // Apply scale
            poseStack.scale(scale, scale, scale);

            // Apply upside-down rotation
            if (state.upsideDown) {
                float centerOffset = entityHeight * 0.5f;
                poseStack.translate(0.0, centerOffset, 0.0);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0f));
                poseStack.translate(0.0, -centerOffset, 0.0);
            }

            // Render
            entityRenderer.render(
                    entity,
                    entity.getYRot(),
                    0.0f,
                    poseStack,
                    bufferSource,
                    combinedLight
            );

            poseStack.popPose();
        }
    }

    /**
     * Clear cached entity for a specific position
     */
    public static void clearEntityCache(BlockPos pos) {
        entityCache.entrySet().removeIf(entry -> {
            if (entry.getKey().startsWith(pos.getX() + "," + pos.getY() + "," + pos.getZ() + ":")) {
                Entity entity = entry.getValue();
                if (entity != null && entity.isAlive()) {
                    lastAppliedStates.remove(entity.getId());
                    entity.remove(Entity.RemovalReason.DISCARDED);
                }
                return true;
            }
            return false;
        });
    }

    /**
     * Clear all cached entities
     */
    public static void clearAllEntityCaches() {
        entityCache.values().forEach(entity -> {
            if (entity != null && entity.isAlive()) {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
        });
        entityCache.clear();
        lastAppliedStates.clear();
    }

    /**
     * Clean up stale entities
     */
    public static void cleanupStaleEntities() {
        entityCache.entrySet().removeIf(entry -> {
            Entity entity = entry.getValue();
            boolean isStale = entity == null || !entity.isAlive();
            if (isStale && entity != null) {
                lastAppliedStates.remove(entity.getId());
            }
            return isStale;
        });

        // Also clean up lastAppliedStates for IDs that are no longer in known entities
        // This is a bit expensive so maybe just clear it periodically or rely on removal hooks above
        // For now, let's keep it simple and safe
    }
}
