package com.udacity.stockhawk.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.udacity.stockhawk.ui.StockDetailFragment;

/**
 * Created by carde on 12/03/17.
 */

public class StocksPagerAdapter extends FragmentStatePagerAdapter {

    private String[] stocks;
    private FragmentManager fm;

    public StocksPagerAdapter(FragmentManager fm, String[] stocks) {
        super(fm);
        this.stocks = stocks;
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {

        StockDetailFragment fragment = new StockDetailFragment();

        Bundle args = new Bundle();
        args.putString("SYMBOL", stocks[position]);
        args.putInt("POSITION", position);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return stocks.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return stocks[position];
    }
}
