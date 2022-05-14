package net.Boster.item.spawner.listeners.pickup;

import net.Boster.item.spawner.hologram.ItemSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class NewPickupListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPickup(EntityPickupItemEvent e) {
        if(e.getItem().getCustomName() != null && e.getItem().getCustomName().equalsIgnoreCase(ItemSpawner.NO_PICKUP)) {
            e.setCancelled(true);
        }
    }
}
