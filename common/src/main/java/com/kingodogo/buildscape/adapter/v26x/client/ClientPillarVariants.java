package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.client.renderer.MobState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Named display variants, using the native saved-data codecs and entity components. */
public final class ClientPillarVariants {
    private ClientPillarVariants() {}

    public static void saveVariants(Entity entity, MobState state, CompoundTag data) {
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
            }
            case "parrot" -> number(data, "Variant", namedIndex(state, "red|cookie", "blue", "green", "cyan", "gray"));
            case "llama", "trader_llama" -> {
                number(data, "Variant", namedIndex(state, "creamy", "white", "brown", "gray"));
                data.putInt("Strength", 5);
            }
            case "fox" -> text(data, "Type", named(state, "red", "snow|white"));
            case "mooshroom" -> text(data, "Type", named(state, "red", "brown"));
            case "panda" -> {
                String gene = named(state, "normal", "lazy", "worried", "playful", "brown", "weak", "aggressive");
                text(data, "MainGene", gene);
                text(data, "HiddenGene", gene);
            }
            case "phantom" -> data.putInt("size", ClientPillarEntities.slimeSize(state) - 1);
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

    public static void applyComponents(Entity entity, MobState state) {
        var registry = entity.level().registryAccess();
        if (entity instanceof net.minecraft.world.entity.animal.feline.Cat) {
            String variant = named(state, "tabby", "black|tuxedo", "red|orange", "siamese", "british_shorthair|british",
                    "calico", "persian", "ragdoll", "white", "jellie", "all_black|midnight");
            if (variant != null) entity.setComponent(DataComponents.CAT_VARIANT, registry.lookupOrThrow(Registries.CAT_VARIANT)
                    .getOrThrow(ResourceKey.create(Registries.CAT_VARIANT, Identifier.withDefaultNamespace(variant))));
            entity.setComponent(DataComponents.CAT_COLLAR, namedColor(state));
        }
        if (entity instanceof net.minecraft.world.entity.animal.wolf.Wolf)
            entity.setComponent(DataComponents.WOLF_COLLAR, namedColor(state));
        if (entity instanceof net.minecraft.world.entity.animal.frog.Frog) {
            String variant = named(state, "temperate", "warm", "cold");
            if (variant != null) entity.setComponent(DataComponents.FROG_VARIANT, registry.lookupOrThrow(Registries.FROG_VARIANT)
                    .getOrThrow(ResourceKey.create(Registries.FROG_VARIANT, Identifier.withDefaultNamespace(variant))));
        }
        if (entity instanceof net.minecraft.world.entity.animal.equine.AbstractHorse horse) {
            horse.setItemSlot(EquipmentSlot.SADDLE, state.saddled ? new ItemStack(Items.SADDLE) : ItemStack.EMPTY);
            if (entity instanceof net.minecraft.world.entity.animal.equine.Horse) {
                String armor = named(state, "diamond|diamond_armor", "gold|gold_armor", "iron|iron_armor", "leather|leather_armor");
                horse.setItemSlot(EquipmentSlot.BODY, armor == null ? ItemStack.EMPTY : new ItemStack(switch (armor) {
                    case "diamond" -> Items.DIAMOND_HORSE_ARMOR;
                    case "gold" -> Items.GOLDEN_HORSE_ARMOR;
                    case "iron" -> Items.IRON_HORSE_ARMOR;
                    default -> Items.LEATHER_HORSE_ARMOR;
                }));
            }
        }
        if (entity instanceof net.minecraft.world.entity.animal.pig.Pig
                || entity instanceof net.minecraft.world.entity.monster.Strider)
            ((net.minecraft.world.entity.LivingEntity) entity).setItemSlot(EquipmentSlot.SADDLE,
                    state.saddled ? new ItemStack(Items.SADDLE) : ItemStack.EMPTY);
        if (entity instanceof net.minecraft.world.entity.animal.equine.Llama llama) {
            boolean decorated = false;
            for (DyeColor color : DyeColor.values()) {
                if (state.parsedStates.contains(color.getName())) {
                    var item = net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(
                            Identifier.withDefaultNamespace(color.getName() + "_carpet"));
                    llama.setItemSlot(EquipmentSlot.BODY, new ItemStack(item));
                    decorated = true;
                    break;
                }
            }
            if (!decorated) llama.setItemSlot(EquipmentSlot.BODY, ItemStack.EMPTY);
        }
        if (entity instanceof net.minecraft.world.entity.animal.fish.TropicalFish) {
            for (var pattern : net.minecraft.world.entity.animal.fish.TropicalFish.Pattern.values()) {
                if (state.parsedStates.contains(pattern.getSerializedName())
                        || pattern == net.minecraft.world.entity.animal.fish.TropicalFish.Pattern.BRINELY
                        && state.parsedStates.contains("brinely")) {
                    entity.setComponent(DataComponents.TROPICAL_FISH_PATTERN, pattern);
                    break;
                }
            }
        }
        if (entity instanceof com.kingodogo.buildscape.mixinsupport.DisplaySpellcaster caster)
            caster.buildscape$setDisplayCasting(state.parsedStates.contains("casting") || state.parsedStates.contains("spell"));
        if (entity instanceof net.minecraft.world.entity.animal.rabbit.Rabbit && state.parsedStates.contains("toast"))
            entity.setCustomName(Component.literal("Toast"));
        if (entity.getType() == net.minecraft.world.entity.EntityTypes.VINDICATOR && state.parsedStates.contains("johnny"))
            entity.setCustomName(Component.literal("Johnny"));
        if (entity instanceof net.minecraft.world.entity.monster.EnderMan enderman)
            enderman.setTarget(state.parsedStates.contains("screaming") || state.parsedStates.contains("staring")
                    ? net.minecraft.client.Minecraft.getInstance().player : null);
    }

    public static int namedIndex(MobState state, String... names) {
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
