package me.tsjollin.kingdoms.command;

import me.tsjollin.kingdoms.util.C;
import me.tsjollin.kingdoms.Main;
import me.tsjollin.kingdoms.managers.KingdomManager;
import me.tsjollin.kingdoms.entity.Kingdom;
import me.tsjollin.kingdoms.entity.PlatformPlayer;
import me.tsjollin.kingdoms.util.Teleporter;
import me.tsjollin.kingdoms.util.Utils;
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
            sender.sendMessage(C.TACWithPrefix("&aFor help, type &2/" + alias.toLowerCase() + " help"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be in-game to use this command.");
            return true;
        }

        SubCommand subCommand = Utils.valueOfFormattedName(args[0], SubCommand.class);
        if (subCommand == null) {
            sender.sendMessage(C.TACWithPrefix("&aFor help, type &2/" + alias.toLowerCase() + " help"));
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
            sender.sendMessage(C.TACWithPrefix("&cInvalid arguments, try: &4/" + alias.toLowerCase() + " create <name>"));
            return;
        }

        String kingdomName = args[1].toLowerCase();
        if (KingdomManager.kingdomExists(kingdomName)) {
            sender.sendMessage(C.TACWithPrefix("&cThe kingdom &4" + kingdomName + " &calready exists!"));
            return;
        }

        if (args[1].equalsIgnoreCase("confirm")) {
            sender.sendMessage(C.TACWithPrefix("&cInvalid kingdom name!"));
            return;
        }

        sender.sendMessage(C.TACWithPrefix("&aSuccessfully created kingdom: &2" + kingdomName + "&a!"));

        KingdomManager.createKingdom(kingdomName);
    }
    private static void deleteKingdom(CommandSender sender, String alias, String[] args) {
        Player player = (Player) sender;
        if (args.length <= 1) {
            sender.sendMessage(C.TACWithPrefix("&cInvalid arguments, try: &4/" + alias.toLowerCase() + " delete <name>"));
            return;
        }

        if (args[1].equalsIgnoreCase("confirm")) {
            if (!confirmDeleteKingdomList.containsKey(player.getUniqueId())) {
                sender.sendMessage(C.TACWithPrefix("&cYou don't have any confirmation!"));
                return;
            }

            String kingdom = confirmDeleteKingdomList.get(player.getUniqueId());

            if (!KingdomManager.kingdomExists(kingdom)) {
                sender.sendMessage(C.TACWithPrefix("&cThe kingdom &4" + kingdom + " &cdoes not exist anymore!"));
                return;
            }

            confirmDeleteKingdomList.remove(player.getUniqueId());
            sender.sendMessage(C.TACWithPrefix("&aSuccessfully deleted kingdom: &2" + kingdom + "&a!"));

            KingdomManager.deleteKingdom(kingdom);
        } else {
            String kingdom = args[1].toLowerCase();

            if (!KingdomManager.kingdomExists(kingdom)) {
                sender.sendMessage(C.TACWithPrefix("&cThe kingdom &4" + kingdom + " &cdoes not exist!"));
                return;
            }

            confirmDeleteKingdomList.put(player.getUniqueId(), kingdom);
            sender.sendMessage(C.TACWithPrefix("&aType: &2/" + alias + " delete confirm &ato delete &2" + kingdom + "&a!"));

            Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    confirmDeleteKingdomList.remove(player.getUniqueId());
                }
            }, 600L); // 30s delay
        }
    }
    private static void listKingdoms(CommandSender sender) {
        List<String> kingdoms = KingdomManager.getKingdoms();
        String kingdomList = "";

        if (kingdoms.isEmpty()) {
            sender.sendMessage(C.TACWithPrefix("&7No kingdoms found!"));
            return;
        }

        for (int i = 0; i < kingdoms.size(); i++) {
            if (i + 1 == kingdoms.size()) {
                kingdomList += kingdoms.get(i);
                continue;
            }
            kingdomList += kingdoms.get(i) + "&a, &2";
        }

        sender.sendMessage(C.TACWithPrefix("&aList of kingdoms: &2" + kingdomList));
    }
    private static void joinKingdom(CommandSender sender, String alias, String[] args) {
        Player player = (Player) sender;
        PlatformPlayer platformPlayer = new PlatformPlayer(player);

        if (args.length <= 1) {
            sender.sendMessage(C.TACWithPrefix("&cInvalid arguments, try: &4/" + alias.toLowerCase() + " join <name>"));
            return;
        }

        String kingdomName = args[1].toLowerCase();
        if (platformPlayer.isInKingdom()) {
            sender.sendMessage(C.TACWithPrefix("&aYou are already in a kingdom!"));
            return;
        }

        if (!KingdomManager.kingdomExists(kingdomName)) {
            sender.sendMessage(C.TACWithPrefix("&cThe kingdom &4" + kingdomName + " &cdoes not exist!"));
            return;
        }

        Kingdom kingdom = KingdomManager.getKingdom(kingdomName);
        if (kingdom.isInviteOnly()) { // and has no invite
            sender.sendMessage(C.TACWithPrefix("&aYou need an invite to join &2" + kingdomName + "&a!"));
            return;
        }
        sender.sendMessage(C.TACWithPrefix("&aYou joined: &2" + kingdomName + "&a!"));
        platformPlayer.setKingdom(kingdom.getId());
    }
    private static void leaveKingdom(CommandSender sender) {
        Player player = (Player) sender;
        PlatformPlayer platformPlayer = new PlatformPlayer(player);

        if (!platformPlayer.isInKingdom()) {
            sender.sendMessage(C.TACWithPrefix("&cYou are currently not in a kingdom!"));
            return;
        }

        sender.sendMessage(C.TACWithPrefix("&aYou left: &2" + platformPlayer.getKingdom().getName() + "&a!"));
        platformPlayer.setKingdom(null);
    }
    private static void renameKingdom(CommandSender sender, String alias, String[] args) {
        if (args.length <= 2) {
            sender.sendMessage(C.TACWithPrefix("&cInvalid arguments, try: &4/" + alias.toLowerCase() + " rename <oldname> <newname>"));
            return;
        }

        String kingdomName = args[1].toLowerCase();

        if (!KingdomManager.kingdomExists(kingdomName)) {
            sender.sendMessage(C.TACWithPrefix("&cThe kingdom &4" + kingdomName + " &cdoes not exist!"));
            return;
        }

        String newName = args[2].toLowerCase();
        if (KingdomManager.kingdomExists(newName)) {
            sender.sendMessage(C.TACWithPrefix("&cThe kingdom &4" + newName + " &calready exists!"));
            return;
        }

        if (newName.equalsIgnoreCase("confirm")) {
            sender.sendMessage(C.TACWithPrefix("&cInvalid kingdom name!"));
            return;
        }

        Kingdom kingdom = KingdomManager.getKingdom(kingdomName);
        sender.sendMessage(C.TACWithPrefix("&aRenamed the kingdom: &2" + kingdom.getName() + " &ato &2" + newName +"&a!"));
        kingdom.renameKingdom(newName);
    }

    private static void setKingdomSpawn(CommandSender sender, String alias, String[] args) {
        Player player = (Player) sender;

        if (args.length <= 1) {
            sender.sendMessage(C.TACWithPrefix("&cInvalid arguments, try: &4/" + alias.toLowerCase() + " setspawn <name>"));
            return;
        }

        String kingdomName = args[1].toLowerCase();
        if (!KingdomManager.kingdomExists(kingdomName)) {
            sender.sendMessage(C.TACWithPrefix("&cThe kingdom &4" + kingdomName + " &cdoes not exist!"));
            return;
        }

        Location location = Utils.roundLocation(player.getLocation());
        Kingdom kingdom = KingdomManager.getKingdom(kingdomName);

        sender.sendMessage(C.TACWithPrefix("&aYou changed the spawn of: &2" + kingdom.getName() + "&a to &2" + location.getX() + ", " + location.getY() + ", " + location.getZ() +"&a!"));
        kingdom.setSpawn(location);
    }
    private static void kingdomSpawn(CommandSender sender) {
        Player player = (Player) sender;
        PlatformPlayer platformPlayer = new PlatformPlayer(player);

        if (!platformPlayer.isInKingdom()) {
            sender.sendMessage(C.TACWithPrefix("&cYou are currently not in a kingdom!"));
            return;
        }

        if (platformPlayer.getKingdom().getSpawn() == null) {
            sender.sendMessage(C.TACWithPrefix("&cThe kingdom spawn has not been setup yet!"));
            return;
        }

        Teleporter.teleportSpawn(player, platformPlayer.getKingdom(), Main.getKConfig().getInt("settings.teleport-delay", 5));
    }
    private static void kingdomInfo(CommandSender sender) {
        Player player = (Player) sender;
        PlatformPlayer platformPlayer = new PlatformPlayer(player);
        if (!platformPlayer.isInKingdom()) {
            sender.sendMessage(C.TACWithPrefix("&cYou are currently not in a kingdom!"));
            return;
        }
        sender.sendMessage(platformPlayer.getKingdom().getName());
    }
    private static void editKingdom(CommandSender sender, String alias, String[] args) {
        Player player = (Player) sender;
        PlatformPlayer platformPlayer = new PlatformPlayer(player);
        if (args.length <= 2) {
            sender.sendMessage(C.TACWithPrefix("&cInvalid arguments, try: &4/" + alias.toLowerCase() + " edit <option> <value>"));
            return;
        }

        if (!platformPlayer.isInKingdom()) {
            sender.sendMessage(C.TACWithPrefix("&cYou are currently not in a kingdom!"));
            return;
        }

        EditOperation editOperation = Utils.valueOfFormattedName(args[1], EditOperation.class);
        if (editOperation == null) {
            sender.sendMessage(C.TACWithPrefix("&cInvalid arguments, try: &4/" + alias.toLowerCase() + " edit <option> <value>"));
            return;
        }

        switch (editOperation) {
            case INVITEONLY:
                editInviteOnly(sender, alias, args);
                break;
            case PREFIX:
                editPrefix(sender, alias, args);
                break;
            case SUFFIX:
                editSuffix(sender, alias, args);
                break;
        }
    }
    private static void editInviteOnly(CommandSender sender, String alias, String[] args) {
        Player player = (Player) sender;
        PlatformPlayer platformPlayer = new PlatformPlayer(player);
        InviteOnlyValues value = Utils.valueOfFormattedName(args[2], InviteOnlyValues.class);

        if (value == null) {
            sender.sendMessage(C.TACWithPrefix("&4" + args[2] + " &cis not a valid, must be &4true &cor &4false&c!"));
            return;
        }
        sender.sendMessage(C.TACWithPrefix("&aChanged invite only of your kingdom to &2" + value.toString().toLowerCase() + "&a!"));
        platformPlayer.getKingdom().setInviteOnly(Boolean.parseBoolean(value.toString().toLowerCase()));

    }
    private static void editPrefix(CommandSender sender, String alias, String[] args) {
        Player player = (Player) sender;
        PlatformPlayer platformPlayer = new PlatformPlayer(player);

        sender.sendMessage(C.TACWithPrefix("&aChanged prefix of your kingdom to &7" + args[2]));
        platformPlayer.getKingdom().setPrefix(args[2]);

    }
    private static void editSuffix(CommandSender sender, String alias, String[] args) {
        Player player = (Player) sender;
        PlatformPlayer platformPlayer = new PlatformPlayer(player);

        sender.sendMessage(C.TACWithPrefix("&aChanged suffix of your kingdom to &7" + args[2]));
        platformPlayer.getKingdom().setSuffix(args[2]);

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
                    return filterStartingWith(args[1], KingdomManager.getKingdoms());
                case DELETE:
                    if (!confirmDeleteKingdomList.containsKey(player.getUniqueId())) {
                        return filterStartingWith(args[1], KingdomManager.getKingdoms());
                    };
                case EDIT:
                    return filterStartingWith(args[1], Stream.of(EditOperation.VALUES).map(Utils::formattedName));
            }
        }
        if (args.length == 3) {
            EditOperation editOperation = Utils.valueOfFormattedName(args[1], EditOperation.class);
            switch (editOperation) {
                case INVITEONLY:
                    return filterStartingWith(args[2], Stream.of(InviteOnlyValues.VALUES).map(Utils::formattedName));
                case PREFIX:
                case SUFFIX:
                    return filterStartingWith(args[2], Stream.of(NullValue.VALUES).map(Utils::formattedName));
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
    private enum NullValue {
        NULL;

        static final NullValue[] VALUES = values();
    }
}
