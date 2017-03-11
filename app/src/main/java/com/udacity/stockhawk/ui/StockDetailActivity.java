package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.udacity.stockhawk.R;

import timber.log.Timber;

public class StockDetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent passedIntent = getIntent();

        StockDetailFragment fragment = new StockDetailFragment();
        fragment.setArguments(passedIntent.getExtras());

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.stock_detail_fragment, fragment)
                .commit();

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
