package net.minecraft.server.dialog.action;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public interface Action {
   Codec CODEC = BuiltInRegistries.DIALOG_ACTION_TYPE.byNameCodec().dispatch(Action::codec, (c) -> c);

   MapCodec codec();

   Optional createAction(Map parameters);

   public interface ValueGetter {
      String asTemplateSubstitution();

      Tag asTag();

      static Map getAsTemplateSubstitutions(final Map parameters) {
         return Maps.transformValues(parameters, Action.ValueGetter::asTemplateSubstitution);
      }

      static Action.ValueGetter of(final String value) {
         return new Action.ValueGetter() {
            public String asTemplateSubstitution() {
               return value;
            }

            public Tag asTag() {
               return StringTag.valueOf(value);
            }
         };
      }

      static Action.ValueGetter of(final Supplier value) {
         return new Action.ValueGetter() {
            public String asTemplateSubstitution() {
               return (String)value.get();
            }

            public Tag asTag() {
               return StringTag.valueOf((String)value.get());
            }
         };
      }
   }
}
