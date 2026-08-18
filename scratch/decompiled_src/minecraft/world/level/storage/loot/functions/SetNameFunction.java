package net.minecraft.world.level.storage.loot.functions;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.ResolutionContext;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class SetNameFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(ComponentSerialization.CODEC.optionalFieldOf("name").forGetter((f) -> f.name), LootContext.EntityTarget.CODEC.optionalFieldOf("entity").forGetter((f) -> f.resolutionContext), SetNameFunction.Target.CODEC.optionalFieldOf("target", SetNameFunction.Target.CUSTOM_NAME).forGetter((f) -> f.target))).apply(i, SetNameFunction::new));
   private final Optional name;
   private final Optional resolutionContext;
   private final SetNameFunction.Target target;

   private SetNameFunction(final Optional condition, final Optional name, final Optional resolutionContext, final SetNameFunction.Target target) {
      super(condition);
      this.name = name;
      this.resolutionContext = resolutionContext;
      this.target = target;
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public Set getReferencedContextParams() {
      return (Set)DataFixUtils.orElse(this.resolutionContext.map((target) -> Set.of(target.contextParam())), Set.of());
   }

   public static UnaryOperator createResolver(final LootContext context, final LootContext.@Nullable EntityTarget entityTarget) {
      if (entityTarget != null) {
         Entity entity = (Entity)context.getOptionalParameter(entityTarget.contextParam());
         if (entity != null) {
            CommandSourceStack commandSourceStack = entity.createCommandSourceStackForNameResolution(context.getLevel()).withPermission(LevelBasedPermissionSet.GAMEMASTER);
            ResolutionContext resolutionContext = ResolutionContext.create(commandSourceStack);
            return (line) -> {
               try {
                  return ComponentUtils.resolve(resolutionContext, line);
               } catch (CommandSyntaxException var3) {
                  LOGGER.warn("Failed to resolve text component", var3);
                  return line;
               }
            };
         }
      }

      return (line) -> line;
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      this.name.ifPresent((name) -> itemStack.set(this.target.component(), (Component)createResolver(context, (LootContext.EntityTarget)this.resolutionContext.orElse((Object)null)).apply(name)));
      return itemStack;
   }

   public static LootItemConditionalFunction.Builder setName(final Component value, final SetNameFunction.Target target) {
      return simpleBuilder((conditions) -> new SetNameFunction(conditions, Optional.of(value), Optional.empty(), target));
   }

   public static LootItemConditionalFunction.Builder setName(final Component value, final SetNameFunction.Target target, final LootContext.EntityTarget resolutionContext) {
      return simpleBuilder((conditions) -> new SetNameFunction(conditions, Optional.of(value), Optional.of(resolutionContext), target));
   }

   public static enum Target implements StringRepresentable {
      CUSTOM_NAME("custom_name"),
      ITEM_NAME("item_name");

      public static final Codec CODEC = StringRepresentable.fromEnum(SetNameFunction.Target::values);
      private final String name;

      private Target(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      public DataComponentType component() {
         DataComponentType var10000;
         switch (this.ordinal()) {
            case 0:
               var10000 = DataComponents.CUSTOM_NAME;
               break;
            case 1:
               var10000 = DataComponents.ITEM_NAME;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      // $FF: synthetic method
      private static SetNameFunction.Target[] $values() {
         return new SetNameFunction.Target[]{CUSTOM_NAME, ITEM_NAME};
      }
   }
}
