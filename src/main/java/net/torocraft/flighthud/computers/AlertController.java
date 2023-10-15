package net.torocraft.flighthud.computers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.sound.SoundManager;
import net.torocraft.flighthud.AlertSoundInstance;
import net.torocraft.flighthud.alerts.AbstractAlert;
import net.torocraft.flighthud.alerts.AlertSoundData;
import net.torocraft.flighthud.alerts.StallAlert;
import net.torocraft.flighthud.alerts.autoflight.ATHRSpeedNotSetAlert;
import net.torocraft.flighthud.alerts.nav.ApproachingVoidDamageLevelAlert;
import net.torocraft.flighthud.alerts.nav.gpws.ExcessiveDescentAlert;
import net.torocraft.flighthud.alerts.nav.gpws.ExcessiveTerrainClosureAlert;

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
                if (interrupt || alert.dismissed) {
                    manager.stop(alert.soundInstance);
                    alert.soundInstance = null;
                    continue;
                }

                if (manager.isPlaying(alert.soundInstance)) {
                    interrupt = true;
                }

                continue;
            }

            if (interrupt || alert.dismissed) {
                continue;
            }

            alert.soundInstance = new AlertSoundInstance(data.sound(), data.volume(), computer.player, data.repeat());
            manager.play(alert.soundInstance);

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
