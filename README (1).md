# SwiftCash ATM Project

## Description
SwiftCash ATM is a Java-based console application that simulates an ATM system. It allows users to create an account, deposit money, withdraw cash, check balance, and print receipts. The system integrates with a MySQL database to store user details securely and enforces security measures such as PIN verification and OTP authentication for large withdrawals.

## Features
- **Account Creation**: Users can create an account with a name, phone number, and 4-digit PIN.
- **Balance Inquiry**: Check the current account balance.
- **Deposit Money**: Add funds to the account.
- **Withdraw Money**: Withdraw funds with daily limits and OTP verification for large amounts.
- **Receipt Printing**: Displays a structured receipt of the transaction.
- **Database Integration**: Uses MySQL to store and retrieve user details.

## Technologies Used
- **Java** (Core logic & user interaction)
- **JDBC** (Database connectivity)
- **MySQL** (Data storage)

## Installation & Setup
1. **Clone the repository**:
   ```sh
   git clone https://github.com/your-username/swiftcash-atm.git
   cd swiftcash-atm
   ```

2. **Set up MySQL database**:
   - Create a database named `atmdb`.
   - Create a table with the following structure:
   ```sql
   CREATE TABLE accounts (
       id INT AUTO_INCREMENT PRIMARY KEY,
       name VARCHAR(255) NOT NULL,
       pin INT NOT NULL,
       balance DOUBLE NOT NULL,
       phone_number VARCHAR(15) NOT NULL,
       daily_withdrawal DOUBLE DEFAULT 0
   );
   ```

3. **Update database credentials**:
   - Open `CashATMProject.java` and update the MySQL connection details:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/atmdb";
   private static final String USER = "root";
   private static final String PASSWORD = "your-password";
   ```

4. **Compile & Run the Java application**:
   ```sh
   javac CashATMProject.java
   java CashATMProject
   ```

## Usage
- Follow on-screen instructions to create an account.
- Enter your PIN to access account features.
- Deposit or withdraw money with required security checks.
- Print a receipt for transaction history.

