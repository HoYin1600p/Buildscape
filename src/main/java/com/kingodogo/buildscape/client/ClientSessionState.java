package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.feature.pillar.PillarIdManager;

/**
 * The per-session state the client keeps for the server it is connected to: the synced copy of the pillar registry
 * and the local player's homemaker cooldown end time. The class only loads when a client path asks for this state, so
 * a dedicated server never loads it.
 * <p>
 * The pillar copy is emptied on logout ({@link PillarIdManager#resetClientCache()}, from {@code ClientEvents.onClientDisconnect}). The
 * cooldown is deliberately not reset there: the server overwrites it with a packet on every login.
 */
public final class ClientSessionState {

    private static final ClientSessionState INSTANCE = new ClientSessionState();

    private final PillarIdManager pillars = PillarIdManager.newClientInstance();
    private volatile long homemakerCooldownEndTime = 0L;

    private ClientSessionState() {}

    /** The single client session state. */
    public static ClientSessionState get() {
        return INSTANCE;
    }

    /** The client copy of the pillar registry, filled by {@code SyncPillarIdsPacket}. */
    public PillarIdManager pillars() {
        return pillars;
    }

    /** The real-time millisecond at which the homemaker cooldown ends; zero when there is none. */
    public long homemakerCooldownEndTime() {
        return homemakerCooldownEndTime;
    }

    /** Stores the cooldown end time last sent by the server. */
    public void setHomemakerCooldownEndTime(long endTime) {
        homemakerCooldownEndTime = endTime;
    }
}
