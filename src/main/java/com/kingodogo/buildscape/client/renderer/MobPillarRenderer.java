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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MobPillarRenderer {

    private static final Map<String, Entity> entityCache = new ConcurrentHashMap<>();

    private static final Map<Integer, MobState> lastAppliedStates = new ConcurrentHashMap<>();
    private static final Map<String, Integer> DYE_COLORS = new HashMap<>();

    static {
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

        MobState state = MobStateParser.parseStates(spawnEggStack, entityType);

        String cacheKey = pos.getX() + "," + pos.getY() + "," + pos.getZ() + ":" +
                net.minecraftforge.registries.ForgeRegistries.ENTITIES.getKey(entityType).toString();

        Entity entity = entityCache.get(cacheKey);
        if (entity == null || entity.getType() != entityType || !entity.isAlive()) {
            if (entity != null && entity.isAlive()) {
                entity.remove(Entity.RemovalReason.DISCARDED);
                lastAppliedStates.remove(entity.getId());
            }

            entity = createEntity(entityType, level, pos, state);
            if (entity != null) {
                entityCache.put(cacheKey, entity);
            }
        }

        if (entity != null && entity.isAlive()) {
            MobState lastState = lastAppliedStates.get(entity.getId());
            boolean needsUpdate = lastState == null || !lastState.equals(state);

            if (needsUpdate) {
                applyStates(entity, state);
                lastAppliedStates.put(entity.getId(), state);
            }

            updateEntityTransform(entity, pos, facingYaw, rotation, gameTime, state);

            boolean isJeb = entity.getType() == EntityType.SHEEP && (state.parsedStates.contains("rainbow") || state.parsedStates.contains("jeb"));
            float renderPartialTicks = isJeb ? (float)((gameTime * 20.0f) % 1.0f) : 0.0f;

            renderEntity(entity, poseStack, bufferSource, combinedLight, renderPartialTicks, state);
        }
    }

    private static Entity createEntity(EntityType<?> entityType, Level level, BlockPos pos, MobState state) {
        if (state.parsedStates.contains("giant") &&
                (entityType == EntityType.ZOMBIE || entityType == EntityType.HUSK || entityType == EntityType.DROWNED)) {
            entityType = EntityType.GIANT;
        }


        CompoundTag nbt = new CompoundTag();
        nbt.putString("id", net.minecraftforge.registries.ForgeRegistries.ENTITIES.getKey(entityType).toString());

        applyVariantToNBT(nbt, entityType, state);

        Entity entity = EntityType.loadEntityRecursive(nbt, level, (e) -> e);

        if (entity == null) {
            entity = entityType.create(level);
        }

        if (entity == null) {
            return null;
        }

        entity.setNoGravity(true);
        entity.setInvulnerable(true);
        entity.setSilent(true);
        entity.setInvisible(state.invisible);
        entity.setUUID(UUID.randomUUID());
        entity.setPos(pos.getX() + 0.5, pos.getY() + 1.125, pos.getZ() + 0.5);
        entity.noPhysics = true;
        entity.tickCount = 0;

        if (state.glowing) {
            entity.setGlowingTag(true);
        }

        if (state.fire) {
            entity.setSecondsOnFire(999999);
        }

        if (state.frozen) {
            entity.setTicksFrozen(999999);
        }

        return entity;
    }

    private static void applyStates(Entity entity, MobState state) {
        entity.tickCount = 0;

        if (state.fire) {
            entity.setSecondsOnFire(1);
        }

        if (state.glowing) {
            entity.setGlowingTag(true);
        }

        if (state.invisible) {
            entity.setInvisible(true);
        }

        if (entity instanceof LivingEntity livingEntity) {

            if (state.parsedStates.contains("hurt") || state.parsedStates.contains("damage")) {
                livingEntity.hurtTime = 10;
                livingEntity.hurtDuration = 10;
                livingEntity.deathTime = 0;
            }

            if (entity instanceof Mob) {
                ((Mob) entity).setNoAi(true);
            }

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

            if (state.baby) {
                boolean handled = false;

                if (livingEntity instanceof net.minecraft.world.entity.AgeableMob) {
                    ((net.minecraft.world.entity.AgeableMob) livingEntity).setBaby(true);
                    handled = true;
                }

                if (!handled && livingEntity instanceof net.minecraft.world.entity.monster.Zombie) {
                    ((net.minecraft.world.entity.monster.Zombie) livingEntity).setBaby(true);
                    handled = true;
                }

                if (!handled && livingEntity instanceof net.minecraft.world.entity.monster.Zoglin) {
                    ((net.minecraft.world.entity.monster.Zoglin) livingEntity).setBaby(true);
                    handled = true;
                }

                if (!handled && livingEntity.getClass().getName().contains("Piglin")) {
                    try {
                        java.lang.reflect.Method setBaby = livingEntity.getClass().getMethod("setBaby", boolean.class);
                        setBaby.invoke(livingEntity, true);
                        handled = true;
                    } catch (Exception ignored) {
                    }
                }

                if (!handled) {
                    try {
                        java.lang.reflect.Method setBaby = livingEntity.getClass().getMethod("setBaby", boolean.class);
                        setBaby.invoke(livingEntity, true);
                    } catch (Exception ignored) {
                        try {
                            java.lang.reflect.Method setIsBaby = livingEntity.getClass().getMethod("setIsBaby", boolean.class);
                            setIsBaby.invoke(livingEntity, true);
                        } catch (Exception ignored2) {
                        }
                    }
                }
            }
        }

        updateEntityFromState(entity, state);

        applyEntitySpecificStates(entity, state);
    }

    private static void applyEntitySpecificStates(Entity entity, MobState state) {
        if (entity instanceof net.minecraft.world.entity.animal.Bee bee) {
            if (state.angry) {
                bee.setRemainingPersistentAngerTime(999999);
            } else {
                bee.setRemainingPersistentAngerTime(0);
            }
        }

        if (entity instanceof net.minecraft.world.entity.animal.Wolf wolf) {
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

        if (entity instanceof net.minecraft.world.entity.animal.Cat cat) {
            if (state.tamed) {
                cat.setTame(true);
            }
            if (state.sitting) {
                cat.setInSittingPose(true);
            }
        }

        if (entity instanceof net.minecraft.world.entity.animal.Fox fox) {
            if (state.sitting) {
                fox.setSitting(true);
            }
        }

        if (entity instanceof net.minecraft.world.entity.monster.Creeper) {
            if (state.charged || state.powered) {
            }
        }

        if (entity instanceof net.minecraft.world.entity.animal.Sheep sheep) {
            if (state.sheared) {
                sheep.setSheared(true);
            }
        }

        if (entity instanceof net.minecraft.world.entity.ambient.Bat bat) {
            bat.setResting(state.parsedStates.contains("hanging") || state.parsedStates.contains("roosting"));
        }

        if (entity instanceof net.minecraft.world.entity.animal.PolarBear bear) {
            bear.setStanding(state.parsedStates.contains("standing") || state.parsedStates.contains("rearing"));
        }

        if (entity instanceof net.minecraft.world.entity.monster.EnderMan enderman) {
            if (state.parsedStates.contains("screaming") || state.parsedStates.contains("staring")) {
                enderman.setTarget(Minecraft.getInstance().player);
            }
        }

        if (entity instanceof net.minecraft.world.entity.monster.Spider spider) {
            spider.setClimbing(state.parsedStates.contains("climbing"));
        }

        if (entity instanceof net.minecraft.world.entity.monster.Vex vex) {
            if (state.parsedStates.contains("charging")) {
                vex.setIsCharging(true);
            }
        }
    }

    private static void applyVariantToNBT(CompoundTag nbt, EntityType<?> entityType, MobState state) {
        String entityTypeName = entityType.getDescriptionId().toLowerCase();
        if (entityTypeName.contains(".")) {
            String[] parts = entityTypeName.split("\\.");
            entityTypeName = parts[parts.length - 1];
        }

        nbt.putBoolean("NoAI", true);
        nbt.putBoolean("Silent", true);
        nbt.putBoolean("Invulnerable", true);
        nbt.putBoolean("PersistenceRequired", true);
        nbt.putBoolean("NoGravity", true);

        if (state.glowing) nbt.putBoolean("Glowing", true);
        if (state.fire) nbt.putShort("Fire", (short) 32767);
        if (state.invisible) nbt.putBoolean("Invisible", true);

        if (state.frozen) nbt.putInt("TicksFrozen", 140);

        if (state.parsedStates.contains("lefty") || state.parsedStates.contains("left_handed")) {
            nbt.putBoolean("LeftHanded", true);
        }

        if (state.baby) {
            nbt.putInt("Age", -25000);
            nbt.putBoolean("IsBaby", true);
        } else {
            nbt.putInt("Age", 0);
            nbt.putBoolean("IsBaby", false);
        }


        boolean isTameable = entityTypeName.equals("wolf") || entityTypeName.equals("cat") || entityTypeName.equals("parrot");
        if (isTameable) {
            if (state.tamed) {
                if (!nbt.hasUUID("Owner")) nbt.putUUID("Owner", UUID.randomUUID());
            } else {
                nbt.remove("Owner");
            }
            nbt.putBoolean("Sitting", state.sitting);
        }


        if (state.saddled) nbt.putBoolean("Saddle", true);
        if (state.sheared) nbt.putBoolean("Sheared", true);

        if (state.parsedStates.contains("chested")) nbt.putBoolean("ChestedHorse", true);

        int genericColor = getDyeColor(state, -1);
        if (genericColor >= 0) {
            if (!nbt.contains("Color")) {
                nbt.putByte("Color", (byte) genericColor);
            }
        }


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
            }
        } else if (entityTypeName.equals("strider")) {
            nbt.putBoolean("Saddle", state.saddled);
        } else if (entityTypeName.equals("vindicator")) {
            if (state.parsedStates.contains("johnny")) {
                nbt.putString("CustomName", "{\"text\":\"Johnny\"}");
                nbt.putBoolean("CustomNameVisible", false);
            }
        } else if (entityTypeName.equals("evoker") || entityTypeName.equals("illusioner")) {
            if (state.parsedStates.contains("casting") || state.parsedStates.contains("spell")) {
                nbt.putInt("SpellTicks", 20);
            }
        } else if (entityTypeName.equals("enderman")) {
            if (state.parsedStates.contains("block") || state.parsedStates.contains("carrying")) {
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
            if (genericColor >= 0) nbt.putInt("DecorColor", genericColor);
        } else if (entityTypeName.contains("horse") || entityTypeName.equals("donkey") || entityTypeName.equals("mule")) {
            if (entityTypeName.equals("horse")) {
                int variant = getHorseVariant(state);
                if (variant >= 0) nbt.putInt("Variant", variant);

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
            int size = 1;
            if (state.parsedStates.contains("tiny")) size = 0;
            else if (state.parsedStates.contains("small")) size = 1;
            else if (state.parsedStates.contains("medium")) size = 2;
            else if (state.parsedStates.contains("large")) size = 4;
            else if (state.parsedStates.contains("huge") || state.parsedStates.contains("giant")) size = 8;

            nbt.putInt("Size", size);
        } else if (entityTypeName.equals("iron_golem")) {
            if (state.parsedStates.contains("cracked") || state.parsedStates.contains("broken")) {
                nbt.putFloat("Health", 10.0f);
            }
        } else if (entityTypeName.equals("tropical_fish")) {
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
                nbt.putInt("Invul", 100);
            }
        } else if (entityTypeName.equals("iron_golem")) {
            if (state.parsedStates.contains("cracked") || state.parsedStates.contains("broken")) {
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
                villagerData.putString("profession", profession != null ? "minecraft:" + profession : "minecraft:none");
                villagerData.putString("type", type != null ? "minecraft:" + type : "minecraft:plains");
                villagerData.putInt("level", 1);
                nbt.put("VillagerData", villagerData);
            }
        }
    }

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
        }
    }


    private static int getCatType(MobState state) {
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

    private static void updateEntityTransform(
            Entity entity,
            BlockPos pos,
            float facingYaw,
            float rotation,
            float gameTime,
            MobState state
    ) {
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

        float prevYRot = entity.getYRot();
        entity.setYRot(finalYaw);
        entity.yRotO = prevYRot;

        if (entity instanceof LivingEntity livingEntity) {
            float prevBodyRot = livingEntity.yBodyRot;
            livingEntity.yBodyRot = finalYaw;
            livingEntity.yBodyRotO = prevBodyRot;

            float prevHeadRot = livingEntity.yHeadRot;
            livingEntity.yHeadRot = finalYaw;
            livingEntity.yHeadRotO = prevHeadRot;
        }

        float bobAmount = (float) Math.sin(gameTime * 2.0f) * 0.05f;
        float baseY = pos.getY() + 1.125f;

        if (entity.getType() == EntityType.SHEEP && (state.parsedStates.contains("rainbow") || state.parsedStates.contains("jeb"))) {
            entity.tickCount = (int)(gameTime * 20);
        } else {
            entity.tickCount = 0;
            if (entity instanceof LivingEntity livingEntity) {
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
            }
        }

        entity.setPos(pos.getX() + 0.5, baseY + bobAmount, pos.getZ() + 0.5);
    }

    private static void renderEntity(
            Entity entity,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int combinedLight,
            float partialTicks,
            MobState state
    ) {
        float entityWidth = entity.getBbWidth();
        float entityHeight = entity.getBbHeight();
        float scale;

        if (state.parsedStates.contains("giant") || state.parsedStates.contains("huge")) {
            if (entity instanceof net.minecraft.world.entity.animal.Rabbit) {
                scale = 1.8f;
            } else if (entityHeight > 6.0f) {
                scale = 0.4f;
            } else {
                scale = 1.2f;
            }
        } else if (state.parsedStates.contains("large")) {
            scale = 0.9f;
        } else if (state.parsedStates.contains("medium")) {
            scale = 0.7f;
        } else if (state.parsedStates.contains("small") && (entity instanceof net.minecraft.world.entity.monster.Slime || entity instanceof net.minecraft.world.entity.monster.MagmaCube)) {
            scale = 0.8f;
        } else if (state.parsedStates.contains("tiny") && (entity instanceof net.minecraft.world.entity.monster.Slime || entity instanceof net.minecraft.world.entity.monster.MagmaCube)) {
            scale = 1.0f;
        } else {
            float targetSize = state.baby ? 0.45f : 0.8f;

            if (entityHeight <= 1.0f) {
                float maxDimension = Math.max(entityWidth, entityHeight);
                scale = targetSize / maxDimension;
                scale = Math.min(state.baby ? 1.0f : 1.5f, scale);
            } else {
                if (entityHeight > 2.5f) {
                    scale = (targetSize * 2.25f) / entityHeight;
                } else {
                    scale = state.baby ? 0.5f : 0.9f;
                }
                scale = Math.max(0.3f, scale);
            }
        }

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        @SuppressWarnings("unchecked")
        EntityRenderer<Entity> entityRenderer = (EntityRenderer<Entity>) dispatcher.getRenderer(entity);

        if (entityRenderer != null) {
            poseStack.pushPose();

            poseStack.scale(scale, scale, scale);

            if (state.upsideDown) {
                float centerOffset = entityHeight * 0.5f;
                poseStack.translate(0.0, centerOffset, 0.0);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0f));
                poseStack.translate(0.0, -centerOffset, 0.0);
            }

            entityRenderer.render(
                    entity,
                    entity.getYRot(),
                    partialTicks,
                    poseStack,
                    bufferSource,
                    combinedLight
            );

            poseStack.popPose();
        }
    }

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

    public static void clearAllEntityCaches() {
        entityCache.values().forEach(entity -> {
            if (entity != null && entity.isAlive()) {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
        });
        entityCache.clear();
        lastAppliedStates.clear();
    }

    public static void cleanupStaleEntities() {
        entityCache.entrySet().removeIf(entry -> {
            Entity entity = entry.getValue();
            boolean isStale = entity == null || !entity.isAlive();
            if (isStale && entity != null) {
                lastAppliedStates.remove(entity.getId());
            }
            return isStale;
        });

    }
}
