package net.minecraft.client.gui.components;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

public class CycleButton extends AbstractButton implements ResettableOptionWidget {
   public static final BooleanSupplier DEFAULT_ALT_LIST_SELECTOR = () -> Minecraft.getInstance().hasAltDown();
   private static final List BOOLEAN_OPTIONS = ImmutableList.of(Boolean.TRUE, Boolean.FALSE);
   private final Supplier defaultValueSupplier;
   private final Component name;
   private int index;
   private Object value;
   private final CycleButton.ValueListSupplier values;
   private final Function valueStringifier;
   private final Function narrationProvider;
   private final CycleButton.OnValueChange onValueChange;
   private final CycleButton.DisplayState displayState;
   private final OptionInstance.TooltipSupplier tooltipSupplier;
   private final CycleButton.SpriteSupplier spriteSupplier;

   private CycleButton(final int x, final int y, final int width, final int height, final Component message, final Component name, final int index, final Object value, final Supplier defaultValueSupplier, final CycleButton.ValueListSupplier values, final Function valueStringifier, final Function narrationProvider, final CycleButton.OnValueChange onValueChange, final OptionInstance.TooltipSupplier tooltipSupplier, final CycleButton.DisplayState displayState, final CycleButton.SpriteSupplier spriteSupplier) {
      super(x, y, width, height, message);
      this.name = name;
      this.index = index;
      this.defaultValueSupplier = defaultValueSupplier;
      this.value = value;
      this.values = values;
      this.valueStringifier = valueStringifier;
      this.narrationProvider = narrationProvider;
      this.onValueChange = onValueChange;
      this.displayState = displayState;
      this.tooltipSupplier = tooltipSupplier;
      this.spriteSupplier = spriteSupplier;
      this.updateTooltip();
   }

