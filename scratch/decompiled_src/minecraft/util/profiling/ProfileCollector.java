package net.minecraft.util.profiling;

import java.util.Set;
import org.jspecify.annotations.Nullable;

public interface ProfileCollector extends ProfilerFiller {
   ProfileResults getResults();

   ActiveProfiler.@Nullable PathEntry getEntry(final String path);

   Set getChartedPaths();
}
