package models;

import org.jgroups.ReceiverAdapter;

import java.io.*;
import java.util.Hashtable;

/**
 * Class to simulate a bank
 *
 * @author Bruno Tomé
 * @author Cláudio Menezes
 * @since 03/07/2016
 */
public class Bank extends ReceiverAdapter implements Serializable {
    private final Double totalCash = 10000.0;
    private final int totalAccounts = 10;
    private Hashtable<Integer, Account> allAccounts = new Hashtable();

    public Bank() throws Exception {
        initalizeBank();
    }

    /**
     * Initialize the bank if there isn't an object bank serialized
     */
    private void initalizeBank() {
        File f = new File("allAccounts.ser");
        if (f.exists() && !f.isDirectory()) {
            System.out.println("Banco existe, carregando snapshot");
            this.loadState();
        } else {
            System.out.println("Banco inexistente, criando banco inicial");
            for (int i = 0; i < this.totalAccounts; i++) {
                Account a = new Account();
                a.setAccountNumber(i);
                a.setName("user" + i);
                a.setPassword("pass" + i);
                a.setBalance(this.totalCash / this.totalAccounts);
                a.addToExtract("\n----------------------------\nDEPÓSITO\n"
                        + "----------------------------\nValor: R$ " + a.getBalance() + "\nMeu novo saldo: R$ "
                        + (a.getBalance()) + "\n----------------------------\n");
                this.allAccounts.put(a.getAccountNumber(), a);
            }
        }
    }

    /**
     * Serialize the actual allAccounts object to save the actual state
     */
    public void saveState() {
        try {
            FileOutputStream fileOut =
                    new FileOutputStream("allAccounts.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this.allAccounts);
            out.close();
            fileOut.close();
            System.out.println("Estado atual do banco salvo com sucesso");
        } catch (IOException i) {
            System.err.println("Não foi possível salvar o estado atual do banco");
        }

    }

    /**
     * If file allAccounts.ser exists, unserialize it and load the snapshot
     */
    private void loadState() {
        try {
            FileInputStream fileIn = new FileInputStream("allAccounts.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            this.allAccounts = (Hashtable<Integer, Account>) in.readObject();
            System.out.println("Snapshot do banco carregado com sucesso");
            in.close();
            fileIn.close();
        } catch (Exception i) {
            System.err.println("Não foi possível carregar o estado atual do banco");
        }
    }

    /**
     * Transfer an amount of cash between two accounts
     *
     * @param a1
     * @param a2
     * @param amount
     */
    public void transference(Account a1, Account a2, Double amount) {
        a1.setBalance(a1.getBalance() - amount);
        a2.setBalance(a2.getBalance() + amount);

        this.allAccounts.replace(a1.getAccountNumber(), a1);
        this.allAccounts.replace(a2.getAccountNumber(), a2);
    }

    /**
     * Get the balance of an account
     *
     * @param a
     * @return String
     */
    public String getBalance(Account a) {
        return "O saldo da conta " + a.getAccountNumber() + " é: R$ " + a.getBalance();
    }

    /**
     * Add an account to allAccounts
     *
     * @param a
     */
    public void addAccount(Account a) {
        a.addToExtract("\n----------------------------\nDEPÓSITO\n"
                + "----------------------------\nValor: R$ " + a.getBalance() + "\nMeu novo saldo: R$ "
                + (a.getBalance()) + "\n----------------------------\n");
        this.allAccounts.put(a.getAccountNumber(), a);
    }

    /**
     * Get all acounts table
     *
     * @return Hashtable
     */
    public Hashtable<Integer, Account> getAllAccounts() {
        return this.allAccounts;
    }

    /**
     * Set the hashtable allAccounts
     *
     * @param allAccounts
     */
    public void setAllAccounts(Hashtable<Integer, Account> allAccounts) {
        this.allAccounts = allAccounts;
    }

    public String sumBankCash() {
        Double total = 0.0;
        for (Account a : this.allAccounts.values()) {
            total += a.getBalance();
        }
        return "Soma total: " + total;
    }

}
