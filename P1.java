import java.util.Scanner;

/**
 * This class provides a command line interface for interacting with the Self Service Banking System
 */
public class P1 {

    private static Scanner in;
    private static int id = -1, pin = -1, cmd = -1;
    private static final String err = "Error: There was a problem understanding your input. Please try again.";

    /**
     * The entry point into the program
     */
	public static void main(String argv[])
    {
		if (argv.length < 1) {
			System.out.println("Need database properties filename");
            return;
		} 
        else {
			BankingSystem.init(argv[0]);
			BankingSystem.testConnection();
		}
        boolean done = false;
        in = new Scanner(System.in);
        while (!done)
        {
            System.out.println("Welcome to the Self Service Banking System!"
                    + "\n1. New Customer"
                    + "\n2. Customer Login"
                    + "\n3. Exit");
            try {
                cmd = in.nextInt();
            }
            catch (Exception e) {
                in.next();
                System.out.println(err);
                continue;
            }
            switch (cmd) 
            {
                case 1:
                    System.out.println();
                    newCustomer();
                    break;
                case 2:
                    try {
                        System.out.print("Customer ID: ");
                        id = in.nextInt();
                        System.out.print("Pin: ");
                        pin = in.nextInt();
                    }
                    catch (Exception e) {
                        System.out.println(err);
                    }
                    if (id == 0 && pin == 0) { // 0/0 ID/PIN combo hardcoded for admin menu
                        adminMenu();
                        break;
                    }
                    if (!BankingSystem.authenticate(id, pin)) { // checks if ID/PIN combo is valid
                        System.out.println("Error: No customer found with this ID/PIN combo. Please try again.\n");
                        continue;
                    }
                    else {
                        customerMenu();
                    }
                    break;
                case 3:
                    System.out.println("Thank you for using the Self Service Banking System. Goodbye!");
                    done = true;
                    break;
                default:
                    System.out.println(err);
                    break;
            } // end switch
            System.out.println();
        } // end while
        in.close();
        BankingSystem.disconnect();
	} // end main

    /**
     * Takes necessary input from user to create a new user. 
     * Error checking is done
     */
    private static void newCustomer()
    {
        String name, gender, age, npin;
        try 
        {
            System.out.print("Please enter your name: ");
            name = in.next();
            System.out.print("Please enter your gender (m/f): ");
            gender = in.next();
            gender = gender.toUpperCase();
            if (!(gender.equals("M") || gender.equals("F"))) { // only male and female are allowed
                System.out.println(err);
                return;
            }
            System.out.print("Please enter your age: ");
            age = in.next();
            if (Integer.parseInt(age) < 0) { // age can not be negative
                System.out.println(err);
                return;
            }
            System.out.print("Please enter a pin for your account: ");
            npin = in.next();
            if (Integer.parseInt(npin) < 0) { // pin can not be negative
                System.out.println(err);
                return;
            }
        }
        catch (Exception e) {
            System.out.println(err);
            return;
        }
        BankingSystem.newCustomer(name, gender, age, npin);
    }

    /**
     * Provides the primary menu for customers to interact with the banking system once logged in.
     */
    private static void customerMenu()
    {
        boolean done = false;
        while (!done)
        {
            System.out.println();
            System.out.println("Customer Main Menu"
                    + "\n1. Open Account"
                    + "\n2. Close Account"
                    + "\n3. Deposit"
                    + "\n4. Withdraw"
                    + "\n5. Transfer"
                    + "\n6. Account Summary"
                    + "\n7. Exit");
            try {
                cmd = in.nextInt();
            }
            catch (Exception e) {
                in.next();
                System.out.println(err);
                continue;
            }
            switch (cmd) 
            {
                case 1:
                    openAccount();
                    break;
                case 2:
                    closeAccount();
                    break;
                case 3:
                    deposit();
                    break;
                case 4:
                    withdraw();
                    break;
                case 5:
                    transfer();
                    break;
                case 6:
                    BankingSystem.accountSummary(String.valueOf(id));
                    break;
                case 7:
                    System.out.println("Logging you out.");
                    done = true;
                    break;
                default:
                    System.out.println(err);
                    break;
            } // end switch
        } // end while
    } // end customerMenu()

    /**
     * Provides the primary menu for administrators to interact with the banking system once logged in.
     */
    private static void adminMenu()
    {
        boolean done = false;
        int idToCheck = -1, min = -1, max = -1;
        while (!done)
        {
            System.out.println();
            System.out.println("Administrator Main Menu"
                    + "\n1. Account Summary for a Customer"
                    + "\n2. Report A :: Customer Information with Total Balance in Decreasing Order"
                    + "\n3. Report B :: Find the Average Total Balance Between Age Groups"
                    + "\n4. Exit");
            try {
                cmd = in.nextInt();
            }
            catch (Exception e) {
                in.next();
                System.out.println(err);
                continue;
            }
            switch (cmd)
            {
                case 1:
                    try {
                        System.out.print("Enter customer ID: ");
                        idToCheck = in.nextInt();
                        if (!BankingSystem.idExists(idToCheck)) {
                            System.out.println("Error: Invalid customer ID.");
                            break;
                        }
                    }
                    catch (Exception e) {
                        System.out.println(err);
                        break;
                    }
                    BankingSystem.accountSummary(String.valueOf(idToCheck));
                    break;
                case 2:
                    BankingSystem.reportA();
                    break;
                case 3:
                    try {
                        System.out.print("Enter minimum age to get average of: ");
                        min = in.nextInt();
                        System.out.print("Enter maximum age to get average of: ");
                        max = in.nextInt();
                    }
                    catch (Exception e) {
                        System.out.println(err);
                        break;
                    }
                    if (min < 0 || max < 0) {
                        System.out.println("Error: You can not have a negative age.");
                        break;
                    }
                    if (min > max) {
                        System.out.println("Error: minimum age should be smaller than the maximum.");
                        break;
                    }
                    BankingSystem.reportB(String.valueOf(min), String.valueOf(max));
                    break;
                case 4:
                    System.out.println("Logging you out.");
                    done = true;
                    break;
                default:
                    System.out.println(err);
                    break;
            } // end switch
        } //end while
    } // end adminMenu()

