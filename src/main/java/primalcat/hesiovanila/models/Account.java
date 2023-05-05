package primalcat.hesiovanila.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Account {

    private final String name;
    private final String hashedPassword;
    private boolean isLoggedIn = false;
    // @TODO make same password validation
    public boolean checkPassword(String password) {
        return true;
    }

    public void setLoggedIn(boolean b) {
        isLoggedIn = true;
    }
}
