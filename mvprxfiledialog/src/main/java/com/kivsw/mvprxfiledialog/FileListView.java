package com.kivsw.mvprxfiledialog;
/*
* This class is intended to show the file list of a directory
*/


import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kivsw.cloud.disk.IDiskIO;
import com.kivsw.cloud.disk.IDiskRepresenter;

import java.util.List;


public class FileListView extends ListView
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
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

    interface OnFileMenuItemClick
    {
        boolean onMenuItemClick(MenuItem item, IDiskIO.ResourceInfo resource);
    }


    private FileAdapter fileAdapter;
    private DiskRepresenterAdapter diskAdapter;
    private OnFileMenuItemClick onMenuItemClickListener;
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

    public void setOnMenuItemClickListener(OnFileMenuItemClick onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
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
        setOnItemLongClickListener(this);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onKeyUp(KeyEvent.KEYCODE_MENU,  new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MENU));
                return true;
            }
        });
    }

    @Override
    public boolean onKeyUp(int keyCode,  KeyEvent event)
    {
        if( (getAdapter()==fileAdapter) &&
             (event.getAction()==KeyEvent.ACTION_UP) &&
                (keyCode==KeyEvent.KEYCODE_MENU))
        {

            PopupMenu popup = new PopupMenu(getContext(), this);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.filelist_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(onMenuItemClickListener!=null)
                        return onMenuItemClickListener.onMenuItemClick(item, null);
                    return false;
                }
            });
            popup.show();

            return true;
        }
        return false;
    };

    public List<IDiskIO.ResourceInfo> getFileList()
    {
        return fileAdapter.getData();
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

    public int getItemPosition(String itemName)
    {
        return fileAdapter.getDirPosition(itemName);
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

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if(getAdapter()!=fileAdapter)
            return false;

        final IDiskIO.ResourceInfo resource = fileAdapter.getData().get(position);

        PopupMenu popup = new PopupMenu(getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        if(resource.name().equals("..")) inflater.inflate(R.menu.filelist_menu, popup.getMenu());
            else                         inflater.inflate(R.menu.file_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(onMenuItemClickListener!=null) {

                    return onMenuItemClickListener.onMenuItemClick(item, resource);
                }
                return false;
            }
        });
        popup.show();

        return true;
    }
    //----------------------------------------------------------------------------------

}
