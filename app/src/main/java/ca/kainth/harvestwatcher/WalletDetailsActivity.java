package ca.kainth.harvestwatcher;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ca.kainth.harvestwatcher.db.Transaction;
import ca.kainth.harvestwatcher.db.Wallet;

import static ca.kainth.harvestwatcher.Constants.HMC_ADDRESS_ENDPOINT;
import static ca.kainth.harvestwatcher.Constants.HMC_TRANSACTION_DECRYPTED;
import static ca.kainth.harvestwatcher.Constants.HMC_TRANSACTION_ENDPOINT;

public class WalletDetailsActivity extends AppCompatActivity {
    TextView tvWalletAddress;
    long walletId;
    Wallet wallet;
    List<Transaction> transactions = new ArrayList<>();
    RecyclerView recyclerView;
    TransactionAdapter transactionAdapter;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_details);
        // set views
        recyclerView = findViewById(R.id.tx_recycler_view);
        transactionAdapter = new TransactionAdapter(transactions);
        tvWalletAddress = findViewById(R.id.tvWalletAddress);
        queue = Volley.newRequestQueue(this);
        // walletId passed from MainActivity
        walletId = getIntent().getLongExtra("walletId", -1);
        // load Wallet from the database
        new LoadWalletFromDatabaseTask().execute();



    }

    /**
     * This AsyncTask loads wallet object from the database.
     * onPostExecute loads required wallet info from the API
     */
    private class LoadWalletFromDatabaseTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // loads wallet information from database
            wallet = App.get().getDB().walletDao().findById((int)walletId);
            //populateWallets(walletList);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadWalletFromAPI(wallet.getAddress(), wallet.getName());
        }
    }

    private class SaveTransactionToDatabaseTask extends AsyncTask<Void, Void, Void> {

        String transactionId;
        int unixTimeStamp;
        double value;

        SaveTransactionToDatabaseTask(String transactionId, int unixTimeStamp, double value){
            this.transactionId = transactionId;
            this.unixTimeStamp = unixTimeStamp;
            this.value = value;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Transaction transaction = App.get().getDB().transactionDao().findByTransactionId(transactionId);
            transaction.setUnixTimeStamp(unixTimeStamp);
            transaction.setValue(value);
            //App.get().getDB().transactionDao().update(transaction);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new LoadTransactionsFromDatabaseTask().execute();
        }
    }

    private class LoadTransactionsFromDatabaseTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            transactions = App.get().getDB().transactionDao().findByWalletId((int)walletId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            populateTransactions(transactions);
        }
    }

    /**
     * This AsyncTask checks the database to see whether the transaction
     * that was loaded from the API exists in the database, and adds it if not.
     * - onPostExecute starts loadTransactionInformationFromAPI
     */
    private class CheckDatabaseForTransaction extends AsyncTask<Void, Void, Void> {

        String transactionId;

        private CheckDatabaseForTransaction(String transactionId) {
            this.transactionId = transactionId;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Create a new transaction object using the passed in transactionId
            Transaction transaction = App.get().getDB().transactionDao().findByTransactionId(transactionId);

            // in the case that the transaction is null (no transaction exists in the database with that id)
            if (transaction == null) {
                // create a new transaction
                transaction = new Transaction();
                transaction.setTransactionId(transactionId);
                transaction.setWalletId(wallet.getId());
                // insert the new transaction into the database.
                App.get().getDB().transactionDao().insert(transaction);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // uses the transactionId to load information from the API
            loadTransactionInformationFromAPI(transactionId);
        }
    }

    /**
     * This method loads the transaction from the API and then starts parseTransactionForInformation
     * @param transactionId - the transactionId to load
     */
    private void loadTransactionInformationFromAPI(final String transactionId) {

        String connectionString = HMC_TRANSACTION_ENDPOINT + transactionId + HMC_TRANSACTION_DECRYPTED;

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                HMC_TRANSACTION_ENDPOINT + transactionId + HMC_TRANSACTION_DECRYPTED,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseTransactionForInformation(response, transactionId);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(WalletDetailsActivity.this, "Error loading transaction.", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    /**
     * This method loads the wallet from the API and then starts parseAddressForTransactions
     * @param address - the wallet address to load information for
     * @param name - the wallet name in the database
     */
    private void loadWalletFromAPI(final String address, final String name) {

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                HMC_ADDRESS_ENDPOINT + address,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        final double balance = Double.parseDouble(parseAddressForBalance(response));
//                        new UpdateWalletBalanceInDatabaseTask(name, balance).execute();

                        parseAddressForTransactions(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(WalletDetailsActivity.this, "Error loading " + name +  " Balance : "
                        + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    /**
     * This method parses the transaction list from the API using the wallet address.
     * - it runs after loadWalletFromAPI - starts CheckDatabaseForTransaction
     * @param response - the string response from loadWalletFromAPI
     */
    private void parseAddressForTransactions(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray lastTransactionsArray = jsonObject.getJSONArray("last_txs");
            // get a list of all transactions for wallet
            for (int i=0; i<lastTransactionsArray.length(); i++) {
                JSONObject transactionObject = lastTransactionsArray.getJSONObject(i);
                String transactionId = transactionObject.getString("addresses");
                // call asynctask to check if transaction id already exists in the database

                new CheckDatabaseForTransaction(transactionId).execute();
            }

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method parses the transaction information from the API
     * @param response
     * @return
     */
    private String parseTransactionForInformation(String response, String transactionId) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray voutArray = jsonObject.getJSONArray("vout");
            int unixTimeStamp = Integer.valueOf(jsonObject.getString("time"));

            //Log.d("KAINTHCA", "unixTimeStamp: " + String.valueOf(unixTimeStamp));

            // add all/ the vouts to a list to be searched
            //List<JSONObject> vouts = new ArrayList<>();
            for (int i = 0; i < voutArray.length(); i++) {
                //vout/s.add(voutArray.getJSONObject(i));
                Double value = voutArray.getJSONObject(i).getDouble("value");
                if (value > 0) {
                    //Log.d("KAINTHCA", "value: " + value);
                    JSONObject scriptPubKeyObject = voutArray.getJSONObject(i).getJSONObject("scriptPubKey");
                    // load output addresses from scriptPubKey
                    JSONArray addresses = scriptPubKeyObject.getJSONArray("addresses");
                    for (int j = 0; j < addresses.length(); j++) {
                        // check if output addresses contain our current wallet address
                        //Log.d("KAINTHCA", "our current wallet address:" + wallet.getAddress());
                        //Log.d("KAINTHCA", "transaction wallet #" + j + ": " + addresses.get(j));
                        if (addresses.getString(j).equals(wallet.getAddress())) {
                            // our wallet was found - save the transaction information to the database.
                            new SaveTransactionToDatabaseTask(transactionId, unixTimeStamp, value).execute();

                        }
                    }
                }
            }

        } catch (Exception e) {
            Toast.makeText(this, "some sort of exception", Toast.LENGTH_SHORT).show();
        }

        return "";
    }

    private void populateTransactions(final List<Transaction> transactions) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                transactionAdapter.setAdapterItems(transactions);
                recyclerView.setAdapter(transactionAdapter);
                transactionAdapter.notifyDataSetChanged();
            }
        });
    }
}
