package ru.octol1ttle.flightassistant.computers.navigation;

import dev.isxander.yacl3.platform.YACLPlatform;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.InvalidNbtException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.Nullable;
import ru.octol1ttle.flightassistant.FlightAssistant;

public class FlightPlanNbt {
    private static final Path PLAN_PATH = YACLPlatform.getConfigDir().resolve("%s/plans/".formatted(FlightAssistant.MODID));

    public static void write(List<Waypoint> plan, String name) {
        NbtCompound planNbt = new NbtCompound();

        NbtList listNbt = new NbtList();
        for (Waypoint waypoint : plan) {
            listNbt.add(waypoint.writeToNbt(new NbtCompound()));
        }

        planNbt.put("Waypoints", listNbt);

        Path path = PLAN_PATH.resolve("%s.dat".formatted(name));
        try {
            Files.createDirectories(PLAN_PATH);
            NbtIo.write(planNbt, path);
        } catch (IOException e) {
            FlightAssistant.LOGGER.error("IO error detected during flight plan serialization to: %s".formatted(path.toAbsolutePath().toString()), e);
        }
    }

    public static @Nullable List<Waypoint> read(String name) {
        List<Waypoint> loaded = new ArrayList<>();

        Path path = PLAN_PATH.resolve("%s.dat".formatted(name));
        try {
            NbtCompound compound = NbtIo.read(path);
            if (compound == null) {
                return null;
            }

            NbtList list = compound.getList("Waypoints", NbtElement.COMPOUND_TYPE);
            for (NbtElement waypointElement : list) {
                if (waypointElement.getNbtType() != NbtCompound.TYPE) {
                    throw new InvalidNbtException("List item is not NbtCompound");
                }

                NbtCompound waypointNbt = (NbtCompound) waypointElement;
                loaded.add(Waypoint.readFromNbt(waypointNbt));
            }
        } catch (InvalidNbtException e) {
            FlightAssistant.LOGGER.error("Invalid NBT detected during flight plan deserialization from: %s".formatted(path.toAbsolutePath().toString()));
        } catch (IOException e) {
            FlightAssistant.LOGGER.error("IO error detected during flight plan deserialization from: %s".formatted(path.toAbsolutePath().toString()));
        }

        return loaded;
    }
}
