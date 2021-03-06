package net.pl3x.map.plugin;

import net.pl3x.map.plugin.command.CommandManager;
import net.pl3x.map.plugin.configuration.Config;
import net.pl3x.map.plugin.configuration.Lang;
import net.pl3x.map.plugin.httpd.IntegratedServer;
import net.pl3x.map.plugin.task.UpdatePlayers;
import net.pl3x.map.plugin.task.UpdateWorldData;
import net.pl3x.map.plugin.util.FileUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Pl3xMap extends JavaPlugin {
    private static Pl3xMap instance;
    private UpdateWorldData updateWorldData;
    private UpdatePlayers updatePlayers;
    private MapUpdateListeners mapUpdateListeners;

    public Pl3xMap() {
        instance = this;
    }

    @Override
    public void onEnable() {
        Config.reload();
        Lang.reload();

        try {
            new CommandManager(this);
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Failed to initialize command manager", e);
            this.setEnabled(false);
            return;
        }

        start();
    }

    @Override
    public void onDisable() {
        stop();
    }

    public static Pl3xMap getInstance() {
        return instance;
    }

    public void start() {
        FileUtil.extractWebFolder();

        this.updatePlayers = new UpdatePlayers();
        this.updatePlayers.runTaskTimer(this, 20, 20);
        this.updateWorldData = new UpdateWorldData();
        this.updateWorldData.runTaskTimer(this, 0, 20 * 5);

        WorldManager.start();

        this.mapUpdateListeners = new MapUpdateListeners(this);
        this.mapUpdateListeners.register();

        if (Config.HTTPD_ENABLED) {
            IntegratedServer.startServer();
        }
    }

    public void stop() {
        this.mapUpdateListeners.unregister();
        this.mapUpdateListeners = null;

        if (Config.HTTPD_ENABLED) {
            IntegratedServer.stopServer();
        }

        if (this.updatePlayers != null && !this.updatePlayers.isCancelled()) {
            this.updatePlayers.cancel();
            this.updatePlayers = null;
        }
        if (this.updateWorldData != null && !this.updateWorldData.isCancelled()) {
            this.updateWorldData.cancel();
            this.updateWorldData = null;
        }

        WorldManager.shutdown();

        getServer().getScheduler().cancelTasks(this);
    }

}
