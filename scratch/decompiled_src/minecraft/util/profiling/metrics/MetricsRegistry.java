package net.minecraft.util.profiling.metrics;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;

public class MetricsRegistry {
   public static final MetricsRegistry INSTANCE = new MetricsRegistry();
   private final WeakHashMap measuredInstances = new WeakHashMap();

   private MetricsRegistry() {
   }

   public void add(final ProfilerMeasured profilerMeasured) {
      this.measuredInstances.put(profilerMeasured, (Object)null);
   }

   public List getRegisteredSamplers() {
      Map samplersByName = (Map)this.measuredInstances.keySet().stream().flatMap((measuredInstance) -> measuredInstance.profiledMetrics().stream()).collect(Collectors.groupingBy(MetricSampler::getName));
      return aggregateDuplicates(samplersByName);
   }

   private static List aggregateDuplicates(final Map potentialDuplicates) {
      return (List)potentialDuplicates.entrySet().stream().map((entry) -> {
         String samplerName = (String)entry.getKey();
         List duplicateSamplers = (List)entry.getValue();
         return (MetricSampler)(duplicateSamplers.size() > 1 ? new MetricsRegistry.AggregatedMetricSampler(samplerName, duplicateSamplers) : (MetricSampler)duplicateSamplers.get(0));
      }).collect(Collectors.toList());
   }

   private static class AggregatedMetricSampler extends MetricSampler {
      private final List delegates;

      private AggregatedMetricSampler(final String name, final List delegates) {
         super(name, MetricSampler.SamplingPhase.END_TICK, ((MetricSampler)delegates.get(0)).getCategory(), () -> averageValueFromDelegates(delegates), () -> beforeTick(delegates), thresholdTest(delegates));
         this.delegates = delegates;
      }

      private static MetricSampler.ThresholdTest thresholdTest(final List delegates) {
         return (value) -> delegates.stream().anyMatch((delegate) -> delegate.thresholdTest != null ? delegate.thresholdTest.test(value) : false);
      }

      private static void beforeTick(final List delegates) {
         for(MetricSampler delegate : delegates) {
            delegate.onStartTick();
         }

      }

      private static double averageValueFromDelegates(final List delegates) {
         double aggregatedValue = 0.0D;

         for(MetricSampler delegate : delegates) {
            aggregatedValue += delegate.getSampler().getAsDouble();
         }

         return aggregatedValue / (double)delegates.size();
      }

      public boolean equals(final @Nullable Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            if (!super.equals(o)) {
               return false;
            } else {
               MetricsRegistry.AggregatedMetricSampler that = (MetricsRegistry.AggregatedMetricSampler)o;
               return this.delegates.equals(that.delegates);
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{super.hashCode(), this.delegates});
      }
   }
}
