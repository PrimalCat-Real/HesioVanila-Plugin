package primalcat.hesiovanila.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
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

        AccountManager accountManager = HesioVanila.getAccountManager();

        Connection connection = HesioVanila.getConnection();

        if (accountManager.searchPlayerByName(player.getName())){
            System.out.println("player exist");
            player.sendMessage("/login");
        }else {
//            String welcomeMessage = messages.getString("welcome_message");
            player.sendMessage("/register");
            System.out.println("new player");
        }
        String welcomeMessage = "Welcome Message";

        player.sendMessage(ChatColor.GREEN + welcomeMessage);

        // Disable player movement and actions for 5 seconds
//        player.setWalkSpeed(0.2F);
//        player.setFlySpeed(0.1F);
//        player.setAllowFlight(false);
//        player.setFlying(false);
//        player.setGameMode(GameMode.ADVENTURE);


        // Prevent mobs from targeting the player when they join
//        for (Entity entity : player.getNearbyEntities(32, 32, 32)) {
//            if (entity instanceof Mob) {
//                Mob mob = (Mob) entity;
//                EntityTargetEvent targetEvent = new EntityTargetEvent(mob, player, EntityTargetEvent.TargetReason.CUSTOM);
//                Bukkit.getPluginManager().callEvent(targetEvent);
//                if (!targetEvent.isCancelled()) {
//                    mob.setTarget(player);
//                }
//            }
//        }

        // Log a message to the server console
        getLogger().info(player.getName() + " has joined the server!");
    }

//    @EventHandler
//    public void onBlockBreak(BlockBreakEvent event) {
//        // Check if the player is trying to break a block
//        if (event.getPlayer() != null) {
//            Player player = event.getPlayer();
//
//            // Cancel the event to prevent the player from breaking blocks
//            event.setCancelled(true);
//
//        }
//    }

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