   protected void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      Identifier sprite = this.spriteSupplier.apply(this, this.getValue());
      if (sprite != null) {
         graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, this.getX(), this.getY(), this.getWidth(), this.getHeight());
      } else {
         this.extractDefaultSprite(graphics);
      }

      if (this.displayState != CycleButton.DisplayState.HIDE) {
         this.extractDefaultLabel(graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
      }

   }

   private void updateTooltip() {
      this.setTooltip(this.tooltipSupplier.apply(this.value));
   }

   public void onPress(final InputWithModifiers input) {
      if (input.hasShiftDown()) {
         this.cycleValue(-1);
      } else {
         this.cycleValue(1);
      }

   }

   private void cycleValue(final int delta) {
      List list = this.values.getSelectedList();
      this.index = Mth.positiveModulo(this.index + delta, list.size());
      Object newValue = (T)list.get(this.index);
      this.updateValue(newValue);
      this.onValueChange.onValueChange(this, newValue);
   }

   private Object getCycledValue(final int delta) {
      List list = this.values.getSelectedList();
      return list.get(Mth.positiveModulo(this.index + delta, list.size()));
   }

   public boolean mouseScrolled(final double x, final double y, final double scrollX, final double scrollY) {
      if (scrollY > 0.0D) {
         this.cycleValue(-1);
      } else if (scrollY < 0.0D) {
         this.cycleValue(1);
      }

      return true;
   }

   public void setValue(final Object newValue) {
      List list = this.values.getSelectedList();
      int newIndex = list.indexOf(newValue);
      if (newIndex != -1) {
         this.index = newIndex;
      }

      this.updateValue(newValue);
   }

   public void resetValue() {
      this.setValue(this.defaultValueSupplier.get());
   }

   private void updateValue(final Object newValue) {
      Component newMessage = this.createLabelForValue(newValue);
      this.setMessage(newMessage);
      this.value = newValue;
      this.updateTooltip();
   }

   private Component createLabelForValue(final Object newValue) {
      return (Component)(this.displayState == CycleButton.DisplayState.VALUE ? (Component)this.valueStringifier.apply(newValue) : this.createFullName(newValue));
   }

   private MutableComponent createFullName(final Object newValue) {
      return CommonComponents.optionNameValue(this.name, (Component)this.valueStringifier.apply(newValue));
   }

   public Object getValue() {
      return this.value;
   }

   protected MutableComponent createNarrationMessage() {
      return (MutableComponent)this.narrationProvider.apply(this);
   }

   public void updateWidgetNarration(final NarrationElementOutput output) {
      output.add(NarratedElementType.TITLE, (Component)this.createNarrationMessage());
      if (this.active) {
         Object nextValue = (T)this.getCycledValue(1);
         Component nextValueText = this.createLabelForValue(nextValue);
         if (this.isFocused()) {
            output.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.cycle_button.usage.focused", nextValueText));
         } else {
            output.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.cycle_button.usage.hovered", nextValueText));
         }
      }

   }

   public MutableComponent createDefaultNarrationMessage() {
      return wrapDefaultNarrationMessage((Component)(this.displayState == CycleButton.DisplayState.VALUE ? this.createFullName(this.value) : this.getMessage()));
   }

   public static CycleButton.Builder builder(final Function valueStringifier, final Supplier defaultValueSupplier) {
      return new CycleButton.Builder(valueStringifier, defaultValueSupplier);
   }

   public static CycleButton.Builder builder(final Function valueStringifier, final Object defaultValue) {
      return new CycleButton.Builder(valueStringifier, () -> defaultValue);
   }

   public static CycleButton.Builder booleanBuilder(final Component trueText, final Component falseText, final boolean defaultValue) {
      return (new CycleButton.Builder((b) -> b == Boolean.TRUE ? trueText : falseText, () -> defaultValue)).withValues(BOOLEAN_OPTIONS);
   }

   public static CycleButton.Builder onOffBuilder(final boolean initialValue) {
      return (new CycleButton.Builder((b) -> b == Boolean.TRUE ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF, () -> initialValue)).withValues(BOOLEAN_OPTIONS);
   }

   public static class Builder {
      private final Supplier defaultValueSupplier;
      private final Function valueStringifier;
      private OptionInstance.TooltipSupplier tooltipSupplier = (value) -> null;
      private CycleButton.SpriteSupplier spriteSupplier = (button, value) -> null;
      private Function narrationProvider = CycleButton::createDefaultNarrationMessage;
      private CycleButton.ValueListSupplier values = CycleButton.ValueListSupplier.create(ImmutableList.of());
      private CycleButton.DisplayState displayState = CycleButton.DisplayState.NAME_AND_VALUE;

      public Builder(final Function valueStringifier, final Supplier defaultValueSupplier) {
         this.valueStringifier = valueStringifier;
         this.defaultValueSupplier = defaultValueSupplier;
      }

      public CycleButton.Builder withValues(final Collection values) {
         return this.withValues(CycleButton.ValueListSupplier.create(values));
      }

      @SafeVarargs
      public final CycleButton.Builder withValues(final Object... values) {
         return this.withValues(ImmutableList.copyOf(values));
      }

      public CycleButton.Builder withValues(final List values, final List altValues) {
         return this.withValues(CycleButton.ValueListSupplier.create(CycleButton.DEFAULT_ALT_LIST_SELECTOR, values, altValues));
      }

      public CycleButton.Builder withValues(final BooleanSupplier altCondition, final List values, final List altValues) {
         return this.withValues(CycleButton.ValueListSupplier.create(altCondition, values, altValues));
      }

      public CycleButton.Builder withValues(final CycleButton.ValueListSupplier valueListSupplier) {
         this.values = valueListSupplier;
         return this;
      }

      public CycleButton.Builder withTooltip(final OptionInstance.TooltipSupplier tooltipSupplier) {
         this.tooltipSupplier = tooltipSupplier;
         return this;
      }

      public CycleButton.Builder withCustomNarration(final Function narrationProvider) {
         this.narrationProvider = narrationProvider;
         return this;
      }

      public CycleButton.Builder withSprite(final CycleButton.SpriteSupplier spriteSupplier) {
         this.spriteSupplier = spriteSupplier;
         return this;
      }

      public CycleButton.Builder displayState(final CycleButton.DisplayState state) {
         this.displayState = state;
         return this;
      }

      public CycleButton.Builder displayOnlyValue() {
         return this.displayState(CycleButton.DisplayState.VALUE);
      }

      public CycleButton create(final Component name, final CycleButton.OnValueChange valueChangeListener) {
         return this.create(0, 0, 150, 20, name, valueChangeListener);
      }

      public CycleButton create(final int x, final int y, final int width, final int height, final Component name) {
         return this.create(x, y, width, height, name, (button, value) -> {
         });
      }

      public CycleButton create(final int x, final int y, final int width, final int height, final Component name, final CycleButton.OnValueChange valueChangeListener) {
         List values = this.values.getDefaultList();
         if (values.isEmpty()) {
            throw new IllegalStateException("No values for cycle button");
         } else {
            Object initialValue = (T)this.defaultValueSupplier.get();
            int initialIndex = values.indexOf(initialValue);
            Component valueText = (Component)this.valueStringifier.apply(initialValue);
            Component initialTitle = (Component)(this.displayState == CycleButton.DisplayState.VALUE ? valueText : CommonComponents.optionNameValue(name, valueText));
            return new CycleButton(x, y, width, height, initialTitle, name, initialIndex, initialValue, this.defaultValueSupplier, this.values, this.valueStringifier, this.narrationProvider, valueChangeListener, this.tooltipSupplier, this.displayState, this.spriteSupplier);
         }
      }
   }

   public static enum DisplayState {
      NAME_AND_VALUE,
      VALUE,
      HIDE;

      // $FF: synthetic method
      private static CycleButton.DisplayState[] $values() {
         return new CycleButton.DisplayState[]{NAME_AND_VALUE, VALUE, HIDE};
      }
   }

   @FunctionalInterface
   public interface OnValueChange {
      void onValueChange(CycleButton button, Object value);
   }

   @FunctionalInterface
   public interface SpriteSupplier {
      @Nullable Identifier apply(CycleButton button, Object value);
   }

   public interface ValueListSupplier {
      List getSelectedList();

      List getDefaultList();

      static CycleButton.ValueListSupplier create(final Collection values) {
         final List copy = ImmutableList.copyOf(values);
         return new CycleButton.ValueListSupplier() {
            public List getSelectedList() {
               return copy;
            }

            public List getDefaultList() {
               return copy;
            }
         };
      }

      static CycleButton.ValueListSupplier create(final BooleanSupplier altSelector, final List defaultList, final List altList) {
         final List defaultCopy = ImmutableList.copyOf(defaultList);
         final List altCopy = ImmutableList.copyOf(altList);
         return new CycleButton.ValueListSupplier() {
            public List getSelectedList() {
               return altSelector.getAsBoolean() ? altCopy : defaultCopy;
            }

            public List getDefaultList() {
               return defaultCopy;
            }
         };
      }
   }
}
