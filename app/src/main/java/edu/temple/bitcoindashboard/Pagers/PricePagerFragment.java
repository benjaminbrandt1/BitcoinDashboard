package edu.temple.bitcoindashboard.Pagers;

import android.content.Context;
import android.net.Uri;
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

import edu.temple.bitcoindashboard.InfoFragments.CurrentPriceFragment;
import edu.temple.bitcoindashboard.InfoFragments.PriceTrackerFragment;
import edu.temple.bitcoindashboard.MainActivity;
import edu.temple.bitcoindashboard.R;


/*
Fragment containing a FragmentPager to display PriceTrackerFragment and CurrentPriceFragment
 */
public class PricePagerFragment extends Fragment{
    private PricePagerAdapter pricePagerAdapter;
    private ViewPager viewPager;
    private static String title;
    private ArrayList<String> tabTitles;
    private static CurrentPriceFragment currentPriceFragment;
    private static PriceTrackerFragment priceTracker;
    private static int current;

    public PricePagerFragment() {
        // Required empty public constructor
    }

    //Factory Method
    public static PricePagerFragment newInstance(Bundle args) {
        PricePagerFragment fragment = new PricePagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getActivity().getString(R.string.price_information);
        tabTitles = new ArrayList<>();
        if (getArguments() != null) {
            current = getArguments().getInt(MainActivity.PRICE_PAGER_INDEX, -100);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_price_pager, container, false);

        tabTitles.add(getActivity().getString(R.string.current_price));
        tabTitles.add(getActivity().getString(R.string.price_tracker));


        viewPager = (ViewPager)v.findViewById(R.id.price_pager);
        pricePagerAdapter = new PricePagerAdapter(getChildFragmentManager(), tabTitles);
        viewPager.setAdapter(pricePagerAdapter);

        TabLayout tabLayout = (TabLayout)v.findViewById(R.id.price_pager_tabs);
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
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //TODO if interface attached, nullify here
    }

    public Bundle getState(Bundle outState) {
        outState.putInt(MainActivity.PRICE_PAGER_INDEX, current);
        return outState;
    }

    /*
    Adapter for the ViewPager
     */
    public static class PricePagerAdapter extends FragmentPagerAdapter {

        private ArrayList<String> tabTitles;
        public PricePagerAdapter(FragmentManager fm) {
            super(fm);
        }
        public PricePagerAdapter(FragmentManager fm, ArrayList<String> tabTitles){
            super(fm);
            this.tabTitles = tabTitles;
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    if(currentPriceFragment == null) {
                        currentPriceFragment = CurrentPriceFragment.newInstance();
                    }
                    return currentPriceFragment;

                case 1:
                    if(priceTracker == null){
                        priceTracker = PriceTrackerFragment.newInstance();
                    }
                    return priceTracker;

                default:
                    Log.d("PricePagerError", "Index out of bounds");
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Log.d("GETTING TITLES", tabTitles.get(position));
         return tabTitles.get(position);
        }
    }
}
