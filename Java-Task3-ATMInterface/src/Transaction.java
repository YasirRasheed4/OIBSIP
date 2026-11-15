import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Immutable record of a single banking operation.
 * Stored in an ArrayList inside each Account.
 */
public class Transaction {

    public enum Type { DEPOSIT, WITHDRAW, TRANSFER_OUT, TRANSFER_IN }

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Type type;
    private final double amount;
    private final double balanceAfter;
    private final String details;
    private final LocalDateTime timestamp;

    public Transaction(Type type, double amount, double balanceAfter, String details) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    public Type getType() { return type; }
    public double getAmount() { return amount; }
    public double getBalanceAfter() { return balanceAfter; }
    public String getDetails() { return details; }

    @Override
    public String toString() {
        return String.format("%s | %-12s | %10.2f | Balance: %10.2f | %s",
                timestamp.format(FORMAT), type, amount, balanceAfter, details);
    }
}
