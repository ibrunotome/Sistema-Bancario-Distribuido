package views;

import controllers.MessageAlert;
import controllers.ServerBank;
import models.Account;
import models.Data;
import models.MessageAlertTag;
import models.ProtocolTag;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

import javax.swing.*;
import java.awt.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Class to show a GUI for users of BCBank
 *
 * @author Bruno Tomé
 * @author Cláudio Menezes
 * @since 03/07/2016
 */
public class UserScreen extends ReceiverAdapter {

    private ServerBank server = new ServerBank();
    private Account theUser = new Account();
    private JChannel channel;

    // GUI variables
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel statusLabel;
    private JPanel controlPanel;

    public UserScreen() throws Exception {
        this.start();
    }

    /**
     * Prepare the initial GUI, set the mainFrame that will be used
     * for put any other kind of graphic element
     */
    public void prepareGUI() {
        this.mainFrame = new JFrame("BCBank");
        this.mainFrame.setSize(400, 500);
        this.mainFrame.setLayout(new GridLayout(3, 1));
        this.mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        this.headerLabel = new JLabel("", JLabel.CENTER);
        this.statusLabel = new JLabel("", JLabel.CENTER);
        this.controlPanel = new JPanel();
        this.controlPanel.setLayout(new FlowLayout());
        this.statusLabel.setSize(350, 100);
        this.statusLabel.setVisible(true);
        this.mainFrame.add(this.headerLabel);
        this.mainFrame.add(this.controlPanel);
        this.mainFrame.add(this.statusLabel);
        this.mainFrame.setLocationRelativeTo(null);
        this.mainFrame.setVisible(true);
        this.mainFrame.setResizable(false);
    }

    /**
     * Show the menu for logged user
     */
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
            this.extract();
        });

        JButton exit = new JButton("Sair");
        exit.addActionListener(e -> {
            this.controlPanel.removeAll();
            this.mainFrame.setVisible(false);
            this.statusLabel.setVisible(false);
            this.login();
        });

        this.statusLabel.setText(this.server.toString());
        this.statusLabel.setVisible(true);
        this.controlPanel.add(transferButton);
        this.controlPanel.add(showBalanceButton);
        this.controlPanel.add(extract);
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
            Account accountAux = new Account();
            accountAux.setAccountNumber(Integer.parseInt(accountNumber.getText()));
            accountAux.setPassword(passwordText.getText());
            Data data = new Data();
            data.setProtocolTag(ProtocolTag.LOGIN);
            data.setAccountAux(accountAux);
            Message message = new Message(null, data);
            try {
                this.channel.send(message);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        JButton signupButton = new JButton("Não possui conta?");
        signupButton.addActionListener(e -> {
            this.controlPanel.removeAll();
            this.mainFrame.setVisible(false);
            this.statusLabel.setVisible(false);
            this.showSignUpMenu();
        });

        this.controlPanel.add(namelabel);
        this.controlPanel.add(accountNumber);
        this.controlPanel.add(passwordLabel);
        this.controlPanel.add(passwordText);
        this.controlPanel.add(loginButton);
        this.controlPanel.add(signupButton);
        this.mainFrame.setVisible(true);
    }

    /**
     * Show the signUpMenu for users
     */
    private void showSignUpMenu() {
        this.headerLabel.setText("CRIAR NOVA CONTA");
        JLabel accountNumberLabel = new JLabel(" Nº Conta: ", JLabel.LEFT);
        JLabel nameLabel = new JLabel("Seu nome: ", JLabel.LEFT);
        JLabel passwordLabel = new JLabel("      Senha: ", JLabel.LEFT);
        JLabel amountLabel = new JLabel("   Valor à depositar: ", JLabel.LEFT);
        final JTextField accountNumber = new JTextField(25);
        final JPasswordField password = new JPasswordField(25);
        final JTextField name = new JTextField(25);
        final JTextField amount = new JTextField(20);

        JButton signup = new JButton("Cadastrar");
        signup.addActionListener(e -> {
            MessageAlertTag messageAlertTag;
            Account accountAux = new Account();
            accountAux.setAccountNumber(Integer.parseInt(accountNumber.getText()));
            accountAux.setName(name.getText());
            accountAux.setPassword(password.getText());
            accountAux.setBalance(Double.parseDouble(amount.getText()));
            messageAlertTag = server.signUp(accountAux);
            this.statusLabel.setText(MessageAlert.toString(messageAlertTag));
            this.statusLabel.setVisible(true);
            if (messageAlertTag == MessageAlertTag.SIGNUP_SUCCESSFUL) {
                this.controlPanel.removeAll();
                this.mainFrame.setVisible(false);
                this.statusLabel.setVisible(false);
                this.theUser = accountAux;
                this.mainFrame.setTitle("BCBank - Bem vindo " + this.theUser.getName());
                this.showMenu();
            }
        });

        JButton menu = new JButton("Voltar");
        menu.addActionListener(e -> {
            this.controlPanel.removeAll();
            this.mainFrame.setVisible(false);
            this.statusLabel.setVisible(false);
            this.login();
        });

        this.controlPanel.add(accountNumberLabel);
        this.controlPanel.add(accountNumber);
        this.controlPanel.add(nameLabel);
        this.controlPanel.add(name);
        this.controlPanel.add(passwordLabel);
        this.controlPanel.add(password);
        this.controlPanel.add(amountLabel);
        this.controlPanel.add(amount);
        this.controlPanel.add(signup);
        this.controlPanel.add(menu);
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
            // Call to server to make the transference
            Data data = new Data(this.theUser, Integer.parseInt(toAccount.getText()), Double.parseDouble(amount.getText()));
            Message message = new Message(null, data);
            try {
                this.channel.send(message);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            // this.statusLabel.setText(MessageAlert.toString(messageAlertTag));
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

    /**
     * Print the extractinto textarea element
     */
    private void extract() {
        this.headerLabel.setText("Meu extrato");
        JTextArea textArea = new JTextArea(7, 30);
        textArea.setEditable(false);
        textArea.setText(this.theUser.getExtractToString());
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

    /******************************************************************************************
     * Trying to make the distributed functions
     *****************************************************************************************/

    private void start() throws Exception {
        this.channel = new JChannel("xml-configs/udp.xml"); // Usa a configuração default
        this.channel.setReceiver(this);                     // Quem irá lidar com as mensagens recebidas
        this.channel.connect("BCBankGroup");
        // eventLoop();
        // this.channel.close();
    }

    public void receive(Message message) {
        Data data = (Data) message.getObject();
        switch (data.getProtocolTag()) {
            case TRANSFER:
                break;
            case LOGIN:
                this.statusLabel.setText(MessageAlert.toString(data.getAccountAux().getAlertTag()));
                if (data.getAccountAux().getAlertTag() == MessageAlertTag.LOGIN_SUCCESSFUL) {
                    this.controlPanel.removeAll();
                    this.mainFrame.setVisible(false);
                    this.statusLabel.setText(this.server.toString());
                    this.statusLabel.setVisible(true);
                    this.theUser = data.getAccountAux();
                    this.mainFrame.setTitle("BCBank - Bem vindo " + this.theUser.getName());
                    this.showMenu();
                }
                break;
            case BALANCE:
                break;
            case EXTRACT:
                break;
            default:
                break;
        }
    }

    public static void main(String args[]) throws Exception {
        UserScreen screen = new UserScreen();
        screen.prepareGUI();
        screen.login();
    }
}
