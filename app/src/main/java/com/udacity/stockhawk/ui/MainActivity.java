package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.utils.GeneralUtils;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements StockListFragment.OnStockClickHandler {

    StockListFragment mListFragment;
    StockDetailFragment mDetailFragment;
    boolean isDualView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        isDualView = getResources().getBoolean(R.bool.is_dual_pane);


        mListFragment = new StockListFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.stock_list_fragment, mListFragment)
                .commit();

        if (isDualView) {
            mDetailFragment = new StockDetailFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.stock_detail_tablet_fragment, mDetailFragment)
                    .commit();
        }
    }

    private void setDisplayModeMenuItemIcon(MenuItem item) {
        if (PrefUtils.getDisplayMode(this)
                .equals(getString(R.string.pref_display_mode_absolute_key))) {
            item.setIcon(R.drawable.ic_percentage);
            item.setTitle(getString(R.string.dollar_mode_on));
        } else {
            item.setIcon(R.drawable.ic_dollar);
            item.setTitle(getString(R.string.percentage_mode_on));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_settings, menu);
        MenuItem item = menu.findItem(R.id.action_change_units);
        setDisplayModeMenuItemIcon(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_change_units) {
            PrefUtils.toggleDisplayMode(this);
            setDisplayModeMenuItemIcon(item);
            mListFragment.adapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Shows the dialog to add a new stock
     *
     * @param view
     */
    public void button(@SuppressWarnings("UnusedParameters") View view) {
        new AddStockDialog().show(getSupportFragmentManager(), "StockDialogFragment");
    }

    public void addStock(String symbol) {
        if (symbol != null && !symbol.isEmpty()) {

            if (GeneralUtils.isNetworkUp(this)) {
                mListFragment.swipeRefreshLayout.setRefreshing(true);
            } else {
                String message = getString(R.string.toast_stock_added_no_connectivity, symbol);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }

            PrefUtils.addStock(this, symbol);
            QuoteSyncJob.syncImmediately(this);
        }
    }

    @Override
    public void onStockClick(String symbol) {
        if (!isDualView) {
            Intent intent = new Intent(this, StockDetailActivity.class);
            intent.putExtra("SYMBOL", symbol);

            startActivity(intent);
        } else {
            Timber.d("onStockClick " + symbol);
            StockDetailFragment newFragment = new StockDetailFragment();

            Bundle args = new Bundle();
            args.putString("SYMBOL", symbol);
            newFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.stock_detail_tablet_fragment, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    }
}
