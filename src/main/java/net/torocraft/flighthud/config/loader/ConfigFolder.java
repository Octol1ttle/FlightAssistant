package net.torocraft.flighthud.config.loader;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

public class ConfigFolder {

    public static File get() {
        return FabricLoader.getInstance().getConfigDir().toFile();
    }

}
