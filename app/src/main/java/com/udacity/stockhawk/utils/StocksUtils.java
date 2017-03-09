package com.udacity.stockhawk.utils;

import android.content.Context;

import com.udacity.stockhawk.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yahoofinance.Stock;

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
                .put("key", context.getString(R.string.prev_close_propery))
                .put("value", stock.getQuote().getPreviousClose())
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

}
