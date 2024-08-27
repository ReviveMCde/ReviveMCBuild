package de.revivemc.build.listener.inventory;

import de.revivemc.build.BuildSystem;
import de.revivemc.build.modules.inventory.InventoryModule;
import de.revivemc.build.modules.world.WorldModule;
import de.revivemc.core.ReviveMCAPI;
import de.revivemc.core.entitiesutils.items.ItemCreator;
import de.revivemc.core.playerutils.ReviveMCPlayer;
import de.revivemc.core.playerutils.scoreboard.ReviveMCScoreboardBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        final ReviveMCPlayer reviveMCPlayer = ReviveMCAPI.getInstance().getCyturaPlayerManager().getPlayers().get(player.getUniqueId());
        InventoryModule inventoryModule = new InventoryModule(player);

        if (event.getInventory().getName().equals("§8» §eMaps §8«")) {
            event.setCancelled(true);
            if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§8» §cBedWars")) {
                inventoryModule.openBedWarsMapInventory();
            }

            if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§8» §eMLGRush")) {
                inventoryModule.openMLGRushMapInventory();
            }

            if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§8» §9BuildFFA")) {
                inventoryModule.openBuildFFAMapInventory();
            }

            if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§8» §bRushFight")) {
                inventoryModule.openRushFightMapInventory();
            }
        }

        //BEDWARS
        if (event.getInventory().getName().equalsIgnoreCase("§8» §cBedWars §8× §7Maps §8«")) {
            event.setCancelled(true);
            try {
                final ResultSet resultSet = BuildSystem.getInstance().getDatabaseDriver().query("SELECT * FROM Maps WHERE MapType='BedWars'");
                while (resultSet.next()) {
                    final Inventory inventory = Bukkit.createInventory(null, 9 * 3, "§8» §c" + resultSet.getString("MapName"));

                    setPlaceholder(inventory);
                    inventory.setItem(11, new ItemCreator(Material.ENDER_PEARL).setName("§8» §7Teleportieren").setAmount(1).toItemStack());
                    inventory.setItem(13, new ItemCreator(Material.SIGN).setName("§8» §7Status ändern").setAmount(1).toItemStack());

                    if (player.hasPermission("build.head")) {
                        inventory.setItem(15, new ItemCreator(Material.REDSTONE_BLOCK).setName("§8» §cMap löschen").setAmount(1).toItemStack());
                    }

                    player.openInventory(inventory);
                }
            }catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        try {
            final ResultSet resultSet = BuildSystem.getInstance().getDatabaseDriver().query("SELECT * FROM Maps WHERE MapType='BedWars'");
            while (resultSet.next()) {
                if (event.getInventory().getName().equalsIgnoreCase("§8» §c" + resultSet.getString("MapName"))) {
                    event.setCancelled(true);
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §7Teleportieren")) {
                        event.getView().close();
                        if (resultSet.getString("MapStatus").equalsIgnoreCase("IN PROGRESS")) {
                            player.teleport(Bukkit.getWorld(resultSet.getString("MapName")).getSpawnLocation());
                            final ReviveMCScoreboardBuilder cyturaScoreboardBuilder = reviveMCPlayer.getCyturaScoreboardBuilder();
                            cyturaScoreboardBuilder.updateBoard(4, " §8» ", reviveMCPlayer.getSecondColor() + resultSet.getString("MapStatus"));
                            cyturaScoreboardBuilder.updateBoard(1, " §8» ", reviveMCPlayer.getSecondColor() + player.getWorld().getName());
                        } else {
                            if (player.hasPermission("build.head")) {
                                player.teleport(Bukkit.getWorld(resultSet.getString("MapName")).getSpawnLocation());
                                final ReviveMCScoreboardBuilder cyturaScoreboardBuilder = reviveMCPlayer.getCyturaScoreboardBuilder();
                                cyturaScoreboardBuilder.updateBoard(4, " §8» ", reviveMCPlayer.getSecondColor() + resultSet.getString("MapStatus"));
                                cyturaScoreboardBuilder.updateBoard(1, " §8» ", reviveMCPlayer.getSecondColor() + player.getWorld().getName());
                            } else {
                                player.sendMessage(BuildSystem.getInstance().getPrefix(reviveMCPlayer) + "Diese Map wurde bereits als 'FINISHED' gestellt.");
                            }
                        }
                    }

                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §cMap löschen")) {
                        final Inventory inventory = Bukkit.createInventory(null, 9 * 3, "§8» §c" + resultSet.getString("MapName") + " §8× §7Löschen");

                        setPlaceholder(inventory);

                        inventory.setItem(11, new ItemCreator(Material.WOOL, (short) 5).setName("§8» §aJa").setAmount(1).toItemStack());
                        inventory.setItem(13, new ItemCreator(Material.SIGN).setName("§8» §7Möchtest du die Map §c" + resultSet.getString("MapName") + " §7wirklich löschen?").setAmount(1).toItemStack());
                        inventory.setItem(15, new ItemCreator(Material.WOOL, (short) 14).setName("§8» §cNein").setAmount(1).toItemStack());

                        player.openInventory(inventory);
                    }

                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §7Status ändern")) {
                        event.getView().close();
                        if (resultSet.getString("MapStatus").equalsIgnoreCase("IN PROGRESS")) {
                            final Inventory inventory = Bukkit.createInventory(null, 9 * 3, "§8» §c" + resultSet.getString("MapName") + " §8× §7Status");

                            setPlaceholder(inventory);
                            inventory.setItem(11, new ItemCreator(Material.DIAMOND_AXE).setName("§8» §7IN PROGRESS").setAmount(1).toItemStack());
                            inventory.setItem(15, new ItemCreator(Material.EMERALD_BLOCK).setName("§8» §aFINISHED").setAmount(1).toItemStack());

                            player.openInventory(inventory);
                        } else {
                            if (player.hasPermission("build.head")) {
                                final Inventory inventory = Bukkit.createInventory(null, 9 * 3, "§8» §c" + resultSet.getString("MapName") + " §8× §7Status");

                                setPlaceholder(inventory);
                                inventory.setItem(11, new ItemCreator(Material.DIAMOND_AXE).setName("§8» §7IN PROGRESS").setAmount(1).toItemStack());
                                inventory.setItem(15, new ItemCreator(Material.EMERALD_BLOCK).setName("§8» §aFINISHED").setAmount(1).toItemStack());

                                player.openInventory(inventory);
                            } else {
                                player.sendMessage(BuildSystem.getInstance().getPrefix(reviveMCPlayer) + "Der Map Status dieser Map kann nicht mehr verändert werden.");
                            }
                        }
                    }
                }

                if (event.getInventory().getName().equalsIgnoreCase("§8» §c" + resultSet.getString("MapName") + " §8× §7Löschen")) {
                    event.setCancelled(true);
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §aJa")) {
                        player.sendMessage(BuildSystem.getInstance().getPrefix(reviveMCPlayer) + "Du hast die Map " + reviveMCPlayer.getSecondColor() + resultSet.getString("MapName") + " §7gelöscht.");
                        WorldModule.getWorldModule().deleteWorld(resultSet.getString("MapName"));
                        event.getView().close();
                    }

                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §cNein")) {
                        event.getView().close();
                    }
                }

                if (event.getInventory().getName().equalsIgnoreCase("§8» §c" + resultSet.getString("MapName") + " §8× §7Status")) {
                    event.setCancelled(true);
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §7IN PROGRESS")) {
                        event.getView().close();
                        WorldModule.getWorldModule().setWorldState(resultSet.getString("MapName"), "IN PROGRESS");
                    }

                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §aFINISHED")) {
                        event.getView().close();
                        WorldModule.getWorldModule().setWorldState(resultSet.getString("MapName"), "FINISHED");
                    }
                }
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
        }

        //BuildFFA
        if (event.getInventory().getName().equalsIgnoreCase("§8» §9BuildFFA §8× §7Maps §8«")) {
            event.setCancelled(true);
            try {
                final ResultSet resultSet = BuildSystem.getInstance().getDatabaseDriver().query("SELECT * FROM Maps WHERE MapType='BuildFFA'");
                while (resultSet.next()) {
                    final Inventory inventory = Bukkit.createInventory(null, 9 * 3, "§8» §9" + resultSet.getString("MapName"));

                    setPlaceholder(inventory);
                    inventory.setItem(11, new ItemCreator(Material.ENDER_PEARL).setName("§8» §7Teleportieren").setAmount(1).toItemStack());
                    inventory.setItem(13, new ItemCreator(Material.SIGN).setName("§8» §7Status ändern").setAmount(1).toItemStack());

                    if (player.hasPermission("build.head")) {
                        inventory.setItem(15, new ItemCreator(Material.REDSTONE_BLOCK).setName("§8» §cMap löschen").setAmount(1).toItemStack());
                    }

                    player.openInventory(inventory);
                }
            }catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        try {
            final ResultSet resultSet = BuildSystem.getInstance().getDatabaseDriver().query("SELECT * FROM Maps WHERE MapType='BuildFFA'");
            while (resultSet.next()) {
                if (event.getInventory().getName().equalsIgnoreCase("§8» §9" + resultSet.getString("MapName"))) {
                    event.setCancelled(true);
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §7Teleportieren")) {
                        event.getView().close();
                        if (resultSet.getString("MapStatus").equalsIgnoreCase("IN PROGRESS")) {
                            player.teleport(Bukkit.getWorld(resultSet.getString("MapName")).getSpawnLocation());
                            final ReviveMCScoreboardBuilder cyturaScoreboardBuilder = reviveMCPlayer.getCyturaScoreboardBuilder();
                            cyturaScoreboardBuilder.updateBoard(4, " §8» ", reviveMCPlayer.getSecondColor() + resultSet.getString("MapStatus"));
                            cyturaScoreboardBuilder.updateBoard(1, " §8» ", reviveMCPlayer.getSecondColor() + player.getWorld().getName());
                        } else {
                            if (player.hasPermission("build.head")) {
                                player.teleport(Bukkit.getWorld(resultSet.getString("MapName")).getSpawnLocation());
                                final ReviveMCScoreboardBuilder cyturaScoreboardBuilder = reviveMCPlayer.getCyturaScoreboardBuilder();
                                cyturaScoreboardBuilder.updateBoard(4, " §8» ", reviveMCPlayer.getSecondColor() + resultSet.getString("MapStatus"));
                                cyturaScoreboardBuilder.updateBoard(1, " §8» ", reviveMCPlayer.getSecondColor() + player.getWorld().getName());
                            } else {
                                player.sendMessage(BuildSystem.getInstance().getPrefix(reviveMCPlayer) + "Diese Map wurde bereits als 'FINISHED' gestellt.");
                            }
                        }
                    }

                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §cMap löschen")) {
                        final Inventory inventory = Bukkit.createInventory(null, 9 * 3, "§8» §9" + resultSet.getString("MapName") + " §8× §7Löschen");

                        setPlaceholder(inventory);

                        inventory.setItem(11, new ItemCreator(Material.WOOL, (short) 5).setName("§8» §aJa").setAmount(1).toItemStack());
                        inventory.setItem(13, new ItemCreator(Material.SIGN).setName("§8» §7Möchtest du die Map §9" + resultSet.getString("MapName") + " §7wirklich löschen?").setAmount(1).toItemStack());
                        inventory.setItem(15, new ItemCreator(Material.WOOL, (short) 14).setName("§8» §cNein").setAmount(1).toItemStack());

                        player.openInventory(inventory);
                    }

                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §7Status ändern")) {
                        event.getView().close();
                        if (resultSet.getString("MapStatus").equalsIgnoreCase("IN PROGRESS")) {
                            final Inventory inventory = Bukkit.createInventory(null, 9 * 3, "§8» §9" + resultSet.getString("MapName") + " §8× §7Status");

                            setPlaceholder(inventory);
                            inventory.setItem(11, new ItemCreator(Material.DIAMOND_AXE).setName("§8» §7IN PROGRESS").setAmount(1).toItemStack());
                            inventory.setItem(15, new ItemCreator(Material.EMERALD_BLOCK).setName("§8» §aFINISHED").setAmount(1).toItemStack());

                            player.openInventory(inventory);
                        } else {
                            if (player.hasPermission("build.head")) {
                                final Inventory inventory = Bukkit.createInventory(null, 9 * 3, "§8» §9" + resultSet.getString("MapName") + " §8× §7Status");

                                setPlaceholder(inventory);
                                inventory.setItem(11, new ItemCreator(Material.DIAMOND_AXE).setName("§8» §7IN PROGRESS").setAmount(1).toItemStack());
                                inventory.setItem(15, new ItemCreator(Material.EMERALD_BLOCK).setName("§8» §aFINISHED").setAmount(1).toItemStack());

                                player.openInventory(inventory);
                            } else {
                                player.sendMessage(BuildSystem.getInstance().getPrefix(reviveMCPlayer) + "Der Map Status dieser Map kann nicht mehr verändert werden.");
                            }
                        }
                    }
                }

                if (event.getInventory().getName().equalsIgnoreCase("§8» §9" + resultSet.getString("MapName") + " §8× §7Löschen")) {
                    event.setCancelled(true);
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §aJa")) {
                        player.sendMessage(BuildSystem.getInstance().getPrefix(reviveMCPlayer) + "Du hast die Map " + reviveMCPlayer.getSecondColor() + resultSet.getString("MapName") + " §7gelöscht.");
                        WorldModule.getWorldModule().deleteWorld(resultSet.getString("MapName"));
                        event.getView().close();
                    }

                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §cNein")) {
                        event.getView().close();
                    }
                }

                if (event.getInventory().getName().equalsIgnoreCase("§8» §9" + resultSet.getString("MapName") + " §8× §7Status")) {
                    event.setCancelled(true);
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §7IN PROGRESS")) {
                        event.getView().close();
                        WorldModule.getWorldModule().setWorldState(resultSet.getString("MapName"), "IN PROGRESS");
                    }

                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §aFINISHED")) {
                        event.getView().close();
                        WorldModule.getWorldModule().setWorldState(resultSet.getString("MapName"), "FINISHED");
                    }
                }
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
        }

        //RushFight
        if (event.getInventory().getName().equalsIgnoreCase("§8» §bRushFight §8× §7Maps §8«")) {
            event.setCancelled(true);
            try {
                final ResultSet resultSet = BuildSystem.getInstance().getDatabaseDriver().query("SELECT * FROM Maps WHERE MapType='RushFight'");
                while (resultSet.next()) {
                    final Inventory inventory = Bukkit.createInventory(null, 9 * 3, "§8» §b" + resultSet.getString("MapName"));

                    setPlaceholder(inventory);
                    inventory.setItem(11, new ItemCreator(Material.ENDER_PEARL).setName("§8» §7Teleportieren").setAmount(1).toItemStack());
                    inventory.setItem(13, new ItemCreator(Material.SIGN).setName("§8» §7Status ändern").setAmount(1).toItemStack());

                    if (player.hasPermission("build.head")) {
                        inventory.setItem(15, new ItemCreator(Material.REDSTONE_BLOCK).setName("§8» §cMap löschen").setAmount(1).toItemStack());
                    }

                    player.openInventory(inventory);
                }
            }catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        try {
            final ResultSet resultSet = BuildSystem.getInstance().getDatabaseDriver().query("SELECT * FROM Maps WHERE MapType='RushFight'");
            while (resultSet.next()) {
                if (event.getInventory().getName().equalsIgnoreCase("§8» §b" + resultSet.getString("MapName"))) {
                    event.setCancelled(true);
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §7Teleportieren")) {
                        event.getView().close();
                        if (resultSet.getString("MapStatus").equalsIgnoreCase("IN PROGRESS")) {
                            player.teleport(Bukkit.getWorld(resultSet.getString("MapName")).getSpawnLocation());
                            final ReviveMCScoreboardBuilder cyturaScoreboardBuilder = reviveMCPlayer.getCyturaScoreboardBuilder();
                            cyturaScoreboardBuilder.updateBoard(4, " §8» ", reviveMCPlayer.getSecondColor() + resultSet.getString("MapStatus"));
                            cyturaScoreboardBuilder.updateBoard(1, " §8» ", reviveMCPlayer.getSecondColor() + player.getWorld().getName());
                        } else {
                            if (player.hasPermission("build.head")) {
                                player.teleport(Bukkit.getWorld(resultSet.getString("MapName")).getSpawnLocation());
                                final ReviveMCScoreboardBuilder cyturaScoreboardBuilder = reviveMCPlayer.getCyturaScoreboardBuilder();
                                cyturaScoreboardBuilder.updateBoard(4, " §8» ", reviveMCPlayer.getSecondColor() + resultSet.getString("MapStatus"));
                                cyturaScoreboardBuilder.updateBoard(1, " §8» ", reviveMCPlayer.getSecondColor() + player.getWorld().getName());
                            } else {
                                player.sendMessage(BuildSystem.getInstance().getPrefix(reviveMCPlayer) + "Diese Map wurde bereits als 'FINISHED' gestellt.");
                            }
                        }
                    }

                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §cMap löschen")) {
                        final Inventory inventory = Bukkit.createInventory(null, 9 * 3, "§8» §b" + resultSet.getString("MapName") + " §8× §7Löschen");

                        setPlaceholder(inventory);

                        inventory.setItem(11, new ItemCreator(Material.WOOL, (short) 5).setName("§8» §aJa").setAmount(1).toItemStack());
                        inventory.setItem(13, new ItemCreator(Material.SIGN).setName("§8» §7Möchtest du die Map §b" + resultSet.getString("MapName") + " §7wirklich löschen?").setAmount(1).toItemStack());
                        inventory.setItem(15, new ItemCreator(Material.WOOL, (short) 14).setName("§8» §cNein").setAmount(1).toItemStack());

                        player.openInventory(inventory);
                    }

                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §7Status ändern")) {
                        event.getView().close();
                        if (resultSet.getString("MapStatus").equalsIgnoreCase("IN PROGRESS")) {
                            final Inventory inventory = Bukkit.createInventory(null, 9 * 3, "§8» §b" + resultSet.getString("MapName") + " §8× §7Status");

                            setPlaceholder(inventory);
                            inventory.setItem(11, new ItemCreator(Material.DIAMOND_AXE).setName("§8» §7IN PROGRESS").setAmount(1).toItemStack());
                            inventory.setItem(15, new ItemCreator(Material.EMERALD_BLOCK).setName("§8» §aFINISHED").setAmount(1).toItemStack());

                            player.openInventory(inventory);
                        } else {
                            if (player.hasPermission("build.head")) {
                                final Inventory inventory = Bukkit.createInventory(null, 9 * 3, "§8» §b" + resultSet.getString("MapName") + " §8× §7Status");

                                setPlaceholder(inventory);
                                inventory.setItem(11, new ItemCreator(Material.DIAMOND_AXE).setName("§8» §7IN PROGRESS").setAmount(1).toItemStack());
                                inventory.setItem(15, new ItemCreator(Material.EMERALD_BLOCK).setName("§8» §aFINISHED").setAmount(1).toItemStack());

                                player.openInventory(inventory);
                            } else {
                                player.sendMessage(BuildSystem.getInstance().getPrefix(reviveMCPlayer) + "Der Map Status dieser Map kann nicht mehr verändert werden.");
                            }
                        }
                    }
                }

                if (event.getInventory().getName().equalsIgnoreCase("§8» §b" + resultSet.getString("MapName") + " §8× §7Löschen")) {
                    event.setCancelled(true);
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §aJa")) {
                        player.sendMessage(BuildSystem.getInstance().getPrefix(reviveMCPlayer) + "Du hast die Map " + reviveMCPlayer.getSecondColor() + resultSet.getString("MapName") + " §7gelöscht.");
                        WorldModule.getWorldModule().deleteWorld(resultSet.getString("MapName"));
                        event.getView().close();
                    }

                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §cNein")) {
                        event.getView().close();
                    }
                }

                if (event.getInventory().getName().equalsIgnoreCase("§8» §b" + resultSet.getString("MapName") + " §8× §7Status")) {
                    event.setCancelled(true);
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §7IN PROGRESS")) {
                        event.getView().close();
                        WorldModule.getWorldModule().setWorldState(resultSet.getString("MapName"), "IN PROGRESS");
                    }

                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §aFINISHED")) {
                        event.getView().close();
                        WorldModule.getWorldModule().setWorldState(resultSet.getString("MapName"), "FINISHED");
                    }
                }
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
        }

        //MLGRush
        if (event.getInventory().getName().equalsIgnoreCase("§8» §eMLGRush §8× §7Maps §8«")) {
            event.setCancelled(true);
            try {
                final ResultSet resultSet = BuildSystem.getInstance().getDatabaseDriver().query("SELECT * FROM Maps WHERE MapType='MLGRush'");
                while (resultSet.next()) {
                    final Inventory inventory = Bukkit.createInventory(null, 9 * 3, "§8» §e" + resultSet.getString("MapName"));

                    setPlaceholder(inventory);
                    inventory.setItem(11, new ItemCreator(Material.ENDER_PEARL).setName("§8» §7Teleportieren").setAmount(1).toItemStack());
                    inventory.setItem(13, new ItemCreator(Material.SIGN).setName("§8» §7Status ändern").setAmount(1).toItemStack());

                    if (player.hasPermission("build.head")) {
                        inventory.setItem(15, new ItemCreator(Material.REDSTONE_BLOCK).setName("§8» §cMap löschen").setAmount(1).toItemStack());
                    }

                    player.openInventory(inventory);
                }
            }catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        try {
            final ResultSet resultSet = BuildSystem.getInstance().getDatabaseDriver().query("SELECT * FROM Maps WHERE MapType='MLGRush'");
            while (resultSet.next()) {
                if (event.getInventory().getName().equalsIgnoreCase("§8» §e" + resultSet.getString("MapName"))) {
                    event.setCancelled(true);
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §7Teleportieren")) {
                        event.getView().close();
                        if (resultSet.getString("MapStatus").equalsIgnoreCase("IN PROGRESS")) {
                            player.teleport(Bukkit.getWorld(resultSet.getString("MapName")).getSpawnLocation());
                            final ReviveMCScoreboardBuilder cyturaScoreboardBuilder = reviveMCPlayer.getCyturaScoreboardBuilder();
                            cyturaScoreboardBuilder.updateBoard(4, " §8» ", reviveMCPlayer.getSecondColor() + resultSet.getString("MapStatus"));
                            cyturaScoreboardBuilder.updateBoard(1, " §8» ", reviveMCPlayer.getSecondColor() + player.getWorld().getName());
                        } else {
                            if (player.hasPermission("build.head")) {
                                player.teleport(Bukkit.getWorld(resultSet.getString("MapName")).getSpawnLocation());
                                final ReviveMCScoreboardBuilder cyturaScoreboardBuilder = reviveMCPlayer.getCyturaScoreboardBuilder();
                                cyturaScoreboardBuilder.updateBoard(4, " §8» ", reviveMCPlayer.getSecondColor() + resultSet.getString("MapStatus"));
                                cyturaScoreboardBuilder.updateBoard(1, " §8» ", reviveMCPlayer.getSecondColor() + player.getWorld().getName());
                            } else {
                                player.sendMessage(BuildSystem.getInstance().getPrefix(reviveMCPlayer) + "Diese Map wurde bereits als 'FINISHED' gestellt.");
                            }
                        }
                    }

                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §cMap löschen")) {
                        final Inventory inventory = Bukkit.createInventory(null, 9 * 3, "§8» §e" + resultSet.getString("MapName") + " §8× §7Löschen");

                        setPlaceholder(inventory);

                        inventory.setItem(11, new ItemCreator(Material.WOOL, (short) 5).setName("§8» §aJa").setAmount(1).toItemStack());
                        inventory.setItem(13, new ItemCreator(Material.SIGN).setName("§8» §7Möchtest du die Map §e" + resultSet.getString("MapName") + " §7wirklich löschen?").setAmount(1).toItemStack());
                        inventory.setItem(15, new ItemCreator(Material.WOOL, (short) 14).setName("§8» §cNein").setAmount(1).toItemStack());

                        player.openInventory(inventory);
                    }

                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §7Status ändern")) {
                        event.getView().close();
                        if (resultSet.getString("MapStatus").equalsIgnoreCase("IN PROGRESS")) {
                            final Inventory inventory = Bukkit.createInventory(null, 9 * 3, "§8» §e" + resultSet.getString("MapName") + " §8× §7Status");

                            setPlaceholder(inventory);
                            inventory.setItem(11, new ItemCreator(Material.DIAMOND_AXE).setName("§8» §7IN PROGRESS").setAmount(1).toItemStack());
                            inventory.setItem(15, new ItemCreator(Material.EMERALD_BLOCK).setName("§8» §aFINISHED").setAmount(1).toItemStack());

                            player.openInventory(inventory);
                        } else {
                            if (player.hasPermission("build.head")) {
                                final Inventory inventory = Bukkit.createInventory(null, 9 * 3, "§8» §e" + resultSet.getString("MapName") + " §8× §7Status");

                                setPlaceholder(inventory);
                                inventory.setItem(11, new ItemCreator(Material.DIAMOND_AXE).setName("§8» §7IN PROGRESS").setAmount(1).toItemStack());
                                inventory.setItem(15, new ItemCreator(Material.EMERALD_BLOCK).setName("§8» §aFINISHED").setAmount(1).toItemStack());

                                player.openInventory(inventory);
                            } else {
                                player.sendMessage(BuildSystem.getInstance().getPrefix(reviveMCPlayer) + "Der Map Status dieser Map kann nicht mehr verändert werden.");
                            }
                        }
                    }
                }

                if (event.getInventory().getName().equalsIgnoreCase("§8» §e" + resultSet.getString("MapName") + " §8× §7Löschen")) {
                    event.setCancelled(true);
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §aJa")) {
                        player.sendMessage(BuildSystem.getInstance().getPrefix(reviveMCPlayer) + "Du hast die Map " + reviveMCPlayer.getSecondColor() + resultSet.getString("MapName") + " §7gelöscht.");
                        WorldModule.getWorldModule().deleteWorld(resultSet.getString("MapName"));
                        event.getView().close();
                    }

                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §cNein")) {
                        event.getView().close();
                    }
                }

                if (event.getInventory().getName().equalsIgnoreCase("§8» §e" + resultSet.getString("MapName") + " §8× §7Status")) {
                    event.setCancelled(true);
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §7IN PROGRESS")) {
                        event.getView().close();
                        WorldModule.getWorldModule().setWorldState(resultSet.getString("MapName"), "IN PROGRESS");
                    }

                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §aFINISHED")) {
                        event.getView().close();
                        WorldModule.getWorldModule().setWorldState(resultSet.getString("MapName"), "FINISHED");
                    }
                }
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    private void setPlaceholder(Inventory  inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            final ItemStack pane = new ItemCreator(Material.STAINED_GLASS_PANE, (short) 7).setName(" ").setAmount(1).toItemStack();
            inventory.setItem(i, pane);
        }
    }
}
