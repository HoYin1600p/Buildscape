package com.kingodogo.buildscape.feature.biome;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;

public class BiomeBrushItem extends Item {

    public enum BiomeBrushTier {
        COPPER(1024),
        DIAMOND(2048),
        NETHERITE(4096);

        private final int durability;

        BiomeBrushTier(int durability) {
            this.durability = durability;
        }

        public int getDurability() {
            return durability;
        }
    }

    public static final String KEY_BIOME = "CapturedBiome";
    public static final String KEY_POS1 = "Pos1";
    public static final String KEY_POS2 = "Pos2";
    public static final String KEY_POS1_DIMENSION = "Pos1Dimension";
    public static final String KEY_POS2_DIMENSION = "Pos2Dimension";
    public static final int MAX_HORIZONTAL_SIZE = 64;

    private final BiomeBrushTier tier;

    public BiomeBrushItem(BiomeBrushTier tier, Properties properties) {
        super(properties.stacksTo(1).durability(tier.getDurability()));
        this.tier = tier;
    }

    public BiomeBrushTier getTier() {
        return tier;
    }

    @Override
    public int getEnchantmentValue() {
        return 14;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.UNBREAKING || enchantment == Enchantments.MENDING;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return true;
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return switch (tier) {
            case COPPER -> repair.is(Items.COPPER_INGOT);
            case DIAMOND -> repair.is(Items.DIAMOND);
            case NETHERITE -> repair.is(Items.NETHERITE_SCRAP);
        };
    }

    @Override
    public boolean canBeHurtBy(DamageSource source) {
        if (tier == BiomeBrushTier.NETHERITE && (source.isBypassInvul() || source.isFire() || source.isExplosion())) {
            if (source.isBypassInvul()) {
                return super.canBeHurtBy(source);
            }
            return false;
        }
        return super.canBeHurtBy(source);
    }

    @Nullable
    public String getCapturedBiome(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return (tag != null && tag.contains(KEY_BIOME)) ? tag.getString(KEY_BIOME) : null;
    }

    public void setCapturedBiome(ItemStack stack, String biomeId) {
        stack.getOrCreateTag().putString(KEY_BIOME, biomeId);
    }


