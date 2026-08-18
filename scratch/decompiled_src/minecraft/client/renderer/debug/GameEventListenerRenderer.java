package net.minecraft.client.renderer.debug;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class GameEventListenerRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final float BOX_HEIGHT = 1.0F;

   private void forEachListener(final DebugValueAccess debugValues, final GameEventListenerRenderer.ListenerVisitor visitor) {
      debugValues.forEachBlock(DebugSubscriptions.GAME_EVENT_LISTENERS, (blockPos, listener) -> visitor.accept(Vec3.atCenterOf(blockPos), listener.listenerRadius()));
      debugValues.forEachEntity(DebugSubscriptions.GAME_EVENT_LISTENERS, (entity, listener) -> visitor.accept(entity.position(), listener.listenerRadius()));
   }

   public void emitGizmos(final double camX, final double camY, final double camZ, final DebugValueAccess debugValues, final Frustum frustum, final float partialTicks) {
      this.forEachListener(debugValues, (origin, radius) -> {
         double size = (double)radius * 2.0D;
         Gizmos.cuboid(AABB.ofSize(origin, size, size, size), GizmoStyle.fill(ARGB.colorFromFloat(0.35F, 1.0F, 1.0F, 0.0F)));
      });
      this.forEachListener(debugValues, (origin, radius) -> Gizmos.cuboid(AABB.ofSize(origin, 0.5D, 1.0D, 0.5D).move(0.0D, 0.5D, 0.0D), GizmoStyle.fill(ARGB.colorFromFloat(0.35F, 1.0F, 1.0F, 0.0F))));
      this.forEachListener(debugValues, (origin, radius) -> {
         Gizmos.billboardText("Listener Origin", origin.add(0.0D, 1.8D, 0.0D), TextGizmo.Style.whiteAndCentered().withScale(0.4F));
         Gizmos.billboardText(BlockPos.containing(origin).toString(), origin.add(0.0D, 1.5D, 0.0D), TextGizmo.Style.forColorAndCentered(-6959665).withScale(0.4F));
      });
      debugValues.forEachEvent(DebugSubscriptions.GAME_EVENTS, (event, remainingTicks, totalLifetime) -> {
         Vec3 origin = event.pos();
         double size = 0.4D;
         AABB box = AABB.ofSize(origin.add(0.0D, 0.5D, 0.0D), 0.4D, 0.9D, 0.4D);
         Gizmos.cuboid(box, GizmoStyle.fill(ARGB.colorFromFloat(0.2F, 1.0F, 1.0F, 1.0F)));
         Gizmos.billboardText(event.event().getRegisteredName(), origin.add(0.0D, 0.85D, 0.0D), TextGizmo.Style.forColorAndCentered(-7564911).withScale(0.12F));
      });
   }

   @FunctionalInterface
   private interface ListenerVisitor {
      void accept(Vec3 origin, int radius);
   }
}
