import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.awt.event.*;
import java.sql.*;

/**
 * Smart Expense Tracker — GUI layer.
 *
 * This class is now ONLY responsible for building the UI and reacting to
 * clicks. It does not contain a single line of SQL — every read/write goes
 * through ExpenseDAO. This is the layered structure interviewers expect:
 *
 *   MoneyManager (UI / presentation)
 *         |
 *   ExpenseDAO (data access — the only class that talks to JDBC)
 *         |
 *   MySQL Database
 *
 * @author Vishwajeet
 * @version 3.0 — refactored to a DAO-based layered structure
 */
public class MoneyManager {

    // Single account name used throughout the app. Pulling this into one
    // constant fixes a bug in the original code, where the account was
    // created as "vishwajeet" but later updated using "Kshiti" — those
    // mismatched names meant balance updates were silently failing to
    // match any row.
    private static final String ACCOUNT_NAME = "vishwajeet";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        try {
            // ============================================================
            // DATABASE CONNECTION + DAO SETUP
            // ============================================================
            String url = "jdbc:mysql://localhost:3306/expensemanagerjava";
            String username = "root";
            String password = "MySql@2026Strong!";

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, username, password);

            // The GUI talks to this one object for every database operation.
            ExpenseDAO dao = new ExpenseDAO(con);
            dao.initializeSchema();

            // ============================================================
            // FETCH INITIAL ACCOUNT DATA
            // ============================================================
            AccountSummary account = dao.getAccount(ACCOUNT_NAME);

            String accountbalancestr = String.format("₹ %.2f", account.getAccountBalance());
            String incomestr = String.format("₹ %.2f", account.getCredit());
            String expensestr = String.format("₹ %.2f", account.getDebit());
            String totalstr = String.format("₹ %.2f", account.getTotal());
            String name = account.getName();

            // ============================================================
            // DROPDOWN DATA
            // ============================================================
            String[] monthlist = {"January", "February", "March", "April", "May", "June",
                                  "July", "August", "September", "October", "November", "December"};

            String[] datelist = new String[31];
            for (int i = 1; i <= 31; i++) {
                datelist[i - 1] = Integer.toString(i);
            }

            String[] yearlist = new String[10];
            Calendar instance = Calendar.getInstance();
            int currentyear = instance.get(Calendar.YEAR);
            for (int i = 0; i < 10; i++) {
                yearlist[i] = Integer.toString(currentyear - i);
            }

            String[] categories = {"Food", "Self Development", "Transportation", "Beauty",
                                   "Household", "Health", "Apparel", "Education", "Gift"};

            // ============================================================
            // UI STYLING
            // ============================================================
            Color primaryColor = new Color(41, 128, 185);
            Color secondaryColor = new Color(52, 73, 94);
            Color accentColor = new Color(46, 204, 113);
            Color dangerColor = new Color(231, 76, 60);
            Color backgroundColor = new Color(236, 240, 241);
            Color lightText = Color.WHITE;

