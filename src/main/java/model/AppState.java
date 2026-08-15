package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistable application state. Users, albums, and photos live here so the
 * JavaFX {@link Photos} application itself is not serialized.
 */
public class AppState implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final List<User> users = new ArrayList<>();

    /**
     * Live list of users. Callers may mutate it; prefer {@link #addUser} /
     * {@link #removeUser} when validation is required.
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * @return the user with this username, or {@code null} if none
     */
    public User findUser(String username) {
        if (username == null) {
            return null;
        }
        for (User user : users) {
            if (username.equals(user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    /**
     * Adds a user. Rejects {@code null}, blank usernames, the reserved
     * {@code admin} name, and duplicates.
     *
     * @return {@code true} if the user was added
     */
    public boolean addUser(User user) {
        if (user == null) {
            return false;
        }
        String username = user.getUsername();
        if (username == null || username.isBlank() || "admin".equals(username) || findUser(username) != null) {
            return false;
        }
        users.add(user);
        return true;
    }

    /**
     * Removes the user with this username if present.
     *
     * @return {@code true} if a user was removed
     */
    public boolean removeUser(String username) {
        if (username == null) {
            return false;
        }
        return users.removeIf(user -> username.equals(user.getUsername()));
    }
}
