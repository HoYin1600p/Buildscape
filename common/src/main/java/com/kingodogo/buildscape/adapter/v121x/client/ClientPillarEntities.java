package com.kingodogo.buildscape.adapter.v121x.client;

import com.kingodogo.buildscape.client.renderer.MobState;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;

/** 1.21.1 pillar display entities: mobs from spawn eggs and the armour stand used for gear. */
public final class ClientPillarEntities {
    private ClientPillarEntities() {}

    public static Entity createArmorStand(Level level, double x, double y, double z) {
        Entity stand = EntityType.ARMOR_STAND.create(level);
        if (stand != null) stand.setPos(x, y, z);
        return stand;
    }

    public static void setupArmorStand(Entity entity) {
        if (!(entity instanceof ArmorStand stand)) return;
        stand.setShowArms(true);
        stand.setNoBasePlate(true);
        stand.setInvisible(true);
        stand.setNoGravity(true);
        stand.noPhysics = true;
    }

    public static void updateArmorStand(Entity entity, ItemStack stack, EquipmentSlot slot, boolean standItem) {
        if (!(entity instanceof ArmorStand stand)) return;
        for (EquipmentSlot equipment : EquipmentSlot.values()) stand.setItemSlot(equipment, ItemStack.EMPTY);
        stand.setInvisible(!standItem);
        stand.setNoBasePlate(!standItem);
        if (!standItem && slot != null) stand.setItemSlot(slot, stack.copy());
    }

    public static Entity createMob(ItemStack egg, Level level, BlockPos pos, Object argument) {
        MobState state = (MobState) argument;
        EntityType<?> eggType = egg.getItem() instanceof SpawnEggItem spawnEgg ? spawnEgg.getType(egg) : null;
        if (eggType == null) return null;
        EntityType<?> displayType = state.parsedStates.contains("giant")
                && (eggType == EntityType.ZOMBIE || eggType == EntityType.HUSK || eggType == EntityType.DROWNED)
                ? EntityType.GIANT : eggType;
        Entity entity = displayType.create(level);
        if (entity != null) {
            applyMobState(entity, state);
            entity.setPos(pos.getX() + 0.5, pos.getY() + 1.125, pos.getZ() + 0.5);
            entity.noPhysics = true;
        }
        return entity;
    }

    public static void applyMobState(Entity entity, Object argument) {
        MobState state = (MobState) argument;
        // Retain unrelated saved fields when applying states, as the reference renderer did.
        CompoundTag data = new CompoundTag();
        entity.saveWithoutId(data);
        data.putBoolean("powered", state.charged || state.powered);
        data.putBoolean("ignited", state.parsedStates.contains("ignite") || state.parsedStates.contains("ignited"));
        data.putBoolean("ChestedHorse", state.parsedStates.contains("chested"));
        data.putBoolean("Sitting", state.sitting);
        data.putBoolean("Sleeping", state.parsedStates.contains("sleep") || state.parsedStates.contains("sleeping"));
        data.putBoolean("Crouching", state.parsedStates.contains("crouch") || state.parsedStates.contains("crouching"));
        data.putBoolean("HasNectar", state.parsedStates.contains("nectar"));
        data.putBoolean("HasStung", state.parsedStates.contains("stung"));
        data.putBoolean("IsScreamingGoat", state.parsedStates.contains("scream") || state.parsedStates.contains("screaming"));
        boolean horns = !state.parsedStates.contains("no_horns") && !state.parsedStates.contains("nohorns");
        data.putBoolean("HasLeftHorn", horns);
        data.putBoolean("HasRightHorn", horns);
        data.putBoolean("Pumpkin", !state.parsedStates.contains("no_pumpkin"));
        data.putByte("Peek", (byte) (state.parsedStates.contains("open") ? 100 : 0));
        data.putInt("PuffState", state.parsedStates.contains("full") || state.parsedStates.contains("puff")
                ? 2 : state.parsedStates.contains("half") ? 1 : 0);
        saveVariants(entity, state, data);
        entity.load(data);
        entity.setNoGravity(true);
        entity.setInvulnerable(true);
        entity.setSilent(true);
        entity.setInvisible(state.invisible);
        entity.setGlowingTag(state.glowing);
        entity.setRemainingFireTicks(state.fire ? 20 : 0);
        entity.setTicksFrozen(state.frozen ? 140 : 0);
        if (entity instanceof Mob mob) {
            mob.setNoAi(true);
            mob.setBaby(state.baby);
            mob.setLeftHanded(state.parsedStates.contains("lefty") || state.parsedStates.contains("left_handed"));
        }
        if (entity instanceof LivingEntity living) {
            living.hurtTime = state.parsedStates.contains("hurt") || state.parsedStates.contains("damage") ? 10 : 0;
            living.deathTime = 0;
            living.hurtDuration = living.hurtTime;
            living.walkAnimation.setSpeed(0.0F);
            living.setDeltaMovement(0, 0, 0);
            living.setSpeed(0);
        }
        if (entity instanceof TamableAnimal tameable) {
            tameable.setTame(state.tamed, false);
            tameable.setInSittingPose(state.sitting);
        }
        if (entity instanceof net.minecraft.world.entity.animal.Sheep sheep) {
            sheep.setSheared(state.sheared);
            sheep.setColor(dyeColor(state));
            sheep.setCustomName(state.parsedStates.contains("jeb") || state.parsedStates.contains("rainbow")
                    ? Component.literal("jeb_") : null);
        }
        if (entity instanceof net.minecraft.world.entity.animal.Wolf wolf) {
            wolf.setRemainingPersistentAngerTime(state.angry ? 999999 : 0);
            wolf.setIsInterested(state.parsedStates.contains("begging"));
        }
        if (entity instanceof net.minecraft.world.entity.animal.Bee bee)
            bee.setRemainingPersistentAngerTime(state.angry ? 999999 : 0);
        if (entity instanceof net.minecraft.world.entity.ambient.Bat bat)
            bat.setResting(state.parsedStates.contains("hanging") || state.parsedStates.contains("roosting"));
        if (entity instanceof net.minecraft.world.entity.animal.PolarBear bear)
            bear.setStanding(state.parsedStates.contains("standing") || state.parsedStates.contains("rearing"));
        if (entity instanceof net.minecraft.world.entity.monster.Spider spider)
            spider.setClimbing(state.parsedStates.contains("climbing"));
        if (entity instanceof net.minecraft.world.entity.monster.Vex vex)
            vex.setIsCharging(state.parsedStates.contains("charging"));
        if (entity instanceof net.minecraft.world.entity.monster.Slime slime) slime.setSize(slimeSize(state), true);
        applyEquipment(entity, state);
    }

