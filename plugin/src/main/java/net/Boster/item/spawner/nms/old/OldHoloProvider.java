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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OldHoloProvider implements HoloProvider {

    @Getter @NotNull private final List<NMSEntity> entities = new ArrayList<>();

    protected OldHoloProvider instance;

    private final Class<?> WORLD = NMSHelper.getNMSClass("World");
    private final Class<?> ENTITY = NMSHelper.getNMSClass("Entity");
    private final Class<?> DATA_WATCHER = NMSHelper.getNMSClass("DataWatcher");
    private final Class<?> ENTITY_ARMOR_STAND = NMSHelper.getNMSClass("EntityArmorStand");
    private final Class<?> ENTITY_ITEM = NMSHelper.getNMSClass("EntityItem");
    private final Class<?> ITEM_STACK = NMSHelper.getNMSClass("ItemStack");
    private final Class<?> CRAFT_ITEM_STACK = NMSHelper.getCBClass("inventory.CraftItemStack");
    private final Class<?> I_CHAT_BASE_COMPONENT = NMSHelper.getNMSClass("IChatBaseComponent");
    private final Class<?> CRAFT_CHAT_MESSAGE = NMSHelper.getCBClass("util.CraftChatMessage");
    private final Class<?> PACKET_PLAY_OUT_ENTITY_DESTROY = NMSHelper.getNMSClass("PacketPlayOutEntityDestroy");
    private final Class<?> PACKET_PLAY_OUT_SPAWN_ENTITY = NMSHelper.getNMSClass("PacketPlayOutSpawnEntity");
    private final Class<?> PACKET_PLAY_OUT_ENTITY_METADATA = NMSHelper.getNMSClass("PacketPlayOutEntityMetadata");
    private final Class<?> PACKET_PLAY_OUT_MOUNT = NMSHelper.getNMSClass("PacketPlayOutMount");

    private final Constructor<?> ENTITY_CONSTRUCTOR = NMSHelper.getConstructor(ENTITY, WORLD, double.class, double.class, double.class);
    private final Constructor<?> ENTITY_ITEM_CONSTRUCTOR = NMSHelper.getConstructor(ENTITY_ITEM, WORLD, double.class, double.class, double.class, ITEM_STACK);

    private final Method GET_ID_METHOD = NMSHelper.getMethod(ENTITY, "getId");
    private final Method SET_INVISIBLE_METHOD = NMSHelper.getMethod(ENTITY, "setInvisible", boolean.class);
    private final Method SET_NO_GRAVITY_METHOD = NMSHelper.getMethod(ENTITY, "setNoGravity", boolean.class);
    private final Method SET_SMALL_METHOD = NMSHelper.getMethod(ENTITY_ARMOR_STAND, "setSmall", boolean.class);
    private final Method SET_MARKER_METHOD = NMSHelper.getMethod(ENTITY_ARMOR_STAND, "setMarker", boolean.class);
    private final Method SET_BASE_PLATE_METHOD = NMSHelper.getMethod(ENTITY_ARMOR_STAND, "setBasePlate", boolean.class);
    private final Method SET_CUSTOM_NAME_VISIBLE_METHOD = NMSHelper.getMethod(ENTITY, "setCustomNameVisible", boolean.class);
    private final Method AS_NMS_COPY_METHOD = NMSHelper.getMethod(CRAFT_ITEM_STACK, "asNMSCopy", ItemStack.class);
    private final Method SET_ITEM_STACK_METHOD = NMSHelper.getMethod(ENTITY_ITEM, "setItemStack", ItemStack.class);
    private final Method GET_DATA_WATCHER_METHOD = NMSHelper.getMethod(ENTITY, "getDataWatcher");
    private final Method START_RIDING_METHOD = NMSHelper.getMethod(ENTITY, "startRiding", ENTITY);

    private final ISetCustomNameMethod SET_CUSTOM_NAME_METHOD;

    public OldHoloProvider() {
        NMSHelper.load();
        instance = this;

        if(Version.getCurrentVersion().getVersionInteger() < 8) {
            SET_CUSTOM_NAME_METHOD = new OldCustomNameMethod();
        } else {
            SET_CUSTOM_NAME_METHOD = new NewCustomNameMethod();
        }
    }

    @Override
    public NMSEntity createLineEntity(@NotNull HoloLine line) {
        try {
            Object c;
            Location loc = line.getPosition().toLocation();
            Object w = NMSHelper.getHandle(loc.getWorld());

            c = ENTITY_CONSTRUCTOR.newInstance(w, loc.getX(), loc.getY(), loc.getZ());

            SET_INVISIBLE_METHOD.invoke(c, true);
            SET_NO_GRAVITY_METHOD.invoke(c, true);
            SET_SMALL_METHOD.invoke(c, true);
            SET_MARKER_METHOD.invoke(c, true);
            SET_BASE_PLATE_METHOD.invoke(c, true);

            if(line instanceof HoloItemLine) {
                HoloItemLine t = (HoloItemLine) line;

                SET_CUSTOM_NAME_VISIBLE_METHOD.invoke(c, false);

                Object it = t.getItem() != null ? AS_NMS_COPY_METHOD.invoke(null, t.getItem()) : null;
                Object item = ENTITY_ITEM_CONSTRUCTOR.newInstance(w, loc.getX(), loc.getY(), loc.getZ(), it);

                int id = (Integer) GET_ID_METHOD.invoke(c);
                int topId = (Integer) GET_ID_METHOD.invoke(item);
                Object[] dp = new Object[2];
                dp[0] = PACKET_PLAY_OUT_ENTITY_DESTROY.getConstructor(int[].class).newInstance((Object) new int[]{id});
                dp[1] = PACKET_PLAY_OUT_ENTITY_DESTROY.getConstructor(int[].class).newInstance((Object) new int[]{topId});
                return new CraftItemEntity(id, topId, c, item, SET_ITEM_STACK_METHOD, AS_NMS_COPY_METHOD, dp);
            } else {
                HoloTextLine t = (HoloTextLine) line;

                SET_CUSTOM_NAME_VISIBLE_METHOD.invoke(c, t.getText() != null);

                SET_CUSTOM_NAME_METHOD.set(c, t.getText());

                int id = (Integer) GET_ID_METHOD.invoke(c);
                return new CraftTextEntity(id, c, PACKET_PLAY_OUT_ENTITY_DESTROY.getConstructor(int[].class).newInstance((Object) new int[]{id})) {
                    @Override
                    public void setText(@Nullable String text) {
                        try {
                            SET_CUSTOM_NAME_VISIBLE_METHOD.invoke(c, text != null);
                            SET_CUSTOM_NAME_METHOD.set(c, text);
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

            Object pa = Version.getCurrentVersion().getVersionInteger() >= 10 ?
                    PACKET_PLAY_OUT_SPAWN_ENTITY.getConstructor(ENTITY).newInstance(ent.getObject()) :
                    PACKET_PLAY_OUT_SPAWN_ENTITY.getConstructor(ENTITY, int.class).newInstance(ent.getObject(), 78);
            Object pa2 = PACKET_PLAY_OUT_ENTITY_METADATA.getConstructor(int.class, DATA_WATCHER, boolean.class)
                    .newInstance(ent.getId(), GET_DATA_WATCHER_METHOD.invoke(ent.getObject()), true);

            Object pa3 = null;
            Object pa4 = null;
            Object pa5 = null;

            if(line instanceof HoloItemLine) {
                CraftItemEntity ce = (CraftItemEntity) ent;

                pa3 = Version.getCurrentVersion().getVersionInteger() >= 10 ?
                        PACKET_PLAY_OUT_SPAWN_ENTITY.getConstructor(ENTITY).newInstance(ce.getTopObject()) :
                        PACKET_PLAY_OUT_SPAWN_ENTITY.getConstructor(ENTITY, int.class).newInstance(ce.getTopObject(), 2);
                pa4 = PACKET_PLAY_OUT_ENTITY_METADATA.getConstructor(int.class, DATA_WATCHER, boolean.class)
                        .newInstance(ce.getTopID(), GET_DATA_WATCHER_METHOD.invoke(ce.getTopObject()), true);

                START_RIDING_METHOD.invoke(ce.getTopObject(), ce.getObject());

                pa5 = PACKET_PLAY_OUT_MOUNT.getConstructor(ENTITY).newInstance(ce.getObject());
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

    interface ISetCustomNameMethod {
        void set(Object entity, String name);
    }

    class OldCustomNameMethod implements ISetCustomNameMethod {

        private final Method SET_CUSTOM_NAME = NMSHelper.getMethod(ENTITY, "setCustomName", String.class);

        @Override
        public void set(Object entity, String name) {
            try {
                SET_CUSTOM_NAME.invoke(entity, name);
            } catch (Throwable e) {
                throw new Error(e);
            }
        }
    }

    class NewCustomNameMethod implements ISetCustomNameMethod {

        private final Method SET_CUSTOM_NAME = NMSHelper.getMethod(ENTITY, "setCustomName", I_CHAT_BASE_COMPONENT);
        private final Method CHAT_MESSAGE_FROM_STRING = NMSHelper.getMethod(CRAFT_CHAT_MESSAGE, "fromString", String.class);

        @Override
        public void set(Object entity, String name) {
            try {
                SET_CUSTOM_NAME.invoke(entity, I_CHAT_BASE_COMPONENT.cast(Object[].class.cast(CHAT_MESSAGE_FROM_STRING.invoke(null, name))[0]));
            } catch (Throwable e) {
                throw new Error(e);
            }
        }
    }
}
