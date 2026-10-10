package com.kingodogo.buildscape.data;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

/** Loader-owned attachments retain all reference tag keys and do not retain their owners globally. */
public final class PersistentData {
    public static final String ATTACHMENT_PATH = "persistent_data";
    // Copy at the serialization boundary so restored owners never share mutable tags.
    public static final Codec<CompoundTag> CODEC = CompoundTag.CODEC.xmap(CompoundTag::copy, CompoundTag::copy);
    public static final MapCodec<CompoundTag> MAP_CODEC = CODEC.fieldOf("data");
    private static volatile PersistentDataAccess access;

    private PersistentData() {}

    public static synchronized void install(PersistentDataAccess backend) {
        Objects.requireNonNull(backend, "backend");
        if (access != null) throw new IllegalStateException("Persistent data backend already installed");
        access = backend;
    }

    public static CompoundTag get(Object owner) {
        Objects.requireNonNull(owner, "owner");
        try {
            PersistentDataAccess backend = access;
            if (backend == null) throw new IllegalStateException("Loader did not install Buildscape persistent data attachments");
            return Objects.requireNonNull(backend.getOrCreate(owner), "owner attachment");
        } catch (RuntimeException exception) {
            BuildscapeCommon.LOGGER.error("Failed to access persistent data for {}", owner.getClass().getName(), exception);
            throw exception;
        }
    }
}
