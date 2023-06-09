package primalcat.hesiovanila.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import primalcat.hesiovanila.HesioVanila;
import primalcat.hesiovanila.manager.AccountManager;
import primalcat.hesiovanila.manager.AuthenticationManager;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class Register implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length != 2) {
            commandSender.sendMessage("Invalid parameters. Usage: /register <password> <confirm_password>");
            return false;
        }

        String playerName = commandSender.getName();
        String password = strings[0];
        String confirmPassword = strings[1];

        // Check if password and confirm password match
        if (!password.equals(confirmPassword)) {
            commandSender.sendMessage("Passwords do not match.");
            return false;
        }

        // Check if password length is valid
        if (password.length() < 8 || password.length() > 16) {
            commandSender.sendMessage("Password must be between 8 and 16 characters long.");
            return false;
        }

        AccountManager accountManager = HesioVanila.getAccountManager();
        AuthenticationManager authenticationManager = HesioVanila.getAuthenticationManager();
        try {
            // Check if player already exists
            if (accountManager.searchPlayerByName(playerName)) {
                commandSender.sendMessage("Player already registered.");
                return false;
            }

            // Create new account
            accountManager.registerUser(playerName, password);
            Player player = Bukkit.getPlayer(playerName);
            player.sendTitle("§l§aSuccessfully registered.", "§qWelcome to the server!", 20, 10, 20);
//            commandSender.sendMessage("Account created successfully.");
            authenticationManager.addAuthenticatedPlayer(playerName);
            return true;
        } catch (SQLException e) {
            commandSender.sendMessage("An error occurred while registering the account.");
            e.printStackTrace();
            return false;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
