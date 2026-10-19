package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.advancement.AdvancementEvents;
import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.item.HammerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class HammerReplacePacket {

    private final BlockPos pos;

    public HammerReplacePacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(HammerReplacePacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static HammerReplacePacket decode(FriendlyByteBuf buf) {
        return new HammerReplacePacket(buf.readBlockPos());
    }

    public static void handle(HammerReplacePacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            ServerLevel level = player.getLevel();
            BlockPos pos = msg.pos;

            if (!level.isLoaded(pos)) return;
            if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 64.0) return;
            if (!player.mayBuild() || player.blockActionRestricted(level, pos, player.gameMode.getGameModeForPlayer())) return;
            if (!level.mayInteract(player, pos)) return;

            ItemStack hammerStack = player.getMainHandItem();
            if (hammerStack.isEmpty() || !(hammerStack.getItem() instanceof HammerItem hammer)) return;

            ItemStack offhandStack = player.getOffhandItem();
            if (offhandStack.isEmpty() || !(offhandStack.getItem() instanceof BlockItem blockItem)) return;

            BlockState targetState = level.getBlockState(pos);
            if (targetState.isAir()) return;

            float destroyTime = targetState.getDestroySpeed(level, pos);
            if (destroyTime < 0) return;

            HammerItem.HammerTier tier = hammer.getHammerTier();
            if (!tier.canReplaceObsidianLevel() && destroyTime >= 50.0f) return;
            ItemStack harvestTool = createHarvestTool(tier, hammerStack);
            if (targetState.requiresCorrectToolForDrops() && !harvestTool.isCorrectToolForDrops(targetState)) return;

            Block replacementBlock = blockItem.getBlock();
            if (targetState.getBlock() == replacementBlock) return;
            if (isMultiBlockPart(targetState)) return;
            if (offhandStack.getTagElement(NbtKeys.BLOCK_ENTITY_TAG) != null) return;

            BlockState newState = replacementBlock.defaultBlockState();
            if (isMultiBlockPart(newState) || !newState.canSurvive(level, pos)) return;

            int experience = ForgeHooks.onBlockBreakEvent(level, player.gameMode.getGameModeForPlayer(), player, pos);
            if (experience < 0) return;
            BlockSnapshot before = BlockSnapshot.create(level.dimension(), level, pos);
            BlockEvent.EntityPlaceEvent placement = new BlockEvent.EntityPlaceEvent(
                    before, level.getBlockState(pos.below()), player) {
                @Override
                public BlockState getState() {
                    return newState;
                }

                @Override
                public BlockState getPlacedBlock() {
                    return newState;
                }
            };
            if (MinecraftForge.EVENT_BUS.post(placement)) {
                player.connection.send(new ClientboundBlockUpdatePacket(level, pos));
                return;
            }
            if (level.getBlockState(pos) != targetState) return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            LootContext.Builder builder = new LootContext.Builder(level)
                    .withRandom(level.random)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                    .withParameter(LootContextParams.TOOL, harvestTool)
                    .withParameter(LootContextParams.THIS_ENTITY, player)
                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
            List<ItemStack> drops = targetState.getDrops(builder);
            if (!level.setBlock(pos, newState, 3)) return;
            AdvancementEvents.onHammerReplace(player);

            if (!player.isCreative()) {
                offhandStack.shrink(1);
            }

            for (ItemStack drop : drops) {
                Block.popResource(level, pos, drop);
            }
            if (!player.isCreative() && experience > 0
                    && level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
                ExperienceOrb.award(level, Vec3.atCenterOf(pos), experience);
            }

            if (!player.isCreative()) {
                hammerStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            }

            level.playSound(null, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 0.5f, 1.2f);
            level.sendParticles(
                    ParticleTypes.CRIT,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    8, 0.3, 0.3, 0.3, 0.05
            );
        });
        ctx.setPacketHandled(true);
    }

    private static boolean isMultiBlockPart(BlockState state) {
        return state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)
                || state.hasProperty(BlockStateProperties.BED_PART)
                || state.getBlock() instanceof PistonHeadBlock
                || (state.hasProperty(BlockStateProperties.EXTENDED) && state.getValue(BlockStateProperties.EXTENDED));
    }

    static ItemStack createHarvestTool(HammerItem.HammerTier tier, ItemStack hammerStack) {
        ItemStack tool = new ItemStack(tier.getHarvestToolItem());
        EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(hammerStack), tool);
        return tool;
    }
}