    /**
     * Takes user input needed for customers to open a new account.
     * Error checking is done.
     */
    private static void openAccount()
    {
        int newId = 0, initialDeposit = 0;
        String type = "";
        try 
        {
            System.out.print("Enter customer ID for new account: ");
            newId = in.nextInt();
            if (!BankingSystem.idExists(newId)) { // customer id must exist to open account for it
                System.out.println("Error: invalid customer ID.");
                return;
            }
            System.out.print("Enter the account type (c)hecking or (s)avings: ");
            type = in.next();
            type = type.toUpperCase();
            if (!(type.equals("C") || type.equals("S"))) { // type must be C for checking or S for savings
                System.out.println(err);
                return;
            }
            System.out.print("Enter the amount for your initial deposit: ");
            initialDeposit = in.nextInt();
            if (initialDeposit < 0) { // can not open account with negative balance
                System.out.println("Error: you can not open an account with a negative balance.");
                return;
            }
        }
        catch (Exception e) {
            System.out.println(err);
            return;
        }
        BankingSystem.openAccount(String.valueOf(newId), type, String.valueOf(initialDeposit));
    }

    /**
     * Takes user input needed for customers to close a new account.
     * Error checking is done.
     */
    private static void closeAccount()
    {
        int accNum = 0;
        try {
            System.out.print("Enter account number to close: ");
            accNum = in.nextInt();
        }
        catch (Exception e) {
            System.out.println(err);
            return;
        }
        if (!BankingSystem.accountExists(accNum)) { // the acount must exist to be closed
            System.out.println("Error: There is no active account with that account number.");
            return;
        }
        if (BankingSystem.getAccOwner(accNum) != id || id == -1) { // only an owner of an account can close it, and user must be logged in
            System.out.println("Error: Only the owner of an account can close it.");
            return;
        }
        BankingSystem.closeAccount(String.valueOf(accNum));
    }

    /**
     * Takes user input needed for customers to deposit funds.
     * Error checking is done.
     */
    private static void deposit()
    {
        int accNum = -1, amount = 0;
        try 
        {
            System.out.print("Enter account number: ");
            accNum = in.nextInt();
            if (!BankingSystem.accountExists(accNum)) { // account must exist
                System.out.println("Error: There is no active account with that account number.");
                return;
            }
            System.out.print("Enter amount to deposit: ");
            amount = in.nextInt();
            if (amount < 0) { // can not deposit a negative amount
                System.out.println("Error: You can not deposit a negative number.");
                return;
            }
        }
        catch (Exception e) {
            System.out.println(err);
            return;
        }
        BankingSystem.deposit(String.valueOf(accNum), String.valueOf(amount));
    }

    /**
     * Takes user input needed for customers to withdraw funds.
     * Error checking is done.
     */
    private static void withdraw()
    {
        int accNum = -1, amount = 0;
        try 
        {
            System.out.print("Enter account number: ");
            accNum = in.nextInt();
            if (!BankingSystem.accountExists(accNum)) { // account must exist
                System.out.println("Error: There is no active account with that account number.");
                return;
            }
            if (BankingSystem.getAccOwner(accNum) != id) { // You can only withdraw funds from your own account
                System.out.println("Error: You must own an account to withdraw funds from it.");
                return;
            }
            System.out.print("Enter amount to withdraw: ");
            amount = in.nextInt();
            if (amount < 0) { // can not withdraw a negative amount
                System.out.println("Error: You can not withdraw a negative number.");
                return;
            }
        }
        catch (Exception e) {
            System.out.println(err);
            return;
        }
        BankingSystem.withdraw(String.valueOf(accNum), String.valueOf(amount));
    }

    /**
     * Takes user input needed for customers to transfer funds.
     * Error checking is done.
     */
    private static void transfer()
    {
        int accSrc = -1, accDest = -1, amount = 0;
        try 
        {
            System.out.print("Enter account number to transfer from: ");
            accSrc = in.nextInt();
            if (!BankingSystem.accountExists(accSrc)) { // account must exist
                System.out.println("Error: There is no active account with that account number.");
                return;
            }
            if (BankingSystem.getAccOwner(accSrc) != id) { // You can only withdraw funds from your own account
                System.out.println("Error: You must own an account to transfer funds from it.");
                return;
            }
            System.out.print("Enter account number to transfer to: ");
            accDest = in.nextInt();
            if (!BankingSystem.accountExists(accDest)) { // account must exist
                System.out.println("Error: There is no active account with that account number.");
                return;
            }
            System.out.print("Enter amount to transfer: ");
            amount = in.nextInt();
            if (amount < 0) {
                System.out.println("Error: Can not transfer negative funds.");
                return;
            }
        }
        catch (Exception e) {
            System.out.println(err);
            return;
        }
        BankingSystem.transfer(String.valueOf(accSrc), String.valueOf(accDest), String.valueOf(amount));
    }

}

