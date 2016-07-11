package models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class to implement the model of an user account
 *
 * @author Bruno Tomé
 * @author Cláudio Menezes
 * @since 03/07/2016
 */
public class Account implements Serializable {

    private int accountNumber;
    private String name;
    private String password;
    private Double balance;
    private ArrayList<String> extract = new ArrayList<>();
    private MessageAlertTag alertTag;

    public String getExtractToString() {
        String allTransferences = "";
        for (String s : this.extract) {
            allTransferences += s;
        }

        return allTransferences;
    }

    public MessageAlertTag getAlertTag() {
        return alertTag;
    }

    public void setAlertTag(MessageAlertTag alertTag) {
        this.alertTag = alertTag;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getBalance() {
        return this.balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public int getAccountNumber() {
        return this.accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void addToExtract(String s) {
        this.extract.add(s);
    }

    @Override
    public String toString() {
        return "\n----------------------------\nNome: " + this.name + "\nSaldo: R$ " + this.balance + "\n----------------------------\n";
    }
}
