package com.kivsw.mvprxfiledialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ivan on 5/11/16.
 */
public class IconSpinnerAdapter extends ArrayAdapter<String>
 {

    Drawable[] icons=null;

    public IconSpinnerAdapter(Context context, /*int textViewResourceId,*/
                              String[] objects, Drawable[] icons) {
        super(context, R.layout.icon_spinner_adapter_item, objects);
        //R.layout.support_simple_spinner_dropdown_item
        this.icons = icons;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View row;
        if(convertView!=null)
            row =convertView;
        else {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //icon_spinner_adapter_item = inflater.inflate(R.layout.icon_spinner_adapter_item, parent, false);
            row = inflater.inflate(R.layout.icon_spinner_adapter_item, null);
        }

        TextView label=(TextView)row.findViewById(R.id.textView);
        label.setText(getItem(position));

        ImageView icon=(ImageView)row.findViewById(R.id.imageView);

        if (icons!=null && icons.length>position){
            icon.setImageDrawable(icons[position]);
            //icon.setImageResource(R.drawable.icon);
        }
        else{
            icon.setImageResource(0);
            //icon.setImageResource(R.drawable.icongray);
        }

        return row;
    }
}

