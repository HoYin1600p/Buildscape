package com.kingodogo.buildscape.particle;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, BuildScape.MODID);

    public static final RegistryObject<SimpleParticleType> GLOW_LIME_SPARKLE =
            PARTICLES.register("glow_lime_sparkle", () -> new SimpleParticleType(false)
            );
    public static final RegistryObject<SimpleParticleType> TINTED_DRIP_FALL =
            PARTICLES.register("tinted_drip_fall", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> TINTED_SPORE =
            PARTICLES.register("tinted_spore", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> SNOWFLAKE =
            PARTICLES.register("snowflake", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> SNOWFLAKE_STILL =
            PARTICLES.register("snowflake_still", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> CONFETTI =
            PARTICLES.register("confetti", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> TINTABLE_HEART =
            PARTICLES.register("tintable_heart", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> CAKE =
            PARTICLES.register("cake", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> CHERRY =
            PARTICLES.register("cherry", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> BUBBLE =
            PARTICLES.register("bubble", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> TRAIL_NOTE =
            PARTICLES.register("trail_note", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> COLORED_SMOKE =
            PARTICLES.register("colored_smoke", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> CASCADE =
            PARTICLES.register("cascade", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> NOXIOUS_GAS =
            PARTICLES.register("noxious_gas", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> COPPER_FIRE_FLAME =
            PARTICLES.register("copper_fire_flame", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> FIREFLY =
            PARTICLES.register("firefly", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> SULFUR_BUBBLES =
            PARTICLES.register("sulfur_bubbles", () -> new SimpleParticleType(false));

    public static final RegistryObject<ParticleType<GeyserParticleOptions>> GEYSER =
            PARTICLES.register("geyser", () -> new net.minecraft.core.particles.ParticleType<GeyserParticleOptions>(false, GeyserParticleOptions.DESERIALIZER) {
                @Override
                public com.mojang.serialization.Codec<GeyserParticleOptions> codec() {
                    return GeyserParticleOptions.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<GeyserBaseParticleOptions>> GEYSER_BASE =
            PARTICLES.register("geyser_base", () -> new net.minecraft.core.particles.ParticleType<GeyserBaseParticleOptions>(false, GeyserBaseParticleOptions.DESERIALIZER) {
                @Override
                public com.mojang.serialization.Codec<GeyserBaseParticleOptions> codec() {
                    return GeyserBaseParticleOptions.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<GeyserBaseParticleOptions>> GEYSER_POOF =
            PARTICLES.register("geyser_poof", () -> new net.minecraft.core.particles.ParticleType<GeyserBaseParticleOptions>(false, GeyserBaseParticleOptions.DESERIALIZER) {
                @Override
                public com.mojang.serialization.Codec<GeyserBaseParticleOptions> codec() {
                    return GeyserBaseParticleOptions.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<GeyserParticleOptions>> GEYSER_PLUME =
            PARTICLES.register("geyser_plume", () -> new net.minecraft.core.particles.ParticleType<GeyserParticleOptions>(false, GeyserParticleOptions.DESERIALIZER) {
                @Override
                public com.mojang.serialization.Codec<GeyserParticleOptions> codec() {
                    return GeyserParticleOptions.CODEC;
                }
            });
}
