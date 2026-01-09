import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.awt.event.*;
import java.sql.*;

/**
 * Smart Expense Tracker Application
 * A comprehensive personal finance management system built with Java Swing and MySQL
 * Features: Transaction tracking, expense categorization, balance management, and reporting
 * 
 * @author Vishwajeet
 * @version 2.0
 * @since january 2026
 */
public class moneymanager {

    public static void main(String[] args) {
        // Use SwingUtilities to ensure UI runs on Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        try {
            // ============================================================
            // DATABASE CONFIGURATION & CONNECTION SETUP
            // ============================================================
            
            // MySQL database connection parameters
            String url = "jdbc:mysql://localhost:3306/expensemanagerjava";
            String username = "root";
            String password = "vishwa@123";

            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish database connection
            Connection con = DriverManager.getConnection(url, username, password);
            Statement code = con.createStatement();

            // ============================================================
            // DATABASE INITIALIZATION - CREATE TABLES IF NOT EXISTS
            // ============================================================
            try {
                // ACCOUNT table: Stores user account summary (balance, totals)
                code.execute("CREATE TABLE ACCOUNT(NAME VARCHAR(20) PRIMARY KEY, DEBIT NUMERIC(65,3), CREDIT NUMERIC(65,3), TOTAL NUMERIC(65,3), ACCOUNT_BALANCE NUMERIC(65,3));");
                
                // DEBIT table: Records all debit/expense transactions
                code.execute("CREATE TABLE DEBIT(AMOUNT NUMERIC(65,3), DATE_OF_TRANSACTION DATE, CATEGORY VARCHAR(25), NOTE VARCHAR(200));");
                
                // CREDIT table: Records all credit/income transactions
                code.execute("CREATE TABLE CREDIT(AMOUNT NUMERIC(65,3), DATE_OF_TRANSACTION DATE, CATEGORY VARCHAR(25), NOTE VARCHAR(200));");
                
                // TRANSFER table: Records all transfer transactions
                code.execute("CREATE TABLE TRANSFER(AMOUNT NUMERIC(65,3), DATE_OF_TRANSACTION DATE, CATEGORY VARCHAR(25), NOTE VARCHAR(200));");
                
                // STATEMENTS table: Unified view of all transactions for reporting
                code.execute("CREATE TABLE STATEMENTS(DATE_OF_TRANSACTION DATE, AMOUNT NUMERIC(65,3), TYPE_OF_TRANSACTION VARCHAR(11), CATEGORY VARCHAR(25), NOTE VARCHAR(200));");
                
                // Initialize default account with zero balances
                code.execute("INSERT INTO ACCOUNT VALUES('vishwajeet',0,0,0,0);");
            } catch (SQLException e) {
                // Tables already exist - this is expected on subsequent runs
                System.out.println("Tables already exist: " + e.getMessage());
            }

            // ============================================================
            // FETCH INITIAL ACCOUNT DATA FROM DATABASE
            // ============================================================
            ResultSet accountinfo = code.executeQuery("SELECT * FROM ACCOUNT");
            accountinfo.next(); // Move cursor to first record

            // Extract account information
            double accountbalance = accountinfo.getDouble("ACCOUNT_BALANCE");
            double income = accountinfo.getDouble("CREDIT");
            double expense = accountinfo.getDouble("DEBIT");
            double total = accountinfo.getDouble("TOTAL");
            String name = accountinfo.getString("NAME");

            // Convert numeric values to strings for display
            String accountbalancestr = String.format("₹ %.2f", accountbalance);
            String incomestr = String.format("₹ %.2f", income);
            String expensestr = String.format("₹ %.2f", expense);
            String totalstr = String.format("₹ %.2f", total);

            // ============================================================
            // PREPARE DATA FOR DROPDOWN MENUS
            // ============================================================
            
            // Month names for date picker
            String[] monthlist = {"January", "February", "March", "April", "May", "June", 
                                  "July", "August", "September", "October", "November", "December"};
            
            // Generate day numbers (1-31)
            String[] datelist = new String[31];
            for (int i = 1; i <= 31; i++) {
                datelist[i - 1] = Integer.toString(i);
            }
            
            // Generate year list (current year and 9 years back)
            String[] yearlist = new String[10];
            Calendar instance = Calendar.getInstance();
            int currentyear = instance.get(Calendar.YEAR);
            for (int i = 0; i < 10; i++) {
                yearlist[i] = Integer.toString(currentyear - i);
            }

            // Expense categories for transaction classification
            String[] categories = {"Food", "Self Development", "Transportation", "Beauty", 
                                   "Household", "Health", "Apparel", "Education", "Gift"};

            // ============================================================
            // UI STYLING & THEME CONFIGURATION
            // ============================================================
            
            // Custom color scheme
            Color primaryColor = new Color(41, 128, 185);      // Professional blue
            Color secondaryColor = new Color(52, 73, 94);      // Dark slate
            Color accentColor = new Color(46, 204, 113);       // Success green
            Color dangerColor = new Color(231, 76, 60);        // Alert red
            Color backgroundColor = new Color(236, 240, 241);  // Light gray
            Color lightText = Color.WHITE;
            
            // Font definitions for hierarchy
            Font titleFont = new Font("Segoe UI", Font.BOLD, 32);
            Font headerFont = new Font("Segoe UI", Font.BOLD, 18);
            Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
            Font valueFont = new Font("Segoe UI", Font.BOLD, 16);
            Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);

