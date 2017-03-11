package com.udacity.stockhawk.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.stockhawk.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;


public class AddStockDialog extends DialogFragment implements LoaderManager.LoaderCallbacks {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.dialog_stock)
    EditText stock;

    Context context;

    private static final int LOADER_ID = 458;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") View custom = inflater.inflate(R.layout.add_stock_dialog, null);

        ButterKnife.bind(this, custom);

        builder.setView(custom);

        builder.setMessage(getString(R.string.dialog_title));
        builder.setPositiveButton(getString(R.string.dialog_add), null);

        builder.setNegativeButton(getString(R.string.dialog_cancel), null);

        Dialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {

                        String stockSymbol = stock.getText().toString();

                        Bundle bundle = new Bundle();
                        bundle.putString("symbol", stockSymbol);

                        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, bundle, AddStockDialog.this);
                    }
                });
            }
        });

        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        return dialog;
    }

    private void addStock(String stockName) {
        Fragment parent = getParentFragment();
        if (parent instanceof StockListFragment) {
            ((StockListFragment) parent).addStock(stockName);
        }
        dismissAllowingStateLoss();
    }

    ProgressDialog pDialog;


    @Override
    public Loader onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader(context) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                pDialog = new ProgressDialog(context);
                pDialog.setMessage(context.getString(R.string.loading_stock_message));
                pDialog.setIndeterminate(true);
                pDialog.show();

                forceLoad();
            }

            @Override
            public Object loadInBackground() {
                try {
                    Stock stock = YahooFinance.get(args.getString("symbol"));
                    return stock;
                } catch (IOException e) {
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        pDialog.dismiss();
        try {
            Stock stock = (Stock) data;
            Timber.d(stock + " " + stock.getSymbol());
            if (!stock.isValid())
                throw new Exception();

            addStock(stock.getSymbol());
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.stock_not_found_error), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
