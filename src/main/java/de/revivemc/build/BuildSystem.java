package de.revivemc.build;

import de.revivemc.build.commands.CPSCommand;
import de.revivemc.build.commands.GamemodeCommand;
import de.revivemc.build.commands.MapCommand;
import de.revivemc.build.listener.inventory.InventoryClickListener;
import de.revivemc.build.listener.player.CyturaPlayerJoinListener;
import de.revivemc.build.listener.player.PlayerQuitListener;
import de.revivemc.build.listener.world.BlockBreakListener;
import de.revivemc.build.listener.world.BlockPlaceListener;
import de.revivemc.build.listener.world.WeatherChangeListener;
import de.revivemc.build.modules.database.DatabaseDriver;
import de.revivemc.core.playerutils.ReviveMCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BuildSystem extends JavaPlugin {

    private static BuildSystem instance;
    private DatabaseDriver databaseDriver;

    @Override
    public void onEnable() {
        instance = this;
        databaseDriver = new DatabaseDriver("localhost", "ReviveMC_Cloud", "root", "~aO_8QPm|5S!LNp{?PZt(+Ez%ldr$iY%6My[kjEaYy*D`(4A0FmM1ajku{z402]0");
        databaseDriver.update("CREATE TABLE IF NOT EXISTS Maps(MapName varchar(32), MapType varchar(10), MapStatus varchar(16), MapCreator varchar(16))");
        initListener();
        initCommands();
    }

    @Override
    public void onDisable() {
        /*try {
            final ResultSet resultSet = databaseDriver.query("SELECT * FROM Maps");
            while (resultSet.next()) {
                final World world = Bukkit.getWorld(resultSet.getString("MapName"));
                final WorldCreator worldCreator = new WorldCreator(resultSet.getString("MapName"));
                worldCreator.copy(world);
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
        } */
        databaseDriver.close();
    }

    public void initListener() {
        Bukkit.getPluginManager().registerEvents(new CyturaPlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(), this);
        Bukkit.getPluginManager().registerEvents(new WeatherChangeListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
        //Bukkit.getPluginManager().registerEvents(new CPSCommand(), this);
    }

    public void initCommands() {
        getCommand("gm").setExecutor(new GamemodeCommand());
        getCommand("gamemode").setExecutor(new GamemodeCommand());
        getCommand("map").setExecutor(new MapCommand());
        //getCommand("cps").setExecutor(new CPSCommand());
    }

    public static BuildSystem getInstance() {
        return instance;
    }

    public String getPrefix(ReviveMCPlayer reviveMCPlayer) {
        return "§8» " + reviveMCPlayer.getFirstColor() + "Build §8 §7";
    }

    public DatabaseDriver getDatabaseDriver() {
        return databaseDriver;
    }
}
