package net.minecraft.world.level.entity;

import java.util.UUID;
import org.jspecify.annotations.Nullable;

public interface UUIDLookup {
   @Nullable UniquelyIdentifyable lookup(UUID uuid);
}
