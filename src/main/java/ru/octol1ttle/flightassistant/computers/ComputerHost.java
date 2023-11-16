package ru.octol1ttle.flightassistant.computers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.FlightAssistant;
import ru.octol1ttle.flightassistant.computers.autoflight.AutoFlightComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.FireworkController;
import ru.octol1ttle.flightassistant.computers.autoflight.PitchController;
import ru.octol1ttle.flightassistant.computers.safety.AlertController;
import ru.octol1ttle.flightassistant.computers.safety.GPWSComputer;
import ru.octol1ttle.flightassistant.computers.safety.StallComputer;
import ru.octol1ttle.flightassistant.computers.safety.VoidLevelComputer;

public class ComputerHost {
    public final List<IComputer> faulted;
    public final AirDataComputer data;
    public final StallComputer stall;
    public final GPWSComputer gpws;
    public final VoidLevelComputer voidLevel;
    public final FireworkController firework;
    public final AutoFlightComputer autoflight;
    public final AlertController alert;
    public final TimeComputer time;
    public final PitchController pitch;
    @NotNull
    private final MinecraftClient mc;
    @NotNull
    private final PlayerEntity player;
    private final List<ITickableComputer> tickables;
    private final List<IRenderTickableComputer> renderTickables;

    public ComputerHost(@NotNull MinecraftClient mc) {
        this.mc = mc;
        assert mc.player != null;
        this.player = mc.player;

        this.data = new AirDataComputer(this.mc, player);
        this.time = new TimeComputer();
        this.firework = new FireworkController(time, data, player.getInventory(), this.mc.interactionManager);
        this.stall = new StallComputer(firework, data);
        this.voidLevel = new VoidLevelComputer(data, firework, stall);
        this.gpws = new GPWSComputer(data);

        this.pitch = new PitchController(data, stall, time, voidLevel, gpws);
        this.voidLevel.pitch = this.pitch;
        this.gpws.pitch = this.pitch;

        this.autoflight = new AutoFlightComputer(data, gpws, firework);
        this.alert = new AlertController(this, this.mc.getSoundManager());

        // computers are sorted in the order they should be ticked to avoid errors
        this.tickables = new ArrayList<>(List.of(data, stall, gpws, voidLevel, firework, autoflight, alert));
        this.renderTickables = new ArrayList<>(List.of(time, pitch));
        Collections.reverse(this.tickables); // we tick computers in reverse, so reverse the collection so that the order is correct
        Collections.reverse(this.renderTickables);

        this.faulted = new ArrayList<>(tickables.size() + renderTickables.size());
    }

    public void tick() {
        for (int i = tickables.size() - 1; i >= 0; i--) {
            ITickableComputer computer = tickables.get(i);
            try {
                computer.tick();
            } catch (Exception e) {
                FlightAssistant.LOGGER.error("Exception ticking computer", e);
                faulted.add(computer);
                tickables.remove(computer);
            }
        }
    }

    public void render() {
        for (int i = renderTickables.size() - 1; i >= 0; i--) {
            IRenderTickableComputer computer = renderTickables.get(i);
            try {
                computer.tick();
            } catch (Exception e) {
                FlightAssistant.LOGGER.error("Exception ticking computer (on render)", e);
                faulted.add(computer);
                renderTickables.remove(computer);
            }
        }
    }
}
