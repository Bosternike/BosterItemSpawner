package net.Boster.item.spawner.nms.old;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.Boster.item.spawner.holo.entity.NMSEntity;
import net.Boster.item.spawner.holo.entity.NMSItemEntity;
import net.Boster.item.spawner.holo.entity.NMSTextEntity;
import net.Boster.item.spawner.holo.line.HoloItemLine;
import net.Boster.item.spawner.holo.line.HoloLine;
import net.Boster.item.spawner.holo.line.HoloTextLine;
import net.Boster.item.spawner.nms.HoloProvider;
import net.Boster.item.spawner.utils.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OldHoloProvider implements HoloProvider {

    @Getter @NotNull private final List<NMSEntity> entities = new ArrayList<>();

    protected OldHoloProvider instance;

    private final Class<?> World = NMSHelper.getNMSClass("World");
    private final Class<?> Entity = NMSHelper.getNMSClass("Entity");
    private final Class<?> DataWatcher = NMSHelper.getNMSClass("DataWatcher");
    private final Class<?> EntityArmorStand = NMSHelper.getNMSClass("EntityArmorStand");
    private final Class<?> EntityItem = NMSHelper.getNMSClass("EntityItem");
    private final Class<?> ItemStack = NMSHelper.getNMSClass("ItemStack");
    private final Class<?> CraftItemStack = NMSHelper.getCBClass("inventory.CraftItemStack");
    private final Class<?> IChatBaseComponent = NMSHelper.getNMSClass("IChatBaseComponent");
    private final Class<?> CraftChatMessage = NMSHelper.getCBClass("util.CraftChatMessage");
    private final Class<?> PacketPlayOutEntityDestroy = NMSHelper.getNMSClass("PacketPlayOutEntityDestroy");
    private final Class<?> PacketPlayOutSpawnEntity = NMSHelper.getNMSClass("PacketPlayOutSpawnEntity");
    private final Class<?> PacketPlayOutEntityMetadata = NMSHelper.getNMSClass("PacketPlayOutEntityMetadata");
    private final Class<?> PacketPlayOutMount = NMSHelper.getNMSClass("PacketPlayOutMount");

    public OldHoloProvider() {
        NMSHelper.load();
        instance = this;
    }

    @Override
    public NMSEntity createLineEntity(@NotNull HoloLine line) {
        try {
            Object c;
            Location loc = line.getPosition().toLocation();
            Object w = loc.getWorld().getClass().getMethod("getHandle").invoke(loc.getWorld());

            c = EntityArmorStand.getConstructor(World, double.class, double.class, double.class).newInstance(w, loc.getX(), loc.getY(), loc.getZ());

            Entity.getMethod("setInvisible", boolean.class).invoke(c, true);
            Entity.getMethod("setNoGravity", boolean.class).invoke(c, true);
            EntityArmorStand.getMethod("setSmall", boolean.class).invoke(c, true);
            EntityArmorStand.getMethod("setMarker", boolean.class).invoke(c, true);
            EntityArmorStand.getMethod("setBasePlate", boolean.class).invoke(c, true);

            if(line instanceof HoloItemLine) {
                HoloItemLine t = (HoloItemLine) line;

                Entity.getMethod("setCustomNameVisible", boolean.class).invoke(c, false);

                Object it = t.getItem() != null ? CraftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, t.getItem()) : null;
                Object item = EntityItem.getConstructor(World, double.class, double.class, double.class, it.getClass()).newInstance(w, loc.getX(), loc.getY(), loc.getZ(), it);

                Method si = EntityItem.getMethod("setItemStack", ItemStack);

                int id = (Integer) Entity.getMethod("getId").invoke(c);
                int topId = (Integer) Entity.getMethod("getId").invoke(item);
                Object[] dp = new Object[2];
                dp[0] = PacketPlayOutEntityDestroy.getConstructor(int[].class).newInstance((Object) new int[]{id});
                dp[1] = PacketPlayOutEntityDestroy.getConstructor(int[].class).newInstance((Object) new int[]{topId});
                return new CraftItemEntity(id, topId, c, item, si, CraftItemStack.getMethod("asNMSCopy", ItemStack.class), dp);
            } else {
                HoloTextLine t = (HoloTextLine) line;

                Method scnv = Entity.getMethod("setCustomNameVisible", boolean.class);
                scnv.invoke(c, t.getText() != null);

                if(Version.getCurrentVersion().getVersionInteger() < 8) {
                    Entity.getMethod("setCustomName", String.class).invoke(c, t.getText());
                } else {
                    Entity.getMethod("setCustomName", IChatBaseComponent).invoke(c, IChatBaseComponent.cast(Object[].class.cast(CraftChatMessage.getMethod("fromString", String.class).invoke(null, t.getText()))[0]));
                }

                int id = (Integer) Entity.getMethod("getId").invoke(c);
                return new CraftTextEntity(id, c, PacketPlayOutEntityDestroy.getConstructor(int[].class).newInstance((Object) new int[]{id})) {
                    @Override
                    public void setText(@Nullable String text) {
                        try {
                            scnv.invoke(c, text != null);

                            if(Version.getCurrentVersion().getVersionInteger() < 7) {
                                Entity.getMethod("setCustomName", String.class).invoke(c, text);
                            } else {
                                Entity.getMethod("setCustomName", IChatBaseComponent).invoke(c, IChatBaseComponent.cast(Object[].class.cast(CraftChatMessage.getMethod("fromString", String.class).invoke(null, text))[0]));
                            }
                        } catch (ReflectiveOperationException e1) {
                            e1.printStackTrace();
                        }
                    }
                };
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void show(@NotNull HoloLine line, @NotNull Collection<? extends Player> players) {
        try {
            NMSEntity ent = line.getEntity();

            Object pa = Version.getCurrentVersion().getVersionInteger() >= 10 ? PacketPlayOutSpawnEntity.getConstructor(Entity).newInstance(ent.getObject()) : PacketPlayOutSpawnEntity.getConstructor(Entity, int.class).newInstance(ent.getObject(), 78);
            Object pa2 = PacketPlayOutEntityMetadata.getConstructor(int.class, DataWatcher, boolean.class).newInstance(ent.getId(), Entity.getMethod("getDataWatcher").invoke(ent.getObject()), true);

            Object pa3 = null;
            Object pa4 = null;
            Object pa5 = null;

            if(line instanceof HoloItemLine) {
                CraftItemEntity ce = (CraftItemEntity) ent;

                pa3 = Version.getCurrentVersion().getVersionInteger() >= 10 ? PacketPlayOutSpawnEntity.getConstructor(Entity).newInstance(ce.getTopObject()) : PacketPlayOutSpawnEntity.getConstructor(Entity, int.class).newInstance(ce.getTopObject(), 2);
                pa4 = PacketPlayOutEntityMetadata.getConstructor(int.class, DataWatcher, boolean.class).newInstance(ce.getTopID(), Entity.getMethod("getDataWatcher").invoke(ce.getTopObject()), true);

                Entity.getMethod("startRiding", Entity).invoke(ce.getTopObject(), ce.getObject());

                pa5 = PacketPlayOutMount.getConstructor(Entity).newInstance(ce.getObject());
            }

            for(Player p : players) {
                if(!line.getEntity().getViewers().contains(p)) {
                    if(pa3 != null) {
                        NMSHelper.sendPacket(p, pa, pa2, pa3, pa4, pa5);
                    } else {
                        NMSHelper.sendPacket(p, pa, pa2);
                    }

                    line.getEntity().getViewers().add(p);
                } else {
                    if (pa4 != null) {
                        NMSHelper.sendPacket(p, pa2, pa4);
                    } else {
                        NMSHelper.sendPacket(p, pa2);
                    }
                }
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @RequiredArgsConstructor
    static class CraftItemEntity implements NMSItemEntity {

        @Getter @NotNull private final List<Player> viewers = new ArrayList<>();

        @Getter private final int id;
        @Getter private final int topID;

        @Getter @NotNull private final Object object;
        @Getter @NotNull private final Object topObject;

        private final Method si;
        private final Method asNMSMethod;
        private final Object[] destroyPacket;

        @Override
        public void destroy() {
            for(Player p : viewers) {
                NMSHelper.sendPacket(p, destroyPacket);
            }
        }

        @Override
        public void setItemStack(@Nullable ItemStack item) {
            try {
                si.invoke(asNMSMethod.invoke(topObject, item));
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }

    @RequiredArgsConstructor
    static abstract class CraftTextEntity implements NMSTextEntity {

        @Getter @NotNull private final List<Player> viewers = new ArrayList<>();
        @Getter private final int id;
        @Getter @NotNull private final Object object;
        private final Object destroyPacket;

        @Override
        public void destroy() {
            for(Player p : viewers) {
                NMSHelper.sendPacket(p, destroyPacket);
            }
        }
    }
}
