package com.kingodogo.buildscape.client.screen;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Individual pillar edits waiting to be sent, one entry per pillar id: a new edit replaces the older one and
 * restarts that pillar's wait of {@link #DELAY_MS}, so only the latest values go out. Everything pending is
 * sent when the screen closes, and a pillar's entry is sent when the selection moves to another pillar.
 */
final class PillarUploadBatch<T> {
    static final long DELAY_MS = ConfigUploadBatch.DELAY_MS;

    private final Map<String, T> latest = new LinkedHashMap<>();
    private final Map<String, Long> editedAt = new LinkedHashMap<>();

    void edited(String pillarId, T payload, long now) {
        latest.put(pillarId, payload);
        editedAt.put(pillarId, now);
    }

    boolean hasPending() {
        return !latest.isEmpty();
    }

    /** Payloads whose wait ran out, removed from the batch. */
    List<T> takeDue(long now) {
        List<T> due = new ArrayList<>();
        for (String id : new ArrayList<>(latest.keySet())) {
            if (now - editedAt.get(id) >= DELAY_MS) {
                due.add(latest.remove(id));
                editedAt.remove(id);
            }
        }
        return due;
    }

    /** Every pending payload except the one for {@code keepId} (null keeps none), removed from the batch. */
    List<T> takeAllExcept(String keepId) {
        List<T> taken = new ArrayList<>();
        for (String id : new ArrayList<>(latest.keySet())) {
            if (!Objects.equals(id, keepId)) {
                taken.add(latest.remove(id));
                editedAt.remove(id);
            }
        }
        return taken;
    }

    /** Every pending payload, removed from the batch. */
    List<T> takeAll() {
        return takeAllExcept(null);
    }

    /** The pending payload of one pillar, removed from the batch; null when there is none. */
    T take(String pillarId) {
        editedAt.remove(pillarId);
        return latest.remove(pillarId);
    }
}
