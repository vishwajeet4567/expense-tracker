# expense-tracker
# 💰 Smart Expense Tracker

A comprehensive desktop application for personal finance management built with Java Swing and MySQL. Track your income, expenses, and transfers with an intuitive interface and real-time analytics.



## 📋 Table of Contents
- [Features](#-features)
- [Screenshots](#-screenshots)
- [Technologies Used](#-technologies-used)
- [Architecture](#-architecture)
- [Database Schema](#-database-schema)
- [Installation](#-installation)
- [Usage](#-usage)
- [Configuration](#-configuration)
- [Future Enhancements](#-future-enhancements)
- [Contributing](#-contributing)
- [License](#-license)
- [Contact](#-contact)

## ✨ Features

### Core Functionality
- 💵 **Income Tracking** - Record all credit transactions with detailed categorization
- 💸 **Expense Management** - Track debits across 9 different categories
- 🔄 **Transfer Recording** - Log money transfers between accounts
- 📊 **Real-time Statistics** - View live updates of income, expenses, and net balance
- 🏠 **Account Dashboard** - Centralized view of account balance and user info
- 📝 **Transaction Notes** - Add descriptions to every transaction for better tracking
- 📅 **Date Tracking** - Record transactions with specific dates
- 🔄 **Data Reset** - Reset all data with confirmation safeguards

### User Experience
- 🎨 **Modern UI Design** - Clean, professional interface with intuitive navigation
- 🎯 **Category-based Organization** - 9 pre-defined expense categories
- ⚡ **Instant Updates** - Real-time balance calculations
- ⚠️ **Error Handling** - User-friendly error messages and validation
- 🖱️ **Interactive Buttons** - Hover effects and responsive design
- 💾 **Persistent Storage** - All data saved to MySQL database

### Technical Features
- 🏗️ **MVC Architecture** - Separation of concerns for maintainability
- 🔒 **Transaction Safety** - Database-backed reliability
- 📈 **Scalable Design** - Structured for future enhancements
- 🧹 **Clean Code** - Comprehensive comments and documentation

## 📸 Screenshots

### Home Dashboard
*Account overview with current balance and quick actions*

### View Statistics
*Comprehensive financial summary with income, expenses, and net total*

### Add Transaction
*Transaction entry form with category selection and date picker*

## 🛠️ Technologies Used

### Frontend
- **Java Swing** - GUI framework for desktop applications
- **AWT** - Abstract Window Toolkit for event handling
- **Custom Styling** - Modern color scheme and typography

### Backend
- **Java** - Core programming language
- **JDBC** - Database connectivity
- **MySQL** - Relational database management

### Tools & Libraries
- **MySQL Connector/J** - JDBC driver for MySQL
- **JDK 8+** - Java Development Kit
- **Calendar API** - Date handling

## 🏗️ Architecture

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  ┌──────────┬──────────┬─────────────┐ │
│  │   Home   │   View   │    Add      │ │
│  │  Panel   │   Data   │ Transaction │ │
│  └──────────┴──────────┴─────────────┘ │
└─────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│         Business Logic Layer            │
│  ┌───────────────────────────────────┐ │
│  │  Transaction Processing           │ │
│  │  Balance Calculations             │ │
│  │  Validation & Error Handling      │ │
│  └───────────────────────────────────┘ │
└─────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│           Data Layer (JDBC)             │
│  ┌───────────────────────────────────┐ │
│  │  MySQL Database Connection        │ │
│  │  CRUD Operations                  │ │
│  │  Query Execution                  │ │
│  └───────────────────────────────────┘ │
└─────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│          MySQL Database                 │
│  ┌─────────┬─────────┬────────────┐   │
│  │ ACCOUNT │  DEBIT  │   CREDIT   │   │
│  ├─────────┼─────────┼────────────┤   │
│  │TRANSFER │STATEMENTS│            │   │
│  └─────────┴─────────┴────────────┘   │
└─────────────────────────────────────────┘
```

## 🗄️ Database Schema

### Tables

#### 1. ACCOUNT
Stores user account summary and aggregated balances
```sql
CREATE TABLE ACCOUNT (
    NAME VARCHAR(20) PRIMARY KEY,
    DEBIT NUMERIC(65,3),
    CREDIT NUMERIC(65,3),
    TOTAL NUMERIC(65,3),
    ACCOUNT_BALANCE NUMERIC(65,3)
);
```

#### 2. DEBIT
Records all expense transactions
```sql
CREATE TABLE DEBIT (
    AMOUNT NUMERIC(65,3),
    DATE_OF_TRANSACTION DATE,
    CATEGORY VARCHAR(25),
    NOTE VARCHAR(200)
);
```

#### 3. CREDIT
Records all income transactions
```sql
CREATE TABLE CREDIT (
    AMOUNT NUMERIC(65,3),
    DATE_OF_TRANSACTION DATE,
    CATEGORY VARCHAR(25),
    NOTE VARCHAR(200)
);
```

#### 4. TRANSFER
Records all transfer transactions
```sql
CREATE TABLE TRANSFER (
    AMOUNT NUMERIC(65,3),
    DATE_OF_TRANSACTION DATE,
    CATEGORY VARCHAR(25),
    NOTE VARCHAR(200)
);
```

#### 5. STATEMENTS
Unified view of all transactions for reporting
```sql
CREATE TABLE STATEMENTS (
    DATE_OF_TRANSACTION DATE,
    AMOUNT NUMERIC(65,3),
    TYPE_OF_TRANSACTION VARCHAR(11),
    CATEGORY VARCHAR(25),
    NOTE VARCHAR(200)
);
```

### Categories
- Food
- Self Development
- Transportation
- Beauty
- Household
- Health
- Apparel
- Education
- Gift

## 📥 Installation

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- MySQL Server 5.7 or higher
- MySQL Connector/J (JDBC Driver)

### Step-by-Step Setup

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/smart-expense-tracker.git
   cd smart-expense-tracker
   ```

2. **Set Up MySQL Database**
   ```sql
   -- Create database
   CREATE DATABASE expensemanagerjava;
   
   -- Use the database
   USE expensemanagerjava;
   ```

3. **Configure Database Connection**
   
   Open `moneymanager.java` and update the database credentials:
   ```java
   String url = "jdbc:mysql://localhost:3306/expensemanagerjava";
   String username = "your_mysql_username";
   String password = "your_mysql_password";
   ```

4. **Add MySQL Connector to Classpath**
   
   Download [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/) and add to your project:
   
   **Using Command Line:**
   ```bash
   javac -cp .:mysql-connector-java-8.0.xx.jar moneymanager.java
   java -cp .:mysql-connector-java-8.0.xx.jar moneymanager
   ```
   
   **Using IDE (Eclipse/IntelliJ):**
   - Right-click project → Build Path → Add External JARs
   - Select the mysql-connector-java JAR file

5. **Compile and Run**
   ```bash
   javac moneymanager.java
   java moneymanager
   ```

## 🚀 Usage

### Adding a Transaction

1. Click **"➕ Add Transaction"** button
2. Enter the **amount**
3. Select the **date** (day, month, year)
4. Choose a **category** from dropdown
5. Add optional **notes**
6. Click one of:
   - **💳 Debit (Expense)** - Records an expense and decreases balance
   - **💵 Credit (Income)** - Records income and increases balance
   - **🔄 Transfer** - Records a transfer (no balance change)

### Viewing Statistics

1. Click **"📊 View Statistics"** button
2. See real-time updates of:
   - Total Income (Green)
   - Total Expenses (Red)
   - Net Total (Blue)

### Managing Account

1. Click **"🏠 Home"** button
2. View current account balance
3. Options:
   - **✏️ Edit Profile** - Update account information
   - **🔄 Reset All Data** - Clear all transactions (with confirmation)

## ⚙️ Configuration

### Customizing Categories

Edit the `categories` array in `moneymanager.java`:
```java
String[] categories = {
    "Food", 
    "Self Development", 
    "Transportation", 
    "Beauty", 
    "Household", 
    "Health", 
    "Apparel", 
    "Education", 
    "Gift",
    "Your Custom Category"  // Add new categories here
};
```

### Changing Color Scheme

Modify the color variables:
```java
Color primaryColor = new Color(41, 128, 185);      // Main accent color
Color secondaryColor = new Color(52, 73, 94);      // Secondary color
Color accentColor = new Color(46, 204, 113);       // Success/Income color
Color dangerColor = new Color(231, 76, 60);        // Danger/Expense color
Color backgroundColor = new Color(236, 240, 241);  // Background
```

### Database Connection Pooling (Recommended for Production)

Replace direct connection with HikariCP:
```java
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:mysql://localhost:3306/expensemanagerjava");
config.setUsername("root");
config.setPassword("password");
config.addDataSourceProperty("cachePrepStmts", "true");

HikariDataSource ds = new HikariDataSource(config);
Connection con = ds.getConnection();
```

## 🔮 Future Enhancements

### Planned Features
- [ ] **Data Visualization**
  - Pie charts for expense breakdown by category
  - Line graphs for income/expense trends over time
  - Monthly/yearly comparison reports

- [ ] **Export Functionality**
  - Export transactions to PDF
  - Generate Excel reports
  - CSV data export

- [ ] **Advanced Filtering**
  - Filter by date range
  - Filter by category
  - Search transactions by note

- [ ] **Budget Management**
  - Set monthly budgets per category
  - Budget alerts and notifications
  - Spending limit warnings

- [ ] **Multi-User Support**
  - User authentication system
  - Individual user accounts
  - Role-based access control

- [ ] **Security Enhancements**
  - PreparedStatements for SQL injection prevention
  - Password encryption
  - Session management

- [ ] **UI/UX Improvements**
  - Responsive design for different screen sizes
  - Dark mode theme
  - Customizable dashboard
  - Transaction history table view

- [ ] **Database Optimization**
  - Add indexes for faster queries
  - Implement stored procedures
  - Database backup and restore functionality

- [ ] **Code Quality**
  - Refactor to DAO pattern
  - Implement proper MVC architecture
  - Add unit tests (JUnit)
  - Integration tests

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/AmazingFeature
   ```
3. **Commit your changes**
   ```bash
   git commit -m 'Add some AmazingFeature'
   ```
4. **Push to the branch**
   ```bash
   git push origin feature/AmazingFeature
   ```
5. **Open a Pull Request**

### Contribution Guidelines
- Follow existing code style and conventions
- Add comments for complex logic
- Update README if adding new features
- Test thoroughly before submitting PR

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2025 Kshiti

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 📧 Contact

**Kshiti**
- GitHub: [@yourusername](https://github.com/yourusername)
- Email: your.email@example.com
- LinkedIn: [Your LinkedIn](https://linkedin.com/in/yourprofile)

## 🙏 Acknowledgments

- Inspired by the need for simple, effective personal finance management
- Built as part of learning Java Swing and JDBC
- Thanks to the open-source community for MySQL and Java resources

## 📊 Project Stats

- **Development Period**: March 2025 - May 2025
- **Lines of Code**: ~600+
- **Database Tables**: 5
- **Supported Categories**: 9
- **Technologies**: 3 (Java, MySQL, JDBC)

## 🐛 Known Issues

- Currently uses Statement instead of PreparedStatement (security improvement needed)
- No input validation for special characters
- Connection remains open throughout application lifecycle
- No data backup functionality

## 📚 Documentation

For detailed code documentation and API references, see the [Wiki](https://github.com/yourusername/smart-expense-tracker/wiki).

---

⭐ **If you found this project helpful, please consider giving it a star!**

**Made with ❤️ by Kshiti**
