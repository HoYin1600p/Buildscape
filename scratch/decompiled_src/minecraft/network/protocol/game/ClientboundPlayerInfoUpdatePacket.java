package net.minecraft.network.protocol.game;

import com.google.common.base.MoreObjects;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.IntFunction;
import net.minecraft.Optionull;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.level.GameType;
import org.jspecify.annotations.Nullable;

public class ClientboundPlayerInfoUpdatePacket implements Packet {
   public static final StreamCodec STREAM_CODEC = Packet.codec(ClientboundPlayerInfoUpdatePacket::write, ClientboundPlayerInfoUpdatePacket::new);
   private final EnumSet actions;
   private final List entries;

   public ClientboundPlayerInfoUpdatePacket(final EnumSet actions, final Collection players) {
      this.actions = actions;
      this.entries = players.stream().map(ClientboundPlayerInfoUpdatePacket.Entry::new).toList();
   }

   public ClientboundPlayerInfoUpdatePacket(final ClientboundPlayerInfoUpdatePacket.Action action, final ServerPlayer player) {
      this.actions = EnumSet.of(action);
      this.entries = List.of(new ClientboundPlayerInfoUpdatePacket.Entry(player));
   }

   public static ClientboundPlayerInfoUpdatePacket createPlayerInitializing(final Collection players) {
      EnumSet actions = EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, ClientboundPlayerInfoUpdatePacket.Action.INITIALIZE_CHAT, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_HAT, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LIST_ORDER);
      return new ClientboundPlayerInfoUpdatePacket(actions, players);
   }

   private static StreamCodec entryCodec(final EnumSet actions) {
      return StreamCodec.of((output, entry) -> {
         output.writeUUID(entry.profileId());

         for(ClientboundPlayerInfoUpdatePacket.Action action : actions) {
            action.writer.write(output, entry);
         }

      }, (input) -> {
         ClientboundPlayerInfoUpdatePacket.EntryBuilder builder = new ClientboundPlayerInfoUpdatePacket.EntryBuilder(input.readUUID());

         for(ClientboundPlayerInfoUpdatePacket.Action action : actions) {
            action.reader.read(builder, input);
         }

         return builder.build();
      });
   }

   private ClientboundPlayerInfoUpdatePacket(final RegistryFriendlyByteBuf input) {
      this.actions = input.readEnumSet(ClientboundPlayerInfoUpdatePacket.Action.class);
      this.entries = (List)entryCodec(this.actions).apply(ByteBufCodecs.list()).decode(input);
   }

   private void write(final RegistryFriendlyByteBuf output) {
      output.writeEnumSet(this.actions, ClientboundPlayerInfoUpdatePacket.Action.class);
      entryCodec(this.actions).apply(ByteBufCodecs.list()).encode(output, this.entries);
   }

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_PLAYER_INFO_UPDATE;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handlePlayerInfoUpdate(this);
   }

   public EnumSet actions() {
      return this.actions;
   }

   public List entries() {
      return this.entries;
   }

   public List newEntries() {
      return this.actions.contains(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER) ? this.entries : List.of();
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("actions", this.actions).add("entries", this.entries).toString();
   }

   public static enum Action {
      ADD_PLAYER(0, (entry, input) -> {
         String name = (String)ByteBufCodecs.PLAYER_NAME.decode(input);
         PropertyMap properties = (PropertyMap)ByteBufCodecs.GAME_PROFILE_PROPERTIES.decode(input);
         entry.profile = new GameProfile(entry.profileId, name, properties);
      }, (output, entry) -> {
         GameProfile profile = (GameProfile)Objects.requireNonNull(entry.profile());
         ByteBufCodecs.PLAYER_NAME.encode(output, profile.name());
         ByteBufCodecs.GAME_PROFILE_PROPERTIES.encode(output, profile.properties());
      }),
      INITIALIZE_CHAT(1, (entry, input) -> entry.chatSession = (RemoteChatSession.Data)input.readNullable(RemoteChatSession.Data.STREAM_CODEC), (output, entry) -> output.writeNullable(entry.chatSession, RemoteChatSession.Data.STREAM_CODEC)),
      UPDATE_GAME_MODE(2, (entry, input) -> entry.gameMode = (GameType)GameType.STREAM_CODEC.decode(input), (output, entry) -> GameType.STREAM_CODEC.encode(output, entry.gameMode)),
      UPDATE_LISTED(3, (entry, input) -> entry.listed = input.readBoolean(), (output, entry) -> output.writeBoolean(entry.listed())),
      UPDATE_LATENCY(4, (entry, input) -> entry.latency = input.readVarInt(), (output, entry) -> output.writeVarInt(entry.latency())),
      UPDATE_DISPLAY_NAME(5, (entry, input) -> entry.displayName = (Component)FriendlyByteBuf.readNullable(input, ComponentSerialization.TRUSTED_STREAM_CODEC), (output, entry) -> FriendlyByteBuf.writeNullable(output, entry.displayName(), ComponentSerialization.TRUSTED_STREAM_CODEC)),
      UPDATE_LIST_ORDER(6, (entry, input) -> entry.listOrder = input.readVarInt(), (output, entry) -> output.writeVarInt(entry.listOrder)),
      UPDATE_HAT(7, (entry, input) -> entry.showHat = input.readBoolean(), (output, entry) -> output.writeBoolean(entry.showHat));

      private final int id;
      private final ClientboundPlayerInfoUpdatePacket.Action.Reader reader;
      private final ClientboundPlayerInfoUpdatePacket.Action.Writer writer;
      private static final IntFunction BY_ID = ByIdMap.continuous((a) -> a.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final StreamCodec STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, (a) -> a.id);

      private Action(final int id, final ClientboundPlayerInfoUpdatePacket.Action.Reader reader, final ClientboundPlayerInfoUpdatePacket.Action.Writer writer) {
         this.id = id;
         this.reader = reader;
         this.writer = writer;
      }

      // $FF: synthetic method
      private static ClientboundPlayerInfoUpdatePacket.Action[] $values() {
         return new ClientboundPlayerInfoUpdatePacket.Action[]{ADD_PLAYER, INITIALIZE_CHAT, UPDATE_GAME_MODE, UPDATE_LISTED, UPDATE_LATENCY, UPDATE_DISPLAY_NAME, UPDATE_LIST_ORDER, UPDATE_HAT};
      }

      public interface Reader {
         void read(ClientboundPlayerInfoUpdatePacket.EntryBuilder entry, RegistryFriendlyByteBuf input);
      }

      public interface Writer {
         void write(RegistryFriendlyByteBuf output, ClientboundPlayerInfoUpdatePacket.Entry entry);
      }
   }

   public static record Entry(UUID profileId, @Nullable GameProfile profile, boolean listed, int latency, GameType gameMode, @Nullable Component displayName, boolean showHat, int listOrder, RemoteChatSession.@Nullable Data chatSession) {
      private Entry(final ServerPlayer player) {
         this(player.getUUID(), player.getGameProfile(), true, player.connection.latency(), player.gameMode(), player.getTabListDisplayName(), player.isModelPartShown(PlayerModelPart.HAT), player.getTabListOrder(), (RemoteChatSession.Data)Optionull.map(player.getChatSession(), RemoteChatSession::asData));
      }
   }

   private static class EntryBuilder {
      private final UUID profileId;
      private @Nullable GameProfile profile;
      private boolean listed;
      private int latency;
      private GameType gameMode = GameType.DEFAULT_MODE;
      private @Nullable Component displayName;
      private boolean showHat;
      private int listOrder;
      private RemoteChatSession.@Nullable Data chatSession;

      private EntryBuilder(final UUID profileId) {
         this.profileId = profileId;
      }

      private ClientboundPlayerInfoUpdatePacket.Entry build() {
         return new ClientboundPlayerInfoUpdatePacket.Entry(this.profileId, this.profile, this.listed, this.latency, this.gameMode, this.displayName, this.showHat, this.listOrder, this.chatSession);
      }
   }
}
