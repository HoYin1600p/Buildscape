package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public class PoiTypeTags {
   public static final TagKey ACQUIRABLE_JOB_SITE = create("acquirable_job_site");
   public static final TagKey VILLAGE = create("village");
   public static final TagKey BEE_HOME = create("bee_home");

   private PoiTypeTags() {
   }

   private static TagKey create(final String name) {
      return TagKey.create(Registries.POINT_OF_INTEREST_TYPE, Identifier.withDefaultNamespace(name));
   }
}
