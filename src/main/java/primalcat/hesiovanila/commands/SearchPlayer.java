package primalcat.hesiovanila.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import primalcat.hesiovanila.manager.AccountManager;

public class SearchPlayer implements CommandExecutor {
    private final AccountManager accountManager;

    public SearchPlayer(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("Usage: /searchplayer <playername>");
            return true;
        }

        String playerName = args[0];
        boolean playerExists;

        try {
            playerExists = accountManager.searchPlayerByName(playerName);
        } catch (Exception e) {
            sender.sendMessage("An error occurred while searching for the player: " + e.getMessage());
            return true;
        }

        if (playerExists) {
            sender.sendMessage("Player '" + playerName + "' was found in the database.");
        } else {
            sender.sendMessage("Player '" + playerName + "' was not found in the database.");
        }

        return true;
    }
}