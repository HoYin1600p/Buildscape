package net.minecraft.client.gui.screens.packs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;

public class PackSelectionModel {
   private final PackRepository repository;
   private final List selected;
   private final List unselected;
   private final Function iconGetter;
   private final Consumer onListChanged;
   private final Consumer output;

   public PackSelectionModel(final Consumer onListChanged, final Function iconGetter, final PackRepository repository, final Consumer output) {
      this.onListChanged = onListChanged;
      this.iconGetter = iconGetter;
      this.repository = repository;
      this.selected = Lists.newArrayList(repository.getSelectedPacks());
      Collections.reverse(this.selected);
      this.unselected = Lists.newArrayList(repository.getAvailablePacks());
      this.unselected.removeAll(this.selected);
      this.output = output;
   }

   public Stream getUnselected() {
      return this.unselected.stream().map((x$0) -> new PackSelectionModel.UnselectedPackEntry(x$0));
   }

   public Stream getSelected() {
      return this.selected.stream().map((x$0) -> new PackSelectionModel.SelectedPackEntry(x$0));
   }

   private void updateRepoSelectedList() {
      this.repository.setSelected((Collection)Lists.reverse(this.selected).stream().map(Pack::getId).collect(ImmutableList.toImmutableList()));
   }

   public void commit() {
      this.updateRepoSelectedList();
      this.output.accept(this.repository);
   }

   public void findNewPacks() {
      this.repository.reload();
      this.selected.retainAll(this.repository.getAvailablePacks());
      this.unselected.clear();
      this.unselected.addAll(this.repository.getAvailablePacks());
      this.unselected.removeAll(this.selected);
   }

   public interface Entry {
      Identifier getIconTexture();

      PackCompatibility getCompatibility();

      String getId();

      Component getTitle();

      Component getDescription();

      PackSource getPackSource();

      default Component getExtendedDescription() {
         return this.getPackSource().decorate(this.getDescription());
      }

      boolean isFixedPosition();

      boolean isRequired();

      void select();

      void unselect();

      void moveUp();

      void moveDown();

      boolean isSelected();

      default boolean canSelect() {
         return !this.isSelected();
      }

      default boolean canUnselect() {
         return this.isSelected() && !this.isRequired();
      }

      boolean canMoveUp();

      boolean canMoveDown();
   }

   public abstract class EntryBase implements PackSelectionModel.Entry {
      private final Pack pack;

      public EntryBase(final Pack pack) {
         Objects.requireNonNull(PackSelectionModel.this);
         super();
         this.pack = pack;
      }

      protected abstract List getSelfList();

      protected abstract List getOtherList();

      public Identifier getIconTexture() {
         return (Identifier)PackSelectionModel.this.iconGetter.apply(this.pack);
      }

      public PackCompatibility getCompatibility() {
         return this.pack.getCompatibility();
      }

      public String getId() {
         return this.pack.getId();
      }

      public Component getTitle() {
         return this.pack.getTitle();
      }

      public Component getDescription() {
         return this.pack.getDescription();
      }

      public PackSource getPackSource() {
         return this.pack.getPackSource();
      }

      public boolean isFixedPosition() {
         return this.pack.isFixedPosition();
      }

      public boolean isRequired() {
         return this.pack.isRequired();
      }

      protected void toggleSelection() {
         this.getSelfList().remove(this.pack);
         this.pack.getDefaultPosition().insert(this.getOtherList(), this.pack, Pack::selectionConfig, true);
         PackSelectionModel.this.onListChanged.accept(this);
         PackSelectionModel.this.updateRepoSelectedList();
         this.updateHighContrastOptionInstance();
      }

      private void updateHighContrastOptionInstance() {
         if (this.pack.getId().equals("high_contrast")) {
            OptionInstance highContrastMode = Minecraft.getInstance().options.highContrast();
            highContrastMode.set(!highContrastMode.get());
         }

      }

      protected void move(final int direction) {
         List list = this.getSelfList();
         int currentPos = list.indexOf(this.pack);
         list.remove(currentPos);
         list.add(currentPos + direction, this.pack);
         PackSelectionModel.this.onListChanged.accept(this);
      }

      public boolean canMoveUp() {
         List list = this.getSelfList();
         int index = list.indexOf(this.pack);
         return index > 0 && !((Pack)list.get(index - 1)).isFixedPosition();
      }

      public void moveUp() {
         this.move(-1);
      }

      public boolean canMoveDown() {
         List list = this.getSelfList();
         int index = list.indexOf(this.pack);
         return index >= 0 && index < list.size() - 1 && !((Pack)list.get(index + 1)).isFixedPosition();
      }

      public void moveDown() {
         this.move(1);
      }
   }

   private class SelectedPackEntry extends PackSelectionModel.EntryBase {
      public SelectedPackEntry(final Pack pack) {
         Objects.requireNonNull(PackSelectionModel.this);
         super(pack);
      }

      protected List getSelfList() {
         return PackSelectionModel.this.selected;
      }

      protected List getOtherList() {
         return PackSelectionModel.this.unselected;
      }

      public boolean isSelected() {
         return true;
      }

      public void select() {
      }

      public void unselect() {
         this.toggleSelection();
      }
   }

   private class UnselectedPackEntry extends PackSelectionModel.EntryBase {
      public UnselectedPackEntry(final Pack pack) {
         Objects.requireNonNull(PackSelectionModel.this);
         super(pack);
      }

      protected List getSelfList() {
         return PackSelectionModel.this.unselected;
      }

      protected List getOtherList() {
         return PackSelectionModel.this.selected;
      }

      public boolean isSelected() {
         return false;
      }

      public void select() {
         this.toggleSelection();
      }

      public void unselect() {
      }
   }
}
