package net.minecraft.client.gui.screens.dialog.input;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dialog.action.Action;
import net.minecraft.server.dialog.input.BooleanInput;
import net.minecraft.server.dialog.input.InputControl;
import net.minecraft.server.dialog.input.NumberRangeInput;
import net.minecraft.server.dialog.input.SingleOptionInput;
import net.minecraft.server.dialog.input.TextInput;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class InputControlHandlers {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map HANDLERS = new HashMap();

   private static void register(final MapCodec type, final InputControlHandler handler) {
      HANDLERS.put(type, handler);
   }

   private static @Nullable InputControlHandler get(final InputControl inputControl) {
      return (InputControlHandler)HANDLERS.get(inputControl.mapCodec());
   }

   public static void createHandler(final InputControl inputControl, final Screen screen, final InputControlHandler.Output outputConsumer) {
      InputControlHandler handler = get(inputControl);
      if (handler == null) {
         LOGGER.warn("Unrecognized input control {}", inputControl);
      } else {
         handler.addControl(inputControl, screen, outputConsumer);
      }
   }

   public static void bootstrap() {
      register(TextInput.MAP_CODEC, new InputControlHandlers.TextInputHandler());
      register(SingleOptionInput.MAP_CODEC, new InputControlHandlers.SingleOptionHandler());
      register(BooleanInput.MAP_CODEC, new InputControlHandlers.BooleanHandler());
      register(NumberRangeInput.MAP_CODEC, new InputControlHandlers.NumberRangeHandler());
   }

   private static class BooleanHandler implements InputControlHandler {
      public void addControl(final BooleanInput input, final Screen screen, final InputControlHandler.Output output) {
         Font font = screen.getFont();
         final Checkbox control = Checkbox.builder(input.label(), font).selected(input.initial()).build();
         output.accept(control, new Action.ValueGetter(this) {
            {
               Objects.requireNonNull(this$0);
            }

            public String asTemplateSubstitution() {
               return control.selected() ? input.onTrue() : input.onFalse();
            }

            public Tag asTag() {
               return ByteTag.valueOf(control.selected());
            }
         });
      }
   }

   private static class NumberRangeHandler implements InputControlHandler {
      public void addControl(final NumberRangeInput input, final Screen screen, final InputControlHandler.Output output) {
         float initialValue = input.rangeInfo().initialSliderValue();
         final InputControlHandlers.NumberRangeHandler.SliderImpl control = new InputControlHandlers.NumberRangeHandler.SliderImpl(input, (double)initialValue);
         output.accept(control, new Action.ValueGetter(this) {
            {
               Objects.requireNonNull(this$0);
            }

            public String asTemplateSubstitution() {
               return control.stringValueToSend();
            }

            public Tag asTag() {
               return FloatTag.valueOf(control.floatValueToSend());
            }
         });
      }

      private static class SliderImpl extends AbstractSliderButton {
         private final NumberRangeInput input;

         private SliderImpl(final NumberRangeInput input, final double initialSliderValue) {
            super(0, 0, input.width(), 20, computeMessage(input, initialSliderValue), initialSliderValue);
            this.input = input;
         }

         protected void updateMessage() {
            this.setMessage(computeMessage(this.input, this.value));
         }

         protected void applyValue() {
         }

         public String stringValueToSend() {
            return sliderValueToString(this.input, this.value);
         }

         public float floatValueToSend() {
            return scaledValue(this.input, this.value);
         }

         private static float scaledValue(final NumberRangeInput input, final double sliderValue) {
            return input.rangeInfo().computeScaledValue((float)sliderValue);
         }

         private static String sliderValueToString(final NumberRangeInput input, final double sliderValue) {
            return valueToString(scaledValue(input, sliderValue));
         }

         private static Component computeMessage(final NumberRangeInput input, final double sliderValue) {
            return input.computeLabel(sliderValueToString(input, sliderValue));
         }

         private static String valueToString(final float v) {
            int intV = (int)v;
            return (float)intV == v ? Integer.toString(intV) : Float.toString(v);
         }
      }
   }

   private static class SingleOptionHandler implements InputControlHandler {
      public void addControl(final SingleOptionInput input, final Screen screen, final InputControlHandler.Output output) {
         SingleOptionInput.Entry initial = (SingleOptionInput.Entry)input.initial().orElse((SingleOptionInput.Entry)input.entries().getFirst());
         CycleButton.Builder controlBuilder = CycleButton.builder(SingleOptionInput.Entry::displayOrDefault, initial).withValues(input.entries()).displayState(!input.labelVisible() ? CycleButton.DisplayState.VALUE : CycleButton.DisplayState.NAME_AND_VALUE);
         CycleButton control = controlBuilder.create(0, 0, input.width(), 20, input.label());
         output.accept(control, Action.ValueGetter.of((Supplier)(() -> ((SingleOptionInput.Entry)control.getValue()).id())));
      }
   }

   private static class TextInputHandler implements InputControlHandler {
      public void addControl(final TextInput input, final Screen screen, final InputControlHandler.Output output) {
         Font font = screen.getFont();
         LayoutElement control;
         final Supplier getter;
         if (input.multiline().isPresent()) {
            TextInput.MultilineOptions multiline = (TextInput.MultilineOptions)input.multiline().get();
            int computedHeight = multiline.height().orElseGet(() -> {
               int lineCountToFit = multiline.maxLines().orElse(4);
               return Math.min(9 * lineCountToFit + 8, 512);
            });
            MultiLineEditBox editBox = MultiLineEditBox.builder().build(font, input.width(), computedHeight, CommonComponents.EMPTY);
            editBox.setCharacterLimit(input.maxLength());
            multiline.maxLines().ifPresent(editBox::setLineLimit);
            editBox.setValue(input.initial());
            control = editBox;
            getter = editBox::getValue;
         } else {
            EditBox editBox = new EditBox(font, input.width(), 20, input.label());
            editBox.setMaxLength(input.maxLength());
            editBox.setValue(input.initial());
            control = editBox;
            getter = editBox::getValue;
         }

         LayoutElement wrappedControl = (LayoutElement)(input.labelVisible() ? CommonLayouts.labeledElement(font, control, input.label()) : control);
         output.accept(wrappedControl, new Action.ValueGetter(this) {
            {
               Objects.requireNonNull(this$0);
            }

            public String asTemplateSubstitution() {
               return StringTag.escapeWithoutQuotes((String)getter.get());
            }

            public Tag asTag() {
               return StringTag.valueOf((String)getter.get());
            }
         });
      }
   }
}
