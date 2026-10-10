package com.kingodogo.buildscape.particle;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.registry.RegistrySupplier;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleType;
public class ModParticles {
    public static final RegistrySupplier<SimpleParticleType> GLOW_LIME_SPARKLE = registerSimple("glow_lime_sparkle");
    public static final RegistrySupplier<SimpleParticleType> TINTED_DRIP_FALL = registerSimple("tinted_drip_fall");
    public static final RegistrySupplier<SimpleParticleType> TINTED_SPORE = registerSimple("tinted_spore");
    public static final RegistrySupplier<SimpleParticleType> SNOWFLAKE = registerSimple("snowflake");
    public static final RegistrySupplier<SimpleParticleType> SNOWFLAKE_STILL = registerSimple("snowflake_still");
    public static final RegistrySupplier<SimpleParticleType> CONFETTI = registerSimple("confetti");
    public static final RegistrySupplier<SimpleParticleType> TINTABLE_HEART = registerSimple("tintable_heart");
    public static final RegistrySupplier<SimpleParticleType> CAKE = registerSimple("cake");
    public static final RegistrySupplier<SimpleParticleType> CHERRY = registerSimple("cherry");
    public static final RegistrySupplier<SimpleParticleType> BUBBLE = registerSimple("bubble");
    public static final RegistrySupplier<SimpleParticleType> TRAIL_NOTE = registerSimple("trail_note");
    public static final RegistrySupplier<SimpleParticleType> COLORED_SMOKE = registerSimple("colored_smoke");
    public static final RegistrySupplier<SimpleParticleType> CASCADE = registerSimple("cascade");
    public static final RegistrySupplier<SimpleParticleType> NOXIOUS_GAS = registerSimple("noxious_gas");
    public static final RegistrySupplier<SimpleParticleType> COPPER_FIRE_FLAME = registerSimple("copper_fire_flame");
    public static final RegistrySupplier<SimpleParticleType> FIREFLY = registerSimple("firefly");
    public static final RegistrySupplier<SimpleParticleType> SULFUR_BUBBLES = registerSimple("sulfur_bubbles");
    public static final RegistrySupplier<ParticleType<GeyserParticleOptions>> GEYSER =
            Services.REGISTRY.registerParticle("geyser", GeyserParticleTypes::geyser);
    public static final RegistrySupplier<ParticleType<GeyserBaseParticleOptions>> GEYSER_BASE =
            Services.REGISTRY.registerParticle("geyser_base", GeyserParticleTypes::geyserBase);
    public static final RegistrySupplier<ParticleType<GeyserBaseParticleOptions>> GEYSER_POOF =
            Services.REGISTRY.registerParticle("geyser_poof", GeyserParticleTypes::geyserBase);
    public static final RegistrySupplier<ParticleType<GeyserParticleOptions>> GEYSER_PLUME =
            Services.REGISTRY.registerParticle("geyser_plume", GeyserParticleTypes::geyser);
    public static final RegistrySupplier<SimpleParticleType> XP_PARTICLE = registerSimple("xp_particle");
    private static RegistrySupplier<SimpleParticleType> registerSimple(String name) {
        return Services.REGISTRY.registerParticle(name, () -> new SimpleParticleType(false) {});
    }
    public static void init() {
    }
}
