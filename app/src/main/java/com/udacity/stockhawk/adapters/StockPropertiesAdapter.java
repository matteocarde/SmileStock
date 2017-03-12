package com.udacity.stockhawk.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.stockhawk.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

/**
 * Created by carde on 09/03/17.
 */

public class StockPropertiesAdapter extends RecyclerView.Adapter<StockPropertiesAdapter.StockPropertyViewHolder> {

    JSONArray mProperties;
    Context mContext;

    public StockPropertiesAdapter(Context context, JSONArray properties) {
        mProperties = properties;
        mContext = context;
    }

    @Override
    public StockPropertyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_stock_properties, parent, false);
        return new StockPropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StockPropertyViewHolder holder, int position) {
        if (mProperties != null) {
            try {
                JSONObject property = mProperties.getJSONObject(position);
                holder.bindProperty(property);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mProperties == null ? 0 : mProperties.length();
    }

    public void refreshData(JSONArray properties) {
        mProperties = properties;
        notifyDataSetChanged();
    }

    public class StockPropertyViewHolder extends RecyclerView.ViewHolder {

        final TextView mKey;
        TextView mValue;

        public StockPropertyViewHolder(View itemView) {
            super(itemView);
            mKey = (TextView) itemView.findViewById(R.id.stock_property_key);
            mValue = (TextView) itemView.findViewById(R.id.stock_property_value);
        }

        public void bindProperty(JSONObject property) throws JSONException {
            String key = property.getString("key");
            String value = property.getString("value");
            Timber.d("Key: %s, Value:%s", key, value);
            mKey.setText(key);
            mValue.setText(value);
        }
    }
}
