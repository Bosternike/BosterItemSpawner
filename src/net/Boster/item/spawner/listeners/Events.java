package net.Boster.item.spawner.listeners;

import net.Boster.item.spawner.hologram.ItemSpawner;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class Events implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onManipulate(PlayerArmorStandManipulateEvent e) {
        if(ItemSpawner.getHologram(e.getRightClicked()) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent e) {
        if(ItemSpawner.getHologram(e.getEntity()) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkUnload(ChunkUnloadEvent e) {
        for(Entity ent : e.getChunk().getEntities()) {
            ItemSpawner h = ItemSpawner.getHologram(ent);
            if(h != null && h.isRunning()) {
                h.stopTask();
                h.removeHolograms();
                h.clearDrops();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkLoad(ChunkLoadEvent e) {
        for(ItemSpawner i : ItemSpawner.holograms()) {
            Chunk loc = i.getLocation().getChunk();
            if(loc.getWorld() == e.getChunk().getWorld() &&
                    loc.getX() == e.getChunk().getX() && loc.getZ() == loc.getZ() && !i.isRunning()) {
                i.start();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPickup(PlayerPickupItemEvent e) {
        if(e.getItem().getCustomName() != null && e.getItem().getCustomName().equalsIgnoreCase(ItemSpawner.NO_PICKUP)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPickup(InventoryPickupItemEvent e) {
        if(e.getItem().getCustomName() != null && e.getItem().getCustomName().equalsIgnoreCase(ItemSpawner.NO_PICKUP)) {
            e.setCancelled(true);
        }
    }
}
