package com.kivsw.mvprxfiledialog;

import android.content.Context;
import android.database.DataSetObserver;
import android.widget.ListAdapter;

import java.util.ArrayList;

/**
 * Created by ivan on 9/9/2017.
 */

public abstract class BaseListAdapter implements ListAdapter {
    private ArrayList<DataSetObserver> dataSetObservers;
    protected Context context;

    public BaseListAdapter(Context cnt)
    {
        context = cnt;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        int i = dataSetObservers.indexOf(observer);
        if (i < 0) {
            dataSetObservers.add(observer);
            observer.onChanged();
        }

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

        dataSetObservers.remove(observer);
    }

    protected void observersOnChanges() {

        for( int i = 0;i<dataSetObservers.size();i++)
        {
            DataSetObserver o = dataSetObservers.get(i);
            if (o != null) o.onChanged();
        }

}

    @Override
    public boolean hasStableIds() {

        return true;
    }

    @Override
    public boolean isEmpty() {

        return getCount()==0;
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
    public int getItemViewType(int position) {

        return IGNORE_ITEM_VIEW_TYPE;
    }
}
