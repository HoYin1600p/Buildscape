package net.minecraft.commands.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class EntityAnchorArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("eyes", "feet");
   private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType((name) -> Component.translatableEscape("argument.anchor.invalid", name));

   public static EntityAnchorArgument.Anchor getAnchor(final CommandContext context, final String name) {
      return (EntityAnchorArgument.Anchor)context.getArgument(name, EntityAnchorArgument.Anchor.class);
   }

   public static EntityAnchorArgument anchor() {
      return new EntityAnchorArgument();
   }

   public EntityAnchorArgument.Anchor parse(final StringReader reader) throws CommandSyntaxException {
      int start = reader.getCursor();
      String name = reader.readUnquotedString();
      EntityAnchorArgument.Anchor anchor = EntityAnchorArgument.Anchor.getByName(name);
      if (anchor == null) {
         reader.setCursor(start);
         throw ERROR_INVALID.createWithContext(reader, name);
      } else {
         return anchor;
      }
   }

   public CompletableFuture listSuggestions(final CommandContext context, final SuggestionsBuilder builder) {
      return SharedSuggestionProvider.suggest(EntityAnchorArgument.Anchor.BY_NAME.keySet(), builder);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   public static enum Anchor {
      FEET("feet", (p, e) -> p),
      EYES("eyes", (p, e) -> new Vec3(p.x, p.y + (double)e.getEyeHeight(), p.z));

      private static final Map BY_NAME = (Map)Util.make(Maps.newHashMap(), (map) -> {
         for(EntityAnchorArgument.Anchor anchor : values()) {
            map.put(anchor.name, anchor);
         }

      });
      private final String name;
      private final BiFunction transform;

      private Anchor(final String name, final BiFunction transform) {
         this.name = name;
         this.transform = transform;
      }

      public static EntityAnchorArgument.@Nullable Anchor getByName(final String name) {
         return (EntityAnchorArgument.Anchor)BY_NAME.get(name);
      }

      public Vec3 apply(final Entity entity) {
         return (Vec3)this.transform.apply(entity.position(), entity);
      }

      public Vec3 apply(final CommandSourceStack source) {
         Entity entity = source.getEntity();
         return entity == null ? source.getPosition() : (Vec3)this.transform.apply(source.getPosition(), entity);
      }

      // $FF: synthetic method
      private static EntityAnchorArgument.Anchor[] $values() {
         return new EntityAnchorArgument.Anchor[]{FEET, EYES};
      }
   }
}
