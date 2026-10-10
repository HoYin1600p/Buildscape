package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
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
    public boolean canDestroyBlock(ItemStack stack, net.minecraft.world.level.block.state.BlockState state,
                                   Level level, BlockPos pos, net.minecraft.world.entity.LivingEntity entity) {
        return false;
    }

    public int getEnchantmentValue() {
        return 14;
    }

    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return true;
    }

    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return switch (tier) {
            case COPPER -> repair.is(net.minecraft.world.item.Items.COPPER_INGOT);
            case DIAMOND -> repair.is(net.minecraft.world.item.Items.DIAMOND);
            case NETHERITE -> repair.is(net.minecraft.world.item.Items.NETHERITE_SCRAP);
        };
    }

    public boolean supportsEnchantment(ItemStack stack, Object enchantment) {
        return Services.PLATFORM.supportsBiomeBrushEnchantment(stack, enchantment);
    }

    public String getCapturedBiome(ItemStack stack) {
        CompoundTag tag = Services.PLATFORM.getCustomData(stack, false);
        return (tag != null && tag.contains(KEY_BIOME)) ? Services.PLATFORM.getTagString(tag, KEY_BIOME, null) : null;
    }

    public void setCapturedBiome(ItemStack stack, String biomeId) {
        Services.PLATFORM.updateCustomData(stack, tag -> tag.putString(KEY_BIOME, biomeId));
    }

    public void clearCapturedBiome(ItemStack stack, Player player) {
        Services.PLATFORM.updateCustomData(stack, tag -> tag.remove(KEY_BIOME));
        Component clearedMsg = com.kingodogo.buildscape.util.ComponentHelper.translatable("message.buildscape.biome_brush.cleared").withStyle(ChatFormatting.YELLOW);
        Services.PLATFORM.sendActionBarMessage(player, clearedMsg);
        Level pLevel = Services.PLATFORM.getEntityLevel(player);
        if (!pLevel.isClientSide()) {
            Services.PLATFORM.playWoolBreak(pLevel, player.getX(), player.getY(), player.getZ());
        }
    }

    private static boolean isInDimension(ItemStack stack, String key, String dimension) {
        CompoundTag tag = Services.PLATFORM.getCustomData(stack, false);
        if (tag == null || !tag.contains(key)) return true;
        return Services.PLATFORM.getTagString(tag, key, "").equals(dimension);
    }

    public BlockPos getPos1(ItemStack stack) {
        CompoundTag tag = Services.PLATFORM.getCustomData(stack, false);
        if (tag == null) return null;
        if (tag.contains(KEY_POS1 + "X")) {
            return new BlockPos(
                    Services.PLATFORM.getTagInt(tag, KEY_POS1 + "X", 0),
                    Services.PLATFORM.getTagInt(tag, KEY_POS1 + "Y", 0),
                    Services.PLATFORM.getTagInt(tag, KEY_POS1 + "Z", 0)
            );
        }
        return null;
    }

    public void setPos1(ItemStack stack, BlockPos pos, Player player) {
        String dimension = Services.PLATFORM.getDimensionId(Services.PLATFORM.getEntityLevel(player)).toString();
        Services.PLATFORM.updateCustomData(stack, tag -> {
            tag.putInt(KEY_POS1 + "X", pos.getX());
            tag.putInt(KEY_POS1 + "Y", pos.getY());
            tag.putInt(KEY_POS1 + "Z", pos.getZ());
            tag.putString(KEY_POS1_DIMENSION, dimension);
        });
        Component msg = ComponentHelper.translatable("message.buildscape.biome_brush.pos1", pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.WHITE);
        Services.PLATFORM.sendActionBarMessage(player, msg);
        Services.PLATFORM.playNoteBlockChime(Services.PLATFORM.getEntityLevel(player), pos, 1.0f);
    }

    public BlockPos getPos2(ItemStack stack) {
        CompoundTag tag = Services.PLATFORM.getCustomData(stack, false);
        if (tag == null) return null;
        if (tag.contains(KEY_POS2 + "X")) {
            return new BlockPos(
                    Services.PLATFORM.getTagInt(tag, KEY_POS2 + "X", 0),
                    Services.PLATFORM.getTagInt(tag, KEY_POS2 + "Y", 0),
                    Services.PLATFORM.getTagInt(tag, KEY_POS2 + "Z", 0)
            );
        }
        return null;
    }

    public void setPos2(ItemStack stack, BlockPos pos, Player player) {
        String dimension = Services.PLATFORM.getDimensionId(Services.PLATFORM.getEntityLevel(player)).toString();
        Services.PLATFORM.updateCustomData(stack, tag -> {
            tag.putInt(KEY_POS2 + "X", pos.getX());
            tag.putInt(KEY_POS2 + "Y", pos.getY());
            tag.putInt(KEY_POS2 + "Z", pos.getZ());
            tag.putString(KEY_POS2_DIMENSION, dimension);
        });
        Component msg = ComponentHelper.translatable("message.buildscape.biome_brush.pos2", pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.WHITE);
        Services.PLATFORM.sendActionBarMessage(player, msg);
        Services.PLATFORM.playNoteBlockChime(Services.PLATFORM.getEntityLevel(player), pos, 1.2f);

        BlockPos pos1 = getPos1(stack);
        if (pos1 != null) {
            int width = Math.abs(pos.getX() - pos1.getX()) + 1;
            int height = Math.abs(pos.getY() - pos1.getY()) + 1;
            int length = Math.abs(pos.getZ() - pos1.getZ()) + 1;
            long count = (long) width * height * length;
            Services.PLATFORM.playExperienceOrbPickup(Services.PLATFORM.getEntityLevel(player), player.getX(), player.getY(), player.getZ(), 0.5f, 0.8f);
            Services.PLATFORM.sendSystemMessage(player, ComponentHelper.translatable("message.buildscape.biome_brush.selection", width, height, length, count).withStyle(ChatFormatting.AQUA));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();
        BlockPos clickedPos = context.getClickedPos();

        if (stack.getDamageValue() >= stack.getMaxDamage()) {
            Services.PLATFORM.playDispenserFail(level, player.getX(), player.getY(), player.getZ());
            Services.PLATFORM.sendActionBarMessage(player, ComponentHelper.translatable("message.buildscape.biome_brush.broken").withStyle(ChatFormatting.RED));
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
                Services.PLATFORM.sendActionBarMessage(player, ComponentHelper.translatable("message.buildscape.biome_brush.cannot_apply").withStyle(ChatFormatting.RED));
                return InteractionResult.FAIL;
            }

            CommonId biomeKey = CommonId.tryParse(biomeStr);
            if (biomeKey == null) return InteractionResult.FAIL;

            ServerLevel serverLevel = (ServerLevel) level;
            Holder<Biome> biomeHolder = Services.PLATFORM.getBiomeHolder(serverLevel, biomeKey);

            if (biomeHolder == null) {
                Services.PLATFORM.sendActionBarMessage(player, ComponentHelper.translatable("message.buildscape.biome_brush.invalid_biome", biomeStr).withStyle(ChatFormatting.RED));
                return InteractionResult.FAIL;
            }

            int minX = Math.min(pos1.getX(), pos2.getX());
            int maxX = Math.max(pos1.getX(), pos2.getX());
            int minY = Math.min(pos1.getY(), pos2.getY());
            int maxY = Math.max(pos1.getY(), pos2.getY());
            int minZ = Math.min(pos1.getZ(), pos2.getZ());
            int maxZ = Math.max(pos1.getZ(), pos2.getZ());

            String currentDimension = Services.PLATFORM.getDimensionId(serverLevel).toString();
            if (!isInDimension(stack, KEY_POS1_DIMENSION, currentDimension)
                    || !isInDimension(stack, KEY_POS2_DIMENSION, currentDimension)) {
                Services.PLATFORM.sendActionBarMessage(player, ComponentHelper.translatable("message.buildscape.biome_brush.wrong_dimension").withStyle(ChatFormatting.RED));
                return InteractionResult.FAIL;
            }
            if (maxX - minX + 1 > MAX_HORIZONTAL_SIZE || maxZ - minZ + 1 > MAX_HORIZONTAL_SIZE) {
                Services.PLATFORM.sendActionBarMessage(player, ComponentHelper.translatable("message.buildscape.biome_brush.too_large", MAX_HORIZONTAL_SIZE).withStyle(ChatFormatting.RED));
                return InteractionResult.FAIL;
            }

            int affectedBlocks = Services.PLATFORM.applyBiomeToArea(serverLevel, pos1, pos2, biomeHolder, stack);

            Services.PLATFORM.playBoneMealUse(level, player.getX(), player.getY(), player.getZ());

            Component biomeName = ComponentHelper.translatable("biome." + biomeKey.getNamespace() + "." + biomeKey.getPath());
            Services.PLATFORM.sendActionBarMessage(player, ComponentHelper.translatable("message.buildscape.biome_brush.applied", biomeName).withStyle(ChatFormatting.GREEN));
            Services.PLATFORM.sendSystemMessage(player, ComponentHelper.translatable("message.buildscape.biome_brush.applied_detail", affectedBlocks).withStyle(ChatFormatting.GREEN));

            Services.PLATFORM.updateCustomData(stack, tag -> {
                tag.remove(KEY_POS1 + "X");
                tag.remove(KEY_POS1 + "Y");
                tag.remove(KEY_POS1 + "Z");
                tag.remove(KEY_POS2 + "X");
                tag.remove(KEY_POS2 + "Y");
                tag.remove(KEY_POS2 + "Z");
                tag.remove(KEY_POS1_DIMENSION);
                tag.remove(KEY_POS2_DIMENSION);
            });

            return InteractionResult.SUCCESS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        String biomeStr = getCapturedBiome(stack);
        if (biomeStr == null) {
            Holder<Biome> biomeHolder = level.getBiome(clickedPos);
            CommonId biomeKey = Services.PLATFORM.getBiomeId(level, biomeHolder);
            if (biomeKey != null) {
                setCapturedBiome(stack, biomeKey.toString());
                Component biomeName = ComponentHelper.translatable("biome." + biomeKey.getNamespace() + "." + biomeKey.getPath());
                Services.PLATFORM.sendActionBarMessage(player, ComponentHelper.translatable("message.buildscape.biome_brush.captured", biomeName).withStyle(ChatFormatting.GREEN));
                Services.PLATFORM.playDragonBreathFill(level, clickedPos);
                if (player instanceof ServerPlayer serverPlayer) {
                    Services.PLATFORM.awardAdvancement(serverPlayer, new CommonId("buildscape", "touch_grass"), "capture_biome");
                }
            }
        } else {
            setPos1(stack, clickedPos, player);
        }

        return InteractionResult.SUCCESS;
    }

    public void addHoverText(ItemStack stack, java.util.function.Consumer<Component> tooltip) {
        String biomeStr = getCapturedBiome(stack);
        if (biomeStr != null && !biomeStr.isEmpty()) {
            CommonId biomeKey = CommonId.tryParse(biomeStr);
            if (biomeKey != null) {
                Component biomeName = ComponentHelper.translatable("biome." + biomeKey.getNamespace() + "." + biomeKey.getPath());
                tooltip.accept(ComponentHelper.translatable("tooltip.buildscape.biome_brush.biome", biomeName).withStyle(ChatFormatting.GREEN));
            } else {
                tooltip.accept(ComponentHelper.translatable("tooltip.buildscape.biome_brush.biome", ComponentHelper.translatable("tooltip.buildscape.biome_brush.none")).withStyle(ChatFormatting.GRAY));
            }
        } else {
            tooltip.accept(ComponentHelper.translatable("tooltip.buildscape.biome_brush.biome", ComponentHelper.translatable("tooltip.buildscape.biome_brush.none")).withStyle(ChatFormatting.GRAY));
        }

        tooltip.accept(ComponentHelper.translatable("tooltip.buildscape.biome_brush.desc1").withStyle(ChatFormatting.GRAY));
        tooltip.accept(ComponentHelper.translatable("tooltip.buildscape.biome_brush.desc2").withStyle(ChatFormatting.GRAY));
        tooltip.accept(ComponentHelper.translatable("tooltip.buildscape.biome_brush.desc3").withStyle(ChatFormatting.AQUA));
    }
}
