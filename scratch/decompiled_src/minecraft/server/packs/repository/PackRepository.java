package net.minecraft.server.packs.repository;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.Util;
import net.minecraft.world.flag.FeatureFlagSet;
import org.jspecify.annotations.Nullable;

public class PackRepository {
   private final Set sources;
   private Map available = ImmutableMap.of();
   private List selected = ImmutableList.of();

   public PackRepository(final RepositorySource... sources) {
      this.sources = ImmutableSet.copyOf(sources);
   }

   public static String displayPackList(final Collection packs) {
      return (String)packs.stream().map((pack) -> pack.getId() + (pack.getCompatibility().isCompatible() ? "" : " (incompatible)")).collect(Collectors.joining(", "));
   }

   public void reload() {
      List currentlySelectedNames = (List)this.selected.stream().map(Pack::getId).collect(ImmutableList.toImmutableList());
      this.available = this.discoverAvailable();
      this.selected = this.rebuildSelected(currentlySelectedNames);
   }

   private Map discoverAvailable() {
      Map discovered = Maps.newTreeMap();

      for(RepositorySource source : this.sources) {
         source.loadPacks((pack) -> discovered.put(pack.getId(), pack));
      }

      return ImmutableMap.copyOf(discovered);
   }

   public boolean isAbleToClearAnyPack() {
      List newSelected = this.rebuildSelected(List.of());
      return !this.selected.equals(newSelected);
   }

   public void setSelected(final Collection packs) {
      this.selected = this.rebuildSelected(packs);
   }

   public boolean addPack(final String packId) {
      Pack pack = (Pack)this.available.get(packId);
      if (pack != null && !this.selected.contains(pack)) {
         List selectedCopy = Lists.newArrayList(this.selected);
         selectedCopy.add(pack);
         this.selected = selectedCopy;
         return true;
      } else {
         return false;
      }
   }

   public boolean removePack(final String packId) {
      Pack pack = (Pack)this.available.get(packId);
      if (pack != null && this.selected.contains(pack)) {
         List selectedCopy = Lists.newArrayList(this.selected);
         selectedCopy.remove(pack);
         this.selected = selectedCopy;
         return true;
      } else {
         return false;
      }
   }

   private List rebuildSelected(final Collection selectedNames) {
      List selectedAndPresent = (List)this.getAvailablePacks(selectedNames).collect(Util.toMutableList());

      for(Pack pack : this.available.values()) {
         if (pack.isRequired() && !selectedAndPresent.contains(pack)) {
            pack.getDefaultPosition().insert(selectedAndPresent, pack, Pack::selectionConfig, false);
         }
      }

      return ImmutableList.copyOf(selectedAndPresent);
   }

   private Stream getAvailablePacks(final Collection ids) {
      return ids.stream().map(this.available::get).filter(Objects::nonNull);
   }

   public Collection getAvailableIds() {
      return this.available.keySet();
   }

   public Collection getAvailablePacks() {
      return this.available.values();
   }

   public Collection getSelectedIds() {
      return (Collection)this.selected.stream().map(Pack::getId).collect(ImmutableSet.toImmutableSet());
   }

   public FeatureFlagSet getRequestedFeatureFlags() {
      return (FeatureFlagSet)this.getSelectedPacks().stream().map(Pack::getRequestedFeatures).reduce(FeatureFlagSet::join).orElse(FeatureFlagSet.of());
   }

   public Collection getSelectedPacks() {
      return this.selected;
   }

   public @Nullable Pack getPack(final String id) {
      return (Pack)this.available.get(id);
   }

   public boolean isAvailable(final String id) {
      return this.available.containsKey(id);
   }

   public List openAllSelected() {
      return (List)this.selected.stream().flatMap(Pack::open).collect(ImmutableList.toImmutableList());
   }
}
