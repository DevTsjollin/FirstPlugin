package me.tsjollin.kingdoms.listeners;

import me.tsjollin.kingdoms.KingdomManager;
import me.tsjollin.kingdoms.Main;
import me.tsjollin.kingdoms.entity.Kingdom;
import me.tsjollin.kingdoms.entity.PlatformPlayer;
import me.tsjollin.kingdoms.util.C;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;


public class TeamManager {
    public static void updatePlayer(Player player) {
        Scoreboard sc = Bukkit.getScoreboardManager().getMainScoreboard();
        PlatformPlayer platformPlayer = new PlatformPlayer(player);
        if (!platformPlayer.isInKingdom() && sc.getEntryTeam(player.getName()) != null) {
            sc.getEntryTeam(player.getName()).removeEntry(player.getName());
            return;
        }

        if (platformPlayer.isInKingdom() && sc.getEntryTeam(player.getName()) == null) {
            Kingdom kingdom  = platformPlayer.getKingdom();
            if (!checkTeamExists(kingdom.getName())) {
                createTeam(kingdom);
            }
            sc.getTeam(kingdom.getName()).addEntry(player.getName());
        }
    }
    public static void createTeams() {
        Scoreboard sc = Bukkit.getScoreboardManager().getMainScoreboard();
        sc.getTeams().forEach(team -> {
            team.unregister();
        });
        List<String> kingdoms = KingdomManager.getKingdoms();
        for (int i = 0; i < kingdoms.size(); i++) {
            Kingdom kingdom = KingdomManager.getKingdom(kingdoms.get(i));
            createTeam(kingdom);
        }
    }
    public static void createTeam(Kingdom kingdom) {
        Scoreboard sc = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = sc.registerNewTeam(kingdom.getName());
        if (kingdom.getPrefix() != null) updatePrefix(kingdom);
        if (kingdom.getSuffix() != null) updateSuffix(kingdom);
    }

    public static void deleteTeam(String name) {
        Scoreboard sc = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = sc.getTeam(name);
        if (checkTeamExists(name)) {
            team.unregister();
        }
    }

    public static Boolean checkTeamExists(String name) {
        Scoreboard sc = Bukkit.getScoreboardManager().getMainScoreboard();
        return (sc.getTeam(name) != null);
    }

    public static void updatePrefix(Kingdom kingdom) {
        if (!checkTeamExists(kingdom.getName())) {
            createTeam(kingdom);
            return;
        }
        Scoreboard sc = Bukkit.getScoreboardManager().getMainScoreboard();
        if (kingdom.getPrefix().isEmpty()) {
            sc.getTeam(kingdom.getName()).setPrefix("");
            return;
        }
        sc.getTeam(kingdom.getName()).setPrefix(C.TAC(kingdom.getPrefix() + " "));
    }
    public static void updateSuffix(Kingdom kingdom) {
        if (!checkTeamExists(kingdom.getName())) {
            createTeam(kingdom);
            return;
        }
        Scoreboard sc = Bukkit.getScoreboardManager().getMainScoreboard();
        if (kingdom.getSuffix().isEmpty()) {
            sc.getTeam(kingdom.getName()).setSuffix("");
            return;
        }
        sc.getTeam(kingdom.getName()).setSuffix(C.TAC(" " + kingdom.getSuffix()));
    }
}
