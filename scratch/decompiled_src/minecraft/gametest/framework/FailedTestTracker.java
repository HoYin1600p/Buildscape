package net.minecraft.gametest.framework;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.core.Holder;

public class FailedTestTracker {
   private static final Set LAST_FAILED_TESTS = Sets.newHashSet();

   public static Stream getLastFailedTests() {
      return LAST_FAILED_TESTS.stream();
   }

   public static void rememberFailedTest(final Holder.Reference test) {
      LAST_FAILED_TESTS.add(test);
   }

   public static void forgetFailedTests() {
      LAST_FAILED_TESTS.clear();
   }
}
