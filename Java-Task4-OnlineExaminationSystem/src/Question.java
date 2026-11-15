/** One multiple-choice question with exactly four options. */
public class Question {

    private final String text;
    private final String[] options;
    private final int correctIndex;

    public Question(String text, String[] options, int correctIndex) {
        if (options.length != 4) {
            throw new IllegalArgumentException("A question needs exactly 4 options");
        }
        this.text = text;
        this.options = options.clone();
        this.correctIndex = correctIndex;
    }

    public String getText() { return text; }
    public String getOption(int index) { return options[index]; }
    public int getCorrectIndex() { return correctIndex; }

    /** "B. extends" style label used on the result screen. */
    public String optionLabel(int index) {
        return (char) ('A' + index) + ". " + options[index];
    }
}
