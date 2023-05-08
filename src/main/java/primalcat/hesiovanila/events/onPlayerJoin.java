package primalcat.hesiovanila.events;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import primalcat.hesiovanila.HesioVanila;
import primalcat.hesiovanila.Utilities;
import primalcat.hesiovanila.manager.AccountManager;
import primalcat.hesiovanila.manager.AuthenticationManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.google.common.io.Resources.getResource;
import static org.bukkit.Bukkit.getLogger;

public class onPlayerJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException, SQLException, ClassNotFoundException {
        // Get the player who just joined
        Player player = event.getPlayer();

        // Get the account manager, utilities, connection, and player name
        AccountManager accountManager = HesioVanila.getAccountManager();
        Utilities utilities = new Utilities();
        Connection connection = HesioVanila.getConnection();
        String name = player.getName();

        // Check if the player is inside a Nether portal
        Block block = player.getLocation().getBlock();
        getLogger().info(name + " is join inside nether portal");
        // Save from portal trap
        // Teleport player to nearest safe location
        World world = player.getWorld();
        Location location = player.getLocation();
        if (block.getType() == Material.NETHER_PORTAL) {
            Location safeLocation = utilities.findSafeLocation(world, location.getBlockX(), location.getBlockY(), location.getBlockZ(), 8);
            if (safeLocation != null) {
                player.teleport(safeLocation);
            } else {
                location.getBlock().breakNaturally();
            }
        }

        // TODO: Load welcome message from configuration file
        // Check if the player has an account
        if (accountManager.searchPlayerByName(name)) {
            // If the player has an account, send a login prompt
            player.sendTitle("§l§qPlease Login", "§7/login §8<password>", 20, 10, 1728000);
            player.sendMessage("/login");
        } else {
            // If the player does not have an account, send a registration prompt
            player.sendTitle("§l§qPlease register", "§7/register §8<password> <password>", 20, 10, 1728000);
            player.sendMessage("/register");
        }

        // Log a message to the server console
        getLogger().info(player.getName() + " has joined the server!");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String name = event.getPlayer().getName();
        if (!HesioVanila.getAuthenticationManager().isPlayerAuthenticated(name)) {
            event.setCancelled(true);
            // Optionally, you can send a message to the player to let them know they can't chat
            event.getPlayer().sendMessage("You cannot send messages until you are authenticated.");
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        if (!HesioVanila.getAuthenticationManager().isPlayerAuthenticated(event.getPlayer().getName())) {
            if (!command.startsWith("/register") && !command.startsWith("/login")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("You must be authenticated to use commands.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent e) {
        String name = e.getPlayer().getName();
        if (!HesioVanila.getAuthenticationManager().isPlayerAuthenticated(name)) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent e) {
        String name = e.getPlayer().getName();
        if (!HesioVanila.getAuthenticationManager().isPlayerAuthenticated(name)) e.setCancelled(true);
    }




    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.isCancelled()) return;
        Player player = e.getPlayer();
        String name = player.getName();
        if (HesioVanila.getAuthenticationManager().isPlayerAuthenticated(name)) return;

        Location to = e.getTo();
        if (to != null && e.getFrom().getY() > to.getY()) return;

        player.teleport(e.getFrom());
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Check if the entity that caused the damage is a player
        if (event.getCause() == EntityDamageEvent.DamageCause.SUICIDE) return;
        if (!event.isCancelled() && (event.getEntity() instanceof Player && !HesioVanila.getAuthenticationManager().isPlayerAuthenticated(event.getEntity().getName().toString()) || event.getDamager() instanceof Player && !HesioVanila.getAuthenticationManager().isPlayerAuthenticated(event.getDamager().getName())))
            event.setCancelled(true);
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        String name = e.getWhoClicked().getName();
        if (HesioVanila.getAuthenticationManager().isPlayerAuthenticated(name)) return;
        e.setCancelled(true);
//        if (!plugin.getLoginManagement().isAuthenticated(name))
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        String name = e.getPlayer().getName();
        if (HesioVanila.getAuthenticationManager().isPlayerAuthenticated(name)) return;
        e.setCancelled(true);
//        if (!e.isCancelled() && !plugin.getLoginManagement().isAuthenticated(name))
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        String name = player.getName();
        AuthenticationManager authenticationManager = HesioVanila.getAuthenticationManager();
        authenticationManager.deauthenticatePlayer(name);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.SUICIDE) return;
        if (!event.isCancelled() && event.getEntity() instanceof Player && !HesioVanila.getAuthenticationManager().isPlayerAuthenticated(event.getEntity().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        String name = event.getPlayer().getName();
        if (!HesioVanila.getAuthenticationManager().isPlayerAuthenticated(name)) event.setCancelled(true);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        String name = e.getPlayer().getName();
        if (!HesioVanila.getAuthenticationManager().isPlayerAuthenticated(name)) e.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        String name = e.getPlayer().getName();
        if (!HesioVanila.getAuthenticationManager().isPlayerAuthenticated(name)) e.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerShearEntity(PlayerShearEntityEvent e) {
        String name = e.getPlayer().getName();
        e.setCancelled(true);
        if (!HesioVanila.getAuthenticationManager().isPlayerAuthenticated(name)) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerFish(PlayerFishEvent e) {
        String name = e.getPlayer().getName();
        if (!HesioVanila.getAuthenticationManager().isPlayerAuthenticated(name)) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerBedEnter(PlayerBedEnterEvent e) {
        String name = e.getPlayer().getName();
        if (!HesioVanila.getAuthenticationManager().isPlayerAuthenticated(name)) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerEditBook(PlayerEditBookEvent e) {
        String name = e.getPlayer().getName();
        if (!HesioVanila.getAuthenticationManager().isPlayerAuthenticated(name)) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSignChange(SignChangeEvent e) {
        String name = e.getPlayer().getName();
        if (!HesioVanila.getAuthenticationManager().isPlayerAuthenticated(name)) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerItemHeld(PlayerItemHeldEvent e) {
        String name = e.getPlayer().getName();
        if (!HesioVanila.getAuthenticationManager().isPlayerAuthenticated(name)) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
        String name = e.getPlayer().getName();
        if (!HesioVanila.getAuthenticationManager().isPlayerAuthenticated(name)) e.setCancelled(true);
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPickupItem(PlayerPickupItemEvent e) {
        String name = e.getPlayer().getName();
        if (!HesioVanila.getAuthenticationManager().isPlayerAuthenticated(name)) e.setCancelled(true);
    }
}
