import java.sql.*;

/**
 * Data Access Object (DAO) for the Smart Expense Tracker.
 *
 * This is the ONLY class in the app allowed to write SQL or talk to JDBC
 * directly. Every other class (the GUI) calls methods on this class instead
 * of writing its own queries.
 *
 * Why this matters (Single Responsibility Principle):
 * - The GUI's job is to display things and react to clicks.
 * - This class's job is to read/write the database.
 * If we ever swapped MySQL for PostgreSQL, only this file would change —
 * the GUI code wouldn't need to know or care.
 *
 * Every query below uses PreparedStatement with "?" placeholders instead of
 * building SQL strings by concatenating user input. This is the fix for the
 * SQL injection risk that existed in the original version: with a
 * PreparedStatement, a value like O'Brien or a malicious "'; DROP TABLE..."
 * is always treated as plain data, never as part of the SQL command itself.
 */
public class ExpenseDAO {

    private final Connection connection;

    public ExpenseDAO(Connection connection) {
        this.connection = connection;
    }

    /** Creates all five tables if they don't already exist, and seeds the one account row. */
    public void initializeSchema() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS ACCOUNT(" +
                    "NAME VARCHAR(20) PRIMARY KEY, DEBIT NUMERIC(65,3), CREDIT NUMERIC(65,3), " +
                    "TOTAL NUMERIC(65,3), ACCOUNT_BALANCE NUMERIC(65,3))");

            stmt.execute("CREATE TABLE IF NOT EXISTS DEBIT(" +
                    "AMOUNT NUMERIC(65,3), DATE_OF_TRANSACTION DATE, CATEGORY VARCHAR(25), NOTE VARCHAR(200))");

            stmt.execute("CREATE TABLE IF NOT EXISTS CREDIT(" +
                    "AMOUNT NUMERIC(65,3), DATE_OF_TRANSACTION DATE, CATEGORY VARCHAR(25), NOTE VARCHAR(200))");

            stmt.execute("CREATE TABLE IF NOT EXISTS TRANSFER(" +
                    "AMOUNT NUMERIC(65,3), DATE_OF_TRANSACTION DATE, CATEGORY VARCHAR(25), NOTE VARCHAR(200))");

