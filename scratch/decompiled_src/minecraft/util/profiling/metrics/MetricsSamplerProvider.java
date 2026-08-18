package net.minecraft.util.profiling.metrics;

import java.util.Set;
import java.util.function.Supplier;

public interface MetricsSamplerProvider {
   Set samplers(final Supplier singleTickProfiler);
}
