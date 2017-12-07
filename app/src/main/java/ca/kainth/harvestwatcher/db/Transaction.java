package ca.kainth.harvestwatcher.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
            entity = Wallet.class,
            parentColumns = "id",
            childColumns = "walletId",
            onDelete = ForeignKey.CASCADE))
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int walletId;

    private String transactionId;

    private double value;

    private int unixTimeStamp;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public int getWalletId() {
        return walletId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getUnixTimeStamp() {
        return unixTimeStamp;
    }

    public void setUnixTimeStamp(int unixTimeStamp) {
        this.unixTimeStamp = unixTimeStamp;
    }

}
