package com.kingodogo.buildscape.client;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/**
 * Client-side manager to track marked pillars that should display blinking borders.
 * Marks expire after 15 seconds.
 */
public class PillarMarkerManager {
    private static final PillarMarkerManager INSTANCE = new PillarMarkerManager();
    private static final long MARK_DURATION_MS = 4500; // 4.5 seconds
    
    private final Map<String, MarkedPillar> markedPillars = new HashMap<>();
    
    public static PillarMarkerManager get() {
        return INSTANCE;
    }
    
    public static class MarkedPillar {
        public final String pillarId;
        public final BlockPos pos;
        public final String dimension;
        public final long markTime;
        
        public MarkedPillar(String pillarId, BlockPos pos, String dimension) {
            this.pillarId = pillarId;
            this.pos = pos;
            this.dimension = dimension;
            this.markTime = System.currentTimeMillis();
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() - markTime > MARK_DURATION_MS;
        }
        
        public float getBlinkAlpha() {
            long elapsed = System.currentTimeMillis() - markTime;
            // Blink every 500ms (on/off cycle)
            boolean isOn = (elapsed / 500) % 2 == 0;
            return isOn ? 1.0f : 0.3f;
        }
        
        /**
         * Get the color for the gradient (yellow to red)
         * Returns a value from 0.0 (yellow) to 1.0 (red) based on elapsed time
         */
        public float getGradientProgress() {
            long elapsed = System.currentTimeMillis() - markTime;
            // Gradually transition from yellow (0.0) to red (1.0) over the duration
            return Math.min(1.0f, elapsed / (float)MARK_DURATION_MS);
        }
    }
    
    /**
     * Mark a pillar to display blinking borders.
     */
    public void markPillar(String pillarId, BlockPos pos, String dimension) {
        markedPillars.put(pillarId, new MarkedPillar(pillarId, pos, dimension));
    }
    
    /**
     * Check if a pillar at the given position is marked.
     */
    public MarkedPillar getMarkedPillar(Level level, BlockPos pos) {
        String dimension = level.dimension().location().toString();
        
        // Clean up expired marks first
        cleanupExpired();
        
        for (MarkedPillar marked : markedPillars.values()) {
            if (marked.dimension.equals(dimension) &&
                marked.pos.getX() == pos.getX() &&
                marked.pos.getY() == pos.getY() &&
                marked.pos.getZ() == pos.getZ()) {
                if (!marked.isExpired()) {
                    return marked;
                }
            }
        }
        return null;
    }
    
    /**
     * Get all marked pillars in the given dimension.
     */
    public Map<String, MarkedPillar> getMarkedPillars(String dimension) {
        cleanupExpired();
        Map<String, MarkedPillar> result = new HashMap<>();
        for (Map.Entry<String, MarkedPillar> entry : markedPillars.entrySet()) {
            if (entry.getValue().dimension.equals(dimension) && !entry.getValue().isExpired()) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
    
    /**
     * Remove expired marks.
     */
    private void cleanupExpired() {
        Iterator<Map.Entry<String, MarkedPillar>> it = markedPillars.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().isExpired()) {
                it.remove();
            }
        }
    }
    
    /**
     * Clear all marks (useful for testing or cleanup).
     */
    public void clearAll() {
        markedPillars.clear();
    }
}

