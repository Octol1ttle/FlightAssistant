package net.torocraft.flighthud;

import net.minecraft.util.math.Vec3d;

public class Util {
    public static Vec3d copyVec3d(Vec3d from) {
        return new Vec3d(from.x, from.y, from.z);
    }
}
