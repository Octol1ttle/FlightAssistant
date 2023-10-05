package net.torocraft.flighthud.computers;

import net.minecraft.entity.player.PlayerEntity;

public class PitchController {

    /**
     * USE MINECRAFT PITCH (minus is up and plus is down)
     **/
    public float targetPitch;
    public boolean forceLevelOff = false;

    public void tick(PlayerEntity player, float tickDelta) {
        if (forceLevelOff) {
            player.setPitch(player.getPitch() - player.getPitch() * tickDelta);
            return;
        }

        player.setPitch(player.getPitch() - (player.getPitch() - targetPitch) * tickDelta);
    }
}
