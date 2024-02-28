package ru.octol1ttle.flightassistant.config;

import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import java.util.Locale;
import net.minecraft.text.Text;

public class ComputerConfig {
    @SerialEntry
    public FlightProtectionsMode protectionsMode = FlightProtectionsMode.NO_OVERLAYS;

    public enum FlightProtectionsMode implements NameableEnum {
        FULL,
        // TODO: LIMIT TO NO_OVERLAYS ON SERVERS
        NO_OVERLAYS,
        DISABLED;

        @Override
        public Text getDisplayName() {
            return Text.translatable("config.flightassistant.computers.protections." + name().toLowerCase(Locale.ROOT));
        }
    }
}
