package ru.octol1ttle.flightassistant.config.loader;

import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import ru.octol1ttle.flightassistant.FlightAssistant;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class FileWatcher implements Runnable {
    private final File file;
    private final Path filename;
    private final Path parent;
    private final Listener listener;

    private FileWatcher(File file, Listener listener) {
        this.file = file;
        this.listener = listener;
        this.filename = file.toPath().getFileName();
        this.parent = file.toPath().getParent();
    }

    public static FileWatcher watch(File file, Listener listener) {
        FileWatcher watcher = new FileWatcher(file, listener);
        Thread thread = new Thread(watcher);
        thread.setDaemon(true);
        thread.start();
        return watcher;
    }

    @Override
    public void run() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            this.parent.register(watchService, ENTRY_MODIFY, ENTRY_CREATE);
            boolean poll = true;
            while (poll) {
                poll = pollEvents(watchService);
            }
        } catch (IOException | InterruptedException | ClosedWatchServiceException e) {
            FlightAssistant.LOGGER.error("Exception polling events", e);
            Thread.currentThread().interrupt();
        }
    }

    protected boolean pollEvents(WatchService watchService) throws InterruptedException {
        WatchKey key = watchService.take();
        for (WatchEvent<?> event : key.pollEvents()) {
            Path changedFilename = ((Path) event.context()).getFileName();
            if (changedFilename.equals(filename)) {
                try {
                    listener.onUpdate();
                } catch (Exception e) {
                    FlightAssistant.LOGGER.error("Exception watching file: " + file.getAbsolutePath(), e);
                }
            }
        }
        return key.reset();
    }

    @FunctionalInterface
    public interface Listener {
        void onUpdate();
    }

}
