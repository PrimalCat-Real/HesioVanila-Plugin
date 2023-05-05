package primalcat.hesiovanila.manager;

import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;


@RequiredArgsConstructor
public class AuthenticationManager {
    private final Set<String> authenticatedPlayers = new HashSet<>();

//    public boolean authenticatePlayer(String playerName, String password) {
//        // Authenticate player here
//
//        if (authenticated) {
//            authenticatedPlayers.add(playerName);
//            return true;
//        }
//
//        return false;
//    }

    public void addAuthenticatedPlayer(String playerName){
        this.authenticatedPlayers.add(playerName);
    }

    public boolean isPlayerAuthenticated(String playerName) {
        return authenticatedPlayers.contains(playerName);
    }

    public void deauthenticatePlayer(String playerName) {
        authenticatedPlayers.remove(playerName);
    }
}
