package controllers;

import models.*;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

import java.io.Serializable;
import java.util.Hashtable;

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
    private JChannel channel;
    public boolean CONTINUE = true;

    public ServerBank() throws Exception {
        this.start();
    }

    /**
     * Check if the BCBank have the account passed into parameter, if there is,
     * return this account with the alert tag LOGIN_SUCCESSFUL for the UserScreen receive method
     * or return with the alert tag LOGIN_ERROR if there is not
     *
     * @param a
     * @return Account
     */
    public Account login(Account a) {
        Account accountAux = this.BCBank.getAllAccounts().get(a.getAccountNumber());
        if (accountAux != null && accountAux.getPassword().equals(a.getPassword())) {
            accountAux.setAlertTag(MessageAlertTag.LOGIN_SUCCESSFUL);

        } else {
            accountAux.setAlertTag(MessageAlertTag.LOGIN_ERROR);
        }
        a = accountAux;
        return accountAux;
    }

    /**
     * Check if toUser account exists and make a transference
     * if there is no logical errors, returning the corresponding
     * alert tags of each cause
     *
     * @param byUser
     * @param toUser
     * @param amount
     */
    public MessageAlertTag transference(Account byUser, int toUser, Double amount) {
        Account toUserAux = this.BCBank.getAllAccounts().get(toUser);
        if (toUserAux != null && byUser.getAccountNumber() != toUserAux.getAccountNumber()) {
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
     * @param newUser
     * @return MessageAlertTag
     */

    public MessageAlertTag signUp(Account newUser) {
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
     * @param a
     * @return String
     */
    public String getBalance(Account a) {
        return this.BCBank.getBalance(a);
    }

    /**
     * Get the extract of an account
     *
     * @param a
     * @return String
     */
    public String getExtract(Account a) {
        return a.toString();
    }

    /**
     * Sum the total cash of the bank
     *
     * @return
     */
    @Override
    public String toString() {
        return this.BCBank.sumBankCash();
    }


    /**
     * This method receive the messages from the group and make a
     * switch of the protocol tag of the message and do the corresponding action
     *
     * @param message
     */
    public void receive(Message message) {

        System.out.println("DEBUG: ServerBank.receive()=>message.toString(): "+message.toString());
        System.out.println("DEBUG: ServerBank.receive()=>message.getObject().toString(): "+(message.getObject()).toString());

        Address solicitante = message.getSrc();

        Data data = (Data) message.getObject();
        Account accountReceived = data.getAccountAux();

        //System.out.println("\n\nRecebida: \n "+data.toString());

        switch (data.getProtocolTag()) {
            case TRANSFER:
                /**
                 * If the received protocol tag in the message is TRANSFER,
                 * try to make a transference between the to accounts passed to
                 * the data object and send this object back to UserScreen
                 */
                MessageAlertTag transferenceTag = this.transference(accountReceived, data.getAccountNumberToTransfer(), data.getAmount());
                accountReceived.setAlertTag(transferenceTag);
                data.setAccountAux(accountReceived);
                message.setObject(data);
                try {
                    this.channel.send(message.getSrc(), message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case LOGIN:
                /**
                 * If the received protocol tag in the message is LOGIN,
                 * try to make the login with the to accounts passed by parameter
                 * and send the data object back to User Screen with and account
                 * containing the alert tag with the message if the login was successful or not
                 */
                accountReceived = this.login(accountReceived);

                System.out.println("DEBUG: ServerBank.login(): account="+accountReceived.toString());

                //DEBUG
                accountReceived.setAccountNumber(2016);
                data.setAccountAux(accountReceived);
                //System.out.println("\n\nENVIAR: \n "+data.toString());

                Message resposta = new Message(solicitante, data);
                //message.setObject(data);
                try {
                    System.out.println("DEBUG: ServerBank.send() --> UserScreen: resposta.getObject="+resposta.getObject().toString());

                    this.channel.send(resposta);   //envia mensagem unicast pro dst ou se for null, sera multicast
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case BALANCE:
                /**
                 * If the received protocol tag in the message is BALANCE,
                 * send the data object back to the UserScreen with the balance text
                 */
                String balance = this.getBalance(accountReceived);
                data.setText(balance);
                message.setObject(data);
                try {
                    this.channel.send(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case EXTRACT:
                /**
                 * If the received protocol tag in the message is EXTRACT,
                 * send the data object back to the UserScreen with the extract text
                 */
                String extract = this.getExtract(accountReceived);
                data.setText(extract);
                message.setObject(data);
                try {
                    this.channel.send(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case SINGUP:
                /**
                 * If the received protocol tag in the message is SIGNUP,
                 * try to make create a new Account with signUp method, and send
                 * a message back for the UserScreen with the received account
                 * that contains the alert tag saying if the signup was successful or not
                 */
                MessageAlertTag signupTag = this.signUp(accountReceived);
                accountReceived.setAlertTag(signupTag);
                System.out.println("\n\nALERT_TAG: \n "+accountReceived.getAlertTag().toString());
                data.setAccountAux(accountReceived);
                System.out.println("\n\nENVIAR: \n "+data.toString());
                message.setObject(data);
                try {
                    this.channel.send(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case TO_STRING_SERVER:
                /**
                 * If the received protocol tag in the message is TO_STRING_SERVER,
                 * send the data object back to the UserScreen with the total bank amount of cash text
                 */
                data.setText(this.toString());
                message.setObject(data);
                try {
                    this.channel.send(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        this.channel = new JChannel("xml-configs/udp.xml");
        this.channel.setDiscardOwnMessages(true);
        this.channel.setReceiver(this);
        this.channel.connect("BCBankGroup");
        while (CONTINUE) {
            Thread.sleep(100);
        }
        this.channel.close();
    }

    public static void main(String args[]) throws Exception {
        // Use this property because an error reporting the unavailability of IPV6
        System.setProperty("java.net.preferIPv4Stack", "true");
        ServerBank server = new ServerBank();
    }
}
