package ca.kainth.harvestwatcher;

/**
 * Created by Harry on 11/29/2017.
 */

public final class Constants {

    private Constants() {

    }
    public static final String BTC_API_ENDPOINT = "https://api.coindesk.com/v1/bpi/currentprice.json";
    public static final String HC_API_ENDPOINT = "https://coinsmarkets.com/apicoin.php";
    public static final String HMC_ADDRESS_ENDPOINT = "http://hmcexplorer.com/ext/getaddress/";
    public static final String HMC_TRANSACTION_ENDPOINT = "http://hmcexplorer.com/api/getrawtransaction?txid=";
    public static final String HMC_TRANSACTION_DECRYPTED = "&decrypt=1";
    public static final String HMC_BTC_RATE_ENDPOINT = "https://www.cryptopia.co.nz/api/GetMarket/HC_BTC";
}
