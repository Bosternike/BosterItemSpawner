package net.Boster.item.spawner.commands.subcommands;

import net.Boster.item.spawner.BosterItemSpawner;
import net.Boster.item.spawner.files.SpawnerFile;
import net.Boster.item.spawner.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCommand {

    protected final BosterItemSpawner plugin;
    private final String[] arguments;

    public SubCommand(BosterItemSpawner plugin, String argument) {
        this(plugin, new String[]{argument});
    }

    public SubCommand(BosterItemSpawner plugin, String[] arguments) {
        this.plugin = plugin;
        this.arguments = arguments;
    }

    public abstract boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args);

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        List<String> spawners = new ArrayList<>();
        SpawnerFile.getValues().forEach(s -> spawners.add(s.getName()));
        return createDefaultTabComplete(spawners, args, args.length - 1);
    }

    public static List<String> createDefaultTabComplete(List<String> vars, String[] args, int arg) {
        if(args.length == arg + 1) {
            try {
                List<String> list = new ArrayList<>();
                for(String i : vars) {
                    if(i.toLowerCase().startsWith(args[arg].toLowerCase())) {
                        list.add(i);
                    }
                }
                return list;
            } catch (Exception e) {
                return vars;
            }
        }

        List<String> spawners = new ArrayList<>();
        SpawnerFile.getValues().forEach(s -> spawners.add(s.getName()));
        return createDefaultTabComplete(spawners, args, args.length - 1);
    }

    public String[] getArguments() {
        return arguments;
    }

    public void sendUsage(CommandSender sender) {
        String msg = plugin.getConfig().getString("Messages." + arguments[0] + ".usage");
        if(msg != null) {
            sender.sendMessage(Utils.toColor(msg));
        }
    }

    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("boster.itemspawner.command." + arguments[0]);
    }

    public boolean checkPermission(CommandSender sender) {
        if(hasPermission(sender)) {
            return true;
        } else {
            String msg = plugin.getConfig().getString("Messages." + arguments[0] + ".noPermission");
            if(msg == null) {
                msg = plugin.getConfig().getString("Messages.noPermission");
            }
            sender.sendMessage(Utils.toColor(msg));
            return false;
        }
    }

    public List<String> getHelp() {
        return plugin.getConfig().getStringList("Messages." + arguments[0] + ".help");
    }

    public String getMessage(String path) {
        return plugin.getConfig().getString("Messages." + arguments[0] + "." + path);
    }

    public List<String> getMessages(String path) {
        return plugin.getConfig().getStringList("Messages." + arguments[0] + "." + path);
    }

    public String getMessageNullSpawner(String arg) {
        return plugin.getConfig().getString("Messages.spawnerNull").replace("%name%", arg);
    }

    public String getMessageNotNullSpawner(String arg) {
        return plugin.getConfig().getString("Messages.spawnerExists").replace("%name%", arg);
    }

    public boolean isConsole(CommandSender sender) {
        if(sender instanceof Player) {
            return false;
        } else {
            sender.sendMessage(Utils.toColor("%prefix% &cYou must be a player to perform this command!"));
            return true;
        }
    }
}
