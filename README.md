<<<<<<< HEAD
# expense-tracker
# Smart Expense Tracker

A desktop personal finance app built with Java Swing and MySQL. Tracks income, expenses, and transfers across 9 categories, with real-time balance and statistics.

## Tech Stack
- Java (Swing for GUI)
- JDBC
- MySQL

## Architecture

```
MoneyManager.java   →   ExpenseDAO.java   →   MySQL
   (UI layer)          (data access layer)
```

- **MoneyManager** — builds the Swing UI and wires up button click handlers. Contains no SQL.
- **ExpenseDAO** — the only class that talks to JDBC. Every query uses `PreparedStatement` with bound parameters.
- **AccountSummary** — a small model class representing one row of the `ACCOUNT` table.

This is a refactor of an earlier single-file version. Splitting into a DAO layer follows the Single Responsibility Principle: the UI doesn't need to know how data is stored, and the DAO doesn't need to know anything about Swing.

## What changed in this version
- **Extracted a DAO layer** — `ExpenseDAO.java` now owns all database access; the GUI calls its methods instead of writing SQL inline
- **Switched to `PreparedStatement`** everywhere — removes the SQL injection risk that came from concatenating user input directly into SQL strings
- **Fixed an account-name mismatch bug** — the account row was originally seeded as `'vishwajeet'` but updates referenced `'Kshiti'`, so balance updates were silently failing. Now a single `ACCOUNT_NAME` constant is used consistently.
- **Added a model class** (`AccountSummary`) instead of passing raw `ResultSet` data around

## Database Schema
```sql
CREATE TABLE ACCOUNT (
    NAME VARCHAR(20) PRIMARY KEY,
    DEBIT NUMERIC(65,3),
    CREDIT NUMERIC(65,3),
    TOTAL NUMERIC(65,3),
    ACCOUNT_BALANCE NUMERIC(65,3)
);

CREATE TABLE DEBIT (
    AMOUNT NUMERIC(65,3),
    DATE_OF_TRANSACTION DATE,
    CATEGORY VARCHAR(25),
    NOTE VARCHAR(200)
);

CREATE TABLE CREDIT (
    AMOUNT NUMERIC(65,3),
    DATE_OF_TRANSACTION DATE,
    CATEGORY VARCHAR(25),
    NOTE VARCHAR(200)
);

CREATE TABLE TRANSFER (
    AMOUNT NUMERIC(65,3),
    DATE_OF_TRANSACTION DATE,
    CATEGORY VARCHAR(25),
    NOTE VARCHAR(200)
);

CREATE TABLE STATEMENTS (
    DATE_OF_TRANSACTION DATE,
    AMOUNT NUMERIC(65,3),
    TYPE_OF_TRANSACTION VARCHAR(11),
    CATEGORY VARCHAR(25),
    NOTE VARCHAR(200)
);
```
Tables and the initial account row are created automatically the first time the app runs (`ExpenseDAO.initializeSchema()`).

## Categories
Food, Self Development, Transportation, Beauty, Household, Health, Apparel, Education, Gift

## Setup

### 1. Prerequisites
- JDK 8+
- MySQL Server 5.7+
- MySQL Connector/J (included as `mysql-connector-j-9.5.0.jar`)

### 2. Create the database
```sql
CREATE DATABASE expensemanagerjava;
```

### 3. Configure credentials
Open `MoneyManager.java` and update the connection details inside `createAndShowGUI()`:
```java
String url = "jdbc:mysql://localhost:3306/expensemanagerjava";
String username = "root";
String password = "YOUR_MYSQL_PASSWORD";
```

### 4. Compile and run
=======
# Smart Expense Tracker (Refactored — DAO Layer)

## What changed from the original
1. **DAO layer added** — `ExpenseDAO.java` is now the only class that talks to JDBC/SQL.
2. **PreparedStatement everywhere** — every value (amount, date, category, note) is bound
   via `?` placeholders instead of being concatenated into the SQL string. This removes
   the SQL injection risk noted in the original README's "Known Issues".
3. **Model class** — `AccountSummary.java` is a small POJO representing one row of the
   ACCOUNT table, so the GUI deals with objects instead of raw `ResultSet` columns.
4. **Bug fix** — the original code created the account as `'vishwajeet'` but later ran
   `UPDATE ... WHERE NAME='Kshiti'`, so debit/credit updates were silently no-ops. Now a
   single `ACCOUNT_NAME` constant is used everywhere.

## Structure
```
MoneyManager.java   -> GUI / presentation layer (Swing). No SQL here.
ExpenseDAO.java      -> Data access layer. All SQL lives here, using PreparedStatement.
AccountSummary.java  -> Simple model class for one ACCOUNT row.
```

## How to compile & run
>>>>>>> 03187f7 (Initial commit - expense tracker)
```bash
javac -cp ".:mysql-connector-j-9.5.0.jar" *.java
java -cp ".:mysql-connector-j-9.5.0.jar" MoneyManager
```
<<<<<<< HEAD
(On Windows, replace `:` with `;` in the classpath.)

## Usage
- **Home** — view account holder name and current balance
- **View Statistics** — see total income, total expenses, and net total
- **Add Transaction** — enter amount, date, category, and note, then choose Debit (expense), Credit (income), or Transfer
- **Reset All Data** — clears every table and re-seeds a zero-balance account (with confirmation)

## Project Structure
```
MoneyManager.java      → Swing UI, click handlers (no SQL)
ExpenseDAO.java         → All database access, PreparedStatement-based
AccountSummary.java     → Model class for one ACCOUNT row
mysql-connector-j-9.5.0.jar → JDBC driver
```

## Possible Next Steps
- Add data visualization (charts for category breakdown, trends over time)
- Export transactions to CSV/PDF
- Filter transactions by date range or category
- Multi-user support with authentication
- Unit tests for `ExpenseDAO`
=======

Make sure MySQL is running and the `expensemanagerjava` database exists:
```sql
CREATE DATABASE IF NOT EXISTS expensemanagerjava;
```

Update the username/password in `MoneyManager.java` (`createAndShowGUI` method) to match
your local MySQL credentials before running.
>>>>>>> 03187f7 (Initial commit - expense tracker)
