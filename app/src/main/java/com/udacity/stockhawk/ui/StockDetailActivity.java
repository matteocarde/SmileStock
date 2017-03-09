package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.adapters.StockPropertiesAdapter;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.utils.StocksUtils;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class StockDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    String mSymbol;
    String TAG;
    Stock mData;

    @BindView(R.id.stock_symbol)
    TextView stockSymbol;

    @BindView(R.id.stock_detail_chart)
    LineChart historyChart;

    @BindView(R.id.stock_detail_recyclerview)
    RecyclerView recyclerView;

    private static final int LOADER_ID = 291;
    private StockPropertiesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        TAG = getLocalClassName();

        //Change it with toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        Intent passedIntent = getIntent();

        if (!passedIntent.hasExtra("SYMBOL")) {
            Log.e(TAG, "Symbol is not passed to the activity");
            //TODO: Show error
            return;
        }

        mSymbol = passedIntent.getStringExtra("SYMBOL");
        stockSymbol.setText(mSymbol);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mAdapter = new StockPropertiesAdapter(this, null);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void bindData() {
        if (mData == null) {
            Timber.e("Data is not available");
            //TODO: Show error
            return;
        }

        stockSymbol.setText(mData.getName() + " (" + mSymbol + ")");

        try {
            updateGraph(mData.getHistory());

            JSONArray properties = StocksUtils.getStockProperties(this, mData);
            Timber.d(properties.toString());
            mAdapter.refreshData(properties);

        } catch (Exception e) {
            //TODO: Catch error
            e.printStackTrace();
        }
    }


    private void updateGraph(List<HistoricalQuote> history) throws IOException {
        List<Entry> entries = new ArrayList<>();

        Timber.d("History count: %s", "" + history.size());
        int i = 0;
        for (HistoricalQuote quote : history) {
            //I have to use i because for some reason it doesn't ac
            entries.add(new Entry(i++, quote.getClose().floatValue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Valori");
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        LineData lineData = new LineData(dataSets);

        XAxis xAxis = historyChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setLabelCount(3, true);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);


        YAxis axisLeft = historyChart.getAxisLeft();
        axisLeft.setDrawTopYLabelEntry(true);
        axisLeft.setSpaceBottom(0);
        axisLeft.setSpaceTop(0);
        axisLeft.setDrawAxisLine(false);
        axisLeft.setLabelCount(3, true);
        axisLeft.setTextColor(Color.WHITE);
        axisLeft.setDrawGridLines(false);
        axisLeft.setDrawZeroLine(false);
        axisLeft.setZeroLineColor(Color.RED);

        historyChart.getAxisRight().setEnabled(false);
        historyChart.getLegend().setEnabled(false);
        historyChart.getDescription().setEnabled(false);

        historyChart.setData(lineData);
        historyChart.invalidate();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri uri = Contract.Quote.URI.buildUpon().appendPath(mSymbol).build();
        return new AsyncTaskLoader(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Override
            public Object loadInBackground() {
                try {

                    Calendar from = Calendar.getInstance();
                    Calendar to = Calendar.getInstance();
                    //Get stocks from two years ago
                    //TODO: Evaluate to maybe let the user decide via shared preferences ?
                    from.add(Calendar.YEAR, -2);

                    Stock stock = YahooFinance.get(mSymbol, from, to, Interval.WEEKLY);
                    stock.getHistory(); //FROM DOCS: If the historical quotes are not yet available, the getHistory() method will automatically send a new request to Yahoo Finance.
                    return stock;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        mData = (Stock) data;
        bindData();
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

}
