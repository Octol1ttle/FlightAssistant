package ru.octol1ttle.flightassistant.computers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.sound.SoundManager;
import ru.octol1ttle.flightassistant.AlertSoundInstance;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.alerts.StallAlert;
import ru.octol1ttle.flightassistant.alerts.autoflight.ATHRSpeedNotSetAlert;
import ru.octol1ttle.flightassistant.alerts.nav.ApproachingVoidDamageLevelAlert;
import ru.octol1ttle.flightassistant.alerts.nav.gpws.ExcessiveDescentAlert;
import ru.octol1ttle.flightassistant.alerts.nav.gpws.ExcessiveTerrainClosureAlert;

public class AlertController {
    public final List<AbstractAlert> activeAlerts;
    private final FlightComputer computer;
    private final SoundManager manager;
    private final AbstractAlert[] ALERTS;

    public AlertController(FlightComputer computer, SoundManager manager) {
        this.computer = computer;
        this.manager = manager;
        ALERTS = new AbstractAlert[]{
                new StallAlert(computer),
                new ExcessiveDescentAlert(computer), new ExcessiveTerrainClosureAlert(computer), // GPWS
                new ApproachingVoidDamageLevelAlert(computer),
                new ATHRSpeedNotSetAlert(computer) // Autoflight
        };
        activeAlerts = new ArrayList<>();
    }

    public void tick() {
        for (AbstractAlert alert : ALERTS) {
            if (alert.isTriggered()) {
                if (!activeAlerts.contains(alert)) {
                    activeAlerts.add(alert);
                }
                continue;
            }

            if (!activeAlerts.contains(alert)) {
                continue;
            }

            alert.played = false;
            alert.hidden = false;
            alert.dismissed = false;

            if (alert.soundInstance != null) {
                manager.stop(alert.soundInstance);
                alert.soundInstance = null;
            }

            activeAlerts.remove(alert);
        }

        boolean interrupt = false;
        activeAlerts.sort(Comparator.comparingDouble(alert -> alert.getAlertSoundData().priority()));
        for (AbstractAlert alert : activeAlerts) {
            AlertSoundData data = alert.getAlertSoundData();
            if (data.sound() == null) {
                continue;
            }

            if (alert.soundInstance != null) {
                boolean soundChanged = !data.sound().getId().equals(alert.soundInstance.getId());
                if (soundChanged || interrupt || alert.dismissed) {
                    manager.stop(alert.soundInstance);
                    alert.soundInstance = null;
                    if (!interrupt) {
                        alert.played = false;
                    }
                    if (!soundChanged) {
                        continue;
                    }
                }

                if (manager.isPlaying(alert.soundInstance)) {
                    interrupt = true;
                }

                if (!soundChanged) {
                    continue;
                }
            }

            if (interrupt || alert.played || alert.dismissed) {
                continue;
            }

            alert.soundInstance = new AlertSoundInstance(data.sound(), data.volume(), computer.player, data.repeat());
            manager.play(alert.soundInstance);
            alert.played = true;

            interrupt = true;
        }
    }

    public void dismiss(AlertSoundData data) {
        boolean anyDismissed = false;
        for (AbstractAlert alert : activeAlerts) {
            if (!alert.dismissed && alert.getAlertSoundData().equals(data)) {
                alert.dismissed = true;
                anyDismissed = true;
            }
        }

        if (anyDismissed) {
            return;
        }

        for (AbstractAlert alert : activeAlerts) {
            if (!alert.hidden && alert.getAlertSoundData().equals(data)) {
                alert.hidden = true;
                return;
            }
        }
    }
}
