package net.torocraft.flighthud.compatibility;

public class ImmediatelyFastBatchingAccessor {
    public static void beginHudBatching() {
        net.raphimc.immediatelyfastapi.ImmediatelyFastApi.getApiImpl().getBatching().beginHudBatching();
    }

    public static void endHudBatching() {
        net.raphimc.immediatelyfastapi.ImmediatelyFastApi.getApiImpl().getBatching().endHudBatching();
    }
}
