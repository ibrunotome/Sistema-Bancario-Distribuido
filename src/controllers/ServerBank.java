package controllers;

import models.*;
import org.jgroups.*;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Controller that makes the comunications between the UserScreen view
 * and the Bank model
 *
 * @author Bruno Tomé
 * @author Cláudio Menezes
 * @since 03/07/2016
 */
public class ServerBank extends ReceiverAdapter implements Serializable {

    private Bank BCBank = new Bank();
    private JChannel channelScreen;
    private JChannel channelBank;

    public ServerBank() throws Exception {
        this.start();
    }

    /**
     * Check if the BCBank have the account passed into parameter, if there is,
     * return this account with the alert tag LOGIN_SUCCESSFUL for the UserScreen receive method
     * or return with the alert tag LOGIN_ERROR if there is not
     *
     * @param a Account that will try to login
     * @return Account
     */
    private Account login(Account a) {
        Account accountAux = this.BCBank.getAllAccounts().get(a.getAccountNumber());
        if (accountAux != null && accountAux.getPassword().equals(a.getPassword())) {
            accountAux.setAlertTag(MessageAlertTag.LOGIN_SUCCESSFUL);

        } else {
            accountAux = new Account();
            accountAux.setAlertTag(MessageAlertTag.LOGIN_ERROR);
        }
        return accountAux;
    }

    /**
     * Check if toUser account exists and make a transference
     * if there is no logical errors, returning the corresponding
     * alert tags of each cause
     *
     * @param byUser Account will make the transference
     * @param toUser Account will receive the transference
     * @param amount Amount to transfer
     */
    private MessageAlertTag transference(Account byUser, int toUser, Double amount) {
        Account toUserAux = this.BCBank.getAllAccounts().get(toUser);
        byUser = this.BCBank.getAllAccounts().get(byUser.getAccountNumber());

        /*
            If toUser exists and he is different from from byUser, try to make the transference
         */
        if (toUserAux != null && (byUser.getAccountNumber() != toUserAux.getAccountNumber())) {
            if (byUser.getBalance() >= amount && amount > 0) {
                this.BCBank.transference(byUser, toUserAux, amount);
                // Add a new transfer to extract of byUser
                byUser.addToExtract("\n----------------------------\nSAÍDA\n"
                        + "----------------------------\nPara a conta nº "
                        + toUser + "\nValor: R$ " + amount + "\nMeu novo saldo: R$ "
                        + (byUser.getBalance()) + "\n----------------------------\n");
                // Add a new transfer to extract of toUser
                toUserAux.addToExtract("\n----------------------------\nENTRADA\n"
                        + "----------------------------\nProvindo da conta nº "
                        + byUser.getAccountNumber() + "\nValor: R$ " + amount + "\nMeu novo saldo: R$ "
                        + (toUserAux.getBalance()) + "\n----------------------------\n");

                // Update the allAccounts of BCBank
                Hashtable<Integer, Account> allAccounts;
                allAccounts = this.BCBank.getAllAccounts();
                allAccounts.replace(byUser.getAccountNumber(), byUser);
                allAccounts.replace(toUserAux.getAccountNumber(), toUserAux);
                this.BCBank.setAllAccounts(allAccounts);
                // Serialize the accounts after each transference
                this.BCBank.saveState();
                return MessageAlertTag.TRANSFER_SUCCESSFUL;
            } else if (amount <= 0) {
                return MessageAlertTag.TRANSFER_ERROR_NEGATIVE;
            } else {
                return MessageAlertTag.TRANSFER_ERROR_AMOUNT;
            }
        } else if (toUserAux != null && byUser.getAccountNumber() == toUserAux.getAccountNumber()) {
            return MessageAlertTag.TRANSFER_ERROR_SAME_ACCOUNT;
        } else {
            return MessageAlertTag.TRANSFER_ERROR_ACCOUNT;
        }
    }

    /**
     * Create a new account if there isn't an equal account created yet,
     * return alert tag SIGNUP_SUCCESSFUL if there isn't, or SIGNUP_ERROR
     * if the account already exist
     *
     * @param newUser Account that will try to signup
     * @return MessageAlertTag
     */

    private MessageAlertTag signUp(Account newUser) {
        if (this.BCBank.getAllAccounts().get(newUser.getAccountNumber()) == null) {
            this.BCBank.addAccount(newUser);
            return MessageAlertTag.SIGNUP_SUCCESSFUL;
        } else {
            return MessageAlertTag.SIGNUP_ERROR;
        }
    }

