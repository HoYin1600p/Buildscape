package com.kingodogo.buildscape.trophy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public abstract class TrophyBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    private final TrophyDefinition definition;

    public TrophyBlock(TrophyDefinition definition, BlockBehaviour.Properties properties) {
        super(properties);
        this.definition = definition;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public TrophyDefinition getDefinition() {
        return definition;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TrophyBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return definition.getShape(facing);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TrophyBlockEntity trophyBe) {
            CompoundTag tag = com.kingodogo.buildscape.platform.Services.PLATFORM.getCustomData(stack, false);
            if (tag != null && tag.contains("ObtainedBy")) {
                trophyBe.setObtainedBy(com.kingodogo.buildscape.platform.Services.PLATFORM.getTagString(tag, "ObtainedBy", ""));
                trophyBe.setObtainedOn(com.kingodogo.buildscape.platform.Services.PLATFORM.getTagString(tag, "ObtainedOn", ""));
            } else if (placer instanceof Player player) {
                trophyBe.setObtainedBy(player.getScoreboardName());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                trophyBe.setObtainedOn(LocalDateTime.now().format(formatter));
            }
        }
    }

    public ItemStack createTrophyStack(BlockEntity blockEntity) {
        ItemStack stack = new ItemStack(this.asItem());
        if (blockEntity instanceof TrophyBlockEntity trophyBe) {
            CompoundTag tag = new CompoundTag();
            if (!trophyBe.getObtainedBy().isEmpty()) {
                tag.putString("ObtainedBy", trophyBe.getObtainedBy());
            }
            if (!trophyBe.getObtainedOn().isEmpty()) {
                tag.putString("ObtainedOn", trophyBe.getObtainedOn());
            }
            if (!tag.isEmpty()) {
                com.kingodogo.buildscape.platform.Services.PLATFORM.updateCustomData(stack, root -> root.merge(tag));
            }
        }
        return stack;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
