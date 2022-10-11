package me.tsjollin.kingdoms.entity;

import me.tsjollin.kingdoms.Main;
import me.tsjollin.kingdoms.listeners.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Kingdom {
    private final Integer id;
    public Kingdom(int id) {
        this.id = id;
    }
    public Kingdom(String name) {
        this.id = Main.getKDDatabase().getKingdomId(name);
    }
    public Integer getId() {
        return id;
    }
    public String getName() {
        return Main.getKDDatabase().getKingdomName(id);
    }
    public void renameKingdom(String newName) {
        Main.getKDDatabase().renameKingdom(id, newName);
    }
    public void setSpawn(Location location) {
        Main.getKDDatabase().setKingdomSpawn(id, location);
    }
    public Location getSpawn() {
        return Main.getKDDatabase().getKingdomSpawn(id);
    }
    public void setInviteOnly(Boolean value) {
        Main.getKDDatabase().setInviteOnly(id, value);
    }
    public Boolean isInviteOnly() {
        return Main.getKDDatabase().isInviteOnly(id);
    }
    public String getPrefix() {
        if (Main.getKDDatabase().getPrefix(id) == null) {
            return "";
        } else {
            return Main.getKDDatabase().getPrefix(id);
        }
    }
    public void setPrefix(String prefix) {
        if (prefix.equalsIgnoreCase( "null")) prefix = null;

        Main.getKDDatabase().setPrefix(id, prefix);
        TeamManager.updatePrefix(this);
    }
    public String getSuffix() {
        if (Main.getKDDatabase().getSuffix(id) == null) {
            return "";
        } else {
            return Main.getKDDatabase().getSuffix(id);
        }
    }
    public void setSuffix(String suffix) {
        if (suffix.equalsIgnoreCase( "null")) suffix = null;

        Main.getKDDatabase().setSuffix(id, suffix);
        TeamManager.updateSuffix(this);
    }
    public List<Player> getOnlinePlayers() {
        List<Player> players = new ArrayList<Player>();
        for(Player player: Bukkit.getOnlinePlayers()) {
            PlatformPlayer platformPlayer = new PlatformPlayer(player);
            if (platformPlayer.getKingdom().getId() == id) {
                players.add(player);
            }
        }
        return players;
    }
    public void sendMessageToOnline(String message) {
        List<Player> players = getOnlinePlayers();
        players.forEach(player -> {
            player.sendMessage(message);
        });
    }
}
