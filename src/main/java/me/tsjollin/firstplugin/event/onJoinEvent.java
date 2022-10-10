package me.tsjollin.firstplugin.event;

import me.tsjollin.firstplugin.entity.PlatformPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class onJoinEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        PlatformPlayer platformPlayer = new PlatformPlayer(player);
        if(!platformPlayer.playerExists()) {
            platformPlayer.createPlayer();
        }
    }
}
