package com.kingodogo.buildscape.parity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

final class RegistryDump {
    private RegistryDump() {}

    static <T> JsonObject entries(Registry<T> registry, Function<T, JsonElement> describe) {
        JsonObject result = new JsonObject();
        registry.keySet().stream().filter(DumpIO::included).sorted().forEach(id -> result.add(id.toString(), describe.apply(registry.get(id))));
        return result;
    }

    static Map<String, JsonElement> common() {
        Map<String, JsonElement> files = new TreeMap<>();
        files.put("blocks.json", entries(Registry.BLOCK, RegistryDump::block));
        files.put("items.json", entries(Registry.ITEM, RegistryDump::item));
        files.put("creative-tabs.json", tabs());
        files.put("block-entities.json", entries(Registry.BLOCK_ENTITY_TYPE, type -> {
            List<String> valid = new ArrayList<>();
            // isValid queries the real set without instantiating block entities.
            Registry.BLOCK.keySet().stream().sorted().forEach(id -> {
                if (type.isValid(Registry.BLOCK.get(id).defaultBlockState())) valid.add(id.toString());
            });
            return DumpIO.object("implementation", DumpIO.implementation(type), "validBlocks", valid);
        }));
        files.put("entities.json", entries(Registry.ENTITY_TYPE, type -> DumpIO.object(
                "implementation", DumpIO.implementation(type), "category", type.getCategory().getName(),
                "width", type.getWidth(), "height", type.getHeight(), "save", type.canSerialize(),
                "summon", type.canSummon(), "trackingRange", type.clientTrackingRange(), "updateInterval", type.updateInterval())));
        files.put("menus.json", entries(Registry.MENU, type -> DumpIO.object("implementation", DumpIO.implementation(type))));
        files.put("particles.json", entries(Registry.PARTICLE_TYPE, type -> DumpIO.object(
                "implementation", DumpIO.implementation(type), "overrideLimiter", type.getOverrideLimiter())));
        files.put("sounds.json", entries(Registry.SOUND_EVENT, sound -> DumpIO.object("location", sound.getLocation())));
        JsonObject allRegistries = new JsonObject();
        Registry.REGISTRY.forEach(registry -> allRegistries.add(registry.key().location().toString(),
                entries(registry, entry -> DumpIO.object("implementation", DumpIO.implementation(entry)))));
        files.put("registry-entries.json", allRegistries);
        files.put("mixins.json", MixinDump.snapshot());
        return files;
    }

