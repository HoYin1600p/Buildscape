package net.minecraft.server.jsonrpc.internalapi;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.players.NameAndId;

public interface MinecraftOperatorListService {
   Collection getEntries();

   void op(NameAndId nameAndId, Optional permissionLevel, Optional canBypassPlayerLimit, ClientInfo clientInfo);

   void op(NameAndId nameAndId, ClientInfo clientInfo);

   void deop(NameAndId nameAndId, ClientInfo clientInfo);

   void clear(ClientInfo clientInfo);
}
