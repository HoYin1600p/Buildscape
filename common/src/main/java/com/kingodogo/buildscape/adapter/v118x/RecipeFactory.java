package com.kingodogo.buildscape.adapter.v118x;

import com.kingodogo.buildscape.item.BuildersPouchItem;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.registry.RegistrySupplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public final class RecipeFactory {
    public static final RegistrySupplier<RecipeSerializer<ShapedDurabilityRecipe>> SHAPED_DURABILITY =
            Services.REGISTRY.registerRecipeSerializer("shaped_durability", ShapedDurabilitySerializer::new);
    public static final RegistrySupplier<RecipeSerializer<ShapelessDurabilityRecipe>> SHAPELESS_DURABILITY =
            Services.REGISTRY.registerRecipeSerializer("shapeless_durability", ShapelessDurabilitySerializer::new);
    public static final RegistrySupplier<RecipeSerializer<ConfettiConfigureRecipe>> CONFETTI_CONFIGURE =
            Services.REGISTRY.registerRecipeSerializer("confetti_configure", () -> new net.minecraft.world.item.crafting.SimpleRecipeSerializer<>(ConfettiConfigureRecipe::new));
    public static final RegistrySupplier<RecipeSerializer<ClearShulkerFiltersRecipe>> CLEAR_SHULKER_FILTERS =
            Services.REGISTRY.registerRecipeSerializer("clear_shulker_filters", () -> new net.minecraft.world.item.crafting.SimpleRecipeSerializer<>(ClearShulkerFiltersRecipe::new));
    public static final RegistrySupplier<RecipeSerializer<CustomFireworkStarRecipe>> CUSTOM_FIREWORK_STAR =
            Services.REGISTRY.registerRecipeSerializer("custom_firework_star", () -> new net.minecraft.world.item.crafting.SimpleRecipeSerializer<>(CustomFireworkStarRecipe::new));
    public static final RegistrySupplier<RecipeSerializer<InfinitePhoenixFireworkStarRecipe>> INFINITE_PHOENIX =
            Services.REGISTRY.registerRecipeSerializer("infinite_phoenix_firework_star", () -> new net.minecraft.world.item.crafting.SimpleRecipeSerializer<>(InfinitePhoenixFireworkStarRecipe::new));

    private RecipeFactory() {}
    public static void register() {}

    public static final class ShapedDurabilityRecipe extends ShapedRecipe {
        private final int damageAmount;
        public ShapedDurabilityRecipe(ResourceLocation id, String group, int width, int height,
                                      NonNullList<Ingredient> ingredients, ItemStack result, int damageAmount) {
            super(id, group, width, height, ingredients, result);
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
        @Override public ShapedDurabilityRecipe fromJson(ResourceLocation id, com.google.gson.JsonObject json) {
            ShapedRecipe base = RecipeSerializer.SHAPED_RECIPE.fromJson(id, json);
            return new ShapedDurabilityRecipe(id, base.getGroup(), base.getWidth(), base.getHeight(),
                    base.getIngredients(), base.getResultItem(), GsonHelper.getAsInt(json, "damageAmount", 1));
        }
        @Override public ShapedDurabilityRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            ShapedRecipe base = RecipeSerializer.SHAPED_RECIPE.fromNetwork(id, buffer);
            return new ShapedDurabilityRecipe(id, base.getGroup(), base.getWidth(), base.getHeight(),
                    base.getIngredients(), base.getResultItem(), buffer.readVarInt());
        }
        @Override public void toNetwork(FriendlyByteBuf buffer, ShapedDurabilityRecipe recipe) {
            RecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe);
            buffer.writeVarInt(recipe.damageAmount);
        }
    }

    public static final class ShapelessDurabilityRecipe extends ShapelessRecipe {
        private final int damageAmount;
        public ShapelessDurabilityRecipe(ResourceLocation id, String group, ItemStack result,
                                         NonNullList<Ingredient> ingredients, int damageAmount) {
            super(id, group, result, ingredients);
            this.damageAmount = damageAmount;
        }
        @Override public NonNullList<ItemStack> getRemainingItems(CraftingContainer input) {
            NonNullList<ItemStack> remaining = NonNullList.withSize(input.getContainerSize(), ItemStack.EMPTY);
            for (int i = 0; i < input.getContainerSize(); i++) remaining.set(i,
                    com.kingodogo.buildscape.recipe.DurabilityRecipeLogic.getRemainingItem(input.getItem(i), damageAmount));
            return remaining;
        }
        @Override public RecipeSerializer<?> getSerializer() { return SHAPELESS_DURABILITY.get(); }
    }

    private static final class ShapelessDurabilitySerializer implements RecipeSerializer<ShapelessDurabilityRecipe> {
        @Override public ShapelessDurabilityRecipe fromJson(ResourceLocation id, com.google.gson.JsonObject json) {
            ShapelessRecipe base = RecipeSerializer.SHAPELESS_RECIPE.fromJson(id, json);
            return new ShapelessDurabilityRecipe(id, base.getGroup(), base.getResultItem(),
                    base.getIngredients(), GsonHelper.getAsInt(json, "damageAmount", 1));
        }
        @Override public ShapelessDurabilityRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            ShapelessRecipe base = RecipeSerializer.SHAPELESS_RECIPE.fromNetwork(id, buffer);
            return new ShapelessDurabilityRecipe(id, base.getGroup(), base.getResultItem(),
                    base.getIngredients(), buffer.readVarInt());
        }
        @Override public void toNetwork(FriendlyByteBuf buffer, ShapelessDurabilityRecipe recipe) {
            RecipeSerializer.SHAPELESS_RECIPE.toNetwork(buffer, recipe);
            buffer.writeVarInt(recipe.damageAmount);
        }
    }

    public static final class ClearShulkerFiltersRecipe extends CustomRecipe {
        private static final String BLOCK_ENTITY_TAG = "BlockEntityTag";
        private static final String FILTERS_TAG = "GhostFilters";

        public ClearShulkerFiltersRecipe(ResourceLocation id) { super(id); }

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

        @Override public ItemStack assemble(CraftingContainer input) {
            for (int i = 0; i < input.getContainerSize(); i++) {
                ItemStack stack = input.getItem(i);
                if (!stack.isEmpty() && isSupported(stack) && hasFilters(stack)) {
                    ItemStack result = stack.copy();
                    result.setCount(1);
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

        private static boolean hasFilters(ItemStack stack) {
            if (stack.getItem() instanceof BuildersPouchItem) return BuildersPouchItem.hasFilters(stack);
            CompoundTag tag = stack.getTagElement(BLOCK_ENTITY_TAG);
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
            CompoundTag tag = stack.getTagElement(BLOCK_ENTITY_TAG);
            if (tag == null) return;
            tag.remove(FILTERS_TAG);
            if (!tag.contains("Items", Tag.TAG_LIST)) return;
            ListTag items = tag.getList("Items", Tag.TAG_COMPOUND);
            for (int i = items.size() - 1; i >= 0; i--) {
                CompoundTag item = items.getCompound(i);
                if (item.contains("tag", Tag.TAG_COMPOUND) && item.getCompound("tag").getBoolean("ghost")) items.remove(i);
            }
            tag.put("Items", items);
        }
    }

    public static final class ConfettiConfigureRecipe extends CustomRecipe {
        public ConfettiConfigureRecipe(ResourceLocation id) { super(id); }

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

        @Override public ItemStack assemble(CraftingContainer input) {
            ItemStack confetti = ItemStack.EMPTY;
            int gunpowder = 0;
            for (int i = 0; i < input.getContainerSize(); i++) {
                ItemStack stack = input.getItem(i);
                if (stack.is(com.kingodogo.buildscape.item.ModItems.CONFETTI_ITEM.get().asItem())) confetti = stack;
                else if (stack.is(net.minecraft.world.item.Items.GUNPOWDER)) gunpowder++;
            }
            if (confetti.isEmpty()) return ItemStack.EMPTY;
            int current = confetti.hasTag() && confetti.getTag().contains("BurstLevel") ? confetti.getTag().getInt("BurstLevel") : 1;
            ItemStack result = confetti.copy();
            result.setCount(1);
            result.getOrCreateTag().putInt("BurstLevel", Math.min(5, current + gunpowder));
            return result;
        }

        @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 2; }
        @Override public RecipeSerializer<?> getSerializer() { return CONFETTI_CONFIGURE.get(); }
    }

    public static final class InfinitePhoenixFireworkStarRecipe extends CustomRecipe {
        public InfinitePhoenixFireworkStarRecipe(ResourceLocation id) { super(id); }

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

        @Override public ItemStack assemble(CraftingContainer input) {
            int flight = 0;
            ItemStack star = ItemStack.EMPTY;
            for (int i = 0; i < input.getContainerSize(); i++) {
                ItemStack stack = input.getItem(i);
                if (stack.is(net.minecraft.world.item.Items.GUNPOWDER)) flight++;
                else if (stack.is(com.kingodogo.buildscape.item.ModItems.INFINITE_PHOENIX_FIREWORK_STAR.get().asItem())) star = stack;
            }
            int[] colors = com.kingodogo.buildscape.platform.Services.PLATFORM.getFireworkStarColors(star);
            if (colors.length == 0) colors = new int[]{0xFFFFFF, 0xFFF200, 0xFFB000, 0xFF6500, 0xE52B00};
            ItemStack result = new ItemStack(net.minecraft.world.item.Items.FIREWORK_ROCKET, 3);
            com.kingodogo.buildscape.platform.Services.PLATFORM.configureFireworkRocket(result, flight,
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
        public CustomFireworkStarRecipe(ResourceLocation id) { super(id); }

        @Override public boolean matches(CraftingContainer input, Level level) {
            return com.kingodogo.buildscape.recipe.CustomFireworkRecipeLogic.matches(input.getContainerSize(), input::getItem);
        }

        @Override public ItemStack assemble(CraftingContainer input) {
            return com.kingodogo.buildscape.recipe.CustomFireworkRecipeLogic.assemble(input.getContainerSize(), input::getItem);
        }

        @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 2; }
        @Override public RecipeSerializer<?> getSerializer() { return CUSTOM_FIREWORK_STAR.get(); }
    }

    private static Ingredient parseIngredient(String s) {
        if (s == null || s.isEmpty() || "{}".equals(s)) return Ingredient.EMPTY;
        try {
            if (s.startsWith("{") || s.startsWith("[")) {
                return Ingredient.fromJson(com.google.gson.JsonParser.parseString(s));
            } else if (s.contains(":")) {
                Item item = Registry.ITEM.get(new ResourceLocation(s));
                if (item != null && item != net.minecraft.world.item.Items.AIR) {
                    return Ingredient.of(item);
                }
            }
        } catch (Throwable ignored) {}
        return Ingredient.EMPTY;
    }

    private static ItemStack parseResult(String itemStr, int count, String nbt) {
        Item item = Registry.ITEM.get(new ResourceLocation(itemStr != null && !itemStr.isBlank() ? itemStr : "minecraft:air"));
        if (item == null) item = net.minecraft.world.item.Items.AIR;
        ItemStack stack = new ItemStack(item, count > 0 ? count : 1);
        if (nbt != null && !nbt.isBlank()) {
            try {
                stack.setTag(net.minecraft.nbt.TagParser.parseTag(nbt));
            } catch (Throwable ignored) {}
        }
        return stack;
    }

    public static net.minecraft.world.item.crafting.Recipe<?> createRecipe(com.kingodogo.buildscape.recipe.framework.parser.RecipeIR.CompiledRecipe cr) {
        if (cr == null) return null;
        ResourceLocation id = ResourceLocation.tryParse(cr.id());
        if (id == null) return null;
        String group = cr.group();
        ItemStack result = parseResult(cr.resultItem(), cr.resultCount(), cr.resultNbt());
        String type = cr.type().toLowerCase(java.util.Locale.ROOT);
        if (type.startsWith("buildscape:")) type = type.substring("buildscape:".length());

        switch (type) {
            case "confetti_configure" -> {
                return new ConfettiConfigureRecipe(id);
            }
            case "clear_shulker_filters" -> {
                return new ClearShulkerFiltersRecipe(id);
            }
            case "shaped" -> {
                NonNullList<Ingredient> ings = NonNullList.withSize(cr.ingredients().size(), Ingredient.EMPTY);
                for (int i = 0; i < cr.ingredients().size(); i++) {
                    ings.set(i, parseIngredient(cr.ingredients().get(i)));
                }
                return new ShapedRecipe(id, group, cr.width(), cr.height(), ings, result);
            }
            case "shapeless" -> {
                NonNullList<Ingredient> ings = NonNullList.create();
                for (String s : cr.ingredients()) {
                    Ingredient ing = parseIngredient(s);
                    if (!ing.isEmpty()) ings.add(ing);
                }
                return new ShapelessRecipe(id, group, result, ings);
            }
            case "shaped_durability" -> {
                NonNullList<Ingredient> ings = NonNullList.withSize(cr.ingredients().size(), Ingredient.EMPTY);
                for (int i = 0; i < cr.ingredients().size(); i++) {
                    ings.set(i, parseIngredient(cr.ingredients().get(i)));
                }
                int dmg = cr.damageAmount() > 0 ? cr.damageAmount() : 1;
                return new ShapedDurabilityRecipe(id, group, cr.width(), cr.height(), ings, result, dmg);
            }
            case "shapeless_durability" -> {
                NonNullList<Ingredient> ings = NonNullList.create();
                for (String s : cr.ingredients()) {
                    Ingredient ing = parseIngredient(s);
                    if (!ing.isEmpty()) ings.add(ing);
                }
                int dmg = cr.damageAmount() > 0 ? cr.damageAmount() : 1;
                return new ShapelessDurabilityRecipe(id, group, result, ings, dmg);
            }
            case "stonecutting" -> {
                Ingredient input = parseIngredient(cr.input());
                return new net.minecraft.world.item.crafting.StonecutterRecipe(id, group, input, result);
            }
            case "smelting" -> {
                Ingredient input = parseIngredient(cr.input());
                return new net.minecraft.world.item.crafting.SmeltingRecipe(id, group, input, result, cr.experience(), cr.cookingTime());
            }
            case "blasting" -> {
                Ingredient input = parseIngredient(cr.input());
                return new net.minecraft.world.item.crafting.BlastingRecipe(id, group, input, result, cr.experience(), cr.cookingTime());
            }
            case "smoking" -> {
                Ingredient input = parseIngredient(cr.input());
                return new net.minecraft.world.item.crafting.SmokingRecipe(id, group, input, result, cr.experience(), cr.cookingTime());
            }
            case "campfire", "campfire_cooking" -> {
                Ingredient input = parseIngredient(cr.input());
                return new net.minecraft.world.item.crafting.CampfireCookingRecipe(id, group, input, result, cr.experience(), cr.cookingTime());
            }
            case "smithing" -> {
                Ingredient base = parseIngredient(cr.input());
                Ingredient addition = parseIngredient(cr.addition());
                return new net.minecraft.world.item.crafting.UpgradeRecipe(id, base, addition, result);
            }
            default -> {
                return null;
            }
        }
    }

    public static void injectRecipes(net.minecraft.world.item.crafting.RecipeManager recipeManager,
                                     java.util.List<com.kingodogo.buildscape.recipe.framework.parser.RecipeIR.CompiledRecipe> recipes) {
        if (recipeManager == null || recipes == null || recipes.isEmpty()) return;
        java.util.Map<ResourceLocation, net.minecraft.world.item.crafting.Recipe<?>> merged = new java.util.LinkedHashMap<>();
        for (net.minecraft.world.item.crafting.Recipe<?> existing : recipeManager.getRecipes()) {
            if (existing != null) merged.put(existing.getId(), existing);
        }
        for (com.kingodogo.buildscape.recipe.framework.parser.RecipeIR.CompiledRecipe cr : recipes) {
            net.minecraft.world.item.crafting.Recipe<?> r = createRecipe(cr);
            if (r != null) merged.put(r.getId(), r);
        }
        recipeManager.replaceRecipes(merged.values());
    }
}
