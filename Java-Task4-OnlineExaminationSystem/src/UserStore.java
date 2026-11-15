import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * In-memory list of registered students (profile changes last until the app closes).
 * Demo accounts: student1 / pass123  and  student2 / pass456.
 */
public class UserStore {

    private final Map<String, User> users = new HashMap<>();

    public UserStore() {
        add(new User("student1", "Student One", hash("pass123")));
        add(new User("student2", "Student Two", hash("pass456")));
    }

    private void add(User user) {
        users.put(user.getUsername(), user);
    }

    /** @return the matching user, or null if username/password is wrong. */
    public User authenticate(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPasswordHash().equals(hash(password))) {
            return user;
        }
        return null;
    }

    /** Updates the display name and, if newPassword is non-empty, the password. */
    public void updateProfile(User user, String newDisplayName, String newPassword) {
        user.setDisplayName(newDisplayName);
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPasswordHash(hash(newPassword));
        }
    }

    static String hash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
