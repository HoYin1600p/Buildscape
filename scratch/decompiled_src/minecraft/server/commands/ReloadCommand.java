package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.WorldData;
import org.slf4j.Logger;

public class ReloadCommand {
   private static final Logger LOGGER = LogUtils.getLogger();

   public static void reloadPacks(final Collection selectedPacks, final CommandSourceStack source) {
      source.getServer().reloadResources(selectedPacks).exceptionally((throwable) -> {
         LOGGER.warn("Failed to execute reload", throwable);
         source.sendFailure(Component.translatable("commands.reload.failure"));
         return null;
      });
   }

   private static Collection discoverNewPacks(final PackRepository packRepository, final WorldData worldData, final Collection currentPacks) {
      packRepository.reload();
      Collection selected = Lists.newArrayList(currentPacks);
      Collection disabled = worldData.getDataConfiguration().dataPacks().getDisabled();

      for(String pack : packRepository.getAvailableIds()) {
         if (!disabled.contains(pack) && !selected.contains(pack)) {
            selected.add(pack);
         }
      }

      return selected;
   }

   public static void register(final CommandDispatcher dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("reload").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))).executes((s) -> {
         CommandSourceStack source = (CommandSourceStack)s.getSource();
         MinecraftServer server = source.getServer();
         PackRepository packRepository = server.getPackRepository();
         WorldData worldData = server.getWorldData();
         Collection currentPacks = packRepository.getSelectedIds();
         Collection newSelectedPacks = discoverNewPacks(packRepository, worldData, currentPacks);
         source.sendSuccess(() -> Component.translatable("commands.reload.success"), true);
         reloadPacks(newSelectedPacks, source);
         return 0;
      }));
   }
}
