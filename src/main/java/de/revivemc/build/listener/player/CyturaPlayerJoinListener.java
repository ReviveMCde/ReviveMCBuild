package de.revivemc.build.listener.player;

import de.revivemc.build.modules.scoreboard.ScoreboardModule;
import de.revivemc.core.ReviveMCAPI;
import de.revivemc.core.playerutils.ReviveMCPlayer;
import de.revivemc.core.playerutils.events.ReviveMCPlayerJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CyturaPlayerJoinListener implements Listener {

    @EventHandler
    public void onCyturaPlayerJoin(ReviveMCPlayerJoinEvent event) {
        Player player = event.getPlayer();

        ReviveMCPlayer reviveMCPlayer = ReviveMCAPI.getInstance().getCyturaPlayerManager().getPlayers().get(player.getUniqueId());
        reviveMCPlayer.setTablist();

        new ScoreboardModule().buildScoreboard(reviveMCPlayer);
    }
}
