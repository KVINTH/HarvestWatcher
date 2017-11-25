package ca.kainth.harvestwatcher;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddWalletActivity extends AppCompatActivity {

    EditText etWalletAddress;
    EditText etWalletAlias;
    EditText etWalletNumber;
    Button btnAddWallet;
    static String walletName;
    static String walletAddress;

    private static final String PREFS_NAME = "KAINTHCA_CRYPTO_PREFS_FILE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wallet);

        etWalletAddress = findViewById(R.id.etWalletAddress);
        etWalletAlias = findViewById(R.id.etWalletAlias);
        //etWalletNumber = findViewById(R.id.etWalletNumber);
        btnAddWallet = findViewById(R.id.btnAddWallet);

        btnAddWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                walletName = etWalletAlias.getText().toString();
                walletAddress = etWalletAddress.getText().toString();
                boolean isError = false;

                // create a new Wallet object to populate with data


                if (walletName.equals("")) {
                    Toast.makeText(AddWalletActivity.this, "Please enter a name for your wallet", Toast.LENGTH_SHORT).show();
                    isError = true;
                }
                if (walletAddress.equals("")) {
                    Toast.makeText(AddWalletActivity.this, "Please enter your wallet address", Toast.LENGTH_SHORT).show();
                    isError = false;
                }

                if (!isError) {
                    new DatabaseAsync().execute();
                }



//
//                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//                SharedPreferences.Editor editor = settings.edit();
//
//
//                if (etWalletAddress.getText().toString().equals("") || etWalletAlias.getText().toString().equals("")) {
//
//                    isError = true;
//                }
//
//                if (!isError) {
//                    String walletIdentifier = "W";
//                    String addressIdentifier = "Address";
//                    String aliasIdentifier = "Alias";
//                    String walletAddress = etWalletAddress.getText().toString();
//                    String walletAlias = etWalletAlias.getText().toString();
//
//                    editor.putString(walletIdentifier + walletNumber + addressIdentifier, walletAddress);
//                    editor.putString(walletIdentifier + walletNumber + aliasIdentifier, walletAlias);
//                    editor.apply();
//                    Toast.makeText(AddWalletActivity.this, "Wallet " + walletNumber + " successfully saved.", Toast.LENGTH_SHORT).show();
//                }
            }
        });

    }

    static class DatabaseAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // perform pre adding operation here
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ca.kainth.harvestwatcher.db.Wallet wallet = new ca.kainth.harvestwatcher.db.Wallet();
            wallet.setName(walletName);
            wallet.setAddress(walletAddress);

            // insert data into DB
            App.get().getDB().walletDao().insert(wallet);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
