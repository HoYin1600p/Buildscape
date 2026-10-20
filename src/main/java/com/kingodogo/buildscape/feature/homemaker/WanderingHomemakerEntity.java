package com.kingodogo.buildscape.feature.homemaker;

import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.registry.items.CopperItems;
import com.kingodogo.buildscape.registry.items.MasonryItems;
import com.kingodogo.buildscape.registry.items.ShapedWoodItems;
import com.kingodogo.buildscape.registry.items.WoodItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WanderingHomemakerEntity extends WanderingTrader {
    private int despawnDelay = 48000;

    public WanderingHomemakerEntity(EntityType<? extends WanderingTrader> type, Level level) {
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

    @Override
    protected void updateTrades() {
        MerchantOffers offers = this.getOffers();
        offers.clear();

        List<MerchantOffer> list = new ArrayList<>();
        list.add(
                new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(WoodItems.MANGROVE_PROPAGULE.get(), 5),
                        8, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1),
                new ItemStack(ShapedWoodItems.POPLAR_SAPLING.get(), 4), 8,
                1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1),
                new ItemStack(ShapedWoodItems.CHERRY_SAPLING.get(), 4), 8,
                1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1),
                new ItemStack(ShapedWoodItems.PALE_OAK_SAPLING.get(), 4),
                8, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(WoodItems.RED_MONETS.get(), 2), 6, 1,
                0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(WoodItems.BLUE_MONETS.get(), 2), 6, 1,
                0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(WoodItems.PURPLE_MONETS.get(), 2), 6,
                1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(WoodItems.LIGHT_BLUE_MONETS.get(), 2),
                6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(WoodItems.PINK_MONETS.get(), 2), 6, 1,
                0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(WoodItems.YELLOW_MONETS.get(), 2), 6,
                1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(WoodItems.CLOVER.get(), 7), 6, 1,
                0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(WoodItems.RED_ROSE_VINES.get(), 2), 6,
                1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(WoodItems.BLACK_ROSE_VINES.get(), 2),
                6, 1, 0.05f));
        list.add(
                new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(WoodItems.BLUE_ROSE_VINES.get(), 2), 6,
                        1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(WoodItems.WHITE_ROSE_VINES.get(), 2),
                6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(WoodItems.SNOWY_GRASS_BLOCK.get(), 2),
                2, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(WoodItems.RED_SPORE_BLOSSOM.get(), 2),
                6, 1, 0.05f));
        list.add(
                new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(WoodItems.CYAN_SPORE_BLOSSOM.get(), 2),
                        6, 1, 0.05f));
        list.add(
                new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(WoodItems.BLUE_SPORE_BLOSSOM.get(), 2),
                        6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1),
                new ItemStack(WoodItems.PURPLE_SPORE_BLOSSOM.get(), 2), 6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1),
                new ItemStack(WoodItems.ORANGE_SPORE_BLOSSOM.get(), 2), 6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(WoodItems.ICICLE.get(), 32), 8, 1,
                0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1),
                new ItemStack(ShapedWoodItems.SULFUR_SPIKE.get(), 32), 8,
                1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1),
                new ItemStack(WoodItems.PACKED_ICICLE_BLOCK.get(), 4), 8, 1, 0.05f));
        list.add(
                new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ShapedWoodItems.SULFUR.get(), 4), 8, 1,
                        0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ShapedWoodItems.CINNABAR.get(), 2), 8,
                1,
                0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.WILDFLOWERS.get(), 4), 6,
                1,
                0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.LEAF_LITTER.get(), 4), 6,
                1,
                0.05f));

        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.DRY_GRASS.get(), 6), 8, 1,
                0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.TALL_DRY_GRASS.get(), 6),
                8,
                1, 0.05f));
        list.add(
                new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.BUSH.get(), 6), 8, 1,
                        0.05f));
        list.add(
                new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.CACTUS_FLOWER.get(), 6), 8,
                        1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.RED_BUSH.get(), 6), 8, 1,
                0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.FIREFLY_BUSH.get(), 6), 8,
                1,
                0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.OPEN_EYEBLOSSOM.get(), 6),
                8,
                1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1),
                new ItemStack(CopperItems.CLOSED_EYEBLOSSOM.get(), 6),
                8, 1, 0.05f));

        list.add(
                new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.ORANGE_HAZE_BUSH.get(), 6),
                        6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1),
                new ItemStack(CopperItems.MAGENTA_HAZE_BUSH.get(), 6),
                6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1),
                new ItemStack(CopperItems.LIGHT_BLUE_HAZE_BUSH.get(), 6), 6, 1, 0.05f));
        list.add(
                new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.YELLOW_HAZE_BUSH.get(), 6),
                        6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.LIME_HAZE_BUSH.get(), 6),
                6,
                1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.PINK_HAZE_BUSH.get(), 6),
                6,
                1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.GRAY_HAZE_BUSH.get(), 6),
                6,
                1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1),
                new ItemStack(CopperItems.LIGHT_GRAY_HAZE_BUSH.get(), 6), 6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.CYAN_HAZE_BUSH.get(), 6),
                6,
                1, 0.05f));
        list.add(
                new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.PURPLE_HAZE_BUSH.get(), 6),
                        6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.BLUE_HAZE_BUSH.get(), 6),
                6,
                1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.BROWN_HAZE_BUSH.get(), 6),
                6,
                1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.GREEN_HAZE_BUSH.get(), 6),
                6,
                1, 0.05f));
        list.add(
                new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.RED_HAZE_BUSH.get(), 6), 6,
                        1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.BLACK_HAZE_BUSH.get(), 6),
                6,
                1, 0.05f));

        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.SCULK_CATALYST.get(), 2),
                3,
                1, 0.05f));

        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ShapedWoodItems.MUFF_BLOCK.get(), 2),
                4, 1,
                0.05f));

        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1),
                new ItemStack(MasonryItems.BOTTLE_OF_MIST.get(), 16), 4,
                1, 0.05f));

        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(MasonryItems.CONFETTI_ITEM.get(), 32),
                6,
                1, 0.05f));

        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.RESIN_CLUMP.get(), 16), 8,
                1,
                0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.RESIN_BLOCK.get(), 16), 8,
                1,
                0.05f));

        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(CopperItems.CREAKING_HEART.get(), 2),
                2,
                1, 0.05f));

        Collections.shuffle(list, this.random);
        for (int j = 0; j < Math.min(5, list.size()); j++) {
            offers.add(list.get(j));
        }

        if (this.random.nextInt(5000) == 0) {
            offers.add(new MerchantOffer(new ItemStack(Items.DIAMOND, 12),
                    new ItemStack(MasonryItems.ANCIENT_ASHEN_SCROLL.get(), 1), 1, 1, 0.0f));
        }
    }
}
