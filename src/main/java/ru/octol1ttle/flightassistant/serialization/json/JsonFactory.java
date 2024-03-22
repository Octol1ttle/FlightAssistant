package ru.octol1ttle.flightassistant.serialization.json;

import ru.octol1ttle.flightassistant.serialization.api.ISerializableFactory;
import ru.octol1ttle.flightassistant.serialization.api.ISerializableList;
import ru.octol1ttle.flightassistant.serialization.api.ISerializableObject;
import ru.octol1ttle.flightassistant.serialization.api.IFlightPlanSerializer;

public class JsonFactory implements ISerializableFactory {
    @Override
    public ISerializableObject createObject() {
        return new JsonSerializableObject();
    }

    @Override
    public ISerializableList<ISerializableObject> createList(int capacity) {
        return new JsonSerializableList<>(capacity);
    }

    @Override
    public IFlightPlanSerializer createSerializer() {
        return new JsonSerializer();
    }
}
