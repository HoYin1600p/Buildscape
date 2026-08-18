package net.minecraft.client.renderer.block.dispatch;

import java.util.function.UnaryOperator;

@FunctionalInterface
public interface VariantMutator extends UnaryOperator {
   VariantMutator.VariantProperty X_ROT = Variant::withXRot;
   VariantMutator.VariantProperty Y_ROT = Variant::withYRot;
   VariantMutator.VariantProperty Z_ROT = Variant::withZRot;
   VariantMutator.VariantProperty MODEL = Variant::withModel;
   VariantMutator.VariantProperty UV_LOCK = Variant::withUvLock;

   default VariantMutator then(final VariantMutator other) {
      return (variant) -> (Variant)other.apply((Variant)this.apply(variant));
   }

   @FunctionalInterface
   public interface VariantProperty {
      Variant apply(Variant input, Object value);

      default VariantMutator withValue(final Object value) {
         return (variant) -> this.apply(variant, value);
      }
   }
}
