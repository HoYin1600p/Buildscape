package net.minecraft.world.scores;

import com.mojang.serialization.Codec;
import java.util.Collection;
import java.util.Optional;
import java.util.function.IntFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.Nullable;

public abstract class Team {
   public boolean isAlliedTo(final @Nullable Team other) {
      if (other == null) {
         return false;
      } else {
         return this == other;
      }
   }

   public abstract String getName();

   public abstract MutableComponent getFormattedName(Component teamMemberName);

   public abstract boolean canSeeFriendlyInvisibles();

   public abstract boolean isAllowFriendlyFire();

   public abstract Team.Visibility getNameTagVisibility();

   public abstract Optional getColor();

   public abstract Collection getPlayers();

   public abstract Team.Visibility getDeathMessageVisibility();

   public abstract Team.CollisionRule getCollisionRule();

   public static enum CollisionRule implements StringRepresentable {
      ALWAYS("always", 0),
      NEVER("never", 1),
      PUSH_OTHER_TEAMS("pushOtherTeams", 2),
      PUSH_OWN_TEAM("pushOwnTeam", 3);

      public static final Codec CODEC = StringRepresentable.fromEnum(Team.CollisionRule::values);
      private static final IntFunction BY_ID = ByIdMap.continuous((r) -> r.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final StreamCodec STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, (r) -> r.id);
      public final String name;
      public final int id;

      private CollisionRule(final String name, final int id) {
         this.name = name;
         this.id = id;
      }

      public Component getDisplayName() {
         return Component.translatable("team.collision." + this.name);
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Team.CollisionRule[] $values() {
         return new Team.CollisionRule[]{ALWAYS, NEVER, PUSH_OTHER_TEAMS, PUSH_OWN_TEAM};
      }
   }

   public static enum Visibility implements StringRepresentable {
      ALWAYS("always", 0),
      NEVER("never", 1),
      HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
      HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

      public static final Codec CODEC = StringRepresentable.fromEnum(Team.Visibility::values);
      private static final IntFunction BY_ID = ByIdMap.continuous((v) -> v.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final StreamCodec STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, (v) -> v.id);
      public final String name;
      public final int id;

      private Visibility(final String name, final int id) {
         this.name = name;
         this.id = id;
      }

      public Component getDisplayName() {
         return Component.translatable("team.visibility." + this.name);
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Team.Visibility[] $values() {
         return new Team.Visibility[]{ALWAYS, NEVER, HIDE_FOR_OTHER_TEAMS, HIDE_FOR_OWN_TEAM};
      }
   }
}
