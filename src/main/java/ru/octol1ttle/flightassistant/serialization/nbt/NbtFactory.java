package ru.octol1ttle.flightassistant.serialization.nbt;

import ru.octol1ttle.flightassistant.serialization.api.ISerializableFactory;
import ru.octol1ttle.flightassistant.serialization.api.ISerializableList;
import ru.octol1ttle.flightassistant.serialization.api.ISerializableObject;
import ru.octol1ttle.flightassistant.serialization.api.IFlightPlanSerializer;

public class NbtFactory implements ISerializableFactory {
    @Override
    public ISerializableObject createObject() {
        return new NbtSerializableObject();
    }

    @Override
    public ISerializableList<ISerializableObject> createList(int capacity) {
        return new NbtSerializableList<>();
    }

    @Override
    public IFlightPlanSerializer createSerializer() {
        return new NbtSerializer();
    }
}
