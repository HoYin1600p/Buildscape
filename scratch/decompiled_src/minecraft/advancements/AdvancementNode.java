package net.minecraft.advancements;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Set;
import org.jspecify.annotations.Nullable;

public class AdvancementNode {
   private final AdvancementHolder holder;
   private final @Nullable AdvancementNode parent;
   private final Set children = new ReferenceOpenHashSet();

   @VisibleForTesting
   public AdvancementNode(final AdvancementHolder holder, final @Nullable AdvancementNode parent) {
      this.holder = holder;
      this.parent = parent;
   }

   public Advancement advancement() {
      return this.holder.value();
   }

   public AdvancementHolder holder() {
      return this.holder;
   }

   public @Nullable AdvancementNode parent() {
      return this.parent;
   }

   public AdvancementNode root() {
      return getRoot(this);
   }

   public static AdvancementNode getRoot(final AdvancementNode advancement) {
      AdvancementNode root = advancement;

      while(true) {
         AdvancementNode parent = root.parent();
         if (parent == null) {
            return root;
         }

         root = parent;
      }
   }

   public Iterable children() {
      return this.children;
   }

   @VisibleForTesting
   public void addChild(final AdvancementNode child) {
      this.children.add(child);
   }

   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      } else {
         if (obj instanceof AdvancementNode) {
            AdvancementNode that = (AdvancementNode)obj;
            if (this.holder.equals(that.holder)) {
               return true;
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return this.holder.hashCode();
   }

   public String toString() {
      return this.holder.id().toString();
   }
}
