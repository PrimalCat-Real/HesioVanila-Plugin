package primalcat.hesiovanila;

import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import primalcat.hesiovanila.commands.DeleteAccount;
import primalcat.hesiovanila.commands.Login;
import primalcat.hesiovanila.commands.Register;
import primalcat.hesiovanila.commands.SearchPlayer;
import primalcat.hesiovanila.events.onPlayerJoin;
import primalcat.hesiovanila.manager.AccountManager;
import primalcat.hesiovanila.manager.AuthenticationManager;

import java.sql.*;

public final class HesioVanila extends JavaPlugin {

    public static Connection getConnection() {
        return connection;
    }

    public static Connection connection;

    public static AccountManager getAccountManager() {
        return accountManager;
    }

    private static AccountManager accountManager;

    private static AuthenticationManager authenticationManager;

    public static AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    @Override
    public void onEnable() {
        Server server = getServer();


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

        // setup database
        try {
            if (!this.setupDatabase()) {
                server.shutdown();
                return;
            }
            System.out.println("db setup success");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("cannot setup db");
            System.out.println(e.getMessage());
        }

        accountManager = new AccountManager(connection);
        authenticationManager = new AuthenticationManager();

        // Plugin startup login
        getCommand("login").setExecutor(new Login(accountManager, authenticationManager));
        getCommand("register").setExecutor(new Register());
        getCommand("searchPlayerInDB").setExecutor(new SearchPlayer(accountManager));
        getCommand("deletePlayerInDB").setExecutor(new DeleteAccount());
    }

    private boolean setupDatabase() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        // Connect to the PostgreSQL database
        String url = "jdbc:postgresql://127.0.0.1:5432/hesiovanilla";
        String user = "postgres";
        String password = "SimplePass123";

        connection = DriverManager.getConnection(url, user, password);
        System.out.println(connection);
        String sql = "CREATE TABLE IF NOT EXISTS vanilla_accountants (name TEXT, realname TEXT, password TEXT, address TEXT, lastlogin INTEGER, regdate INTEGER, discord_account TEXT, jwt_token TEXT, hardware_id TEXT)";
//        // Use the connection to execute SQL queries
        try(Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

//        return false;

    @Override
    public void onDisable() {
    }

    public void sendMessage(String message) {
        getServer().getConsoleSender().sendMessage("[" + getName() + "] " + message);
    }

    public void sendMessage(String message, String color) {
        getServer().getConsoleSender().sendMessage(color + "[" + getName() + "] " + message);
    }
}
