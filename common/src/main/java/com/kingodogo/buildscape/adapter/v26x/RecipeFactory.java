package com.kingodogo.buildscape.adapter.v26x;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.kingodogo.buildscape.item.BuildersPouchItem;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.registry.RegistrySupplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public final class RecipeFactory {
    private static final RegistrySupplier<RecipeSerializer<ShapedDurabilityRecipe>> SHAPED_DURABILITY =
            Services.REGISTRY.registerRecipeSerializer("shaped_durability",
                    () -> new RecipeSerializer<>(ShapedDurabilityRecipe.MAP_CODEC, ShapedDurabilityRecipe.STREAM_CODEC));
    private static final RegistrySupplier<RecipeSerializer<ShapelessDurabilityRecipe>> SHAPELESS_DURABILITY =
            Services.REGISTRY.registerRecipeSerializer("shapeless_durability",
                    () -> new RecipeSerializer<>(ShapelessDurabilityRecipe.MAP_CODEC, ShapelessDurabilityRecipe.STREAM_CODEC));
    private static final ClearShulkerFiltersRecipe CLEAR_SHULKER_FILTERS_RECIPE = new ClearShulkerFiltersRecipe();
    private static final RegistrySupplier<RecipeSerializer<ClearShulkerFiltersRecipe>> CLEAR_SHULKER_FILTERS =
            Services.REGISTRY.registerRecipeSerializer("clear_shulker_filters",
                    () -> new RecipeSerializer<>(com.mojang.serialization.MapCodec.unit(CLEAR_SHULKER_FILTERS_RECIPE),
                            net.minecraft.network.codec.StreamCodec.unit(CLEAR_SHULKER_FILTERS_RECIPE)));
    private static final ConfettiConfigureRecipe CONFETTI_CONFIGURE_RECIPE = new ConfettiConfigureRecipe();
    private static final RegistrySupplier<RecipeSerializer<ConfettiConfigureRecipe>> CONFETTI_CONFIGURE =
            Services.REGISTRY.registerRecipeSerializer("confetti_configure",
                    () -> new RecipeSerializer<>(com.mojang.serialization.MapCodec.unit(CONFETTI_CONFIGURE_RECIPE),
                            net.minecraft.network.codec.StreamCodec.unit(CONFETTI_CONFIGURE_RECIPE)));
    private static final InfinitePhoenixFireworkStarRecipe INFINITE_PHOENIX_RECIPE = new InfinitePhoenixFireworkStarRecipe();
    private static final RegistrySupplier<RecipeSerializer<InfinitePhoenixFireworkStarRecipe>> INFINITE_PHOENIX =
            Services.REGISTRY.registerRecipeSerializer("infinite_phoenix_firework_star",
                    () -> new RecipeSerializer<>(com.mojang.serialization.MapCodec.unit(INFINITE_PHOENIX_RECIPE),
                            net.minecraft.network.codec.StreamCodec.unit(INFINITE_PHOENIX_RECIPE)));
    private static final CustomFireworkStarRecipe CUSTOM_FIREWORK_STAR_RECIPE = new CustomFireworkStarRecipe();
    private static final RegistrySupplier<RecipeSerializer<CustomFireworkStarRecipe>> CUSTOM_FIREWORK_STAR =
            Services.REGISTRY.registerRecipeSerializer("custom_firework_star",
                    () -> new RecipeSerializer<>(com.mojang.serialization.MapCodec.unit(CUSTOM_FIREWORK_STAR_RECIPE),
                            net.minecraft.network.codec.StreamCodec.unit(CUSTOM_FIREWORK_STAR_RECIPE)));

    private RecipeFactory() {}
    public static void register() {}

    public static final class ShapedDurabilityRecipe extends ShapedRecipe {
        private final ShapedRecipePattern recipePattern;
        private final ItemStackTemplate recipeResult;
        private final int damageAmount;
        private static final MapCodec<ShapedDurabilityRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Recipe.CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
                CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(recipe -> recipe.bookInfo),
                ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.recipePattern),
                ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.recipeResult),
                com.mojang.serialization.Codec.INT.optionalFieldOf("damageAmount", 1).forGetter(recipe -> recipe.damageAmount)
        ).apply(instance, ShapedDurabilityRecipe::new));
        private static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, ShapedDurabilityRecipe> STREAM_CODEC =
                net.minecraft.network.codec.StreamCodec.of(
                        (buffer, recipe) -> {
                            Recipe.CommonInfo.STREAM_CODEC.encode(buffer, recipe.commonInfo);
                            CraftingRecipe.CraftingBookInfo.STREAM_CODEC.encode(buffer, recipe.bookInfo);
                            ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.recipePattern);
                            ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.recipeResult);
                            buffer.writeVarInt(recipe.damageAmount);
                        },
                        buffer -> new ShapedDurabilityRecipe(Recipe.CommonInfo.STREAM_CODEC.decode(buffer),
                                CraftingRecipe.CraftingBookInfo.STREAM_CODEC.decode(buffer),
                                ShapedRecipePattern.STREAM_CODEC.decode(buffer), ItemStackTemplate.STREAM_CODEC.decode(buffer),
                                buffer.readVarInt()));
        private ShapedDurabilityRecipe(Recipe.CommonInfo common, CraftingRecipe.CraftingBookInfo book,
                                       ShapedRecipePattern pattern, ItemStackTemplate result, int damageAmount) {
            super(common, book, pattern, result);
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
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override public RecipeSerializer<ShapedRecipe> getSerializer() { return (RecipeSerializer) SHAPED_DURABILITY.get(); }
    }

    public static final class ShapelessDurabilityRecipe extends ShapelessRecipe {
        private final ItemStackTemplate recipeResult;
        private final java.util.List<Ingredient> recipeIngredients;
        private final int damageAmount;

        private static final MapCodec<ShapelessDurabilityRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Recipe.CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
                CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(recipe -> recipe.bookInfo),
                ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.recipeResult),
                Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(recipe -> recipe.recipeIngredients),
                com.mojang.serialization.Codec.INT.optionalFieldOf("damageAmount", 1).forGetter(recipe -> recipe.damageAmount)
        ).apply(instance, ShapelessDurabilityRecipe::new));

        private static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, ShapelessDurabilityRecipe> STREAM_CODEC =
                net.minecraft.network.codec.StreamCodec.of(
                        (buffer, recipe) -> {
                            Recipe.CommonInfo.STREAM_CODEC.encode(buffer, recipe.commonInfo);
                            CraftingRecipe.CraftingBookInfo.STREAM_CODEC.encode(buffer, recipe.bookInfo);
                            ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.recipeResult);
                            buffer.writeVarInt(recipe.recipeIngredients.size());
                            for (Ingredient ingredient : recipe.recipeIngredients) Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
                            buffer.writeVarInt(recipe.damageAmount);
                        },
                        buffer -> {
                            Recipe.CommonInfo common = Recipe.CommonInfo.STREAM_CODEC.decode(buffer);
                            CraftingRecipe.CraftingBookInfo book = CraftingRecipe.CraftingBookInfo.STREAM_CODEC.decode(buffer);
                            ItemStackTemplate result = ItemStackTemplate.STREAM_CODEC.decode(buffer);
                            int count = buffer.readVarInt();
                            java.util.List<Ingredient> ingredients = new java.util.ArrayList<>(count);
                            for (int i = 0; i < count; i++) ingredients.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
                            return new ShapelessDurabilityRecipe(common, book, result, ingredients, buffer.readVarInt());
                        });

        private ShapelessDurabilityRecipe(Recipe.CommonInfo common, CraftingRecipe.CraftingBookInfo book,
                                          ItemStackTemplate result, java.util.List<Ingredient> ingredients,
                                          int damageAmount) {
            super(common, book, result, ingredients);
            this.recipeResult = result;
            this.recipeIngredients = java.util.List.copyOf(ingredients);
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

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override public RecipeSerializer<ShapelessRecipe> getSerializer() {
            return (RecipeSerializer) SHAPELESS_DURABILITY.get();
        }
    }

    public static final class ClearShulkerFiltersRecipe extends CustomRecipe {
        private static final String FILTERS_TAG = "GhostFilters";

        public ClearShulkerFiltersRecipe() { super(); }

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

        @Override public ItemStack assemble(CraftingInput input) {
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

        @Override public RecipeSerializer<? extends CustomRecipe> getSerializer() { return CLEAR_SHULKER_FILTERS.get(); }

        private static boolean isSupported(ItemStack stack) {
            return stack.getItem() instanceof BuildersPouchItem
                    || stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
        }

        private static CompoundTag shulkerTag(ItemStack stack) {
            TypedEntityData<?> data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
            return data == null ? null : data.copyTagWithoutId();
        }

        private static boolean hasFilters(ItemStack stack) {
            if (stack.getItem() instanceof BuildersPouchItem) return BuildersPouchItem.hasFilters(stack);
            CompoundTag tag = shulkerTag(stack);
            if (tag == null) return false;
            if (tag.contains(FILTERS_TAG)) {
                ListTag filters = tag.getListOrEmpty(FILTERS_TAG);
                for (int i = 0; i < filters.size(); i++) if (!filters.getString(i).orElse("").isEmpty()) return true;
            }
            if (!tag.contains("Items")) return false;
            ListTag items = tag.getListOrEmpty("Items");
            for (int i = 0; i < items.size(); i++) {
                CompoundTag item = items.getCompound(i).orElse(null);
                CompoundTag itemTag = item == null ? null : item.getCompound("tag").orElse(null);
                if (itemTag != null && itemTag.getBooleanOr("ghost", false)) return true;
            }
            return false;
        }

        private static void clearFilters(ItemStack stack) {
            if (stack.getItem() instanceof BuildersPouchItem) { BuildersPouchItem.clearFilters(stack); return; }
            CompoundTag tag = shulkerTag(stack);
            if (tag == null) return;
            tag.remove(FILTERS_TAG);
            ListTag items = tag.getListOrEmpty("Items");
            for (int i = items.size() - 1; i >= 0; i--) {
                CompoundTag item = items.getCompound(i).orElse(null);
                CompoundTag itemTag = item == null ? null : item.getCompound("tag").orElse(null);
                if (itemTag != null && itemTag.getBooleanOr("ghost", false)) items.remove(i);
            }
            tag.put("Items", items);
            TypedEntityData<net.minecraft.world.level.block.entity.BlockEntityType<?>> existing = stack.get(DataComponents.BLOCK_ENTITY_DATA);
            if (existing != null) setBlockEntityData(stack, existing, tag);
        }

        private static void setBlockEntityData(ItemStack stack, TypedEntityData<net.minecraft.world.level.block.entity.BlockEntityType<?>> existing, CompoundTag tag) {
            stack.set(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(existing.type(), tag));
        }
    }

    public static final class ConfettiConfigureRecipe extends CustomRecipe {
        public ConfettiConfigureRecipe() { super(); }

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

        @Override public ItemStack assemble(CraftingInput input) {
            ItemStack confetti = ItemStack.EMPTY;
            int gunpowder = 0;
            for (int i = 0; i < input.size(); i++) {
                ItemStack stack = input.getItem(i);
                if (stack.is(com.kingodogo.buildscape.item.ModItems.CONFETTI_ITEM.get().asItem())) confetti = stack;
                else if (stack.is(net.minecraft.world.item.Items.GUNPOWDER)) gunpowder++;
            }
            if (confetti.isEmpty()) return ItemStack.EMPTY;
            CompoundTag data = Services.PLATFORM.getCustomData(confetti, false);
            int current = data != null && data.contains("BurstLevel") ? data.getIntOr("BurstLevel", 1) : 1;
            ItemStack result = confetti.copyWithCount(1);
            int next = Math.min(5, current + gunpowder);
            Services.PLATFORM.updateCustomData(result, tag -> tag.putInt("BurstLevel", next));
            return result;
        }

        @Override public RecipeSerializer<? extends CustomRecipe> getSerializer() { return CONFETTI_CONFIGURE.get(); }
    }

    public static final class InfinitePhoenixFireworkStarRecipe extends CustomRecipe {
        public InfinitePhoenixFireworkStarRecipe() { super(); }

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

        @Override public ItemStack assemble(CraftingInput input) {
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

        @Override public RecipeSerializer<? extends CustomRecipe> getSerializer() { return INFINITE_PHOENIX.get(); }
    }

    public static final class CustomFireworkStarRecipe extends CustomRecipe {
        public CustomFireworkStarRecipe() { super(); }

        @Override public boolean matches(CraftingInput input, Level level) {
            return com.kingodogo.buildscape.recipe.CustomFireworkRecipeLogic.matches(input.size(), input::getItem);
        }

        @Override public ItemStack assemble(CraftingInput input) {
            return com.kingodogo.buildscape.recipe.CustomFireworkRecipeLogic.assemble(input.size(), input::getItem);
        }

        @Override public RecipeSerializer<? extends CustomRecipe> getSerializer() { return CUSTOM_FIREWORK_STAR.get(); }
    }

    private static final com.kingodogo.buildscape.recipe.framework.compiler.AliasResolver ALIASES =
            new com.kingodogo.buildscape.recipe.framework.compiler.AliasResolver();

    private static Ingredient parseIngredient(String s) {
        if (s == null || s.isEmpty() || "{}".equals(s)) return null;
        try {
            if (s.startsWith("[") && s.length() > 1 && s.charAt(1) != '"' && s.charAt(1) != '{' && s.charAt(1) != ']') {
                // Compiler alternative list such as [BS:a,BS:b,#F:tag]: not JSON, so resolve each entry here.
                java.util.List<net.minecraft.core.Holder<net.minecraft.world.item.Item>> holders = new ArrayList<>();
                for (String part : s.substring(1, s.length() - 1).split(",")) {
                    String resolved = ALIASES.resolveString(part.trim());
                    boolean tag = resolved.startsWith("#");
                    Identifier partId = Identifier.tryParse(tag ? resolved.substring(1) : resolved);
                    if (partId == null) continue;
                    if (tag) {
                        for (net.minecraft.core.Holder<net.minecraft.world.item.Item> holder
                                : BuiltInRegistries.ITEM.getTagOrEmpty(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, partId))) {
                            holders.add(holder);
                        }
                    } else {
                        net.minecraft.world.item.Item item = BuiltInRegistries.ITEM.getValue(partId);
                        if (item != null && item != net.minecraft.world.item.Items.AIR) holders.add(BuiltInRegistries.ITEM.wrapAsHolder(item));
                    }
                }
                return holders.isEmpty() ? null : Ingredient.of(net.minecraft.core.HolderSet.direct(holders));
            }
            if (s.startsWith("{") || s.startsWith("[")) {
                return Ingredient.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, com.google.gson.JsonParser.parseString(s)).result().orElse(null);
            } else if (s.contains(":")) {
                net.minecraft.world.item.Item item = BuiltInRegistries.ITEM.getValue(Identifier.tryParse(s));
                if (item != null && item != net.minecraft.world.item.Items.AIR) {
                    return Ingredient.of(item);
                }
            }
        } catch (Throwable exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Failed to parse recipe ingredient {}", s, exception);
        }
        return null;
    }

    private static ItemStackTemplate parseResult(String itemStr, int count, String nbt) {
        net.minecraft.world.item.Item item = BuiltInRegistries.ITEM.getValue(Identifier.tryParse(itemStr != null && !itemStr.isBlank() ? itemStr : "minecraft:air"));
        if (item == null || item == net.minecraft.world.item.Items.AIR) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Skipping recipe with unknown or empty result item '{}'", itemStr);
            return null;
        }
        // Built without an ItemStack: item components are not bound yet while recipes are injected during reload.
        net.minecraft.core.component.DataComponentPatch.Builder patch = net.minecraft.core.component.DataComponentPatch.builder();
        if (nbt != null && !nbt.isBlank()) {
            try {
                patch.set(DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(net.minecraft.nbt.TagParser.parseCompoundFully(nbt)));
            } catch (Throwable exception) {
                com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Failed to parse recipe result data for {}", itemStr, exception);
            }
        }
        return new ItemStackTemplate(BuiltInRegistries.ITEM.wrapAsHolder(item), count > 0 ? count : 1, patch.build());
    }

    public static RecipeHolder<?> createRecipe(com.kingodogo.buildscape.recipe.framework.parser.RecipeIR.CompiledRecipe cr) {
        if (cr == null) return null;
        Identifier id = Identifier.tryParse(cr.id());
        if (id == null) return null;
        ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, id);
        String group = cr.group();
        ItemStackTemplate result = parseResult(cr.resultItem(), cr.resultCount(), cr.resultNbt());
        if (result == null) return null;
        String type =cr.type().toLowerCase(java.util.Locale.ROOT);
        if (type.startsWith("buildscape:")) type = type.substring("buildscape:".length());

        Recipe.CommonInfo common = new Recipe.CommonInfo(true);
        CraftingRecipe.CraftingBookInfo book = new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, group);

        switch (type) {
            case "confetti_configure" -> {
                return new RecipeHolder<>(key, CONFETTI_CONFIGURE_RECIPE);
            }
            case "clear_shulker_filters" -> {
                return new RecipeHolder<>(key, CLEAR_SHULKER_FILTERS_RECIPE);
            }
            case "shaped" -> {
                List<Optional<Ingredient>> ings = new ArrayList<>(cr.ingredients().size());
                for (int i = 0; i < cr.ingredients().size(); i++) {
                    Ingredient ing = parseIngredient(cr.ingredients().get(i));
                    ings.add(ing != null && !ing.isEmpty() ? Optional.of(ing) : Optional.empty());
                }
                ShapedRecipePattern pattern = new ShapedRecipePattern(cr.width(), cr.height(), ings, Optional.empty());
                return new RecipeHolder<>(key, new ShapedRecipe(common, book, pattern, result));
            }
            case "shapeless" -> {
                List<Ingredient> ings = new ArrayList<>();
                for (String s : cr.ingredients()) {
                    Ingredient ing = parseIngredient(s);
                    if (ing != null && !ing.isEmpty()) ings.add(ing);
                }
                return new RecipeHolder<>(key, new ShapelessRecipe(common, book, result, ings));
            }
            case "shaped_durability" -> {
                List<Optional<Ingredient>> ings = new ArrayList<>(cr.ingredients().size());
                for (int i = 0; i < cr.ingredients().size(); i++) {
                    Ingredient ing = parseIngredient(cr.ingredients().get(i));
                    ings.add(ing != null && !ing.isEmpty() ? Optional.of(ing) : Optional.empty());
                }
                ShapedRecipePattern pattern = new ShapedRecipePattern(cr.width(), cr.height(), ings, Optional.empty());
                int dmg = cr.damageAmount() > 0 ? cr.damageAmount() : 1;
                return new RecipeHolder<>(key, new ShapedDurabilityRecipe(common, book, pattern, result, dmg));
            }
            case "shapeless_durability" -> {
                List<Ingredient> ings = new ArrayList<>();
                for (String s : cr.ingredients()) {
                    Ingredient ing = parseIngredient(s);
                    if (ing != null && !ing.isEmpty()) ings.add(ing);
                }
                int dmg = cr.damageAmount() > 0 ? cr.damageAmount() : 1;
                return new RecipeHolder<>(key, new ShapelessDurabilityRecipe(common, book, result, ings, dmg));
            }
            case "stonecutting" -> {
                Ingredient input = parseIngredient(cr.input());
                if (input == null) return null;
                return new RecipeHolder<>(key, new net.minecraft.world.item.crafting.StonecutterRecipe(common, input, result));
            }
            case "smelting" -> {
                Ingredient input = parseIngredient(cr.input());
                if (input == null) return null;
                AbstractCookingRecipe.CookingBookInfo cookBook = new AbstractCookingRecipe.CookingBookInfo(CookingBookCategory.MISC, group);
                return new RecipeHolder<>(key, new net.minecraft.world.item.crafting.SmeltingRecipe(common, cookBook, input, result, cr.experience(), cr.cookingTime()));
            }
            case "blasting" -> {
                Ingredient input = parseIngredient(cr.input());
                if (input == null) return null;
                AbstractCookingRecipe.CookingBookInfo cookBook = new AbstractCookingRecipe.CookingBookInfo(CookingBookCategory.MISC, group);
                return new RecipeHolder<>(key, new net.minecraft.world.item.crafting.BlastingRecipe(common, cookBook, input, result, cr.experience(), cr.cookingTime()));
            }
            case "smoking" -> {
                Ingredient input = parseIngredient(cr.input());
                if (input == null) return null;
                AbstractCookingRecipe.CookingBookInfo cookBook = new AbstractCookingRecipe.CookingBookInfo(CookingBookCategory.MISC, group);
                return new RecipeHolder<>(key, new net.minecraft.world.item.crafting.SmokingRecipe(common, cookBook, input, result, cr.experience(), cr.cookingTime()));
            }
            case "campfire", "campfire_cooking" -> {
                Ingredient input = parseIngredient(cr.input());
                if (input == null) return null;
                AbstractCookingRecipe.CookingBookInfo cookBook = new AbstractCookingRecipe.CookingBookInfo(CookingBookCategory.MISC, group);
                return new RecipeHolder<>(key, new net.minecraft.world.item.crafting.CampfireCookingRecipe(common, cookBook, input, result, cr.experience(), cr.cookingTime()));
            }
            case "smithing" -> {
                Ingredient base = parseIngredient(cr.input());
                if (base == null) return null;
                Ingredient addition = parseIngredient(cr.addition());
                return new RecipeHolder<>(key, new net.minecraft.world.item.crafting.SmithingTransformRecipe(common, Optional.empty(), base, Optional.ofNullable(addition), result));
            }
            default -> {
                return null;
            }
        }
    }

    public static void injectRecipes(net.minecraft.world.item.crafting.RecipeManager recipeManager,
                                     java.util.List<com.kingodogo.buildscape.recipe.framework.parser.RecipeIR.CompiledRecipe> recipes) {
        if (recipeManager == null || recipes == null || recipes.isEmpty()) return;
        java.util.Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> merged = new java.util.LinkedHashMap<>();
        for (RecipeHolder<?> existing : recipeManager.getRecipes()) {
            if (existing != null) merged.put(existing.id(), existing);
        }
        for (com.kingodogo.buildscape.recipe.framework.parser.RecipeIR.CompiledRecipe cr : recipes) {
            RecipeHolder<?> holder = createRecipe(cr);
            if (holder != null) merged.put(holder.id(), holder);
        }
        try {
            net.minecraft.world.item.crafting.RecipeMap map = net.minecraft.world.item.crafting.RecipeMap.create(merged.values());
            java.lang.reflect.Field field = net.minecraft.world.item.crafting.RecipeManager.class.getDeclaredField("recipes");
            field.setAccessible(true);
            field.set(recipeManager, map);
        } catch (Throwable t) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.error("Failed to inject recipes into RecipeManager on 26.x", t);
        }
    }
}