    private static void applyEquipment(Entity entity, MobState state) {
        if (entity instanceof net.minecraft.world.entity.animal.horse.Horse horse) {
            String armor = named(state, "diamond|diamond_armor", "gold|gold_armor", "iron|iron_armor", "leather|leather_armor");
            horse.setItemSlot(EquipmentSlot.BODY, armor == null ? ItemStack.EMPTY : new ItemStack(switch (armor) {
                case "diamond" -> net.minecraft.world.item.Items.DIAMOND_HORSE_ARMOR;
                case "gold" -> net.minecraft.world.item.Items.GOLDEN_HORSE_ARMOR;
                case "iron" -> net.minecraft.world.item.Items.IRON_HORSE_ARMOR;
                default -> net.minecraft.world.item.Items.LEATHER_HORSE_ARMOR;
            }));
        }
        if (entity instanceof net.minecraft.world.entity.animal.horse.Llama llama) {
            ItemStack carpet = ItemStack.EMPTY;
            for (DyeColor color : DyeColor.values()) {
                if (state.parsedStates.contains(color.getName())) {
                    var item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
                            net.minecraft.resources.ResourceLocation.withDefaultNamespace(color.getName() + "_carpet"));
                    carpet = new ItemStack(item);
                    break;
                }
            }
            llama.setItemSlot(EquipmentSlot.BODY, carpet);
        }
        if (entity instanceof net.minecraft.world.entity.monster.EnderMan enderman) {
            enderman.setTarget(state.parsedStates.contains("screaming") || state.parsedStates.contains("staring")
                    ? net.minecraft.client.Minecraft.getInstance().player : null);
        }
        if (entity instanceof net.minecraft.world.entity.animal.Rabbit && state.parsedStates.contains("toast"))
            entity.setCustomName(Component.literal("Toast"));
        if (entity.getType() == EntityType.VINDICATOR && state.parsedStates.contains("johnny"))
            entity.setCustomName(Component.literal("Johnny"));
    }

    private static void saveVariants(Entity entity, MobState state, CompoundTag data) {
        String type = entity.getType().getDescriptionId();
        type = type.substring(type.lastIndexOf('.') + 1);
        switch (type) {
            case "axolotl" -> number(data, "Variant", namedIndex(state, "lucy|pink", "wild|brown", "gold|yellow", "cyan", "blue"));
            case "rabbit" -> number(data, "RabbitType", state.parsedStates.contains("killer") ? 99
                    : namedIndex(state, "brown", "white", "black", "white_splotched|spotted", "gold", "salt"));
            case "horse" -> {
                number(data, "Variant", state.parsedStates.contains("dark") && state.parsedStates.contains("brown") ? 6
                        : namedIndex(state, "white", "creamy", "chestnut", "brown", "black", "gray", "dark_brown"));
                data.putBoolean("Tame", !state.baby);
                if (state.saddled) data.put("SaddleItem", saddle());
            }
            case "donkey", "mule" -> {
                data.putBoolean("Tame", !state.baby);
                if (state.saddled) data.put("SaddleItem", saddle());
            }
            case "pig", "strider" -> data.putBoolean("Saddle", state.saddled);
            case "parrot" -> number(data, "Variant", namedIndex(state, "red|cookie", "blue", "green", "cyan", "gray"));
            case "llama", "trader_llama" -> {
                number(data, "Variant", namedIndex(state, "creamy", "white", "brown", "gray"));
                data.putInt("Strength", 5);
            }
            case "cat" -> {
                text(data, "variant", prefixed(named(state, "tabby", "black|tuxedo", "red|orange", "siamese",
                        "british_shorthair|british", "calico", "persian", "ragdoll", "white", "jellie", "all_black|midnight")));
                data.putByte("CollarColor", (byte) namedColor(state).getId());
            }
            case "wolf" -> data.putByte("CollarColor", (byte) namedColor(state).getId());
            case "frog" -> text(data, "variant", prefixed(named(state, "temperate", "warm", "cold")));
            case "fox" -> text(data, "Type", named(state, "red", "snow|white"));
            case "mooshroom" -> text(data, "Type", named(state, "red", "brown"));
            case "panda" -> {
                String gene = named(state, "normal", "lazy", "worried", "playful", "brown", "weak", "aggressive");
                text(data, "MainGene", gene);
                text(data, "HiddenGene", gene);
            }
            case "phantom" -> data.putInt("Size", slimeSize(state) - 1);
            case "iron_golem" -> {
                if (state.parsedStates.contains("cracked") || state.parsedStates.contains("broken")) data.putFloat("Health", 10);
            }
            case "wither" -> data.putInt("Invul", state.parsedStates.contains("shield") || state.parsedStates.contains("invul") ? 100 : 0);
            case "evoker", "illusioner" -> data.putInt("SpellTicks",
                    state.parsedStates.contains("casting") || state.parsedStates.contains("spell") ? 20 : 0);
            case "armor_stand" -> {
                data.putBoolean("ShowArms", state.parsedStates.contains("arms") || state.parsedStates.contains("show_arms"));
                data.putBoolean("Small", state.baby || state.parsedStates.contains("small"));
                data.putBoolean("NoBasePlate", state.parsedStates.contains("no_base") || state.parsedStates.contains("nobase"));
            }
            case "end_crystal" -> data.putBoolean("ShowBottom", !state.parsedStates.contains("no_bottom"));
            case "enderman" -> {
                if (state.parsedStates.contains("block") || state.parsedStates.contains("carrying")) {
                    CompoundTag block = new CompoundTag();
                    block.putString("Name", "minecraft:grass_block");
                    data.put("carriedBlockState", block);
                } else data.remove("carriedBlockState");
            }
            case "villager", "zombie_villager" -> {
                String profession = named(state, "farmer", "fisherman", "shepherd", "fletcher", "librarian", "cartographer",
                        "cleric", "armorer", "weaponsmith", "toolsmith", "butcher", "leatherworker", "mason", "nitwit", "none");
                String biome = named(state, "desert", "jungle", "savanna", "snow|snowy", "swamp", "taiga", "plains");
                if (profession != null || biome != null) {
                    CompoundTag villager = new CompoundTag();
                    villager.putString("profession", "minecraft:" + (profession == null ? "none" : profession));
                    villager.putString("type", "minecraft:" + (biome == null ? "plains" : biome));
                    villager.putInt("level", 1);
                    data.put("VillagerData", villager);
                }
            }
            default -> { }
        }
    }

    private static CompoundTag saddle() {
        CompoundTag saddle = new CompoundTag();
        saddle.putString("id", "minecraft:saddle");
        saddle.putInt("count", 1);
        return saddle;
    }

    private static String prefixed(String name) {
        return name == null ? null : "minecraft:" + name;
    }

    public static DyeColor dyeColor(MobState state) {
        for (DyeColor color : DyeColor.values()) if (state.parsedStates.contains(color.getName())) return color;
        return DyeColor.WHITE;
    }

    public static int slimeSize(MobState state) {
        if (state.parsedStates.contains("tiny")) return 1;
        if (state.parsedStates.contains("medium")) return 3;
        if (state.parsedStates.contains("large")) return 5;
        if (state.parsedStates.contains("huge") || state.parsedStates.contains("giant")) return 9;
        return 2;
    }

    private static int namedIndex(MobState state, String... names) {
        for (int i = 0; i < names.length; i++)
            for (String alias : names[i].split("\\|")) if (state.parsedStates.contains(alias)) return i;
        return -1;
    }

    private static String named(MobState state, String... names) {
        int index = namedIndex(state, names);
        return index < 0 ? null : names[index].split("\\|")[0];
    }

    private static DyeColor namedColor(MobState state) {
        for (DyeColor color : DyeColor.values()) if (state.parsedStates.contains(color.getName())) return color;
        return DyeColor.RED;
    }

    private static void number(CompoundTag data, String key, int value) { if (value >= 0) data.putInt(key, value); }

    private static void text(CompoundTag data, String key, String value) { if (value != null) data.putString(key, value); }
}
