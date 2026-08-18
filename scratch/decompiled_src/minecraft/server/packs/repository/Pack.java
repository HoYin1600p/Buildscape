package net.minecraft.server.packs.repository;

import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FeatureFlagsMetadataSection;
import net.minecraft.server.packs.OverlayMetadataSection;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackMetadataResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.world.flag.FeatureFlagSet;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class Pack {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final PackLocationInfo location;
   private final Pack.ResourcesSupplier resources;
   private final Pack.Metadata metadata;
   private final PackSelectionConfig selectionConfig;

   public static @Nullable Pack readMetaAndCreate(final PackLocationInfo location, final Pack.ResourcesSupplier resources, final PackType packType, final PackSelectionConfig selectionConfig) {
      PackFormat currentPackVersion = SharedConstants.getCurrentVersion().packVersion(packType);
      Pack.Metadata meta = readPackMetadata(location, resources, currentPackVersion, packType);
      return meta != null ? new Pack(location, resources, meta, selectionConfig) : null;
   }

   public Pack(final PackLocationInfo location, final Pack.ResourcesSupplier resources, final Pack.Metadata metadata, final PackSelectionConfig selectionConfig) {
      this.location = location;
      this.resources = resources;
      this.metadata = metadata;
      this.selectionConfig = selectionConfig;
   }

   public static Pack.@Nullable Metadata readPackMetadata(final PackLocationInfo location, final Pack.ResourcesSupplier resources, final PackFormat currentPackVersion, final PackType type) {
      try (PackMetadataResources pack = resources.openMetadata(location)) {
         PackMetadataSection meta;
         try {
            meta = (PackMetadataSection)pack.getMetadataSection(PackMetadataSection.forPackType(type));
         } catch (JsonParseException var13) {
            LOGGER.warn("Error reading pack metadata, attempting fallback type", var13);
            meta = (PackMetadataSection)pack.getMetadataSection(PackMetadataSection.FALLBACK_TYPE);
         }

         if (meta == null) {
            LOGGER.warn("Missing metadata in pack {}", location.id());
            return null;
         } else {
            FeatureFlagsMetadataSection featureFlagMeta = (FeatureFlagsMetadataSection)pack.getMetadataSection(FeatureFlagsMetadataSection.TYPE);
            FeatureFlagSet requiredFlags = featureFlagMeta != null ? featureFlagMeta.flags() : FeatureFlagSet.of();
            PackCompatibility packCompatibility = PackCompatibility.forVersion(meta.supportedFormats(), currentPackVersion);
            OverlayMetadataSection overlays = (OverlayMetadataSection)pack.getMetadataSection(OverlayMetadataSection.forPackType(type));
            List overlaySet = overlays != null ? overlays.overlaysForVersion(currentPackVersion) : List.of();
            return new Pack.Metadata(meta.description(), packCompatibility, requiredFlags, overlaySet);
         }
      } catch (Exception var15) {
         LOGGER.warn("Failed to read pack {} metadata", location.id(), var15);
         return null;
      }
   }

   public PackLocationInfo location() {
      return this.location;
   }

   public Component getTitle() {
      return this.location.title();
   }

   public Component getDescription() {
      return this.metadata.description();
   }

   public Component getChatLink(final boolean enabled) {
      return this.location.createChatLink(enabled, this.metadata.description);
   }

   public PackCompatibility getCompatibility() {
      return this.metadata.compatibility();
   }

   public FeatureFlagSet getRequestedFeatures() {
      return this.metadata.requestedFeatures();
   }

   public PackMetadataResources openMetadata() {
      return this.resources.openMetadata(this.location);
   }

   public Stream open() {
      return this.resources.openResources(this.location, this.metadata);
   }

   public String getId() {
      return this.location.id();
   }

   public PackSelectionConfig selectionConfig() {
      return this.selectionConfig;
   }

   public boolean isRequired() {
      return this.selectionConfig.required();
   }

   public boolean isFixedPosition() {
      return this.selectionConfig.fixedPosition();
   }

   public Pack.Position getDefaultPosition() {
      return this.selectionConfig.defaultPosition();
   }

   public PackSource getPackSource() {
      return this.location.source();
   }

   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      } else if (o instanceof Pack) {
         Pack that = (Pack)o;
         return this.location.equals(that.location);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.location.hashCode();
   }

   public static record Metadata(Component description, PackCompatibility compatibility, FeatureFlagSet requestedFeatures, List overlays) {
   }

   public static enum Position {
      TOP,
      BOTTOM;

      public int insert(final List list, final Object value, final Function converter, final boolean reverse) {
         Pack.Position self = reverse ? this.opposite() : this;
         if (self == BOTTOM) {
            int index;
            for(index = 0; index < list.size(); ++index) {
               PackSelectionConfig pack = (PackSelectionConfig)converter.apply(list.get(index));
               if (!pack.fixedPosition() || pack.defaultPosition() != this) {
                  break;
               }
            }

            list.add(index, value);
            return index;
         } else {
            int index;
            for(index = list.size() - 1; index >= 0; --index) {
               PackSelectionConfig pack = (PackSelectionConfig)converter.apply(list.get(index));
               if (!pack.fixedPosition() || pack.defaultPosition() != this) {
                  break;
               }
            }

            list.add(index + 1, value);
            return index + 1;
         }
      }

      public Pack.Position opposite() {
         return this == TOP ? BOTTOM : TOP;
      }

      // $FF: synthetic method
      private static Pack.Position[] $values() {
         return new Pack.Position[]{TOP, BOTTOM};
      }
   }

   public interface ResourcesSupplier {
      PackMetadataResources openMetadata(PackLocationInfo location);

      Stream openResources(PackLocationInfo location, Pack.Metadata metadata);
   }
}
