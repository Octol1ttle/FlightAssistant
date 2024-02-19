package net.torocraft.flighthud.mixin;

import net.minecraft.util.math.Matrix3f;
import net.torocraft.flighthud.shims.Matrix3fShim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Matrix3f.class)
public abstract class Matrix3fShimMixin implements Matrix3fShim {

    @Shadow
    protected float a00;
    @Shadow
    protected float a01;
    @Shadow
    protected float a02;
    @Shadow
    protected float a10;
    @Shadow
    protected float a11;
    @Shadow
    protected float a12;
    @Shadow
    protected float a20;
    @Shadow
    protected float a21;
    @Shadow
    protected float a22;


    // shim for JOML#getRowColumn in 1.19.3+
    @Override
    public float flightassistant$getRowColumn(int column, int row) {
        if (column == 0) {
            switch (row) {
                case 0:
                    return this.a00;
                case 1:
                    return this.a01;
                case 2:
                    return this.a02;
                default:
                    break;
            }
        } else if (column == 1) {
            switch (row) {
                case 0:
                    return this.a10;
                case 1:
                    return this.a11;
                case 2:
                    return this.a12;
                default:
                    break;
            }
        } else if (column == 2) {
            switch (row) {
                case 0:
                    return this.a20;
                case 1:
                    return this.a21;
                case 2:
                    return this.a22;
                default:
                    break;
            }
        }
        throw new IllegalArgumentException();
    }

}
