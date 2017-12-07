package ca.kainth.harvestwatcher.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by Harry on 11/29/2017.
 */

@Dao
public interface TransactionDao {

    @Query("SELECT * FROM `transaction`")
    List<Transaction> getAll();

    @Query("SELECT * FROM `transaction` WHERE id LIKE :id LIMIT 1")
    Transaction findById(int id);

    @Query("SELECT * FROM `transaction` WHERE transactionId LIKE :transactionId LIMIT 1")
    Transaction findByTransactionId(String transactionId);

    @Query("SELECT * FROM `transaction` WHERE walletId LIKE :walletId")
    List<Transaction> findByWalletId(int walletId);
    @Insert
    void insertAll(List<Transaction> transactions);

    @Insert
    void insert(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);
}
