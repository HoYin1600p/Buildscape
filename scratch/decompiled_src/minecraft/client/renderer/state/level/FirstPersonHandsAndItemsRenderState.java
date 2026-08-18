package net.minecraft.client.renderer.state.level;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class FirstPersonHandsAndItemsRenderState {
   public InteractionHand attackHand;
   public float viewXRot;
   public float viewYRot;
   public float xBob;
   public float yBob;
   public boolean isScoping;
   public int useItemRemainingTicks;
   public int mainHandUseDuration;
   public int offHandUseDuration;
   public int mainHandChargeDuration;
   public int offHandChargeDuration;
   public float mainHandSwapScale;
   public float offHandSwapScale;
   public FirstPersonHandsAndItemsRenderState.HandRenderSelection handRenderSelection;
   public ItemStack mainHandItem = ItemStack.EMPTY;
   public ItemStack offHandItem = ItemStack.EMPTY;
   public float mainHandHeight;
   public float oldMainHandHeight;
   public float offHandHeight;
   public float oldOffHandHeight;
   public final ItemStackRenderState mainHandRenderState = new ItemStackRenderState();
   public final ItemStackRenderState offHandRenderState = new ItemStackRenderState();
   public final MapRenderState mainHandMapRenderState = new MapRenderState();
   public final MapRenderState offHandMapRenderState = new MapRenderState();
   public boolean hasMainHandMapData;
   public boolean hasOffHandMapData;

   @VisibleForTesting
   public static enum HandRenderSelection {
      RENDER_BOTH_HANDS(true, true),
      RENDER_MAIN_HAND_ONLY(true, false),
      RENDER_OFF_HAND_ONLY(false, true);

      public final boolean renderMainHand;
      public final boolean renderOffHand;

      private HandRenderSelection(final boolean renderMainHand, final boolean renderOffHand) {
         this.renderMainHand = renderMainHand;
         this.renderOffHand = renderOffHand;
      }

      public static FirstPersonHandsAndItemsRenderState.HandRenderSelection onlyForHand(final InteractionHand hand) {
         return hand == InteractionHand.MAIN_HAND ? RENDER_MAIN_HAND_ONLY : RENDER_OFF_HAND_ONLY;
      }

      // $FF: synthetic method
      private static FirstPersonHandsAndItemsRenderState.HandRenderSelection[] $values() {
         return new FirstPersonHandsAndItemsRenderState.HandRenderSelection[]{RENDER_BOTH_HANDS, RENDER_MAIN_HAND_ONLY, RENDER_OFF_HAND_ONLY};
      }
   }
}
