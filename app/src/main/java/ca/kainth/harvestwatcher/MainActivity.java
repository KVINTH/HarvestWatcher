package ca.kainth.harvestwatcher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ca.kainth.harvestwatcher.db.Wallet;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

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
    private TextView tvTotalBalance;
    SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<String> harvestWalletAddresses = new ArrayList<>();
    private ArrayList<String> harvestWalletDisplayTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         //set toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // USE WALLET INFORMATION TO ACCESS WALLET ADDRESS TO LOAD BALANCE FROM API
        // ADD BALANCE TO THE EXISTING WALLET IN DATABASE
        // DISPLAY INFO TO USER IN RECYCLERVIEW

        // set views
        recyclerView = findViewById(R.id.recycler_view);
        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        walletAdapter = new WalletAdapter(walletList, new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Toast.makeText(MainActivity.this, "clicked position: " + position, Toast.LENGTH_SHORT).show();
                long walletId = walletList.get(position).getId();
                Intent intent = new Intent(MainActivity.this, WalletDetailsActivity.class);
                intent.putExtra("walletId", walletId);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        refreshData();
    }

    @Override
    protected void onResume() {
        // load settings
        loadSettings();

        // retrieve information from database
        new LoadWalletsFromDatabaseTask().execute();
        super.onResume();
    }

    /**
     * This method inflates the layout of the options menu
     * @param menu - the menu to inflate
     * @return - super.onCreateOptionsMenu(menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        //MenuItem item =
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This method controls what occurs when an item is selected in the options menu.
     * @param item - the selected item.
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent prefsIntent = new Intent (MainActivity.this, PreferencesActivity.class);
                startActivity(prefsIntent);
                return true;
            case R.id.action_add:
                Intent intent = new Intent(MainActivity.this, AddWalletActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_refresh:
                //loadHarvestCoinWalletAddress();
                mSwipeRefreshLayout.setRefreshing(true);
                loadSettings();
                refreshData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This AsyncTask retrieves all wallet information from the database
     */
    private class LoadWalletsFromDatabaseTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // loads wallet information from database into a list
            walletList = App.get().getDB().walletDao().getAll();
            boolean force = App.get().isForceUpdate();
            //populateWallets(walletList);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // load wallet information from the API
            if (walletList.size() > 0) {
                for (int i = 0; i < walletList.size(); i++) {
                    // loads the wallet information from the API
                    loadWalletFromAPI(walletList.get(i).getAddress(), walletList.get(i).getName());
                }
            }
        }
    }

    private class RemoveWalletFromDatabaseTask extends AsyncTask<Void, Void, Void> {

        private int id;

        RemoveWalletFromDatabaseTask(int id) {
            this.id = id;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // retrieve specific wallet
            Wallet wallet = App.get().getDB().walletDao().findById(id);
            // delete wallet from database
            App.get().getDB().walletDao().delete(wallet);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshData();
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
    /**
     * This AsyncTask updates the specified wallet's balance in the database
     */
    private class UpdateWalletBalanceInDatabaseTask extends AsyncTask<Void, Void, Void> {

        private String name;
        private double balance;

        UpdateWalletBalanceInDatabaseTask(String name, double balance) {
            this.name = name;
            this.balance = balance;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // retrieve specific wallet
            Wallet wallet = App.get().getDB().walletDao().findByName(name);
            // add balance to wallet
            wallet.setBalance(balance);
            // update wallet in database
            App.get().getDB().walletDao().update(wallet);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    populateWallets(walletList);

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    /**
     * This method creates a new request for Wallet information from the API
     * @param address - the wallet address to get information for
     * @param name - the name of the wallet in our database
     */
    private void loadWalletFromAPI(final String address, final String name) {
        // instantiate the request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET,
                HMC_ADDRESS_ENDPOINT + address,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final double balance = Double.parseDouble(parseAddressForBalance(response));
                        new UpdateWalletBalanceInDatabaseTask(name, balance).execute();
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error loading " + name +  " Balance : "
                        + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    /**
     * This method parses a JSON response for the balance of the wallet.
     * @param body - JSON Response from Harvest Wallet API
     * @return - balance as a string
     */
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

    /**
     * Populates the RecyclerView with the list of Wallets
     * @param walletList - list of wallet objects
     */
    private void populateWallets(final List<Wallet> walletList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // prepare recycler view
                //walletAdapter = new WalletAdapter(walletList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                walletAdapter.setAdapterItems(walletList);
                recyclerView.setAdapter(walletAdapter);
                //refreshData();
                tvTotalBalance.setText(String.valueOf(calculateTotalBalance(walletList)));
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * This method loads from SharedPreferences
     */
    private void loadSettings() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        for (int i = 0; i < 10; i++) {
            if (settings.contains("W" + (i + 1) + "Alias")) {
                harvestWalletAddresses.add(i, settings.getString("W" + (i + 1) + "Address", "NA"));
                harvestWalletDisplayTags.add(i, settings.getString("W" + (i + 1) + "Alias", "NA"));
            }
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

    /**
     * This method refreshes the data within the RecyclerView */
    private void refreshData() {
        new LoadWalletsFromDatabaseTask().execute();
    }


    private Double calculateTotalBalance(List<Wallet> wallets) {
        double total = 0;
        for (Wallet wallet : wallets) {
            total += wallet.getBalance();
        }
        return total;
    }

}
