package net.minecraft.server.dialog.action;

import com.mojang.serialization.MapCodec;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.util.Util;

public record StaticAction(ClickEvent value) implements Action {
   public static final Map WRAPPED_CODECS = (Map)Util.make(() -> {
      Map result = new EnumMap(ClickEvent.Action.class);

      for(ClickEvent.Action action : (ClickEvent.Action[])ClickEvent.Action.class.getEnumConstants()) {
         if (action.isAllowedFromServer()) {
            MapCodec mapCodec = action.valueCodec();
            result.put(action, mapCodec.xmap(StaticAction::new, StaticAction::value));
         }
      }

      return Collections.unmodifiableMap(result);
   });

   public MapCodec codec() {
      return (MapCodec)WRAPPED_CODECS.get(this.value.action());
   }

   public Optional createAction(final Map parameters) {
      return Optional.of(this.value);
   }
}
