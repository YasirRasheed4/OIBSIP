import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A bank account. All fields are private (encapsulation); the only way to change
 * the balance is through deposit/withdraw, which also log a Transaction.
 */
public class Account {

    private final String userId;
    private final String holderName;
    private final String pin;
    private double balance;
    private final ArrayList<Transaction> history = new ArrayList<>();

    public Account(String userId, String holderName, String pin, double openingBalance) {
        this.userId = userId;
        this.holderName = holderName;
        this.pin = pin;
        this.balance = openingBalance;
    }

    public String getUserId() { return userId; }
    public String getHolderName() { return holderName; }
    public double getBalance() { return balance; }

    public boolean checkPin(String candidate) {
        return pin.equals(candidate);
    }

    /** Read-only view so callers cannot tamper with the log. */
    public List<Transaction> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public boolean hasSufficientFunds(double amount) {
        return amount <= balance;
    }

    public void deposit(double amount) {
        balance += amount;
        history.add(new Transaction(Transaction.Type.DEPOSIT, amount, balance, "Cash deposit"));
    }

    /** @return false (and changes nothing) if funds are insufficient. */
    public boolean withdraw(double amount) {
        if (!hasSufficientFunds(amount)) {
            return false;
        }
        balance -= amount;
        history.add(new Transaction(Transaction.Type.WITHDRAW, amount, balance, "Cash withdrawal"));
        return true;
    }

    /** Debit side of a transfer. Called by Bank.transfer(). */
    boolean debitForTransfer(double amount, String toAccountId) {
        if (!hasSufficientFunds(amount)) {
            return false;
        }
        balance -= amount;
        history.add(new Transaction(Transaction.Type.TRANSFER_OUT, amount, balance, "Transfer to " + toAccountId));
        return true;
    }

    /** Credit side of a transfer. Called by Bank.transfer(). */
    void creditFromTransfer(double amount, String fromAccountId) {
        balance += amount;
        history.add(new Transaction(Transaction.Type.TRANSFER_IN, amount, balance, "Transfer from " + fromAccountId));
    }
}
