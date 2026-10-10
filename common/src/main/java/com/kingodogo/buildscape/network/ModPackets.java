package com.kingodogo.buildscape.network;

public final class ModPackets {

    private ModPackets() {}

    public static void registerAll() {
        PacketFactory.register(ActionBarMessagePacket.ID, PacketDirection.SERVER_TO_CLIENT, ActionBarMessagePacket::decode);
        PacketFactory.register(SyncConfigPacket.ID, PacketDirection.SERVER_TO_CLIENT, SyncConfigPacket::decode);
        PacketFactory.register(TreeChopPacket.ID, PacketDirection.CLIENT_TO_SERVER, TreeChopPacket::decode);
        PacketFactory.register(UpdatePillarDataPacket.ID, PacketDirection.CLIENT_TO_SERVER, UpdatePillarDataPacket::decode);
        PacketFactory.register(SyncPillarIdsPacket.ID, PacketDirection.SERVER_TO_CLIENT, SyncPillarIdsPacket::decode);
        PacketFactory.register(RequestPillarIdsPacket.ID, PacketDirection.CLIENT_TO_SERVER, RequestPillarIdsPacket::decode);
        PacketFactory.register(UpdateConfigPacket.ID, PacketDirection.CLIENT_TO_SERVER, UpdateConfigPacket::decode);
        PacketFactory.register(RemovePillarPacket.ID, PacketDirection.CLIENT_TO_SERVER, RemovePillarPacket::decode);
        PacketFactory.register(UpdateAllPillarIdsPacket.ID, PacketDirection.CLIENT_TO_SERVER, UpdateAllPillarIdsPacket::decode);
        PacketFactory.register(SyncGameRulesPacket.ID, PacketDirection.SERVER_TO_CLIENT, SyncGameRulesPacket::decode);
        PacketFactory.register(UpdateGameRulePacket.ID, PacketDirection.CLIENT_TO_SERVER, UpdateGameRulePacket::decode);
        PacketFactory.register(SyncHomemakerCooldownPacket.ID, PacketDirection.SERVER_TO_CLIENT, SyncHomemakerCooldownPacket::decode);
        PacketFactory.register(RotateBlockPacket.ID, PacketDirection.CLIENT_TO_SERVER, RotateBlockPacket::decode);
        PacketFactory.register(HammerReplacePacket.ID, PacketDirection.CLIENT_TO_SERVER, HammerReplacePacket::decode);
        PacketFactory.register(BuildersWorkbenchResultsPacket.ID, PacketDirection.CLIENT_TO_SERVER, BuildersWorkbenchResultsPacket::decode);
        PacketFactory.register(ClearBiomeBrushPacket.ID, PacketDirection.CLIENT_TO_SERVER, ClearBiomeBrushPacket::decode);
        PacketFactory.register(SyncSignFramePacket.ID, PacketDirection.SERVER_TO_CLIENT, SyncSignFramePacket::decode);
        PacketFactory.register(ConfettiBurstPacket.ID, PacketDirection.SERVER_TO_CLIENT, ConfettiBurstPacket::decode);
    }
}
