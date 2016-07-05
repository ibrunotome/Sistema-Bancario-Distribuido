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

/**
 * @author Bruno Tomé
 * @author Cláudio Menezes
 * @since 03/07/2016
 */
public class UserScreen {

    private ServerBank server = new ServerBank();
    private Account theUser = new Account();
    private JChannel channel;

    // GUI variables
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel statusLabel;
    private JPanel controlPanel;

    public UserScreen() {

    }

    public void prepareGUI() {
        this.mainFrame = new JFrame("BCBank");
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

    private void showMenu() {
        this.headerLabel.setText("MENU");
        JButton transferButton = new JButton("Transferir");
        transferButton.addActionListener(e -> {
            this.controlPanel.removeAll();
            this.mainFrame.setVisible(false);
            this.statusLabel.setVisible(false);
            this.transference();
        });

        JButton showBalanceButton = new JButton("Meu Saldo");
        showBalanceButton.addActionListener(e -> {
            this.controlPanel.removeAll();
            this.mainFrame.setVisible(false);
            this.statusLabel.setVisible(false);
            this.getBalance();
        });

        JButton extract = new JButton("Extrato");
        extract.addActionListener(e -> {
            this.controlPanel.removeAll();
            this.mainFrame.setVisible(false);
            this.statusLabel.setVisible(false);
            this.getBalance();
        });

        JButton totalBankMoney = new JButton("Soma");
        totalBankMoney.addActionListener(e -> {
            this.controlPanel.removeAll();
            this.mainFrame.setVisible(false);
            this.statusLabel.setVisible(false);
            this.totalBankMoney();
        });

        JButton exit = new JButton("Sair");
        exit.addActionListener(e -> System.exit(0));

        this.controlPanel.add(transferButton);
        this.controlPanel.add(showBalanceButton);
        this.controlPanel.add(extract);
        this.controlPanel.add(totalBankMoney);
        this.controlPanel.add(exit);
        this.mainFrame.setVisible(true);
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
            accountAux = server.login(accountAux);
            messageAlertTag = accountAux != null ? MessageAlertTag.LOGIN_SUCCESSFUL : MessageAlertTag.LOGIN_ERROR;
            this.statusLabel.setText(MessageAlert.toString(messageAlertTag));
            if (messageAlertTag == MessageAlertTag.LOGIN_SUCCESSFUL) {
                this.controlPanel.removeAll();
                this.mainFrame.setVisible(false);
                this.statusLabel.setVisible(false);
                this.theUser = accountAux;
                this.showMenu();
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
    private void transference() {
        this.headerLabel.setText("TRANSFERÊNCIA");
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

        JButton menu = new JButton("Menu");
        menu.addActionListener(e -> {
            this.controlPanel.removeAll();
            this.mainFrame.setVisible(false);
            this.statusLabel.setVisible(false);
            this.showMenu();
        });

        this.controlPanel.add(toAccountLabel);
        this.controlPanel.add(toAccount);
        this.controlPanel.add(amountLabel);
        this.controlPanel.add(amount);
        this.controlPanel.add(transferButton);
        this.controlPanel.add(menu);
        this.mainFrame.setVisible(true);
    }

    /**
     * Get the balance of the user account
     */
    private void getBalance() {
        this.headerLabel.setText(this.server.getBalance(this.theUser));
        JButton menu = new JButton("Menu");
        menu.addActionListener(e -> {
            this.controlPanel.removeAll();
            this.mainFrame.setVisible(false);
            this.statusLabel.setVisible(false);
            this.showMenu();
        });
        this.controlPanel.add(menu);
        this.mainFrame.setVisible(true);
    }

    private void totalBankMoney() {
        this.headerLabel.setText("Todas as contas e a soma total");
        JTextArea textArea = new JTextArea(5, 30);
        textArea.setEditable(false);
        textArea.setText(this.server.toString());
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        JButton menu = new JButton("Menu");
        menu.addActionListener(e -> {
            this.controlPanel.removeAll();
            this.mainFrame.setVisible(false);
            this.statusLabel.setVisible(false);
            this.showMenu();
        });
        this.controlPanel.add(scroll);
        this.controlPanel.add(menu);
        this.mainFrame.setVisible(true);
    }

}
