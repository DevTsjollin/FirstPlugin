package me.tsjollin.kingdoms;

import me.tsjollin.kingdoms.Main;
import me.tsjollin.kingdoms.entity.Kingdom;
import me.tsjollin.kingdoms.listeners.TeamManager;
import org.bukkit.scoreboard.Team;

import java.util.List;

public class KingdomManager {
    public static List<String> getKingdoms() {
        return Main.getKDDatabase().getKingdoms();
    }
    public static Boolean kingdomExists(String name) {
        return Main.getKDDatabase().kingdomExists(name);
    }
    public static void createKingdom(String name) {
        Main.getKDDatabase().createKingdom(name);
        TeamManager.deleteTeam(name);
    }
    public static void deleteKingdom(String name) {
        Main.getKDDatabase().deleteKingdom(name);
        TeamManager.deleteTeam(name);
    }
    public static Kingdom getKingdom(int id) {
        return new Kingdom(id);
    }
    public static Kingdom getKingdom(String name) {
        return new Kingdom(name);
    }
}
