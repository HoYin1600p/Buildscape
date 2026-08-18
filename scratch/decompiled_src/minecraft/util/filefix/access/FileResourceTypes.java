package net.minecraft.util.filefix.access;

import com.mojang.datafixers.DSL;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;

public class FileResourceTypes {
   public static final FileResourceType LEVEL_DAT = new FileResourceType(LevelDat::new);
   public static final FileResourceType PLAYER_DATA = new FileResourceType(PlayerData::new);

   public static FileResourceType savedData(final DSL.TypeReference type) {
      return savedData(type, CompressedNbt.MissingSeverity.NEUTRAL);
   }

   public static FileResourceType savedData(final DSL.TypeReference type, final CompressedNbt.MissingSeverity missingSeverity) {
      return new FileResourceType((path, dataVersion) -> new SavedDataNbt(type, path, dataVersion, missingSeverity));
   }

   public static FileResourceType chunk(final DataFixTypes type, final RegionStorageInfo info) {
      return new FileResourceType((path, dataVersion) -> new ChunkNbt(info, path, type, dataVersion));
   }
}
