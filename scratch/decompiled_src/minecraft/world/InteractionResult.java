package net.minecraft.world;

import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public sealed interface InteractionResult {
   InteractionResult.Success SUCCESS = new InteractionResult.Success(InteractionResult.SwingSource.PREDICTED, InteractionResult.ItemContext.DEFAULT);
   InteractionResult.Success SUCCESS_SERVER = new InteractionResult.Success(InteractionResult.SwingSource.SERVER_ONLY, InteractionResult.ItemContext.DEFAULT);
   InteractionResult.Success CONSUME = new InteractionResult.Success(InteractionResult.SwingSource.NONE, InteractionResult.ItemContext.DEFAULT);
   InteractionResult.Fail FAIL = new InteractionResult.Fail();
   InteractionResult.Pass PASS = new InteractionResult.Pass();
   InteractionResult.TryEmptyHandInteraction TRY_WITH_EMPTY_HAND = new InteractionResult.TryEmptyHandInteraction();

   default boolean consumesAction() {
      return false;
   }

   public static record Fail() implements InteractionResult {
   }

   public static record ItemContext(boolean wasItemInteraction, @Nullable ItemStack heldItemTransformedTo) {
      public static final InteractionResult.ItemContext NONE = new InteractionResult.ItemContext(false, (ItemStack)null);
      public static final InteractionResult.ItemContext DEFAULT = new InteractionResult.ItemContext(true, (ItemStack)null);
   }

   public static record Pass() implements InteractionResult {
   }

   public static record Success(InteractionResult.SwingSource swingSource, InteractionResult.ItemContext itemContext) implements InteractionResult {
      public boolean shouldSwing() {
         return this.swingSource != InteractionResult.SwingSource.NONE;
      }

      public boolean consumesAction() {
         return true;
      }

      public InteractionResult.Success heldItemTransformedTo(final ItemStack itemStack) {
         return new InteractionResult.Success(this.swingSource, new InteractionResult.ItemContext(true, itemStack));
      }

      public InteractionResult.Success withoutItem() {
         return new InteractionResult.Success(this.swingSource, InteractionResult.ItemContext.NONE);
      }

      public boolean wasItemInteraction() {
         return this.itemContext.wasItemInteraction;
      }

      public @Nullable ItemStack heldItemTransformedTo() {
         return this.itemContext.heldItemTransformedTo;
      }
   }

   public static enum SwingSource {
      NONE,
      PREDICTED,
      SERVER_ONLY;

      // $FF: synthetic method
      private static InteractionResult.SwingSource[] $values() {
         return new InteractionResult.SwingSource[]{NONE, PREDICTED, SERVER_ONLY};
      }
   }

   public static record TryEmptyHandInteraction() implements InteractionResult {
   }
}
