package com.kivsw.cloud.disk.localdisk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.kivsw.cloud.disk.BaseDiskRepresenter;
import com.kivsw.cloud_disk.R;

/**
 *
 */

public class LocalDiskRepresenter extends BaseDiskRepresenter {

   /* static public LocalDiskRepresenter getLocalFS(Context context)
    {
        return new LocalDiskRepresenter(context);
    }

    static public LocalDiskRepresenter getExternalSD(Context context)
    {
        String external = Environment.getExternalStorageDirectory().getAbsolutePath();
        return new LocalDiskRepresenter(context, new LocalDiskIo(external), "extSD", "External SD", BitmapFactory.decodeResource(context.getResources(), R.drawable.micro_sd));
    }*/

    public static String ROOTFS_SCHEME ="file", NAME_ROOT="Root FS";
    public static String APPFS_SCHEME ="app", NAME_PRIVATE_DIR="Private directory";
    static public LocalDiskRepresenter createRootFS(Context context)
    {
        return new LocalDiskRepresenter(context, new LocalDiskIo(), ROOTFS_SCHEME, NAME_ROOT,
                BitmapFactory.decodeResource(context.getResources(), R.drawable.micro_sd));
    }
    static public LocalDiskRepresenter createPrivateStorageFS(Context context)
    {
        String dir=context.getFilesDir().getParent();//getAbsolutePath();
        return new LocalDiskRepresenter(context, new LocalDiskIo(dir), APPFS_SCHEME, NAME_PRIVATE_DIR,
                BitmapFactory.decodeResource(context.getResources(), R.drawable.micro_sd));
    }


    public LocalDiskRepresenter(Context context, LocalDiskIo localDiskIo, String scheme, String name, Bitmap bmp)
    {
        super(localDiskIo, scheme, name,bmp);
    }

}
