package models;

/**
 * Struct to most used data
 *
 * @author Bruno Tomé
 * @author Cláudio Menezes
 * @since 11/07/2016
 */
public class Data {
    private Account accountAux;
    private int accountNumberTotTransfer;
    private Double amount;
    private ProtocolTag protocolTag;
    private String text;



    public Data() {

    }

    public Data(Account accountAux, int accountNumberTotTransfer, Double amount) {
        this.accountAux = accountAux;
        this.accountNumberTotTransfer = accountNumberTotTransfer;
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

    public int getAccountNumberTotTransfer() {
        return accountNumberTotTransfer;
    }

    public void setAccountNumberTotTransfer(int accountNumberTotTransfer) {
        this.accountNumberTotTransfer = accountNumberTotTransfer;
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
}
