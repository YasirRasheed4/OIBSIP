import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main window. A CardLayout swaps between the four screens:
 * login -> profile -> exam -> result (and back to login on logout).
 *
 * Exam length defaults to 10 minutes; override with  -Dexam.seconds=30  to test auto-submit.
 */
public class ExamApp extends JFrame {

    private static final String LOGIN = "login";
    private static final String PROFILE = "profile";
    private static final String EXAM = "exam";
    private static final String RESULT = "result";

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final UserStore users = new UserStore();

    private final LoginPanel loginPanel;
    private final ProfilePanel profilePanel;
    private ExamPanel examPanel;
    private ResultPanel resultPanel;

    private User currentUser;
    private boolean examInProgress = false;

    public ExamApp() {
        super("Online Examination System");
        loginPanel = new LoginPanel(this, users);
        profilePanel = new ProfilePanel(this, users);
        root.add(loginPanel, LOGIN);
        root.add(profilePanel, PROFILE);
        setContentPane(root);

        // We handle the close button ourselves so we can ask for confirmation mid-exam.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleClose();
            }
        });

        setSize(1000, 660);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        cards.show(root, LOGIN);
    }

    // ---------- navigation between screens ----------

    public void onLoginSuccess(User user) {
        currentUser = user;
        profilePanel.load(user);
        cards.show(root, PROFILE);
    }

    public void startExam() {
        if (examPanel != null) {
            examPanel.stop();
            root.remove(examPanel);
        }
        int seconds = Integer.getInteger("exam.seconds", 10 * 60);
        examPanel = new ExamPanel(this, currentUser.getDisplayName(), QuestionBank.createExam(), seconds);
        root.add(examPanel, EXAM);
        examInProgress = true;
        cards.show(root, EXAM);
        examPanel.start();
    }

    public void finishExam(ExamResult result) {
        examInProgress = false;
        if (resultPanel != null) {
            root.remove(resultPanel);
        }
        resultPanel = new ResultPanel(this, currentUser.getDisplayName(), result);
        root.add(resultPanel, RESULT);
        cards.show(root, RESULT);
    }

    public void logout() {
        currentUser = null;
        examInProgress = false;
        loginPanel.reset();
        cards.show(root, LOGIN);
    }

    // ---------- session management ----------

    private void handleClose() {
        if (examInProgress) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to quit?\nYour exam progress will be lost.",
                    "Quit Exam", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
            if (examPanel != null) {
                examPanel.stop();
            }
        }
        dispose();
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored) {
                // fall back to the default look and feel
            }
            new ExamApp().setVisible(true);
        });
    }
}