            // ============================================================
            // MAIN APPLICATION FRAME SETUP
            // ============================================================
            JFrame app = new JFrame();
            app.setLayout(null);
            app.setSize(800, 650);
            app.setTitle("Smart Expense Tracker");
            app.getContentPane().setBackground(backgroundColor);
            app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            app.setResizable(false);
            app.setLocationRelativeTo(null); // Center on screen

            // ============================================================
            // DECLARE ALL PANELS FIRST (so they can be referenced anywhere)
            // ============================================================
            JPanel UserInfo = new JPanel(new GridLayout(4, 2, 20, 25));
            JPanel ViewData = new JPanel(new GridLayout(3, 2, 20, 30));
            JPanel AddTransaction = new JPanel(new GridLayout(6, 2, 15, 20));

            // ============================================================
            // HEADER SECTION - Title and Branding
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
            // NAVIGATION PANEL - Main Menu Buttons
            // ============================================================
            JPanel navPanel = new JPanel();
            navPanel.setBounds(20, 120, 760, 60);
            navPanel.setBackground(backgroundColor);
            navPanel.setLayout(new GridLayout(1, 3, 15, 0));

            // Home button - Shows account overview
            JButton home_btn = new JButton("🏠 Home");
            styleButton(home_btn, primaryColor, lightText, buttonFont);
            navPanel.add(home_btn);

            // View Data button - Shows financial statistics
            JButton view_data_btn = new JButton("📊 View Statistics");
            styleButton(view_data_btn, secondaryColor, lightText, buttonFont);
            navPanel.add(view_data_btn);

            // Add Transaction button - Form to add new transactions
            JButton add_new_btn = new JButton("➕ Add Transaction");
            styleButton(add_new_btn, accentColor, lightText, buttonFont);
            navPanel.add(add_new_btn);

            app.add(navPanel);

