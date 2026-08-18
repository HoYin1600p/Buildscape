package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.services.request.AbuseReportRequest;
import com.mojang.realmsclient.dto.RealmsServer;
import java.util.Locale;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.Nullable;

public record ReportEnvironment(String clientVersion, ReportEnvironment.@Nullable Server server) {
   public static ReportEnvironment local() {
      return create((ReportEnvironment.Server)null);
   }

   public static ReportEnvironment thirdParty(final String ip) {
      return create(new ReportEnvironment.Server.ThirdParty(ip));
   }

   public static ReportEnvironment realm(final RealmsServer realm) {
      return create(new ReportEnvironment.Server.Realm(realm));
   }

   public static ReportEnvironment create(final ReportEnvironment.@Nullable Server server) {
      return new ReportEnvironment(getClientVersion(), server);
   }

   public AbuseReportRequest.ClientInfo clientInfo() {
      return new AbuseReportRequest.ClientInfo(this.clientVersion, Locale.getDefault().toLanguageTag());
   }

   public AbuseReportRequest.@Nullable ThirdPartyServerInfo thirdPartyServerInfo() {
      ReportEnvironment.Server var2 = this.server;
      if (var2 instanceof ReportEnvironment.Server.ThirdParty thirdParty) {
         return new AbuseReportRequest.ThirdPartyServerInfo(thirdParty.ip);
      } else {
         return null;
      }
   }

   public AbuseReportRequest.@Nullable RealmInfo realmInfo() {
      ReportEnvironment.Server var2 = this.server;
      if (var2 instanceof ReportEnvironment.Server.Realm realm) {
         return new AbuseReportRequest.RealmInfo(String.valueOf(realm.realmId()), realm.slotId());
      } else {
         return null;
      }
   }

   private static String getClientVersion() {
      StringBuilder version = new StringBuilder();
      version.append(SharedConstants.getCurrentVersion().id());
      if (Minecraft.checkModStatus().shouldReportAsModified()) {
         version.append(" (modded)");
      }

      return version.toString();
   }

   public interface Server {
      public static record Realm(long realmId, int slotId) implements ReportEnvironment.Server {
         public Realm(final RealmsServer realm) {
            this(realm.id, realm.activeSlot);
         }
      }

      public static record ThirdParty(String ip) implements ReportEnvironment.Server {
      }
   }
}
