package ru.octol1ttle.flightassistant.serialization.nbt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.serialization.api.ISerializableList;
import ru.octol1ttle.flightassistant.serialization.api.ISerializableObject;

public class NbtSerializableList<T extends ISerializableObject> implements ISerializableList<T> {
    private final NbtList nbtList;

    public NbtSerializableList() {
        this.nbtList = new NbtList();
    }

    public NbtSerializableList(NbtList nbtList) {
        this.nbtList = nbtList;
    }

    public NbtList getNbtList() {
        return nbtList;
    }

    @Override
    public void add(T object) {
        if (object instanceof NbtSerializableObject serializable) {
            nbtList.add(serializable.getCompound());
            return;
        }

        throw new IllegalStateException();
    }

    @NotNull
    @Override
    public Iterator<ISerializableObject> iterator() {
        List<ISerializableObject> list = new ArrayList<>(nbtList.size());

        nbtList.forEach(element -> list.add(new NbtSerializableObject((NbtCompound) element)));
        return list.iterator();
    }
}
