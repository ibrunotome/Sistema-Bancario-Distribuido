package controllers;

import models.Bank;
import models.Data;
import org.jgroups.*;

import java.io.Serializable;

/**
 * This class represents the persistance of the BCBank system
 *
 * @author Bruno Tomé
 * @author Cláudio Menezes
 * @since 21/07/2016
 */

public class ServerBackUp extends ReceiverAdapter implements Serializable {

    private Bank BCBackUp = new Bank();
    private Channel channelBackUp;

    public ServerBackUp() throws Exception {
        this.BCBackUp.initalizeBank();
        this.start();
    }

    private void start() throws Exception {
        this.channelBackUp = new JChannel("jgroups-settings.xml");
        this.channelBackUp.setDiscardOwnMessages(false);
        this.channelBackUp.setReceiver(this);
        this.channelBackUp.connect("BCBankBackUp");

        boolean CONTINUE = true;

        while (CONTINUE) {
            Thread.sleep(500);
            System.out.println("banco-persistencia: " + this.channelBackUp.getView().getMembers().toString());
        }
    }

    @Override
    public void receive(Message message) {

        Address sender = message.getSrc();
        Data data = (Data) message.getObject();

        switch (data.getProtocolTag()) {
            case BACKUP_LOGIN:
                /*
                    Load the current state of the Bank when the user makes the login
                 */
                data.setAllAccounts(this.BCBackUp.getAllAccounts());
                Message response = new Message(sender, data);
                try {
                    this.channelBackUp.send(response);
                } catch (Exception e) {
                    System.err.println("Falhou ao enviar mensagem no BACKUP_LOGIN");
                }

                break;
            case BACKUP_UPDATE_STATE:
                /*
                    Save the current state of the Bank when user makes a trasnference
                 */
                this.BCBackUp.setAllAccounts(data.getAllAccounts());
                this.BCBackUp.saveState();
                System.out.println("Estado atual do banco salvo com sucesso");
                break;
        }
    }

    public static void main(String args[]) throws Exception {
        // Use this property because an error reporting the unavailability of IPV6
        System.setProperty("java.net.preferIPv4Stack", "true");
        new ServerBackUp();
    }

}
