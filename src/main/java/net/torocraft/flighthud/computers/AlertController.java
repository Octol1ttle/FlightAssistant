package net.torocraft.flighthud.computers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvent;
import net.torocraft.flighthud.AlertSoundInstance;
import net.torocraft.flighthud.alerts.AlertSoundData;
import net.torocraft.flighthud.alerts.IAlert;
import net.torocraft.flighthud.alerts.StallAlert;
import net.torocraft.flighthud.alerts.nav.gpws.ExcessiveDescentAlert;
import net.torocraft.flighthud.alerts.nav.gpws.ExcessiveTerrainClosureAlert;
import org.jetbrains.annotations.Nullable;

public class AlertController {
    public final List<IAlert> activeAlerts;
    private final FlightComputer computer;
    private final SoundManager manager;
    private final IAlert[] ALERTS;
    private @Nullable AlertSoundInstance activeSound;

    public AlertController(FlightComputer computer, SoundManager manager) {
        this.computer = computer;
        this.manager = manager;
        ALERTS = new IAlert[]{
                new ExcessiveDescentAlert(computer), new ExcessiveTerrainClosureAlert(computer), // GPWS
                new StallAlert(computer)
        };
        activeAlerts = new ArrayList<>();
    }

    public void tick() {
        for (IAlert alert : ALERTS) {
            if (alert.isTriggered()) {
                if (!activeAlerts.contains(alert)) {
                    activeAlerts.add(alert);
                }
                continue;
            }

            if (!activeAlerts.contains(alert)) {
                continue;
            }

            SoundEvent sound = alert.getAlertSoundData().sound;
            if (sound != null && activeSound != null && sound.getId().equals(activeSound.getId())) {
                manager.stop(activeSound);
                activeSound = null;
            }

            activeAlerts.remove(alert);
        }

        activeAlerts.sort(Comparator.comparingDouble(alert -> alert.getAlertSoundData().getPriority()));
        for (IAlert alert : activeAlerts) {
            AlertSoundData data = alert.getAlertSoundData();
            if (data.sound == null) {
                continue;
            }

            if (activeSound == null || !activeSound.getId().equals(data.sound.getId())) {
                if (activeSound != null) {
                    manager.stop(activeSound);
                }
                activeSound = new AlertSoundInstance(data.sound, data.volume, computer.player, data.repeat);
                manager.play(activeSound);
            }

            break;
        }
    }
}
