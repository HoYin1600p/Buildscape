package com.kingodogo.buildscape.network;

public final class ModPackets {
    public static final int PROTOCOL_VERSION = 6;
    public static final String PROTOCOL_NAME = Integer.toString(PROTOCOL_VERSION);

    private ModPackets() {}

    public static void registerAll() {
        PacketFactory.register(SyncBiomeTintPacket.ID, PacketDirection.SERVER_TO_CLIENT, SyncBiomeTintPacket::decode);
        PacketFactory.register(ActionBarMessagePacket.ID, PacketDirection.SERVER_TO_CLIENT, ActionBarMessagePacket::decode);
        PacketFactory.register(SyncConfigPacket.ID, PacketDirection.SERVER_TO_CLIENT, SyncConfigPacket::decode);
        PacketFactory.register(SyncConfigChunkPacket.ID, PacketDirection.SERVER_TO_CLIENT, SyncConfigChunkPacket::decode);
        PacketFactory.register(TreeChopPacket.ID, PacketDirection.CLIENT_TO_SERVER, TreeChopPacket::decode);
        PacketFactory.register(UpdatePillarDataPacket.ID, PacketDirection.CLIENT_TO_SERVER, UpdatePillarDataPacket::decode);
        PacketFactory.register(SyncPillarIdsPacket.ID, PacketDirection.SERVER_TO_CLIENT, SyncPillarIdsPacket::decode);
        PacketFactory.register(RequestPillarIdsPacket.ID, PacketDirection.CLIENT_TO_SERVER, RequestPillarIdsPacket::decode);
        PacketFactory.register(UpdateConfigPacket.ID, PacketDirection.CLIENT_TO_SERVER, UpdateConfigPacket::decode);
        PacketFactory.register(RemovePillarPacket.ID, PacketDirection.CLIENT_TO_SERVER, RemovePillarPacket::decode);
        PacketFactory.register(SyncGameRulesPacket.ID, PacketDirection.SERVER_TO_CLIENT, SyncGameRulesPacket::decode);
        PacketFactory.register(UpdateGameRulePacket.ID, PacketDirection.CLIENT_TO_SERVER, UpdateGameRulePacket::decode);
        PacketFactory.register(SyncHomemakerCooldownPacket.ID, PacketDirection.SERVER_TO_CLIENT, SyncHomemakerCooldownPacket::decode);
        PacketFactory.register(RotateBlockPacket.ID, PacketDirection.CLIENT_TO_SERVER, RotateBlockPacket::decode);
        PacketFactory.register(HammerReplacePacket.ID, PacketDirection.CLIENT_TO_SERVER, HammerReplacePacket::decode);
        PacketFactory.register(BuildersWorkbenchResultsPacket.ID, PacketDirection.CLIENT_TO_SERVER, BuildersWorkbenchResultsPacket::decode);
        PacketFactory.register(ClearBiomeBrushPacket.ID, PacketDirection.CLIENT_TO_SERVER, ClearBiomeBrushPacket::decode);
        PacketFactory.register(SyncSignFramePacket.ID, PacketDirection.SERVER_TO_CLIENT, SyncSignFramePacket::decode);
        PacketFactory.register(ConfettiBurstPacket.ID, PacketDirection.SERVER_TO_CLIENT, ConfettiBurstPacket::decode);
        PacketFactory.register(ResetPillarsPacket.ID, PacketDirection.CLIENT_TO_SERVER, ResetPillarsPacket::decode);
        PacketFactory.register(EditRefusedPacket.ID, PacketDirection.SERVER_TO_CLIENT, EditRefusedPacket::decode);
    }
}
