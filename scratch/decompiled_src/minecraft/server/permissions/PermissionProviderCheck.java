package net.minecraft.server.permissions;

import java.util.function.Predicate;

public record PermissionProviderCheck(PermissionCheck test) implements Predicate {
   public boolean test(final PermissionSetSupplier t) {
      return this.test.check(t.permissions());
   }
}
