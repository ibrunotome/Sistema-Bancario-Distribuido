package controllers;

import models.Account;
import models.Bank;
import models.Data;
import org.jgroups.*;

import java.io.Serializable;

/**
 * Created by claudio on 22/07/16.
 */
public class ServerBackUp extends ReceiverAdapter implements Serializable{

    private Bank BCBackUp = new Bank();
    private Channel channelBackUp;

    public ServerBackUp() throws Exception {
        BCBackUp.initalizeBank();
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
        }
    }

    @Override
    public void receive (Message message){

        Address sender = message.getSrc();

        Data data = (Data) message.getObject();
        Account accountReceived = data.getAccountAux();

        switch (data.getProtocolTag()){

            case BACKUP_LOGIN:
                data.setAllAccounts(BCBackUp.getAllAccounts());
                Message response = new Message(sender, data);
                try {
                    this.channelBackUp.send(response);
                } catch (Exception e) {
                    System.err.println("Falhou ao enviar mensagem no SERVER_LOGIN");
                }

                break;
            case BACKUP_UPDATE_STATE:
                this.BCBackUp.setAllAccounts(data.getAllAccounts());
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
