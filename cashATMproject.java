package swiftcash;

import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class CashATMProject {

    private static final String URL = "jdbc:mysql://localhost:3306/atmdb";
    private static final String USER = "root";
    private static final String PASSWORD = "Aarthi2005";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Enter your name to create an account:");
            scanner.nextLine(); // Clear buffer
            String name = scanner.nextLine();

            System.out.println("Enter your phone number:");
            String phoneNumber = scanner.next();

            int pin;
            while (true) {
                System.out.println("Set a 4-digit PIN number for your account:");
                pin = scanner.nextInt();
                if (String.valueOf(pin).length() == 4) break;
                System.out.println("Invalid PIN! Please enter a valid 4-digit PIN.");
            }

            double balance = 1000; // Default initial balance
            double dailyWithdrawal = 0;

            String insertAccountSQL = "INSERT INTO accounts (name, pin, balance, phone_number, daily_withdrawal) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertAccountSQL, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, name);
                pstmt.setInt(2, pin);
                pstmt.setDouble(3, balance);
                pstmt.setString(4, phoneNumber);
                pstmt.setDouble(5, dailyWithdrawal);
                pstmt.executeUpdate();

                ResultSet rs = pstmt.getGeneratedKeys();
                int accountId = rs.next() ? rs.getInt(1) : 0;

                System.out.println("\nAccount created successfully!");

                while (true) {
                    System.out.println("\nEnter your PIN to access your account:");
                    int enteredPin = scanner.nextInt();

                    if (enteredPin != pin) {
                        System.out.println("Incorrect PIN! Try again.");
                        continue;
                    }

                    System.out.println("Welcome, " + name);
                    while (true) {
                        System.out.println("\nSelect an option:");
                        System.out.println("1. Check Balance");
                        System.out.println("2. Add Amount");
                        System.out.println("3. Withdraw Amount");
                        System.out.println("4. Print Receipt");
                        System.out.println("5. Exit");

                        int option = scanner.nextInt();
                        switch (option) {
                            case 1:
                                System.out.println("Your current balance is: ₹" + balance);
                                break;

                            case 2:
                                System.out.println("Enter the amount to add:");
                                double addAmount = scanner.nextDouble();
                                balance += addAmount;

                                updateDatabase(conn, "UPDATE accounts SET balance = ? WHERE id = ?", balance, accountId);
                                System.out.println("Amount credited. New balance: ₹" + balance);
                                break;

                            case 3:
                                System.out.println("Enter the amount to withdraw:");
                                double withdrawAmount = scanner.nextDouble();

                                if (withdrawAmount + dailyWithdrawal > 100000) {
                                    System.out.println("Daily withdrawal limit of ₹1 lakh exceeded!");
                                    break;
                                }

                                boolean otpVerified = withdrawAmount > 10000 && verifyOTP(scanner);
                                if (withdrawAmount <= balance - 100 && (withdrawAmount <= 10000 || otpVerified)) {
                                    dailyWithdrawal += withdrawAmount;
                                    balance -= withdrawAmount;

                                    updateDatabase(conn, "UPDATE accounts SET balance = ?, daily_withdrawal = ? WHERE id = ?", balance, dailyWithdrawal, accountId);
                                    System.out.println("Amount withdrawn successfully. New balance: ₹" + balance);
                                } else {
                                    System.out.println("Insufficient balance or incorrect OTP.");
                                }
                                break;

                            case 4:
                                printReceipt(name, balance, phoneNumber);
                                break;

                            case 5:
                                System.out.println("Thank you for using SwiftCash ATM, " + name);
                                return;

                            default:
                                System.out.println("Invalid option! Try again.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void updateDatabase(Connection conn, String query, double value1, double value2, int id) throws SQLException {
        try (PreparedStatement updateStmt = conn.prepareStatement(query)) {
            updateStmt.setDouble(1, value1);
            updateStmt.setDouble(2, value2);
            updateStmt.setInt(3, id);
            updateStmt.executeUpdate();
        }
    }

    private static boolean verifyOTP(Scanner scanner) {
        String otp = generateOTP();
        long otpTime = System.currentTimeMillis();
        System.out.println("OTP sent: " + otp);

        System.out.println("Enter OTP (expires in 3 minutes):");
        String enteredOtp = scanner.next();

        if (System.currentTimeMillis() - otpTime > 180000) {
            System.out.println("OTP expired!");
            return false;
        }
        return otp.equals(enteredOtp);
    }

    private static void printReceipt(String name, double balance, String phoneNumber) {
        System.out.println("\nATM Receipt");
        System.out.println("┌───────────────────────────┐");
        System.out.println("│ Name: " + name + "           │");
        System.out.println("│ Balance: ₹" + balance + "      │");
        System.out.println("│ Phone: " + phoneNumber + "   │");
        System.out.println("└───────────────────────────┘");
    }

    private static String generateOTP() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
}
