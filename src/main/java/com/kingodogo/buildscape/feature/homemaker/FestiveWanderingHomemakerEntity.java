package com.kingodogo.buildscape.feature.homemaker;

import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.registry.items.MasonryItems;
import com.kingodogo.buildscape.registry.items.StoneItems;
import com.kingodogo.buildscape.registry.items.WoodItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class FestiveWanderingHomemakerEntity extends WanderingTrader {
    private int despawnDelay = 48000;

    public FestiveWanderingHomemakerEntity(EntityType<? extends WanderingTrader> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide) {
            if (this.getTradingPlayer() == null) {
                if (--this.despawnDelay <= 0) {
                    this.discard();
                }
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt(NbtKeys.DESPAWN_DELAY, this.despawnDelay);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains(NbtKeys.DESPAWN_DELAY)) {
            this.despawnDelay = compound.getInt(NbtKeys.DESPAWN_DELAY);
        }
    }

    private Item getRandom(Supplier<Item>[] items) {
        return items[this.random.nextInt(items.length)].get();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void updateTrades() {
        MerchantOffers offers = this.getOffers();
        offers.clear();

        List<MerchantOffer> list = new ArrayList<>();

        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(StoneItems.WHITE_SAND.get(), 8), 8, 1,
                0.05f));

        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(StoneItems.GREEN_SAND.get(), 8), 8, 1,
                0.05f));

        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(StoneItems.RED_SAND.get(), 8), 8, 1,
                0.05f));

        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(StoneItems.RED_TILES.get(), 8), 8, 1,
                0.05f));

        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(StoneItems.LIME_TILES.get(), 8), 8, 1,
                0.05f));

        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(StoneItems.SNOW_OVERLAY.get(), 4), 4,
                1,
                0.05f));

        Supplier<Item>[] stockings = new Supplier[]{
                WoodItems.BLACK_FESTIVE_STOCKING, WoodItems.BLUE_FESTIVE_STOCKING, WoodItems.BROWN_FESTIVE_STOCKING,
                WoodItems.CYAN_FESTIVE_STOCKING, WoodItems.GRAY_FESTIVE_STOCKING, WoodItems.GREEN_FESTIVE_STOCKING,
                WoodItems.LIGHT_BLUE_FESTIVE_STOCKING, WoodItems.LIGHT_GRAY_FESTIVE_STOCKING,
                WoodItems.LIME_FESTIVE_STOCKING,
                WoodItems.MAGENTA_FESTIVE_STOCKING, WoodItems.ORANGE_FESTIVE_STOCKING, WoodItems.PINK_FESTIVE_STOCKING,
                WoodItems.PURPLE_FESTIVE_STOCKING, WoodItems.RED_FESTIVE_STOCKING, WoodItems.WHITE_FESTIVE_STOCKING,
                WoodItems.YELLOW_FESTIVE_STOCKING, WoodItems.FESTIVE_STOCKING
        };
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(getRandom(stockings), 8), 4, 1,
                0.05f));

        Supplier<Item>[] stringLights = new Supplier[]{
                WoodItems.WHITE_STRING_LIGHT, WoodItems.ORANGE_STRING_LIGHT, WoodItems.MAGENTA_STRING_LIGHT,
                WoodItems.LIGHT_BLUE_STRING_LIGHT, WoodItems.YELLOW_STRING_LIGHT, WoodItems.LIME_STRING_LIGHT,
                WoodItems.PINK_STRING_LIGHT, WoodItems.GRAY_STRING_LIGHT, WoodItems.LIGHT_GRAY_STRING_LIGHT,
                WoodItems.CYAN_STRING_LIGHT, WoodItems.PURPLE_STRING_LIGHT, WoodItems.BLUE_STRING_LIGHT,
                WoodItems.BROWN_STRING_LIGHT, WoodItems.GREEN_STRING_LIGHT, WoodItems.RED_STRING_LIGHT,
                WoodItems.BLACK_STRING_LIGHT, WoodItems.MULTICOLOR_STRING_LIGHT
        };
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 2), new ItemStack(getRandom(stringLights), 16), 8, 1,
                0.05f));

        Supplier<Item>[] snowyLeaves = new Supplier[]{
                WoodItems.SNOWY_LEAVES, WoodItems.SNOWY_OAK_LEAVES, WoodItems.SNOWY_SPRUCE_LEAVES,
                WoodItems.SNOWY_BIRCH_LEAVES, WoodItems.SNOWY_JUNGLE_LEAVES, WoodItems.SNOWY_ACACIA_LEAVES,
                WoodItems.SNOWY_DARK_OAK_LEAVES, WoodItems.SNOWY_MANGROVE_LEAVES, WoodItems.SNOWY_AZALEA_LEAVES,
                WoodItems.SNOWY_FLOWERING_AZALEA_LEAVES, WoodItems.SNOWY_CHERRY_LEAVES, WoodItems.SNOWY_PALE_OAK_LEAVES,
                WoodItems.SNOWY_ORANGE_POPLAR_LEAVES, WoodItems.SNOWY_RED_POPLAR_LEAVES,
                WoodItems.SNOWY_YELLOW_POPLAR_LEAVES
        };
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(getRandom(snowyLeaves), 8), 8, 1,
                0.05f));

        Supplier<Item>[] ornaments = new Supplier[]{
                WoodItems.WHITE_ORNAMENT, WoodItems.ORANGE_ORNAMENT, WoodItems.MAGENTA_ORNAMENT,
                WoodItems.LIGHT_BLUE_ORNAMENT, WoodItems.YELLOW_ORNAMENT, WoodItems.LIME_ORNAMENT,
                WoodItems.PINK_ORNAMENT, WoodItems.GRAY_ORNAMENT, WoodItems.LIGHT_GRAY_ORNAMENT,
                WoodItems.CYAN_ORNAMENT, WoodItems.PURPLE_ORNAMENT, WoodItems.BLUE_ORNAMENT,
                WoodItems.BROWN_ORNAMENT, WoodItems.GREEN_ORNAMENT, WoodItems.RED_ORNAMENT,
                WoodItems.BLACK_ORNAMENT, WoodItems.GLASS_ORNAMENT, WoodItems.TINTED_GLASS_ORNAMENT
        };
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 2), new ItemStack(getRandom(ornaments), 16), 8, 1,
                0.05f));

        Supplier<Item>[] stars = new Supplier[]{
                WoodItems.WHITE_STAR, WoodItems.ORANGE_STAR, WoodItems.MAGENTA_STAR,
                WoodItems.LIGHT_BLUE_STAR, WoodItems.YELLOW_STAR, WoodItems.LIME_STAR,
                WoodItems.PINK_STAR, WoodItems.GRAY_STAR, WoodItems.LIGHT_GRAY_STAR,
                WoodItems.CYAN_STAR, WoodItems.PURPLE_STAR, WoodItems.BLUE_STAR,
                WoodItems.BROWN_STAR, WoodItems.GREEN_STAR, WoodItems.RED_STAR,
                WoodItems.BLACK_STAR, WoodItems.GLOW_STAR
        };
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(getRandom(stars), 8), 8, 1, 0.05f));

        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1),
                new ItemStack(WoodItems.PACKED_ICICLE_BLOCK.get(), 8), 8, 1, 0.05f));

        Supplier<Item>[] glowLights = new Supplier[]{
                WoodItems.GLOW_LIGHTS, MasonryItems.MULTICOLOR_GLOW_LIGHTS
        };
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(getRandom(glowLights), 8), 8, 1,
                0.05f));

        list.add(
                new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(WoodItems.FESTIVE_LAMP.get(), 8), 8, 1,
                        0.05f));

        Collections.shuffle(list, this.random);
        for (int j = 0; j < Math.min(6, list.size()); j++) {
            offers.add(list.get(j));
        }

        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();
        int rareChance = (month == 12 && day == 25) ? 15 : 250;
        if (this.random.nextInt(rareChance) == 0) {
            offers.add(new MerchantOffer(new ItemStack(Items.DIAMOND, 5), new ItemStack(WoodItems.FROST_ROSE.get(), 1),
                    5, 1, 0.05f));
        }
    }
}
