package net.Boster.item.spawner.nms.v1_17_R1;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.Boster.item.spawner.holo.entity.NMSEntity;
import net.Boster.item.spawner.holo.entity.NMSItemEntity;
import net.Boster.item.spawner.holo.entity.NMSTextEntity;
import net.Boster.item.spawner.holo.line.HoloItemLine;
import net.Boster.item.spawner.holo.line.HoloLine;
import net.Boster.item.spawner.holo.line.HoloTextLine;
import net.Boster.item.spawner.nms.HoloProvider;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraft.network.protocol.game.*;

import java.util.Collection;

public class HoloProviderImpl implements HoloProvider {

    @Override
    public NMSEntity createLineEntity(@NotNull HoloLine line) {
        Location loc = line.getPosition().toLocation();
        Level w = ((CraftWorld) loc.getWorld()).getHandle();

        ArmorStand cp = new ArmorStand(w, loc.getX(), loc.getY(), loc.getZ());

        cp.setInvisible(true);
        cp.setSmall(true);
        cp.setNoGravity(true);
        cp.setMarker(true);
        cp.setNoBasePlate(true);

        if(line instanceof HoloItemLine) {
            ItemEntity ent = new ItemEntity(w, loc.getX(), loc.getY(), loc.getZ(), CraftItemStack.asNMSCopy(((HoloItemLine) line).getItem()));

            return new CraftItemEntity(cp, ent);
        } else {
            String text = ((HoloTextLine) line).getText();

            cp.setCustomNameVisible(text != null);
            cp.setCustomName(CraftChatMessage.fromString(text)[0]);

            return new CraftTextEntity(cp);
        }
    }

    @Override
    public void show(@NotNull HoloLine line, @NotNull Collection<? extends Player> players) {
        ClientboundAddEntityPacket pa;
        ClientboundSetEntityDataPacket pa2;

        ClientboundAddEntityPacket pa3 = null;
        ClientboundSetEntityDataPacket pa4 = null;
        ClientboundSetPassengersPacket pa5 = null;

        if(line instanceof HoloItemLine) {
            CraftItemEntity ent = (CraftItemEntity) line.getEntity();

            pa = new ClientboundAddEntityPacket(ent.getObject());
            pa2 = new ClientboundSetEntityDataPacket(ent.getId(), ent.getObject().getEntityData(), true);

            pa3 = new ClientboundAddEntityPacket(ent.getItemEntity());
            pa4 = new ClientboundSetEntityDataPacket(ent.getItemEntity().getId(), ent.getItemEntity().getEntityData(), true);

            ent.getItemEntity().startRiding(ent.getObject());
            pa5 = new ClientboundSetPassengersPacket(ent.getObject());
        } else {
            CraftTextEntity ent = (CraftTextEntity) line.getEntity();

            pa = new ClientboundAddEntityPacket(ent.getObject());
            pa2 = new ClientboundSetEntityDataPacket(ent.getId(), ent.getObject().getEntityData(), true);
        }

        for(Player p : players) {
            ServerPlayer tc = ((CraftPlayer) p).getHandle();

            if(!line.getHologram().getViewers().contains(p)) {
                if(pa3 != null) {
                    sendPackets(tc, pa, pa2, pa3, pa4, pa5);
                } else {
                    sendPackets(tc, pa, pa2);
                }
            } else {
                if (pa4 != null) {
                    sendPackets(tc, pa2, pa4);
                } else {
                    sendPackets(tc, pa2);
                }
            }
        }
    }

    private static void sendPackets(@NotNull ServerPlayer p, @NotNull Packet<?>... packets) {
        for(Packet<?> packet : packets) {
            p.connection.send(packet);
        }
    }

    @RequiredArgsConstructor
    static class CraftItemEntity implements NMSItemEntity {

        @Getter @NotNull private final ArmorStand object;
        @Getter @NotNull private final ItemEntity itemEntity;

        @Override
        public int getId() {
            return object.getId();
        }

        @Override
        public void destroy() {
            ClientboundRemoveEntitiesPacket r = new ClientboundRemoveEntitiesPacket(object.getId());
            ClientboundRemoveEntitiesPacket r2 = new ClientboundRemoveEntitiesPacket(itemEntity.getId());

            for(Player p : Bukkit.getOnlinePlayers()) {
                sendPackets(((CraftPlayer) p).getHandle(), r, r2);
            }
        }

        @Override
        public void setItemStack(@Nullable ItemStack item) {
            itemEntity.setItem(CraftItemStack.asNMSCopy(item));
        }
    }

    @RequiredArgsConstructor
    static class CraftTextEntity implements NMSTextEntity {

        @Getter @NotNull private final ArmorStand object;

        @Override
        public int getId() {
            return object.getId();
        }

        @Override
        public void destroy() {
            ClientboundRemoveEntitiesPacket r = new ClientboundRemoveEntitiesPacket(object.getId());

            for(Player p : Bukkit.getOnlinePlayers()) {
                ((CraftPlayer) p).getHandle().connection.send(r);
            }
        }

        @Override
        public void setText(@Nullable String text) {
            object.setCustomNameVisible(text != null);
            object.setCustomName(CraftChatMessage.fromString(text)[0]);
        }
    }
}
