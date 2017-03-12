package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.adapters.StockPropertiesAdapter;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.utils.GeneralUtils;
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

public class StockDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks {

    String mSymbol;
    String TAG;
    Stock mData;

    @BindView(R.id.stock_symbol)
    TextView stockSymbol;

    @BindView(R.id.stock_detail_chart)
    LineChart historyChart;

    @BindView(R.id.stock_detail_recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.stock_detail_container)
    View detailContainer;

    @BindView(R.id.select_stock_from_list_message)
    TextView selectStockMessage;

    @BindView(R.id.chart_loader)
    ProgressBar chartLoader;

    @BindView(R.id.stock_list_loader)
    ProgressBar stockListLoader;

    private static final int LOADER_ID = 291;
    private StockPropertiesAdapter mAdapter;
    private int mPositon;
    private boolean isDualPane;

    private static final String NO_INTERNET_CONNECTION_ERROR = "NO_INTERNET_CONNECTION_ERROR";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_detail, container, false);

        TAG = getActivity().getLocalClassName();

        ButterKnife.bind(this, view);

        isDualPane = getResources().getBoolean(R.bool.is_dual_pane);


        if (getArguments() != null && getArguments().getString("SYMBOL") != null) {
            mSymbol = getArguments().getString("SYMBOL");
            mPositon = getArguments().getInt("POSITION", 0);

            Timber.d("onCreateView " + mSymbol);
            stockSymbol.setText(mSymbol);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

            mAdapter = new StockPropertiesAdapter(getActivity(), null);

            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(mAdapter);

            historyChart.setNoDataText("");
            showLoaders();
            getActivity().getSupportLoaderManager().restartLoader(LOADER_ID + mPositon, null, this);
        } else {
            detailContainer.setVisibility(View.INVISIBLE);
            selectStockMessage.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private void showLoaders() {
        chartLoader.setVisibility(View.VISIBLE);
        stockListLoader.setVisibility(View.VISIBLE);
    }

    private void hideLoaders() {
        chartLoader.setVisibility(View.INVISIBLE);
        stockListLoader.setVisibility(View.INVISIBLE);
    }

    private void bindData() {
        if (mData == null) {
            Timber.e("Data is not available");
            hideLoaders();
            historyChart.setNoDataText(getString(R.string.no_data_available));
            return;
        }

        stockSymbol.setText(mData.getName());

        try {
            updateGraph(mData.getHistory());

            JSONArray properties = StocksUtils.getStockProperties(getActivity(), mData);
            Timber.d(properties.toString());
            mAdapter.refreshData(properties);
            hideLoaders();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateGraph(List<HistoricalQuote> history) throws IOException {

        List<Entry> entries = StocksUtils.prepareGraphData(getActivity(), history);

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(false);
        dataSet.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryLight));

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

        historyChart.getAxisRight().setEnabled(false);
        historyChart.getLegend().setEnabled(false);
        historyChart.getDescription().setEnabled(false);
        if (isDualPane) {
            historyChart.animateX(1000);
        }

        historyChart.setData(lineData);
        historyChart.invalidate();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri uri = Contract.Quote.URI.buildUpon().appendPath(mSymbol).build();
        return new AsyncTaskLoader(getActivity()) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Override
            public Object loadInBackground() {
                if (!GeneralUtils.isNetworkUp(getActivity())) {
                    return NO_INTERNET_CONNECTION_ERROR;
                }
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
        if(data == NO_INTERNET_CONNECTION_ERROR){
            hideLoaders();
            historyChart.setNoDataText(getString(R.string.no_internet_connection));
            return;
        }
        mData = (Stock) data;
        bindData();
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

}
