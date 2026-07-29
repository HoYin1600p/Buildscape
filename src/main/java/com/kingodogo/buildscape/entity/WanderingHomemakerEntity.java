package com.kingodogo.buildscape.entity;

import com.kingodogo.buildscape.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
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
    private int despawnDelay = 48000; // 40 minutes in ticks

    public WanderingHomemakerEntity(EntityType<? extends WanderingTrader> type, Level level) {
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

    @Override
    protected void updateTrades() {
        MerchantOffers offers = this.getOffers();
        offers.clear();

        List<MerchantOffer> list = new ArrayList<>();
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 5), new ItemStack(ModItems.MANGROVE_PROPAGULE.get(), 2), 8, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.POPLAR_SAPLING.get(), 4), 8, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.CHERRY_SAPLING.get(), 4), 8, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.PALE_OAK_SAPLING.get(), 4), 8, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.RED_MONETS.get(), 2), 6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.BLUE_MONETS.get(), 2), 6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.PURPLE_MONETS.get(), 2), 6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.LIGHT_BLUE_MONETS.get(), 2), 6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.PINK_MONETS.get(), 2), 6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.YELLOW_MONETS.get(), 2), 6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.CLOVER.get(), 7), 6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.RED_ROSE_VINES.get(), 2), 6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.BLACK_ROSE_VINES.get(), 2), 6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.BLUE_ROSE_VINES.get(), 2), 6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.WHITE_ROSE_VINES.get(), 2), 6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.SNOWY_GRASS_BLOCK.get(), 2), 2, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.RED_SPORE_BLOSSOM.get(), 2), 6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.CYAN_SPORE_BLOSSOM.get(), 2), 6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.BLUE_SPORE_BLOSSOM.get(), 2), 6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.PURPLE_SPORE_BLOSSOM.get(), 2), 6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.ORANGE_SPORE_BLOSSOM.get(), 2), 6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.ICICLE.get(), 4), 6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.SULFUR_SPIKE.get(), 4), 6, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.SULFUR.get(), 2), 8, 1, 0.05f));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.CINNABAR.get(), 2), 8, 1, 0.05f));

        Collections.shuffle(list, this.random);
        for (int j = 0; j < Math.min(5, list.size()); j++) {
            offers.add(list.get(j));
        }

        if (this.random.nextFloat() <= 0.15f) {
            offers.add(new MerchantOffer(new ItemStack(Items.DIAMOND, 12), new ItemStack(ModItems.ANCIENT_ASHEN_SCROLL.get(), 2), 1, 1, 0.0f));
        }
    }
}
