package ru.octol1ttle.flightassistant.config.loader;

public interface IConfig {
    void update();

    boolean shouldWatch();
}