package me.tsjollin.firstplugin.database;

import me.tsjollin.firstplugin.Main;
import me.tsjollin.firstplugin.entity.Kingdom;

import java.util.List;

public class KingdomManager {
    public static List<String> getKingdoms() {
        return Main.getKDDatabase().getKingdoms();
    }
    public static Boolean kingdomExists(String name) {
        return Main.getKDDatabase().kingdomExists(name);
    }
    public static void createKingdom(String name) {
        Main.getKDDatabase().createKingdom(name);
    }
    public static void deleteKingdom(String name) {
        Main.getKDDatabase().deleteKingdom(name);
    }
    public static Kingdom getKingdom(int id) {
        return new Kingdom(id);
    }
    public static Kingdom getKingdom(String name) {
        return new Kingdom(name);
    }
}
