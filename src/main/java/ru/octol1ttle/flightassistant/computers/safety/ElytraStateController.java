package ru.octol1ttle.flightassistant.computers.safety;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;

public class ElytraStateController implements ITickableComputer {
    private final AirDataComputer data;
    private boolean syncedState;
    private boolean changesPending;

    public ElytraStateController(AirDataComputer data) {
        this.data = data;
    }

    @Override
    public void tick() {
        if (syncedState != data.isFlying) {
            changesPending = false;
        }
        if (changesPending) {
            return;
        }

        if (data.isFlying && data.player.isTouchingWater()) {
            // Retract the wings
            sendSwitchState();
        }

        boolean hasUsableElytra = data.elytraHealth != null && data.elytraHealth > (1 / 432.0f);
        boolean notLookingToClutch = data.pitch > -70.0f;
        if (data.fallDistance > 3.0f && !data.isFlying && hasUsableElytra && notLookingToClutch) {
            // Extend the wings
            sendSwitchState();
        }
    }

    private void sendSwitchState() {
        syncedState = data.isFlying;
        data.player.networkHandler.sendPacket(new ClientCommandC2SPacket(data.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        changesPending = true;
    }

    @Override
    public String getId() {
        return "elytra_state";
    }

    @Override
    public void reset() {
        syncedState = false;
        changesPending = false;
    }
}