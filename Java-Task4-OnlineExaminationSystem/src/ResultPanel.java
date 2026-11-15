import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Screen 4: score, time taken, and a per-question correct/incorrect breakdown. */
public class ResultPanel extends JPanel {

    public ResultPanel(ExamApp app, String studentName, ExamResult result) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        int total = result.total();
        int correct = result.correctCount();
        int percent = total == 0 ? 0 : Math.round(100f * correct / total);

        // ---- summary block
        JPanel summary = new JPanel(new GridLayout(0, 1, 0, 4));
        JLabel heading = new JLabel("Exam Result - " + studentName, SwingConstants.CENTER);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 22f));
        JLabel score = new JLabel("Score: " + correct + " out of " + total + "  (" + percent + "%)", SwingConstants.CENTER);
        score.setFont(score.getFont().deriveFont(Font.BOLD, 28f));
        score.setForeground(new Color(30, 90, 160));
        JLabel details = new JLabel("Time taken: " + ExamResult.formatTime(result.getSecondsTaken())
                + "     Correct: " + correct
                + "     Incorrect: " + result.incorrectCount()
                + "     Unanswered: " + result.unansweredCount(), SwingConstants.CENTER);
        details.setFont(details.getFont().deriveFont(15f));
        summary.add(heading);
        summary.add(score);
        summary.add(details);
        if (result.isAutoSubmitted()) {
            JLabel auto = new JLabel("(Submitted automatically when the timer reached zero)", SwingConstants.CENTER);
            auto.setForeground(new Color(190, 30, 30));
            summary.add(auto);
        }
        add(summary, BorderLayout.NORTH);

        // ---- breakdown table
        String[] columns = {"#", "Question", "Your answer", "Correct answer", "Result"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        for (int i = 0; i < total; i++) {
            Question q = result.getQuestions().get(i);
            String yours = result.isAnswered(i) ? q.optionLabel(result.getChosen(i)) : "-";
            String status = !result.isAnswered(i) ? "Unanswered" : (result.isCorrect(i) ? "Correct" : "Wrong");
            model.addRow(new Object[]{i + 1, q.getText(), yours, q.optionLabel(q.getCorrectIndex()), status});
        }
        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(430);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                String s = String.valueOf(value);
                c.setForeground(s.equals("Correct") ? new Color(20, 130, 50)
                        : s.equals("Wrong") ? new Color(190, 30, 30) : Color.GRAY);
                setFont(getFont().deriveFont(Font.BOLD));
                return c;
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ---- logout
        JButton logout = new JButton("Logout");
        logout.setFont(logout.getFont().deriveFont(Font.BOLD, 15f));
        logout.addActionListener(e -> app.logout());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.add(logout);
        add(bottom, BorderLayout.SOUTH);
    }
}
