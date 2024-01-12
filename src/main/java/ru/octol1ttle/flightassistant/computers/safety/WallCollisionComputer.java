package ru.octol1ttle.flightassistant.computers.safety;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;

public class WallCollisionComputer implements ITickableComputer {
    private final AirDataComputer data;
    public boolean collided = false;

    public WallCollisionComputer(AirDataComputer data) {
        this.data = data;
    }

    @Override
    public void tick() {
        DamageSource recent = data.player.getRecentDamageSource();
        if (recent != null) {
            collided = recent.isOf(DamageTypes.FLY_INTO_WALL);
        }
    }

    @Override
    public String getId() {
        return "collision_det";
    }

    @Override
    public void reset() {
        collided = false;
    }
}
