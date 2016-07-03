package models;

import java.util.Hashtable;

/**
 * @author Bruno Tomé
 * @author Cláudio Menezes
 * @since 03/07/2016
 */
public class Bank {
    private final Double totalCash = 10000.0;
    private final int totalAccounts = 10;
    private Hashtable<Integer, Account> allAccounts = new Hashtable();

    public Bank() {
        initalizeBank();
    }

    /**
     * Initialize the bank if there isn' an object bank serialized
     */
    private void initalizeBank() {
        for (int i = 0; i < this.totalAccounts; i++) {
            Account a = new Account();
            a.setAccountNumber(i);
            a.setUsername("user" + i);
            a.setPassword("pass" + i);
            a.setBalance(this.totalCash / this.totalAccounts);
            this.allAccounts.put(a.getAccountNumber(), a);
        }
    }

    /**
     * Transfer an amount of cash between two accounts
     *
     * @param a1
     * @param a2
     * @param amount
     */
    public void transference(Account a1, Account a2, Double amount) {
        a1.setBalance(a1.getBalance() - amount);
        a2.setBalance(a2.getBalance() + amount);

        this.allAccounts.replace(a1.getAccountNumber(), a1);
        this.allAccounts.replace(a2.getAccountNumber(), a2);
    }

    /**
     * Get the balance of an account
     *
     * @param a
     * @return String
     */
    public String getBalance(Account a) {
        return "O saldo da conta " + a.getAccountNumber() + " é: R$" + a.getBalance();
    }

    /**
     * Add an account to allAccounts
     *
     * @param a
     */
    public void addAccount(Account a) {
        this.allAccounts.put(a.getAccountNumber(), a);
    }

    public Hashtable<Integer, Account> getAllAccounts() {
        return this.allAccounts;
    }
}
