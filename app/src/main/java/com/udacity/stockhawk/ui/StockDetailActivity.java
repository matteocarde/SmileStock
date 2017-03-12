package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.adapters.StocksPagerAdapter;
import com.udacity.stockhawk.data.Contract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private StocksPagerAdapter mPagerAdapter;
    private String mSymbol;
    private static final int LOADER_ID = 534;

    @BindView(R.id.detail_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.detail_tabs)
    TabLayout mTabs;

    @BindView(R.id.detail_pager)
    ViewPager mViewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager.setAdapter(null);

        mTabs.setupWithViewPager(mViewPager);


        if (getIntent() != null && getIntent().hasExtra("SYMBOL")) {
            mSymbol = getIntent().getStringExtra("SYMBOL");
        }

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        String[] stocks = new String[data.getCount()];
        int selectedPage = -1;
        for (int i = 0; i < data.getCount(); i++) {
            data.moveToPosition(i);
            String stock = data.getString(Contract.Quote.POSITION_SYMBOL);
            stocks[i] = stock;
            selectedPage = stock.equals(mSymbol) ? i : selectedPage;
        }

        mPagerAdapter = new StocksPagerAdapter(getSupportFragmentManager(), stocks);
        mViewPager.setAdapter(mPagerAdapter);
        if (selectedPage > -1) {
            mViewPager.setCurrentItem(selectedPage);
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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

}
