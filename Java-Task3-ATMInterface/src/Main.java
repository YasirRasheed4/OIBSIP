import java.util.Scanner;

/**
 * Entry point. Creates a Bank with three demo accounts and starts the ATM.
 */
public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();
        bank.addAccount(new Account("user1", "Yasir Gill", "1234", 5000.00));
        bank.addAccount(new Account("user2", "Sara Ahmed", "4321", 2500.00));
        bank.addAccount(new Account("user3", "Usman Raza", "1111", 10000.00));

        try (Scanner scanner = new Scanner(System.in)) {
            new ATM(bank, scanner).start();
        }
    }
}
