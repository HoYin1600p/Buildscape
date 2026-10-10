package com.kingodogo.buildscape.adapter.v121x;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.kingodogo.buildscape.item.BuildersPouchItem;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.registry.RegistrySupplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public final class RecipeFactory {
    private static final RegistrySupplier<RecipeSerializer<ShapedDurabilityRecipe>> SHAPED_DURABILITY =
            Services.REGISTRY.registerRecipeSerializer("shaped_durability", ShapedDurabilitySerializer::new);
    private static final RegistrySupplier<RecipeSerializer<ShapelessDurabilityRecipe>> SHAPELESS_DURABILITY =
            Services.REGISTRY.registerRecipeSerializer("shapeless_durability", ShapelessDurabilitySerializer::new);
    private static final RegistrySupplier<RecipeSerializer<ClearShulkerFiltersRecipe>> CLEAR_SHULKER_FILTERS =
            Services.REGISTRY.registerRecipeSerializer("clear_shulker_filters",
                    () -> new SimpleCraftingRecipeSerializer<>(ClearShulkerFiltersRecipe::new));
    private static final RegistrySupplier<RecipeSerializer<ConfettiConfigureRecipe>> CONFETTI_CONFIGURE =
            Services.REGISTRY.registerRecipeSerializer("confetti_configure",
                    () -> new SimpleCraftingRecipeSerializer<>(ConfettiConfigureRecipe::new));
    private static final RegistrySupplier<RecipeSerializer<InfinitePhoenixFireworkStarRecipe>> INFINITE_PHOENIX =
            Services.REGISTRY.registerRecipeSerializer("infinite_phoenix_firework_star",
                    () -> new SimpleCraftingRecipeSerializer<>(InfinitePhoenixFireworkStarRecipe::new));
    private static final RegistrySupplier<RecipeSerializer<CustomFireworkStarRecipe>> CUSTOM_FIREWORK_STAR =
            Services.REGISTRY.registerRecipeSerializer("custom_firework_star",
                    () -> new SimpleCraftingRecipeSerializer<>(CustomFireworkStarRecipe::new));

    private RecipeFactory() {}
    public static void register() {}

    public static final class ShapedDurabilityRecipe extends ShapedRecipe {
        private final ShapedRecipePattern recipePattern;
        private final ItemStack recipeResult;
        private final int damageAmount;
        private ShapedDurabilityRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern,
                                       ItemStack result, int damageAmount) {
            super(group, category, pattern, result);
            this.recipePattern = pattern;
            this.recipeResult = result;
            this.damageAmount = damageAmount;
        }
        @Override public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
            NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
            for (int i = 0; i < input.size(); i++) remaining.set(i,
                    com.kingodogo.buildscape.recipe.DurabilityRecipeLogic.getRemainingItem(input.getItem(i), damageAmount));
            return remaining;
        }
        @Override public RecipeSerializer<?> getSerializer() { return SHAPED_DURABILITY.get(); }
    }

    private static final class ShapedDurabilitySerializer implements RecipeSerializer<ShapedDurabilityRecipe> {
        private static final MapCodec<ShapedDurabilityRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                com.mojang.serialization.Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedDurabilityRecipe::getGroup),
                CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(ShapedDurabilityRecipe::category),
                ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.recipePattern),
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.recipeResult),
                com.mojang.serialization.Codec.INT.optionalFieldOf("damageAmount", 1).forGetter(recipe -> recipe.damageAmount)
        ).apply(instance, ShapedDurabilityRecipe::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, ShapedDurabilityRecipe> STREAM_CODEC = StreamCodec.of(
                (buffer, recipe) -> {
                    buffer.writeUtf(recipe.getGroup());
                    CraftingBookCategory.STREAM_CODEC.encode(buffer, recipe.category());
                    ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.recipePattern);
                    ItemStack.STREAM_CODEC.encode(buffer, recipe.recipeResult);
                    buffer.writeVarInt(recipe.damageAmount);
                },
                buffer -> new ShapedDurabilityRecipe(buffer.readUtf(), CraftingBookCategory.STREAM_CODEC.decode(buffer),
                        ShapedRecipePattern.STREAM_CODEC.decode(buffer), ItemStack.STREAM_CODEC.decode(buffer), buffer.readVarInt()));
        @Override public MapCodec<ShapedDurabilityRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, ShapedDurabilityRecipe> streamCodec() { return STREAM_CODEC; }
    }

    public static final class ShapelessDurabilityRecipe extends ShapelessRecipe {
        private final int damageAmount;

        private ShapelessDurabilityRecipe(String group, CraftingBookCategory category, ItemStack result,
                                          NonNullList<Ingredient> ingredients, int damageAmount) {
            super(group, category, result, ingredients);
            this.damageAmount = damageAmount;
        }

        @Override public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
            NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
            for (int i = 0; i < input.size(); i++) {
                remaining.set(i, com.kingodogo.buildscape.recipe.DurabilityRecipeLogic.getRemainingItem(
                        input.getItem(i), damageAmount));
            }
            return remaining;
        }

        @Override public RecipeSerializer<?> getSerializer() { return SHAPELESS_DURABILITY.get(); }
    }

    private static final class ShapelessDurabilitySerializer implements RecipeSerializer<ShapelessDurabilityRecipe> {
        private static final MapCodec<ShapelessDurabilityRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                com.mojang.serialization.Codec.STRING.optionalFieldOf("group", "").forGetter(ShapelessDurabilityRecipe::getGroup),
                CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(ShapelessDurabilityRecipe::category),
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.getResultItem(HolderLookup.Provider.create(java.util.stream.Stream.empty()))),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(recipe -> java.util.List.copyOf(recipe.getIngredients())),
                com.mojang.serialization.Codec.INT.optionalFieldOf("damageAmount", 1).forGetter(recipe -> recipe.damageAmount)
        ).apply(instance, (group, category, result, ingredients, damageAmount) ->
                new ShapelessDurabilityRecipe(group, category, result, toNonNullList(ingredients), damageAmount)));

        private static final StreamCodec<RegistryFriendlyByteBuf, ShapelessDurabilityRecipe> STREAM_CODEC = StreamCodec.of(
                (buffer, recipe) -> {
                    buffer.writeUtf(recipe.getGroup());
                    CraftingBookCategory.STREAM_CODEC.encode(buffer, recipe.category());
                    ItemStack.STREAM_CODEC.encode(buffer, recipe.getResultItem(buffer.registryAccess()));
                    buffer.writeVarInt(recipe.getIngredients().size());
                    for (Ingredient ingredient : recipe.getIngredients()) Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
                    buffer.writeVarInt(recipe.damageAmount);
                },
                buffer -> {
                    String group = buffer.readUtf();
                    CraftingBookCategory category = CraftingBookCategory.STREAM_CODEC.decode(buffer);
                    ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
                    int count = buffer.readVarInt();
                    NonNullList<Ingredient> ingredients = NonNullList.withSize(count, Ingredient.EMPTY);
                    for (int i = 0; i < count; i++) ingredients.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
                    return new ShapelessDurabilityRecipe(group, category, result, ingredients, buffer.readVarInt());
                });

        @Override public MapCodec<ShapelessDurabilityRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, ShapelessDurabilityRecipe> streamCodec() { return STREAM_CODEC; }

        private static NonNullList<Ingredient> toNonNullList(java.util.List<Ingredient> values) {
            NonNullList<Ingredient> ingredients = NonNullList.create();
            ingredients.addAll(values);
            return ingredients;
        }
    }

    public static final class ClearShulkerFiltersRecipe extends CustomRecipe {
        private static final String FILTERS_TAG = "GhostFilters";

        public ClearShulkerFiltersRecipe(CraftingBookCategory category) { super(category); }

        @Override public boolean matches(CraftingInput input, Level level) {
            ItemStack filtered = ItemStack.EMPTY;
            for (int i = 0; i < input.size(); i++) {
                ItemStack stack = input.getItem(i);
                if (stack.isEmpty()) continue;
                if (!filtered.isEmpty() || !isSupported(stack)) return false;
                filtered = stack;
            }
            return !filtered.isEmpty() && hasFilters(filtered);
        }

        @Override public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
            for (int i = 0; i < input.size(); i++) {
                ItemStack stack = input.getItem(i);
                if (!stack.isEmpty() && isSupported(stack) && hasFilters(stack)) {
                    ItemStack result = stack.copyWithCount(1);
                    clearFilters(result);
                    return result;
                }
            }
            return ItemStack.EMPTY;
        }

        @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 1; }
        @Override public RecipeSerializer<?> getSerializer() { return CLEAR_SHULKER_FILTERS.get(); }

        private static boolean isSupported(ItemStack stack) {
            return stack.getItem() instanceof BuildersPouchItem
                    || stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
        }

        private static CompoundTag shulkerTag(ItemStack stack) {
            CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
            return data == null ? null : data.copyTag();
        }

        private static boolean hasFilters(ItemStack stack) {
            if (stack.getItem() instanceof BuildersPouchItem) return BuildersPouchItem.hasFilters(stack);
            CompoundTag tag = shulkerTag(stack);
            if (tag == null) return false;
            if (tag.contains(FILTERS_TAG, Tag.TAG_LIST)) {
                ListTag filters = tag.getList(FILTERS_TAG, Tag.TAG_STRING);
                for (int i = 0; i < filters.size(); i++) if (!filters.getString(i).isEmpty()) return true;
            }
            if (!tag.contains("Items", Tag.TAG_LIST)) return false;
            ListTag items = tag.getList("Items", Tag.TAG_COMPOUND);
            for (int i = 0; i < items.size(); i++) {
                CompoundTag item = items.getCompound(i);
                if (item.contains("tag", Tag.TAG_COMPOUND) && item.getCompound("tag").getBoolean("ghost")) return true;
            }
            return false;
        }

        private static void clearFilters(ItemStack stack) {
            if (stack.getItem() instanceof BuildersPouchItem) { BuildersPouchItem.clearFilters(stack); return; }
            CompoundTag tag = shulkerTag(stack);
            if (tag == null) return;
            tag.remove(FILTERS_TAG);
            if (tag.contains("Items", Tag.TAG_LIST)) {
                ListTag items = tag.getList("Items", Tag.TAG_COMPOUND);
                for (int i = items.size() - 1; i >= 0; i--) {
                    CompoundTag item = items.getCompound(i);
                    if (item.contains("tag", Tag.TAG_COMPOUND) && item.getCompound("tag").getBoolean("ghost")) items.remove(i);
                }
                tag.put("Items", items);
            }
            stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
        }
    }

    public static final class ConfettiConfigureRecipe extends CustomRecipe {
        public ConfettiConfigureRecipe(CraftingBookCategory category) { super(category); }

        @Override public boolean matches(CraftingInput input, Level level) {
            int confetti = 0, gunpowder = 0;
            for (int i = 0; i < input.size(); i++) {
                ItemStack stack = input.getItem(i);
                if (stack.isEmpty()) continue;
                if (stack.is(com.kingodogo.buildscape.item.ModItems.CONFETTI_ITEM.get().asItem())) confetti++;
                else if (stack.is(net.minecraft.world.item.Items.GUNPOWDER)) gunpowder++;
                else return false;
            }
            return confetti == 1 && gunpowder >= 1;
        }

        @Override public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
            ItemStack confetti = ItemStack.EMPTY;
            int gunpowder = 0;
            for (int i = 0; i < input.size(); i++) {
                ItemStack stack = input.getItem(i);
                if (stack.is(com.kingodogo.buildscape.item.ModItems.CONFETTI_ITEM.get().asItem())) confetti = stack;
                else if (stack.is(net.minecraft.world.item.Items.GUNPOWDER)) gunpowder++;
            }
            if (confetti.isEmpty()) return ItemStack.EMPTY;
            CompoundTag data = Services.PLATFORM.getCustomData(confetti, false);
            int current = data != null && data.contains("BurstLevel") ? data.getInt("BurstLevel") : 1;
            ItemStack result = confetti.copyWithCount(1);
            int next = Math.min(5, current + gunpowder);
            Services.PLATFORM.updateCustomData(result, tag -> tag.putInt("BurstLevel", next));
            return result;
        }

        @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 2; }
        @Override public RecipeSerializer<?> getSerializer() { return CONFETTI_CONFIGURE.get(); }
    }

    public static final class InfinitePhoenixFireworkStarRecipe extends CustomRecipe {
        public InfinitePhoenixFireworkStarRecipe(CraftingBookCategory category) { super(category); }

        @Override public boolean matches(CraftingInput input, Level level) {
            int gunpowder = 0, paper = 0, star = 0;
            for (int i = 0; i < input.size(); i++) {
                ItemStack stack = input.getItem(i);
                if (stack.isEmpty()) continue;
                if (stack.is(net.minecraft.world.item.Items.GUNPOWDER)) gunpowder++;
                else if (stack.is(net.minecraft.world.item.Items.PAPER)) paper++;
                else if (stack.is(com.kingodogo.buildscape.item.ModItems.INFINITE_PHOENIX_FIREWORK_STAR.get().asItem())) star++;
                else return false;
            }
            return gunpowder >= 1 && gunpowder <= 3 && paper == 1 && star == 1;
        }

        @Override public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
            int flight = 0;
            ItemStack star = ItemStack.EMPTY;
            for (int i = 0; i < input.size(); i++) {
                ItemStack stack = input.getItem(i);
                if (stack.is(net.minecraft.world.item.Items.GUNPOWDER)) flight++;
                else if (stack.is(com.kingodogo.buildscape.item.ModItems.INFINITE_PHOENIX_FIREWORK_STAR.get().asItem())) star = stack;
            }
            int[] colors = Services.PLATFORM.getFireworkStarColors(star);
            if (colors.length == 0) colors = new int[]{0xFFFFFF, 0xFFF200, 0xFFB000, 0xFF6500, 0xE52B00};
            ItemStack result = new ItemStack(net.minecraft.world.item.Items.FIREWORK_ROCKET, 3);
            Services.PLATFORM.configureFireworkRocket(result, flight,
                    com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry.PHOENIX_ID, colors, true, true);
            return result;
        }

        @Override public net.minecraft.core.NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
            net.minecraft.core.NonNullList<ItemStack> remaining = net.minecraft.core.NonNullList.withSize(input.size(), ItemStack.EMPTY);
            for (int i = 0; i < input.size(); i++) {
                ItemStack stack = input.getItem(i);
                if (stack.is(com.kingodogo.buildscape.item.ModItems.INFINITE_PHOENIX_FIREWORK_STAR.get().asItem())) remaining.set(i, stack.copy());
            }
            return remaining;
        }

        @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 3; }
        @Override public RecipeSerializer<?> getSerializer() { return INFINITE_PHOENIX.get(); }
    }

    public static final class CustomFireworkStarRecipe extends CustomRecipe {
        public CustomFireworkStarRecipe(CraftingBookCategory category) { super(category); }

        @Override public boolean matches(CraftingInput input, Level level) {
            return com.kingodogo.buildscape.recipe.CustomFireworkRecipeLogic.matches(input.size(), input::getItem);
        }

        @Override public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
            return com.kingodogo.buildscape.recipe.CustomFireworkRecipeLogic.assemble(input.size(), input::getItem);
        }

        @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 2; }
        @Override public RecipeSerializer<?> getSerializer() { return CUSTOM_FIREWORK_STAR.get(); }
    }

    private static Ingredient parseIngredient(String s) {
        if (s == null || s.isEmpty() || "{}".equals(s)) return Ingredient.EMPTY;
        try {
            if (s.startsWith("{") || s.startsWith("[")) {
                return Ingredient.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, com.google.gson.JsonParser.parseString(s)).result().orElse(Ingredient.EMPTY);
            } else if (s.contains(":")) {
                net.minecraft.world.item.Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(s));
                if (item != null && item != net.minecraft.world.item.Items.AIR) {
                    return Ingredient.of(item);
                }
            }
        } catch (Throwable ignored) {}
        return Ingredient.EMPTY;
    }

    private static ItemStack parseResult(String itemStr, int count, String nbt) {
        net.minecraft.world.item.Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(itemStr != null && !itemStr.isBlank() ? itemStr : "minecraft:air"));
        if (item == null) item = net.minecraft.world.item.Items.AIR;
        ItemStack stack = new ItemStack(item, count > 0 ? count : 1);
        if (nbt != null && !nbt.isBlank()) {
            try {
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(net.minecraft.nbt.TagParser.parseTag(nbt)));
            } catch (Throwable ignored) {}
        }
        return stack;
    }

    public static net.minecraft.world.item.crafting.RecipeHolder<?> createRecipe(com.kingodogo.buildscape.recipe.framework.parser.RecipeIR.CompiledRecipe cr) {
        if (cr == null) return null;
        ResourceLocation id = ResourceLocation.tryParse(cr.id());
        if (id == null) return null;
        String group = cr.group();
        ItemStack result = parseResult(cr.resultItem(), cr.resultCount(), cr.resultNbt());
        String type = cr.type().toLowerCase(java.util.Locale.ROOT);
        if (type.startsWith("buildscape:")) type = type.substring("buildscape:".length());

        switch (type) {
            case "confetti_configure" -> {
                return new net.minecraft.world.item.crafting.RecipeHolder<>(id, new ConfettiConfigureRecipe(CraftingBookCategory.MISC));
            }
            case "clear_shulker_filters" -> {
                return new net.minecraft.world.item.crafting.RecipeHolder<>(id, new ClearShulkerFiltersRecipe(CraftingBookCategory.MISC));
            }
            case "shaped" -> {
                NonNullList<Ingredient> ings = NonNullList.withSize(cr.ingredients().size(), Ingredient.EMPTY);
                for (int i = 0; i < cr.ingredients().size(); i++) {
                    ings.set(i, parseIngredient(cr.ingredients().get(i)));
                }
                ShapedRecipePattern pattern = new ShapedRecipePattern(cr.width(), cr.height(), ings, java.util.Optional.empty());
                return new net.minecraft.world.item.crafting.RecipeHolder<>(id, new ShapedRecipe(group, CraftingBookCategory.MISC, pattern, result));
            }
            case "shapeless" -> {
                NonNullList<Ingredient> ings = NonNullList.create();
                for (String s : cr.ingredients()) {
                    Ingredient ing = parseIngredient(s);
                    if (!ing.isEmpty()) ings.add(ing);
                }
                return new net.minecraft.world.item.crafting.RecipeHolder<>(id, new ShapelessRecipe(group, CraftingBookCategory.MISC, result, ings));
            }
            case "shaped_durability" -> {
                NonNullList<Ingredient> ings = NonNullList.withSize(cr.ingredients().size(), Ingredient.EMPTY);
                for (int i = 0; i < cr.ingredients().size(); i++) {
                    ings.set(i, parseIngredient(cr.ingredients().get(i)));
                }
                ShapedRecipePattern pattern = new ShapedRecipePattern(cr.width(), cr.height(), ings, java.util.Optional.empty());
                int dmg = cr.damageAmount() > 0 ? cr.damageAmount() : 1;
                return new net.minecraft.world.item.crafting.RecipeHolder<>(id, new ShapedDurabilityRecipe(group, CraftingBookCategory.MISC, pattern, result, dmg));
            }
            case "shapeless_durability" -> {
                NonNullList<Ingredient> ings = NonNullList.create();
                for (String s : cr.ingredients()) {
                    Ingredient ing = parseIngredient(s);
                    if (!ing.isEmpty()) ings.add(ing);
                }
                int dmg = cr.damageAmount() > 0 ? cr.damageAmount() : 1;
                return new net.minecraft.world.item.crafting.RecipeHolder<>(id, new ShapelessDurabilityRecipe(group, CraftingBookCategory.MISC, result, ings, dmg));
            }
            case "stonecutting" -> {
                Ingredient input = parseIngredient(cr.input());
                return new net.minecraft.world.item.crafting.RecipeHolder<>(id, new net.minecraft.world.item.crafting.StonecutterRecipe(group, input, result));
            }
            case "smelting" -> {
                Ingredient input = parseIngredient(cr.input());
                return new net.minecraft.world.item.crafting.RecipeHolder<>(id, new net.minecraft.world.item.crafting.SmeltingRecipe(group, net.minecraft.world.item.crafting.CookingBookCategory.MISC, input, result, cr.experience(), cr.cookingTime()));
            }
            case "blasting" -> {
                Ingredient input = parseIngredient(cr.input());
                return new net.minecraft.world.item.crafting.RecipeHolder<>(id, new net.minecraft.world.item.crafting.BlastingRecipe(group, net.minecraft.world.item.crafting.CookingBookCategory.MISC, input, result, cr.experience(), cr.cookingTime()));
            }
            case "smoking" -> {
                Ingredient input = parseIngredient(cr.input());
                return new net.minecraft.world.item.crafting.RecipeHolder<>(id, new net.minecraft.world.item.crafting.SmokingRecipe(group, net.minecraft.world.item.crafting.CookingBookCategory.MISC, input, result, cr.experience(), cr.cookingTime()));
            }
            case "campfire", "campfire_cooking" -> {
                Ingredient input = parseIngredient(cr.input());
                return new net.minecraft.world.item.crafting.RecipeHolder<>(id, new net.minecraft.world.item.crafting.CampfireCookingRecipe(group, net.minecraft.world.item.crafting.CookingBookCategory.MISC, input, result, cr.experience(), cr.cookingTime()));
            }
            case "smithing" -> {
                Ingredient base = parseIngredient(cr.input());
                Ingredient addition = parseIngredient(cr.addition());
                return new net.minecraft.world.item.crafting.RecipeHolder<>(id, new net.minecraft.world.item.crafting.SmithingTransformRecipe(Ingredient.EMPTY, base, addition, result));
            }
            default -> {
                return null;
            }
        }
    }

    public static void injectRecipes(net.minecraft.world.item.crafting.RecipeManager recipeManager,
                                     java.util.List<com.kingodogo.buildscape.recipe.framework.parser.RecipeIR.CompiledRecipe> recipes) {
        if (recipeManager == null || recipes == null || recipes.isEmpty()) return;
        java.util.Map<ResourceLocation, net.minecraft.world.item.crafting.RecipeHolder<?>> merged = new java.util.LinkedHashMap<>();
        for (net.minecraft.world.item.crafting.RecipeHolder<?> existing : recipeManager.getRecipes()) {
            if (existing != null) merged.put(existing.id(), existing);
        }
        for (com.kingodogo.buildscape.recipe.framework.parser.RecipeIR.CompiledRecipe cr : recipes) {
            net.minecraft.world.item.crafting.RecipeHolder<?> holder = createRecipe(cr);
            if (holder != null) merged.put(holder.id(), holder);
        }
        recipeManager.replaceRecipes(merged.values());
    }
}
