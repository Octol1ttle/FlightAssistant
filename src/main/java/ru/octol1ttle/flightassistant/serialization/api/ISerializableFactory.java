package ru.octol1ttle.flightassistant.serialization.api;

public interface ISerializableFactory {
    ISerializableObject createObject();
    ISerializableList<ISerializableObject> createList(int capacity);
    IFlightPlanSerializer createSerializer();
}
