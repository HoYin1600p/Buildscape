package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.item.HazeBushItem;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public abstract class HazeBushBlock extends ModBushBlock {
    public static final BooleanProperty HAS_HAZE = BooleanProperty.create("has_haze");
    private final DyeColor color;

    public HazeBushBlock(DyeColor color, Properties properties) {
        super(properties);
        this.color = color;
        registerDefaultState(defaultBlockState().setValue(HAS_HAZE, true));
    }

    public DyeColor getColor() { return color; }
    public abstract BlockItem createHazeItem(Item.Properties properties);

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HAS_HAZE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(HAS_HAZE, !HazeBushItem.isDrained(context.getItemInHand()));
    }

    public ItemStack cloneStack(BlockState state) {
        ItemStack stack = new ItemStack(this);
        return state.getValue(HAS_HAZE) ? stack : HazeBushItem.markDrained(stack);
    }

    @Override
    public InteractionResult onInteract(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
        InteractionResult result = interact(state, level, pos, player, hand, color, false);
        return result != InteractionResult.PASS ? result : super.onInteract(state, level, pos, player, hand);
    }

    static InteractionResult interact(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, DyeColor color, boolean potted) {
        ItemStack held = player.getItemInHand(hand);
        Item mist = Services.PLATFORM.getItem(new CommonId("buildscape", "bottle_of_mist"));
        if (held.is(Items.GLASS_BOTTLE) && state.getValue(HAS_HAZE)) {
            if (!level.isClientSide()) {
                level.setBlock(pos, state.setValue(HAS_HAZE, false), 3);
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.BLOCKS, 1, 1);
                level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, .4F, 1.2F);
                exchangeBottle(player, hand, new ItemStack(mist));
            }
            return Services.PLATFORM.sidedSuccess(level.isClientSide());
        }
        DyeColor dye = Services.PLATFORM.getDyeColor(held);
        if (dye != null && dye != color) {
            Block target = ModBlocks.get((potted ? ModBlocks.POTTED_COLORED_HAZE_BUSHES
                    : ModBlocks.COLORED_HAZE_BUSHES).get(dye));
            if (target != null) {
                if (!level.isClientSide()) {
                    level.setBlock(pos, target.defaultBlockState().setValue(HAS_HAZE, state.getValue(HAS_HAZE)), 3);
                    level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1, 1);
                    if (!player.getAbilities().instabuild) held.shrink(1);
                }
                return Services.PLATFORM.sidedSuccess(level.isClientSide());
            }
        }
        if (held.is(mist) && !state.getValue(HAS_HAZE)) {
            if (!level.isClientSide()) {
                level.setBlock(pos, state.setValue(HAS_HAZE, true), 3);
                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1, 1);
                level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, .4F, .8F);
                exchangeBottle(player, hand, new ItemStack(Items.GLASS_BOTTLE));
                level.levelEvent(2005, pos, 0);
            }
            return Services.PLATFORM.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    private static void exchangeBottle(Player player, InteractionHand hand, ItemStack result) {
        ItemStack held = player.getItemInHand(hand);
        if (!player.getAbilities().instabuild) held.shrink(1);
        if (held.isEmpty()) player.setItemInHand(hand, result);
        else if (!player.getInventory().add(result)) player.drop(result, false);
    }

    public static float[] getPastelColor(DyeColor color) {
        if (color == null) return new float[]{.94F, .94F, .94F};
        return switch (color) {
            case WHITE -> new float[]{.96F, .96F, .98F};
            case ORANGE -> new float[]{1, .80F, .62F};
            case MAGENTA -> new float[]{.92F, .68F, .88F};
            case LIGHT_BLUE -> new float[]{.72F, .86F, .98F};
            case YELLOW -> new float[]{1, .94F, .65F};
            case LIME -> new float[]{.75F, .95F, .70F};
            case PINK -> new float[]{1, .78F, .85F};
            case GRAY -> new float[]{.62F, .65F, .70F};
            case LIGHT_GRAY -> new float[]{.82F, .84F, .87F};
            case CYAN -> new float[]{.68F, .92F, .92F};
            case PURPLE -> new float[]{.80F, .70F, .92F};
            case BLUE -> new float[]{.65F, .76F, .95F};
            case BROWN -> new float[]{.80F, .70F, .62F};
            case GREEN -> new float[]{.65F, .85F, .68F};
            case RED -> new float[]{.98F, .68F, .70F};
            case BLACK -> new float[]{.35F, .35F, .38F};
        };
    }
}
