package com.kingodogo.buildscape.feature.pillar;

/** The resolved particle settings of one pillar tick: its own values where set, else the config. */
public record PillarParticleSettings(String pattern, boolean usePattern, double speed, double spread, double intensity) {
}
