package me.tsjollin.firstplugin.entity;

import me.tsjollin.firstplugin.Main;
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
