package de.revivemc.build.commands;

import de.revivemc.build.BuildSystem;
import de.revivemc.core.ReviveMCAPI;
import de.revivemc.core.playerutils.ReviveMCPlayer;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        final Player player = (Player) sender;
        final ReviveMCPlayer reviveMCPlayer = ReviveMCAPI.getInstance().getCyturaPlayerManager().getPlayers().get(player.getUniqueId());
        if (!player.hasPermission("build.command.gamemode")) {
            player.sendMessage(BuildSystem.getInstance().getPrefix(reviveMCPlayer) + "§cDu hast keine Rechte auf diesen Befehl.");
            return false;
        }

        if (args.length != 1) {
            player.sendMessage(BuildSystem.getInstance().getPrefix(reviveMCPlayer) + "Verwende: 'gamemode (0, 1, 2, 3)'");
            return false;
        }

        int gamemodeId = Integer.parseInt(args[0]);
        switch (gamemodeId) {
            case 0:
                player.setGameMode(GameMode.SURVIVAL);
                player.sendMessage(BuildSystem.getInstance().getPrefix(reviveMCPlayer) + "Du bist nun im Gamemode Surivival");
                break;

            case 1:
                player.setGameMode(GameMode.CREATIVE);
                player.sendMessage(BuildSystem.getInstance().getPrefix(reviveMCPlayer) + "Du bist nun im Gamemode Creative");
                break;

            case 2:
                player.setGameMode(GameMode.ADVENTURE);
                player.sendMessage(BuildSystem.getInstance().getPrefix(reviveMCPlayer) + "Du bist nun im Gamemode Adventure");
                break;

            case 3:
                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage(BuildSystem.getInstance().getPrefix(reviveMCPlayer) + "Du bist nun im Gamemode Spectator");
                break;

            default:
                player.sendMessage(BuildSystem.getInstance().getPrefix(reviveMCPlayer) + "Verwende: 'gamemode (0, 1, 2, 3)'");

        }
        return false;
    }
}
