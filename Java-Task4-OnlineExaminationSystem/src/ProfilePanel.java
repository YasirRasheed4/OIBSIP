import javax.swing.*;
import java.awt.*;

/** Screen 2: change display name / password before starting the exam. */
public class ProfilePanel extends JPanel {

    private final ExamApp app;
    private final UserStore users;
    private User user;

    private final JLabel welcomeLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JTextField nameField = new JTextField(18);
    private final JPasswordField newPasswordField = new JPasswordField(18);
    private final JPasswordField confirmField = new JPasswordField(18);
    private final JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);

    public ProfilePanel(ExamApp app, UserStore users) {
        this.app = app;
        this.users = users;
        setLayout(new GridBagLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(Font.BOLD, 22f));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        form.add(welcomeLabel, c);

        JLabel info = new JLabel("<html><div style='text-align:center'>Update your profile if you wish, then start the exam.<br>"
                + "Leave the password fields empty to keep your current password.</div></html>", SwingConstants.CENTER);
        c.gridy = 1;
        form.add(info, c);

        c.gridwidth = 1;
        c.gridy = 2; c.gridx = 0; form.add(new JLabel("Display name:"), c);
        c.gridx = 1; form.add(nameField, c);

        c.gridy = 3; c.gridx = 0; form.add(new JLabel("New password:"), c);
        c.gridx = 1; form.add(newPasswordField, c);

        c.gridy = 4; c.gridx = 0; form.add(new JLabel("Confirm password:"), c);
        c.gridx = 1; form.add(confirmField, c);

        c.gridy = 5; c.gridx = 0; c.gridwidth = 2;
        form.add(statusLabel, c);

        JButton saveButton = new JButton("Save Changes");
        JButton startButton = new JButton("Start Exam");
        JButton logoutButton = new JButton("Logout");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttons.add(saveButton);
        buttons.add(startButton);
        buttons.add(logoutButton);
        c.gridy = 6;
        form.add(buttons, c);

        add(form);

        saveButton.addActionListener(e -> saveProfile());
        startButton.addActionListener(e -> app.startExam());
        logoutButton.addActionListener(e -> app.logout());
    }

    /** Fills the form for the user who just logged in. */
    public void load(User user) {
        this.user = user;
        welcomeLabel.setText("Welcome, " + user.getDisplayName());
        nameField.setText(user.getDisplayName());
        newPasswordField.setText("");
        confirmField.setText("");
        setStatus(" ", false);
    }

    private void saveProfile() {
        String name = nameField.getText().trim();
        String pass = new String(newPasswordField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (name.isEmpty()) {
            setStatus("Display name cannot be empty.", true);
            return;
        }
        if (!pass.isEmpty()) {
            if (pass.length() < 4) {
                setStatus("Password must be at least 4 characters.", true);
                return;
            }
            if (!pass.equals(confirm)) {
                setStatus("Passwords do not match.", true);
                return;
            }
        }
        users.updateProfile(user, name, pass);
        welcomeLabel.setText("Welcome, " + user.getDisplayName());
        newPasswordField.setText("");
        confirmField.setText("");
        setStatus("Profile updated successfully.", false);
    }

    private void setStatus(String message, boolean error) {
        statusLabel.setForeground(error ? new Color(190, 30, 30) : new Color(20, 130, 50));
        statusLabel.setText(message);
    }
}
