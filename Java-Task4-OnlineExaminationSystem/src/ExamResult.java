import java.util.List;

/** Everything the result screen needs: the questions, the chosen answers and the time used. */
public class ExamResult {

    private final List<Question> questions;
    private final int[] answers;        // chosen option per question, -1 = not answered
    private final long secondsTaken;
    private final boolean autoSubmitted;

    public ExamResult(List<Question> questions, int[] answers, long secondsTaken, boolean autoSubmitted) {
        this.questions = questions;
        this.answers = answers;
        this.secondsTaken = secondsTaken;
        this.autoSubmitted = autoSubmitted;
    }

    public List<Question> getQuestions() { return questions; }
    public int getChosen(int i) { return answers[i]; }
    public long getSecondsTaken() { return secondsTaken; }
    public boolean isAutoSubmitted() { return autoSubmitted; }
    public int total() { return questions.size(); }

    public boolean isAnswered(int i) { return answers[i] >= 0; }
    public boolean isCorrect(int i) { return answers[i] == questions.get(i).getCorrectIndex(); }

    public int correctCount() {
        int n = 0;
        for (int i = 0; i < total(); i++) if (isCorrect(i)) n++;
        return n;
    }

    public int unansweredCount() {
        int n = 0;
        for (int i = 0; i < total(); i++) if (!isAnswered(i)) n++;
        return n;
    }

    public int incorrectCount() {
        return total() - correctCount() - unansweredCount();
    }

    public static String formatTime(long seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }
}
