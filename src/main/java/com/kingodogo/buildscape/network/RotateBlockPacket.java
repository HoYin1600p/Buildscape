package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RotateBlockPacket {

    public enum ArrowDirection {
        UP, DOWN, LEFT, RIGHT;

        public static ArrowDirection fromOrdinal(int ord) {
            ArrowDirection[] values = values();
            if (ord >= 0 && ord < values.length) {
                return values[ord];
            }
            return UP;
        }
    }

    private final BlockPos pos;
    private final ArrowDirection direction;
    private final Direction playerFacing;

    public RotateBlockPacket(BlockPos pos, ArrowDirection direction, Direction playerFacing) {
        this.pos = pos;
        this.direction = direction;
        this.playerFacing = playerFacing;
    }

    public static void encode(RotateBlockPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeByte(msg.direction.ordinal());
        buf.writeByte(msg.playerFacing.get3DDataValue());
    }

    public static RotateBlockPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        ArrowDirection dir = ArrowDirection.fromOrdinal(buf.readByte());
        Direction playerFacing = Direction.from3DDataValue(buf.readByte());
        return new RotateBlockPacket(pos, dir, playerFacing);
    }

    public static void handle(RotateBlockPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null || !player.hasPermissions(2) || !player.mayBuild()) return;

            if (!player.isCrouching()) return;

            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();
            boolean hasWrench = (!mainHand.isEmpty() && mainHand.getItem() == ModItems.WRENCH.get()) ||
                                (!offHand.isEmpty() && offHand.getItem() == ModItems.WRENCH.get());
            if (!hasWrench) return;

            ServerLevel level = player.getLevel();
            BlockPos pos = msg.pos;

            if (!level.isLoaded(pos)) return;
            if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 64.0) return;
            if (player.blockActionRestricted(level, pos, player.gameMode.getGameModeForPlayer())) return;

            BlockState state = level.getBlockState(pos);
            if (state.isAir()) return;

            BlockState newState = calculateRotatedState(state, msg.direction, msg.playerFacing);

            if (!newState.equals(state)) {
                level.setBlock(pos, newState, 3);
                level.updateNeighborsAt(pos, state.getBlock());
                level.updateNeighborsAt(pos, newState.getBlock());
                level.updateNeighbourForOutputSignal(pos, newState.getBlock());
                newState.updateNeighbourShapes(level, pos, 3);
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS, 1.0f, 1.2f);
                level.sendParticles(
                        ParticleTypes.WAX_OFF,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        6, 0.25, 0.25, 0.25, 0.05
                );
            }
        });
        ctx.setPacketHandled(true);
    }

    @SuppressWarnings("unchecked")
    private static BlockState calculateRotatedState(BlockState state, ArrowDirection arrowDir, Direction playerFacing) {
        BlockState newState = state;

        if (arrowDir == ArrowDirection.UP || arrowDir == ArrowDirection.DOWN) {
            if (state.hasProperty(BlockStateProperties.HALF)) {
                Half targetHalf = arrowDir == ArrowDirection.UP ? Half.TOP : Half.BOTTOM;
                if (state.getValue(BlockStateProperties.HALF) != targetHalf) {
                    return state.setValue(BlockStateProperties.HALF, targetHalf);
                }
            }
            if (state.hasProperty(BlockStateProperties.SLAB_TYPE)) {
                SlabType current = state.getValue(BlockStateProperties.SLAB_TYPE);
                if (current != SlabType.DOUBLE) {
                    SlabType targetSlab = arrowDir == ArrowDirection.UP ? SlabType.TOP : SlabType.BOTTOM;
                    if (current != targetSlab) {
                        return state.setValue(BlockStateProperties.SLAB_TYPE, targetSlab);
                    }
                }
            }
        }

        Property<?> rawFacing = state.getBlock().getStateDefinition().getProperty("facing");
        if (rawFacing instanceof Property<?> && rawFacing.getValueClass() == Direction.class) {
            Property<Direction> facingProp = (Property<Direction>) rawFacing;
            Direction targetDirection = null;

            switch (arrowDir) {
                case UP -> targetDirection = Direction.UP;
                case DOWN -> targetDirection = Direction.DOWN;
                case LEFT -> targetDirection = playerFacing.getCounterClockWise();
                case RIGHT -> targetDirection = playerFacing.getClockWise();
            }

            if (targetDirection != null && facingProp.getPossibleValues().contains(targetDirection)) {
                return state.setValue(facingProp, targetDirection);
            }
        }

        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            Direction targetDirection = switch (arrowDir) {
                case UP -> playerFacing;
                case DOWN -> playerFacing.getOpposite();
                case LEFT -> playerFacing.getCounterClockWise();
                case RIGHT -> playerFacing.getClockWise();
            };
            if (state.getValue(BlockStateProperties.HORIZONTAL_FACING) != targetDirection) {
                return state.setValue(BlockStateProperties.HORIZONTAL_FACING, targetDirection);
            }
        }

        if (state.hasProperty(BlockStateProperties.AXIS)) {
            Direction.Axis currentAxis = state.getValue(BlockStateProperties.AXIS);
            Direction.Axis targetAxis = switch (arrowDir) {
                case UP, DOWN -> Direction.Axis.Y;
                case LEFT -> playerFacing.getCounterClockWise().getAxis();
                case RIGHT -> playerFacing.getClockWise().getAxis();
            };
            if (currentAxis != targetAxis) {
                return state.setValue(BlockStateProperties.AXIS, targetAxis);
            }
        }
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
            Direction.Axis targetAxis = switch (arrowDir) {
                case LEFT -> playerFacing.getCounterClockWise().getAxis();
                case RIGHT -> playerFacing.getClockWise().getAxis();
                case UP, DOWN -> playerFacing.getAxis();
            };
            if (state.getValue(BlockStateProperties.HORIZONTAL_AXIS) != targetAxis) {
                return state.setValue(BlockStateProperties.HORIZONTAL_AXIS, targetAxis);
            }
        }

        if (state.hasProperty(BlockStateProperties.ROTATION_16)) {
            int currentRot = state.getValue(BlockStateProperties.ROTATION_16);
            int newRot = switch (arrowDir) {
                case RIGHT, UP -> (currentRot + 1) % 16;
                case LEFT, DOWN -> (currentRot - 1 + 16) % 16;
            };
            return state.setValue(BlockStateProperties.ROTATION_16, newRot);
        }

        Rotation rot = switch (arrowDir) {
            case RIGHT -> Rotation.CLOCKWISE_90;
            case LEFT -> Rotation.COUNTERCLOCKWISE_90;
            case UP, DOWN -> Rotation.CLOCKWISE_180;
        };

        return state.rotate(rot);
    }
}
