package com.kingodogo.buildscape.particle;
public final class PillarSparkleDataQueue {

    private PillarSparkleDataQueue() {}

    private static final ThreadLocal<PendingSpawn> PENDING = ThreadLocal.withInitial(PendingSpawn::new);

    public static final class PendingSpawn {
        public double x;
        public double y;
        public double z;
        public String colorCode;
        public float sizeMultiplier;

        public void target(double x, double y, double z) {
            if (this.x != x || this.y != y || this.z != z) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.colorCode = null;
                this.sizeMultiplier = 0.0F;
            }
        }

        public boolean matches(double x, double y, double z) {
            return this.x == x && this.y == y && this.z == z;
        }

        public void clear() {
            this.colorCode = null;
            this.sizeMultiplier = 0.0F;
        }
    }

    public static void queueColor(double x, double y, double z, String colorCode) {
        PendingSpawn pending = PENDING.get();
        pending.target(x, y, z);
        pending.colorCode = colorCode;
    }

    public static void queueSize(double x, double y, double z, float sizeMultiplier) {
        PendingSpawn pending = PENDING.get();
        pending.target(x, y, z);
        pending.sizeMultiplier = sizeMultiplier;
    }

    public static PendingSpawn getPending() {
        return PENDING.get();
    }
}
