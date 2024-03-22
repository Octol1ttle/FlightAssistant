package ru.octol1ttle.flightassistant.serialization.nbt;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import ru.octol1ttle.flightassistant.serialization.api.ISerializableList;
import ru.octol1ttle.flightassistant.serialization.api.ISerializableObject;

public class NbtSerializableObject implements ISerializableObject {
    private final NbtCompound compound;

    NbtSerializableObject() {
        this.compound = new NbtCompound();
    }

    NbtSerializableObject(NbtCompound compound) {
        this.compound = compound;
    }

    NbtCompound getCompound() {
        return compound;
    }

    @Override
    public boolean contains(String key) {
        return compound.contains(key);
    }

    @Override
    public ISerializableObject get(String key) {
        return new NbtSerializableObject(compound.getCompound(key));
    }

    @Override
    public void put(String key, ISerializableObject object) {
        if (object instanceof NbtSerializableObject serializable) {
            compound.put(key, serializable.compound);
            return;
        }
        if (object instanceof NbtSerializableList<?> serializable) {
            compound.put(key, serializable.getNbtList());
            return;
        }

        throw new IllegalStateException();
    }

    @Override
    public boolean getBoolean(String key) {
        return compound.getBoolean(key);
    }

    @Override
    public void putBoolean(String key, boolean b) {
        compound.putBoolean(key, b);
    }

    @Override
    public int getInt(String key) {
        return compound.getInt(key);
    }

    @Override
    public void putInt(String key, int i) {
        compound.putInt(key, i);
    }

    @Override
    public double getDouble(String key) {
        return compound.getDouble(key);
    }

    @Override
    public void putDouble(String key, double d) {
        compound.putDouble(key, d);
    }

    @Override
    public String getString(String key) {
        return compound.getString(key);
    }

    @Override
    public void putString(String key, String s) {
        compound.putString(key, s);
    }

    @Override
    public ISerializableList<ISerializableObject> getList(String key) {
        return new NbtSerializableList<>(compound.getList(key, NbtElement.COMPOUND_TYPE));
    }
}
