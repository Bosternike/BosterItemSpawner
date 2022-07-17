package net.Boster.item.spawner.listeners;

import com.google.common.collect.ImmutableList;
import net.Boster.item.spawner.spawner.ItemSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        for(ItemSpawner s : ItemSpawner.spawners()) {
            s.getHologram().show(ImmutableList.of(p));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        for(ItemSpawner s : ItemSpawner.spawners()) {
            s.getHologram().getViewers().remove(p);
        }
    }
}
