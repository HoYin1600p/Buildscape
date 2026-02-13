package com.kingodogo.buildscape.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kingodogo.buildscape.config.PillarIdManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Syncs pillar IDs data from server to client so the GUI can display it.
 */
public class SyncPillarIdsPacket {

    private static final Gson GSON = new GsonBuilder().create();
    private final List<PillarIdManager.PillarData> pillarDataList;

    public SyncPillarIdsPacket(List<PillarIdManager.PillarData> pillarDataList) {
        this.pillarDataList = new ArrayList<>(pillarDataList);
    }

    public SyncPillarIdsPacket(FriendlyByteBuf buf) {
        int count = buf.readInt();
        this.pillarDataList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String json = buf.readUtf(32767); // Max string length
            PillarIdManager.PillarData data = GSON.fromJson(json, PillarIdManager.PillarData.class);
            this.pillarDataList.add(data);
        }
    }

    public static SyncPillarIdsPacket decode(FriendlyByteBuf buf) {
        return new SyncPillarIdsPacket(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(pillarDataList.size());

        for (PillarIdManager.PillarData data : pillarDataList) {
            String json = GSON.toJson(data);
            buf.writeUtf(json, 32767);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(
                    Dist.CLIENT,
                    () -> () -> {
                        PillarIdManager manager = PillarIdManager.get();

                        // IMPORTANT: Clear existing data and prepare for server sync
                        // This resets the isServerSynced flag so new data can be loaded
                        manager.clearForServerSync();

                        // Add all pillar data from server
                        for (PillarIdManager.PillarData data : pillarDataList) {
                            manager.addPillarDataFromSync(data);
                        }

                        // Mark as loaded so GUI can display
                        // This also sets isServerSynced=true to indicate we have fresh server data
                        manager.markAsLoaded();

                        // Refresh GUI if it's open
                        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                        if (mc.screen instanceof com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen configScreen) {
                            configScreen.refreshCurrentTab();
                        }
                    }
            );
        });
        ctx.get().setPacketHandled(true);
    }
}
