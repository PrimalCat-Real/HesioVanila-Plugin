package primalcat.hesiovanila.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.io.IOException;
import java.util.Locale;

import static com.google.common.io.Resources.getResource;
import static org.bukkit.Bukkit.getLogger;

public class onPlayerJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException {
        // Get the player who just joined
        Player player = event.getPlayer();

        // Get the player's language
        String language = player.getLocale();

        // Get the player's locale
        Locale playerLocale = Locale.forLanguageTag(player.getLocale());

        // Try to load the appropriate localization file based on the player's locale
//        InputStream inputStream = getResource("lang/messages_" + playerLocale.toLanguageTag() + ".properties").openStream();
//        if (inputStream == null) {
//            // If the appropriate file can't be found, use the default file
//            inputStream = getResource("lang/messages.properties").openStream();
//        }
//
//        // Load the localization file
//        Properties messages = new Properties();
//        try {
//            messages.load(inputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return;
//        }

        // Retrieve the localized welcome message and send it to the player
        String welcomeMessage = "Welcome Message";

        player.sendMessage(ChatColor.GREEN + welcomeMessage);

        // Disable player movement and actions for 5 seconds
        player.setWalkSpeed(0F);
        player.setFlySpeed(0F);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setGameMode(GameMode.ADVENTURE);


        // Prevent mobs from targeting the player when they join
        for (Entity entity : player.getNearbyEntities(32, 32, 32)) {
            if (entity instanceof Mob) {
                Mob mob = (Mob) entity;
                EntityTargetEvent targetEvent = new EntityTargetEvent(mob, player, EntityTargetEvent.TargetReason.CUSTOM);
                Bukkit.getPluginManager().callEvent(targetEvent);
                if (!targetEvent.isCancelled()) {
                    mob.setTarget(player);
                }
            }
        }

        // Log a message to the server console
        getLogger().info(player.getName() + " has joined the server!");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Check if the player is trying to break a block
        if (event.getPlayer() != null) {
            Player player = event.getPlayer();

            // Cancel the event to prevent the player from breaking blocks
            event.setCancelled(true);

        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Get the player who moved
        Player player = event.getPlayer();

        // Set the player's location to a fixed point
        Location fixedLocation = new Location(player.getWorld(), 0, 64, 0);
        player.teleport(fixedLocation);

        // Cancel the event to prevent the player from moving
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Check if the entity that caused the damage is a player
        if (event.getDamager() instanceof Player) {
            // Cancel the event to prevent the player from dealing damage
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Cancel the event to prevent the player from interacting with the environment
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        // Check if the damaged entity is a player
        if (event.getEntity() instanceof Player) {
            // Cancel the event to prevent the player from taking damage
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        event.setCancelled(true);
    }
}
