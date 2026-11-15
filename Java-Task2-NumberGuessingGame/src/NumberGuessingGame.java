import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Console number guessing game.
 *
 * Features: random number each round, Too High / Too Low / Correct hints,
 * visible attempt counter, attempt limit, play-again prompt,
 * round-by-round score summary and three difficulty levels (bonus).
 */
public class NumberGuessingGame {

    /** Settings for one difficulty level. */
    private enum Difficulty {
        EASY("Easy", 50, 10),
        MEDIUM("Medium", 100, 7),
        HARD("Hard", 200, 5);

        final String label;
        final int max;
        final int maxAttempts;

        Difficulty(String label, int max, int maxAttempts) {
            this.label = label;
            this.max = max;
            this.maxAttempts = maxAttempts;
        }
    }

    /** Result of one finished round, kept for the final summary. */
    private static class RoundResult {
        final int roundNumber;
        final Difficulty difficulty;
        final boolean won;
        final int attemptsUsed;
        final int secret;

        RoundResult(int roundNumber, Difficulty difficulty, boolean won, int attemptsUsed, int secret) {
            this.roundNumber = roundNumber;
            this.difficulty = difficulty;
            this.won = won;
            this.attemptsUsed = attemptsUsed;
            this.secret = secret;
        }

        @Override
        public String toString() {
            if (won) {
                return "Round " + roundNumber + " [" + difficulty.label + "] - guessed in " + attemptsUsed + " attempts";
            }
            return "Round " + roundNumber + " [" + difficulty.label + "] - lost (number was " + secret + ")";
        }
    }

    private final Scanner scanner;
    private final Random random = new Random();
    private final ArrayList<RoundResult> results = new ArrayList<>();

    public NumberGuessingGame(Scanner scanner) {
        this.scanner = scanner;
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            new NumberGuessingGame(scanner).run();
        }
    }

    public void run() {
        System.out.println("=====================================");
        System.out.println("      NUMBER GUESSING GAME");
        System.out.println("=====================================");

        boolean playAgain = true;
        int round = 1;
        while (playAgain) {
            Difficulty difficulty = chooseDifficulty();
            if (difficulty == null) break; // input ended
            playRound(round, difficulty);
            round++;
            playAgain = askPlayAgain();
        }
        printSummary();
    }

    private Difficulty chooseDifficulty() {
        while (true) {
            System.out.println("\nChoose difficulty:");
            for (int i = 0; i < Difficulty.values().length; i++) {
                Difficulty d = Difficulty.values()[i];
                System.out.printf("  %d. %-6s (1-%d, %d attempts)%n", i + 1, d.label, d.max, d.maxAttempts);
            }
            System.out.print("Your choice [2]: ");
            String line = readLine();
            if (line == null) return null;
            if (line.isEmpty()) return Difficulty.MEDIUM; // default

            switch (line) {
                case "1": return Difficulty.EASY;
                case "2": return Difficulty.MEDIUM;
                case "3": return Difficulty.HARD;
                default: System.out.println("Please enter 1, 2 or 3.");
            }
        }
    }

    private void playRound(int round, Difficulty difficulty) {
        int secret = random.nextInt(difficulty.max) + 1; // 1..max inclusive
        int attempts = 0;
        boolean won = false;

        System.out.println("\n--- Round " + round + " ---");
        System.out.println("I'm thinking of a number between 1 and " + difficulty.max
                + ". You have " + difficulty.maxAttempts + " attempts.");

        while (attempts < difficulty.maxAttempts) {
            System.out.printf("Attempt %d of %d - enter your guess: ", attempts + 1, difficulty.maxAttempts);
            String line = readLine();
            if (line == null) return; // input ended, abandon round

            int guess;
            try {
                guess = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
                continue; // invalid input does not use up an attempt
            }
            if (guess < 1 || guess > difficulty.max) {
                System.out.println("Your guess must be between 1 and " + difficulty.max + ".");
                continue;
            }

            attempts++;
            if (guess == secret) {
                System.out.println("Correct! You got it in " + attempts + " attempt" + (attempts == 1 ? "" : "s") + ".");
                won = true;
                break;
            } else if (guess > secret) {
                System.out.println("Too High!");
            } else {
                System.out.println("Too Low!");
            }
        }

        if (!won) {
            System.out.println("You Lost! The number was " + secret + ".");
        }
        RoundResult result = new RoundResult(round, difficulty, won, attempts, secret);
        results.add(result);
        System.out.println(result);
    }

    private boolean askPlayAgain() {
        while (true) {
            System.out.print("\nPlay again? (yes/no): ");
            String line = readLine();
            if (line == null) return false;
            line = line.toLowerCase();
            if (line.equals("yes") || line.equals("y")) return true;
            if (line.equals("no") || line.equals("n")) return false;
            System.out.println("Please type yes or no.");
        }
    }

    private void printSummary() {
        System.out.println("\n=========== GAME SUMMARY ===========");
        if (results.isEmpty()) {
            System.out.println("No rounds played.");
        } else {
            int wins = 0;
            for (RoundResult r : results) {
                System.out.println(r);
                if (r.won) wins++;
            }
            System.out.println("Rounds won: " + wins + " / " + results.size());
        }
        System.out.println("Thanks for playing!");
    }

    /** Returns the trimmed next line, or null if the input has ended. */
    private String readLine() {
        return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
    }
}
