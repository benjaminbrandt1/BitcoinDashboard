package edu.temple.bitcoindashboard.InfoFragments;

import android.content.Context;
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


/*
Fragment used to retrieve and display information about specified blocks
 */
public class BlockInfoFragment extends Fragment {

    private static final String BLOCK_NUMBER = "param1";
    private static final String BLOCKR_API = "http://btc.blockr.io/api/v1/block/info/";



    private double blockNumber;
    private TextView fee, size, difficulty, number;
    private ProgressBar progress;
    private Handler handler;
    private String doubleWithoutDecimal;

    private BlockSwitchListener mListener;

    public BlockInfoFragment() {
        // Required empty public constructor
    }


    //Factory method
    public static BlockInfoFragment newInstance(double blockNumber) {
        BlockInfoFragment fragment = new BlockInfoFragment();
        Bundle args = new Bundle();
        args.putDouble(BLOCK_NUMBER, blockNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            blockNumber = getArguments().getDouble(BLOCK_NUMBER);
            Log.d("DOUBLEVAL", String.valueOf(blockNumber));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_block_info, container, false);

        progress = (ProgressBar)v.findViewById(R.id.block_info_progress);
        progress.setVisibility(View.INVISIBLE);

        handler = new Handler();

        v.findViewById(R.id.next_block).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.nextBlock();
            }
        });

        v.findViewById(R.id.previous_block).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.previousBlock();
            }
        });

        doubleWithoutDecimal = removeDecimal(blockNumber);

        number = ((TextView)v.findViewById(R.id.current_block_number));
        number.setText(doubleWithoutDecimal);

        fee = (TextView)v.findViewById(R.id.retrieved_fee);

        size = (TextView)v.findViewById(R.id.retrieved_size);

        difficulty = (TextView)v.findViewById(R.id.retrieved_difficulty);

        retrieveBlockInfo();

        return v;
    }

    //Make a connection to the Blockr API and retrieve a JSON object containing the block information
    public void retrieveBlockInfo(){
        progress.setVisibility(View.VISIBLE);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    URL api = new URL(BLOCKR_API + doubleWithoutDecimal);

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
                    blockResponseHandler.sendMessage(msg);

                } catch (Exception e) {

                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            number.setText(R.string.block_not_found);
                            progress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
        t.start();
    }

    //Format a double as a String so that it has no decimal places and can be used in the Blockr API
    private String removeDecimal(Double doubleNum){
        Log.d("DOUBLEVAL", String.format("%.0f", doubleNum));
        return String.format("%.0f", doubleNum);
    }

    //Parse the JSON object from retrieveBlockInfo() and extract the necessary information
    private Handler blockResponseHandler = new Handler(new Handler.Callback() {

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
                    fee.setText(data.getString("fee"));
                    size.setText(data.getString("size"));
                    difficulty.setText(String.valueOf(data.getDouble("difficulty")));
                } else {
                    number.setText(R.string.block_not_found);
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
       if (context instanceof BlockSwitchListener) {
            mListener = (BlockSwitchListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BlockSwitchListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void closeKeyboard(){
        if(mListener!= null){
            mListener.hideKeyboard();
        }
    }


    public interface BlockSwitchListener {
        void nextBlock();
        void previousBlock();
        void hideKeyboard();
    }

}
