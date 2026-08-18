package net.minecraft.server.jsonrpc.internalapi;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public interface MinecraftPlayerListService {
   List getPlayers();

   @Nullable ServerPlayer getPlayer(UUID uuid);

   default CompletableFuture getUser(final Optional id, final Optional name) {
      if (id.isPresent()) {
         Optional nameAndId = this.getCachedUserById((UUID)id.get());
         return nameAndId.isPresent() ? CompletableFuture.completedFuture(nameAndId) : CompletableFuture.supplyAsync(() -> this.fetchUserById((UUID)id.get()), Util.nonCriticalIoPool());
      } else {
         return name.isPresent() ? CompletableFuture.supplyAsync(() -> this.fetchUserByName((String)name.get()), Util.nonCriticalIoPool()) : CompletableFuture.completedFuture(Optional.empty());
      }
   }

   Optional fetchUserByName(String name);

   Optional fetchUserById(UUID id);

   Optional getCachedUserById(UUID id);

   Optional getPlayer(Optional id, Optional name);

   List getPlayersWithAddress(String ip);

   @Nullable ServerPlayer getPlayerByName(String name);

   void remove(ServerPlayer player, ClientInfo clientInfo);
}
