package ru.octol1ttle.flightassistant.commands.plan;

import ru.octol1ttle.flightassistant.HudRenderer;
import ru.octol1ttle.flightassistant.computers.ComputerHost;

public class ExecutePlanCommand {
    public static int execute(int fromWaypoint) {
        ComputerHost host = HudRenderer.getHost();
        if (host != null) {
            host.plan.execute(fromWaypoint);
        }
        return 0;
    }
}