package net.minecraft.client.gui.screens.worldselection;

import java.nio.file.Path;
import java.util.Optional;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.world.level.storage.LevelDataAndDimensions;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface CreateWorldCallback {
   boolean create(CreateWorldScreen createWorldScreen, LayeredRegistryAccess finalLayers, LevelDataAndDimensions.WorldDataAndGenSettings worldDataAndGenSettings, Optional gameRules, @Nullable Path tempDataPackDir);
}
