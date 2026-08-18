package net.minecraft.gametest.framework;

import java.util.stream.Stream;

@FunctionalInterface
public interface TestInstanceFinder {
   Stream findTests();
}
