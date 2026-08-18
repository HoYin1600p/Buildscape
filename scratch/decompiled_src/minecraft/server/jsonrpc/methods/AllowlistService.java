package net.minecraft.server.jsonrpc.methods;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.minecraft.server.jsonrpc.api.PlayerDto;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.UserWhiteListEntry;
import net.minecraft.util.Util;

public class AllowlistService {
   public static List get(final MinecraftApi minecraftApi) {
      return minecraftApi.allowListService().getEntries().stream().filter((p) -> p.getUser() != null).map((u) -> PlayerDto.from((NameAndId)u.getUser())).toList();
   }

   public static List add(final MinecraftApi minecraftApi, final List playerDtos, final ClientInfo clientInfo) {
      List fetch = playerDtos.stream().map((playerDto) -> minecraftApi.playerListService().getUser(playerDto.id(), playerDto.name())).toList();

      for(Optional user : (List)Util.sequence(fetch).join()) {
         user.ifPresent((nameAndId) -> minecraftApi.allowListService().add(new UserWhiteListEntry(nameAndId), clientInfo));
      }

      return get(minecraftApi);
   }

   public static List clear(final MinecraftApi minecraftApi, final ClientInfo clientInfo) {
      minecraftApi.allowListService().clear(clientInfo);
      return get(minecraftApi);
   }

   public static List remove(final MinecraftApi minecraftApi, final List playerDtos, final ClientInfo clientInfo) {
      List fetch = playerDtos.stream().map((playerDto) -> minecraftApi.playerListService().getUser(playerDto.id(), playerDto.name())).toList();

      for(Optional user : (List)Util.sequence(fetch).join()) {
         user.ifPresent((nameAndId) -> minecraftApi.allowListService().remove(nameAndId, clientInfo));
      }

      minecraftApi.allowListService().kickUnlistedPlayers(clientInfo);
      return get(minecraftApi);
   }

   public static List set(final MinecraftApi minecraftApi, final List playerDtos, final ClientInfo clientInfo) {
      List fetch = playerDtos.stream().map((playerDto) -> minecraftApi.playerListService().getUser(playerDto.id(), playerDto.name())).toList();
      Set finalAllowList = (Set)((List)Util.sequence(fetch).join()).stream().flatMap(Optional::stream).collect(Collectors.toSet());
      Set currentAllowList = (Set)minecraftApi.allowListService().getEntries().stream().map(StoredUserEntry::getUser).collect(Collectors.toSet());
      currentAllowList.stream().filter((user) -> !finalAllowList.contains(user)).forEach((user) -> minecraftApi.allowListService().remove(user, clientInfo));
      finalAllowList.stream().filter((user) -> !currentAllowList.contains(user)).forEach((user) -> minecraftApi.allowListService().add(new UserWhiteListEntry(user), clientInfo));
      minecraftApi.allowListService().kickUnlistedPlayers(clientInfo);
      return get(minecraftApi);
   }
}
