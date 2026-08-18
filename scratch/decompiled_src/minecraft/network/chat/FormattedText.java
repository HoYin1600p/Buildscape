package net.minecraft.network.chat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.Unit;

public interface FormattedText {
   Optional STOP_ITERATION = Optional.of(Unit.INSTANCE);
   FormattedText EMPTY = new FormattedText() {
      public Optional visit(final FormattedText.ContentConsumer output) {
         return Optional.empty();
      }

      public Optional visit(final FormattedText.StyledContentConsumer output, final Style parentStyle) {
         return Optional.empty();
      }
   };

   Optional visit(final FormattedText.ContentConsumer output);

   Optional visit(final FormattedText.StyledContentConsumer output, final Style parentStyle);

   static FormattedText of(final String text) {
      return new FormattedText() {
         public Optional visit(final FormattedText.ContentConsumer output) {
            return output.accept(text);
         }

         public Optional visit(final FormattedText.StyledContentConsumer output, final Style parentStyle) {
            return output.accept(parentStyle, text);
         }
      };
   }

   static FormattedText of(final String text, final Style style) {
      return new FormattedText() {
         public Optional visit(final FormattedText.ContentConsumer output) {
            return output.accept(text);
         }

         public Optional visit(final FormattedText.StyledContentConsumer output, final Style parentStyle) {
            return output.accept(style.applyTo(parentStyle), text);
         }
      };
   }

   static FormattedText composite(final FormattedText... parts) {
      return composite(ImmutableList.copyOf(parts));
   }

   static FormattedText composite(final List parts) {
      return new FormattedText() {
         public Optional visit(final FormattedText.ContentConsumer output) {
            for(FormattedText part : parts) {
               Optional result = part.visit(output);
               if (result.isPresent()) {
                  return result;
               }
            }

            return Optional.empty();
         }

         public Optional visit(final FormattedText.StyledContentConsumer output, final Style parentStyle) {
            for(FormattedText part : parts) {
               Optional result = part.visit(output, parentStyle);
               if (result.isPresent()) {
                  return result;
               }
            }

            return Optional.empty();
         }
      };
   }

   default String getString() {
      StringBuilder builder = new StringBuilder();
      this.visit((contents) -> {
         builder.append(contents);
         return Optional.empty();
      });
      return builder.toString();
   }

   public interface ContentConsumer {
      Optional accept(final String contents);
   }

   public interface StyledContentConsumer {
      Optional accept(final Style style, final String contents);
   }
}
