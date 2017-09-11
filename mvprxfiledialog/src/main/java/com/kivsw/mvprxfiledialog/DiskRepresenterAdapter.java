package com.kivsw.mvprxfiledialog;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.kivsw.cloud.disk.IDiskIO;
import com.kivsw.cloud.disk.IDiskRepresenter;

import java.util.List;

/**
 * This adapter helps to visualize IDiskRepresenter
 */

public class DiskRepresenterAdapter
        extends BaseListAdapter
{
    protected List<IDiskRepresenter> data;

    DiskRepresenterAdapter(Context cnt)
    {
        super(cnt);
    }
    @Override
    public int getCount() {
        if(data==null)
            return 0;
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String s;

        class ViewHolder
        {
            TextView textView;
            ImageView imageView;
            int position;
        }
        ViewHolder viewHolder;

        if(convertView==null)
        {
            convertView = View.inflate(context, R.layout.disk_represente_item, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.textView);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder)convertView.getTag();

        viewHolder.position = position;
        if((position>=0)&&(position<getCount())) {
            IDiskRepresenter disk = data.get(position);

            viewHolder.imageView.setImageBitmap(disk.getIcon());
            viewHolder.textView.setText(disk.getName());
        }

        return convertView;

    }


    public void setData(List<IDiskRepresenter> data) {
        this.data = data;
        observersOnChanges();
    }
}
