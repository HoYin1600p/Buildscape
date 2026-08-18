package net.minecraft.util.profiling;

import java.util.function.Supplier;
import net.minecraft.util.profiling.metrics.MetricCategory;

public interface ProfilerFiller {
   String ROOT = "root";

   void startTick();

   void endTick();

   void push(String name);

   void push(Supplier name);

   void pop();

   void popPush(String name);

   void popPush(Supplier name);

   default void addZoneText(final String text) {
   }

   default void addZoneValue(final long value) {
   }

   default void setZoneColor(final int color) {
   }

   default Zone zone(final String name) {
      this.push(name);
      return new Zone(this);
   }

   default Zone zone(final Supplier name) {
      this.push(name);
      return new Zone(this);
   }

   void markForCharting(MetricCategory category);

   default void incrementCounter(final String name) {
      this.incrementCounter(name, 1);
   }

   void incrementCounter(String name, int amount);

   default void incrementCounter(final Supplier name) {
      this.incrementCounter(name, 1);
   }

   void incrementCounter(Supplier name, int amount);

   static ProfilerFiller combine(final ProfilerFiller first, final ProfilerFiller second) {
      if (first == InactiveProfiler.INSTANCE) {
         return second;
      } else {
         return (ProfilerFiller)(second == InactiveProfiler.INSTANCE ? first : new ProfilerFiller.CombinedProfileFiller(first, second));
      }
   }

   public static class CombinedProfileFiller implements ProfilerFiller {
      private final ProfilerFiller first;
      private final ProfilerFiller second;

      public CombinedProfileFiller(final ProfilerFiller first, final ProfilerFiller second) {
         this.first = first;
         this.second = second;
      }

      public void startTick() {
         this.first.startTick();
         this.second.startTick();
      }

      public void endTick() {
         this.first.endTick();
         this.second.endTick();
      }

      public void push(final String name) {
         this.first.push(name);
         this.second.push(name);
      }

      public void push(final Supplier name) {
         this.first.push(name);
         this.second.push(name);
      }

      public void markForCharting(final MetricCategory category) {
         this.first.markForCharting(category);
         this.second.markForCharting(category);
      }

      public void pop() {
         this.first.pop();
         this.second.pop();
      }

      public void popPush(final String name) {
         this.first.popPush(name);
         this.second.popPush(name);
      }

      public void popPush(final Supplier name) {
         this.first.popPush(name);
         this.second.popPush(name);
      }

      public void incrementCounter(final String name, final int amount) {
         this.first.incrementCounter(name, amount);
         this.second.incrementCounter(name, amount);
      }

      public void incrementCounter(final Supplier name, final int amount) {
         this.first.incrementCounter(name, amount);
         this.second.incrementCounter(name, amount);
      }

      public void addZoneText(final String text) {
         this.first.addZoneText(text);
         this.second.addZoneText(text);
      }

      public void addZoneValue(final long value) {
         this.first.addZoneValue(value);
         this.second.addZoneValue(value);
      }

      public void setZoneColor(final int color) {
         this.first.setZoneColor(color);
         this.second.setZoneColor(color);
      }
   }
}
