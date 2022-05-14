package net.Boster.item.spawner.commands.subcommands;

import net.Boster.item.spawner.BosterItemSpawner;
import net.Boster.item.spawner.files.SpawnerFile;
import net.Boster.item.spawner.utils.Utils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class InfoCommand extends SubCommand {

    public InfoCommand(BosterItemSpawner plugin) {
        super(plugin, "info");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if(!checkPermission(sender)) return false;

        if(args.length == 0) {
            sendUsage(sender);
            return false;
        }

        SpawnerFile f = SpawnerFile.get(args[0]);
        if(f == null) {
            sender.sendMessage(Utils.toColor(getMessageNotNullSpawner(args[0])));
            return false;
        }

        String source = f.getFile().getString("source", "Unknown");
        String loc = f.getFile().getString("location", "Unknown");
        String delay = f.getFile().getString("Delay", "20");
        String sl = String.valueOf(f.getFile().getBoolean("SpawnLimit.Enabled", false));
        String cf = f.getFile().getBoolean("CounterFormat.Automatic", true) ? "Automatic" : "Premade";
        String text = "";
        String ta = "";
        for(String s : f.getFile().getStringList("Hologram")) {
            text += ta + s;
            ta = "\n";
        }
        for(String s : getMessages("success")) {
            sender.sendMessage(Utils.toColor(s.replace("%name%", args[0])
                    .replace("%source%", source)
                    .replace("%location%", loc)
                    .replace("%delay%", delay)
                    .replace("%spawn_limit%", sl)
                    .replace("%counter_format%", cf)
                    .replace("%text%", text)));
        }
        return false;
    }
}
