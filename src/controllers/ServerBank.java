package controllers;

import models.Account;
import models.Bank;

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
    public String login(Account a) {
        Account accountAux = this.BCBank.getAllAccounts().get(a.getAccountNumber());
        if (accountAux != null && accountAux.getPassword().equals(a.getPassword())) {
            return "Login efetuado com sucesso";
        } else {
            return "Conta ou senha inválida";
        }
    }

    /**
     * Transfer an amount of cash between two accounts
     *
     * @param byUser
     * @param toUser
     * @param amount
     */
    public String transference(Account byUser, int toUser, Double amount) {
        Account toUserAux = this.BCBank.getAllAccounts().get(toUser);
        if (toUserAux != null) {
            if (byUser.getBalance() >= amount) {
                this.BCBank.transference(byUser, toUserAux, amount);
                return "Transferência realizada com sucesso";
            } else {
                return "ERRO: Saldo insuficiente";
            }
        } else {
            return "ERRO: Conta inexistente";
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
