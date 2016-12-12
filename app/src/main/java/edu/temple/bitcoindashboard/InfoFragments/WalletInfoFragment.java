package edu.temple.bitcoindashboard.InfoFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import edu.temple.bitcoindashboard.R;
import edu.temple.bitcoindashboard.TabFragment;

/**
 * Fragment that retrieves information about wallet addresses
 * User can enter an address in addressEntry and search with searchButton
 */
public class WalletInfoFragment extends Fragment implements TabFragment{
    public static final String ADDRESS_KEY = "address";
    private static final String title = "Wallet Information";
    private static final String BLOCKR_API = "http://btc.blockr.io/api/v1/address/info/";
    private String address;
    private EditText addressEntry;
    private Button searchButton;
    private TextView addressText, balanceText, totRecText, dateText, blockText, valText;
    private ProgressBar progress;
    private Handler handler;

    public WalletInfoFragment() {
        // Required empty public constructor
    }

    //Factory Method
    public static WalletInfoFragment newInstance(String address) {
        WalletInfoFragment fragment = new WalletInfoFragment();
        Bundle args = new Bundle();
        args.putString(ADDRESS_KEY, address);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            address = getArguments().getString(ADDRESS_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_wallet_info, container, false);

        addressEntry = (EditText)v.findViewById(R.id.wallet_address_entry);

        searchButton = (Button)v.findViewById(R.id.wallet_search_button);

        //Initialize all of the textviews for the retrieved information
        addressText = (TextView)v.findViewById(R.id.wallet_address);
        balanceText = (TextView)v.findViewById(R.id.balance_amount);
        totRecText = (TextView)v.findViewById(R.id.total_received_amount);
        dateText = (TextView)v.findViewById(R.id.date_amount);
        blockText = (TextView)v.findViewById(R.id.block_value);
        valText = (TextView)v.findViewById(R.id.last_tx_value);

        progress = (ProgressBar)v.findViewById(R.id.wallet_progress_bar);
        progress.setVisibility(View.INVISIBLE);

        handler = new Handler();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearViews();
                searchForWallet(addressEntry.getText().toString());
            }
        });

        if(address != null){
            addressEntry.setText(address);
            searchButton.callOnClick();
        }

        return v;
    }

    //Set all of the information to null
    private void clearViews(){
        balanceText.setText(null);
        totRecText.setText(null);
        dateText.setText(null);
        blockText.setText(null);
        valText.setText(null);
    }

    //Set the address in addressEntry and search for the given address
    public void setAddress(String address){
        addressEntry.setText(address);
        addressText.setText("");
        searchButton.callOnClick();

    }

    //Retrieve information about the given address using the Blockr API
    //Information is returned as a JSON object, which is parsed by the walletResponseHandler
    private void searchForWallet(final String address){
        progress.setVisibility(View.VISIBLE);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    URL api = new URL(BLOCKR_API + address);

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    api.openStream()));

                    String response = "", tmpResponse;

                    tmpResponse = reader.readLine();
                    while (tmpResponse != null) {
                        response = response + tmpResponse;
                        tmpResponse = reader.readLine();
                    }

                    JSONObject blockInfo = new JSONObject(response);
                    Message msg = Message.obtain();
                    msg.obj = blockInfo;
                    walletResponseHandler.sendMessage(msg);
                } catch (Exception e) {

                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            addressText.setText(R.string.wallet_not_found);
                            progress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
        t.start();
    }

    //Parse the JSON object from searchForWallet() and put the information in the proper fields
    private Handler walletResponseHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            JSONObject responseObject = (JSONObject) msg.obj;

            try {
                String status;
                Log.d("RETRIEVING", "STATUS");
                status = responseObject.getString("status");
                Log.d("RETRIEVING", status);

                if(status.equalsIgnoreCase("success")) {
                    JSONObject data = responseObject.getJSONObject("data");
                    Log.d("TX", data.toString());
                    balanceText.setText(String.valueOf(data.getDouble("balance")));
                    totRecText.setText(String.valueOf(data.getDouble("totalreceived")));

                    JSONObject lastTx = data.getJSONObject("last_tx");
                    Log.d("TX", lastTx.toString());
                    dateText.setText(lastTx.getString("time_utc"));
                    blockText.setText(lastTx.getString("block_nb"));
                    valText.setText(lastTx.getString("value"));
                } else {
                    addressText.setText(R.string.wallet_not_found);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            progress.setVisibility(View.INVISIBLE);

            return false;
        }
    });


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //TODO if interface attached, nullify here
    }

    @Override
    public String getTitle() {
        return title;
    }

    public Bundle getState(Bundle outState) {
        outState.putString(ADDRESS_KEY, addressEntry.getText().toString());
        return outState;
    }


}
