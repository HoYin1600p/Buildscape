package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {

    private static final String PROTOCOL_VERSION = "3";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BuildScape.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        register(ActionBarMessagePacket.class, 1, NetworkDirection.PLAY_TO_CLIENT,
                ActionBarMessagePacket::encode, ActionBarMessagePacket::decode, ActionBarMessagePacket::handle);
        register(SyncConfigPacket.class, 2, NetworkDirection.PLAY_TO_CLIENT,
                SyncConfigPacket::encode, SyncConfigPacket::decode, SyncConfigPacket::handle);
        register(TreeChopPacket.class, 3, NetworkDirection.PLAY_TO_SERVER,
                TreeChopPacket::encode, TreeChopPacket::decode, TreeChopPacket::handle);
        register(UpdatePillarDataPacket.class, 4, NetworkDirection.PLAY_TO_SERVER,
                UpdatePillarDataPacket::encode, UpdatePillarDataPacket::decode, UpdatePillarDataPacket::handle);
        register(SyncPillarIdsPacket.class, 5, NetworkDirection.PLAY_TO_CLIENT,
                SyncPillarIdsPacket::encode, SyncPillarIdsPacket::decode, SyncPillarIdsPacket::handle);
        register(RequestPillarIdsPacket.class, 6, NetworkDirection.PLAY_TO_SERVER,
                RequestPillarIdsPacket::encode, RequestPillarIdsPacket::decode, RequestPillarIdsPacket::handle);
        register(UpdateConfigPacket.class, 7, NetworkDirection.PLAY_TO_SERVER,
                UpdateConfigPacket::encode, UpdateConfigPacket::decode, UpdateConfigPacket::handle);
        register(RemovePillarPacket.class, 8, NetworkDirection.PLAY_TO_SERVER,
                RemovePillarPacket::encode, RemovePillarPacket::decode, RemovePillarPacket::handle);
        register(UpdateAllPillarIdsPacket.class, 9, NetworkDirection.PLAY_TO_SERVER,
                UpdateAllPillarIdsPacket::encode, UpdateAllPillarIdsPacket::decode, UpdateAllPillarIdsPacket::handle);
        register(SyncGameRulesPacket.class, 10, NetworkDirection.PLAY_TO_CLIENT,
                SyncGameRulesPacket::encode, SyncGameRulesPacket::decode, SyncGameRulesPacket::handle);
        register(UpdateGameRulePacket.class, 11, NetworkDirection.PLAY_TO_SERVER,
                UpdateGameRulePacket::encode, UpdateGameRulePacket::decode, UpdateGameRulePacket::handle);
        register(SyncHomemakerCooldownPacket.class, 12, NetworkDirection.PLAY_TO_CLIENT,
                SyncHomemakerCooldownPacket::encode, SyncHomemakerCooldownPacket::decode, SyncHomemakerCooldownPacket::handle);
        register(RotateBlockPacket.class, 13, NetworkDirection.PLAY_TO_SERVER,
                RotateBlockPacket::encode, RotateBlockPacket::decode, RotateBlockPacket::handle);
        register(HammerReplacePacket.class, 14, NetworkDirection.PLAY_TO_SERVER,
                HammerReplacePacket::encode, HammerReplacePacket::decode, HammerReplacePacket::handle);
        register(BuildersWorkbenchResultsPacket.class, 15, NetworkDirection.PLAY_TO_SERVER,
                BuildersWorkbenchResultsPacket::encode, BuildersWorkbenchResultsPacket::decode, BuildersWorkbenchResultsPacket::handle);
        register(ClearBiomeBrushPacket.class, 16, NetworkDirection.PLAY_TO_SERVER,
                ClearBiomeBrushPacket::encode, ClearBiomeBrushPacket::decode, ClearBiomeBrushPacket::handle);
        register(SyncSignFramePacket.class, 17, NetworkDirection.PLAY_TO_CLIENT,
                SyncSignFramePacket::encode, SyncSignFramePacket::decode, SyncSignFramePacket::handle);
    }

    private static <T> void register(
            Class<T> type,
            int id,
            NetworkDirection direction,
            java.util.function.BiConsumer<T, net.minecraft.network.FriendlyByteBuf> encoder,
            java.util.function.Function<net.minecraft.network.FriendlyByteBuf, T> decoder,
            java.util.function.BiConsumer<T, java.util.function.Supplier<net.minecraftforge.network.NetworkEvent.Context>> consumer
    ) {
        INSTANCE.messageBuilder(type, id, direction)
                .encoder(encoder)
                .decoder(decoder)
                .consumer(consumer)
                .add();
    }
}
