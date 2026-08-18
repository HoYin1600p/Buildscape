package net.minecraft.world.entity.ai.behavior;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.level.pathfinder.Path;

public class YieldJobSite {
   public static BehaviorControl create(final float speedModifier) {
      return BehaviorBuilder.create((i) -> i.group(i.present(MemoryModuleType.POTENTIAL_JOB_SITE), i.absent(MemoryModuleType.JOB_SITE), i.present(MemoryModuleType.NEAREST_LIVING_ENTITIES), i.registered(MemoryModuleType.WALK_TARGET), i.registered(MemoryModuleType.LOOK_TARGET)).apply(i, (potentialJob, jobSite, nearestEntities, walkTarget, lookTarget) -> (level, body, timestamp) -> {
               if (body.isBaby()) {
                  return false;
               } else if (!body.getVillagerData().profession().is(VillagerProfession.NONE)) {
                  return false;
               } else {
                  BlockPos poiPos = ((GlobalPos)i.get(potentialJob)).pos();
                  Optional poiType = level.getPoiManager().getType(poiPos);
                  if (poiType.isEmpty()) {
                     return true;
                  } else {
                     ((List)i.get(nearestEntities)).stream().filter((v) -> v instanceof Villager && v != body).map((v) -> (Villager)v).filter(LivingEntity::isAlive).filter((v) -> nearbyWantsJobsite((Holder)poiType.get(), v, poiPos)).findFirst().ifPresent((nearbyVillager) -> {
                        walkTarget.erase();
                        lookTarget.erase();
                        potentialJob.erase();
                        if (nearbyVillager.getBrain().getMemory(MemoryModuleType.JOB_SITE).isEmpty()) {
                           BehaviorUtils.setWalkAndLookTargetMemories(nearbyVillager, poiPos, speedModifier, 1);
                           nearbyVillager.getBrain().setMemory(MemoryModuleType.POTENTIAL_JOB_SITE, GlobalPos.of(level.dimension(), poiPos));
                           level.debugSynchronizers().updatePoi(poiPos);
                        }

                     });
                     return true;
                  }
               }
            }));
   }

   private static boolean nearbyWantsJobsite(final Holder type, final Villager nearbyVillager, final BlockPos poiPos) {
      boolean nearbyHasPotentialJobSite = nearbyVillager.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).isPresent();
      if (nearbyHasPotentialJobSite) {
         return false;
      } else {
         Optional nearbyVillagerJobSiteMemory = nearbyVillager.getBrain().getMemory(MemoryModuleType.JOB_SITE);
         Holder nearbyProfession = nearbyVillager.getVillagerData().profession();
         if (((VillagerProfession)nearbyProfession.value()).heldJobSite().test(type)) {
            return nearbyVillagerJobSiteMemory.isEmpty() ? canReachPos(nearbyVillager, poiPos, (PoiType)type.value()) : ((GlobalPos)nearbyVillagerJobSiteMemory.get()).pos().equals(poiPos);
         } else {
            return false;
         }
      }
   }

   private static boolean canReachPos(final PathfinderMob nearbyVillager, final BlockPos poiPos, final PoiType type) {
      Path path = nearbyVillager.getNavigation().createPath(poiPos, type.validRange());
      return path != null && path.canReach();
   }
}
