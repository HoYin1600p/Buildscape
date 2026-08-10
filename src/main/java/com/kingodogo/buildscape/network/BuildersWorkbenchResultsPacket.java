package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.block.BuildersWorkbenchBlockEntity;
import com.kingodogo.buildscape.util.ColorGradientSolver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Applies nine client-computed ghost results. No items are created: the server
 * validates every registry id and the workbench result slots remain read-only.
 */
public final class BuildersWorkbenchResultsPacket {
    private static final int RESULT_COUNT = 9;

    private final BlockPos pos;
    private final int tab;
    private final int filterMask;
    private final int[] offsets;
    private final List<ResourceLocation> results;

    public BuildersWorkbenchResultsPacket(BlockPos pos, int tab, int filterMask, int[] offsets,
                                          List<ItemStack> results) {
        this.pos = pos;
        this.tab = tab;
        this.filterMask = filterMask;
        this.offsets = normalizedOffsets(offsets);
        this.results = new ArrayList<>(RESULT_COUNT);
        for (int i = 0; i < RESULT_COUNT; i++) {
            ItemStack stack = results != null && i < results.size() ? results.get(i) : ItemStack.EMPTY;
            this.results.add(stack == null || stack.isEmpty() ? null : Registry.ITEM.getKey(stack.getItem()));
        }
    }

    private BuildersWorkbenchResultsPacket(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.tab = buffer.readByte();
        this.filterMask = buffer.readUnsignedByte();
        this.offsets = new int[RESULT_COUNT];
        for (int i = 0; i < RESULT_COUNT; i++) offsets[i] = buffer.readVarInt();
        this.results = new ArrayList<>(RESULT_COUNT);
        for (int i = 0; i < RESULT_COUNT; i++) {
            results.add(buffer.readBoolean() ? buffer.readResourceLocation() : null);
        }
    }

    public static BuildersWorkbenchResultsPacket decode(FriendlyByteBuf buffer) {
        return new BuildersWorkbenchResultsPacket(buffer);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeByte(tab);
        buffer.writeByte(filterMask);
        for (int offset : offsets) buffer.writeVarInt(Math.max(0, offset));
        for (ResourceLocation result : results) {
            buffer.writeBoolean(result != null);
            if (result != null) buffer.writeResourceLocation(result);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || player.level == null) return;
            if (!(player.level.getBlockEntity(pos) instanceof BuildersWorkbenchBlockEntity workbench)
                    || !(player.containerMenu instanceof BuildersWorkbenchMenu menu)
                    || menu.getBlockEntity() != workbench
                    || !workbench.stillValid(player)
                    || tab < 0 || tab > 1
                    || tab != workbench.getActiveTab()
                    || filterMask < 0 || (filterMask & ~ColorGradientSolver.FILTER_STATE_MASK) != 0) {
                return;
            }

            List<ItemStack> validated = new ArrayList<>(RESULT_COUNT);
            for (ResourceLocation id : results) {
                if (id == null) {
                    validated.add(ItemStack.EMPTY);
                    continue;
                }
                Item item = Registry.ITEM.getOptional(id).orElse(null);
                if (!(item instanceof BlockItem) || !ColorGradientSolver.isCandidateBlock(item)) return;
                validated.add(new ItemStack(item));
            }
            if (!workbench.resultsMatchInputs(tab, validated)) return;
            workbench.applyClientResults(tab, filterMask, offsets, validated);
        });
        context.setPacketHandled(true);
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
