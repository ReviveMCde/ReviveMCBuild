package de.revivemc.build.commands;

import de.revivemc.build.BuildSystem;
import de.revivemc.core.ReviveMCAPI;
import de.revivemc.core.playerutils.ReviveMCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.UUID;

public class CPSCommand implements CommandExecutor, Listener {

    //A HashMap to store the click count for each player
    public static HashMap<UUID, Integer> clickCount = new HashMap<>();

    //A HashMap to store the click rate for each player
    public static HashMap<UUID, Double> clickRate = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        final Player player = (Player) sender;
        final ReviveMCPlayer reviveMCPlayer = ReviveMCAPI.getInstance().getCyturaPlayerManager().getPlayers().get(player.getUniqueId());
        if (!player.hasPermission("game.cps")) {
            player.sendMessage(BuildSystem.getInstance().getPrefix(reviveMCPlayer) + "§cDu hast keine Rechte auf diesen Befehl.");
            return false;
        }

        if (args.length != 1) {
            player.sendMessage(BuildSystem.getInstance().getPrefix(reviveMCPlayer) + "Verwende: 'cps (Name)'");
            return false;
        }

        try {
            Player target = Bukkit.getPlayer(args[0]);
            Bukkit.getScheduler().runTaskLaterAsynchronously(BuildSystem.getInstance(), new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 20; i++) {
                        updateRates();

                        double rate = 0;

                        if (clickRate.get(target.getUniqueId()) != null) {
                            rate = clickRate.get(target.getUniqueId());
                        }


                        player.sendMessage(BuildSystem.getInstance().getPrefix(reviveMCPlayer) + "Die CPS-Rate von dem Spieler " + reviveMCPlayer.getSecondColor() + target.getName() + "beträgt: " + rate);
                    }
                }
            }, 40L);
        }catch (Exception ex) {
            player.sendMessage(BuildSystem.getInstance().getPrefix(reviveMCPlayer) + "Der Spieler " + reviveMCPlayer.getSecondColor() + args[0] + " §7existiert nicht.");
        }
        return false;
    }

    public void onInteract(PlayerInteractEvent event) {

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            int newCount = 1;

            if (clickCount.get(event.getPlayer().getUniqueId()) != null) {
                newCount = newCount +clickCount.get(event.getPlayer().getUniqueId());
            }

            clickCount.put(event.getPlayer().getUniqueId(), newCount);
        }
    }

    private void updateRates()
    {
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin) this, new Runnable()
        {
            public void run()
            {
                //We need to perform the code that follows for each online player.
                //This is done via an enhanced for-loop
                for(Player p:Bukkit.getOnlinePlayers())
                {
                    //Check whether the player has clicked yet to avoid a NullPointerExpection
                    if(clickCount.containsKey(p.getUniqueId()))
                    {
                        //Getting their click count. It is required to cast to a double to get a fractional answer
                        double count = (double) clickCount.get(p.getUniqueId());

                        //Divide the count by 20 to get a rate in clicks per second
                        double rate = count/20;

                        //Put this rate in our clickRate HashMap
                        clickRate.put(p.getUniqueId(), rate);

                        //Reset the clickCount to 0
                        clickCount.put(p.getUniqueId(), 0);
                    }
                }
            }
            //We have an initial delay of 0 seconds, and then the task repeats at an interval of 20 seconds
            //As there are 20 ticks in a second, this can be written as below.
            //I could have done 400L, but this way just makes it easier to read at a glance later on.
        }, 0L, 20*20L);
    }
}
