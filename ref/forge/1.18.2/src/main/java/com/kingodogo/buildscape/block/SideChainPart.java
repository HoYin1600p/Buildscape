package com.kingodogo.buildscape.block;

import net.minecraft.util.StringRepresentable;

public enum SideChainPart implements StringRepresentable {
   UNCONNECTED("unconnected"),
   RIGHT("right"),
   CENTER("center"),
   LEFT("left");

   private final String name;

   private SideChainPart(final String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return this.getSerializedName();
   }

   @Override
   public String getSerializedName() {
      return this.name;
   }

   public boolean isConnected() {
      return this != UNCONNECTED;
   }

   public boolean isConnectionTowards(final SideChainPart endPart) {
      return this == CENTER || this == endPart;
   }

   public boolean isChainEnd() {
      return this != CENTER;
   }

   public SideChainPart whenConnectedToTheRight() {
      switch (this) {
         case UNCONNECTED:
         case LEFT:
            return LEFT;
         case RIGHT:
         case CENTER:
            return CENTER;
         default:
            throw new IllegalArgumentException();
      }
   }

   public SideChainPart whenConnectedToTheLeft() {
      switch (this) {
         case UNCONNECTED:
         case RIGHT:
            return RIGHT;
         case CENTER:
         case LEFT:
            return CENTER;
         default:
            throw new IllegalArgumentException();
      }
   }

   public SideChainPart whenDisconnectedFromTheRight() {
      switch (this) {
         case UNCONNECTED:
         case LEFT:
            return UNCONNECTED;
         case RIGHT:
         case CENTER:
            return RIGHT;
         default:
            throw new IllegalArgumentException();
      }
   }

   public SideChainPart whenDisconnectedFromTheLeft() {
      switch (this) {
         case UNCONNECTED:
         case RIGHT:
            return UNCONNECTED;
         case CENTER:
         case LEFT:
            return LEFT;
         default:
            throw new IllegalArgumentException();
      }
   }
}
