package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
public class TrappedDecoratedPotBlock extends DecoratedPotBlock {

    public TrappedDecoratedPotBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TrappedDecoratedPotBlockEntity(pos, state);
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof TrappedDecoratedPotBlockEntity be)) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = player.getItemInHand(hand);

        if (heldItem.isEmpty()) {
            if (player.isShiftKeyDown() && !be.isEmpty()) {
                if (!level.isClientSide()) {
                    ItemStack stored = be.getStoredItem().copy();
                    be.setStoredItem(ItemStack.EMPTY, true);

                    if (stored.getItem() instanceof SpawnEggItem && level instanceof ServerLevel serverLevel) {
                        Services.PLATFORM.spawnMobFromEgg(serverLevel, pos, stored);
                    } else {
                        if (!player.getInventory().add(stored)) {
                            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stored);
                        }
                    }
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
                return Services.PLATFORM.sidedSuccess(level.isClientSide());
            }

            if (!player.isShiftKeyDown()) {
                if (!level.isClientSide()) {
                    level.playSound(null, pos, ModSounds.DECORATED_POT_HIT.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
                    be.triggerWobble(DecoratedPotBlockEntity.WobbleStyle.POSITIVE);
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
                return Services.PLATFORM.sidedSuccess(level.isClientSide());
            }

            return InteractionResult.PASS;
        }

        if (heldItem.getItem() instanceof net.minecraft.world.item.BlockItem blockItem && blockItem.getBlock() instanceof TrappedDecoratedPotBlock) {
            return InteractionResult.PASS;
        }

        ItemStack stored = be.getStoredItem();
        boolean shift = player.isShiftKeyDown();

        if (stored.isEmpty()) {
            if (!level.isClientSide()) {
                ItemStack toStore = heldItem.copy();
                int maxCapacity = TrappedDecoratedPotBlockEntity.maxStoredCount(toStore);
                int toTake = shift ? Math.min(heldItem.getCount(), maxCapacity) : 1;
                toStore.setCount(toTake);
                be.setStoredItem(toStore, true);
                heldItem.shrink(toTake);
                if (heldItem.isEmpty()) player.setItemInHand(hand, ItemStack.EMPTY);
                level.playSound(null, pos, ModSounds.DECORATED_POT_INSERT_ITEM.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
                spawnInsertParticles(level, pos);
                be.triggerWobble(DecoratedPotBlockEntity.WobbleStyle.POSITIVE);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            }
            return Services.PLATFORM.sidedSuccess(level.isClientSide());
        } else if (Services.PLATFORM.isSameItemSameComponents(stored, heldItem) && stored.getCount() < TrappedDecoratedPotBlockEntity.maxStoredCount(stored)) {
            if (!level.isClientSide()) {
                int space = TrappedDecoratedPotBlockEntity.maxStoredCount(stored) - stored.getCount();
                int toAdd = shift ? Math.min(heldItem.getCount(), space) : 1;
                stored.grow(toAdd);
                be.setStoredItem(stored, true);
                heldItem.shrink(toAdd);
                if (heldItem.isEmpty()) player.setItemInHand(hand, ItemStack.EMPTY);
                level.playSound(null, pos, ModSounds.DECORATED_POT_INSERT_ITEM.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
                spawnInsertParticles(level, pos);
                be.triggerWobble(DecoratedPotBlockEntity.WobbleStyle.POSITIVE);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            }
            return Services.PLATFORM.sidedSuccess(level.isClientSide());
        }

        if (!level.isClientSide()) {
            level.playSound(null, pos, ModSounds.DECORATED_POT_INSERT_FAIL.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
            be.triggerWobble(DecoratedPotBlockEntity.WobbleStyle.NEGATIVE);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void dropStoredContents(Level level, BlockPos pos) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof TrappedDecoratedPotBlockEntity potEntity && !potEntity.isEmpty()) {
            potEntity.dropContents(level, pos);
        }
    }
    private void spawnInsertParticles(Level level, BlockPos pos) {
        double centerX = pos.getX() + 0.5;
        double topY = pos.getY() + 1.0;
        double centerZ = pos.getZ() + 0.5;

        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 2; i++) {
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, centerX, topY, centerZ, 0, 0.0, 0.1, 0.0, 1.0);
            }
        }
    }
}
