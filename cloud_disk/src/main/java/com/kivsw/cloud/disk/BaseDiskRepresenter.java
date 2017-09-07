package com.kivsw.cloud.disk;

import android.content.Context;
import android.graphics.Bitmap;

import com.kivsw.cloud.disk.localdisk.LocalDiskIo;

/**
 * Created by ivan on 9/7/2017.
 */

public class BaseDiskRepresenter implements IDiskRepresenter {
    private IDiskIO diskIo;
    //private Context context;
    private String scheme;
    private String name;
    private Bitmap icon;

    public BaseDiskRepresenter(IDiskIO diskIo, String scheme, String name, Bitmap icon)
    {
        //this.context = context;
        this.diskIo =  diskIo;
        this.scheme = scheme;
        this.name = name;
        this.icon = icon;
    }

    @Override
    public IDiskIO getDiskIo() {
        return diskIo;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getScheme() {
        return scheme;
    }

    @Override
    public Bitmap getIcon() {
        return icon;
    }
}
