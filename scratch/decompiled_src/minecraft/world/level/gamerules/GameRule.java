package net.minecraft.world.level.gamerules;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Objects;
import java.util.function.ToIntFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlagSet;

public final class GameRule implements FeatureElement {
   private final GameRuleCategory category;
   private final GameRuleType gameRuleType;
   private final ArgumentType argument;
   private final GameRules.VisitorCaller visitorCaller;
   private final Codec valueCodec;
   private final ToIntFunction commandResultFunction;
   private final Object defaultValue;
   private final FeatureFlagSet requiredFeatures;

   public GameRule(final GameRuleCategory category, final GameRuleType gameRuleType, final ArgumentType argument, final GameRules.VisitorCaller visitorCaller, final Codec valueCodec, final ToIntFunction commandResultFunction, final Object defaultValue, final FeatureFlagSet requiredFeatures) {
      this.category = category;
      this.gameRuleType = gameRuleType;
      this.argument = argument;
      this.visitorCaller = visitorCaller;
      this.valueCodec = valueCodec;
      this.commandResultFunction = commandResultFunction;
      this.defaultValue = defaultValue;
      this.requiredFeatures = requiredFeatures;
   }

   public String toString() {
      return this.id();
   }

   public String id() {
      return this.getIdentifier().toShortString();
   }

   public Identifier getIdentifier() {
      return (Identifier)Objects.requireNonNull(BuiltInRegistries.GAME_RULE.getKey(this));
   }

   public Identifier getIdentifierWithFallback() {
      return (Identifier)Objects.requireNonNullElse(BuiltInRegistries.GAME_RULE.getKey(this), Identifier.withDefaultNamespace("unregistered_sadface"));
   }

   public String getDescriptionId() {
      return Util.makeDescriptionId("gamerule", this.getIdentifier());
   }

   public String serialize(final Object value) {
      return value.toString();
   }

   public DataResult deserialize(final String value) {
      try {
         StringReader reader = new StringReader(value);
         Object result = (T)this.argument.parse(reader);
         return reader.canRead() ? DataResult.error(() -> "Failed to deserialize; trailing characters", result) : DataResult.success(result);
      } catch (CommandSyntaxException var4) {
         return DataResult.error(() -> "Failed to deserialize");
      }
   }

   public Class valueClass() {
      return this.defaultValue.getClass();
   }

   public void callVisitor(final GameRuleTypeVisitor visitor) {
      this.visitorCaller.call(visitor, this);
   }

   public int getCommandResult(final Object value) {
      return this.commandResultFunction.applyAsInt(value);
   }

   public GameRuleCategory category() {
      return this.category;
   }

   public GameRuleType gameRuleType() {
      return this.gameRuleType;
   }

   public ArgumentType argument() {
      return this.argument;
   }

   public Codec valueCodec() {
      return this.valueCodec;
   }

   public Object defaultValue() {
      return this.defaultValue;
   }

   public FeatureFlagSet requiredFeatures() {
      return this.requiredFeatures;
   }
}
