package me.tsjollin.firstplugin.command;

import me.tsjollin.firstplugin.C;
import me.tsjollin.firstplugin.Main;
import me.tsjollin.firstplugin.util.Teleporter;
import me.tsjollin.firstplugin.util.Utils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Stream;

public class CommandKingdom extends TabCompleterBase implements CommandExecutor {
    static Map<UUID, String> confirmDeleteKingdomList = new HashMap<UUID, String>();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(C.TAC("&aFor help, type &2/" + alias.toLowerCase() + " help"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be in-game to use this command.");
            return true;
        }

        SubCommand subCommand = Utils.valueOfFormattedName(args[0], SubCommand.class);
        if (subCommand == null) {
            sender.sendMessage(C.TAC("&aFor help, type &2/" + alias.toLowerCase() + " help"));
            return true;
        }

        switch (subCommand) {
            case CREATE:
                createKingdom(sender, alias, args);
                break;
            case DELETE:
                deleteKingdom(sender, alias, args);
                break;
            case LIST:
                listKingdoms(sender);
                break;
            case JOIN:
                joinKingdom(sender, alias, args);
                break;
            case LEAVE:
                leaveKingdom(sender);
                break;
            case RENAME:
                renameKingdom(sender, alias, args);
                break;
            case INFO:
                kingdomInfo(sender);
                break;
            case SETSPAWN:
                setKingdomSpawn(sender, alias, args);
                break;
            case SPAWN:
                kingdomSpawn(sender);
                break;
            case EDIT:
                editKingdom(sender, alias, args);
        }
        return true;
    }
    private static void createKingdom(CommandSender sender, String alias, String[] args) {
        if (args.length <= 1) {
            sender.sendMessage(C.TAC("&cInvalid arguments, try: &4/" + alias.toLowerCase() + " create <name>"));
            return;
        }

        String kingdom = args[1].toLowerCase();
        if (Main.getKDDatabase().kingdomExists(kingdom)) {
            sender.sendMessage(C.TAC("&cThe kingdom &4" + kingdom + " &calready exists!"));
            return;
        }

        if (args[1].equalsIgnoreCase("confirm")) {
            sender.sendMessage(C.TAC("&cInvalid kingdom name!"));
            return;
        }

        sender.sendMessage(C.TAC("&aSuccessfully created kingdom: &2" + kingdom + "&a!"));

        Main.getKDDatabase().createKingdom(kingdom);
    }
    private static void deleteKingdom(CommandSender sender, String alias, String[] args) {
        Player player = (Player) sender;
        if (args.length <= 1) {
            sender.sendMessage(C.TAC("&cInvalid arguments, try: &4/" + alias.toLowerCase() + " delete <name>"));
            return;
        }

        if (args[1].equalsIgnoreCase("confirm")) {
            if (!confirmDeleteKingdomList.containsKey(player.getUniqueId())) {
                sender.sendMessage(C.TAC("&cYou don't have any confirmation!"));
                return;
            }

            String kingdom = confirmDeleteKingdomList.get(player.getUniqueId());

            if (!Main.getKDDatabase().kingdomExists(kingdom)) {
                sender.sendMessage(C.TAC("&cThe kingdom &4" + kingdom + " &cdoes not exist anymore!"));
                return;
            }

            confirmDeleteKingdomList.remove(player.getUniqueId());
            sender.sendMessage(C.TAC("&aSuccessfully deleted kingdom: &2" + kingdom + "&a!"));

            Main.getKDDatabase().deleteKingdom(kingdom);
        } else {
            String kingdom = args[1].toLowerCase();

            if (!Main.getKDDatabase().kingdomExists(kingdom)) {
                sender.sendMessage(C.TAC("&cThe kingdom &4" + kingdom + " &cdoes not exist!"));
                return;
            }

            confirmDeleteKingdomList.put(player.getUniqueId(), kingdom);
            sender.sendMessage(C.TAC("&aType: &2/" + alias + " delete confirm &ato delete &2" + kingdom + "&a!"));

            Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    confirmDeleteKingdomList.remove(player.getUniqueId());
                }
            }, 600L); // 30s delay
        }
    }
    private static void listKingdoms(CommandSender sender) {
        List<String> kingdoms = Main.getKDDatabase().getKingdoms();
        String kingdomList = "";

        if (kingdoms.isEmpty()) {
            sender.sendMessage(C.TAC("&7No kingdoms found!"));
            return;
        }

        for (int i = 0; i < kingdoms.size(); i++) {
            if (i + 1 == kingdoms.size()) {
                kingdomList += kingdoms.get(i);
                continue;
            }
            kingdomList += kingdoms.get(i) + "&a, &2";
        }

        sender.sendMessage(C.TAC("&aList of kingdoms: &2" + kingdomList));
    }
    private static void joinKingdom(CommandSender sender, String alias, String[] args) {
        Player player = (Player) sender;
        String UUID = player.getUniqueId().toString();

        if (args.length <= 1) {
            sender.sendMessage(C.TAC("&cInvalid arguments, try: &4/" + alias.toLowerCase() + " join <name>"));
            return;
        }

        String kingdom = args[1].toLowerCase();
        if (Main.getKDDatabase().isInKingdom(UUID)) {
            sender.sendMessage(C.TAC("&aYou are already in a kingdom!"));
            return;
        }

        if (!Main.getKDDatabase().kingdomExists(kingdom)) {
            sender.sendMessage(C.TAC("&cThe kingdom &4" + kingdom + " &cdoes not exist!"));
            return;
        }

        if (!Main.getKDDatabase().getInviteOnly(kingdom)) { // and has no invite
            sender.sendMessage(C.TAC("&aYou need an invite to join &2" + kingdom + "&a!"));
            return;
        }

        int kingdomId = Main.getKDDatabase().getKingdomId(kingdom);
        sender.sendMessage(C.TAC("&aYou joined: &2" + kingdom + "&a!"));

        Main.getKDDatabase().setKingdom(UUID, kingdomId);
    }
    private static void leaveKingdom(CommandSender sender) {
        Player player = (Player) sender;
        String UUID = player.getUniqueId().toString();
        String oldKingdom = Main.getKDDatabase().getPlayerKingdomName(UUID);

        if (!Main.getKDDatabase().isInKingdom(UUID)) {
            sender.sendMessage(C.TAC("&cYou are currently not in a kingdom!"));
            return;
        }

        sender.sendMessage(C.TAC("&aYou left: &2" + oldKingdom + "&a!"));

        Main.getKDDatabase().setKingdom(UUID, null);
    }
    private static void renameKingdom(CommandSender sender, String alias, String[] args) {
        if (args.length <= 2) {
            sender.sendMessage(C.TAC("&cInvalid arguments, try: &4/" + alias.toLowerCase() + " rename <oldname> <newname>"));
            return;
        }

        String kingdom = args[1].toLowerCase();
        if (!Main.getKDDatabase().kingdomExists(kingdom)) {
            sender.sendMessage(C.TAC("&cThe kingdom &4" + kingdom + " &cdoes not exist!"));
            return;
        }

        String newName = args[2].toLowerCase();
        if (Main.getKDDatabase().kingdomExists(newName)) {
            sender.sendMessage(C.TAC("&cThe kingdom &4" + kingdom + " &calready exists!"));
            return;
        }

        if (newName.equalsIgnoreCase("confirm")) {
            sender.sendMessage(C.TAC("&cInvalid kingdom name!"));
            return;
        }

        int kingdomId = Main.getKDDatabase().getKingdomId(kingdom);
        sender.sendMessage(C.TAC("&aRenamed the kingdom: &2" + kingdom + " &ato &2" + newName +"&a!"));

        Main.getKDDatabase().renameKingdom(kingdomId, newName);
    }

    private static void setKingdomSpawn(CommandSender sender, String alias, String[] args) {
        Player player = (Player) sender;
        String UUID = player.getUniqueId().toString();

        if (args.length <= 1) {
            sender.sendMessage(C.TAC("&cInvalid arguments, try: &4/" + alias.toLowerCase() + " setspawn <name>"));
            return;
        }

        if (!Main.getKDDatabase().isInKingdom(UUID)) {
            sender.sendMessage(C.TAC("&cYou are currently not in a kingdom!"));
            return;
        }

        String kingdom = args[1].toLowerCase();
        if (!Main.getKDDatabase().kingdomExists(kingdom)) {
            sender.sendMessage(C.TAC("&cThe kingdom &4" + kingdom + " &cdoes not exist!"));
            return;
        }
        Location location = Utils.roundLocation(player.getLocation());
        int kingdomId = Main.getKDDatabase().getKingdomId(kingdom);

        sender.sendMessage(C.TAC("&aYou changed the spawn of: &2" + kingdom + "&a to &2" + location.getX() + ", " + location.getY() + ", " + location.getZ() +"&a!"));
        Main.getKDDatabase().setKingdomSpawn(kingdomId, location);
    }
    private static void kingdomSpawn(CommandSender sender) {
        Player player = (Player) sender;
        String UUID = player.getUniqueId().toString();

        if (!Main.getKDDatabase().isInKingdom(UUID)) {
            sender.sendMessage(C.TAC("&cYou are currently not in a kingdom!"));
            return;
        }

        Integer kingdomId = Main.getKDDatabase().getPlayerKingdom(UUID);
        String kingdom = Main.getKDDatabase().getKingdomName(kingdomId);
        if (Main.getKDDatabase().getKingdomSpawn(kingdomId) == null) {
            sender.sendMessage(C.TAC("&cThe kingdom spawn has not been setup yet!"));
            return;
        }

        Teleporter.teleportSpawn(player, kingdom, Main.getKConfig().getInt("settings.teleport-delay", 5));
    }
    private static void kingdomInfo(CommandSender sender) {
        Player player = (Player) sender;
        String UUID = player.getUniqueId().toString();
        if (!Main.getKDDatabase().isInKingdom(UUID)) {
            sender.sendMessage(C.TAC("&cYou are currently not in a kingdom!"));
            return;
        }

        String kingdom = Main.getKDDatabase().getPlayerKingdomName(UUID);
        sender.sendMessage(kingdom);
    }
    private static void editKingdom(CommandSender sender, String alias, String[] args) {
        Player player = (Player) sender;
        String UUID = player.getUniqueId().toString();
        if (args.length <= 2) {
            sender.sendMessage(C.TAC("&cInvalid arguments, try: &4/" + alias.toLowerCase() + " edit <option> <value>"));
            return;
        }

        if (!Main.getKDDatabase().isInKingdom(UUID)) {
            sender.sendMessage(C.TAC("&cYou are currently not in a kingdom!"));
            return;
        }

        EditOperation editOperation = Utils.valueOfFormattedName(args[1], EditOperation.class);
        if (editOperation == null) {
            sender.sendMessage(C.TAC("&cInvalid arguments, try: &4/" + alias.toLowerCase() + " edit <option> <value>"));
            return;
        }

        switch (editOperation) {
            case INVITEONLY:
                editInviteOnly(sender, alias, args);
        }
    }
    private static void editInviteOnly(CommandSender sender, String alias, String[] args) {
        Player player = (Player) sender;
        String UUID = player.getUniqueId().toString();
        InviteOnlyValues value = Utils.valueOfFormattedName(args[2], InviteOnlyValues.class);
        Integer kingdom = Main.getKDDatabase().getPlayerKingdom(UUID);

        if (value == null) {
            sender.sendMessage(C.TAC("&4" + args[2] + " &cis not a valid, must be &4true &cor &4false&c!"));
            return;
        }
        sender.sendMessage(C.TAC("&aChanged invite only of your kingdom to &2" + value.toString().toLowerCase() + "&a!"));
        Main.getKDDatabase().setInviteOnly(kingdom, Boolean.parseBoolean(value.toString().toLowerCase()));
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
            throws IllegalArgumentException {
        if (args.length == 1) {
            return filterStartingWith(args[0], Stream.of(SubCommand.VALUES).map(Utils::formattedName));
        }

        SubCommand subCommand = Utils.valueOfFormattedName(args[0], SubCommand.class);
        if (subCommand == null)
            return Collections.emptyList();

        Player player = (Player) sender;
        if (args.length == 2) {
            switch (subCommand) {
                case JOIN:
                case SETSPAWN:
                case RENAME:
                    return filterStartingWith(args[1], Main.getKDDatabase().getKingdoms());
                case DELETE:
                    if (!confirmDeleteKingdomList.containsKey(player.getUniqueId())) {
                        return filterStartingWith(args[1], Main.getKDDatabase().getKingdoms());
                    };
                case EDIT:
                    return filterStartingWith(args[1], Stream.of(EditOperation.VALUES).map(Utils::formattedName));
            }
        }
        switch (subCommand) {
            case EDIT:
                if (args.length == 3) {
                    return filterStartingWith(args[2], Stream.of(InviteOnlyValues.VALUES).map(Utils::formattedName));
                }
        }
        return Collections.emptyList();
    }
    private enum SubCommand {
        CREATE, DELETE, LIST, JOIN, LEAVE, RENAME, SETSPAWN, SPAWN, INFO, EDIT;

        static final SubCommand[] VALUES = values();
    }
    private enum EditOperation {
        PREFIX, SUFFIX, INVITEONLY, MAXMEMBERS;

        static final EditOperation[] VALUES = values();
    }
    private enum InviteOnlyValues {
        TRUE, FALSE;

        static final InviteOnlyValues[] VALUES = values();
    }
}
