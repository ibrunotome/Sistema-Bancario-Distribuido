package views;

import controllers.MessageAlert;
import controllers.ServerBank;
import models.Account;
import models.MessageAlertTag;
import org.jgroups.JChannel;

import javax.swing.*;
import java.awt.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Scanner;

/**
 * @author Bruno Tomé
 * @author Cláudio Menezes
 * @since 03/07/2016
 */
public class UserScreen {

    private ServerBank server = new ServerBank();
    private Account theUser = new Account();
    private JChannel channel;

    // JSwing variables
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel statusLabel;
    private JPanel controlPanel;

    public UserScreen() {

    }

    public void prepareGUI() {
        this.mainFrame = new JFrame("Login BCBank");
        this.mainFrame.setSize(400, 400);
        this.mainFrame.setLayout(new GridLayout(3, 1));
        this.mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        this.headerLabel = new JLabel("", JLabel.CENTER);
        this.statusLabel = new JLabel("", JLabel.CENTER);
        this.statusLabel.setSize(350, 100);
        this.controlPanel = new JPanel();
        this.controlPanel.setLayout(new FlowLayout());
        this.mainFrame.add(this.headerLabel);
        this.mainFrame.add(this.controlPanel);
        this.mainFrame.add(this.statusLabel);
        this.mainFrame.setLocationRelativeTo(null);
        this.mainFrame.setVisible(true);
        this.mainFrame.setResizable(false);
    }

    /**
     * Try to login into the system
     */
    public void login() {
        this.headerLabel.setText("Digite o número da conta e senha para login");
        JLabel namelabel = new JLabel("Nº Conta: ", JLabel.LEFT);
        JLabel passwordLabel = new JLabel("     Senha: ", JLabel.LEFT);
        final JTextField accountNumber = new JTextField(25);
        final JPasswordField passwordText = new JPasswordField(25);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            MessageAlertTag messageAlertTag;
            Account accountAux = new Account();
            accountAux.setAccountNumber(Integer.parseInt(accountNumber.getText()));
            accountAux.setPassword(passwordText.getText());
            System.out.println(accountAux.getAccountNumber());
            System.out.println(accountAux.getPassword());
            accountAux = server.login(accountAux);
            messageAlertTag = accountAux != null ? MessageAlertTag.LOGIN_SUCCESSFUL : MessageAlertTag.LOGIN_ERROR;
            this.statusLabel.setText(MessageAlert.toString(messageAlertTag));
            if (messageAlertTag == MessageAlertTag.LOGIN_SUCCESSFUL) {
                this.controlPanel.removeAll();
                this.mainFrame.setVisible(false);
                this.statusLabel.setVisible(false);
                this.theUser = accountAux;
                this.transference();
            }
        });

        this.controlPanel.add(namelabel);
        this.controlPanel.add(accountNumber);
        this.controlPanel.add(passwordLabel);
        this.controlPanel.add(passwordText);
        this.controlPanel.add(loginButton);
        this.mainFrame.setVisible(true);
    }

    /**
     * Transfer an amount of cash between two accounts
     */
    public void transference() {
        this.headerLabel.setText("Digite o número da conta e o valor à transferir");
        JLabel toAccountLabel = new JLabel("Para a conta nº: ", JLabel.LEFT);
        JLabel amountLabel = new JLabel("               Valor: ", JLabel.LEFT);
        final JTextField toAccount = new JTextField(20);
        final JTextField amount = new JTextField(20);

        JButton transferButton = new JButton("Transferir");
        transferButton.addActionListener(e -> {
            MessageAlertTag messageAlertTag;
            messageAlertTag = this.server.transference(this.theUser, Integer.parseInt(toAccount.getText()),
                    Double.parseDouble(amount.getText()));
            this.statusLabel.setText(MessageAlert.toString(messageAlertTag));
            this.statusLabel.setVisible(true);
        });

        this.controlPanel.add(toAccountLabel);
        this.controlPanel.add(toAccount);
        this.controlPanel.add(amountLabel);
        this.controlPanel.add(amount);
        this.controlPanel.add(transferButton);
        this.mainFrame.setVisible(true);
    }

//    /**
//     * Try to login into the system
//     */
//    public void login() {
//        int accountNumber;
//        String password;
//        Scanner keyboard = new Scanner(System.in);
//        Account accountAux = new Account();
//        MessageAlertTag messageAlertTag;
//        do {
//            System.out.print("Número da conta: ");
//            accountNumber = Integer.parseInt(keyboard.nextLine());
//            System.out.print("Senha: ");
//            password = keyboard.nextLine();
//
//            messageAlertTag = server.login(accountAux);
//            System.out.println(MessageAlert.toString(messageAlertTag));
//        } while (messageAlertTag != MessageAlertTag.LOGIN_SUCCESSFUL);
//
//        keyboard.close();
//    }

//    /**
//     * Transfer an amount of cash between two accounts
//     */
//    public void transference() {
//        int toAccount;
//        Double amount;
//        Scanner keyboard = new Scanner(System.in);
//        System.out.println("Digite o número da conta para transferência: ");
//        toAccount = keyboard.nextInt();
//        System.out.println("Digite o valor a ser transferido: ");
//        amount = keyboard.nextDouble();
//        this.server.transference(this.theUser, toAccount, amount);
//    }

    /**
     * Get the balance of the user account
     */
    public void getBalance() {
        System.out.println(this.server.getBalance(this.theUser));
    }

}
