package ca.kainth.harvestwatcher;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.SharedPreferences;

import ca.kainth.harvestwatcher.db.WalletDatabase;

/**
 * Created by Harry on 11/25/2017.
 */

public class App extends Application {

    public static App INSTANCE;
    private static final String DATABASE_NAME = "HKWalletDatabase";
    private static final String PREFERENCES = "KAINTHCA_CRYPTO_PREFS_FILE";
    private static final String KEY_FORCE_UPDATE = "force_update";

    private WalletDatabase walletDatabase;

    public static App get() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // create database
        walletDatabase = Room.databaseBuilder(getApplicationContext(), WalletDatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration()
//                .addMigrations(WalletDatabase.MIGRATION_1_2)
//                .addMigrations(WalletDatabase.MIGRATION_2_3)
                .build();

        INSTANCE = this;
    }

    public WalletDatabase getDB() {
        return walletDatabase;
    }

    public boolean isForceUpdate() {
        return  getSP().getBoolean(KEY_FORCE_UPDATE, true);
    }

    public void setForceUpdate(boolean force) {
        SharedPreferences.Editor edit = getSP().edit();
        edit.putBoolean(KEY_FORCE_UPDATE, force);
        edit.apply();
    }

    private SharedPreferences getSP() {
        return getSharedPreferences(PREFERENCES, MODE_PRIVATE);
    }
}
