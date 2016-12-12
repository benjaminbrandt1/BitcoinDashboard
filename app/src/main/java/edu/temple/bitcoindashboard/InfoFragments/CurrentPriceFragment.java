package edu.temple.bitcoindashboard.InfoFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import edu.temple.bitcoindashboard.R;
import edu.temple.bitcoindashboard.TabFragment;


/**
 * Fragment that retrieves and displays the current price of a Bitcoin in USD.
 * Uses Blockr API
 * Although the price is retrieved continuously, a thread is used because it only runs
 while this fragment is in view, unlike a service, which would run continuously even when the fragment
 was not in view
 */
public class CurrentPriceFragment extends Fragment implements TabFragment {
    public static String title, priceApi;
    private String price;
    private TextView priceTextView;
    private boolean currentFragment;
    private ProgressBar progress;
    private Handler handler;
    private boolean visible;

    public CurrentPriceFragment() {
        // Required empty public constructor
    }

    //Factory method
    public static CurrentPriceFragment newInstance() {
        CurrentPriceFragment fragment = new CurrentPriceFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_current_price, container, false);
        currentFragment = true;

        handler = new Handler();

        title = getActivity().getString(R.string.current_price);
        Log.d("GETTING TITLE", title);

        priceApi = getActivity().getString(R.string.price_api);

        progress = (ProgressBar)v.findViewById(R.id.current_price_progress);
        progress.setVisibility(View.INVISIBLE);
        visible = false;

        priceTextView = (TextView)v.findViewById(R.id.price);

        //Thread that runs every 15 seconds to retrieve the current price
        Thread timer = new Thread(){
            @Override
            public void run() {
                while (currentFragment) {

                    try {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progress.setVisibility(View.VISIBLE);
                            }
                        });

                        URL api = new URL(priceApi);

                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(
                                        api.openStream()));

                        String response = "", tmpResponse;

                        tmpResponse = reader.readLine();
                        while (tmpResponse != null) {
                            response = response + tmpResponse;
                            tmpResponse = reader.readLine();
                        }

                        JSONObject coinInfo = new JSONObject(response);

                        Message msg = Message.obtain();
                        msg.obj = coinInfo;
                        priceResponseHandler.sendMessage(msg);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progress.setVisibility(View.INVISIBLE);
                            }
                        });
                        Thread.sleep(20000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return;
            }

        };
        timer.start();

        return v;
    }

    public void setPrice(String price){
        price = "$" + price;
        priceTextView.setText(price);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        currentFragment = false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //TODO if interface is added, nullify it here
    }

    @Override
    public String getTitle() {
        return title;
    }

    //Handler to receive JSON object returned from the Blockr API
    //Handler parses the object and obtains the current price
    private Handler priceResponseHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            JSONObject responseObject = (JSONObject) msg.obj;

            try {
                String temp;
                temp = responseObject.getJSONObject("data")
                        .getJSONObject("markets")
                        .getJSONObject("coinbase")
                        .getString("value");
                price = "$" + temp;
            } catch (Exception e) {
                e.printStackTrace();
            }

            priceTextView.setText(price);

            return false;
        }
    });


}