    public void clearCapturedBiome(ItemStack stack, Player player) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            tag.remove(KEY_BIOME);
        }
        if (player.level.isClientSide()) {
            player.displayClientMessage(new TranslatableComponent("message.buildscape.biome_brush.cleared").withStyle(ChatFormatting.YELLOW), true);
        } else {
            player.displayClientMessage(new TranslatableComponent("message.buildscape.biome_brush.cleared").withStyle(ChatFormatting.YELLOW), true);
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WOOL_BREAK, SoundSource.PLAYERS, 1.0f, 1.2f);
        }
    }

    private static boolean isInDimension(ItemStack stack, String key, String dimension) {
        CompoundTag tag = stack.getTag();
        return tag == null || !tag.contains(key, 8) || tag.getString(key).equals(dimension);
    }

    @Nullable
    public BlockPos getPos1(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return (tag != null && tag.contains(KEY_POS1)) ? NbtUtils.readBlockPos(tag.getCompound(KEY_POS1)) : null;
    }

    public void setPos1(ItemStack stack, BlockPos pos, Player player) {
        stack.getOrCreateTag().put(KEY_POS1, NbtUtils.writeBlockPos(pos));
        stack.getOrCreateTag().putString(KEY_POS1_DIMENSION, player.level.dimension().location().toString());
        player.displayClientMessage(new TranslatableComponent("message.buildscape.biome_brush.pos1", pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.WHITE), true);
        player.level.playSound(null, pos, SoundEvents.NOTE_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    @Nullable
    public BlockPos getPos2(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return (tag != null && tag.contains(KEY_POS2)) ? NbtUtils.readBlockPos(tag.getCompound(KEY_POS2)) : null;
    }

    public void setPos2(ItemStack stack, BlockPos pos, Player player) {
        stack.getOrCreateTag().put(KEY_POS2, NbtUtils.writeBlockPos(pos));
        stack.getOrCreateTag().putString(KEY_POS2_DIMENSION, player.level.dimension().location().toString());
        player.displayClientMessage(new TranslatableComponent("message.buildscape.biome_brush.pos2", pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.WHITE), true);
        player.level.playSound(null, pos, SoundEvents.NOTE_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 1.2f);

        BlockPos pos1 = getPos1(stack);
        if (pos1 != null) {
            int width = Math.abs(pos.getX() - pos1.getX()) + 1;
            int height = Math.abs(pos.getY() - pos1.getY()) + 1;
            int length = Math.abs(pos.getZ() - pos1.getZ()) + 1;
            long count = (long) width * height * length;
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 0.8f);
            player.displayClientMessage(new TranslatableComponent("message.buildscape.biome_brush.selection", width, height, length, count).withStyle(ChatFormatting.AQUA), false);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();
        BlockPos clickedPos = context.getClickedPos();

        if (stack.getDamageValue() >= stack.getMaxDamage()) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0f, 0.8f);
            player.displayClientMessage(new TranslatableComponent("message.buildscape.biome_brush.broken").withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }

        if (player.isShiftKeyDown()) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }

            String biomeStr = getCapturedBiome(stack);
            BlockPos pos1 = getPos1(stack);
            BlockPos pos2 = getPos2(stack);

            if (biomeStr == null || pos1 == null || pos2 == null) {
                player.displayClientMessage(new TranslatableComponent("message.buildscape.biome_brush.cannot_apply").withStyle(ChatFormatting.RED), true);
                return InteractionResult.FAIL;
            }

            ResourceLocation biomeKey = ResourceLocation.tryParse(biomeStr);
            if (biomeKey == null) {
                return InteractionResult.FAIL;
            }

            ServerLevel serverLevel = (ServerLevel) level;
            Registry<Biome> registry = serverLevel.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
            ResourceKey<Biome> key = ResourceKey.create(Registry.BIOME_REGISTRY, biomeKey);
            Optional<Holder<Biome>> holderOpt = registry.getHolder(key);

            if (holderOpt.isEmpty()) {
                player.displayClientMessage(new TranslatableComponent("message.buildscape.biome_brush.invalid_biome", biomeStr).withStyle(ChatFormatting.RED), true);
                return InteractionResult.FAIL;
            }
            int minX = Math.min(pos1.getX(), pos2.getX());
            int maxX = Math.max(pos1.getX(), pos2.getX());
            int minY = Math.min(pos1.getY(), pos2.getY());
            int maxY = Math.max(pos1.getY(), pos2.getY());
            int minZ = Math.min(pos1.getZ(), pos2.getZ());
            int maxZ = Math.max(pos1.getZ(), pos2.getZ());

            String currentDimension = serverLevel.dimension().location().toString();
            if (!isInDimension(stack, KEY_POS1_DIMENSION, currentDimension)
                    || !isInDimension(stack, KEY_POS2_DIMENSION, currentDimension)) {
                player.displayClientMessage(new TranslatableComponent("message.buildscape.biome_brush.wrong_dimension").withStyle(ChatFormatting.RED), true);
                return InteractionResult.FAIL;
            }
            if ((long) maxX - minX + 1 > MAX_HORIZONTAL_SIZE || (long) maxZ - minZ + 1 > MAX_HORIZONTAL_SIZE) {
                player.displayClientMessage(new TranslatableComponent("message.buildscape.biome_brush.too_large", MAX_HORIZONTAL_SIZE).withStyle(ChatFormatting.RED), true);
                return InteractionResult.FAIL;
            }

            if (!(player instanceof ServerPlayer serverPlayer) || !player.mayBuild()
                    || player.distanceToSqr(pos1.getX() + 0.5, pos1.getY() + 0.5, pos1.getZ() + 0.5) > 64.0 * 64.0
                    || player.distanceToSqr(pos2.getX() + 0.5, pos2.getY() + 0.5, pos2.getZ() + 0.5) > 64.0 * 64.0
                    || minY < level.getMinBuildHeight() || maxY >= level.getMaxBuildHeight()) {
                player.displayClientMessage(new TextComponent("Cannot paint: both corners must be within 64 blocks and you must have build permission.").withStyle(ChatFormatting.RED), true);
                return InteractionResult.FAIL;
            }

            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos bottom = new BlockPos(x, minY, z);
                    BlockPos top = new BlockPos(x, maxY, z);
                    boolean columnAllowed = level.hasChunkAt(bottom) && level.getWorldBorder().isWithinBounds(bottom)
                            && canPaintAt(level, serverPlayer, bottom, context.getClickedFace(), stack)
                            && (bottom.equals(top) || canPaintAt(level, serverPlayer, top, context.getClickedFace(), stack));
                    for (int y = level.getMinBuildHeight(); y < level.getMaxBuildHeight() && columnAllowed; y += 16) {
                        if (y != minY && y != maxY) {
                            columnAllowed = canPaintAt(level, serverPlayer, new BlockPos(x, y, z), context.getClickedFace(), stack);
                        }
                    }
                    if (!columnAllowed) {
                        player.displayClientMessage(new TextComponent("Cannot paint: the selection includes an unloaded or protected column.").withStyle(ChatFormatting.RED), true);
                        return InteractionResult.FAIL;
                    }
                }
            }

            int unbreakingLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack);
            boolean creative = player.getAbilities().instabuild;
            int affectedBlocks = 0;
            Set<ChunkPos> modifiedChunks = new HashSet<>();
            BiomeTintData tints = BiomeTintData.get(serverLevel);
            boolean broken = false;
            for (int x = minX; x <= maxX && !broken; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (!creative && stack.getDamageValue() >= stack.getMaxDamage()) {
                        broken = true;
                        break;
                    }
                    if (!tints.putColumn(x, z, biomeKey)) {
                        continue;
                    }
                    modifiedChunks.add(new ChunkPos(x >> 4, z >> 4));
                    affectedBlocks++;
                    if (BiomeBrushDurability.chargesDurability(creative, unbreakingLevel, level.random::nextInt)) {
                        stack.setDamageValue(stack.getDamageValue() + 1);
                    }
                }
            }

            for (ChunkPos chunk : modifiedChunks) {
                BiomeTintSync.sync(serverLevel, chunk);
            }
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BONE_MEAL_USE, SoundSource.PLAYERS, 1.0f, 1.0f);

            Component biomeName = new TranslatableComponent("biome." + biomeKey.getNamespace() + "." + biomeKey.getPath());
            player.displayClientMessage(new TranslatableComponent("message.buildscape.biome_brush.applied", biomeName).withStyle(ChatFormatting.GREEN), true);
            player.displayClientMessage(new TranslatableComponent("message.buildscape.biome_brush.applied_detail", affectedBlocks).withStyle(ChatFormatting.GREEN), false);

            CompoundTag tag = stack.getTag();
            if (tag != null) {
                tag.remove(KEY_POS1);
                tag.remove(KEY_POS2);
                tag.remove(KEY_POS1_DIMENSION);
                tag.remove(KEY_POS2_DIMENSION);
            }

            return InteractionResult.SUCCESS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        String biomeStr = getCapturedBiome(stack);
        if (biomeStr == null) {
            Holder<Biome> biomeHolder = level.getBiome(clickedPos);
            ResourceLocation biomeKey = registryKey(level, biomeHolder.value());
            if (biomeKey != null) {
                setCapturedBiome(stack, biomeKey.toString());
                Component biomeName = new TranslatableComponent("biome." + biomeKey.getNamespace() + "." + biomeKey.getPath());
                player.displayClientMessage(new TranslatableComponent("message.buildscape.biome_brush.captured", biomeName).withStyle(ChatFormatting.GREEN), true);
                level.playSound(null, clickedPos, SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.PLAYERS, 1.0f, 1.2f);
                if (player instanceof ServerPlayer serverPlayer) {
                    Advancement adv = serverPlayer.getServer().getAdvancements().getAdvancement(new ResourceLocation("buildscape", "touch_grass"));
                    if (adv != null) {
                        serverPlayer.getAdvancements().award(adv, "capture_biome");
                    }
                }
            }
        } else {
            setPos1(stack, clickedPos, player);
        }

        return InteractionResult.SUCCESS;
    }

    private static ResourceLocation registryKey(Level level, Biome biome) {
        return level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(biome);
    }

    private static boolean canPaintAt(Level level, ServerPlayer player, BlockPos pos, Direction face, ItemStack stack) {
        return level.mayInteract(player, pos)
                && player.mayUseItemAt(pos, face, stack)
                && !player.blockActionRestricted(level, pos, player.gameMode.getGameModeForPlayer())
                && !ForgeEventFactory.onBlockPlace(player, BlockSnapshot.create(level.dimension(), level, pos), Direction.UP);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        String biomeStr = getCapturedBiome(stack);
        if (biomeStr != null && !biomeStr.isEmpty()) {
            ResourceLocation biomeKey = ResourceLocation.tryParse(biomeStr);
            if (biomeKey != null) {
                Component biomeName = new TranslatableComponent("biome." + biomeKey.getNamespace() + "." + biomeKey.getPath());
                tooltip.add(new TranslatableComponent("tooltip.buildscape.biome_brush.biome", biomeName).withStyle(ChatFormatting.GREEN));
            } else {
                tooltip.add(new TranslatableComponent("tooltip.buildscape.biome_brush.biome", new TranslatableComponent("tooltip.buildscape.biome_brush.none")).withStyle(ChatFormatting.GRAY));
            }
        } else {
            tooltip.add(new TranslatableComponent("tooltip.buildscape.biome_brush.biome", new TranslatableComponent("tooltip.buildscape.biome_brush.none")).withStyle(ChatFormatting.GRAY));
        }

        tooltip.add(new TranslatableComponent("tooltip.buildscape.biome_brush.desc1").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("tooltip.buildscape.biome_brush.desc2").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("tooltip.buildscape.biome_brush.desc3").withStyle(ChatFormatting.AQUA));
        tooltip.add(new TranslatableComponent("tooltip.buildscape.biome_brush.desc4").withStyle(ChatFormatting.GRAY));

        super.appendHoverText(stack, level, tooltip, flag);
    }
}
