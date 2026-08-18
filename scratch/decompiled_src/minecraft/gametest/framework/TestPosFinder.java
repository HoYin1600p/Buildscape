package net.minecraft.gametest.framework;

import java.util.stream.Stream;

@FunctionalInterface
public interface TestPosFinder {
   Stream findTestPos();
}
