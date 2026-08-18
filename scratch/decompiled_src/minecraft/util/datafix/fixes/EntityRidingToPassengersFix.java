package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.ExtraDataFixUtils;

public class EntityRidingToPassengersFix extends DataFix {
   public EntityRidingToPassengersFix(final Schema outputSchema, final boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      Schema inputSchema = this.getInputSchema();
      Schema outputSchema = this.getOutputSchema();
      Type oldEntityTreeType = inputSchema.getTypeRaw(References.ENTITY_TREE);
      Type newEntityTreeType = outputSchema.getTypeRaw(References.ENTITY_TREE);
      Type entityType = inputSchema.getTypeRaw(References.ENTITY);
      return this.cap(inputSchema, outputSchema, oldEntityTreeType, newEntityTreeType, entityType);
   }

   private TypeRewriteRule cap(final Schema inputSchema, final Schema outputType, final Type oldEntityTreeType, final Type newEntityTreeType, final Type entityType) {
      Type oldType = DSL.named(References.ENTITY_TREE.typeName(), DSL.and(DSL.optional(DSL.field("Riding", oldEntityTreeType)), entityType));
      Type newType = DSL.named(References.ENTITY_TREE.typeName(), DSL.and(DSL.optional(DSL.field("Passengers", DSL.list(newEntityTreeType))), entityType));
      Type oldEntityType = inputSchema.getType(References.ENTITY_TREE);
      Type newEntityType = outputType.getType(References.ENTITY_TREE);
      if (!Objects.equals(oldEntityType, oldType)) {
         throw new IllegalStateException("Old entity type is not what was expected.");
      } else if (!newEntityType.equals(newType, true, true)) {
         throw new IllegalStateException("New entity type is not what was expected.");
      } else {
         Type patchedEntityTreeType = ExtraDataFixUtils.patchSubType(oldType, oldType, newType);
         OpticFinder entityFinder = DSL.typeFinder(entityType);
         OpticFinder newEntityTreeValueFinder = DSL.typeFinder(newType);
         OpticFinder ridingFinder = DSL.fieldFinder("Riding", newEntityTreeType);
         Type oldPlayerType = inputSchema.getType(References.PLAYER);
         Type newPlayerType = outputType.getType(References.PLAYER);
         return TypeRewriteRule.seq(this.fixTypeEverywhere("EntityRidingToPassengerFix", oldType, newType, (ops) -> (badlyTypedInput) -> {
               Typed input = ExtraDataFixUtils.cast(patchedEntityTreeType, badlyTypedInput, ops);
               Optional maybeRiding = input.getOptionalTyped(ridingFinder).flatMap((t) -> t.getOptional(newEntityTreeValueFinder));
               Object entity = (Entity)input.getOptional(entityFinder).orElseThrow();
               if (maybeRiding.isEmpty()) {
                  Either passengers = Either.right(Unit.INSTANCE);
                  return Pair.of(References.ENTITY_TREE.typeName(), Pair.of(passengers, entity));
               } else {
                  return addPassengerToTop((Pair)maybeRiding.get(), entity, ops, newEntityTreeType, newEntityTreeValueFinder);
               }
            }), this.writeAndRead("player RootVehicle injecter", oldPlayerType, newPlayerType));
      }
   }

   private static Pair addPassengerToTop(final Pair root, final Object passengerEntity, final DynamicOps ops, final Type rawEntityTreeType, final OpticFinder entityTreeFinder) {
      Object rootEntity = (Entity)((Pair)root.getSecond()).getSecond();
      Optional passengers = ((Either)((Pair)root.getSecond()).getFirst()).left();
      Pair newPassenger;
      if (passengers.isPresent() && !((List)passengers.get()).isEmpty()) {
         Pair unwrappedPassenger = (Pair)unwrapRecursiveValue(((List)passengers.get()).getFirst(), ops, rawEntityTreeType, entityTreeFinder);
         newPassenger = addPassengerToTop(unwrappedPassenger, passengerEntity, ops, rawEntityTreeType, entityTreeFinder);
      } else {
         newPassenger = Pair.of(References.ENTITY_TREE.typeName(), Pair.of(Either.right(Unit.INSTANCE), passengerEntity));
      }

      List newPassengers = List.of(wrapRecursiveValue(newPassenger, ops, rawEntityTreeType, entityTreeFinder));
      return Pair.of(References.ENTITY_TREE.typeName(), Pair.of(Either.left(newPassengers), rootEntity));
   }

   private static Object unwrapRecursiveValue(final Object raw, final DynamicOps ops, final Type rawType, final OpticFinder valueFinder) {
      return (new Typed(rawType, ops, raw)).getOptional(valueFinder).orElseThrow();
   }

   private static Object wrapRecursiveValue(final Object value, final DynamicOps ops, final Type rawType, final OpticFinder valueFinder) {
      return ((Typed)rawType.pointTyped(ops).orElseThrow()).set(valueFinder, value).getValue();
   }
}
