import java.util.HashMap;
import java.util.Map;

/**
 * Holds every account and performs operations that involve more than one account.
 */
public class Bank {

    public enum TransferResult { SUCCESS, INSUFFICIENT_FUNDS, RECIPIENT_NOT_FOUND, SAME_ACCOUNT }

    private final Map<String, Account> accounts = new HashMap<>();

    public void addAccount(Account account) {
        accounts.put(account.getUserId(), account);
    }

    public Account findAccount(String userId) {
        return accounts.get(userId);
    }

    /** @return the account if the ID/PIN pair is valid, otherwise null. */
    public Account authenticate(String userId, String pin) {
        Account account = accounts.get(userId);
        if (account != null && account.checkPin(pin)) {
            return account;
        }
        return null;
    }

    public TransferResult transfer(Account from, String toUserId, double amount) {
        if (from.getUserId().equals(toUserId)) {
            return TransferResult.SAME_ACCOUNT;
        }
        Account to = accounts.get(toUserId);
        if (to == null) {
            return TransferResult.RECIPIENT_NOT_FOUND;
        }
        if (!from.debitForTransfer(amount, toUserId)) {
            return TransferResult.INSUFFICIENT_FUNDS;
        }
        to.creditFromTransfer(amount, from.getUserId());
        return TransferResult.SUCCESS;
    }
}
