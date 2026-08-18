package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractOptionSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.ResettableOptionWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public final class OptionInstance {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final OptionInstance.Enum BOOLEAN_VALUES = new OptionInstance.Enum(ImmutableList.of(Boolean.TRUE, Boolean.FALSE), Codec.BOOL);
   public static final OptionInstance.CaptionBasedToString BOOLEAN_TO_STRING = (var0, b) -> b ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
   public static final OptionInstance.ValueUpdateListener NO_ACTION = (var0) -> {
   };
   private final OptionInstance.TooltipSupplier tooltip;
   private final Function toString;
   private final OptionInstance.ValueSet values;
   private final Codec codec;
   private final Object initialValue;
   private final OptionInstance.ValueUpdateListener onValueUpdate;
   private final Component caption;
   private Object value;

   public static OptionInstance createBoolean(final String captionId, final boolean initialValue, final OptionInstance.ValueUpdateListener onValueUpdate) {
      return createBoolean(captionId, noTooltip(), initialValue, onValueUpdate);
   }

   public static OptionInstance createBoolean(final String captionId, final boolean initialValue) {
      return createBoolean(captionId, noTooltip(), initialValue, NO_ACTION);
   }

   public static OptionInstance createBoolean(final String captionId, final OptionInstance.TooltipSupplier tooltip, final boolean initialValue) {
      return createBoolean(captionId, tooltip, initialValue, NO_ACTION);
   }

   public static OptionInstance createBoolean(final String captionId, final OptionInstance.TooltipSupplier tooltip, final boolean initialValue, final OptionInstance.ValueUpdateListener onValueUpdate) {
      return createBoolean(captionId, tooltip, BOOLEAN_TO_STRING, initialValue, onValueUpdate);
   }

   public static OptionInstance createBoolean(final String captionId, final OptionInstance.TooltipSupplier tooltip, final OptionInstance.CaptionBasedToString toString, final boolean initialValue, final OptionInstance.ValueUpdateListener onValueUpdate) {
      return new OptionInstance(captionId, tooltip, toString, BOOLEAN_VALUES, initialValue, onValueUpdate);
   }

   public OptionInstance(final String captionId, final OptionInstance.TooltipSupplier tooltip, final OptionInstance.CaptionBasedToString toString, final OptionInstance.ValueSet values, final Object initialValue, final OptionInstance.ValueUpdateListener onValueUpdate) {
      this(captionId, tooltip, toString, values, values.codec(), initialValue, onValueUpdate);
   }

   public OptionInstance(final String captionId, final OptionInstance.TooltipSupplier tooltip, final OptionInstance.CaptionBasedToString toString, final OptionInstance.ValueSet values, final Codec codec, final Object initialValue, final OptionInstance.ValueUpdateListener onValueUpdate) {
      this.caption = Component.translatable(captionId);
      this.tooltip = tooltip;
      this.toString = (value) -> toString.toString(this.caption, value);
      this.values = values;
      this.codec = codec;
      this.initialValue = initialValue;
      this.onValueUpdate = onValueUpdate;
      this.value = this.initialValue;
   }

   public static OptionInstance.TooltipSupplier noTooltip() {
      return (var0) -> null;
   }

   public static OptionInstance.TooltipSupplier cachedConstantTooltip(final Component tooltipComponent) {
      return (var1) -> Tooltip.create(tooltipComponent);
   }

   public AbstractWidget createButton(final Options options) {
      return this.createButton(options, 0, 0, 150);
   }

   public AbstractWidget createButton(final Options options, final int x, final int y, final int width) {
      return this.createButton(options, x, y, width, NO_ACTION);
   }

   public AbstractWidget createButton(final Options options, final int x, final int y, final int width, final OptionInstance.ValueUpdateListener onValueChanged) {
      return (AbstractWidget)this.values.createButton(this.tooltip, options, x, y, width, onValueChanged).apply(this);
   }

   public Object get() {
      return this.value;
   }

   public Codec codec() {
      return this.codec;
   }

   public String toString() {
      return this.caption.getString();
   }

   public void set(final Object value) {
      Object newValue = (T)this.values.validateValue(value).orElseGet(() -> {
         LOGGER.error("Illegal option value {} for {}", value, this.caption.getString());
         return this.initialValue;
      });
      if (!Minecraft.getInstance().isRunning()) {
         this.value = newValue;
      } else {
         if (!Objects.equals(this.value, newValue)) {
            this.value = newValue;
            this.onValueUpdate.valueChanged(newValue);
         }

      }
   }

   public OptionInstance.ValueSet values() {
      return this.values;
   }

   public static record AltEnum(List values, List altValues, BooleanSupplier altCondition, OptionInstance.CycleableValueSet.ValueSetter valueSetter, Codec codec) implements OptionInstance.CycleableValueSet {
      public CycleButton.ValueListSupplier valueListSupplier() {
         return CycleButton.ValueListSupplier.create(this.altCondition, this.values, this.altValues);
      }

      public Optional validateValue(final Object value) {
         return (this.altCondition.getAsBoolean() ? this.altValues : this.values).contains(value) ? Optional.of(value) : Optional.empty();
      }
   }

   @FunctionalInterface
   public interface CaptionBasedToString {
      Component toString(Component caption, Object value);
   }

   public static record ClampingLazyMaxIntRange(int minInclusive, IntSupplier maxSupplier, int encodableMaxInclusive) implements OptionInstance.IntRangeBase, OptionInstance.SliderableOrCyclableValueSet {
      public Optional validateValue(final Integer value) {
         return Optional.of(Mth.clamp(value, this.minInclusive(), this.maxInclusive()));
      }

      public int maxInclusive() {
         return this.maxSupplier.getAsInt();
      }

      public Codec codec() {
         return Codec.INT.validate((value) -> {
            int maxExclusive = this.encodableMaxInclusive + 1;
            return value.compareTo(this.minInclusive) >= 0 && value.compareTo(maxExclusive) <= 0 ? DataResult.success(value) : DataResult.error(() -> "Value " + value + " outside of range [" + this.minInclusive + ":" + maxExclusive + "]", value);
         });
      }

      public boolean createCycleButton() {
         return true;
      }

      public CycleButton.ValueListSupplier valueListSupplier() {
         return CycleButton.ValueListSupplier.create(IntStream.range(this.minInclusive, this.maxInclusive() + 1).boxed().toList());
      }
   }

   public interface CycleableValueSet extends OptionInstance.ValueSet {
      CycleButton.ValueListSupplier valueListSupplier();

      default OptionInstance.CycleableValueSet.ValueSetter valueSetter() {
         return OptionInstance::set;
      }

      default Function createButton(final OptionInstance.TooltipSupplier tooltip, final Options options, final int x, final int y, final int width, final OptionInstance.ValueUpdateListener onValueChanged) {
         return (instance) -> CycleButton.builder(instance.toString, instance::get).withValues(this.valueListSupplier()).withTooltip(tooltip).create(x, y, width, 20, instance.caption, (var4, value) -> {
               this.valueSetter().set(instance, value);
               options.save();
               onValueChanged.valueChanged(value);
            });
      }

      public interface ValueSetter {
         void set(final OptionInstance instance, final Object value);
      }
   }

   public static record Enum(List values, Codec codec) implements OptionInstance.CycleableValueSet {
      public Optional validateValue(final Object value) {
         return this.values.contains(value) ? Optional.of(value) : Optional.empty();
      }

      public CycleButton.ValueListSupplier valueListSupplier() {
         return CycleButton.ValueListSupplier.create(this.values);
      }
   }

   public static record IntRange(int minInclusive, int maxInclusive, boolean applyValueImmediately) implements OptionInstance.IntRangeBase {
      public IntRange(final int minInclusive, final int maxInclusive) {
         this(minInclusive, maxInclusive, true);
      }

      public Optional validateValue(final Integer value) {
         return value.compareTo(this.minInclusive()) >= 0 && value.compareTo(this.maxInclusive()) <= 0 ? Optional.of(value) : Optional.empty();
      }

      public Codec codec() {
         return Codec.intRange(this.minInclusive, this.maxInclusive + 1);
      }
   }

   public interface IntRangeBase extends OptionInstance.SliderableValueSet {
      int minInclusive();

      int maxInclusive();

      default Optional next(final Integer current) {
         return Optional.of(current + 1);
      }

      default Optional previous(final Integer current) {
         return Optional.of(current - 1);
      }

      default double toSliderValue(final Integer value) {
         if (value == this.minInclusive()) {
            return 0.0D;
         } else {
            return value == this.maxInclusive() ? 1.0D : Mth.map((double)value.intValue() + 0.5D, (double)this.minInclusive(), (double)this.maxInclusive() + 1.0D, 0.0D, 1.0D);
         }
      }

      default Integer fromSliderValue(double slider) {
         if (slider >= 1.0D) {
            slider = (double)0.99999F;
         }

         return Mth.floor(Mth.map(slider, 0.0D, 1.0D, (double)this.minInclusive(), (double)this.maxInclusive() + 1.0D));
      }

      default OptionInstance.SliderableValueSet xmap(final IntFunction to, final ToIntFunction from, final boolean discrete) {
         return new OptionInstance.SliderableValueSet() {
            {
               Objects.requireNonNull(IntRangeBase.this);
            }

            public Optional validateValue(final Object value) {
               return IntRangeBase.this.validateValue(Integer.valueOf(from.applyAsInt(value))).map(to::apply);
            }

            public double toSliderValue(final Object value) {
               return IntRangeBase.this.toSliderValue(from.applyAsInt(value));
            }

            public Optional next(final Object current) {
               if (!discrete) {
                  return Optional.empty();
               } else {
                  int currentIndex = from.applyAsInt(current);
                  return Optional.of(to.apply(IntRangeBase.this.validateValue(Integer.valueOf(currentIndex + 1)).orElse(currentIndex)));
               }
            }

            public Optional previous(final Object current) {
               if (!discrete) {
                  return Optional.empty();
               } else {
                  int currentIndex = from.applyAsInt(current);
                  return Optional.of(to.apply(IntRangeBase.this.validateValue(Integer.valueOf(currentIndex - 1)).orElse(currentIndex)));
               }
            }

            public Object fromSliderValue(final double slider) {
               return to.apply(IntRangeBase.this.fromSliderValue(slider));
            }

            public Codec codec() {
               return IntRangeBase.this.codec().xmap(to::apply, from::applyAsInt);
            }
         };
      }
   }

   public static record LazyEnum(Supplier values, Function validateValue, Codec codec) implements OptionInstance.CycleableValueSet {
      public Optional validateValue(final Object value) {
         return (Optional)this.validateValue.apply(value);
      }

      public CycleButton.ValueListSupplier valueListSupplier() {
         return CycleButton.ValueListSupplier.create((Collection)this.values.get());
      }
   }

   public static final class OptionInstanceSliderButton extends AbstractOptionSliderButton implements ResettableOptionWidget {
      private final OptionInstance instance;
      private final OptionInstance.SliderableValueSet values;
      private final OptionInstance.TooltipSupplier tooltipSupplier;
      private final OptionInstance.ValueUpdateListener onValueChanged;
      private @Nullable Long delayedApplyAt;
      private final boolean applyValueImmediately;

      private OptionInstanceSliderButton(final Options options, final int x, final int y, final int width, final int height, final OptionInstance instance, final OptionInstance.SliderableValueSet values, final OptionInstance.TooltipSupplier tooltipSupplier, final OptionInstance.ValueUpdateListener onValueChanged, final boolean applyValueImmediately) {
         super(options, x, y, width, height, values.toSliderValue(instance.get()));
         this.instance = instance;
         this.values = values;
         this.tooltipSupplier = tooltipSupplier;
         this.onValueChanged = onValueChanged;
         this.applyValueImmediately = applyValueImmediately;
         this.updateMessage();
      }

      protected void updateMessage() {
         this.setMessage((Component)this.instance.toString.apply(this.values.fromSliderValue(this.value)));
         this.setTooltip(this.tooltipSupplier.apply(this.values.fromSliderValue(this.value)));
      }

      protected void applyValue() {
         if (this.applyValueImmediately) {
            this.applyUnsavedValue();
         } else {
            this.delayedApplyAt = Util.getMillis() + 600L;
         }

      }

      public void applyUnsavedValue() {
         Object sliderValue = (N)this.values.fromSliderValue(this.value);
         if (!Objects.equals(sliderValue, this.instance.get())) {
            this.instance.set(sliderValue);
            this.onValueChanged.valueChanged(this.instance.get());
         }

      }

      public void resetValue() {
         if (this.value != this.values.toSliderValue(this.instance.get())) {
            this.value = this.values.toSliderValue(this.instance.get());
            this.delayedApplyAt = null;
            this.updateMessage();
         }

      }

      public void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
         super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
         if (this.delayedApplyAt != null && Util.getMillis() >= this.delayedApplyAt) {
            this.delayedApplyAt = null;
            this.applyUnsavedValue();
            this.resetValue();
         }

      }

      public void onRelease(final MouseButtonEvent event) {
         super.onRelease(event);
         if (this.applyValueImmediately) {
            this.resetValue();
         }

      }

      public boolean keyPressed(final KeyEvent event) {
         if (event.isSelection()) {
            this.canChangeValue = !this.canChangeValue;
            return true;
         } else {
            if (this.canChangeValue) {
               boolean left = event.isLeft();
               boolean right = event.isRight();
               if (left) {
                  Optional previous = this.values.previous(this.values.fromSliderValue(this.value));
                  if (previous.isPresent()) {
                     this.setValue(this.values.toSliderValue(previous.get()));
                     return true;
                  }
               }

               if (right) {
                  Optional next = this.values.next(this.values.fromSliderValue(this.value));
                  if (next.isPresent()) {
                     this.setValue(this.values.toSliderValue(next.get()));
                     return true;
                  }
               }

               if (left || right) {
                  float direction = left ? -1.0F : 1.0F;
                  this.setValue(this.value + (double)(direction / (float)(this.width - 8)));
                  return true;
               }
            }

            return false;
         }
      }
   }

   public static record SliderableEnum(List values, Codec codec) implements OptionInstance.SliderableValueSet {
      public double toSliderValue(final Object value) {
         if (value == this.values.getFirst()) {
            return 0.0D;
         } else {
            return value == this.values.getLast() ? 1.0D : Mth.map((double)this.values.indexOf(value), 0.0D, (double)(this.values.size() - 1), 0.0D, 1.0D);
         }
      }

      public Optional next(final Object current) {
         int currentIntex = this.values.indexOf(current);
         int nextIndex = Mth.clamp(currentIntex + 1, 0, this.values.size() - 1);
         return Optional.of(this.values.get(nextIndex));
      }

      public Optional previous(final Object current) {
         int currentIntex = this.values.indexOf(current);
         int previousIndex = Mth.clamp(currentIntex - 1, 0, this.values.size() - 1);
         return Optional.of(this.values.get(previousIndex));
      }

      public Object fromSliderValue(double slider) {
         if (slider >= 1.0D) {
            slider = (double)0.99999F;
         }

         int index = Mth.floor(Mth.map(slider, 0.0D, 1.0D, 0.0D, (double)this.values.size()));
         return this.values.get(Mth.clamp(index, 0, this.values.size() - 1));
      }

      public Optional validateValue(final Object value) {
         int index = this.values.indexOf(value);
         return index > -1 ? Optional.of(value) : Optional.empty();
      }
   }

   public interface SliderableOrCyclableValueSet extends OptionInstance.SliderableValueSet, OptionInstance.CycleableValueSet {
      boolean createCycleButton();

      default Function createButton(final OptionInstance.TooltipSupplier tooltip, final Options options, final int x, final int y, final int width, final OptionInstance.ValueUpdateListener onValueChanged) {
         return this.createCycleButton() ? OptionInstance.CycleableValueSet.super.createButton(tooltip, options, x, y, width, onValueChanged) : OptionInstance.SliderableValueSet.super.createButton(tooltip, options, x, y, width, onValueChanged);
      }
   }

   public interface SliderableValueSet extends OptionInstance.ValueSet {
      double toSliderValue(final Object value);

      default Optional next(final Object current) {
         return Optional.empty();
      }

      default Optional previous(final Object current) {
         return Optional.empty();
      }

      Object fromSliderValue(final double slider);

      default boolean applyValueImmediately() {
         return true;
      }

      default Function createButton(final OptionInstance.TooltipSupplier tooltip, final Options options, final int x, final int y, final int width, final OptionInstance.ValueUpdateListener onValueChanged) {
         return (instance) -> new OptionInstance.OptionInstanceSliderButton(options, x, y, width, 20, instance, this, tooltip, onValueChanged, this.applyValueImmediately());
      }
   }

   @FunctionalInterface
   public interface TooltipSupplier {
      @Nullable Tooltip apply(Object value);
   }

   public static enum UnitDouble implements OptionInstance.SliderableValueSet {
      INSTANCE;

      public Optional validateValue(final Double value) {
         return value >= 0.0D && value <= 1.0D ? Optional.of(value) : Optional.empty();
      }

      public double toSliderValue(final Double value) {
         return value;
      }

      public Double fromSliderValue(final double slider) {
         return slider;
      }

      public OptionInstance.SliderableValueSet xmap(final DoubleFunction to, final ToDoubleFunction from) {
         return new OptionInstance.SliderableValueSet() {
            {
               Objects.requireNonNull(UnitDouble.this);
            }

            public Optional validateValue(final Object value) {
               return UnitDouble.this.validateValue(from.applyAsDouble(value)).map(to::apply);
            }

            public double toSliderValue(final Object value) {
               return UnitDouble.this.toSliderValue(from.applyAsDouble(value));
            }

            public Object fromSliderValue(final double slider) {
               return to.apply(UnitDouble.this.fromSliderValue(slider));
            }

            public Codec codec() {
               return UnitDouble.this.codec().xmap(to::apply, from::applyAsDouble);
            }
         };
      }

      public Codec codec() {
         return Codec.withAlternative(Codec.doubleRange(0.0D, 1.0D), Codec.BOOL, (b) -> b ? 1.0D : 0.0D);
      }

      // $FF: synthetic method
      private static OptionInstance.UnitDouble[] $values() {
         return new OptionInstance.UnitDouble[]{INSTANCE};
      }
   }

   public interface ValueSet {
      Function createButton(final OptionInstance.TooltipSupplier tooltip, Options options, final int x, final int y, final int width, final OptionInstance.ValueUpdateListener onValueChanged);

      Optional validateValue(final Object value);

      Codec codec();
   }

   @FunctionalInterface
   public interface ValueUpdateListener {
      void valueChanged(Object newValue);
   }
}
