package ru.octol1ttle.flightassistant.compatibility;

public class ImmediatelyFastBatchingAccessor {
    public static void beginHudBatching() {
        net.raphimc.immediatelyfastapi.ImmediatelyFastApi.getApiImpl().getBatching().beginHudBatching();
    }

    public static void endHudBatching() {
        net.raphimc.immediatelyfastapi.ImmediatelyFastApi.getApiImpl().getBatching().endHudBatching();
    }
}
