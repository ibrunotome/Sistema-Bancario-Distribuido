package controllers;

import models.Account;
import models.Bank;
import models.MessageAlertTag;

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
     * @return String
     */
    public MessageAlertTag login(Account a) {
        Account accountAux = this.BCBank.getAllAccounts().get(a.getAccountNumber());
        if (accountAux != null && accountAux.getPassword().equals(a.getPassword())) {
            return MessageAlertTag.LOGIN_SUCCESSFUL;
        } else {
            return MessageAlertTag.LOGIN_ERROR;
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
            if (byUser.getBalance() >= amount) {
                this.BCBank.transference(byUser, toUserAux, amount);
                return MessageAlertTag.TRANSFER_SUCCESSFUL;
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
}
