package ca.kainth.harvestwatcher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // API ENDPOINTS
    private static final String BTC_API_ENDPOINT = "https://api.coindesk.com/v1/bpi/currentprice.json";
    private static final String HC_API_ENDPOINT = "https://coinsmarkets.com/apicoin.php";
    private static final String HMC_ADDRESS_ENDPOINT = "http://hmcexplorer.com/ext/getaddress/";

    // PREFERENCES
    private static final String PREFS_NAME = "KAINTHCA_CRYPTO_PREFS_FILE";

    // CLASS VARIABLES
    private List<Wallet> walletList = new ArrayList<>();
    private RecyclerView recyclerView;
    private WalletAdapter walletAdapter;

    private ArrayList<String> harvestWalletAddresses = new ArrayList<>();
    private ArrayList<String> harvestWalletDisplayTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         //set toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // set views
        recyclerView = findViewById(R.id.recycler_view);

        // load settings
        loadSettings();

        // prepare recycler view
        walletAdapter = new WalletAdapter(walletList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(walletAdapter);

        // load wallets
        if (harvestWalletAddresses.size() > 0) {
            for (int i = 0; i < harvestWalletAddresses.size(); i++) {
                loadHarvestCoinWalletAddress(harvestWalletAddresses.get(i), harvestWalletDisplayTags.get(i));
            }
        }


    }

    private void loadHarvestCoinWalletAddress(final String address, final String displayTag) {
        // instantiate the request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET,
                HMC_ADDRESS_ENDPOINT + address,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final double balance = Double.parseDouble(parseAddressForBalance(response));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Wallet wallet = new Wallet(displayTag, address, balance);
                                walletList.add(wallet);
                                walletAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error loading " + displayTag +  " Balance : "
                        + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    private String parseAddressForBalance(String body) {
        String balance = "0";
        try {
            JSONObject jsonObject = new JSONObject(body);
            balance = jsonObject.getString("balance");

//            totalWalletBalance += Double.parseDouble(balance);
//            DecimalFormat df = new DecimalFormat("#.000");
//            String formattedTotalBalance =  df.format(totalWalletBalance);

            //tvTotalWalletBalance.setText("Total Balance: " + formattedTotalBalance + " HC");
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return balance;
    }

    private void loadSettings() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        for (int i = 0; i < 10; i++) {
            if (settings.contains("W" + (i + 1) + "Alias")) {
                harvestWalletAddresses.add(i, settings.getString("W" + (i + 1) + "Address", "NA"));
                harvestWalletDisplayTags.add(i, settings.getString("W" + (i + 1) + "Alias", "NA"));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        //MenuItem item =
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_add:
                Intent intent = new Intent(MainActivity.this, AddWalletActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_refresh:
                //loadHarvestCoinWalletAddress();
                loadSettings();
                walletAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String parseBpiForPrice(String body) {
        String price = "0";
        try {
            JSONObject jsonObject = new JSONObject(body);
            JSONObject bpiObject = jsonObject.getJSONObject("bpi");
            JSONObject usdObject = bpiObject.getJSONObject("USD");
            price = usdObject.getString("rate");
        } catch (Exception e) {
            Toast.makeText(this, "Error processing BTC Price: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return price;
    }

    @Override
    protected void onResume() {
        loadSettings();
        walletAdapter.notifyDataSetChanged();
        super.onResume();
    }
}
