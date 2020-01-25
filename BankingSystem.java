/**
 * This program interfaces with the CS157A database tables P1.Account and P1.Customer.
 * Database information should be provided in a properties file, loaded with the init() method.
 * Errors are caught, but not checked.
 */

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * Manage connection to database and perform SQL statements.
 */
public class BankingSystem 
{
	// Connection properties
	private static String driver;
	private static String url;
	private static String username;
	private static String password;
	
	// JDBC Objects
	private static Connection con;
	private static Statement stmt;
	private static ResultSet rs;

	/**
	 * Initialize database connection given properties file.
	 * @param filename name of properties file
	 */
	public static void init(String filename) 
    {
		try {
			Properties props = new Properties();					// Create a new Properties object
			FileInputStream input = new FileInputStream(filename);	// Create a new FileInputStream object using our filename parameter
			props.load(input);										// Load the file contents into the Properties object
			driver = props.getProperty("jdbc.driver");				// Load the driver
			url = props.getProperty("jdbc.url");					// Load the url
			username = props.getProperty("jdbc.username");			// Load the username
			password = props.getProperty("jdbc.password");			// Load the password
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Test database connection.
	 */
	public static void testConnection() 
    {
		System.out.println(":: TEST - CONNECTING TO DATABASE");
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement();
			System.out.println(":: TEST - SUCCESSFULLY CONNECTED TO DATABASE");
			} catch (Exception e) {
				System.out.println(":: TEST - FAILED CONNECTED TO DATABASE");
				e.printStackTrace();
			}
	  }

	/**
	 * Create a new customer.
	 * @param name customer name
	 * @param gender customer gender
	 * @param age customer age
	 * @param pin customer pin
	 */
	public static void newCustomer(String name, String gender, String age, String pin) 
	{
        String insertStmt = "INSERT INTO P1.Customer (Name, Gender, Age, Pin) VALUES ('"
            + name + "', '" + gender + "', " + age + ", " + pin + ")";
        String getIdQuery = "SELECT Id From P1.Customer WHERE Name='" + name + "' AND Gender='"
            + gender + "' AND Age=" + age + " AND Pin=" + pin;
        int id = 0;
        try {
            stmt.executeUpdate(insertStmt);
            rs = stmt.executeQuery(getIdQuery);
            while (rs.next())
                id = rs.getInt("Id");
            System.out.println(":: CREATE NEW CUSTOMER - SUCCESS");
            System.out.println("Account ID: " + id);
        } 
        catch (Exception e) {
            System.out.println(":: CREATE NEW CUSTOMER - FAILED");
            e.printStackTrace();
        }
	}

	/**
	 * Open a new account.
	 * @param id customer id
	 * @param type type of account
	 * @param amount initial deposit amount
	 */
	public static void openAccount(String id, String type, String amount) 
	{
        String insertStmt = "INSERT INTO P1.Account (ID, Balance, Type, Status) VALUES ("
            + id + "," + amount + ", '" + type + "', 'A')";
        String getNumberQuery = "SELECT Number FROM P1.Account WHERE Id=" + id + " AND Type='" 
            + type + "' AND Status='A' AND Balance=" + amount;
        int accNumber = 0;
        try {
            stmt.executeUpdate(insertStmt);
            rs = stmt.executeQuery(getNumberQuery);
            while (rs.next())
                accNumber = rs.getInt("Number");
            System.out.println(":: OPEN ACCOUNT - SUCCESS");
            System.out.println("Account number: " + accNumber);
        } 
        catch (Exception e) {
            System.out.println(":: OPEN ACCOUNT - FAILED");
            e.printStackTrace();
        }
	}

	/**
	 * Close an account.
	 * @param accNum account number
	 */
	public static void closeAccount(String accNum) 
	{
        String updateStmt = "UPDATE P1.Account SET Status='I', Balance=0 WHERE Number=" + accNum;
        try {
            stmt.executeUpdate(updateStmt);
            System.out.println(":: CLOSE ACCOUNT - SUCCESS");
        } 
        catch (Exception e) {
            System.out.println(":: CLOSE ACCOUNT - FAILED");
            e.printStackTrace();
        }
	}

	/**
	 * Deposit into an account.
	 * @param accNum account number
	 * @param amount deposit amount
	 */
	public static void deposit(String accNum, String amount) 
	{
        String updateStmt = "UPDATE P1.Account SET Balance=((SELECT Balance FROM P1.Account WHERE Number=" 
            + accNum + ") + " + amount + ") WHERE Number=" + accNum; 
        try {
            stmt.executeUpdate(updateStmt);
            System.out.println(":: DEPOSIT - SUCCESS");
        } 
        catch (Exception e) {
            System.out.println(":: DEPOSIT - FAILED");
            e.printStackTrace();
        }
	}

	/**
	 * Withdraw from an account.
	 * @param accNum account number
	 * @param amount withdraw amount
	 */
	public static void withdraw(String accNum, String amount) 
	{
        String updateStmt = "UPDATE P1.Account SET Balance=((SELECT Balance FROM P1.Account WHERE Number=" 
            + accNum + ") - " + amount + ") WHERE Number=" + accNum; 
        try {
            stmt.executeUpdate(updateStmt);
            System.out.println(":: WITHDRAW - SUCCESS");
        } 
        catch (Exception e) {
            System.out.println(":: WITHDRAW - FAILED");
            e.printStackTrace();
        }
	}

	/**
	 * Transfer amount from source account to destination account. 
	 * @param srcAccNum source account number
	 * @param destAccNum destination account number
	 * @param amount transfer amount
	 */
	public static void transfer(String srcAccNum, String destAccNum, String amount) 
	{
        String withdrawStmt = "UPDATE P1.Account SET Balance=((SELECT Balance FROM P1.Account WHERE Number=" 
            + srcAccNum + ") - " + amount + ") WHERE Number=" + srcAccNum; 
        String depositStmt = "UPDATE P1.Account SET Balance=((SELECT Balance FROM P1.Account WHERE Number=" 
            + destAccNum + ") + " + amount + ") WHERE Number=" + destAccNum; 
        try {
            stmt.executeUpdate(withdrawStmt);
            stmt.executeUpdate(depositStmt);
            System.out.println(":: TRANSFER - SUCCESS");
        } 
        catch (Exception e) {
            System.out.println(":: TRANSFER - FAILED");
            e.printStackTrace();
        }
	}

	/**
	 * Display account summary.
	 * @param cusID customer ID
	 */
	public static void accountSummary(String cusID) 
	{
        String totalBalanceQuery = "SELECT Total FROM Total WHERE Id=" + cusID; 
        String accountSummaryQuery = "SELECT Number, Balance FROM P1.Account WHERE ID=" + cusID + " AND Status='A'";
        int totalBalance = 0, accNumber, balance;
        String out = "";
        try {
            rs = stmt.executeQuery(totalBalanceQuery);
            while (rs.next())
                totalBalance = rs.getInt("Total");
            rs = stmt.executeQuery(accountSummaryQuery);
            while (rs.next()) {
                accNumber = rs.getInt("Number");
                balance = rs.getInt("Balance");
                out += accNumber + "\t\t" + balance + "\t\n";
            }
            if (out.length() == 0) // out.length() is only 0 when the user has no open accounts
                System.out.println("User with ID " + cusID + " has no open accounts.");
            else {
                out = out.substring(0, out.length() - 2); //remove last newline
                System.out.println(":: ACCOUNT SUMMARY - SUCCESS");
                System.out.println("Account\t\tBalance");
                System.out.println(out);
                System.out.println("Total balance: " + totalBalance);
            }
        } 
        catch (Exception e) {
            System.out.println(":: ACCOUNT SUMMARY - FAILED");
            e.printStackTrace();
        }
	}

	/**
	 * Display Report A - Customer Information with Total Balance in Decreasing Order.
	 */
	public static void reportA() 
	{
        String reportAQuery1 = "SELECT Total, Id, Name, Age, Gender FROM Total ORDER BY Total DESC";
        String reportAQuery2 = "SELECT DISTINCT C.Id, Name, Age, Gender FROM P1.Customer AS C, "
            + "P1.Account AS A WHERE C.Id NOT IN (SELECT Id FROM P1.Account)";
        int id, age, totalBalance;
        String name, gender, out = "Id\tName\t\tAge\tGender\tTotal Balance\n";
        try {
            rs = stmt.executeQuery(reportAQuery1);
            while (rs.next()) {
                id = rs.getInt("Id");
                name = rs.getString("Name");
                age = rs.getInt("Age");
                gender = rs.getString("Gender");
                totalBalance = rs.getInt("Total");
                out += id + "\t" + name+ "\t\t" + age + "\t" + gender + "\t" + totalBalance + "\t\n";
            }
            rs = stmt.executeQuery(reportAQuery2);
            while (rs.next()) {
                id = rs.getInt("Id");
                name = rs.getString("Name");
                age = rs.getInt("Age");
                gender = rs.getString("Gender");
                out += id + "\t" + name+ "\t\t" + age + "\t" + gender + "\t0 \n";
            }
            out = out.substring(0, out.length() - 2); //remove last newline
            System.out.println(":: REPORT A - SUCCESS");
            System.out.println(out);
        } 
        catch (Exception e) {
            System.out.println(":: REPORT A - FAILED");
            e.printStackTrace();
        }
	}

	/**
	 * Display Report B - Find the Average Total Balance Between Age Groups
	 * @param min minimum age
	 * @param max maximum age
	 */
	public static void reportB(String min, String max) 
	{
        String reportBQuery = "SELECT AVG(Total) AS \"Average Balance\" FROM Total WHERE Age>=" + min + " AND Age<=" + max;
        int avgBalance = 0;
        try {
            rs = stmt.executeQuery(reportBQuery);
            while (rs.next())
                avgBalance = rs.getInt("Average Balance");
            System.out.println(":: REPORT B - SUCCESS");
            System.out.println("Average balance from customers ages " + min + " to " + max + ": " + avgBalance);
        } 
        catch (Exception e) {
            System.out.println(":: REPORT B - FAILED");
            e.printStackTrace();
        }
	}

    /**
     * Checks if ID/PIN combo exists in P1.Customer table in database
     * @param id customer id to check
     * @param ping corresponding pin for given id
     * @return true if a user exists with given id/pin combo
     */
    public static boolean authenticate(int id, int pin)
    {
        String authenticateQuery = "SELECT Pin FROM P1.Customer WHERE Id=" + id;
        int dbPin = -1;
        try {
            rs = stmt.executeQuery(authenticateQuery);
            while (rs.next())
                dbPin = rs.getInt("Pin");
        } 
        catch (Exception e) {
            return false;
        }
        return pin == dbPin;
    }

    /**
     * Finds the ID of the account owner.
     * @param accNum account number to find owner of
     * @return owner's ID
     */
    public static int getAccOwner(int accNum)
    {
        String getOwnerIdQuery = "SELECT Id FROM P1.Account WHERE Number=" + accNum + " AND Status='A'";
        int id = -1;
        try {
            rs = stmt.executeQuery(getOwnerIdQuery);
            while (rs.next())
                id = rs.getInt("Id");
        } 
        catch (Exception e) {
            System.out.println("Error: database query failed.");
            e.printStackTrace();
        }
        return id;
    }

    /**
     * Checks if an account with the given account number exists.
     * @param accNum account number to check for
     * @return true if account exists
     */
    public static boolean accountExists(int accNum)
    {
        String checkIfExistsQuery = "SELECT Id FROM P1.Account WHERE Number=" + accNum + " AND Status='A'";
        int id = -1;
        boolean exists = false;
        try {
            rs = stmt.executeQuery(checkIfExistsQuery);
            while (rs.next())
                id = rs.getInt("Id");
        } 
        catch (Exception e) {
            System.out.println("Error: database query failed.");
            e.printStackTrace();
        }
        if (id != -1)
            exists = true;
        return exists;
    }

    /**
     * Checks if a customer with the given ID exists.
     * @param id ID to check for
     * @return true if customer exists
     */
    public static boolean idExists(int id)
    {
        String checkIfExistsQuery = "SELECT Id FROM P1.Customer WHERE Id=" + id;
        int idVar = -1;
        boolean exists = false;
        try {
            rs = stmt.executeQuery(checkIfExistsQuery);
            while (rs.next())
                idVar = rs.getInt("Id");
        } 
        catch (Exception e) {
            System.out.println("Error: database query failed.");
            e.printStackTrace();
        }
        if (idVar != -1)
            exists = true;
        return exists;
    }

    /**
     * Closes database connection
     */
    public static void disconnect()
    {
        try {
            con.close();
        }
        catch (Exception e) {
            System.out.println(":: Disconnect Failed");
        }
    }
}
