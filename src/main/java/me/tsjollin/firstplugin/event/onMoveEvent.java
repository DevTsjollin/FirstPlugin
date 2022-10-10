package me.tsjollin.firstplugin.event;

import me.tsjollin.firstplugin.util.Teleporter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class onMoveEvent implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if ( event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        Teleporter.cancel(event.getPlayer());
    }

}
