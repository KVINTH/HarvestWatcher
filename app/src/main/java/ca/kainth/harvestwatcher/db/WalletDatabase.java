package ca.kainth.harvestwatcher.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities =  {Wallet.class}, version = 1)
//@TypeConverters({})
public abstract class WalletDatabase extends RoomDatabase {
    public abstract WalletDao walletDao();

//    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE wallet "
//            + "ADD COLUMN something STRING");
//
//            // enable flag to force update wallets
//            App.get().setForceUpdate(true);
//        }
//    };
}
