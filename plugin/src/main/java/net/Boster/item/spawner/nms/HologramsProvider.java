package net.Boster.item.spawner.nms;

import lombok.Getter;
import net.Boster.item.spawner.nms.old.OldHoloProvider;
import net.Boster.item.spawner.utils.Version;

public class HologramsProvider {

    @Getter private static HoloProvider provider;

    public static boolean load() {
        int i = Version.getCurrentVersion().getVersionInteger();
        if(i < 6) return false;

        if(i < 15) {
            provider = new OldHoloProvider();
            return true;
        } else {
            try {
                Class<?> c = Class.forName("net.Boster.item.spawner.nms." + Version.getCurrentVersion().name() + ".HoloProviderImpl");
                provider = (HoloProvider) c.getDeclaredConstructor().newInstance();
                return true;
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
