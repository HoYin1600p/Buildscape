package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.item.HazeBushItem;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;

public class PottedHazeBushBlock extends FlowerPotBlock {
    public static final BooleanProperty HAS_HAZE = HazeBushBlock.HAS_HAZE;
    private final DyeColor color;
    private final Block plant;

    public PottedHazeBushBlock(DyeColor color, Block plant, Properties properties) {
        super(plant, properties);
        this.color = color;
        this.plant = plant;
        registerDefaultState(defaultBlockState().setValue(HAS_HAZE, true));
    }

    public DyeColor getColor() { return color; }
    public Block getPlant() { return plant; }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HAS_HAZE);
    }

    public ItemStack plantStack(BlockState state) {
        ItemStack stack = new ItemStack(plant);
        return state.getValue(HAS_HAZE) ? stack : HazeBushItem.markDrained(stack);
    }

    public InteractionResult onInteract(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
        InteractionResult result = HazeBushBlock.interact(state, level, pos, player, hand, color, true);
        if (result != InteractionResult.PASS) return result;
        ItemStack held = player.getItemInHand(hand);
        if (held.is(Items.BONE_MEAL)) {
            if (!level.isClientSide()) {
                popResource(level, pos, new ItemStack(plant));
                if (!player.getAbilities().instabuild) held.shrink(1);
                level.levelEvent(1505, pos, 0);
            }
            return Services.PLATFORM.sidedSuccess(level.isClientSide());
        }
        if (HollowLogBlock.getPottedBlockState(held.getItem()) != null) return InteractionResult.CONSUME;
        if (!level.isClientSide()) {
            ItemStack drop = plantStack(state);
            if (held.isEmpty()) player.setItemInHand(hand, drop);
            else if (!player.addItem(drop)) player.drop(drop, false);
            level.setBlock(pos, Blocks.FLOWER_POT.defaultBlockState(), 3);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
        return Services.PLATFORM.sidedSuccess(level.isClientSide());
    }
}
