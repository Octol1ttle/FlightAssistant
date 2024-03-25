package ru.octol1ttle.flightassistant.serialization.nbt;

import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import org.jetbrains.annotations.Nullable;
import ru.octol1ttle.flightassistant.serialization.api.IFlightPlanSerializer;
import ru.octol1ttle.flightassistant.serialization.api.ISerializableObject;

public class NbtSerializer implements IFlightPlanSerializer {
    @Override
    public @Nullable ISerializableObject read(Path path, String fileName) throws IOException {
        NbtCompound compound = NbtIo.read(path.resolve("%s.dat".formatted(fileName)));
        if (compound == null) {
            return null;
        }
        return new NbtSerializableObject(compound);
    }

    @Override
    public void write(Path path, String fileName, ISerializableObject object) throws IOException {
        if (object instanceof NbtSerializableObject serializable) {
            NbtIo.write(serializable.getCompound(), path.resolve("%s.dat".formatted(fileName)));
            return;
        }

        throw new IllegalStateException();
    }
}
