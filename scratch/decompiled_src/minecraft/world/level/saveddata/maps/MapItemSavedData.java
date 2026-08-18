package net.minecraft.world.level.saveddata.maps;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.MapDecorations;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jspecify.annotations.Nullable;

public class MapItemSavedData extends SavedData {
   private static final int MAP_SIZE = 128;
   private static final int HALF_MAP_SIZE = 64;
   public static final int MAX_SCALE = 4;
   public static final int TRACKED_DECORATION_LIMIT = 256;
   private static final String FRAME_PREFIX = "frame-";
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter((m) -> m.dimension), Codec.INT.fieldOf("xCenter").forGetter((m) -> m.centerX), Codec.INT.fieldOf("zCenter").forGetter((m) -> m.centerZ), Codec.BYTE.optionalFieldOf("scale", (byte)0).forGetter((m) -> m.scale), Codec.BYTE_BUFFER.fieldOf("colors").forGetter((m) -> ByteBuffer.wrap(m.colors)), Codec.BOOL.optionalFieldOf("trackingPosition", true).forGetter((m) -> m.trackingPosition), Codec.BOOL.optionalFieldOf("unlimitedTracking", false).forGetter((m) -> m.unlimitedTracking), Codec.BOOL.optionalFieldOf("locked", false).forGetter((m) -> m.locked), MapBanner.CODEC.listOf().optionalFieldOf("banners", List.of()).forGetter((m) -> List.copyOf(m.bannerMarkers.values())), MapFrame.CODEC.listOf().optionalFieldOf("frames", List.of()).forGetter((m) -> List.copyOf(m.frameMarkers.values()))).apply(i, MapItemSavedData::new));
   public final int centerX;
   public final int centerZ;
   public final ResourceKey dimension;
   private final boolean trackingPosition;
   private final boolean unlimitedTracking;
   public final byte scale;
   public byte[] colors = new byte[16384];
   public final boolean locked;
   private final List carriedBy = Lists.newArrayList();
   private final Map carriedByPlayers = Maps.newHashMap();
   private final Map bannerMarkers = Maps.newHashMap();
   private final Map decorations = Maps.newLinkedHashMap();
   private final Map frameMarkers = Maps.newHashMap();
   private int trackedDecorationCount;

   public static SavedDataType type(final MapId id) {
      return new SavedDataType(Identifier.withDefaultNamespace(id.key()), () -> {
         throw new IllegalStateException("Should never create an empty map saved data");
      }, CODEC, DataFixTypes.SAVED_DATA_MAP_DATA);
   }

   private MapItemSavedData(final int centerX, final int centerZ, final byte scale, final boolean trackingPosition, final boolean unlimitedTracking, final boolean locked, final ResourceKey dimension) {
      this.scale = scale;
      this.centerX = centerX;
      this.centerZ = centerZ;
      this.dimension = dimension;
      this.trackingPosition = trackingPosition;
      this.unlimitedTracking = unlimitedTracking;
      this.locked = locked;
   }

   private MapItemSavedData(final ResourceKey dimension, final int centerX, final int centerZ, final byte scale, final ByteBuffer colors, final boolean trackingPosition, final boolean unlimitedTracking, final boolean locked, final List banners, final List frames) {
      this(centerX, centerZ, (byte)Mth.clamp(scale, 0, 4), trackingPosition, unlimitedTracking, locked, dimension);
      if (colors.array().length == 16384) {
         this.colors = colors.array();
      }

      for(MapBanner banner : banners) {
         this.bannerMarkers.put(banner.getId(), banner);
         this.addDecoration(banner.getDecoration(), (LevelAccessor)null, banner.getId(), (double)banner.pos().getX(), (double)banner.pos().getZ(), 180.0D, (Component)banner.name().orElse((Object)null));
      }

      for(MapFrame frame : frames) {
         this.frameMarkers.put(frame.getId(), frame);
         this.addDecoration(MapDecorationTypes.FRAME, (LevelAccessor)null, getFrameKey(frame.entityId()), (double)frame.pos().getX(), (double)frame.pos().getZ(), (double)frame.rotation(), (Component)null);
      }

   }

   public static MapItemSavedData createFresh(final double originX, final double originY, final byte scale, final boolean trackingPosition, final boolean unlimitedTracking, final ResourceKey dimension) {
      int size = 128 * (1 << scale);
      int areaX = Mth.floor((originX + 64.0D) / (double)size);
      int areaZ = Mth.floor((originY + 64.0D) / (double)size);
      int x = areaX * size + size / 2 - 64;
      int z = areaZ * size + size / 2 - 64;
      return new MapItemSavedData(x, z, scale, trackingPosition, unlimitedTracking, false, dimension);
   }

   public static MapItemSavedData createForClient(final byte scale, final boolean isLocked, final ResourceKey dimension) {
      return new MapItemSavedData(0, 0, scale, false, false, isLocked, dimension);
   }

   public MapItemSavedData locked() {
      MapItemSavedData result = new MapItemSavedData(this.centerX, this.centerZ, this.scale, this.trackingPosition, this.unlimitedTracking, true, this.dimension);
      result.bannerMarkers.putAll(this.bannerMarkers);
      result.decorations.putAll(this.decorations);
      result.trackedDecorationCount = this.trackedDecorationCount;
      System.arraycopy(this.colors, 0, result.colors, 0, this.colors.length);
      return result;
   }

   public MapItemSavedData scaled() {
      return createFresh((double)this.centerX, (double)this.centerZ, (byte)Mth.clamp(this.scale + 1, 0, 4), this.trackingPosition, this.unlimitedTracking, this.dimension);
   }

   private static Predicate mapMatcher(final ItemStack mapStack) {
      MapId mapId = (MapId)mapStack.get(DataComponents.MAP_ID);
      return (stack) -> {
         if (stack == mapStack) {
            return true;
         } else {
            return stack.is(mapStack.getItem()) && Objects.equals(mapId, stack.get(DataComponents.MAP_ID));
         }
      };
   }

   public void tickCarriedBy(final Player tickingPlayer, final ItemStack itemStack, final @Nullable ItemFrame placedInFrame) {
      if (!this.carriedByPlayers.containsKey(tickingPlayer)) {
         MapItemSavedData.HoldingPlayer holdingPlayer = new MapItemSavedData.HoldingPlayer(tickingPlayer);
         this.carriedByPlayers.put(tickingPlayer, holdingPlayer);
         this.carriedBy.add(holdingPlayer);
      }

      Predicate mapMatcher = mapMatcher(itemStack);
      if (!tickingPlayer.getInventory().contains(mapMatcher)) {
         this.removeDecoration(tickingPlayer.getPlainTextName());
      }

      for(int i = 0; i < this.carriedBy.size(); ++i) {
         MapItemSavedData.HoldingPlayer otherHoldingPlayer = (MapItemSavedData.HoldingPlayer)this.carriedBy.get(i);
         Player otherPlayer = otherHoldingPlayer.player;
         String otherPlayerName = otherPlayer.getPlainTextName();
         if (!otherPlayer.isRemoved() && (placedInFrame != null || otherPlayer.getInventory().contains(mapMatcher))) {
            if (placedInFrame == null && otherPlayer.level().dimension() == this.dimension && this.trackingPosition) {
               this.addDecoration(MapDecorationTypes.PLAYER, otherPlayer.level(), otherPlayerName, otherPlayer.getX(), otherPlayer.getZ(), (double)otherPlayer.getYRot(), (Component)null);
            }
         } else {
            this.carriedByPlayers.remove(otherPlayer);
            this.carriedBy.remove(otherHoldingPlayer);
            this.removeDecoration(otherPlayerName);
         }

         if (!otherPlayer.equals(tickingPlayer) && hasMapInvisibilityItemEquipped(otherPlayer)) {
            this.removeDecoration(otherPlayerName);
         }
      }

      if (placedInFrame != null && this.trackingPosition) {
         BlockPos pos = placedInFrame.getPos();
         MapFrame existingFrame = (MapFrame)this.frameMarkers.get(MapFrame.frameId(pos));
         if (existingFrame != null && placedInFrame.getId() != existingFrame.entityId() && this.frameMarkers.containsKey(existingFrame.getId())) {
            this.removeDecoration(getFrameKey(existingFrame.entityId()));
         }

         MapFrame mapFrame = new MapFrame(pos, placedInFrame.getDirection().get2DDataValue() * 90, placedInFrame.getId());
         this.addDecoration(MapDecorationTypes.FRAME, tickingPlayer.level(), getFrameKey(placedInFrame.getId()), (double)pos.getX(), (double)pos.getZ(), (double)(placedInFrame.getDirection().get2DDataValue() * 90), (Component)null);
         MapFrame oldFrame = (MapFrame)this.frameMarkers.put(mapFrame.getId(), mapFrame);
         if (!mapFrame.equals(oldFrame)) {
            this.setDirty();
         }
      }

      MapDecorations staticDecorations = (MapDecorations)itemStack.getOrDefault(DataComponents.MAP_DECORATIONS, MapDecorations.EMPTY);
      if (!this.decorations.keySet().containsAll(staticDecorations.decorations().keySet())) {
         staticDecorations.decorations().forEach((id, entry) -> {
            if (!this.decorations.containsKey(id)) {
               this.addDecoration(entry.type(), tickingPlayer.level(), id, entry.x(), entry.z(), (double)entry.rotation(), (Component)null);
            }

         });
      }

   }

   private static boolean hasMapInvisibilityItemEquipped(final Player player) {
      for(EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
         if (equipmentSlot != EquipmentSlot.MAINHAND && equipmentSlot != EquipmentSlot.OFFHAND && player.getItemBySlot(equipmentSlot).is(ItemTags.MAP_INVISIBILITY_EQUIPMENT)) {
            return true;
         }
      }

      return false;
   }

   private void removeDecoration(final String string) {
      MapDecoration decoration = (MapDecoration)this.decorations.remove(string);
      if (decoration != null && ((MapDecorationType)decoration.type().value()).trackCount()) {
         --this.trackedDecorationCount;
      }

      this.setDecorationsDirty();
   }

   public static void addTargetDecoration(final ItemStack itemStack, final BlockPos position, final String key, final Holder decorationType) {
      MapDecorations.Entry newDecoration = new MapDecorations.Entry(decorationType, (double)position.getX(), (double)position.getZ(), 180.0F);
      itemStack.update(DataComponents.MAP_DECORATIONS, MapDecorations.EMPTY, (decorations) -> decorations.withDecoration(key, newDecoration));
   }

   private void addDecoration(final Holder type, final @Nullable LevelAccessor level, final String key, final double xPos, final double zPos, final double yRot, final @Nullable Component name) {
      int scaling = 1 << this.scale;
      float xDeltaFromCenter = (float)(xPos - (double)this.centerX) / (float)scaling;
      float yDeltaFromCenter = (float)(zPos - (double)this.centerZ) / (float)scaling;
      MapItemSavedData.MapDecorationLocation locationAndType = this.calculateDecorationLocationAndType(type, level, yRot, xDeltaFromCenter, yDeltaFromCenter);
      if (locationAndType == null) {
         this.removeDecoration(key);
      } else {
         MapDecoration newDecoration = new MapDecoration(locationAndType.type(), locationAndType.x(), locationAndType.y(), locationAndType.rot(), Optional.ofNullable(name));
         MapDecoration previousDecoration = (MapDecoration)this.decorations.put(key, newDecoration);
         if (!newDecoration.equals(previousDecoration)) {
            if (previousDecoration != null && ((MapDecorationType)previousDecoration.type().value()).trackCount()) {
               --this.trackedDecorationCount;
            }

            if (((MapDecorationType)locationAndType.type().value()).trackCount()) {
               ++this.trackedDecorationCount;
            }

            this.setDecorationsDirty();
         }

      }
   }

   private MapItemSavedData.@Nullable MapDecorationLocation calculateDecorationLocationAndType(final Holder type, final @Nullable LevelAccessor level, final double yRot, final float xDeltaFromCenter, final float yDeltaFromCenter) {
      byte clampedXDeltaFromCenter = clampMapCoordinate(xDeltaFromCenter);
      byte clampedYDeltaFromCenter = clampMapCoordinate(yDeltaFromCenter);
      if (type.is(MapDecorationTypes.PLAYER)) {
         Pair typeAndRotation = this.playerDecorationTypeAndRotation(type, level, yRot, xDeltaFromCenter, yDeltaFromCenter);
         return typeAndRotation == null ? null : new MapItemSavedData.MapDecorationLocation((Holder)typeAndRotation.getFirst(), clampedXDeltaFromCenter, clampedYDeltaFromCenter, typeAndRotation.getSecond());
      } else {
         return !isInsideMap(xDeltaFromCenter, yDeltaFromCenter) && !this.unlimitedTracking ? null : new MapItemSavedData.MapDecorationLocation(type, clampedXDeltaFromCenter, clampedYDeltaFromCenter, this.calculateRotation(level, yRot));
      }
   }

   private @Nullable Pair playerDecorationTypeAndRotation(final Holder type, final @Nullable LevelAccessor level, final double yRot, final float xDeltaFromCenter, final float yDeltaFromCenter) {
      if (isInsideMap(xDeltaFromCenter, yDeltaFromCenter)) {
         return Pair.of(type, this.calculateRotation(level, yRot));
      } else {
         Holder outsideMapDecorationType = this.decorationTypeForPlayerOutsideMap(xDeltaFromCenter, yDeltaFromCenter);
         return outsideMapDecorationType == null ? null : Pair.of(outsideMapDecorationType, this.calculateRotation(level, yRot));
      }
   }

   private byte calculateRotation(final @Nullable LevelAccessor level, final double yRot) {
      if (this.dimension == Level.NETHER && level != null) {
         int s = (int)(level.getGameTime() / 10L);
         return (byte)(s * s * 34187121 + s * 121 >> 15 & 15);
      } else {
         double adjustedYRot = yRot < 0.0D ? yRot - 8.0D : yRot + 8.0D;
         return (byte)((int)(adjustedYRot * 16.0D / 360.0D));
      }
   }

   private static boolean isInsideMap(final float xd, final float yd) {
      int halfSize = 63;
      return xd >= -63.0F && yd >= -63.0F && xd <= 63.0F && yd <= 63.0F;
   }

   private @Nullable Holder decorationTypeForPlayerOutsideMap(final float xDeltaFromCenter, final float yDeltaFromCenter) {
      int rangeLimit = 320;
      boolean isWithinLimits = Math.abs(xDeltaFromCenter) < 320.0F && Math.abs(yDeltaFromCenter) < 320.0F;
      if (isWithinLimits) {
         return MapDecorationTypes.PLAYER_OFF_MAP;
      } else {
         return this.unlimitedTracking ? MapDecorationTypes.PLAYER_OFF_LIMITS : null;
      }
   }

   private static byte clampMapCoordinate(final float deltaFromCenter) {
      int halfSize = 63;
      if (deltaFromCenter <= -63.0F) {
         return -128;
      } else {
         return deltaFromCenter >= 63.0F ? 127 : (byte)((int)((double)(deltaFromCenter * 2.0F) + 0.5D));
      }
   }

   public @Nullable Packet getUpdatePacket(final MapId id, final Player player) {
      MapItemSavedData.HoldingPlayer holdingPlayer = (MapItemSavedData.HoldingPlayer)this.carriedByPlayers.get(player);
      return holdingPlayer == null ? null : holdingPlayer.nextUpdatePacket(id);
   }

   private void setColorsDirty(final int x, final int y) {
      this.setDirty();

      for(MapItemSavedData.HoldingPlayer holdingPlayer : this.carriedBy) {
         holdingPlayer.markColorsDirty(x, y);
      }

   }

   private void setDecorationsDirty() {
      this.carriedBy.forEach(MapItemSavedData.HoldingPlayer::markDecorationsDirty);
   }

   public MapItemSavedData.HoldingPlayer getHoldingPlayer(final Player player) {
      MapItemSavedData.HoldingPlayer holdingPlayer = (MapItemSavedData.HoldingPlayer)this.carriedByPlayers.get(player);
      if (holdingPlayer == null) {
         holdingPlayer = new MapItemSavedData.HoldingPlayer(player);
         this.carriedByPlayers.put(player, holdingPlayer);
         this.carriedBy.add(holdingPlayer);
      }

      return holdingPlayer;
   }

   public boolean toggleBanner(final LevelAccessor level, final BlockPos pos) {
      double xPos = (double)pos.getX() + 0.5D;
      double zPos = (double)pos.getZ() + 0.5D;
      int scale = 1 << this.scale;
      double xd = (xPos - (double)this.centerX) / (double)scale;
      double yd = (zPos - (double)this.centerZ) / (double)scale;
      int halfSize = 63;
      if (xd >= -63.0D && yd >= -63.0D && xd <= 63.0D && yd <= 63.0D) {
         MapBanner banner = MapBanner.fromWorld(level, pos);
         if (banner == null) {
            return false;
         }

         if (this.bannerMarkers.remove(banner.getId(), banner)) {
            this.removeDecoration(banner.getId());
            this.setDirty();
            return true;
         }

         if (!this.isTrackedCountOverLimit(256)) {
            this.bannerMarkers.put(banner.getId(), banner);
            this.addDecoration(banner.getDecoration(), level, banner.getId(), xPos, zPos, 180.0D, (Component)banner.name().orElse((Object)null));
            this.setDirty();
            return true;
         }
      }

      return false;
   }

   public void checkBanners(final BlockGetter level, final int x, final int z) {
      Iterator iterator = this.bannerMarkers.values().iterator();

      while(iterator.hasNext()) {
         MapBanner expected = (MapBanner)iterator.next();
         if (expected.pos().getX() == x && expected.pos().getZ() == z) {
            MapBanner current = MapBanner.fromWorld(level, expected.pos());
            if (!expected.equals(current)) {
               iterator.remove();
               this.removeDecoration(expected.getId());
               this.setDirty();
            }
         }
      }

   }

   public Collection getBanners() {
      return this.bannerMarkers.values();
   }

   public void removedFromFrame(final BlockPos pos, final int entityID) {
      this.removeDecoration(getFrameKey(entityID));
      this.frameMarkers.remove(MapFrame.frameId(pos));
      this.setDirty();
   }

   public boolean updateColor(final int x, final int y, final byte newColor) {
      byte oldColor = this.colors[x + y * 128];
      if (oldColor != newColor) {
         this.setColor(x, y, newColor);
         return true;
      } else {
         return false;
      }
   }

   public void setColor(final int x, final int y, final byte newColor) {
      this.colors[x + y * 128] = newColor;
      this.setColorsDirty(x, y);
   }

   public void addClientSideDecorations(final List decorations) {
      this.decorations.clear();
      this.trackedDecorationCount = 0;

      for(int i = 0; i < decorations.size(); ++i) {
         MapDecoration decoration = (MapDecoration)decorations.get(i);
         this.decorations.put("icon-" + i, decoration);
         if (((MapDecorationType)decoration.type().value()).trackCount()) {
            ++this.trackedDecorationCount;
         }
      }

   }

   public Iterable getDecorations() {
      return this.decorations.values();
   }

   public boolean isTrackedCountOverLimit(final int limit) {
      return this.trackedDecorationCount > limit;
   }

   private static String getFrameKey(final int id) {
      return "frame-" + id;
   }

   public class HoldingPlayer {
      public final Player player;
      private boolean dirtyData;
      private int minDirtyX;
      private int minDirtyY;
      private int maxDirtyX;
      private int maxDirtyY;
      private boolean dirtyDecorations;
      private int tick;
      public int step;

      private HoldingPlayer(final Player player) {
         Objects.requireNonNull(MapItemSavedData.this);
         super();
         this.dirtyData = true;
         this.maxDirtyX = 127;
         this.maxDirtyY = 127;
         this.dirtyDecorations = true;
         this.player = player;
      }

      private MapItemSavedData.MapPatch createPatch() {
         int startX = this.minDirtyX;
         int startY = this.minDirtyY;
         int width = this.maxDirtyX + 1 - this.minDirtyX;
         int height = this.maxDirtyY + 1 - this.minDirtyY;
         byte[] patch = new byte[width * height];

         for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
               patch[x + y * width] = MapItemSavedData.this.colors[startX + x + (startY + y) * 128];
            }
         }

         return new MapItemSavedData.MapPatch(startX, startY, width, height, patch);
      }

      private @Nullable Packet nextUpdatePacket(final MapId id) {
         MapItemSavedData.MapPatch patch;
         if (this.dirtyData) {
            this.dirtyData = false;
            patch = this.createPatch();
         } else {
            patch = null;
         }

         Collection decorations;
         if (this.dirtyDecorations && this.tick++ % 5 == 0) {
            this.dirtyDecorations = false;
            decorations = MapItemSavedData.this.decorations.values();
         } else {
            decorations = null;
         }

         return decorations == null && patch == null ? null : new ClientboundMapItemDataPacket(id, MapItemSavedData.this.scale, MapItemSavedData.this.locked, decorations, patch);
      }

      private void markColorsDirty(final int x, final int y) {
         if (this.dirtyData) {
            this.minDirtyX = Math.min(this.minDirtyX, x);
            this.minDirtyY = Math.min(this.minDirtyY, y);
            this.maxDirtyX = Math.max(this.maxDirtyX, x);
            this.maxDirtyY = Math.max(this.maxDirtyY, y);
         } else {
            this.dirtyData = true;
            this.minDirtyX = x;
            this.minDirtyY = y;
            this.maxDirtyX = x;
            this.maxDirtyY = y;
         }

      }

      private void markDecorationsDirty() {
         this.dirtyDecorations = true;
      }
   }

   private static record MapDecorationLocation(Holder type, byte x, byte y, byte rot) {
   }

   public static record MapPatch(int startX, int startY, int width, int height, byte[] mapColors) {
      public static final StreamCodec STREAM_CODEC = StreamCodec.of(MapItemSavedData.MapPatch::write, MapItemSavedData.MapPatch::read);

      private static void write(final ByteBuf output, final Optional optional) {
         if (optional.isPresent()) {
            MapItemSavedData.MapPatch patch = (MapItemSavedData.MapPatch)optional.get();
            output.writeByte(patch.width);
            output.writeByte(patch.height);
            output.writeByte(patch.startX);
            output.writeByte(patch.startY);
            FriendlyByteBuf.writeByteArray(output, patch.mapColors);
         } else {
            output.writeByte(0);
         }

      }

      private static Optional read(final ByteBuf input) {
         int width = input.readUnsignedByte();
         if (width > 0) {
            int height = input.readUnsignedByte();
            int startX = input.readUnsignedByte();
            int startY = input.readUnsignedByte();
            byte[] mapColors = FriendlyByteBuf.readByteArray(input);
            return Optional.of(new MapItemSavedData.MapPatch(startX, startY, width, height, mapColors));
         } else {
            return Optional.empty();
         }
      }

      public void applyToMap(final MapItemSavedData map) {
         for(int x = 0; x < this.width; ++x) {
            for(int y = 0; y < this.height; ++y) {
               map.setColor(this.startX + x, this.startY + y, this.mapColors[x + y * this.width]);
            }
         }

      }
   }
}