            stmt.execute("CREATE TABLE IF NOT EXISTS STATEMENTS(" +
                    "DATE_OF_TRANSACTION DATE, AMOUNT NUMERIC(65,3), TYPE_OF_TRANSACTION VARCHAR(11), " +
                    "CATEGORY VARCHAR(25), NOTE VARCHAR(200))");
        }

        // Only seed the account row the very first time the app runs.
        if (getAccount("vishwajeet") == null) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO ACCOUNT VALUES (?, 0, 0, 0, 0)")) {
                ps.setString(1, "vishwajeet");
                ps.executeUpdate();
            }
        }
    }

    /** Fetches the single account row by name. Returns null if it doesn't exist yet. */
    public AccountSummary getAccount(String name) throws SQLException {
        String sql = "SELECT * FROM ACCOUNT WHERE NAME = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new AccountSummary(
                        rs.getString("NAME"),
                        rs.getDouble("DEBIT"),
                        rs.getDouble("CREDIT"),
                        rs.getDouble("TOTAL"),
                        rs.getDouble("ACCOUNT_BALANCE")
                );
            }
        }
    }

    /** Updates the account's running totals after a transaction. */
    private void updateAccountTotals(String name, double debit, double credit,
                                      double total, double accountBalance) throws SQLException {
        String sql = "UPDATE ACCOUNT SET DEBIT = ?, CREDIT = ?, TOTAL = ?, ACCOUNT_BALANCE = ? WHERE NAME = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, debit);
            ps.setDouble(2, credit);
            ps.setDouble(3, total);
            ps.setDouble(4, accountBalance);
            ps.setString(5, name);
            ps.executeUpdate();
        }
    }

    /**
     * Records an expense: inserts into DEBIT + STATEMENTS, and decreases the
     * account balance and total accordingly.
     */
    public void recordDebit(String accountName, double amount, String date, String category, String note)
            throws SQLException {
        AccountSummary account = getAccount(accountName);
        double newDebit = account.getDebit() - amount;
        double newTotal = account.getTotal() - amount;
        double newBalance = account.getAccountBalance() - amount;

        try (PreparedStatement insertDebit = connection.prepareStatement(
                "INSERT INTO DEBIT VALUES (?, ?, ?, ?)");
             PreparedStatement insertStatement = connection.prepareStatement(
                "INSERT INTO STATEMENTS VALUES (?, ?, 'Debit', ?, ?)")) {

            insertDebit.setDouble(1, amount);
            insertDebit.setString(2, date);
            insertDebit.setString(3, category);
            insertDebit.setString(4, note);
            insertDebit.executeUpdate();

            insertStatement.setString(1, date);
            insertStatement.setDouble(2, amount);
            insertStatement.setString(3, category);
            insertStatement.setString(4, note);
            insertStatement.executeUpdate();
                
            
        }

        updateAccountTotals(accountName, newDebit, account.getCredit(), newTotal, newBalance);
    }

    /**
     * Records income: inserts into CREDIT + STATEMENTS, and increases the
     * account balance and total accordingly.
     */
    public void recordCredit(String accountName, double amount, String date, String category, String note)
            throws SQLException {
        AccountSummary account = getAccount(accountName);
        double newCredit = account.getCredit() + amount;
        double newTotal = account.getTotal() + amount;
        double newBalance = account.getAccountBalance() + amount;

        try (PreparedStatement insertCredit = connection.prepareStatement(
                "INSERT INTO CREDIT VALUES (?, ?, ?, ?)");
             PreparedStatement insertStatement = connection.prepareStatement(
                "INSERT INTO STATEMENTS VALUES (?, ?, 'Credit', ?, ?)")) {

            insertCredit.setDouble(1, amount);
            insertCredit.setString(2, date);
            insertCredit.setString(3, category);
            insertCredit.setString(4, note);
            insertCredit.executeUpdate();

            insertStatement.setString(1, date);
            insertStatement.setDouble(2, amount);
            insertStatement.setString(3, category);
            insertStatement.setString(4, note);
            insertStatement.executeUpdate();
        }

        updateAccountTotals(accountName, account.getDebit(), newCredit, newTotal, newBalance);
    }

    /**
     * Records a transfer: inserts into TRANSFER + STATEMENTS only.
     * Transfers don't change the account balance (money is just moving
     * between places the user tracks, not entering/leaving overall).
     */
    public void recordTransfer(double amount, String date, String category, String note) throws SQLException {
        try (PreparedStatement insertTransfer = connection.prepareStatement(
                "INSERT INTO TRANSFER VALUES (?, ?, ?, ?)");
             PreparedStatement insertStatement = connection.prepareStatement(
                "INSERT INTO STATEMENTS VALUES (?, ?, 'Transfer', ?, ?)")) {

            insertTransfer.setDouble(1, amount);
            insertTransfer.setString(2, date);
            insertTransfer.setString(3, category);
            insertTransfer.setString(4, note);
            insertTransfer.executeUpdate();

            insertStatement.setString(1, date);
            insertStatement.setDouble(2, amount);
            insertStatement.setString(3, category);
            insertStatement.setString(4, note);
            insertStatement.executeUpdate();
        }
    }

    /**
     * Wipes every table and re-seeds a fresh zero-balance account.
     * Used by the "Reset All Data" button.
     */
    
    public void resetAllData(String accountName) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("TRUNCATE TABLE ACCOUNT");
            stmt.execute("TRUNCATE TABLE CREDIT");
            stmt.execute("TRUNCATE TABLE DEBIT");
            stmt.execute("TRUNCATE TABLE TRANSFER");
            stmt.execute("TRUNCATE TABLE STATEMENTS");
        }
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO ACCOUNT VALUES (?, 0, 0, 0, 0)")) {
            ps.setString(1, accountName);
            ps.executeUpdate();
        }
    }
                }


