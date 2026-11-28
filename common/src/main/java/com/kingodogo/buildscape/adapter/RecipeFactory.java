package com.kingodogo.buildscape.adapter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.kingodogo.buildscape.item.BuildersPouchItem;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.registry.RegistrySupplier;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.inventory.CraftingContainer;
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
    private static final RegistrySupplier<RecipeSerializer<HazeBushDyeRecipe>> HAZE_BUSH_DYE =
            Services.REGISTRY.registerRecipeSerializer("haze_bush_dye", HazeBushDyeSerializer::new);
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
        @Override public NonNullList<ItemStack> getRemainingItems(CraftingContainer input) {
            NonNullList<ItemStack> remaining = NonNullList.withSize(input.getContainerSize(), ItemStack.EMPTY);
            for (int i = 0; i < input.getContainerSize(); i++) remaining.set(i,
                    com.kingodogo.buildscape.recipe.DurabilityRecipeLogic.getRemainingItem(input.getItem(i), damageAmount));
            return remaining;
        }
        @Override public RecipeSerializer<?> getSerializer() { return SHAPED_DURABILITY.get(); }
    }

    private static final class ShapedDurabilitySerializer implements RecipeSerializer<ShapedDurabilityRecipe> {
        private static final Codec<ShapedDurabilityRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                com.mojang.serialization.Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedDurabilityRecipe::getGroup),
                CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(ShapedDurabilityRecipe::category),
                ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.recipePattern),
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.recipeResult),
                com.mojang.serialization.Codec.INT.optionalFieldOf("damageAmount", 1).forGetter(recipe -> recipe.damageAmount)
        ).apply(instance, ShapedDurabilityRecipe::new));
        @Override public Codec<ShapedDurabilityRecipe> codec() { return CODEC; }
        @Override public ShapedDurabilityRecipe fromNetwork(FriendlyByteBuf buffer) {
            return new ShapedDurabilityRecipe(buffer.readUtf(), buffer.readEnum(CraftingBookCategory.class),
                    ShapedRecipePattern.fromNetwork(buffer), buffer.readItem(), buffer.readVarInt());
        }
        @Override public void toNetwork(FriendlyByteBuf buffer, ShapedDurabilityRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeEnum(recipe.category());
            recipe.recipePattern.toNetwork(buffer);
            buffer.writeItem(recipe.recipeResult);
            buffer.writeVarInt(recipe.damageAmount);
        }
    }

    public static final class HazeBushDyeRecipe extends ShapelessRecipe {

        private HazeBushDyeRecipe(String group, CraftingBookCategory category, ItemStack result,
                                          NonNullList<Ingredient> ingredients) {
            super(group, category, result, ingredients);
        }

        @Override public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
            ItemStack result = super.assemble(input, registries);
            for (int i = 0; i < input.getContainerSize(); i++) {
                ItemStack bush = input.getItem(i);
                if (getIngredients().get(0).test(bush)) {
                    CompoundTag data = bush.getTag();
                    if (data != null) {
                        if (data.contains("BlockStateTag", Tag.TAG_COMPOUND)) result.addTagElement("BlockStateTag", data.getCompound("BlockStateTag").copy());
                        if (data.contains("HasHaze")) result.addTagElement("HasHaze", data.get("HasHaze").copy());
                    }
                    return result;
                }
            }
            return result;
        }

        @Override public RecipeSerializer<?> getSerializer() { return HAZE_BUSH_DYE.get(); }
    }

    private static final class HazeBushDyeSerializer implements RecipeSerializer<HazeBushDyeRecipe> {
        private static final Codec<HazeBushDyeRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                com.mojang.serialization.Codec.STRING.optionalFieldOf("group", "").forGetter(HazeBushDyeRecipe::getGroup),
                CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(HazeBushDyeRecipe::category),
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.getResultItem(RegistryAccess.EMPTY)),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(recipe -> java.util.List.copyOf(recipe.getIngredients()))
        ).apply(instance, (group, category, result, ingredients) ->
                new HazeBushDyeRecipe(group, category, result, toNonNullList(ingredients))));

        @Override public Codec<HazeBushDyeRecipe> codec() { return CODEC; }
        @Override public HazeBushDyeRecipe fromNetwork(FriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            CraftingBookCategory category = buffer.readEnum(CraftingBookCategory.class);
            int count = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(count, Ingredient.EMPTY);
            for (int i = 0; i < count; i++) ingredients.set(i, Ingredient.fromNetwork(buffer));
            ItemStack result = buffer.readItem();
            return new HazeBushDyeRecipe(group, category, result, ingredients);
        }
        @Override public void toNetwork(FriendlyByteBuf buffer, HazeBushDyeRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeEnum(recipe.category());
            buffer.writeVarInt(recipe.getIngredients().size());
            for (Ingredient ingredient : recipe.getIngredients()) ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.getResultItem(RegistryAccess.EMPTY));
        }

        private static NonNullList<Ingredient> toNonNullList(java.util.List<Ingredient> values) {
            NonNullList<Ingredient> ingredients = NonNullList.create();
            ingredients.addAll(values);
            return ingredients;
        }
    }

    public static final class ShapelessDurabilityRecipe extends ShapelessRecipe {
        private final int damageAmount;

        private ShapelessDurabilityRecipe(String group, CraftingBookCategory category, ItemStack result,
                                          NonNullList<Ingredient> ingredients, int damageAmount) {
            super(group, category, result, ingredients);
            this.damageAmount = damageAmount;
        }

        @Override public NonNullList<ItemStack> getRemainingItems(CraftingContainer input) {
            NonNullList<ItemStack> remaining = NonNullList.withSize(input.getContainerSize(), ItemStack.EMPTY);
            for (int i = 0; i < input.getContainerSize(); i++) {
                remaining.set(i, com.kingodogo.buildscape.recipe.DurabilityRecipeLogic.getRemainingItem(
                        input.getItem(i), damageAmount));
            }
            return remaining;
        }

        @Override public RecipeSerializer<?> getSerializer() { return SHAPELESS_DURABILITY.get(); }
    }

    private static final class ShapelessDurabilitySerializer implements RecipeSerializer<ShapelessDurabilityRecipe> {
        private static final Codec<ShapelessDurabilityRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                com.mojang.serialization.Codec.STRING.optionalFieldOf("group", "").forGetter(ShapelessDurabilityRecipe::getGroup),
                CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(ShapelessDurabilityRecipe::category),
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.getResultItem(RegistryAccess.EMPTY)),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(recipe -> java.util.List.copyOf(recipe.getIngredients())),
                com.mojang.serialization.Codec.INT.optionalFieldOf("damageAmount", 1).forGetter(recipe -> recipe.damageAmount)
        ).apply(instance, (group, category, result, ingredients, damageAmount) ->
                new ShapelessDurabilityRecipe(group, category, result, toNonNullList(ingredients), damageAmount)));

        @Override public Codec<ShapelessDurabilityRecipe> codec() { return CODEC; }
        @Override public ShapelessDurabilityRecipe fromNetwork(FriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            CraftingBookCategory category = buffer.readEnum(CraftingBookCategory.class);
            int count = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(count, Ingredient.EMPTY);
            for (int i = 0; i < count; i++) ingredients.set(i, Ingredient.fromNetwork(buffer));
            ItemStack result = buffer.readItem();
            return new ShapelessDurabilityRecipe(group, category, result, ingredients, buffer.readVarInt());
        }
        @Override public void toNetwork(FriendlyByteBuf buffer, ShapelessDurabilityRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeEnum(recipe.category());
            buffer.writeVarInt(recipe.getIngredients().size());
            for (Ingredient ingredient : recipe.getIngredients()) ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.getResultItem(RegistryAccess.EMPTY));
            buffer.writeVarInt(recipe.damageAmount);
        }

        private static NonNullList<Ingredient> toNonNullList(java.util.List<Ingredient> values) {
            NonNullList<Ingredient> ingredients = NonNullList.create();
            ingredients.addAll(values);
            return ingredients;
        }
    }

    public static final class ClearShulkerFiltersRecipe extends CustomRecipe {
        private static final String FILTERS_TAG = "GhostFilters";

        public ClearShulkerFiltersRecipe(CraftingBookCategory category) { super(category); }

        @Override public boolean matches(CraftingContainer input, Level level) {
            ItemStack filtered = ItemStack.EMPTY;
            for (int i = 0; i < input.getContainerSize(); i++) {
                ItemStack stack = input.getItem(i);
                if (stack.isEmpty()) continue;
                if (!filtered.isEmpty() || !isSupported(stack)) return false;
                filtered = stack;
            }
            return !filtered.isEmpty() && hasFilters(filtered);
        }

        @Override public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
            for (int i = 0; i < input.getContainerSize(); i++) {
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
            return BlockItem.getBlockEntityData(stack);
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
            BlockItem.setBlockEntityData(stack, net.minecraft.world.level.block.entity.BlockEntityType.SHULKER_BOX, tag);
        }
    }

    public static final class ConfettiConfigureRecipe extends CustomRecipe {
        public ConfettiConfigureRecipe(CraftingBookCategory category) { super(category); }

        @Override public boolean matches(CraftingContainer input, Level level) {
            int confetti = 0, gunpowder = 0;
            for (int i = 0; i < input.getContainerSize(); i++) {
                ItemStack stack = input.getItem(i);
                if (stack.isEmpty()) continue;
                if (stack.is(com.kingodogo.buildscape.item.ModItems.CONFETTI_ITEM.get().asItem())) confetti++;
                else if (stack.is(net.minecraft.world.item.Items.GUNPOWDER)) gunpowder++;
                else return false;
            }
            return confetti == 1 && gunpowder >= 1;
        }

        @Override public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
            ItemStack confetti = ItemStack.EMPTY;
            int gunpowder = 0;
            for (int i = 0; i < input.getContainerSize(); i++) {
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

        @Override public boolean matches(CraftingContainer input, Level level) {
            int gunpowder = 0, paper = 0, star = 0;
            for (int i = 0; i < input.getContainerSize(); i++) {
                ItemStack stack = input.getItem(i);
                if (stack.isEmpty()) continue;
                if (stack.is(net.minecraft.world.item.Items.GUNPOWDER)) gunpowder++;
                else if (stack.is(net.minecraft.world.item.Items.PAPER)) paper++;
                else if (stack.is(com.kingodogo.buildscape.item.ModItems.INFINITE_PHOENIX_FIREWORK_STAR.get().asItem())) star++;
                else return false;
            }
            return gunpowder >= 1 && gunpowder <= 3 && paper == 1 && star == 1;
        }

        @Override public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
            int flight = 0;
            ItemStack star = ItemStack.EMPTY;
            for (int i = 0; i < input.getContainerSize(); i++) {
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

        @Override public net.minecraft.core.NonNullList<ItemStack> getRemainingItems(CraftingContainer input) {
            net.minecraft.core.NonNullList<ItemStack> remaining = net.minecraft.core.NonNullList.withSize(input.getContainerSize(), ItemStack.EMPTY);
            for (int i = 0; i < input.getContainerSize(); i++) {
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

        @Override public boolean matches(CraftingContainer input, Level level) {
            return com.kingodogo.buildscape.recipe.CustomFireworkRecipeLogic.matches(input.getContainerSize(), input::getItem);
        }

        @Override public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
            return com.kingodogo.buildscape.recipe.CustomFireworkRecipeLogic.assemble(input.getContainerSize(), input::getItem);
        }

        @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 2; }
        @Override public RecipeSerializer<?> getSerializer() { return CUSTOM_FIREWORK_STAR.get(); }
    }

    private static Ingredient parseIngredient(String s) {
        if (s == null || s.isEmpty() || "{}".equals(s)) return Ingredient.EMPTY;
        try {
            if (s.startsWith("{") || s.startsWith("[")) {
                com.google.gson.JsonElement json = com.google.gson.JsonParser.parseString(s);
                resolveIngredientItems(json);
                return Ingredient.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, json).result().orElse(Ingredient.EMPTY);
            } else if (s.contains(":")) {
                net.minecraft.world.item.Item item = Services.PLATFORM.getItem(com.kingodogo.buildscape.util.CommonId.parse(s));
                if (item != null && item != net.minecraft.world.item.Items.AIR) {
                    return Ingredient.of(item);
                }
            }
        } catch (Throwable ignored) {}
        return Ingredient.EMPTY;
    }

    private static void resolveIngredientItems(com.google.gson.JsonElement json) {
        if (json.isJsonArray()) {
            for (com.google.gson.JsonElement entry : json.getAsJsonArray()) resolveIngredientItems(entry);
        } else if (json.isJsonObject()) {
            com.google.gson.JsonObject object = json.getAsJsonObject();
            if (object.has("item") && object.get("item").isJsonPrimitive()
                    && object.get("item").getAsJsonPrimitive().isString()) {
                net.minecraft.world.item.Item item = Services.PLATFORM.getItem(
                        com.kingodogo.buildscape.util.CommonId.parse(object.get("item").getAsString()));
                if (item != null && item != net.minecraft.world.item.Items.AIR) {
                    object.addProperty("item", Services.PLATFORM.getItemId(item).toString());
                }
            }
            for (var entry : object.entrySet()) resolveIngredientItems(entry.getValue());
        }
    }

    private static ItemStack parseResult(String itemStr, int count, String nbt) {
        net.minecraft.world.item.Item item = Services.PLATFORM.getItem(com.kingodogo.buildscape.util.CommonId.parse(itemStr != null && !itemStr.isBlank() ? itemStr : "minecraft:air"));
        if (item == null) item = net.minecraft.world.item.Items.AIR;
        ItemStack stack = new ItemStack(item, count > 0 ? count : 1);
        if (nbt != null && !nbt.isBlank()) {
            try {
                stack.setTag(net.minecraft.nbt.TagParser.parseTag(nbt));
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
            case "haze_bush_dye" -> {
                NonNullList<Ingredient> ings = NonNullList.create();
                for (String s : cr.ingredients()) {
                    Ingredient ing = parseIngredient(s);
                    if (!ing.isEmpty()) ings.add(ing);
                }
                return new net.minecraft.world.item.crafting.RecipeHolder<>(id, new HazeBushDyeRecipe(group, CraftingBookCategory.MISC, result, ings));
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
        var accessor = (com.kingodogo.buildscape.mixin.RecipeManagerAccessor) recipeManager;
        java.util.Map<net.minecraft.world.item.crafting.RecipeType<?>,
                java.util.Map<ResourceLocation, net.minecraft.world.item.crafting.RecipeHolder<?>>> byType =
                new java.util.LinkedHashMap<>();
        accessor.buildscape$getByType().entries().forEach(entry ->
                byType.computeIfAbsent(entry.getKey(), ignored -> new java.util.LinkedHashMap<>())
                        .put(entry.getValue().id(), entry.getValue()));
        java.util.List<net.minecraft.world.item.crafting.RecipeHolder<?>> nativeRecipes = new java.util.ArrayList<>();
        for (com.kingodogo.buildscape.recipe.framework.parser.RecipeIR.CompiledRecipe cr : recipes) {
            net.minecraft.world.item.crafting.RecipeHolder<?> holder = createRecipe(cr);
            if (holder != null) nativeRecipes.add(holder);
        }
        var indexes = com.kingodogo.buildscape.recipe.framework.integration.RecipeManagerInjector
                .mergeIndexesGeneric(byType, accessor.buildscape$getByName(), nativeRecipes,
                        holder -> holder.value().getType(), holder -> holder.id());
        var immutableByType = com.google.common.collect.ImmutableMultimap
                .<net.minecraft.world.item.crafting.RecipeType<?>, net.minecraft.world.item.crafting.RecipeHolder<?>>builder();
        indexes.byType().forEach((type, holders) -> immutableByType.putAll(type, holders.values()));
        var mergedByType = immutableByType.build();
        accessor.buildscape$setByType(mergedByType);
        accessor.buildscape$setByName(indexes.byName());
    }
}
