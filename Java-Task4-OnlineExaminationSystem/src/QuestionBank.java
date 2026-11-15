import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Holds the Java quiz questions. */
public class QuestionBank {

    private static List<Question> all() {
        List<Question> q = new ArrayList<>();
        q.add(new Question("Which keyword is used to inherit from a class in Java?",
                new String[]{"implements", "extends", "inherits", "super"}, 1));
        q.add(new Question("Which of the following is NOT a primitive data type in Java?",
                new String[]{"int", "boolean", "String", "char"}, 2));
        q.add(new Question("What is the default value of an int instance variable?",
                new String[]{"null", "1", "Undefined", "0"}, 3));
        q.add(new Question("Which collection does NOT allow duplicate elements?",
                new String[]{"ArrayList", "HashSet", "LinkedList", "Vector"}, 1));
        q.add(new Question("Which method is the entry point of a Java application?",
                new String[]{"start()", "run()", "main()", "init()"}, 2));
        q.add(new Question("What does JVM stand for?",
                new String[]{"Java Variable Machine", "Java Visual Model", "Joint Virtual Machine", "Java Virtual Machine"}, 3));
        q.add(new Question("Which keyword prevents a method from being overridden?",
                new String[]{"static", "abstract", "final", "volatile"}, 2));
        q.add(new Question("How many bits does an int occupy in Java?",
                new String[]{"8", "16", "32", "64"}, 2));
        q.add(new Question("Which exception is thrown when an integer is divided by zero?",
                new String[]{"NullPointerException", "ArithmeticException", "NumberFormatException", "ClassCastException"}, 1));
        q.add(new Question("Which interface can you implement to define the task of a new thread?",
                new String[]{"Runnable", "Serializable", "Comparable", "Cloneable"}, 0));
        return q;
    }

    /** A fresh copy of the questions in random order for each exam attempt. */
    public static List<Question> createExam() {
        List<Question> list = all();
        Collections.shuffle(list);
        return list;
    }
}
