package ru.octol1ttle.flightassistant.serialization.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.serialization.api.ISerializableList;
import ru.octol1ttle.flightassistant.serialization.api.ISerializableObject;

public class JsonSerializableList<T extends ISerializableObject> implements ISerializableList<T> {
    private final JsonArray array;

    public JsonSerializableList(int capacity) {
        this.array = new JsonArray(capacity);
    }

    public JsonSerializableList(JsonArray array) {
        this.array = array;
    }

    JsonArray getArray() {
        return array;
    }

    @Override
    public void add(T object) {
        if (object instanceof JsonSerializableObject serializable) {
            array.add(serializable.getJsonObject());
            return;
        }

        throw new IllegalStateException();
    }

    @NotNull
    @Override
    public Iterator<ISerializableObject> iterator() {
        List<ISerializableObject> list = new ArrayList<>(array.size());

        array.forEach(element -> list.add(new JsonSerializableObject((JsonObject) element)));
        return list.iterator();
    }
}
