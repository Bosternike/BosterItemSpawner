package net.Boster.item.spawner.commands.subcommands;

import com.google.common.collect.Lists;
import net.Boster.item.spawner.BosterItemSpawner;
import net.Boster.item.spawner.commands.SubCommand;
import net.Boster.item.spawner.files.SpawnerFile;
import net.Boster.item.spawner.spawner.ItemSpawner;
import net.Boster.item.spawner.manager.SpawnersManager;
import net.Boster.item.spawner.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EditCommand extends SubCommand {

    private final List<String> cmds;

    public EditCommand(BosterItemSpawner plugin) {
        super(plugin, "edit");
        this.cmds = Lists.newArrayList("setLocation", "setItem", "setSpawnDelay", "setTicksDelay", "load");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if(isConsole(sender)) return false;
        if(!checkPermission(sender)) return false;

        if(args.length < 2) {
            sendUsage(sender);
            return false;
        }

        Player p = (Player) sender;
        SpawnerFile f = SpawnerFile.get(args[0]);
        if(f == null) {
            sender.sendMessage(Utils.toColor(getMessageNullSpawner(args[0])));
            return false;
        }

        if(args[1].equalsIgnoreCase("setlocation")) {
            String loc = Utils.locationToString(p.getLocation().add(0, 1, 0), true);
            f.getFile().set("location", loc);
            f.save();
            f.tryLoad();
            sender.sendMessage(Utils.toColor(getMessage("location").replace("%location%", loc)));
            return true;
        } else if(args[1].equalsIgnoreCase("setitem")) {
            ItemStack item = p.getInventory().getItem(p.getInventory().getHeldItemSlot()); //doing this to avoid multi-version problems.
            if (item == null) {
                sender.sendMessage(Utils.toColor(getMessage("nullItem")));
                return true;
            } else {
                f.getFile().set("item", Utils.serializeItem(item));
                f.save();
                f.tryLoad();
                sender.sendMessage(Utils.toColor(getMessage("item")));
                return true;
            }
        } else if(args[1].equalsIgnoreCase("setspawndelay") || args[1].equalsIgnoreCase("setticksdelay")) {
            if(args.length < 3) {
                sender.sendMessage(Utils.toColor(getMessage("setDelayUsage")));
                return false;
            }

            int delay;
            try {
                delay = Integer.parseInt(args[2]);
            } catch (Exception e) {
                sender.sendMessage(Utils.toColor(getMessage("notInteger").replace("%arg%", args[2])));
                return false;
            }

            if(args[1].equalsIgnoreCase("setspawndelay")) {
                f.getFile().set("SpawnDelay", delay);
                sender.sendMessage(Utils.toColor(getMessage("setSpawnDelay").replace("%spawner%", args[0]).replace("%delay%", args[2])));
            } else {
                f.getFile().set("DelayTicksAmount", delay);
                sender.sendMessage(Utils.toColor(getMessage("setTicksDelay").replace("%spawner%", args[0]).replace("%delay%", args[2])));
            }
            f.save();
            f.tryLoad();
            return true;
        } else if(args[1].equalsIgnoreCase("load")) {
            ItemSpawner is = ItemSpawner.get(args[1]);
            if(is != null) {
                is.remove();
            }

            if(!SpawnersManager.loadSpawner(f)) {
                sender.sendMessage(Utils.toColor(getMessage("couldNotLoad").replace("%spawner%", args[0])));
            } else {
                sender.sendMessage(Utils.toColor(getMessage("loaded").replace("%spawner%", args[0])));
            }
            return true;
        } else {
            sendUsage(sender);
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        return createDefaultTabComplete(cmds, args, 1);
    }
}
