package views;

import models.Account;

import java.util.Scanner;

/**
 * @author Bruno Tomé
 * @author Cláudio Menezes
 * @since 03/07/2016
 */
public class UserScreen {

    public void login() {
        String username;
        String password;
        Scanner keyboard = new Scanner(System.in);

        System.out.println("Usuário: ");
        username = keyboard.nextLine();
        System.out.println("Senha: ");
        password = keyboard.nextLine();

        Account accountAux = new Account();
        accountAux.setUsername(username);
        accountAux.setPassword(password);
        keyboard.close();
    }

    public void transference() {
        String toAccount;
        Double ammount;
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Digite o número da conta para transferência: ");
        toAccount = keyboard.nextLine();
        System.out.println("Digite o valor a ser transferido: ");
        ammount = keyboard.nextDouble();
    }

    public void getBalance(Account userAccount) {
        System.out.println("Seu saldo atual é: " + userAccount.getBalance());
    }

}
