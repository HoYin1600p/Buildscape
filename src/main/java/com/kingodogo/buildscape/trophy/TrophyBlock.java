package com.kingodogo.buildscape.trophy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class TrophyBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private final TrophyDefinition definition;

    public TrophyBlock(TrophyDefinition definition) {
        super(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_YELLOW)
                .strength(definition.getHardness(), definition.getResistance())
                .sound(definition.getSoundType())
                .lightLevel(state -> definition.getLightEmission())
                .hasPostProcess((state, getter, pos) -> true)
                .emissiveRendering((state, getter, pos) -> true)
                .noOcclusion());
        this.definition = definition;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public TrophyDefinition getDefinition() {
        return definition;
    }

    @Nullable
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

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TrophyBlockEntity trophyBe) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains("ObtainedBy")) {
                trophyBe.setObtainedBy(tag.getString("ObtainedBy"));
                trophyBe.setObtainedOn(tag.getString("ObtainedOn"));
            } else if (placer instanceof Player player) {
                trophyBe.setObtainedBy(player.getScoreboardName());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                trophyBe.setObtainedOn(LocalDateTime.now().format(formatter));
            }
        }
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TrophyBlockEntity trophyBe && !level.isClientSide && !player.isCreative()) {
            ItemStack stack = new ItemStack(this.asItem());
            CompoundTag tag = stack.getOrCreateTag();
            if (!trophyBe.getObtainedBy().isEmpty()) {
                tag.putString("ObtainedBy", trophyBe.getObtainedBy());
            }
            if (!trophyBe.getObtainedOn().isEmpty()) {
                tag.putString("ObtainedOn", trophyBe.getObtainedOn());
            }
            ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack);
            entity.setDefaultPickUpDelay();
            level.addFreshEntity(entity);
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(level, pos, state);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TrophyBlockEntity trophyBe) {
            CompoundTag tag = stack.getOrCreateTag();
            if (!trophyBe.getObtainedBy().isEmpty()) {
                tag.putString("ObtainedBy", trophyBe.getObtainedBy());
            }
            if (!trophyBe.getObtainedOn().isEmpty()) {
                tag.putString("ObtainedOn", trophyBe.getObtainedOn());
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

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
        if (definition.isFoil() || definition.getTier() == TrophyTier.SPECIAL || definition.getTier() == TrophyTier.NETHERITE) {
            if (random.nextFloat() < 0.15F) {
                double x = pos.getX() + 0.3D + random.nextDouble() * 0.4D;
                double y = pos.getY() + 0.5D + random.nextDouble() * 0.5D;
                double z = pos.getZ() + 0.3D + random.nextDouble() * 0.4D;
                level.addParticle(ParticleTypes.END_ROD, x, y, z, 0.0D, 0.01D, 0.0D);
            }
        }
    }
}
