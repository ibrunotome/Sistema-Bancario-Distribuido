package controllers;

import models.Account;
import models.Bank;

/**
 * Created by iBrunoTome on 7/3/16.
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
     * @param a1
     * @param a2
     * @param amount
     */
    public String transference(Account a1, Account a2, Double amount) {
        Account accountAux = this.BCBank.getAllAccounts().get(a2.getAccountNumber());
        if (accountAux != null) {
            if (a1.getBalance() >= amount) {
                this.BCBank.transference(a1, a2, amount);
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
