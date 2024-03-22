package ru.octol1ttle.flightassistant.serialization.api;

public interface ISerializableList<T extends ISerializableObject> extends ISerializableObject, Iterable<ISerializableObject> {
    void add(T object);
}
