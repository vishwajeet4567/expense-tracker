/**
 * Plain data class representing one row of the ACCOUNT table.
 * This is the "model" in our layered design — it just holds data,
 * with no SQL or business logic inside it.
 */
public class AccountSummary {
    private String name;
    private double debit;
    private double credit;
    private double total;
    private double accountBalance;

    public AccountSummary(String name, double debit, double credit, double total, double accountBalance) {
        this.name = name;
        this.debit = debit;
        this.credit = credit;
        this.total = total;
        this.accountBalance = accountBalance;
    }

    public String getName() { return name; }
    public double getDebit() { return debit; }
    public double getCredit() { return credit; }
    public double getTotal() { return total; }
    public double getAccountBalance() { return accountBalance; }
}
