package net.minecraft.world.entity.animal.fish;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FollowFlockLeaderGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jspecify.annotations.Nullable;

public abstract class AbstractSchoolingFish extends AbstractFish {
   private @Nullable AbstractSchoolingFish leader;
   private int schoolSize = 1;

   public AbstractSchoolingFish(final EntityType type, final Level level) {
      super(type, level);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(5, new FollowFlockLeaderGoal(this));
   }

   public int getMaxSpawnClusterSize() {
      return this.getMaxSchoolSize();
   }

   public int getMaxSchoolSize() {
      return super.getMaxSpawnClusterSize();
   }

   protected boolean canRandomSwim() {
      return !this.isFollower();
   }

   public boolean isFollower() {
      return this.leader != null && this.leader.isAlive();
   }

   public AbstractSchoolingFish startFollowing(final AbstractSchoolingFish leader) {
      this.leader = leader;
      leader.addFollower();
      return leader;
   }

   public void stopFollowing() {
      this.leader.removeFollower();
      this.leader = null;
   }

   private void addFollower() {
      ++this.schoolSize;
   }

   private void removeFollower() {
      --this.schoolSize;
   }

   public boolean canBeFollowed() {
      return this.hasFollowers() && this.schoolSize < this.getMaxSchoolSize();
   }

   public void tick() {
      super.tick();
      if (this.hasFollowers() && this.level().getRandom().nextInt(200) == 1) {
         List neighbors = this.level().getEntitiesOfClass(this.getClass(), this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D));
         if (neighbors.size() <= 1) {
            this.schoolSize = 1;
         }
      }

   }

   public boolean hasFollowers() {
      return this.schoolSize > 1;
   }

   public boolean inRangeOfLeader() {
      return this.distanceToSqr(this.leader) <= 121.0D;
   }

   public void pathToLeader() {
      if (this.isFollower()) {
         this.getNavigation().moveTo(this.leader, 1.0D);
      }

   }

   public void addFollowers(final Stream abstractSchoolingFishStream) {
      abstractSchoolingFishStream.limit((long)(this.getMaxSchoolSize() - this.schoolSize)).filter((f) -> f != this).forEach((otherFish) -> otherFish.startFollowing(this));
   }

   public @Nullable SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, @Nullable SpawnGroupData groupData) {
      super.finalizeSpawn(level, difficulty, spawnReason, groupData);
      if (groupData == null) {
         groupData = new AbstractSchoolingFish.SchoolSpawnGroupData(this);
      } else {
         this.startFollowing(((AbstractSchoolingFish.SchoolSpawnGroupData)groupData).leader);
      }

      return groupData;
   }

   public static class SchoolSpawnGroupData implements SpawnGroupData {
      public final AbstractSchoolingFish leader;

      public SchoolSpawnGroupData(final AbstractSchoolingFish leader) {
         this.leader = leader;
      }
   }
}
