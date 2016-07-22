package views;

import controllers.MessageAlert;
import models.Account;
import models.Data;
import models.MessageAlertTag;
import models.ProtocolTag;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

import javax.swing.*;
import java.awt.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

import static org.jgroups.Message.*;

/**
 * Class to show a GUI for users of BCBank
 *
 * @author Bruno Tomé
 * @author Cláudio Menezes
 * @since 03/07/2016
 */
@SuppressWarnings("deprecation")
public class UserScreen extends ReceiverAdapter implements Serializable {

    private Account theUser = new Account();
    private JChannel channel;
    private String toStringServer = "";
    // GUI variables
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel statusLabel;
    private JPanel controlPanel;

    public UserScreen() throws Exception {
        this.prepareGUI();
        this.login();
        this.start();
    }

    /**
     * Prepare the initial GUI, set the mainFrame that will be used
     * for put any other kind of graphic element
     */
    private void prepareGUI() {
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
     * Show the main menu for logged user
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

        // Set the protocol tag to receive method of ServerBank class work on it
        Data data = new Data();
        data.setProtocolTag(ProtocolTag.SCREEN_TO_STRING_SERVER);
        Address memberOfBank = this.chooseAddress();

        /*
          Send a message to the channel, ServerBank will send back the
          total amount of cash in the bank and this message will be
          displayed in a label on the main menu
         */
        Message request = new Message(memberOfBank, data);
        try {
            this.channel.send(request);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        this.statusLabel.setText(this.toStringServer);
        this.statusLabel.setVisible(true);
        this.controlPanel.add(transferButton);
        this.controlPanel.add(showBalanceButton);
        this.controlPanel.add(extract);
        this.controlPanel.add(exit);
        this.mainFrame.setVisible(true);
    }

    /**
     * Login GUI input fields. Get the inputed data and send a message
     * to receive method in the ServerBank class with the tag SCREEN_LOGIN,
     * trying to login into the system
     */
    private void login() {
        this.headerLabel.setText("Digite o número da conta e senha para login");
        JLabel namelabel = new JLabel("Nº Conta: ", JLabel.LEFT);
        JLabel passwordLabel = new JLabel("     Senha: ", JLabel.LEFT);
        final JTextField accountNumber = new JTextField(25);
        final JPasswordField passwordText = new JPasswordField(25);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            // Create an auxiliar account to try login into the system
            Account accountAux = new Account();
            accountAux.setAccountNumber(Integer.parseInt(accountNumber.getText()));
            accountAux.setPassword(passwordText.getText());

            // Set the protocolTag to send to method receive on ServerBank
            Data data = new Data();
            data.setProtocolTag(ProtocolTag.SCREEN_LOGIN);
            data.setAccountAux(accountAux);
            /*
              Send a message to the receive method in the ServerBank class with the tag SCREEN_LOGIN
              and the account that will be used to try to login  in the system and get the
              response back on the receive method of this class
             */
            Address memberOfBank = this.chooseAddress();
            Message request = new Message(memberOfBank, data);

            try {
                this.channel.send(request);
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
     * Show the signupMenu for users
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
            // Instantiate and set the attributes
            Account accountAux = new Account();
            accountAux.setAccountNumber(Integer.parseInt(accountNumber.getText()));
            accountAux.setName(name.getText());
            accountAux.setPassword(password.getText());
            accountAux.setBalance(Double.parseDouble(amount.getText()));

            /*
              Send a message to reveive method on the ServerBank class with the
              tag SIGNUP to try to make a new Account in the system and get the
              response back into the receive method in this class
             */
            Address memberOfBank = this.chooseAddress();

            Data data = new Data();
            data.setAccountAux(accountAux);
            data.setProtocolTag(ProtocolTag.SCREEN_SINGUP);
            Message request = new Message(memberOfBank, data);

            try {
                this.channel.send(request);
            } catch (Exception e1) {
                e1.printStackTrace();
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
     * Transfer GUI input fields. Get the inputed data and
     * try to make a transfer of cash between two accounts sending
     * a message to the ServerBank class with the tag SCREEN_TRANSFER.
     * Get the response back into the receive method of this class
     */
    private void transference() {
        this.headerLabel.setText("TRANSFERÊNCIA");
        JLabel toAccountLabel = new JLabel("Para a conta nº: ", JLabel.LEFT);
        JLabel amountLabel = new JLabel("               Valor: ", JLabel.LEFT);
        final JTextField toAccount = new JTextField(20);
        final JTextField amount = new JTextField(20);

        JButton transferButton = new JButton("Transferir");
        transferButton.addActionListener(e -> {
            /*
              Send a message to receive method on ServerBank class, using the
              SCREEN_TRANSFER tag to make the transference and get the response back on
              receive method of this class
             */
            Data data = new Data(this.theUser, Integer.parseInt(toAccount.getText()), Double.parseDouble(amount.getText()));
            data.setProtocolTag(ProtocolTag.SCREEN_TRANSFER);
            Address memberOfBank = this.chooseAddress();
            Message message = new Message(memberOfBank, data);
            message.setFlag(Flag.RSVP);
            try {
                this.channel.send(message);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
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
     * Send a message to receive method of ServerBank class with the
     * tag SCREEN_BALANCE. Get the response back with the balance on the
     * receive method of this class
     */
    private void getBalance() {
        // Send a message to server to make the transference
        Data data = new Data();
        data.setAccountAux(this.theUser);
        data.setProtocolTag(ProtocolTag.SCREEN_BALANCE);
        Address memberOfBank = this.chooseAddress();
        Message message = new Message(memberOfBank, data);
        try {
            this.channel.send(message);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Send a message to receive method of ServerBank class with the
     * tag SCREEN_EXTRACT. Get the response back with the balance on the
     * receive method of this class
     */
    private void extract() {
        Data data = new Data();
        data.setAccountAux(this.theUser);
        data.setProtocolTag(ProtocolTag.SCREEN_EXTRACT);
        Address memberOfBank = this.chooseAddress();
        Message message = new Message(memberOfBank, data);
        try {
            this.channel.send(message);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * choose a randomic address from the members of the channel
     * and return the choosenOne.
     */
    private Address chooseAddress() {
        Address choosenOne = this.channel.getView().getMembers().get(0);
        System.out.println("\nEu :" + this.channel.getAddress().toString());

        int choosen = 0;
        int totalMembers = this.channel.getView().getMembers().size();

        System.out.println("\nMembros: " + this.channel.getView().getMembers().toString());

        if (totalMembers > 2) {
            while ((choosenOne == this.channel.getAddress())) {
                choosen = ThreadLocalRandom.current().nextInt(0, totalMembers);
                choosenOne = this.channel.getView().getMembers().get(choosen);
                System.out.println("entrei :" + choosenOne.toString());
            }
        } else {
            choosenOne = this.channel.getView().getMembers().get(choosen);
        }
//        System.out.println("vacaFIM :"+choosenOne.toString());

        return choosenOne;
    }

    @Override
    public void receive(Message message) {

        //System.out.println("DEBUG: UserScreen.receive()=>message.toString(): "+message.toString());
        //System.out.println("DEBUG: UserScreen.receive()=>message.getObject().toString(): "+(message.getObject()).toString());

        Data data = (Data) (message.getObject());
        Account accountReceive = data.getAccountAux();

        switch (data.getProtocolTag()) {
            case SCREEN_TRANSFER:
                /*
                  If the received protocol tag in the message is SCREEN_TRANSFER,
                  set the statusLabel text with the string that correspond with the
                  alert tag of the account received in the message
                 */
                this.statusLabel.setText(MessageAlert.toString(accountReceive.getAlertTag()));
                this.statusLabel.setVisible(true);
                break;
            case SCREEN_LOGIN:
                /*
                  If the received protocol tag in the message is SCREEN_LOGIN,
                  check if the message alert tag is LOGIN_SUCCESSFUL, if is it, set
                  the global variable theUser with the account received in the message
                  and show the main menu to this user
                 */
                this.statusLabel.setText(MessageAlert.toString(accountReceive.getAlertTag()));
                this.statusLabel.setVisible(true);
                if (accountReceive.getAlertTag() == MessageAlertTag.LOGIN_SUCCESSFUL) {
                    this.controlPanel.removeAll();
                    this.mainFrame.setVisible(false);
                    this.theUser = accountReceive;
                    this.mainFrame.setTitle("BCBank - Bem vindo " + this.theUser.getName());
                    this.showMenu();
                }

                break;
            case SCREEN_BALANCE:
                /*
                  If the received protocol tag in the message is SCREEN_BALANCE,
                  set the headerLabel text with the balance of this user.

                  The data.getText() contains the balance string
                 */
                this.headerLabel.setText(data.getText());
                JButton menu = new JButton("Menu");
                menu.addActionListener(e -> {
                    this.controlPanel.removeAll();
                    this.mainFrame.setVisible(false);
                    this.statusLabel.setVisible(false);
                    this.showMenu();
                });
                this.controlPanel.add(menu);
                this.mainFrame.setVisible(true);
                break;
            case SCREEN_EXTRACT:
                /*
                  If the received protocol tag in the message is SCREEN_EXTRACT,
                  set the textArea below with the extract of this user
                 */
                this.headerLabel.setText("Meu extrato");
                JTextArea textArea = new JTextArea(7, 30);
                textArea.setEditable(false);
                // the data.getText contains the extract string
                textArea.setText(data.getText());
                JScrollPane scroll = new JScrollPane(textArea);
                scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                menu = new JButton("Menu");
                menu.addActionListener(e -> {
                    this.controlPanel.removeAll();
                    this.mainFrame.setVisible(false);
                    this.statusLabel.setVisible(false);
                    this.showMenu();
                });
                this.controlPanel.add(scroll);
                this.controlPanel.add(menu);
                this.mainFrame.setVisible(true);
                break;
            case SCREEN_SINGUP:
                /*
                  If the received protocol tag in the message is SIGNUP,
                  check if the messageAlertTag in the receivedAccount of the message
                  have the tag SIGNUP_SUCCESSFUL and show the main menu if is it
                 */
                MessageAlertTag messageAlertTag;
                messageAlertTag = accountReceive.getAlertTag();
                this.statusLabel.setText(MessageAlert.toString(messageAlertTag));
                this.statusLabel.setVisible(true);
                if (messageAlertTag == MessageAlertTag.SIGNUP_SUCCESSFUL) {
                    this.controlPanel.removeAll();
                    this.mainFrame.setVisible(false);
                    this.statusLabel.setVisible(false);
                    this.theUser = accountReceive;
                    this.mainFrame.setTitle("BCBank - Bem vindo " + this.theUser.getName());
                    this.showMenu();
                }
                break;
            case SCREEN_TO_STRING_SERVER:
                /*
                  If the received protocol tag in the message is SCREEN_TO_STRING_SERVER,
                  set the statusLabel below with the total bank amount of cash
                 */
                this.toStringServer = data.getText();
                this.statusLabel.setText(this.toStringServer);
                this.statusLabel.setVisible(true);
                break;
            default:
                break;
        }
    }

    /**
     * Instantiate the channel, set the xml with the configs,
     * set this class as receiver, connect to the BCBankGroup
     *
     * @throws Exception
     */
    private void start() throws Exception {
        this.channel = new JChannel("jgroups-settings.xml");
        this.channel.setDiscardOwnMessages(true);
        this.channel.setReceiver(this);
        this.channel.connect("BCBScreenGroup");
        boolean CONTINUE = true;
        while (CONTINUE) {
            Thread.sleep(500);
        }
        this.channel.close();
    }

    /**
     * Prepare the GUI for the user, showing the login screen
     *
     * @param args
     * @throws Exception of channel methods
     */
    public static void main(String args[]) throws Exception {
        // Use this property because an error reporting the unavailability of IPV6
        System.setProperty("java.net.preferIPv4Stack", "true");
        new UserScreen();

    }
}
