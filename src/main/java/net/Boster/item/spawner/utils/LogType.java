package net.Boster.item.spawner.utils;

public enum LogType {

    FINE("§d[§bBosterItemSpawner§d] §7[§aFINE§7] ", "§a"),
    INFO("§d[§bBosterItemSpawner§d] §7[§9INFO§7] ", "§9"),
    WARNING("§d[§bBosterItemSpawner§d] §7[§cWARNING§7] ", "§c"),
    ERROR("§d[§bBosterItemSpawner§d] §7[§4ERROR§7] ", "§4");

    private final String format;
    private final String color;

    LogType(String s, String color) {
        this.format = s;
        this.color = color;
    }

    public String getFormat() {
        return format;
    }

    public String getColor() {
        return color;
    }
}
