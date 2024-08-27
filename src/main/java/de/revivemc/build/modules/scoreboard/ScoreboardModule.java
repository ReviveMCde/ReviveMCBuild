package de.revivemc.build.modules.scoreboard;

import de.revivemc.build.modules.world.WorldModule;
import de.revivemc.core.ReviveMCAPI;
import de.revivemc.core.playerutils.ReviveMCPlayer;
import de.revivemc.core.playerutils.scoreboard.ReviveMCScoreboardBuilder;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;

public class ScoreboardModule {

    public void buildScoreboard(ReviveMCPlayer reviveMCPlayer) {
        reviveMCPlayer.initialScoreboard();
        final ReviveMCScoreboardBuilder cyturaScoreboardBuilder = reviveMCPlayer.getCyturaScoreboardBuilder();
        cyturaScoreboardBuilder.setLine(10, "§8§m---------", "§8§m---------");
        cyturaScoreboardBuilder.setLine(9, " ", " ");
        cyturaScoreboardBuilder.setLine(8, " §8§l» ", "§7Rang");
        cyturaScoreboardBuilder.setLine(7, " §8» ", reviveMCPlayer.getPermissionGroupColor() + reviveMCPlayer.getPermissionGroup().getName());
        cyturaScoreboardBuilder.setLine(6, " ", " ");
        cyturaScoreboardBuilder.setLine(5, " §8§l» ", "§7Status");
        cyturaScoreboardBuilder.setLine(4, " §8» ", reviveMCPlayer.getSecondColor() + "NULL");
        cyturaScoreboardBuilder.setLine(3, " ", " ");
        cyturaScoreboardBuilder.setLine(2, " §8§l» ", "§7Welt");
        cyturaScoreboardBuilder.setLine(1, " §8» ", reviveMCPlayer.getSecondColor() + Bukkit.getPlayer(reviveMCPlayer.getUuid()).getWorld().getName());
        cyturaScoreboardBuilder.setLine(0, " ", " ");
        MinecraftServer.getServer().postToMainThread(() -> {
            cyturaScoreboardBuilder.setBoard(reviveMCPlayer.getFirstColor() + "§lReviveMC §8× §7Build");
            Bukkit.getOnlinePlayers().forEach(player -> {
                ReviveMCAPI.getInstance().getCyturaTablistManager().setDefaultTablist(ReviveMCAPI.getInstance().getCyturaPlayerManager().getPlayers().get(player.getUniqueId()));
            });
        });
    }
}
