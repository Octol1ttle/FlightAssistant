package ru.octol1ttle.flightassistant.config;

import dev.isxander.yacl3.platform.YACLPlatform;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import ru.octol1ttle.flightassistant.FlightAssistant;
import ru.octol1ttle.flightassistant.computers.navigation.LandingMinimums;
import ru.octol1ttle.flightassistant.computers.navigation.LandingWaypoint;
import ru.octol1ttle.flightassistant.computers.navigation.Waypoint;

public class FlightPlanNbt {
    private static final Path PLAN_PATH = YACLPlatform.getConfigDir().resolve("%s/plans/".formatted(FlightAssistant.MODID));

    public static void write(List<Waypoint> plan, String name) {
        NbtCompound planNbt = new NbtCompound();

        NbtList listNbt = new NbtList();
        for (Waypoint waypoint : plan) {
            NbtCompound waypointNbt = new NbtCompound();
            waypointNbt.putDouble("TargetX", waypoint.targetPosition().x);
            waypointNbt.putDouble("TargetZ", waypoint.targetPosition().y);

            if (waypoint instanceof LandingWaypoint landing) {
                waypointNbt.putBoolean("IsLanding", true);

                NbtCompound minimumsNbt = new NbtCompound();
                minimumsNbt.putString("AltitudeType", landing.minimums.type().nbtName);
                minimumsNbt.putInt("Altitude", landing.minimums.altitude());

                waypointNbt.put("Minimums", minimumsNbt);
            } else {
                waypointNbt.putBoolean("IsLanding", false);
                if (waypoint.targetAltitude() != null) {
                    waypointNbt.putInt("TargetAltitude", waypoint.targetAltitude());
                }
                if (waypoint.targetSpeed() != null) {
                    waypointNbt.putInt("TargetSpeed", waypoint.targetSpeed());
                }
            }

            listNbt.add(waypointNbt);
        }

        planNbt.put("Waypoints", listNbt);

        Path path = PLAN_PATH.resolve("%s.dat".formatted(name));
        try {
            Files.createDirectories(PLAN_PATH);
            NbtIo.write(planNbt, path);
        } catch (IOException e) {
            FlightAssistant.LOGGER.error("Failed to serialize flight plan to: %s".formatted(path.toAbsolutePath().toString()), e);
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
                    throw new IOException("List item is not NbtCompound");
                }

                NbtCompound waypointNbt = (NbtCompound) waypointElement;
                Vector2d targetPosition = new Vector2d(waypointNbt.getDouble("TargetX"), waypointNbt.getDouble("TargetZ"));
                if (waypointNbt.getBoolean("IsLanding")) {
                    NbtCompound minimumsNbt = waypointNbt.getCompound("Minimums");

                    String typeString = minimumsNbt.getString("AltitudeType");
                    LandingMinimums.AltitudeType type = switch (typeString) {
                        case "Absolute" -> LandingMinimums.AltitudeType.ABSOLUTE;
                        case "AboveGround" -> LandingMinimums.AltitudeType.ABOVE_GROUND;
                        default -> throw new IOException("Unknown altitude type: %s".formatted(typeString));
                    };
                    LandingMinimums minimums = new LandingMinimums(type, minimumsNbt.getInt("Altitude"));

                    loaded.add(new LandingWaypoint(targetPosition, minimums));
                } else {
                    Integer targetAltitude = null;
                    Integer targetSpeed = null;
                    if (waypointNbt.contains("TargetAltitude", NbtElement.INT_TYPE)) {
                        targetAltitude = waypointNbt.getInt("TargetAltitude");
                    }
                    if (waypointNbt.contains("TargetSpeed", NbtElement.INT_TYPE)) {
                        targetSpeed = waypointNbt.getInt("TargetSpeed");
                    }

                    loaded.add(new Waypoint(targetPosition, targetAltitude, targetSpeed));
                }
            }
        } catch (IOException e) {
            FlightAssistant.LOGGER.error("Failed to deserialize flight plan from: %s".formatted(path.toAbsolutePath().toString()));
        }

        return loaded;
    }
}
