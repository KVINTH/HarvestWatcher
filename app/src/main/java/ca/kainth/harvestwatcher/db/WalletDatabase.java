package ca.kainth.harvestwatcher.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

@Database(entities =  {Wallet.class, Transaction.class}, version = 5)
//@TypeConverters({})
public abstract class WalletDatabase extends RoomDatabase {
    public abstract WalletDao walletDao();
    public abstract TransactionDao transactionDao();

//    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//
//        }
//    };
    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `transaction` (`id` INTEGER, "
                    + "`transactionId` TEXT, "
                    + "`walletId` INTEGER,"
                    + "PRIMARY KEY(`id`))");
        }
    };
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
