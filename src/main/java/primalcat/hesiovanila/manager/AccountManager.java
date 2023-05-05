package primalcat.hesiovanila.manager;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import primalcat.hesiovanila.HesioVanila;
import primalcat.hesiovanila.models.Account;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@RequiredArgsConstructor
public class AccountManager {
    private final Connection database;

    /**
     * Searches for a player by name.
     *
     * @param playerName the name of the player to search for
     * @return true if the player exists, false otherwise
     * @throws SQLException if an SQL exception occurs
     */
    public boolean searchPlayerByName(@NonNull String playerName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM vanilla_accountants WHERE name = ?";
        try (PreparedStatement statement = database.prepareStatement(sql)) {
            statement.setString(1, playerName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    /**
     * Registers a new user with the specified name and password.
     *
     * @param playerName the name of the player to register
     * @param password the password for the player account
     * @return true if the registration was successful, false otherwise
     * @throws SQLException if an SQL exception occurs
     */
    public boolean registerUser(@NonNull String playerName, @NonNull String password) throws SQLException, NoSuchAlgorithmException, NoSuchAlgorithmException {
        // Check if player already exists
        if (searchPlayerByName(playerName)) {
            return false;
        }
        // Hash the password using SHA-256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        String hashedPassword = hexString.toString();
        // Add the player to the database
        String sql = "INSERT INTO vanilla_accountants (name, password) VALUES (?, ?)";
        try (PreparedStatement statement = database.prepareStatement(sql)) {
            statement.setString(1, playerName);
            statement.setString(2, hashedPassword);
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a user with the specified name.
     *
     * @param playerName the name of the player to delete
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException if an SQL exception occurs
     */
    public boolean deleteAccountByName(@NonNull String playerName) throws SQLException {
        String sql = "DELETE FROM vanilla_accountants WHERE name = ?";
        try (PreparedStatement statement = database.prepareStatement(sql)) {
            statement.setString(1, playerName);
            return statement.executeUpdate() > 0;
        }
    }


    /**
     * Logs in a user with the specified name and password.
     *
     * @param playerName the name of the player to log in
     * @param password the password for the player account
     * @return an Optional containing the Account for the player if the login was successful, or an empty Optional otherwise
     * @throws SQLException if an SQL exception occurs
     */
    public Optional<Account> loginUser(@NonNull String playerName, @NonNull String password) throws SQLException, NoSuchAlgorithmException {
        // Search for the player by name
        String sql = "SELECT password FROM vanilla_accountants WHERE name = ?";
        try (PreparedStatement statement = database.prepareStatement(sql)) {
            statement.setString(1, playerName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    // Player not found
                    return Optional.empty();
                }
                // Verify the password
                String hashedPassword = resultSet.getString("password");
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(password.getBytes());
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex);
                }
                String inputHashedPassword = hexString.toString();
                if (!inputHashedPassword.equals(hashedPassword)) {
                    // Passwords don't match
                    return Optional.empty();
                }
                // Return the Account for the player
                return Optional.of(new Account(playerName, hashedPassword));
            }
        }
    }
}
