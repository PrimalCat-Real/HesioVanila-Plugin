package primalcat.hesiovanila;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import primalcat.hesiovanila.commands.Login;
import primalcat.hesiovanila.events.onPlayerJoin;

import java.sql.*;

public final class HesioVanila extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("login").setExecutor(new Login());
//        getServer().getPluginManager().registerEvent();
        getServer().getPluginManager().registerEvents(new onPlayerJoin(), this);

        PluginManager pm = getServer().getPluginManager();
        String c = "§9";
        sendMessage("");
        sendMessage(c + " __    __                      __         ");
        sendMessage(c + "/  |  /  |                    /  |        ");
        sendMessage(c + "$$ |  $$ |  ______    _______ $$/   ______ ");
        sendMessage(c + "$$ |__$$ | /      \\  /       |/  | /      \\");
        sendMessage(c + "$$    $$ |/$$$$$$  |/$$$$$$$/ $$ |/$$$$$$  | ");
        sendMessage(c + "$$$$$$$$ |$$    $$ |$$      \\ $$ |$$ |  $$ | ");
        sendMessage(c + "$$ |  $$ |$$$$$$$$/  $$$$$$  |$$ |$$ \\__$$ |");
        sendMessage(c + "$$ |  $$ |$$       |/     $$/ $$ |$$    $$/ ");
        sendMessage(c + "$$/   $$/  $$$$$$$/ $$$$$$$/  $$/  $$$$$$/ ");
        sendMessage("");

        try {
            this.setupDatabase();
        } catch (SQLException e) {
            System.out.println("cannot setup db");
            System.out.println(e.getSQLState());
        }
    }

    private boolean setupDatabase() throws SQLException {

        // Connect to the PostgreSQL database
        String url = "jdbc:postgresql://127.0.0.1:5432/hesiovanilla";
        String user = "primalcat";
        String password = "E5665e16";

        Connection connection = DriverManager.getConnection(url, user, password);
        System.out.println(connection);
        return false;
    }
//        String sql = "CREATE TABLE IF NOT EXISTS vanilla_accountants (name TEXT, realname TEXT, password TEXT, address TEXT, lastlogin INTEGER, regdate INTEGER, discord_account TEXT, jwt_token TEXT, hardware_id TEXT)";
//        // Use the connection to execute SQL queries
//        try (Statement statement = connection.createStatement()) {
//            statement.executeUpdate(sql);
//        }
//        return false;

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void sendMessage(String message) {
        getServer().getConsoleSender().sendMessage("[" + getName() + "] " + message);
    }

    public void sendMessage(String message, String color) {
        getServer().getConsoleSender().sendMessage(color + "[" + getName() + "] " + message);
    }
}
