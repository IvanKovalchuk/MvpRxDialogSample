package com.kivsw.cloud.disk.localdisk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.kivsw.cloud.disk.BaseDiskRepresenter;
import com.kivsw.cloud_disk.R;

/**
 *
 */

public class LocalDiskRepresenter extends BaseDiskRepresenter {

    static public LocalDiskRepresenter getLocalFS(Context context)
    {
        return new LocalDiskRepresenter(context);
    }

    static public LocalDiskRepresenter getExternalSD(Context context)
    {
        String external = Environment.getExternalStorageDirectory().getAbsolutePath();
        return new LocalDiskRepresenter(context, new LocalDiskIo(external), "extSD", "External SD", BitmapFactory.decodeResource(context.getResources(), R.drawable.micro_sd));
    }

    public LocalDiskRepresenter(Context context)
    {
        super(new LocalDiskIo(), "file", "local disk",
                BitmapFactory.decodeResource(context.getResources(), R.drawable.micro_sd));
    }


    protected LocalDiskRepresenter(Context context, LocalDiskIo localDiskIo, String scheme, String name, Bitmap bmp)
    {
        super(localDiskIo, scheme, name,bmp);
    }

}
