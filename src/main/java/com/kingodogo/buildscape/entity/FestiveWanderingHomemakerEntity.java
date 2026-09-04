package com.kingodogo.buildscape.entity;

import com.kingodogo.buildscape.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class FestiveWanderingHomemakerEntity extends WanderingTrader {
    private int despawnDelay = 48000;

    public FestiveWanderingHomemakerEntity(EntityType<? extends WanderingTrader> type, Level level) {
        super(type, level);
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.Mob.createMobAttributes()
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, 0.5D)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 20.0D)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE, 16.0D);
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
        compound.putInt("DespawnDelay", this.despawnDelay);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("DespawnDelay")) {
            this.despawnDelay = compound.getInt("DespawnDelay");
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

        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.WHITE_SAND.get(), 8), 8, 1,
                0.05f));

        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.GREEN_SAND.get(), 8), 8, 1,
                0.05f));

        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.RED_SAND.get(), 8), 8, 1,
                0.05f));

        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.RED_TILES.get(), 8), 8, 1,
                0.05f));

        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.LIME_TILES.get(), 8), 8, 1,
                0.05f));

        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.SNOW_OVERLAY.get(), 4), 4, 1,
                0.05f));

        Supplier<Item>[] stockings = new Supplier[] {
                ModItems.BLACK_FESTIVE_STOCKING, ModItems.BLUE_FESTIVE_STOCKING, ModItems.BROWN_FESTIVE_STOCKING,
                ModItems.CYAN_FESTIVE_STOCKING, ModItems.GRAY_FESTIVE_STOCKING, ModItems.GREEN_FESTIVE_STOCKING,
                ModItems.LIGHT_BLUE_FESTIVE_STOCKING, ModItems.LIGHT_GRAY_FESTIVE_STOCKING,
                ModItems.LIME_FESTIVE_STOCKING,
                ModItems.MAGENTA_FESTIVE_STOCKING, ModItems.ORANGE_FESTIVE_STOCKING, ModItems.PINK_FESTIVE_STOCKING,
                ModItems.PURPLE_FESTIVE_STOCKING, ModItems.RED_FESTIVE_STOCKING, ModItems.WHITE_FESTIVE_STOCKING,
                ModItems.YELLOW_FESTIVE_STOCKING, ModItems.FESTIVE_STOCKING
        };
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(getRandom(stockings), 8), 4, 1,
                0.05f));

        Supplier<Item>[] stringLights = new Supplier[] {
                ModItems.WHITE_STRING_LIGHT, ModItems.ORANGE_STRING_LIGHT, ModItems.MAGENTA_STRING_LIGHT,
                ModItems.LIGHT_BLUE_STRING_LIGHT, ModItems.YELLOW_STRING_LIGHT, ModItems.LIME_STRING_LIGHT,
                ModItems.PINK_STRING_LIGHT, ModItems.GRAY_STRING_LIGHT, ModItems.LIGHT_GRAY_STRING_LIGHT,
                ModItems.CYAN_STRING_LIGHT, ModItems.PURPLE_STRING_LIGHT, ModItems.BLUE_STRING_LIGHT,
                ModItems.BROWN_STRING_LIGHT, ModItems.GREEN_STRING_LIGHT, ModItems.RED_STRING_LIGHT,
                ModItems.BLACK_STRING_LIGHT, ModItems.MULTICOLOR_STRING_LIGHT
        };
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 2), new ItemStack(getRandom(stringLights), 16), 8, 1,
                0.05f));

        Supplier<Item>[] snowyLeaves = new Supplier[] {
                ModItems.SNOWY_LEAVES, ModItems.SNOWY_OAK_LEAVES, ModItems.SNOWY_SPRUCE_LEAVES,
                ModItems.SNOWY_BIRCH_LEAVES, ModItems.SNOWY_JUNGLE_LEAVES, ModItems.SNOWY_ACACIA_LEAVES,
                ModItems.SNOWY_DARK_OAK_LEAVES, ModItems.SNOWY_MANGROVE_LEAVES, ModItems.SNOWY_AZALEA_LEAVES,
                ModItems.SNOWY_FLOWERING_AZALEA_LEAVES, ModItems.SNOWY_CHERRY_LEAVES, ModItems.SNOWY_PALE_OAK_LEAVES,
                ModItems.SNOWY_ORANGE_POPLAR_LEAVES, ModItems.SNOWY_RED_POPLAR_LEAVES,
                ModItems.SNOWY_YELLOW_POPLAR_LEAVES
        };
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(getRandom(snowyLeaves), 8), 8, 1,
                0.05f));

        Supplier<Item>[] ornaments = new Supplier[] {
                ModItems.WHITE_ORNAMENT, ModItems.ORANGE_ORNAMENT, ModItems.MAGENTA_ORNAMENT,
                ModItems.LIGHT_BLUE_ORNAMENT, ModItems.YELLOW_ORNAMENT, ModItems.LIME_ORNAMENT,
                ModItems.PINK_ORNAMENT, ModItems.GRAY_ORNAMENT, ModItems.LIGHT_GRAY_ORNAMENT,
                ModItems.CYAN_ORNAMENT, ModItems.PURPLE_ORNAMENT, ModItems.BLUE_ORNAMENT,
                ModItems.BROWN_ORNAMENT, ModItems.GREEN_ORNAMENT, ModItems.RED_ORNAMENT,
                ModItems.BLACK_ORNAMENT, ModItems.GLASS_ORNAMENT, ModItems.TINTED_GLASS_ORNAMENT
        };
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 2), new ItemStack(getRandom(ornaments), 16), 8, 1,
                0.05f));

        Supplier<Item>[] stars = new Supplier[] {
                ModItems.WHITE_STAR, ModItems.ORANGE_STAR, ModItems.MAGENTA_STAR,
                ModItems.LIGHT_BLUE_STAR, ModItems.YELLOW_STAR, ModItems.LIME_STAR,
                ModItems.PINK_STAR, ModItems.GRAY_STAR, ModItems.LIGHT_GRAY_STAR,
                ModItems.CYAN_STAR, ModItems.PURPLE_STAR, ModItems.BLUE_STAR,
                ModItems.BROWN_STAR, ModItems.GREEN_STAR, ModItems.RED_STAR,
                ModItems.BLACK_STAR, ModItems.GLOW_STAR
        };
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(getRandom(stars), 8), 8, 1, 0.05f));

        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1),
                new ItemStack(ModItems.PACKED_ICICLE_BLOCK.get(), 8), 8, 1, 0.05f));

        Supplier<Item>[] glowLights = new Supplier[] {
                ModItems.GLOW_LIGHTS, ModItems.MULTICOLOR_GLOW_LIGHTS
        };
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(getRandom(glowLights), 8), 8, 1,
                0.05f));

        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.FESTIVE_LAMP.get(), 8), 8, 1,
                0.05f));

        Collections.shuffle(list, this.random);
        for (int j = 0; j < Math.min(6, list.size()); j++) {
            offers.add(list.get(j));
        }

        java.time.LocalDate today = java.time.LocalDate.now();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();
        int rareChance = (month == 12 && day == 25) ? 15 : 250;
        if (this.random.nextInt(rareChance) == 0) {
            offers.add(new MerchantOffer(new ItemStack(Items.DIAMOND, 5), new ItemStack(ModItems.FROST_ROSE.get(), 1),
                    5, 1, 0.05f));
        }
    }
}
