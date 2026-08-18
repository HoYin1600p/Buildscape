package net.minecraft.world.scores.criteria;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatType;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.scores.TeamColor;

public class ObjectiveCriteria {
   private static final Map CUSTOM_CRITERIA = Maps.newHashMap();
   private static final Map CRITERIA_CACHE = Maps.newHashMap();
   public static final Codec CODEC = Codec.STRING.comapFlatMap((name) -> (DataResult)byName(name).map(DataResult::success).orElse(DataResult.error(() -> "No scoreboard criteria with name: " + name)), ObjectiveCriteria::getName);
   public static final ObjectiveCriteria DUMMY = registerCustom("dummy");
   public static final ObjectiveCriteria TRIGGER = registerCustom("trigger");
   public static final ObjectiveCriteria DEATH_COUNT = registerCustom("deathCount");
   public static final ObjectiveCriteria KILL_COUNT_PLAYERS = registerCustom("playerKillCount");
   public static final ObjectiveCriteria KILL_COUNT_ALL = registerCustom("totalKillCount");
   public static final ObjectiveCriteria HEALTH = registerCustom("health", true, ObjectiveCriteria.RenderType.HEARTS);
   public static final ObjectiveCriteria FOOD = registerCustom("food", true, ObjectiveCriteria.RenderType.INTEGER);
   public static final ObjectiveCriteria AIR = registerCustom("air", true, ObjectiveCriteria.RenderType.INTEGER);
   public static final ObjectiveCriteria ARMOR = registerCustom("armor", true, ObjectiveCriteria.RenderType.INTEGER);
   public static final ObjectiveCriteria EXPERIENCE = registerCustom("xp", true, ObjectiveCriteria.RenderType.INTEGER);
   public static final ObjectiveCriteria LEVEL = registerCustom("level", true, ObjectiveCriteria.RenderType.INTEGER);
   public static final Map TEAM_KILL = registerForEveryTeamColor((format) -> "teamkill." + format.getSerializedName());
   public static final Map KILLED_BY_TEAM = registerForEveryTeamColor((format) -> "killedByTeam." + format.getSerializedName());
   private final String name;
   private final boolean readOnly;
   private final ObjectiveCriteria.RenderType renderType;

   private static Map registerForEveryTeamColor(final Function idFactory) {
      Map result = new EnumMap(TeamColor.class);

      for(TeamColor value : TeamColor.values()) {
         String id = (String)idFactory.apply(value);
         result.put(value, registerCustom(id));
      }

      return result;
   }

   private static ObjectiveCriteria registerCustom(final String name, final boolean readOnly, final ObjectiveCriteria.RenderType renderType) {
      ObjectiveCriteria result = new ObjectiveCriteria(name, readOnly, renderType);
      CUSTOM_CRITERIA.put(name, result);
      return result;
   }

   private static ObjectiveCriteria registerCustom(final String name) {
      return registerCustom(name, false, ObjectiveCriteria.RenderType.INTEGER);
   }

   protected ObjectiveCriteria(final String name) {
      this(name, false, ObjectiveCriteria.RenderType.INTEGER);
   }

   protected ObjectiveCriteria(final String name, final boolean readOnly, final ObjectiveCriteria.RenderType renderType) {
      this.name = name;
      this.readOnly = readOnly;
      this.renderType = renderType;
      CRITERIA_CACHE.put(name, this);
   }

   public static Set getCustomCriteriaNames() {
      return ImmutableSet.copyOf(CUSTOM_CRITERIA.keySet());
   }

   public static Optional byName(final String name) {
      ObjectiveCriteria value = (ObjectiveCriteria)CRITERIA_CACHE.get(name);
      if (value != null) {
         return Optional.of(value);
      } else {
         int colonPos = name.indexOf(58);
         return colonPos < 0 ? Optional.empty() : BuiltInRegistries.STAT_TYPE.getOptional(Identifier.bySeparator(name.substring(0, colonPos), '.')).flatMap((statType) -> getStat(statType, Identifier.bySeparator(name.substring(colonPos + 1), '.')));
      }
   }

   private static Optional getStat(final StatType statType, final Identifier key) {
      return statType.getRegistry().getOptional(key).map(statType::get);
   }

   public String getName() {
      return this.name;
   }

   public boolean isReadOnly() {
      return this.readOnly;
   }

   public ObjectiveCriteria.RenderType getDefaultRenderType() {
      return this.renderType;
   }

   public static enum RenderType implements StringRepresentable {
      INTEGER("integer"),
      HEARTS("hearts");

      private final String id;
      public static final StringRepresentable.EnumCodec CODEC = StringRepresentable.fromEnum(ObjectiveCriteria.RenderType::values);

      private RenderType(final String id) {
         this.id = id;
      }

      public String getId() {
         return this.id;
      }

      public String getSerializedName() {
         return this.id;
      }

      public static ObjectiveCriteria.RenderType byId(final String key) {
         return (ObjectiveCriteria.RenderType)CODEC.byName(key, INTEGER);
      }

      // $FF: synthetic method
      private static ObjectiveCriteria.RenderType[] $values() {
         return new ObjectiveCriteria.RenderType[]{INTEGER, HEARTS};
      }
   }
}
