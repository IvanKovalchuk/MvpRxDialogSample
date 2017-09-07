package com.kivsw.cloud.disk.pcloud;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.kivsw.cloud.disk.BaseDiskRepresenter;
import com.kivsw.cloud.disk.IDiskIO;
import com.kivsw.cloud.disk.IDiskRepresenter;
import com.kivsw.cloud.disk.localdisk.LocalDiskIo;
import com.kivsw.cloud_disk.R;

/**
 * Created by ivan on 9/7/2017.
 */

public class PcloudRepresenter extends BaseDiskRepresenter {
    public PcloudRepresenter(Context context, String clientId)
    {
        super(new PCloudDiskIo(context, clientId), "pcloud", "pCloud",
                BitmapFactory.decodeResource(context.getResources(), R.drawable.pcloud));
    }
}
