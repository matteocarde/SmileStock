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
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;

public class StocksUtils {

    public static JSONArray getStockProperties(Context context, Stock stock) throws JSONException {
        JSONArray array = new JSONArray();
        DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);

        //Here I fetch some data just to make the UI prettier but I've no idea of what these numbers are :D
        array.put(createPropriety(context, context.getString(R.string.currency_property), stock.getCurrency()));
        array.put(createPropriety(context, context.getString(R.string.stock_exchange_property), stock.getStockExchange()));
        array.put(createPropriety(context, context.getString(R.string.ask_property), dollarFormat.format(stock.getQuote().getAsk())));
        array.put(createPropriety(context, context.getString(R.string.bid_property), dollarFormat.format(stock.getQuote().getBid())));
        array.put(createPropriety(context, context.getString(R.string.price_property), dollarFormat.format(stock.getQuote().getPrice())));
        array.put(createPropriety(context, context.getString(R.string.eps_property), dollarFormat.format(stock.getStats().getEps())));
        array.put(createPropriety(context, context.getString(R.string.pe_property), stock.getStats().getPe()));
        array.put(createPropriety(context, context.getString(R.string.peg_property), stock.getStats().getPeg()));
        array.put(createPropriety(context, context.getString(R.string.annual_yield_property), stock.getDividend().getAnnualYield()));

        return array;
    }


    private static JSONObject createPropriety(Context context, String key, BigDecimal value) throws JSONException {
        String fValue = value == null ? context.getString(R.string.not_available) : value.toString();
        return createPropriety(context, key, fValue);
    }

    private static JSONObject createPropriety(Context context, String key, String value) throws JSONException {

        String fValue = value == null ? context.getString(R.string.not_available) : value;

        return new JSONObject()
                .put("key", key)
                .put("value", fValue);
    }

    public static List<Entry> prepareGraphData(Context context, List<HistoricalQuote> history) {
        List<Entry> entries = new ArrayList<>();

        for (int i = 0; i < history.size(); i++) {
            HistoricalQuote quote = history.get(i);
            entries.add(new Entry(i, quote.getClose().floatValue()));
        }

        return entries;
    }


}
