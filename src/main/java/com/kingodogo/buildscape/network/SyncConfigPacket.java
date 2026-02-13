package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.PillarParticleConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class SyncConfigPacket {

    public double particle_speed;
    public double particle_spread;
    public int particle_lifetime;
    public int particle_density;
    public boolean use_pattern;
    public String pattern;
    public double pattern_speed;
    public double pattern_spread;
    public double pattern_intensity;
    public List<String> particle_color;
    public int max_particle_color;

    public Set<String> items;

    public SyncConfigPacket() {
    }

    public SyncConfigPacket(PillarParticleConfig config) {
        this.particle_speed = config.particle_speed;
        this.particle_spread = config.particle_spread;
        this.particle_lifetime = config.particle_lifetime;
        this.particle_density = config.particle_density;
        this.use_pattern = config.use_pattern;
        this.pattern = config.pattern != null ? config.pattern : "ring";
        this.pattern_speed = config.pattern_speed;
        this.pattern_spread = config.pattern_spread;
        this.pattern_intensity = config.pattern_intensity;
        this.particle_color = new ArrayList<>(
                config.particle_color != null ? config.particle_color : new ArrayList<>()
        );
        this.max_particle_color = config.max_particle_color;
        this.items = new HashSet<>(
                config.items != null ? config.items : new HashSet<>()
        );
    }

    public SyncConfigPacket(FriendlyByteBuf buf) {
        this.particle_speed = buf.readDouble();
        this.particle_spread = buf.readDouble();
        this.particle_lifetime = buf.readInt();
        this.particle_density = buf.readInt();
        this.use_pattern = buf.readBoolean();
        this.pattern = buf.readUtf();
        this.pattern_speed = buf.readDouble();
        this.pattern_spread = buf.readDouble();
        this.pattern_intensity = buf.readDouble();

        int colorCount = buf.readInt();
        this.particle_color = new ArrayList<>();
        for (int i = 0; i < colorCount; i++) {
            this.particle_color.add(buf.readUtf());
        }
        this.max_particle_color = buf.readInt();

        int itemCount = buf.readInt();
        this.items = new HashSet<>();
        for (int i = 0; i < itemCount; i++) {
            this.items.add(buf.readUtf());
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(particle_speed);
        buf.writeDouble(particle_spread);
        buf.writeInt(particle_lifetime);
        buf.writeInt(particle_density);
        buf.writeBoolean(use_pattern);
        buf.writeUtf(pattern != null ? pattern : "ring");
        buf.writeDouble(pattern_speed);
        buf.writeDouble(pattern_spread);
        buf.writeDouble(pattern_intensity);

        buf.writeInt(particle_color != null ? particle_color.size() : 0);
        if (particle_color != null) {
            for (String color : particle_color) {
                buf.writeUtf(color);
            }
        }
        buf.writeInt(max_particle_color);

        buf.writeInt(items != null ? items.size() : 0);
        if (items != null) {
            for (String item : items) {
                buf.writeUtf(item);
            }
        }
    }

    public static SyncConfigPacket decode(FriendlyByteBuf buf) {
        return new SyncConfigPacket(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx
                .get()
                .enqueueWork(() -> {
                    DistExecutor.unsafeRunWhenOn(
                            Dist.CLIENT,
                            () ->
                                    () -> {
                                        PillarParticleConfig.setServerConfig(this);
                                    }
                    );
                });
        ctx.get().setPacketHandled(true);
    }
}
