package net.Boster.item.spawner.holo.entity;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface NMSItemEntity extends NMSEntity {

    void setItemStack(@Nullable ItemStack item);
}
