package net.minecraft.server.players;

import java.util.Optional;
import java.util.UUID;

public interface UserNameToIdResolver {
   void add(NameAndId nameAndId);

   Optional get(String name);

   Optional get(UUID id);

   void resolveOfflineUsers(boolean value);

   void save();
}