            // ============================================================
            // CONFIGURE HOME PANEL - Account Overview
            // ============================================================
            UserInfo.setBounds(40, 210, 720, 350);
            UserInfo.setBackground(Color.WHITE);
            UserInfo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primaryColor, 2, true),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
            ));
            UserInfo.setVisible(true); // Show by default

            // User name display
            JLabel user_name_lbl = new JLabel("Account Holder:");
            user_name_lbl.setFont(labelFont);
            user_name_lbl.setForeground(secondaryColor);
            UserInfo.add(user_name_lbl);

            JLabel user_name = new JLabel(name);
            user_name.setFont(valueFont);
            user_name.setForeground(primaryColor);
            UserInfo.add(user_name);

            // Account balance display
            JLabel account_balance_lbl = new JLabel("Current Balance:");
            account_balance_lbl.setFont(labelFont);
            account_balance_lbl.setForeground(secondaryColor);
            UserInfo.add(account_balance_lbl);

            JLabel account_balance = new JLabel(accountbalancestr);
            account_balance.setFont(new Font("Segoe UI", Font.BOLD, 24));
            account_balance.setForeground(accentColor);
            UserInfo.add(account_balance);

            // Action buttons
            JButton change_info_btn = new JButton("✏️ Edit Profile");
            styleButton(change_info_btn, secondaryColor, lightText, buttonFont);
            UserInfo.add(change_info_btn);

            JButton reset_info_btn = new JButton("🔄 Reset All Data");
            styleButton(reset_info_btn, dangerColor, lightText, buttonFont);
            UserInfo.add(reset_info_btn);

            // Empty labels for spacing
            UserInfo.add(new JLabel(""));
            UserInfo.add(new JLabel(""));

            // Reset button functionality - Clears all data
            reset_info_btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Confirmation dialog to prevent accidental data loss
                    int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to reset all data? This action cannot be undone!",
                        "Confirm Reset",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            // Truncate all tables (removes all records)
                            code.execute("TRUNCATE TABLE ACCOUNT");
                            code.execute("TRUNCATE TABLE CREDIT");
                            code.execute("TRUNCATE TABLE DEBIT");
                            code.execute("TRUNCATE TABLE TRANSFER");
                            code.execute("TRUNCATE TABLE STATEMENTS");
                            
                            // Re-initialize account with default values
                            code.execute("INSERT INTO ACCOUNT VALUES('Kshiti',0,0,0,0)");
                            
                            JOptionPane.showMessageDialog(null, 
                                "All data has been successfully reset!", 
                                "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                            
                            // Refresh display
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

            // Home button functionality - Shows account overview
            home_btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Toggle panel visibility
                    UserInfo.setVisible(true);
                    ViewData.setVisible(false);
                    AddTransaction.setVisible(false);

                    try {
                        // Fetch latest account balance from database
                        ResultSet accountinfo = code.executeQuery("SELECT * FROM ACCOUNT");
                        accountinfo.next();
                        double accountbalance = accountinfo.getDouble("ACCOUNT_BALANCE");
                        
                        // Update display with formatted currency
                        account_balance.setText(String.format("₹ %.2f", accountbalance));
                    } catch (SQLException error) {
                        JOptionPane.showMessageDialog(null, 
                            "Error loading account data.", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // ============================================================
            // CONFIGURE VIEW DATA PANEL - Financial Statistics Display
            // ============================================================
            ViewData.setBounds(40, 210, 720, 350);
            ViewData.setBackground(Color.WHITE);
            ViewData.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primaryColor, 2, true),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
            ));
            ViewData.setVisible(false);

            // Income display
            JLabel IncomeLabel = new JLabel("💵 Total Income:");
            IncomeLabel.setFont(headerFont);
            IncomeLabel.setForeground(secondaryColor);
            ViewData.add(IncomeLabel);

            JLabel Income = new JLabel(incomestr);
            Income.setFont(new Font("Segoe UI", Font.BOLD, 26));
            Income.setForeground(accentColor);
            ViewData.add(Income);

            // Expense display
            JLabel ExpenseLabel = new JLabel("💸 Total Expenses:");
            ExpenseLabel.setFont(headerFont);
            ExpenseLabel.setForeground(secondaryColor);
            ViewData.add(ExpenseLabel);

            JLabel Expense = new JLabel(expensestr);
            Expense.setFont(new Font("Segoe UI", Font.BOLD, 26));
            Expense.setForeground(dangerColor);
            ViewData.add(Expense);

            // Net total display
            JLabel TotalLabel = new JLabel("📈 Net Total:");
            TotalLabel.setFont(headerFont);
            TotalLabel.setForeground(secondaryColor);
            ViewData.add(TotalLabel);

            JLabel Total = new JLabel(totalstr);
            Total.setFont(new Font("Segoe UI", Font.BOLD, 26));
            Total.setForeground(primaryColor);
            ViewData.add(Total);

            // View Data button functionality - Shows statistics
            view_data_btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Toggle panel visibility
                    UserInfo.setVisible(false);
                    ViewData.setVisible(true);
                    AddTransaction.setVisible(false);

                    try {
                        // Fetch latest financial data from database
                        ResultSet accountinfo = code.executeQuery("SELECT * FROM ACCOUNT");
                        accountinfo.next();

                        double income = accountinfo.getDouble("CREDIT");
                        double expense = accountinfo.getDouble("DEBIT");
                        double total = accountinfo.getDouble("TOTAL");

                        // Update display with formatted values
                        Income.setText(String.format("₹ %.2f", income));
                        Expense.setText(String.format("₹ %.2f", expense));
                        Total.setText(String.format("₹ %.2f", total));
                    } catch (SQLException error) {
                        JOptionPane.showMessageDialog(null, 
                            "Error loading statistics.", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // ============================================================
            // CONFIGURE ADD TRANSACTION PANEL - Transaction Entry Form
            // ============================================================
            AddTransaction.setBounds(40, 210, 720, 380);
            AddTransaction.setBackground(Color.WHITE);
            AddTransaction.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primaryColor, 2, true),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
            ));
            AddTransaction.setVisible(false);

            // Amount input field
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

            // Date picker (Day, Month, Year dropdowns)
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

            // Category selection dropdown
            JLabel Category = new JLabel("🏷️ Category:");
            Category.setFont(labelFont);
            Category.setForeground(secondaryColor);
            AddTransaction.add(Category);

            JComboBox<String> CategoryInput = new JComboBox<>(categories);
            styleComboBox(CategoryInput, labelFont);
            AddTransaction.add(CategoryInput);

            // Note/description input field
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
            // TRANSACTION BUTTONS - Debit, Credit, Transfer
            // ============================================================

            // DEBIT BUTTON - Records expense transactions
            JButton debit_btn = new JButton("💳 Debit (Expense)");
            styleButton(debit_btn, dangerColor, lightText, buttonFont);
            debit_btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        // Parse and validate amount input
                        double amount = Double.parseDouble(AmountInput.getText());
                        
                        // Format date as YYYY-MM-DD for SQL
                        String date = eYear.getSelectedItem().toString() + "-" + 
                                      (eMonth.getSelectedIndex() + 1) + "-" + 
                                      eDate.getSelectedItem().toString();
                        
                        String category = CategoryInput.getSelectedItem().toString();
                        String note = NoteInput.getText();

                        // Fetch current account values
                        ResultSet accountinfo = code.executeQuery("SELECT * FROM ACCOUNT");
                        accountinfo.next();
                        double accountbalance = accountinfo.getDouble("ACCOUNT_BALANCE");
                        double expense = accountinfo.getDouble("DEBIT");
                        double total = accountinfo.getDouble("TOTAL");

                        // Update balances (subtract for debit)
                        accountbalance -= amount;
                        expense -= amount;  // Stored as negative
                        total -= amount;

                        // Update ACCOUNT table with new balances
                        code.execute("UPDATE ACCOUNT SET ACCOUNT_BALANCE='" + accountbalance + 
                                     "', DEBIT='" + expense + "', TOTAL='" + total + 
                                     "' WHERE NAME='Kshiti';");

                        // Insert transaction into DEBIT table
                        code.execute("INSERT INTO DEBIT VALUES('" + amount + "','" + date + 
                                     "','" + category + "','" + note + "');");
                        
                        // Insert into STATEMENTS table for unified reporting
                        code.execute("INSERT INTO STATEMENTS VALUES('" + date + "','" + amount + 
                                     "','Debit','" + category + "','" + note + "');");

                        JOptionPane.showMessageDialog(null, 
                            "Debit transaction added successfully!", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);

                        // Clear form fields after successful entry
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

            // CREDIT BUTTON - Records income transactions
            JButton credit_btn = new JButton("💵 Credit (Income)");
            styleButton(credit_btn, accentColor, lightText, buttonFont);
            credit_btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        // Parse and validate amount input
                        double amount = Double.parseDouble(AmountInput.getText());
                        
                        // Format date as YYYY-MM-DD for SQL
                        String date = eYear.getSelectedItem().toString() + "-" + 
                                      (eMonth.getSelectedIndex() + 1) + "-" + 
                                      eDate.getSelectedItem().toString();
                        
                        String category = CategoryInput.getSelectedItem().toString();
                        String note = NoteInput.getText();

                        // Fetch current account values
                        ResultSet accountinfo = code.executeQuery("SELECT * FROM ACCOUNT");
                        accountinfo.next();
                        double accountbalance = accountinfo.getDouble("ACCOUNT_BALANCE");
                        double income = accountinfo.getDouble("CREDIT");
                        double total = accountinfo.getDouble("TOTAL");

                        // Update balances (add for credit)
                        accountbalance += amount;
                        income += amount;
                        total += amount;

                        // Update ACCOUNT table with new balances
                        code.execute("UPDATE ACCOUNT SET ACCOUNT_BALANCE='" + accountbalance + 
                                     "', CREDIT='" + income + "', TOTAL='" + total + 
                                     "' WHERE NAME='Kshiti'");

                        // Insert transaction into CREDIT table
                        code.execute("INSERT INTO CREDIT VALUES('" + amount + "','" + date + 
                                     "','" + category + "','" + note + "');");
                        
                        // Insert into STATEMENTS table for unified reporting
                        code.execute("INSERT INTO STATEMENTS VALUES('" + date + "','" + amount + 
                                     "','Credit','" + category + "','" + note + "');");

                        JOptionPane.showMessageDialog(null, 
                            "Credit transaction added successfully!", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);

                        // Clear form fields after successful entry
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

            // TRANSFER BUTTON - Records transfer transactions (no balance impact)
            JButton transfer_btn = new JButton("🔄 Transfer");
            styleButton(transfer_btn, new Color(155, 89, 182), lightText, buttonFont);
            transfer_btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        // Parse and validate amount input
                        double amount = Double.parseDouble(AmountInput.getText());
                        
                        // Format date as YYYY-MM-DD for SQL
                        String date = eYear.getSelectedItem().toString() + "-" + 
                                      (eMonth.getSelectedIndex() + 1) + "-" + 
                                      eDate.getSelectedItem().toString();
                        
                        String category = CategoryInput.getSelectedItem().toString();
                        String note = NoteInput.getText();

                        // Insert transaction into TRANSFER table
                        code.execute("INSERT INTO TRANSFER VALUES('" + amount + "','" + date + 
                                     "','" + category + "','" + note + "');");
                        
                        // Insert into STATEMENTS table for reporting
                        code.execute("INSERT INTO STATEMENTS VALUES('" + date + "','" + amount + 
                                     "','Transfer','" + category + "','" + note + "');");

                        JOptionPane.showMessageDialog(null, 
                            "Transfer transaction added successfully!", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);

                        // Clear form fields after successful entry
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

            // Add Transaction button functionality - Shows entry form
            add_new_btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    UserInfo.setVisible(false);
                    ViewData.setVisible(false);
                    AddTransaction.setVisible(true);
                }
            });

            // ============================================================
            // ADD ALL PANELS TO MAIN FRAME
            // ============================================================
            app.add(UserInfo);
            app.add(ViewData);
            app.add(AddTransaction);

            // Make application visible (MUST BE LAST)
            app.setVisible(true);

        } catch (ClassNotFoundException e) {
            // JDBC driver not found
            JOptionPane.showMessageDialog(null, 
                "MySQL JDBC Driver not found. Please add mysql-connector-java to classpath.", 
                "Driver Error", 
                JOptionPane.ERROR_MESSAGE);
            System.out.println(e);
        } catch (SQLException e) {
            // Database connection or query error
            JOptionPane.showMessageDialog(null, 
                "Database connection failed. Please check MySQL server and credentials.", 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            System.out.println(e);
        }
    }

    // ============================================================
    // UTILITY METHODS FOR UI STYLING
    // ============================================================

    /**
     * Applies consistent styling to buttons
     * @param button The JButton to style
     * @param bgColor Background color
     * @param fgColor Text color
     * @param font Font style
     */
    private static void styleButton(JButton button, Color bgColor, Color fgColor, Font font) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(font);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }

    /**
     * Applies consistent styling to combo boxes
     * @param comboBox The JComboBox to style
     * @param font Font style
     */
    private static void styleComboBox(JComboBox<String> comboBox, Font font) {
        comboBox.setFont(font);
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
    }

    /**
     * Clears all input fields in the transaction form
     * Resets form to default state after successful transaction entry
     */
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