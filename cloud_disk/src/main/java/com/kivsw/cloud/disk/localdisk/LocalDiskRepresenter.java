package com.kivsw.cloud.disk.localdisk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.kivsw.cloud.disk.BaseDiskRepresenter;
import com.kivsw.cloud.disk.IDiskIO;
import com.kivsw.cloud.disk.IDiskRepresenter;
import com.kivsw.cloud_disk.R;

/**
 *
 */

public class LocalDiskRepresenter extends BaseDiskRepresenter {

    public LocalDiskRepresenter(Context context)
    {
        super(new LocalDiskIo(), "file", "local disk",
                BitmapFactory.decodeResource(context.getResources(), R.drawable.micro_sd));
    }

}
