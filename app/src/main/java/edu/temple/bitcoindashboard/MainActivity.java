package edu.temple.bitcoindashboard;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import edu.temple.bitcoindashboard.InfoFragments.AddWalletFragment;
import edu.temple.bitcoindashboard.InfoFragments.BlockInfoFragment;
import edu.temple.bitcoindashboard.InfoFragments.StoredWalletsFragment;
import edu.temple.bitcoindashboard.Pagers.BlockInfoPagerFragment;
import edu.temple.bitcoindashboard.Pagers.PricePagerFragment;
import edu.temple.bitcoindashboard.Pagers.WalletPagerFragment;

public class MainActivity extends AppCompatActivity implements BlockInfoFragment.BlockSwitchListener,
        StoredWalletsFragment.WalletClickedListener, AddWalletFragment.AddWalletListener{
    public static final String CURRENT_FRAG = "current";
    public static final String PRICE_PAGER_INDEX = "price index";
    public static final String WALLET_PAGER_INDEX = "wallet index";
    public static final String BLOCK_PAGER_INDEX = "block index";
    private PricePagerFragment currentPriceFragment;
    private WalletPagerFragment walletPagerFragment;
    private BlockInfoPagerFragment blockInfoPagerFragment;
    private String[] appFunctions;
    private DrawerLayout mDrawerLayout;
    private ListView functionList;
    private boolean twoPane, bound;
    private String current;
    private Bundle savedState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        savedState = savedInstanceState;

        if(savedInstanceState != null) {
            current = savedInstanceState.getString(CURRENT_FRAG);
            //Log.d("current", current);
        }

        /*
        Check to see if in landscape mode
        If yes, display menu along with details, if no, create a hamburger menu
         */
        if(findViewById(R.id.function_menu) == null){
            //Not landscape
            twoPane = false;

            mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main);
            functionList = (ListView) findViewById(R.id.left_drawer);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_drawer);
        } else {
            //landscape
            twoPane = true;
            functionList = (ListView)findViewById(R.id.function_menu);
        }

        appFunctions = getResources().getStringArray(R.array.app_functions);

        functionList.setAdapter(new ArrayAdapter<String>(MainActivity.this, R.layout.drawer_list_item, appFunctions));
        functionList.setOnItemClickListener(new DrawerItemClickListener());

        if(current == null) {
            selectItem(0);
        } else {
            selectItem(Integer.parseInt(current));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                toggleDrawer();
                return true;
        }
        return false;
    }

    //Open or close the hamburger menu
    public void toggleDrawer(){
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void nextBlock() {
        blockInfoPagerFragment.nextBlock();
    }

    @Override
    public void previousBlock() {
        blockInfoPagerFragment.previousBlock();
    }

    @Override
    public void showWalletDetails(String address) {
        walletPagerFragment.showWalletDetails(address);
    }

    @Override
    public void returnToWalletList() {
        walletPagerFragment.returnToWalletList();
    }

    private class DrawerItemClickListener implements  ListView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position){
        current = String.valueOf(position);
        switch(position){
            case 0:
                if(currentPriceFragment == null) {
                    currentPriceFragment = PricePagerFragment.newInstance(savedState);
                }
                loadFragment(currentPriceFragment);

                closeDrawer(position);
                break;
            case 1:
                if(walletPagerFragment == null) {
                    walletPagerFragment = WalletPagerFragment.newInstance(savedState);
                }
                loadFragment(walletPagerFragment);

                closeDrawer(position);
                break;
            case 2:
                if(blockInfoPagerFragment == null) {
                    blockInfoPagerFragment = BlockInfoPagerFragment.newInstance(savedState);
                }
                loadFragment(blockInfoPagerFragment);

                closeDrawer(position);
                break;
            default:
                break;

        }
        Log.d("current", current);
    }

    private void closeDrawer(int position){
        functionList.setItemChecked(position, true);
        setTitle(appFunctions[position]);
        //no drawer if landscape
        if(twoPane){ return;}
        //otherwise, close the drawer
        mDrawerLayout.closeDrawer(functionList);
    }

    @Override
    public void setTitle(CharSequence title){
        getSupportActionBar().setTitle(title);
    }

    private void loadFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_frame, fragment)
                .commit();
        fm.executePendingTransactions();
    }

    @Override
    public void newWallet(){
        walletPagerFragment.newWallet();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(CURRENT_FRAG, current);

        switch(Integer.parseInt(current)){
            case 0:
                outState = currentPriceFragment.getState(outState);
                break;
            case 1:
                outState = walletPagerFragment.getState(outState);
                break;
            case 2:
                outState = blockInfoPagerFragment.getState(outState);
            default:
                break;
        }

    }

    public void hideKeyboard(){
        try {
            InputMethodManager inputManager = (InputMethodManager)
                    MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e){

        }
    }

}


