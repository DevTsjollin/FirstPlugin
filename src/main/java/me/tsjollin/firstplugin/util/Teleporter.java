package me.tsjollin.firstplugin.util;

import me.tsjollin.firstplugin.C;
import me.tsjollin.firstplugin.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Teleporter {
    public static Map<UUID, Integer> teleports = new HashMap<UUID, Integer>();
    public static void teleportSpawn(Player player, String kingdom, int delay) {
        int kingdomId = Main.getKDDatabase().getKingdomId(kingdom);

        if (teleports.containsKey(player.getUniqueId())) {
            Bukkit.getScheduler().cancelTask(teleports.get(player.getUniqueId()));
            teleports.remove(player.getUniqueId());
        }
        if (delay <= 0) {
            Location spawn = Main.getKDDatabase().getKingdomSpawn(kingdomId);
            Location safeLocation = Utils.findSafeNear(spawn,1, 255);

            player.spawnParticle(Particle.PORTAL, player.getLocation(), 100);
            player.playNote(player.getLocation(), Instrument.BELL, Note.sharp(2, Note.Tone.F));
            player.teleport(safeLocation);
            player.sendMessage(C.TAC("&aYou have been teleported to &2" + kingdom + "&a!"));
            return;
        }
        player.sendMessage(C.TAC("&aTeleporting in &2" + delay + " &aseconds, don't move!"));
        int taskId = Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                Location spawn = Main.getKDDatabase().getKingdomSpawn(kingdomId);
                Location safeLocation = Utils.findSafeNear(spawn,1, 255);

                player.spawnParticle(Particle.PORTAL, player.getLocation(), 100);
                player.playNote(player.getLocation(), Instrument.BELL, Note.sharp(2, Note.Tone.F));
                player.teleport(safeLocation);
                player.sendMessage(C.TAC("&aYou have been teleported to &2" + kingdom + "&a!"));
                teleports.remove(player.getUniqueId());
            }
        }, delay * 20).getTaskId();
        teleports.put(player.getUniqueId(), taskId);
    }
    public static void cancel(Player player) {
        if (teleports.containsKey(player.getUniqueId())) {
            Bukkit.getScheduler().cancelTask(teleports.get(player.getUniqueId()));
            teleports.remove(player.getUniqueId());
            player.sendMessage(C.TAC("&cTeleportation cancelled!"));
        }
    }
}