package com.kingodogo.buildscape.feature.homemaker;

/**
 * Client copy of the local player's homemaker cooldown end time (real-time milliseconds), sent by the server in
 * {@link SyncHomemakerCooldownPacket} on every login, dimension change and homemaker spawn. Zero means no cooldown.
 */
public class HomemakerCooldownTracker {
    private static long cooldownEndTime = 0;

    /** The real-time millisecond at which the cooldown ends; zero when there is none. */
    public static long getCooldownEndTime() {
        return cooldownEndTime;
    }

    /** Stores the cooldown end time last sent by the server. */
    public static void setCooldownEndTime(long endTime) {
        cooldownEndTime = endTime;
    }
}
