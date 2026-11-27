package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.block.BuildersWorkbenchBlockEntity;
import com.kingodogo.buildscape.menu.BuildersWorkbenchMenu;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ColorGradientSolver;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class BuildersWorkbenchResultsPacket implements CommonPacket {

    public static final CommonId ID = new CommonId("buildscape", "builders_workbench_results");
    private static final int RESULT_COUNT = 9;

    private final BlockPos pos;
    private final int tab;
    private final int filterMask;
    private final int[] offsets;
    private final List<CommonId> results;

    public BuildersWorkbenchResultsPacket(BlockPos pos, int tab, int filterMask, int[] offsets,
                                          List<ItemStack> results) {
        this.pos = pos;
        this.tab = tab;
        this.filterMask = filterMask;
        this.offsets = normalizedOffsets(offsets);
        this.results = new ArrayList<>(RESULT_COUNT);
        for (int i = 0; i < RESULT_COUNT; i++) {
            ItemStack stack = results != null && i < results.size() ? results.get(i) : ItemStack.EMPTY;
            this.results.add(stack == null || stack.isEmpty() ? null : Services.PLATFORM.getItemId(stack.getItem()));
        }
    }

    private BuildersWorkbenchResultsPacket(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.tab = buffer.readByte();
        this.filterMask = buffer.readUnsignedByte();
        this.offsets = new int[RESULT_COUNT];
        for (int i = 0; i < RESULT_COUNT; i++) offsets[i] = NetworkPacketLimits.readResultOffset(buffer);
        this.results = new ArrayList<>(RESULT_COUNT);
        for (int i = 0; i < RESULT_COUNT; i++) {
            results.add(buffer.readBoolean()
                    ? CommonId.parse(NetworkPacketLimits.readUtf(buffer,
                    NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "workbench result")) : null);
        }
    }

    public static BuildersWorkbenchResultsPacket decode(FriendlyByteBuf buffer) {
        return new BuildersWorkbenchResultsPacket(buffer);
    }

    @Override
    public CommonId getId() {
        return ID;
    }

    @Override
    public PacketDirection getDirection() {
        return PacketDirection.CLIENT_TO_SERVER;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeByte(tab);
        buffer.writeByte(filterMask);
        for (int offset : offsets) buffer.writeVarInt(Math.max(0, offset));
        for (CommonId result : results) {
            buffer.writeBoolean(result != null);
            if (result != null) {
                NetworkPacketLimits.writeUtf(buffer, result.toString(),
                        NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "workbench result");
            }
        }
    }

    @Override
    public void handle(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) || Services.PLATFORM.getEntityLevel(serverPlayer) == null) return;
        if (!(serverPlayer.containerMenu instanceof BuildersWorkbenchMenu menu)) return;

        BuildersWorkbenchBlockEntity workbench = menu.getBlockEntity();
        if (workbench == null
                || !workbench.getBlockPos().equals(pos)
                || !workbench.stillValid(serverPlayer)
                || tab < 0 || tab > 1
                || tab != workbench.getActiveTab()
                || filterMask < 0 || (filterMask & ~ColorGradientSolver.FILTER_STATE_MASK) != 0) {
            return;
        }

        List<ItemStack> validated = new ArrayList<>(RESULT_COUNT);
        for (CommonId id : results) {
            if (id == null) {
                validated.add(ItemStack.EMPTY);
                continue;
            }
            Item item = Services.PLATFORM.getItem(id);
            if (!(item instanceof BlockItem) || !ColorGradientSolver.isCandidateBlock(item)) return;
            validated.add(new ItemStack(item));
        }
        if (!workbench.resultsMatchInputs(tab, validated)) return;
        workbench.applyClientResults(tab, filterMask, offsets, validated);
    }

    private static int[] normalizedOffsets(int[] input) {
        int[] result = new int[RESULT_COUNT];
        if (input != null) {
            for (int i = 0; i < Math.min(input.length, RESULT_COUNT); i++) {
                result[i] = Math.max(0, input[i]);
            }
        }
        return result;
    }
}
