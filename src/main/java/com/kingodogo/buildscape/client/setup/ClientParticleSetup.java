package com.kingodogo.buildscape.client.setup;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.client.particle.GeyserBaseParticle;
import com.kingodogo.buildscape.client.particle.GeyserEruptionParticle;
import com.kingodogo.buildscape.client.particle.GeyserPlumeParticle;
import com.kingodogo.buildscape.client.particle.SulfurBubbleParticle;
import com.kingodogo.buildscape.client.particle.BubbleParticle;
import com.kingodogo.buildscape.client.particle.CakeParticle;
import com.kingodogo.buildscape.client.particle.CascadeParticle;
import com.kingodogo.buildscape.client.particle.CherryParticle;
import com.kingodogo.buildscape.client.particle.ColoredSmokeParticle;
import com.kingodogo.buildscape.client.particle.ConfettiParticle;
import com.kingodogo.buildscape.client.particle.FireflyParticle;
import com.kingodogo.buildscape.client.particle.HazeParticle;
import com.kingodogo.buildscape.client.particle.NoxiousGasParticle;
import com.kingodogo.buildscape.client.particle.PillarSparkleParticle;
import com.kingodogo.buildscape.client.particle.SnowflakeParticle;
import com.kingodogo.buildscape.client.particle.SnowflakeStillParticle;
import com.kingodogo.buildscape.client.particle.TintableHeartParticle;
import com.kingodogo.buildscape.client.particle.TintedDripParticle;
import com.kingodogo.buildscape.client.particle.TintedSporeParticle;
import com.kingodogo.buildscape.client.particle.TrailNoteParticle;
import com.kingodogo.buildscape.client.particle.XpParticle;
import com.kingodogo.buildscape.registry.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientParticleSetup {

    @SubscribeEvent
    public static void registerFactories(
            ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particleEngine.register(
                ModParticles.GLOW_LIME_SPARKLE.get(),
                sprites -> new PillarSparkleParticle.Provider(
                        sprites));

        Minecraft.getInstance().particleEngine.register(
                ModParticles.TINTED_DRIP_FALL.get(),
                sprites -> new TintedDripParticle.Provider(
                        sprites));

        Minecraft.getInstance().particleEngine.register(
                ModParticles.TINTED_SPORE.get(),
                sprites -> new TintedSporeParticle.Provider(
                        sprites));

        Minecraft.getInstance().particleEngine.register(
                ModParticles.SNOWFLAKE.get(),
                sprites -> new SnowflakeParticle.Provider(
                        sprites));

        Minecraft.getInstance().particleEngine.register(
                ModParticles.SNOWFLAKE_STILL.get(),
                sprites -> new SnowflakeStillParticle.Provider(
                        sprites));

        Minecraft.getInstance().particleEngine.register(
                ModParticles.CONFETTI.get(),
                sprites -> new ConfettiParticle.Provider(
                        sprites));

        Minecraft.getInstance().particleEngine.register(
                ModParticles.BUBBLE.get(),
                sprites -> new BubbleParticle.Provider(
                        sprites));

        Minecraft.getInstance().particleEngine.register(
                ModParticles.TRAIL_NOTE.get(),
                sprites -> new TrailNoteParticle.Provider(
                        sprites));

        Minecraft.getInstance().particleEngine.register(
                ModParticles.COLORED_SMOKE.get(),
                sprites -> new ColoredSmokeParticle.Provider(
                        sprites));

        Minecraft.getInstance().particleEngine.register(
                ModParticles.TINTABLE_HEART.get(),
                sprites -> new TintableHeartParticle.Provider(
                        sprites));

        Minecraft.getInstance().particleEngine.register(
                ModParticles.CAKE.get(),
                sprites -> new CakeParticle.Provider(
                        sprites));

        Minecraft.getInstance().particleEngine.register(
                ModParticles.CHERRY.get(),
                sprites -> new CherryParticle.Provider(
                        sprites));

        Minecraft.getInstance().particleEngine.register(
                ModParticles.CASCADE.get(),
                sprites -> new CascadeParticle.Provider(
                        sprites));

        Minecraft.getInstance().particleEngine.register(
                ModParticles.NOXIOUS_GAS.get(),
                sprites -> new NoxiousGasParticle.Provider(
                        sprites));

        Minecraft.getInstance().particleEngine.register(
                ModParticles.FIREFLY.get(),
                sprites -> new FireflyParticle.Provider(
                        sprites));

        Minecraft.getInstance().particleEngine.register(
                ModParticles.HAZE.get(),
                sprites -> new HazeParticle.Provider(
                        sprites));

        Minecraft.getInstance().particleEngine.register(
                ModParticles.SULFUR_BUBBLES.get(),
                SulfurBubbleParticle.Provider::new);

        Minecraft.getInstance().particleEngine.register(
                ModParticles.GEYSER.get(),
                new GeyserEruptionParticle.Provider());

        Minecraft.getInstance().particleEngine.register(
                ModParticles.GEYSER_BASE.get(),
                GeyserBaseParticle.Provider::new);

        Minecraft.getInstance().particleEngine.register(
                ModParticles.GEYSER_POOF.get(),
                GeyserBaseParticle.Provider::new);

        Minecraft.getInstance().particleEngine.register(
                ModParticles.GEYSER_PLUME.get(),
                GeyserPlumeParticle.Provider::new);

        Minecraft.getInstance().particleEngine.register(
                ModParticles.XP_PARTICLE.get(),
                sprites -> new XpParticle.Provider(sprites));
    }
}
