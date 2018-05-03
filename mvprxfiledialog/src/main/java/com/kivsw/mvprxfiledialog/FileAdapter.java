package com.kivsw.mvprxfiledialog;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kivsw.cloud.disk.IDiskIO;

import java.util.ArrayList;
import java.util.List;

//import java.util.Locale;

/**
 * This adapter helps to visualize ResourceInfo
 */
class FileAdapter  extends BaseListAdapter
{

    private List<IDiskIO.ResourceInfo> fileList=new ArrayList(); // null value means that it's necessary to rebuild fileList

    //----------------------------------------
    /** Creates an entitle of FileAdapter
     * @param cnt
     */
    public FileAdapter(Context cnt)
    {
        super(cnt);
    }
    //----------------------------------------
    /** returns position number of the Dir in fileList
     * @param Dir
     * @return position number of the Dir in fileList
     */
    public int getDirPosition(String Dir) {

        int i;
        for(i=0;i<fileList.size();i++)
        {
            if(0==fileList.get(i).name().compareTo(Dir))
                return i;
        }
        return -1;

    }
    //----------------------------------------
    /**
     * @return File list of the curent directory
     */

    //----------------------------------------
    public List<IDiskIO.ResourceInfo> getData()
    {
        return fileList;
    }
    /** this method gets the file list of the path
     *
     */
    protected void setData(List<IDiskIO.ResourceInfo> newFileList)
    {
        if(newFileList==null)
            this.fileList=new ArrayList();
        else
            this.fileList=newFileList;

        observersOnChanges();
    }


    //----------------------------------------
    /**  return amount of the files in fileList
     *  @return
     */
    public int getCount() {

        return fileList.size();
    }

    //----------------------------------------
    public Object getItem(int position) {

        if((position<0)||(position>=getCount()))
            return null;

        return fileList.get(position);

    }

    //----------------------------------------


    //----------------------------------------
    /** return and create (if necessary) the view of a file
     *
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        String s;

        class ViewHolder
        {
            TextView fileName, fileInfo;
            ImageView image;
            LinearLayout linearLayout;
            int position;
        }
        ViewHolder viewHolder;

        if(convertView==null)
        {
            convertView = View.inflate(context, R.layout.file_item, null);
            viewHolder = new ViewHolder();
            viewHolder.fileName = (TextView) convertView.findViewById(R.id.textViewFileName);
            viewHolder.fileInfo = (TextView) convertView.findViewById(R.id.textViewFileInfo);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.linearLayout = (LinearLayout)  convertView.findViewById(R.id.linearLayout);
            convertView.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder)convertView.getTag();

        viewHolder.position = position;
        if((position>=0)&&(position<getCount()))
        {
            IDiskIO.ResourceInfo fi=fileList.get(position);

            // chooses background  colour
            if((position&1)==0)
            {
                viewHolder.linearLayout.setBackgroundColor(0);
            }
            else
            {
                int color=viewHolder.fileName.getCurrentTextColor();
                color = (color & 0x00FFFFFF) | 0x11000000;
                viewHolder.linearLayout.setBackgroundColor(color);
            }

            // chooses file/dir picture
            if(fi.isFolder())
            {
                if((position==0) && 0==fi.name().compareTo("..")) viewHolder.image.setImageResource(R.drawable.icodirup);
                else viewHolder.image.setImageResource(R.drawable.icodir);
            }
            else
            if(fi.isFile())  viewHolder.image.setImageResource(R.drawable.icofile);
            else viewHolder.image.setImageResource(R.drawable.icospecial);

            // file name
            viewHolder.fileName.setText(fi.name());

            // file info
            StringBuilder info=new StringBuilder();

            if(fi.isFile())
            {
                // the file size
                info.append(sizeToString(fi.size()));
                info.append("\t");
            }

            if(!fi.isFolder() || !fi.name().equals(".."))
            {
                // date and time when the file was modified
                info.append(String.format("%tF\n%tT", fi.modified(), fi.modified()));
            }

            viewHolder.fileInfo.setText(info.toString());
        }

        return convertView;
    }

    /**
     * converts file size to human readable format
     * @param sz
     * @return
     */
    protected String sizeToString(long sz)
    {
        String s;
        if(sz<1024)
            s=(String.format("%4db ",sz));
        else if(sz<1024*1024)
            s=(String.format("%4dk ",sz/1024));
        else if(sz<1024*1024*1024)
            s=(String.format("%4dM ",sz/(1024*1024)));
        else if(sz<1024*1024*1024*1024)
            s=(String.format("%4dG ",sz/(1024*1024*1024)));
        else
            s=(String.format("%4dT ",sz/(1024*1024*1024*1024)));

        return s;
    }


}
//-------------------------------------------------------------------------------
//-------------------------------------------------------------------------------


