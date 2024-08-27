package de.revivemc.build.commands;

import com.google.common.collect.Maps;
import de.revivemc.build.BuildSystem;
import de.revivemc.build.modules.inventory.InventoryModule;
import de.revivemc.build.modules.world.WorldModule;
import de.revivemc.core.ReviveMCAPI;
import de.revivemc.core.entitiesutils.items.ItemCreator;
import de.revivemc.core.gameutils.phase.Phase;
import de.revivemc.core.gameutils.phase.PhaseInfo;
import de.revivemc.core.playerutils.ReviveMCPlayer;
import de.revivemc.core.playerutils.scoreboard.ReviveMCScoreboardBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MapCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        final Player player = (Player) sender;
        final ReviveMCPlayer reviveMCPlayer = ReviveMCAPI.getInstance().getCyturaPlayerManager().getPlayers().get(player.getUniqueId());
        final String prefix = BuildSystem.getInstance().getPrefix(reviveMCPlayer);
        final WorldModule worldModule = new WorldModule();
        if (!player.hasPermission("build.command.map")) {
            player.sendMessage(prefix + "§cDu hast keine Rechte auf diesen Befehl.");
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(prefix + " ");
            player.sendMessage(prefix + "-/map list §8| §7Bekomme eine Liste aller Maps.");
            player.sendMessage(prefix + "-/map create <Name> <Typ> <voidWorld> §8| §7Erstelle eine Neue Map.");
            player.sendMessage(prefix + "-/map tp <Name> §8| §7Teleportiere dich zu einer Map.");
            player.sendMessage(prefix + " ");
            return false;
        }

        if (args[0].equalsIgnoreCase("list")) {
            new InventoryModule(player).openMapInventory();
            return false;
        }

        if (args[0].equalsIgnoreCase("tp")) {
            if (args.length != 2) {
                player.sendMessage(prefix + "Verwende: 'map tp <Name>'");
                return false;
            }

            if (!worldModule.existMap(args[1])) {
                player.sendMessage(prefix + "Diese Map existiert nicht.");
                return false;
            }

            player.teleport(Bukkit.getWorld(args[1]).getSpawnLocation());
            final ReviveMCScoreboardBuilder cyturaScoreboardBuilder = reviveMCPlayer.getCyturaScoreboardBuilder();
            cyturaScoreboardBuilder.updateBoard(4, " §8» ", reviveMCPlayer.getSecondColor() + WorldModule.getWorldModule().getWorldState(Bukkit.getPlayer(reviveMCPlayer.getUuid()).getWorld().getName()));
            cyturaScoreboardBuilder.updateBoard(1, " §8» ", reviveMCPlayer.getSecondColor() + player.getWorld().getName());
            return false;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (args.length != 4) {
                player.sendMessage(prefix + "Verwende: 'map create <Name> <Typ> <voidWorld>'");
                return false;
            }

            if (worldModule.existMap(args[1])) {
                player.sendMessage(prefix + "Diese Map existiert bereits.");
                return false;
            }

            String type = args[2];
            if (!(type.equals("BedWars") || type.equals("RushFight") || type.equals("MLGRush") || type.equals("BuildFFA"))) {
                player.sendMessage(prefix + "Folgende Typen gibt es: BedWars, RushFight, MLGRush und BuildFFA");
                return false;
            }

            player.sendMessage(prefix + "Deine Welt wird erstellt...");
            worldModule.createWorld(args[1], type, "IN PROGRESS", player.getName(), args[3]);
            player.sendMessage(prefix + "Deine Welt wurde erstellt.");
         }
        return false;
    }
}
