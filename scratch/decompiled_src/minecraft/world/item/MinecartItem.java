package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class MinecartItem extends Item {
   private final EntityType type;

   public MinecartItem(final EntityType type, final Item.Properties properties) {
      super(properties);
      this.type = type;
   }

   public InteractionResult useOn(final UseOnContext context) {
      Level level = context.getLevel();
      BlockPos pos = context.getClickedPos();
      BlockState blockState = level.getBlockState(pos);
      if (!blockState.is(BlockTags.RAILS)) {
         return InteractionResult.FAIL;
      } else {
         ItemStack itemStack = context.getItemInHand();
         RailShape shape = blockState.getBlock() instanceof BaseRailBlock ? (RailShape)blockState.getValue(((BaseRailBlock)blockState.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
         double offset = 0.0D;
         if (shape.isSlope()) {
            offset = 0.5D;
         }

         Vec3 spawnPos = new Vec3((double)pos.getX() + 0.5D, (double)pos.getY() + 0.0625D + offset, (double)pos.getZ() + 0.5D);
         AbstractMinecart cart = AbstractMinecart.createMinecart(level, spawnPos.x, spawnPos.y, spawnPos.z, this.type, EntitySpawnReason.DISPENSER, itemStack, context.getPlayer());
         if (cart == null) {
            return InteractionResult.FAIL;
         } else {
            if (AbstractMinecart.useExperimentalMovement(level)) {
               for(Entity entity : level.getEntities((Entity)null, cart.getBoundingBox())) {
                  if (entity instanceof AbstractMinecart) {
                     return InteractionResult.FAIL;
                  }
               }
            }

            if (level instanceof ServerLevel) {
               ServerLevel serverLevel = (ServerLevel)level;
               serverLevel.addFreshEntity(cart);
               serverLevel.gameEvent(GameEvent.ENTITY_PLACE, pos, GameEvent.Context.of(context.getPlayer(), serverLevel.getBlockState(pos.below())));
            }

            itemStack.shrink(1);
            return InteractionResult.SUCCESS;
         }
      }
   }
}
