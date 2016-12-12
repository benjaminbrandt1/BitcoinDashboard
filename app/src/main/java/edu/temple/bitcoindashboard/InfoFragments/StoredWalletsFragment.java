package edu.temple.bitcoindashboard.InfoFragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import edu.temple.bitcoindashboard.DataBase.WalletDBContract;
import edu.temple.bitcoindashboard.DataBase.WalletDBHelper;
import edu.temple.bitcoindashboard.R;
import edu.temple.bitcoindashboard.TabFragment;

/**
 * Fragment that retrieves a list of stored wallet addresses from the database
 * Addresses are displayed as a ListView
 * When clicked, addresses are entered and searched for in the WalletInfoFragment
 */
public class StoredWalletsFragment extends Fragment implements TabFragment {

    private static final String title="Stored Wallets";
    private WalletClickedListener mListener;
    private ListView addressList;
    private String[] addresses;
    SQLiteDatabase db;
    WalletDBHelper mDbHelper;

    public StoredWalletsFragment() {
        // Required empty public constructor
    }

    //Factory method
    public static StoredWalletsFragment newInstance() {
        StoredWalletsFragment fragment = new StoredWalletsFragment();
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
        View v =  inflater.inflate(R.layout.fragment_stored_wallets, container, false);

        mDbHelper = new WalletDBHelper(getActivity());

        addressList = (ListView)v.findViewById(R.id.address_list);
        getAddressArray();

        addressList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String str = ((TextView)view.findViewById(R.id.this_address)).getText().toString();

                mListener.showWalletDetails(str);
               // Log.d("POSITION", String.valueOf(position));
            }
        });

        ((FloatingActionButton)v.findViewById(R.id.floatingActionButtonWallets))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.newWallet();
                    }
                });

        return v;
    }

    //Retrieve list of addresses from the database
    public void getAddressArray(){

        db = mDbHelper.getReadableDatabase();

        Cursor cursor = db.query(WalletDBContract.WalletEntry.TABLE_NAME,
                new String[]{"_id", WalletDBContract.WalletEntry.COLUMN_NAME_ADDRESS}, null, null, null, null, null);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.wallet_list_item, cursor, new String[]{WalletDBContract.WalletEntry.COLUMN_NAME_ADDRESS}, new int[]{R.id.this_address}, 0);


        addressList.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WalletClickedListener) {
            mListener = (WalletClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement WalletClickedListener");
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


    public interface WalletClickedListener {
        void showWalletDetails(String address);
        void newWallet();
    }

}
