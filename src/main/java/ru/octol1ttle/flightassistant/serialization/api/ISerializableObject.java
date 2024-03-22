package ru.octol1ttle.flightassistant.serialization.api;

public interface ISerializableObject {
    default boolean contains(String key) {
        throw new AssertionError();
    }

    default ISerializableObject get(String key) {
        throw new AssertionError();
    }

    default void put(String key, ISerializableObject object) {
        throw new AssertionError();
    }

    default boolean getBoolean(String key) {
        throw new AssertionError();
    }

    default void putBoolean(String key, boolean b) {
        throw new AssertionError();
    }

    default int getInt(String key) {
        throw new AssertionError();
    }

    default void putInt(String key, int i) {
        throw new AssertionError();
    }

    default double getDouble(String key) {
        throw new AssertionError();
    }

    default void putDouble(String key, double d) {
        throw new AssertionError();
    }

    default String getString(String key) {
        throw new AssertionError();
    }

    default void putString(String key, String s) {
        throw new AssertionError();
    }

    default ISerializableList<ISerializableObject> getList(String key) {
        throw new AssertionError();
    }
}
