import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Screen 3: the exam itself. Shows one question at a time, a live countdown,
 * Next/Previous navigation, numbered question buttons and a Submit button.
 */
public class ExamPanel extends JPanel {

    private final ExamApp app;
    private final List<Question> questions;
    private final int[] answers;          // -1 = unanswered
    private final int totalSeconds;

    private int current = 0;
    private boolean submitted = false;
    private long startNanos;
    private JDialog confirmDialog;        // open "Submit?" dialog, so a timeout can close it

    private final javax.swing.Timer timer;
    private final JLabel timerLabel = new JLabel("00:00", SwingConstants.RIGHT);
    private final JLabel counterLabel = new JLabel();
    private final JTextArea questionText = new JTextArea();
    private final JRadioButton[] optionButtons = new JRadioButton[4];
    private final ButtonGroup group = new ButtonGroup();
    private final JButton prevButton = new JButton("◀ Previous");
    private final JButton nextButton = new JButton("Next ▶");
    private final JButton submitButton = new JButton("Submit Exam");
    private final List<NavButton> navButtons = new ArrayList<>();

    public ExamPanel(ExamApp app, String studentName, List<Question> questions, int totalSeconds) {
        this.app = app;
        this.questions = questions;
        this.answers = new int[questions.size()];
        java.util.Arrays.fill(answers, -1);
        this.totalSeconds = totalSeconds;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        // ---- header: title + counter on the left, timer on the right
        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Exam - " + studentName);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        counterLabel.setForeground(Color.DARK_GRAY);
        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.add(title);
        titleBox.add(counterLabel);
        header.add(titleBox, BorderLayout.WEST);
        timerLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 34));
        header.add(timerLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ---- centre: question text + four radio buttons
        JPanel centre = new JPanel(new BorderLayout());
        centre.setBorder(BorderFactory.createTitledBorder("Question"));

        questionText.setEditable(false);
        questionText.setLineWrap(true);
        questionText.setWrapStyleWord(true);
        questionText.setOpaque(false);
        questionText.setBackground(new Color(0, 0, 0, 0)); // Nimbus ignores setOpaque(false) alone
        questionText.setFont(questionText.getFont().deriveFont(Font.BOLD, 19f));
        questionText.setBorder(BorderFactory.createEmptyBorder(12, 12, 20, 12));
        centre.add(questionText, BorderLayout.NORTH);

        JPanel optionsBox = new JPanel();
        optionsBox.setLayout(new BoxLayout(optionsBox, BoxLayout.Y_AXIS));
        centre.add(optionsBox, BorderLayout.CENTER);

        for (int i = 0; i < 4; i++) {
            final int index = i;
            JRadioButton rb = new JRadioButton();
            rb.setFont(rb.getFont().deriveFont(16f));
            rb.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 8));
            rb.setAlignmentX(Component.LEFT_ALIGNMENT);
            rb.addActionListener(e -> {
                answers[current] = index;
                refreshNav();
            });
            group.add(rb);
            optionButtons[i] = rb;
            optionsBox.add(rb);
        }
        optionsBox.add(Box.createVerticalGlue());
        add(centre, BorderLayout.CENTER);

        // ---- bottom: numbered navigator + Previous / Next / Submit
        JPanel navigator = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        for (int i = 0; i < questions.size(); i++) {
            final int target = i;
            NavButton nb = new NavButton(String.valueOf(i + 1));
            nb.addActionListener(e -> showQuestion(target));
            navButtons.add(nb);
            navigator.add(nb);
        }

        JPanel buttons = new JPanel(new BorderLayout());
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.add(prevButton);
        left.add(nextButton);
        buttons.add(left, BorderLayout.WEST);
        buttons.add(submitButton, BorderLayout.EAST);

        JPanel bottom = new JPanel(new GridLayout(2, 1, 0, 12));
        bottom.add(navigator);
        bottom.add(buttons);
        add(bottom, BorderLayout.SOUTH);

        prevButton.addActionListener(e -> showQuestion(current - 1));
        nextButton.addActionListener(e -> showQuestion(current + 1));
        submitButton.addActionListener(e -> {
            if (!submitted && confirmSubmit()) {
                submit(false);
            }
        });

        // Fires 4x per second; remaining time is computed from the clock, so it never drifts.
        timer = new javax.swing.Timer(250, e -> tick());
    }

    /** Starts the countdown and shows the first question. */
    public void start() {
        startNanos = System.nanoTime();
        timer.start();
        tick();
        showQuestion(0);
    }

    /** Stops the countdown (used when the window is closed mid-exam). */
    public void stop() {
        timer.stop();
    }

    private void tick() {
        if (submitted) return;
        long elapsed = (System.nanoTime() - startNanos) / 1_000_000_000L;
        long remaining = Math.max(0, totalSeconds - elapsed);
        timerLabel.setText(ExamResult.formatTime(remaining));
        timerLabel.setForeground(remaining <= 60 ? new Color(200, 30, 30) : new Color(30, 90, 160));
        if (remaining == 0) {
            submit(true);
        }
    }

    private void showQuestion(int index) {
        if (index < 0 || index >= questions.size()) return;
        current = index;
        Question q = questions.get(index);

        counterLabel.setText("Question " + (index + 1) + " of " + questions.size());
        questionText.setText(q.getText());

        group.clearSelection();
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText((char) ('A' + i) + ".  " + q.getOption(i));
        }
        if (answers[index] >= 0) {
            optionButtons[answers[index]].setSelected(true);
        }

        prevButton.setEnabled(index > 0);
        nextButton.setEnabled(index < questions.size() - 1);
        refreshNav();
    }

    private void refreshNav() {
        for (int i = 0; i < navButtons.size(); i++) {
            navButtons.get(i).setState(i == current, answers[i] >= 0);
        }
    }

    private int answeredCount() {
        int n = 0;
        for (int a : answers) if (a >= 0) n++;
        return n;
    }

    /** "Are you sure?" dialog. Built by hand so a timeout can close it from the timer. */
    private boolean confirmSubmit() {
        int unanswered = answers.length - answeredCount();
        String msg = "You have answered " + answeredCount() + " of " + answers.length + " questions."
                + (unanswered > 0 ? "\n" + unanswered + " question(s) are still unanswered." : "")
                + "\n\nDo you want to submit your exam now?";
        JOptionPane pane = new JOptionPane(msg, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
        confirmDialog = pane.createDialog(app, "Submit Exam");
        confirmDialog.setVisible(true);   // blocks until answered or disposed
        confirmDialog = null;
        Object value = pane.getValue();
        return value instanceof Integer && (Integer) value == JOptionPane.YES_OPTION;
    }

    private void submit(boolean auto) {
        if (submitted) return;            // guards against double submission
        submitted = true;
        timer.stop();
        long taken = Math.min(totalSeconds, (System.nanoTime() - startNanos) / 1_000_000_000L);
        ExamResult result = new ExamResult(questions, answers.clone(), taken, auto);

        if (confirmDialog != null) {
            confirmDialog.dispose();      // time ran out while the confirm dialog was open
        }
        app.finishExam(result);
        if (auto) {
            JOptionPane.showMessageDialog(app, "Time is up! Your exam was submitted automatically.",
                    "Time's up", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /** Small round number button; paints itself so colours look the same in every look-and-feel. */
    private static class NavButton extends JButton {
        private boolean isCurrent;
        private boolean isAnswered;

        NavButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setBorder(BorderFactory.createEmptyBorder()); // no insets, so "10" is never truncated
            setMargin(new Insets(0, 0, 0, 0));
            setPreferredSize(new Dimension(44, 32));
        }

        void setState(boolean current, boolean answered) {
            this.isCurrent = current;
            this.isAnswered = answered;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color fill = isAnswered ? new Color(120, 200, 140) : new Color(225, 225, 225);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.setColor(isCurrent ? new Color(30, 90, 160) : new Color(170, 170, 170));
            g2.setStroke(new BasicStroke(isCurrent ? 3f : 1f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);
            g2.dispose();
            setForeground(Color.BLACK);
            super.paintComponent(g);
        }
    }
}
