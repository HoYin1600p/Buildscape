package net.minecraft.world.level.entity;

public interface LevelCallback {
   void onCreated(Object entity);

   void onDestroyed(Object entity);

   void onTickingStart(Object entity);

   void onTickingEnd(Object entity);

   void onTrackingStart(Object entity);

   void onTrackingEnd(Object entity);

   void onSectionChange(Object entity);
}