    static Map<String, JsonElement> server(MinecraftServer server) {
        Map<String, JsonElement> files = common();
        RegistryAccess access = server.registryAccess();
        files.put("configured-features.json", features(access.registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY), ConfiguredFeature.DIRECT_CODEC, access));
        files.put("placed-features.json", features(access.registryOrThrow(Registry.PLACED_FEATURE_REGISTRY), PlacedFeature.DIRECT_CODEC, access));
        JsonObject recipes = new JsonObject();
        server.getRecipeManager().getRecipes().stream().filter(recipe -> DumpIO.included(recipe.getId()))
                .sorted(Comparator.comparing(recipe -> recipe.getId().toString())).forEach(recipe -> {
                    JsonArray ingredients = new JsonArray();
                    recipe.getIngredients().forEach(ingredient -> ingredients.add(ingredient.toJson()));
                    recipes.add(recipe.getId().toString(), DumpIO.object("id", recipe.getId(),
                            "type", Registry.RECIPE_TYPE.getKey(recipe.getType()),
                            "serializer", Registry.RECIPE_SERIALIZER.getKey(recipe.getSerializer()),
                            "result", stack(recipe.getResultItem()), "ingredients", ingredients,
                            "group", recipe.getGroup(), "special", recipe.isSpecial()));
                });
        files.put("recipes.json", recipes);
        files.put("loot-tables.json", DumpIO.json(server.getLootTables().getIds().stream()
                .filter(DumpIO::included).map(ResourceLocation::toString).sorted().toList()));
        return files;
    }

    private static <T> JsonObject features(Registry<T> registry, Codec<T> codec, RegistryAccess access) {
        var ops = RegistryOps.create(JsonOps.INSTANCE, access);
        return entries(registry, feature -> codec.encodeStart(ops, feature).getOrThrow(false,
                message -> { throw new IllegalStateException("Feature serialization: " + message); }));
    }

    static JsonElement stack(ItemStack stack) {
        return DumpIO.nbt(stack.save(new CompoundTag()));
    }

    private static JsonElement item(Item item) {
        ItemStack stack = new ItemStack(item);
        FoodProperties food = item.getFoodProperties(stack, null);
        JsonElement foodJson = null;
        if (food != null) {
            JsonArray effects = new JsonArray();
            food.getEffects().forEach(pair -> effects.add(DumpIO.object("effect",
                    pair.getFirst() == null ? null : DumpIO.nbt(pair.getFirst().save(new CompoundTag())),
                    "probability", pair.getSecond())));
            foodJson = DumpIO.object("nutrition", food.getNutrition(), "saturation", food.getSaturationModifier(),
                    "meat", food.isMeat(), "alwaysEat", food.canAlwaysEat(), "fast", food.isFastFood(), "effects", effects);
        }
        // Forge supports multiple tabs; enumerate those instead of only the vanilla primary tab.
        List<String> tabs = item.getCreativeTabs().stream().filter(java.util.Objects::nonNull)
                .map(RegistryDump::tabId).sorted().toList();
        return DumpIO.object("implementation", DumpIO.implementation(item), "maxStack", stack.getMaxStackSize(),
                "creativeTabs", tabs, "rarity", item.getRarity(stack).name(), "food", foodJson,
                "tags", item.builtInRegistryHolder().tags().map(tag -> tag.location().toString()).sorted().toList());
    }

    private static String tabId(CreativeModeTab tab) {
        return tab.getId() + ":" + tab.getRecipeFolderName();
    }

    private static JsonElement tabs() {
        JsonObject result = new JsonObject();
        for (CreativeModeTab tab : CreativeModeTab.TABS) {
            if (tab == null) continue;
            NonNullList<ItemStack> items = NonNullList.create();
            tab.fillItemList(items);
            JsonArray order = new JsonArray();
            // Keep absolute indices so ordering relative to other mods is visible.
            for (int index = 0; index < items.size(); index++) {
                ItemStack stack = items.get(index);
                if (DumpIO.included(Registry.ITEM.getKey(stack.getItem()))) {
                    order.add(DumpIO.object("index", index, "stack", stack(stack)));
                }
            }
            result.add(tabId(tab), order);
        }
        return result;
    }

    private static <T extends Comparable<T>> String propertyValue(Property<T> property, Comparable<?> value) {
        return property.getName((T) value);
    }

    static String stateId(BlockState state) {
        TreeMap<String, String> values = new TreeMap<>();
        state.getValues().forEach((property, value) -> values.put(property.getName(), propertyValue(property, value)));
        return values.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(java.util.stream.Collectors.joining(","));
    }

    private static JsonElement block(Block block) {
        JsonObject properties = new JsonObject();
        block.getStateDefinition().getProperties().forEach(property -> {
            List<String> values = new ArrayList<>();
            property.getPossibleValues().forEach(value -> values.add(propertyValue(property, value)));
            values.sort(String::compareTo);
            properties.add(property.getName(), DumpIO.json(values));
        });
        JsonObject states = new JsonObject();
        block.getStateDefinition().getPossibleStates().forEach(state -> states.add(stateId(state), DumpIO.object(
                "destroyTime", state.getDestroySpeed(EmptyBlockGetter.INSTANCE, BlockPos.ZERO),
                "explosionResistance", block.getExplosionResistance(), "light", state.getLightEmission(),
                "sound", sound(state.getSoundType()))));
        return DumpIO.object("implementation", DumpIO.implementation(block), "properties", properties,
                "defaultState", stateId(block.defaultBlockState()), "states", states,
                "tags", block.builtInRegistryHolder().tags().map(tag -> tag.location().toString()).sorted().toList());
    }

    private static JsonElement sound(SoundType sound) {
        return DumpIO.object("volume", sound.getVolume(), "pitch", sound.getPitch(),
                "break", sound.getBreakSound().getLocation(), "step", sound.getStepSound().getLocation(),
                "place", sound.getPlaceSound().getLocation(), "hit", sound.getHitSound().getLocation(),
                "fall", sound.getFallSound().getLocation());
    }
}
