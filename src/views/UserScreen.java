package views;

import controllers.ServerBank;
import models.Account;
import org.jgroups.JChannel;

import java.util.Scanner;

/**
 * @author Bruno Tomé
 * @author Cláudio Menezes
 * @since 03/07/2016
 */
public class UserScreen {

    ServerBank server = new ServerBank();
    Account theUser = new Account();
    JChannel channel;

    /**
     * Try to login into the system
     */
    public void login() {
        int accountNumber;
        String password;
        Scanner keyboard = new Scanner(System.in);

        System.out.println("Número da conta: ");
        accountNumber = keyboard.nextInt();
        System.out.println("Senha: ");
        password = keyboard.nextLine();

        Account accountAux = new Account();
        accountAux.setAccountNumber(accountNumber);
        accountAux.setPassword(password);
        keyboard.close();

        System.out.println(server.login(accountAux));
    }

    /**
     * Transfer an amount of cash between two accounts
     */
    public void transference() {
        int toAccount;
        Double amount;
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Digite o número da conta para transferência: ");
        toAccount = keyboard.nextInt();
        System.out.println("Digite o valor a ser transferido: ");
        amount = keyboard.nextDouble();
        this.server.transference(this.theUser, toAccount, amount);
    }

    /**
     * Get the balance of the user account
     */
    public void getBalance() {
        System.out.println(this.server.getBalance(this.theUser));
    }

}
