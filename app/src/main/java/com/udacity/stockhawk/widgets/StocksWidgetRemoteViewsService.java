package com.udacity.stockhawk.widgets;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.adapters.StockAdapter;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.ui.StockDetailActivity;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by carde on 10/03/17.
 */

public class StocksWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {

            Cursor mData = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (mData != null) {
                    mData.close();
                }

                final long identityToken = Binder.clearCallingIdentity();
                mData = getContentResolver().query(Contract.Quote.URI, null, null, null, Contract.Quote.COLUMN_SYMBOL + " ASC");
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (mData != null) {
                    mData.close();
                    mData = null;
                }
            }

            @Override
            public int getCount() {
                return mData == null ? 0 : mData.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION || mData == null || !mData.moveToPosition(position)) {
                    return null;
                }

                Context context = getApplicationContext();

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_stocks_list_item);

                DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                DecimalFormat dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                dollarFormatWithPlus.setPositivePrefix("+$");
                DecimalFormat percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
                percentageFormat.setMaximumFractionDigits(2);
                percentageFormat.setMinimumFractionDigits(2);
                percentageFormat.setPositivePrefix("+");

                mData.moveToPosition(position);

                String symbol = mData.getString(Contract.Quote.POSITION_SYMBOL);
                String price = dollarFormat.format(mData.getFloat(Contract.Quote.POSITION_PRICE));
                views.setTextViewText(R.id.widget_item_symbol, symbol);
                views.setTextViewText(R.id.widget_item_price, price);

                float rawAbsoluteChange = mData.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
                float percentageChange = mData.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

                if (rawAbsoluteChange > 0) {
                    views.setTextColor(R.id.widget_item_change, ContextCompat.getColor(context, R.color.material_green_700));
                } else {
                    views.setTextColor(R.id.widget_item_change, ContextCompat.getColor(context, R.color.material_red_700));
                }

                String change = dollarFormatWithPlus.format(rawAbsoluteChange);
                String percentage = percentageFormat.format(percentageChange / 100);

                if (PrefUtils.getDisplayMode(context).equals(context.getString(R.string.pref_display_mode_absolute_key))) {
                    views.setTextViewText(R.id.widget_item_change, change);
                } else {
                    views.setTextViewText(R.id.widget_item_change, percentage);
                }

                Intent fillInIntent = new Intent(getApplicationContext(), StockDetailActivity.class);

                fillInIntent.putExtra("SYMBOL", symbol);

                views.setOnClickFillInIntent(R.id.widget_list_item_linear_layout, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_stocks_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
