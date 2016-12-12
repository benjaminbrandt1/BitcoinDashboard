package edu.temple.bitcoindashboard.InfoFragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.InputStream;

import edu.temple.bitcoindashboard.R;
import edu.temple.bitcoindashboard.TabFragment;

/*
Fragment to retrieve and display price charts of Bitcoin from yahoo.com
Although the chart is retrieved continuously, a thread is used because it only runs
while this fragment is in view, unlike a service, which would run continuously even when the
fragment was not in view
 */
public class PriceTrackerFragment extends Fragment implements TabFragment {
    public static String title;
    private static final String ONE_DAY = "1d";
    private static final String FIVE_DAY = "5d";
    private static final String FIFTEEN_DAY = "15d";
    private static final String YAHOO_API = "http://chart.yahoo.com/z?s=BTCUSD=X&t=";
    private ImageDownloader downloader;
    private ImageView chart;
    private Spinner chartChoices;
    private String[] chartTimespans;
    private ProgressBar progressBar;
    private int current;
    private boolean running;
    private Handler handler;

    public PriceTrackerFragment() {
        // Required empty public constructor
    }

    //Factory Method
    public static PriceTrackerFragment newInstance() {
        PriceTrackerFragment fragment = new PriceTrackerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getActivity().getString(R.string.price_tracker);
        if (getArguments() != null) {

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_price_tracker, container, false);

        progressBar = (ProgressBar)v.findViewById(R.id.price_tracker_progress);
        progressBar.setVisibility(View.INVISIBLE);

        handler = new Handler();

        running = true;

        chartTimespans = getResources().getStringArray(R.array.chart_timespans);

        chartChoices = (Spinner)v.findViewById(R.id.chart_choices);
        chartChoices.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1 , chartTimespans));
        chartChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(view != null){
                    current = position;

                    changeChart(((TextView)view).getText().toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        chart = (ImageView)v.findViewById(R.id.price_chart);

        /*
        This thread retrieves the chart from yahoo.com every 15 seconds
        If the fragment is paused, the thread stops
         */
        Thread timer = new Thread(new Runnable() {
            @Override
            public void run() {
                while(running){

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String timespan = chartTimespans[current];
                            changeChart(timespan);
                        }
                    });

                    try{
                        Thread.sleep(15000);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        timer.start();

        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause(){
        super.onPause();
        running = false;
    }

    @Override
    public void onResume(){
        super.onResume();
        running = true;
    }

    @Override
    public String getTitle() {
        return title;
    }

    //Build the url for the required chart and call the downloader to begin retrieval
    public void changeChart(String timespan){
        Log.d("CHANGING", "CHART");
        String url = YAHOO_API;

        if(timespan.equals(chartTimespans[0])){
            url = url + ONE_DAY;
        } else if (timespan.equals(chartTimespans[1])){
            url = url + FIVE_DAY;
        } else if (timespan.equals(chartTimespans[2])) {
            url = url + FIFTEEN_DAY;
        }

        progressBar.setVisibility(View.VISIBLE);

        downloader = new ImageDownloader(chart);
        downloader.execute(url);

    }

    //Retrieves the bitmap representing the price chart from the Yahoo API
    private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public ImageDownloader(ImageView bmImage) {
            this.bmImage = bmImage;
            Log.d("Error", "not here");
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap mIcon = null;
            try {
                Log.d("Error", "nor here");
                InputStream in = new java.net.URL(url).openStream();
                Log.d("Error", "got here");
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.d("Error", e.toString());
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            progressBar.setVisibility(View.INVISIBLE);
            cancel(true);
        }
    }
}
