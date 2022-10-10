package me.tsjollin.firstplugin;

import me.tsjollin.firstplugin.command.CommandKingdom;
import me.tsjollin.firstplugin.database.Database;
import me.tsjollin.firstplugin.database.SQLite;
import me.tsjollin.firstplugin.event.onJoinEvent;
import me.tsjollin.firstplugin.event.onMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Database db;
    private static Main instance;

    public Main() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        initConfig();
        this.db = new SQLite(this);
        this.db.load();
        registerCommands();
        registerEvents();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public static void log(Object x) {
        Bukkit.getLogger().info("[FirstPlugin] " + x);
    }

    public static void error(Object x) {
        Bukkit.getLogger().severe("[FirstPlugin] " + x);
    }

    private void initConfig() {
        FileConfiguration config = getConfig();
        config.addDefault("SQLite.Filename", "database");
        config.addDefault("settings.prefix", "&4&lKingdoms &8&l> ");
        config.addDefault("settings.teleport-delay", 5);
        config.options().copyDefaults(true);
        saveConfig();
    }

    private void registerCommand(String name, Object impl) {
        getCommand(name).setExecutor((CommandExecutor) impl);
        if (impl instanceof TabCompleter)
            getCommand(name).setTabCompleter((TabCompleter) impl);
    }

    private void registerCommands() {
        registerCommand("kingdom", new CommandKingdom());
    }
    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new onJoinEvent(), this);
        getServer().getPluginManager().registerEvents(new onMoveEvent(), this);
    }
    public static Database getKDDatabase() {
        return db;
    }

    public static FileConfiguration getKConfig() {
        return instance.getConfig();
    }

    public static Main getInstance() {
        return instance;
    }
}