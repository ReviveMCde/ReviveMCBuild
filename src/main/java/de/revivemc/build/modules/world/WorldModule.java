package de.revivemc.build.modules.world;

import de.revivemc.build.BuildSystem;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import de.revivemc.core.maputils.world.BaseWorldCreator;

public class WorldModule {

    private static WorldModule worldModule;
    private World world;

    public WorldModule() {
        worldModule = this;
    }

    public void createWorld(String worldName, String worldType, String worldStatus, String creator, String cleanWorld) {
        BuildSystem.getInstance().getDatabaseDriver().update("INSERT INTO `Maps`(`MapName`, `MapType`, `MapStatus`, `MapCreator`) VALUES ('" + worldName + "','" + worldType + "','" + worldStatus + "','" + creator + "')");

        WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.type(WorldType.FLAT);
        worldCreator.generateStructures(false);
        if (Objects.equals(cleanWorld, "true")) {
            worldCreator.generatorSettings("2;0;1;");
        }
        world = worldCreator.createWorld();
    }

    public String getWorldState(String worldName) {
        String status = "";
        try {
            ResultSet resultSet = BuildSystem.getInstance().getDatabaseDriver().query("SELECT * FROM Maps WHERE MapName='" + worldName + "'");
            while (resultSet.next()) {
                status = resultSet.getString("MapStatus");
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
        return status;
    }

    public void setWorldState(String worldName, String worldStatus) {
        BuildSystem.getInstance().getDatabaseDriver().update("UPDATE Maps SET MapStatus='" + worldStatus + "' WHERE MapName='" + worldName + "'");
    }

    public void deleteWorld(String worldName) {
        BuildSystem.getInstance().getDatabaseDriver().update("DELETE FROM `Maps` WHERE MapName='" + worldName + "'");
        Server server = Bukkit.getServer();
        World world = Bukkit.getWorld(worldName);
        File active  = world.getWorldFolder();

        server.unloadWorld(world, false);
        Chunk[] chunks = world.getLoadedChunks();
        for (Chunk chunk : chunks) {
            chunk.unload(false);
        }
        FileUtils.deleteQuietly(active);
    }

    public boolean existMap(String worldName) {
        try {
            final ResultSet resultSet = BuildSystem.getInstance().getDatabaseDriver().query("SELECT * FROM `Maps` WHERE `MapName`= '" + worldName + "'");
            if (resultSet.next()) {
                return resultSet.getString("MapName") != null;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static WorldModule getWorldModule() {
        return worldModule;
    }
}
