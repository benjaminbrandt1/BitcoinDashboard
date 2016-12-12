package edu.temple.bitcoindashboard.InfoFragments;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import edu.temple.bitcoindashboard.DataBase.WalletDBContract;
import edu.temple.bitcoindashboard.DataBase.WalletDBHelper;
import edu.temple.bitcoindashboard.R;
import edu.temple.bitcoindashboard.TabFragment;

/*
This class displays a text field to enter new wallet addresses. When a user presses save, the address
is stored in the SQLite database
 */
public class AddWalletFragment extends Fragment implements TabFragment{
    private final String title = "Add Wallet";
    public final static String ADD_WALLET_ADDRESS = "addWallet";
    private EditText walletAddress;
    private Button save;
    SQLiteDatabase db;
    WalletDBHelper mDbHelper;
    private String address;


    private AddWalletListener mListener;

    public AddWalletFragment() {
        // Required empty public constructor
    }


    //Factory method
    public static AddWalletFragment newInstance(String address) {
        AddWalletFragment fragment = new AddWalletFragment();
        Bundle args = new Bundle();
        args.putString(ADD_WALLET_ADDRESS, address);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            address = getArguments().getString(ADD_WALLET_ADDRESS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_add_wallet, container, false);

        walletAddress = (EditText)v.findViewById(R.id.add_wallet_address);

        save = (Button)v.findViewById(R.id.wallet_save);

        //When the save button is clicked, the text in walletAddress is stored in the SQLite database
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address =walletAddress.getText().toString();

                if(address.equalsIgnoreCase("") || address == null){
                    return;
                }
                saveAddress(address);
                walletAddress.setText(null);
                mListener.hideKeyboard();
                mListener.returnToWalletList();
            }
        });

        mDbHelper = new WalletDBHelper(getActivity());

        if(address != null){
            walletAddress.setText(address);
        }

        return v;
    }

    //Get access to the database and store the given String
    private void saveAddress(String address){

        db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WalletDBContract.WalletEntry.COLUMN_NAME_ADDRESS, address);

        long newRowId;
        newRowId = db.insert(
                WalletDBContract.WalletEntry.TABLE_NAME,
                null,
                values);

        if(newRowId > 0){
            Log.d("Wallet Saved", newRowId + ": " + address);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddWalletListener) {
            mListener = (AddWalletListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AddWalletListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public String getTitle() {
        return title;
    }

    //Called by the pager class, this method adds this fragment's important state info to the app's saved state
    public Bundle getState(Bundle outState) {
        outState.putString(ADD_WALLET_ADDRESS, walletAddress.getText().toString());
        Log.d("Storing", walletAddress.getText().toString());
        return outState;

    }


    public interface AddWalletListener {
        void returnToWalletList();
        void hideKeyboard();
    }
}
