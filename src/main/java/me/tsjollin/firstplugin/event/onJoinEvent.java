package me.tsjollin.firstplugin.event;

import me.tsjollin.firstplugin.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class onJoinEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerLoginEvent event) {
        Player p = event.getPlayer();
        Main.getKDDatabase().playerExists(p.getUniqueId().toString());
        if(!Main.getKDDatabase().playerExists(p.getUniqueId().toString())) {
            Main.getKDDatabase().createPlayer(p.getUniqueId().toString());
        }
    }
}
