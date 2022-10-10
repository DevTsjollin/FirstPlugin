package me.tsjollin.firstplugin.entity;

import me.tsjollin.firstplugin.Main;
import org.bukkit.entity.Player;

public class PlatformPlayer {
    private Player player;
    private String UUID;

    public PlatformPlayer(Player player) {
        this.player = player;
        this.UUID = player.getUniqueId().toString();
    }
    public Boolean playerExists() {
        return Main.getKDDatabase().playerExists(UUID);
    }
    public void createPlayer() {
        Main.getKDDatabase().createPlayer(UUID);
    }
    public Kingdom getKingdom() {
        if (!Main.getKDDatabase().isInKingdom(UUID)) {
            return null;
        } else {
            return new Kingdom(Main.getKDDatabase().getPlayerKingdom(UUID));
        }
    }
    public void setKingdom(Integer id) {
        Main.getKDDatabase().setKingdom(UUID, id);
    }
    public Boolean isInKingdom() {
        return Main.getKDDatabase().isInKingdom(UUID);
    }
}
