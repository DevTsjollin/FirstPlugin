package me.tsjollin.kingdoms.util;

import me.tsjollin.kingdoms.Main;
import org.bukkit.ChatColor;

public class C {
    private C() {}

    public static String TACWithPrefix(String input) {
        return ChatColor.translateAlternateColorCodes('&', Main.getKConfig().getString("settings.prefix", "&4&lKingdoms &8&l> ") + input);
    }
    public static String TAC(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
