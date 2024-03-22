package ru.octol1ttle.flightassistant.serialization.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jetbrains.annotations.Nullable;
import ru.octol1ttle.flightassistant.serialization.api.ISerializableObject;
import ru.octol1ttle.flightassistant.serialization.api.IFlightPlanSerializer;

public class JsonSerializer implements IFlightPlanSerializer {
    private static final Gson GSON = new Gson();

    @Override
    public @Nullable ISerializableObject read(Path path, String fileName) throws IOException {
        Path filePath = path.resolve("%s.json".formatted(fileName));
        if (!Files.exists(filePath)) {
            return null;
        }

        try (FileReader reader = new FileReader(filePath.toFile())) {
            return new JsonSerializableObject(GSON.fromJson(reader, JsonElement.class).getAsJsonObject());
        }
    }

    @Override
    public void write(Path path, String fileName, ISerializableObject object) throws IOException {
        if (object instanceof JsonSerializableObject serializable) {
            try (FileWriter writer = new FileWriter(path.resolve("%s.json".formatted(fileName)).toFile())) {
                GSON.toJson(serializable.getJsonObject(), writer);
                return;
            }
        }

        throw new IllegalStateException();
    }
}
