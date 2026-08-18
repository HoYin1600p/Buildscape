package net.minecraft.world.entity.ai.behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Block;

public class LongJumpToPreferredBlock extends LongJumpToRandomPos {
   private final TagKey preferredBlockTag;
   private final float preferredBlocksChance;
   private final List notPrefferedJumpCandidates = new ArrayList();
   private boolean currentlyWantingPreferredOnes;

   public LongJumpToPreferredBlock(final UniformInt timeBetweenLongJumps, final int maxLongJumpHeight, final int maxLongJumpWidth, final float maxJumpVelocity, final Function getJumpSound, final TagKey preferredBlockTag, final float preferredBlocksChance, final BiPredicate acceptableLandingSpot) {
      super(timeBetweenLongJumps, maxLongJumpHeight, maxLongJumpWidth, maxJumpVelocity, getJumpSound, acceptableLandingSpot);
      this.preferredBlockTag = preferredBlockTag;
      this.preferredBlocksChance = preferredBlocksChance;
   }

   protected void start(final ServerLevel level, final Mob body, final long timestamp) {
      super.start(level, body, timestamp);
      this.notPrefferedJumpCandidates.clear();
      this.currentlyWantingPreferredOnes = body.getRandom().nextFloat() < this.preferredBlocksChance;
   }

   protected Optional getJumpCandidate(final ServerLevel level) {
      if (!this.currentlyWantingPreferredOnes) {
         return super.getJumpCandidate(level);
      } else {
         BlockPos.MutableBlockPos testPos = new BlockPos.MutableBlockPos();

         while(!this.jumpCandidates.isEmpty()) {
            Optional jumpCandidate = super.getJumpCandidate(level);
            if (jumpCandidate.isPresent()) {
               LongJumpToRandomPos.PossibleJump possibleJump = (LongJumpToRandomPos.PossibleJump)jumpCandidate.get();
               if (level.getBlockState(testPos.setWithOffset(possibleJump.targetPos(), Direction.DOWN)).is(this.preferredBlockTag)) {
                  return jumpCandidate;
               }

               this.notPrefferedJumpCandidates.add(possibleJump);
            }
         }

         return !this.notPrefferedJumpCandidates.isEmpty() ? Optional.of((LongJumpToRandomPos.PossibleJump)this.notPrefferedJumpCandidates.remove(0)) : Optional.empty();
      }
   }
}
