import javax.swing.*;
import java.awt.*;

/** Screen 1: username + password. */
public class LoginPanel extends JPanel {

    private final ExamApp app;
    private final UserStore users;

    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);
    private final JLabel errorLabel = new JLabel(" ");

    public LoginPanel(ExamApp app, UserStore users) {
        this.app = app;
        this.users = users;
        setLayout(new GridBagLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Online Examination System", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        form.add(title, c);

        JLabel sub = new JLabel("Please log in to continue", SwingConstants.CENTER);
        c.gridy = 1;
        form.add(sub, c);

        c.gridwidth = 1;
        c.gridy = 2; c.gridx = 0; form.add(new JLabel("Username:"), c);
        c.gridx = 1; form.add(usernameField, c);

        c.gridy = 3; c.gridx = 0; form.add(new JLabel("Password:"), c);
        c.gridx = 1; form.add(passwordField, c);

        errorLabel.setForeground(new Color(190, 30, 30));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridy = 4; c.gridx = 0; c.gridwidth = 2;
        form.add(errorLabel, c);

        JButton loginButton = new JButton("Login");
        c.gridy = 5;
        form.add(loginButton, c);

        JLabel hint = new JLabel("Demo accounts: student1 / pass123   |   student2 / pass456", SwingConstants.CENTER);
        hint.setForeground(Color.GRAY);
        c.gridy = 6;
        form.add(hint, c);

        add(form);

        loginButton.addActionListener(e -> attemptLogin());
        usernameField.addActionListener(e -> passwordField.requestFocusInWindow());
        passwordField.addActionListener(e -> attemptLogin());
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }
        User user = users.authenticate(username, password);
        if (user == null) {
            errorLabel.setText("Invalid username or password.");
            passwordField.setText("");
            return;
        }
        errorLabel.setText(" ");
        app.onLoginSuccess(user);
    }

    /** Clears the form (used after logout). */
    public void reset() {
        usernameField.setText("");
        passwordField.setText("");
        errorLabel.setText(" ");
        usernameField.requestFocusInWindow();
    }
}
