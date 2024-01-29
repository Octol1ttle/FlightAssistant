package ru.octol1ttle.flightassistant.config.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.FlightAssistant;

public class ConfigLoader<T extends IConfig> {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    //private final Class<T> configClass;
    private final Consumer<T> onLoad;
    private final File file;
    private final T defaultConfig;
    private FileWatcher watcher;

    public ConfigLoader(T defaultConfig, String filename, Consumer<T> onLoad) {
        this.defaultConfig = defaultConfig;
        this.onLoad = onLoad;
        this.file = new File(ConfigFolder.get(), filename);
    }

    public void load() {
        T config = defaultConfig;

        if (!file.exists()) {
            save(config);
        }

        config = read();
        config.update();
        onLoad.accept(config);

        if (config.shouldWatch()) {
            watch(file);
        }
    }

    @SuppressWarnings("unchecked")
    public @NotNull T read() {
        try (FileReader reader = new FileReader(file)) {
            // TODO: what the FUCK is wrong with this
            // TODO: it always decides "i no worky" because "fuck you" I guess
            // TODO: there are ZERO REASONS this should be broken BUT IT IS
            // TODO: call "fromJson(new FileReader()) yourself? boom it works
            // TODO: actually use this in production? OH NO IT FUCKING BROKE AND THE DEFAULT CONFIG DOESNT EVEN LOAD
            // TODO: this config system is insanely stupid anyway BECAUSE THERE IS NO DEFINITIVE CONFIG LIBRARY
            // TODO: EVERYONE JUST ROLLS THEIR OWN AND PRAYS ITS GOOD ENOUGH
            // TODO: LIKE WHAT THE ACTUAL HELL
            T config = (T) GSON.fromJson(reader, defaultConfig.getClass());
            if (config == null) {
                throw new EOFException();
            }
            return config;
        } catch (Exception e) {
            FlightAssistant.LOGGER.error("Exception reading config: " + file.getAbsolutePath(), e);
            return defaultConfig;
        }
    }

    public void save(T config) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(GSON.toJson(config));
        } catch (Exception e) {
            FlightAssistant.LOGGER.error("Exception saving config: " + file.getAbsolutePath(), e);
        }
    }

    public void watch(File file) {
        if (watcher != null) {
            return;
        }
        watcher = FileWatcher.watch(file, this::load);
    }

}
