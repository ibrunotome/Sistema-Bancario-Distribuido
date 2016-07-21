package models;

import org.jgroups.Address;
import org.jgroups.stack.AddressGenerator;

import java.io.Serializable;

/**
 * Struct to most used data
 *
 * @author Bruno Tomé
 * @author Cláudio Menezes
 * @since 11/07/2016
 */
public class Data implements Serializable {
    private Account accountAux;
    private int accountNumberToTransfer;
    private Double amount;
    private ProtocolTag protocolTag;
    private String text;
    private Address sender;

    public Data() {

    }

    public Data(Account accountAux, int accountNumberToTransfer, Double amount) {
        this.accountAux = accountAux;
        this.accountNumberToTransfer = accountNumberToTransfer;
        this.amount = amount;
    }

    public ProtocolTag getProtocolTag() {
        return this.protocolTag;
    }

    public void setProtocolTag(ProtocolTag protocolTag) {
        this.protocolTag = protocolTag;
    }

    public Account getAccountAux() {
        return accountAux;
    }

    public void setAccountAux(Account accountAux) {
        this.accountAux = accountAux;
    }

    public int getAccountNumberToTransfer() {
        return accountNumberToTransfer;
    }

    public void setAccountNumberToTransfer(int accountNumberToTransfer) {
        this.accountNumberToTransfer = accountNumberToTransfer;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Address getSender() {
        return sender;
    }

    public void setSender(Address sender) {
        this.sender = sender;
    }


    public String toString () {
        String line = " ";

        if(this.accountAux != null){
            line += "\n Acount: "+ this.accountAux.toString();
        }

        if(this.accountNumberToTransfer != -1){
            line += "\n AcountNumberTransfer: "+this.accountNumberToTransfer;
        }

        if(this.amount != null){
            line += "\n Amount: "+this.amount;
        }

        if(this.protocolTag != null){
            line += "\n protocolTag: "+this.protocolTag.toString();
        }

        if(this.text != null){
            line += "\n Text: "+this.text;
        }

        return line;
    }
}
