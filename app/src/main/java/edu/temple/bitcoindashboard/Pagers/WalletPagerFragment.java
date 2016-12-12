package edu.temple.bitcoindashboard.Pagers;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.temple.bitcoindashboard.InfoFragments.AddWalletFragment;
import edu.temple.bitcoindashboard.InfoFragments.StoredWalletsFragment;
import edu.temple.bitcoindashboard.InfoFragments.WalletInfoFragment;
import edu.temple.bitcoindashboard.MainActivity;
import edu.temple.bitcoindashboard.R;
import edu.temple.bitcoindashboard.TabFragment;

import static edu.temple.bitcoindashboard.InfoFragments.AddWalletFragment.ADD_WALLET_ADDRESS;
import static edu.temple.bitcoindashboard.InfoFragments.WalletInfoFragment.ADDRESS_KEY;

/*
Fragment containing ViewPager to display StoredWalletsFragment, WalletInfoFragment, and AddWalletFragment
 */

public class WalletPagerFragment extends Fragment implements TabFragment, StoredWalletsFragment.WalletClickedListener,
        AddWalletFragment.AddWalletListener{
    private WalletPagerAdapter walletPagerAdapter;
    private ViewPager viewPager;
    private ArrayList<String> tabTitles;
    private String walletAddress;
    private static WalletInfoFragment walletInfoFragment;
    private static StoredWalletsFragment storedWalletsFragment;
    private static AddWalletFragment addWalletFragment;
    private static int current;
    private static String savedWalletAddress;
    private static String addWalletAddress;

    private static String title;


    public WalletPagerFragment() {
        // Required empty public constructor
    }

    //Factory Method
    public static WalletPagerFragment newInstance(Bundle args) {
        WalletPagerFragment fragment = new WalletPagerFragment();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getActivity().getString(R.string.wallet_info);
        tabTitles = new ArrayList<>();
        if (getArguments() != null) {
            current = getArguments().getInt(MainActivity.WALLET_PAGER_INDEX, -100);
            savedWalletAddress = getArguments().getString(ADDRESS_KEY);
            addWalletAddress = getArguments().getString(ADD_WALLET_ADDRESS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_wallet_pager, container, false);

        tabTitles.add(getActivity().getString(R.string.stored_wallets));
        tabTitles.add(getActivity().getString(R.string.wallet_info_text));
        tabTitles.add(getActivity().getString(R.string.add_wallet));

        viewPager = (ViewPager)v.findViewById(R.id.wallet_pager);
        walletPagerAdapter = new WalletPagerAdapter(getChildFragmentManager(), tabTitles);

        viewPager.setAdapter(walletPagerAdapter);
        TabLayout tabLayout = (TabLayout)v.findViewById(R.id.wallet_pager_tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                current = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if(current >= 0){
            viewPager.setCurrentItem(current);
        }

        return v;
    }

    @Override
    public String getTitle() {
        return title;
    }

    //Switch the page to the WalletInfoFragment and search for the given address
    @Override
    public void showWalletDetails(String address) {
        walletInfoFragment.setAddress(address);
        viewPager.setCurrentItem(1);
    }

    //Switch the page to the AddWalletFragment
    @Override
    public void newWallet(){
        viewPager.setCurrentItem(2);
    }

    //Switch the page to the StoredWalletsFragment
    @Override
    public void returnToWalletList() {
        viewPager.setCurrentItem(0);
        storedWalletsFragment.getAddressArray();
    }

    @Override
    public void hideKeyboard() {

    }

    public Bundle getState(Bundle outState) {
        outState.putInt(MainActivity.WALLET_PAGER_INDEX, current);
        Log.d("WALLET_CURRENT", String.valueOf(current));
        return getCurrentPageState(outState);
    }

    //Gets the state information of the fragment that's currently being displayed in the pager
    private Bundle getCurrentPageState(Bundle outState){
        Log.d("Storing", String.valueOf(current));
        switch(current){
            case 0:
                //Stored wallets, no info needs to be saved
                break;
            case 1:
                if(walletInfoFragment != null){
                    return walletInfoFragment.getState(outState);
                }
                break;
            case 2:
                if(addWalletFragment != null){
                    return addWalletFragment.getState(outState);
                }
                break;
            default:
                break;
        }
        return outState;
    }

    public static class WalletPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<String> tabTitles;

        public WalletPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public WalletPagerAdapter(FragmentManager fm, ArrayList<String> tabTitles){
            super(fm);
            this.tabTitles = tabTitles;
        }

        @Override
        public Fragment getItem(int i) {
            Log.d("WALLET_CURRENT2", String.valueOf(current));
            switch (i) {
                case 0:
                    if(storedWalletsFragment == null) {
                        storedWalletsFragment = StoredWalletsFragment.newInstance();
                    }
                    return storedWalletsFragment;

                case 1:
                    Log.d("SendingAddress", "YO");
                    walletInfoFragment= WalletInfoFragment.newInstance(savedWalletAddress);
                    return walletInfoFragment;

                case 2:
                    addWalletFragment = AddWalletFragment.newInstance(addWalletAddress);
                    return addWalletFragment;

                default:
                    Log.d("WalletPagerError", "Index out of bounds");
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Log.d("GETTING TITLES", tabTitles.get(position));
            return tabTitles.get(position);
        }
    }

}
