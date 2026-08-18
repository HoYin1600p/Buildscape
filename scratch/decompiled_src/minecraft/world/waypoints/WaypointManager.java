package net.minecraft.world.waypoints;

public interface WaypointManager {
   void trackWaypoint(Waypoint waypoint);

   void updateWaypoint(Waypoint waypoint);

   void untrackWaypoint(Waypoint waypoint);
}
