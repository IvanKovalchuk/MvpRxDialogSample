package com.kivsw.mvprxfiledialog;
/*
* This class is intended to show the file list of a directory
*/


import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.content.Context;

import com.kivsw.cloud.disk.IDiskIO;
import com.kivsw.cloud.disk.IDiskRepresenter;

import java.util.List;


public class FileListView extends ListView
        implements AdapterView.OnItemClickListener
{
    // interface for notification of directory change and a clicked file
    interface OnFileClick
    {
        void onFileClick(FileListView flf, IDiskIO.ResourceInfo fi, int position);
    };

    interface OnDiskClick
    {
        void onDiskClick(FileListView flf, IDiskRepresenter fi, int position);
    };


    FileAdapter fileAdapter;
    DiskRepresenterAdapter diskAdapter;


    private OnFileClick  onFileClick=null;
    private OnDiskClick onDiskClick=null;


    public OnDiskClick getOnDiskClick() {
        return onDiskClick;
    }

    public void setOnDiskClick(OnDiskClick onDiskClick) {
        this.onDiskClick = onDiskClick;
    }

    public OnFileClick getOnFileClick() {
        return onFileClick;
    }

    public void setOnFileClick(OnFileClick onFileClick) {
        this.onFileClick = onFileClick;
    }


    //-------------------------------------------------------
    public FileListView(Context context) {
        super(context);
        init();
    }
    public  FileListView(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        init();
    }
    public  FileListView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context,attrs, defStyleAttr);
        init();
    }
    private void init()
    {
        fileAdapter = new FileAdapter(getContext());
        diskAdapter = new DiskRepresenterAdapter(getContext());

        setItemsCanFocus(true);
        setOnItemClickListener(this);
    }
    public void setFileList(List<IDiskIO.ResourceInfo> fileList)
    {
        if(getAdapter()!=fileAdapter)
             setAdapter(fileAdapter);
        fileAdapter.setData(fileList);
    }
    public void setDiskList(List<IDiskRepresenter> diskList)
    {
        if(getAdapter()!=diskAdapter)
            setAdapter(diskAdapter);
        diskAdapter.setData(diskList);
    }


    //--------------------------------------------------------
    // implements AdapterView.OnItemClickListener
    // Processes a click on a file or directory
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(onFileClick==null) return;

        if(getAdapter()==fileAdapter) {

            IDiskIO.ResourceInfo ri = (IDiskIO.ResourceInfo) fileAdapter.getItem(position);
            if(onFileClick!=null) onFileClick.onFileClick(this, ri, position);
        }
        else
        {
            IDiskRepresenter dsk=(IDiskRepresenter)diskAdapter.getItem(position);
            if(onDiskClick!=null) onDiskClick.onDiskClick(this, dsk, position);
        }

   /*     FileAdapter.FileInfo fi=(FileAdapter.FileInfo)fileAdapter.getItem(position);

        if(fi.isDir)
        {
            String NewPath="", SelDir=null;
            if(0==fi.name.compareTo(".."))  // go to the upper directory
            {
                String Path = getPath();
                int i=0;
                i=Path.lastIndexOf("/",Path.length()-2);
                if(i>=0)
                    NewPath = Path.substring(0,i);

                SelDir = Path.substring(i+1, Path.length()-1);
            }
            else
                NewPath=getPath()+fi.name; // go to the chosen directory

            if(setPath(NewPath))
            {
                if(SelDir!=null && SelDir.length()>0)
                {
                    int p= fileAdapter.getDirPosition(SelDir);
                    if(p>=0)
                        setSelection(p);
                }
                else
                    setSelection(0);
            }

        }
        else
        {
            if(onDiskClick!=null) onDiskClick.onDiskClick(this,fi);
        }*/

    }
    //----------------------------------------------------------------------------------

}
