package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.client.network.ClientPacketHandlers;
import com.kingodogo.buildscape.config.ConfigSetting;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.core.ParticlePatterns;
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
    // Present only for assembled uploads; server-to-client sync always contains all settings.
    public Set<ConfigSetting> uploadSettings =
            ConfigSetting.uploadable();
    public static final double MIN_PARTICLE_RATE = NetworkPacketLimits.MIN_PARTICLE_RATE;
    public static final double MAX_PARTICLE_SPEED = NetworkPacketLimits.MAX_PARTICLE_SPEED;
    public static final double MAX_PARTICLE_SPREAD = NetworkPacketLimits.MAX_PARTICLE_SPREAD;
    public static final double MAX_PARTICLE_INTENSITY = NetworkPacketLimits.MAX_PARTICLE_INTENSITY;
    public static final int MAX_PARTICLE_LIFETIME = NetworkPacketLimits.MAX_PARTICLE_LIFETIME;
    public static final int MAX_PARTICLE_DENSITY = NetworkPacketLimits.MAX_PARTICLE_DENSITY;

    public static double clampParticleValue(double value, double maximum) {
        return NetworkPacketLimits.clamp(value, MIN_PARTICLE_RATE, maximum);
    }

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
    public PillarParticleConfig.EditPermissionMode edit_permission_mode = PillarParticleConfig.EditPermissionMode.STRICT;

    public Set<String> items;
    public boolean itemsReadOnly;
    public boolean propertiesReadOnly;

    public SyncConfigPacket() {
    }

    public SyncConfigPacket(PillarParticleConfig config) {
        this.particle_speed = NetworkPacketLimits.clamp(config.particle_speed,
                NetworkPacketLimits.MIN_PARTICLE_RATE, NetworkPacketLimits.MAX_PARTICLE_SPEED);
        this.particle_spread = NetworkPacketLimits.clamp(config.particle_spread,
                NetworkPacketLimits.MIN_PARTICLE_RATE, NetworkPacketLimits.MAX_PARTICLE_SPREAD);
        this.particle_lifetime = clamp(config.particle_lifetime, 1, NetworkPacketLimits.MAX_PARTICLE_LIFETIME);
        this.particle_density = clamp(config.particle_density, 0, NetworkPacketLimits.MAX_PARTICLE_DENSITY);
        this.use_pattern = config.use_pattern;
        this.pattern = config.pattern != null ? config.pattern : ParticlePatterns.RING;
        this.pattern_speed = NetworkPacketLimits.clamp(config.pattern_speed,
                NetworkPacketLimits.MIN_PARTICLE_RATE, NetworkPacketLimits.MAX_PARTICLE_SPEED);
        this.pattern_spread = NetworkPacketLimits.clamp(config.pattern_spread,
                NetworkPacketLimits.MIN_PARTICLE_RATE, NetworkPacketLimits.MAX_PARTICLE_SPREAD);
        this.pattern_intensity = NetworkPacketLimits.clamp(config.pattern_intensity,
                NetworkPacketLimits.MIN_PARTICLE_RATE, NetworkPacketLimits.MAX_PARTICLE_INTENSITY);
        this.particle_color = new ArrayList<>(
                config.particle_color != null ? config.particle_color : new ArrayList<>()
        );
        this.max_particle_color = clamp(config.max_particle_color, 0, NetworkPacketLimits.MAX_DYE_COLORS);
        this.edit_permission_mode = config.edit_permission_mode != null
                ? config.edit_permission_mode
                : PillarParticleConfig.EditPermissionMode.STRICT;
        this.items = new HashSet<>(
                config.items != null ? config.items : new HashSet<>()
        );
        this.itemsReadOnly = !config.canEditItems();
        this.propertiesReadOnly = !config.canEditProperties();
    }

    public SyncConfigPacket(FriendlyByteBuf buf) {
        this.particle_speed = NetworkPacketLimits.readBoundedDouble(buf, NetworkPacketLimits.MIN_PARTICLE_RATE,
                NetworkPacketLimits.MAX_PARTICLE_SPEED, "particle speed");
        this.particle_spread = NetworkPacketLimits.readBoundedDouble(buf, NetworkPacketLimits.MIN_PARTICLE_RATE,
                NetworkPacketLimits.MAX_PARTICLE_SPREAD, "particle spread");
        this.particle_lifetime = NetworkPacketLimits.readBoundedInt(buf, 1,
                NetworkPacketLimits.MAX_PARTICLE_LIFETIME, "particle lifetime");
        this.particle_density = NetworkPacketLimits.readBoundedInt(buf, 0,
                NetworkPacketLimits.MAX_PARTICLE_DENSITY, "particle density");
        this.use_pattern = buf.readBoolean();
        this.pattern = NetworkPacketLimits.readUtf(buf, NetworkPacketLimits.MAX_PATTERN_LENGTH, "pattern");
        this.pattern_speed = NetworkPacketLimits.readBoundedDouble(buf, NetworkPacketLimits.MIN_PARTICLE_RATE,
                NetworkPacketLimits.MAX_PARTICLE_SPEED, "pattern speed");
        this.pattern_spread = NetworkPacketLimits.readBoundedDouble(buf, NetworkPacketLimits.MIN_PARTICLE_RATE,
                NetworkPacketLimits.MAX_PARTICLE_SPREAD, "pattern spread");
        this.pattern_intensity = NetworkPacketLimits.readBoundedDouble(buf, NetworkPacketLimits.MIN_PARTICLE_RATE,
                NetworkPacketLimits.MAX_PARTICLE_INTENSITY, "pattern intensity");

        int colorCount = NetworkPacketLimits.readCount(buf, NetworkPacketLimits.MAX_DYE_COLORS, "particle color");
        this.particle_color = new ArrayList<>();
        for (int i = 0; i < colorCount; i++) {
            this.particle_color.add(NetworkPacketLimits.readUtf(buf, NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "particle color"));
        }
        this.max_particle_color = NetworkPacketLimits.readBoundedInt(buf, 0, NetworkPacketLimits.MAX_DYE_COLORS, "maximum particle colors");
        this.edit_permission_mode = PillarParticleConfig.parseEditPermissionMode(
                NetworkPacketLimits.readUtf(buf, 16, "edit permission mode"));

        int itemCount = NetworkPacketLimits.readCount(buf, NetworkPacketLimits.MAX_CONFIG_ITEMS, "configured item");
        this.items = new HashSet<>();
        for (int i = 0; i < itemCount; i++) {
            this.items.add(NetworkPacketLimits.readUtf(buf, NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "configured item"));
        }
        this.itemsReadOnly = buf.readBoolean();
        this.propertiesReadOnly = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(particle_speed);
        buf.writeDouble(particle_spread);
        buf.writeInt(particle_lifetime);
        buf.writeInt(particle_density);
        buf.writeBoolean(use_pattern);
        NetworkPacketLimits.writeUtf(buf, pattern != null ? pattern : ParticlePatterns.RING,
                NetworkPacketLimits.MAX_PATTERN_LENGTH, "pattern");
        buf.writeDouble(pattern_speed);
        buf.writeDouble(pattern_spread);
        buf.writeDouble(pattern_intensity);

        NetworkPacketLimits.checkCount(particle_color != null ? particle_color.size() : 0,
                NetworkPacketLimits.MAX_DYE_COLORS, "particle color");
        buf.writeInt(particle_color != null ? particle_color.size() : 0);
        if (particle_color != null) {
            for (String color : particle_color) {
                NetworkPacketLimits.writeUtf(buf, color,
                        NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "particle color");
            }
        }
        buf.writeInt(max_particle_color);
        NetworkPacketLimits.writeUtf(buf,
                edit_permission_mode != null ? edit_permission_mode.name() : "STRICT",
                16, "edit permission mode");

        NetworkPacketLimits.checkCount(items != null ? items.size() : 0,
                NetworkPacketLimits.MAX_CONFIG_ITEMS, "configured item");
        buf.writeInt(items != null ? items.size() : 0);
        if (items != null) {
            for (String item : items) {
                NetworkPacketLimits.writeUtf(buf, item,
                        NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "configured item");
            }
        }
        buf.writeBoolean(itemsReadOnly);
        buf.writeBoolean(propertiesReadOnly);
    }

    public static SyncConfigPacket decode(FriendlyByteBuf buf) {
        return new SyncConfigPacket(buf);
    }

    private static int clamp(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlers.applyServerConfig(this)));
        ctx.get().setPacketHandled(true);
    }
}
