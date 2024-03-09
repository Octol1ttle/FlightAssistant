package ru.octol1ttle.flightassistant.computers.navigation;

import java.util.ArrayList;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import ru.octol1ttle.flightassistant.FAMathHelper;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.PitchController;

public class FlightPlanner extends ArrayList<Waypoint> implements ITickableComputer {
    private final AirDataComputer data;
    private @Nullable Waypoint targetWaypoint;
    public boolean landingInProgress = false;
    public @Nullable Integer landAltitude = null;

    public FlightPlanner(AirDataComputer data) {
        this.data = data;
    }

    @Override
    public void tick() {
        landAltitude = null;
        if (targetWaypoint != null && !this.contains(targetWaypoint)) {
            nextWaypoint(MathHelper.floor(data.altitude()));
        }

        if (targetWaypoint == null) {
            return;
        }

        Vector2d target = new Vector2d(targetWaypoint.targetPosition());

        if (targetWaypoint instanceof LandingWaypoint) {
            landingInProgress = tickLanding(target);
            if (landingInProgress) {
                setLandTargetAltitude(landAltitude);
            }
            return;
        } else {
            landingInProgress = false;
            landAltitude = null;
        }

        float altitude = targetWaypoint.targetAltitude() != null ? targetWaypoint.targetAltitude() : data.altitude();
        if (target.sub(data.position().x, data.position().z).length() <= 20.0f && Math.abs(altitude - data.altitude()) <= 10.0f) {
            nextWaypoint(MathHelper.floor(altitude));
        }
    }

    private boolean tickLanding(Vector2d target) {
        double distance = Vector2d.distance(target.x, target.y, data.position().x, data.position().z);
        if (distance <= 10.0 && data.heightAboveGround() <= 3.0f) {
            nextWaypoint(null);
            return false;
        }

        BlockPos landPos = data.findGround(new BlockPos.Mutable(target.x, 320, target.y));
        if (landPos == null) {
            return false;
        }
        landAltitude = landPos.getY();
        if (distance / Math.min(data.heightAboveGround(), Math.abs(data.altitude() - landAltitude)) > AirDataComputer.OPTIMUM_GLIDE_RATIO) {
            return false;
        }

        float landAngle = FAMathHelper.toDegrees(MathHelper.atan2(landAltitude - data.altitude(), distance));
        if (landAngle > 5.0f || landAngle < PitchController.DESCEND_PITCH + 10) {
            return false;
        }
        BlockHitResult result = data.world().raycast(new RaycastContext(data.position(), new Vec3d(target.x, landAltitude, target.y), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, data.player()));

        return result.getType() == HitResult.Type.MISS || Math.abs(result.getPos().y - landAltitude) <= 5.0;
    }

    private void nextWaypoint(Integer altitude) {
        int nextIndex = this.indexOf(targetWaypoint) + 1;
        if (waypointExistsAt(nextIndex)) {
            targetWaypoint = this.get(nextIndex);
            setLandTargetAltitude(altitude);
        } else {
            targetWaypoint = null;
        }
    }


    public @Nullable Integer getManagedSpeed() {
        if (targetWaypoint == null) {
            return null;
        }
        return targetWaypoint.targetSpeed();
    }

    public @Nullable Integer getManagedAltitude() {
        if (targetWaypoint == null) {
            return null;
        }
        return targetWaypoint.targetAltitude();
    }

    public @Nullable Float getManagedHeading() {
        if (targetWaypoint == null) {
            return null;
        }

        Vec3d current = data.position();
        Vector2d target = targetWaypoint.targetPosition();

        return AirDataComputer.toHeading(FAMathHelper.toDegrees(MathHelper.atan2(-(target.x - current.x), target.y - current.z)));
    }

    public @Nullable Vector2d getTargetPosition() {
        if (targetWaypoint == null) {
            return null;
        }

        return targetWaypoint.targetPosition();
    }

    public @Nullable Double getDistanceToNextWaypoint() {
        Vector2d planPos = getTargetPosition();
        if (planPos == null) {
            return null;
        }

        return Vector2d.distance(planPos.x, planPos.y, data.position().x, data.position().z);
    }

    public @Nullable Integer getMinimums(int ground) {
        if (targetWaypoint instanceof LandingWaypoint land) {
            return land.minimums(ground);
        }

        return null;
    }

    public @Nullable Text formatMinimums() {
        if (targetWaypoint instanceof LandingWaypoint land) {
            return land.formatMinimums();
        }

        return null;
    }

    public boolean isBelowMinimums() {
        Integer minimums = getMinimums(data.groundLevel);
        return data.isFlying() && landingInProgress && minimums != null && data.altitude() <= minimums;
    }

    public void execute(int waypointIndex) {
        targetWaypoint = this.get(waypointIndex);
        setLandTargetAltitude(MathHelper.floor(data.altitude()));
    }

    private void setLandTargetAltitude(Integer altitude) {
        if (targetWaypoint instanceof LandingWaypoint land && altitude != null) {
            land.setTargetAltitude(altitude);
        }
    }

    public boolean isOnApproach() {
        return targetWaypoint instanceof LandingWaypoint;
    }

    public boolean waypointExistsAt(int index) {
        return index < this.size();
    }

    @Override
    public Waypoint set(int index, Waypoint element) {
        if (index == this.indexOf(targetWaypoint)) {
            targetWaypoint = element;
        }
        return super.set(index, element);
    }

    @Override
    public void reset() {
        targetWaypoint = null;
        landingInProgress = false;
        landAltitude = null;
    }

    @Override
    public String getId() {
        return "flt_plan";
    }
}
