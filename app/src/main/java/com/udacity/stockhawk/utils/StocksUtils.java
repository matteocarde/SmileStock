package com.udacity.stockhawk.utils;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.udacity.stockhawk.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;

/**
 * Created by carde on 09/03/17.
 */

public class StocksUtils {

    public static JSONArray getStockProperties(Context context, Stock stock) throws JSONException {
        JSONArray array = new JSONArray();

        //Here I fetch some data just to make the UI prettier but I've no idea of what these numbers are :D
        array.put(new JSONObject()
                .put("key", context.getString(R.string.currency_property))
                .put("value", stock.getCurrency())
        );

        array.put(new JSONObject()
                .put("key", context.getString(R.string.stock_exchange_property))
                .put("value", stock.getStockExchange())
        );

        array.put(new JSONObject()
                .put("key", context.getString(R.string.ask_property))
                .put("value", stock.getQuote().getAsk())
        );

        array.put(new JSONObject()
                .put("key", context.getString(R.string.bid_property))
                .put("value", stock.getQuote().getBid())
        );

        array.put(new JSONObject()
                .put("key", context.getString(R.string.price_property))
                .put("value", stock.getQuote().getPrice())
        );


        array.put(new JSONObject()
                .put("key", context.getString(R.string.eps_property))
                .put("value", stock.getStats().getEps())
        );

        array.put(new JSONObject()
                .put("key", context.getString(R.string.pe_property))
                .put("value", stock.getStats().getPe())
        );

        array.put(new JSONObject()
                .put("key", context.getString(R.string.peg_property))
                .put("value", stock.getStats().getPeg())
        );

        array.put(new JSONObject()
                .put("key", context.getString(R.string.annual_yield_property))
                .put("value", stock.getDividend().getAnnualYield() != null ? stock.getDividend().getAnnualYield() : context.getString(R.string.not_available)));

        return array;
    }

    public static List<Entry> prepareGraphData(List<HistoricalQuote> history) {
        List<Entry> entries = new ArrayList<>();

        int i = 0;
        for (HistoricalQuote quote : history) {
            //I have to use i because for some reason it doesn't ac
            entries.add(new Entry(i++, quote.getClose().floatValue()));
        }

        return entries;
    }


}
