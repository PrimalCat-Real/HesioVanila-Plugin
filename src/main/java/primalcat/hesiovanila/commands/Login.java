package primalcat.hesiovanila.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import primalcat.hesiovanila.manager.AccountManager;
import primalcat.hesiovanila.manager.AuthenticationManager;
import primalcat.hesiovanila.models.Account;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Optional;

public class Login implements CommandExecutor {
    private final AccountManager accountManager;
    private final AuthenticationManager authenticationManager;

    public Login(AccountManager accountManager, AuthenticationManager authenticationManager) {
        this.accountManager = accountManager;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be run by a player.");
            return true;
        }
//        Player player = (Player) sender;
//        if (args.length < 1) {
//            player.sendMessage(ChatColor.RED + "Usage: /login <password>");
//            return true;
//        }
        String username = sender.getName();
        String password = args[0];
        try {
            Optional<Account> optionalAccount = accountManager.loginUser(username, password);
            if (optionalAccount.isPresent()) {
                Player player = Bukkit.getPlayer(username);
                player.sendTitle("§l§aWelcome back!", "§qYou have logged in successfully.", 20, 10, 20);
//                sender.sendMessage(ChatColor.GREEN + "You have successfully logged in.");
                authenticationManager.addAuthenticatedPlayer(username);
                // Add any other logic you want to happen after a successful login
            } else {
                Player player = Bukkit.getPlayer(username);
                player.sendTitle("§l§cWrong password", "§qPlease try again", 20, 10, 20);
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "An error occurred while trying to log you in. Please try again later.");
        }
        return true;
    }
}