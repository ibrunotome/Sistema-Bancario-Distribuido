package models;

/**
 * @author Bruno Tomé
 * @author Cláudio Menezes
 * @since 03/07/2016
 */
public class Account {

    private int accountNumber;
    private String name;
    private String password;
    private Double balance;

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

    @Override
    public String toString() {
        return "\n----------------------------\nNome: " + this.name + "\nSaldo: R$ " + this.balance + "\n----------------------------\n";
    }
}