    /**
     * Get the balance of an account
     *
     * @param a Account to get the balance
     * @return String
     */
    private String getBalance(Account a) {
        return this.BCBank.getBalance(a);
    }

    /**
     * Get the extract of an account
     *
     * @param a Account to add the extract
     * @return String
     */
    private String getExtract(Account a) {
        return this.BCBank.getAllAccounts().get(a.getAccountNumber()).getExtractToString();
    }

    /**
     * Sum the total cash of the bank
     *
     * @return String with the total bank cash
     */
    @Override
    public String toString() {
        return this.BCBank.sumBankCash();
    }

    /**
     * This method receive the messages from the group and make a
     * switch of the protocol tag of the message and do the corresponding action
     *
     * @param message Message sended from Jchannel
     */
    public void receive(Message message) {

//        System.out.println("DEBUG: ServerBank.receive()=>message.toString(): " + message.toString());
//        System.out.println("DEBUG: ServerBank.receive()=>message.getObject().toString(): " + (message.getObject()).toString());

        Address sender = message.getSrc();

        Data data = (Data) message.getObject();
        Account accountReceived = data.getAccountAux();
        System.out.println("\nTag: " + data.getProtocolTag());
        System.out.println("\nBanco: " + sender);
        System.out.println("\nTela: " + data.getSender());

        //System.out.println("\n\nRecebida: \n "+data.toString());

        switch (data.getProtocolTag()) {
            case SCREEN_TRANSFER:
                /*
                  If the received protocol tag in the message is SCREEN_TRANSFER,
                  try to make a transference between the to accounts passed to
                  the data object and send this object back to UserScreen
                 */
                data.setProtocolTag(ProtocolTag.SERVER_TRANSFER);
                data.setSender(sender);
                message = new Message(null, data);
                message.setFlag(Message.Flag.RSVP, Message.Flag.OOB);

                try {
                    this.channelBank.send(message);
                } catch (Exception e) {
                    data.setProtocolTag(ProtocolTag.SCREEN_TRANSFER);
                    data.getAccountAux().setAlertTag(MessageAlertTag.UNKNOWN_ERROR);
                    message = new Message(sender, data);
                    try {
                        this.channelScreen.send(message);
                    } catch (Exception e1) {
                        System.err.println("Falhou ao enviar mensagem no SCREEN_TRANSFER");
                    }
                }

                break;
            case SCREEN_LOGIN:
                /*
                  If the received protocol tag in the message is SCREEN_LOGIN,
                  try to make the login with the to accounts passed by parameter
                  and send the data object back to User Screen with and account
                  containing the alert tag with the message if the login was successful or not
                 */
                accountReceived = this.login(accountReceived);
                data.setAccountAux(accountReceived);
                Message response = new Message(sender, data);

                try {
                    this.channelScreen.send(response);   //envia mensagem unicast pro dst ou se for null, sera multicast
                } catch (Exception e) {
                    data.setProtocolTag(ProtocolTag.SCREEN_LOGIN);
                    data.getAccountAux().setAlertTag(MessageAlertTag.UNKNOWN_ERROR);
                    message = new Message(sender, data);
                    try {
                        this.channelScreen.send(message);
                    } catch (Exception e1) {
                        System.err.println("Falhou ao enviar mensagem no SCREEN_LOGIN");
                    }
                }
                break;
            case SCREEN_BALANCE:
                /*
                  If the received protocol tag in the message is SCREEN_BALANCE,
                  send the data object back to the UserScreen with the balance text
                 */
                String balance = this.getBalance(accountReceived);
                data.setText(balance);
                response = new Message(sender, data);
                try {
                    this.channelScreen.send(response);
                } catch (Exception e) {
                    data.setProtocolTag(ProtocolTag.SCREEN_BALANCE);
                    data.getAccountAux().setAlertTag(MessageAlertTag.UNKNOWN_ERROR);
                    message = new Message(sender, data);
                    try {
                        this.channelScreen.send(message);
                    } catch (Exception e1) {
                        System.err.println("Falhou ao enviar mensagem no SCREEN_BALANCE");
                    }
                }
                break;
            case SCREEN_EXTRACT:
                /*
                  If the received protocol tag in the message is SCREEN_EXTRACT,
                  send the data object back to the UserScreen with the extract text
                 */
                String extract = this.getExtract(accountReceived);
                data.setText(extract);
                response = new Message(sender, data);
                try {
                    this.channelScreen.send(response);
                } catch (Exception e) {
                    data.setProtocolTag(ProtocolTag.SCREEN_EXTRACT);
                    data.getAccountAux().setAlertTag(MessageAlertTag.UNKNOWN_ERROR);
                    message = new Message(sender, data);
                    try {
                        this.channelScreen.send(message);
                    } catch (Exception e1) {
                        System.err.println("Falhou ao enviar mensagem no SCREEN_EXTRACT");
                    }
                }
                break;
            case SCREEN_SINGUP:
                /*
                  If the received protocol tag in the message is SIGNUP,
                  try to make create a new Account with signUp method, and send
                  a message back for the UserScreen with the received account
                  that contains the alert tag saying if the signup was successful or not
                 */
                MessageAlertTag signupTag = this.signUp(accountReceived);
                accountReceived.setAlertTag(signupTag);
                System.out.println("\n\nALERT_TAG: \n " + accountReceived.getAlertTag().toString());
                data.setAccountAux(accountReceived);
                response = new Message(sender, data);
                try {
                    this.channelScreen.send(response);
                } catch (Exception e) {
                    data.setProtocolTag(ProtocolTag.SCREEN_SINGUP);
                    data.getAccountAux().setAlertTag(MessageAlertTag.UNKNOWN_ERROR);
                    message = new Message(sender, data);
                    try {
                        this.channelScreen.send(message);
                    } catch (Exception e1) {
                        System.err.println("Falhou ao enviar mensagem no SCREEN_SINGUP");
                    }
                }
                break;
            case SCREEN_TO_STRING_SERVER:
                /*
                  If the received protocol tag in the message is SCREEN_TO_STRING_SERVER,
                  send the data object back to the UserScreen with the total bank amount of cash text
                 */
                data.setText(this.toString());
                response = new Message(sender, data);
                try {
                    this.channelScreen.send(response);
                } catch (Exception e) {
                    data.setProtocolTag(ProtocolTag.SCREEN_TO_STRING_SERVER);
                    data.getAccountAux().setAlertTag(MessageAlertTag.UNKNOWN_ERROR);
                    message = new Message(sender, data);
                    try {
                        this.channelScreen.send(message);
                    } catch (Exception e1) {
                        System.err.println("Falhou ao enviar mensagem no SCREEN_TO_STRING_SERVER");
                    }
                }
                break;
            case SERVER_TRANSFER:
                /*
                  If the received protocol tag in the message is SERVER_TRANSFER,
                  try to make the transference between the banks
                 */
                data.setProtocolTag(ProtocolTag.SCREEN_TRANSFER);
                MessageAlertTag transferenceTag = this.transference(accountReceived, data.getAccountNumberToTransfer(), data.getAmount());
                if (sender == this.channelBank.getAddress()) {
                    accountReceived.setAlertTag(transferenceTag);
                    data.setAccountAux(accountReceived);
                    response = new Message(data.getSender(), data);
                    try {
                        this.channelScreen.send(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
            case SERVER_LOGIN:
                break;
            default:
                break;
        }
    }

    /**
     * Instantiate the channelScreen, set the xml with the configs,
     * set this class as receiver, connect to the BCBankGroup
     *
     * @throws Exception of channel methods
     */
    private void start() throws Exception {
        // Channel Screen cluster
        this.channelScreen = new JChannel("jgroups-settings.xml");
        this.channelScreen.setDiscardOwnMessages(true);
        this.channelScreen.setReceiver(this);
        this.channelScreen.connect("BCBScreenGroup");

        // Channel Bank cluster
        this.channelBank = new JChannel("jgroups-settings.xml");
        this.channelBank.setDiscardOwnMessages(false);
        this.channelBank.setReceiver(this);
        this.channelBank.connect("BCBankGroup");

        boolean CONTINUE = true;

        while (CONTINUE) {
            Thread.sleep(500);
        }
        this.channelScreen.close();
        this.channelBank.close();
    }

    public static void main(String args[]) throws Exception {
        // Use this property because an error reporting the unavailability of IPV6
        System.setProperty("java.net.preferIPv4Stack", "true");
        new ServerBank();
    }
}