            Font titleFont = new Font("Segoe UI", Font.BOLD, 32);
            Font headerFont = new Font("Segoe UI", Font.BOLD, 18);
            Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
            Font valueFont = new Font("Segoe UI", Font.BOLD, 16);
            Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);

            // ============================================================
            // MAIN FRAME
            // ============================================================
            JFrame app = new JFrame();
            app.setLayout(null);
            app.setSize(800, 650);
            app.setTitle("Smart Expense Tracker");
            app.getContentPane().setBackground(backgroundColor);
            app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            app.setResizable(false);
            app.setLocationRelativeTo(null);

            JPanel UserInfo = new JPanel(new GridLayout(4, 2, 20, 25));
            JPanel ViewData = new JPanel(new GridLayout(3, 2, 20, 30));
            JPanel AddTransaction = new JPanel(new GridLayout(6, 2, 15, 20));

            // ============================================================
            // HEADER
            // ============================================================
            JPanel headerPanel = new JPanel();
            headerPanel.setBounds(0, 0, 800, 100);
            headerPanel.setBackground(primaryColor);
            headerPanel.setLayout(null);

            JLabel heading = new JLabel("💰 SMART EXPENSE TRACKER");
            heading.setBounds(150, 25, 500, 50);
            heading.setFont(titleFont);
            heading.setForeground(lightText);
            headerPanel.add(heading);

            app.add(headerPanel);

            // ============================================================
            // NAVIGATION
            // ============================================================
            JPanel navPanel = new JPanel();
            navPanel.setBounds(20, 120, 760, 60);
            navPanel.setBackground(backgroundColor);
            navPanel.setLayout(new GridLayout(1, 3, 15, 0));

            JButton home_btn = new JButton("🏠 Home");
            styleButton(home_btn, primaryColor, lightText, buttonFont);
            navPanel.add(home_btn);

            JButton view_data_btn = new JButton("📊 View Statistics");
            styleButton(view_data_btn, secondaryColor, lightText, buttonFont);
            navPanel.add(view_data_btn);

            JButton add_new_btn = new JButton("➕ Add Transaction");
            styleButton(add_new_btn, accentColor, lightText, buttonFont);
            navPanel.add(add_new_btn);

            app.add(navPanel);

            // ============================================================
            // HOME PANEL
            // ============================================================
            UserInfo.setBounds(40, 210, 720, 350);
            UserInfo.setBackground(Color.WHITE);
            UserInfo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primaryColor, 2, true),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
            ));
            UserInfo.setVisible(true);

            JLabel user_name_lbl = new JLabel("Account Holder:");
            user_name_lbl.setFont(labelFont);
            user_name_lbl.setForeground(secondaryColor);
            UserInfo.add(user_name_lbl);

            JLabel user_name = new JLabel(name);
            user_name.setFont(valueFont);
            user_name.setForeground(primaryColor);
            UserInfo.add(user_name);

            JLabel account_balance_lbl = new JLabel("Current Balance:");
            account_balance_lbl.setFont(labelFont);
            account_balance_lbl.setForeground(secondaryColor);
            UserInfo.add(account_balance_lbl);

            JLabel account_balance = new JLabel(accountbalancestr);
            account_balance.setFont(new Font("Segoe UI", Font.BOLD, 24));
            account_balance.setForeground(accentColor);
            UserInfo.add(account_balance);

            JButton change_info_btn = new JButton("✏️ Edit Profile");
            styleButton(change_info_btn, secondaryColor, lightText, buttonFont);
            UserInfo.add(change_info_btn);

            JButton reset_info_btn = new JButton("🔄 Reset All Data");
            styleButton(reset_info_btn, dangerColor, lightText, buttonFont);
            UserInfo.add(reset_info_btn);

            UserInfo.add(new JLabel(""));
            UserInfo.add(new JLabel(""));

            // RESET — now calls dao.resetAllData() instead of raw SQL
            reset_info_btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to reset all data? This action cannot be undone!",
                        "Confirm Reset",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            dao.resetAllData(ACCOUNT_NAME);

                            JOptionPane.showMessageDialog(null,
                                "All data has been successfully reset!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);

                            account_balance.setText("₹ 0.00");
                        } catch (SQLException error) {
                            JOptionPane.showMessageDialog(null,
                                "Error occurred while resetting data. Please try again.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });

            // HOME — now calls dao.getAccount() instead of raw SQL
            home_btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    UserInfo.setVisible(true);
                    ViewData.setVisible(false);
                    AddTransaction.setVisible(false);

                    try {
                        AccountSummary refreshed = dao.getAccount(ACCOUNT_NAME);
                        account_balance.setText(String.format("₹ %.2f", refreshed.getAccountBalance()));
                    } catch (SQLException error) {
                        JOptionPane.showMessageDialog(null,
                            "Error loading account data.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // ============================================================
            // VIEW DATA PANEL
            // ============================================================
            ViewData.setBounds(40, 210, 720, 350);
            ViewData.setBackground(Color.WHITE);
            ViewData.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primaryColor, 2, true),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
            ));
            ViewData.setVisible(false);

            JLabel IncomeLabel = new JLabel("💵 Total Income:");
            IncomeLabel.setFont(headerFont);
            IncomeLabel.setForeground(secondaryColor);
            ViewData.add(IncomeLabel);

            JLabel Income = new JLabel(incomestr);
            Income.setFont(new Font("Segoe UI", Font.BOLD, 26));
            Income.setForeground(accentColor);
            ViewData.add(Income);

            JLabel ExpenseLabel = new JLabel("💸 Total Expenses:");
            ExpenseLabel.setFont(headerFont);
            ExpenseLabel.setForeground(secondaryColor);
            ViewData.add(ExpenseLabel);

            JLabel Expense = new JLabel(expensestr);
            Expense.setFont(new Font("Segoe UI", Font.BOLD, 26));
            Expense.setForeground(dangerColor);
            ViewData.add(Expense);

            JLabel TotalLabel = new JLabel("📈 Net Total:");
            TotalLabel.setFont(headerFont);
            TotalLabel.setForeground(secondaryColor);
            ViewData.add(TotalLabel);

            JLabel Total = new JLabel(totalstr);
            Total.setFont(new Font("Segoe UI", Font.BOLD, 26));
            Total.setForeground(primaryColor);
            ViewData.add(Total);

            // VIEW STATS — now calls dao.getAccount() instead of raw SQL
            view_data_btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    UserInfo.setVisible(false);
                    ViewData.setVisible(true);
                    AddTransaction.setVisible(false);

                    try {
                        AccountSummary refreshed = dao.getAccount(ACCOUNT_NAME);
                        Income.setText(String.format("₹ %.2f", refreshed.getCredit()));
                        Expense.setText(String.format("₹ %.2f", refreshed.getDebit()));
                        Total.setText(String.format("₹ %.2f", refreshed.getTotal()));
                    } catch (SQLException error) {
                        JOptionPane.showMessageDialog(null,
                            "Error loading statistics.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // ============================================================
            // ADD TRANSACTION PANEL
            // ============================================================
            AddTransaction.setBounds(40, 210, 720, 380);
            AddTransaction.setBackground(Color.WHITE);
            AddTransaction.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primaryColor, 2, true),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
            ));
            AddTransaction.setVisible(false);

            JLabel Amount = new JLabel("💰 Amount:");
            Amount.setFont(labelFont);
            Amount.setForeground(secondaryColor);
            AddTransaction.add(Amount);

            JTextField AmountInput = new JTextField();
            AmountInput.setFont(labelFont);
            AmountInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            AddTransaction.add(AmountInput);

            JLabel Date = new JLabel("📅 Date:");
            Date.setFont(labelFont);
            Date.setForeground(secondaryColor);
            AddTransaction.add(Date);

            JPanel DateInput = new JPanel(new GridLayout(1, 3, 10, 0));
            DateInput.setBackground(Color.WHITE);
            AddTransaction.add(DateInput);

            JComboBox<String> eDate = new JComboBox<>(datelist);
            styleComboBox(eDate, labelFont);
            DateInput.add(eDate);

            JComboBox<String> eMonth = new JComboBox<>(monthlist);
            styleComboBox(eMonth, labelFont);
            DateInput.add(eMonth);

            JComboBox<String> eYear = new JComboBox<>(yearlist);
            styleComboBox(eYear, labelFont);
            DateInput.add(eYear);

            JLabel Category = new JLabel("🏷️ Category:");
            Category.setFont(labelFont);
            Category.setForeground(secondaryColor);
            AddTransaction.add(Category);

            JComboBox<String> CategoryInput = new JComboBox<>(categories);
            styleComboBox(CategoryInput, labelFont);
            AddTransaction.add(CategoryInput);

            JLabel Note = new JLabel("📝 Note:");
            Note.setFont(labelFont);
            Note.setForeground(secondaryColor);
            AddTransaction.add(Note);

            JTextField NoteInput = new JTextField();
            NoteInput.setFont(labelFont);
            NoteInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            AddTransaction.add(NoteInput);

            // ============================================================
            // DEBIT — now calls dao.recordDebit() instead of raw SQL
            // ============================================================
            JButton debit_btn = new JButton("💳 Debit (Expense)");
            styleButton(debit_btn, dangerColor, lightText, buttonFont);
            debit_btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        double amount = Double.parseDouble(AmountInput.getText());
                        String date = eYear.getSelectedItem().toString() + "-" +
                                      (eMonth.getSelectedIndex() + 1) + "-" +
                                      eDate.getSelectedItem().toString();
                        String category = CategoryInput.getSelectedItem().toString();
                        String note = NoteInput.getText();

                        dao.recordDebit(ACCOUNT_NAME, amount, date, category, note);

                        JOptionPane.showMessageDialog(null,
                            "Debit transaction added successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                        clearTransactionForm(AmountInput, eDate, eMonth, eYear, CategoryInput, NoteInput);

                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(null,
                            "Please enter a valid amount.",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    } catch (SQLException error) {
                        JOptionPane.showMessageDialog(null,
                            "Database error. Please try again.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            AddTransaction.add(debit_btn);

            // ============================================================
            // CREDIT — now calls dao.recordCredit() instead of raw SQL
            // ============================================================
            JButton credit_btn = new JButton("💵 Credit (Income)");
            styleButton(credit_btn, accentColor, lightText, buttonFont);
            credit_btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        double amount = Double.parseDouble(AmountInput.getText());
                        String date = eYear.getSelectedItem().toString() + "-" +
                                      (eMonth.getSelectedIndex() + 1) + "-" +
                                      eDate.getSelectedItem().toString();
                        String category = CategoryInput.getSelectedItem().toString();
                        String note = NoteInput.getText();

                        dao.recordCredit(ACCOUNT_NAME, amount, date, category, note);

                        JOptionPane.showMessageDialog(null,
                            "Credit transaction added successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                        clearTransactionForm(AmountInput, eDate, eMonth, eYear, CategoryInput, NoteInput);

                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(null,
                            "Please enter a valid amount.",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    } catch (SQLException error) {
                        JOptionPane.showMessageDialog(null,
                            "Database error. Please try again.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            AddTransaction.add(credit_btn);

            // ============================================================
            // TRANSFER — now calls dao.recordTransfer() instead of raw SQL
            // ============================================================
            JButton transfer_btn = new JButton("🔄 Transfer");
            styleButton(transfer_btn, new Color(155, 89, 182), lightText, buttonFont);
            transfer_btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        double amount = Double.parseDouble(AmountInput.getText());
                        String date = eYear.getSelectedItem().toString() + "-" +
                                      (eMonth.getSelectedIndex() + 1) + "-" +
                                      eDate.getSelectedItem().toString();
                        String category = CategoryInput.getSelectedItem().toString();
                        String note = NoteInput.getText();

                        dao.recordTransfer(amount, date, category, note);

                        JOptionPane.showMessageDialog(null,
                            "Transfer transaction added successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                        clearTransactionForm(AmountInput, eDate, eMonth, eYear, CategoryInput, NoteInput);

                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(null,
                            "Please enter a valid amount.",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    } catch (SQLException error) {
                        JOptionPane.showMessageDialog(null,
                            "Database error. Please try again.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            AddTransaction.add(transfer_btn);

            add_new_btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    UserInfo.setVisible(false);
                    ViewData.setVisible(false);
                    AddTransaction.setVisible(true);
                }
            });

            // ============================================================
            // ASSEMBLE + SHOW
            // ============================================================
            app.add(UserInfo);
            app.add(ViewData);
            app.add(AddTransaction);

            app.setVisible(true);

        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "MySQL JDBC Driver not found. Please add mysql-connector-java to classpath.",
                "Driver Error",
                JOptionPane.ERROR_MESSAGE);
            System.out.println(e);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Database connection failed. Please check MySQL server and credentials.",
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            System.out.println(e);
        }
    }

    // ============================================================
    // UI STYLING HELPERS (unchanged — these are presentation only)
    // ============================================================

    private static void styleButton(JButton button, Color bgColor, Color fgColor, Font font) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(font);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }

    private static void styleComboBox(JComboBox<String> comboBox, Font font) {
        comboBox.setFont(font);
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
    }

    private static void clearTransactionForm(JTextField amountField, JComboBox<String> dateBox,
                                             JComboBox<String> monthBox, JComboBox<String> yearBox,
                                             JComboBox<String> categoryBox, JTextField noteField) {
        amountField.setText("");
        dateBox.setSelectedIndex(0);
        monthBox.setSelectedIndex(0);
        yearBox.setSelectedIndex(0);
        categoryBox.setSelectedIndex(0);
        noteField.setText("");
    }
}
