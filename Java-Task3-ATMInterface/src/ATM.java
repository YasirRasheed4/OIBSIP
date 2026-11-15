import java.util.List;
import java.util.Scanner;

/**
 * The user-facing console interface: login, menu and each menu action.
 */
public class ATM {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    private final Bank bank;
    private final Scanner scanner;

    public ATM(Bank bank, Scanner scanner) {
        this.bank = bank;
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("=====================================");
        System.out.println("        WELCOME TO OIB ATM");
        System.out.println("=====================================");

        Account account = login();
        if (account == null) {
            System.out.println("\nToo many incorrect attempts. Access denied. Card blocked.");
            return;
        }

        System.out.println("\nLogin successful. Welcome, " + account.getHolderName() + "!");
        runMenu(account);
    }

    /** Gives the user 3 tries; returns null when they are all used up. */
    private Account login() {
        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS; attempt++) {
            System.out.print("\nEnter User ID: ");
            String userId = scanner.nextLine().trim();
            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine().trim();

            Account account = bank.authenticate(userId, pin);
            if (account != null) {
                return account;
            }
            int left = MAX_LOGIN_ATTEMPTS - attempt;
            if (left > 0) {
                System.out.println("Invalid User ID or PIN. " + left + " attempt(s) remaining.");
            }
        }
        return null;
    }

    private void runMenu(Account account) {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": showHistory(account); break;
                case "2": withdraw(account); break;
                case "3": deposit(account); break;
                case "4": transfer(account); break;
                case "5":
                    System.out.println("\nThank you for using OIB ATM. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 5.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n-------------- MAIN MENU --------------");
        System.out.println("1. Transaction History");
        System.out.println("2. Withdraw");
        System.out.println("3. Deposit");
        System.out.println("4. Transfer");
        System.out.println("5. Quit");
        System.out.print("Choose an option: ");
    }

    private void showHistory(Account account) {
        List<Transaction> history = account.getHistory();
        System.out.println("\n----------- TRANSACTION HISTORY -----------");
        if (history.isEmpty()) {
            System.out.println("No transactions in this session yet.");
        } else {
            int n = 1;
            for (Transaction t : history) {
                System.out.println(n++ + ". " + t);
            }
        }
        System.out.printf("Current balance: %.2f%n", account.getBalance());
    }

    private void withdraw(Account account) {
        double amount = readAmount("Enter amount to withdraw: ");
        if (amount < 0) return;

        if (!account.hasSufficientFunds(amount)) {
            System.out.println("Insufficient Funds. Your balance is " + String.format("%.2f", account.getBalance()));
            return;
        }
        account.withdraw(amount);
        System.out.printf("Please collect your cash: %.2f%nNew balance: %.2f%n", amount, account.getBalance());
    }

    private void deposit(Account account) {
        double amount = readAmount("Enter amount to deposit: ");
        if (amount < 0) return;

        account.deposit(amount);
        System.out.printf("Deposited %.2f successfully.%nNew balance: %.2f%n", amount, account.getBalance());
    }

    private void transfer(Account account) {
        System.out.print("Enter recipient Account ID: ");
        String recipientId = scanner.nextLine().trim();
        double amount = readAmount("Enter amount to transfer: ");
        if (amount < 0) return;

        Bank.TransferResult result = bank.transfer(account, recipientId, amount);
        switch (result) {
            case SUCCESS:
                System.out.printf("Transferred %.2f to %s.%nNew balance: %.2f%n",
                        amount, recipientId, account.getBalance());
                break;
            case INSUFFICIENT_FUNDS:
                System.out.println("Insufficient Funds. Your balance is " + String.format("%.2f", account.getBalance()));
                break;
            case RECIPIENT_NOT_FOUND:
                System.out.println("Recipient account '" + recipientId + "' does not exist.");
                break;
            case SAME_ACCOUNT:
                System.out.println("You cannot transfer money to your own account.");
                break;
        }
    }

    /** Reads a positive amount. Returns -1 if the input is invalid (an error is printed). */
    private double readAmount(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            double amount = Double.parseDouble(input);
            if (Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0) {
                System.out.println("Amount must be a number greater than zero.");
                return -1;
            }
            return amount;
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter digits only (e.g. 500 or 250.75).");
            return -1;
        }
    }
}
