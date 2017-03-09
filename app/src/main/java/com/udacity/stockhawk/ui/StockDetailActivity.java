package com.udacity.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class StockDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    String mSymbol;
    String TAG;
    Cursor mData;

    @BindView(R.id.stock_symbol)
    TextView stockSymbol;


    @BindView(R.id.stock_detail_chart)
    LineChart historyChart;
    private static final int LOADER_ID = 291;

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

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void bindData() {
        if (mData == null) {
            //TODO: Show error
            return;
        }

        mData.moveToFirst();
        String historyText = mData.getString(mData.getColumnIndex(Contract.Quote.COLUMN_HISTORY));

        try {
            updateGraph(historyText);
        } catch (IOException e) {
            //TODO: Show error
            e.printStackTrace();
        }
    }

    private void updateGraph(String history) throws IOException {
        List<Entry> entries = new ArrayList<>();

        CSVReader csv = new CSVReader(new StringReader(history));

        String[] row = csv.readNext();
        int i = 0;
        while (row != null) {
            //I have to use i because for some reason it doesn't ac
            entries.add(new Entry(i++, Float.parseFloat(row[1])));

            row = csv.readNext();
        }

//        float[] valuesX = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
//        float[] valuesY = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
//
//        for (int i=0 ; i< valuesX.length ; i++) {
//            entries.add(new Entry(valuesX[i], valuesY[i]));
//        }

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
//        xAxis.setValueFormatter(new GraphXAxisFormatter());
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);


        YAxis axisLeft = historyChart.getAxisLeft();
        axisLeft.setDrawTopYLabelEntry(true);
        axisLeft.setSpaceBottom(0);
        axisLeft.setSpaceTop(0);
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
        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mData = data;
        bindData();
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

}
