package ru.octol1ttle.flightassistant.serialization.json;

import com.google.gson.JsonObject;
import ru.octol1ttle.flightassistant.serialization.api.ISerializableList;
import ru.octol1ttle.flightassistant.serialization.api.ISerializableObject;

public class JsonSerializableObject implements ISerializableObject {
    private final JsonObject json;

    JsonSerializableObject() {
        this.json = new JsonObject();
    }

    JsonSerializableObject(JsonObject json) {
        this.json = json;
    }

    JsonObject getJsonObject() {
        return json;
    }

    @Override
    public boolean contains(String key) {
        return json.has(key);
    }

    @Override
    public ISerializableObject get(String key) {
        return new JsonSerializableObject((JsonObject) json.get(key));
    }

    @Override
    public void put(String key, ISerializableObject object) {
        if (object instanceof JsonSerializableObject serializable) {
            json.add(key, serializable.json);
            return;
        }
        if (object instanceof JsonSerializableList<?> serializable) {
            json.add(key, serializable.getArray());
            return;
        }

        throw new IllegalStateException();
    }

    @Override
    public boolean getBoolean(String key) {
        return json.get(key).getAsBoolean();
    }

    @Override
    public void putBoolean(String key, boolean b) {
        json.addProperty(key, b);
    }

    @Override
    public int getInt(String key) {
        return json.get(key).getAsInt();
    }

    @Override
    public void putInt(String key, int i) {
        json.addProperty(key, i);
    }

    @Override
    public double getDouble(String key) {
        return json.get(key).getAsDouble();
    }

    @Override
    public void putDouble(String key, double d) {
        json.addProperty(key, d);
    }

    @Override
    public String getString(String key) {
        return json.get(key).getAsString();
    }

    @Override
    public void putString(String key, String s) {
        json.addProperty(key, s);
    }

    @Override
    public ISerializableList<ISerializableObject> getList(String key) {
        return new JsonSerializableList<>(json.get(key).getAsJsonArray());
    }
}
