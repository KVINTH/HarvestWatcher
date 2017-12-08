package ca.kainth.harvestwatcher;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import static ca.kainth.harvestwatcher.Constants.PREFS_NAME;

public class PreferencesActivity extends AppCompatActivity {

    RadioButton radHC, radBTC;
    Button btnSavePreferences;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        preferences = getSharedPreferences(PREFS_NAME, 0);

        radBTC = findViewById(R.id.radBTC);
        radHC = findViewById(R.id.radHC);
        btnSavePreferences = findViewById(R.id.btnSavePreferences);

        btnSavePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = preferences.edit();

                if (radHC.isChecked()) {
                    editor.putString("displayCurrency", "HC");
                    Toast.makeText(PreferencesActivity.this, "Balances will now display in HC", Toast.LENGTH_SHORT).show();
                    editor.apply();
                }
                else if (radBTC.isChecked()) {
                    editor.putString("displayCurrency", "BTC");
                    Toast.makeText(PreferencesActivity.this, "Balances will now display in BTC", Toast.LENGTH_SHORT).show();
                    editor.apply();
                }


            }
        });
    }
}
