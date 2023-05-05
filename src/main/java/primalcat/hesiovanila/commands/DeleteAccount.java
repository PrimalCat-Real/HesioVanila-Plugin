package primalcat.hesiovanila.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import primalcat.hesiovanila.HesioVanila;
import primalcat.hesiovanila.manager.AccountManager;

import java.sql.SQLException;

public class DeleteAccount implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        String playerName = commandSender.getName();
        AccountManager accountManager = HesioVanila.getAccountManager();
        try {
            boolean result = accountManager.deleteAccountByName(playerName);
            if (result) {
                commandSender.sendMessage("Account deleted successfully.");
                return true;
            } else {
                commandSender.sendMessage("Account not found.");
            }
        } catch (SQLException e) {
            commandSender.sendMessage("An error occurred while deleting the account.");
            e.printStackTrace();
        }
        return false;
    }
}
