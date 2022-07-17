package net.Boster.item.spawner.listeners;

import net.Boster.item.spawner.spawner.ItemSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkUnload(ChunkUnloadEvent e) {
        for(ItemSpawner s : ItemSpawner.spawners()) {
            if(s.isRunning()) {
                if(s.isInChunk(e.getChunk())) {
                    s.stopTask();
                    s.removeHolograms();
                    s.clearDrops();
                } else {
                    s.dropsInChunk(e.getChunk()).forEach(Entity::remove);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkLoad(ChunkLoadEvent e) {
        for(ItemSpawner s : ItemSpawner.spawners()) {
            if(!s.isRunning() && s.isInChunk(e.getChunk())) {
                s.start();
            }
        }
    }
}
