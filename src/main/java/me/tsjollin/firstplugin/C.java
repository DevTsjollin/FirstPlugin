package me.tsjollin.firstplugin;

import org.bukkit.ChatColor;

public class C {
    private C() {}

    public static String TAC(String input) {
        return ChatColor.translateAlternateColorCodes('&', Main.getKConfig().getString("settings.prefix", "&4&lKingdoms &8&l> ") + input);
    }
}
