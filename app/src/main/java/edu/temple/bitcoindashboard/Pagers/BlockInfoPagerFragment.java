package edu.temple.bitcoindashboard.Pagers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import edu.temple.bitcoindashboard.InfoFragments.BlockInfoFragment;
import edu.temple.bitcoindashboard.MainActivity;
import edu.temple.bitcoindashboard.R;
import edu.temple.bitcoindashboard.TabFragment;


/*
Placeholder fragment. In the future, this will contain the pager that allows users to swipe between BlockInfoFragments
This will simulate swiping through the block chain
Currently, it just displays one BlockInfoFragment
 */
public class BlockInfoPagerFragment extends Fragment implements TabFragment, BlockInfoFragment.BlockSwitchListener {
    private static String title;
    private EditText blockEntry;
    private Button submit;
    private double number;

    public BlockInfoPagerFragment() {
        // Required empty public constructor
    }


    public static BlockInfoPagerFragment newInstance(Bundle args) {
        BlockInfoPagerFragment fragment = new BlockInfoPagerFragment();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getActivity().getString(R.string.block_info);
        if (getArguments() != null) {
            number = getArguments().getDouble(MainActivity.BLOCK_PAGER_INDEX, -100);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_block_info_pager, container, false);

        blockEntry = (EditText)v.findViewById(R.id.block_number_entry);

        submit = (Button)v.findViewById(R.id.block_go_button);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberStr = blockEntry.getText().toString();
                try{
                    number = Double.parseDouble(numberStr);
                    BlockInfoFragment blockInfoFragment = BlockInfoFragment.newInstance(number);
                    FragmentManager fm = getChildFragmentManager();
                    fm.beginTransaction()
                            .replace(R.id.block_fragment_frame, blockInfoFragment)
                            .commit();
                    fm.executePendingTransactions();
                    blockInfoFragment.closeKeyboard();

                } catch (NumberFormatException e){
                    Log.d("NUMFORMAT", "BLOCK INFO PAGER");

                }

            }
        });

        if(number >= 0){
            removeDecimalAndSubmit(number);
        }

        return v;
    }

    @Override
    public String getTitle() {
        return title;
    }

    //Increment the value in the block number field and retrieve its information
    @Override
    public void nextBlock() {
        double current = 0;
        try {
            current = Double.parseDouble(blockEntry.getText().toString());
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        current++;
        removeDecimalAndSubmit(current);
    }

    //Decrement the value in the block number field and retrieve its information
    @Override
    public void previousBlock() {
        double current = 0;
        try {
            current = Double.parseDouble(blockEntry.getText().toString());
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        current--;
        removeDecimalAndSubmit(current);
    }

    @Override
    public void hideKeyboard() {

    }

    public Bundle getState(Bundle outState) {
        outState.putDouble(MainActivity.BLOCK_PAGER_INDEX, number);
        return outState;
    }

    //Remove the decimal from the double value so that it can be used in the Blockr API
    private void removeDecimalAndSubmit(double current){
        String temp = String.format("%.0f", current);
        Log.d("DOUBLEVAL", temp);
        //String temp = String.valueOf(current);
        //blockEntry.setText(temp.substring(0, temp.length()-2));
        blockEntry.setText(temp);
        submit.callOnClick();
    }

}
