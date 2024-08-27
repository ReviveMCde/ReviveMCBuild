package de.revivemc.build.modules.inventory;

import de.revivemc.build.BuildSystem;
import de.revivemc.build.modules.world.WorldModule;
import de.revivemc.core.entitiesutils.items.ItemCreator;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryModule {

    private final Player player;

    public InventoryModule(Player player) {
        this.player = player;
    }

    public void openMapInventory() {
        final Inventory inventory = Bukkit.createInventory(null,  9 * 5, "§8» §eMaps §8«");

        setPlaceholder(inventory);
        inventory.setItem(21, new ItemCreator(Material.BED).setName("§8» §cBedWars").setAmount(1).toItemStack());
        inventory.setItem(13, new ItemCreator(Material.SANDSTONE).setName("§8» §eMLGRush").setAmount(1).toItemStack());
        inventory.setItem(23, new ItemCreator(Material.IRON_SWORD).setName("§8» §9BuildFFA").setAmount(1).toItemStack());
        inventory.setItem(31, new ItemCreator(Material.RED_SANDSTONE).setName("§8» §bRushFight").setAmount(1).toItemStack());

        player.openInventory(inventory);
    }

    public void openBedWarsMapInventory() {
        final Inventory inventory = Bukkit.createInventory(null,  9 * 5, "§8» §cBedWars §8× §7Maps §8«");

        setPlaceholder(inventory);
        try {
            final ResultSet resultSet = BuildSystem.getInstance().getDatabaseDriver().query("SELECT * FROM Maps WHERE MapType='BedWars'");
            int i = 0;
            while (resultSet.next()) {
                final ItemStack itemStack = new ItemCreator(Material.BED).setName("§8» §c" + resultSet.getString("MapName")).setLore("§8» §cStatus §8» §7" + resultSet.getString("MapStatus"), "§8» §cErsteller §8» §7" + resultSet.getString("MapCreator")).setAmount(1).toItemStack();
                inventory.setItem(i, itemStack);
                i++;
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
        }

        player.openInventory(inventory);
    }

    public void openBuildFFAMapInventory() {
        final Inventory inventory = Bukkit.createInventory(null,  9 * 5, "§8» §9BuildFFA §8× §7Maps §8«");

        setPlaceholder(inventory);
        try {
            final ResultSet resultSet = BuildSystem.getInstance().getDatabaseDriver().query("SELECT * FROM Maps WHERE MapType='BuildFFA'");
            int i = 0;
            while (resultSet.next()) {
                final ItemStack itemStack = new ItemCreator(Material.IRON_SWORD).setName("§8» §9" + resultSet.getString("MapName")).setLore("§8» §9Status §8» §7" + resultSet.getString("MapStatus"), "§8» §9Ersteller §8» §7" + resultSet.getString("MapCreator")).setAmount(1).toItemStack();
                inventory.setItem(i, itemStack);
                i++;
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
        }

        player.openInventory(inventory);
    }

    public void openRushFightMapInventory() {
        final Inventory inventory = Bukkit.createInventory(null,  9 * 5, "§8» §bRushFight §8× §7Maps §8«");

        setPlaceholder(inventory);
        try {
            final ResultSet resultSet = BuildSystem.getInstance().getDatabaseDriver().query("SELECT * FROM Maps WHERE MapType='RushFight'");
            int i = 0;
            while (resultSet.next()) {
                final ItemStack itemStack = new ItemCreator(Material.RED_SANDSTONE).setName("§8» §b" + resultSet.getString("MapName")).setLore("§8» §bStatus §8» §7" + resultSet.getString("MapStatus"), "§8» §bErsteller §8» §7" + resultSet.getString("MapCreator")).setAmount(1).toItemStack();
                inventory.setItem(i, itemStack);
                i++;
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
        }

        player.openInventory(inventory);
    }

    public void openMLGRushMapInventory() {
        final Inventory inventory = Bukkit.createInventory(null,  9 * 5, "§8» §eMLGRush §8× §7Maps §8«");

        setPlaceholder(inventory);
        try {
            final ResultSet resultSet = BuildSystem.getInstance().getDatabaseDriver().query("SELECT * FROM Maps WHERE MapType='MLGRush'");
            int i = 0;
            while (resultSet.next()) {
                final ItemStack itemStack = new ItemCreator(Material.SANDSTONE).setName("§8» §e" + resultSet.getString("MapName")).setLore("§8» §eStatus §8» §7" + resultSet.getString("MapStatus"), "§8» §eErsteller §8» §7" + resultSet.getString("MapCreator")).setAmount(1).toItemStack();
                inventory.setItem(i, itemStack);
                i++;
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
        }

        player.openInventory(inventory);
    }

    private void setPlaceholder(Inventory  inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            final ItemStack pane = new ItemCreator(Material.STAINED_GLASS_PANE, (short) 7).setName(" ").setAmount(1).toItemStack();
            inventory.setItem(i, pane);
        }
    }
}
