package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BuildScape.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        INSTANCE.registerMessage(
                id++,
                CyclePillarPatternPacket.class,
                CyclePillarPatternPacket::encode,
                CyclePillarPatternPacket::decode,
                CyclePillarPatternPacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                ActionBarMessagePacket.class,
                ActionBarMessagePacket::encode,
                ActionBarMessagePacket::decode,
                ActionBarMessagePacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                SyncConfigPacket.class,
                SyncConfigPacket::encode,
                SyncConfigPacket::decode,
                SyncConfigPacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                TreeChopPacket.class,
                TreeChopPacket::encode,
                TreeChopPacket::decode,
                TreeChopPacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                UpdatePillarDataPacket.class,
                UpdatePillarDataPacket::encode,
                UpdatePillarDataPacket::decode,
                UpdatePillarDataPacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                SyncPillarIdsPacket.class,
                SyncPillarIdsPacket::encode,
                SyncPillarIdsPacket::decode,
                SyncPillarIdsPacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                RequestPillarIdsPacket.class,
                RequestPillarIdsPacket::encode,
                RequestPillarIdsPacket::decode,
                RequestPillarIdsPacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                UpdateConfigPacket.class,
                UpdateConfigPacket::encode,
                UpdateConfigPacket::decode,
                UpdateConfigPacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                RemovePillarPacket.class,
                RemovePillarPacket::encode,
                RemovePillarPacket::decode,
                RemovePillarPacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                UpdateAllPillarIdsPacket.class,
                UpdateAllPillarIdsPacket::encode,
                UpdateAllPillarIdsPacket::decode,
                UpdateAllPillarIdsPacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                SyncGameRulesPacket.class,
                SyncGameRulesPacket::encode,
                SyncGameRulesPacket::decode,
                SyncGameRulesPacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                UpdateGameRulePacket.class,
                UpdateGameRulePacket::encode,
                UpdateGameRulePacket::decode,
                UpdateGameRulePacket::handle
        );
    }
}
