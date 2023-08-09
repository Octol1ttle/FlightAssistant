package net.torocraft.flighthud.compat;

public class ImmediatelyFastBatchingAccessor {
    public static void beginHudBatching() {
        net.raphimc.immediatelyfast.feature.batching.BatchingBuffers.beginHudBatching();
    }

    public static void endHudBatching() {
        net.raphimc.immediatelyfast.feature.batching.BatchingBuffers.endHudBatching();
    }
}
