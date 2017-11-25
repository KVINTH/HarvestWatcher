package ca.kainth.harvestwatcher;

/**
 * Created by Harry on 11/24/2017.
 */

public class Wallet {
    private String alias, address;
    private double balance;

    public Wallet (String alias, String address, double balance) {
        this.alias = alias;
        this.address = address;
        this.balance = balance;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
