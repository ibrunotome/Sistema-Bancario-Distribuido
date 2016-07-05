package controllers;

import models.Account;
import models.Bank;
import models.MessageAlertTag;

import java.util.Hashtable;

/**
 * @author Bruno Tomé
 * @author Cláudio Menezes
 * @since 03/07/2016
 */
public class ServerBank {

    private Bank BCBank = new Bank();

    /**
     * Try to login into the system
     *
     * @param a
     * @return Account
     */
    public Account login(Account a) {
        Account accountAux = this.BCBank.getAllAccounts().get(a.getAccountNumber());
        if (accountAux != null && accountAux.getPassword().equals(a.getPassword())) {
            return accountAux;
        } else {
            return null;
        }
    }

    /**
     * Transfer an amount of cash between two accounts
     *
     * @param byUser
     * @param toUser
     * @param amount
     */
    public MessageAlertTag transference(Account byUser, int toUser, Double amount) {
        Account toUserAux = this.BCBank.getAllAccounts().get(toUser);
        if (toUserAux != null) {
            if (byUser.getBalance() >= amount && amount > 0) {
                this.BCBank.transference(byUser, toUserAux, amount);
                // Add a new transfer to extract of byUser
                byUser.addToExtract("\n----------------------------\nSAÍDA\n"
                        + "----------------------------\nPara a conta nº "
                        + toUser + "\nValor: R$ " + amount + "\nMeu novo saldo: R$ "
                        + (byUser.getBalance()) + "\n----------------------------\n");
                // Add a new transfer to extract of toUser
                toUserAux.addToExtract("\n----------------------------\nENTRADA\n"
                        + "----------------------------\nProvindo da conta nº "
                        + byUser.getAccountNumber() + "\nValor: R$ " + amount + "\nMeu novo saldo: R$ "
                        + (toUserAux.getBalance()) + "\n----------------------------\n");

                // Update the allAccounts of BCBank
                Hashtable<Integer, Account> allAccounts;
                allAccounts = this.BCBank.getAllAccounts();
                allAccounts.replace(byUser.getAccountNumber(), byUser);
                allAccounts.replace(toUserAux.getAccountNumber(), toUserAux);
                this.BCBank.setAllAccounts(allAccounts);
                return MessageAlertTag.TRANSFER_SUCCESSFUL;
            } else if (amount <= 0) {
                return MessageAlertTag.TRANSFER_ERROR_NEGATIVE;
            } else {
                return MessageAlertTag.TRANSFER_ERROR_AMOUNT;
            }
        } else {
            return MessageAlertTag.TRANSFER_ERROR_ACCOUNT;
        }
    }

    /**
     * Get the balance of an account
     *
     * @param a
     * @return String
     */
    public String getBalance(Account a) {
        return this.BCBank.getBalance(a);
    }

    @Override
    public String toString() {
        return this.BCBank.toString();
    }
}
