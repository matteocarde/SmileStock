package com.udacity.stockhawk.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.ui.MainActivity;
import com.udacity.stockhawk.ui.StockDetailActivity;

/**
 * Implementation of App Widget functionality.
 */
public class StocksWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int id : appWidgetIds) {
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_stocks);

            Intent intent = new Intent(context, StocksWidgetRemoteViewsService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);

            Class destinationClass;
            if (context.getResources().getBoolean(R.bool.is_dual_pane)) {
                destinationClass = MainActivity.class;
            } else {
                destinationClass = StockDetailActivity.class;
            }

            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(new Intent(context, destinationClass))
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            remoteView.setPendingIntentTemplate(R.id.widget_stocks_listview, clickPendingIntentTemplate);

            remoteView.setRemoteAdapter(R.id.widget_stocks_listview, intent);

            appWidgetManager.updateAppWidget(id, remoteView);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();

        if (action.equals(context.getString(R.string.update_widget_action))) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_stocks_listview);
        }
    }
}

