package views;

import controllers.MessageAlert;
import controllers.ServerBank;
import models.Account;
import models.MessageAlertTag;
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
        Account accountAux = new Account();
        MessageAlertTag messageAlertTag;
        do {
            System.out.print("Número da conta: ");
            accountNumber = Integer.parseInt(keyboard.nextLine());
            System.out.print("Senha: ");
            password = keyboard.nextLine();
            accountAux.setAccountNumber(accountNumber);
            accountAux.setPassword(password);
            messageAlertTag = server.login(accountAux);
            System.out.println(MessageAlert.toString(messageAlertTag));
        } while (messageAlertTag != MessageAlertTag.LOGIN_SUCCESSFUL);

        keyboard.close();
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
