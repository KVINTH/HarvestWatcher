package ca.kainth.harvestwatcher.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface WalletDao {

    @Query("SELECT * FROM wallet")
    List<Wallet> getAll();

    @Query("SELECT * FROM wallet WHERE id LIKE :id LIMIT 1")
    Wallet findById(int id);

    @Query("SELECT * FROM wallet WHERE name LIKE :name LIMIT 1")
    Wallet findByName(String name);

    @Insert
    void insertAll(List<Wallet> wallets);

    @Insert
    void insert(Wallet wallet);

    @Update
    void update(Wallet wallet);

    @Delete
    void delete(Wallet wallet);
}
