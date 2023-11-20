package ru.octol1ttle.flightassistant.computers.safety;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.sound.SoundManager;
import ru.octol1ttle.flightassistant.AlertSoundInstance;
import ru.octol1ttle.flightassistant.FlightAssistant;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.alerts.autoflight.ATHRNoFireworksInHotbarAlert;
import ru.octol1ttle.flightassistant.alerts.fault.ComputerFaultAlert;
import ru.octol1ttle.flightassistant.alerts.firework.FireworkCountZeroAlert;
import ru.octol1ttle.flightassistant.alerts.firework.FireworkDelayedResponseAlert;
import ru.octol1ttle.flightassistant.alerts.firework.FireworkLowCountAlert;
import ru.octol1ttle.flightassistant.alerts.firework.FireworkNoResponseAlert;
import ru.octol1ttle.flightassistant.alerts.firework.FireworkUnsafeAlert;
import ru.octol1ttle.flightassistant.alerts.nav.ApproachingVoidDamageLevelAlert;
import ru.octol1ttle.flightassistant.alerts.nav.gpws.ExcessiveDescentAlert;
import ru.octol1ttle.flightassistant.alerts.nav.gpws.ExcessiveTerrainClosureAlert;
import ru.octol1ttle.flightassistant.alerts.other.ElytraHealthLowAlert;
import ru.octol1ttle.flightassistant.alerts.other.StallAlert;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;

public class AlertController implements ITickableComputer {
    public final List<AbstractAlert> activeAlerts;
    private final ComputerHost host;
    private final SoundManager manager;
    private final List<AbstractAlert> allAlerts;
    private final List<AbstractAlert> toDelete;

    public AlertController(ComputerHost host, SoundManager manager) {
        this.host = host;
        this.manager = manager;
        // TODO: ECAM actions
        allAlerts = new ArrayList<>(List.of(
                new StallAlert(this.host.stall, this.host.data),
                new ExcessiveDescentAlert(this.host.data, this.host.gpws), new ExcessiveTerrainClosureAlert(this.host.gpws),
                new ComputerFaultAlert(this.host),
                new ApproachingVoidDamageLevelAlert(this.host.voidLevel),
                new ElytraHealthLowAlert(this.host.data),
                new FireworkUnsafeAlert(this.host.firework),
                new FireworkCountZeroAlert(this.host.firework),
                new FireworkNoResponseAlert(this.host.firework), new FireworkDelayedResponseAlert(this.host.firework),
                new FireworkLowCountAlert(this.host.firework),
                new ATHRNoFireworksInHotbarAlert(this.host.firework)));
        activeAlerts = new ArrayList<>(allAlerts.size());
        toDelete = new ArrayList<>(allAlerts.size());
    }

    public void tick() {
        if (HudComponent.CONFIG == null) { // HUD hidden
            for (AbstractAlert alert : activeAlerts) {
                alert.played = false;

                if (alert.soundInstance != null) {
                    manager.stop(alert.soundInstance);
                    alert.soundInstance = null;
                }
            }
            return;
        }

        for (AbstractAlert alert : allAlerts) {
            try {
                if (alert.isTriggered()) {
                    if (!activeAlerts.contains(alert)) {
                        activeAlerts.add(alert);
                    }
                    continue;
                }
            } catch (Exception e) {
                FlightAssistant.LOGGER.error("Exception triggering alert", e);
                toDelete.add(alert);
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

        if (allAlerts.removeAll(toDelete)) {
            toDelete.clear();
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

            if (interrupt || alert.played && !data.repeat() || alert.dismissed) {
                continue;
            }

            alert.soundInstance = new AlertSoundInstance(data.sound(), data.volume(), host.data.player, data.repeat());
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

    @Override
    public String getId() {
        return "alert_mgr";
    }

    @Override
    public void reset() {
        activeAlerts.clear();
        allAlerts.clear();
        toDelete.clear();
    }
}
