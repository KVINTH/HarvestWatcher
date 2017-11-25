package ca.kainth.harvestwatcher;

import android.content.SharedPreferences;
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

    private static final String PREFS_NAME = "KAINTHCA_CRYPTO_PREFS_FILE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wallet);

        etWalletAddress = findViewById(R.id.etWalletAddress);
        etWalletAlias = findViewById(R.id.etWalletAlias);
        etWalletNumber = findViewById(R.id.etWalletNumber);
        btnAddWallet = findViewById(R.id.btnAddWallet);

        btnAddWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();

                boolean isError = false;
                int walletNumber = Integer.valueOf(etWalletNumber.getText().toString());

                if (walletNumber < 1 ||
                    walletNumber > 10 ) {
                    Toast.makeText(AddWalletActivity.this, "Please enter a number between 1 and 10", Toast.LENGTH_SHORT).show();
                    isError = true;
                }
                if (etWalletAddress.getText().toString().equals("") || etWalletAlias.getText().toString().equals("")) {
                    Toast.makeText(AddWalletActivity.this, "Please enter a wallet address & alias", Toast.LENGTH_SHORT).show();
                    isError = true;
                }

                if (!isError) {
                    String walletIdentifier = "W";
                    String addressIdentifier = "Address";
                    String aliasIdentifier = "Alias";
                    String walletAddress = etWalletAddress.getText().toString();
                    String walletAlias = etWalletAlias.getText().toString();

                    editor.putString(walletIdentifier + walletNumber + addressIdentifier, walletAddress);
                    editor.putString(walletIdentifier + walletNumber + aliasIdentifier, walletAlias);
                    editor.apply();
                    Toast.makeText(AddWalletActivity.this, "Wallet " + walletNumber + "successfully saved.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
