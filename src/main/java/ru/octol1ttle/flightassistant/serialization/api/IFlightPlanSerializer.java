package ru.octol1ttle.flightassistant.serialization.api;

import java.io.IOException;
import java.nio.file.Path;
import org.jetbrains.annotations.Nullable;

public interface IFlightPlanSerializer {
    @Nullable ISerializableObject read(Path path, String fileName) throws IOException;

    void write(Path path, String fileName, ISerializableObject object) throws IOException;
}
