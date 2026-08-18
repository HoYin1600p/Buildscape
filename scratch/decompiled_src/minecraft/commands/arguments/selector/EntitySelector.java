package net.minecraft.commands.arguments.selector;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.util.CompilableString;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class EntitySelector {
   public static final int INFINITE = Integer.MAX_VALUE;
   public static final BiConsumer ORDER_ARBITRARY = (p, c) -> {
   };
   private static final EntityTypeTest ANY_TYPE = new EntityTypeTest() {
      public Entity tryCast(final Entity entity) {
         return entity;
      }

      public Class getBaseClass() {
         return Entity.class;
      }
   };
   public static final Codec COMPILABLE_CODEC = CompilableString.codec(new CompilableString.CommandParserHelper() {
      protected EntitySelector parse(final StringReader reader) throws CommandSyntaxException {
         return (new EntitySelectorParser(reader, true)).parse();
      }

      protected String errorMessage(final String original, final CommandSyntaxException exception) {
         return "Invalid selector component: " + original + ": " + exception.getMessage();
      }
   });
   private final int maxResults;
   private final boolean includesEntities;
   private final boolean worldLimited;
   private final List contextFreePredicates;
   private final MinMaxBounds.@Nullable Doubles range;
   private final Function position;
   private final @Nullable AABB aabb;
   private final BiConsumer order;
   private final boolean currentEntity;
   private final @Nullable String playerName;
   private final @Nullable UUID entityUUID;
   private final EntityTypeTest type;
   private final boolean usesSelector;

   public EntitySelector(final int maxResults, final boolean includesEntities, final boolean worldLimited, final List contextFreePredicates, final MinMaxBounds.@Nullable Doubles range, final Function position, final @Nullable AABB aabb, final BiConsumer order, final boolean currentEntity, final @Nullable String playerName, final @Nullable UUID entityUUID, final @Nullable EntityType type, final boolean usesSelector) {
      this.maxResults = maxResults;
      this.includesEntities = includesEntities;
      this.worldLimited = worldLimited;
      this.contextFreePredicates = contextFreePredicates;
      this.range = range;
      this.position = position;
      this.aabb = aabb;
      this.order = order;
      this.currentEntity = currentEntity;
      this.playerName = playerName;
      this.entityUUID = entityUUID;
      this.type = (EntityTypeTest)(type == null ? ANY_TYPE : type);
      this.usesSelector = usesSelector;
   }

   public int getMaxResults() {
      return this.maxResults;
   }

   public boolean includesEntities() {
      return this.includesEntities;
   }

   public boolean isSelfSelector() {
      return this.currentEntity;
   }

   public boolean isWorldLimited() {
      return this.worldLimited;
   }

   public boolean usesSelector() {
      return this.usesSelector;
   }

   private void checkPermissions(final CommandSourceStack sender) throws CommandSyntaxException {
      if (this.usesSelector && !sender.permissions().hasPermission(Permissions.COMMANDS_ENTITY_SELECTORS)) {
         throw EntityArgument.ERROR_SELECTORS_NOT_ALLOWED.create();
      }
   }

   public Entity findSingleEntity(final CommandSourceStack sender) throws CommandSyntaxException {
      this.checkPermissions(sender);
      List entities = this.findEntities(sender);
      if (entities.isEmpty()) {
         throw EntityArgument.NO_ENTITIES_FOUND.create();
      } else if (entities.size() > 1) {
         throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
      } else {
         return (Entity)entities.get(0);
      }
   }

   public List findEntities(final CommandSourceStack sender) throws CommandSyntaxException {
      this.checkPermissions(sender);
      if (!this.includesEntities) {
         return this.findPlayers(sender);
      } else if (this.playerName != null) {
         ServerPlayer result = sender.getServer().getPlayerList().getPlayerByName(this.playerName);
         return result == null ? List.of() : List.of(result);
      } else if (this.entityUUID != null) {
         for(ServerLevel level : sender.getServer().getAllLevels()) {
            Entity entity = level.getEntity(this.entityUUID);
            if (entity != null) {
               if (entity.getType().isEnabled(sender.enabledFeatures())) {
                  return List.of(entity);
               }
               break;
            }
         }

         return List.of();
      } else {
         Vec3 pos = (Vec3)this.position.apply(sender.getPosition());
         AABB absoluteAabb = this.getAbsoluteAabb(pos);
         if (this.currentEntity) {
            Predicate predicate = this.getPredicate(pos, absoluteAabb, (FeatureFlagSet)null);
            return sender.getEntity() != null && predicate.test(sender.getEntity()) ? List.of(sender.getEntity()) : List.of();
         } else {
            Predicate predicate = this.getPredicate(pos, absoluteAabb, sender.enabledFeatures());
            List result = new ObjectArrayList();
            if (this.isWorldLimited()) {
               this.addEntities(result, sender.getLevel(), absoluteAabb, predicate);
            } else {
               for(ServerLevel level : sender.getServer().getAllLevels()) {
                  this.addEntities(result, level, absoluteAabb, predicate);
               }
            }

            return this.sortAndLimit(pos, result);
         }
      }
   }

   private void addEntities(final List result, final ServerLevel level, final @Nullable AABB absoluteAABB, final Predicate predicate) {
      int limit = this.getResultLimit();
      if (result.size() < limit) {
         if (absoluteAABB != null) {
            level.getEntities(this.type, absoluteAABB, predicate, result, limit);
         } else {
            level.getEntities(this.type, predicate, result, limit);
         }

      }
   }

   private int getResultLimit() {
      return this.order == ORDER_ARBITRARY ? this.maxResults : Integer.MAX_VALUE;
   }

   public ServerPlayer findSinglePlayer(final CommandSourceStack sender) throws CommandSyntaxException {
      this.checkPermissions(sender);
      List players = this.findPlayers(sender);
      if (players.size() != 1) {
         throw EntityArgument.NO_PLAYERS_FOUND.create();
      } else {
         return (ServerPlayer)players.get(0);
      }
   }

   public List findPlayers(final CommandSourceStack sender) throws CommandSyntaxException {
      this.checkPermissions(sender);
      if (this.playerName != null) {
         ServerPlayer result = sender.getServer().getPlayerList().getPlayerByName(this.playerName);
         return result == null ? List.of() : List.of(result);
      } else if (this.entityUUID != null) {
         ServerPlayer result = sender.getServer().getPlayerList().getPlayer(this.entityUUID);
         return result == null ? List.of() : List.of(result);
      } else {
         Vec3 pos = (Vec3)this.position.apply(sender.getPosition());
         AABB absoluteAabb = this.getAbsoluteAabb(pos);
         Predicate predicate = this.getPredicate(pos, absoluteAabb, (FeatureFlagSet)null);
         if (this.currentEntity) {
            Entity var12 = sender.getEntity();
            if (var12 instanceof ServerPlayer) {
               ServerPlayer player = (ServerPlayer)var12;
               if (predicate.test(player)) {
                  return List.of(player);
               }
            }

            return List.of();
         } else {
            int limit = this.getResultLimit();
            List result;
            if (this.isWorldLimited()) {
               result = sender.getLevel().getPlayers(predicate, limit);
            } else {
               result = new ObjectArrayList();

               for(ServerPlayer player : sender.getServer().getPlayerList().getPlayers()) {
                  if (predicate.test(player)) {
                     result.add(player);
                     if (result.size() >= limit) {
                        return result;
                     }
                  }
               }
            }

            return this.sortAndLimit(pos, result);
         }
      }
   }

   private @Nullable AABB getAbsoluteAabb(final Vec3 pos) {
      return this.aabb != null ? this.aabb.move(pos) : null;
   }

   private Predicate getPredicate(final Vec3 pos, final @Nullable AABB absoluteAabb, final @Nullable FeatureFlagSet enabledFeatures) {
      boolean filterFeatures = enabledFeatures != null;
      boolean filterAabb = absoluteAabb != null;
      boolean filterRange = this.range != null;
      int extraCount = (filterFeatures ? 1 : 0) + (filterAabb ? 1 : 0) + (filterRange ? 1 : 0);
      List completePredicates;
      if (extraCount == 0) {
         completePredicates = this.contextFreePredicates;
      } else {
         List predicates = new ObjectArrayList(this.contextFreePredicates.size() + extraCount);
         predicates.addAll(this.contextFreePredicates);
         if (filterFeatures) {
            predicates.add((Predicate)(e) -> e.getType().isEnabled(enabledFeatures));
         }

         if (filterAabb) {
            predicates.add((Predicate)(e) -> absoluteAabb.intersects(e.getBoundingBox()));
         }

         if (filterRange) {
            predicates.add((Predicate)(e) -> this.range.matchesSqr(e.distanceToSqr(pos)));
         }

         completePredicates = predicates;
      }

      return Util.allOf(completePredicates);
   }

   private List sortAndLimit(final Vec3 pos, final List result) {
      if (result.size() > 1) {
         this.order.accept(pos, result);
      }

      return result.subList(0, Math.min(this.maxResults, result.size()));
   }

   public static Component joinNames(final List entities) {
      return ComponentUtils.formatList(entities, Entity::getDisplayName);
   }
}
