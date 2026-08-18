package net.minecraft.client;

import com.mojang.util.UndashedUuid;
import java.util.Optional;
import java.util.UUID;

public class User {
   private final String name;
   private final UUID uuid;
   private final String accessToken;
   private final Optional xuid;
   private final Optional clientId;

   public User(final String name, final UUID uuid, final String accessToken, final Optional xuid, final Optional clientId) {
      this.name = name;
      this.uuid = uuid;
      this.accessToken = accessToken;
      this.xuid = xuid;
      this.clientId = clientId;
   }

   public String getSessionId() {
      return "token:" + this.accessToken + ":" + UndashedUuid.toString(this.uuid);
   }

   public UUID getProfileId() {
      return this.uuid;
   }

   public String getName() {
      return this.name;
   }

   public String getAccessToken() {
      return this.accessToken;
   }

   public Optional getClientId() {
      return this.clientId;
   }

   public Optional getXuid() {
      return this.xuid;
   }
}
